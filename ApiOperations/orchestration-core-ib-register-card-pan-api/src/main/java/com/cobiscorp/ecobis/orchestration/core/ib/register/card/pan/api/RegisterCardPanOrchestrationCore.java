package com.cobiscorp.ecobis.orchestration.core.ib.register.card.pan.api;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.crypt.ReadAlgn;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.AESCrypt;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.JKeyStore;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.EncryptData;

/**
 * @author Sochoa
 * @since Jun 1, 2023
 * @version 1.0.0
 */
@Component(name = "RegisterCardPanOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "RegisterCardPanOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "RegisterCardPanOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_registerCardPan_api")})
public class RegisterCardPanOrchestrationCore extends SPJavaOrchestrationBase {
	
	private static final String CLASS_NAME = "RegisterCardPanOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	
	private static final String PANCRYPT = "pancrypt";
	//return variables bag
	private static final String CARD_ID = "cardid";
	private static final String UNIQUE_ID = "uniqueid";
	private static final String SUCCESS = "success";
	private static final String CODE = "code";
	private static final String MESSAJE = "messaje";
	private static final String ACCOUNT = "account";
	//@o return
	private static final String O_UNIQUE_ID = "@o_unique_id";
	private static final String O_CARD_ID = "@o_card_id";
	private static final String O_DESCRIPTION = "@o_description";
	private static final String O_ACCESS_AUTH = "@o_access_auth";
	
	

	private ILogger logger = (ILogger) this.getLogger();
	
	private java.util.Properties properties;
	private AESCrypt cryptaes;
	private JKeyStore jks;
	
	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		properties = aConfigurationReader.getProperties("//property");
		jks = new JKeyStore();
		String jkey = "";
		
