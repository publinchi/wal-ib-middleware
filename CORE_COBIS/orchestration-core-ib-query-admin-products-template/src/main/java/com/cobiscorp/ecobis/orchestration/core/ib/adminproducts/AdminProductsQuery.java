package com.cobiscorp.ecobis.orchestration.core.ib.adminproducts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryProducts;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQueryProducts;

@Component(name = "SummaryProductsQuery", immediate = false)
@Service(value = { ICoreServiceQueryProducts.class })
@Properties(value = { @Property(name = "service.description", value = "SummaryProductsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SummaryProductsQuery") })

public class AdminProductsQuery extends SPJavaOrchestrationBase implements ICoreServiceQueryProducts {

	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(AdminProductsQuery.class);
	private String IProduct = null;
	private String IOrigen = null;
	private String IType = null;
	private static final int COL_CODE = 0;
	private static final int COL_PRODUCT_NUMBER = 1;
	private static final int COL_CURRENCYID = 2;
	private static final int COL_PRODUCT_NAME = 3;
	private static final int COL_LOAN_TYPE = 3;
	private static final int COL_PRODUCT_OFFICIAL = 4;
	private static final int COL_ALIAS = 4;
	private static final int COL_PRODUCT_DESCRIPTION = 5;
	private static final int COL_NAME_CLIENT = 5;
	private static final int COL_PRODUCT_NEMONIC = 6;
	private static final int COL_TYPE_ACCOUNT = 7;

