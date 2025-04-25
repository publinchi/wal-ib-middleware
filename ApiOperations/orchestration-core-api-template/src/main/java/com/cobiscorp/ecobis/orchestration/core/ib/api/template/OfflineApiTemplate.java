package com.cobiscorp.ecobis.orchestration.core.ib.api.template;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
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
	
	protected static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION"; 
	public String MESSAGE_RESPONSE =  "SUCCESS";
	public String transaccionDate;

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

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking TTPA: " + aServerStatusRequest.getProcedureRequestAsString());}

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking TTPA: " + wServerStatusResp.getProcedureResponseAsString());}

		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);

			if (wServerStatusResp.readValueParam("@o_en_linea") != null)
				serverResponse.setOnLine(wServerStatusResp.readValueParam("@o_en_linea").equals("S") ? true : false);

			if (wServerStatusResp.readValueParam("@o_fecha_proceso") != null) {
				transaccionDate = wServerStatusResp.readValueParam("@o_fecha_proceso");
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003
				|| wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Respuesta Devuelta: " + serverResponse);}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("TERMINANDO SERVICIO");}

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
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_originCode = " + originCode);
			logger.logDebug("request FHU-->1 " + request);
		}
		
		if (originCode == null) {
			request.addInputParam("@i_originCode", ICTSTypes.SQLINT4, "");
		}

		aBagSPJavaOrchestration.put("rty_ssn", request.readValueFieldInHeader("ssn"));

		reentryPersister = componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
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
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, saveReentry failed");}
			
			response.addFieldInHeader("executionResult", 'S', "1");
			response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
		} else {
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, saveReentry success");}
			
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
			String bank_account_number = null;

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
			
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE) || tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL)) {
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gtm"));
				}else{
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gmt"));
				}
				
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE) || tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL)) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				} else if(tipoTran.equals(Constants.AUTHORIZE_DEPOSIT)) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				}
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);

				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE)) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , null);
				} else if(tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL)) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , Constants.DEBIT_AT_STORE);
				}else if(tipoTran.equals(Constants.AUTHORIZE_DEPOSIT)) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , Constants.CREDIT_AT_STORE);
				}

				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, tipoTran);
				request.addInputParam("@i_transactionText", ICTSTypes.SQLVARCHAR, null);

				if(!tipoTran.equals(Constants.AUTHORIZE_DEPOSIT)) {
					request.addInputParam("@i_sourceAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				}
				
				if(tipoTran.equals(Constants.AUTHORIZE_DEPOSIT)) {
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				}
				
				request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_speiTransactionReferenceNumber", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_atmBankName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmLocationId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_atmTransactionId", ICTSTypes.SQLVARCHAR, null);		
				request.addInputParam("@i_atmbankBranchCode", ICTSTypes.SQLVARCHAR, null);

				request.addInputParam("@i_cardId", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_maskedCardNumber", ICTSTypes.SQLVARCHAR, null);

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
				request.addInputParam("@i_errorCode", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_errorMessage", ICTSTypes.SQLMONEY, null);
				
			} else if(canal.equals("DOCK")) {
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, externalCustomerId);
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK)){
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val_source_value"));
				}else{
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_source_value"));
				}
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK)){
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gtm"));
				}else{
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gmt"));
				}
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
			
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK) || tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL_DOCK)) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				}
				
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);

				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK)) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , null);
				} else if(tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL_DOCK)) {
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , Constants.DEBIT_AT_STORE);
				}
				
				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK)) {
					request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client-id"));
				}else{
					request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client_id"));
				}
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, tipoTran);
				request.addInputParam("@i_transactionText", ICTSTypes.SQLVARCHAR, null);

				if(!tipoTran.equals(Constants.AUTHORIZE_DEPOSIT_DOCK)) {
					request.addInputParam("@i_sourceAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_account_number"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				 }
				
				if(tipoTran.equals(Constants.AUTHORIZE_DEPOSIT_DOCK)) {
					if (aRequest.readValueParam("@i_bank_account_number") != null && !"null".equals(aRequest.readValueParam("@i_bank_account_number"))) {
						bank_account_number = aRequest.readValueParam("@i_bank_account_number");
					}else {
						bank_account_number = aRequest.readValueParam("@i_account_id");
					}
					
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR , Constants.CREDIT_AT_STORE);
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, bank_account_number);
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				}
				
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

			request.addInputParam("@i_authorizationCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_authorization_code"));
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
				
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
				
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerTransactionSuccess: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerTransactionSuccess");
			}
		}catch(Exception e){
			if (logger.isErrorEnabled()){logger.logError("Fallo catastrofico registerTransactionSuccess", e);}
		}
		
	}
	
	public void registerAllTransactionSuccess(String tipoTran, IProcedureRequest aRequest,String causal , Map<String, Object> aBagSPJavaOrchestration) {	
		if (logger.isInfoEnabled()) {logger.logInfo(" Entrando en registerAllTransactionSuccess");}

		if (logger.isDebugEnabled()) {
			logger.logDebug("transaccionDate: " +  transaccionDate);
		}
		
		try{
			IProcedureRequest request = new ProcedureRequestAS();
			String movementId = null;
			String transDate = null;
			String movementType = null;

			if(!tipoTran.equals("transferThirdPartyAccount") && !aBagSPJavaOrchestration.get("transaccionDate").toString().isEmpty()) {
				transDate = aBagSPJavaOrchestration.get("transaccionDate").toString();
			}
			
			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
			
			if(tipoTran.equals("transferThirdPartyAccount")) {
				request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				
				if (causal.equals("1010")) {
					movementType = Constants.P2P_CREDIT;
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.P2P_CREDIT);
				} else if(causal.equals("1020")) {
					movementType = Constants.P2P_DEBIT;
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.P2P_DEBIT);
				}else {
					movementType = "";
				}
				
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				
				if(movementType.equals(Constants.P2P_CREDIT)) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				} else if(movementType.equals(Constants.P2P_DEBIT)) {
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				}

				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@x_end_user_request_date"));
					
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , "0");
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , "0");
				request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, movementId);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));

				if(movementType.equals(Constants.P2P_DEBIT)){
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
					//cuenta origen
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
					//cuenta destino
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null); 
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR,aRequest.readValueParam("@i_cta_des"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
				}else if(movementType.equals(Constants.P2P_CREDIT)){
					//cuenta origen
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
					//cuenta destino
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
				}			
			}else if(tipoTran.equals("AccountCreditOperationOrchestrationCore")) {
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null); 
				request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null); 
				request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
			
				if(causal.equals("4050")) {
					movementType = Constants.BONUS;
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.BONUS);
				}
				
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR,movementType);
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber")); 		 
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude")); 
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude")); 
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));

			}else if(tipoTran.equals("AccountDebitOperationOrchestrationCore")) {
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_debitReason"));
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber"));  
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude")); 
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude")); 
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);				
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transaccionDate );
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
			}
			
			if (logger.isDebugEnabled()){
				logger.logDebug("@i_request_trans_success: "+ aRequest.readValueParam("@i_json_req"));
			}
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
				
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerAllTransactionSuccess: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerAllTransactionSuccess");
			}
		}catch(Exception e){
			if (logger.isErrorEnabled()){logger.logError("Fallo catastrofico registerAllTransactionSuccess:", e);}
		}
	}

    protected Map<String, Object> validateBvTransaction(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Initialize method validateBvTransaction");
		}
		
		 Map<String, Object> mapResponse = new  HashMap<String, Object>();
				
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
		request.addOutputParam("@o_habilitado", ICTSTypes.SYBCHAR, "N");
		
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize method validateBvTransaction");
		}
		
		// Ejecuta validacion a la tabla bv_transaccion
		IProcedureResponse tResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: " + tResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finaliza validacion local");
		
		
		mapResponse.put("@o_autenticacion",tResponse.readValueParam("@o_autenticacion"));
		mapResponse.put("@o_fuera_de_linea",tResponse.readValueParam("@o_fuera_de_linea"));
		mapResponse.put("@o_doble_autorizacion",tResponse.readValueParam("@o_doble_autorizacion"));
		mapResponse.put("@o_sincroniza_saldos",tResponse.readValueParam("@o_sincroniza_saldos"));
		mapResponse.put("@o_mostrar_costo",tResponse.readValueParam("@o_mostrar_costo"));
		mapResponse.put("@o_tipo_costo",tResponse.readValueParam("@o_tipo_costo"));
		mapResponse.put("@o_habilitado",tResponse.readValueParam("@o_habilitado"));
		
		aBagSPJavaOrchestration.put(RESPONSE_BV_TRANSACTION, tResponse);

		// Valida si ocurrio un error en la ejecucion
		if (Utils.flowError("validateBvTransaction", tResponse)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, tResponse);
		}
		
		return mapResponse;
		
		
	}

	public Boolean validateContextTransacction(Map<String, Object> aBagSPJavaOrchestration, Boolean isOnline) {
		//Valida el fuera de línea
		  boolean SUPPORT_OFFLINE = false;
		  boolean SUPPORT_HABILITA = false;
		  
		
				if (logger.isInfoEnabled())
					logger.logInfo("Llama a la funcion validateBvTransaction");
				
				
				 Map<String, Object> responseContextTrans = validateBvTransaction(aBagSPJavaOrchestration);	
				 
				 String responseHabilitado = responseContextTrans.get("@o_habilitado").toString();
				 String responseSupportOffline = responseContextTrans.get("@o_fuera_de_linea").toString();
				 
				if (logger.isInfoEnabled())
					logger.logInfo("responseSupportOffline ---> " + responseContextTrans);
				
				if(responseContextTrans == null || responseSupportOffline.isEmpty()) {
					MESSAGE_RESPONSE = "Ha ocurrido un error intentando validar si la transferencia permite fuera de línea";
					//aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea"));
					//return Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea");
					return false;
				}
				
				if(responseHabilitado == null || responseHabilitado.isEmpty()) {
					MESSAGE_RESPONSE = "Ha ocurrido un error intentando validar si la transferencia esta habilitada";
					//aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la transferencia esta habilitada"));
					//return Utils.returnException("Ha ocurrido un error intentando validar si la transferencia esta habilitada");
					return false;
				}
			
				if(responseSupportOffline.equals("S")) {
					SUPPORT_OFFLINE = true;
				}else {
					SUPPORT_OFFLINE = false;
				}
				
				if(responseHabilitado.equals("S")) {
					SUPPORT_HABILITA = true;
				}else {
					SUPPORT_HABILITA = false;
				}
				
				if (!SUPPORT_OFFLINE && !isOnline) {
					MESSAGE_RESPONSE = "Transferencia no permite ejecución mientras el servidor este fuera de linea";
					//aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea"));
					//return Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea");	
					return false;
				}
				
				if (!SUPPORT_HABILITA) {
					MESSAGE_RESPONSE = "Transaccion no habilitada, revise la parametrizacion";
					//aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Transferencia no habilitada"));
					//return Utils.returnException("Transferencia no habilitada");	
					return false;
				}
				
				return true;

	}
	
	public IProcedureResponse getValAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
	    IProcedureRequest request = new ProcedureRequestAS();
	    IProcedureResponse response = null;

	    try {
	        if (logger.isInfoEnabled()) {
	            logger.logInfo(CLASS_NAME + " Entrando en getValAccountReq");
	            logger.logInfo(CLASS_NAME + " Entrando en getValAccountReq FHU " + aBagSPJavaOrchestration.toString());
	        }

	        request.setSpName("cobis..sp_val_data_account_api");

	        // Agregar encabezados
	        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
	        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

	        Object accountNumberObj = aBagSPJavaOrchestration.get("accountNumber");
	        Object debitoCreditoObj = aBagSPJavaOrchestration.get("debitoCredito");

	        String accountNumber = (accountNumberObj != null) ? accountNumberObj.toString() : null;
	        String debitoCredito = (debitoCreditoObj != null) ? debitoCreditoObj.toString() : null;

	        if ("C".equals(debitoCredito)) {
	        	request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, accountNumber);
	        }
	        else {
	        	request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, accountNumber);
	        }
	        	        
            request.addInputParam("@i_error_ndnc", ICTSTypes.SQLVARCHAR, "S");
			
	        // Ejecutar el procedimiento
	        response = executeCoreBanking(request);

	        if (logger.isDebugEnabled()) {
	            logger.logDebug("Response Corebanking getValAccountReq FHU : " + response.getProcedureResponseAsString());
	        }
	    } catch (Exception e) {
	        logger.logError(CLASS_NAME + " Error al obtener la validación de la cuenta: " + e.getMessage(), e);
	        throw new RuntimeException("Error en la validación de la cuenta", e); 
	    } finally {
	        if (logger.isInfoEnabled()) {
	            logger.logInfo(CLASS_NAME + " Saliendo de getValAccountReq");
	        }
	    }

	    return response;
	}

}
