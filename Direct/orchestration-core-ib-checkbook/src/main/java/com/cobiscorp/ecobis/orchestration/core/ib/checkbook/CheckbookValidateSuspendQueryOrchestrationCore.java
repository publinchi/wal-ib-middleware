package com.cobiscorp.ecobis.orchestration.core.ib.checkbook;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

@Component(name = "CheckbookValidateSuspendQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CheckbookValidateSuspendQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookValidateSuspendQueryOrchestrationCore") })

public class CheckbookValidateSuspendQueryOrchestrationCore extends SPJavaOrchestrationBase {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCheckbook coreServiceCheckbook;
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String CLASS_NAME = "--->";
	ILogger logger = LogFactory.getLogger(CheckbookValidateSuspendQueryOrchestrationCore.class);

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceCheckbook service) {
		coreServiceCheckbook = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceCheckbook service) {
		coreServiceCheckbook = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceCheckbook", coreServiceCheckbook);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);

		Map<String, Object> wprocedureResponse = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);

		Boolean wSuccessExecutionOperation = (Boolean) wprocedureResponse.get("SuccessExecutionOperation");
		if (logger.isDebugEnabled())
			logger.logDebug("wSuccessExecutionOperation " + wSuccessExecutionOperation.toString());

		IProcedureResponse wIProcedureResponse = (IProcedureResponse) wprocedureResponse.get("IProcedureResponse");
		if (logger.isDebugEnabled())
			logger.logDebug("wIProcedureResponse " + wIProcedureResponse.toString());

		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse
				.get("ErrorProcedureResponse");
		if (logger.isDebugEnabled())
			logger.logDebug("wErrorProcedureResponse " + wErrorProcedureResponse);

		if (wSuccessExecutionOperation) {
			return wIProcedureResponse;
		}
		if (wErrorProcedureResponse != null) {
			return wErrorProcedureResponse;
		}

		// wIProcedureResponse = (IProcedureResponse)
		// aBagSPJavaOrchestration.get("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE");
		return wIProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "validateSuspend");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation = executeValidateSuspend(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation: " + wSuccessExecutionOperation);

		IProcedureResponse wProcedureResponseOperation = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation);

		return ret;
	}

	protected boolean executeValidateSuspend(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeValidateAccountsRelations");

		try {
			IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
			CheckbookValidateSuspendRequest validateSuspendRequest = transformRequestToDto(anOriginalRequest.clone());

			CheckbookValidateSuspendResponse validateSuspendResponse = coreServiceCheckbook
					.validateSuspendCheckBook(validateSuspendRequest);

			wProcedureResponse = transformDtoToResponse(validateSuspendResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("VALIDATE_SUSPEND_RESPONSE", wProcedureResponse);

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "!wProcedureResponse.hasError() " + !wProcedureResponse.hasError());
			return !wProcedureResponse.hasError();

		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("VALIDATE_SUSPEND_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("VALIDATE_SUSPEND_RESPONSE", null);
			return false;
		}

	}

	private CheckbookValidateSuspendRequest transformRequestToDto(IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		// IProcedureRequest wOriginalRequest = (ProcedureRequestAS)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		CheckbookValidateSuspendRequest validateSuspendRequest = new CheckbookValidateSuspendRequest();
		validateSuspendRequest.setAccount(anOriginalRequest.readValueParam("@i_cuenta"));
		validateSuspendRequest.setInitialCheck(Integer.parseInt(anOriginalRequest.readValueParam("@i_cheque_inicio")));
		validateSuspendRequest.setNumberOfChecks(Integer.parseInt(anOriginalRequest.readValueParam("@i_cheque_fin")));

		validateSuspendRequest.setOriginalRequest(anOriginalRequest);
		return validateSuspendRequest;
	}

	private IProcedureResponse transformDtoToResponse(CheckbookValidateSuspendResponse validateSuspendResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + validateSuspendResponse.toString());
		// IProcedureResponse pResponse =
		// initProcedureResponse((IProcedureRequest)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();

		if (validateSuspendResponse != null && validateSuspendResponse.getChecks().size() > 0) {
			metaData = new ResultSetHeader();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("checkNumber", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 10));

			for (Check check : validateSuspendResponse.getChecks()) {
				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, check.getCheckNumber()));
				row.addRowData(2, new ResultSetRowColumnData(false, check.getAmount().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, check.getStatus()));
				data.addRow(row);
			}
			resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		}

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (validateSuspendResponse.getReturnCode() != 0) {
			wProcedureResponse = Utils.returnException(validateSuspendResponse.getMessages());
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida IProcedureResponse:"
					+ wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
