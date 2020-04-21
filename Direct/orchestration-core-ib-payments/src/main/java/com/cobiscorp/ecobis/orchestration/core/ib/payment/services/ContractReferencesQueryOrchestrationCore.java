package com.cobiscorp.ecobis.orchestration.core.ib.payment.services;

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
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ContractReferences;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContractReferences;

/**
 * 
 * @author jveloz
 *
 */
@Component(name = "ContractReferencesQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ContractReferencesQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ContractReferencesQueryOrchestrationCore") })
public class ContractReferencesQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceContractReferences.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceContractReferences coreServiceContract;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceContractReferences service) {
		coreServiceContract = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceContractReferences service) {
		coreServiceContract = null;
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

		ContractReferencesResponse wContractReferencesResponse = null;
		ContractReferencesRequest wContractReferencesRequest = transformContractReferencesRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + wContractReferencesRequest.getContractId());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");

			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "getContractReference: " + wContractReferencesRequest.getContractId();
			queryName = "getContractReference";
			// IProcedureResponse
			// aProcedureResponse=(IProcedureResponse)aBagSPJavaOrchestration.get(QueryBaseTemplate.RESPONSE_VALIDATE_LOCAL);
			wContractReferencesRequest.setOriginalRequest(request);
			// aContractReferencesRequest.setCustomerCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_cliente_mis")));
			wContractReferencesResponse = coreServiceContract.getContractReference(wContractReferencesRequest);
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

		return transformProcedureResponse(wContractReferencesResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceContract", coreServiceContract);
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

	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// se sobreescribe este medoto ya que no es utilizado jve
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	};

	/******************
	 * Transformación de ProcedureRequest a ContractReferencesRequest
	 ********************/

	private ContractReferencesRequest transformContractReferencesRequest(IProcedureRequest aRequest) {
		ContractReferencesRequest wContractReferencesRequest = new ContractReferencesRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform ContractReferencesRequest->"
					+ aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_convenio") == null ? " - @i_convenio can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		wContractReferencesRequest.setContractId(Integer.parseInt(aRequest.readValueParam("@i_convenio")));
		wContractReferencesRequest.setUserName(aRequest.readValueParam("@i_login"));
		return wContractReferencesRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(ContractReferencesResponse aContractReferencesResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("campo", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("etiqueta", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("tipo", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("habilitado", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("obligatorio", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("tipoDato", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("longitud", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("campoDefault", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("catalogo", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("redigitar", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("visible", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("orden", ICTSTypes.SQLVARCHAR, 30));
		// capturando Código ERROR
		wProcedureResponse.setReturnCode(aContractReferencesResponse.getReturnCode());
		if (aContractReferencesResponse.getReturnCode() == 0) {

			for (ContractReferences contractReferences : aContractReferencesResponse.getListContractLabel()) {
				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, contractReferences.getCampo()));
				row.addRowData(2, new ResultSetRowColumnData(false, contractReferences.getEtiqueta()));
				row.addRowData(3, new ResultSetRowColumnData(false, contractReferences.getTipo()));
				row.addRowData(4, new ResultSetRowColumnData(false, contractReferences.getHabilitado()));
				row.addRowData(5, new ResultSetRowColumnData(false, contractReferences.getObligatorio()));
				row.addRowData(6, new ResultSetRowColumnData(false, contractReferences.getTipoDato()));
				row.addRowData(7, new ResultSetRowColumnData(false, contractReferences.getLongitud()));
				row.addRowData(8, new ResultSetRowColumnData(false, contractReferences.getCampoDefault()));
				row.addRowData(9, new ResultSetRowColumnData(false, contractReferences.getCatalogo()));
				row.addRowData(10, new ResultSetRowColumnData(false, contractReferences.getRedigitar()));
				row.addRowData(11, new ResultSetRowColumnData(false, contractReferences.getVisible()));
				row.addRowData(12, new ResultSetRowColumnData(false, contractReferences.getOrden()));
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(aContractReferencesResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

}