		String ctsPath = System.getProperty(COBIS_HOME);
		if (properties != null && properties.get("jksalgncon") != null) {
			Map<String, String> wSecret = getAlgnCredentials(ctsPath+(String)properties.get("jksalgncon"));
			if (wSecret != null) 
			{
				properties.put("user", wSecret.get("user"));
				properties.put("pss", wSecret.get("pass"));
				
				jkey = jks.getSecretKeyStringFromKeyStore(ctsPath+(String)properties.get("jks"), wSecret.get("pass"), wSecret.get("user"));
				
				if(logger.isDebugEnabled())
				{
					logger.logDebug("jks private:"+jkey);
				}
				cryptaes = new AESCrypt(jkey);
			}
		}
		if(logger.isDebugEnabled())
		{
			logger.logDebug("pathprivateKey:"+jkey);
		}
	}
	private Map<String, String> getAlgnCredentials(String algnPath) {

		if(logger.isInfoEnabled())
			logger.logInfo("algn path: " + algnPath);
		if (algnPath == null || "".equals(algnPath)) {
			if(logger.isWarningEnabled())
				logger.logWarning("No secret param in configuration file. Default secret will be used");
			return null;
		}

		String wAlgnPath = algnPath;
		if (!new File(wAlgnPath).exists()) {
			if(logger.isErrorEnabled())
		 		logger.logError("The algn file specified does not exist. Default secret will be used:" + wAlgnPath);
			return null;
		}

		ReadAlgn algn = new ReadAlgn(wAlgnPath);
		if(logger.isInfoEnabled())
			logger.logInfo("Reading algn properties...");
		java.util.Properties propertiesAlgn = algn.leerParametros();
		Map<String, String> secret = new HashMap<String, String>();
		secret.put("user", propertiesAlgn.getProperty("l"));
		secret.put("pass", propertiesAlgn.getProperty("p"));
		secret.put("serv",propertiesAlgn.getProperty("s"));
		return secret;
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if(logger.isDebugEnabled())
		{
			logger.logDebug(CLASS_NAME+" Begin flow, RegisterCardPanOrchestrationCore starts executeJavaOrchestration...");
		}
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
	
		registerCardPan(aBagSPJavaOrchestration, anOriginalRequest);
		
		return processResponseApi(aBagSPJavaOrchestration);
	}
	
	private void registerCardPan(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
	
		String panCrypt = cryptaes.encryptData(anOriginalRequest.readValueParam("@i_card_number"));
		String message = "Success";
		int returnCode = 0;
		boolean success = true;
		aBagSPJavaOrchestration.put(PANCRYPT, panCrypt);
		aBagSPJavaOrchestration.put(CARD_ID, "");
		aBagSPJavaOrchestration.put(UNIQUE_ID, "");
		if(logger.isDebugEnabled())
		{
			logger.logDebug("registerCardPan cript card:"+panCrypt );
		}
		IProcedureResponse responseValidateCustomer = validateCustomer(anOriginalRequest, aBagSPJavaOrchestration);
		if(logger.isDebugEnabled())
		{
			logger.logDebug("responseValidateCustomer card return code:"+responseValidateCustomer.getReturnCode() );
		}
		if(responseValidateCustomer.getReturnCode() == 0)
		{
			
			if(logger.isDebugEnabled())
			{
				String decrypt = cryptaes.decryptData(panCrypt);
				logger.logDebug("decript card:"+decrypt );
			}
						
			//valida si la tarjeta esta en dock // no se recupera el response ya que puede ser un tercero
			validateCardAccount(anOriginalRequest, aBagSPJavaOrchestration);
			
			IProcedureResponse wProcedureResponseLocal = registerPANcard(anOriginalRequest, aBagSPJavaOrchestration);
			if (wProcedureResponseLocal.getReturnCode() != 0) 
			{
				returnCode = wProcedureResponseLocal.getReturnCode();
				success = false;
				if(wProcedureResponseLocal.getMessageListSize() >= 0)
					message = wProcedureResponseLocal.getMessage(1).getMessageText();
				else
					message = "Card registration error.";
				
			}else
			{
				if (logger.isDebugEnabled()) 
				{
					logger.logDebug(O_UNIQUE_ID + wProcedureResponseLocal.readValueParam(O_UNIQUE_ID));
					logger.logDebug(O_CARD_ID + wProcedureResponseLocal.readValueParam(O_CARD_ID));
				}
				aBagSPJavaOrchestration.put(UNIQUE_ID, wProcedureResponseLocal.readValueParam(O_UNIQUE_ID));
				aBagSPJavaOrchestration.put(CARD_ID, wProcedureResponseLocal.readValueParam(O_CARD_ID));
			}
		}
		else
		{
			returnCode = responseValidateCustomer.getReturnCode();
			success =  Boolean.parseBoolean(responseValidateCustomer.readValueParam(O_ACCESS_AUTH));
			if(responseValidateCustomer.readValueParam(O_DESCRIPTION) != null)
				message = responseValidateCustomer.readValueParam(O_DESCRIPTION);
			else
				message = "Error validating client.";
		}
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug(SUCCESS +":" + success);
			logger.logDebug(CODE +":" + returnCode);
			logger.logDebug(MESSAJE +":"  + message);
		}
		aBagSPJavaOrchestration.put(SUCCESS, success);
		aBagSPJavaOrchestration.put(CODE, returnCode);
		aBagSPJavaOrchestration.put(MESSAJE, message);
			
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	public IProcedureResponse processResponseApi(Map<String, Object> aBagSPJavaOrchestration) {
		if(logger.isInfoEnabled())
		{
			logger.logInfo("processResponseApi [INI] --->" );
		}
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get(SUCCESS).toString()));
		data.addRow(row);
		
		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get(CODE).toString()));
		row2.addRowData(2, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get(MESSAJE).toString()));
		data2.addRow(row2);
		
		
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("unique_id", ICTSTypes.SQLVARCHAR, 100));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("card_id", ICTSTypes.SQLVARCHAR, 100));
		
		IResultSetRow row3 = new ResultSetRow();
		row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get(UNIQUE_ID).toString()));
		row3.addRowData(2, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get(CARD_ID).toString()));
		data3.addRow(row3);
		
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
		
		return wProcedureResponse;		
	}	
	
	private IProcedureResponse registerPANcard(IProcedureRequest anOriginalRequest,  Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Begin validateAccountType");
		}
		
		IProcedureRequest procedureRequest = (initProcedureRequest(anOriginalRequest));		
		procedureRequest.setSpName("cob_bvirtual..sp_card_pan");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500165");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500165");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		procedureRequest.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get(CARD_ID)!=null?aBagSPJavaOrchestration.get(CARD_ID).toString():"") ;
		procedureRequest.addInputParam("@i_card_pan", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get(PANCRYPT) != null?aBagSPJavaOrchestration.get(PANCRYPT).toString():null);
		
		procedureRequest.addOutputParam("@o_unique_id", ICTSTypes.SQLINT4, "0");
		procedureRequest.addOutputParam("@o_card_id", ICTSTypes.SQLVARCHAR, "X");
	    
		IProcedureResponse wProcedureResponseLocal = executeCoreBanking(procedureRequest);
		
	    if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Ending flow, registerPANcard " + wProcedureResponseLocal.getProcedureResponseAsString());
		}
	    return wProcedureResponseLocal;
	}
	
	private IProcedureResponse validateCardAccount(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration)
	{
	    Integer code = 0;
        String message = "Success";
        String idCard = null;
        String ctaBen = anOriginalRequest.readValueParam("@i_card_number");
					
        if(logger.isInfoEnabled())
		{
			logger.logInfo("ctaBen:"+ctaBen );
		}
		IProcedureResponse anProcedureResponse =  new ProcedureResponseAS();

		
		Map<String, Object> dataMapEncrypt = EncryptData.encryptWithAESGCM(ctaBen, properties.get("publicKeyDock").toString());
		if(logger.isDebugEnabled())
		{
			logger.logDebug("[res]: + ctaDestEncrypt " + dataMapEncrypt);
		}
		
		aBagSPJavaOrchestration.putAll(dataMapEncrypt);
	
		IProcedureResponse anProcedureResPan = findCardByPanConector(anOriginalRequest, aBagSPJavaOrchestration);
		
		if(anProcedureResPan != null){
			
			idCard = anProcedureResPan.readValueParam("@o_id_card");
			if(logger.isDebugEnabled())
			{
				logger.logDebug("[res]: + idCard: " + idCard);
			}
			if (idCard == null || "Non-existent".equals(idCard) ){
				code = 50201;
				message = "Non-existent card number";
			}else
			{
				aBagSPJavaOrchestration.put(CARD_ID, idCard);
				
				IProcedureResponse anProcedureResFind = findCardId(anOriginalRequest, anProcedureResPan, aBagSPJavaOrchestration);
				
				if (anProcedureResFind.getResultSets() != null && anProcedureResFind.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
					anOriginalRequest.setValueParam("@i_cuentaBeneficiario", (String) aBagSPJavaOrchestration.get("o_account"));
					if(logger.isDebugEnabled())
					{
						logger.logDebug("ACCOUNT RESPONSE:: " + anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
					}
				}else{
					code = 50201;
					message = "Non-existent card number";
				}
			}
		}else{
			code = 50200;
			message = "Connection failure with provider";
		}
		
		anProcedureResponse.setReturnCode(code);
		if(code != 0)
		{
			anProcedureResponse.addMessage(code, message);
		}
		return anProcedureResponse;
	}
	
	private IProcedureResponse findCardByPanConector(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorAccountResponse = null;

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en findCardByPanConector");
		}
		try {
			// PARAMETROS DE ENTRADA		
			anOriginalRequest.addInputParam("@i_pan", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("pan"));
			anOriginalRequest.addInputParam("@i_iv", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("iv"));
			anOriginalRequest.addInputParam("@i_aes", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("aes"));
			anOriginalRequest.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, "GCM");
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "FCP");

			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=SpeiInTransferOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			anOriginalRequest.setSpName("cob_procesador..sp_transfer_spei_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");			
			
			// SE EJECUTA CONECTOR
			connectorAccountResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("findCardByPanConector response: " + connectorAccountResponse);

			if (connectorAccountResponse.readValueParam("@o_status") != null)
				aBagSPJavaOrchestration.put("@o_status", connectorAccountResponse.readValueParam("@o_status"));
			else
				aBagSPJavaOrchestration.put("@o_status", "null");
			
			if (connectorAccountResponse.readValueParam("@o_id_card") != null)
				aBagSPJavaOrchestration.put("@o_id_card", connectorAccountResponse.readValueParam("@o_id_card"));
			else
				aBagSPJavaOrchestration.put("@o_id_card", "null");
			
			if (connectorAccountResponse.readValueParam("@o_response_find_card") != null)
				aBagSPJavaOrchestration.put("@o_response_find_card", connectorAccountResponse.readValueParam("@o_response_find_card"));
			else
				aBagSPJavaOrchestration.put("@o_response_find_card", "null");
			
			if (connectorAccountResponse.readValueParam("@o_request_find_card") != null)
				aBagSPJavaOrchestration.put("@o_request_find_card", connectorAccountResponse.readValueParam("@o_request_find_card"));
			else
				aBagSPJavaOrchestration.put("@o_request_find_card", "null");


		} catch (Exception e) {
			e.printStackTrace();
			aBagSPJavaOrchestration.put("@o_card_number", "null");
			connectorAccountResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de findCardByPanConector");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> findCardByPanConector");
			}
		}
		return connectorAccountResponse;
	}

	private IProcedureResponse findCardId(IProcedureRequest aRequest, IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration) {
	
		IProcedureRequest request = new ProcedureRequestAS();
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en findCardId");
		}
	
		request.setSpName("cob_bvirtual..sp_val_data_tarjeta");
	
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_id_dock", ICTSTypes.SQLVARCHAR, aResponse.readValueParam("@o_id_card"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "C");
		
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("o_account es " +  wProductsQueryResp.readValueParam("@o_account"));
		}
		
		aBagSPJavaOrchestration.put(ACCOUNT, wProductsQueryResp.readValueParam("@o_account"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking findCardId DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de findCardId");
		}
		return wProductsQueryResp;
	}
	
	private IProcedureResponse validateCustomer(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
	
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalReq);
		
		procedureRequest.setSpName("cob_procesador..sp_customer_validate");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18700116");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700116");
		anOriginalReq.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700116");
		anOriginalReq.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");
		anOriginalReq.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
		anOriginalReq.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		anOriginalReq.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=CustomerValidateOrchestrationCore)");
		anOriginalReq.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		procedureRequest.addInputParam("@i_wm_token", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@x_auth_token"));
		procedureRequest.addInputParam("@i_wm_session", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@x_session_id"));
		procedureRequest.addInputParam("@i_wm_customer_id", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@x_customer_id"));
		procedureRequest.addOutputParam(O_ACCESS_AUTH, ICTSTypes.SQLBIT, "");
		procedureRequest.addOutputParam(O_DESCRIPTION, ICTSTypes.SQLVARCHAR, "");

		IProcedureResponse procedureResponse = executeCoreBanking(procedureRequest);
		
		if(logger.isDebugEnabled())
			logger.logInfo("Response validateCustomer: "+procedureResponse.getProcedureResponseAsString());
		
		return procedureResponse;
	}
	


}
