/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.compensation.download;

import java.io.File;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;

/**
 * @author Cesar H
 * @since May 30, 2025
 * @version 1.0.0
 */
@Component(name = "CompensationDownloadOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CompensationDownloadOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "CompensationDownloadOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_compensation_download") })
public class CompensationDownloadOrchestrationCore extends SPJavaOrchestrationBase {

	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "CompensationDownloadOrchestrationCore";
	private java.util.Properties properties;
	protected static final String TRN_18500144 = "18500144";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" loadConfiguration INI CompensationProcessOrchestrationCore");
		}
		properties = arg0.getProperties("//property");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, CompensationDownloadOrchestrationCore start.");

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();

		anProcedureResponse = executeCompensation(anOriginalRequest, aBagSPJavaOrchestration);

		return anProcedureResponse;
	}

	private IProcedureResponse executeCompensation(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeCompensation: ");
		}

		IProcedureResponse wAccountsResp = new ProcedureResponseAS();

		execDownloadFile(aRequest, aBagSPJavaOrchestration);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de executeCompensation");
		}

		return wAccountsResp;
	}

	private IProcedureResponse execDownloadFile(IProcedureRequest anOriginalReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = new ProcedureResponseAS();

		aBagSPJavaOrchestration.remove("trn_virtual");

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en execDownloadFile");
		}
		try {

			anOriginalReq.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator",
					ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CompensationDownloadOrchestrationCore)");
			anOriginalReq.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalReq.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalReq.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalReq.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalReq.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalReq.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, TRN_18500144);
			anOriginalReq.setValueFieldInHeader(ICOBISTS.HEADER_TRN, TRN_18500144);

			anOriginalReq.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, TRN_18500144);

			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorCompensacion)");
			anOriginalReq.setSpName("cob_procesador..sp_compensation");

			anOriginalReq.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, TRN_18500144);
			anOriginalReq.addInputParam("@t_trn", ICTSTypes.SYBINT4, TRN_18500144);
			anOriginalReq.addInputParam("@i_accion", ICTSTypes.SYBINT4, "U");

			if (logger.isDebugEnabled())
				logger.logDebug("Compensation--> request execDownloadFile app: " + anOriginalReq.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalReq, aBagSPJavaOrchestration);

		} catch (Exception e) {
			this.logger.logInfo("CompensationDownloadOrchestrationCore Error Catastrofico de execDownloadFile", e);

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("CompensationDownloadOrchestrationCore --> Saliendo de execDownloadFile");
			}
		}

		return connectorCardResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

}
