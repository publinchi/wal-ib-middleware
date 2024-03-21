package com.cobiscorp.ecobis.ib.orchestration.base.transfers;

import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

public abstract class DispacherSpeiTemplate extends SPJavaOrchestrationBase {

	protected static final String CLASS_NAME = " [SpeiInTemplate] ";
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
	protected static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";
	protected static final String INIT_TASK = "-----------------> init task ";
	protected static final String END_TASK = "-----------------> end task ";

	private static ILogger logger = LogFactory.getLogger(TransferInBaseTemplate.class);

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
	 * @param Map<String,       Object> aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	protected abstract IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	public abstract NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration);

	protected abstract void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest);

	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	public String generaMensaje(String vars) {
		vars = vars.substring(vars.indexOf("]") + 1, vars.length());
		return vars;
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
	protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando metodo executeStepsTransactionsBase: " + anOriginalRequest);

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		StringBuilder messageErrorTransfer = new StringBuilder();
		messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

		IProcedureResponse responseTransfer = null;
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		/*ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		logger.logInfo("SERVER RESPONSE: " + responseServer.toString());
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);*/
		mensaje message = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");

		/*
		 * if(anOriginalRequest.readValueFieldInHeader("comision") != null) { if
		 * (logger.isInfoEnabled()) logger.logInfo("Llegada de comisiom ---> " +
		 * anOriginalRequest.readValueFieldInHeader("comision")); }
		 */

		SUPPORT_OFFLINE = true;

		// Valida firmas fisicas
		/*
		 * IProcedureResponse responseSigner = new ProcedureResponseAS();
		 * responseSigner.setReturnCode(0);
		 * aBagSPJavaOrchestration.put(RESPONSE_QUERY_SIGNER, responseSigner);
		 */

		if (message != null) {

			// METODO GUARDAR CAMPOS SEPARADOS QUE EXISTAN

			if (message.getCategoria() != null) {

				if (message.getCategoria().equals("ODPS_LIQUIDADAS_CARGOS")) 
				{
					if (logger.isDebugEnabled())
					{
						logger.logDebug("ODPS_LIQUIDADAS_CARGOS" );
					}
					chargesSettled(anOriginalRequest, aBagSPJavaOrchestration);
				}else
					if(message.getCategoria().equals("ODPS_LIQUIDADAS_ABONOS")) 
					{
						if (logger.isDebugEnabled())
						{
							logger.logDebug("ODPS_LIQUIDADAS_ABONOS");
						}
						paymentIn(anOriginalRequest, aBagSPJavaOrchestration);
					}else
						if (message.getCategoria().equals("ODPS_CANCELADAS_LOCAL")) 
						{
							if (logger.isDebugEnabled())
							{
								logger.logDebug("ODPS_LIQUIDADAS_ABONOS");
							}
							
						} else 
							if (message.getCategoria().equals("ODPS_CANCELADAS_X_BANXICO")) 
							{

							}

			}
		}



		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	protected abstract void executeCreditTransferOrchest(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

	protected abstract Object chargesSettled(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);
	
	protected abstract Object paymentIn(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

}
