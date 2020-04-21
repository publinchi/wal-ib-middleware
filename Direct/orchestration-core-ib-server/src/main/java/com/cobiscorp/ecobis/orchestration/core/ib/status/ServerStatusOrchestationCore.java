
package com.cobiscorp.ecobis.orchestration.core.ib.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;

/**
 * @since Dec 17, 2014
 * @author jbaque
 * @version 1.0.0
 */

@Component(name = "ServerStatusOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ServerStatusOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ServerStatusOrchestationCore") })
public class ServerStatusOrchestationCore extends SPJavaOrchestrationBase {
	private static final String SERVER_STATUS_RESP = "SERVER_STATUS_CENTRAL_RESP";
	// private static final String ACCOUNTS_QUERY_RESP =
	// "SERVER_STATUS_LOCAL_RESP";
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final int ERROR40004 = 40004;
	private static final int ERROR40002 = 40002;
	ArrayList<String> allProcedureResponseCodes = new ArrayList<String>();
	private static final String CLASS_NAME = " >-----> ";
	ILogger logger = this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en la orquestación StatusQueryOrchestation");
		}

		CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

		boolean wHasErrorStatusServer = true;

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {

			// 1. Consulta de estado del central
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "1. Consulta de estado del central ---> Método executeServerStatusCentral");
			}
			IProcedureRequest wServerStatusRequest = initProcedureRequest(anOriginalRequest);
			wHasErrorStatusServer = executeServerStatusCentral(wServerStatusRequest, anOriginalRequest,
					aBagSPJavaOrchestration);

			IProcedureResponse wServerStatusResp = (IProcedureResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (validateErrorCode(wServerStatusResp, ERROR40002)) {

				// 2. Consulta de cuentas en el local
				if (logger.isInfoEnabled()) {
					logger.logInfo(
							CLASS_NAME + "2. Consulta de estado en el local ---> Método executeServerStatusLocal");
				}
				wServerStatusRequest = initProcedureRequest(anOriginalRequest);
				wHasErrorStatusServer = executeServerStatusLocal(wServerStatusRequest, anOriginalRequest,
						aBagSPJavaOrchestration);

				wServerStatusResp = (IProcedureResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Saliendo de la orquestación StatusQueryOrchestation");
			}

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("error", e);
			}
			cisResponseHelper = new CISResponseManagmentHelper();
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Servicio no disponible");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	boolean executeServerStatusCentral(IProcedureRequest aServerStatusRequest, IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeServerStatus");
		}

		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		Util.copyParam("@t_show_version", anOriginalRequest, aServerStatusRequest);
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SQLCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SQLCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SQLDATETIME, "01/01/2010");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@t_show_version: " + aServerStatusRequest.readValueParam("@t_show_version"));
			logger.logDebug(CLASS_NAME + "@i_cis: " + aServerStatusRequest.readValueParam("@i_cis"));
			logger.logDebug("Request Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());
		}
		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wServerStatusResp.getProcedureResponseAsString());
		}
		aBagSPJavaOrchestration.put(SERVER_STATUS_RESP, wServerStatusResp);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeServerStatus");
		}
		return wServerStatusResp.hasError();
	}

	/**
	 * This method executes an server status operation to a core database
	 * 
	 * @param aStatusRequest
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	boolean executeServerStatusLocal(IProcedureRequest aServerStatusRequest, IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeServerStatus");
		}
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		Util.copyParam("@t_show_version", anOriginalRequest, aServerStatusRequest);
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SQLCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SQLCHAR, "N");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SQLDATETIME, "01/01/2010");
		aServerStatusRequest.addOutputParam("@o_con_saldo", ICTSTypes.SQLVARCHAR, "N");
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@t_show_version: " + aServerStatusRequest.readValueParam("@t_show_version"));
			logger.logDebug(CLASS_NAME + "@i_cis: " + aServerStatusRequest.readValueParam("@i_cis"));
			logger.logDebug("Request Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());
		}
		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);
		wServerStatusResp.addParam("@o_con_saldo", ICTSTypes.SQLVARCHAR, 0, "N");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wServerStatusResp.getProcedureResponseAsString());
		}
		aBagSPJavaOrchestration.put(SERVER_STATUS_RESP, wServerStatusResp);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeServerStatus");
		}
		return wServerStatusResp.hasError();
	}

	public void addResultsetToRequest(IProcedureRequest aprocedureRequest, Map<String, Object> aBagSPJavaOrchestration,
			String aResultSet, String aResponse, int aOrder) {
		IProcedureResponse wProcedureResp = (IProcedureResponse) aBagSPJavaOrchestration.get(aResponse);
		aprocedureRequest.addResultSetParam(aResultSet, wProcedureResp.getResultSet(aOrder));
	}

	private boolean validateErrorCode(IProcedureResponse response, int code) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Validando existencia del codigo " + code + " en la respuesta :"
					+ response.getProcedureResponseAsString());
		}

		if (response.hasError() == false && code == 0) {
			if (logger.isInfoEnabled()) {
				logger.logInfo(" No existe mensajes de error");
			}
			return true;
		}

		int messageNumber;

		Collection responseBlocks = response.getResponseBlocks();
		if (responseBlocks != null) {
			Iterator it = responseBlocks.iterator();
			while (it.hasNext()) {
				Object msgBlock = it.next();
				if (msgBlock instanceof IMessageBlock) {
					messageNumber = ((IMessageBlock) msgBlock).getMessageNumber();
					if (messageNumber == code) {
						if (logger.isInfoEnabled()) {
							logger.logInfo(" Existe el código " + code + " en la respuesta");
						}
						return true;
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo(" No existe el código " + code + " en la respuesta");
		}

		return false;
	}

	/**
	 * Elimina los mensages con código 40002 y 40004 de la respuesta
	 * 
	 * @param response
	 */
	private void deleteErrorMessageOffline(IProcedureResponse response) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Eliminando mensages con error 40004 y 40002");
		}
		int messageNumber;
		Collection responseBlocks = response.getResponseBlocks();
		Collection messages = response.getMessages();
		ArrayList<IMessageBlock> messageToDelete = new ArrayList<IMessageBlock>();
		if (responseBlocks != null) {
			Iterator it = responseBlocks.iterator();
			while (it.hasNext()) {
				Object msgBlock = it.next();
				if (msgBlock instanceof IMessageBlock) {
					messageNumber = ((IMessageBlock) msgBlock).getMessageNumber();
					if (messageNumber == ERROR40004 || messageNumber == ERROR40002) {
						messageToDelete.add((IMessageBlock) msgBlock);
					}
				}
			}
			responseBlocks.removeAll(messageToDelete);
		}

		if (messages != null) {
			messages.removeAll(messageToDelete);
		}
	}

	/**
	 * Estructura mensaje de respuesta
	 */
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Armando respuesta final");
		}

		CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalProcedureReq);

		ArrayList<String> allProcedureResponseCodes = new ArrayList<String>();

		for (Map.Entry<String, Object> element : aBagSPJavaOrchestration.entrySet()) {
			if (element.getValue() != null) {
				if (element.getValue() instanceof IProcedureResponse) {
					IProcedureResponse response = (IProcedureResponse) element.getValue();
					allProcedureResponseCodes.add(element.getKey());
					deleteErrorMessageOffline(response);
				}
			}
		}
		String[] allProcedureResponseCodesArray = allProcedureResponseCodes.toArray(new String[0]);

		cisResponseHelper.addOutputParamsResponse(wProcedureRespFinal, anOriginalProcedureReq,
				allProcedureResponseCodesArray, aBagSPJavaOrchestration);
		cisResponseHelper.addResultsetsResponse(wProcedureRespFinal, allProcedureResponseCodesArray,
				aBagSPJavaOrchestration);

		boolean hasMessageError = false;
		for (Map.Entry<String, Object> element : aBagSPJavaOrchestration.entrySet()) {
			if (element.getValue() != null) {
				if (element.getValue() instanceof IProcedureResponse) {
					if (((IProcedureResponse) element.getValue()).getMessageListSize() != 0) {
						cisResponseHelper.addMessages(wProcedureRespFinal, allProcedureResponseCodesArray,
								aBagSPJavaOrchestration);
						if (wProcedureRespFinal.getReturnCode() == ERROR40002
								|| wProcedureRespFinal.getReturnCode() == ERROR40004) {
							if (logger.isInfoEnabled()) {
								logger.logInfo(
										CLASS_NAME + " Cambio " + wProcedureRespFinal.getReturnCode() + " por 0");
							}
							wProcedureRespFinal.setReturnCode(0);
						}
						if (logger.isInfoEnabled()) {
							logger.logInfo(CLASS_NAME + " Termina de armar respuesta final. Es la siguiente: "
									+ wProcedureRespFinal.getProcedureResponseAsString());
						}
						return wProcedureRespFinal;
					}
				}
			}

		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Termina de armar respuesta final. Es la siguiente: "
					+ wProcedureRespFinal.getProcedureResponseAsString());
		}
		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);
		return wProcedureRespFinal;
	}
}
