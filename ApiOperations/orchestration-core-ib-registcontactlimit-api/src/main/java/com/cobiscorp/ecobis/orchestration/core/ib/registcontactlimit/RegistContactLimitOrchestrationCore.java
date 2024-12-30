/**
 *
 */
package com.cobiscorp.ecobis.orchestration.core.ib.registcontactlimit;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
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
import com.cobiscorp.ecobis.admintoken.dto.DataTokenRequest;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenResponse;
import com.cobiscorp.ecobis.admintoken.interfaces.IAdminTokenUser;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * @author 
 * 	- Cesar Hidalgo
 * 	- Marcela Ochoa
 * @since Oct 22, 2024
 * @version 1.0.0
 */
@Component(name = "RegistContacLimitOrchestrationCore", immediate = false)
@Service(value = {ICISSPBaseOrchestration.class, IOrchestrator.class})
@Properties(value = {
		@Property(name = "service.description", value = "RegistContacLimitOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "RegistContacLimitOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_orq_regist_contac_limit")})
public class RegistContactLimitOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase

	private ILogger logger = (ILogger) this.getLogger();
	private java.util.Properties properties;
	private static final String className = "RegistContacLimitOrchestrationCore";
	private static final String FALSE = "false";  

	@Reference(bind = "setTokenService", unbind = "unsetTokenService", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private IAdminTokenUser tokenService;

	public void setTokenService(IAdminTokenUser tokenService) {
		this.tokenService = tokenService;
	}

	public void unsetTokenService(IAdminTokenUser tokenService) {
		this.tokenService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader configurationReader) {

		logger.logInfo(className + " loadConfiguration INI RegistContacLimitOrchestrationCore");

		properties = configurationReader.getProperties("//property");

		logger.logInfo(className + "CONFIG RegistContacLimit" + properties.toString());

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		final String METHOD_NAME = "[executeJavaOrchestration RegistContacLimitOrchestrationCore2]";
		logger.logDebug(className + " " + METHOD_NAME + " [INI]");
		long t1 = System.currentTimeMillis();

		IProcedureResponse procedureResponse = new ProcedureResponseAS();

		// Guarda el request original recibido en la orquestacion
		aBagSPJavaOrchestration.put("originalProcedureRequest", anOriginalRequest);

		String operation = anOriginalRequest.readValueParam("@i_operation");
		aBagSPJavaOrchestration.put("operation", operation);

		//GetOneContactLimits Flag (One Contact)
		if(operation.equals("C")){
			procedureResponse = callGetOneContactLimits(anOriginalRequest, aBagSPJavaOrchestration);
			if(!procedureResponse.getResultSetRowColumnData(1, 1, 1).getValue().equals("true")){
				return processResponseError(procedureResponse);	
			}
			procedureResponse = processTransforResponseGetOneContactLimits(procedureResponse, aBagSPJavaOrchestration);
		}

		//Save/Regist ContactLimit Flag
		else if(operation.equals("S")){

			// Valida limites del cliente, antes de guardar limites de un contacto

			// NOTA: Se hará la validación desde Front

			//callCustomerLimits(anOriginalRequest, aBagSPJavaOrchestration);
			/*if ("true".equals(aBagSPJavaOrchestration.get("successGetCustomerLimits"))) {
				obtainLimitsValidations(anOriginalRequest, aBagSPJavaOrchestration);

				if ((Boolean) aBagSPJavaOrchestration.get("isUsrTxnLimitExceeded")) {
					IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN SUPERA LIMITE TRANSACCIONAL MAXIMO POR CONTACTO CONFIGURADO");
					logger.logDebug("Respose Exeption1: " + resp.toString());
					return resp;
				}

				if ((Boolean) aBagSPJavaOrchestration.get("isMaxTxnLimitExceeded")) {
					IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN SUPERA LIMITE TRANSACCIONAL MAXIMO REGULATORIO CONFIGURADO");
					logger.logDebug("Respose Exeption2: " + resp.toString());
					return resp;
				}

				if ((Boolean) aBagSPJavaOrchestration.get("isUsrDailyLimitExceeded")) {
					IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN SUPERA LIMITE DIARIO MAXIMO POR CONTACTO CONFIGURADO");
					logger.logDebug("Respose Exeption3: " + resp.toString());
					return resp;
				}

				if ((Boolean) aBagSPJavaOrchestration.get("isMaxDailyLimitExceeded")) {
					IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN SUPERA LIMITE DIARIO MAXIMO REGULATORIO CONFIGURADO");
					logger.logDebug("Respose Exeption4: " + resp.toString());
					return resp;
				}
			}
			else if ("false".equals(aBagSPJavaOrchestration.get("successGetCustomerLimits")))
			{
				IProcedureResponse resp = Utils.returnException(18055, "ERROR AL TRATAR DE CONSULTAR LOS LIMITES GENERALES DEL CLIENTE");
				logger.logDebug("Respose Exeption5: " + resp.toString());
				return resp;
			}*/ 

			// Guardar limites del contacto
			procedureResponse = callSaveContactLimit(aBagSPJavaOrchestration);

			if (FALSE.equals(aBagSPJavaOrchestration.get("successSaveContactLimit"))) {
				return processResponseError(procedureResponse);
			}
			procedureResponse = processTransfoResponseSaveContactLimit(procedureResponse, aBagSPJavaOrchestration);
		}

		//QueryContactsForCustomer  Flag
		else if (operation.equals("Q")){
			procedureResponse = callQueryContacts(anOriginalRequest, aBagSPJavaOrchestration);
			if(!procedureResponse.getResultSetRowColumnData(1, 1, 1).getValue().equals("true")){
				return processResponseError(procedureResponse);
			}
			procedureResponse = processTransforResponseQueryConctacts(procedureResponse, aBagSPJavaOrchestration);
		}


		long t2 = System.currentTimeMillis();
		logger.logDebug(" End Service Execution Proveedor: " + " Time: (" + (t2 - t1) + " ms.)");
		logger.logDebug("response RegistContacLimitOrchestrationCore: " + procedureResponse.toString());
		logger.logDebug(METHOD_NAME + " [FIN]");
		return procedureResponse; 
	}

	/**
	 * Llama conector =>  para Guardar un contacto junto con los limites
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse del connector llamado
	 */
	private IProcedureResponse callSaveContactLimit(Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug(className + " callSaveContactLimit [INI]");

		IProcedureRequest originalProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration
				.get("originalProcedureRequest");

		//1. Validar Token OTP
		String otpCode = originalProcedureRequest.readValueParam("@i_otp_code");
		logger.logDebug("otpCodeReceived: "+otpCode);
		String otpReturnCode = null;
		String otpReturnCodeNew = null;
		String login = null;		

		if (otpCode!=null && !otpCode.equals("null") && !otpCode.trim().isEmpty()) {

			getLoginById(originalProcedureRequest, aBagSPJavaOrchestration);

			login = aBagSPJavaOrchestration.get("o_login").toString();

			logger.logDebug("User login: "+login);

			if (!login.equals("X")) {

				DataTokenResponse  wResponseOtp = validateOTPCode(originalProcedureRequest, aBagSPJavaOrchestration);

				logger.logDebug("ValidateOTP response: "+wResponseOtp.getSuccess());

				if(!wResponseOtp.getSuccess()) {

					otpReturnCode = wResponseOtp.getMessage().getCode();
					aBagSPJavaOrchestration.put("o_codErrorOTP", otpReturnCode);

					if (logger.isDebugEnabled()) {
						logger.logDebug("ValidateOTP return code: "+otpReturnCode);}

				} else {					
					otpReturnCode = "0";

					if (logger.isDebugEnabled()) {
						logger.logDebug("ValidateOTP successful code: "+otpReturnCode);}
				}
			}else {
				logger.logDebug("No consulto el login");
			}
		}

		// Validamos si el error fue de otp invalido
		if (!otpReturnCode.equals("0")) {
			if ( otpReturnCode.equals("1890000") ) {
				try {
					// Ejecutamos el servicio de generación de token
					DataTokenResponse  wResponseGOtp = generareOTPCode(originalProcedureRequest, aBagSPJavaOrchestration);
					if (logger.isDebugEnabled()) {	
						logger.logDebug("GeneracionOTP dinámica response: "+wResponseGOtp.getSuccess());
					}

					if(!wResponseGOtp.getSuccess()) {
						otpReturnCodeNew = wResponseGOtp.getMessage().getCode();
						if (logger.isDebugEnabled()) {
							logger.logDebug("GeneracionOTP dinámica no exitosa: "+ otpReturnCodeNew);}				
					} else {
						registerRequestType(login);
						if (logger.isDebugEnabled()) {
							logger.logDebug("GeneracionOTP dinámica exitosa: "+otpReturnCodeNew);}
					}					
				}catch(Exception ex) {
					aBagSPJavaOrchestration.replace("o_codErrorOTP", "1890010");
					logger.logError(ex.toString());
				}		
				//Ingresamos el log de OTP ingresadas fallidas por sistema
				registrosFallidos(aBagSPJavaOrchestration);		
			}else {
				if ( otpReturnCode.equals("1890004") || otpReturnCode.equals("1890005")) {
					//Ingresamos el log de OTP ingresadas fallidas por el usuario en bloqueo y asistencia requerida
					registrosFallidos(aBagSPJavaOrchestration);
				}
			}

			//Validacion para llamar al conector blockOperation
			if(otpReturnCode.equals("1890005")){
				IProcedureResponse wConectorBlockOperationResponseConn = executeBlockOperationConnector(originalProcedureRequest, aBagSPJavaOrchestration);
				logger.logDebug(className + " OTPReturnCode 1890005 [INI]");
			}

			otpReturnCode = processOtpReturnCode(otpReturnCode);
		}
		
		if (otpReturnCode != null && !otpReturnCode.isEmpty() && !"0".equals(otpReturnCode)) {
			IProcedureResponse resp = Utils.returnException(Integer.parseInt(otpReturnCode), "Validacion de token falló con codigo de exception/error " + otpReturnCode);
			logger.logDebug("Response Exeption2: " + resp.toString());
			aBagSPJavaOrchestration.put("successSaveContactLimit", FALSE);
			return resp;
		}

		// 3. Guardar Contacto
		IProcedureResponse connectorSaveContacLimitResponse = new ProcedureResponseAS();
		if (originalProcedureRequest != null) {

			// Create JSON body
			JsonObject requestBody = createRequestBody(originalProcedureRequest);
			aBagSPJavaOrchestration.put("requestBodySaveContactLimit", requestBody.toString());

			logger.logDebug("requestBody SaveContactLimit: " + requestBody.toString());

			IProcedureRequest anOriginalRequestRegistContacLimit = new ProcedureRequestAS();
			try {

				anOriginalRequestRegistContacLimit.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("operation").toString());
				anOriginalRequestRegistContacLimit.addInputParam("@i_request_json", ICTSTypes.SQLVARCHAR, requestBody.toString());

				anOriginalRequestRegistContacLimit.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
				anOriginalRequestRegistContacLimit.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
				anOriginalRequestRegistContacLimit.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");
				anOriginalRequestRegistContacLimit.addOutputParam("@o_responseBody", ICTSTypes.SQLVARCHAR, "X");

				anOriginalRequestRegistContacLimit.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorRegistContacLimit)");
				anOriginalRequestRegistContacLimit.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
				anOriginalRequestRegistContacLimit.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

				anOriginalRequestRegistContacLimit.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE,"transformAndSend");
				anOriginalRequestRegistContacLimit.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
				anOriginalRequestRegistContacLimit.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
				anOriginalRequestRegistContacLimit.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
				anOriginalRequestRegistContacLimit.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");

				// SE HACE LA LLAMADA AL CONECTOR
				aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorRegistContacLimit)");
				anOriginalRequestRegistContacLimit.setSpName("cob_procesador..sp_con_regist_contac_limit");

				anOriginalRequestRegistContacLimit.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700131");
				anOriginalRequestRegistContacLimit.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700131");
				anOriginalRequestRegistContacLimit.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700131");

				if (logger.isDebugEnabled()) {
					logger.logDebug("connectorSaveContacLimit --> request: " + anOriginalRequestRegistContacLimit.toString());
				}
				// SE EJECUTA CONECTOR
				connectorSaveContacLimitResponse = executeProvider(anOriginalRequestRegistContacLimit, aBagSPJavaOrchestration);

				if (logger.isDebugEnabled()) {
					logger.logDebug( "connectorSaveContacLimit --> response: " + connectorSaveContacLimitResponse.toString());
				}

				String responseCode = connectorSaveContacLimitResponse.readValueParam("@o_responseCode") == null ? "0" : connectorSaveContacLimitResponse.readValueParam("@o_responseCode");
				String message = connectorSaveContacLimitResponse.readValueParam("@o_message") == null ? "Error" : connectorSaveContacLimitResponse.readValueParam("@o_message");
				String success = connectorSaveContacLimitResponse.readValueParam("@o_success") == null ? "false" : connectorSaveContacLimitResponse.readValueParam("@o_success");
				String responseBody = connectorSaveContacLimitResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorSaveContacLimitResponse.readValueParam("@o_responseBody");

				logger.logDebug("responseCodeSaveContactLimit:: " + responseCode);
				logger.logDebug("messageSaveContactLimit:: " + message);
				logger.logDebug("successSaveContactLimit:: " + success);
				logger.logDebug("responseBodySaveContactLimit:: " + responseBody);

				aBagSPJavaOrchestration.put("responseCodeSaveContactLimit", responseCode);
				aBagSPJavaOrchestration.put("messageSaveContactLimit", message);
				aBagSPJavaOrchestration.put("successSaveContactLimit", success);
				aBagSPJavaOrchestration.put("responseBodySaveContactLimit", responseBody);

			} catch (Exception e) {
				// imprimir todo el traceback del error
				e.printStackTrace();
				logger.logError(className + " Error en callSaveContacLimit Connector: " + e.getMessage());
				logger.logInfo(className + " Error Catastrofico de callSaveContacLimit Conneector");

			} finally {
				if (logger.isInfoEnabled()) {
					logger.logInfo(className + "--> Saliendo de callSaveContacLimit bag:: "	+ aBagSPJavaOrchestration.toString());
				}
			}
		}else {
			logger.logError(className + " Error en callSaveContacLimit: originalProcedureRequest es null");
		}

		logRequestAndResponse(originalProcedureRequest, aBagSPJavaOrchestration);
		
		
		return connectorSaveContacLimitResponse;
	}

	/**
	 * Crea el cuerpo/body JSON para una peticion de guardar/registrar un nuevo contacto
	 * @param originalProcedureRequest
	 * @return JsonObject
	 */
	private JsonObject createRequestBody(IProcedureRequest originalProcedureRequest) {
		JsonObject jsonRequest = new JsonObject();

		// Agregar propiedades básicas
		jsonRequest.addProperty("externalCustomerId", originalProcedureRequest.readValueParam("@i_externalCustomerId"));

		String accountNumber = originalProcedureRequest.readValueParam("@i_accountNumber");
		if (accountNumber != null && !accountNumber.isEmpty()) {
			jsonRequest.addProperty("accountNumber", accountNumber);
		}

		// Crear el objeto creditorAccount
		JsonObject creditorAccount = new JsonObject();
		creditorAccount.addProperty("identification", originalProcedureRequest.readValueParam("@i_identification"));
		creditorAccount.addProperty("bankCode", originalProcedureRequest.readValueParam("@i_bankCode"));

		String bankName = originalProcedureRequest.readValueParam("@i_bankName");
		if (bankName != null && !bankName.isEmpty()) {
			creditorAccount.addProperty("bankName", bankName);
		}		

		creditorAccount.addProperty("name", originalProcedureRequest.readValueParam("@i_name"));
		creditorAccount.addProperty("type", originalProcedureRequest.readValueParam("@i_type"));

		String alias = originalProcedureRequest.readValueParam("@i_alias");
		if (alias != null && !alias.isEmpty()) {
			creditorAccount.addProperty("alias", alias);
		}

		creditorAccount.addProperty("saveAccount",Boolean.parseBoolean(originalProcedureRequest.readValueParam("@i_saveAccount")));

		String maskedCardNumber = originalProcedureRequest.readValueParam("@i_maskedCardNumber");
		if (maskedCardNumber != null && !maskedCardNumber.isEmpty()) {
			creditorAccount.addProperty("maskedCardNumber", maskedCardNumber);
		}
		creditorAccount.addProperty("isFavorite", Boolean.parseBoolean(originalProcedureRequest.readValueParam("@i_isFavorite")));

		// Crear el array transactionLimits
		JsonArray transactionLimits = new JsonArray();

		// Leer y verificar valores de limitType, amount, y currency
		String limitType = originalProcedureRequest.readValueParam("@i_limitType");
		String amountStr = originalProcedureRequest.readValueParam("@i_amount");
		String currency = originalProcedureRequest.readValueParam("@i_currency");

		// Solo proceder si al menos uno de los campos tiene un valor válido
		if ((limitType != null && !limitType.isEmpty()) ||
				(amountStr != null && !amountStr.isEmpty()) ||
				(currency != null && !currency.isEmpty())) {

			JsonObject limitObject = new JsonObject();

			if (limitType != null && !limitType.isEmpty()) {
				limitObject.addProperty("limitType", limitType);
			}

			// Solo crear el objeto limit si amount o currency tienen valores
			if ((amountStr != null && !amountStr.isEmpty()) || (currency != null && !currency.isEmpty())) {
				JsonObject limit = new JsonObject();
				if (amountStr != null && !amountStr.isEmpty()) {
					limit.addProperty("amount", new BigDecimal(amountStr));
				}
				if (currency != null && !currency.isEmpty()) {
					limit.addProperty("currency", currency);
				}
				limitObject.add("limit", limit);
			}

			// Añadir limitObject al array si contiene datos válidos
			if (!limitObject.entrySet().isEmpty()) {
				transactionLimits.add(limitObject);
			}
		}

		// Añadir transactionLimits al creditorAccount si no está vacío
		if (transactionLimits.size() > 0) {
			creditorAccount.add("transactionLimits", transactionLimits);
		}

		// Añadir creditorAccount al jsonRequest
		jsonRequest.add("creditorAccount", creditorAccount);
		
		return jsonRequest;
	}


	/**
	 * LLama conector =>  para Obtener los limites de un contacto dado
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse del connector llamado
	 */
	private IProcedureResponse callGetOneContactLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration)
	{
		if(logger.isDebugEnabled())
			logger.logDebug(" callGetOneContactLimits [INI]");

		IProcedureResponse connectorGetOneContactLimitsResponse = new ProcedureResponseAS();
		try {

			IProcedureRequest anOriginalRequestOneContactLimits = new ProcedureRequestAS();

			// Query Params
			anOriginalRequestOneContactLimits.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_operation"));
			anOriginalRequestOneContactLimits.addInputParam("@i_transactionType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transType"));
			anOriginalRequestOneContactLimits.addInputParam("@i_transactionSubType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transSubType"));

			anOriginalRequestOneContactLimits.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_externalCustomerId"));
			anOriginalRequestOneContactLimits.addInputParam("@i_contactId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_contactId"));

			anOriginalRequestOneContactLimits.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestOneContactLimits.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestOneContactLimits.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");

			// Connector Headers y sp name
			anOriginalRequestOneContactLimits.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestOneContactLimits.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, "Y");
			anOriginalRequestOneContactLimits.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

			anOriginalRequestOneContactLimits.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalRequestOneContactLimits.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequestOneContactLimits.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestOneContactLimits.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestOneContactLimits.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");

			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestOneContactLimits.setSpName("cob_procesador..sp_conn_get_limits");
			anOriginalRequestOneContactLimits.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700128");
			anOriginalRequestOneContactLimits.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700128");
			anOriginalRequestOneContactLimits.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700128");

			// SE HACE LA LLAMADA AL CONECTOR
			connectorGetOneContactLimitsResponse = executeProvider(anOriginalRequestOneContactLimits, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()){
				logger.logDebug("connectorGetOneContactLimitsResponse ->" + connectorGetOneContactLimitsResponse.toString());
			}

			String responseCode = connectorGetOneContactLimitsResponse.readValueParam("@o_responseCode") == null ? "0" : connectorGetOneContactLimitsResponse.readValueParam("@o_responseCode");
			String message = connectorGetOneContactLimitsResponse.readValueParam("@o_message") == null ? "Error" : connectorGetOneContactLimitsResponse.readValueParam("@o_message");
			String success = connectorGetOneContactLimitsResponse.readValueParam("@o_success") == null ? "false" : connectorGetOneContactLimitsResponse.readValueParam("@o_success");
			String responseBody = connectorGetOneContactLimitsResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorGetOneContactLimitsResponse.readValueParam("@o_responseBody");
			String queryString = connectorGetOneContactLimitsResponse.readValueParam("@o_queryString") == null ? "" : connectorGetOneContactLimitsResponse.readValueParam("@o_queryString");

			aBagSPJavaOrchestration.put("responseCodeGetOneContactLimits", responseCode);
			aBagSPJavaOrchestration.put("messageGetOneContactLimits", message);
			aBagSPJavaOrchestration.put("successGetOneContactLimits", success);
			aBagSPJavaOrchestration.put("responseBodyGetOneContactLimits", responseBody);
			aBagSPJavaOrchestration.put("queryString", queryString);

			logRequestAndResponse(aRequest, aBagSPJavaOrchestration);
			return connectorGetOneContactLimitsResponse;

		} catch (Exception e) {
			logger.logError(" Error en callGetOneContactLimitsConn: " + e.getMessage());
		}
		return connectorGetOneContactLimitsResponse;
	}

	/**
	 * Obtiene limites generados configurados para el cliente y valida el monto a configurar sea menor a ellos
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 */
	private void obtainLimitsValidations(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		try {
			JsonParser jsonParser = new JsonParser();
			String jsonRequestStringClean = aBagSPJavaOrchestration.get("responseBodyGetCustomerLimits").toString().replace("&quot;", "\"");
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);

			logger.logInfo("jsonObject obtainLimitsValidations:: " + jsonObject);

			JsonArray transactionLimits = jsonObject.getAsJsonArray("transactionLimits");

			double transactionAmount = Double.parseDouble(aRequest.readValueParam("@i_amount"));// Monto de la transacción
			logger.logInfo("transactionAmount:: " + transactionAmount);

			// Inicializar variables para límites - Transaccional
			Double maxTxnLimit = null;
			Double maxUsrTxnLimit = null;

			// Inicializar variables para límites - Daily
			Double maxDailyLimit = null;
			Double maxUsrDailyLimit = null;


			for (JsonElement limitElement : transactionLimits) {
				JsonArray subTypeLimits = limitElement.getAsJsonObject().getAsJsonArray("transactionSubTypeLimits");
				for (JsonElement subTypeElement : subTypeLimits) {
					String limitType = subTypeElement.getAsJsonObject().get("transactionLimitsType").getAsString();

					if ("MAX_TXN_LIMIT".equals(limitType)) {
						maxTxnLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("configuredLimit")
								.get("amount").getAsDouble();
						maxUsrTxnLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
					}

					if ("DAILY".equals(limitType)) {
						maxDailyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("configuredLimit")
								.get("amount").getAsDouble();
						maxUsrDailyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
					}
				}
			}

			// Txn limits validations
			aBagSPJavaOrchestration.put("isMaxTxnLimitExceeded", maxTxnLimit != null && transactionAmount > maxTxnLimit);
			aBagSPJavaOrchestration.put("isUsrTxnLimitExceeded", maxUsrTxnLimit != null && transactionAmount > maxUsrTxnLimit);

			// Daily limits validations
			aBagSPJavaOrchestration.put("isMaxDailyLimitExceeded", maxDailyLimit != null && transactionAmount > maxDailyLimit);
			aBagSPJavaOrchestration.put("isUsrDailyLimitExceeded", maxUsrDailyLimit != null && transactionAmount > maxUsrDailyLimit);


			if (logger.isDebugEnabled()) {
				logger.logDebug("maxTxnLimit:: " + maxTxnLimit);
				logger.logDebug("maxUsrTxnLimit:: " + maxUsrTxnLimit);
				logger.logDebug("maxDailyLimit:: " + maxDailyLimit);
				logger.logDebug("maxUsrDailyLimit:: " + maxUsrDailyLimit);
				logger.logDebug("transactionAmount:: " + transactionAmount);
			}

		} catch (JsonSyntaxException e) {
			logger.logError("Error parsing JSON: Invalid JSON syntax", e);
		} catch (IllegalStateException e) {
			logger.logError("Error parsing JSON: Illegal state", e);
		} catch (Exception e) {
			logger.logError("Unexpected error while parsing JSON", e);
		}
	}

	/**
	 * Llama al conector =>  para Consultar la lista de contactos (paginada) para un cliente/externalCustomer dado
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse del connector llamado
	 */
	private IProcedureResponse callQueryContacts(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if(logger.isDebugEnabled())
			logger.logDebug(" callQueryContacts [INI]");

		IProcedureResponse connectorQueryContactsResponse = new ProcedureResponseAS();
		try {

			IProcedureRequest anOriginalQueryContacts = new ProcedureRequestAS();

			// Add query params
			anOriginalQueryContacts.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_operation"));
			anOriginalQueryContacts.addInputParam("@i_pageSize", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_pageSize"));
			anOriginalQueryContacts.addInputParam("@i_pageNumber", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_pageNumber"));
			anOriginalQueryContacts.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_externalCustomerId"));

			anOriginalQueryContacts.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
			anOriginalQueryContacts.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
			anOriginalQueryContacts.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");

			// Connector headers
			anOriginalQueryContacts.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE,"(service.identifier=CISConnectorRegistContacLimit)");
			anOriginalQueryContacts.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE,	ICOBISTS.YES);
			anOriginalQueryContacts.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

			anOriginalQueryContacts.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalQueryContacts.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalQueryContacts.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalQueryContacts.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalQueryContacts.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");


			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorRegistContacLimit)");
			anOriginalQueryContacts.setSpName("cob_procesador..sp_con_regist_contac_limit");

			anOriginalQueryContacts.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700131");
			anOriginalQueryContacts.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700131");
			anOriginalQueryContacts.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700131");

			connectorQueryContactsResponse = executeProvider(anOriginalQueryContacts, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()){
				logger.logDebug("connectorQueryContactsResponse ->" + connectorQueryContactsResponse.toString());
			}

			String responseCode = connectorQueryContactsResponse.readValueParam("@o_responseCode") == null ? "0" : connectorQueryContactsResponse.readValueParam("@o_responseCode");
			String message = connectorQueryContactsResponse.readValueParam("@o_message") == null ? "Error" : connectorQueryContactsResponse.readValueParam("@o_message");
			String success = connectorQueryContactsResponse.readValueParam("@o_success") == null ? "false" : connectorQueryContactsResponse.readValueParam("@o_success");
			String responseBody = connectorQueryContactsResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorQueryContactsResponse.readValueParam("@o_responseBody");
			String queryString = connectorQueryContactsResponse.readValueParam("@o_queryString") == null ? "" : connectorQueryContactsResponse.readValueParam("@o_queryString");

			logger.logDebug("responseCodeQueryContacts:: " + responseCode);
			logger.logDebug("messageQueryContacts:: " + message);
			logger.logDebug("successQueryContacts:: " + success);
			logger.logDebug("responseBodyQueryContacts:: " + responseBody);
			logger.logDebug("queryStringQueryContacts:: " + responseBody);

			aBagSPJavaOrchestration.put("responseCodeQueryContacts", responseCode);
			aBagSPJavaOrchestration.put("messageQueryContacts", message);
			aBagSPJavaOrchestration.put("successQueryContacts", success);
			aBagSPJavaOrchestration.put("responseBodyQueryContacts", responseBody);
			aBagSPJavaOrchestration.put("queryStringQueryContacts", queryString);

			logRequestAndResponse(aRequest, aBagSPJavaOrchestration);
			return connectorQueryContactsResponse;

		} catch (Exception e) {
			logger.logError(" Error en callQueryContactsConn: " + e.getMessage());
		}
		return connectorQueryContactsResponse;
	}

	/**
	 * Llama al conector =>  para Consultar los limites globales configurados por cliente/customer
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	/**
	 */
	private IProcedureResponse callCustomerLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if(logger.isDebugEnabled())
			logger.logDebug(" callGetOneContactLimits [INI]");

		IProcedureResponse connectorCustomerLimitsResponse = new ProcedureResponseAS();
		try {

			IProcedureRequest anOriginalRequestOneContactLimits = new ProcedureRequestAS();

			// Query Params
			anOriginalRequestOneContactLimits.addInputParam("@i_transactionType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transType"));
			//anOriginalRequestOneContactLimits.addInputParam("@i_transactionType", ICTSTypes.SQLVARCHAR, "DEBIT");

			anOriginalRequestOneContactLimits.addInputParam("@i_transactionSubType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transSubType"));
			//anOriginalRequestOneContactLimits.addInputParam("@i_transactionSubType", ICTSTypes.SQLVARCHAR, "P2P_DEBIT");

			anOriginalRequestOneContactLimits.addInputParam("@i_externalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_externalCustomerId"));
			//anOriginalRequestOneContactLimits.addInputParam("@i_contactId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_contactId"));

			anOriginalRequestOneContactLimits.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestOneContactLimits.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestOneContactLimits.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");

			// Connector Headers y sp name
			anOriginalRequestOneContactLimits.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestOneContactLimits.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, "Y");
			anOriginalRequestOneContactLimits.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

			anOriginalRequestOneContactLimits.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalRequestOneContactLimits.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequestOneContactLimits.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestOneContactLimits.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestOneContactLimits.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");

			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestOneContactLimits.setSpName("cob_procesador..sp_conn_get_limits");
			anOriginalRequestOneContactLimits.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700128");
			anOriginalRequestOneContactLimits.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700128");
			anOriginalRequestOneContactLimits.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700128");

			// SE HACE LA LLAMADA AL CONECTOR
			connectorCustomerLimitsResponse = executeProvider(anOriginalRequestOneContactLimits, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()){
				logger.logDebug("connectorGetOneContactLimitsResponse ->" + connectorCustomerLimitsResponse.toString());
			}

			String responseCode = connectorCustomerLimitsResponse.readValueParam("@o_responseCode") == null ? "0" : connectorCustomerLimitsResponse.readValueParam("@o_responseCode");
			String message = connectorCustomerLimitsResponse.readValueParam("@o_message") == null ? "Error" : connectorCustomerLimitsResponse.readValueParam("@o_message");
			String success = connectorCustomerLimitsResponse.readValueParam("@o_success") == null ? "false" : connectorCustomerLimitsResponse.readValueParam("@o_success");
			String responseBody = connectorCustomerLimitsResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorCustomerLimitsResponse.readValueParam("@o_responseBody");
			String queryString = connectorCustomerLimitsResponse.readValueParam("@o_queryString") == null ? "" : connectorCustomerLimitsResponse.readValueParam("@o_queryString");

			aBagSPJavaOrchestration.put("responseCodeGetCustomerLimits", responseCode);
			aBagSPJavaOrchestration.put("messageGetCustomerLimits", message);
			aBagSPJavaOrchestration.put("successGetCustomerLimits", success);
			aBagSPJavaOrchestration.put("responseBodyGetCustomerLimits", responseBody);
			aBagSPJavaOrchestration.put("queryString", queryString);

			logRequestAndResponse(aRequest, aBagSPJavaOrchestration);
			return connectorCustomerLimitsResponse;

		} catch (Exception e) {
			logger.logError(" Error en callCustomerLimitsConn: " + e.getMessage());
		}
		return connectorCustomerLimitsResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub - No Usado
		return null;
	}

	/**
	 * 
	 * @param anOriginalProcedureRes
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	public IProcedureResponse processTransforResponseGetOneContactLimits(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransforResponse GetOneContactLimits--->");
		}

		//Construye Response del orquestador
		IProcedureResponse finalGetOneContactLimitsResponse = new ProcedureResponseAS();

		// Agregar header/metadata 1
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar header/metadata 2
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar header/metadata 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();

		// Agregar header/metadata 4
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();

		// Resultados tomados del response del connector
		String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();
		String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();

		//data1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);

		//data2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);

		String requestBody = null;
		if(aBagSPJavaOrchestration.get("responseBodyGetOneContactLimits") != null  && !aBagSPJavaOrchestration.get("responseBodyGetOneContactLimits").toString().equals("{}")){
			requestBody = aBagSPJavaOrchestration.get("responseBodyGetOneContactLimits").toString();
		}

		if(requestBody != null){
			JsonParser jsonParser = new JsonParser();

			String jsonRequestStringClean = requestBody.replace("&quot;", "\"");
			logger.logInfo("jsonRequestStringClean:: " + jsonRequestStringClean );
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);
			logger.logInfo("jsonObject:: " + jsonObject );


			//metaData3 = new ResultSetHeader();
			//data3 = new ResultSetData();

			metaData3.addColumnMetaData(new ResultSetHeaderColumn("externalCustomerId", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("accountNumber", ICTSTypes.SQLVARCHAR, 100));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("transactionType", ICTSTypes.SQLVARCHAR, 100));


			String externalCustomerId = jsonObject.get("externalCustomerId").getAsString();
			String accountNumber = jsonObject.get("accountNumber").getAsString();
			String transactionType = jsonObject.get("transactionType").getAsString();
			logger.logInfo("externalCustomerId::: " + externalCustomerId);
			logger.logInfo("accountNumber::: " + accountNumber);
			logger.logInfo("transactionType::: " + transactionType);

			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, externalCustomerId));
			row3.addRowData(2, new ResultSetRowColumnData(false, accountNumber));
			row3.addRowData(3, new ResultSetRowColumnData(false, transactionType));
			data3.addRow(row3);

			//metaData4
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("balanceAmount_amount", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("balanceAmount_currency", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("userConfiguredLimit_amount", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("userConfiguredLimit_currency", ICTSTypes.SQLVARCHAR, 100));

			JsonArray oneContactLimits = jsonObject.getAsJsonArray("contactLimit");
			logger.logInfo("oneContactLimits LENGTH::" + oneContactLimits.size());

			for (JsonElement limitElement : oneContactLimits) {
				IResultSetRow oneContactRow = new ResultSetRow();

				if (limitElement.getAsJsonObject().has("balanceAmount")) {
					oneContactRow.addRowData(1, new ResultSetRowColumnData(false, limitElement.getAsJsonObject().getAsJsonObject("balanceAmount").get("amount").getAsString())); // balance amount
					oneContactRow.addRowData(2, new ResultSetRowColumnData(false, limitElement.getAsJsonObject().getAsJsonObject("balanceAmount").get("currency").getAsString())); // balance currency
				}
				if (limitElement.getAsJsonObject().has("userConfiguredLimit")) {
					oneContactRow.addRowData(3, new ResultSetRowColumnData(false, limitElement.getAsJsonObject().getAsJsonObject("userConfiguredLimit").get("amount").getAsString())); // limit amount
					oneContactRow.addRowData(4, new ResultSetRowColumnData(false, limitElement.getAsJsonObject().getAsJsonObject("userConfiguredLimit").get("currency").getAsString())); // limit currency
				}

				data4.addRow(oneContactRow);
			}

		}	

		// Declara bloques para el response final del orquestador
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock3 = null;
		IResultSetBlock resultsetBlock4 = null;
		if(requestBody != null) {
			resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		}


		// Agrega informacion a bloques del response final del orquestador
		finalGetOneContactLimitsResponse.setReturnCode(200);
		finalGetOneContactLimitsResponse.addResponseBlock(resultsetBlock);
		finalGetOneContactLimitsResponse.addResponseBlock(resultsetBlock2);
		if(requestBody != null) {
			finalGetOneContactLimitsResponse.addResponseBlock(resultsetBlock3);
			finalGetOneContactLimitsResponse.addResponseBlock(resultsetBlock4);
		}

		return finalGetOneContactLimitsResponse;
	}

	/**
	 * 
	 * @param anOriginalProcedureRes
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	public IProcedureResponse processTransfoResponseSaveContactLimit(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransforResponse SaveContactLimit--->");
		}

		//Construye Response del orquestador
		IProcedureResponse finalSaveContactLimitResponse = new ProcedureResponseAS();

		// Agregar header/metadata 1
		IResultSetHeader metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		IResultSetData data = new ResultSetData();

		// Agregar header/metadata 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar header/metadata 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();

		// Agregar header/metadata 4
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();

		// Resultados tomados del response del connector
		String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();
		String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();

		//data1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);

		//data2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);

		String responseBody = null;
		if(aBagSPJavaOrchestration.get("responseBodySaveContactLimit") != null  && !aBagSPJavaOrchestration.get("responseBodySaveContactLimit").toString().equals("{}")){
			responseBody = aBagSPJavaOrchestration.get("responseBodySaveContactLimit").toString();
		}

		if(responseBody != null){
			@SuppressWarnings("deprecation")
			JsonParser jsonParser = new JsonParser();

			String jsonRequestStringClean = responseBody.replace("&quot;", "\"");
			logger.logInfo("jsonRequestStringClean:: " + jsonRequestStringClean );

			@SuppressWarnings("deprecation")
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);
			logger.logInfo("jsonObject:: " + jsonObject );


			//metaData3
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("pageSize", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("pageNumber", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("totalRecords", ICTSTypes.SQLINT4, 8));

			//data3
			String pageSize = jsonObject.get("pageSize").getAsString();
			String pageNumber = jsonObject.get("pageNumber").getAsString();
			String totalRecords = jsonObject.get("totalRecords").getAsString();

			logger.logInfo("pageSize::: " + pageSize);
			logger.logInfo("pageNumber::: " + pageNumber);
			logger.logInfo("totalRecords::: " + totalRecords);

			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, pageSize));
			row3.addRowData(2, new ResultSetRowColumnData(false, pageNumber));
			row3.addRowData(3, new ResultSetRowColumnData(false, totalRecords));
			data3.addRow(row3);

			//metaData4 
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("identification", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("displayIdentification", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("bankCode", ICTSTypes.SQLINT4, 8));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("bankName", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("name", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("type", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("alias", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("maskedCardNumber", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("externalCardId", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("dailyTxnLimitAmount", ICTSTypes.SQLDECIMAL, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("dailyTxnLimitCurrency", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("isFavorite", ICTSTypes.SQLBIT, 5));

			JsonArray savedContactsArray = jsonObject.getAsJsonArray("savedContacts");
			logger.logInfo("savedContacts LENGTH::" + savedContactsArray.size());

			//data4
			for (JsonElement savedContact : savedContactsArray) {

				JsonObject savedContactObject = savedContact.getAsJsonObject();

				IResultSetRow savedContactRow = new ResultSetRow();
				savedContactRow.addRowData(1, new ResultSetRowColumnData(false, savedContactObject.get("id").getAsString()));
				savedContactRow.addRowData(2, new ResultSetRowColumnData(false, savedContactObject.get("identification").getAsString()));
				savedContactRow.addRowData(3, new ResultSetRowColumnData(false, savedContactObject.get("displayIdentification").getAsString()));

				Integer bankCode = savedContactObject.has("bankCode") ? savedContactObject.get("bankCode").getAsInt() : null;
				savedContactRow.addRowData(4, new ResultSetRowColumnData(false, bankCode!=null?bankCode.toString():null ));

				String bankName = savedContactObject.has("bankName") ? savedContactObject.get("bankName").getAsString() : null;
				savedContactRow.addRowData(5, new ResultSetRowColumnData(false, bankName));				

				savedContactRow.addRowData(6, new ResultSetRowColumnData(false, savedContactObject.get("name").getAsString()));
				savedContactRow.addRowData(7, new ResultSetRowColumnData(false, savedContactObject.get("type").getAsString()));

				String alias = savedContactObject.has("alias") ? savedContactObject.get("alias").getAsString() : null;
				savedContactRow.addRowData(8, new ResultSetRowColumnData(false, alias));			

				String maskedCardNumber = savedContactObject.has("maskedCardNumber") ? savedContactObject.get("maskedCardNumber").getAsString() : null;
				savedContactRow.addRowData(9, new ResultSetRowColumnData(false, maskedCardNumber));				    

				String externalCardId = savedContactObject.has("externalCardId") ? savedContactObject.get("externalCardId").getAsString() : null;
				savedContactRow.addRowData(10, new ResultSetRowColumnData(false, externalCardId));				

				BigDecimal dailyTxnLimitAmount = savedContactObject.has("dailyTxnLimitAmount") ? savedContactObject.get("dailyTxnLimitAmount").getAsBigDecimal() : null;
				savedContactRow.addRowData(11, new ResultSetRowColumnData(false, dailyTxnLimitAmount!=null? dailyTxnLimitAmount.toString() : null));

				String dailyTxnLimitCurrency = savedContactObject.has("dailyTxnLimitCurrency") ? savedContactObject.get("dailyTxnLimitCurrency").getAsString() : null;
				savedContactRow.addRowData(12, new ResultSetRowColumnData(false, dailyTxnLimitCurrency));

				// Verifica si el objeto JSON tiene el campo "isFavorite" y si no es null
				boolean isFavorite = savedContactObject.has("isFavorite") && !savedContactObject.get("isFavorite").isJsonNull()
						? savedContactObject.get("isFavorite").getAsBoolean()
								: false; // O cualquier valor predeterminado que desees si es null

				savedContactRow.addRowData(13, new ResultSetRowColumnData(false, String.valueOf(isFavorite)));

				// savedContactRow.addRowData(13, new ResultSetRowColumnData(false, String.valueOf(savedContactObject.get("isFavorite").getAsBoolean())));

				data4.addRow(savedContactRow);
			}

		}

		// Declara bloques para el response final del orquestador
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = null;
		IResultSetBlock resultsetBlock4 = null;
		if(responseBody != null) {
			resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		}

		// Agrega informacion a bloques del response final del orquestador
		finalSaveContactLimitResponse.setReturnCode(200);
		finalSaveContactLimitResponse.addResponseBlock(resultsetBlock);
		finalSaveContactLimitResponse.addResponseBlock(resultsetBlock2);
		if(responseBody != null) {
			finalSaveContactLimitResponse.addResponseBlock(resultsetBlock3);
			finalSaveContactLimitResponse.addResponseBlock(resultsetBlock4);
		}

		
		// Log de request original y response de save contact limit
		IProcedureRequest originalProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration
				.get("originalProcedureRequest");		
				
		JsonObject jsonResponseLog = createJsonResponseLog(code, message, success);
		aBagSPJavaOrchestration.put("jsonResponseLog", jsonResponseLog);
		registerGlobalRequestAndResponse(originalProcedureRequest, aBagSPJavaOrchestration, finalSaveContactLimitResponse);
		
		return finalSaveContactLimitResponse;
	}


	/**
	 * Construye Response del orquestador para la peticion de QueryContacts
	 * @param anOriginalProcedureRes
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	public IProcedureResponse processTransforResponseQueryConctacts(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransforResponse QueryConctacts--->");
		}

		//Construye Response del orquestador
		IProcedureResponse finalQueryContactsReponse = new ProcedureResponseAS();

		// Agregar header/metadata 1
		IResultSetHeader metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		IResultSetData data = new ResultSetData();

		// Agregar header/metadata 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar header/metadata 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();

		// Agregar header/metadata 4
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();

		// Resultados tomados del response del connector
		String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();
		String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();

		//data1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);

		//data2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);

		String responseBody = null;
		if(aBagSPJavaOrchestration.get("responseBodyQueryContacts") != null  && !aBagSPJavaOrchestration.get("responseBodyQueryContacts").toString().equals("{}")){
			responseBody = aBagSPJavaOrchestration.get("responseBodyQueryContacts").toString();
		}

		if(responseBody != null){
			@SuppressWarnings("deprecation")
			JsonParser jsonParser = new JsonParser();

			String jsonRequestStringClean = responseBody.replace("&quot;", "\"");
			logger.logInfo("jsonRequestStringClean:: " + jsonRequestStringClean );

			@SuppressWarnings("deprecation")
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);
			logger.logInfo("jsonObject:: " + jsonObject );


			//metaData3
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("pageSize", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("pageNumber", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("totalRecords", ICTSTypes.SQLINT4, 8));

			//data3
			String pageSize = jsonObject.get("pageSize").getAsString();
			String pageNumber = jsonObject.get("pageNumber").getAsString();
			String totalRecords = jsonObject.get("totalRecords").getAsString();

			logger.logInfo("pageSize::: " + pageSize);
			logger.logInfo("pageNumber::: " + pageNumber);
			logger.logInfo("totalRecords::: " + totalRecords);

			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, pageSize));
			row3.addRowData(2, new ResultSetRowColumnData(false, pageNumber));
			row3.addRowData(3, new ResultSetRowColumnData(false, totalRecords));
			data3.addRow(row3);

			//metaData4 
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("identification", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("displayIdentification", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("bankCode", ICTSTypes.SQLINT4, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("bankName", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("name", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("type", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("alias", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("maskedCardNumber", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("externalCardId", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("dailyTxnLimitAmount", ICTSTypes.SQLDECIMAL, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("dailyTxnLimitCurrency", ICTSTypes.SQLVARCHAR, 100));			
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("isFavorite", ICTSTypes.SQLBIT, 5));

			JsonArray savedContactsArray = jsonObject.getAsJsonArray("savedContacts");
			logger.logInfo("savedContacts LENGTH::" + savedContactsArray.size());

			//data4
			for (JsonElement contactElement : savedContactsArray) {
				JsonObject contactObject = contactElement.getAsJsonObject();

				IResultSetRow contactRow = new ResultSetRow();
				contactRow.addRowData(1, new ResultSetRowColumnData(false, contactObject.get("id").getAsString()));
				contactRow.addRowData(2, new ResultSetRowColumnData(false, contactObject.get("identification").getAsString()));
				contactRow.addRowData(3, new ResultSetRowColumnData(false, contactObject.get("displayIdentification").getAsString()));

				Integer bankCode = contactObject.has("bankCode") ? contactObject.get("bankCode").getAsInt() : null;
				contactRow.addRowData(4, new ResultSetRowColumnData(false, bankCode!=null?bankCode.toString():null ));

				contactRow.addRowData(5, new ResultSetRowColumnData(false, contactObject.get("bankName").getAsString()));
				contactRow.addRowData(6, new ResultSetRowColumnData(false, contactObject.get("name").getAsString()));
				contactRow.addRowData(7, new ResultSetRowColumnData(false, contactObject.get("type").getAsString()));

				String alias = contactObject.has("alias") ? contactObject.get("alias").getAsString() : null;
				contactRow.addRowData(8, new ResultSetRowColumnData(false, alias));

				String maskedCardNumber = contactObject.has("maskedCardNumber") ? contactObject.get("maskedCardNumber").getAsString() : null;
				contactRow.addRowData(9, new ResultSetRowColumnData(false, maskedCardNumber));				

				String externalCardId = contactObject.has("externalCardId") ? contactObject.get("externalCardId").getAsString() : null;
				contactRow.addRowData(10, new ResultSetRowColumnData(false, externalCardId));

				BigDecimal dailyTxnLimitAmount = contactObject.has("dailyTxnLimitAmount") ? contactObject.get("dailyTxnLimitAmount").getAsBigDecimal() : null;
				contactRow.addRowData(11, new ResultSetRowColumnData(false, dailyTxnLimitAmount!=null? dailyTxnLimitAmount.toString() : null));

				String dailyTxnLimitCurrency = contactObject.has("dailyTxnLimitCurrency") ? contactObject.get("dailyTxnLimitCurrency").getAsString() : null;
				contactRow.addRowData(12, new ResultSetRowColumnData(false, dailyTxnLimitCurrency));

				contactRow.addRowData(13, new ResultSetRowColumnData(false, String.valueOf(contactObject.get("isFavorite").getAsBoolean())));

				data4.addRow(contactRow);
			}
		}

		// Declara bloques para el response final del orquestador
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = null;
		IResultSetBlock resultsetBlock4 = null;
		if(responseBody != null) {
			resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		}

		// Agrega informacion a bloques del response final del orquestador
		finalQueryContactsReponse.setReturnCode(200);
		finalQueryContactsReponse.addResponseBlock(resultsetBlock);
		finalQueryContactsReponse.addResponseBlock(resultsetBlock2);
		if(responseBody != null) {
			finalQueryContactsReponse.addResponseBlock(resultsetBlock3);
			finalQueryContactsReponse.addResponseBlock(resultsetBlock4);
		}

		return finalQueryContactsReponse;
	}

	/**
	 * Crea una respuesta generica tipo error (casos con respuesta del conector con success diferente de true)
	 * @param anOriginalProcedureRes
	 * @return IProcedureResponse
	 */
	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}
		String success  ="";
		String code = "";
		String message = "";
		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		// Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		IResultSetData data = new ResultSetData();

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		//String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		//String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();
		//String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();

		if(anOriginalProcedureRes.getReturnCode()> 0) {
			success = "false";
			for (Object messageObj : anOriginalProcedureRes.getMessages()) {
				if (messageObj instanceof IMessageBlock) {
					IMessageBlock messageBlock = (IMessageBlock) messageObj;
					code = String.valueOf(messageBlock.getMessageNumber());
					message = messageBlock.getMessageText();
				}
			}

		}else
		{
			success = (anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1) != null && !anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()) ? anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue() : "false";
			code = (anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2) != null && !anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull()) ? anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue() : "400218";
			message = (anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3) != null && !anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull()) ? anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue() : "Service execution error";
		}

		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);


		anOriginalProcedureResponse.setReturnCode(500);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);

		logger.logInfo("processResponseError creado= " + anOriginalProcedureResponse.toString()  );
		return anOriginalProcedureResponse;
	}

	/**
	 * Hace Log del Response en base de datos ( s_ssn de request original + el request enviado y response recibido de IDC )
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 */
	private void logRequestAndResponse(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Entrando en logRequestAndResponse");
		}

		request.setSpName("cob_bvirtual..sp_log_configuracion_limite");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		//Log response GetOneContactLimits
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");

		if(aBagSPJavaOrchestration.get("operation").equals("C")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "GetOneContactLimits");
			request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("queryString").toString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodyGetOneContactLimits").toString());
		} 

		//Log response SaveRegistContactLimit
		else if (aBagSPJavaOrchestration.get("operation").equals("S")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "SaveRegistContactLimit");
			request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("requestBodySaveContactLimit").toString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodySaveContactLimit").toString());
		}

		//Log response QueryContacts
		else if (aBagSPJavaOrchestration.get("operation").equals("Q")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "QueryContacts");
			request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("queryStringQueryContacts").toString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodyQueryContacts").toString());
		}

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response logRequestAndResponse: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Saliendo de logRequestAndResponse");
		}
	}


	private void registerGlobalRequestAndResponse(IProcedureRequest originalProcedureRequest, Map<String, Object> aBagSPJavaOrchestration, IProcedureResponse finalSaveContactLimitResponse) {
		IProcedureRequest request = new ProcedureRequestAS();

		JsonObject jsonResponseLog = (JsonObject) aBagSPJavaOrchestration.get("jsonResponseLog");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(" Entrando en registerGlobalRequestAndResponse");
		}

		request.setSpName("cob_bvirtual..sp_log_configuracion_limite");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		if (aBagSPJavaOrchestration.get("operation").equals("S")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "SaveRegistContactLimit");
			request.addInputParam("@i_transaccion", ICTSTypes.SQLVARCHAR, "SaveRegistContactLimit");
			request.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, originalProcedureRequest.readValueParam("@i_externalCustomerId"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, originalProcedureRequest.readValueParam("@i_json_req")); 
			//request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, finalSaveContactLimitResponse.getProcedureResponseAsString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, jsonResponseLog.toString());
		}

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response registerGlobalRequestAndResponse: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}	

	private JsonObject createJsonResponseLog(String code, String message, String success){
		JsonObject responseObject = new JsonObject();
		responseObject.addProperty("code", code);
		responseObject.addProperty("message", message);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("success", success);
		jsonObject.add("response", responseObject);
		return jsonObject;
	}

	/**
	 * Consulta datos para envío de OTP
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */

	private IProcedureResponse getLoginById(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Entrando en getLoginById..."); 
		}

		request.setSpName("cob_bvirtual..sp_cons_ente_med_envio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		//			if (aBagSPJavaOrchestration.get("card_id_dock") != null){			
		//				request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("card_id_dock"));
		//				
		//			} else {		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
		//			}
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");

		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_mail_ente", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_num_phone", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("login es: " +  wProductsQueryResp.readValueParam("@o_login"));
			logger.logDebug("phone es: " +  wProductsQueryResp.readValueParam("@o_num_phone"));
		}

		aBagSPJavaOrchestration.put("o_login", wProductsQueryResp.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_phone", wProductsQueryResp.readValueParam("@o_num_phone"));
		aBagSPJavaOrchestration.put("o_entebv", wProductsQueryResp.readValueParam("@o_ente"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getLoginById: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Saliendo de getLoginById...");
		}

		return wProductsQueryResp;
	}


	/**
	 * Realiza validación de OTP
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private DataTokenResponse validateOTPCode(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) { 

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Entrando en validateOTPCode...");
		}

		DataTokenRequest tokenRequest = new DataTokenRequest();

		tokenRequest.setLogin(aBagSPJavaOrchestration.get("o_login").toString());
		tokenRequest.setToken(aRequest.readValueParam("@i_otp_code"));
		tokenRequest.setChannel(8);

		DataTokenResponse tokenResponse = this.tokenService.validateTokenUser(tokenRequest);

		logger.logDebug("Token response: "+tokenResponse.getSuccess());

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Saliendo de validateOTPCode...");
		}

		return tokenResponse;
	}


	/**
	 * Genera OTP
	 * @param aRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */

	private DataTokenResponse generareOTPCode (IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) { 

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Entrando en generarOTPCode...");
		}

		DataTokenRequest tokenRequest = new DataTokenRequest();

		tokenRequest.setLogin(aBagSPJavaOrchestration.get("o_login").toString());
		tokenRequest.setToken(aRequest.readValueParam("@i_otp_code"));
		tokenRequest.setChannel(8);

		DataTokenResponse tokenResponseG = this.tokenService.generateTokenUser(tokenRequest);

		logger.logDebug("Token response: "+tokenResponseG.getSuccess());

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Saliendo de generarOTPCode...");
		}

		return tokenResponseG;
	}

	/**
	 * Genera los logs de las solicitudes de token
	 * @param login
	 */

	private void registerRequestType(String login) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo( " Entrando en registerRequestType");
		}

		request.setSpName("cob_bvirtual..sp_solicitud_OTP");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, login);
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "S");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}

	/**
	 * Genera los logs generados de la tabla se_token
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private IProcedureResponse registrosFallidos(Map<String, Object> aBagSPJavaOrchestration) {		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Entrando en registrosFallidos...");
		}

		request.setSpName("cob_bvirtual..sp_log_ingfallo_2FA");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_entebv"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8" );
		request.addInputParam("@i_cod_error", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_codErrorOTP").toString());

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registrosFallidos: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Saliendo de registrosFallidos...");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse executeBlockOperationConnector(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(className + " Entrando en executeBlockOperation");
		}
		String phoneNumber = null;
		Integer phoneCode = 52;
		String channel = null;

		IProcedureResponse connectorBlockOperationResponse = null;

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");

		if(logger.isDebugEnabled())
			logger.logDebug("aRequest execute blockOperation: " + aRequest);

		try {
			//Parametros de entrada
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=CISConnectorBlockOperation)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700122");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700122");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700122");

			anOriginalRequest.addInputParam("@i_customer_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));

			if(aRequest.readValueParam("@i_channel").toString().contains("DESKTOP_BROWSER")) {
				channel = "web";
			}
			anOriginalRequest.addInputParam("@i_channel", ICTSTypes.SQLVARCHAR, channel);

			//Construccion del body para el conector
			JsonObject jsonRequest = new JsonObject();

			//Validacion del numero de telefono
			if(aBagSPJavaOrchestration.get("o_phone") != null) {
				phoneNumber = aBagSPJavaOrchestration.get("o_phone").toString();
			}
			jsonRequest.addProperty("phoneNumber", phoneCode + phoneNumber);
			anOriginalRequest.addInputParam("@i_phone_header", ICTSTypes.SQLVARCHAR, phoneCode + phoneNumber);

			//Validacion del blockCode
			jsonRequest.addProperty("blockCode", "21");

			//Validacion de blockResason
			jsonRequest.addProperty("blockReason", "Token bloqueado por exceder limite de intentos");

			anOriginalRequest.addInputParam("@i_json_request", ICTSTypes.SQLVARCHAR, jsonRequest.toString());

			//Se llama al conector de blockOperation
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorBlockOperation)");
			anOriginalRequest.setSpName("cob_procesador..sp_conne_block_operation");

			// SE EJECUTA CONECTOR
			connectorBlockOperationResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (connectorBlockOperationResponse.readValueParam("@o_responseCode") != null)
				aBagSPJavaOrchestration.put("responseCode", connectorBlockOperationResponse.readValueParam("@o_responseCode"));

			if (connectorBlockOperationResponse.readValueParam("@o_message") != null)
				aBagSPJavaOrchestration.put("message", connectorBlockOperationResponse.readValueParam("@o_message"));

			if (connectorBlockOperationResponse.readValueParam("@o_success") != null) {
				aBagSPJavaOrchestration.put("success_block_operation", connectorBlockOperationResponse.readValueParam("@o_success"));
			}
			else {
				aBagSPJavaOrchestration.put("success_block_operation", "false");
			}

			if(logger.isDebugEnabled())
				logger.logDebug("Response executeBlockOperationConnector: "+ connectorBlockOperationResponse.getProcedureResponseAsString());

			registerRequestBlockOperation(connectorBlockOperationResponse, jsonRequest.toString(), aRequest.readValueParam("@i_external_customer_id"));
		} catch (Exception e) {
			e.printStackTrace();
			connectorBlockOperationResponse = null;
			logger.logInfo(className +" Error Catastrofico de executeBlockOperationConnector");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(className + "--> executeBlockOperationConnector");
			}
		}

		return connectorBlockOperationResponse;
	}

	private void registerRequestBlockOperation(IProcedureResponse wProcedureResponse, String requestSend, String customerId){
		IProcedureRequest request = new ProcedureRequestAS();
		final String METHOD_NAME = "[registerRequestBlockOperationError]";

		if (logger.isInfoEnabled()) {
			logger.logInfo( " Entrando en registerRequestBlockOperationError");
		}

		String bodyResponse = wProcedureResponse.readValueParam("@o_body_response");

		String success = wProcedureResponse.getResultSetRowColumnData(1, 1, 1).isNull()?"false":wProcedureResponse.getResultSetRowColumnData(1, 1, 1).getValue();

		String code = wProcedureResponse.getResultSetRowColumnData(1, 1, 2).isNull()?"":wProcedureResponse.getResultSetRowColumnData(1, 1, 2).getValue();
		String message = wProcedureResponse.getResultSetRowColumnData(1, 1, 3).isNull()?"":wProcedureResponse.getResultSetRowColumnData(1, 1, 3).getValue();

		logger.logInfo("code:: " + code);
		logger.logInfo("message:: " + message);
		logger.logInfo("bodyResponse:: " + bodyResponse);

		request.setSpName("cob_bvirtual..sp_log_ingfallo_2FA");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "B");
		request.addInputParam("@i_request_block_operation", ICTSTypes.SQLVARCHAR, requestSend);
		request.addInputParam("@i_response_block_operation", ICTSTypes.SQLVARCHAR, bodyResponse);
		request.addInputParam("@i_cod_error", ICTSTypes.SQLVARCHAR, code);
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, customerId);
		request.addInputParam("@i_error_message", ICTSTypes.SQLVARCHAR, message);

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}
	private String processOtpReturnCode(String otpReturnCode) {

		if (otpReturnCode != null && !otpReturnCode.equals("0")) {
			if (otpReturnCode.equals("1890000")) { // Token no existe o inválido
				return "400385";

			} else if (otpReturnCode.equals("1890006")) { // Usuario sin nuevo token validable
				return "400387";

			} else if (otpReturnCode.equals("1890003")) { // Token validado
				return "400386";

			} else if (otpReturnCode.equals("1887677")) { // Token expirado
				return "400381";

			} else if (otpReturnCode.equals("1890004")) { // Token bloqueado
				return "400382";

			} else if (otpReturnCode.equals("1890005")) { // Token bloqueado, asistencia requerida
				return "400383";

			} else if (otpReturnCode.equals("1890002")) { // Longitud del token encriptado
				return "400384";

			} else {
				return "1"; 
			}
		}

		return "1"; 
	}
}
