package com.cobiscorp.ecobis.orchestration.core.ib.query.accounts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.dom4j.Node;

//import org.springframework.core.convert.converter.Converter;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
//import com.cobiscorp.cobis.csp.services.ICSPExecutorDynamicParams;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
//import com.cobiscorp.cobis.cts.domains.ICTSMessage;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
//import com.cobiscorp.cobis.cts.domains.IResponseBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
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
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditLine;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.GRB;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreConsolidateAccountsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceClient;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCreditLine;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQueryBankGuarantee;
import com.cobiscorp.ecobis.orchestration.core.ib.model.ConsolidateAccountsDto;
import com.cobiscorp.ecobis.orchestration.core.ib.model.ConsolidateCardsDto;
import com.cobiscorp.ecobis.orchestration.core.ib.model.ConsolidateSummaryDto;
import com.cobiscorp.ecobis.orchestration.core.ib.model.DetailsAccountDto;
import com.cobiscorp.ecobis.orchestration.core.ib.model.TotalDetailsAccountDto;

import com.cobiscorp.ecobis.orchestration.core.ib.utils.UtilityTransform;

/**
 *
 * @author schancay
 * @since Jul 3, 2014
 * @version 1.0.0
 */
@Component(name = "ConsolidateQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ConsolidateQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ConsolidateQueryOrchestationCore") })
public class ConsolidateQueryOrchestationCore extends SPJavaOrchestrationBase {
	/**
	 * This CONSTANTS help us to save in bag aBagSPJavaOrchestration
	 */
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final String SERVER_STATUS_RESP = "SERVER_STATUS_RESP";
	private static final String ACCOUNTS_QUERY_RESP = "ACCOUNTS_QUERY_RESP";
	// private static final String EXECUTE_QUERY_RESP = "EXECUTE_QUERY_RESP";
	private static final String SUMMARY_QUERY_CENTRAL_RESP = "SUMMARY_QUERY_CENTRAL_RESP";
	private static final String SUMMARY_QUERY_LOCAL_RESP = "SUMMARY_QUERY_LOCAL_RESP";
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final int ERROR40004 = 40004;
	private static final int ERROR40002 = 40002;

	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTACTE = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTACTE";
	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTAAHO = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTAAHO";
	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTALOAN = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTALOAN";
	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTADPF = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTADPF";
	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_CREDITCARD = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_CREDITCARD";
	private static final String RESPONSE_SUMMARY_CONSOLIDATE_CORE_EMPTY = "RESPONSE_SUMMARY_CONSOLIDATE_CORE_EMPTY";

	private static final String CONSOLIDATE_CTACTE = "CONSOLIDATE_CTACTE";
	private static final String CONSOLIDATE_CTAAHO = "CONSOLIDATE_CTAAHO";
	private static final String CONSOLIDATE_CTALOAN = "CONSOLIDATE_CTALOAN";
	private static final String CONSOLIDATE_CTADPF = "CONSOLIDATE_CTADPF";
	private static final String CONSOLIDATE_CREDITCARD = "CONSOLIDATE_CREDITCARD";

	private static final String TOTAL_CONSOLIDATE_ASSETS = "TOTAL_CONSOLIDATE_ASSETS";

	private static final String LIST_CURRENCY_CTACTE = "LIST_CURRENCY_CTACTE";
	private static final String LIST_CURRENCY_CTAAHO = "LIST_CURRENCY_CTAAHO";
	private static final String LIST_CURRENCY_CTALOAN = "LIST_CURRENCY_CTALOAN";
	private static final String LIST_CURRENCY_CTADPF = "LIST_CURRENCY_CTADPF";
	private static final String LIST_CURRENCY_CREDITCARD = "LIST_CURRENCY_CREDITCARD";

	private static final String LIST_TOTAL_CONSOLIDATE_ASSETS = "LIST_TOTAL_CONSOLIDATE_ASSETS";

	private static final Integer PRODUCT_CTACTE = 3;
	private static final Integer PRODUCT_CTAAHO = 4;
	private static final Integer PRODUCT_CTALOAN = 7;
	private static final Integer PRODUCT_CTADPF = 14;
	private static final Integer PRODUCT_CREDIT_CARD = 83;
	private static final Integer PRODUCT_CREDIT_LINE = 21;
	private static final Integer PRODUCT_BANK_GUARANTEE = 9;
	private static final Integer MON_NAC = 0;
	private static final Integer DOLAR = 1;
	private static final Integer UFV = 2;

	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int LOAN = 7;
	private static final int TIME_DEPOSIT = 14;
	private static final int CREDIT_CARD = 83;
	private static final int CREDIT_LINE = 21;
	private static final int BANK_GUARANTEE = 9;

	UtilityTransform utilityTransform = new UtilityTransform();

	ArrayList<String> allProcedureResponseCodes = new ArrayList<String>();
	/**
	 * Class name
	 */
	private static final String CLASS_NAME = " >-----> ";

	ILogger logger = getLogger();

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
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceClient.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServiceClient", unbind = "unbindCoreServiceClient")
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

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceCreditLine.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServiceCreditLineQuery", unbind = "unbindCoreServiceCreditLineQuery")
	private ICoreServiceCreditLine CoreServiceCreditLineQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceCreditLineQuery(ICoreServiceCreditLine service) {
		CoreServiceCreditLineQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceCreditLineQuery(ICoreServiceCreditLine service) {
		CoreServiceCreditLineQuery = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQueryBankGuarantee.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServiceBankGuaranteeQuery", unbind = "unbindCoreServiceBankGuaranteeQuery")
	private ICoreServiceQueryBankGuarantee CoreServiceBankGuaranteeQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceBankGuaranteeQuery(ICoreServiceQueryBankGuarantee service) {
		CoreServiceBankGuaranteeQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceBankGuaranteeQuery(ICoreServiceQueryBankGuarantee service) {
		CoreServiceBankGuaranteeQuery = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en la orquestación ConsolidatedQueryOrchestation");
		}

		CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

		boolean wHasErrorStatusServer = true;
		boolean wHasErrorAccountsQuery = true;
		boolean wHasErrorExecuteQuery = true;
		boolean wHasErrorSummaryQuery = true;

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {

			// 1. Consulta de estado del central
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "1. Consulta de estado del central ---> Método executeServerStatus");
			}

			wHasErrorStatusServer = executeServerStatus(aBagSPJavaOrchestration);

			ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);

			if (serverResponse.getSuccess() || serverResponse.getOnLine()) {

				// 2. Consulta de cuentas en el local
				if (logger.isInfoEnabled()) {
					logger.logInfo(CLASS_NAME + "2. Consulta de cuentas en el local ---> Método executeAccountsQuery");
				}
				IProcedureRequest wAccountsQueryRequest = initProcedureRequest(anOriginalRequest);
				wHasErrorAccountsQuery = executeAccountsQuery(wAccountsQueryRequest, anOriginalRequest,
						aBagSPJavaOrchestration);

				IProcedureResponse wAccountsQueryResp = (IProcedureResponse) aBagSPJavaOrchestration
						.get(ACCOUNTS_QUERY_RESP);
				String registers = wAccountsQueryResp.readValueParam("@o_registros");

				int registersValue = 0;
				if ((registers != null) && !registers.isEmpty())
					registersValue = Integer.parseInt(registers);

				if (!wHasErrorAccountsQuery && (registersValue > 0)) {
					// TODO: Este bloque se debera eliminar para la finalizacion
					// de la consultas de ctas
					// 3. Ejecución de la clase ExecuteQuery
					// if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME +
					// "3. Ejecución de la clase ExecuteQuery ---> Método
					// executeQuery");
					// IProcedureRequest wExecuteQueryRequest =
					// initProcedureRequest(anOriginalRequest);
					// wHasErrorExecuteQuery =
					// executeQuery(wExecuteQueryRequest, anOriginalRequest,
					// aBagSPJavaOrchestration);

					// if (!wHasErrorExecuteQuery) {
					// 4. Ejecución de consulta de resumen de cuentas
					if (logger.isInfoEnabled())
						logger.logInfo(CLASS_NAME
								+ "4. Ejecución de consulta de resumen de cuentas en central---> Método executeSummaryQueryCentral");
					wHasErrorSummaryQuery = executeSummaryQueryCore(aBagSPJavaOrchestration);
					if (!wHasErrorSummaryQuery)
						getConsolidateBalanceLocal(anOriginalRequest, aBagSPJavaOrchestration);
					// }
				}

			} else if (!serverResponse.getOnLine()) {

				// 2. Consulta de cuentas en el local
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME
							+ "2. Consulta de cuentas en el local ---> Método executeAccountsQueryWithoutCentral");

				IProcedureRequest wAccountsQueryRequest = initProcedureRequest(anOriginalRequest);
				wHasErrorAccountsQuery = executeAccountsQueryWithoutCentral(wAccountsQueryRequest, anOriginalRequest,
						aBagSPJavaOrchestration);

				IProcedureResponse wAccountsQueryResp = (IProcedureResponse) aBagSPJavaOrchestration
						.get(ACCOUNTS_QUERY_RESP);
				String registers = wAccountsQueryResp.readValueParam("@o_registros");

				int registersValue = 0;
				if ((registers != null) && !registers.isEmpty())
					registersValue = Integer.parseInt(registers);

				if (!wHasErrorAccountsQuery && (registersValue > 0)) {

					getConsolidateBalanceLocal(anOriginalRequest, aBagSPJavaOrchestration);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Saliendo de la orquestación ConsolidatedQueryOrchestation");
			}

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("error", e);
			}
			cisResponseHelper = new CISResponseManagmentHelper();
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Servicio no disponible");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	private void getConsolidateBalanceLocal(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		boolean wHasErrorSummaryQuery = true;

