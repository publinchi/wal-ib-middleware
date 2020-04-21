package com.cobiscorp.ecobis.ib.orchestration.receiving.printing.archive;

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
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReceivingPrintingArchive;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;

/**
 * 
 * @author itorres
 * @since Ago 06, 2014
 * @version 1.0.0
 */

@Component(name = "GenerateInventoryLotOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GenerateInventoryLotOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateInventoryLotOrchestrationCore") })

public class GenerateInventoryLotOrchestrationCore extends QueryBaseTemplate {

	protected static String dbms;
	protected static IDBServiceFactory dbServiceFactory;

	ILogger logger = LogFactory.getLogger(GenerateInventoryLotOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceReceivingPrintingArchive.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceReceivingPrintingArchive coreService;

	protected void bindCoreService(ICoreServiceReceivingPrintingArchive service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceReceivingPrintingArchive service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		InventoryLotResponse aInventoryLotResponse = new InventoryLotResponse();
		InventoryLotRequest aInventoryLotRequest = transformToInventoryLotRequest(request.clone());

		try {
			messageError = "getBatchProcessing: ERROR EXECUTING SERVICE";
			messageLog = "getBatchProcessing " + aInventoryLotRequest.getLote();
			queryName = "getBatchProcessing";
			aInventoryLotRequest.setOriginalRequest(request);

			aInventoryLotResponse = coreService.getGenerateBatch(aInventoryLotRequest);
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

		IProcedureResponse response = transformProcedureResponse(aInventoryLotResponse, aBagSPJavaOrchestration);

		return response;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "GENERACION DE DATOS DE CLIENTES NUEVOS");
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
	 * Transformación de ProcedureRequest a InventoryLotRequest
	 ********************/
	private InventoryLotRequest transformToInventoryLotRequest(IProcedureRequest aRequest) {
		InventoryLotRequest aInventoryLotRequest = new InventoryLotRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_lote") == null ? " - @i_lote can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aInventoryLotRequest.setLote(Integer.parseInt(aRequest.readValueParam("@i_lote")));

		return aInventoryLotRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(InventoryLotResponse aInventoryLotResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aInventoryLotResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aInventoryLotResponse.getMessages())); // COLOCA
																					// ERRORES
																					// COMO
																					// RESPONSE
																					// DE
																					// LA
																					// TRANSACCIÓN
			Utils.returnException(aInventoryLotResponse.getMessages());
		}

		wProcedureResponse.addParam("@o_respuesta", ICTSTypes.SYBINT4, 1,
				aInventoryLotResponse.getResponse().toString());
		wProcedureResponse.setReturnCode(aInventoryLotResponse.getReturnCode());

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

}
