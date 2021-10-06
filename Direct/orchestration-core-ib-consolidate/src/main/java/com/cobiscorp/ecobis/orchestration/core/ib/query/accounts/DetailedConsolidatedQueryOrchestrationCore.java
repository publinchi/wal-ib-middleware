package com.cobiscorp.ecobis.orchestration.core.ib.query.accounts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
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
import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProductQueryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProductQueryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreConsolidateAccountsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceClient;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQuery;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryProductBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * This plugin is used.
 *
 * @since July 08, 2014
 * @author Carlos Echeverría
 * @version 1.0.0
 *
 */

@Component(name = "DetailedConsolidatedQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DetailedConsolidatedQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DetailedConsolidatedQueryOrchestrationCore") })
public class DetailedConsolidatedQueryOrchestrationCore extends QueryProductBaseTemplate {

	private static final String DATEFORMATQUERY = "MM/dd/yyyy";
	private static final String COBIS_CONTEXT = "COBIS";

	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int LOAN = 7;
	private static final int TIME_DEPOSIT = 14;
	private static final int CREDIT_CARD = 83;

	private static final int ALL_ACCOUNTS = 0;

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DetailedConsolidatedQueryOrchestrationCore.class);

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
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
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
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
	@Reference(referenceInterface = ICoreConsolidateAccountsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreConsolidateAccountsQuery", unbind = "unbindCoreConsolidateAccountsQuery")
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
	@Reference(referenceInterface = ICoreServiceCardsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCardsQuery", unbind = "unbindCoreServiceCardsQuery")
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

	@Reference(referenceInterface = ICoreServiceClient.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceClient", unbind = "unbindCoreServiceClient")
	private ICoreServiceClient coreServiceClient;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceClient(ICoreServiceClient service) {
		coreServiceClient = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceClient(ICoreServiceClient service) {
		coreServiceClient = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServerResponse executeServerStatus(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
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

	@Override
	protected IProcedureResponse getAllAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null, responseProductsQuery;
		boolean flagSetting = true;// true: primera iteracion, false: otras
									// iteracion
		setFlagConsolidated(true);
		// IProcedureResponse responseProductsQuery = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		ConsolidateResponse wConsolidateAccount = new ConsolidateResponse();
		if (logger.isDebugEnabled())
			logger.logDebug(
					"--->Inicia getAllAccountsCore-request original>>>" + request.getProcedureRequestAsString());
		try {
			setColProductNumber(0);
			setColCurrency(19);
			setColProductType(1);
			ProductQueryRequest productQueryRequest = transformRequestToDto(request);

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);
			if (Resp.getOnLine()) {
				if (logger.isDebugEnabled())
					logger.logDebug("--->getAllAccountsCore- getOnline is True>>>");
				// llamado del sp del core
				ConsolidateResponse consolidateResponseCtaCte = null;
				ConsolidateResponse consolidateResponseCtaAho = null;
				// get RESPONSE_PRODUCTS_QUERY
				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);

				if (logger.isDebugEnabled())
					logger.logDebug("--->getAllAccountsCore-Response execution local-->>>"
							+ responseProductsQuery.getProcedureResponseAsString());
				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration
						.get(CONSOLIDATE_REQUEST);

				Client wCliente = consolidateRequest.getClient();
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					// validando ep_miembro
					if (logger.isDebugEnabled())
						logger.logDebug("---->getAllAccountsCore-ValidateClient-IDClientConsolidateReq->>>"
								+ wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>" + columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);
						consolidateResponseCtaCte = CoreConsolidateAccountsQuery
								.getConsolidateCheckingAccountByClient(consolidateRequest);
						consolidateResponseCtaAho = CoreConsolidateAccountsQuery
								.getConsolidateSavingAccountByClient(consolidateRequest);
						// joinResponse local(unifica cta_aho y cta_cte)
						wConsolidateAccount = joinResponseLocal(consolidateResponseCtaCte, consolidateResponseCtaAho);

						if (wConsolidateAccount.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->wConsolidateAccount conDatos isNotNull-returncode:"
										+ consolidateResponseCtaAho.getReturnCode());
							if (flagSetting) {
								wSummaryQueryResp.setProductCollection(wConsolidateAccount.getProductCollection());
								flagSetting = false;
							} else
								wSummaryQueryResp.getProductCollection()
										.addAll(wConsolidateAccount.getProductCollection());

						} else if (logger.isDebugEnabled())
							logger.logDebug("---->wConsolidateAccount isNULL-returncode:"
									+ consolidateResponseCtaAho.getReturnCode());

					}

				}
				// Seteando codigo de Error
				wSummaryQueryResp.setSuccess(wConsolidateAccount.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setReturnCode(wConsolidateAccount.getReturnCode());
				wSummaryQueryResp.setMessages(wConsolidateAccount.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS3", consolidateResponseCtaCte.getSbProducts());
				aBagSPJavaOrchestration.put("SBPRODUCTS4", consolidateResponseCtaAho.getSbProducts());
				// Utils de capa commons-Se envia mapSP para manejo de errores

				response = transformResponse(wSummaryQueryResp);
			} else {
				// logger.logInfo("Entramos aquí ITO, jejejeje");
				/*
				 * setColProductNumber(0); setColCurrency(1);
				 * setColProductType(2);
				 */

				setColProductNumber(0);
				setColCurrency(19);
				setColProductType(1);

				if (logger.isDebugEnabled())
					logger.logDebug("--->getAllAccountsCore - getOnline is False>>>");
				response = this.getAccountBalanceLocal(productQueryRequest);
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}
		if (logger.isInfoEnabled())
			logger.logInfo("getAllAccountsCore-RESPUESTA AQUI !!!! -->" + response.getProcedureResponseAsString());
		return response;
	}

	@Override
	protected IProcedureResponse getSavingAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-Start Execution GetSavingAccount-->");
		return getAccountsCore(SAVING_ACCOUNT, request, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse getCheckingAccountsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-Start Execution GetCheckingAccount-->");
		return getAccountsCore(CHECKING_ACCOUNT, request, aBagSPJavaOrchestration);

	}

	private IProcedureResponse getAccountsCore(int prod, IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse responseProductsQuery = null, response = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		setFlagConsolidated(true);

		try {
			setColProductNumber(0);
			setColCurrency(1);
			setColProductType(2);
			ProductQueryRequest productQueryRequest = transformRequestToDto(request);

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);
			if (Resp.getOnLine()) {

				// llamado del sp del core
				ConsolidateResponse consolidateResponseCtaCte = null;

				// get RESPONSE_PRODUCTS_QUERY
				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug(
							"--->DetailedConsolidated - AccountsCore- Response execution local RESPONSE_PRODUCTS_QUERY-->>>"
									+ responseProductsQuery.getProcedureResponseAsString());

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration
						.get(CONSOLIDATE_REQUEST);

				Client wCliente = consolidateRequest.getClient();

				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsTemp) {

					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (logger.isDebugEnabled())
						logger.logDebug(
								"---->DetailedConsolidated-AccoutsCore-ValidateClient-IDClientConsolidateReq->>>"
										+ wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>"
										+ columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						if (prod == CHECKING_ACCOUNT)
							consolidateResponseCtaCte = CoreConsolidateAccountsQuery
									.getConsolidateCheckingAccountByClient(consolidateRequest);
						if (prod == SAVING_ACCOUNT)
							consolidateResponseCtaCte = CoreConsolidateAccountsQuery
									.getConsolidateSavingAccountByClient(consolidateRequest);

						if (consolidateResponseCtaCte.getProductCollection() != null) {
							if (wSummaryQueryResp.getProductCollection() == null)
								wSummaryQueryResp
										.setProductCollection(consolidateResponseCtaCte.getProductCollection());
							else
								wSummaryQueryResp.getProductCollection()
										.addAll(consolidateResponseCtaCte.getProductCollection());
						}

					}
				}

				wSummaryQueryResp.setSuccess(consolidateResponseCtaCte.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setReturnCode(consolidateResponseCtaCte.getReturnCode());
				wSummaryQueryResp.setMessages(consolidateResponseCtaCte.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS" + String.valueOf(prod),
						consolidateResponseCtaCte.getSbProducts());
				response = transformResponse(wSummaryQueryResp);
			} else {
				if (logger.isDebugEnabled())
					logger.logDebug("--->DetailedConsolidated-AccountsCore-getOnline is FALSE>>>");
				response = this.getAccountBalanceLocal(productQueryRequest);
			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-AccountsCore-RESPUESTA AQUI !!!! -->"
					+ response.getProcedureResponseAsString());
		return response;

	}

	private IProcedureResponse getProductsCore(int prod, IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		setFlagConsolidated(true);
		IProcedureResponse responseProductsQuery = null, response = null;
		// int wcont=0;

		setColProductNumber(0);// 3
		setColCurrency(19);// 8
		setColProductType(1);// 0
		ConsolidateResponse consiladateRes = null;
		ConsolidateResponse wSummaryQueryResp = new ConsolidateResponse();
		if (logger.isDebugEnabled())
			logger.logDebug("---->Inicia DetailedConsolidated getProductsCore-original request--->>>"
					+ request.getProcedureRequestAsString());
		try {

			ServerResponse Resp = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (!Resp.getOfflineWithBalances()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("--->Mode OfflineWithBalances: FALSE--->>>");
					logger.logDebug("--->DetailedConsolidated -GetProductsCore - OfflineWithBalances - Product: "
							+ String.valueOf(prod));
				}

				wSummaryQueryResp.setReturnCode(-1);
				wSummaryQueryResp.setSuccess(false);
				response = transformResponse(wSummaryQueryResp);
				return response;
			} else {

				ConsolidateRequest consolidateRequest = (ConsolidateRequest) aBagSPJavaOrchestration
						.get(CONSOLIDATE_REQUEST);

				responseProductsQuery = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_PRODUCTS_QUERY);
				if (logger.isDebugEnabled())
					logger.logDebug("---->DetailedConsolidated getProductsCore-Get RESPONSE_PRODUCTS_QUERY--->>>"
							+ responseProductsQuery.getProcedureResponseAsString());
				Client wCliente = consolidateRequest.getClient();
				//
				wCliente.setId("0");

				IResultSetRow[] rowsTemp = responseProductsQuery.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (logger.isDebugEnabled())
						logger.logDebug(
								"---->DetailedConsolidated getProductsCore-ValidateClient-IDClientConsolidateReq->>>"
										+ wCliente.getId() + "--->>>EpMiembroLocalResponse--->>>"
										+ columns[19].getValue());

					if (!(wCliente.getId().equals(columns[19].getValue()))) {
						wCliente.setId(columns[19].getValue());
						consolidateRequest.setClient(wCliente);

						if (prod == LOAN)
							consiladateRes = CoreConsolidateAccountsQuery
									.getConsolidateLoanAccountByClient(consolidateRequest);
						if (prod == TIME_DEPOSIT)
							consiladateRes = CoreConsolidateAccountsQuery
									.getConsolidateFixedTermDepositAccountByClient(consolidateRequest);
						if (prod == CREDIT_CARD)
							consiladateRes = CoreConsolidateAccountsQuery
									.getConsolidateCreditCardByClient(consolidateRequest);

						if (consiladateRes.getProductCollection() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("---->consiladateRes tieneData isNotNull-returncode:"
										+ consiladateRes.getReturnCode());
							if (wSummaryQueryResp.getProductCollection() == null)
								wSummaryQueryResp.setProductCollection(consiladateRes.getProductCollection());
							else
								wSummaryQueryResp.getProductCollection().addAll(consiladateRes.getProductCollection());
						} else if (logger.isDebugEnabled())
							logger.logDebug("---->consiladateRes isNULL-returnCode: " + consiladateRes.getReturnCode());
					}
				}
				// Setenado Codigo de Error
				wSummaryQueryResp.setReturnCode(consiladateRes.getReturnCode());
				wSummaryQueryResp.setSuccess(consiladateRes.getReturnCode() == 0 ? true : false);
				wSummaryQueryResp.setMessages(consiladateRes.getMessages());
				aBagSPJavaOrchestration.put("SBPRODUCTS" + String.valueOf(prod), consiladateRes.getSbProducts());

			}

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(request, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(request, e);
		}

		response = transformResponse(wSummaryQueryResp);

		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-getProductsCore RESPUESTA AQUI !!!! -->"
					+ response.getProcedureResponseAsString());
		return response;
	}

	@Override
	protected IProcedureResponse getLoansCore(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-Start Execution GetLoans-->");
		return getProductsCore(LOAN, request, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse getTimeDepositsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-Start Execution GetTimeDeposit-->");
		return getProductsCore(TIME_DEPOSIT, request, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse getCreditCardsCore(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidated-Start Execution GetCreditCard-->");
		return getProductsCore(CREDIT_CARD, request, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("DetailedConsolidatedQueryOrchestrationCore: executeJavaOrchestration");

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();

			mapInterfaces.put("CoreServer", CoreServer);
			mapInterfaces.put("coreServiceClient", coreServiceClient);
			mapInterfaces.put("CoreConsolidateAccountsQuery", CoreConsolidateAccountsQuery);
			com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);

			setGetSchemaProduct(true);
			if (logger.isInfoEnabled())
				logger.logInfo("---->>SchemaProduct-" + isGetSchemaProduct());
			Utils.validateComponentInstance(mapInterfaces);

			IProcedureResponse wProcedureResponse = executeStepsQueryBase(anOriginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "*********  Error en " + e.getMessage(), e);
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
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> bag) {

		return (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);
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
			logger.logInfo(
					CLASS_NAME + "Ejecutando Transformacion de ProcedureRequest a DTO ProductQueryRequest" + request);

		ProductQueryRequest productQueryRequest = new ProductQueryRequest();
		BalanceProduct balanceProductRequest = new BalanceProduct();
		Office officeAccount = new Office();
		Client user = new Client();
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
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

	private IProcedureResponse getAccountBalanceLocal(ProductQueryRequest productQueryRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getAccountBalanceLocal");
		}
		
		//JCOS XDX

		ProductQueryResponse productQueryResponse = new ProductQueryResponse();
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getAccountBalanceLocal");
		}
          //JCOS XDX
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
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
		if (productQueryRequest.getBalanceProductRequest() != null
				&& productQueryRequest.getBalanceProductRequest().getProcessDate() != null) {
			anOriginalRequest.addInputParam("@s_date", ICTSTypes.SYBDATETIME,
					"" + productQueryRequest.getBalanceProductRequest().getProcessDate());
		}
		if (productQueryRequest.getBalanceProductRequest() != null
				&& productQueryRequest.getBalanceProductRequest().getOfficeAccount() != null) {
			anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4,
					"" + productQueryRequest.getBalanceProductRequest().getOfficeAccount().getId());
		}
		if (productQueryRequest.getUser() != null && productQueryRequest.getUser().getLogin() != null) {
			anOriginalRequest.addInputParam("@s_user", ICTSTypes.SYBVARCHAR,
					"" + productQueryRequest.getUser().getLogin());
		}
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SYBVARCHAR, session.getTerminal());

		anOriginalRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SYBCHAR, "L");
		anOriginalRequest.addInputParam("@i_consolidado", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_nregistros", ICTSTypes.SYBINT4,
				"" + productQueryRequest.getQueryResultsNumber());
		anOriginalRequest.addInputParam("@i_siguiente", ICTSTypes.SYBINT4,
				"" + productQueryRequest.getNextQueryNumber());

		if (productQueryRequest.getUser() != null && productQueryRequest.getUser().getIdCustomer() != null) {
			anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SYBINT4,
					"" + productQueryRequest.getUser().getIdCustomer());
		}
		if (productQueryRequest.getChannelId() != null) {
			anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "" + productQueryRequest.getChannelId());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + response.getProcedureResponseAsString());
		}

		return response;

	}

	/**
	 * Transform Response that the dummy service is returning and convert it
	 * like the front-end is catching
	 */
	IProcedureResponse transformResponse(ConsolidateResponse aProcedureResponse) {

		if (!aProcedureResponse.getSuccess())
			return Utils.returnException(aProcedureResponse.getMessages());

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SQLINT2, 21));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productName", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountName", ICTSTypes.SQLVARCHAR, 32));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY, 21));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("drawBalance", ICTSTypes.SQLMONEY, 21));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SQLMONEY, 21));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalance", ICTSTypes.SQLMONEY, 21));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalanceMaturity", ICTSTypes.SQLMONEY, 21));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("monthlyPaymentDay", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("nextPaymentDate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("nextPaymentValue", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("totalBalance", ICTSTypes.SQLMONEY, 21));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productType", ICTSTypes.SQLCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("alias", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 21));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("totalCredit", ICTSTypes.SQLMONEY, 21));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("clabeInterBank", ICTSTypes.SQLVARCHAR, 22));

		for (ProductConsolidate consolidate : aProcedureResponse.getProductCollection()) {
			Product product = consolidate.getProduct();
			Currency currency = consolidate.getCurrency();
			BalanceProduct balance = consolidate.getBalance();

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, product.getProductNumber())); // productNumber
			row.addRowData(2, new ResultSetRowColumnData(false, product.getProductType().toString())); // productId
			row.addRowData(3, new ResultSetRowColumnData(false, currency.getCurrencyDescription()));// currencyName
			row.addRowData(4, new ResultSetRowColumnData(false, product.getProductDescription()));// productName
			row.addRowData(5, new ResultSetRowColumnData(false,
					(product.getProductAlias() == null ? "" : product.getProductAlias())));// accountName

			row.addRowData(6, new ResultSetRowColumnData(false,
					(balance.getEquityBalance() != null ? balance.getEquityBalance().toString() : "0")));
			row.addRowData(7, new ResultSetRowColumnData(false,
					(balance.getDrawBalance() == null ? "0" : balance.getDrawBalance().toString())));// drawBalance
			row.addRowData(8, new ResultSetRowColumnData(false, balance.getAvailableBalance().toString()));
			row.addRowData(9, new ResultSetRowColumnData(false,
					(balance.getCashBalance() == null ? "0" : balance.getCashBalance().toString())));// capitalBalance
			row.addRowData(10,
					new ResultSetRowColumnData(false, Utils.formatDateToString(balance.getExpirationDate())));// expirationDate
			row.addRowData(11, new ResultSetRowColumnData(false, balance.getRate()));// rate
			row.addRowData(12, new ResultSetRowColumnData(false, ""));// capitalBalanceMaturity
			row.addRowData(13, new ResultSetRowColumnData(false, ""));// monthlyPaymentDay
			row.addRowData(14, new ResultSetRowColumnData(false,
					balance.getState() == null ? " - " : balance.getState().toString()));// status
			row.addRowData(15, new ResultSetRowColumnData(false, balance.getDateLastMovent()));// nextPaymentDate
			row.addRowData(16, new ResultSetRowColumnData(false,
					balance.getNextPaymentValue() == null ? "0" : balance.getNextPaymentValue().toString()));// nextPaymentValue
			row.addRowData(17, new ResultSetRowColumnData(false,
					(balance.getTotalBalance() == null ? "0" : balance.getTotalBalance().toString())));// totalBalance
			row.addRowData(18, new ResultSetRowColumnData(false, ""));// productType
			row.addRowData(19, new ResultSetRowColumnData(false, product.getProductAlias()));
			row.addRowData(20, new ResultSetRowColumnData(false, currency.getCurrencyId().toString()));
			
			row.addRowData(21, new ResultSetRowColumnData(false, balance.getTotalCredit().toString()));
			
			row.addRowData(22, new ResultSetRowColumnData(false, product.getClabeInterbank()));
			data.addRow(row);

		}

		// Agregar Data - Consolidado General
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Data devuelta:" + wProcedureResponse.getProcedureResponseAsString());

		}
		return wProcedureResponse;

	}

	private ConsolidateResponse executionInteface(String nameOfMethod, ConsolidateRequest consolidateRequest)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class clase = CoreConsolidateAccountsQuery.getClass();
		ConsolidateResponse consolidateResponse = null;
		Method metodo;
		try {
			metodo = clase.getMethod(nameOfMethod, consolidateRequest.getClass());
			consolidateResponse = (ConsolidateResponse) metodo.invoke(CoreConsolidateAccountsQuery, consolidateRequest);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return consolidateResponse;
	}

	public ConsolidateResponse joinResponseLocal(ConsolidateResponse consolidateResponse1,
			ConsolidateResponse consolidateResponse2) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Procedure Response Cta CTE " + consolidateResponse1);
			logger.logInfo(CLASS_NAME + "Procedure Response Cta AHO " + consolidateResponse2);
		}
		ConsolidateResponse consolidateResponse = new ConsolidateResponse();

		if (consolidateResponse1.getProductCollection() == null)
			if (logger.isInfoEnabled())
				logger.logInfo("consolidateResponse1.getProductCollection is null ");
		if (consolidateResponse2.getProductCollection() == null)
			if (logger.isInfoEnabled())
				logger.logInfo("consolidateResponse2.getProductCollection is null ");

		// consolidateResponse.setProductCollection(consolidateResponse1.getProductCollection());
		// consolidateResponse.getProductCollection().addAll(consolidateResponse2.getProductCollection());
		// Validaciones para manejor de error
		if (consolidateResponse1.getReturnCode() != 0 && consolidateResponse2.getReturnCode() != 0) {
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
				logger.logInfo(CLASS_NAME + "*** CtaAho - CtaCte sin Datos, seteando codigo de error <>0.ErrorCode:"
						+ consolidateResponse1.getReturnCode());
		} else {
			// Se setean en verdadero ya que uno de los dos response pueden
			// tener datos
			consolidateResponse.setSuccess(true);
			consolidateResponse.setReturnCode(0);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "*** CtaAho o CtaCte CON Datos, seteando codigo de error=0.ErrorCodeCtaCte:"
						+ consolidateResponse1.getReturnCode() + " -ErrorCodeCtaAho:"
						+ consolidateResponse2.getReturnCode());
			if (consolidateResponse1.getReturnCode() == 0 && consolidateResponse2.getReturnCode() == 0) {
				consolidateResponse.setProductCollection(consolidateResponse1.getProductCollection());
				consolidateResponse.getProductCollection().addAll(consolidateResponse2.getProductCollection());
				return consolidateResponse;
			}
			if (consolidateResponse1.getProductCollection() != null) {
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

}
