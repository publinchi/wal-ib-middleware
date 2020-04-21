package com.cobiscorp.ecobis.orchestration.core.ib.query.ach.detail;

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
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AchDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceACHDetail;

/**
 * Detalle ACH
 * 
 * @since Oct 26, 2015
 * @author dmorla
 * @version 1.0.0
 * 
 */
@Component(name = "ACHDetailOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ACHDetailOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ACHDetailOrchestrationCore") })
public class ACHDetailOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(ACHDetailOrchestrationCore.class);
	private static final String CLASS_NAME = "ACHDetailOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceACHDetail.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceACHDetail", unbind = "unbindCoreServiceACHDetail")
	protected ICoreServiceACHDetail coreServiceACHDetail;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceACHDetail(ICoreServiceACHDetail service) {
		coreServiceACHDetail = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceACHDetail(ICoreServiceACHDetail service) {
		coreServiceACHDetail = null;
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
			mapInterfaces.put("coreServiceACHDetail", coreServiceACHDetail);

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

			// consulta de GRB

			responseProc = queryTransaction(anOriginalRequest, aBag);

			aBag.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("updateProviderTransaction", responseProc)) {

				return responseProc;
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
	 * name queryTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		ACHDetailResponse wACHDetailResp = new ACHDetailResponse();
		ACHDetailRequest wACHDetailRequest = transformACHRequest(anOriginalRequest.clone());

		try {
			wACHDetailRequest.setOriginalRequest(anOriginalRequest);
			wACHDetailResp = coreServiceACHDetail.getACHDetail(wACHDetailRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformACHResponse(wACHDetailResp, aBag);
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

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	/******************
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private ACHDetailRequest transformACHRequest(IProcedureRequest aRequest) {
		ACHDetailRequest wACHDetailRequest = new ACHDetailRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_number_order") == null ? " - @i_number_order can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wACHDetailRequest.setNumberOrder(aRequest.readValueParam("@i_number_order"));

		return wACHDetailRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformACHResponse(ACHDetailResponse aACHDetailResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aACHDetailResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aACHDetailResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ORDER", ICTSTypes.SYBVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO", ICTSTypes.SYBVARCHAR, 24));

			for (AchDetail aACHDetal : aACHDetailResponse.getAchDetailCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aACHDetal.getNumberOrder()));
				row.addRowData(2, new ResultSetRowColumnData(false, aACHDetal.getStatus()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aACHDetailResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
