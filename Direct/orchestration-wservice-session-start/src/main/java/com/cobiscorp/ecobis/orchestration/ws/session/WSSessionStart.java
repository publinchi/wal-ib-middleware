package com.cobiscorp.ecobis.orchestration.ws.session;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
//import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

/**
 * Orchestration to start session Sintesis
 * 
 * @since Jul 14, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */
@Component(name = "WSSessionStart", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "WSSessionStart"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "WSSessionStart") })
public class WSSessionStart extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(WSSessionStart.class);

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
	protected java.util.Properties properties;

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		this.properties = arg0.getProperties("//property");
		if (logger.isInfoEnabled())
			logger.logInfo(" Connector Properties --> " + this.properties);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("!executeJavaOrchestration_WSSessionStart" + aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		executeSteps(aBagSPJavaOrchestration);

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

	}

	/**
	 * /** Execute webservice to get a session id
	 * <p>
	 * This method execute a Sintesis Web Service and get a session id
	 * 
	 */

	private IProcedureResponse startSession(Map<String, Object> aBagSPJavaOrchestration,
			IProcedureRequest anOriginalRequest) {

		IProcedureRequest wIniciarSesionTMP = initProcedureRequest(anOriginalRequest);
		IProcedureResponse wIniciarSesionResp;

		wIniciarSesionTMP.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "687");
		wIniciarSesionTMP.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "687");
		wIniciarSesionTMP.addOutputParam("@o_id_operativo", ICTSTypes.SQLVARCHAR, "");
		wIniciarSesionTMP.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_TIMEOUT")));
		wIniciarSesionTMP.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_CATALOG_PROVIDER")));
		wIniciarSesionTMP.removeFieldInHeader("trn_virtual");
		wIniciarSesionTMP.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "687");

		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) this.properties.get("CONNECTOR_TYPE")));
		wIniciarSesionResp = executeProvider(wIniciarSesionTMP, aBagSPJavaOrchestration);

		return wIniciarSesionResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	public IProcedureResponse executeWSMethod(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse wIniciarSesionResp = null;

		wIniciarSesionResp = startSession(aBagSPJavaOrchestration, anOriginalRequest);
		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, wIniciarSesionResp);
		wIniciarSesionResp.setReturnCode(Integer.parseInt(wIniciarSesionResp.readValueParam("@o_coderror")));
		wIniciarSesionResp.addMessage(wIniciarSesionResp.getReturnCode(),
				wIniciarSesionResp.readValueParam("@o_mensaje"));

		if (logger.isInfoEnabled())
			logger.logInfo("wStartSession retorna getReturnCode  " + wIniciarSesionResp.getReturnCode());

		return wIniciarSesionResp;
	}

}
