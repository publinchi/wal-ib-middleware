package com.cobiscorp.ecobis.orchestration.core.ib.query.products;

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
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryProducts;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQueryProducts;

/**
 * @author gcondo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
@Component(name = "QueryProductsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "QueryProductsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "QueryProductsOrchestrationCore") })
public class QueryProductsOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = "QueryProductsOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	public static final int PRODUCT_CTACTE = 3;
	public static final int PRODUCT_CTAAHO = 4;
	public static final int PRODUCT_LOAN = 7;
	public static final int PRODUCT_CARDSCREDIT = 83;
	public static final int PRODUCT_CARDSDEBIT = 16;
	public static final int PRODUCT_TIMEDEPOSIT = 14;
	public static int PRODUCT_TYPE;
	public static String TYPE;

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQueryProducts.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceQueryProducts coreServiceQueryProducts;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceQueryProducts service) {
		coreServiceQueryProducts = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceQueryProducts service) {
		coreServiceQueryProducts = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		QueryProductsResponse wQueryProdutcsResponse = null;

		try {
			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Origanl Request ISNULL");

			QueryProductsRequest QueryProductsRequest = transformQueryProductsRequest(anOriginalRequest.clone());

			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceQueryProducts", coreServiceQueryProducts);
			Utils.validateComponentInstance(mapInterfaces);

			wQueryProdutcsResponse = coreServiceQueryProducts.getQueryProducts(QueryProductsRequest);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wQueryProdutcsResponse);

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
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST), response);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	/**************************************************************************/
	private QueryProductsRequest transformQueryProductsRequest(IProcedureRequest aRequest) {
		QueryProductsRequest queryProductsReq = new QueryProductsRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		queryProductsReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));
		queryProductsReq.setOperation(aRequest.readValueParam("@i_operacion"));
		queryProductsReq.setProduct(Integer.parseInt(aRequest.readValueParam("@i_producto")));
		queryProductsReq.setClientType(aRequest.readValueParam("@i_tipo_cliente"));
		queryProductsReq.setOrigen(Integer.parseInt(aRequest.readValueParam("@i_origen")));
		queryProductsReq.setCliente(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		TYPE = aRequest.readValueParam("@i_cliente");
		if (TYPE.equals("G")) {
			queryProductsReq.setType(aRequest.readValueParam("@i_tipo"));
			queryProductsReq.setClient1(aRequest.readValueParam("@i_clientes1"));
			queryProductsReq.setClient2(aRequest.readValueParam("@i_clientes2"));
			queryProductsReq.setClient3(aRequest.readValueParam("@i_clientes3"));
			queryProductsReq.setCode(Integer.parseInt(aRequest.readValueParam("@i_codigo")));
		} else {
			queryProductsReq.setType("P");
		}

		/*
		 * queryProductsReq.setTrn(1875055); queryProductsReq.setOperation("S");
		 * queryProductsReq.setProduct(3); queryProductsReq.setClientType("P");
		 * queryProductsReq.setOrigen(1); queryProductsReq.setCliente(13036);
		 */

		return queryProductsReq;
	}

	/**************************************************************************/
	private IProcedureResponse transformProcedureResponse(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest wProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		String wProd = wProcedureRequest.readValueParam("@i_producto");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response>>>");
		PRODUCT_TYPE = Integer.parseInt(wProd.trim());
		if (TYPE.equals("G")) {
			wProcedureResponse = transformProcedureResponseTypeG(aBagSPJavaOrchestration);
		} else {
			switch (PRODUCT_TYPE) {
			case PRODUCT_CTACTE:
			case PRODUCT_CTAAHO:
				wProcedureResponse = transformProcedureResponseAccounts(aBagSPJavaOrchestration);
				break;
			case PRODUCT_LOAN:
				wProcedureResponse = transformProcedureResponseLoan(aBagSPJavaOrchestration);
				break;
			case PRODUCT_CARDSCREDIT:
			case PRODUCT_CARDSDEBIT:
				wProcedureResponse = transformProcedureResponseCards(aBagSPJavaOrchestration);
				break;
			case PRODUCT_TIMEDEPOSIT:
				wProcedureResponse = transformProcedureResponseTimeDeposit(aBagSPJavaOrchestration);
				break;
			default:
				break;
			}
		}

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Response Final -->>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	/***************************************************************/
	private IProcedureResponse transformProcedureResponseAccounts(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest wProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		QueryProductsResponse wQueryProductsResponse = (QueryProductsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response Accounts>>>");

		// ****************
		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		IResultSetRow row1 = new ResultSetRow();
		row1.addRowData(1, new ResultSetRowColumnData(false, wQueryProductsResponse.getRowCount().toString()));
		data1.addRow(row1);
		IResultSetBlock resultRowCount = new ResultSetBlock(metaData1, data1);
		wProcedureResponse.addResponseBlock(resultRowCount);
		// ****************
		if (wProcedureRequest.readValueParam("@i_origen").equals("1")) {
			// Add Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICIAL", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTOABREVIACION", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TYPEACCOUNT", ICTSTypes.SQLVARCHAR, 2)); // ITO

			for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
				// if (!IsValidAccountStatementResponse(aAccountStatement))
				// return null;
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
				row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));
				row.addRowData(5, new ResultSetRowColumnData(false, aQueryProducts.getOfficial()));
				row.addRowData(6,
						new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductDescription()));
				row.addRowData(7, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNemonic()));
				row.addRowData(8,
						new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductTypeAccount()));
				data.addRow(row);
			} // for

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);

		} else if (wProcedureRequest.readValueParam("@i_origen").equals("3")) {
			// Add Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));

			for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
				// if (!IsValidAccountStatementResponse(aAccountStatement))
				// return null;
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
				row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));

				data.addRow(row);
			} // for

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);

		}

		return wProcedureResponse;
	}

	/***************************************************************/
	private IProcedureResponse transformProcedureResponseLoan(Map<String, Object> aBagSPJavaOrchestration) {

		QueryProductsResponse wQueryProductsResponse = (QueryProductsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response Loan>>>");

		// ****************
		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		IResultSetRow row1 = new ResultSetRow();
		row1.addRowData(1, new ResultSetRowColumnData(false, wQueryProductsResponse.getRowCount().toString()));
		data1.addRow(row1);
		IResultSetBlock resultRowCount = new ResultSetBlock(metaData1, data1);
		wProcedureResponse.addResponseBlock(resultRowCount);
		// ****************

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ALIAS", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTOABREVIACION", ICTSTypes.SQLVARCHAR, 10));

		for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
			// if (!IsValidAccountStatementResponse(aAccountStatement)) return
			// null;
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
			row.addRowData(3, new ResultSetRowColumnData(false,
					aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));
			row.addRowData(5, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductAlias()));
			row.addRowData(6, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductDescription()));
			row.addRowData(7, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNemonic()));
			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);

		return wProcedureResponse;
	}

	/***************************************************************/
	private IProcedureResponse transformProcedureResponseCards(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest wProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		QueryProductsResponse wQueryProductsResponse = (QueryProductsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response Cards>>>");

		// ****************
		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		IResultSetRow row1 = new ResultSetRow();
		row1.addRowData(1, new ResultSetRowColumnData(false, wQueryProductsResponse.getRowCount().toString()));
		data1.addRow(row1);
		IResultSetBlock resultRowCount = new ResultSetBlock(metaData1, data1);
		wProcedureResponse.addResponseBlock(resultRowCount);
		// ****************

		if (wProcedureRequest.readValueParam("@i_operacion").equals("S")) {
			// Add Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICIAL", ICTSTypes.SQLINT4, 4));

			for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
				// if (!IsValidAccountStatementResponse(aAccountStatement))
				// return null;
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
				row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));
				row.addRowData(5, new ResultSetRowColumnData(false, aQueryProducts.getOfficial()));
				data.addRow(row);
			} // for

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);

		} else if (wProcedureRequest.readValueParam("@i_operacion").equals("V")) {
			// Add Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));

			for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
				// if (!IsValidAccountStatementResponse(aAccountStatement))
				// return null;
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));

				data.addRow(row);
			} // for

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);

		}

		return wProcedureResponse;
	}

	/***************************************************************/
	private IProcedureResponse transformProcedureResponseTimeDeposit(Map<String, Object> aBagSPJavaOrchestration) {

		QueryProductsResponse wQueryProductsResponse = (QueryProductsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response Loan>>>");
		// ****************
		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		IResultSetRow row1 = new ResultSetRow();
		row1.addRowData(1, new ResultSetRowColumnData(false, wQueryProductsResponse.getRowCount().toString()));
		data1.addRow(row1);
		IResultSetBlock resultRowCount = new ResultSetBlock(metaData1, data1);
		wProcedureResponse.addResponseBlock(resultRowCount);
		// ****************

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("DEPOSITO", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE", ICTSTypes.SQLVARCHAR, 50));

		for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
			// if (!IsValidAccountStatementResponse(aAccountStatement)) return
			// null;
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
			row.addRowData(3, new ResultSetRowColumnData(false,
					aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));
			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);

		return wProcedureResponse;
	}

	/***************************************************************/
	private IProcedureResponse transformProcedureResponseTypeG(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest wProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		QueryProductsResponse wQueryProductsResponse = (QueryProductsResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response Cards>>>");

		// ****************
		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		IResultSetRow row1 = new ResultSetRow();
		row1.addRowData(1, new ResultSetRowColumnData(false, wQueryProductsResponse.getRowCount().toString()));
		data1.addRow(row1);
		IResultSetBlock resultRowCount = new ResultSetBlock(metaData1, data1);
		wProcedureResponse.addResponseBlock(resultRowCount);
		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE CUENTA", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICIAL", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CLIENTE", ICTSTypes.SQLVARCHAR, 50));

		for (QueryProducts aQueryProducts : wQueryProductsResponse.getQueryProductsCollection()) {
			// if (!IsValidAccountStatementResponse(aAccountStatement)) return
			// null;
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, aQueryProducts.getCode().toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductNumber()));
			row.addRowData(3, new ResultSetRowColumnData(false,
					aQueryProducts.getProduct().getCurrency().getCurrencyId().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, aQueryProducts.getProduct().getProductName()));
			row.addRowData(5, new ResultSetRowColumnData(false, aQueryProducts.getOfficial()));
			row.addRowData(6, new ResultSetRowColumnData(false, aQueryProducts.getClient().getCompleteName()));
			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);

		return wProcedureResponse;
	}

}
