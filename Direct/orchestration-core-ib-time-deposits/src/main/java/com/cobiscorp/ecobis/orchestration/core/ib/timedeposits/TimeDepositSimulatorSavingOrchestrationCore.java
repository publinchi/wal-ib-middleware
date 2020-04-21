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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationSaving;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorSaving;

@Component(name = "TimeDepositSimulatorSavingOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositSimulatorSavingOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositSimulatorSavingOrchestrationCore") })

public class TimeDepositSimulatorSavingOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TimeDepositSimulatorSavingOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceSimulatorSaving.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceSimulatorSaving coreServiceSimulationSaving;

	protected void bindCoreService(ICoreServiceSimulatorSaving service) {
		coreServiceSimulationSaving = service;
	}

	protected void unbindCoreService(ICoreServiceSimulatorSaving service) {
		coreServiceSimulationSaving = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logDebug("executeJavaOrchestration");

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceSimulationSaving", coreServiceSimulationSaving);
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

	protected Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationSaving");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);
		String wOperacion = null;
		wOperacion = anOriginalRequest.readValueParam("@i_operacion");
		if (logger.isInfoEnabled())
			logger.logInfo("lee parametro del servicio " + wOperacion);

		// GetMaxSaving

		if (wOperacion.equals("T")) {
			boolean wSuccessExecutionOperation = getMaxSaving(anOriginalRequest, aBagSPJavaOrchestration);
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
			// GetSavings
			if (wOperacion.equals("S")) {
				boolean wSuccessExecutionOperation = getSaving(anOriginalRequest, aBagSPJavaOrchestration);
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
				// GetExcuteSavingSimulation

				if (wOperacion.equals("M") || wOperacion.equals("P")) {
					boolean wSuccessExecutionOperation = getExecuteSaving(anOriginalRequest, aBagSPJavaOrchestration);
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
		if (logger.isInfoEnabled())
			logger.logInfo("retorno ProcedureResponse " + ret.toString());
		return ret;

	}

	// GetMaxSavings
	private boolean getMaxSaving(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationSaving");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationSavingResponse simulatorSavingResponse = new SimulationSavingResponse();
		try {
			SimulationSavingRequest simulationSavingRequest = transformRequestToDtoMaxSaving(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationSavingRequest.toString());
			simulatorSavingResponse = coreServiceSimulationSaving.getSimulationMaxSaving(simulationSavingRequest);

			wProcedureResponse = transformDtoToResponseMaxSaving(simulatorSavingResponse, aBagSPJavaOrchestration);
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

	};
	// GetSavings

	private boolean getSaving(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationSaving");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();// initProcedureResponse(anOriginalRequest);
		SimulationSavingResponse simulatorSavingResponse = new SimulationSavingResponse();
		try {
			SimulationSavingRequest simulationSavingRequest = transformRequestToDtoSaving(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationSavingRequest.toString());
			simulatorSavingResponse = coreServiceSimulationSaving.getSimulationSaving(simulationSavingRequest);

			wProcedureResponse = transformDtoToResponseSaving(simulatorSavingResponse, aBagSPJavaOrchestration);
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

	};
	// GetExecuteSaving

	private boolean getExecuteSaving(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationSaving");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationSavingResponse simulatorSavingResponse = new SimulationSavingResponse();
		try {
			SimulationSavingRequest simulationSavingRequest = transformRequestToDtoExecuteSaving(
					aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request" + simulationSavingRequest.toString());
			simulatorSavingResponse = coreServiceSimulationSaving.getSimulationExecuteSaving(simulationSavingRequest);

			wProcedureResponse = transformDtoToResponseExecuteSaving(simulatorSavingResponse, aBagSPJavaOrchestration);
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

	};

	/*********************
	 * Transformación de Request a SimulationMaxSavingRequest
	 ***********************/

	private SimulationSavingRequest transformRequestToDtoMaxSaving(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoMaxSavings");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationSavingRequest simulationSavingRequest = new SimulationSavingRequest();
		SimulationSaving simulationSaving = new SimulationSaving();
		simulationSaving.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));
		simulationSaving.setInitialAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_ini")));
		simulationSaving.setEntityType(wOriginalRequest.readValueParam("@i_tipocta"));
		simulationSaving.setCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod_banc")));
		simulationSaving.setCategory(wOriginalRequest.readValueParam("@i_categoria"));
		simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));

		simulationSavingRequest.setSimulationSaving(simulationSaving);
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationMaxSaving " + aBagSPJavaOrchestration);
		simulationSavingRequest.setOriginalRequest(wOriginalRequest);
		return simulationSavingRequest;
	}

	/*********************
	 * Transformación de Request a SimulationSavingRequest
	 ***********************/

	private SimulationSavingRequest transformRequestToDtoSaving(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoSaving");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationSavingRequest simulationSavingRequest = new SimulationSavingRequest();
		SimulationSaving simulationSaving = new SimulationSaving();

		simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));
		simulationSavingRequest.setSimulationSaving(simulationSaving);

		simulationSavingRequest.setEntityType(wOriginalRequest.readValueParam("@i_tipo_ente"));
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationSaving " + aBagSPJavaOrchestration);
		simulationSavingRequest.setOriginalRequest(wOriginalRequest);
		return simulationSavingRequest;

	}

	/*********************
	 * Transformación de Request a SimulationExecuteSavingRequest
	 ***********************/
	private SimulationSavingRequest transformRequestToDtoExecuteSaving(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoSaving");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationSavingRequest simulationSavingRequest = new SimulationSavingRequest();
		SimulationSaving simulationSaving = new SimulationSaving();
		simulationSaving.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));
		simulationSaving.setInitialAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_ini")));
		simulationSaving.setEntityType(wOriginalRequest.readValueParam("@i_tipocta"));
		simulationSaving.setCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod_banc")));
		simulationSaving.setCategory(wOriginalRequest.readValueParam("@i_categoria"));
		simulationSaving.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_periodo")));
		simulationSaving.setFinalAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_aprox")));
		simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));

		simulationSavingRequest.setSimulationSaving(simulationSaving);
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoSimulationExecuteSaving " + aBagSPJavaOrchestration);
		simulationSavingRequest.setOriginalRequest(wOriginalRequest);
		return simulationSavingRequest;
	}

	/*********************
	 * Transformación de MaxSavingResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponseMaxSaving(SimulationSavingResponse simulatorSavingResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida CdTypeResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();

		if (simulatorSavingResponse.getReturnCode() == 0) {

			if (simulatorSavingResponse != null) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MaxAcount", ICTSTypes.SQLVARCHAR, 30));

				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getMaxAmount().toString()));
				data.addRow(row);

				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

			wResponse.setReturnCode(simulatorSavingResponse.getReturnCode());
		} else {
			wResponse = Utils.returnException(simulatorSavingResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}
		return wResponse;
	}

	/*********************
	 * Transformación de SavingResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponseSaving(SimulationSavingResponse simulatorSavingResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					CLASS_NAME + "Transformando Dto de Salida SavingResponse :" + simulatorSavingResponse.toString());
		IProcedureResponse wResponse = new ProcedureResponseAS();// initProcedureResponse((IProcedureRequest)
																	// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();

		if (simulatorSavingResponse.getReturnCode() == 0) {

			if (simulatorSavingResponse != null) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Code", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Description", ICTSTypes.SQLVARCHAR, 80));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Category", ICTSTypes.SQLVARCHAR, 60));

				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getDescription().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getCategory().toString()));
				data.addRow(row);

				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

			wResponse.setReturnCode(simulatorSavingResponse.getReturnCode());
		} else {
			wResponse = Utils.returnException(simulatorSavingResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}
		return wResponse;
	}

	/*********************
	 * Transformación de ExecuteSavingResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponseExecuteSaving(SimulationSavingResponse simulatorSavingResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida SavingResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();

		if (simulatorSavingResponse.getReturnCode() == 0) {

			if (simulatorSavingResponse != null) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FinalAmount", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Rate", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Term", ICTSTypes.SQLVARCHAR, 30));

				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getFinalAmount().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getRate().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						simulatorSavingResponse.getSimulationSaving().getTerm().toString()));
				data.addRow(row);

				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

			wResponse.setReturnCode(simulatorSavingResponse.getReturnCode());
			if (logger.isInfoEnabled())
				logger.logInfo("CODIGO DE RETORNO TYPE: " + simulatorSavingResponse.getReturnCode());
			// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
			// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		} else {
			wResponse = Utils.returnException(simulatorSavingResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}
		return wResponse;
	}
}