	@Override
	public QueryProductsResponse getQueryProducts(QueryProductsRequest wQueryProductsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getQueryProducts");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}

		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		if (wQueryProductsRequest.getType().equals("G"))
			request.setSpName("cob_cuentas..sp_tr03_cons_cuentas_grupo");
		else
			request.setSpName("cob_cuentas..sp_tr03_cons_cuentas");

		IType = wQueryProductsRequest.getType();

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, wQueryProductsRequest.getTrn().toString()); // "18300"
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, wQueryProductsRequest.getOperation()); // "S"
		request.addInputParam("@i_producto", ICTSTypes.SQLINT4, wQueryProductsRequest.getProduct().toString());
		request.addInputParam("@i_tipo_cliente", ICTSTypes.SQLVARCHAR, wQueryProductsRequest.getClientType());
		request.addInputParam("@i_origen", ICTSTypes.SQLINT4, wQueryProductsRequest.getOrigen().toString()); // 1
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, wQueryProductsRequest.getCliente().toString());

		if (wQueryProductsRequest.getType().equals("G")) {
			request.addInputParam("@i_clientes1", ICTSTypes.SQLVARCHAR, wQueryProductsRequest.getClient1());
			request.addInputParam("@i_clientes2", ICTSTypes.SQLVARCHAR, wQueryProductsRequest.getClient2());
			request.addInputParam("@i_clientes3", ICTSTypes.SQLVARCHAR, wQueryProductsRequest.getClient3());
			request.addInputParam("@i_codigo", ICTSTypes.SQLINT4, wQueryProductsRequest.getCode().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);
		IProduct = wQueryProductsRequest.getProduct().toString();
		IOrigen = wQueryProductsRequest.getOrigen().toString();

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		QueryProductsResponse wQueryProductsResponse = transformToQueryProductsResponse(pResponse, IProduct, IOrigen);
		return wQueryProductsResponse;

	}

	private QueryProductsResponse transformToQueryProductsResponse(IProcedureResponse aProcedureResponse,
			String wProduct, String IOrigen) {
		QueryProductsResponse QueryProductsResp = new QueryProductsResponse();
		List<QueryProducts> aQueryProductsCollection = new ArrayList<QueryProducts>();
		QueryProducts aQueryProducts = null;
		Product product = null;
		Currency currency = null;
		Client objClient = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsRowCount = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
		IResultSetRow[] rowsQueryProducts = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsRowCount) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			QueryProductsResp.setRowCount(columns[0].getValue());
		}

		if (IType.equals("G")) {
			for (IResultSetRow iResultSetRow : rowsQueryProducts) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aQueryProducts = new QueryProducts();
				product = new Product();
				currency = new Currency();
				aQueryProducts.setCode(Integer.parseInt(columns[COL_CODE].getValue())); // CODIGO
				product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue()); // CUENTA
				currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCYID].getValue())); // MONEDA
				product.setCurrency(currency);
				product.setProductName(columns[COL_PRODUCT_NAME].getValue()); // NOMBRE
																				// CUENTA
				if (IOrigen.equals("1")) {
					aQueryProducts.setOfficial(columns[COL_PRODUCT_OFFICIAL].getValue()); // OFICIAL
				}
				objClient.setCompleteName(columns[COL_NAME_CLIENT].getValue()); // NOMBRE
																				// CLIENTE
				aQueryProducts.setProduct(product);
				aQueryProducts.setClient(objClient);
				aQueryProductsCollection.add(aQueryProducts);
			}
		} else {
			if (wProduct.equals("3") || wProduct.equals("4")) {
				for (IResultSetRow iResultSetRow : rowsQueryProducts) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aQueryProducts = new QueryProducts();
					product = new Product();
					currency = new Currency();
					aQueryProducts.setCode(Integer.parseInt(columns[COL_CODE].getValue())); // CODIGO
					product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue()); // CUENTA
					currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCYID].getValue())); // MONEDA
					product.setCurrency(currency);
					product.setProductName(columns[COL_PRODUCT_NAME].getValue()); // NOMBRE
																					// CUENTA
					if (IOrigen.equals("1")) {
						aQueryProducts.setOfficial(columns[COL_PRODUCT_OFFICIAL].getValue()); // OFICIAL
						product.setProductDescription(columns[COL_PRODUCT_DESCRIPTION].getValue());// PRODUCTO
						product.setProductNemonic(columns[COL_PRODUCT_NEMONIC].getValue()); 
						product.setProductTypeAccount(columns[COL_TYPE_ACCOUNT].getValue()); 
					}
					aQueryProducts.setProduct(product);
					aQueryProductsCollection.add(aQueryProducts);
				}
			}
			if (wProduct.equals("16") || wProduct.equals("83")) {
				for (IResultSetRow iResultSetRow : rowsQueryProducts) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aQueryProducts = new QueryProducts();
					product = new Product();
					currency = new Currency();
					aQueryProducts.setCode(Integer.parseInt(columns[COL_CODE].getValue())); // CODIGO
					product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue()); // CUENTA
					currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCYID].getValue())); // MONEDA
					product.setCurrency(currency);
					product.setProductName(columns[COL_PRODUCT_NAME].getValue()); // NOMBRE
																					// CUENTA
					if (IOrigen.equals("1")) {
						aQueryProducts.setOfficial(columns[COL_PRODUCT_OFFICIAL].getValue()); // OFICIAL
					}
					aQueryProducts.setProduct(product);
					aQueryProductsCollection.add(aQueryProducts);
				}
			}
			if (wProduct.equals("7")) {
				for (IResultSetRow iResultSetRow : rowsQueryProducts) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aQueryProducts = new QueryProducts();
					product = new Product();
					currency = new Currency();
					aQueryProducts.setCode(Integer.parseInt(columns[COL_CODE].getValue())); // CODIGO
					product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue()); // PRESTAMO
					currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCYID].getValue())); // MONEDA
					product.setCurrency(currency);
					product.setProductName(columns[COL_LOAN_TYPE].getValue()); // TIPO
																				// PRESTAMO
					product.setProductAlias(columns[COL_ALIAS].getValue()); // ALIAS
					product.setProductDescription(columns[COL_PRODUCT_DESCRIPTION].getValue()); // PRODUCTO
					product.setProductNemonic(columns[COL_PRODUCT_NEMONIC].getValue()); // PRODUCTO
																						// ABREVIACION
					aQueryProducts.setProduct(product);
					aQueryProductsCollection.add(aQueryProducts);
				}
			}
			if (wProduct.equals("14")) {
				for (IResultSetRow iResultSetRow : rowsQueryProducts) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aQueryProducts = new QueryProducts();
					product = new Product();
					currency = new Currency();
					aQueryProducts.setCode(Integer.parseInt(columns[COL_CODE].getValue())); // CODIGO
					product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue()); // DEPOSITO
					currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCYID].getValue())); // MONEDA
					product.setCurrency(currency);
					product.setProductName(columns[COL_PRODUCT_NAME].getValue()); // NOMBRE
																					// CUENTA
					aQueryProducts.setProduct(product);
					aQueryProductsCollection.add(aQueryProducts);
				}
			}
		}

		QueryProductsResp.setQueryProductsCollection(aQueryProductsCollection);

		return QueryProductsResp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
