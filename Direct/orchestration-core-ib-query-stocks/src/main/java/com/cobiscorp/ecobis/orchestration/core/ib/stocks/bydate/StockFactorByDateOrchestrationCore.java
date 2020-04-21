package com.cobiscorp.ecobis.orchestration.core.ib.stocks.bydate;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
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
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.StockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.StockResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Region;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Stock;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceStock;

/**
 * Authentication device stock query by Date
 * 
 * @since Jun 16, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */
@Component(name = "StockFactorByDateOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "StockFactorByDateOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "StockFactorByDateOrchestrationCore") })
public class StockFactorByDateOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(StockFactorByDateOrchestrationCore.class);
	private static final String CLASS_NAME = "StockFactorByDateOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String RESPONSE_LOCAL_UPDATE = "RESPONSE_LOCAL_UPDATE";
	static final String RESPONSE_PROVIDER = "RESPONSE_PROVIDER";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceStock.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceDateStock", unbind = "unbindCoreServiceDateStock")
	protected ICoreServiceStock coreServiceDateStock;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceDateStock(ICoreServiceStock service) {
		coreServiceDateStock = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceDateStock(ICoreServiceStock service) {
		coreServiceDateStock = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceDateStock", coreServiceDateStock);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	private IProcedureResponse executeSteps(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {
			if (anOriginalRequest.readValueParam("@i_operacion").equals("Q")) // Search
			{
				responseProc = queryProviderTransaction(anOriginalRequest, aBag);

				aBag.put(RESPONSE_TRANSACTION, responseProc);
				aBag.put(RESPONSE_PROVIDER, responseProc);
				if (Utils.flowError("updateProviderTransaction", responseProc)) {

					return responseProc;
				}

			}

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryProviderTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		StockResponse wStockResp = new StockResponse();
		StockRequest wStockRequest = transformStockRequest(anOriginalRequest.clone());

		try {
			wStockRequest.setOriginalRequest(anOriginalRequest);
			wStockResp = coreServiceDateStock.getStockbyDate(wStockRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformStockResponse(wStockResp, aBag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST), response);
		return response;
	}

	/******************
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private StockRequest transformStockRequest(IProcedureRequest aRequest) {
		StockRequest wStockRequest = new StockRequest();
		Office wOffice = new Office();
		Region wregion = new Region();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		if (!(aRequest.readValueParam("@i_oficina") == null)) {
			wOffice.setId(Integer.parseInt(aRequest.readValueParam("@i_oficina")));
			wStockRequest.setOffice(wOffice);
		}
		if (!(aRequest.readValueParam("@i_region") == null)) {
			wregion.setId(aRequest.readValueParam("@i_region"));
			wStockRequest.setRegion(wregion);
		}
		if (!(aRequest.readValueParam("@i_secuencial") == null)) {
			wStockRequest.setSequential(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		}
		return wStockRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformStockResponse(StockResponse aStockResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aStockResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aStockResponse.getMessages()));
			// wProcedureResponse =
			// Utils.returnException(aCheckResponse.getMessages());
		} else {

			wProcedureResponse.addParam("@o_num_filas", ICTSTypes.SQLINT4, 1, aStockResponse.getRows().toString());

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("SECUENCIA", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO REGION", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REGIONAL", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO OFICINA", ICTSTypes.SYBINT2, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICINA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("STOCK", ICTSTypes.SYBINT4, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA", ICTSTypes.SYBDATETIME, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO", ICTSTypes.SYBVARCHAR, 20));

			for (Stock aStock : aStockResponse.getStockCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aStock.getSequential().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aStock.getCod_region()));
				row.addRowData(3, new ResultSetRowColumnData(false, aStock.getRegion()));
				row.addRowData(4, new ResultSetRowColumnData(false, aStock.getCod_office().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aStock.getOffice()));
				row.addRowData(6, new ResultSetRowColumnData(false, aStock.getStock().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aStock.getFecha().toString()));
				row.addRowData(8, new ResultSetRowColumnData(false, aStock.getTipo().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aStockResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
