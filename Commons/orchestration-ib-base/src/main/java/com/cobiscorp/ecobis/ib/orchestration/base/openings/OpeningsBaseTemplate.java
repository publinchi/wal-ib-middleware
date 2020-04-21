package com.cobiscorp.ecobis.ib.orchestration.base.openings;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SignerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SignerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;

/**
 * @author promero
 * @description This class implement logic to do a Opening  
 */
public abstract class OpeningsBaseTemplate extends SPJavaOrchestrationBase{
	protected static final String CLASS_NAME = " >-----> ";
	private static final String COBIS_CONTEXT = "COBIS";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_VALIDATE_LOCAL= "RESPONSE_VALIDATE_LOCAL";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
	protected static final String RESPONSE_SERVER ="RESPONSE_SERVER";
	protected static final String RESPONSE_OFFLINE ="RESPONSE_OFFLINE";
	protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION";
	protected static final String APPLICATION_NAME = "APPLICATION_NAME";
	protected static final String LOG_MESSAGE = "LOG_MESSAGE";
	protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
	protected static final int TRN_SEARCHOFFICAL_REQUEST = 1800196;
	protected static final int CODE_OFFLINE = 40004;
	protected static final int CODE_OFFLINE_WITHOUT_BALANCE = 40002;
	
	public boolean SUPPORT_OFFLINE = false; 
    public boolean VALIDATE_CENTRAL = false;
	
	private static ILogger logger = LogFactory.getLogger(ApplicationsBaseTemplate.class);
	
	protected abstract ICoreService getCoreService();
	
	protected abstract ICoreServer getCoreServer();
	
	public abstract ICoreServiceNotification getCoreServiceNotification();
	
