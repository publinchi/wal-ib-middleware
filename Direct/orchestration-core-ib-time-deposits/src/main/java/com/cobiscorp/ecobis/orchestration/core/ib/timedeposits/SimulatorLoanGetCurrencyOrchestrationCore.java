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
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CurrencyDefinition;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCurrencyDef;

/**
 * @author jchonillo
 *
 */

@Component(name = "SimulatorLoanGetCurrencyOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SimulatorLoanGetCurrencyOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SimulatorLoanGetCurrencyOrchestrationCore") })

public class SimulatorLoanGetCurrencyOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(SimulatorLoanGetCurrencyOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceCurrencyDef.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCurrencyDef coreServiceCurrencyDef;

	protected void bindCoreService(ICoreServiceCurrencyDef service) {
		coreServiceCurrencyDef = service;
	}

	protected void unbindCoreService(ICoreServiceCurrencyDef service) {
		coreServiceCurrencyDef = null;
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
		mapInterfaces.put("coreServiceCurrencyDef", coreServiceCurrencyDef);
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
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation = getCurrencyHelp(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);
		if (logger.isDebugEnabled())
			logger.logDebug("result execution operation 1 operacion H: " + wSuccessExecutionOperation);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("ErrorProcedureResponse", aBagSPJavaOrchestration.get("ErrorProcedureResponse"));
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	private boolean getCurrencyHelp(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "getCurrencyHelp");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CurrencyDefinitionResponse currencyDefinitionResponse = new CurrencyDefinitionResponse();

		try {
			CurrencyDefinitionRequest currencyDefinitionRequest = transformRequestToDtoCurrency(
					aBagSPJavaOrchestration);
			currencyDefinitionResponse = coreServiceCurrencyDef.getCurrencyHelp(currencyDefinitionRequest);

			wProcedureResponse = transformToDtoResponse(currencyDefinitionResponse, aBagSPJavaOrchestration);
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
	 * Transformación de CurrencyDefinitionResponse a Response
	 ***********************/

	private IProcedureResponse transformToDtoResponse(CurrencyDefinitionResponse currencyDefinitionResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida LoansResponse: " + currencyDefinitionResponse);
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		// if (simulationLoanResponse.getReturnCode() != null){
		if (currencyDefinitionResponse.getReturnCode() == 0) {

			if (currencyDefinitionResponse.getListCurrencyDef().size() > 0) {
				metaData = new ResultSetHeader();
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Codigo", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Moneda", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("Simbolo", ICTSTypes.SQLVARCHAR, 30));
				for (CurrencyDefinition obj : currencyDefinitionResponse.getListCurrencyDef()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getCode().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescription()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getSymbol()));
					data.addRow(row);
				}
				resultBlock = new ResultSetBlock(metaData, data);
				wResponse.addResponseBlock(resultBlock);
			}

			wResponse.setReturnCode(currencyDefinitionResponse.getReturnCode());
		} else {
			wResponse = Utils.returnException(currencyDefinitionResponse.getMessages());
			aBagSPJavaOrchestration.put("ErrorProcedureResponse", wResponse);
		}

		return wResponse;
	}

	/*********************
	 * Transformación de Request a CurrencyDefinitionRequest
	 ***********************/

	private CurrencyDefinitionRequest transformRequestToDtoCurrency(Map<String, Object> aBagSPJavaOrchestration) {

		// if (logger.isInfoEnabled())

		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		CurrencyDefinitionRequest currencyDefinitionRequest = new CurrencyDefinitionRequest();
		currencyDefinitionRequest.setOriginalRequest(wOriginalRequest);
		currencyDefinitionRequest.setMode(Integer.parseInt(wOriginalRequest.readValueParam("@i_modo")));
		return currencyDefinitionRequest;

	}

}