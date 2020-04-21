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
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimuladorLoanItem;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationLoan;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorLoan;

@Component(name = "SimulatorLoan ", immediate = false)
@Service(value = { ICoreServiceSimulatorLoan.class })
@Properties(value = { @Property(name = "service.description", value = "SimulatorLoan"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SimulatorLoan") })
public class SimulatorLoan extends SPJavaOrchestrationBase implements ICoreServiceSimulatorLoan {

	private static ILogger logger = LogFactory.getLogger(SimulatorLoan.class);
	private static final String SP_NAME = "cobis..sp_bv_simulador_prestamos";
	private static final int COL_END_DATE = 0;
	private static final int COL_AMOUNT = 1;
	private static final int COL_TERM = 2;
	private static final int COL_OPERATION_TYPE = 3;
	private static final int COL_OPERATION = 4;
	private static final int COL_SECTOR = 5;
	private static final int COL_CODE = 0;
	private static final int COL_PERCENTAGE = 0;
	private static final int COL_CONCEPT = 0;
	private static final int COL_DESCRIPTION = 1;
	private static final int COL_ITEM_TYPE = 2;
	private static final int COL_PERCENTAGE1 = 3;
	private static final int COL_OPERATION_TYPE1 = 0;
	private static final int COL_PRODUCT_NAME = 1;
	private static final int COL_SECTOR1 = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorLoan#getSimulationLoans(com.cobiscorp.ecobis.ib.
	 * application.dtos.SimulationLoanRequest)
	 */
	@Override
	public SimulationLoanResponse getSimulationLoans(SimulationLoanRequest loanRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationLoans");

		IProcedureResponse pResponse = Execution(SP_NAME, loanRequest, "getSimulationLoans");
		SimulationLoanResponse loanResponse = transformSimulationLoanResponse(pResponse);
		return loanResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorLoan#getSimulationLoanItems(com.cobiscorp.ecobis.ib.
	 * application.dtos.SimulationLoanRequest)
	 */
	@Override
	public SimulationLoanResponse getSimulationLoanItems(SimulationLoanRequest loanRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationLoanItems");

		IProcedureResponse pResponse = Execution(SP_NAME, loanRequest, "getSimulationLoanItems");
		SimulationLoanResponse loanResponse = transformSimulationLoanItemsResponse(pResponse);
		return loanResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorLoan#getSimulationLoanCreate(com.cobiscorp.ecobis.ib
	 * .application.dtos.SimulationLoanRequest)
	 */
	@Override
	public SimulationLoanResponse getSimulationLoanCreate(SimulationLoanRequest loanRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationLoanCreate");

		IProcedureResponse pResponse = Execution(SP_NAME, loanRequest, "getSimulationLoanCreate");
		SimulationLoanResponse loanResponse = transformSimulationLoanCreateResponse(pResponse);
		return loanResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceSimulatorLoan#getSimulationLoanExecute(com.cobiscorp.ecobis.
	 * ib.application.dtos.SimulationLoanRequest)
	 */
	@Override
	public SimulationLoanResponse getSimulationLoanExecute(SimulationLoanRequest loanRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationLoanExecute");

		IProcedureResponse pResponse = Execution(SP_NAME, loanRequest, "getSimulationLoanExecute");
		SimulationLoanResponse loanResponse = transformSimulationLoanExecuteResponse(pResponse);
		return loanResponse;
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

	/**
	 * @param spName
	 * @param loanRequest
	 * @param string
	 * @return
	 */
	private IProcedureResponse Execution(String spName, SimulationLoanRequest loanRequest, String string) {

		IProcedureRequest request = initProcedureRequest(loanRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800140");
		request.setSpName(spName);

		if (loanRequest.getSimulationLoan().getOperationType().equals("S")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					loanRequest.getSimulationLoan().getOperationType());
			request.addInputParam("@i_tipo_ente", ICTSTypes.SQLVARCHAR, loanRequest.getEntityType());
		}

		if (loanRequest.getSimulationLoan().getOperationType().equals("C")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					loanRequest.getSimulationLoan().getOperationType());
			request.addInputParam("@i_monto", ICTSTypes.SQLMONEY,
					loanRequest.getSimulationLoan().getAmount().toString());
			request.addInputParam("@i_sector", ICTSTypes.SQLVARCHAR, loanRequest.getSimulationLoan().getSector());
			request.addInputParam("@i_toperacion", ICTSTypes.SQLVARCHAR,
					loanRequest.getSimulationLoan().getOperation());
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
					loanRequest.getSimulationLoan().getCurrencyId().toString());
			request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR,
					loanRequest.getSimulationLoan().getInicialDate());
		}

		if (loanRequest.getSimulationLoan().getOperationType().equals("R")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					loanRequest.getSimulationLoan().getOperationType());
			request.addInputParam("@i_codigo", ICTSTypes.SQLVARCHAR, loanRequest.getSimulationLoan().getCode());
		}
		if (loanRequest.getSimulationLoan().getOperationType().equals("T")
				|| loanRequest.getSimulationLoan().getOperationType().equals("P")) {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					loanRequest.getSimulationLoan().getOperationType());
			request.addInputParam("@i_codigo", ICTSTypes.SQLVARCHAR,
					loanRequest.getSimulationLoan().getCode().toString());
			request.addInputParam("@i_cuota", ICTSTypes.SQLDECIMAL,
					loanRequest.getSimulationLoan().getPayment().toString());
			request.addInputParam("@i_plazo", ICTSTypes.SQLINT4, loanRequest.getSimulationLoan().getTerm().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Simulation Loan: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Simulation*** ");
		}

		return pResponse;
	}

	private SimulationLoanResponse transformSimulationLoanExecuteResponse(IProcedureResponse pResponse) {
		SimulationLoanResponse loanResponse = new SimulationLoanResponse();
		List<SimulationLoan> listsimulationLoan = new ArrayList<SimulationLoan>();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationLoanExecuteResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSimulation = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {
				SimulationLoan simulationLoan = new SimulationLoan();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				simulationLoan.setEndDate(columns[COL_END_DATE].getValue());
				simulationLoan.setAmount(columns[COL_AMOUNT].getValue() == null ? 0
						: Double.parseDouble(columns[COL_AMOUNT].getValue()));
				simulationLoan.setTerm(
						columns[COL_TERM].getValue() == null ? 0 : Integer.parseInt(columns[COL_TERM].getValue()));
				simulationLoan.setOperationType(columns[COL_OPERATION_TYPE].getValue());
				simulationLoan.setOperation(columns[COL_OPERATION].getValue());
				simulationLoan.setSector(columns[COL_SECTOR].getValue());
				listsimulationLoan.add(simulationLoan);
			}
			loanResponse.setSuccess(true);
			loanResponse.setSimulationLoan(listsimulationLoan);
		} else
			loanResponse.setSuccess(false);

		loanResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		loanResponse.setMessages(message);
		return loanResponse;
	}

	private SimulationLoanResponse transformSimulationLoanItemsResponse(IProcedureResponse pResponse) {
		SimulationLoanResponse loanResponse = new SimulationLoanResponse();
		List<SimulationLoan> listsimulationLoan = new ArrayList<SimulationLoan>();
		List<SimuladorLoanItem> listsimuladorLoanItem = new ArrayList<SimuladorLoanItem>();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationLoanItemsResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSimulation = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {
				SimulationLoan simulationLoan = new SimulationLoan();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				simulationLoan.setPercentage(columns[COL_PERCENTAGE].getValue() == null ? 0
						: Double.parseDouble(columns[COL_PERCENTAGE].getValue()));
				listsimulationLoan.add(simulationLoan);
			}

			IResultSetRow[] rowsSimulation2 = pResponse.getResultSet(2).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation2.length; i++) {
				SimuladorLoanItem simuladorLoanItem = new SimuladorLoanItem();
				IResultSetRow iResultSetRow = rowsSimulation2[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				simuladorLoanItem.setConcept(columns[COL_CONCEPT].getValue());
				simuladorLoanItem.setDescription(columns[COL_DESCRIPTION].getValue());
				simuladorLoanItem.setItemType(columns[COL_ITEM_TYPE].getValue());
				simuladorLoanItem.setPercentage(columns[COL_PERCENTAGE1].getValue() == null ? 0
						: Double.parseDouble(columns[COL_PERCENTAGE1].getValue()));
				listsimuladorLoanItem.add(simuladorLoanItem);
			}

			loanResponse.setSuccess(true);
			loanResponse.setSimulationLoan(listsimulationLoan);
			loanResponse.setSimuladorLoanItem(listsimuladorLoanItem);
		} else
			loanResponse.setSuccess(false);

		loanResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		loanResponse.setMessages(message);

		return loanResponse;
	}

	private SimulationLoanResponse transformSimulationLoanResponse(IProcedureResponse pResponse) {

		SimulationLoanResponse loanResponse = new SimulationLoanResponse();
		List<SimuladorLoanItem> listsimuladorLoanItem = new ArrayList<SimuladorLoanItem>();
		IResultSetRow[] rowsSimulation = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationLoanResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {

			for (int i = 0; i < rowsSimulation.length; i++) {
				SimuladorLoanItem simuladorLoanItem = new SimuladorLoanItem();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				simuladorLoanItem.setOperationType(columns[COL_OPERATION_TYPE1].getValue());
				simuladorLoanItem.setProductName(columns[COL_PRODUCT_NAME].getValue());
				simuladorLoanItem.setSector(columns[COL_SECTOR1].getValue());
				listsimuladorLoanItem.add(simuladorLoanItem);
			}
			loanResponse.setSuccess(true);
			loanResponse.setSimuladorLoanItem(listsimuladorLoanItem);
		} else
			loanResponse.setSuccess(false);

		loanResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		loanResponse.setMessages(message);
		return loanResponse;
	}

	private SimulationLoanResponse transformSimulationLoanCreateResponse(IProcedureResponse pResponse) {

		SimulationLoanResponse loanResponse = new SimulationLoanResponse();
		List<SimulationLoan> listsimulationLoan = new ArrayList<SimulationLoan>();
		if (logger.isInfoEnabled())
			logger.logInfo("*** transformimulationLoanCreateResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSimulation = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {
				SimulationLoan simulationLoan = new SimulationLoan();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				simulationLoan.setCode(columns[COL_CODE].getValue());
				listsimulationLoan.add(simulationLoan);
			}
			loanResponse.setSimulationLoan(listsimulationLoan);
			loanResponse.setSuccess(true);
		} else
			loanResponse.setSuccess(false);

		loanResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		loanResponse.setMessages(message);
		if (logger.isInfoEnabled())
			logger.logInfo("*** Loan Create Response: ***" + loanResponse.toString());

		return loanResponse;
	}
}
