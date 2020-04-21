package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits.config;

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
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationSaving;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorSaving;

@Component(name = "SimulatorSaving ", immediate = false)
@Service(value = { ICoreServiceSimulatorSaving.class })
@Properties(value = { @Property(name = "service.description", value = "SimulatorSaving"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SimulatorSaving") })
public class SimulatorSaving extends SPJavaOrchestrationBase implements ICoreServiceSimulatorSaving {
	private static ILogger logger = LogFactory.getLogger(SimulatorSaving.class);
	private static final String SP_NAME = "cobis..sp_bv_simulador_ahorros";
	private static final int COL_CODE = 0;
	private static final int COL_DESCRIPTION = 1;
	private static final int COL_CATEGORY = 2;
	private static final int COL_MAX_AMOUNT = 0;
	private static final int COL_FINAL_AMOUNT = 0;
	private static final int COL_RATE = 1;
	private static final int COL_TERM = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorSaving#getSimulationSaving(com.cobiscorp.ecobis.ib.
	 * application.dtos.SimulationSavingRequest)
	 */
	@Override
	public SimulationSavingResponse getSimulationSaving(SimulationSavingRequest savingRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationSaving");

		IProcedureResponse pResponse = Execution(SP_NAME, savingRequest, "getSimulationSaving");
		SimulationSavingResponse savingResponse = transformSimulationSavingResponse(pResponse);
		return savingResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorSaving#getSimulationMaxSaving(com.cobiscorp.ecobis.
	 * ib.application.dtos.SimulationSavingRequest)
	 */
	@Override
	public SimulationSavingResponse getSimulationMaxSaving(SimulationSavingRequest savingRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationMaxSaving");

		IProcedureResponse pResponse = Execution(SP_NAME, savingRequest, "getSimulationMaxSaving");
		SimulationSavingResponse savingResponse = transformimulationMaxSavingResponse(pResponse);
		return savingResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorSaving#getSimulationExecuteSaving(com.cobiscorp.
	 * ecobis.ib.application.dtos.SimulationSavingRequest)
	 */
	@Override
	public SimulationSavingResponse getSimulationExecuteSaving(SimulationSavingRequest savingRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationExecuteSaving");

		IProcedureResponse pResponse = Execution(SP_NAME, savingRequest, "getSimulationExecuteSaving");
		SimulationSavingResponse savingResponse = transformimulationExecuteSavingResponse(pResponse);
		return savingResponse;
	}

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

	private IProcedureResponse Execution(String spName, SimulationSavingRequest savingRequest, String method) {
		IProcedureRequest request = initProcedureRequest(savingRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800142");
		request.setSpName(spName);

		if (savingRequest.getSimulationSaving().getOperationType().equals("S")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					savingRequest.getSimulationSaving().getOperationType());
			request.addInputParam("@i_tipo_ente", ICTSTypes.SQLCHAR, savingRequest.getEntityType());
		}
		if (savingRequest.getSimulationSaving().getOperationType().equals("T")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					savingRequest.getSimulationSaving().getOperationType());
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
					savingRequest.getSimulationSaving().getCurrencyId().toString());
			request.addInputParam("@i_monto_ini", ICTSTypes.SQLMONEY,
					savingRequest.getSimulationSaving().getInitialAmount().toString());
			request.addInputParam("@i_tipocta", ICTSTypes.SQLCHAR, savingRequest.getSimulationSaving().getEntityType());
			request.addInputParam("@i_prod_banc", ICTSTypes.SQLINT2,
					savingRequest.getSimulationSaving().getCode().toString());
			request.addInputParam("@i_categoria", ICTSTypes.SQLCHAR, savingRequest.getSimulationSaving().getCategory());
		}
		if (savingRequest.getSimulationSaving().getOperationType().equals("M")
				|| savingRequest.getSimulationSaving().getOperationType().equals("P")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					savingRequest.getSimulationSaving().getOperationType());
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
					savingRequest.getSimulationSaving().getCurrencyId().toString());
			request.addInputParam("@i_monto_ini", ICTSTypes.SQLMONEY,
					savingRequest.getSimulationSaving().getInitialAmount().toString());
			request.addInputParam("@i_tipocta", ICTSTypes.SQLCHAR, savingRequest.getSimulationSaving().getEntityType());
			request.addInputParam("@i_prod_banc", ICTSTypes.SQLINT2,
					savingRequest.getSimulationSaving().getCode().toString());
			request.addInputParam("@i_categoria", ICTSTypes.SQLCHAR, savingRequest.getSimulationSaving().getCategory());

			if (savingRequest.getSimulationSaving().getTerm() != null)
				request.addInputParam("@i_periodo", ICTSTypes.SQLINT2,
						savingRequest.getSimulationSaving().getTerm().toString());

			if (savingRequest.getSimulationSaving().getFinalAmount() != null)
				request.addInputParam("@i_monto_aprox", ICTSTypes.SQLMONEY,
						savingRequest.getSimulationSaving().getFinalAmount().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Simulation Saving: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Simulation*** ");
		}
		return pResponse;
	}

	private SimulationSavingResponse transformSimulationSavingResponse(IProcedureResponse pResponse) {
		SimulationSavingResponse savingResponse = new SimulationSavingResponse();
		SimulationSaving simulationSaving = new SimulationSaving();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationSavingResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {

			IResultSetRow[] rowSaving = pResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRow iResultSetRow = rowSaving[0];
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			simulationSaving
					.setCode(columns[COL_CODE].getValue() == null ? 0 : Integer.parseInt(columns[COL_CODE].getValue()));
			simulationSaving.setDescription(columns[COL_DESCRIPTION].getValue());
			simulationSaving.setCategory(columns[COL_CATEGORY].getValue());

			savingResponse.setSuccess(true);
		} else
			savingResponse.setSuccess(false);

		simulationSaving.setInterestRate(pResponse.readValueParam("@o_tasa_interes"));
		simulationSaving.setPeriod(pResponse.readValueParam("@o_periodo") == null ? 0
				: Integer.parseInt(pResponse.readValueParam("@o_periodo")));
		simulationSaving.setAmount(pResponse.readValueParam("@o_monto") == null ? 0
				: Double.parseDouble(pResponse.readValueParam("@o_monto")));

		savingResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		savingResponse.setMessages(message);
		savingResponse.setSimulationSaving(simulationSaving);
		return savingResponse;

	}

	private SimulationSavingResponse transformimulationMaxSavingResponse(IProcedureResponse pResponse) {
		SimulationSavingResponse savingResponse = new SimulationSavingResponse();
		SimulationSaving simulationSaving = new SimulationSaving();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationMaxSavingResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowSaving = pResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRow iResultSetRow = rowSaving[0];
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			simulationSaving.setMaxAmount(columns[COL_MAX_AMOUNT].getValue() == null ? 0
					: Double.parseDouble(columns[COL_MAX_AMOUNT].getValue()));
			savingResponse.setSuccess(true);
		} else
			savingResponse.setSuccess(false);

		simulationSaving.setInterestRate(pResponse.readValueParam("@o_tasa_interes"));
		simulationSaving.setPeriod(pResponse.readValueParam("@o_periodo") == null ? 0
				: Integer.parseInt(pResponse.readValueParam("@o_periodo")));
		simulationSaving.setAmount(pResponse.readValueParam("@o_monto") == null ? 0
				: Double.parseDouble(pResponse.readValueParam("@o_monto")));

		savingResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		savingResponse.setMessages(message);
		savingResponse.setSimulationSaving(simulationSaving);
		return savingResponse;
	}

	private SimulationSavingResponse transformimulationExecuteSavingResponse(IProcedureResponse pResponse) {
		SimulationSavingResponse savingResponse = new SimulationSavingResponse();
		SimulationSaving simulationSaving = new SimulationSaving();
		if (logger.isInfoEnabled())
			logger.logInfo(
					"*** transformimulationExecuteSavingResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowSaving = pResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRow iResultSetRow = rowSaving[0];
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			simulationSaving.setFinalAmount(columns[COL_FINAL_AMOUNT].getValue() == null ? 0
					: Double.parseDouble(columns[COL_FINAL_AMOUNT].getValue()));
			simulationSaving.setRate(
					columns[COL_RATE].getValue() == null ? 0 : Double.parseDouble(columns[COL_RATE].getValue()));
			simulationSaving
					.setTerm(columns[COL_TERM].getValue() == null ? 0 : Integer.parseInt(columns[COL_TERM].getValue()));

			savingResponse.setSuccess(true);
		} else
			savingResponse.setSuccess(false);

		simulationSaving.setInterestRate(pResponse.readValueParam("@o_tasa_interes"));
		simulationSaving.setPeriod(pResponse.readValueParam("@o_periodo") == null ? 0
				: Integer.parseInt(pResponse.readValueParam("@o_periodo")));
		simulationSaving.setAmount(pResponse.readValueParam("@o_monto") == null ? 0
				: Double.parseDouble(pResponse.readValueParam("@o_monto")));

		savingResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		savingResponse.setMessages(message);
		savingResponse.setSimulationSaving(simulationSaving);
		return savingResponse;
	}
}
