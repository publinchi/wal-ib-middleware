package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSettingsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationSettings;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorSettings;

@Component(name = "SimulatorSettings ", immediate = false)
@Service(value = { ICoreServiceSimulatorSettings.class })
@Properties(value = { @Property(name = "service.description", value = "SimulatorSettings"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SimulatorSettings") })
public class SimulatorSettings extends SPJavaOrchestrationBase implements ICoreServiceSimulatorSettings {
	private static ILogger logger = LogFactory.getLogger(SimulatorSettings.class);
	private static final String SP_NAME = "cobis..sp_bv_configuracion_simulador";
	private static final int COL_MIN_DPF = 0;
	private static final int COL_MAX_DAY_DPF = 1;
	private static final int COL_MIN_DAY_DPF = 2;
	private static final int COL_MIN_LOAN = 3;
	private static final int COL_MAX_LOAN = 4;
	private static final int COL_MAX_TERM_LOAN = 5;
	private static final int COL_MAX_TERM_SAVING = 6;
	private static final int COL_MIN_MON_DPF = 7;
	private static final int COL_MIN_TERM_SAVING = 8;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorSettings#getSimulatorsettings(com.cobiscorp.ecobis.
	 * ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public SimulationSettingsResponse getSimulatorsettings(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: executeSimulation");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, certificateDeposit, "getSimulatorsettings");
		SimulationSettingsResponse simulationResponse = transformimulationSettingsResponse(pResponse,
				"getSimulatorsettings");
		return simulationResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private IProcedureResponse Execution(String spName, CertificateDepositCommonRequest certicateRequest,
			String method) {
		IProcedureRequest request = initProcedureRequest(certicateRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800139");
		request.setSpName(spName);

		request.addInputParam("@i_money", ICTSTypes.SQLINT4,
				certicateRequest.getCertificateDeposit().getMoney().toString());
		request.addInputParam("@i_cd_nemonic", ICTSTypes.SQLVARCHAR,
				certicateRequest.getCertificateDeposit().getNemonic());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_cd_nemonic: " + certicateRequest.getCertificateDeposit().getNemonic());
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Simulation Setting: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Simulation*** ");
		}

		return pResponse;
	}

	private SimulationSettingsResponse transformimulationSettingsResponse(IProcedureResponse pResponse, String method) {
		SimulationSettingsResponse wsimulatorResponse = new SimulationSettingsResponse();

		List<SimulationSettings> simulationSettingsList = null;
		if (logger.isInfoEnabled())
			logger.logInfo("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsSettings = pResponse.getResultSet(1).getData().getRowsAsArray();

		simulationSettingsList = new ArrayList<SimulationSettings>();
		for (int i = 0; i < rowsSettings.length; i++) {
			SimulationSettings wsimulatorSettings = new SimulationSettings();
			IResultSetRow iResultSetRow = rowsSettings[i];
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			wsimulatorSettings.setMaxDayCd(columns[COL_MAX_DAY_DPF].getValue() != null
					? Integer.valueOf(columns[COL_MAX_DAY_DPF].getValue()) : 0);
			wsimulatorSettings.setMaxLoan(
					columns[COL_MAX_LOAN].getValue() != null ? Double.valueOf(columns[COL_MAX_LOAN].getValue()) : 0);
			wsimulatorSettings.setMaxTermLoan(columns[COL_MAX_TERM_LOAN].getValue() != null
					? Integer.valueOf(columns[COL_MAX_TERM_LOAN].getValue()) : 0);
			wsimulatorSettings.setMaxTermSaving(columns[COL_MAX_TERM_SAVING].getValue() != null
					? Integer.valueOf(columns[COL_MAX_TERM_SAVING].getValue()) : 0);
			wsimulatorSettings.setMiMonPf(columns[COL_MIN_MON_DPF].getValue() != null
					? Double.valueOf(columns[COL_MIN_MON_DPF].getValue()) : 0);
			wsimulatorSettings.setMinCd(
					columns[COL_MIN_DPF].getValue() != null ? Double.valueOf(columns[COL_MIN_DPF].getValue()) : 0);
			wsimulatorSettings.setMinDayCd(columns[COL_MIN_DAY_DPF].getValue() != null
					? Integer.valueOf(columns[COL_MIN_DAY_DPF].getValue()) : 0);
			wsimulatorSettings.setMinLoan(
					columns[COL_MIN_LOAN].getValue() != null ? Double.valueOf(columns[COL_MIN_LOAN].getValue()) : 0);
			wsimulatorSettings.setMinTermSaving(columns[COL_MIN_TERM_SAVING].getValue() != null
					? Integer.valueOf(columns[COL_MIN_TERM_SAVING].getValue()) : 0);

			simulationSettingsList.add(wsimulatorSettings);
		}

		wsimulatorResponse.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		wsimulatorResponse.setMessages(message);

		wsimulatorResponse.setSimulationSettings(simulationSettingsList);
		if (logger.isInfoEnabled())
			logger.logInfo("********SIMULATOR RESPONSE*******" + wsimulatorResponse.toString());
		return wsimulatorResponse;
	}
}
