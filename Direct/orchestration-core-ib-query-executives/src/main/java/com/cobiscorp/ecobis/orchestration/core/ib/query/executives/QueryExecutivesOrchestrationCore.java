package com.cobiscorp.ecobis.orchestration.core.ib.query.executives;

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
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Executives;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceExecutives;

/**
 * @author gcondo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
@Component(name = "QueryExecutivesOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "QueryExecutivesOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "QueryExecutivesOrchestrationCore") })
public class QueryExecutivesOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = "QueryExecutivesOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	public static final int PRODUCT_CTACTE = 3;
	public static final int PRODUCT_CTAAHO = 4;
	public static final int PRODUCT_LOAN = 7;
	public static final int PRODUCT_CARDSCREDIT = 83;
	public static final int PRODUCT_CARDSDEBIT = 16;
	public static final int PRODUCT_TIMEDEPOSIT = 14;
	public static int PRODUCT_TYPE;

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceExecutives.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceExecutives coreServiceExecutives;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceExecutives service) {
		coreServiceExecutives = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceExecutives service) {
		coreServiceExecutives = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		ExecutivesResponse wExecutivesResp = null;

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceExecutives", coreServiceExecutives);
			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Origanl Request ISNULL");

			ExecutivesRequest wExecutivesRequest = transformExecutivesRequest(anOriginalRequest.clone());

			// wQueryProdutcsResponse=coreServiceQueryProducts.getQueryProducts(QueryProductsRequest);
			wExecutivesResp = coreServiceExecutives.GetExecutives(wExecutivesRequest);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wExecutivesResp);

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

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = transformProcedureResponse(aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	/**************************************************************************/
	private ExecutivesRequest transformExecutivesRequest(IProcedureRequest aRequest) {
		ExecutivesRequest ExecutivesReq = new ExecutivesRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());
		Client wClient = new Client();
		wClient.setId(aRequest.readValueParam("@i_ente"));
		ExecutivesReq.setClient(wClient);

		return ExecutivesReq;
	}

	/**************************************************************************/
	private IProcedureResponse transformProcedureResponse(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response>>");

		ExecutivesResponse wExecutivesResp = (ExecutivesResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("EMAIL", ICTSTypes.SQLVARCHAR, 20));

		for (Executives aExecutivesResp : wExecutivesResp.getExecutivesCollection()) {
			// if (!IsValidAccountStatementResponse(aAccountStatement)) return
			// null;
			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, aExecutivesResp.getName()));
			row.addRowData(2, new ResultSetRowColumnData(false, aExecutivesResp.getEmail()));
			data.addRow(row);

		}
		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Response Final -->>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
