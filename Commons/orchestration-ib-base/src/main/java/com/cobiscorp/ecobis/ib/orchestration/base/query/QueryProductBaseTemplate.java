package com.cobiscorp.ecobis.ib.orchestration.base.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.query.ProductPredicate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 * @author cecheverria
 * @since Sept 3, 2014
 * @modified gcondo
 * @version 1.0.0
 */

public abstract class QueryProductBaseTemplate extends SPJavaOrchestrationBase {
	protected static final String CLASS_NAME = " >-----> ";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_PRODUCTS_QUERY = "RESPONSE_PRODUCTS_QUERY";
	protected static final String RESPONSE_PRODUCTS_CORE = "RESPONSE_PRODUCTS_CORE";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String QUERY_NAME = "QUERY_NAME";
	protected static final String SERVER_STATUS_RESP = "SERVER_STATUS_RESP";
	protected static final String CONSOLIDATE_REQUEST = "CONSOLIDATE_REQUEST";
	private static final int ACCOUNTS = 100;
	private static final int ALL_ACCOUNTS = 0;// TODOS LOS PRODUCTOS PARA
												// POSICION CONSOLIDADA
	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int LOAN = 7;
	private static final int TIME_DEPOSIT = 14;
	private static final int CREDIT_CARD = 83;
	private static final int NOT_PRODUCTS = -1;

	private int colProductType;
	private int colCurrency;
	private int colProductNumber;
	private boolean getSchemaProduct;
	private boolean flagConsolidated = false;

	/**
	 * @return the flagConsolidated
	 */
	public boolean isFlagConsolidated() {
		return flagConsolidated;
	}

	/**
	 * @param flagConsolidated
	 *            the flagConsolidated to set
	 */
	public void setFlagConsolidated(boolean flagConsolidated) {
		this.flagConsolidated = flagConsolidated;
	}

	/**
	 * @return the getSchemaProduct
	 */
	public boolean isGetSchemaProduct() {
		return getSchemaProduct;
	}

	/**
	 * @param getSchemaProduct
	 *            the getSchemaProduct to set
	 */
	public void setGetSchemaProduct(boolean getSchemaProduct) {
		this.getSchemaProduct = getSchemaProduct;
	}

	/**
	 * @return the colProductType
	 */
	public int getColProductType() {
		return colProductType;
	}

	/**
	 * @param colProductType
	 *            the colProductType to set
	 */
	public void setColProductType(int colProductType) {
		this.colProductType = colProductType;
	}

	/**
	 * @return the colCurrency
	 */
	public int getColCurrency() {
		return colCurrency;
	}

	/**
	 * @param colCurrency
	 *            the colCurrency to set
	 */
	public void setColCurrency(int colCurrency) {
		this.colCurrency = colCurrency;
	}

	/**
	 * @return the colProductNumber
	 */
	public int getColProductNumber() {
		return colProductNumber;
	}

	/**
	 * @param colProductNumber
	 *            the colProductNumber to set
	 */
	public void setColProductNumber(int colProductNumber) {
		this.colProductNumber = colProductNumber;
	}

	private static ILogger logger = LogFactory.getLogger(QueryProductBaseTemplate.class);

	protected abstract ServerResponse executeServerStatus(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;

	protected abstract IProcedureResponse getAllAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse getSavingAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse getCheckingAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse getLoansCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse getTimeDepositsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse getCreditCardsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration);

	protected ConsolidateRequest transformConsolidateRequest(IProcedureRequest aRequest) {

		ConsolidateRequest consolidateRequest = new ConsolidateRequest();
		Client client = new Client();
		Currency currency = new Currency();

		if (aRequest.readValueParam("@i_miembro") != null)
			client.setId(aRequest.readValueParam("@i_miembro").toString());

		if (aRequest.readValueParam("@i_filtro_moneda") != null) {
			if (aRequest.readValueParam("@i_filtro_moneda").equals("S")) {
				if (aRequest.readValueParam("@i_valor_moneda") != null)
					currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_valor_moneda")));
			}
		}

		consolidateRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@t_trn").toString());
		consolidateRequest.setClient(client);
		consolidateRequest.setCurrency(currency);
		if (aRequest.readValueParam("@i_nregistros") != null)
			consolidateRequest.setNumberRegister(Integer.parseInt(aRequest.readValueParam("@i_nregistros").toString()));

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request Corebanking: " + consolidateRequest);

		return consolidateRequest;
	}

