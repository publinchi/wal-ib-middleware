package com.cobiscorp.ecobis.orchestration.core.ib.execute.causeandcost;

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
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CauseAndCost;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCauseAndCost;

/**
 * 
 * @author itorres
 * @since Ago 06, 2014
 * @version 1.0.0
 */

@Component(name = "CauseAndCostOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CauseAndCostOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CauseAndCostOrchestrationCore") })

public class CauseAndCostOrchestrationCore extends QueryBaseTemplate {

	private String Operacion;

	ILogger logger = LogFactory.getLogger(CauseAndCostOrchestrationCore.class);

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

	@Reference(referenceInterface = ICoreServiceCauseAndCost.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCauseAndCost coreService;

	protected void bindCoreService(ICoreServiceCauseAndCost service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceCauseAndCost service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		CauseAndCostResponse aCauseAndCostResponse = new CauseAndCostResponse();
		CauseAndCostRequest aCauseAndCostRequest = transformToCauseAndCostRequestRequest(request.clone());

		try {
			messageError = "executeCauseAndCost: ERROR EXECUTING SERVICE";
			messageLog = "aCauseAndCostRequest.getProduct() " + aCauseAndCostRequest.getProduct();
			queryName = "aCauseAndCostRequest.getProduct()";
			aCauseAndCostRequest.setOriginalRequest(request);
			aCauseAndCostResponse = coreService.executeCauseAndCost(aCauseAndCostRequest);
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

		return transformProcedureResponse(aCauseAndCostResponse, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CAUSAS Y COSTOS DE OPERACIONES");
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

	/******************
	 * Transformación de ProcedureRequest a CauseAndCostRequest
	 ********************/
	private CauseAndCostRequest transformToCauseAndCostRequestRequest(IProcedureRequest aRequest) {
		CauseAndCostRequest aCauseAndCostRequest = new CauseAndCostRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform-> " + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@t_trn") == null ? " - @t_trn can't be null" : "";
		messageError += aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_transaccion") == null ? " - @i_transaccion can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aCauseAndCostRequest.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));
		aCauseAndCostRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		aCauseAndCostRequest.setTransaction(Integer.parseInt(aRequest.readValueParam("@i_transaccion")));

		if (!aRequest.readValueParam("@i_operacion").equals("S")
				&& !aRequest.readValueParam("@i_operacion").equals("ST")) {
			aCauseAndCostRequest.setProduct(Integer.parseInt(aRequest.readValueParam("@i_producto")));
			aCauseAndCostRequest.setService(aRequest.readValueParam("@i_servicio"));
			aCauseAndCostRequest.setCause(aRequest.readValueParam("@i_causa"));
			aCauseAndCostRequest.setCostTransaction(aRequest.readValueParam("@i_costo_transaccion"));
		}

		if (aRequest.readValueParam("@i_operacion").equals("R")) {
			aCauseAndCostRequest.setTransBefore(Integer.parseInt(aRequest.readValueParam("@i_tran_ant")));
			aCauseAndCostRequest.setType(aRequest.readValueParam("@i_tipo"));
			aCauseAndCostRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		}

		Operacion = aRequest.readValueParam("@i_operacion");

		return aCauseAndCostRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(CauseAndCostResponse aCauseAndCostResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");
		Integer colum; // Acumula el orden sucesivo de las columnas

		if (aCauseAndCostResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aCauseAndCostResponse.getMessages())); // COLOCA
																					// ERRORES
																					// COMO
																					// RESPONSE
																					// DE
																					// LA
																					// TRANSACCIÓN
			Utils.returnException(aCauseAndCostResponse.getMessages());
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo("Tranforma el procedure");
			if (Operacion.equals("S") || Operacion.equals("ST")) {
				// Agregar Header
				IResultSetHeader metaData = new ResultSetHeader();
				IResultSetData data = new ResultSetData();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("SERVICIODISPONIBLE", ICTSTypes.SQLVARCHAR, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLINT4, 4));
				if (Operacion.equals("S"))
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("SERVICIODESCRIPCION", ICTSTypes.SQLVARCHAR, 10));

				metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTODESCRIPCION", ICTSTypes.SQLVARCHAR, 1));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("CAUSA", ICTSTypes.SQLVARCHAR, 64));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA REG", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA MODIF", ICTSTypes.SQLVARCHAR, 5));

				for (CauseAndCost aCauseAndCost : aCauseAndCostResponse.getCauseAndCostCollection()) {
					if (!IsValidCauseAndCostResponse(aCauseAndCost))
						return null;

					IResultSetRow row = new ResultSetRow();
					if (logger.isInfoEnabled())
						logger.logInfo("aCauseAndCost.getService() ==> " + aCauseAndCost.getService());
					row.addRowData(1, new ResultSetRowColumnData(false,
							aCauseAndCost.getService() != null ? aCauseAndCost.getService() : ""));
					if (logger.isInfoEnabled())
						logger.logInfo("aCauseAndCost.getProduct() ==> " + aCauseAndCost.getProduct());
					row.addRowData(2, new ResultSetRowColumnData(false,
							aCauseAndCost.getProduct() != null ? aCauseAndCost.getProduct().toString() : "0"));
					colum = 3;
					if (Operacion.equals("S")) {
						if (logger.isInfoEnabled())
							logger.logInfo("aCauseAndCost.getDescriptionService() ==> "
									+ aCauseAndCost.getDescriptionService());
						row.addRowData(colum,
								new ResultSetRowColumnData(false, aCauseAndCost.getDescriptionService() != null
										? aCauseAndCost.getDescriptionService() : ""));
						colum += 1;
					}
					if (logger.isInfoEnabled())
						logger.logInfo(
								"aCauseAndCost.getDescriptionProduct() ==> " + aCauseAndCost.getDescriptionProduct());
					row.addRowData(colum,
							new ResultSetRowColumnData(false, aCauseAndCost.getDescriptionProduct() != null
									? aCauseAndCost.getDescriptionProduct() : ""));
					colum += 1;
					if (logger.isInfoEnabled())
						logger.logInfo("aCauseAndCost.getCause() ==> " + aCauseAndCost.getCause());
					row.addRowData(colum, new ResultSetRowColumnData(false,
							aCauseAndCost.getCause() != null ? aCauseAndCost.getCause() : ""));
					colum += 1;
					if (logger.isInfoEnabled())
						logger.logInfo("aCauseAndCost.getType() ==> " + aCauseAndCost.getType());
					row.addRowData(colum, new ResultSetRowColumnData(false,
							aCauseAndCost.getType() != null ? aCauseAndCost.getType() : ""));
					colum += 1;
					if (logger.isInfoEnabled())
						logger.logInfo("aCauseAndCost.getCreationDate() ==> " + aCauseAndCost.getCreationDate());
					row.addRowData(colum, new ResultSetRowColumnData(false,
							aCauseAndCost.getCreationDate() != null ? aCauseAndCost.getCreationDate() : ""));
					colum += 1;
					if (logger.isInfoEnabled())
						logger.logInfo(
								"aCauseAndCost.getModificationDate() ==> " + aCauseAndCost.getModificationDate());
					row.addRowData(colum, new ResultSetRowColumnData(false,
							aCauseAndCost.getModificationDate() != null ? aCauseAndCost.getModificationDate() : ""));

					data.addRow(row);
				}
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				wProcedureResponse.addResponseBlock(resultBlock);
			}
			wProcedureResponse.setReturnCode(aCauseAndCostResponse.getReturnCode());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	// Valida que la data retornada no sea nula
	private boolean IsValidCauseAndCostResponse(CauseAndCost aCauseAndCost) {
		String messageError = null;

		messageError = aCauseAndCost.getService() == null ? "Service can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}
}
