package com.cobiscorp.ecobis.orchestration.core.ib.transfer.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;

public abstract class TransferOfflineTemplate extends TransferBaseTemplate {

	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
	protected static final String TRANSFER_NAME = "TRANSFER_NAME";
	protected static final int CODE_OFFLINE = 40004;

	protected abstract IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);

	private static ILogger logger = LogFactory.getLogger(TransferOfflineTemplate.class);

	public abstract ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent();

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseToSychronize = null;
		IProcedureResponse responseTransfer = null;

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

		responseTransfer = executeTransfer(aBagSPJavaOrchestration); 
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);
		
		if (serverResponse.getOnLine()) {

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + " Respuesta de ejecución método executeTransfer: " + responseTransfer.getProcedureResponseAsString());

			if (Utils.flowError(messageErrorTransfer.append(" --> executeTransfer").toString(), responseTransfer)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + messageErrorTransfer);
				return responseTransfer;
			}
		} else {
			// Si no es ejecucion de reentry, grabar en reentry
			if (!getFromReentryExcecution(aBagSPJavaOrchestration)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + " Transferencia en OffLine serverResponse :" + serverResponse.toString());

				responseTransfer = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
				aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseTransfer);
			}
		}

		if (serverResponse.getOnLine() || (!serverResponse.getOnLine() && serverResponse.getOfflineWithBalances())) {
			responseToSychronize = new ProcedureResponseAS();
			responseToSychronize.setReturnCode(responseTransfer.getReturnCode());
			if (responseTransfer.getResultSetListSize() > 0) {
				responseToSychronize.addResponseBlock(responseTransfer.getResultSet(1));
			}
			aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseToSychronize);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta de ejecución método executeTransaction Response:  " + responseTransfer.getProcedureResponseAsString());
		return responseTransfer;
	}

	protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = anOriginalRequest.clone();
		IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);

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
