package com.cobiscorp.ecobis.orchestration.core.ib.api.template;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ApplicationException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ConstantsErrorsException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.ParameterValidationUtil;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.BusinessException;
import com.cobiscorp.ecobis.orchestration.core.ib.common.SaveAdditionalDataImpl;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;



public abstract class OfflineApiTemplate extends SPJavaOrchestrationBase {
	private static ILogger logger = LogFactory.getLogger(OfflineApiTemplate.class);
	protected static final String CLASS_NAME = "OfflineApiTemplate >-----> ";
	public String MESSAGE_RESPONSE =  "SUCCESS";
	public static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	public static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String REENTRY_EXE = "reentryExecution";
	protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
	protected static final String RESPONSE_LOCAL_VALIDATION = "RESPONSE_LOCAL_VALIDATION";
	protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
	public String transaccionDate;
	public abstract ICoreServer getCoreServer();

	public ServerResponse serverStatus() throws CTSServiceException, CTSInfrastructureException{
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse serverResponse = new ServerResponse();
		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);

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
		} else if (wServerStatusResp.getReturnCode() == ConstantsErrorsException.ERROR_40002_CODE || wServerStatusResp.getReturnCode() == ConstantsErrorsException.ERROR_40003_CODE
				|| wServerStatusResp.getReturnCode() == ConstantsErrorsException.ERROR_40004_CODE) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ConstantsErrorsException.ERROR_40002_CODE ? false : true);
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Respuesta Devuelta: " + serverResponse);}

		if (logger.isInfoEnabled()) {
			logger.logInfo("TERMINANDO SERVICIO");}

		return serverResponse;
	}

	public Boolean getServerStatus() throws CTSServiceException, CTSInfrastructureException {
		ServerResponse serverResponse = serverStatus();
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

	public IProcedureResponse saveReentry(IProcedureRequest wQueryRequest, Map<String, Object> aBagSPJavaOrchestration) {
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

				if (tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL)) {			        
			        request.addInputParam("@i_deviceIp", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
			    }

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
					
					request.addInputParam("@i_deviceIp", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
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
					//Entrymode
					request.addInputParam("@i_entryCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_crd_code"));
					request.addInputParam("@i_entryPin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_crd_pin"));
					request.addInputParam("@i_entryMode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_crd_mode"));
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


				if(causal.equals("9030") || causal.equals("9050")) {
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment"));
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, null);
				}else{
					request.addInputParam("@i_merchantEstablishmentName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_merchantTransactionId", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_storeEstablishmentName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment"));
					request.addInputParam("@i_storeTransactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
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

		try{
			IProcedureRequest request = new ProcedureRequestAS();
			String movementId = null;
			String transDate = null;
			String movementType = null;
			String description = null;
			Integer longSsnBranch = 0;
			String authorizationCode = null;
			String debitReason = ""; 
			String originMovementId = null;
			String originTrnRefNum = null;
			String amount = null;
			String externalCustomerId = null;
			String accountNumber = null;
			String concept = "";
			String reversalConcept = "";
			String originCode = "";
			String commission = "";

			if (aBagSPJavaOrchestration.get("ssn") != null) {
				movementId = (String)aBagSPJavaOrchestration.get("ssn");
			}else {
				movementId = aRequest.readValueParam("@s_ssn");
			}

			if (aBagSPJavaOrchestration.get("creditConcept") != null){
				concept = aBagSPJavaOrchestration.get("creditConcept").toString();
			}
			else if (aRequest.readValueParam("@i_creditConcept") != null) {
					concept = aRequest.readValueParam("@i_creditConcept");
			}
			
			if (aRequest.readValueParam("@i_reversal_concept") != null) {
				reversalConcept = aRequest.readValueParam("@i_reversal_concept");
			}else {
				reversalConcept = aRequest.readValueParam("@i_reversalConcept");
			}

			if(tipoTran.equals("transferThirdPartyAccount")) {
				authorizationCode = aBagSPJavaOrchestration.containsKey("o_ssn_branch") ? aBagSPJavaOrchestration.get("o_ssn_branch").toString() : null;
			}else {
				if (aBagSPJavaOrchestration.get("ssn_branch") != null) {
					authorizationCode = aBagSPJavaOrchestration.get("ssn_branch").toString();
				}
				else {
					authorizationCode = aRequest.readValueParam("@s_ssn_branch");
				}
			}
			
			if (authorizationCode != null && authorizationCode.length() >= 6) {					
				longSsnBranch = Math.max(authorizationCode.length() - 6, 0);
				authorizationCode = authorizationCode.substring(longSsnBranch);
			}
			
			if(!tipoTran.equals("transferThirdPartyAccount") && !aBagSPJavaOrchestration.get("transaccionDate").toString().isEmpty()) {
				transDate = aBagSPJavaOrchestration.get("transaccionDate").toString();
			}else {
				transDate = transaccionDate;
			}

			if ( aBagSPJavaOrchestration.get("@i_originMovementId") != null) {
				originMovementId = aBagSPJavaOrchestration.get("@i_originMovementId").toString();
			}
			
			if (aBagSPJavaOrchestration.get("@i_originReferenceNumber") != null) {
				originTrnRefNum = aBagSPJavaOrchestration.get("@i_originReferenceNumber").toString();
			}	
			
			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
			
			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");

			if(tipoTran.equals("transferThirdPartyAccount")) {
				if (causal.equals("1010")) {
					movementType = Constants.P2P_CREDIT;
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.P2P_CREDIT);
				} else if(causal.equals("1020")) {
					movementType = Constants.P2P_DEBIT;
					request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.P2P_DEBIT);
				}else {
					movementType = "";
				}

				if(aRequest.readValueParam(Constants.CONCEPTO_TRN) != null) {
					description = aRequest.readValueParam(Constants.CONCEPTO_TRN);
				} else {
					description = movementType;
				}
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, description);
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

				//cuenta origen
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
				//cuenta destino
				request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR,aRequest.readValueParam("@i_cta_des"));

				if(movementType.equals(Constants.P2P_DEBIT)){
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));					
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));					
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
				}else if(movementType.equals(Constants.P2P_CREDIT)){
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
					request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
				}			
				
				request.addInputParam("@i_deviceIp", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
				
			}else if(tipoTran.equals("AccountCreditOperationOrchestrationCore") || 
					(tipoTran.equals("ConsignmentCreditOrchestrationCore") && concept.equals(Constants.REMITTANCE_CREDIT))) {
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , aRequest.readValueParam("@i_commission"));
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
				request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));

				if(causal.equals("4050")) {
					movementType = Constants.BONUS;
				}

				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber"));
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude"));
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude"));
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));

				//REFUND
				if(concept.equals(Constants.REFUND)) {
					movementType = Constants.ACCOUNT_CREDIT;
					request.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, concept);
					request.addInputParam("@i_originCode", ICTSTypes.SQLVARCHAR, "CCA");
					request.addInputParam("@i_originMovementId", ICTSTypes.SQLINTN, originMovementId);
					request.addInputParam("@i_originTrnRefNum", ICTSTypes.SQLVARCHAR, originTrnRefNum);
				}

				//REMITTANCE
				if (concept.equals(Constants.REMITTANCE_CREDIT)) {
					movementType = Constants.ACCOUNT_CREDIT;
					request.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, Constants.REMITTANCE_CREDIT);
					request.addInputParam("@i_originCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_originCode"));
					request.addInputParam("@i_originMovementId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_originMovementId"));
					request.addInputParam("@i_originTrnRefNum", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_originReferenceNumber"));
				}

				request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);

			}else if(tipoTran.equals("AccountDebitOperationOrchestrationCore")) {
				amount = aRequest.readValueParam("@i_amount");
				externalCustomerId = aRequest.readValueParam("@i_externalCustomerId");
				accountNumber = aRequest.readValueParam("@i_accountNumber");
				
				if (aBagSPJavaOrchestration.get("@i_debitReason")!=null) {
					debitReason = aBagSPJavaOrchestration.get("@i_debitReason").toString();
				}
				
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D"); 
				
				if (logger.isDebugEnabled()){
					logger.logDebug("ALLTRN debitReason: "+ debitReason);
				}
				
				switch (debitReason) {
		            case "Card delivery fee":
		            	description = Constants.CARD_DELIVERY_FEE;
		                break;
		            case "False chargeback claim":
		            	description = Constants.FALSE_CHARGEBACK_PENALTY;
		            	break;
		            case "FALSE_CHARGEBACK":
		            	description = Constants.FALSE_CHARGEBACK;
		                break;
		            default:
		            	description = Constants.FALSE_CHARGEBACK_PENALTY;					
				}
				
				//FALSE_CHARGEBACK
				if (description.equals(Constants.FALSE_CHARGEBACK)) {
					if (aRequest.readValueParam("@i_amount_com") != null) {
						amount = aRequest.readValueParam("@i_amount_com");
					}
					if (aRequest.readValueParam("@i_externalCustomerId_ori") != null) {
						externalCustomerId = aRequest.readValueParam("@i_externalCustomerId_ori");
					}
					if (aRequest.readValueParam("@i_accountNumber_ori") != null) {
						accountNumber = aRequest.readValueParam("@i_accountNumber_ori");
					}
					if (aRequest.readValueParam("@i_movementId_com_ori") != null) {
						originMovementId =  aRequest.readValueParam("@i_movementId_com_ori");
					}
					if (aRequest.readValueParam("@i_referenceNumber_com_ori") != null) {
						originTrnRefNum =  aRequest.readValueParam("@i_referenceNumber_com_ori");
					}
				}
				
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, amount);
				request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.COMMISSION);
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, description);
				request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, externalCustomerId);
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , null);
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber"));
				request.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_latitude"));
				request.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, aRequest.readValueParam("@i_longitude"));
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR , movementId);
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_beginningBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_accountingBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_availableBalance", ICTSTypes.SQLMONEY, null);
				request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				request.addInputParam("@i_originMovementId", ICTSTypes.SQLINTN, originMovementId);
				request.addInputParam("@i_originTrnRefNum", ICTSTypes.SQLVARCHAR, originTrnRefNum);
				
			} else if(tipoTran.equals("AccountReversalOperationOrchestrationCore") || 
					(tipoTran.equals("ConsignmentCreditOrchestrationCore") && reversalConcept.equals(Constants.REMITTANCE_REVERSAL))) 
			{ 
				searchDataTransactionOrigin(aRequest, aBagSPJavaOrchestration);
				
				if (aBagSPJavaOrchestration.get("amount_ori") != null) {
					amount = aBagSPJavaOrchestration.get("amount_ori").toString();
				}
				
				if (aBagSPJavaOrchestration.get("originCode") != null) {
					originCode = aBagSPJavaOrchestration.get("originCode").toString();
				}
				
				if (reversalConcept.equals(Constants.REMITTANCE_REVERSAL)) {
					originMovementId = aRequest.readValueParam("@i_movementId");
					originTrnRefNum  = aRequest.readValueParam("@i_referenceNumber_trn");
					description = aRequest.readValueParam("@i_reversal_reason");
					accountNumber = aRequest.readValueParam("@i_accountNumber");
					externalCustomerId = aRequest.readValueParam("@i_externalCustomerId");					
				}else {
					originMovementId = aRequest.readValueParam("@i_movementId_ori");
					originTrnRefNum  = aRequest.readValueParam("@i_referenceNumber_ori");
					description = aRequest.readValueParam("@i_reversalReason_ori");
					accountNumber = aRequest.readValueParam("@i_accountNumber_ori");
					externalCustomerId = aRequest.readValueParam("@i_externalCustomerId_ori");
					commission =  aRequest.readValueParam("@i_amount_com");
				}
				
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, externalCustomerId);
				request.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, reversalConcept);
				request.addInputParam("@i_originCode", ICTSTypes.SQLVARCHAR, originCode);
				request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , Constants.MXN);
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transaccionDate);
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				request.addInputParam("@i_commission", ICTSTypes.SQLMONEY, commission);				
				request.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_referenceNumber"));
				request.addInputParam("@i_movementId", ICTSTypes.SQLMONEY, movementId);
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, description);
				request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, Constants.CREDIT_REVERSAL);
				request.addInputParam("@i_originMovementId", ICTSTypes.SQLINTN, originMovementId);
				request.addInputParam("@i_originTrnRefNum", ICTSTypes.SQLVARCHAR, originTrnRefNum);
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, amount);
				request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
			}
			
			request.addInputParam("@i_authorizationCode", ICTSTypes.SQLVARCHAR, authorizationCode);
			
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
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(Constants.ORIGINAL_REQUEST);
		IProcedureRequest request = initProcedureRequest(originalRequest);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800090");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
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

		aBagSPJavaOrchestration.put(Constants.RESPONSE_BV_TRANSACTION, tResponse);

		// Valida si ocurrio un error en la ejecucion
		if (Utils.flowError("validateBvTransaction", tResponse)) {
			aBagSPJavaOrchestration.put(Constants.RESPONSE_TRANSACTION, tResponse);
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

	public void validateContextTransacction(Map<String, Object> aBagSPJavaOrchestration) {
		boolean SUPPORT_OFFLINE;
		boolean SUPPORT_HABILITA;

		Map<String, Object> responseContextTrans = validateBvTransaction(aBagSPJavaOrchestration);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response responseContextTrans: " + responseContextTrans);
		}

		String responseHabilitado = responseContextTrans.get("@o_habilitado").toString();
		String responseSupportOffline = responseContextTrans.get("@o_fuera_de_linea").toString();

		if(responseContextTrans == null || responseSupportOffline.isEmpty()) {
			throw new ApplicationException(-1, "Ha ocurrido un error intentando validar si la transferencia permite fuera de línea");
		}

		if(responseHabilitado == null || responseHabilitado.isEmpty()) {
			throw new ApplicationException(-1, "Ha ocurrido un error intentando validar si la transferencia esta habilitada");
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

		if (!SUPPORT_OFFLINE && !(Boolean)aBagSPJavaOrchestration.get(Constants.IS_ONLINE)) {
			throw new BusinessException(-2, "Transferencia no permite ejecución mientras el servidor este fuera de linea");
		}

		if (!SUPPORT_HABILITA) {
			throw new BusinessException(-2, "Transaccion no habilitada, revise la parametrizacion");
		}
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
	
	// METODO PARA GUARDAR RE_TRAN_MONET LOCAL
	
	protected IProcedureResponse validateLocalExecution(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Inicia validacion local");

		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local originalRequest:"+originalRequest);
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(originalRequest.readValueFieldInHeader("servicio"));
		ServerResponse serverResponse;
		IProcedureResponse pResponse = null;
		try {
			serverResponse = getCoreServer().getServerStatus(serverRequest);
		
	    IProcedureRequest request = initProcedureRequest(originalRequest);	

				
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800048");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_validacion");

		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, originalRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, originalRequest.readValueParam("@s_cliente"));
		request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, originalRequest.readValueParam("@s_perfil"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, originalRequest.readValueParam("@s_servicio"));
				
		if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
		} else { // no es ejecucion de reentry
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
		}

		int t_trn = Integer.parseInt(originalRequest.readValueParam("@t_trn"));		
		if (logger.isInfoEnabled())
			logger.logInfo("t_trn a evaluar: " + t_trn);
		
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, String.valueOf(Utils.getTransactionMenu(t_trn)));	
		
		request.addInputParam("@i_prod", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_prod"));
		request.addInputParam("@i_prod_des", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_prod_des"));
		request.addInputParam("@i_login", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_login"));
		request.addInputParam("@i_cta_des", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_cta_des"));
		request.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_cta"));
		request.addInputParam("@i_concepto", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_concepto"));
		request.addInputParam("@i_val", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_val"));
		request.addInputParam("@i_mon", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_mon"));
		
		// Parametros Withdrawal
		request.addInputParam("@i_movement_type", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_movement_type"));
		request.addInputParam("@i_establishmentName", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_establishmentName")); 
		request.addInputParam("@i_transactionId", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_transactionId")); 
		request.addInputParam("@i_uuid", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_uuid")); 
		
		// Parametros Dock
		request.addInputParam("@i_card_id", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_card_id"));
		request.addInputParam("@i_pin", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_pin")); 
		request.addInputParam("@i_code", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_code")); 
		request.addInputParam("@i_mode", ICTSTypes.SYBVARCHAR, (String)aBagSPJavaOrchestration.get("i_mode")); 
		
		
		request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR,"N");
		request.addInputParam("@i_origen", ICTSTypes.SYBVARCHAR, "API");
		
		if (!getFromReentryExcecution(aBagSPJavaOrchestration)) {
			request.addInputParam("@i_genera_clave", ICTSTypes.SYBVARCHAR,"S");
			request.addInputParam("@i_valida_limites", ICTSTypes.SYBCHAR,"S");	
		}else {
			request.addInputParam("@i_valida_limites", ICTSTypes.SYBCHAR,"N");	
		}
		
		if(!serverResponse.getOnLine()) {	
			request.addInputParam("@i_linea", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_saldo", ICTSTypes.SQLVARCHAR, "S");
			request.addInputParam("@i_proceso", ICTSTypes.SYBVARCHAR,"O");
		}else {
			request.addInputParam("@i_proceso", ICTSTypes.SYBVARCHAR,"N");
			request.addInputParam("@i_linea", ICTSTypes.SQLVARCHAR, "S");
		}
			
		request.addOutputParam("@o_cliente_mis", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_prod", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_mon", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cta_des", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_mon_des", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_fecha_ini", ICTSTypes.SYBDATETIME, "01/01/2013");
		request.addOutputParam("@o_fecha_fin", ICTSTypes.SYBDATETIME, "01/01/2013");
		request.addOutputParam("@o_ult_fecha", ICTSTypes.SYBDATETIME, "01/01/2013");
		request.addOutputParam("@o_srv_host", ICTSTypes.SYBVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_cta_cobro", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_prod_cobro", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cod_mis", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_clave_bv", ICTSTypes.SYBINT4, "0");
		
		request.addOutputParam("@o_saldo_local", ICTSTypes.SQLMONEY, "0");
		request.addOutputParam("@o_aplica_tran", ICTSTypes.SYBVARCHAR, "X");

	
		if (!Utils.isNull(originalRequest.readParam("@i_val"))) {
			
			String valies=originalRequest.readParam("@i_val").toString();
			
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Valorsito " + valies);			
		}
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: Transaccion "+String.valueOf(t_trn)+" monto::::  "  );
		
		
		request.addOutputParam("@o_comision", ICTSTypes.SYBMONEY, "0");
		
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, request: " + request.getProcedureRequestAsString());

		// Ejecuta validacion
		pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: " + pResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finaliza validacion local");

		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pResponse;
	}
	
	protected Boolean getFromReentryExcecution(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled())
		{
			logger.logDebug(CLASS_NAME + "getFromReentryExcecution local originalRequest:"+request);
			logger.logDebug("getFromReentryExcecution: "+request.readValueFieldInHeader("reentryExecution"));
		}
		if (!Utils.isNull(request.readValueFieldInHeader("reentryExecution"))){
			return ("Y".equals(request.readValueFieldInHeader("reentryExecution")));
		}else
			return false;
			
	}

	
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		if (logger.isDebugEnabled())
			logger.logDebug("Ejecutando metodo updateLocalExecution: " + anOriginalRequest.toString());
		IProcedureResponse pResponse = null;
		try
		{
			
			IProcedureRequest request = initProcedureRequest(anOriginalRequest);
	
			request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
			request.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE, anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
			request.setSpName("cob_bvirtual..sp_bv_transaccion");
	
			request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
			request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
			request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
			request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
			request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
			request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "N");

			request.addInputParam("@i_causa", ICTSTypes.SQLINT4, isInteger(bag.get("causa")));
			
			// Datos de cuenta origen
			Utils.copyParam("@i_cta", anOriginalRequest, request);
			Utils.copyParam("@i_prod", anOriginalRequest, request);
			Utils.copyParam("@i_mon", anOriginalRequest, request);
			
			Utils.copyParam("@i_val", anOriginalRequest, request);
			Utils.copyParam("@i_concepto", anOriginalRequest, request);
	
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
				request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
			else
				request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));

			String error = String.valueOf(Objects.nonNull(bag.get("s_error_cobis")) ? bag.get("s_error_cobis") : bag.get("s_error"));
			request.addInputParam("@s_error", ICTSTypes.SQLVARCHAR, error);
			request.addInputParam("@s_msg", ICTSTypes.SQLVARCHAR, (String) bag.get("s_msg"));
			request.addInputParam("@i_auth_code", ICTSTypes.SQLINT4, (String)bag.get("i_auth_code"));

			//DOCK
			request.addInputParam("@i_movement_type", ICTSTypes.SQLVARCHAR, (String)bag.get("i_movement_type"));
			request.addInputParam("@i_tarjeta_mascara",ICTSTypes.SQLVARCHAR, (String)bag.get("i_tarjeta_mascara"));

			String authorizationCode = anOriginalRequest.readValueParam("@i_authorization_code");
			if (authorizationCode != null && !authorizationCode.isEmpty()) {
				request.addInputParam("@i_authorization_code", ICTSTypes.SQLVARCHAR, authorizationCode);
			}
	
			request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "S");
	
			// envio de t_rty
			if (logger.isInfoEnabled())
				logger.logInfo("Update local param reentryExecution" + request.readValueFieldInHeader("reentryExecution"));
			request.removeParam("@t_rty");
			if (getFromReentryExcecution(bag)) {
				Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
			} else { // no es ejecucion de reentry
				Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
			}
	
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Update local, request: " + request.getProcedureRequestAsString());
			}
	
			/* Ejecuta y obtiene la respuesta */
			pResponse = executeCoreBanking(request);
	
			if (logger.isDebugEnabled()) {
				logger.logDebug("Update local, response: " + pResponse.getProcedureResponseAsString());
			}
			if (logger.isInfoEnabled()) {
				logger.logInfo("Finalize Update local");
			}
			
		}catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.logError("Error Update local trn: ",e );
			}
		}
		return pResponse;
	}

    public Boolean registerMovementsP2PAdditionalData (String movType, boolean isOnline, IProcedureRequest request, IProcedureResponse response, Map<String, Object> aBagSPJavaOrchestration){
    	
	    Map<String, String> aditionalData = new HashMap<String, String>();
	    Boolean respSaveAdditionalDataImplDebt = Boolean.FALSE;
	    Boolean respSaveAdditionalDataImplCred = Boolean.FALSE;
	    String accountNumberOrg = movType.equals("TRANSFER") ? request.readValueParam("@i_cta") : request.readValueParam("@i_accountNumber");
	    String accountNumberDes = movType.equals("TRANSFER") ? request.readValueParam("@i_cta_des") : request.readValueParam("@i_accountNumber");
	    String xRequestId = aBagSPJavaOrchestration.get("x_request_id")==null ? "0" : aBagSPJavaOrchestration.get("x_request_id").toString();
	    
	    String dataDebit = (movType.equals("TRANSFER") 
	    		            ? request.readValueParam("@o_benef_cta_des") + "|" + accountNumberDes
	    		            : request.readValueParam("@o_benef_cta_org") + "|" + accountNumberOrg)
	    		   + "|" + (movType.equals("TRANSFER") ? xRequestId : "0") 
	    		   + "|" + movType.charAt(0);
	    
	    String dataCredit = (movType.equals("TRANSFER") 
	                         ? request.readValueParam("@o_benef_cta_org") + "|" + accountNumberOrg
	                         : request.readValueParam("@o_benef_cta_des") + "|" + accountNumberDes)
	    		   + "|" + (movType.equals("TRANSFER") ? xRequestId : "0")  
	               + "|" + movType.charAt(0);
	    
	    aditionalData.put("secuential", isOnline ? response.readValueParam("@o_ssn"): response.readValueFieldInHeader("ssn"));  // request.readValueParam("@s_ssn")
	    aditionalData.put("secBranch" , isOnline ? response.readValueParam("@o_ssn_branch"): request.readValueParam("@o_referencia"));
	    aditionalData.put("transaction", request.readValueFieldInHeader("trn"));

	    if (movType.equals("DEBIT") || movType.equals("TRANSFER")){
			SaveAdditionalDataImpl aditionalDataProcDebt = new SaveAdditionalDataImpl();
		    aditionalData.put("alternateCod", request.readValueParam("@o_cod_alt_org"));
		    aditionalData.put("movementType", Constants.P2P_DEBIT);
		    aditionalData.put("data", dataDebit);
		    respSaveAdditionalDataImplDebt = aditionalDataProcDebt.saveData(Constants.P2P_DEBIT, isOnline, aditionalData);
		    respSaveAdditionalDataImplDebt = respSaveAdditionalDataImplDebt != null ? respSaveAdditionalDataImplDebt : Boolean.FALSE;
		    
			if (logger.isDebugEnabled())
				logger.logDebug("Registro datos adicionales P2P  Transacción: " + movType + " Tipo: Débito"
						+ " Codigo alterno org: " + request.readValueParam("@o_cod_alt_org")
						+ " Datos adicionales org: " + dataDebit
						+ " Resultado: " + (respSaveAdditionalDataImplDebt ? "Exitoso" : "Fallido"));
	    }
	    
	    if (movType.equals("CREDIT") || movType.equals("TRANSFER")){
	    	SaveAdditionalDataImpl aditionalDataProcCred = new SaveAdditionalDataImpl();
			aditionalData.remove("alternateCod");
			aditionalData.remove("movementType");
			aditionalData.remove("data");
		    aditionalData.put("alternateCod", request.readValueParam("@o_cod_alt_des"));
		    aditionalData.put("movementType", Constants.P2P_CREDIT);
		    aditionalData.put("data", dataCredit);
		    respSaveAdditionalDataImplCred = aditionalDataProcCred.saveData(Constants.P2P_CREDIT, isOnline, aditionalData);
		    respSaveAdditionalDataImplCred = respSaveAdditionalDataImplCred != null ? respSaveAdditionalDataImplCred : Boolean.FALSE;
		    
            if (logger.isDebugEnabled())
				logger.logDebug("Registro datos adicionales P2P  Transacción: " + movType + " Tipo: Crédito"
						+ " Codigo alterno des: " + request.readValueParam("@o_cod_alt_des")
						+ " Datos adicionales des: " + dataCredit
						+ " Resultado: " + (respSaveAdditionalDataImplCred ? "Exitoso" : "Fallido"));
	    }
	    
	    return movType.equals("DEBIT") 
	    	    ? respSaveAdditionalDataImplDebt 
	    	    : movType.equals("CREDIT") 
	    	        ? respSaveAdditionalDataImplCred 
	    	        : (respSaveAdditionalDataImplDebt && respSaveAdditionalDataImplCred 
	    	            ? Boolean.TRUE 
	    	            : Boolean.FALSE);
	}

	private String isInteger(Object obj) {
		if (obj == null) {
			return null; // Null objects are not integers
		}

		if (obj instanceof Integer) {
			return obj.toString(); // Already an integer, return its string representation
		}
	
		// Check if the object is a String
		if (obj instanceof String) {
			String str = (String) obj;
	
			if (str.isEmpty()) {
				return null; // Empty strings are not integers
			}
	
			try {
				Integer.parseInt(str); // Try parsing the string as an integer
				return obj.toString(); // Successfully parsed, it is an integer
			} catch (NumberFormatException e) {
				return null; // Parsing failed, not an integer
			}
		}
		return null; // Not an integer or string, return null
	}

	public IProcedureResponse logIdempotence(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][logIdempotence]");
		}

		String xRequestId = aRequest.readValueParam("@x_request_id");
		String xEndUserRequestDateTime = aRequest.readValueParam("@x_end_user_request_date");
		String xEndUserIp = aRequest.readValueParam("@x_end_user_ip");
		String xChannel = aRequest.readValueParam("@x_channel");
		String xProcess = (String)aBagSPJavaOrchestration.get("process");
		aBagSPJavaOrchestration.put(Constants.PROCESS_DATE, xEndUserRequestDateTime);

		IProcedureRequest idempotenceRequest = new ProcedureRequestAS();

		idempotenceRequest.setSpName("cob_bvirtual..sp_idempotency_ope_reg");
		idempotenceRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		idempotenceRequest.addFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, ICOBISTS.HEADER_STRING_TYPE, Constants.COBIS_CONTEXT);
		idempotenceRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500111");
		idempotenceRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		idempotenceRequest.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, xRequestId);
		idempotenceRequest.addInputParam("@x_end_user_request_date",ICTSTypes.SQLVARCHAR, xEndUserRequestDateTime);
		idempotenceRequest.addInputParam("@x_end_user_ip",ICTSTypes.SQLVARCHAR, xEndUserIp);
		idempotenceRequest.addInputParam("@x_channel",ICTSTypes.SQLVARCHAR, xChannel);
		idempotenceRequest.addInputParam("@x_process",ICTSTypes.SQLVARCHAR, xProcess);

		if (logger.isDebugEnabled()) {
			logger.logDebug("REQUEST [idempotenceRequest] " + idempotenceRequest.getProcedureRequestAsString());
		}

		return executeCoreBanking(idempotenceRequest);
	}

	/**
	 * Método para establecer errores
	 */
	protected void setError(Map<String, Object> aBagSPJavaOrchestration, String errorCode, String errorMessage) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][setError]");
		}
		aBagSPJavaOrchestration.put(Constants.IS_ERRORS, true);
		aBagSPJavaOrchestration.put("error_code", errorCode);
		aBagSPJavaOrchestration.put("error_message", errorMessage);
		aBagSPJavaOrchestration.put("code",errorCode);
		aBagSPJavaOrchestration.put("msg",errorMessage);
	}


	public void registerTransactionFailed(String tipoTran, String canal, IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isInfoEnabled()) {logger.logInfo(" Entrando en registerTransactionFailed");}

		String movementId = "0";
		String codeError = "0";
		String messageError = "";
		String causal = "";
		String transDate = "";
		String movementType = null;
		String bankAccountNumber = null;
		String description = tipoTran;
		String debitReason = "";
		String amount = null;
		String extCustomerId = null;
		String accountNumber = null;
		String concept = "";
		String reversalConcept = "";

		try {
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

			if (aBagSPJavaOrchestration.get("@i_debitReason")!=null) {
				debitReason = aBagSPJavaOrchestration.get("@i_debitReason").toString();
			}

			if (aBagSPJavaOrchestration.get("creditConcept") != null){
				concept = aBagSPJavaOrchestration.get("creditConcept").toString();
			}
			else if (aRequest.readValueParam("@i_creditConcept") != null) {
				concept = aRequest.readValueParam("@i_creditConcept");
			}
			
			if (aRequest.readValueParam("@i_reversal_concept") != null) {
				reversalConcept = aRequest.readValueParam("@i_reversal_concept");
			}
			else if (aRequest.readValueParam("@i_reversalConcept") != null){
				reversalConcept = aRequest.readValueParam("@i_reversalConcept");
			}
			
			if (aBagSPJavaOrchestration.get("transaccionDate")!= null) {
				transDate = aBagSPJavaOrchestration.get("transaccionDate").toString();
			}else {
				transDate = transaccionDate;
			}
			
			IProcedureRequest request = new ProcedureRequestAS();

			request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
			request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION FAILED");
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
			request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
			request.addInputParam("@i_errorDetailsCode", ICTSTypes.SQLINTN, codeError);
			request.addInputParam("@i_errorDetailsMessage", ICTSTypes.SQLVARCHAR, messageError);
			request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, movementId);
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
			request.addInputParam("@i_cardId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));

            // Manejo de transacciones P2P
			if ("transferThirdPartyAccount".equals(tipoTran)) {	 
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
				request.addInputParam("@i_movementId", ICTSTypes.SQLVARCHAR, movementId);
				
		        if ("1010".equals(causal)) {
					description = Constants.P2P_CREDIT;
					movementType = Constants.P2P_CREDIT;
		            request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "C");
		            request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		            request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
		            request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		            request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("originAccountType"));
					request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
		        
		         	request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
					request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
					
		        } else if ("1020".equals(causal)) {
					description = Constants.P2P_DEBIT;
					movementType = Constants.P2P_DEBIT;
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		            request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "D");
		            request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		            request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
		            request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		            request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("destinationAccountType"));
		        	request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
					request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
		        }

				if(aRequest.readValueParam(Constants.CONCEPTO_TRN) != null) {
					description = aRequest.readValueParam(Constants.CONCEPTO_TRN);
				}
			}	        	               

			//Credi at store -- Debi at store
		    if (canal.equals("IDC")){
		    	request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		        request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		        request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
		        request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transmission_date_time_gmt"));
		      
		        if (tipoTran.equals(Constants.AUTHORIZE_DEPOSIT)) {
		        	movementType = Constants.CREDIT_AT_STORE;
		            request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "C");
		            String safeCausal = (causal != null && !causal.isEmpty()) ? causal : "4010";
		            request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, safeCausal);	                
		                          
		            //Parámetros adicionales solo si es Authorize Deposit
		            request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));
		            request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
		            request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		            request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, null);
		            
		        } else if (tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL)) {
		        	movementType = Constants.DEBIT_AT_STORE;
		            request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "D");
		            request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gtm"));
		            
		            String safeCausal = (causal != null && !causal.isEmpty()) ? causal : "4020"; 
		            request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, safeCausal);
		       }
		    }
		
			if(canal.equals("DOCK")) 
			{
				if (tipoTran.equals(Constants.AUTHORIZE_DEPOSIT_DOCK) || tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL_DOCK))
				{		
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_source_value"));
			    	request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client_id"));
			    	request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gmt"));
			    	request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente").toString());
			    			
					if (tipoTran.equals(Constants.AUTHORIZE_DEPOSIT_DOCK))
					{					
						movementType = Constants.CREDIT_AT_STORE;
						request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "C");
			            request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_account_number"));
			            
			        	if (aRequest.readValueParam("@i_bank_account_number") != null ) {
							bankAccountNumber = aRequest.readValueParam("@i_bank_account_number");
						}else {
							bankAccountNumber = aRequest.readValueParam("@i_account_id");
						}
		            
						request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLVARCHAR, null);
						request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
						request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR,bankAccountNumber);
						request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destiny_account_type"));
				
					} else if (tipoTran.equals(Constants.AUTHORIZE_WITHDRAWAL_DOCK)) {
						movementType = Constants.DEBIT_AT_STORE;
						request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR, "D");
			          
			        }
				}

				if(tipoTran.equals(Constants.AUTHORIZE_PURCHASE_DOCK)){
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
					request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client-id"));
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val_source_value"));
					request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_transmission_date_time_gtm"));
					//Objeto destination
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_id"));
					request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
				}
			}else if(tipoTran.equals("AccountCreditOperationOrchestrationCore") || tipoTran.equals("AccountDebitOperationOrchestrationCore") || 
					(tipoTran.equals("ConsignmentCreditOrchestrationCore") && concept.equals(Constants.REMITTANCE_CREDIT) )) 
			{
				amount = aRequest.readValueParam("@i_amount");
				extCustomerId = aRequest.readValueParam("@i_externalCustomerId");
				accountNumber = aRequest.readValueParam("@i_accountNumber");
								
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR ,transDate );
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
				
				if(tipoTran.equals("AccountCreditOperationOrchestrationCore") || tipoTran.equals("ConsignmentCreditOrchestrationCore")) {
					description = Constants.BONUS;
					movementType = Constants.BONUS;

					if(concept.equals(Constants.REFUND)) {
						movementType = Constants.ACCOUNT_CREDIT;
						description = Constants.ACCOUNT_CREDIT;
					}

					if(concept.equals(Constants.REMITTANCE_CREDIT)) {
						movementType = Constants.ACCOUNT_CREDIT;
						description = Constants.REMITTANCE_CREDIT;
					}
					
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
					request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
					request.addInputParam("@i_destinationBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, null);
					request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLINTN, extCustomerId);					
				}
				
				if(tipoTran.equals("AccountDebitOperationOrchestrationCore")) 
				{
					movementType = Constants.COMMISSION;
					
					switch (debitReason) {
			            case "Card delivery fee":
			            	description = Constants.CARD_DELIVERY_FEE;
			                break;
			            case "False chargeback claim":
			            	description = Constants.FALSE_CHARGEBACK_PENALTY;
			            	break;
			            case "FALSE_CHARGEBACK":
			            	description = Constants.FALSE_CHARGEBACK;
			                break;
			            default:
			            	description = Constants.FALSE_CHARGEBACK_PENALTY;
					}

					if (description.equals(Constants.FALSE_CHARGEBACK)) {
						if (aRequest.readValueParam("@i_externalCustomerId_ori") != null) {
							extCustomerId = aRequest.readValueParam("@i_externalCustomerId_ori");
						}
						if (aRequest.readValueParam("@i_accountNumber_ori") != null) {
							accountNumber = aRequest.readValueParam("@i_accountNumber_ori");
						}
						if (aRequest.readValueParam("@i_amount_com") != null) {
							amount = aRequest.readValueParam("@i_amount_com");
						}
					}
					request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
					request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, Constants.CASHI);
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				}
				
				request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, extCustomerId);
				request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, amount);
			}

			if(tipoTran.equals("AccountReversalOperationOrchestrationCore") || 
			  (tipoTran.equals("ConsignmentCreditOrchestrationCore") && reversalConcept.equals(Constants.REMITTANCE_REVERSAL))) {
				movementType = Constants.CREDIT_REVERSAL;
				description = Constants.CREDIT_REVERSAL;
				
				if (tipoTran.equals("AccountReversalOperationOrchestrationCore")) {
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber_ori"));
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId_ori"));
				}
				else if (tipoTran.equals("ConsignmentCreditOrchestrationCore")){
					description = Constants.REMITTANCE_REVERSAL;
					request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
					request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
				}				
				
				searchDataTransactionOrigin(aRequest, aBagSPJavaOrchestration);	
				
				if (aBagSPJavaOrchestration.get("amount_ori") != null) {
					request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aBagSPJavaOrchestration.get("amount_ori").toString());
				}
				
				request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR, transDate);
				request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "D");
				request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
			}

			request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, description);
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerTransactionFailed: " + wProductsQueryResp.getProcedureResponseAsString());
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerTransactionFailed");
			}
		}catch(Exception e){
			if (logger.isErrorEnabled()){logger.logError("Fallo catastrofico registerTransactionFailed:", e);}
		}
	}

	public boolean registerMovementsAuthAdditionalData(boolean isOnline, String provider, String movementType, String sequential, String secBranch, String codAlter,
													   String authorizationCode, String maskedCardNumber, IProcedureRequest aRequest){
		try{
			String transaction  = aRequest.readValueParam("@t_trn");
			if (logger.isInfoEnabled()) {
				logger.logInfo(" Entrando en registerMovementsAdditionalData");
			}
			String externalCustomerId = aRequest.readValueParam("@i_external_customer_id");
			SaveAdditionalDataImpl saveAdditional = new SaveAdditionalDataImpl();
			Map<String, String> additionalData = new HashMap<String, String>();

			String cardId = aRequest.readValueParam("@i_card_id");
			String typeAuth = aRequest.readValueParam("@i_type");
			String locationId = aRequest.readValueParam("@i_terminal_code");
			String bankBranchCode = aRequest.readValueParam("@i_bank_branch_number");
			String transactionID = aRequest.readValueParam("@i_retrieval_reference_number");
			String establishmentName = aRequest.readValueParam("@i_establishment");
			String institutionName = aRequest.readValueParam("@i_institution_name");
			String requestID = aRequest.readValueParam("@i_uuid") == null ? aRequest.readValueParam("@x_uuid") :
					aRequest.readValueParam("@i_uuid");
			String cardEntryCode =  aRequest.readValueParam("@i_card_entry_code");
			String cardEntryPin =  aRequest.readValueParam("@i_pin");
			String cardEntryMode =  aRequest.readValueParam("@i_mode");
			String data = String.join("|", provider, cardId, typeAuth, locationId,
					bankBranchCode,transactionID,establishmentName,institutionName,
					authorizationCode,externalCustomerId,requestID,maskedCardNumber,
					cardEntryCode, cardEntryPin, cardEntryMode,"N");
			additionalData.put("secuential", sequential);
			additionalData.put("secBranch", secBranch);
			additionalData.put("alternateCod", codAlter);
			additionalData.put("transaction", transaction);
			additionalData.put("movementType", movementType);
			additionalData.put("provider", provider);
			additionalData.put("data",data);



			Boolean res = saveAdditional.saveData(movementType, isOnline, additionalData );

			if(Boolean.TRUE.equals(res) && logger.isInfoEnabled()) {
				logger.logInfo("saveAditionalData: " + res);
			}

			return res;

		}catch(Exception e){
			if (logger.isErrorEnabled()){logger.logError("Fallo  registerMovementsAdditionalData", e);}
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerMovementsAdditionalData");
			}
		}
		return false;
	}

	public Boolean registerMovementsCreditDebitAdditionalData (String operation, boolean isOnline, IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration){
		logger.logDebug("KCZ REFOUND: "+aBagSPJavaOrchestration.toString());
		SaveAdditionalDataImpl saveAdditionalData = new SaveAdditionalDataImpl();
		String movementType = "";
		Map<String, String> additionalData = new HashMap<String, String>();
		String transaction = request.readValueFieldInHeader("trn");
		String accountNumber = "CREDIT_REVERSAL".equals(operation) ? request.readValueParam("@i_accountNumber_ori")
				: request.readValueParam("@i_accountNumber");
		String xRequestId = request.readValueParam("@x_request_id");
		String sequential = (String) aBagSPJavaOrchestration.get("@o_ssn");
		String secBranch = (String) aBagSPJavaOrchestration.get("@o_ssn_branch");
		String data = String.join("|",  accountNumber, xRequestId);
		boolean isSaved = false;

		additionalData.put("secuential", sequential);
		additionalData.put("secBranch" , secBranch);
		additionalData.put("transaction",transaction);

		if("DEBIT".equals(operation)){
			movementType = "COMMISSION";
			String alternateCod = (String) aBagSPJavaOrchestration.get("@o_cod_alt_org");
			String debitConcept = request.readValueParam("@i_debitConcept");
			String originMovementId = request.readValueParam("@i_originMovementId");
			String originReferenceNumber = request.readValueParam("@i_originReferenceNumber");
			additionalData.put("alternateCod", alternateCod);
			data = data + '|' +  String.join("|", debitConcept,originMovementId,originReferenceNumber);
			additionalData.put("data", data);
			isSaved = saveAdditionalData.saveData(movementType,isOnline,additionalData);

		}else if("CREDIT".equals(operation)){
			movementType = "ACCOUNT_CREDIT";
			String alternateCod = (String) aBagSPJavaOrchestration.get("@o_cod_alt_des");
			String creditConcept = request.readValueParam("@i_creditConcept");
			String originMovementId = request.readValueParam("@i_originMovementId");
			String originReferenceNumber = request.readValueParam("@i_originReferenceNumber");
			String originCode = request.readValueParam("@i_originCode");
			additionalData.put("alternateCod", alternateCod);
			data = data + '|' + String.join("|",creditConcept,originMovementId,originReferenceNumber, originCode);
			additionalData.put("data", data);
			isSaved = saveAdditionalData.saveData(movementType,isOnline,additionalData);

		} else if("CREDIT_REVERSAL".equals(operation)){
			movementType = "CREDIT_REVERSAL";
			String alternateCod = (String) aBagSPJavaOrchestration.get("@o_cod_alt_org");
			String alternateCodCom = (String) aBagSPJavaOrchestration.get("@o_cod_alt_com");
			String reversalConcept = request.readValueParam("@i_reversalConcept");
			String originMovementId = request.readValueParam("@i_movementId_ori");
			String originReferenceNumber = request.readValueParam("@i_referenceNumber_ori");
			//String commissionOriginMovementId = request.readValueParam("@i_movementId_com_ori");
			//String commissionOriginReferenceNumber = request.readValueParam("@i_referenceNumber_com_ori");
			additionalData.put("alternateCod", alternateCod);
			data = data + '|' + String.join("|", reversalConcept,originMovementId,originReferenceNumber);
			additionalData.put("data", data);
			isSaved = saveAdditionalData.saveData(movementType,isOnline,additionalData);
			if (alternateCodCom != null) {
				IProcedureRequest debitRequest = request.clone();
				Map<String, Object> debitBag = new HashMap<>(aBagSPJavaOrchestration);
				String commissionOriginMovementId = request.readValueParam("@i_movementId_com_ori");
				String commissionOriginReferenceNumber = request.readValueParam("@i_referenceNumber_com_ori");
				debitRequest.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				debitRequest.addInputParam("@i_debitConcept", ICTSTypes.SQLVARCHAR, reversalConcept);
				debitRequest.addInputParam("@i_originMovementId", ICTSTypes.SQLVARCHAR, commissionOriginMovementId);
				debitRequest.addInputParam("@i_originReferenceNumber", ICTSTypes.SQLVARCHAR, commissionOriginReferenceNumber);
				debitBag.put("@o_cod_alt_org", alternateCodCom);
				return registerMovementsCreditDebitAdditionalData("DEBIT", isOnline, debitRequest, debitBag);
			}
		}
		return isSaved;
	}

	protected void validateParameters(Map<String, Object> aBagSPJavaOrchestration ){
		Object[] validations = (Object[]) aBagSPJavaOrchestration.get(Constants.PARAMETERS_VALIDATE);

		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(Constants.ORIGINAL_REQUEST);

		if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][validateParameters]");
        }

		for (Object validation : validations) {
			if (validation instanceof ParameterValidationUtil) {
				ParameterValidationUtil v = (ParameterValidationUtil) validation;
				//String paramValue = (String) aBagSPJavaOrchestration.get(v.getParamName());
				String paramValue = originalRequest.readValueParam(v.getParamName());

				// Realiza las validaciones según el tipo especificado
				switch (v.getType()) {
					case NOT_EMPTY:
						if (paramValue == null || paramValue.isEmpty()) {
							throw new BusinessException(v.getErrorCode(), v.getErrorMessage());
						}
						break;
					case LENGTH:
						Integer expectedLength = (Integer) v.getAdditionalParam("expectedLength");
						if (paramValue.length() != expectedLength) {
							throw new BusinessException(v.getErrorCode(), v.getErrorMessage());
						}
						break;
					case GREATER_THAN_ZERO_INTEGER:
						if (Integer.parseInt(paramValue) <= 0) {
							throw new BusinessException(v.getErrorCode(), v.getErrorMessage());
						}
						break;
					case IS_DOUBLE:
						if (!isValidDouble(paramValue)) {
							throw new BusinessException(v.getErrorCode(), v.getErrorMessage());
						}
						break;
					case GREATER_THAN_ZERO_DOUBLE:
						if (Double.parseDouble(paramValue) <= 0) {
							throw new BusinessException(v.getErrorCode(), v.getErrorMessage());
						}
						break;
				}
			}
		}
	}

	private boolean isValidDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false; // Null or empty strings are not valid doubles
        }
        try {
            Double.parseDouble(str);
            return true; // If parsing succeeds, it's a valid double
        } catch (NumberFormatException e) {
            return false; // If parsing fails, it's not a valid double
        }
    }

	public void searchDataTransactionOrigin(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		String amountOri = "";
		Double valOrigin = 0.00;
		String originMovementId = "";
		String originTrnRefNum = "";
		String concept = "";
		String accountNumber = "";
		String externalCustomerId = "";
		String commission = "";
		String originCode = "";

		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][searchDataTransactionOrigin]");
		}

		try{
			if (anOriginalRequest.readValueParam("@i_reversal_concept") != null) {
				concept = anOriginalRequest.readValueParam("@i_reversal_concept");
			}else {
				concept = anOriginalRequest.readValueParam("@i_reversalConcept");
			}

			if (concept.equals(Constants.REMITTANCE_REVERSAL)) {
			   accountNumber = anOriginalRequest.readValueParam("@i_accountNumber");
			   externalCustomerId = anOriginalRequest.readValueParam("@i_externalCustomerId");
			   originMovementId = anOriginalRequest.readValueParam("@i_movementId");
			   originTrnRefNum = anOriginalRequest.readValueParam("@i_referenceNumber_trn");
			}else {
			   accountNumber = anOriginalRequest.readValueParam("@i_accountNumber_ori");
			   externalCustomerId = anOriginalRequest.readValueParam("@i_externalCustomerId_ori");
			   originMovementId = anOriginalRequest.readValueParam("@i_movementId_ori");
			   originTrnRefNum = anOriginalRequest.readValueParam("@i_referenceNumber_ori");
			}
			
			IProcedureRequest reqTMPCentral = new ProcedureRequestAS();

			reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
			reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
			reqTMPCentral.setSpName("cob_bvirtual..sp_bv_cons_val_webhook_central");
			reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
			reqTMPCentral.addInputParam("@i_movementId", ICTSTypes.SQLINT4, originMovementId);
			reqTMPCentral.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, originTrnRefNum);
			reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, externalCustomerId);
			reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
			reqTMPCentral.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, concept);
			reqTMPCentral.addOutputParam("@o_amount", ICTSTypes.SQLMONEY, "0");
			reqTMPCentral.addOutputParam("@o_commission", ICTSTypes.SQLMONEY, "0");
			reqTMPCentral.addOutputParam("@o_originCode", ICTSTypes.SQLVARCHAR, "X");

			IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);

			if (logger.isDebugEnabled()) {
				logger.logDebug("Response executeCoreBanking cob_bvirtual..sp_bv_cons_val_webhook_central: " + wProcedureResponseCentral.getProcedureResponseAsString());
			}

			amountOri  =  wProcedureResponseCentral.readValueParam("@o_amount");
			commission =  wProcedureResponseCentral.readValueParam("@o_commission");
			originCode =  wProcedureResponseCentral.readValueParam("@o_originCode");

			if (amountOri != null) {
				valOrigin = Double.parseDouble(amountOri);
			}

			if (valOrigin.equals(0.00) || valOrigin < 0.00) {
				IProcedureRequest reqTMPLocal = new ProcedureRequestAS();

				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
				reqTMPLocal.setSpName("cob_bvirtual..sp_bv_cons_val_webhook_local");
				reqTMPLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
				reqTMPLocal.addInputParam("@i_movementId", ICTSTypes.SQLINT4, originMovementId);
				reqTMPLocal.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, originTrnRefNum);
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, externalCustomerId);
				reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, concept);
				reqTMPLocal.addOutputParam("@o_amount", ICTSTypes.SQLMONEY, "0");
				reqTMPLocal.addOutputParam("@o_commission", ICTSTypes.SQLMONEY, "0");
				reqTMPLocal.addOutputParam("@o_originCode", ICTSTypes.SQLVARCHAR, "X");

				IProcedureResponse wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);

				if (logger.isDebugEnabled()) {
					logger.logDebug("Response executeCoreBanking cob_bvirtual..sp_bv_cons_val_webhook_local: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				amountOri =  wProcedureResponseLocal.readValueParam("@o_amount");
				commission =  wProcedureResponseCentral.readValueParam("@o_commission");
				originCode =  wProcedureResponseCentral.readValueParam("@o_originCode");
			}

			aBagSPJavaOrchestration.put("amount_ori", amountOri);
			aBagSPJavaOrchestration.put("commission_ori", commission);
			aBagSPJavaOrchestration.put("originCode", originCode);

		} catch (NumberFormatException e) {
			if (logger.isErrorEnabled()) {
				logger.logError(CLASS_NAME + " Numero no es valido: " + e.getMessage(), e);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.logError(CLASS_NAME + " Error al obtener datos de transaccion de origen: " + e.getMessage(), e);
			}
			throw new RuntimeException("Error al obtener datos:", e);
		} finally{
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Saliendo de searchDataTransactionOrigin");
			}
		}

	}
}
