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
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRelationsRequest;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

@Component(name = "CheckbookValidateRelationsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "CheckbookValidateRelationsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookValidateRelationsQueryOrchestrationCore") })

public class CheckbookValidateRelationsQueryOrchestrationCore extends SPJavaOrchestrationBase {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCheckbook coreServiceCheckbook;
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String CLASS_NAME = "--->";
	ILogger logger = LogFactory.getLogger(CheckbookValidateRelationsQueryOrchestrationCore.class);

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

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
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
		IProcedureResponse wIProcedureResponse = new ProcedureResponseAS();

		mapInterfaces.put("coreServiceCheckbook", coreServiceCheckbook);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);

		ServerRequest request = new ServerRequest();
		ServerResponse responseServer = new ServerResponse();
		try {
			responseServer = coreServer.getServerStatus(request);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");

			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");

			return null;
		}

		if (responseServer.getOnLine()) {
			Map<String, Object> wprocedureResponse = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);

			Boolean wSuccessExecutionOperation = (Boolean) wprocedureResponse.get("SuccessExecutionOperation");
			if (logger.isDebugEnabled())
				logger.logDebug("wSuccessExecutionOperation " + wSuccessExecutionOperation.toString());

			wIProcedureResponse = (IProcedureResponse) wprocedureResponse.get("IProcedureResponse");
			if (logger.isDebugEnabled())
				logger.logDebug("wIProcedureResponse " + wIProcedureResponse);

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
		} else {
			// wIProcedureResponse = (IProcedureResponse)
			// aBagSPJavaOrchestration.get("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE");
			IResultSetData data = new ResultSetData();
			IResultSetRow row = new ResultSetRow();
			IResultSetHeader metaData = new ResultSetHeader();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT4, 11));
			row.addRowData(1, new ResultSetRowColumnData(false, "1"));

			data.addRow(row);
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wIProcedureResponse.addResponseBlock(resultBlock);

			wIProcedureResponse.setReturnCode(0);
			return wIProcedureResponse;
		}
		return wIProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation = executeValidateAccountsRelations(anOriginalRequest,
				aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation);

		IProcedureResponse wProcedureResponseOperation = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		wProcedureResponseOperation = (IProcedureResponse) aBagSPJavaOrchestration
				.get("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE");
		ret.put("IProcedureResponse", wProcedureResponseOperation);

		return ret;
	}

	protected boolean executeValidateAccountsRelations(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeValidateAccountsRelations");

		try {
			boolean validate;
			IProcedureResponse pResponse = new ProcedureResponseAS();
			ValidationAccountsRelationsRequest validationAccountsRelationsRequest = transformRequestToDto(
					anOriginalRequest.clone());
			// ValidationAccountsRelationsRequest
			// validationAccountsRelationsRequest =
			// transformRequestToDto(aBagSPJavaOrchestration);
			pResponse = coreServiceCheckbook.validateAccountsRelations(validationAccountsRelationsRequest);
			aBagSPJavaOrchestration.put("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE", pResponse);

			Integer response = pResponse.getReturnCode();
			if (response == 0) {
				validate = true;
				return validate;
			} else {
				validate = false;
				return validate;
			}
			/*
			 * validate = coreServiceCheckbook.validateAccountsRelations(
			 * validationAccountsRelationsRequest); return validate;
			 */

		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("VALIDATE_ACCOUNTS_RELATIONS_RESPONSE", null);
			return false;
		}

	}

	private ValidationAccountsRelationsRequest transformRequestToDto(IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		// IProcedureRequest wOriginalRequest = (ProcedureRequestAS)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		ValidationAccountsRelationsRequest validationAccountsRelationsRequest = new ValidationAccountsRelationsRequest();
		validationAccountsRelationsRequest
				.setEntityId(Integer.parseInt(anOriginalRequest.readValueParam("@i_cliente")));
		validationAccountsRelationsRequest.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));

		validationAccountsRelationsRequest.setOriginalRequest(anOriginalRequest);

		return validationAccountsRelationsRequest;
	}
}
