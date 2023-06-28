package com.cobiscorp.ecobis.ib.orchestration.base.transfers;

import java.awt.print.PrinterException;
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
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.common.AccountCoreSignersValidation;

/**
 * @author schancay
 * @since Aug 14, 2014
 * @version 1.0.0
 */
public abstract class TransferBaseTemplate extends SPJavaOrchestrationBase {
	protected static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
	protected static final String RESPONSE_OFFLINE = "RESPONSE_OFFLINE";
	protected static final String RESPONSE_TRANSFER = "RESPONSE_TRANSFER";
	protected static final String RESPONSE_FIND_OFFICERS = "RESPONSE_FIND_OFFICERS";
	protected static final String RESPONSE_QUERY_SIGNER = "RESPONSE_QUERY_SIGNER";
	protected static final String RESPONSE_LOCAL_VALIDATION = "RESPONSE_LOCAL_VALIDATION";
	protected static final String RESPONSE_CENTRAL_VALIDATION = "RESPONSE_CENTRAL_VALIDATION";
	protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION"; 
	protected static final String REENTRY_EXE = "reentryExecution";
	protected static final String TRANSFER_NAME = "TRANSFER_NAME";
	protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
	protected static final int CODE_OFFLINE = 40004;
	protected static final String TYPE_REENTRY_OFF_SPI="S";
	protected static final String TYPE_REENTRY_OFF="OFF_LINE";
	protected static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";
	private static ILogger logger = LogFactory.getLogger(TransferBaseTemplate.class);

	/**
	 * Constant controller offline functionality activation.<br>
	 * When this value is true the functionality is enabled.
	 */
	public boolean SUPPORT_OFFLINE = false;

	/**
	 * Methods for Dependency Injection.
	 * 
	 * @return ICoreServiceNotification
	 */
	protected abstract ICoreServiceSendNotification getCoreServiceNotification();

	public abstract ICoreService getCoreService();

	public abstract ICoreServer getCoreServer();

	/**
	 * Method for core preconditions validation.
	 * 
	 * @param IProcedureRequest request
	 * @param Map<String, Object> aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	protected abstract IProcedureResponse validateCentralExecution(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

	public abstract NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest, OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration);

	protected abstract void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest, IProcedureRequest anOriginalRequest);

	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

	/**
	 * Finds account officers for notification.
	 * 
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private OfficerByAccountResponse findOfficers(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "START");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "request: " + anOriginalRequest.getProcedureRequestAsString());

		try {
			OfficerByAccountRequest request = new OfficerByAccountRequest();

			Product product = new Product();
			if (anOriginalRequest.readValueParam("@i_cta") != null)
				product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
			if (anOriginalRequest.readValueParam("@i_prod") != null)
				product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
			request.setProduct(product);

			OfficerByAccountResponse response = getCoreService().getOfficerByAccount(request);

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "response: " + response);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "END");

			return response;
		} catch (CTSServiceException e) {
			e.printStackTrace();
			logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			return null;
		}
	}

	/**
	 * Sends notification of the transaction.
	 * 
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */

	protected IProcedureResponse sendNotification(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		OfficerByAccountResponse findOfficersExecutionResponse = findOfficers(anOriginalRequest.clone(), aBagSPJavaOrchestration);
		NotificationRequest notificationRequest = transformNotificationRequest(anOriginalRequest, findOfficersExecutionResponse, aBagSPJavaOrchestration);
		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		ServerResponse responseServer = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login"))) {
			client.setLogin(anOriginalRequest.readValueParam("@i_login"));
		}

		logger.logInfo("CARGADO APPLY DATE "+aBagSPJavaOrchestration.get("APPLY_DATE"));

		if(aBagSPJavaOrchestration.containsKey("APPLY_DATE")
				&& aBagSPJavaOrchestration.get("APPLY_DATE")!=null){
			notificationRequest.getNotificationDetail().
					setAuxiliary27(aBagSPJavaOrchestration.get("APPLY_DATE").toString());

		}else{
			logger.logInfo("No hay fecha transacción");
		}

