/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.update.card.dock.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.IProvider;
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

/**
 * @author nelsonJ
 * @since May 2, 2023
 * @version 1.0.0
 */
@Component(name = "UpdateCardDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateCardDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateCardDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_card_status_api")
})
public class UpdateCardDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "UpdateCardDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String CREATE_PERSON	= "CREATE_PERSON";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, CustomerCardApplication start.");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = updaterCardStatus(anOriginalRequest, aBagSPJavaOrchestration);
		
		//if(anProcedureResponse.getReturnCode()==0){
			
		anProcedureResponse = processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
		//}
		
		return anProcedureResponse;
		//return processResponseCardAppl(anProcedureResponse);
	}
	
	private IProcedureResponse updaterCardStatus(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updaterCardStatus: " );
		}
		
		aBagSPJavaOrchestration.put("ente_mis", aRequest.readValueParam("@i_ente"));
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		String flag = "S";
		
		wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);

		if(wAccountsResp.getResultSets().size()>1 && !wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			return wAccountsResp;
		}
		
	 	String accreditation = aBagSPJavaOrchestration.get("o_accreditation").toString();
	 	
	 	if (logger.isDebugEnabled()) {
			 logger.logDebug("accreditation_1: " + accreditation);
			 logger.logDebug("o_type_card: " + aBagSPJavaOrchestration.get("o_type_card").toString());
			 logger.logDebug("mode: " + aBagSPJavaOrchestration.get("mode").toString());
			 logger.logDebug("o_incomm_card: " + aBagSPJavaOrchestration.get("o_incomm_card").toString());
	 	}
		
		//REASIGNACIÓN Y CANCELACIÓN DE TARJETAS
		if(aBagSPJavaOrchestration.get("o_type_card").toString().equals("PHYSICAL") && aBagSPJavaOrchestration.get("mode").toString().equals("N")) {
			
			//FLUJO INCOMM
			if (aBagSPJavaOrchestration.get("o_incomm_card").toString().equals("Y")) {
				
				IProcedureResponse wAccountsRespIncomm = new ProcedureResponseAS();
				
				wAccountsRespIncomm = executeIncommConector(aRequest, aBagSPJavaOrchestration);
			
				if (logger.isDebugEnabled()) {
					 logger.logDebug("Response Corebanking executeIncommConnector: " + wAccountsRespIncomm.getProcedureResponseAsString());
					 logger.logDebug("wAccountsRespIncomm.toString(): " + wAccountsRespIncomm.toString());
					 logger.logDebug("wAccountsRespIncomm.getResultSets(): " + wAccountsRespIncomm.getResultSets().toString());					 
					 logger.logDebug("wAccountsRespIncomm.getReturnCode(): " + wAccountsRespIncomm.getReturnCode());
					 
					 logger.logDebug("aRequest.getProcedureRequestAsString(): " + aRequest.getProcedureRequestAsString());
					 logger.logDebug("aRequest.toString(): " + aRequest.toString());
					 
					 logger.logDebug("aBagSPJavaOrchestration.toString(): " + aBagSPJavaOrchestration.toString());
				}

				registerLogIncommBd(aRequest, wAccountsRespIncomm, aBagSPJavaOrchestration);
				 
				switch (wAccountsRespIncomm.getReturnCode()) {				 
					case 0:						 
						if (!validateActivationDate(wAccountsRespIncomm))	
							accreditation = "N";						 
						break;	
					default:
						return wAccountsRespIncomm;
				}
			}
			
			if (logger.isDebugEnabled()) 
				 logger.logDebug("accreditation_2: " + accreditation);
			
			if(aBagSPJavaOrchestration.get("o_assigned").toString().equals("Y")) {
				
				if (aBagSPJavaOrchestration.get("o_cancel").toString().equals("Y")) {
					
					cancelCardAtm(aRequest, aBagSPJavaOrchestration);
					
					IProcedureResponse wAccountsRespDock = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
					
					registerLogBd(wAccountsRespDock, aBagSPJavaOrchestration);
				} 
			}
			
			//REALIZAR SOLICITUD TEMPORAL
			registerAtm(aRequest, aBagSPJavaOrchestration);
		}
		
		//ACTIVAR TARJETA COBIS
		if(aBagSPJavaOrchestration.containsKey("o_status_atm") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			if(aBagSPJavaOrchestration.get("o_status_atm").toString().equals("reg"))
			{
				if(!aBagSPJavaOrchestration.get("o_card_available").equals("X")){
					IProcedureResponse wAccountsRespDock = registerAtmCobis(aBagSPJavaOrchestration);
					
					if(wAccountsRespDock.getReturnCode()==0){
						wAccountsRespDock = executeAssingCard(aRequest, aBagSPJavaOrchestration);
						registerAssingLogBd(wAccountsRespDock,aBagSPJavaOrchestration);
						registerLogBd(wAccountsRespDock, aBagSPJavaOrchestration);
						flag = "N";
						aBagSPJavaOrchestration.put("flag_log",flag);
						
						if(wAccountsRespDock.getReturnCode()!=0)
							return wAccountsRespDock;
					}
					else{
						return wAccountsRespDock;
					}
				} else if (wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
						IProcedureResponse wAccountsRespDock = registerAtmCobis(aBagSPJavaOrchestration);
					
						if(wAccountsRespDock.getReturnCode()==0){
							wAccountsRespDock = executeAssingCard(aRequest, aBagSPJavaOrchestration);
							registerAssingLogBd(wAccountsRespDock,aBagSPJavaOrchestration);
							registerLogBd(wAccountsRespDock, aBagSPJavaOrchestration);
							flag = "N";
							aBagSPJavaOrchestration.put("flag_log",flag);
							
							if(wAccountsRespDock.getReturnCode()!=0)
								return wAccountsRespDock;
						}
						else{
							return wAccountsRespDock;
						}
				}
			}
		}
		
		logger.logInfo(
				CLASS_NAME + " code resp card dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		//if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
		if (wAccountsResp.getReturnCode()==0 ){ 
				if(!wAccountsResp.readValueParam("@o_id_card_atm").equals("0") && flag.equals("S")) {
					IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
					wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
					if(aRequest.readValueParam("@i_card_status").equals("C")){
						cancelCardAtm(aRequest, aBagSPJavaOrchestration);
					}
					else{
						updateStatusAtm(aRequest, aBagSPJavaOrchestration);	
					}
					//updateStatusAtm(aRequest, aBagSPJavaOrchestration);
					return wAccountsRespInsert;
				}else if(wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && !wAccountsResp.readValueParam("@o_id_card_dock").toString().equals("X") && flag.equals("S")) {
					IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
					wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
					if(aRequest.readValueParam("@i_card_status").equals("C")){
						cancelCardAtm(aRequest, aBagSPJavaOrchestration);
					}
					else{
						updateStatusAtm(aRequest, aBagSPJavaOrchestration);	
					}
					return wAccountsRespInsert;
				}
		}
		
		if (logger.isDebugEnabled()) 
			 logger.logDebug("accreditation_3: " + accreditation);
		
		if (accreditation == "Y") 
			accountAccreditation(aRequest,  aBagSPJavaOrchestration);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updaterCardStatus");
		}

		return wAccountsResp;
	}
	
	public boolean validateActivationDate(IProcedureResponse reponseIncommCard) {
				
		try {
			
            // Parsear el JSON
            JSONObject jsonObject = new JSONObject(reponseIncommCard.readValueParam("@o_responseGetStatus").toString());

            // Obtener el objeto "metaFields"
            JSONObject productResp = jsonObject.getJSONObject("RetailTransactionTVResponse").getJSONObject("productResp");
            JSONObject metaFields = productResp.getJSONObject("inventoryRespInfo").getJSONObject("metaFields");

            // Obtener el array "metafield"
            JSONArray metafieldArray = metaFields.getJSONArray("metafield");
            
            String value = null;
            
            // Iterar sobre los elementos del array para encontrar el objeto deseado
            for (int i = 0; i < metafieldArray.length(); i++) {
            	
                JSONObject metafield = metafieldArray.getJSONObject(i);
   
                value = metafield.getString("value");  
            }
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            if (logger.isDebugEnabled()) 
            	logger.logDebug("value: " + value.toString());
            
            Date activationDate = format.parse(value);
            
            if (logger.isDebugEnabled()) 
            	logger.logDebug("activationDate: " + activationDate.toString());
            
            Calendar cal = Calendar.getInstance();
            
            cal.setTime(activationDate);
            cal.add(Calendar.DAY_OF_MONTH, 30);
            
            Date fechaLimite = cal.getTime();
            Date fechaActual = new Date();
            
            if (logger.isDebugEnabled()) {
            	logger.logDebug("fechaLimite: " + fechaLimite.toString());
            	logger.logDebug("fechaActual: " + fechaActual.toString());
    		}

            if (fechaLimite.after(fechaActual)) {
            	
                return true;
             }
           
        } catch (JSONException  e) {
        	
            e.printStackTrace();
            
        } catch (ParseException e) {
	
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void accountAccreditation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en accountAccreditation");
		}

		request.setSpName("cobis..sp_account_credit_operation_central_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500161");
		
		request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		request.addInputParam("@i_amount", ICTSTypes.SQLMONEY, "50");
	
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking accountAccreditation: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de accountAccreditation");
		}
	}
	
	private Integer registerAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		Integer appNumber = 0;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAtm");
		}
		
		getDataClient(aRequest, aBagSPJavaOrchestration);
		
		getAvailableCards(aRequest, aBagSPJavaOrchestration);

		getQueryAgreementById(aRequest, aBagSPJavaOrchestration, 1, "MCC");
		
		appNumber = createHeader(aRequest, aBagSPJavaOrchestration);
		
		if (appNumber != 0) {
			
			assignDetail(aRequest, aBagSPJavaOrchestration, appNumber, "MCC", 1);

			assignAccount(aRequest, aBagSPJavaOrchestration, appNumber, 1);
		} 
		

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerAtm");
		}
		
		return appNumber;
	}
	
	private IProcedureResponse getDataClient(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataClient");
		}

		request.setSpName("cobis..sp_get_data_client_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		
		request.addOutputParam("@o_curp", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_full_name", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_birth_date", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_cod_document", ICTSTypes.SQLINT4, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_curp", wProductsQueryResp.readValueParam("@o_curp"));
		aBagSPJavaOrchestration.put("o_full_name", wProductsQueryResp.readValueParam("@o_full_name"));
		aBagSPJavaOrchestration.put("o_birth_date", wProductsQueryResp.readValueParam("@o_birth_date"));
		aBagSPJavaOrchestration.put("o_cod_document", wProductsQueryResp.readValueParam("@o_cod_document"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataClient");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse getAvailableCards(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getAvailableCards");
		}

		request.setSpName("cob_atm..sp_atm_tipo_tarjeta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_banco", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "H");
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, "MCC");
		request.addInputParam("@i_soporta_tj", ICTSTypes.SQLINT4, "2");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "V");
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16006");
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getAvailableCards");
		}
		
		if(!wProductsQueryResp.getResultSetRowColumnData(1, 1, 40).isNull())
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 41) daily_transfer" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 41).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 42) month_transfer" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 42).getValue());
			
			aBagSPJavaOrchestration.put("daily_transfer", wProductsQueryResp.getResultSetRowColumnData(1, 1, 41).getValue());
			aBagSPJavaOrchestration.put("month_transfer", wProductsQueryResp.getResultSetRowColumnData(1, 1, 42).getValue());
		}
		
		return wProductsQueryResp;
	}
	
	private IProcedureResponse getQueryAgreementById(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration, int agreementId, String cardType) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getQueryAgreementById");
		}

		request.setSpName("cob_atm..sp_atm_convenio_servicio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, "MCC");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tipo", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_convenio", ICTSTypes.SQLINT4, "1");
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16050");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT4, "1");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getQueryAgreementById");
		}

		if (!wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).isNull()) {
			
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) period" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 7) quota_type" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 7).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 3) atm_limit: " + wProductsQueryResp.getResultSetRowColumnData(1, 1, 3).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 5) post_limit: " + wProductsQueryResp.getResultSetRowColumnData(1, 1, 5).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 4) atm_int_limit" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 4).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 6) post_int_limit" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 6).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) transfer_limit" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			
			aBagSPJavaOrchestration.put("period", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("quota_type", wProductsQueryResp.getResultSetRowColumnData(1, 1, 7).getValue());
			aBagSPJavaOrchestration.put("atm_limit", wProductsQueryResp.getResultSetRowColumnData(1, 1, 3).getValue());
			aBagSPJavaOrchestration.put("post_limit", wProductsQueryResp.getResultSetRowColumnData(1, 1, 5).getValue());
			aBagSPJavaOrchestration.put("atm_int_limit", wProductsQueryResp.getResultSetRowColumnData(1, 1, 4).getValue());
			aBagSPJavaOrchestration.put("post_int_limit", wProductsQueryResp.getResultSetRowColumnData(1, 1, 6).getValue());
			aBagSPJavaOrchestration.put("transfer_limit", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}

		return wProductsQueryResp;
	}
	
	private Integer createHeader(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		int resApplication;

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en createHeader");
		}

		request.setSpName("cob_atm..sp_atm_graba_sol_tmp");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_tipo_solicitud", ICTSTypes.SQLVARCHAR, "ETI");
		request.addInputParam("@i_tarjeta_prn", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_ofi_org", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "CREADO DESDE API");
		request.addInputParam("@i_periodo", ICTSTypes.SQLVARCHAR, "D");
		request.addInputParam("@i_persona_retira", ICTSTypes.SQLVARCHAR, "PROPIETARIO");
		request.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, "C");
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16558");
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		//request.addInputParam("@i_convenio", ICTSTypes.SQLVARCHAR, "0");
		//request.addInputParam("@i_direccion_ent", ICTSTypes.SQLVARCHAR, "D");
		
		request.addOutputParam("@o_numero", ICTSTypes.SQLINT4, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_numero_ap", wProductsQueryResp.readValueParam("@o_numero"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de createHeader");
		}

		resApplication = wProductsQueryResp.readValueParam("@o_numero")!=null ? Integer.valueOf(wProductsQueryResp.readValueParam("@o_numero")):0;
		
		return resApplication;
	}
	
	private IProcedureResponse assignDetail(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration, int appNumber, String cardType, int convenio) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en assignDetail");
		}

		request.setSpName("cob_atm..sp_atm_agrega_det");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_convenio", ICTSTypes.SQLINT4, String.valueOf(convenio));
		request.addInputParam("@i_cupo_atm_i", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("atm_int_limit").toString());
		request.addInputParam("@i_cupo_atm_n", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("atm_limit").toString());
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_nombre_tarjeta", ICTSTypes.SQLVARCHAR, "INNOMINADA"); //Personalizada??
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, cardType);
		request.addInputParam("@i_ced_ruc", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_curp").toString());
		request.addInputParam("@i_tipo_costo", ICTSTypes.SQLVARCHAR, "CONC");
		request.addInputParam("@i_trans_diarias", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("daily_transfer").toString());
		request.addInputParam("@i_det_solicitud", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_seguro", ICTSTypes.SQLCHAR, "N");
		request.addInputParam("@i_principal", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_trans_mensuales", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("month_transfer").toString());
		request.addInputParam("@i_nombre_cliente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_full_name").toString());
		request.addInputParam("@i_numero", ICTSTypes.SQLINT4, String.valueOf(appNumber));
		request.addInputParam("@i_propietario", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cupo_periodo", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("period").toString());
		request.addInputParam("@i_cupo_pos_i", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("post_int_limit").toString());
		request.addInputParam("@i_cupo_pos_n", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("post_limit").toString());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		request.addInputParam("@i_nombre_corto", ICTSTypes.SQLVARCHAR, "WALMART"); //SALUDOBM
		request.addInputParam("@i_cupo_trans", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("transfer_limit").toString());
		request.addInputParam("@i_tipo_cliente", ICTSTypes.SQLCHAR, "P");
		request.addInputParam("@i_tipo_cupo", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("quota_type").toString());

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16559");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de assignDetail");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse assignAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration, int appNumber, int convenio) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en assignAccount");
		}

		request.setSpName("cob_atm..sp_atm_agrega_cuenta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		request.addInputParam("@i_det_solicitud", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_prod_banc", ICTSTypes.SQLINT4, "3");
		request.addInputParam("@i_categoria", ICTSTypes.SQLCHAR, "C");
		request.addInputParam("@i_prod_cobis", ICTSTypes.SQLINT4, "4");
		request.addInputParam("@i_principal", ICTSTypes.SQLCHAR, "P");
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_solicitud", ICTSTypes.SQLINT4, String.valueOf(appNumber));
		request.addInputParam("@i_cupo_offline", ICTSTypes.SQLMONEY, getQueryExtractionLimit(aBagSPJavaOrchestration.get("period").toString(), Integer.parseInt(aBagSPJavaOrchestration.get("post_limit").toString()), "POS", "N").toString());
		request.addInputParam("@i_cupo_online", ICTSTypes.SQLMONEY, getQueryExtractionLimit(aBagSPJavaOrchestration.get("period").toString(), Integer.parseInt(aBagSPJavaOrchestration.get("atm_limit").toString()), "ATM", "N").toString());
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");
		request.addInputParam("@i_orden", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_periodo", ICTSTypes.SQLCHAR, aBagSPJavaOrchestration.get("period").toString());
		request.addInputParam("@i_producto", ICTSTypes.SQLINT2, "4");
		request.addInputParam("@i_cupo_transferencia", ICTSTypes.SQLMONEY, getQueryExtractionLimit(aBagSPJavaOrchestration.get("period").toString(), Integer.parseInt(aBagSPJavaOrchestration.get("transfer_limit").toString()), "TRN", "N").toString());

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16560");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de assignAccount");
		}
		
		return wProductsQueryResp;
	}
	
	private Double getQueryExtractionLimit(String period, int limiteId, String quotaType, String type) {

		Double monto = null;
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getQueryExtractionLimit");
		}

		request.setSpName("cob_atm..sp_atm_limite_extraccion");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_codigo", ICTSTypes.SQLINT4, String.valueOf(limiteId));
		request.addInputParam("@i_modo", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		request.addInputParam("@i_periodo", ICTSTypes.SQLVARCHAR, period);
		request.addInputParam("@i_tipo_cupo", ICTSTypes.SQLVARCHAR, quotaType);
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, type);
	
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16040");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getQueryExtractionLimit");
		}
		
		if(!wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).isNull())
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) id" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 3) monto" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 3).getValue());
			monto = Double.parseDouble(wProductsQueryResp.getResultSetRowColumnData(1, 1, 3).getValue());
		}
		
		return monto;
	}
	
	private IProcedureResponse executeUpdateCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorCardResponse = null;
		String idCardDock = null, status = null, reasonStatus = null, acccountNumber = null;
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		if(aBagSPJavaOrchestration.get("o_cancel").toString().equals("Y")) {
			
			idCardDock = aBagSPJavaOrchestration.containsKey("o_assigned_card")? aBagSPJavaOrchestration.get("o_assigned_card").toString():null;
			status = "CANCELED";
			reasonStatus = "OWNER_REQUEST";
			
		} else {
			
			idCardDock = aBagSPJavaOrchestration.containsKey("o_id_card_dock")? aBagSPJavaOrchestration.get("o_id_card_dock").toString():null;
			status = aBagSPJavaOrchestration.containsKey("o_detail_status")? aBagSPJavaOrchestration.get("o_detail_status").toString():null;
			reasonStatus = aBagSPJavaOrchestration.containsKey("o_det_reason_stat")? aBagSPJavaOrchestration.get("o_det_reason_stat").toString():"X";
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeUpdateCard " + acccountNumber);
		}
		
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("ente_mis").toString());
			anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, idCardDock);
			anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, status);
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, reasonStatus);
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_type_card"));
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UCS");
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateCardDockOrchestrationCore)");
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
			anOriginalRequest.setSpName("cob_procesador..sp_card_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("cardDock--> request update card app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("jcos--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_card_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de updateCardStatusExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de updateCardStatusExecution");
			}
		}

		return connectorCardResponse;

	}

	private IProcedureResponse executeAssingCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String typeCard = null;
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		aBagSPJavaOrchestration.remove("trn_virtual");
		typeCard = anOriginalReq.readValueParam("@i_type_card").equals("VI")?"VIRTUAL":"PHYSICAL";
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeAssingCard typeCard: " + typeCard);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
			if (typeCard.equals("PHYSICAL")){
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_card_available").toString());
			}else{
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_card_dock").toString());
				anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "NORMAL");
			}
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addInputParam("@i_id_person_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_person_dock").toString());
			anOriginalRequest.addInputParam("@i_id_account_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_account_dock").toString());
			
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, typeCard);
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "ASC");
			anOriginalRequest.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateCardDockOrchestrationCore)");
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
			anOriginalRequest.setSpName("cob_procesador..sp_card_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("cardDock--> request executeAssingCard app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("Dock--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_person_id") != null)
				aBagSPJavaOrchestration.put("o_person_id", connectorCardResponse.readValueParam("@o_person_id"));
			else
				aBagSPJavaOrchestration.put("o_person_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_account_id") != null)
				aBagSPJavaOrchestration.put("o_account_id", connectorCardResponse.readValueParam("@o_account_id"));
			else
				aBagSPJavaOrchestration.put("o_account_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_type") != null)
				aBagSPJavaOrchestration.put("o_card_type", connectorCardResponse.readValueParam("@o_card_type"));
			else
				aBagSPJavaOrchestration.put("o_card_type", "null");
			
			if (connectorCardResponse.readValueParam("@o_assign_date") != null)
				aBagSPJavaOrchestration.put("o_assign_date", connectorCardResponse.readValueParam("@o_assign_date"));
			else
				aBagSPJavaOrchestration.put("o_assign_date", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestAssingCard") != null)
				aBagSPJavaOrchestration.put("o_requestAssingCard", connectorCardResponse.readValueParam("@o_requestAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_requestAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseAssingCard") != null)
				aBagSPJavaOrchestration.put("o_responseAssingCard", connectorCardResponse.readValueParam("@o_responseAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_responseAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_status") != null)
				aBagSPJavaOrchestration.put("o_card_status", connectorCardResponse.readValueParam("@o_card_status"));
			else
				aBagSPJavaOrchestration.put("o_card_status", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_requestUpdateCard", connectorCardResponse.readValueParam("@o_requestUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_requestUpdateCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_responseUpdateCard", connectorCardResponse.readValueParam("@o_responseUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_responseUpdateCard", "null");
			

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_success"));
			else
				aBagSPJavaOrchestration.put("o_success", "null");

			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de executeAssingCard");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de executeAssingCard");
			}
		}

		return connectorCardResponse;

	}
	
	private IProcedureResponse executeIncommConector(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		IProcedureResponse connectorIncommCardResponse = new ProcedureResponseAS();
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeIncommConector");
		}
		
		try {
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorIncomm)");
			
			anOriginalRequest.setSpName("cob_procesador..sp_con_incomm");
			
			anOriginalRequest.addFieldInHeader(IProvider.EXTERNAL_PROVIDER, ICOBISTS.HEADER_STRING_TYPE, "1");
			anOriginalRequest.addFieldInHeader("channel", ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.ID_CHANNEL_SERVICE_INTEGRATION);
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_STRING_TYPE, "18500160");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500160");
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorIncomm)");
			anOriginalRequest.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
			anOriginalRequest.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);
			
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");			
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");
			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500160");
			
			//PARÁMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_van", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_incomm_card_id").toString());	
			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500160");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500160");

			logger.logDebug("incommCard--> request executeIncommConector app: " + anOriginalRequest.toString());
			
			// SE EJECUTA CONECTOR
			connectorIncommCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Incomm--> connectorIncommResponse: " + connectorIncommCardResponse);
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_responseIncomm") != null) {
				
				aBagSPJavaOrchestration.put("o_response_incomm", connectorIncommCardResponse.readValueParam("@o_responseIncomm"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_response_incomm", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_requestGetStatus") != null) {
				
				aBagSPJavaOrchestration.put("o_request_get_status", connectorIncommCardResponse.readValueParam("@o_requestGetStatus"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_request_get_status", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_responseGetStatus") != null) {
				
				aBagSPJavaOrchestration.put("o_response_get_status", connectorIncommCardResponse.readValueParam("@o_responseGetStatus"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_response_get_status", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_requestRedeem") != null) {
				
				aBagSPJavaOrchestration.put("o_request_redeem", connectorIncommCardResponse.readValueParam("@o_requestRedeem"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_request_redeem", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_responseRedeem") != null) {
				
				aBagSPJavaOrchestration.put("o_response_redeem", connectorIncommCardResponse.readValueParam("@o_responseRedeem"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_response_redeem", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_requestCancel") != null) {
				
				aBagSPJavaOrchestration.put("o_request_cancel", connectorIncommCardResponse.readValueParam("@o_requestCancel"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_request_cancel", "null");
			}
			
			if (connectorIncommCardResponse.readValueParam("@o_responseCancel") != null) {
				
				aBagSPJavaOrchestration.put("o_response_cancel", connectorIncommCardResponse.readValueParam("@o_responseCancel"));
				
			} else {
				
				aBagSPJavaOrchestration.put("o_response_cancel", "null");
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			logger.logInfo(CLASS_NAME +" Error Catastrófico de executeIncommConector: "+e.getMessage());
			
			connectorIncommCardResponse.setReturnCode(500);
			connectorIncommCardResponse.setText(e.getMessage());
			
		} finally {
			
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de executeIncommConector");
			}
		}

		return connectorIncommCardResponse;

	}
	
	private IProcedureResponse getDataCardDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		String mode;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataCardDock");
		}
		
		if(aRequest.readValueParam("@i_mode")!=null){
			mode = aRequest.readValueParam("@i_mode").equals("null")?"X":aRequest.readValueParam("@i_mode").toString();
			aBagSPJavaOrchestration.put("mode", mode);
		}
		else
			mode= "X";

		logger.logInfo(CLASS_NAME + " mode_getDataCardDock" + mode);
		
		request.setSpName("cob_atm..sp_get_data_card_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		
		request.addInputParam("@x_val", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_val")!=null?aRequest.readValueParam("@x_val"):null);

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_status"));
		request.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_status_reason"));
		request.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_type_card"));
		request.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mode"));
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		
		request.addOutputParam("@o_id_card_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_detail_status", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_det_reason_stat", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_status_atm", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_card_available", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_person_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_account_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_card_atm", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_assigned_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_assigned_card_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_incomm_card_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_cancel", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_assigned", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_incomm_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_accreditation", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_type_card", wProductsQueryResp.readValueParam("@o_type_card"));
		aBagSPJavaOrchestration.put("o_status_atm", wProductsQueryResp.readValueParam("@o_status_atm"));
		aBagSPJavaOrchestration.put("o_card_available", wProductsQueryResp.readValueParam("@o_card_available"));
		aBagSPJavaOrchestration.put("o_id_person_dock", wProductsQueryResp.readValueParam("@o_id_person_dock"));
		aBagSPJavaOrchestration.put("o_id_account_dock", wProductsQueryResp.readValueParam("@o_id_account_dock"));
		aBagSPJavaOrchestration.put("o_id_card_atm", wProductsQueryResp.readValueParam("@o_id_card_atm"));
		aBagSPJavaOrchestration.put("account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_assigned_card", wProductsQueryResp.readValueParam("@o_assigned_card"));
		aBagSPJavaOrchestration.put("o_assigned_card_id", wProductsQueryResp.readValueParam("@o_assigned_card_id"));
		aBagSPJavaOrchestration.put("o_incomm_card_id", wProductsQueryResp.readValueParam("@o_incomm_card_id"));
		aBagSPJavaOrchestration.put("o_cancel", wProductsQueryResp.readValueParam("@o_cancel"));
		aBagSPJavaOrchestration.put("o_assigned", wProductsQueryResp.readValueParam("@o_assigned"));
		aBagSPJavaOrchestration.put("o_incomm_card", wProductsQueryResp.readValueParam("@o_incomm_card"));
		aBagSPJavaOrchestration.put("o_accreditation", wProductsQueryResp.readValueParam("@o_accreditation"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getDataCardDock: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataCardDock");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse updateStatusAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		Integer trn = 0;
		String process = null, reason = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateStatusAtm");
		}

		if(aRequest.readValueParam("@i_card_status").equals("N"))
		{
			trn = 16537;
			process = "LBW";
			reason = "SCL";
		}else if (aRequest.readValueParam("@i_card_status").equals("B")){
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}else{
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}
		
		
		request.setSpName("cob_atm..sp_atm_bloqueo_tarjeta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		//request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, process);
		request.addInputParam("@i_banco", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLVARCHAR,aBagSPJavaOrchestration.get("o_id_card_atm").toString());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, reason);
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_retener", ICTSTypes.SQLVARCHAR, "N");
		
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, String.valueOf(trn));
		
		request.addOutputParam("@o_secuencial", ICTSTypes.SQLVARCHAR, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateStatusAtm: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateStatusAtm");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse cancelCardAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		
		Integer trn = 16507;
		String reason = "SCL";
		String card = null;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en cancelCardAtm");
		}
		
		if(aBagSPJavaOrchestration.get("o_cancel").toString().equals("Y")) {
			
			card = aBagSPJavaOrchestration.get("o_assigned_card_id").toString();
			
		} else {
			
			card = aBagSPJavaOrchestration.get("o_id_card_atm").toString();
		}
		
		request.setSpName("cob_atm..sp_atm_elimina_tarj");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_tipo_sol_org", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, card);
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, reason);
		request.addInputParam("@i_observaciones", ICTSTypes.SQLVARCHAR, "Eliminacion Tarjeta API");
		request.addInputParam("@i_proceso_val", ICTSTypes.SQLVARCHAR, "CAN");
		
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, String.valueOf(trn));
		request.addInputParam("@s_org", ICTSTypes.SQLCHAR, "");
		
		//request.addOutputParam("@o_secuencial", ICTSTypes.SQLVARCHAR, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking cancelCardAtm: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de cancelCardAtm");
		}

		return wProductsQueryResp;
	}
	
	private void registerLogIncommBd(IProcedureRequest aRequest, IProcedureResponse reponseIncommCard, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerLogIncommBd");
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("aRequest: " + aRequest.toString());
			logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_van", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_incomm_card_id").toString());
		request.addInputParam("@i_res_incomm", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_response_incomm").toString());
		request.addInputParam("@i_req_status", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_request_get_status").toString());
		request.addInputParam("@i_res_status", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_response_get_status").toString());
		request.addInputParam("@i_req_redeem", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_request_redeem").toString());
		request.addInputParam("@i_res_redeem", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_response_redeem").toString());
		request.addInputParam("@i_req_cancel", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_request_cancel").toString());
		request.addInputParam("@i_res_cancel", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_response_cancel").toString());
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "ICL");
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogIncommBd");
		}
	}
	
	private void registerLogBd(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerLogBd");
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
        String cardId = null;
        
        if(aBagSPJavaOrchestration.containsKey("o_type_card") ) {
			if(!aBagSPJavaOrchestration.get("mode").toString().equals("X") ) {
				if(aBagSPJavaOrchestration.get("o_type_card").toString().equals("VIRTUAL")) {
					
					cardId = aBagSPJavaOrchestration.get("o_id_card_dock").toString();
					
				} else {
					
					cardId = aBagSPJavaOrchestration.get("o_card_available").toString();
					
					if(aBagSPJavaOrchestration.get("o_cancel").toString().equals("Y")) {
						
						cardId = aBagSPJavaOrchestration.get("o_assigned_card").toString();
						
						aBagSPJavaOrchestration.put("o_cancel", "N");
					}
				}
				
			} else {
				
				cardId = aBagSPJavaOrchestration.get("o_id_card_dock").toString();
			}
		} 
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UCS");
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, cardId);
		request.addInputParam("@i_request_td", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestUpdateCard"));
		request.addInputParam("@i_estado_tarjeta", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_status"));
		request.addInputParam("@i_estado_upd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_success"));

		//String message = reponseCard.readValueParam("@o_responseUpdateCard")!=null?reponseCard.readValueParam("@o_responseUpdateCard"):reponseCard.getResultSetRowColumnData(2, 1, 2).getValue();
		String message = reponseCard.readValueParam("@o_responseUpdateCard");
		request.addInputParam("@i_response_td", ICTSTypes.SQLVARCHAR, message);
		String typeCard = aBagSPJavaOrchestration.get("o_type_card")!=null?aBagSPJavaOrchestration.get("o_type_card").toString():null;
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, typeCard);
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogBd");
		}
	}

	private void registerAssingLogBd(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAssingLogBd");
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account_number").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "CCA");
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		
		request.addInputParam("@i_asig_date", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_assign_date"));
		request.addInputParam("@i_request_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestAssingCard"));
		request.addInputParam("@i_response_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseAssingCard"));
	
		String typeCard = aBagSPJavaOrchestration.get("o_card_type")!=null?aBagSPJavaOrchestration.get("o_card_type").toString():null;
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, typeCard);
		
		logger.logDebug("Request Corebanking registerAssingLogBd: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerAssingLogBd: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerAssingLogBd");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("customerName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardApplication", ICTSTypes.SYBINT4, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "true"));
			row.addRowData(2, new ResultSetRowColumnData(false, "0"));
			row.addRowData(3, new ResultSetRowColumnData(false, "Success"));
			row.addRowData(4, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_cod_respuesta").toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_desc_respuesta").toString()));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
	
	public IProcedureResponse processResponseApi(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logInfo("processResponseApi [INI] --->" );
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("return code resp Conector --->" + codeReturn );

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SQLVARCHAR, 80));
		
		/*
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		*/
		if(!aBagSPJavaOrchestration.containsKey("flag_log"))
			registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		logger.logDebug("retunr o_card_available: " + aBagSPJavaOrchestration.get("o_card_available"));
		logger.logDebug("retunr o_id_card_dock: " + aBagSPJavaOrchestration.get("o_id_card_dock"));
		logger.logDebug("request mode: " + aBagSPJavaOrchestration.get("mode"));
		
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				
				notifyCardStatusUpdate(aRequest, aBagSPJavaOrchestration);
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_card_available").toString()));
					data3.addRow(row3);
				}
				
				if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_id_card_dock").toString()));
					data3.addRow(row3);
				}
								
			}
			else{
				logger.logDebug("Ending flow, processResponse error");
				
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				data2.addRow(row2);
			}
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: ");
			
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data2.addRow(row2);
		}

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}

		if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		
		return wProcedureResponse;		
	}
	
	private void notifyCardStatusUpdate(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        
        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en notifyCardStatusUpdate...");
        }
        
        String tittle = null;
        
        if (anOriginalRequest.readValueParam("@i_mode").equals("N")) {
        	
        	if (anOriginalRequest.readValueParam("@i_type_card").equals("VI")) {
        		
        		tittle = "Activación de tarjeta virtual realizada exitosamente";
        		
        	} else if (anOriginalRequest.readValueParam("@i_type_card").equals("PH")) {
        		
        		tittle = "Activación de tarjeta física realizada exitosamente";
        	}
        	
        } else {
        	
        	if (anOriginalRequest.readValueParam("@i_type_card").equals("VI")) {
        		
        		if (anOriginalRequest.readValueParam("@i_card_status").equals("N")) {
        			
        			tittle = "Desbloqueo de tarjeta virtual realizado exitosamente";
        			
        		} else if (anOriginalRequest.readValueParam("@i_card_status").equals("B")) {
        			
        			tittle = "Bloqueo de tarjeta virtual realizado exitosamente";
        			
        		} else if (anOriginalRequest.readValueParam("@i_card_status").equals("C")) {
        			
        			tittle = "Cancelación de tarjeta virtual realizada exitosamente";
        		}
        		
        	} else if (anOriginalRequest.readValueParam("@i_type_card").equals("PH")) {
        		
        		if (anOriginalRequest.readValueParam("@i_card_status").equals("N")) {
        			
        			tittle = "Desbloqueo de tarjeta física realizado exitosamente";
        			
        		} else if (anOriginalRequest.readValueParam("@i_card_status").equals("B")) {
        			
        			tittle = "Bloqueo de tarjeta física realizado exitosamente";
        			
        		} else if (anOriginalRequest.readValueParam("@i_card_status").equals("C")) {
        			
        			tittle = "Cancelación de tarjeta física realizada exitosamente";
        		}
        	}
        } 
        
        request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
        
        request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
        
        request.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, tittle);
        request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "N85");
        request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
        request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "18");
        request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
        request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
        request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_card_id"));
        request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
        request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, "0");
        
        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
        
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de notifyCardStatusUpdate...");
        }
    }
	
	private IProcedureResponse registerAtmCobis(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAtmCobis");
		}
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
				
		wProcedureResponse = getApplicationCard(aBagSPJavaOrchestration);
		
		wProcedureResponse = grabarSolicitud(aBagSPJavaOrchestration);
		
		if(wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		if(wProcedureResponse.readValueParam("@o_numero").equals("0") || wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		wProcedureResponse = aprobacionDefault(aBagSPJavaOrchestration);
		
		wProcedureResponse = getCustomerCardByAccount(aBagSPJavaOrchestration);
		
		//IProcedureResponse wProcedureRes = getActivationState(aBagSPJavaOrchestration);
		
		//if(null==wProcedureRes){
		wProcedureResponse = setActivationState(aBagSPJavaOrchestration);
		//}
		
		wProcedureResponse = insertCardActivationApplication(aBagSPJavaOrchestration);
		
		wProcedureResponse = activateCardAtm(aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response: " + wProcedureResponse.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de registerAtmCobis");
		}
		
		return wProcedureResponse;
		
	}

	private IProcedureResponse getApplicationCard(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getApplicationCard");
		}

		request.setSpName("cob_bvirtual..sp_consulta_solicitud");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT4, "0");

		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500048");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getApplicationCard ->getResultSetListSize " + wProductsQueryResp.getResultSetListSize() + "  getResultSets: "+wProductsQueryResp.getResultSets().size());
		}

		if (null!=wProductsQueryResp.getResultSetRowColumnData(1, 1, 1)) {
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) cod_application"+ wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("cod_application",wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse grabarSolicitud(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia grabarSolicitud");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		
		wProcedureRequest.setSpName("cob_atm..sp_atm_graba_solicitud");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16503");
		wProcedureRequest.addInputParam("@t_trn", 56, "16503");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		
		wProcedureRequest.addInputParam("@i_numero", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("cod_application").toString());
		wProcedureRequest.addInputParam("@i_num_detalles", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_producto", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_excepcion", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_ofi_entrega", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_autorizado", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@i_tipo_ent", ICTSTypes.SQLCHAR, "O");
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		wProcedureRequest.addInputParam("@i_oficial_neg", ICTSTypes.SQLVARCHAR, "usuariobv");
		String cardIdDock = aBagSPJavaOrchestration.get("o_type_card").equals("VIRTUAL")?aBagSPJavaOrchestration.get("o_id_card_dock").toString():aBagSPJavaOrchestration.get("o_card_available").toString();
		wProcedureRequest.addInputParam("@i_num_plastico", ICTSTypes.SQLVARCHAR, cardIdDock);

		wProcedureRequest.addOutputParam("@o_numero", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_costo", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_cta_debito", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_prod_debito", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_moneda_deb", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_causal", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_ofi_comprobante", ICTSTypes.SQLINT2, "00000"); 
		wProcedureRequest.addOutputParam("@o_categoria", ICTSTypes.SQLCHAR, "X");
		wProcedureRequest.addOutputParam("@o_tipo_solicitud", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_param_oficina", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_seguro", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_causal_seguro", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_simbolo_mon", ICTSTypes.SQLVARCHAR, "XXXXXXXXXX");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking grabarSolicitud: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de grabarSolicitud");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse aprobacionDefault(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia aprobacionDefault");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_aprobacion_default");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18503");
		
		wProcedureRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_categoria").toString());
		wProcedureRequest.addInputParam("@i_num_solicitud", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_numero").toString());
		wProcedureRequest.addInputParam("@i_tipo_solicitud",ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_tipo_solicitud").toString());
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		
		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18503");
		
		wProcedureRequest.addOutputParam("@o_cod_mascara", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking aprobacionDefault: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de aprobacionDefault");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse getCustomerCardByAccount(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia getCustomerCardByAccount");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_cons_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16520");
		wProcedureRequest.addInputParam("@i_valor", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		wProcedureRequest.addInputParam("@i_valortiny", ICTSTypes.SQLINT2, "4");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "6");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16520");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if(!wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).isNull())
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) num_tarj" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) mask" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			aBagSPJavaOrchestration.put("o_card_id", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("o_mask", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getCustomerCardByAccount(Map<String, Object>): " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCustomerCardByAccount(Map<String, Object>)");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse getActivationState(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia getActivationState");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_bvirtual..sp_activa_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500047");
		wProcedureRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "Q");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500047");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		logger.logInfo(CLASS_NAME + " Inicia getActivationState");
		if(wProductsQueryResp.getReturnCode() == 0)
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) cliente_cv" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) tarjeta_cv" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			aBagSPJavaOrchestration.put("o_cliente_cv", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("o_tarjeta_cv", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}
		else{
			wProductsQueryResp = null;
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getActivationState: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getActivationState");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse setActivationState(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia setActivationState");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_bvirtual..sp_activa_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500047");
		wProcedureRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_modo",ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_cacao_actua",ICTSTypes.SQLCHAR, "S");
		wProcedureRequest.addInputParam("@i_cacao_activ",ICTSTypes.SQLCHAR, "S");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500047");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking setActivationState: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de setActivationState");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse insertCardActivationApplication(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia insertCardActivationApplication");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_sol_activacion");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16759");
		wProcedureRequest.addInputParam("@i_grupo1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_mask").toString() 
				+ "@" + aBagSPJavaOrchestration.get("o_card_id").toString() + "@");
		wProcedureRequest.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "0");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		//wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16759");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking insertCardActivationApplication: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de insertCardActivationApplication");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse activateCardAtm(Map<String, Object> aBagSPJavaOrchestration) {

		ProcedureRequestAS request = new ProcedureRequestAS();
		
		final String METHOD_NAME = "[activateCardAtm]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@i_util_sobre", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "MAN");
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, "ENA");
		request.addInputParam("@i_operacion_pin", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "EMI");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "ACTIVACION API");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");

		request.setSpName("cob_procesador..sp_exec_atm_activacion");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=cob_procesador..sp_exec_atm_activacion)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Enquiries.Service.Enquiries.ExecuteCardActivation");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_exec_atm_activacion");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "16061");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "16061");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo("REQUEST activateCardAtm" + request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR activateCardAtm");

		logger.logInfo(METHOD_NAME + "RESPONSE activateCardAtm: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "CODE RETURN activateCardAtm " + connectorSpeiResponse.getReturnCode());
		
		return connectorSpeiResponse;

	}
	
	private IProcedureResponse getCardDockAviable(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getCardDockAviable");
		}

		request.setSpName("cob_atm..sp_get_data_card_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_status"));
		request.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_status_reason"));
		request.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_type_card"));
		
		request.addOutputParam("@o_id_card_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_detail_status", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_det_reason_stat", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_status_atm", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_card_available", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_type_card", wProductsQueryResp.readValueParam("@o_type_card"));
		aBagSPJavaOrchestration.put("o_status_atm", wProductsQueryResp.readValueParam("@o_status_atm"));
		aBagSPJavaOrchestration.put("o_card_available", wProductsQueryResp.readValueParam("@o_card_available"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getCardDockAviable: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCardDockAviable");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse assingCardAtm(Map<String, Object> aBagSPJavaOrchestration) {

		ProcedureRequestAS request = new ProcedureRequestAS();
		
		final String METHOD_NAME = "[activateCardAtm]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@i_util_sobre", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "MAN");
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, "ENA");
		request.addInputParam("@i_operacion_pin", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "EMI");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "ACTIVACION API");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");

		request.setSpName("cob_procesador..sp_exec_atm_activacion");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=cob_procesador..sp_exec_atm_activacion)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Enquiries.Service.Enquiries.ExecuteCardActivation");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_exec_atm_activacion");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "16061");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "16061");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo("REQUEST activateCardAtm" + request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR activateCardAtm");

		logger.logInfo(METHOD_NAME + "RESPONSE activateCardAtm: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "CODE RETURN activateCardAtm " + connectorSpeiResponse.getReturnCode());
		
		return connectorSpeiResponse;

	}
	
}
