package com.cobiscorp.ecobis.orchestration.core.ib.clientenquiries;

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
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ClientEnquiries;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SearchOption;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceClientEnquiries;

/**
 * Client Enquiries
 * 
 * @since Aug 14, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */
@Component(name = "ClientEnquiriesOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ClientEnquiriesOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ClientEnquiriesOrchestrationCore") })
public class ClientEnquiriesOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(ClientEnquiriesOrchestrationCore.class);
	private static final String CLASS_NAME = "ClientEnquiriesOrchestrationCore--->";
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
	@Reference(referenceInterface = ICoreServiceClientEnquiries.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceClientEnquiries", unbind = "unbindCoreServiceClientEnquiriess")
	protected ICoreServiceClientEnquiries coreServiceClientEnquiries;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceClientEnquiries(ICoreServiceClientEnquiries service) {
		coreServiceClientEnquiries = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceClientEnquiriess(ICoreServiceClientEnquiries service) {
		coreServiceClientEnquiries = null;
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
			mapInterfaces.put("coreServiceClientEnquiries", coreServiceClientEnquiries);

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
			// consulta de solicitudes del cliente
			responseProc = executeTransaction(anOriginalRequest, aBag);

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
	 * name executeTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		ClientEnquiriesResponse wClientEnquiriesResp = new ClientEnquiriesResponse();
		ClientEnquiriesRequest wClientEnquiriesReq = transformClientEnquiriesRequest(anOriginalRequest.clone());

		try {
			wClientEnquiriesReq.setOriginalRequest(anOriginalRequest);

			wClientEnquiriesResp = coreServiceClientEnquiries.getClientEnquiries(wClientEnquiriesReq);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformClientEnquiriesResponse(wClientEnquiriesResp, aBag);
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
	 * Transformación de ProcedureRequest a ClientEnquiriesRequest
	 ********************/

	private ClientEnquiriesRequest transformClientEnquiriesRequest(IProcedureRequest aRequest) {
		ClientEnquiriesRequest wClientEnquiriesRequest = new ClientEnquiriesRequest();
		SearchOption wSearchOption = new SearchOption();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_tipo") == null ? " - @i_tipo can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wSearchOption.setCriteria(aRequest.readValueParam("@i_tipo"));
		wSearchOption.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));
		wSearchOption.setFinalDate(aRequest.readValueParam("@i_fecha_fin"));
		wClientEnquiriesRequest.setSearchOption(wSearchOption);
		wClientEnquiriesRequest.setNumberofRegisters(Integer.parseInt(aRequest.readValueParam("@i_nregistros")));
		wClientEnquiriesRequest.setNext(Integer.parseInt(aRequest.readValueParam("@i_siguiente")));
		wClientEnquiriesRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		wClientEnquiriesRequest.setMISClientId(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		wClientEnquiriesRequest.setId_aux(Integer.parseInt(aRequest.readValueParam("@i_siguiente_aux")));

		return wClientEnquiriesRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformClientEnquiriesResponse(ClientEnquiriesResponse aClientEnquiriesResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aClientEnquiriesResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aClientEnquiriesResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTO", ICTSTypes.SYBMONEY, 2));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO", ICTSTypes.SYBVARCHAR, 25));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("BENEFICIARIO", ICTSTypes.SYBVARCHAR, 25));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CANTIDAD", ICTSTypes.SYBINT2, 2));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID_AUX", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO DE AVAL", ICTSTypes.SYBVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("AVAL", ICTSTypes.SYBVARCHAR, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SUBTIPO", ICTSTypes.SYBVARCHAR, 64));

			for (ClientEnquiries aClientEnquiries : aClientEnquiriesResponse.getClientEnquiriesCollection()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aClientEnquiries.getId().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aClientEnquiries.getEnquiryDate()));
				row.addRowData(3, new ResultSetRowColumnData(false, aClientEnquiries.getAccount()));
				row.addRowData(4, new ResultSetRowColumnData(false, aClientEnquiries.getAmount().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aClientEnquiries.getState().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aClientEnquiries.getBeneficiary().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aClientEnquiries.getQuantity().toString()));
				row.addRowData(8, new ResultSetRowColumnData(false, aClientEnquiries.getIdAux().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false, aClientEnquiries.getCurrencyId().toString()));
				row.addRowData(10, new ResultSetRowColumnData(false, aClientEnquiries.getEndorsementType().toString()));
				row.addRowData(11, new ResultSetRowColumnData(false, aClientEnquiries.getEndorsement().toString()));
				row.addRowData(12, new ResultSetRowColumnData(false, aClientEnquiries.getSubType().toString()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aClientEnquiriesResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