	/**
	 * This method has to be override to implement call of service
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeProductsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = null;
		int wProd = Integer.parseInt(request.readValueParam("@i_prod"));
		if (logger.isInfoEnabled())
			logger.logInfo("PRODUCTO -->" + String.valueOf(wProd) + " !!!!");

		aBagSPJavaOrchestration.put("PRODUCT_SEARCH", wProd);

		// SE VALIDA QUE EFECTIVAMENTE SE BUSQUEN LAS CUENTAS QUE EXISTAN EN EL
		// LOCAL
		if (wProd == ALL_ACCOUNTS || wProd == ACCOUNTS) {
			wProd = this.getProductIdToExecute(aBagSPJavaOrchestration);
		}

		switch (wProd) {
		case ALL_ACCOUNTS:
		case ACCOUNTS:
			wProcedureResponse = getAllAccountsCore(request, aBagSPJavaOrchestration);
			break;
		case SAVING_ACCOUNT:
			wProcedureResponse = getSavingAccountsCore(request, aBagSPJavaOrchestration);
			break;
		case CHECKING_ACCOUNT:
			wProcedureResponse = getCheckingAccountsCore(request, aBagSPJavaOrchestration);
			break;
		case LOAN:
			wProcedureResponse = getLoansCore(request, aBagSPJavaOrchestration);
			break;
		case TIME_DEPOSIT:
			wProcedureResponse = getTimeDepositsCore(request, aBagSPJavaOrchestration);
			break;
		case CREDIT_CARD:
			wProcedureResponse = getCreditCardsCore(request, aBagSPJavaOrchestration);
			break;

		default:
			break;
		}
		return wProcedureResponse;

	}

	// PERMITE OBTENER EL NUMERO DE PRODUCTO A CONSULTAR. APLICA PARA CUENTAS
	// CORRIENTES Y DE AHORROS
	private int getProductIdToExecute(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia getProductIdToExecute");
		}

		IProcedureResponse wProcResponseAccountsClientLocal = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_PRODUCTS_QUERY);

		IResultSetBlock resulset = wProcResponseAccountsClientLocal.getResultSet(1);
		IResultSetRow[] rowsTemp = resulset.getData().getRowsAsArray();
		int wProductTemp;
		boolean existCheckingAccount = false;
		boolean existSavingsAccount = false;

		for (IResultSetRow iResultSetRow : rowsTemp) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			wProductTemp = Integer.parseInt(columns[2].getValue());
			if (wProductTemp == CHECKING_ACCOUNT) {
				existCheckingAccount = true;
			} else if (wProductTemp == SAVING_ACCOUNT) {
				existSavingsAccount = true;
			}
		}
		int wProduct;
		if (existCheckingAccount && existSavingsAccount) {
			wProduct = ALL_ACCOUNTS;
		} else if (existCheckingAccount) {
			wProduct = CHECKING_ACCOUNT;
		} else if (existSavingsAccount) {
			wProduct = SAVING_ACCOUNT;
		} else {
			wProduct = NOT_PRODUCTS;
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza getProductIdToExecute");
			logger.logDebug("ID de Producto a consultar " + wProduct);
		}
		return wProduct;
	}

	/**
	 * Contains primary steps for execution of Query.
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeStepsQueryBase(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "START");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		String messageErrorQuery = null;
		messageErrorQuery = (String) aBagSPJavaOrchestration.get(QUERY_NAME);

		ServerResponse responseExecuteServerStatus = executeServerStatus(anOriginalRequest, aBagSPJavaOrchestration);
		if (aBagSPJavaOrchestration.get(SERVER_STATUS_RESP) != null)
			aBagSPJavaOrchestration.remove(SERVER_STATUS_RESP);

		aBagSPJavaOrchestration.put(SERVER_STATUS_RESP, responseExecuteServerStatus);

		IProcedureResponse responseExecuteProductsQuery = executeProductsQuery(anOriginalRequest.clone(),
				aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorQuery + " --> executeQuery", responseExecuteProductsQuery)) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + messageErrorQuery);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteProductsQuery);
			logger.logDebug("transformProcedureResponse-executeProductsQuery Final GCO-->"
					+ responseExecuteProductsQuery.getProcedureResponseAsString());
			return responseExecuteProductsQuery;// GCO-manejo de errores
		}
		;

		if (logger.isInfoEnabled())
			logger.logInfo("Lista  " + responseExecuteProductsQuery.getResultSet(1).getData().getRows().size());
		if (responseExecuteProductsQuery.getResultSet(1).getData().getRows().size() == 0) {
			if ("EN-US".equals(anOriginalRequest.readValueFieldInHeader("culture")))
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
						Utils.returnException("Do not have products associated"));
			else
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
						Utils.returnException("No tiene productos asociados a Banca en Línea"));
			return null;
		}

		ConsolidateRequest consolidateRequest = transformConsolidateRequest(anOriginalRequest);

		aBagSPJavaOrchestration.put(CONSOLIDATE_REQUEST, consolidateRequest);
		aBagSPJavaOrchestration.put(RESPONSE_PRODUCTS_QUERY, responseExecuteProductsQuery);

		IProcedureResponse responseExecuteProductsCore = executeProductsCore(anOriginalRequest,
				aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorQuery + " --> executeProductsCore", responseExecuteProductsCore)) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + messageErrorQuery);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteProductsCore);
			logger.logDebug("transformProcedureResponse-executeProductsCore Final GCO-->"
					+ responseExecuteProductsCore.getProcedureResponseAsString());
			return responseExecuteProductsCore;// GCO-Manejo de Errores
		}
		;

		if (logger.isInfoEnabled())
			logger.logInfo(
					"RESPONSE_PRODUCTS_QUERY --> " + responseExecuteProductsQuery.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo("RESPONSE_PRODUCTS_CORE --> " + responseExecuteProductsCore.getProcedureResponseAsString());

		aBagSPJavaOrchestration.put(RESPONSE_PRODUCTS_CORE, responseExecuteProductsCore);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "schemaProducto-queryProductBase---->" + isGetSchemaProduct());
		if (this.isGetSchemaProduct()) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Response GET_PRODUCTS is TRUE----> ");
			return joinResponse(aBagSPJavaOrchestration);
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Response GET_PRODUCTS is FALSE----> ");
			return responseExecuteProductsCore;
		}

	}

	/**
	 * Get Products Affiliate .
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeProductsQuery(IProcedureRequest aRequest, Map<String, Object> bag) {
		ServerResponse wServerStatusResp = (ServerResponse) bag.get(SERVER_STATUS_RESP);
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeProductsQuery");
		}

		request.setSpName("cob_bvirtual..sp_consulta_cuentas");

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18752");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18752");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@s_servicio", ICTSTypes.SQLINT1, aRequest.readValueParam("@s_servicio"));

		if (aRequest.readValueParam("@i_tipo_ejec") != null)
			request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, aRequest.readValueParam("@i_tipo_ejec"));
		else
			request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, wServerStatusResp.getOnLine() == true ? "L" : "F");
		Utils.copyParam("@i_valor_moneda", aRequest, request);

		Utils.copyParam("@i_valor_moneda", aRequest, request);
		Utils.copyParam("@i_filtro_moneda", aRequest, request);
		Utils.copyParam("@s_date", aRequest, request);
		Utils.copyParam("@i_operacion", aRequest, request);
		Utils.copyParam("@i_login", aRequest, request);
		Utils.copyParam("@s_cliente", aRequest, request);
		if (aRequest.readValueParam("@i_valor_producto") != null)
			Utils.copyParam("@i_valor_producto", aRequest, request);
		else
			request.addInputParam("@i_valor_producto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_prod"));

		request.addInputParam("@i_valor_cuenta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));

		Utils.copyParam("@t_trn", aRequest, request);
		request.addInputParam("@i_bl_net", ICTSTypes.SQLCHAR, "S");
		Utils.copyParam("@i_miembro", aRequest, request);
		Utils.copyParam("@i_nregistros", aRequest, request);
		Utils.copyParam("@i_siguiente", aRequest, request);

		request.addOutputParam("@o_registros", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@s_servicio: " + request.readValueParam("@s_servicio"));
			logger.logDebug(CLASS_NAME + "@i_tipo_ejec: " + request.readValueParam("@i_tipo_ejec"));
			logger.logDebug(CLASS_NAME + "@i_valor_moneda: " + request.readValueParam("@i_valor_moneda"));
			logger.logDebug(CLASS_NAME + "@i_filtro_moneda: " + request.readValueParam("@i_filtro_moneda"));
			logger.logDebug(CLASS_NAME + "@s_date: " + request.readValueParam("@s_date"));
			logger.logDebug(CLASS_NAME + "@i_operacion: " + request.readValueParam("@i_operacion"));
			logger.logDebug(CLASS_NAME + "@i_login: " + request.readValueParam("@i_login"));
			logger.logDebug(CLASS_NAME + "@s_cliente: " + request.readValueParam("@s_cliente"));
			logger.logDebug(CLASS_NAME + "@i_valor_producto: " + request.readValueParam("@i_valor_producto"));
			logger.logDebug(CLASS_NAME + "@i_valor_cuenta: " + request.readValueParam("@i_valor_cuenta"));
			logger.logDebug(CLASS_NAME + "@t_trn: " + request.readValueParam("@t_trn"));
			logger.logDebug(CLASS_NAME + "@i_bl_net: " + request.readValueParam("@i_bl_net"));
			logger.logDebug(CLASS_NAME + "@i_miembro: " + request.readValueParam("@i_miembro"));
			logger.logDebug(CLASS_NAME + "@i_nregistros: " + request.readValueParam("@i_nregistros"));
			logger.logDebug(CLASS_NAME + "@i_siguiente: " + request.readValueParam("@i_siguiente"));
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeProductsQuery");
		}
		return wProductsQueryResp;

	}

	/*
	 * Join Response Final RESPONSE_PRODUCTS_QUERY and RESPONSE_PRODUCTS_CORE
	 */
	protected IProcedureResponse joinResponse(Map<String, Object> bag) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		intersectAffiliatedTunning((IProcedureResponse) bag.get(RESPONSE_PRODUCTS_QUERY),
				(IProcedureResponse) bag.get(RESPONSE_PRODUCTS_CORE), bag);
		if (!isFlagConsolidated()) {
			if (logger.isInfoEnabled())
				logger.logInfo("FlagConsolidated isFALSE-joinResponse - Product :"
						+ String.valueOf(bag.get("PRODUCT_SEARCH")));
			wProcedureResponse
					.addResponseBlock(((IProcedureResponse) bag.get(RESPONSE_PRODUCTS_QUERY)).getResultSet(1));
			wProcedureResponse.addResponseBlock(((IProcedureResponse) bag.get(RESPONSE_PRODUCTS_CORE)).getResultSet(1));
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo("FlagConsolidated isTRUE-ConsolidatedResponse - Product: "
						+ String.valueOf(bag.get("PRODUCT_SEARCH")));
			wProcedureResponse.addResponseBlock(((IProcedureResponse) bag.get(RESPONSE_PRODUCTS_CORE)).getResultSet(1));
		}