	protected abstract ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction();
	/**
	 * Method to validate some info in Core , this not apply all transactions 
	 */
	protected abstract IProcedureResponse validateCentralExecution(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException ;
	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)  throws CTSServiceException, CTSInfrastructureException;
	/**
	 * This method has to be override to implement call of service
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected abstract IProcedureResponse executeOpening(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;
		/**
	 * Contains primary steps for execution of Request
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeStepsOpeningBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse responseValidateCoreSigners  = null;		
		IProcedureResponse responseOffline = null;
		IProcedureResponse responseLocalExecution=null;	
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		
		//ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);//hsa se cambio el orden
		
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		String messageErrorApplication = null;
		messageErrorApplication =(String)aBagSPJavaOrchestration.get(APPLICATION_NAME);	
		
		//Valida el fuera de línea
		
		if (logger.isInfoEnabled())
			logger.logInfo("Llama a la funcion validateBvTransaction");
		
		String responseSupportOffline = validateBvTransaction(aBagSPJavaOrchestration); 
		
		if (logger.isInfoEnabled())
			logger.logInfo("responseSupportOffline ---> " + responseSupportOffline);
		
		if(responseSupportOffline == null || responseSupportOffline == "") {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la consulta permite fuera de línea"));
			return Utils.returnException("Ha ocurrido un error intentando validar si la consulta permite fuera de línea");
		}
	
		if(responseSupportOffline.equals("S")) {
			SUPPORT_OFFLINE = true;
		}else {
			SUPPORT_OFFLINE = false;
		}

		if (!SUPPORT_OFFLINE && !responseServer.getOnLine()) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea"));
			return Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea");
		}

		if(responseServer.getOnLine() || (!responseServer.getOnLine() && responseServer.getOfflineWithBalances())){
			responseValidateCoreSigners = validateCoreSigners(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorApplication+" --> validateSigners", responseValidateCoreSigners)){ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCoreSigners);
				return null;
			}
		}else{
			if (logger.isInfoEnabled()) logger.logInfo("No se realizó validación por estar en modo fuera de línea sin saldo");
			responseValidateCoreSigners = new ProcedureResponseAS();
			responseValidateCoreSigners.setReturnCode(0);
		}	
		
		IProcedureResponse responseValidateLocalExecution = validateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorApplication +" --> validateLocalExecution", responseValidateLocalExecution)) {
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateLocalExecution);
			return null;
		};
			
		aBagSPJavaOrchestration.put(RESPONSE_VALIDATE_LOCAL, responseValidateLocalExecution);
		
		AccountingParameterRequest requestAccountingParameters = new AccountingParameterRequest();
		requestAccountingParameters.setOriginalRequest(anOriginalRequest);
		requestAccountingParameters.setTransaction(Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))));		
		AccountingParameterResponse responseAccountingParameters =  getCoreServiceMonetaryTransaction().getAccountingParameter(requestAccountingParameters);
		if (logger.isInfoEnabled()) 
		logger.logInfo("RESPONSE ACCOUNTING PARAMETERS -->"+responseAccountingParameters.getAccountingParameters().toString());
		
		if (!responseAccountingParameters.getSuccess()){
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseAccountingParameters);
			return Utils.returnException(responseAccountingParameters.getReturnCode(), new StringBuilder(messageErrorApplication).append(responseAccountingParameters.getMessage()).toString());
		}
		
		
		if (logger.isDebugEnabled()){
			
			if (responseAccountingParameters == null)
				logger.logDebug("responseAccountingParameters igual null");
			logger.logDebug("i_prod --> "+anOriginalRequest.readValueParam("@i_prod"));	
		}
		int prod = 0;
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_pro_debito")))
			prod = Integer.parseInt(anOriginalRequest.readValueParam("@i_pro_debito"));	
		else
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_prod_deb")))
				prod = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_deb"));
			
		Map<String,AccountingParameter> mapDebit   = existsAccountingParameter(responseAccountingParameters , prod, "T","D");
		if (mapDebit!=null)
			aBagSPJavaOrchestration.put("ACCOUNTING_PARAMETER_DEBIT", mapDebit.get("ACCOUNTING_PARAM"));
		
		
		Map<String,AccountingParameter> mapCredit   = existsAccountingParameter(responseAccountingParameters , prod, "T","C");
		if (mapCredit!=null)
			aBagSPJavaOrchestration.put("ACCOUNTING_PARAMETER_CREDIT", mapCredit.get("ACCOUNTING_PARAM"));
		
		Map<String,AccountingParameter> mapCommission   = existsAccountingParameter(responseAccountingParameters , prod, "C");
		if (mapCommission !=null)
			aBagSPJavaOrchestration.put("ACCOUNTING_PARAMETER_COMMISSION", mapCommission.get("ACCOUNTING_PARAM"));
		
		IProcedureResponse responseExecuteQuery = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);
		if(responseServer.getOnLine()||(!responseServer.getOnLine() && responseServer.getOfflineWithBalances())){
			if (Utils.flowError(messageErrorApplication +" --> executeQuery", responseExecuteQuery)) {
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);
				return null;
			};
		}
		
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);
		
		IProcedureResponse responseSendMail = sendOpeningMail(anOriginalRequest, aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorApplication +" --> executeQuery", responseSendMail)) {
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseSendMail);
			return null;
		};
		
		IProcedureResponse wProcAux=(IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		if (logger.isDebugEnabled()) 
		logger.logDebug("**************VALOR PARA EJECUTAR updateLocalExecution---> @o_retorno"+wProcAux.readValueParam("@o_retorno")+" -->@o_retorno:"+wProcAux.readParam("@o_retorno")+" -->@t_trn:"+anOriginalRequest.readValueParam("@t_trn"));
		if (wProcAux.readParam("@o_retorno")!=null || Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")) == 1801005 )
		{
			String oRetorno=wProcAux.readValueParam("@o_retorno");
			if (oRetorno!=null ||  Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")) == 1801005 )
			{
			if (logger.isDebugEnabled())
				logger.logDebug("------------> VALOR RETORNO: "+oRetorno+"------------> VALOR t_trn: "+Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));
				if (Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")) == 1801005 )
				{
					responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
					if (logger.isInfoEnabled()) 
					logger.logInfo("------------>RETURN CODE updateLocalExecution:"+ responseLocalExecution.getReturnCode());					
					if (Utils.flowError(messageErrorApplication +" --> updateLocalExecution", responseLocalExecution)){
						if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
						aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
						return null;
					}										
					if (logger.isInfoEnabled()) logger.logInfo(new StringBuilder("RESPONSE_UPDATE_LOCAL --> ").append(responseLocalExecution.getProcedureResponseAsString()));
				}
				else if (oRetorno.length()>1)
				{
					responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);					
					if (Utils.flowError(messageErrorApplication +" --> updateLocalExecution", responseLocalExecution)){ 
						if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
						aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
						return null;
					}									
					if (logger.isInfoEnabled()) logger.logInfo(new StringBuilder("RESPONSE_UPDATE_LOCAL --> ").append(responseLocalExecution.getProcedureResponseAsString()));
				}
			}
		}
		
		
		IProcedureResponse responseExecuteAmountLimits = processAmountLimits(anOriginalRequest, aBagSPJavaOrchestration);
		
		if (Utils.flowError(messageErrorApplication +" --> Amount Limits", responseExecuteAmountLimits)) {
			if (logger.isInfoEnabled()) logger.logInfo("Respuesta offline processAmountLimits error de flujo: "+responseExecuteAmountLimits);
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteAmountLimits);
			return null;
		};
		
		
		responseOffline = (IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_OFFLINE);
		if (logger.isInfoEnabled()) logger.logInfo("Response OFFLINE "+responseOffline);
		
		if(!Utils.isNull(responseServer)){
			if (logger.isInfoEnabled()) logger.logInfo(new StringBuilder("RESPONSE_SERVER --> STATUS: ").append(responseServer.getOnLine()+" PROCESS_DATE: ").append( responseServer.getProcessDate()));			
		}		
		
		if (logger.isInfoEnabled()) 
			logger.logInfo(new StringBuilder("RESPONSE_CORE_SIGNERS --> ").append(responseValidateCoreSigners.getProcedureResponseAsString()));		
		if (logger.isInfoEnabled()) 
			if (!Utils.isNull(responseExecuteQuery))
				logger.logInfo(new StringBuilder("RESPONSE_TRANSACTION --> ").append(responseExecuteQuery.getProcedureResponseAsString()));	
		
		aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
		
		if (logger.isInfoEnabled()) logger.logInfo(new StringBuilder(CLASS_NAME).append("FINISH").toString());
		IProcedureResponse wIProcedureResponse=(IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);		
		
		
		if (wIProcedureResponse.readParam("@o_retorno")==null || wIProcedureResponse.readValueParam("@o_retorno")==null)
		{
			wIProcedureResponse.addParam("@o_retorno", ICTSTypes.SQLVARCHAR, 1,"0");
		}
		
		if (logger.isInfoEnabled())
		logger.logInfo("IPROCEDURE RESPONSE_TRANSACTION "+wIProcedureResponse);
		
		return wIProcedureResponse;
	}
	
	/**
	 * Validation signers of account for authorization.
	 */
	protected IProcedureResponse validateCoreSigners(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "Iniciando consulta de Firmas Fisicas");

