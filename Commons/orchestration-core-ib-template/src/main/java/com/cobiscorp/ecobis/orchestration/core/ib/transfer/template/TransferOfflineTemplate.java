package com.cobiscorp.ecobis.orchestration.core.ib.transfer.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransferOfflineTemplate extends TransferBaseTemplate {

	private static final Logger log = LoggerFactory.getLogger(TransferOfflineTemplate.class);
	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
	protected static final String TRANSFER_NAME = "TRANSFER_NAME";
	protected static final int CODE_OFFLINE = 40004;

	protected abstract IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);

	private static ILogger logger = LogFactory.getLogger(TransferOfflineTemplate.class);

	public abstract ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent();

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseToSychronize = null;
		IProcedureResponse responseTransfer = null;
		IProcedureRequest anOriginalRequestClone = anOriginalRequest.clone();
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Ejecutando método executeTransaction Request: " + anOriginalRequest);

		StringBuilder messageErrorTransfer = new StringBuilder();
		messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		// if is Online and if is reentryExecution , have to leave
		if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
			if (!serverResponse.getOnLine()) {
				IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
				return resp;
			}
		}

		aBagSPJavaOrchestration.put("origin_spei", anOriginalRequest.readValueParam("@i_origin_req"));
		aBagSPJavaOrchestration.put("ssn_operation", anOriginalRequest.readValueParam("@i_ssn_operation"));
		      
		 responseTransfer = executeTransfer(aBagSPJavaOrchestration);
		
		if (serverResponse.getOnLine()) {

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + " Respuesta de ejecución método executeTransfer: "
						+ responseTransfer.getProcedureResponseAsString());

			if (Utils.flowError(messageErrorTransfer.append(" --> executeTransfer").toString(), responseTransfer)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + messageErrorTransfer);
				return responseTransfer;
			}
		} else {
			// Si no es ejecucion de reentry, grabar en reentry
			if (!getFromReentryExcecution(aBagSPJavaOrchestration)) {
				if (logger.isInfoEnabled())
					logger.logInfo(
							CLASS_NAME + " Transferencia en OffLine serverResponse :" + serverResponse.toString());
                 //saltar reentry si es que hubo problemas con el proveedor
				if (responseTransfer.readValueParam("@i_fail_provider") == null
						|| !responseTransfer.readValueParam("@i_fail_provider").equals("S")) {
					
					if(responseTransfer.readValueParam("@i_type_reentry")!=null && responseTransfer.readValueParam("@i_type_reentry").equals(TYPE_REENTRY_OFF_SPI)) {
						
						anOriginalRequest.addInputParam("@i_type_reentry", ICTSTypes.SQLVARCHAR,TYPE_REENTRY_OFF);
					}
					
					if (logger.isInfoEnabled())
						logger.logInfo("::::SAVED REENTRY:::: "+anOriginalRequest);
				    saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
					aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseTransfer);

					
					logger.logInfo("i_register_off_mov::: "+responseTransfer.readValueParam("@i_register_off_mov"));
					//almacenar movimiento offline JC
					logger.logInfo("i_register_off_mov APLICADO 22");
					if(responseTransfer.readValueParam("@i_register_off_mov")!=null &&
					responseTransfer.readValueParam("@i_register_off_mov").equals("S")) {
						movementOffline(anOriginalRequestClone, aBagSPJavaOrchestration);
					}
                    
					

				}else {				
					
					return Utils.returnException(1, ERROR_SPEI);
				}
			}
		}

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);
		
		if (serverResponse.getOnLine() || (!serverResponse.getOnLine() && serverResponse.getOfflineWithBalances())) {
			responseToSychronize = new ProcedureResponseAS();
			responseToSychronize.setReturnCode(responseTransfer.getReturnCode());
			if (responseTransfer.getResultSetListSize() > 0) {
				responseToSychronize.addResponseBlock(responseTransfer.getResultSet(1));
			}
			aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseToSychronize);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta de ejecución método executeTransaction Response:  "
					+ responseTransfer.getProcedureResponseAsString());
		return responseTransfer;
	}
	
	public void registerAllTransactionSuccess(String tipoTran, IProcedureRequest aRequest,String causal, Map<String, Object> aBagSPJavaOrchestration) {	
		try{
			IProcedureRequest request = new ProcedureRequestAS();
			String movementType = tipoTran;
			String eventType = "TRANSACCION PENDING";
			Integer longSsnBranch = 0;
	        String authorizationCode = null;
	        
	        if (aBagSPJavaOrchestration.get("ssn_branch") !=null) {
	        	authorizationCode = aBagSPJavaOrchestration.get("ssn_branch").toString();
	        	longSsnBranch = Math.max(authorizationCode.length() - 6, 0);
	        	
	    		authorizationCode = authorizationCode.substring(longSsnBranch);
	        }

	        if (aBagSPJavaOrchestration.get("typeTrnWH") !=null && aBagSPJavaOrchestration.get("typeTrnWH").equals("S")) {
	        	eventType = "TRANSACCION SUCCESS";
	        }

			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, eventType);

			request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
			request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
			request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@x_end_user_request_date"));
			request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
			request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
			request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
			request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , aRequest.readValueParam("@i_commission"));
			request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , "0");
			request.addInputParam("@i_movementId", ICTSTypes.SQLINTN , (String)aBagSPJavaOrchestration.get("movementId"));
			
			request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
			request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
			
			request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
			request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_number"));
			request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
			
			request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_owner_name")); 
			request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_number"));
			request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
			request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_name"));
			
			request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@i_codigo_acc"));
			request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@i_clave_rastreo"));
			
			// Asignación de dirección IP
			if (aRequest.readValueParam("@x_end_user_ip") != null) {
				request.addInputParam("@i_deviceIp", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
	        }
			
			request.addInputParam("@i_authorizationCode", ICTSTypes.SQLVARCHAR, authorizationCode);
			
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR,(String)aRequest.readValueParam("@i_json_req"));
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
				
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerAllTransactionSuccess: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerAllTransactionSuccess");
			}
		}catch(Exception e){
			logger.logError("Error Catastrofico en registerAllTransactionSuccess SPEI_DEBIT");	
		}
	}

	private  void movementOffline(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration){

		if (logger.isInfoEnabled()){

			logger.logInfo("JC se almacena movimiento en offline");
		}

		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia Offline a terceros CORE COBIS" + anOriginalRequest);
			logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
			logger.logInfo("********** CAUSA ORIGEN --->>> " + "1010");
			logger.logInfo("********** CAUSA COMISI --->>> " + "185");
			logger.logInfo("********** CAUSA DESTIN --->>> " + "1020");

			logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));
			logger.logInfo("JC ENTRA REQUEST");
			logger.logInfo(anOriginalRequest);
			logger.logInfo("BOLSAX");
			logger.logInfo(aBagSPJavaOrchestration);
		}
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18306");
		anOriginalRequest.addInputParam("@i_trn", ICTSTypes.SYBINT4, "18500115");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, "2010");
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, "2010");
		//anOriginalRequest.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, "185");
		anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
		anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");
		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("o_ente_bv"));
		anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
		anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());
		anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon_des").toString());
		anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod_des").toString());

		anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");

		anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
		anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");

		//-anOriginalRequest.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, request.readValueParam("@i_latitud"));
		//-anOriginalRequest.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, request.readValueParam("@i_longitud"));
		anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
		anOriginalRequest.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));


		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));


		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest);
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

		logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
		response.readValueParam("@o_fecha_tran");

		logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
		if(response.readValueFieldInHeader("ssn")!=null)
			aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));





	}

	public void registerTransactionFailed(String tipoTran, IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) 
	{
		IProcedureRequest request = new ProcedureRequestAS();
		String movementType = tipoTran;
		String movementId = "0";
		String codeError = "0";
		String messageError = "";
		String causal = "";
		
		try{
			
			if (aBagSPJavaOrchestration.get("movementId")!= null) {
				movementId = aBagSPJavaOrchestration.get("movementId").toString();
			}

			if (aBagSPJavaOrchestration.get("code_error")!= null) {
				codeError = aBagSPJavaOrchestration.get("code_error").toString();
			}
			
			if (aBagSPJavaOrchestration.get("message_error")!= null) {
				messageError = aBagSPJavaOrchestration.get("message_error").toString();
			}
			
			if (aBagSPJavaOrchestration.get("causal")!= null) {
				causal = aBagSPJavaOrchestration.get("causal").toString();
			}
			
			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION FAILED");

			request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
			request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
			request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
			request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
			request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@x_end_user_request_date"));
			request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
			request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
			
			request.addInputParam("@i_movementId", ICTSTypes.SQLINTN , movementId);
			
			request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_number"));
			request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_owner_name"));
			request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_number"));
			request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
			
			request.addInputParam("@i_errorDetailsCode", ICTSTypes.SQLVARCHAR, codeError);
			request.addInputParam("@i_errorDetailsMessage", ICTSTypes.SQLVARCHAR, messageError);
			
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
				
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerAllTransactionFailed: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerAllTransactionFailed");
			}
		}catch(Exception e){
			logger.logError("Error Catastrofico en registerAllTransactionFailed SPEI_DEBIT");	
		}
	}
	
	protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = anOriginalRequest.clone();
		IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_LOCAL_VALIDATION);

		ComponentLocator componentLocator = null;
		IReentryPersister reentryPersister = null;
		componentLocator = ComponentLocator.getInstance(this);

		Utils.addInputParam(request, "@i_clave_bv", 56, responseLocalValidation.readValueParam("@o_clave_bv"));
		Utils.addInputParam(request, "@i_en_linea", 39, "N");
		Utils.addOutputParam(request, "@o_clave", 56, "0");

		reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
		if (reentryPersister == null)
			throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

		request.removeFieldInHeader("sessionId");
		request.addFieldInHeader("reentryPriority", 'S', "5");
		request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
		request.addFieldInHeader("targetId", 'S', "local");
		request.removeFieldInHeader("serviceMethodName");
		request.addFieldInHeader("trn", 'N', request.readValueFieldInHeader("trn"));

		request.removeParam("@t_rty");

		if (logger.isDebugEnabled()) {
			logger.logDebug("REQUEST TO SAVE REENTRY -->" + request.getProcedureRequestAsString());
		}
		Boolean reentryResponse = reentryPersister.addTransaction(request);

		IProcedureResponse response = initProcedureResponse(request);
		if (!reentryResponse.booleanValue()) {
			response.addFieldInHeader("executionResult", 'S', "1");
			response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
		} else {
			response.addFieldInHeader("executionResult", 'S', "0");
		}

		return response;

	}

}