		// 3. Ejecución al local
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME
					+ "3. Ejecución de consulta de resumen de cuentas en local---> Método executeSummaryQueryLocal");
		}
		IProcedureRequest wSummaryQueryRequest = initProcedureRequest(anOriginalRequest);
		wHasErrorSummaryQuery = executeSummaryQueryLocal(wSummaryQueryRequest, anOriginalRequest,
				aBagSPJavaOrchestration);

	}

	/**
	 * This method executes an server status operation to a core database
	 *
	 * @param aStatusRequest
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	boolean executeServerStatus(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Iniciando consulta del estado del servidor");

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio").toString());
		serverRequest.setFormatDate(101);
		serverRequest.setCodeTransactionalIdentifier("1800039");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request Corebanking: " + serverRequest);

		try {
			ServerResponse serverResponse = CoreServer.getServerStatus(serverRequest);
			if (logger.isDebugEnabled())
				logger.logDebug("Response Corebanking: " + serverResponse);
			aBagSPJavaOrchestration.put(SERVER_STATUS_RESP, serverResponse);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + " Saliendo de executeServerStatus");

			return serverResponse.getSuccess();
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This method executes an server status operation to a core database
	 *
	 * @param aStatusRequest
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	boolean executeAccountsQuery(IProcedureRequest aAccountsQueryRequest, IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeAccountsQuery");
		}

		aAccountsQueryRequest.setSpName("cob_bvirtual..sp_consulta_cuentas");

		aAccountsQueryRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18752");
		aAccountsQueryRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18752");
		aAccountsQueryRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		aAccountsQueryRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		copyParam("@s_servicio", anOriginalRequest, aAccountsQueryRequest);

		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(SERVER_STATUS_RESP);
		if (!serverResponse.getOnLine()) {
			aAccountsQueryRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, "F");
		} else {
			aAccountsQueryRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, "L");
		}

		copyParam("@s_date", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@i_login", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@s_cliente", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@t_trn", anOriginalRequest, aAccountsQueryRequest);
		aAccountsQueryRequest.addInputParam("@i_bl_net", ICTSTypes.SQLCHAR, "S");
		aAccountsQueryRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "CO");
		copyParam("@i_nregistros", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@i_miembro", anOriginalRequest, aAccountsQueryRequest);

		aAccountsQueryRequest.addOutputParam("@o_registros", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@s_servicio: " + aAccountsQueryRequest.readValueParam("@s_servicio"));
			logger.logDebug(CLASS_NAME + "@i_tipo_ejec: " + aAccountsQueryRequest.readValueParam("@i_tipo_ejec"));
			logger.logDebug(CLASS_NAME + "@s_date: " + aAccountsQueryRequest.readValueParam("@s_date"));
			logger.logDebug(CLASS_NAME + "@i_login: " + aAccountsQueryRequest.readValueParam("@i_login"));
			logger.logDebug(CLASS_NAME + "@s_cliente: " + aAccountsQueryRequest.readValueParam("@s_cliente"));
			logger.logDebug(CLASS_NAME + "@t_trn: " + aAccountsQueryRequest.readValueParam("@t_trn"));
			logger.logDebug(CLASS_NAME + "@i_bl_net: " + aAccountsQueryRequest.readValueParam("@i_bl_net"));
			logger.logDebug(CLASS_NAME + "@i_operacion: " + aAccountsQueryRequest.readValueParam("@i_operacion"));
			logger.logDebug(CLASS_NAME + "@i_nregistros: " + aAccountsQueryRequest.readValueParam("@i_nregistros"));
			logger.logDebug(CLASS_NAME + "@i_miembro: " + aAccountsQueryRequest.readValueParam("@i_bl_net"));
			logger.logDebug("Request Corebanking: " + aAccountsQueryRequest.getProcedureRequestAsString());
		}

		IProcedureResponse wAccountsQueryResp = executeCoreBanking(aAccountsQueryRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wAccountsQueryResp.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.put(ACCOUNTS_QUERY_RESP, wAccountsQueryResp);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeAccountsQuery");
		}
		return wAccountsQueryResp.hasError();
	}

	boolean executeAccountsQueryWithoutCentral(IProcedureRequest aAccountsQueryRequest,
			IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeAccountsQueryWithoutCentral");
		}

		aAccountsQueryRequest.setSpName("cob_bvirtual..sp_consulta_cuentas");

		aAccountsQueryRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18752");
		aAccountsQueryRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18752");
		aAccountsQueryRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		aAccountsQueryRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		copyParam("@s_servicio", anOriginalRequest, aAccountsQueryRequest);
		aAccountsQueryRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, "F");
		copyParam("@s_date", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@i_login", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@s_cliente", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@t_trn", anOriginalRequest, aAccountsQueryRequest);
		aAccountsQueryRequest.addInputParam("@i_bl_net", ICTSTypes.SQLCHAR, "S");
		aAccountsQueryRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "CO");
		copyParam("@i_nregistros", anOriginalRequest, aAccountsQueryRequest);
		copyParam("@i_miembro", anOriginalRequest, aAccountsQueryRequest);

		aAccountsQueryRequest.addOutputParam("@o_registros", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@s_servicio: " + aAccountsQueryRequest.readValueParam("@s_servicio"));
			logger.logDebug(CLASS_NAME + "@i_tipo_ejec: " + aAccountsQueryRequest.readValueParam("@i_tipo_ejec"));
			logger.logDebug(CLASS_NAME + "@s_date: " + aAccountsQueryRequest.readValueParam("@s_date"));
			logger.logDebug(CLASS_NAME + "@i_login: " + aAccountsQueryRequest.readValueParam("@i_login"));
			logger.logDebug(CLASS_NAME + "@s_cliente: " + aAccountsQueryRequest.readValueParam("@s_cliente"));
			logger.logDebug(CLASS_NAME + "@t_trn: " + aAccountsQueryRequest.readValueParam("@t_trn"));
			logger.logDebug(CLASS_NAME + "@i_bl_net: " + aAccountsQueryRequest.readValueParam("@i_bl_net"));
			logger.logDebug(CLASS_NAME + "@i_operacion: " + aAccountsQueryRequest.readValueParam("@i_operacion"));
			logger.logDebug(CLASS_NAME + "@i_nregistros: " + aAccountsQueryRequest.readValueParam("@i_nregistros"));
			logger.logDebug(CLASS_NAME + "@i_miembro: " + aAccountsQueryRequest.readValueParam("@i_bl_net"));
			logger.logDebug("Request Corebanking: " + aAccountsQueryRequest.getProcedureRequestAsString());
		}

		IProcedureResponse wAccountsQueryResp = executeCoreBanking(aAccountsQueryRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wAccountsQueryResp.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.put(ACCOUNTS_QUERY_RESP, wAccountsQueryResp);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeAccountsQueryWithoutCentral");
		}
		return wAccountsQueryResp.hasError();
	}

	/**
	 * This method executes an server status operation to a core database
	 *
	 * @param aStatusRequest
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	boolean executeSummaryQueryCore(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeSummaryQueryCore");
		}

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		ConsolidateRequest consolidateRequest = new ConsolidateRequest();
		Client client = new Client();
		Currency currency = new Currency();
		Map<String, Object> map = new HashMap<String, Object>();
		ClientInformationRequest clientRequest = new ClientInformationRequest();
		Client clientFind = new Client();

		if (anOriginalRequest.readValueParam("@s_cliente") != null)
			clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));
		if (anOriginalRequest.readValueParam("@i_login") != null)
			clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));

		clientRequest.setClient(clientFind);
		if (anOriginalRequest.readValueParam("@s_servicio") != null)
			clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Informacion de Cliente Consultar:" + clientRequest);
		ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Informacion de Cliente devuelta:" + informationClient);
		client.setId(informationClient.getClient().getIdCustomer());

		currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));

		consolidateRequest.setClient(client);
		consolidateRequest.setCurrency(currency);
		consolidateRequest
				.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request Corebanking: " + consolidateRequest);

		try {
			getConsolidateAccountsCore(consolidateRequest, aBagSPJavaOrchestration);
			// return true;
			if (logger.isErrorEnabled())
				logger.logError("RESULTADO JBA"
						+ !(Boolean) aBagSPJavaOrchestration.get(RESPONSE_SUMMARY_CONSOLIDATE_CORE_EMPTY));
			return !(Boolean) aBagSPJavaOrchestration.get(RESPONSE_SUMMARY_CONSOLIDATE_CORE_EMPTY);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME
						+ "SE HA ENCONTRADO UN ERROR EN EL SERVICIO DEL CORE SE OMITE LA INFORMACION DEL CORE Y SE PROCEDE CON LA CONSULTA LOCAL");
			return false;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME
						+ "SE HA ENCONTRADO UN ERROR DE INFRAESTRUCTURA DEL CORE SE OMITE LA INFORMACION DEL CORE Y SE PROCEDE CON LA CONSULTA LOCAL");
			return false;
		}
	}

	boolean executeSummaryQueryLocal(IProcedureRequest aSummaryQueryLocalRequest, IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeSummaryQueryLocal");
		}

		aSummaryQueryLocalRequest.setSpName("cob_bvirtual..sp_cons_resumen_ctas_bv");

		aSummaryQueryLocalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875021");
		aSummaryQueryLocalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1875021");
		aSummaryQueryLocalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		aSummaryQueryLocalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		copyParam("@s_srv", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_cliente", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_ssn", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_ssn_branch", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_date", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_ofi", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_user", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_rol", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_term", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@s_org", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@t_ejec", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@t_rty", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@i_nregistros", anOriginalRequest, aSummaryQueryLocalRequest);
		copyParam("@i_login", anOriginalRequest, aSummaryQueryLocalRequest);

		aSummaryQueryLocalRequest.addInputParam("@i_cliente", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueParam("@s_cliente"));
		copyParam("@i_moneda", anOriginalRequest, aSummaryQueryLocalRequest);

		aSummaryQueryLocalRequest.addOutputParam("@o_ssn_branch", ICTSTypes.SYBINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "@s_srv: " + aSummaryQueryLocalRequest.readValueParam("@s_srv"));
			logger.logDebug(CLASS_NAME + "@s_cliente: " + aSummaryQueryLocalRequest.readValueParam("@s_cliente"));
			logger.logDebug(CLASS_NAME + "@s_ssn: " + aSummaryQueryLocalRequest.readValueParam("@s_ssn"));
			logger.logDebug(CLASS_NAME + "@s_ssn_branch: " + aSummaryQueryLocalRequest.readValueParam("@s_ssn_branch"));
			logger.logDebug(CLASS_NAME + "@s_date: " + aSummaryQueryLocalRequest.readValueParam("@s_date"));
			logger.logDebug(CLASS_NAME + "@s_ofi: " + aSummaryQueryLocalRequest.readValueParam("@s_ofi"));
			logger.logDebug(CLASS_NAME + "@s_user: " + aSummaryQueryLocalRequest.readValueParam("@s_user"));
			logger.logDebug(CLASS_NAME + "@s_lsrv: " + aSummaryQueryLocalRequest.readValueParam("@s_lsrv"));
			logger.logDebug(CLASS_NAME + "@s_rol: " + aSummaryQueryLocalRequest.readValueParam("@s_rol"));
			logger.logDebug(CLASS_NAME + "@i_tipo_ejec: " + aSummaryQueryLocalRequest.readValueParam("@i_tipo_ejec"));
			logger.logDebug(CLASS_NAME + "@s_term: " + aSummaryQueryLocalRequest.readValueParam("@s_term"));
			logger.logDebug(CLASS_NAME + "@s_org: " + aSummaryQueryLocalRequest.readValueParam("@s_org"));
			logger.logDebug(CLASS_NAME + "@t_ejec: " + aSummaryQueryLocalRequest.readValueParam("@t_ejec"));
			logger.logDebug(CLASS_NAME + "@t_rty: " + aSummaryQueryLocalRequest.readValueParam("@t_rty"));
			logger.logDebug(CLASS_NAME + "@i_nregistros: " + aSummaryQueryLocalRequest.readValueParam("@i_nregistros"));
			logger.logDebug(CLASS_NAME + "@i_cliente: " + aSummaryQueryLocalRequest.readValueParam("@i_cliente"));
			logger.logDebug(CLASS_NAME + "@i_login: " + aSummaryQueryLocalRequest.readValueParam("@i_login"));
			logger.logDebug(CLASS_NAME + "@i_moneda: " + aSummaryQueryLocalRequest.readValueParam("@i_moneda"));
			logger.logDebug("Request Corebanking: " + aSummaryQueryLocalRequest.getProcedureRequestAsString());
		}

		IProcedureResponse wSummaryQueryLocalResp = executeCoreBanking(aSummaryQueryLocalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wSummaryQueryLocalResp.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.put(SUMMARY_QUERY_LOCAL_RESP, wSummaryQueryLocalResp);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeSummaryQueryLocal");
		}
		return wSummaryQueryLocalResp.hasError();

	}

	/**
	 * This method adds a ProcedureRequest to the final ProcedureRequest sent to
	 * a Customized Connector. This methos allows to send dynamic parameters to
	 * a Customized Connector
	 *
	 * @param aprocedureRequest
	 * @param aBagSPJavaOrchestration
	 * @param aResultSet
	 * @param aResponse
	 * @param aOrder
	 */
	public void addResultsetToRequest(IProcedureRequest aprocedureRequest, Map<String, Object> aBagSPJavaOrchestration,
			String aResultSet, String aResponse, int aOrder) {
		IProcedureResponse wProcedureResp = (IProcedureResponse) aBagSPJavaOrchestration.get(aResponse);
		aprocedureRequest.addResultSetParam(aResultSet, wProcedureResp.getResultSet(aOrder));
	}

	private boolean validateErrorCode(IProcedureResponse response, int code) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Validando existencia del codigo " + code + " en la respuesta :"
					+ response.getProcedureResponseAsString());
		}

		if ((response.hasError() == false) && (code == 0)) {
			if (logger.isInfoEnabled()) {
				logger.logInfo(" No existe mensajes de error");
			}
			return true;
		}

		int messageNumber;

		Collection responseBlocks = response.getResponseBlocks();

		if (responseBlocks != null) {
			Iterator it = responseBlocks.iterator();
			// int msgBlocksCounter = 1;

			while (it.hasNext()) {
				Object msgBlock = it.next();
				if (msgBlock instanceof IMessageBlock) {
					messageNumber = ((IMessageBlock) msgBlock).getMessageNumber();
					if (messageNumber == code) {
						if (logger.isInfoEnabled()) {
							logger.logInfo(" Existe el código " + code + " en la respuesta");
						}
						return true;
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo(" No existe el código " + code + " en la respuesta");
		}

		return false;
	}

	/**
	 * Elimina los mensages con código 40002 y 40004 de la respuesta
	 *
	 * @param response
	 */
	private void deleteErrorMessageOffline(IProcedureResponse response) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Eliminando mensages con error 40004 y 40002");
		}

		int messageNumber;

		Collection responseBlocks = response.getResponseBlocks();
		Collection messages = response.getMessages();
		ArrayList<IMessageBlock> messageToDelete = new ArrayList<IMessageBlock>();

		if (responseBlocks != null) {
			Iterator it = responseBlocks.iterator();

			while (it.hasNext()) {
				Object msgBlock = it.next();
				if (msgBlock instanceof IMessageBlock) {
					messageNumber = ((IMessageBlock) msgBlock).getMessageNumber();
					if ((messageNumber == ERROR40004) || (messageNumber == ERROR40002)) {
						messageToDelete.add((IMessageBlock) msgBlock);
					}
				}
			}
			responseBlocks.removeAll(messageToDelete);
		}

		if (messages != null) {
			messages.removeAll(messageToDelete);
		}
	}

	/**
	 * Estructura mensaje de respuesta
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Armando respuesta final");
		}

		CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalProcedureReq);

		ArrayList<String> allProcedureResponseCodes = new ArrayList<String>();

		for (Map.Entry<String, Object> element : aBagSPJavaOrchestration.entrySet()) {
			if (element.getValue() != null) {
				if (element.getValue() instanceof IProcedureResponse) {
					IProcedureResponse response = (IProcedureResponse) element.getValue();
					allProcedureResponseCodes.add(element.getKey());
					deleteErrorMessageOffline(response);
					wProcedureRespFinal.setReturnCode(response.getReturnCode());
				}
			}
		}

		String[] allProcedureResponseCodesArray = allProcedureResponseCodes.toArray(new String[0]);

		cisResponseHelper.addOutputParamsResponse(wProcedureRespFinal, anOriginalProcedureReq,
				allProcedureResponseCodesArray, aBagSPJavaOrchestration);
		cisResponseHelper.addResultsetsResponse(wProcedureRespFinal, allProcedureResponseCodesArray,
				aBagSPJavaOrchestration);

		boolean hasMessageError = false;
		for (Map.Entry<String, Object> element : aBagSPJavaOrchestration.entrySet()) {
			if (element.getValue() != null) {
				if (element.getValue() instanceof IProcedureResponse) {
					if (((IProcedureResponse) element.getValue()).getMessageListSize() != 0) {
						cisResponseHelper.addMessages(wProcedureRespFinal, allProcedureResponseCodesArray,
								aBagSPJavaOrchestration);

						if ((wProcedureRespFinal.getReturnCode() == ERROR40002)
								|| (wProcedureRespFinal.getReturnCode() == ERROR40004)) {
							if (logger.isInfoEnabled()) {
								logger.logInfo(
										CLASS_NAME + " Cambio " + wProcedureRespFinal.getReturnCode() + " por 0");
							}
							wProcedureRespFinal.setReturnCode(0);
						}
						if (logger.isInfoEnabled()) {
							logger.logInfo(CLASS_NAME + " Termina de armar respuesta final. Es la siguiente: "
									+ wProcedureRespFinal.getProcedureResponseAsString());
						}
						return wProcedureRespFinal;
					}
				}
			}

		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Termina de armar respuesta final. Es la siguiente: "
					+ wProcedureRespFinal.getProcedureResponseAsString());
		}

		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

		return wProcedureRespFinal;
	}

	/**
	 * Get information accounts by cliente from core services.
	 *
	 * @param consolidateRequest
	 * @param aBagSPJavaOrchestration
	 * @param anOriginalRequest
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	private void getConsolidateAccountsCore(ConsolidateRequest consolidateRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		Map<Object, Object> mapProducts = new HashMap<Object, Object>();
		Map<Object, ConsolidateResponse> mapConsolidateResponse = new HashMap<Object, ConsolidateResponse>();
		// mapa con los productos ,
		// el valor 0 signfica q no ha consultado ,
		// el 1 que lo consulto y trajo datos
		// , y el 2 que no trajo datos
		mapProducts.put(PRODUCT_CTACTE, 0);
		mapProducts.put(PRODUCT_CTAAHO, 0);
		mapProducts.put(PRODUCT_CTALOAN, 0);
		mapProducts.put(PRODUCT_CTADPF, 0);
		mapProducts.put(PRODUCT_CREDIT_LINE, 0);
		mapProducts.put(PRODUCT_BANK_GUARANTEE, 0);
		mapProducts.put(PRODUCT_CREDIT_CARD, 0);

		ConsolidateResponse wProcedureResponseCtaCte = null;
		ConsolidateResponse wProcedureResponseCtaAho = null;
		ConsolidateResponse wProcedureResponseCtaLoan = null;
		ConsolidateResponse wProcedureResponseCtaDpf = null;
		ConsolidateResponse wProcedureResponseCreditLine = null; // ITO
		ConsolidateResponse wProcedureResponseBankGuarantee = null; // ITO
		ConsolidateResponse wProcedureResponseGeneral = null;
		SummaryCreditCardResponse wProcedureResponseCardCredit = null;

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Entrando en getConsolidateAccountsCore");

		// IResponseBlock resultBlock;
		IProcedureResponse responseCta = new ProcedureResponseAS();

		List<ConsolidateResponse> aConsolidateResponseCta = new ArrayList<ConsolidateResponse>();

		IProcedureRequest anOriginalRequest2 = new ProcedureRequestAS();
		anOriginalRequest2.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest2.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest2.setValueFieldInHeader(ICOBISTS.HEADER_SSN, consolidateRequest.getSessionIdCore());
		anOriginalRequest2.setValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, consolidateRequest.getSessionIdIB());

		try {
			IProcedureResponse wProcResponseAccountsClientLocal = (IProcedureResponse) aBagSPJavaOrchestration
					.get(ACCOUNTS_QUERY_RESP);

			responseCta.addResponseBlock(setResulsetBlockEmpty());

			IResultSetBlock resulset = wProcResponseAccountsClientLocal.getResultSet(1);
			IResultSetRow[] rowsTemp = resulset.getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsTemp) {
				IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();

				DetailsAccountDto detailsAccountDto = new DetailsAccountDto();
				detailsAccountDto.setEnteBv(Integer.parseInt(rows[0].getValue()));
				detailsAccountDto.setProductId(Integer.parseInt(rows[2].getValue()));
				detailsAccountDto.setCurrencyId(Integer.parseInt(rows[3].getValue()));
				detailsAccountDto.setAccountNumber(rows[4].getValue());
				detailsAccountDto.setLogin(rows[9].getValue());
				detailsAccountDto.setEnteMis(Integer.parseInt(rows[19].getValue()));

				if (((Integer) mapProducts.get(detailsAccountDto.getProductId())).intValue() == 0) {
					Entity wEntity = new Entity();
					wProcedureResponseGeneral = null;
					consolidateRequest.setHaveToAddCountCte(false);
					switch (detailsAccountDto.getProductId().intValue()) {
					case CHECKING_ACCOUNT:
						wProcedureResponseCtaCte = CoreConsolidateAccountsQuery
								.getConsolidateCheckingAccountByClient(consolidateRequest);
						wProcedureResponseGeneral = wProcedureResponseCtaCte;
						break;
					case SAVING_ACCOUNT:
						wProcedureResponseCtaAho = CoreConsolidateAccountsQuery
								.getConsolidateSavingAccountByClient(consolidateRequest);
						wProcedureResponseGeneral = wProcedureResponseCtaAho;
						break;
					case LOAN:
						wProcedureResponseCtaLoan = CoreConsolidateAccountsQuery
								.getConsolidateLoanAccountByClient(consolidateRequest);
						wProcedureResponseGeneral = wProcedureResponseCtaLoan;
						break;
					case TIME_DEPOSIT:
						wProcedureResponseCtaDpf = CoreConsolidateAccountsQuery
								.getConsolidateFixedTermDepositAccountByClient(consolidateRequest);
						wProcedureResponseGeneral = wProcedureResponseCtaDpf;
						break;
					case CREDIT_CARD:
						SummaryCreditCardRequest summaryCreditCardRequest = new SummaryCreditCardRequest();
						summaryCreditCardRequest.setClient(consolidateRequest.getClient());
						summaryCreditCardRequest.setChannelId(consolidateRequest.getChannelId());
						summaryCreditCardRequest
								.setCodeTransactionalIdentifier(consolidateRequest.getCodeTransactionalIdentifier());
						summaryCreditCardRequest.setCurrency(consolidateRequest.getCurrency());

						wProcedureResponseCardCredit = CoreServiceCardsQuery
								.getSummaryCreditCard(summaryCreditCardRequest);
						// wProcedureResponseGeneral =
						// wProcedureResponseCardCredit;

						break;
					case CREDIT_LINE:
						CreditLineRequest aCreditLineRequest = new CreditLineRequest();
						if (logger.isDebugEnabled())
							logger.logDebug(
									CLASS_NAME + " Lineas de Credito ITO: " + consolidateRequest.getClient().getId());
						wEntity.setEnte(Integer.parseInt(consolidateRequest.getClient().getId()));
						aCreditLineRequest.setEntity(wEntity);
						aCreditLineRequest.setOrigin("IB");
						aCreditLineRequest.setOriginalRequest(anOriginalRequest2);
						wProcedureResponseCreditLine = transformtConsolidateCreditLine(
								CoreServiceCreditLineQuery.getLines(aCreditLineRequest));
						wProcedureResponseGeneral = wProcedureResponseCreditLine;

						break;
					case BANK_GUARANTEE:
						QueryBankGuaranteeRequest aQueryBankGuaranteeReq = new QueryBankGuaranteeRequest();
						if (logger.isDebugEnabled())
							logger.logDebug(
									CLASS_NAME + " Garantias Bancarias ITO: " + consolidateRequest.getClient().getId());
						wEntity.setEnte(Integer.parseInt(consolidateRequest.getClient().getId()));
						aQueryBankGuaranteeReq.setEntity(wEntity);
						aQueryBankGuaranteeReq.setOperation("S");
						aQueryBankGuaranteeReq.setOriginalRequest(anOriginalRequest2);
						wProcedureResponseBankGuarantee = transformtConsolidateBankGuarantee(
								CoreServiceBankGuaranteeQuery.getBankGuarantees(aQueryBankGuaranteeReq));
						wProcedureResponseGeneral = wProcedureResponseBankGuarantee;
						break;
					default:
						break;
					}

					if (wProcedureResponseGeneral != null) {
						if (wProcedureResponseGeneral.getReturnCode() == 0) {
							aConsolidateResponseCta.add(wProcedureResponseGeneral);
							mapConsolidateResponse.put(detailsAccountDto.getProductId(), wProcedureResponseGeneral);
							if (wProcedureResponseGeneral.getProductCollection().size() > 0)
								mapProducts.put(detailsAccountDto.getProductId(), 1);
							else
								mapProducts.put(detailsAccountDto.getProductId(), 2);
						} else
							mapProducts.put(detailsAccountDto.getProductId(), 2);
					} else
						mapProducts.put(detailsAccountDto.getProductId(), 2);
				}
			}

			responseCta.addResponseBlock(intersectAffiliated(wProcResponseAccountsClientLocal, mapConsolidateResponse));
			responseCta.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, "0");
			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + " ITO responseCta.getProcedureResponseAsString(): "
						+ responseCta.getProcedureResponseAsString());
			}

			aBagSPJavaOrchestration.put(SUMMARY_QUERY_CENTRAL_RESP, responseCta);

			if (logger.isDebugEnabled()) {
				if (wProcedureResponseCtaCte != null)
					logger.logDebug(CLASS_NAME + " CTACTE: " + wProcedureResponseCtaCte);
				if (wProcedureResponseCtaAho != null)
					logger.logDebug(CLASS_NAME + " CTAAHO: " + wProcedureResponseCtaAho);
				if (wProcedureResponseCtaLoan != null)
					logger.logDebug(CLASS_NAME + " CTALOAN: " + wProcedureResponseCtaLoan);
				if (wProcedureResponseCtaDpf != null)
					logger.logDebug(CLASS_NAME + " CTADPF: " + wProcedureResponseCtaDpf);
				if (wProcedureResponseCardCredit != null)
					logger.logDebug(CLASS_NAME + " CTACARDCREDIT: " + wProcedureResponseCardCredit);
			}

			aBagSPJavaOrchestration.put(RESPONSE_SUMMARY_CONSOLIDATE_CORE_EMPTY, false);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR DEL SERVICIO OBTENIENDO INFORMACION DEL CONSOLIDADO DEL CORE:"
						+ e.toString());
			throw e;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR INFRAESTRUTURA OBTENIENDO INFORMACION DEL CONSOLIDADO DEL CORE:"
						+ e.toString());
			throw e;
		}
	}

	private ConsolidateResponse transformtConsolidateCreditLine(CreditLineResponse aCreditLineResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando Transformacion de aCreditLineResponse a DTO consolidateResponse:"
					+ aCreditLineResponse);

		ConsolidateResponse consolidateResponse = new ConsolidateResponse();
		List<ProductConsolidate> productConsolidateCollection = new ArrayList<ProductConsolidate>();
		// Utils.transformIprocedureResponseToBaseResponse(consolidateResponse,
		// aCreditLineResponse);
		// if (!response.hasError()) {

		// GCO-manejo de mensajes de Error
		if (aCreditLineResponse.getReturnCode() == 0) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Crea transformtConsolidateCreditLine-ValidationErrorCode 0,Code:"
						+ aCreditLineResponse.getReturnCode());
			/*
			 * IResultSetBlock resulsetProductBalance =
			 * response.getResultSet(1); IResultSetRow[] rowsTemp =
			 * resulsetProductBalance.getData().getRowsAsArray();
			 */
			StringBuilder sbProducts = new StringBuilder(aCreditLineResponse.getCreditLineCollection().size() * 6);
			if (!aCreditLineResponse.getCreditLineCollection().isEmpty()) {

				int i = 0;
				for (CreditLine aCreditLine : aCreditLineResponse.getCreditLineCollection()) {
					// IResultSetRowColumnData[] rows =
					// iResultSetRow.getColumnsAsArray();
					ProductConsolidate productConsolidate = new ProductConsolidate();
					BalanceProduct balanceProduct = new BalanceProduct();
					Product product = new Product();
					Currency currency = new Currency();

					product.setProductType(21);
					product.setProductDescription("--");
					product.setProductName("--");

					if (aCreditLine.getCurrency() != null)
						currency.setCurrencyId(aCreditLine.getCurrency());
					if (aCreditLine.getMoney() != null)
						currency.setCurrencyDescription(aCreditLine.getMoney());
					if (logger.isDebugEnabled())
						logger.logDebug(CLASS_NAME + "*** aCreditLine.getCredit():" + aCreditLine.getCredit());

					if (aCreditLine.getCredit() != null)
						product.setProductNumber(aCreditLine.getCredit());

					if (aCreditLine.getAvailable() != null)
						balanceProduct.setAvailableBalance(new BigDecimal(aCreditLine.getAvailable().toString()));

					if (aCreditLine.getOpeningDate() != null)
						balanceProduct.setDateLastMovent(aCreditLine.getOpeningDate());
					if (aCreditLine.getExpirationDate() != null)
						balanceProduct.setExpirationDate(Utils.formatDate(aCreditLine.getExpirationDate()));

					sbProducts.append(product.getProductNumber());
					sbProducts.append("/");
					sbProducts.append(currency.getCurrencyId().toString());
					sbProducts.append("/");
					sbProducts.append(String.valueOf(i));
					sbProducts.append("~");
					i++;
					productConsolidate.setCurrency(currency);
					productConsolidate.setProduct(product);
					productConsolidate.setBalance(balanceProduct);

					productConsolidateCollection.add(productConsolidate);
				}
			}
			consolidateResponse.setSbProducts(sbProducts);
			consolidateResponse.setProductCollection(productConsolidateCollection);
			// consolidateResponse.setSuccess(response.getReturnCode()==0);
			// consolidateResponse.setReturnCode(response.getReturnCode());
		} else {
			consolidateResponse.setMessages(aCreditLineResponse.getMessages()); // SETEA
																				// ARREGLO
																				// DE
																				// MENSAJES
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Error al transformarResponse en Implementacion-ErrorCode:"
						+ aCreditLineResponse.getReturnCode());

		}
		consolidateResponse.setReturnCode(aCreditLineResponse.getReturnCode());
		/*
		 * }else{ if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME +
		 * "*** Response <hasError> true"); }
		 */

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Respuesta Devuelta productQueryResponse" + consolidateResponse);
		return consolidateResponse;
	}

	private ConsolidateResponse transformtConsolidateBankGuarantee(
			QueryBankGuaranteeResponse aQueryBankGuaranteeResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando Transformacion de aCreditLineResponse a DTO consolidateResponse:"
					+ aQueryBankGuaranteeResponse);

		ConsolidateResponse consolidateResponse = new ConsolidateResponse();
		List<ProductConsolidate> productConsolidateCollection = new ArrayList<ProductConsolidate>();
		// Utils.transformIprocedureResponseToBaseResponse(consolidateResponse,
		// aCreditLineResponse);
		// if (!response.hasError()) {

		// GCO-manejo de mensajes de Error
		if (aQueryBankGuaranteeResponse.getReturnCode() == 0) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Crea transformtConsolidateCreditLine-ValidationErrorCode 0,Code:"
						+ aQueryBankGuaranteeResponse.getReturnCode());
			/*
			 * IResultSetBlock resulsetProductBalance =
			 * response.getResultSet(1); IResultSetRow[] rowsTemp =
			 * resulsetProductBalance.getData().getRowsAsArray();
			 */
			StringBuilder sbProducts = new StringBuilder(
					aQueryBankGuaranteeResponse.getBankGuaranteeCollection().size() * 6);
			if (!aQueryBankGuaranteeResponse.getBankGuaranteeCollection().isEmpty()) {
				int i = 0;
				for (GRB aBankGuarantee : aQueryBankGuaranteeResponse.getBankGuaranteeCollection()) {
					// IResultSetRowColumnData[] rows =
					// iResultSetRow.getColumnsAsArray();
					ProductConsolidate productConsolidate = new ProductConsolidate();
					BalanceProduct balanceProduct = new BalanceProduct();
					Product product = new Product();
					Currency currency = new Currency();

					product.setProductType(9);
					if (aBankGuarantee.getName() != null) {
						product.setProductDescription(aBankGuarantee.getName());
						product.setProductName(aBankGuarantee.getName());
					}

					if (aBankGuarantee.getCurrencyCode() != null)
						currency.setCurrencyId(aBankGuarantee.getCurrencyCode());
					if (aBankGuarantee.getCurrency() != null)
						currency.setCurrencyDescription(aBankGuarantee.getCurrency());
					if (logger.isDebugEnabled())
						logger.logDebug(CLASS_NAME + "*** aCreditLine.getCredit():" + aBankGuarantee.getOperation());

					if (aBankGuarantee.getOperation() != null)
						product.setProductNumber(aBankGuarantee.getOperation());

					if (aBankGuarantee.getAmount() != null)
						balanceProduct.setAvailableBalance(new BigDecimal(aBankGuarantee.getAmount().toString()));

					if (aBankGuarantee.getLaunchingdate() != null)
						balanceProduct.setDateLastMovent(aBankGuarantee.getLaunchingdate());
					if (aBankGuarantee.getExpirationdate() != null)
						balanceProduct.setExpirationDate(Utils.formatDate(aBankGuarantee.getExpirationdate()));

					productConsolidate.setCurrency(currency);
					productConsolidate.setProduct(product);
					productConsolidate.setBalance(balanceProduct);

					sbProducts.append(product.getProductNumber());
					sbProducts.append("/");
					sbProducts.append(currency.getCurrencyId().toString());
					sbProducts.append("/");
					sbProducts.append(String.valueOf(i));
					sbProducts.append("~");
					i++;
					productConsolidateCollection.add(productConsolidate);
				}
			}
			consolidateResponse.setSbProducts(sbProducts);
			consolidateResponse.setProductCollection(productConsolidateCollection);
			// consolidateResponse.setSuccess(response.getReturnCode()==0);
			// consolidateResponse.setReturnCode(response.getReturnCode());
		} else {
			consolidateResponse.setMessages(aQueryBankGuaranteeResponse.getMessages()); // SETEA
																						// ARREGLO
																						// DE
																						// MENSAJES
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Error al transformarResponse en Implementacion-ErrorCode:"
						+ aQueryBankGuaranteeResponse.getReturnCode());

		}
		consolidateResponse.setReturnCode(aQueryBankGuaranteeResponse.getReturnCode());
		/*
		 * }else{ if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME +
		 * "*** Response <hasError> true"); }
		 */

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Respuesta Devuelta productQueryResponse" + consolidateResponse);
		return consolidateResponse;
	}

	private IResultSetBlock intersectAffiliated(IProcedureResponse aResponseAffiliated,
			Map<Object, ConsolidateResponse> aResponseProductsCore) {

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Respuesta Devuelta aResponseAffiliated" + aResponseAffiliated);
			logger.logDebug(CLASS_NAME + " Respuesta Devuelta aResponseProductsCore" + aResponseProductsCore);
		}

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetHeaderColumn columnNumber = new ResultSetHeaderColumn("NRO", ICTSTypes.SQLINT4, 11);
		metaData.addColumnMetaData(columnNumber);
		IResultSetHeaderColumn columnProduct = new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLINT2, 6);
		metaData.addColumnMetaData(columnProduct);
		IResultSetHeaderColumn columnBalance = new ResultSetHeaderColumn("SALDO", ICTSTypes.SQLMONEY, 21);
		metaData.addColumnMetaData(columnBalance);
		IResultSetHeaderColumn columnMorgage = new ResultSetHeaderColumn("HIPOTECARIO", ICTSTypes.SQLCHAR, 1);
		metaData.addColumnMetaData(columnMorgage);
		IResultSetHeaderColumn columnCurrency = new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLTEXT, 511);
		metaData.addColumnMetaData(columnCurrency);
		IResultSetHeaderColumn columnCurrencyDesc = new ResultSetHeaderColumn("CODIGO MONEDA", ICTSTypes.SQLINT2, 6);
		metaData.addColumnMetaData(columnCurrencyDesc);

		IResultSetRow[] dataProductsAffiliated = aResponseAffiliated.getResultSet(1).getData().getRowsAsArray();

		Map<String, Object> quantity = new HashMap<String, Object>();
		Map<String, BigDecimal> summary = new HashMap<String, BigDecimal>();
		Map<String, String> descriptionCurrency = new HashMap<String, String>();
		int product = 0;
		int currency = 0;
		int pos = -1;
		int posSeparador = -1;
		String productNumber = "";

		String dato = null;
		for (int i = 0; i < dataProductsAffiliated.length; i++) {
			IResultSetRowColumnData[] columns = dataProductsAffiliated[i].getColumnsAsArray();
			product = Integer.parseInt(columns[2].getValue());

			currency = Integer.parseInt(columns[3].getValue());
			productNumber = columns[4].getValue();
			pos = -1;
			ConsolidateResponse consolidateResponse = aResponseProductsCore.get(product);
			if (consolidateResponse != null) {

				StringBuilder sb = consolidateResponse.getSbProducts();
				if (sb != null) {
					pos = sb.toString().indexOf(productNumber + "/" + String.valueOf(currency));
					if (pos >= 0) {
						posSeparador = -1;
						posSeparador = sb.toString().substring(pos).indexOf("~");
						if (logger.isDebugEnabled()) {
							logger.logDebug("Existe producto: " + columns[4].getValue() + " Tipo de Producto: "
									+ String.valueOf(product));
						}

						dato = sb.toString().substring(pos, pos + posSeparador);
						String[] arre = dato.split("/");
						if (logger.isDebugEnabled()) {
							logger.logDebug("Dato: " + dato);
						}

						if (arre.length > 0) {
							// sumarizando balance
							int posInConsolidate = Integer.parseInt(arre[2]);
							String key = String.valueOf(product) + "-" + String.valueOf(currency);

							if (logger.isDebugEnabled())
								logger.logDebug("KEY-->" + key);

							if (summary.get(key) != null)
								summary.put(key, summary.get(key).add(consolidateResponse.getProductCollection()
										.get(posInConsolidate).getBalance().getAvailableBalance()));
							else
								summary.put(key, consolidateResponse.getProductCollection().get(posInConsolidate)
										.getBalance().getAvailableBalance());
							// Cuento elementos
							if (logger.isDebugEnabled())
								logger.logDebug("Cuento Elemtos --> " + key);
							// logger.logDebug("Cuento Elemtos --> "+
							// quantity.get(key).toString());
							if (quantity.get(key) != null)
								quantity.put(key, Integer.parseInt(quantity.get(key).toString()) + 1);
							else
								quantity.put(key, 1);
							if (logger.isDebugEnabled())
								logger.logDebug("Cuento Elemtos Fin --> " + key);

							descriptionCurrency.put(key, consolidateResponse.getProductCollection()
									.get(posInConsolidate).getCurrency().getCurrencyDescription());

						}
					}
				}
			}
		}

		IResultSetRow rowCtas;
		try {
			for (Map.Entry<String, BigDecimal> entry : summary.entrySet()) {
				String[] arre = entry.getKey().split("-");

				rowCtas = generateSumConsolidate(Integer.parseInt(quantity.get(entry.getKey()).toString()),
						Integer.parseInt(arre[0]), Integer.parseInt(arre[1]), entry.getValue(),
						descriptionCurrency.get(entry.getKey()));

				data.addRow(rowCtas);

			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR DEL SERVICIO CALCULANDO EL CONSOLIDADO" + e.toString());
		}
		IResultSetBlock resultBlock_CA = new ResultSetBlock(metaData, data);
		return resultBlock_CA;

	}

	private IResultSetBlock setResulsetBlockEmpty() {

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetHeaderColumn columnBalanceAssets = new ResultSetHeaderColumn("SALDO_ACTIVAS", ICTSTypes.SQLMONEY, 11);
		metaData.addColumnMetaData(columnBalanceAssets);
		IResultSetHeaderColumn columnBalanceLiabilities = new ResultSetHeaderColumn("SALDO_PASIVAS", ICTSTypes.SQLMONEY,
				11);
		metaData.addColumnMetaData(columnBalanceLiabilities);
		IResultSetHeaderColumn columnCurrency = new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLTEXT, 511);
		metaData.addColumnMetaData(columnCurrency);

		IResultSetBlock resultBlock_CA = new ResultSetBlock(metaData, data);

		return resultBlock_CA;

	}

	private IResultSetRow generateSumConsolidate(int numberId, int ProductId, int CurrencyId, BigDecimal Balance,
			String CurrencyName) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "RESUMEN CONSOLIDADO DE CUENTAS POR PRODUCTO ITO");
			logger.logDebug(CLASS_NAME + "getNumberId:" + numberId);
			logger.logDebug(CLASS_NAME + "getProductId:" + ProductId);
			logger.logDebug(CLASS_NAME + "getCurrencyId:" + CurrencyId);
			logger.logDebug(CLASS_NAME + "getCurrencyDescription:" + CurrencyName);
			logger.logDebug(CLASS_NAME + "getBalance:" + Balance);
			logger.logDebug(CLASS_NAME + "getMorgage:" + "N");
			logger.logDebug(CLASS_NAME + "----------------------------------------------");
		}

		IResultSetRowColumnData colData1 = new ResultSetRowColumnData(false, Integer.toString(numberId));
		IResultSetRowColumnData colData2 = new ResultSetRowColumnData(false, Integer.toString(ProductId));
		IResultSetRowColumnData colData3 = new ResultSetRowColumnData(false, Balance.toString());
		IResultSetRowColumnData colData4 = new ResultSetRowColumnData(true, "N");
		IResultSetRowColumnData colData5 = new ResultSetRowColumnData(true, Integer.toString(CurrencyId));
		IResultSetRowColumnData colData6 = new ResultSetRowColumnData(false, CurrencyName);

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1, colData1);
		row.addRowData(2, colData2);
		row.addRowData(3, colData3);
		row.addRowData(4, colData4);
		row.addRowData(5, colData6);
		row.addRowData(6, colData5);
		return row;
	}

	/**
	 * Get resulset with information globalizad by product type and currency
	 * type
	 *
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSServiceException
	 */
	private IResultSetBlock createResulsetConsolidateDetail(Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException {

		// Agregar Header - Consolidado General
		IResultSetHeader metaData_CG = new ResultSetHeader();
		IResultSetData data_CG = new ResultSetData();
		IResultSetHeaderColumn columnAssets = new ResultSetHeaderColumn("SALDO ACTIVAS", ICTSTypes.SQLMONEY, 21);
		metaData_CG.addColumnMetaData(columnAssets);
		IResultSetHeaderColumn columnLiabilities = new ResultSetHeaderColumn("SALDO PASIVAS", ICTSTypes.SQLMONEY, 21);
		metaData_CG.addColumnMetaData(columnLiabilities);
		IResultSetHeaderColumn columnCurrencyDescription = new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLTEXT, 286);
		metaData_CG.addColumnMetaData(columnCurrencyDescription);

		List<String> listTotalDetailsAccountDto = (List<String>) aBagSPJavaOrchestration
				.get(LIST_TOTAL_CONSOLIDATE_ASSETS);
		// Agregar Data - Consolidado General
		for (String nameDtoObj : listTotalDetailsAccountDto) {
			IResultSetRow row = new ResultSetRow();
			TotalDetailsAccountDto totalDetailsAccountDto = (TotalDetailsAccountDto) aBagSPJavaOrchestration
					.get(nameDtoObj);

			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + "Assets" + totalDetailsAccountDto.getAssets());
				logger.logDebug(CLASS_NAME + "Liabilities" + totalDetailsAccountDto.getLiabilities());
				logger.logDebug(CLASS_NAME + "CurrencyDescription" + totalDetailsAccountDto.getCurrencyDescription());
			}

			IResultSetRowColumnData row1 = new ResultSetRowColumnData(false,
					totalDetailsAccountDto.getAssets().toString());
			IResultSetRowColumnData row2 = new ResultSetRowColumnData(false,
					totalDetailsAccountDto.getLiabilities().toString());
			IResultSetRowColumnData row3 = new ResultSetRowColumnData(false,
					totalDetailsAccountDto.getCurrencyDescription());

			row.addRowData(1, row1);
			row.addRowData(2, row2);
			row.addRowData(3, row3);
			data_CG.addRow(row);
		}
		IResultSetBlock resultBlock = new ResultSetBlock(metaData_CG, data_CG);
		return resultBlock;
	}

	/**
	 * Get resulset with information globalizad by currency type
	 *
	 * @param listConsolidateAll
	 * @return
	 * @throws CTSServiceException
	 */
	private IResultSetBlock createResulsetConsolidateGroup(List<ConsolidateSummaryDto> listConsolidateAll)
			throws CTSServiceException {
		// Agregar Header - Consolidado Agrupo
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetHeaderColumn columnNumber = new ResultSetHeaderColumn("NRO", ICTSTypes.SQLINT4, 11);
		metaData.addColumnMetaData(columnNumber);
		IResultSetHeaderColumn columnProduct = new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLINT2, 6);
		metaData.addColumnMetaData(columnProduct);
		IResultSetHeaderColumn columnBalance = new ResultSetHeaderColumn("SALDO", ICTSTypes.SQLMONEY, 21);
		metaData.addColumnMetaData(columnBalance);
		IResultSetHeaderColumn columnMorgage = new ResultSetHeaderColumn("HIPOTECARIO", ICTSTypes.SQLCHAR, 1);
		metaData.addColumnMetaData(columnMorgage);
		IResultSetHeaderColumn columnCurrency = new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLTEXT, 511);
		metaData.addColumnMetaData(columnCurrency);
		IResultSetHeaderColumn columnCurrencyDesc = new ResultSetHeaderColumn("CODIGO MONEDA", ICTSTypes.SQLINT2, 6);
		metaData.addColumnMetaData(columnCurrencyDesc);

		// Agregar Data - Consolidado Agrupado
		IResultSetRow rowCtaCte;
		try {
			for (ConsolidateSummaryDto consolidateSummaryDto : listConsolidateAll) {
				rowCtaCte = generateResulsetConsolidate(consolidateSummaryDto);
				if (rowCtaCte != null)
					data.addRow(rowCtaCte);
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR DEL SERVICIO CALCULANDO EL CONSOLIDADO" + e.toString());
			throw new CTSServiceException("Error calculating consolidate data");
		}

		IResultSetBlock resultBlock_CA = new ResultSetBlock(metaData, data);
		return resultBlock_CA;
	}

	/**
	 * Calculate total balance by product type of all core products.
	 *
	 * @param aBagSPJavaOrchestration
	 * @param keyDto
	 * @return List<ConsolidateSummaryDto>
	 */
	private List<ConsolidateSummaryDto> calculateDtoConsolidate(Map<String, Object> aBagSPJavaOrchestration,
			String keyDto) {
		List<ConsolidateSummaryDto> listDto = new ArrayList<ConsolidateSummaryDto>();
		List<String> objDtoCtaCte = (List<String>) aBagSPJavaOrchestration.get(keyDto);
		if (objDtoCtaCte != null) {
			for (String nameDto : objDtoCtaCte) {
				ConsolidateSummaryDto consolidateSummaryDto = (ConsolidateSummaryDto) aBagSPJavaOrchestration
						.get(nameDto);
				if (logger.isDebugEnabled()) {
					logger.logDebug(CLASS_NAME + "Objeto enviado a totalizar" + consolidateSummaryDto);
				}
				acumulateDtoConsolidate(consolidateSummaryDto, aBagSPJavaOrchestration,
						TOTAL_CONSOLIDATE_ASSETS + consolidateSummaryDto.getCurrencyId());
				listDto.add(consolidateSummaryDto);
				if (logger.isDebugEnabled()) {
					logger.logDebug(CLASS_NAME + "Objeto totalizado" + consolidateSummaryDto);
				}
			}
		}
		return listDto;
	}

	/**
	 * Calculate total assets and liabilities by currency type.
	 *
	 * @param consolidateSummaryDto
	 * @param aBagSPJavaOrchestration
	 * @param keyMap
	 */
	private void acumulateDtoConsolidate(ConsolidateSummaryDto consolidateSummaryDto,
			Map<String, Object> aBagSPJavaOrchestration, String keyMap) {
		List<String> listTotalDetailsAccountDto = (List<String>) aBagSPJavaOrchestration
				.get(LIST_TOTAL_CONSOLIDATE_ASSETS);
		if (listTotalDetailsAccountDto == null) {
			listTotalDetailsAccountDto = new ArrayList<String>();
		}
		TotalDetailsAccountDto totalDetailsAccountDto = (TotalDetailsAccountDto) aBagSPJavaOrchestration.get(keyMap);
		if (totalDetailsAccountDto == null) {
			listTotalDetailsAccountDto.add(keyMap);
			aBagSPJavaOrchestration.put(LIST_TOTAL_CONSOLIDATE_ASSETS, listTotalDetailsAccountDto);
			totalDetailsAccountDto = new TotalDetailsAccountDto();
			totalDetailsAccountDto.setCurrencyId(consolidateSummaryDto.getCurrencyId());
			totalDetailsAccountDto.setAssets(new BigDecimal(0));
			totalDetailsAccountDto.setLiabilities(new BigDecimal(0));
		}
		if (consolidateSummaryDto.getCurrencyDescription() != null)
			totalDetailsAccountDto.setCurrencyDescription(
					consolidateSummaryDto.getCurrencyDescription() + "*" + consolidateSummaryDto.getCurrencyId());

		if ((consolidateSummaryDto.getProductId().equals(PRODUCT_CTALOAN))
				|| (consolidateSummaryDto.getProductId().equals(PRODUCT_CREDIT_CARD))) {
			totalDetailsAccountDto
					.setAssets(totalConsolidate(totalDetailsAccountDto.getAssets(), consolidateSummaryDto));
		}

		if ((consolidateSummaryDto.getProductId().equals(PRODUCT_CTACTE))
				|| (consolidateSummaryDto.getProductId().equals(PRODUCT_CTAAHO))
				|| (consolidateSummaryDto.getProductId().equals(PRODUCT_CTADPF))) {
			totalDetailsAccountDto
					.setLiabilities(totalConsolidate(totalDetailsAccountDto.getLiabilities(), consolidateSummaryDto));
		}

		aBagSPJavaOrchestration.put(keyMap, totalDetailsAccountDto);
	}

	private BigDecimal totalConsolidate(BigDecimal valueOriginal, ConsolidateSummaryDto objAditional) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "CALCULANDO SALDO TOTAL DE PRODUCTOS");
			logger.logDebug(CLASS_NAME + "*****************************************");
			logger.logDebug(CLASS_NAME + "Product Id:" + objAditional.getProductId());
			logger.logDebug(CLASS_NAME + "Currency Id:" + objAditional.getCurrencyId());
			logger.logDebug(CLASS_NAME + "Value Original:" + valueOriginal);
			logger.logDebug(CLASS_NAME + "Aditional:" + objAditional.getBalance());
			logger.logDebug(CLASS_NAME + "*****************************************");
		}

		if (objAditional.getBalance() != null)
			return valueOriginal.add(objAditional.getBalance());
		return valueOriginal;
	}

	/**
	 * Generate rows of resulset of detail consolidate.
	 *
	 * @param arg0
	 * @return
	 * @throws Exception
	 */
	private IResultSetRow generateResulsetConsolidate(ConsolidateSummaryDto arg0) throws Exception {
		if (arg0.getNumberId() > 0) {
			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + "RESUMEN CONSOLIDADO DE CUENTAS POR PRODUCTO");
				logger.logDebug(CLASS_NAME + "getNumberId:" + arg0.getNumberId());
				logger.logDebug(CLASS_NAME + "getProductId:" + arg0.getProductId());
				logger.logDebug(CLASS_NAME + "getCurrencyId:" + arg0.getCurrencyId());
				logger.logDebug(CLASS_NAME + "getCurrencyDescription:" + arg0.getCurrencyDescription());
				logger.logDebug(CLASS_NAME + "getBalance:" + arg0.getBalance());
				logger.logDebug(CLASS_NAME + "getMorgage:" + arg0.getMorgage());
				logger.logDebug(CLASS_NAME + "----------------------------------------------");
			}

			IResultSetRowColumnData colData1 = new ResultSetRowColumnData(false, arg0.getNumberId().toString());
			IResultSetRowColumnData colData2 = new ResultSetRowColumnData(false, arg0.getProductId().toString());
			IResultSetRowColumnData colData3 = new ResultSetRowColumnData(false, arg0.getBalance().toString());
			IResultSetRowColumnData colData4 = new ResultSetRowColumnData(true, arg0.getMorgage());
			IResultSetRowColumnData colData5 = new ResultSetRowColumnData(true, arg0.getCurrencyId().toString());
			IResultSetRowColumnData colData6 = new ResultSetRowColumnData(false,
					arg0.getCurrencyDescription() + "*" + arg0.getProductDescription());

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, colData1);
			row.addRowData(2, colData2);
			row.addRowData(3, colData3);
			row.addRowData(4, colData4);
			row.addRowData(5, colData6);
			row.addRowData(6, colData5);
			return row;
		}
		return null;
	}

	/**
	 * Getting products of core with information of local products.
	 *
	 * @param infoAccountLocal
	 * @param aBagSPJavaOrchestration
	 */
	@SuppressWarnings("unchecked")
	private void findAccount(DetailsAccountDto infoAccountLocal, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Entrando en findAccount");
		List<ConsolidateAccountsDto> listDto = null;
		List<ConsolidateCardsDto> listCardsDto = null;

		ConsolidateSummaryDto wConsolidateSummaryDto = null;
		String keyCurrentMapProduct = "";
		String keyListCurrency = "";
		String keyResponseSummaryConsolidate = "";

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " CUENTA SELECCIONADA:" + infoAccountLocal.getCurrencyId() + ","
					+ infoAccountLocal.getAccountNumber());

		if (infoAccountLocal.getProductId() == PRODUCT_CTACTE) {
			keyCurrentMapProduct = CONSOLIDATE_CTACTE + infoAccountLocal.getCurrencyId();
			keyListCurrency = LIST_CURRENCY_CTACTE;
			keyResponseSummaryConsolidate = RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTACTE;
		}

		if (infoAccountLocal.getProductId() == PRODUCT_CTAAHO) {
			keyCurrentMapProduct = CONSOLIDATE_CTAAHO + infoAccountLocal.getCurrencyId();
			keyListCurrency = LIST_CURRENCY_CTAAHO;
			keyResponseSummaryConsolidate = RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTAAHO;
		}

		if (infoAccountLocal.getProductId() == PRODUCT_CTALOAN) {
			keyCurrentMapProduct = CONSOLIDATE_CTALOAN + infoAccountLocal.getCurrencyId();
			keyListCurrency = LIST_CURRENCY_CTALOAN;
			keyResponseSummaryConsolidate = RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTALOAN;
		}

		if (infoAccountLocal.getProductId() == PRODUCT_CTADPF) {
			keyCurrentMapProduct = CONSOLIDATE_CTADPF + infoAccountLocal.getCurrencyId();
			keyListCurrency = LIST_CURRENCY_CTADPF;
			keyResponseSummaryConsolidate = RESPONSE_SUMMARY_CONSOLIDATE_CORE_CTADPF;
		}

		if (infoAccountLocal.getProductId() == PRODUCT_CREDIT_CARD) {
			keyCurrentMapProduct = CONSOLIDATE_CREDITCARD + infoAccountLocal.getCurrencyId();
			keyListCurrency = LIST_CURRENCY_CREDITCARD;
			keyResponseSummaryConsolidate = RESPONSE_SUMMARY_CONSOLIDATE_CORE_CREDITCARD;
		}

		wConsolidateSummaryDto = (ConsolidateSummaryDto) aBagSPJavaOrchestration.get(keyCurrentMapProduct);
		if (wConsolidateSummaryDto == null) {
			utilityTransform.verifyMapService(keyCurrentMapProduct, keyListCurrency, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(keyCurrentMapProduct, new ConsolidateSummaryDto(0,
					infoAccountLocal.getProductId(), new BigDecimal(0), infoAccountLocal.getCurrencyId()));
		}

		if (infoAccountLocal.getProductId() == PRODUCT_CREDIT_CARD) {
			listCardsDto = (List<ConsolidateCardsDto>) aBagSPJavaOrchestration.get(keyResponseSummaryConsolidate);
			if (logger.isDebugEnabled())
				logger.logDebug("BUSQUEDA EN CONSOLIDADO DE TARJETAS DE CREDITO");
		} else {
			listDto = (List<ConsolidateAccountsDto>) aBagSPJavaOrchestration.get(keyResponseSummaryConsolidate);
			if (logger.isDebugEnabled())
				logger.logDebug("BUSQUEDA EN CONSOLIDADO DE CUENTAS");
		}

		wConsolidateSummaryDto = (ConsolidateSummaryDto) aBagSPJavaOrchestration.get(keyCurrentMapProduct);
		if (infoAccountLocal.getProductId() == PRODUCT_CREDIT_CARD) {
			if (listCardsDto != null) {
				for (ConsolidateCardsDto infoCardsCore : listCardsDto) {
					if (logger.isDebugEnabled()) {
						logger.logDebug("CUENTA ENCONTRADA:" + infoCardsCore.getNumber() + " Currency id: "
								+ infoCardsCore.getCurrencyId());
						logger.logDebug(CLASS_NAME + "Saldo Inicial:" + wConsolidateSummaryDto.getBalance());

						logger.logDebug(CLASS_NAME + "INFORMACION DEL CORE: " + infoCardsCore);
						logger.logDebug(CLASS_NAME + "INFORMACION DEL CORE: " + infoAccountLocal);

					}
					if (infoCardsCore != null && infoAccountLocal != null && infoCardsCore.getNumber() != null
							&& infoCardsCore.getCurrencyId() != null) {
						if (infoCardsCore.getNumber().equals(infoAccountLocal.getAccountNumber())
								&& infoCardsCore.getCurrencyId().equals(infoAccountLocal.getCurrencyId())) {

							wConsolidateSummaryDto.setNumberId(wConsolidateSummaryDto.getNumberId() + 1);
							wConsolidateSummaryDto.setProductId(PRODUCT_CREDIT_CARD);
							// DARWIN
							if (wConsolidateSummaryDto.getBalance() != null && infoCardsCore != null
									&& infoCardsCore.getAmmountPayment() != null) {
								wConsolidateSummaryDto.setBalance(
										wConsolidateSummaryDto.getBalance().add(infoCardsCore.getAmmountPayment()));
							} else {
								wConsolidateSummaryDto
										.setBalance(wConsolidateSummaryDto.getBalance().add(new BigDecimal(0)));
							}
							wConsolidateSummaryDto.setMorgage("N");
							if (infoCardsCore.getCurrencyId() != null)
								wConsolidateSummaryDto.setCurrencyId(infoCardsCore.getCurrencyId());
							if (infoCardsCore.getCurrencyDescription() != null)
								wConsolidateSummaryDto.setCurrencyDescription(infoCardsCore.getCurrencyDescription());
							wConsolidateSummaryDto.setProductDescription("TARJETA CREDITO");

							// Almacenamiento del dto en el contexto
							aBagSPJavaOrchestration.put(keyCurrentMapProduct, wConsolidateSummaryDto);

							if (logger.isDebugEnabled())
								logger.logDebug(CLASS_NAME + "Saldo Acumulado:" + wConsolidateSummaryDto.getBalance());
							break;
						}
					}
				}
			}
		} else {
			if (listDto != null) {
				for (ConsolidateAccountsDto infoAccountCore : listDto) {
					if (infoAccountCore.getProductNumber().equals(infoAccountLocal.getAccountNumber())
							&& infoAccountCore.getCurrencyId().equals(infoAccountLocal.getCurrencyId())) {

						if (logger.isDebugEnabled()) {
							logger.logDebug("CUENTA ENCONTRADA:" + infoAccountCore.getProductNumber() + " Currency id: "
									+ infoAccountCore.getCurrencyId() + " Product id:"
									+ infoAccountCore.getProductId());
							logger.logDebug(CLASS_NAME + "Saldo Inicial:" + wConsolidateSummaryDto.getBalance());

							logger.logDebug(CLASS_NAME + "INFORMACION DEL CORE: " + infoAccountCore);
						}

						wConsolidateSummaryDto.setNumberId(wConsolidateSummaryDto.getNumberId() + 1);
						wConsolidateSummaryDto.setProductId(infoAccountCore.getProductId());
						if (infoAccountCore != null && wConsolidateSummaryDto != null
								&& wConsolidateSummaryDto.getBalance() != null
								&& infoAccountCore.getEquityBalance() != null) {
							wConsolidateSummaryDto.setBalance(
									wConsolidateSummaryDto.getBalance().add(infoAccountCore.getEquityBalance()));
						} else {
							wConsolidateSummaryDto
									.setBalance(wConsolidateSummaryDto.getBalance().add(new BigDecimal(0)));
						}
						wConsolidateSummaryDto.setMorgage("N");

						if (infoAccountCore.getCurrencyId() != null)
							wConsolidateSummaryDto.setCurrencyId(infoAccountCore.getCurrencyId());
						if (infoAccountCore.getCurrencyDescription() != null)
							wConsolidateSummaryDto.setCurrencyDescription(infoAccountCore.getCurrencyDescription());
						if (infoAccountCore.getProductDescription() != null)
							wConsolidateSummaryDto.setProductDescription(infoAccountCore.getProductDescription());

						// Almacenamiento del dto en el contexto
						aBagSPJavaOrchestration.put(keyCurrentMapProduct, wConsolidateSummaryDto);

						if (logger.isDebugEnabled())
							logger.logDebug(CLASS_NAME + "Saldo Acumulado:" + wConsolidateSummaryDto.getBalance());
						break;
					}
				}
			}
		}
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Saliendo en findAccount");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader reader) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");

			logger.logInfo("init...");
			logger.addNDC();
		}
		Map<String, String> properties = new HashMap<String, String>();
		List<Node> propertiesList = reader.getNodeList("//config//own//property");
		if (propertiesList.size() > 0) {
			for (Node node : propertiesList) {
				String propName = reader.getProperty(node, "name");
				String propValue = reader.getProperty(node, "value");
				if (logger.isTraceEnabled()) {
					logger.logTrace("[OrchestratorService] [loadConfiguration] *****/////**** Se carga la propiedad "
							+ propName + ", con valor " + propValue);
				}
				properties.put("COBIS_" + propName, propValue);
			}
		} else {
			if (logger.isTraceEnabled())
				logger.logTrace(
						"[OrchestratorService] [loadConfiguration] *****/////**** El archivo de configuraciones no tiene nodos del tipo property");
		}
		if (logger.isTraceEnabled())
			logger.logTrace(
					"[OrchestratorService] [loadConfiguration] *****/////**** propiedades cargadas " + properties);

		if (logger.isInfoEnabled()) {
			logger.removeNDC();
			logger.logInfo("done...");
		}
	}

	public static void copyParam(String wParamName, IProcedureRequest wIProcedureRequestSource,
			IProcedureRequest wIProcedureRequestResult) {
		IProcedureRequestParam wPRParam = wIProcedureRequestSource.readParam(wParamName);
		if (wPRParam != null) {
			wIProcedureRequestResult.addParam(wPRParam.getName(), wPRParam.getDataType(), wPRParam.getIOType(),
					wPRParam.getLen(), wPRParam.getValue());
		}
	}
}