		try {
			if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Servicio inyectado:" + getCoreService());

			SignerRequest signerRequest = new SignerRequest();
			Product product = new Product();
			Client client = new Client();

			if (!Utils.isNull(anOriginalRequest.readParam("@i_ente"))) 
				client.setId(anOriginalRequest.readValueParam("@i_ente"));
			

			if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) 
				product.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
			signerRequest.setOriginalRequest(anOriginalRequest);

			signerRequest.setUser(client);
			signerRequest.setOriginProduct(product);
			if (!Utils.isNull(anOriginalRequest.readParam("@i_monto"))) 
				signerRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_monto").toString()));
			
			if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Consultando firmantes:" + signerRequest);
			SignerResponse signerResponse = getCoreService().getSignatureCondition(signerRequest);
			if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Respuesta firmantes:" + signerResponse);

			IProcedureResponse res = new ProcedureResponseAS();
			res.addParam("@o_condiciones_firmantes", ICTSTypes.SQLVARCHAR, 0, (signerResponse.getSigner() != null ? signerResponse.getSigner().getCondition() : ""));

			if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "RESPUESTA CORE:" + res);
			res.setReturnCode(0);
			return res;
		}
		catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		}
		catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		}
	}
	
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
		
		request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_validacion");
		
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta")))
		Utils.copyParam("@i_cta", anOriginalRequest, request);
		else{
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta_deb")))
				request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_deb"));
		}
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_moneda")))
			request.addInputParam("@i_mon", ICTSTypes.SQLINT1, anOriginalRequest.readValueParam("@i_moneda"));
		else{
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_mon_deb")))
				request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon_deb"));
		}
				
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_pro_debito")))
			request.addInputParam("@i_prod", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_pro_debito"));	
		else{
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_prod_deb")))
				request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod_deb"));
		}
		
		request.addInputParam("@i_concepto",ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@s_ssn"));
		request.addInputParam("@i_tercero",ICTSTypes.SQLVARCHAR,"N");
		request.addInputParam("@i_cond_firmas",ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@o_condiciones_firmantes"));
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(),anOriginalRequest.readValueParam("@i_login"));
		else{
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_usuario")))
				request.addInputParam("@i_login", anOriginalRequest.readParam("@i_usuario").getDataType(),anOriginalRequest.readValueParam("@i_usuario"));
		}
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_fecha")))
			request.addInputParam("@i_fecha", anOriginalRequest.readParam("@i_fecha").getDataType(),anOriginalRequest.readValueParam("@i_fecha"));
		
		Utils.copyParam("@i_doble_autorizacion", anOriginalRequest, request);
		Utils.copyParam("@i_ente", anOriginalRequest, request);
		request.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR,"S");		
		
		request.addOutputParam("@o_autorizacion", ICTSTypes.SQLCHAR, "X");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINT4, "0");		
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, request: "
					+ request.getProcedureRequestAsString());
		}
		
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse response = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, response: "
					+ response.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Validate local");
		}
		
		return response;		
	}
	
	protected IProcedureResponse processAmountLimits(IProcedureRequest anOriginalRequest,Map<String, Object> bag){		
		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800136");
		
		request.setSpName("cob_bvirtual..sp_procesa_montos_limites_bv");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "L");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Process Amount Limits, request: "
					+ request.getProcedureRequestAsString());
		}
		
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse response = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Process Amount Limits, response: "
					+ response.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Process Amount Limits");
		}
		return response;
	}
	
	protected IProcedureResponse sendOpeningMail(IProcedureRequest anOriginalRequest,Map<String, Object> bag){		
		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		IProcedureResponse responseTransaccion = (IProcedureResponse)bag.get(RESPONSE_TRANSACTION);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);		
		
		request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_envia_mail_apertura");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801030");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_mensaje", ICTSTypes.SQLVARCHAR, responseTransaccion.readValueParam("@o_body"));
		request.addInputParam("@i_mail", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mail"));
		request.addInputParam("@i_no_id", ICTSTypes.SQLVARCHAR, "N60");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Send Mail, request: "
					+ request.getProcedureRequestAsString());
		}
		
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse response = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Send Mail, response: "
					+ response.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Sen Mail");
		}
		return response;
	}
	
	protected ValidationAccountsRequest transformToValidationAccountRequest(IProcedureRequest anOriginalRequest){

		ValidationAccountsRequest request = new ValidationAccountsRequest();

		Product originProduct = new Product();
		Currency originCurrency = new Currency();
		if (anOriginalRequest.readValueParam("@i_cta") != null) 
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		else
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_deb"));
		
		if (anOriginalRequest.readValueParam("@i_prod") != null) 
			originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		else
			originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_deb").toString()));
		
		if (anOriginalRequest.readValueParam("@i_mon") != null) 
			originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));
		else
			originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_deb").toString()));
			
		originProduct.setCurrency(originCurrency);
		Secuential originSSn = new Secuential();
		if (anOriginalRequest.readValueParam("@s_ssn") != null) originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());
		if (anOriginalRequest.readValueParam("@s_servicio") != null) request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
		if (anOriginalRequest.readValueParam("@t_trn") != null) request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

		request.setSecuential(originSSn);
		request.setOriginProduct(originProduct);
		request.setOriginalRequest(anOriginalRequest);
		return request;
	}

	protected IProcedureResponse getBalancesToSynchronize(IProcedureRequest anOriginalRequest){
		ValidationAccountsRequest validations = new ValidationAccountsRequest();
		validations = transformToValidationAccountRequest(anOriginalRequest);
		IProcedureResponse response = getCoreServiceMonetaryTransaction().getBalancesToSynchronize(validations);
		
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE getBalancesToSynchronize -->"+response.getProcedureResponseAsString());		
		
		return response ;
	}
	
	/**
	 * updateLocalExecution .
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
		
		IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());
		IProcedureResponse transResponse=(IProcedureResponse)bag.get(RESPONSE_TRANSACTION);
		
		ServerResponse  responseServer = (ServerResponse)bag.get(RESPONSE_SERVER);//hsa
		IProcedureResponse responseBalance = (IProcedureResponse)bag.get(RESPONSE_BALANCE);//hsa
		if (logger.isInfoEnabled()) 
		logger.logInfo("Response Balance To Synchronize : "+responseBalance);
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, request.readValueParam("@t_trn"));
		
		request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_transaccion");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@t_trn"));	
		request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");		
		request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
		request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "D");
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_proposito"));
		
		if (logger.isInfoEnabled()) 
		{
		logger.logInfo("Update local param reentryExecution");
		logger.logInfo("--->VAL REENTRY EXEW " +request.readValueParam("reentryExecution"));
		logger.logInfo("--->VAL REENTRY EXEW " +anOriginalRequest.readValueFieldInHeader("reentryExecution"));
		}
	    		
		if(evaluateExecuteReentry(anOriginalRequest)){
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
		}else{
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "N");
		}	    
		
		if (anOriginalRequest.readValueParam("@i_cta") != null) 
			request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
		else
			request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_deb"));

		if (anOriginalRequest.readValueParam("@i_prod") != null) 
			request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod"));
		else
			request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod_deb"));

		if (anOriginalRequest.readValueParam("@i_mon") != null) 
			request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
		else
			request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon_deb"));			
		
		if(!responseServer.getOnLine())
		{			
			if(responseServer.getOfflineWithBalances()){
			  Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(CODE_OFFLINE)));
			}else{
			  Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(transResponse.getReturnCode())));  
			}	       
		      
			if (transResponse.getMessageListSize() > 0){
		     Utils.addInputParam(request,"@s_msg", ICTSTypes.SQLVARCHAR, (transResponse.getMessage(1).getMessageText()));   
		    }        	           
		    	
			
			if (!Utils.isNull(request.readValueParam("@o_clave"))){
				Utils.addInputParam(request,"@i_clave_rty", request.readParam("@o_clave").getDataType(), request.readValueParam("@o_clave"));
			}else {
	            if (logger.isInfoEnabled())logger.logInfo("Parámetro @o_clave no encontrado");
	        }			
			
			if(responseServer.getOfflineWithBalances()){
				request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "S");
			}else{
				request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
			}
			request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "F");
			request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "PN");
			request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
			request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_factor", ICTSTypes.SQLINTN, "1");
		}
		
		if(responseServer.getOnLine() || evaluateExecuteReentry(anOriginalRequest)){
			request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
			request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
			request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
			request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_factor", ICTSTypes.SQLINTN, "1");									
		}
		
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));		
		request.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0");
		Integer trn = new Integer(anOriginalRequest.readValueParam("@t_trn").toString());
		
		if (!trn.equals(1875056));
		{
			request.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR, transResponse.readValueParam("@o_retorno"));			
			if(anOriginalRequest.readValueParam("@i_tasa_cambio")!=null){
				request.addInputParam("@i_tasa_cambio", ICTSTypes.SQLFLTNi, anOriginalRequest.readValueParam("@i_tasa_cambio"));
			}else{
				request.addInputParam("@i_tasa_cambio", ICTSTypes.SQLFLTNi, anOriginalRequest.readValueParam("@i_tasa"));
			}
		}	

		if (anOriginalRequest.readValueParam("@i_plazo") != null){
			request.addInputParam("@i_convenio", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_plazo"));
			request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, " APERTURA CDP ");
		}
		else{
			request.addInputParam("@i_convenio", ICTSTypes.SQLVARCHAR, "0");
			request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, " COMPRA VENTA DE DIVISAS ");
			request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_cre"));			
			
			if (anOriginalRequest.readValueParam("@i_prod_cre") != null) {
				request.addInputParam("@i_prod_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod_cre"));
			}
			
			if (anOriginalRequest.readValueParam("@i_mon_cre") != null) {
				request.addInputParam("@i_mon_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon_cre"));
			}
		}

		if (anOriginalRequest.readParam("@i_login") == null) {
			request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));
		}
		else
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		
		//copia variables r_ como parametros de entrada para sincronizar saldos hsa
		if (logger.isInfoEnabled()) 
		logger.logInfo("RESPONSE BALANCE " +responseBalance);
		if (!Utils.isNull(responseBalance)){
			if (responseBalance.getResultSetListSize() >0){
				
				IResultSetHeaderColumn[] columns = responseBalance.getResultSet(responseBalance.getResultSetListSize()).getMetaData().getColumnsMetaDataAsArray();
				IResultSetRow[] rows =  responseBalance.getResultSet(responseBalance.getResultSetListSize()).getData().getRowsAsArray();
				IResultSetRowColumnData[] cols =  rows[0].getColumnsAsArray();
				
				int i = 0;
				for (IResultSetHeaderColumn iResultSetHeaderColumn : columns) {
					if (!iResultSetHeaderColumn.getName().equals(""))
					if (cols[i].getValue()!=null){
						if (logger.isDebugEnabled())
							logger.logDebug("PARAMETROS AGREGADOS :"+iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_")+" VALOR: "+cols[i].getValue());
						Utils.addInputParam(request, iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_"), iResultSetHeaderColumn.getType(), cols[i].getValue());
					}
					i++;
				}				
			}
		}		
		
		request.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR,"N");
		request.addOutputParam("@o_tipo_mensaje", ICTSTypes.SQLVARCHAR	, "F");
		request.addOutputParam("@o_numero_producto", ICTSTypes.SQLVARCHAR	, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: "
					+ request.getProcedureRequestAsString());
		}
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, response: "
					+ pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Update local");
		}
		return pResponse;
	}
	/**
	 * Find Officers of Account and Client
	 */
	
	public boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest){		
		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))){
			if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	private OfficerByAccountResponse findOfficers(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException{
		OfficerByAccountRequest request = new OfficerByAccountRequest();
		Product product = new Product();
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta"))) product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_prod"))) product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		request.setProduct(product);
		
		return getCoreService().getOfficerByAccount(request);
		
	}
	private Map<String,AccountingParameter> existsAccountingParameter(AccountingParameterResponse anAccountingParameterResponse, int product, String type){
		return existsAccountingParameter(anAccountingParameterResponse, product, type,"D");
	}
	private Map<String,AccountingParameter> existsAccountingParameter(AccountingParameterResponse anAccountingParameterResponse, int product, String type, String sign ){
		
		Map<String,AccountingParameter> map = null;
				if (anAccountingParameterResponse==null)
					return map;
				
				if (anAccountingParameterResponse.getAccountingParameters().size()==0)
					return map;
					
				for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {
					
						
					if (parameter.getTypeCost().equals(type) && parameter.getProductId()== product && parameter.getSign().equals(sign)){
						if (logger.isDebugEnabled())
						logger.logDebug("SI HAY TRN: "+String.valueOf(parameter.getTransaction())+" CAUSA: "+ parameter.getCause()+" TIPO :"+parameter.getTypeCost() );
						map = new HashMap<String,AccountingParameter>();
						map.put("ACCOUNTING_PARAM", parameter);
						break;
					}
				}
				return map;
			}
	
	/**
	 * validateBvTransaction: local account, virtual signers checking
	 * 
	 * @param aBagSPJavaOrchestration
	 * @return String
	 */
	protected String validateBvTransaction(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Initialize method validateBvTransaction");
		}
		
		String responseSupportOffline = "N";
		
		//valida la parametria de la tabla bv_transaccion
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest request = initProcedureRequest(originalRequest);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800090");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_transaction_context");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "IB");
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINTN, originalRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, originalRequest.readValueParam("@s_servicio"));
		
		request.addOutputParam("@o_autenticacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_fuera_de_linea", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_doble_autorizacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_sincroniza_saldos", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_mostrar_costo", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_tipo_costo", ICTSTypes.SYBCHAR, "N");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize method validateBvTransaction");
		}
		
		// Ejecuta validacion a la tabla bv_transaccion
		IProcedureResponse tResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: " + tResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finaliza validacion local");
		
		responseSupportOffline = tResponse.readValueParam("@o_fuera_de_linea");
		
		aBagSPJavaOrchestration.put(RESPONSE_BV_TRANSACTION, tResponse);

		// Valida si ocurrio un error en la ejecucion
		if (Utils.flowError("validateBvTransaction", tResponse)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, tResponse);
		}
		
		return responseSupportOffline;
		
		
	}
	
}
