package com.cobiscorp.ecobis.orchestration.core.ib.query.detail;

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
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EnquiriesDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnquiriesDetail;

/**
 * Enquiries Detail
 * 
 * @since Aug 20, 2015
 * @author dmorla
 * @version 1.0.0
 * 
 */
@Component(name = "EnquiriesDetailOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EnquiriesDetailOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EnquiriesDetailOrchestrationCore") })
public class EnquiriesDetailOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(EnquiriesDetailOrchestrationCore.class);
	private static final String CLASS_NAME = "EnquiriesDetailOrchestrationCore--->";
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
	@Reference(referenceInterface = ICoreServiceEnquiriesDetail.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceEnquiriesDetail", unbind = "unbindCoreServiceEnquiriesDetail")
	protected ICoreServiceEnquiriesDetail coreServiceEnquiriesDetail;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceEnquiriesDetail(ICoreServiceEnquiriesDetail service) {
		coreServiceEnquiriesDetail = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceEnquiriesDetail(ICoreServiceEnquiriesDetail service) {
		coreServiceEnquiriesDetail = null;
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
			mapInterfaces.put("coreServiceEnquiriesDetail", coreServiceEnquiriesDetail);

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

			// consulta de subtipos

			responseProc = queryExecution(anOriginalRequest, aBag);

			aBag.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("queryExecution", responseProc)) {

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
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryExecution(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		EnquiriesDetailResponse wEnquiriesDetailResp = new EnquiriesDetailResponse();
		EnquiriesDetailRequest wEnquiriesDetailRequest = transformEnquiriesDetailRequest(anOriginalRequest.clone());

		try {
			wEnquiriesDetailRequest.setOriginalRequest(anOriginalRequest);
			wEnquiriesDetailResp = coreServiceEnquiriesDetail.getDetail(wEnquiriesDetailRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformDetailResponse(wEnquiriesDetailResp, aBag);
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

	private EnquiriesDetailRequest transformEnquiriesDetailRequest(IProcedureRequest aRequest) {
		EnquiriesDetailRequest wEnquiriesDetailRequest = new EnquiriesDetailRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_tipo_solicitud") == null ? " - @i_tipo_solicitud can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wEnquiriesDetailRequest.setOperation(aRequest.readValueParam("@i_tipo_solicitud"));
		if (aRequest.readValueParam("@i_cta") != null)
			wEnquiriesDetailRequest.setAccount(aRequest.readValueParam("@i_cta"));
		if (aRequest.readValueParam("@i_chequera") != null)
			wEnquiriesDetailRequest.setCheckbook(Integer.parseInt(aRequest.readValueParam("@i_chequera")));
		if (aRequest.readValueParam("@i_id") != null)
			wEnquiriesDetailRequest.setId(Integer.parseInt(aRequest.readValueParam("@i_id")));

		return wEnquiriesDetailRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformDetailResponse(EnquiriesDetailResponse aEnquiriesDetailResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aEnquiriesDetailResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aEnquiriesDetailResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID", ICTSTypes.SYBINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTO", ICTSTypes.SYBMONEY, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO CHEQUERA", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TOTAL CHEQUES", ICTSTypes.SYBINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICINA ENTREGA", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PROPOSITO", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("BENEFICIARIO", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID AUTORIZADO", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("AUTORIZADO", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SUBTIPO", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PLAZO", ICTSTypes.SYBINT2, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA_FIN", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("GARANTIZAR", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO AVAL", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("AVAL", ICTSTypes.SYBVARCHAR, 64));

			for (EnquiriesDetail aDetail : aEnquiriesDetailResponse.getEnquiriesDetailCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false,
						aDetail.getApplicationNumber() == null ? "" : aDetail.getApplicationNumber().toString()));
				row.addRowData(2,
						new ResultSetRowColumnData(false, aDetail.getAccount() == null ? "" : aDetail.getAccount()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aDetail.getAmount() == null ? "" : aDetail.getAmount().setScale(2).toString()));
				row.addRowData(4, new ResultSetRowColumnData(false,
						aDetail.getCheckbookTipe() == null ? "" : aDetail.getCheckbookTipe()));
				row.addRowData(5, new ResultSetRowColumnData(false,
						aDetail.getChecks() == null ? "" : aDetail.getChecks().toString()));
				row.addRowData(6,
						new ResultSetRowColumnData(false, aDetail.getDelivery() == null ? "" : aDetail.getDelivery()));
				row.addRowData(7,
						new ResultSetRowColumnData(false, aDetail.getPurpose() == null ? "" : aDetail.getPurpose()));
				row.addRowData(8, new ResultSetRowColumnData(false,
						aDetail.getBeneficiary() == null ? "" : aDetail.getBeneficiary()));
				row.addRowData(9, new ResultSetRowColumnData(false,
						aDetail.getThirdIdentification() == null ? "" : aDetail.getThirdIdentification()));
				row.addRowData(10,
						new ResultSetRowColumnData(false, aDetail.getName() == null ? "" : aDetail.getName()));
				row.addRowData(11,
						new ResultSetRowColumnData(false, aDetail.getState() == null ? "" : aDetail.getState()));
				row.addRowData(12,
						new ResultSetRowColumnData(false, aDetail.getType() == null ? "" : aDetail.getType()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aDetail.getSubtype() == null ? "" : aDetail.getSubtype()));
				row.addRowData(14, new ResultSetRowColumnData(false,
						aDetail.getTerm() == null ? "0" : aDetail.getTerm().toString()));
				row.addRowData(15,
						new ResultSetRowColumnData(false, aDetail.getEndDate() == null ? "" : aDetail.getEndDate()));
				row.addRowData(16, new ResultSetRowColumnData(false,
						aDetail.getGuarantee() == null ? "" : aDetail.getGuarantee()));
				row.addRowData(17, new ResultSetRowColumnData(false,
						aDetail.getEndorsementType() == null ? "" : aDetail.getEndorsementType()));
				row.addRowData(18, new ResultSetRowColumnData(false,
						aDetail.getEndorsement() == null ? "" : aDetail.getEndorsement()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aEnquiriesDetailResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
