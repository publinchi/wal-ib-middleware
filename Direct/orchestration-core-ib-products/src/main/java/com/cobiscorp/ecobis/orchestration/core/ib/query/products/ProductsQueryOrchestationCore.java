package com.cobiscorp.ecobis.orchestration.core.ib.query.products;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProductQueryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProductQueryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryProductBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreConsolidateAccountsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQuery;

//import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 * Plugin for do see process about client accounts.
 *
 * @since Sept 11, 2014
 * @author cecheverria
 * @version 1.0.0
 *
 */
@Component(name = "ProductsQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ProductsQueryOrchestationCore"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "ProductsQueryOrchestationCore") })
public class ProductsQueryOrchestationCore extends QueryProductBaseTemplate {

	private static final String DATEFORMATQUERY = "MM/dd/yyyy";
	private static final String COBIS_CONTEXT = "COBIS";
	protected static final String CLASS_NAME = " >-----> ";
	private static final int ALL_ACCOUNTS = 0;// TODOS LOS PRODUCTOS PARA
	// POSICION CONSOLIDADA
	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int LOAN = 7;
	private static final int TIME_DEPOSIT = 14;
	private static final int CREDIT_CARD = 83;
	private static final int NOT_PRODUCTS = -1;
	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(ProductsQueryOrchestationCore.class);

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQuery.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceQuery CoreServiceQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceQuery service) {
		CoreServiceQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceQuery service) {
		CoreServiceQuery = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	private ICoreServer CoreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		CoreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		CoreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreConsolidateAccountsQuery.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreConsolidateAccountsQuery", unbind = "unbindCoreConsolidateAccountsQuery")
	private ICoreConsolidateAccountsQuery CoreConsolidateAccountsQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreConsolidateAccountsQuery(ICoreConsolidateAccountsQuery service) {
		CoreConsolidateAccountsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreConsolidateAccountsQuery(ICoreConsolidateAccountsQuery service) {
		CoreConsolidateAccountsQuery = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceCardsQuery.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServiceCardsQuery", unbind = "unbindCoreServiceCardsQuery")
	private ICoreServiceCardsQuery CoreServiceCardsQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceCardsQuery(ICoreServiceCardsQuery service) {
		CoreServiceCardsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceCardsQuery(ICoreServiceCardsQuery service) {
		CoreServiceCardsQuery = null;
	}

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader reader) {
		if (logger.isInfoEnabled())
			logger.logInfo("Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");

	}

	/*
	 * Get Status Server
	 */
	@Override
	protected ServerResponse executeServerStatus(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		ServerRequest serverRequest = new ServerRequest();
		ServerResponse wServerStatusResp = null;

		serverRequest.setChannelId(request.readValueParam("@s_servicio").toString());
		serverRequest.setFormatDate(101);
		serverRequest.setCodeTransactionalIdentifier("1800039");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request Corebanking: " + serverRequest);

		try {
			wServerStatusResp = CoreServer.getServerStatus(serverRequest);
			if (logger.isDebugEnabled())
				logger.logDebug("Response Corebanking: " + wServerStatusResp);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + " Saliendo de executeServerStatus");

		} catch (CTSServiceException e) {
			e.printStackTrace();
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			return null;
		}
		return wServerStatusResp;
	}

	private IProcedureResponse transformProcedureResponse(ConsolidateResponse aConsolidateResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (!isValidConsolidateResponse(aConsolidateResponse))
			return null;

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productDescription", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productAbbreviation", ICTSTypes.SQLVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productBalance", ICTSTypes.SQLMONEY, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value2", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value3", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value4", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyNemonic", ICTSTypes.SQLVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productAlias", ICTSTypes.SQLVARCHAR, 50));

		if (logger.isDebugEnabled()) {
			logger.logDebug("ARMANDO RESPONSE PRODUCTS ALFA JCOS");
		}
		for (ProductConsolidate product : aConsolidateResponse.getProductCollection()) {
			IResultSetRow row = new ResultSetRow();
			if (logger.isDebugEnabled()) {
				logger.logDebug(" *** ProductNumber: " + product.getProduct().getProductNumber());
				logger.logDebug(" *** ProductType: " + product.getProduct().getProductType().toString());
				logger.logDebug(" Currency Id: " + product.getCurrency().getCurrencyId().toString());
				logger.logDebug(" 	JOS BALANCE: " + product.getBalance().getTotalBalance().toString());
			}

			row.addRowData(1, new ResultSetRowColumnData(false, product.getProduct().getProductType().toString())); // productType
			row.addRowData(2, new ResultSetRowColumnData(false, product.getProduct().getProductDescription())); // productName
			row.addRowData(3, new ResultSetRowColumnData(false, product.getProduct().getProductNemonic())); // ProductNemonic
			row.addRowData(4, new ResultSetRowColumnData(false, product.getProduct().getProductNumber())); // ProductNumber
			row.addRowData(5, new ResultSetRowColumnData(false, product.getBalance().getTotalBalance().toString())); // productBalance

			row.addRowData(6, new ResultSetRowColumnData(false, "")); // value2
			row.addRowData(7, new ResultSetRowColumnData(false, "")); // value3
			row.addRowData(8, new ResultSetRowColumnData(false, "")); // value3

			row.addRowData(9, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyId().toString())); // currencyId
			row.addRowData(10, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyNemonic())); // currencyNemonic
			row.addRowData(11, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyDescription())); // currencyName
			row.addRowData(12, new ResultSetRowColumnData(false, product.getProduct().getProductAlias())); // productAlias

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean isValidConsolidateResponse(ConsolidateResponse aConsolidateResponse) {
		String messageError = "OK";

		for (ProductConsolidate product : aConsolidateResponse.getProductCollection()) {

			messageError += product.getProduct().getProductType() == null ? "Product Type can't be null" : ""; // productType
			messageError += product.getProduct().getProductDescription() == null ? "Product Description can't be null" : ""; // productName
			messageError += product.getProduct().getProductNemonic() == null ? "Product Nemonic can't be null" : ""; // ProductNemonic
			messageError += product.getBalance().getTotalBalance() == null ? "Total Balance can't be null" : ""; // productBalance

			messageError += product.getCurrency().getCurrencyId() == null ? "Currency Id can't be null" : ""; // currencyId
			messageError += product.getCurrency().getCurrencyNemonic() == null ? "Currency Nemonic can't be null" : ""; // currencyNemonic
			messageError += product.getCurrency().getCurrencyDescription() == null ? "Currency Description can't be null" : ""; // currencyName
			messageError += product.getProduct().getProductAlias() == null ? "Product Alias can't be null" : ""; // productAlias

		}

		if (!messageError.trim().equals("OK"))
			throw new IllegalArgumentException(messageError);

		return true;

	}

	public int getProductIdToExecute(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia getProductIdToExecute");
		}

		IProcedureResponse wProcResponseAccountsClientLocal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);

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

	/*
	 * Get All Accounts
	 */
	@Override
	protected IProcedureResponse getAllAccountsCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null, responseProductsQuery;
		boolean flagSetting = true;// true: primera iteracion, false:
									// otras_iteracion

		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		ConsolidateResponse wConsolidateAccount = new ConsolidateResponse();
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Ejecutando getAllAccountsCore: " + request.getProcedureRequestAsString());
		try {
			setColProductNumber(0);
			setColCurrency(1);
			setColProductType(2);

			ProductQueryRequest productQueryRequest = transformRequestToDto(request);

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (Resp.getOnLine()) {
				if (logger.isDebugEnabled())
					logger.logDebug(CLASS_NAME + " Ejecutando getAllAccountsCore server is Online = TRUE ");
				// llamado del sp del core
				ConsolidateResponse consolidateResponseCtaCte = null;
				ConsolidateResponse consolidateResponseCtaAho = null;
				// get RESPONSE_PRODUCTS_QUERY
				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);

				if (logger.isDebugEnabled())
					logger.logDebug("--->getAllAccountsCore-Response execution local-->>>" + responseProductsQuery.getProcedureResponseAsString());

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);

				Client wCliente = consolidateRequest.getClient();

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					// validando ep_miembro
					if (logger.isDebugEnabled())
						logger.logDebug("---->getAllAccountsCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);
						consolidateRequest.setHaveToAddCountCte(true);

						int wProd = this.getProductIdToExecute(aBagSPJavaOrchestration);

						if (ALL_ACCOUNTS == wProd || CHECKING_ACCOUNT == wProd) {
							consolidateResponseCtaCte = CoreConsolidateAccountsQuery.getConsolidateCheckingAccountByClient(consolidateRequest);
						}

						consolidateResponseCtaAho = CoreConsolidateAccountsQuery.getConsolidateSavingAccountByClient(consolidateRequest);
						// joinResponse local(unifica cta_aho y cta_cte)
						wConsolidateAccount = joinResponseLocal(consolidateResponseCtaCte, consolidateResponseCtaAho);

						if (wConsolidateAccount.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->wConsolidateAccount conDatos isNotNull-returncode:" + consolidateResponseCtaAho.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(wConsolidateAccount.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(wConsolidateAccount.getProductCollection());

						} else if (logger.isDebugEnabled())
							logger.logDebug("---->wConsolidateAccount isNULL-returncode:" + consolidateResponseCtaAho.getReturnCode());

					}
				}
				// Seteando codigo de Error
				wSummaryQueryResp.setSuccess(wConsolidateAccount.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setReturnCode(wConsolidateAccount.getReturnCode());
				wSummaryQueryResp.setMessages(wConsolidateAccount.getMessages());
				if (consolidateResponseCtaCte != null) {
					aBagSPJavaOrchestration.put("SBPRODUCTS3", consolidateResponseCtaCte.getSbProducts());
				}
				aBagSPJavaOrchestration.put("SBPRODUCTS4", consolidateResponseCtaAho.getSbProducts());
				// Utils de capa commons-Se envia mapSP para manejo de errores
				response = Utils.transformConsolidateResponseToIProcedureResponseAccounts(wSummaryQueryResp, aBagSPJavaOrchestration);
			} else {
				if (logger.isDebugEnabled())
					logger.logDebug(CLASS_NAME + " Otra vez por aquí ITOOOOO ");

				// setColProductNumber(0);
				// setColCurrency(19);
				// setColProductType(1);

				setColProductNumber(0);
				setColCurrency(1);
				setColProductType(2);

				if (logger.isDebugEnabled())
					logger.logDebug(CLASS_NAME + " Ejecutando getAllAccountsCore server is Online = FALSE ");
				response = this.getAccountBalanceLocal(productQueryRequest);
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando getAllAccountsCore Respuesta Final !!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/**
	 * When The product is ALL_ACCOUNTS , it has to join Response with checking account and saving account
	 */
	public ConsolidateResponse joinResponseLocal(ConsolidateResponse consolidateResponse1, ConsolidateResponse consolidateResponse2) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Procedure Response Cta CTE " + consolidateResponse1);
			logger.logInfo(CLASS_NAME + "Procedure Response Cta AHO " + consolidateResponse2);
		}
		ConsolidateResponse consolidateResponse = new ConsolidateResponse();

		if (consolidateResponse1 != null && consolidateResponse1.getProductCollection() == null)
			if (logger.isInfoEnabled())
				logger.logInfo("consolidateResponse1.getProductCollection is null ");
		if (consolidateResponse2.getProductCollection() == null)
			if (logger.isInfoEnabled())
				logger.logInfo("consolidateResponse2.getProductCollection is null ");

		// consolidateResponse.setProductCollection(consolidateResponse1.getProductCollection());
		// consolidateResponse.getProductCollection().addAll(consolidateResponse2.getProductCollection());
		// Validaciones para manejor de error
		if (consolidateResponse1 != null && consolidateResponse1.getReturnCode() != 0 && consolidateResponse2.getReturnCode() != 0) {
			consolidateResponse.setReturnCode(consolidateResponse1.getReturnCode());// seteo
																					// el
																					// codigo
																					// de
																					// error
																					// devuelto
																					// por
																					// el
																					// sp
			consolidateResponse.setMessages(consolidateResponse1.getMessages());
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "*** CtaAho - CtaCte sin Datos, seteando codigo de error <>0.ErrorCode:" + consolidateResponse1.getReturnCode());
		} else {
			// Se setean en verdadero ya que uno de los dos response pueden
			// tener datos
			consolidateResponse.setSuccess(true);
			consolidateResponse.setReturnCode(0);
			if (logger.isInfoEnabled())
				if (consolidateResponse1 != null)
					logger.logInfo(CLASS_NAME + "*** CtaAho o CtaCte CON Datos, seteando codigo de error=0.ErrorCodeCtaCte:" + consolidateResponse1.getReturnCode() + " -ErrorCodeCtaAho:"
							+ consolidateResponse2.getReturnCode());
			if (consolidateResponse1 != null && consolidateResponse1.getReturnCode() == 0 && consolidateResponse2.getReturnCode() == 0) {
				consolidateResponse.setProductCollection(consolidateResponse1.getProductCollection());
				consolidateResponse.getProductCollection().addAll(consolidateResponse2.getProductCollection());
				return consolidateResponse;
			}
			if (consolidateResponse1 != null && consolidateResponse1.getProductCollection() != null) {
				consolidateResponse.setProductCollection(consolidateResponse1.getProductCollection());
				return consolidateResponse;
			}
			if (consolidateResponse2.getProductCollection() != null) {
				consolidateResponse.setProductCollection(consolidateResponse2.getProductCollection());
				return consolidateResponse;
			}
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Response Unido :" + consolidateResponse);
		return consolidateResponse;
	}

	/**
	 * 
	 * @param productQueryRequest
	 * @return ProductQueryResponse <b>NUMBER_ACCOUNT</b>Number account<br>
	 *         <b>CURRENCY_ID</b> <i>[Type=SQLINT2, MaxLength=6]</i> - Identificator currency of type account<br>
	 *         <b>PRODUCT_ID</b> <i>[Type=SQLINT4, MaxLength=11]</i> - Identificator product of account<br>
	 *         <b>ALIAS_ACCOUNT</b> <i>[Type=SQLVARCHAR, MaxLength=32]</i> - Account name registered<br>
	 *         <b>CURRENCY_DESCRIPTION</b> <i>[Type=SQLVARCHAR, MaxLength=255]</i> - Description of type currency<br>
	 *         <b>PRODUCT_DESCRIPTION</b> <i>[Type=SQLVARCHAR, MaxLength=255]</i> - Description of type product<br>
	 *         <b>PRODUCT_NEMONIC</b> <i>[Type=SQLCHAR, MaxLength=3]</i> - Nemonic or acronym of type product<br>
	 *         <b>BALANCE_ROTATE</b> <i>[Type=SQLMONEY, MaxLength=21]</i> - Rotate account in account<br>
	 *         <b>EQUITY_BALANCE</b> <i>[Type=SQLMONEY, MaxLength=21]</i> - Equity balance in account<br>
	 *         <b>CURRENCY_NEMONIC</b> <i>[Type=SQLCHAR, MaxLength=10]</i> - Nemonic or acronym of type currency<br
	 * 
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	private IProcedureResponse getAccountBalanceLocal(ProductQueryRequest productQueryRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO: getAccountBalanceLocal " + productQueryRequest.toString());

		ProductQueryResponse productQueryResponse = new ProductQueryResponse();

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN, productQueryRequest.getSessionIdCore());
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, productQueryRequest.getSessionIdIB());
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800054");

		anOriginalRequest.setSpName("cob_cuentas..sp_cons_saldos_ctas_bv");

		if (productQueryRequest.getSessionIdCore() != null) {
			anOriginalRequest.addInputParam("@s_ssn", ICTSTypes.SYBINTN, productQueryRequest.getSessionIdCore());
		}
		if (productQueryRequest.getSessionIdIB() != null) {
			anOriginalRequest.addInputParam("@s_ssn_branch", ICTSTypes.SYBINTN, productQueryRequest.getSessionIdIB());
		}
		if (productQueryRequest.getBalanceProductRequest() != null && productQueryRequest.getBalanceProductRequest().getProcessDate() != null) {
			anOriginalRequest.addInputParam("@s_date", ICTSTypes.SYBDATETIME, "" + productQueryRequest.getBalanceProductRequest().getProcessDate());
		}
		if (productQueryRequest.getBalanceProductRequest() != null && productQueryRequest.getBalanceProductRequest().getOfficeAccount() != null) {
			anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "" + productQueryRequest.getBalanceProductRequest().getOfficeAccount().getId());
		}
		if (productQueryRequest.getUser() != null && productQueryRequest.getUser().getLogin() != null) {
			anOriginalRequest.addInputParam("@s_user", ICTSTypes.SYBVARCHAR, "" + productQueryRequest.getUser().getLogin());
		}
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SYBVARCHAR, session.getTerminal());

		anOriginalRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SYBCHAR, "L");
		anOriginalRequest.addInputParam("@i_consolidado", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_nregistros", ICTSTypes.SYBINT4, "" + productQueryRequest.getQueryResultsNumber());
		anOriginalRequest.addInputParam("@i_siguiente", ICTSTypes.SYBINT4, "" + productQueryRequest.getNextQueryNumber());

		anOriginalRequest.addInputParam("@i_combo", ICTSTypes.SYBCHAR, "S");

		if (productQueryRequest.getUser() != null && productQueryRequest.getUser().getIdCustomer() != null) {
			anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SYBINT4, "" + productQueryRequest.getUser().getIdCustomer());
		}
		if (productQueryRequest.getChannelId() != null) {
			anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "" + productQueryRequest.getChannelId());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Ejecutando getAccountBalanceLocal data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta getAccountBalanceLocal " + response.getProcedureResponseAsString());

		return response;
	}

	/**
	 * Transform request to Dto Object
	 * 
	 * @param request
	 * @return
	 * @throws CTSServiceException
	 */
	private ProductQueryRequest transformRequestToDto(IProcedureRequest request) throws CTSServiceException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de ProcedureRequest a DTO ProductQueryRequest" + request);

		ProductQueryRequest productQueryRequest = new ProductQueryRequest();
		BalanceProduct balanceProductRequest = new BalanceProduct();
		Office officeAccount = new Office();
		Client user = new Client();
		Context context = ContextManager.getContext();
		logger.logInfo("Hola 111 " + context);
		CobisSession session = null;
		DateFormat dateFormat = new SimpleDateFormat(DATEFORMATQUERY);

		if (request.readValueParam("@s_ssn") != null)
			productQueryRequest.setSessionIdCore(request.readValueParam("@s_ssn").toString());
		if (request.readValueParam("@s_ssn_branch") != null)
			productQueryRequest.setSessionIdIB(request.readValueParam("@s_ssn_branch").toString());

		if (request.readValueParam("@s_date") != null) {
			try {
				Date processDate = dateFormat.parse(request.readValueParam("@s_date"));
				balanceProductRequest.setProcessDate(processDate);
			} catch (ParseException e) {
				throw new CTSServiceException(e.getMessage());
			}
		}
		if (context != null && context.getSession() != null) // validacion para peticion desde CEN
			session = (CobisSession) context.getSession();

		if (session != null && session.getOffice() != null) {
			officeAccount.setId(Integer.valueOf(session.getOffice()));
			balanceProductRequest.setOfficeAccount(officeAccount);
		}
		if (session != null && session.getUser() != null) {
			user.setLogin(session.getUser());

		}

		if (request.readValueParam("@s_cliente") != null)
			user.setIdCustomer(request.readValueParam("@s_cliente").toString());
		if (request.readValueParam("@s_servicio") != null)
			productQueryRequest.setChannelId(request.readValueParam("@s_servicio").toString());

		productQueryRequest.setQueryResultsNumber(20);
		productQueryRequest.setNextQueryNumber(0);
		productQueryRequest.setUser(user);

		productQueryRequest.setBalanceProductRequest(balanceProductRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta productQueryRequest" + productQueryRequest);
		return productQueryRequest;
	}

	/*
	 * get Saving Accounts Core
	 */
	@Override
	protected IProcedureResponse getSavingAccountsCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("--->Inicia getSavingAccountsCore--->>>");
		IProcedureResponse responseProductsQuery = null, response = null;
		ConsolidateResponse wConsolidateAccount = new ConsolidateResponse();
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		// int wcont = 0;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		try {
			setColProductNumber(0);
			setColCurrency(1);
			setColProductType(2);
			ProductQueryRequest productQueryRequest = transformRequestToDto(request);

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (Resp.getOnLine()) {
				if (logger.isDebugEnabled())
					logger.logDebug("--->getSavingAccountsCore-getOnline is TRUE>>>");
				// llamado del sp del core
				ConsolidateResponse consolidateResponseCtaAho = null;
				// get RESPONSE_PRODUCTS_QUERY
				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug("--->getSavingAccountsCore- Response execution local RESPONSE_PRODUCTS_QUERY-->>>" + responseProductsQuery.getProcedureResponseAsString());
				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);
				// if (logger.isDebugEnabled())
				// logger.logDebug("--->GCO Consolidate
				// request-->>>"+ConsolidateRequest);
				// consolidateRequest.getClient();
				Client wCliente = consolidateRequest.getClient();
				//
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsTemp) {

					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					// validando ep_miembro
					if (logger.isDebugEnabled())
						logger.logDebug("---->getSavingAccountsCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						consolidateResponseCtaAho = CoreConsolidateAccountsQuery.getConsolidateSavingAccountByClient(consolidateRequest);
						if (consolidateResponseCtaAho.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consolidateResponseCtaAho conDatos isNotNull-returncode:" + consolidateResponseCtaAho.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(consolidateResponseCtaAho.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(consolidateResponseCtaAho.getProductCollection());

						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consolidateResponseCtaAho isNULL-returncode:" + consolidateResponseCtaAho.getReturnCode());
					}
				}
				// Seteando codigo de Error
				wSummaryQueryResp.setSuccess(consolidateResponseCtaAho.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setReturnCode(consolidateResponseCtaAho.getReturnCode());
				wSummaryQueryResp.setMessages(consolidateResponseCtaAho.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS4", consolidateResponseCtaAho.getSbProducts());
				// Utils de capa commons-se envia mapSPOrchestration para menejo
				// de errores
				response = Utils.transformConsolidateResponseToIProcedureResponseAccounts(wSummaryQueryResp, aBagSPJavaOrchestration);
			} else {
				if (logger.isDebugEnabled())
					logger.logDebug("--->getSavingAccountsCore-getOnline is FALSE>>>");
				response = this.getAccountBalanceLocal(productQueryRequest);
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		if (logger.isInfoEnabled())
			logger.logInfo("getSavingAccountsCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/*
	 * get Checking Accounts Core
	 */
	@Override
	protected IProcedureResponse getCheckingAccountsCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("--->Inicia getCheckingAccountsCore--->>>");
		IProcedureResponse responseProductsQuery = null, response = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		// int wcont = 0;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		try {
			setColProductNumber(0);
			setColCurrency(1);
			setColProductType(2);
			ProductQueryRequest productQueryRequest = transformRequestToDto(request);

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (Resp.getOnLine()) {
				if (logger.isDebugEnabled())
					logger.logDebug("--->getCheckingAccountsCore-getOnline is TRUE>>>");
				// llamado del sp del core
				ConsolidateResponse consolidateResponseCtaCte = null;

				// get RESPONSE_PRODUCTS_QUERY
				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug("--->getCheckingAccountsCore- Response execution local RESPONSE_PRODUCTS_QUERY-->>>" + responseProductsQuery.getProcedureResponseAsString());

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);

				Client wCliente = consolidateRequest.getClient();
				// //
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsTemp) {

					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					// validando ep_miembro
					if (logger.isDebugEnabled())
						logger.logDebug("---->getCheckingAccoutsCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						consolidateResponseCtaCte = CoreConsolidateAccountsQuery.getConsolidateCheckingAccountByClient(consolidateRequest);
						if (consolidateResponseCtaCte.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consolidateResponseCtaCte conDatos isNotNull-returncode:" + consolidateResponseCtaCte.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(consolidateResponseCtaCte.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(consolidateResponseCtaCte.getProductCollection());

						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consolidateResponseCtaCte  isNULL-returncode:" + consolidateResponseCtaCte.getReturnCode());
					}
				}
				// Seteando codigo de Error
				wSummaryQueryResp.setSuccess(consolidateResponseCtaCte.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setReturnCode(consolidateResponseCtaCte.getReturnCode());
				wSummaryQueryResp.setMessages(consolidateResponseCtaCte.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS3", consolidateResponseCtaCte.getSbProducts());
				// Utils de capa commons-se envia mapSPOrchestration para manejo
				// de Errores
				response = Utils.transformConsolidateResponseToIProcedureResponseAccounts(wSummaryQueryResp, aBagSPJavaOrchestration);
			} else {
				if (logger.isDebugEnabled())
					logger.logDebug("--->getCheckingAccountsCore-getOnline is FALSE>>>");
				response = this.getAccountBalanceLocal(productQueryRequest);
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		if (logger.isInfoEnabled())
			logger.logInfo("getSavingAccountsCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/*
	 * get Loans Core
	 */
	@Override
	protected IProcedureResponse getLoansCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse responseProductsQuery = null, response = null;
		// int wcont=0;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		setColProductNumber(3);// 3
		setColCurrency(8);// 8
		setColProductType(0);// 0
		ConsolidateResponse consiladateResLoan = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		if (logger.isDebugEnabled())
			logger.logDebug("---->Inicia getLoansCore-original request--->>>" + request.getProcedureRequestAsString());
		try {
			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (!Resp.getOfflineWithBalances()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("--->Mode OfflineWithBalances: FALSE--->>>");
					logger.logDebug("--->ProductsQueryOrchestration - GetLoansCore - OfflineWithBalances");
				}

				wSummaryQueryResp.setReturnCode(-1);
				wSummaryQueryResp.setSuccess(false);
				response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);

				return response;
			} else {

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);

				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug("---->getLoansCore-Get RESPONSE_PRODUCTS_QUERY--->>>" + responseProductsQuery.getProcedureResponseAsString());
				Client wCliente = consolidateRequest.getClient();
				//
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (logger.isDebugEnabled())
						logger.logDebug("---->getLoansCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						consiladateResLoan = CoreConsolidateAccountsQuery.getConsolidateLoanAccountByClient(consolidateRequest);
						if (consiladateResLoan.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consiladateResLoan tieneData isNotNull-returncode:" + consiladateResLoan.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(consiladateResLoan.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(consiladateResLoan.getProductCollection());
						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consiladateResLoan isNULL-returnCode: " + consiladateResLoan.getReturnCode());
					}
				}
				// Setenado Codigo de Error
				wSummaryQueryResp.setReturnCode(consiladateResLoan.getReturnCode());
				wSummaryQueryResp.setSuccess(consiladateResLoan.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setMessages(consiladateResLoan.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS7", consiladateResLoan.getSbProducts());
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);

		if (logger.isInfoEnabled())
			logger.logInfo("getLoansCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/*
	 * get Time Deposits Core
	 */
	@Override
	protected IProcedureResponse getTimeDepositsCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse responseProductsQuery = null, response = null;
		ConsolidateResponse consiladateRespTimeDeposit = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		Client wCliente = new Client();
		// int wcont =0;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		setColProductNumber(3);
		setColCurrency(8);
		setColProductType(0);
		try {

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (!Resp.getOfflineWithBalances()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("--->Mode OfflineWithBalances: FALSE--->>>");
					logger.logDebug("--->ProductsQueryOrchestration - GetTimeDepositCore - OfflineWithBalances");
				}

				wSummaryQueryResp.setReturnCode(-1);
				wSummaryQueryResp.setSuccess(false);
				response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);

				return response;
			} else {
				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);

				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug("---->getTimeDepositsCore-Get RESPONSE_PRODUCTS_QUERY--->>>" + responseProductsQuery.getProcedureResponseAsString());

				if (consolidateRequest == null) {
					if (logger.isDebugEnabled())
						logger.logDebug("---->getTimeDepositsCore---->>>consolidateRequest ISNULL set idClient 0");
					wCliente.setId("0");
				} else {
					if (logger.isDebugEnabled())
						logger.logDebug("---->getTimeDepositsCore--->>>consolidateRequest isNOTNULL id_client:--->>" + consolidateRequest.getClient().getId());
					wCliente = consolidateRequest.getClient();
					wCliente.setId("0");
				}

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (logger.isDebugEnabled())
						logger.logDebug("---->getTimeDepositsCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						consiladateRespTimeDeposit = CoreConsolidateAccountsQuery.getConsolidateFixedTermDepositAccountByClient((ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST));
						if (consiladateRespTimeDeposit.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consiladateRespTimeDeposit tieneData isNotNull-returncode:" + consiladateRespTimeDeposit.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(consiladateRespTimeDeposit.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(consiladateRespTimeDeposit.getProductCollection());
						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consiladateRespTimeDeposit isNULL-returnCode: " + consiladateRespTimeDeposit.getReturnCode());
					}
				}
				// Seteando Codigo de Error
				wSummaryQueryResp.setReturnCode(consiladateRespTimeDeposit.getReturnCode());
				wSummaryQueryResp.setSuccess(consiladateRespTimeDeposit.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setMessages(consiladateRespTimeDeposit.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS14", consiladateRespTimeDeposit.getSbProducts());
			}
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);

		if (logger.isInfoEnabled())
			logger.logInfo("getTimeDepositsCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/*
	 * get Credit Cards Core
	 */
	@Override
	protected IProcedureResponse getCreditCardsCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		ConsolidateResponse consiladateRespCreditCards = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		IProcedureResponse responseProductsQuery = null, response = null;
		// int wcont=0;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		setColProductNumber(3);
		setColCurrency(8);
		setColProductType(0);
		try {
			if (logger.isInfoEnabled())
				logger.logInfo("--->Inicia - getCreditCardsCore---->>>>");
			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (!Resp.getOfflineWithBalances()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("--->Mode OfflineWithBalances: FALSE--->>>");
					logger.logDebug("--->ProductsQueryOrchestration - GetCreditCardsCore - OfflineWithBalances");
				}

				wSummaryQueryResp.setReturnCode(-1);
				wSummaryQueryResp.setSuccess(false);
				response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);

				return response;
			} else {

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST);

				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (responseProductsQuery == null)
					if (logger.isInfoEnabled())
						logger.logInfo("--->getCreditCardsCore - responseProductsQuery IS NULL---->>>>");
				if (logger.isDebugEnabled())
					logger.logDebug("---->getCreditCardsCore-Get RESPONSE_PRODUCTS_QUERY--->>>" + responseProductsQuery.getProcedureResponseAsString());
				Client wCliente = consolidateRequest.getClient();
				// /
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (logger.isDebugEnabled())
						logger.logDebug("---->getCreditCardsCore-ValidateClient-IDClientConsolidateReq->>>" + wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						// consiladateResLoan =
						// CoreConsolidateAccountsQuery.getConsolidateLoanAccountByClient(consolidateRequest);
						consiladateRespCreditCards = CoreConsolidateAccountsQuery.getConsolidateCreditCardByClient((ConsolidateRequest) aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST));
						if (consiladateRespCreditCards.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consiladateRespCreditCards tieneData isNotNull-returncode:" + consiladateRespCreditCards.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(consiladateRespCreditCards.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection().addAll(consiladateRespCreditCards.getProductCollection());
						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consiladateRespCreditCards isNULL-returnCode: " + consiladateRespCreditCards.getReturnCode());
					}
				}
				// Seteando Codigo de Error
				wSummaryQueryResp.setReturnCode(consiladateRespCreditCards.getReturnCode());
				wSummaryQueryResp.setSuccess(consiladateRespCreditCards.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setMessages(consiladateRespCreditCards.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS83", consiladateRespCreditCards.getSbProducts());
			}
			// response =
			// CoreConsolidateAccountsQuery.getConsolidateCreditCardByClient((ConsolidateRequest)aBagSPJavaOrchestration.get(CONSOLIDATE_REQUEST));

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		response = Utils.transformConsolidateResponseToIProcedureResponse(wSummaryQueryResp, aBagSPJavaOrchestration);
		if (logger.isInfoEnabled())
			logger.logInfo("getCreditCardsCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	/* 
	 * 
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> bag) {

		return (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);
	}

	/**
	 * Execute transfer first step service
	 * <p>
	 * This method is the main executor of transactional contains the original input parameters.
	 *
	 * @param anOriginalRequest - Information original sended by user's.
	 * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		try {
			// Valida Inyección de dependencias

			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("CoreServer", CoreServer);
			mapInterfaces.put("CoreServiceQuery", CoreServiceQuery);
			mapInterfaces.put("CoreServiceCardsQuery", CoreServiceCardsQuery);
			mapInterfaces.put("CoreConsolidateAccountsQuery", CoreConsolidateAccountsQuery);

			Utils.validateComponentInstance(mapInterfaces);

			setGetSchemaProduct(true); // true = join central y local, false
										// devuelve solo central
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Ejecutando executeJavaOrchestration ---->>SchemaProduct-" + isGetSchemaProduct());

			IProcedureResponse wProcedureResponse = executeStepsQueryBase(anOriginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);

		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);

		}
	}
}