		if (logger.isInfoEnabled())
			logger.logInfo("RESPONSE_FINAL --> " + wProcedureResponse.getProcedureResponseAsString());
		bag.put(RESPONSE_TRANSACTION, wProcedureResponse);

		return wProcedureResponse;
	}

	private void intersectAffiliatedTunning(IProcedureResponse aResponseAffiliated,
			IProcedureResponse aResponseProductsCore, Map<String, Object> bag) {
		ServerResponse Resp = (ServerResponse) bag.get(SERVER_STATUS_RESP);

		if (logger.isDebugEnabled())
			logger.logDebug("--->>>intersectAffiliatedTunning Inicio--->>");
		int product = 0;
		int currency = 0;
		int pos = -1;
		int posSeparador = -1;
		int wNumColummns = 0;

		String productNumber = "";
		String dato = "";
		int wProd = Integer.parseInt(bag.get("PRODUCT_SEARCH").toString());
		IProcedureResponse wResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Afiliados--->>" + aResponseAffiliated.getProcedureResponseAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Core--->>" + aResponseProductsCore.getProcedureResponseAsString());

		IResultSetHeader metaData = aResponseProductsCore.getResultSet(1).getMetaData();
		IResultSetData dataCtaPrincipal = new ResultSetData();
		List<IResultSetRow> dataListFinal = new ArrayList<IResultSetRow>();
		List<IResultSetRow> dataListPrincipal = new ArrayList<IResultSetRow>();
		List<IResultSetRow> dataListSecundaria = new ArrayList<IResultSetRow>();

		IResultSetRow[] rowsTemplocal = aResponseAffiliated.getResultSet(1).getData().getRowsAsArray();
		IResultSetRow[] rowsTempCore = aResponseProductsCore.getResultSet(1).getData().getRowsAsArray();

		if (rowsTempCore.length > 0)
			wNumColummns = rowsTempCore[0].getColumnsNumber();

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>wNumColummns--->>" + wNumColummns);
			logger.logDebug("--->>>Resp.getOnLine()--->>" + Resp.getOnLine());

		}

		if (Resp.getOnLine()) {
			for (IResultSetRow iResultSetRow : rowsTemplocal) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				product = Integer.parseInt(columns[2].getValue());
				currency = Integer.parseInt(columns[3].getValue());
				productNumber = columns[4].getValue();
				if (logger.isDebugEnabled()) {
					logger.logDebug("Product: " + String.valueOf(product) + " currency: " + String.valueOf(currency)
							+ " productNumber: " + productNumber);
				}
				StringBuilder sb = (StringBuilder) bag.get("SBPRODUCTS" + String.valueOf(product));
				if (logger.isDebugEnabled()) {
					logger.logDebug("sb: " + sb.toString());
					logger.logDebug("wProd: " + wProd);
					logger.logDebug("product: " + product);
					logger.logDebug("currency: " + currency);
					logger.logDebug("productNumber: " + productNumber);
				}

				if ((wProd == product || wProd == ACCOUNTS || wProd == ALL_ACCOUNTS) && sb != null) {
					pos = -1;
					pos = sb.toString().indexOf(productNumber + "/" + String.valueOf(currency));
					if (logger.isDebugEnabled())
						logger.logDebug("pos: " + pos);

					if (pos >= 0) {

						posSeparador = -1;
						posSeparador = sb.toString().substring(pos).indexOf("~");
						if (logger.isDebugEnabled())
							logger.logDebug("posSeparador: " + posSeparador);

						dato = sb.toString().substring(pos, pos + posSeparador);

						String[] arre = dato.split("/");

						if (logger.isDebugEnabled()) {
							logger.logDebug("dato: " + dato);
							logger.logDebug("arre Length: " + arre.length);
						}

						if (arre.length > 0) {
							IResultSetRow row = new ResultSetRow();
							// sumarizando balance
							int posInConsolidate = Integer.parseInt(arre[2]);
							if (logger.isDebugEnabled()) {
								logger.logDebug("posInConsolidate: " + posInConsolidate);
								logger.logDebug("rowsTempCore.length: " + rowsTempCore.length);
							}

							int positionSearchInConsolidate = posInConsolidate;

							/*
							 * if (posInConsolidate == 1) {
							 * positionSearchInConsolidate = posInConsolidate -
							 * 1; }
							 */

							if (rowsTempCore.length > positionSearchInConsolidate
									&& rowsTempCore[positionSearchInConsolidate] != null) {
								IResultSetRowColumnData[] columnsCore = rowsTempCore[positionSearchInConsolidate]
										.getColumnsAsArray();

								logger.logDebug("columnsCore.length: " + columnsCore.length);

								IResultSetRowColumnData columnAlias = new ResultSetRowColumnData(false,
										columns[5].getValue());

								for (int i = 0; i < wNumColummns; i++) {
									IResultSetRowColumnData wColumnData = new ResultSetRowColumnData(false,
											columnsCore[i].getValue());
									if (logger.isDebugEnabled())
										logger.logDebug("Producto -->" + columnsCore[3].getValue());

									if ((wProd == ACCOUNTS || wProd == CHECKING_ACCOUNT || wProd == SAVING_ACCOUNT)
											&& i == 3)// verifico la posicion
														// del Alias
									{
										row.addRowData(i + 1, columnAlias);
									} else if ((wProd == LOAN || wProd == TIME_DEPOSIT || wProd == CREDIT_CARD)
											&& i == 11 && !isFlagConsolidated())
										row.addRowData(i + 1, columnAlias);
									else if (isFlagConsolidated() && (i == 4 || i == 18))// posicion
																							// de
																							// accountName
																							// para
																							// servicio
																							// getSummaryProdutc
										row.addRowData(i + 1, columnAlias);
									else
										row.addRowData(i + 1, wColumnData);
								}
								boolean duplicado = false;
								if (columns[10].getValue().equals("S")) {
									if (logger.isDebugEnabled())
										logger.logDebug("Registro dataListPrincipal: " + dataListPrincipal.toString());

									for (IResultSetRow list : dataListPrincipal) {
										if (logger.isDebugEnabled()) {
											logger.logDebug("list: " + list.toString());
											logger.logDebug("ROW: " + row.toString());
										}
										if (list.toString().equals(row.toString())) {
											duplicado = true;

										}
									}
									if (!duplicado)
										dataListPrincipal.add(row);
								} else {
									if (logger.isDebugEnabled())
										logger.logDebug(
												"Registro dataListSecundaria: " + dataListSecundaria.toString());

									for (IResultSetRow list : dataListSecundaria) {
										if (logger.isDebugEnabled()) {
											logger.logDebug("list: " + list.toString());
											logger.logDebug("ROW: " + row.toString());
										}
										if (list.toString().equals(row.toString())) {
											duplicado = true;
										}
									}

									if (!duplicado)
										dataListSecundaria.add(row);
								}
							}
						}
					}
				}
			}
		} else {
			for (IResultSetRow iResultSetRow : rowsTempCore) {
				IResultSetRow row = new ResultSetRow();
				row = iResultSetRow;
				dataListPrincipal.add(row);
			}
		}

		if (logger.isDebugEnabled())
			logger.logDebug("ListProductPrincipal --->" + dataListPrincipal);
		if (logger.isDebugEnabled())
			logger.logDebug("ListProductSecundarios --->" + dataListSecundaria);

		dataListFinal.addAll(dataListPrincipal);
		dataListFinal.addAll(dataListSecundaria);

		if (logger.isDebugEnabled())
			logger.logDebug("ListProductFinal --->" + dataListFinal.toString());

		for (IResultSetRow obj : dataListFinal) {

			dataCtaPrincipal.addRow(obj);
		}

		IResultSetBlock resultsetBlockPrincipal = new ResultSetBlock(metaData, dataCtaPrincipal);
		wResponse.addResponseBlock(resultsetBlockPrincipal);

		bag.remove(RESPONSE_PRODUCTS_CORE);
		bag.put(RESPONSE_PRODUCTS_CORE, wResponse);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Products Core Intersected -->" + wResponse.getProcedureResponseAsString());
	}

	/*
	 * Intersect products Affiliated vs core products
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void intersectAffiliated(IProcedureResponse aResponseAffiliated, IProcedureResponse aResponseProductsCore,
			Map<String, Object> bag) {

		if (logger.isDebugEnabled())
			logger.logDebug("--->>>intersectAffiliated Inicio--->>");

		String wsName = "";
		Product product = null;
		Currency currency = null;
		IProcedureResponse wResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Afiliados--->>" + aResponseAffiliated.getProcedureResponseAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Core--->>" + aResponseProductsCore.getProcedureResponseAsString());

		IResultSetHeader metaData = aResponseProductsCore.getResultSet(1).getMetaData();

		IResultSetData dataCtaPrincipal = new ResultSetData();
		List<IResultSetRow> dataListFinal = new ArrayList<IResultSetRow>();
		List<IResultSetRow> dataListPrincipal = new ArrayList<IResultSetRow>();
		List<IResultSetRow> dataListSecundaria = new ArrayList<IResultSetRow>();

		int wProd = Integer.parseInt(bag.get("PRODUCT_SEARCH").toString());

		List<IResultSetData> dataProductsAffiliated = (List<IResultSetData>) aResponseAffiliated.getResultSet(1)
				.getData().getRows();

		IResultSetRow[] rowsTemplocal = aResponseAffiliated.getResultSet(1).getData().getRowsAsArray();

		if (logger.isDebugEnabled())
			logger.logDebug("--->>>columns--->>cuenta:" + rowsTemplocal[0].getColumns().get(4) + "--->>>cuentaCobro:"
					+ rowsTemplocal[0].getColumns().get(10));

		IResultSetRow[] rowsTemp = aResponseProductsCore.getResultSet(1).getData().getRowsAsArray();
		Map<String, String> mapProd = new HashMap<String, String>();
		for (IResultSetRow iResultSetRow : rowsTemp) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			if (logger.isDebugEnabled())
				logger.logDebug("-->>>ColProductType: " + getColProductType() + " - ProductType: "
						+ columns[getColProductType()].getValue());
			/*
			 * if(wProd == ACCOUNTS || wProd == ALL_ACCOUNTS || wProd ==
			 * CHECKING_ACCOUNT || wProd == SAVING_ACCOUNT ) {
			 */
			if (wProd == Integer.parseInt(columns[getColProductType()].getValue()) || wProd == ACCOUNTS
					|| wProd == ALL_ACCOUNTS) {
				product = new Product();
				currency = new Currency();
				product.setProductNumber(columns[getColProductNumber()].getValue());
				product.setProductType(Integer.parseInt(columns[getColProductType()].getValue()));
				currency.setCurrencyId(Integer.parseInt(columns[getColCurrency()].getValue()));
				product.setCurrency(currency);
				// Validacion de productos Afiliados contra productos del Core
				IResultSetRow row = (IResultSetRow) CollectionUtils.find(dataProductsAffiliated,
						new ProductPredicate(product));

				if (row != null) {

					IResultSetRow wIResultSetRowNew = new ResultSetRow();// creo
																			// un
																			// nuevo
																			// resultset
																			// row

					wsName = product.getProductNumber() + product.getProductType().toString()
							+ product.getCurrency().getCurrencyId().toString();
					if (logger.isDebugEnabled())
						logger.logDebug("-->>>Clave-" + wsName);
					// Validacion que la interseccion de productos no tenga
					// datos repetidos
					if (mapProd.get(wsName) == null) {
						mapProd.put(wsName, "OK");
						if (logger.isDebugEnabled())
							logger.logDebug("ENCONTRE PRODUCTO " + product.toString() + " IGUAL --->" + row.toString());

						IResultSetRowColumnData[] columnProductLocal = row.getColumnsAsArray();

						if (logger.isDebugEnabled())
							logger.logDebug("--->>AliasLocal " + columnProductLocal[5].getValue());

						IResultSetRowColumnData columnAlias = new ResultSetRowColumnData(false,
								columnProductLocal[5].getValue());
						int wNumColummns = iResultSetRow.getColumnsNumber();// obtengo
																			// la
																			// cantidad
																			// de
																			// columnas
																			// del
																			// core

						for (int i = 0; i < wNumColummns; i++) {
							IResultSetRowColumnData wColumnData = new ResultSetRowColumnData(false,
									columns[i].getValue());// creo un nuevo
															// column
															// data para
															// agregarlo al
															// resulsetRow
							if ((wProd == ACCOUNTS || wProd == CHECKING_ACCOUNT || wProd == SAVING_ACCOUNT) && i == 3)// verifico
																														// la
																														// posicion
																														// del
																														// Alias
							{
								wIResultSetRowNew.addRowData(i + 1, columnAlias);
							} else if ((wProd == LOAN || wProd == TIME_DEPOSIT || wProd == CREDIT_CARD) && i == 11
									&& !isFlagConsolidated())
								wIResultSetRowNew.addRowData(i + 1, columnAlias);
							else if (isFlagConsolidated() && (i == 4 || i == 18))// posicion
																					// de
																					// accountName
																					// para
																					// servicio
																					// getSummaryProdutc
								wIResultSetRowNew.addRowData(i + 1, columnAlias);
							else
								wIResultSetRowNew.addRowData(i + 1, wColumnData);
						}
						if (columnProductLocal[10].getValue().equals("S")) {
							dataListPrincipal.add(wIResultSetRowNew);
						} else {
							dataListSecundaria.add(wIResultSetRowNew);
						}
					}
				}
			}
			/////
			// }
		}

		if (logger.isDebugEnabled())
			logger.logDebug("ListProductPrincipal --->" + dataListPrincipal.toString());
		if (logger.isDebugEnabled())
			logger.logDebug("ListProductSecundarios --->" + dataListSecundaria.toString());

		dataListFinal.addAll(dataListPrincipal);
		dataListFinal.addAll(dataListSecundaria);

		if (logger.isDebugEnabled())
			logger.logDebug("ListProductFinal --->" + dataListFinal.toString());

		for (IResultSetRow obj : dataListFinal) {

			dataCtaPrincipal.addRow(obj);
		}

		IResultSetBlock resultsetBlockPrincipal = new ResultSetBlock(metaData, dataCtaPrincipal);
		wResponse.addResponseBlock(resultsetBlockPrincipal);

		bag.remove(RESPONSE_PRODUCTS_CORE);
		bag.put(RESPONSE_PRODUCTS_CORE, wResponse);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Products Core Intersected -->" + wResponse.getProcedureResponseAsString());

	}
}
