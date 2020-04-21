package com.cobiscorp.ecobis.orchestration.core.ib.execute.customservices;

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
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
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
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CustomServices;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCustomServices;

@Component(name = "CustomServicesOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CustomServicesOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CustomServicesOrchestrationCore") })

public class CustomServicesOrchestrationCore extends QueryBaseTemplate {

	ILogger logger = LogFactory.getLogger(CustomServicesOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceCustomServices.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCustomServices coreService;

	protected void bindCoreService(ICoreServiceCustomServices service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceCustomServices service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isDebugEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		CustomServicesResponse aCustomServicesResponse = new CustomServicesResponse();
		CustomServicesRequest aCustomServicesRequest = transformtOCustomServicesRequest(request.clone());
		try {
			messageError = "executeCustomServices: ERROR EXECUTING SERVICE";
			messageLog = "aCustomServicesRequest.getTrn() " + aCustomServicesRequest.getTrn();
			queryName = "aCustomServicesRequest.getTrn()";
			aCustomServicesRequest.setOriginalRequest(request);
			aCustomServicesResponse = coreService.searchCustomServicesAdmin(aCustomServicesRequest);
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

		return transformProcedureResponse(aCustomServicesResponse, aBagSPJavaOrchestration);
	}

	private CustomServicesRequest transformtOCustomServicesRequest(IProcedureRequest aRequest) {
		CustomServicesRequest aCustomServicesRequest = new CustomServicesRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform-> " + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@t_trn") == null ? " - @t_trn can't be null" : "";
		messageError += aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_modo") == null ? " - @i_modo can't be null" : "";
		messageError += aRequest.readValueParam("@s_term") == null ? " - @s_term can't be null" : "";
		messageError += aRequest.readValueParam("@s_ofi") == null ? " - @s_ofi can't be null" : "";
		messageError += aRequest.readValueParam("@s_rol") == null ? " - @s_rol can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aCustomServicesRequest.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));
		aCustomServicesRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		aCustomServicesRequest.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));
		if (aRequest.readValueParam("@i_nemonico") != null)
			aCustomServicesRequest.setNemonic(aRequest.readValueParam("@i_nemonico"));

		if (aRequest.readValueParam("@i_codigo") != null) {
			aCustomServicesRequest.setCode(Integer.parseInt(aRequest.readValueParam("@i_codigo")));
			aCustomServicesRequest.setSsesn(Integer.parseInt(aRequest.readValueParam("@s_sesn")));
			aCustomServicesRequest.setSssn(Integer.parseInt(aRequest.readValueParam("@s_ssn")));
			aCustomServicesRequest.setSdate(aRequest.readValueParam("@s_date"));
			aCustomServicesRequest.setSorg(aRequest.readValueParam("@s_org"));
		}
		aCustomServicesRequest.setTerminal(aRequest.readValueParam("@s_term"));
		aCustomServicesRequest.setOffice(Integer.parseInt(aRequest.readValueParam("@s_ofi")));
		aCustomServicesRequest.setRol(Integer.parseInt(aRequest.readValueParam("@s_rol")));

		return aCustomServicesRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(CustomServicesResponse aCustomServicesResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aCustomServicesResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aCustomServicesResponse.getMessages())); // COLOCA
																					// ERRORES
																					// COMO
																					// RESPONSE
																					// DE
																					// LA
																					// TRANSACCIÓN
			Utils.returnException(aCustomServicesResponse.getMessages());
		} else {
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NEMONICO", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SERVICIO", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("COSTOINTERNO", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NoRUBROS", ICTSTypes.SQLINT4, 10));

			for (CustomServices aCustomServices : aCustomServicesResponse.getCustomServicesCollection()) {
				if (!IsValidCustomServicesResponse(aCustomServices))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						aCustomServices.getCodeService() != null ? aCustomServices.getCodeService().toString() : "0"));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aCustomServices.getNemonic() != null ? aCustomServices.getNemonic() : ""));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aCustomServices.getDescription() != null ? aCustomServices.getDescription() : ""));
				row.addRowData(4, new ResultSetRowColumnData(false,
						aCustomServices.getState() != null ? aCustomServices.getState() : ""));
				row.addRowData(5, new ResultSetRowColumnData(false, aCustomServices.getInternalCost() != null
						? aCustomServices.getInternalCost().toString() : "0"));
				row.addRowData(6, new ResultSetRowColumnData(false,
						aCustomServices.getItemNumber() != null ? aCustomServices.getItemNumber().toString() : "0"));

				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			wProcedureResponse.setReturnCode(aCustomServicesResponse.getReturnCode());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidCustomServicesResponse(CustomServices aCustomServices) {
		String messageError = null;

		messageError = aCustomServices.getCodeService() == null ? "Code Service can't be null" : "";
		messageError += aCustomServices.getNemonic() == null ? "Nemonic can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "SERVICIOS PERSONALIZADOS");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST),
				wProcedureRespFinal);
		return wProcedureRespFinal;
	}

}
