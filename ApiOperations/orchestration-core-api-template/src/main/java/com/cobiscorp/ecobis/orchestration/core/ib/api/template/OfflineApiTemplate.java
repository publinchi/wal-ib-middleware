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
}
