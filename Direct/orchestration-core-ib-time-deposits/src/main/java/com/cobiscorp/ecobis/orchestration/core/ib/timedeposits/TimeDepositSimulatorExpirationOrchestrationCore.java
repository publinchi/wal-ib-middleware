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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDepositConfig;

@Component(name = "TimeDepositSimulatorExpirationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "TimeDepositSimulatorExpirationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositSimulatorExpirationOrchestrationCore") })

public class TimeDepositSimulatorExpirationOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TimeDepositSimulatorSettingsOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceTimeDepositConfig.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDepositConfig coreServiceSimulationExpiration;

	protected void bindCoreService(ICoreServiceTimeDepositConfig service) {
		coreServiceSimulationExpiration = service;
	}

	protected void unbindCoreService(ICoreServiceTimeDepositConfig service) {
		coreServiceSimulationExpiration = null;
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
		mapInterfaces.put("coreServiceSimulationExpiration", coreServiceSimulationExpiration);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);

		Map<String, Object> wprocedureResponse1 = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);
		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");
		if (wErrorProcedureResponse != null) {
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
			logger.logInfo(CLASS_NAME + "getSimulationExpiration");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = getExpirationDate(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	private boolean getExpirationDate(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulationExpiration");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationExpirationResponse simulatorSetResponse = new SimulationExpirationResponse();

		try {
			SimulationExpirationRequest simulationExpirationRequest = transformRequestToDto(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("Se muestra Request Maggy" + simulationExpirationRequest.toString());
			simulatorSetResponse = coreServiceSimulationExpiration.getSimulationExpiration(simulationExpirationRequest);

			wProcedureResponse = transformDtoToResponse(simulatorSetResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_SIMULATOR_RESPONSE", wProcedureResponse);

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
	 * Transformación de Request a SimulationExpirationRequest
	 ***********************/

	private SimulationExpirationRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		SimulationExpirationRequest simulationExpiration = new SimulationExpirationRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		// TransactionRequest transactionRequest = new TransactionRequest();
		if (logger.isInfoEnabled())
			logger.logInfo(wOriginalRequest);

		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_toperacion"));
		if (wOriginalRequest.readValueParam("@i_dias_reales") != null) {
			certificateDeposit.setCalendarDays(wOriginalRequest.readValueParam("@i_dias_reales"));
		}
		certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));
		certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha"));

		if (wOriginalRequest.readValueParam("@i_formato_fecha") != null) {
			simulationExpiration.setDateFormat(Integer.parseInt(wOriginalRequest.readValueParam("@i_formato_fecha")));
		}

		simulationExpiration.setCertificateDeposit(certificateDeposit);
		simulationExpiration.setOriginalRequest(wOriginalRequest);

		return simulationExpiration;
	}

	/*********************
	 * Transformación de SimulationExpirationResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse(SimulationExpirationResponse simulatorSetResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + simulatorSetResponse.toString());
		IProcedureResponse pResponse = new ProcedureResponseAS();// initProcedureResponse((IProcedureRequest)
																	// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		// IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		// Type
		if (simulatorSetResponse != null && simulatorSetResponse.getExpirationDate() != null) {
			metaData = new ResultSetHeader();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("EXPIRATIONDATEHOLD", ICTSTypes.SQLVARCHAR, 20));

			row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, simulatorSetResponse.getExpirationDate()));
			data.addRow(row);

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			pResponse.addResponseBlock(resultBlock1);
		}
		;

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transform Final Maggy1" + pResponse.toString());

		if (simulatorSetResponse != null && simulatorSetResponse.getSimulationExpiration() != null) {
			IResultSetHeader metaData2 = new ResultSetHeader();
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("RESULT", ICTSTypes.SQLVARCHAR, 20));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("ADDITIONALDAYS", ICTSTypes.SQLVARCHAR, 30));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("PROCESSDATE", ICTSTypes.SQLVARCHAR, 30));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("EXPIRATIONDATE", ICTSTypes.SQLVARCHAR, 30));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("PROCESSDATEHOLD", ICTSTypes.SQLVARCHAR, 30));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("EXPIRATIONDATEHOLD", ICTSTypes.SQLVARCHAR, 30));
			metaData2.addColumnMetaData(new ResultSetHeaderColumn("TERMHOLD", ICTSTypes.SQLVARCHAR, 30));

			IResultSetRow row2 = new ResultSetRow();
			IResultSetData data2 = new ResultSetData();
			row2.addRowData(1,
					new ResultSetRowColumnData(false, simulatorSetResponse.getSimulationExpiration().getResult()));
			row2.addRowData(2, new ResultSetRowColumnData(false,
					simulatorSetResponse.getSimulationExpiration().getAdditionalDays().toString()));
			row2.addRowData(3,
					new ResultSetRowColumnData(false, simulatorSetResponse.getSimulationExpiration().getProcessDate()));
			row2.addRowData(4, new ResultSetRowColumnData(false,
					simulatorSetResponse.getSimulationExpiration().getExpirationDate()));
			row2.addRowData(5, new ResultSetRowColumnData(false,
					simulatorSetResponse.getSimulationExpiration().getProcessDateHold()));
			row2.addRowData(6, new ResultSetRowColumnData(false,
					simulatorSetResponse.getSimulationExpiration().getExpirationDateHold()));
			row2.addRowData(7, new ResultSetRowColumnData(false,
					simulatorSetResponse.getSimulationExpiration().getTermHold().toString()));
			data2.addRow(row2);

			IResultSetBlock resultBlock2 = new ResultSetBlock(metaData2, data2);
			pResponse.addResponseBlock(resultBlock2);

		}

		pResponse.addParam("@o_num_dias_labor", ICTSTypes.SQLINT4, 0,
				simulatorSetResponse.getSimulationExpiration().getNumberOfLaborsDays().toString());

		pResponse.setReturnCode(simulatorSetResponse.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo("*******************CODIGO DE RETORNO: " + simulatorSetResponse.getReturnCode().toString());

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO

		if (simulatorSetResponse.getReturnCode() != 0) {
			pResponse = Utils.returnException(simulatorSetResponse.getMessages());
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transform Final Maggy" + pResponse.toString());
		return pResponse;
	}

}
