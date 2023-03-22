package com.cobiscorp.ecobis.orchestration.core.ib.query.get.own.acounts.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISRuntimeException;
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
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceGetCurrencies;

import Utils.ConstantsMessageResponse;
import cobiscorp.ecobis.cts.integration.services.ICTSServiceIntegration;

/**
 * Generated Transaction Factor
 * 
 * @since Mar 14, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "GetOwnAccountsViewQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetOwnAccountsViewQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GetOwnAccountsViewQueryOrchestationCore") })
public class GetOwnAccountsViewQueryOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(GetOwnAccountsViewQueryOrchestationCore.class);
	private static final String CLASS_NAME = "GetOwnAccountsViewQueryOrchestationCore--->";
	// private static final String SERVICE_OUTPUT_VALUES =
	// "com.cobiscorp.cobis.cts.service.response.output";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final int CHANNEL_REQUEST = 8;

	@Reference(referenceInterface = ICoreServiceGetCurrencies.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceGetCurrencies coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceGetCurrencies service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceGetCurrencies service) {
		coreService = null;
	}

	/**
	 * Instance of ICTSServiceIntegration
	 */
	@Reference(bind = "setServiceIntegration", unbind = "unsetServiceIntegration", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private ICTSServiceIntegration serviceIntegration;

	/**
	 * Method that set the instance of ICTSServiceIntegration
	 * 
	 * @param serviceIntegration
	 *            Instance of ICTSServiceIntegration
	 */
	public void setServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = serviceIntegration;
	}

	/**
	 * Method that unset the instance of ICTSServiceIntegration
	 * 
	 * @param serviceIntegration
	 *            Instance of ICTSServiceIntegration
	 */
	public void unsetServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = null;
	}

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
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

		// TransactionFactorResponse responseOtp = new
		// TransactionFactorResponse();
		Map<String, String> login = new HashMap<String, String>();
		// Message message = new Message();

		// Obtener el login del ente
		login = getLoginById(anOriginalRequest);
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		if (login.containsKey("o_login")) {
			return getOwnAccounts(anOriginalRequest);
		} else {
			return processResponseError();
		}

	}

	public IProcedureResponse processResponseError() {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1,
				new ResultSetRowColumnData(false, String.valueOf(ConstantsMessageResponse.MSG400099.getIdMessage())));
		row.addRowData(2, new ResultSetRowColumnData(false, ConstantsMessageResponse.MSG400099.getDescriptionMessage()));

		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "false"));
		data2.addRow(row2);

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);

		return anOriginalProcedureResponse;
	}

	protected Map<String, String> getLoginById(IProcedureRequest aRequest) {

		IProcedureRequest request = new ProcedureRequestAS();
		String login, numPhone, mail, ente = null;
		Map<String, String> responseLogin = new HashMap<String, String>();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getLoginById");
		}

		request.setSpName("cob_bvirtual..sp_cons_ente_med_envio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_customer_id"));
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_mail_ente", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_num_phone", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getLoginById");
		}

		logger.logDebug("readValueParam @o_login: " + wProductsQueryResp.readValueParam("@o_login"));
		login = wProductsQueryResp.readValueParam("@o_login");
		numPhone = wProductsQueryResp.readValueParam("@o_num_phone");
		mail = wProductsQueryResp.readValueParam("@o_mail_ente");
		ente = wProductsQueryResp.readValueParam("@o_ente");

		if (!login.equals("X")) {
			responseLogin.put("o_login", login);
		}
		if (!numPhone.equals("X")) {
			responseLogin.put("o_num_phone", numPhone);
		}
		if (!mail.equals("X")) {
			responseLogin.put("o_mail", mail);
		}
		if (!ente.equals("X")) {
			responseLogin.put("o_ente", ente);
		}

		return responseLogin;
	}

	private IProcedureResponse getOwnAccounts(IProcedureRequest aRequest) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getOwnAccounts");
		}

		request.setSpName("cobis..sp_get_own_account_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_customer_id"));

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getLoginById");
		}

		return wProductsQueryResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse stepsOrquestation(IProcedureRequest anOriginalRequest, String login, Integer enteId,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse wSummeryProductResp = null;
		String ssnBranch;

		if (logger.isDebugEnabled()) {
			logger.logDebug("Iniciando el metodo stepsOrquestation: ");
		}

		// tokenRequest.setChannel(8);
		try {

			IProcedureResponse wProductsQueryResp = this.getSummaryBalance(anOriginalRequest.clone(), login, enteId,
					aBagSPJavaOrchestration);

			logger.logDebug("readValueParam @o_ssn_branch: " + wProductsQueryResp.readValueParam("@o_ssn_branch"));
			ssnBranch = wProductsQueryResp.readValueParam("@o_ssn_branch");

			if (ssnBranch != null) {

				wSummeryProductResp = this.getSummaryProduct(anOriginalRequest.clone(), ssnBranch, login, enteId,
						aBagSPJavaOrchestration);

			}

			return wSummeryProductResp;

		} catch (COBISRuntimeException e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "COBISRuntimeException en metodo getOwnAccounts:");

			}

		}

		return processResponseAccounts(wSummeryProductResp);
	}

	protected IProcedureResponse getSummaryBalance(IProcedureRequest request, String login, Integer enteId,
			Map<String, Object> aBagSPJavaOrchestration) {
		final String METHOD_NAME = "[getSummaryBalance]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@s_cliente", ICTSTypes.SQLINT4, enteId.toString());
		request.addInputParam("@i_nregistros", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_miembro", ICTSTypes.SQLINT4, request.readValueParam("@i_customer_id"));
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, login);

		// VARIABLES DE SALIDA
		logger.logInfo(METHOD_NAME + " Datos Salida");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLVARCHAR, "0");

		logger.logInfo(METHOD_NAME + " Datos Cabecera");
		// Date fecha = new Date();
		// SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
		request.setSpName("cob_procesador..sp_cons_resumen_ctas");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=ConsolidateQueryOrchestationCore)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_cons_resumen_ctas");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800116");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "1800116");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo(request);

		logger.logInfo(METHOD_NAME + "JCOS Replay from orchestrator ");

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR getSummaryBalance");

		logger.logInfo(METHOD_NAME + "LIST ACCOUNT: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "SALIDA EJECUCION ORQUESTADOR getSummaryBalance");

		return connectorSpeiResponse;
	}

	protected IProcedureResponse getSummaryProduct(IProcedureRequest request, String ssnBranch, String login,
			Integer enteId, Map<String, Object> aBagSPJavaOrchestration) {
		final String METHOD_NAME = "[getSummaryProduct]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@s_cliente", ICTSTypes.SQLINT4, enteId.toString());
		request.addInputParam("@i_nregistros", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, login);
		request.addInputParam("@i_ssn", ICTSTypes.SQLINT4, ssnBranch);
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_hipotecario", ICTSTypes.SQLVARCHAR, "A");
		request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, "0");
		request.addInputParam("@i_siguiente", ICTSTypes.SQLINT4, ssnBranch);

		// VARIABLES DE SALIDA
		logger.logInfo(METHOD_NAME + " Datos Salida");

		logger.logInfo(METHOD_NAME + " Datos Cabecera");
		// Date fecha = new Date();
		request.setSpName("cob_procesador..sp_cons_resumen_ctas");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format("01/05/2023"));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=DetailedConsolidatedQueryOrchestrationCore)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Products.Service.Product.GetSummaryProducts");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_cons_resumen_ctas");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800110");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "1800110");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo(METHOD_NAME + "DCO PTO CTR");

		logger.logInfo(request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR getSummaryProduct");

		logger.logInfo(METHOD_NAME + "LIST ACCOUNT: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "SALIDA EJECUCION ORQUESTADOR getSummaryProduct");

		return connectorSpeiResponse;
	}

	public IProcedureResponse processResponseAccounts(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		if (anOriginalProcedureRes != null) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " ProcessResponse original wProcedureRespFinal:"
						+ anOriginalProcedureRes.getProcedureResponseAsString());
			}

		}

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row.addRowData(2, new ResultSetRowColumnData(false, "success"));

		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data2.addRow(row2);

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);

		if (anOriginalProcedureRes != null
				&& anOriginalProcedureRes.getResultSet(1).getData().getRowsAsArray().length > 0) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(
						CLASS_NAME + " Response final: " + anOriginalProcedureResponse.getProcedureResponseAsString());
			}
			// anOriginalProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
			// ICOBISTS.HEADER_STRING_TYPE,
			// ICSP.ERROR_EXECUTION_SERVICE);

			// anOriginalProcedureResponse = new ProcedureResponseAS();

			IResultSetHeader metaData0 = new ResultSetHeader();
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("drawBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT4, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("currencySymbol", ICTSTypes.SQLVARCHAR, 20));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("productAlias", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SQLINT4, 10));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("productName", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("productAbbreviation", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SQLVARCHAR, 10));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SQLVARCHAR, 10));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("totalCredit", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("clabeInterBank", ICTSTypes.SQLVARCHAR, 100));

			IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
			IResultSetData data0 = new ResultSetData();

			for (IResultSetRow iResultSetRow : rowsTemp) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				IResultSetRow rowDat = new ResultSetRow();

				rowDat.addRowData(1, new ResultSetRowColumnData(false, columns[7].getValue()));
				rowDat.addRowData(2, new ResultSetRowColumnData(false, columns[6].getValue()));
				rowDat.addRowData(3, new ResultSetRowColumnData(false, columns[19].getValue()));
				rowDat.addRowData(4,
						new ResultSetRowColumnData(false, getCurrency(Integer.parseInt(columns[19].getValue()))));
				rowDat.addRowData(5, new ResultSetRowColumnData(false, columns[2].getValue()));
				rowDat.addRowData(6, new ResultSetRowColumnData(false, columns[18].getValue()));
				rowDat.addRowData(7, new ResultSetRowColumnData(false, columns[1].getValue()));
				rowDat.addRowData(8, new ResultSetRowColumnData(false, columns[0].getValue()));
				rowDat.addRowData(9, new ResultSetRowColumnData(false, columns[3].getValue()));
				rowDat.addRowData(10, new ResultSetRowColumnData(false, columns[17].getValue()));
				rowDat.addRowData(11, new ResultSetRowColumnData(false, columns[5].getValue()));
				rowDat.addRowData(12, new ResultSetRowColumnData(false, columns[9].getValue()));
				rowDat.addRowData(13, new ResultSetRowColumnData(false, columns[10].getValue()));
				rowDat.addRowData(14, new ResultSetRowColumnData(false, columns[20].getValue()));
				rowDat.addRowData(15, new ResultSetRowColumnData(false, columns[21].getValue()));

				data0.addRow(rowDat);

			}

			IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0, data0);
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock0);

		}

		logger.logInfo(CLASS_NAME + "Response final dco" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
	}

	private String getCurrency(int code) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Iniciando el metodo getCurrency");
		}

		String currencySymbol = "MXN";

		CurrencyResponse aCurrencyResponsen = new CurrencyResponse();
		CurrencyRequest aCurrencyRequestn = new CurrencyRequest();
		aCurrencyRequestn.setMode(0);
		try {
			aCurrencyResponsen = coreService.GetCurrencies(aCurrencyRequestn);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		int i = 0;
		for (com.cobiscorp.ecobis.ib.orchestration.dtos.Currency aCurrency : aCurrencyResponsen
				.getCurrencyCollection()) {

			if (aCurrency.getCurrencyId().equals(code)) {
				currencySymbol = aCurrency.getCurrencyNemonic();
			}
			i = i + 1;

		}

		return currencySymbol;

	}

}
