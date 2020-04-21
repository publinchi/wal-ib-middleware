package com.cobiscorp.ecobis.ib.orchestration.base.templates.transfers;

import java.util.Map;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.interfaces.transfers.ITransferExecution;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.BaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.UtilsTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;

public abstract class ThirdAccountTransferTemplate extends BaseTemplate implements ITransferExecution {
	private static ILogger logger = LogFactory.getLogger(ThirdAccountTransferTemplate.class);
	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")

	public static ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service)

	{

		coreServiceMonetaryTransaction = service;

	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {

		coreServiceMonetaryTransaction = null;

	}

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "executeTransaction START");
			// Boolean onlineStatus = false;
			logger.logInfo("ABCInicia executeTransaction ");
		}
		executeOffline(aBagSPJavaOrchestration);
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(CORE_SERVER);
		if (logger.isInfoEnabled())
			logger.logInfo("AGise tengo linea o no " + serverResponse);

		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("Inicia executeTransaction 2");
		if (SUPPORT_OFFLINE == false && !serverResponse.getOnLine()) {
			if (logger.isInfoEnabled())
				logger.logInfo("Inicia executeTransaction 3");
			wProcedureRespFinal = Utils.returnException("PLUGIN NO SOPORTA OFFLINE!!!");
			return null;
		}

		/*
		 * if (SUPPORT_OFFLINE == false) {
		 * logger.logInfo("NO soporta offline ");
		 */

		// logger.logInfo("Tengo linea o no " + serverResponse);
		// si estoy en linea ejecuto transferencia no se maneja fuera de linea
		if (serverResponse.getOnLine()) {
			TransferResponse responseTransfer = executeTransfer(aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(TRANSFER_RESPONSE, responseTransfer);

			if (responseTransfer != null && responseTransfer.getSuccess()) {
				if (logger.isInfoEnabled())
					logger.logInfo("SUCCESS..." + responseTransfer.getSuccess());

				// onlineStatus = responseTransfer.getSuccess();

				executeUpdateLocal(anOriginalRequest, aBagSPJavaOrchestration);
			} else {
				if (logger.isInfoEnabled())
					logger.logInfo("Fin no se ejecutó la transferencia." + responseTransfer);
				wProcedureRespFinal = Utils.returnException("ERROR EN TRANSFERENCIA!!!");
				//
			}

		}
		return wProcedureRespFinal;
	}

	private IProcedureResponse executeUpdateLocal(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);

		IProcedureResponse wUpdateLocalExecution = updateLocalExecution(aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, wUpdateLocalExecution);
		if (logger.isInfoEnabled())
			logger.logInfo("LOCAL" + wUpdateLocalExecution);
		if ("S".equals(wUpdateLocalExecution.readValueParam("@o_error_ejec"))) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Fin del flujo @o_error_ejec = 'S'");
			}
			ErrorBlock eb = new ErrorBlock(-1, "Error de Ejecucion");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		} else {
			wUpdateLocalExecution.setReturnCode(0);
			if (logger.isWarningEnabled()) {
				for (Object msg : wUpdateLocalExecution.getMessages()) {
					logger.logWarning(msg);
				}
			}
			wUpdateLocalExecution.getMessages().clear();
			if (logger.isDebugEnabled()) {
				logger.logDebug("updateLocalExecution response modificada: "
						+ wUpdateLocalExecution.getProcedureResponseAsString());
			}
			return wUpdateLocalExecution;
		}
	}

	@Override
	protected IProcedureResponse updateLocalExecution(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ValidationAccountsResponse response = (ValidationAccountsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_CENTRAL_VALIDATION);
		// ValidationAccountsResponse response = (ValidationAccountsResponse)
		// aBagSPJavaOrchestration.get(TRANSFER_RESPONSE);
		if (logger.isDebugEnabled())
			logger.logDebug("  validacion del central! " + response);
		IProcedureRequest request = initProcedureRequest(originalRequest);
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);

		IProcedureRequest newRequest = UtilsTransfers.getRequestLocalExecution(request, response, originalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug("  request: " + newRequest);

		if (originalRequest.readValueParam("@s_ssn_branch") != null)
			newRequest.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4,
					originalRequest.readValueFieldInHeader("s_ssn_branch"));

		if (originalRequest.readValueParam("@s_ssn") != null)
			newRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, originalRequest.readValueParam("@s_ssn"));

		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(CORE_SERVER);
		if (logger.isDebugEnabled())
			logger.logDebug("Validar x el id para Reeentry : " + serverResponse);
		// CUANDO SE ESTA FUERA DE LINEA SE ENVIA LA CLAVE DEL REENTRY,
		// EL TIPO DE EJECUCION Y QUE CONSULTE LOS SALDOS EN EL LOCAL
		if (!serverResponse.getOnLine()) {
			if ((originalRequest.readValueParam("@o_clave") != null)
					&& !"".equals(originalRequest.readValueParam("@o_clave"))) {
				newRequest.addInputParam("@i_clave_rty", originalRequest.readParam("@o_clave").getDataType(),
						originalRequest.readValueParam("@o_clave"));
			} else {
				if (logger.isInfoEnabled()) {
					logger.logInfo("::parámetro @o_clave no encontrado");
				}
			}
		}
		newRequest.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "F");
		newRequest.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "S");

		if (logger.isDebugEnabled())
			logger.logDebug("updateLocalExecution request: " + newRequest.getProcedureRequestAsString());

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(newRequest);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "updateLocalExecution response: " + pResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "end updateLocalExecution");

		return pResponse;
	}

}
