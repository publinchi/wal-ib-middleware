package com.cobiscorp.ecobis.orchestration.core.ib.query.office;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.utils.ProductPredicate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceOffice;

/**
 * Credit Lines
 * 
 * @since Jun 30, 2015
 * @author itorres
 * @version 1.0.0
 * 
 */
@Component(name = "OfficeOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "OfficeOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "OfficeOrchestrationCore") })
public class OfficeOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(OfficeOrchestrationCore.class);
	private static final String CLASS_NAME = "OfficeOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	// static final String RESPONSE_LOCAL_UPDATE = "RESPONSE_LOCAL_UPDATE";
	// static final String RESPONSE_PROVIDER = "RESPONSE_PROVIDER";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceOffice.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceOffice", unbind = "unbindCoreServiceOffice")
	protected ICoreServiceOffice coreServiceOffice;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceOffice(ICoreServiceOffice service) {
		coreServiceOffice = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceOffice(ICoreServiceOffice service) {
		coreServiceOffice = null;
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
			mapInterfaces.put("coreServiceOffice", coreServiceOffice);

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
			return Utils.returnException("Service is not available");
		}
	}

	private IProcedureResponse executeSteps(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);

		IProcedureResponse responseProc = null;

		try {
			responseProc = executeOfficeCore(anOriginalRequest, aBag);

			if (Utils.flowError("executeOfficeCore -->", responseProc)) {
				aBag.put(RESPONSE_TRANSACTION, Utils.returnException(Utils.returnArrayMessage(responseProc)));
				return Utils.returnException(Utils.returnArrayMessage(responseProc));
			}
			aBag.remove(RESPONSE_TRANSACTION);
			aBag.put(RESPONSE_TRANSACTION, responseProc);

			if (logger.isDebugEnabled())
				logger.logDebug("Response Office Core  --> " + responseProc.getProcedureResponseAsString());

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}
			return Utils.returnException("Service is not available");
		}
	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse executeOfficeCore(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		OfficeResponse wOfficeResp = new OfficeResponse();
		OfficeRequest wOfficeRequest = transformOfficeRequest(anOriginalRequest.clone());
		try {
			wOfficeRequest.setOriginalRequest(anOriginalRequest);
			wOfficeResp = coreServiceOffice.getOffice(wOfficeRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformOfficeResponse(wOfficeResp, aBag);
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
	private OfficeRequest transformOfficeRequest(IProcedureRequest aRequest) {
		OfficeRequest wOfficeRequest = new OfficeRequest();
		// Entity wEntity = new Entity();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		/*
		 * String messageError = null; messageError =
		 * aRequest.readValueParam("@i_cliente") == null ?
		 * " - @i_cliente can't be null" : ""; if (!messageError.equals(""))
		 * throw new IllegalArgumentException(messageError);
		 * 
		 * if (!(aRequest.readValueParam("@i_cliente") == null)) {
		 * wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_cliente"
		 * ))); wOfficeRequest.setEntity(wEntity); }
		 */

		if (!(aRequest.readValueParam("@i_region") == null)) {
			wOfficeRequest.setRegion(aRequest.readValueParam("@i_region"));
		}

		return wOfficeRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformOfficeResponse(OfficeResponse aOfficeResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aOfficeResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aOfficeResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODOFICINA", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESOFICINA", ICTSTypes.SYBVARCHAR, 64));

			for (Office aOffice : aOfficeResponse.getOfficeCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aOffice.getId().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aOffice.getDescription()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
		}
		wProcedureResponse.setReturnCode(aOfficeResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

}
