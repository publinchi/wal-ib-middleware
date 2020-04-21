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
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSettingsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationSettings;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSimulatorSettings;

@Component(name = "TimeDepositSimulatorSettingsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositSimulatorSettingsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositSimulatorSettingsOrchestrationCore") })
public class TimeDepositSimulatorSettingsOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TimeDepositSimulatorSettingsOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceSimulatorSettings.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceSimulatorSettings coreServiceSimulationSettings;

	protected void bindCoreService(ICoreServiceSimulatorSettings service) {
		coreServiceSimulationSettings = service;
	}

	protected void unbindCoreService(ICoreServiceSimulatorSettings service) {
		coreServiceSimulationSettings = null;
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
		mapInterfaces.put("coreServiceSimulationSettings", coreServiceSimulationSettings);
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
			logger.logInfo(CLASS_NAME + "getSimulatorSettings");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = getSimulatorSettings(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	protected boolean getSimulatorSettings(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getSimulatorSettings");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		SimulationSettingsResponse simulatorSetResponse = new SimulationSettingsResponse();
		try {
			CertificateDepositCommonRequest certificateRequest = transformRequestToDto(aBagSPJavaOrchestration);

			simulatorSetResponse = coreServiceSimulationSettings.getSimulatorsettings(certificateRequest);

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
	 * Transformación de Request a CertificateDepositCommonRequest
	 ***********************/

	private CertificateDepositCommonRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();

		certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_money")));
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_cd_nemonic"));

		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);
		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	}

	/*********************
	 * Transformación de SimulationSettingsResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse(SimulationSettingsResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + response.toString());
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;

		IResultSetData data = new ResultSetData();
		if (response.getReturnCode() == 0) {

			if (response != null && response.getSimulationSettings().size() > 0) {
				metaData = new ResultSetHeader();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("MINDPF", ICTSTypes.SQLMONEY, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MAXDAYDPF", ICTSTypes.SQLINT4, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MINDAYDPF", ICTSTypes.SQLINT4, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MINLOAN", ICTSTypes.SQLMONEY, 5));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MAXLOAN", ICTSTypes.SQLMONEY, 5));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MAXTERMLOAN", ICTSTypes.SQLINT4, 5));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MAXTERMSAVING", ICTSTypes.SQLINT4, 5));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MINMONPF", ICTSTypes.SQLMONEY, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MINTERMSAVING", ICTSTypes.SQLINT4, 5));

				for (SimulationSettings obj : response.getSimulationSettings()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getMinCd().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getMaxDayCd().toString()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getMinDayCd().toString()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getMinLoan().toString()));
					row.addRowData(5, new ResultSetRowColumnData(false, obj.getMaxLoan().toString()));
					row.addRowData(6, new ResultSetRowColumnData(false, obj.getMaxTermLoan().toString()));
					row.addRowData(7, new ResultSetRowColumnData(false, obj.getMaxTermSaving().toString()));
					row.addRowData(8, new ResultSetRowColumnData(false, obj.getMiMonPf().toString()));
					row.addRowData(9, new ResultSetRowColumnData(false, obj.getMinTermSaving().toString()));
					data.addRow(row);
				}
				resultBlock = new ResultSetBlock(metaData, data);
				pResponse.addResponseBlock(resultBlock);
			}
		} else {
			pResponse = Utils.returnException(response.getMessages());
		}
		return pResponse;
	}

}
