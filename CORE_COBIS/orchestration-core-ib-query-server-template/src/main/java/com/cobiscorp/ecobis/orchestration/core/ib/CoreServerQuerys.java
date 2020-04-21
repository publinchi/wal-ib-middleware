/**
 *
 */
package com.cobiscorp.ecobis.orchestration.core.ib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

@Component(name = "CoreServerQuerys", immediate = false)
@Service(value = { ICoreServer.class })
@Properties(value = { @Property(name = "service.description", value = "CoreServerQuerys"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "CoreServerQuerys") })
public class CoreServerQuerys extends SPJavaOrchestrationBase implements ICoreServer {

	private String CLASS_NAME = " >-----> ";
	/** Instancia del Logger */
	private static ILogger logger = LogFactory.getLogger(CoreServerQuerys.class);
	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;

	/**
	 * This method executes an server status operation to a core database
	 *
	 * @param aStatusRequest
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	@Override
	public ServerResponse getServerStatus(ServerRequest serverRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SYBVARCHAR, "XXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response Corebanking: " + wServerStatusResp.getProcedureResponseAsString());

		ServerResponse serverResponse = new ServerResponse();
		
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
					serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003 || wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta: " + serverResponse);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");

		return serverResponse;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
