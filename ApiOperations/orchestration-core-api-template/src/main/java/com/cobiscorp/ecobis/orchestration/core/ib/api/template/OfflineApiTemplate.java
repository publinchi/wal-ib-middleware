package com.cobiscorp.ecobis.orchestration.core.ib.api.template;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

public abstract class OfflineApiTemplate extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(OfflineApiTemplate.class);
	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;

	public Boolean getServerStatus() throws CTSServiceException, CTSInfrastructureException {

		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse serverResponse = new ServerResponse();
		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SYBVARCHAR, "XXXX");

		if (logger.isDebugEnabled())
			logger.logDebug("Request Corebanking TTPA: " + aServerStatusRequest.getProcedureRequestAsString());

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Corebanking TTPA: " + wServerStatusResp.getProcedureResponseAsString());

		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);

			if (wServerStatusResp.readValueParam("@o_en_linea") != null)
				serverResponse.setOnLine(wServerStatusResp.readValueParam("@o_en_linea").equals("S") ? true : false);

			if (wServerStatusResp.readValueParam("@o_fecha_proceso") != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					serverResponse
							.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003
				|| wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Respuesta Devuelta: " + serverResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO");

		return serverResponse.getOnLine();
	}

	public boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest) {
		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))) {
			if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public IProcedureResponse saveReentry(IProcedureRequest wQueryRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = wQueryRequest.clone();
		ComponentLocator componentLocator = null;
		IReentryPersister reentryPersister = null;
		componentLocator = ComponentLocator.getInstance(this);

		String originCode = request.readValueParam("@i_originCode");
		logger.logDebug("@i_originCode = " + originCode);
		if (originCode == null) {
			logger.logDebug("Entre @i_originCode");
			request.addInputParam("@i_originCode", ICTSTypes.SQLINT4, "");
		}

		// Utils.addInputParam(request, "@i_externalCustomerId",
		// ICTSTypes.SQLINT4, request.readValueParam("@i_externalCustomerId"));
		aBagSPJavaOrchestration.put("rty_ssn", request.readValueFieldInHeader("ssn"));

		reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
		if (reentryPersister == null)
			throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

		request.removeFieldInHeader("sessionId");
		request.addFieldInHeader("reentryPriority", 'S', "5");
		request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
		request.addFieldInHeader("targetId", 'S', "local");
		request.removeFieldInHeader("serviceMethodName");
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
		request.removeParam("@t_rty");

		Boolean reentryResponse = reentryPersister.addTransaction(request);

		IProcedureResponse response = initProcedureResponse(request);
		if (!reentryResponse.booleanValue()) {
			logger.logDebug("Ending flow, saveReentry failed");
			response.addFieldInHeader("executionResult", 'S', "1");
			response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
		} else {
			logger.logDebug("Ending flow, saveReentry success");
			response.addFieldInHeader("executionResult", 'S', "0");
		}

		return response;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest iProcedureRequest, Map<String, Object> map) {
		return null;
	}

	protected abstract void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration);

	@Override
	public IProcedureResponse processResponse(IProcedureRequest iProcedureRequest, Map<String, Object> map) {
		return null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader iConfigurationReader) {
	}

	public void registerTransactionSuccess(String tipoTran, String canal, IProcedureRequest aRequest, String movementId, String causal, String externalCustomerId) {	
		try{
			IProcedureRequest request = new ProcedureRequestAS();

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Entrando en registerTransactionSuccess");
			}
			
			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
			request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);

			if(canal.equals("IDC")) {
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
			
				if(tipoTran.equals("Authorize Purchase") || tipoTran.equals("Authorize Withdrawal")) {
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@i_transmission_date_time_gtm"));
				}else{
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@i_transmission_date_time_gmt"));
				}
				
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");

				if(tipoTran.equals("Authorize Purchase") || tipoTran.equals("Authorize Withdrawal")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				} else if(tipoTran.equals("Authorize Deposit")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				}
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);

				if(tipoTran.equals("Authorize Purchase")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , null);
				} else if(tipoTran.equals("Authorize Withdrawal")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , "DEBIT_AT_STORE");
				}else if(tipoTran.equals("Authorize Deposit")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , "CREDIT_AT_STORE");
				}

				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, tipoTran);
				request.addInputParam("@i_transactionText", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_sourceAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
				request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");

				request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTransactionReferenceNumber", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_atmBankName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmLocationId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmTransactionId", ICTSTypes.SQLVARCHAR, null);		
				request.addInputParam("@i_atmbankBranchCode", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_cardId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_maskedCardNumber", ICTSTypes.SQLVARCHAR, null);

				//request.addInputParam("@i_storeTc", ICTSTypes.SQLVARCHAR, null);

				if(aRequest.readValueParam("@i_terminal_code") != null) {
					request.addInputParam("@i_storeTc", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
				}else {
					request.addInputParam("@i_storeTc", ICTSTypes.SQLVARCHAR, null);
				}

				request.addInputParam("@i_storeNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_store_number"));
				if(aRequest.readValueParam("@i_store_number") != null) {
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, null);

					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_institution_name"));
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_merchant_category_code"));
				} else {
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_institution_name"));
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_merchant_category_code"));

					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, null);
				}

				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);

				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);

				request.addInputParam("@i_errorCode", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_errorMessage", ICTSTypes.SQLMONEY, null);
				
			} else if(canal.equals("DOCK")) {

				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, externalCustomerId);
				if(tipoTran.equals("Authorize Purchase Dock")){
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val_source_value"));
				}else{
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_source_value"));
				}
				if(tipoTran.equals("Authorize Purchase Dock")){
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@i_transmission_date_time_gtm"));
				}else{
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@i_transmission_date_time_gmt"));
				}
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
			
				if(tipoTran.equals("Authorize Purchase Dock") || tipoTran.equals("Authorize Withdrawal Dock")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				} else if(tipoTran.equals("Authorize Deposit Dock")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				}
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);

				if(tipoTran.equals("Authorize Purchase Dock")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , null);
				} else if(tipoTran.equals("Authorize Withdrawal Dock")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , "DEBIT_AT_STORE");
				}else if(tipoTran.equals("Authorize Deposit Dock")) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , "CREDIT_AT_STORE");
				}
				
				if(tipoTran.equals("Authorize Purchase Dock")) {
					request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@x_client-id"));
				}else{
					request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@x_client_id"));
				}
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, tipoTran);
				request.addInputParam("@i_transactionText", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_sourceAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_account_number"));
				request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
				
				request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTransactionReferenceNumber", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_atmBankName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmLocationId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmTransactionId", ICTSTypes.SQLVARCHAR, null);		
				request.addInputParam("@i_atmbankBranchCode", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_cardId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
				request.addInputParam("@i_maskedCardNumber", ICTSTypes.SQLVARCHAR, null);

				if(aRequest.readValueParam("@i_store_number") != null) {
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, null);

					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment"));
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
				} else {
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment"));
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));

					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, null);
				}

				if(aRequest.readValueParam("@i_terminal_code") != null) {
					request.addInputParam("@i_storeTc", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
				}else {
					request.addInputParam("@i_storeTc", ICTSTypes.SQLVARCHAR, null);
				}
				request.addInputParam("@i_storeNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_store_number"));

				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);

				request.addInputParam("@i_errorCode", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_errorMessage", ICTSTypes.SQLMONEY, null);
			
			}
		
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@i_json_req"));
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
				
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
				
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerTransactionSuccess: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerTransactionSuccess");
			}
		}catch(Exception e){
			logger.logError("Fallo catastrofico registerTransactionSuccess");
		}
		
	}
	
	public void registerAllTransactionSuccess(String tipoTran, IProcedureRequest aRequest,String causal , Map<String, Object> aBagSPJavaOrchestration) {	
		try{
			IProcedureRequest request = new ProcedureRequestAS();
			String movementId = null;
			String transDate = (String)aBagSPJavaOrchestration.get("transaccionDate");

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Entrando en registerAllTransactionSuccess");
			}
			String movementType = null;
			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			// request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
			
			if(tipoTran.equals("transferThirdPartyAccount")) {
				request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				
				/* 
				String movementId2 = (String)aBagSPJavaOrchestration.get("o_ssn_branch");
				
				if(movementId2 == null){
					movementId2 = movementId;
				}
					*/

				if (causal.equals("1010")) {
					movementType = "P2P_CREDIT";
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, "P2P_CREDIT");
				} else if(causal.equals("1020")) {
					movementType = "P2P_DEBIT";
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, "P2P_DEBIT");
				}
				
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				if(movementType.equals("P2P_CREDIT")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				} else if(movementType.equals("P2P_DEBIT")) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				}

				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@x_end_user_request_date"));
					
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , "0");
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , "0");
				request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, movementId);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@x_request_id"));

				if(movementType.equals("P2P_DEBIT")){
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
					//cuenta origen
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
					//cuenta destino
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null); //consultar
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR,aRequest.readValueParam("@i_cta_des"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
				}else if(movementType.equals("P2P_CREDIT")){
					//cuenta origen
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
					//cuenta destino
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null); //consultar
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
				}			
			}else if(tipoTran.equals("AccountCreditOperationOrchestrationCore")) {
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
				//request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@x_end_user_request_date")); //revisar
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C"); // no tiene la orquestacion
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null); // no tiene iva la orquestacion
				if(causal.equals("4050")) {
					movementType = "BONUS";
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, "BONUS");
				}
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR,movementType);
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber")); // crear campo			 
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude")); // crear 
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude")); // crear 
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null); // consultar
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);

			}else if(tipoTran.equals("AccountDebitOperationOrchestrationCore")) {
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
				//request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , (String)aRequest.readValueParam("@x_end_user_request_date")); //revisar
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D"); // no tiene la orquestacion
				if (causal.equals("4060")) {
					movementType = "CARD_DELIVERY_FEE";
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, "CARD_DELIVERY_FEE");
				}
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR,movementType);
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, "CASHI");
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber")); // crear campo 
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude")); // crear 
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude")); // crear 
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);
				
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null); // consultar
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);

			}
			logger.logInfo("@i_request_trans_success: "+ (String)aRequest.readValueParam("@i_json_req")  +"req:"+ (String)aRequest.readValueParam("@i_json_req"));
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
			logger.logError("Fallo catastrofico registerAllTransactionSuccess");
		}
	}
	

}
