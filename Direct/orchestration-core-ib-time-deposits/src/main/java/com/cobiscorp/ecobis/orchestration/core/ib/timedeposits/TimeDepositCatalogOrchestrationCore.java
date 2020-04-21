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
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositCatalog;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author jveloz
 *
 */
@Component(name = "TimeDepositCatalogOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositCatalogOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositCatalogOrchestrationCore") })
public class TimeDepositCatalogOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceTimeDeposits.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDeposits coreServiceTimeDeposit;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = null;
	}

	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		TimeDepositCatalogResponse aTimeDepositCatalogResponse = null;
		TimeDepositCatalogRequest aTimeDepositCatalogRequest = transformTimeDepositCatalogRequest(request.clone());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "getTimeDepositCatalog: " + aTimeDepositCatalogRequest.getTypePerson();
			queryName = "getTimeDepositCatalog";

			aTimeDepositCatalogRequest.setOriginalRequest(request);

			aTimeDepositCatalogResponse = coreServiceTimeDeposit.getTimeDepositCatalog(aTimeDepositCatalogRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		return transformProcedureResponse(aTimeDepositCatalogResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDeposit", coreServiceTimeDeposit);
		Utils.validateComponentInstance(mapInterfaces);
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO> anOrginalRequest" + anOrginalRequest);
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a TimeDepositRequest
	 ********************/

	private TimeDepositCatalogRequest transformTimeDepositCatalogRequest(IProcedureRequest aRequest) {
		TimeDepositCatalogRequest wTimeDepositCatalogRequest = new TimeDepositCatalogRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_tipo_persona") == null ? " - @i_tipo_persona can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wTimeDepositCatalogRequest.setTypePerson(aRequest.readValueParam("@i_tipo_persona"));

		return wTimeDepositCatalogRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(TimeDepositCatalogResponse aTimeDepositCatalogResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		if (aTimeDepositCatalogResponse.getReturnCode() == 0) {

			metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 100));

			for (TimeDepositCatalog timeDepositCatalog : aTimeDepositCatalogResponse
					.getCollectionTimeDepositCatalog()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, timeDepositCatalog.getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, timeDepositCatalog.getDescription()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(aTimeDepositCatalogResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

}
