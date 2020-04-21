package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimuladorLoanItem;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationLoan;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorLoan;

/**
 * @author areinoso
 *
 */

@Component(name = "TimeDepositSimulatorLoanOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositSimulatorLoanOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositSimulatorLoanOrchestrationCore") })

public class TimeDepositSimulatorLoanOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TimeDepositSimulatorLoanOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceSimulatorLoan.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceSimulatorLoan coreServiceSimulatorLoan;

	protected void bindCoreService(ICoreServiceSimulatorLoan service) {
		coreServiceSimulatorLoan = service;
	}

	protected void unbindCoreService(ICoreServiceSimulatorLoan service) {
		coreServiceSimulatorLoan = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceSimulatorLoan", coreServiceSimulatorLoan);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);
		Map<String, Object> wprocedureResponse1 = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);

		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");
		if (wErrorProcedureResponse != null) {
			if (logger.isDebugEnabled())
				logger.logDebug("wErrorProcedureResponse: " + wErrorProcedureResponse.toString());
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			return wIProcedureResponse1;
		}

		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get("GET_SIMULATOR_RESPONSE");
		if (logger.isDebugEnabled())
			logger.logDebug("ultimo print: " + wIProcedureResponse1.toString());
		return wIProcedureResponse1;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationLoans");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);
		String wOperacion = null;
		wOperacion = anOriginalRequest.readValueParam("@i_operacion");
		if (logger.isInfoEnabled())
			logger.logInfo("lee parametro del servicio " + wOperacion);

		// Getloans

		if (wOperacion.equals("S")) {
			boolean wSuccessExecutionOperation = getSimulationLoans(anOriginalRequest, aBagSPJavaOrchestration);
			ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);
			if (logger.isDebugEnabled())
				logger.logDebug("result execution operation 1 operacion H: " + wSuccessExecutionOperation);
			//
			IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
			wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);
			ret.put("ErrorProcedureResponse", aBagSPJavaOrchestration.get("ErrorProcedureResponse"));
			ret.put("IProcedureResponse", wProcedureResponseOperation1);
		} else {
			// GetCreateLoan
			if (wOperacion.equals("C")) {
				boolean wSuccessExecutionOperation = getSimulationLoanCreate(anOriginalRequest,
						aBagSPJavaOrchestration);
				ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);
				if (logger.isDebugEnabled())
					logger.logDebug("result execution operation 2 operacion S: " + wSuccessExecutionOperation);
				//
				IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
				wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
						ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
				ret.put("ErrorProcedureResponse", aBagSPJavaOrchestration.get("ErrorProcedureResponse"));
				ret.put("IProcedureResponse", wProcedureResponseOperation1);
			} else {
				// GetLoanItem
				if (wOperacion.equals("R")) {
					boolean wSuccessExecutionOperation = getSimulationLoanItems(anOriginalRequest,
							aBagSPJavaOrchestration);
					ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);
					if (logger.isDebugEnabled())
						logger.logDebug("result execution operation 2 operacion S: " + wSuccessExecutionOperation);
					//
					IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
					wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
							ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
					ret.put("ErrorProcedureResponse", aBagSPJavaOrchestration.get("ErrorProcedureResponse"));
					ret.put("IProcedureResponse", wProcedureResponseOperation1);
				} else {

					if (wOperacion.equals("T") || wOperacion.equals("P")) {
						boolean wSuccessExecutionOperation = getSimulationLoanExecute(anOriginalRequest,
								aBagSPJavaOrchestration);
						ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);
						if (logger.isDebugEnabled())
							logger.logDebug("result execution operation 5 operacion C: " + wSuccessExecutionOperation);
						//
						IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
						wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
								ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
						ret.put("ErrorProcedureResponse", aBagSPJavaOrchestration.get("ErrorProcedureResponse"));
						ret.put("IProcedureResponse", wProcedureResponseOperation1);
					}
				}
			}

		}

		if (logger.isInfoEnabled())
			logger.logInfo("retorno ProcedureResponse " + ret.toString());
		return ret;
	}

	private boolean getSimulationLoanExecute(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationLoanExecute");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationLoanResponse simulationLoanResponse = new SimulationLoanResponse();

		try {
			SimulationLoanRequest simulationLoanRequest = transformRequestToDtoLoanExecute(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationLoanRequest.toString());
			simulationLoanResponse = coreServiceSimulatorLoan.getSimulationLoanExecute(simulationLoanRequest);

			wProcedureResponse = transformDtoToResponseLoanExecute(simulationLoanResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", wProcedureResponse);

			if (wProcedureResponse.hasError()) {
				aBagSPJavaOrchestration.put("ErrorProcedureResponse", wProcedureResponse);
			}

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		}
	}

	private boolean getSimulationLoanItems(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationLoanItems");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationLoanResponse simulationLoanResponse = new SimulationLoanResponse();

		try {
			SimulationLoanRequest simulationLoanRequest = transformRequestToDtoLoanItems(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationLoanRequest.toString());
			simulationLoanResponse = coreServiceSimulatorLoan.getSimulationLoanItems(simulationLoanRequest);

			wProcedureResponse = transformDtoToResponseLoanItems(simulationLoanResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", wProcedureResponse);

			if (wProcedureResponse.hasError()) {
				aBagSPJavaOrchestration.put("ErrorProcedureResponse", wProcedureResponse);
			}

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		}

	}

	private boolean getSimulationLoanCreate(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationLoanCreate");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationLoanResponse simulationLoanResponse = new SimulationLoanResponse();

		try {
			SimulationLoanRequest simulationLoanRequest = transformRequestToDtoLoanCreate(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationLoanRequest.toString());
			simulationLoanResponse = coreServiceSimulatorLoan.getSimulationLoanCreate(simulationLoanRequest);

			wProcedureResponse = transformDtoToResponseLoanCreate(simulationLoanResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", wProcedureResponse);

			if (wProcedureResponse.hasError()) {
				aBagSPJavaOrchestration.put("ErrorProcedureResponse", wProcedureResponse);
			}

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		}

	}

	private boolean getSimulationLoans(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationLoans");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationLoanResponse simulationLoanResponse = new SimulationLoanResponse();

		try {
			SimulationLoanRequest simulationLoanRequest = transformRequestToDtoLoans(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationLoanRequest.toString());
			simulationLoanResponse = coreServiceSimulatorLoan.getSimulationLoans(simulationLoanRequest);

			wProcedureResponse = transformDtoToResponseLoans(simulationLoanResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", wProcedureResponse);

			if (wProcedureResponse.hasError()) {
				aBagSPJavaOrchestration.put("ErrorProcedureResponse", wProcedureResponse);
			}

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", null);
			return false;
		}

	}

	/*********************
	 * Transformación de LoansResponse a Response
	 ***********************/

	private IProcedureResponse transformDtoToResponseLoans(SimulationLoanResponse simulationLoanResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida LoansResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		// if (simulationLoanResponse.getReturnCode() != null){
		if (simulationLoanResponse.getReturnCode() == 0) {

			if (simulationLoanResponse.getSimuladorLoanItem().size() > 0) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("OperationType", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("ProductName", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Sector", ICTSTypes.SQLVARCHAR, 30));
				for (SimuladorLoanItem obj : simulationLoanResponse.getSimuladorLoanItem()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getOperationType().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getProductName().toString()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getSector().toString()));
					data.addRow(row);
				}
				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

		} else {
			wResponse = Utils.returnException(simulationLoanResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}

		wResponse.setReturnCode(simulationLoanResponse.getReturnCode());
		return wResponse;
	}

	/*********************
	 * Transformación de LoanItemsResponse a Response
	 ***********************/

	private IProcedureResponse transformDtoToResponseLoanItems(SimulationLoanResponse simulationLoanResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida LoansItemsResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		// IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		// if (simulationLoanResponse.getReturnCode() != null){
		if (simulationLoanResponse.getReturnCode() == 0) {

			if (simulationLoanResponse.getSimulationLoan().size() > 0) {

				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Percentage", ICTSTypes.SQLVARCHAR, 20));

				for (SimulationLoan obj : simulationLoanResponse.getSimulationLoan()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getPercentage().toString()));
					data.addRow(row);
				}

				IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock1);
			}

			if (simulationLoanResponse.getSimuladorLoanItem().size() > 0) {
				metaData = new ResultSetHeader();
				data = new ResultSetData();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Concept", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Description", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("ItemType", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Percentage", ICTSTypes.SQLMONEY, 30));

				for (SimuladorLoanItem obj : simulationLoanResponse.getSimuladorLoanItem()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getConcept()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescription()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getItemType()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getPercentage().toString()));
					data.addRow(row);
				}

				IResultSetBlock resultBlock2 = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock2);
			}

		} else {
			wResponse = Utils.returnException(simulationLoanResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}

		wResponse.setReturnCode(simulationLoanResponse.getReturnCode());
		return wResponse;

	}

	/*********************
	 * Transformación de LoanCreateResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponseLoanCreate(SimulationLoanResponse simulationLoanResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida LoansResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();

		if (simulationLoanResponse.getReturnCode() == 0) {

			if (simulationLoanResponse.getSimulationLoan().size() > 0) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Code", ICTSTypes.SQLVARCHAR, 30));
				for (SimulationLoan obj : simulationLoanResponse.getSimulationLoan()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getCode().toString()));
					data.addRow(row);
				}

				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

		} else {
			wResponse = Utils.returnException(simulationLoanResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}

		wResponse.setReturnCode(simulationLoanResponse.getReturnCode());
		return wResponse;
	}

	/*********************
	 * Transformación de LoanExecuteResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponseLoanExecute(SimulationLoanResponse simulationLoanResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida LoansItemsResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		// if (simulationLoanResponse.getReturnCode() != null){
		if (simulationLoanResponse.getReturnCode() == 0) {

			if (simulationLoanResponse != null) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("EndDate", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Amount", ICTSTypes.SQLMONEY, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Term", ICTSTypes.SQLINT4, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("OperationType", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Operation", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Sector", ICTSTypes.SQLVARCHAR, 30));
				for (SimulationLoan obj : simulationLoanResponse.getSimulationLoan()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getEndDate()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getAmount().toString()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getTerm().toString()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getOperationType()));
					row.addRowData(5, new ResultSetRowColumnData(false, obj.getOperation()));
					row.addRowData(6, new ResultSetRowColumnData(false, obj.getSector()));
					data.addRow(row);
				}
				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

		} else {
			wResponse = Utils.returnException(simulationLoanResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}

		wResponse.setReturnCode(simulationLoanResponse.getReturnCode());
		return wResponse;
	}

	/*********************
	 * Transformación de Request a SimulationLoanRequest
	 ***********************/

	private SimulationLoanRequest transformRequestToDtoLoans(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoLoansSavings");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationLoanRequest simulationLoanRequest = new SimulationLoanRequest();
		SimulationLoan simulationLoan = new SimulationLoan();

		simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationLoanRequest.setSimulationLoan(simulationLoan);
		simulationLoanRequest.setEntityType(wOriginalRequest.readValueParam("@i_tipo_ente"));
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationLoan " + aBagSPJavaOrchestration);
		simulationLoanRequest.setOriginalRequest(wOriginalRequest);
		return simulationLoanRequest;

	}

	/*********************
	 * Transformación de Request a SimulationLoanItemsRequest
	 ***********************/
	private SimulationLoanRequest transformRequestToDtoLoanItems(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoLoanItemsSavings");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationLoanRequest simulationLoanRequest = new SimulationLoanRequest();
		SimulationLoan simulationLoan = new SimulationLoan();

		simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationLoan.setCode(wOriginalRequest.readValueParam("@i_codigo"));

		simulationLoanRequest.setSimulationLoan(simulationLoan);
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationLoanItems " + aBagSPJavaOrchestration);
		simulationLoanRequest.setOriginalRequest(wOriginalRequest);
		return simulationLoanRequest;

	}

	/*********************
	 * Transformación de Request a SimulationLoanCreateRequest
	 ***********************/

	private SimulationLoanRequest transformRequestToDtoLoanCreate(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoLoanItemsSavings");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationLoanRequest simulationLoanRequest = new SimulationLoanRequest();
		SimulationLoan simulationLoan = new SimulationLoan();

		simulationLoan.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));
		simulationLoan.setSector(wOriginalRequest.readValueParam("@i_sector"));
		simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationLoan.setOperation(wOriginalRequest.readValueParam("@i_toperacion"));// setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationLoan.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));
		simulationLoan.setInicialDate(wOriginalRequest.readValueParam("@i_fecha_ini"));
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationLoanCreate " + aBagSPJavaOrchestration);
		simulationLoanRequest.setOriginalRequest(wOriginalRequest);

		simulationLoanRequest.setSimulationLoan(simulationLoan);

		return simulationLoanRequest;
	}

	/*********************
	 * Transformación de Request a SimulationLoanItemsRequest
	 ***********************/

	private SimulationLoanRequest transformRequestToDtoLoanExecute(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoLoanItemsSavings");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationLoanRequest simulationLoanRequest = new SimulationLoanRequest();
		SimulationLoan simulationLoan = new SimulationLoan();

		simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationLoan.setCode(wOriginalRequest.readValueParam("@i_codigo"));
		simulationLoan.setPayment(Double.parseDouble(wOriginalRequest.readValueParam("@i_cuota")));
		simulationLoan.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationMaxSaving " + aBagSPJavaOrchestration);
		simulationLoanRequest.setOriginalRequest(wOriginalRequest);

		simulationLoanRequest.setSimulationLoan(simulationLoan);
		return simulationLoanRequest;
	}

}