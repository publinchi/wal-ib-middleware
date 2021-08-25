package com.cobiscorp.ecobis.ib.orchestration.base.activations;

import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * @author rfu
 * @since Jan 10, 2021
 * @version 1.0.0
 */
public abstract class ActivateCardBaseTemplate extends SPJavaOrchestrationBase {
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
	protected static final String TYPE_REENTRY_OFF_SPI = "S";
	protected static final String TYPE_REENTRY_OFF = "OFF_LINE";
	protected static final String ERROR_CACAO = "ERROR EN ACTIVACION TARJETA CACAO";
	private static ILogger logger = LogFactory.getLogger(ActivateCardBaseTemplate.class);

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

	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

	// ejecutar connector
	protected IProcedureResponse executeConnectorActivateCardBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "rfl--> inicio executeConnectorActivateCardBase: " + anOriginalRequest);

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		IProcedureResponse responseTransfer = null;
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);

		if (logger.isInfoEnabled())
			logger.logInfo("getOnline: " + responseServer.getOnLine());

		// Valida el fuera de línea

		if (logger.isInfoEnabled())
			logger.logInfo("Llama a la funcion validateBvTransaction");

		// ejecutar fuera de linea, N: no, S: si
		String responseSupportOffline = validateBvTransaction(aBagSPJavaOrchestration);

		if (logger.isInfoEnabled())
			logger.logInfo("responseSupportOffline ---> " + responseSupportOffline);

		if (responseSupportOffline == null || responseSupportOffline == "") {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea"));
			return Utils.returnException("Ha ocurrido un error intentando validar si activacion permite fuera de línea");
		}

		if (responseSupportOffline.equals("S")) {
			SUPPORT_OFFLINE = true;
		} else {
			SUPPORT_OFFLINE = false;
		}

		if (!SUPPORT_OFFLINE && !responseServer.getOnLine()) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Activacion no permite ejecución mientras el servidor este fuera de linea"));
			return Utils.returnException("Activacion no permite ejecución mientras el servidor este fuera de linea");
		}

		// Ejecuta transaccion core
		responseTransfer = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);

		if (logger.isInfoEnabled())
			logger.logInfo(new StringBuilder(CLASS_NAME).append("rfl--> Respuesta metodo executeTransaction: " + aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION)).toString());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "rfl--> fin executeConnectorActivateCardBase: " + anOriginalRequest);


		IProcedureResponse processProcedure=(IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);


		logger.logInfo("jcos-> solicitud"+String.valueOf(aBagSPJavaOrchestration.get("descRespuesta")));
		logger.logInfo("jcos-> codigo respuesta"+String.valueOf(aBagSPJavaOrchestration.get("idSolicitud")));
		logger.logInfo("jcos-> descripción respuesta "+String.valueOf(aBagSPJavaOrchestration.get("@o_desc_respuesta")));
		logger.logInfo("jcos-> Valor NIPAX "+String.valueOf(aBagSPJavaOrchestration.get("@o_ValorNIP")));
		//
		//logger.logInfo("jcos-> Valor NIP "+String.valueOf(aBagSPJavaOrchestration.get("@o_id_solicitud")));
		

		processProcedure.addParam("@o_id_solicitud",ICTSTypes.SYBVARCHAR,200,String.valueOf(aBagSPJavaOrchestration.get("@o_id_solicitud")));
		processProcedure.addParam("@o_cod_respuesta",ICTSTypes.SYBVARCHAR,200,String.valueOf(aBagSPJavaOrchestration.get("@o_cod_respuesta")));
		processProcedure.addParam("@o_desc_respuesta",ICTSTypes.SYBVARCHAR,200,String.valueOf(aBagSPJavaOrchestration.get("@o_desc_respuesta")));
		processProcedure.addParam("@o_ValorNIP",ICTSTypes.SYBVARCHAR,200,aBagSPJavaOrchestration.get("@o_ValorNIP")!=null? String.valueOf(aBagSPJavaOrchestration.get("@o_ValorNIP")):"");

		processProcedure.addParam("@o_account_atm",ICTSTypes.SYBVARCHAR,200,aBagSPJavaOrchestration.get("@o_account_atm")!=null? String.valueOf(aBagSPJavaOrchestration.get("@o_account_atm")):"");

		CSPUtil.copyHeaderFields(anOriginalRequest, processProcedure);

		return processProcedure;

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

		// valida la parametria de la tabla bv_transaccion
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