		notificationRequest.setClient(client);
		notificationRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));

		if (!responseServer.getOnLine())
			notificationRequest.getNotification().setNotificationType("O"); // offline
		else {
			if (responseTransaction.getReturnCode() != 0) {
				if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
					notificationRequest.getNotification().setNotificationType("E"); // en linea, con error y por reentry
					if (responseTransaction.getMessageListSize() > 0)
						notificationRequest.getNotificationDetail().setAuxiliary10(generaMensaje(responseTransaction.getMessage(1).getMessageText()));
				}
			} else
				notificationRequest.getNotification().setNotificationType("F"); // en linea y ok
		}

		// NotificationResponse notificationResponse = getCoreServiceNotification().sendNotification(transformNotificationRequest(anOriginalRequest,
		// findOfficersExecutionResponse, aBagSPJavaOrchestration));
		NotificationResponse notificationResponse = getCoreServiceNotification().sendNotification(notificationRequest);

		if (!notificationResponse.getSuccess()) {
			if (logger.isDebugEnabled()) {
				logger.logDebug(" Error enviando notificaciÃ³n: " + notificationResponse.getMessage().getCode() + " - " + notificationResponse.getMessage().getDescription());
			}
		}

		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);

		return response;

	}

	public String generaMensaje(String vars) {
		vars = vars.substring(vars.indexOf("]") + 1, vars.length());
		return vars;
	}

	/**
	 * validateLocalExecution: local account, virtual signers checking
	 * 
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	protected IProcedureResponse validateLocalExecution(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Inicia validacion local");
		


		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse responseSigners = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_QUERY_SIGNER);
		IProcedureRequest request = initProcedureRequest(originalRequest);	
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		ServerResponse responseServer =(ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
	
		
			
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800048");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_validacion");

		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, originalRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, originalRequest.readValueParam("@s_ssn"));
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
		
		switch (t_trn) {
		case 1800009:
			if("S".equals(originalRequest.readValueParam("@i_is_expense_account"))){
				request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "N");
			} else {
				request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "S");
			}
			request.addInputParam("@i_option", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_option"));
			request.addInputParam("@i_detail", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_detail"));
			//same accounts
			
			break;
		case 1800011:
		case 1800012:
			request.addInputParam("@i_tercero", ICTSTypes.SYBVARCHAR, "S");
			request.addInputParam("@i_option", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_option"));
			request.addInputParam("@i_detail", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_detail"));
			if (!Utils.isNull(originalRequest.readValueParam("@i_valida_des")))
				request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_valida_des"));
			else
				request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "S");

			break;
		case 1870001:
			request.addInputParam("@i_option", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_option"));
			request.addInputParam("@i_detail", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_detail"));
			break;
		case 1870013:
			request.addInputParam("@i_tercero", ICTSTypes.SYBVARCHAR, "S");
			request.addInputParam("@i_option", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_option"));
			request.addInputParam("@i_detail", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_detail"));
			request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "N");
		case 1800015:
			Utils.copyParam("@i_nombre_benef", originalRequest, request);
			Utils.copyParam("@i_doc_benef", originalRequest, request);
			Utils.copyParam("@i_telefono_benef", originalRequest, request);
			request.addInputParam("@i_ruta_trans", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_ruta_transito"));
			request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "N");
			request.addInputParam("@i_tercero", ICTSTypes.SYBVARCHAR, "N");
			break;
		default:
			request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "N");
			request.addInputParam("@i_tercero", ICTSTypes.SYBVARCHAR, "N");
			break;
		}
		request.addInputParam("@i_proceso", ICTSTypes.SYBVARCHAR, responseServer!=null &&responseServer.getOnLine() ? "N" : "O" );
		
		if (!getFromReentryExcecution(aBagSPJavaOrchestration))
			request.addInputParam("@i_genera_clave", ICTSTypes.SYBVARCHAR, "S");

		Utils.copyParam("@i_doble_autorizacion", originalRequest, request);
		Utils.copyParam("@i_login", originalRequest, request);
		Utils.copyParam("@i_cta", originalRequest, request);
		Utils.copyParam("@i_prod", originalRequest, request);
		Utils.copyParam("@i_mon", originalRequest, request);

		// Datos de cuenta destino
		if (!Utils.isNull(originalRequest.readValueParam("@i_cta_des"))) {

			Utils.copyParam("@i_cta_des", originalRequest, request);
			Utils.copyParam("@i_prod_des", originalRequest, request);
			Utils.copyParam("@i_mon_des", originalRequest, request);
		}

		String servicio = originalRequest.readValueFieldInHeader("@s_servicio");
		if ("6".equals(servicio) || "7".equals(servicio)) {
			request.addInputParam("@i_prod", ICTSTypes.SYBINT2, "0");
		}
		if (!Utils.isNull(responseSigners.readParam("@o_condiciones_firmantes"))) {
			request.addInputParam("@i_cond_firmas", responseSigners.readParam("@o_condiciones_firmantes").getDataType(), responseSigners.readValueParam("@o_condiciones_firmantes"));
		}
		if (!Utils.isNull(originalRequest.readParam("@i_val"))) {
			request.addInputParam("@i_val", originalRequest.readParam("@i_val").getDataType(), originalRequest.readValueParam("@i_val"));
		}
		if (!Utils.isNull(originalRequest.readParam("@i_concepto"))) {
			request.addInputParam("@i_concepto", originalRequest.readParam("@i_concepto").getDataType(), originalRequest.readValueParam("@i_concepto"));
		}
		request.addInputParam("@i_valida_limites", ICTSTypes.SYBCHAR, "S");
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
		
		if(!serverResponse.getOnLine()) {
			
			request.addInputParam("@i_linea", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_saldo", ICTSTypes.SQLVARCHAR, "S");
		}
		
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: Transaccion "+String.valueOf(t_trn)+" monto::::  "  );
		
		
		request.addOutputParam("@o_comision", ICTSTypes.SYBMONEY, "0");
		
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, request: " + request.getProcedureRequestAsString());

		// Ejecuta validacion
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: " + pResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finaliza validacion local");

		return pResponse;
	}

	/**
	 * Contains primary steps of transaction execution.
	 * 
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando mÃ©todo executeStepsTransactionsBase: " + anOriginalRequest);

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get("coreServiceMonetaryTransaction");
		AccountingParameterRequest requestAccountingParameters = new AccountingParameterRequest();
		AccountingParameterResponse responseAccountingParameters = null;

		StringBuilder messageErrorTransfer = new StringBuilder();
		messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

		IProcedureResponse responseTransfer = null;
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		
		if(anOriginalRequest!=null && anOriginalRequest.readValueFieldInHeader("comision")!=null) {
			if (logger.isInfoEnabled())
				logger.logInfo("Llegada de comisiom ---> " + anOriginalRequest.readValueFieldInHeader("comision"));
		}
		
		//Valida el fuera de línea
		
		if (logger.isInfoEnabled())
			logger.logInfo("Llama a la funcion validateBvTransaction");
		
		String responseSupportOffline = validateBvTransaction(aBagSPJavaOrchestration); 
		
		if (logger.isInfoEnabled())
			logger.logInfo("responseSupportOffline ---> " + responseSupportOffline);
		
		if(responseSupportOffline == null || responseSupportOffline == "") {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea"));
			return Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea");
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

		// Si producto y transaccion estan autorizados
		if (validatePreConditions(aBagSPJavaOrchestration)) {

			// Valida firmas fisicas solo en lÃ­nea
			IProcedureResponse responseSigner = new ProcedureResponseAS();
			if (responseServer.getOnLine()) {
				responseSigner = AccountCoreSignersValidation.validateCoreSigners(getCoreService(), aBagSPJavaOrchestration);
				if (Utils.flowError("querySigners", responseSigner)) {
					aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseSigner);
					return responseSigner;
				}
			} else {
				responseSigner.setReturnCode(0);
			}
			aBagSPJavaOrchestration.put(RESPONSE_QUERY_SIGNER, responseSigner);

			// Validaciones locales
			IProcedureResponse responseLocalValidation = validateLocalExecution(aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_LOCAL_VALIDATION, responseLocalValidation);
			if (Utils.flowError("validateLocal", responseLocalValidation)) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalValidation);
				return responseLocalValidation;
			}

			if ("S".equals(responseLocalValidation.readValueParam("@o_autorizacion"))) {
				if (logger.isInfoEnabled())
					logger.logInfo("Fin del flujo. Requiere autorizacion");
				IProcedureResponse responseTransaction = new ProcedureResponseAS();
				responseTransaction.setReturnCode(0);
				responseTransaction.addParam("@o_referencia", ICTSTypes.SQLINT4, 5, responseLocalValidation.readValueParam("@o_referencia"));
				responseTransaction.addParam("@o_retorno", ICTSTypes.SQLINT4, 5, responseLocalValidation.readValueParam("@o_retorno"));
				responseTransaction.addParam("@o_condicion", ICTSTypes.SQLINT4, 2, responseLocalValidation.readValueParam("@o_condicion"));
				responseTransaction.addParam("@o_autorizacion", ICTSTypes.SQLCHAR, 1, responseLocalValidation.readValueParam("@o_autorizacion"));
				responseTransaction.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 3, responseLocalValidation.readValueParam("@o_ssn_branch"));
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransaction);

				return responseTransaction;
			}

			/*
			 * Obtiene Transacciones y Causa del movimiento a aplicar en el local
			 */
			// if (responseServer.getOnLine()) {
			requestAccountingParameters.setOriginalRequest(anOriginalRequest);
			requestAccountingParameters.setTransaction(Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))));

			responseAccountingParameters = coreServiceMonetaryTransaction.getAccountingParameter(requestAccountingParameters);

			if (logger.isInfoEnabled())
				logger.logInfo("RESPONSE ACCOUNTING PARAMETERS -->" + responseAccountingParameters.getAccountingParameters().toString());

			aBagSPJavaOrchestration.put(ACCOUNTING_PARAMETER, responseAccountingParameters);
			if (!responseAccountingParameters.getSuccess()) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseAccountingParameters);
				return Utils.returnException(responseAccountingParameters.getReturnCode(), new StringBuilder(messageErrorTransfer).append(responseAccountingParameters.getMessage()).toString());
			}
			// }

			// Ejecuta transaccion core
			responseTransfer = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError("executeTransaction", responseTransfer)) {
				if (responseServer.getOnLine()) {
					if (!getFromReentryExcecution(aBagSPJavaOrchestration)) {
						if (logger.isInfoEnabled())
							logger.logInfo(CLASS_NAME + "::Fin prematuro del flujo. No se ejecuto la transferencia.");
						return responseTransfer;
					}
				} else {
					if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
						if (logger.isInfoEnabled())
							logger.logInfo(CLASS_NAME + "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
						return responseTransfer;
					}
				}
			}
            
			// Actualizacion local
			IProcedureResponse responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
			if (Utils.flowError("updateLocalTransfer", responseLocalExecution)) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
				return responseLocalExecution;
			}

			// Envia notificacion
			IProcedureResponse responseNotification = sendNotification(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError("sendNotification", responseNotification)) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseNotification);
				return responseNotification;
			}

		}

		if (logger.isInfoEnabled())
			logger.logInfo(new StringBuilder(CLASS_NAME).append("Respuesta mÃ©todo executeStepsTransactionsBase: " + aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION)).toString());

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	/**
	 * Validates preconditions for transaction execution.
	 * 
	 * @param aBagSPJavaOrchestration
	 * @return boolean
	 */
	private boolean validatePreConditions(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		int product = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));
		boolean trnValidation = evaluateTrn(anOriginalRequest, 1800011, 1800012, 1800015, 1800016, 1800008, 1800009, 1875050, 1870013, 1870001);
		boolean nProd = (3 == product) || (4 == product);

		if (!nProd || !trnValidation) {
			String msg = "Las condiciones no se cumplen para ejecutar el flujo. Producto: " + product + " trn: " + anOriginalRequest.readValueParam("@t_trn");
			logger.logError(msg);
			throw new IllegalArgumentException(msg);
		}
		return true;
	}

	/**
	 * Validates if trn is authorized .
	 * 
	 * @param originalRequest
	 * @param trns
	 * @return
	 */
	private boolean evaluateTrn(IProcedureRequest originalRequest, int... trns) {
		int t_trn = Integer.parseInt(originalRequest.readValueParam("@t_trn"));
		if (logger.isDebugEnabled())
			logger.logDebug("request trn: " + t_trn);

		for (int trn : trns) {
			if (logger.isDebugEnabled())
				logger.logDebug("verificando el trn: " + trn);
			if (trn == t_trn)
				return true;
		}
		return false;
	}

	/**
	 * Local updates: saves monetary trn, saves log, syncs local balances
	 * 
	 * @param originalRequest
	 * @param bag
	 * @return
	 */
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		if (logger.isDebugEnabled())
			logger.logDebug("Ejecutando metodo updateLocalExecution: " + anOriginalRequest.toString());

		IProcedureRequest request = initProcedureRequest(anOriginalRequest);

		ServerResponse responseServer = (ServerResponse) bag.get(RESPONSE_SERVER);
		IProcedureResponse responseBalance = (IProcedureResponse) bag.get(RESPONSE_BALANCE);
		IProcedureResponse responseLocalValidation = (IProcedureResponse) bag.get(RESPONSE_LOCAL_VALIDATION);
		IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
		request.setSpName("cob_bvirtual..sp_bv_transaccion");

		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
		request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
		request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));

		request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
		
		if (anOriginalRequest.readValueParam("@i_latitud") != null) {
			request.addInputParam("@i_latitud", ICTSTypes.SQLFLT8i, anOriginalRequest.readValueParam("@i_latitud"));
		}
		
		if (anOriginalRequest.readValueParam("@i_longitud") != null) {
			request.addInputParam("@i_longitud", ICTSTypes.SQLFLT8i, anOriginalRequest.readValueParam("@i_longitud"));
		}

		if (anOriginalRequest.readValueParam("@i_reference_number") != null)
			request.addInputParam("@i_reference_number", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_reference_number"));

		// Datos de cuenta origen
		Utils.copyParam("@i_cta", anOriginalRequest, request);
		Utils.copyParam("@i_prod", anOriginalRequest, request);
		Utils.copyParam("@i_mon", anOriginalRequest, request);
		
		// Datos de cuenta destino
		Integer t_trn = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta_des")) && !Utils.isNull(anOriginalRequest.readValueParam("@i_prod_des"))
				&& !Utils.isNull(anOriginalRequest.readValueParam("@i_mon_des"))) {

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "t_trn a evaluar: " + t_trn);
			
			try { 
				
				
			
			
			switch (t_trn) {
			case 1800012:
				if (!Utils.isNull(anOriginalRequest.readValueParam("@i_nom_cliente_benef"))) {
					request.addInputParam("@i_nombre_benef", ICTSTypes.SYBVARCHAR, anOriginalRequest.readValueParam("@i_nom_beneficiary"));//pa_beneficiario
					request.addInputParam("@i_nombre_cr", ICTSTypes.SYBVARCHAR, anOriginalRequest.readValueParam("@i_nom_cliente_benef"));//pa_nombre_cr
				}
				break;
			case 1800015:
			case 1800016:
			case 1870001:
			case 1870013:
				request.addInputParam("@i_sinc_cta_des", ICTSTypes.SQLVARCHAR, "N");
				if(bag!=null && bag.containsKey("@i_banco_dest"))
				request.addInputParam("@i_banco_dest", ICTSTypes.SQLVARCHAR, bag.get("@i_banco_dest").toString());
				if(bag!=null && bag.containsKey("@i_clave_rastreo"))
				request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
				request.addInputParam("@i_bandera_spei", ICTSTypes.SQLVARCHAR, "S");
				request.addInputParam("@i_proceso_origen", ICTSTypes.SQLINT1, "1");
				request.addInputParam("@i_mensaje_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_mensaje_acc")!=null? bag.get("@i_mensaje_acc").toString():"");
				request.addInputParam("@i_id_spei_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_id_spei_acc")!=null?bag.get("@i_id_spei_acc").toString():"");
				request.addInputParam("@i_codigo_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_codigo_acc").toString());
				request.addInputParam("@i_transaccion_spei", ICTSTypes.SQLINT4, bag.get("@i_transaccion_spei")!=null?bag.get("@i_transaccion_spei").toString():"");
				request.addInputParam("@i_spei_request", ICTSTypes.SQLVARCHAR, (bag.get("@o_spei_request")!=null)?bag.get("@o_spei_request").toString():"");
				request.addInputParam("@i_spei_response", ICTSTypes.SQLVARCHAR, bag.get("@o_spei_response")!=null?bag.get("@o_spei_response").toString():""  );
				request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, bag.get("@i_ssn_branch").toString());
				if (!Utils.isNull(anOriginalRequest.readValueParam("@i_nombre_benef"))) {
					request.addInputParam("@i_nombre_benef", ICTSTypes.SYBVARCHAR, anOriginalRequest.readValueParam("@i_nombre_benef"));//pa_beneficiario
					request.addInputParam("@i_nombre_cr", ICTSTypes.SYBVARCHAR, anOriginalRequest.readValueParam("@i_nombre_benef"));//pa_nombre_cr
				}
				break;
			default:
				request.addInputParam("@i_sinc_cta_des", ICTSTypes.SQLVARCHAR, "S");
				break;
			}
			
			}catch(Exception xe) {
				
				xe.printStackTrace();
			}

			Utils.copyParam("@i_cta_des", anOriginalRequest, request);
			Utils.copyParam("@i_prod_des", anOriginalRequest, request);
			Utils.copyParam("@i_mon_des", anOriginalRequest, request);
		} else {
			request.addInputParam("@i_sinc_cta_des", ICTSTypes.SQLVARCHAR, "N");
		}

		switch (t_trn) {
		case 1800009:
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "F");
			break;
		case 1800012:
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "T");
			break;
		case 1800015:
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "H");
			Utils.copyParam("@i_ip_proveedor", anOriginalRequest, request);
			request.addInputParam("@i_nom_banco_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_banco_origen"));
			request.addInputParam("@i_nom_banco_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_banco_destino"));
			break;
		case 18862:
		case 1800016:
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "I");
			break;
		default:
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "F");
			break;
		}

		if (anOriginalRequest.readValueParam("@i_recurr_id") != null) {
			Utils.copyParam("@i_recurr_id", anOriginalRequest, request);
		}

		Utils.copyParam("@i_val", anOriginalRequest, request);
		Utils.copyParam("@i_concepto", anOriginalRequest, request);

		// Envio secuencial para bv_log generado en sp_bv_validacion
		if (!getFromReentryExcecution(bag)) {
			if (!(Utils.isNullOrEmpty(responseLocalValidation.readValueParam("@o_clave_bv"))))
				if (Integer.parseInt(responseLocalValidation.readValueParam("@o_clave_bv")) > 0) {
					request.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "N");
					request.addInputParam("@i_clave_bv", ICTSTypes.SQLINT4, responseLocalValidation.readValueParam("@o_clave_bv"));
				}
		} else {
			if (anOriginalRequest.readValueParam("@i_autorizada_rty") != null) {
				if (anOriginalRequest.readValueParam("@i_autorizada_rty").equals("S"))
					request.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "S");
			} else {
				request.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "N");
				request.addInputParam("@i_clave_bv", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_clave_bv"));
			}
		}

		if (logger.isInfoEnabled())
			logger.logInfo("DATA------>>" + responseServer.getOnLine());

		// OrÃ­gen y Destino de fondos
		if (anOriginalRequest.readValueParam("@i_origen_fondos") != null)
			request.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_origen_fondos"));

		if (anOriginalRequest.readValueParam("@i_dest_fondos") != null)
			request.addInputParam("@i_destino_fondos", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_dest_fondos"));

		if (responseServer.getOnLine()) {
			if (responseTransaction != null && responseTransaction.readValueParam("@o_cotizacion") != null)
				request.addInputParam("@i_tasa_cambio", ICTSTypes.SQLDECIMAL, responseTransaction.readValueParam("@o_cotizacion"));

			if (responseTransaction != null && responseTransaction.readValueParam("@o_val_convert") != null)
				request.addInputParam("@i_val", ICTSTypes.SQLMONEY, responseTransaction.readValueParam("@o_val_convert"));

			if (logger.isInfoEnabled()) {
				logger.logInfo(" @o_cotizacion " + responseTransaction.readValueParam("@o_cotizacion"));
				logger.logInfo(" @o_val_convert " + responseTransaction.readValueParam("@o_val_convert"));
			}
		} else
			request.addInputParam("@i_tasa_cambio", ICTSTypes.SQLDECIMAL, "0"); // NO es multimoneda

		addParametersRequestUpdateLocal(request, anOriginalRequest);

		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		else
			request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));

		// Enviar clave de reentry si fue ejecucion en fuera de lÃ­nea
		if (!responseServer.getOnLine()) {
			if (!Utils.isNull(request.readValueParam("@o_clave")))
				Utils.addInputParam(request, "@i_clave_rty", request.readParam("@o_clave").getDataType(), request.readValueParam("@o_clave"));
			else {
				if (logger.isInfoEnabled())
					logger.logInfo("ParÃ¡metro @o_clave no encontrado");
			}
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo("TransacciÃ³n ejecutando en linea: " + responseServer.getOnLine());
			logger.logInfo("Respuesta del core al ejecutar transferencia: " + responseTransaction != null ? responseTransaction.toString() : "ERROR ejecucion en el core es NULL");
		}

		// obtener returnCode de ejecucion de Core, si es fuera de linea el error es 40004
		if (responseTransaction != null && responseTransaction.getReturnCode() != 0) { // error en ejec. core
			Utils.addInputParam(request, "@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(responseTransaction.getReturnCode())));
			if (responseTransaction.getMessageListSize() > 0)
				Utils.addInputParam(request, "@s_msg", ICTSTypes.SQLVARCHAR, (responseTransaction.getMessage(1).getMessageText()));
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

		// copia variables r_ como parametros de entrada para sincronizar saldos
		if (!Utils.isNull(responseBalance)) {
			if (responseBalance.getResultSetListSize() > 0) {

				IResultSetHeaderColumn[] columns = responseBalance.getResultSet(responseBalance.getResultSetListSize()).getMetaData().getColumnsMetaDataAsArray();
				IResultSetRow[] rows = responseBalance.getResultSet(responseBalance.getResultSetListSize()).getData().getRowsAsArray();
				IResultSetRowColumnData[] cols = rows[0].getColumnsAsArray();

				int i = 0;
				for (IResultSetHeaderColumn iResultSetHeaderColumn : columns) {
					if (!iResultSetHeaderColumn.getName().equals(""))
						if (cols[i].getValue() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("PARAMETROS AGREGADOS :" + iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_") + " VALOR: " + cols[i].getValue());
							Utils.addInputParam(request, iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_"), iResultSetHeaderColumn.getType(), cols[i].getValue());
						}
					i++;
				}

			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: " + request.getProcedureRequestAsString());
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Update local");
		}
		return pResponse;
	}

	protected Boolean getFromReentryExcecution(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		return ("Y".equals(request.readValueFieldInHeader(REENTRY_EXE)));
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
