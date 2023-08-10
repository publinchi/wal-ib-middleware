package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobis.trfspeiservice.bsl.dto.SpeiMappingRequest;
import com.cobis.trfspeiservice.bsl.dto.SpeiMappingResponse;
import com.cobis.trfspeiservice.bsl.serv.ISpeiServiceOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
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
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferOfflineTemplate;

/**
 * Register Account
 * 
 * @since Abr 17, 2023
 * @author sochoa
 * @version 1.0.0
 * 
 */
@Component(name = "TransferSpeiApiOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferSpeiApiOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferSpeiApiOrchestationCore") })
public class TransferSpeiApiOrchestationCore extends TransferOfflineTemplate {

	private static final String S_SSN_BRANCH = "@s_ssn_branch";
	private static final String I_NOMBRE_BENEF = "@i_nombre_benef";
	private static final String T_RTY = "@t_rty";
	private static final String T_EJEC = "@t_ejec";
	private static final String S_OFI = "@s_ofi";
	private static final String S_DATE_LOCAL = "@s_date";
	private static final String S_SRV = "@s_srv";
	private static final String S_ROL = "@s_rol";
	private static final String S_TERM = "@s_term";
	private static final String S_USER = "@s_user";
	private static final String I_MON_DES_LOCAL = "@i_mon_des";
	private static final String I_PROD_DES_LOCAL = "@i_prod_des";
	private static final String I_CTA_DES_LOCAL = "@i_cta_des";
	private static final String I_BANCO_BEN = "@i_banco_ben";
	private static final String I_DOC_BENEF = "@i_doc_benef";
	private static final String I_CONCEPTO_LOCAL = "@i_concepto";
	private static final String I_VAL_LOCAL = "@i_val";
	private static final String I_MON_LOCAL = "@i_mon";
	private static final String I_CTA_LOCAL = "@i_cta";
	private static final String S_SERVICIO_LOCAL = "@s_servicio";
	private static final String I_PROD_LOCAL = "@i_prod";
	private static final String CANCEL_OPERATION = "0";

	private static ILogger logger = LogFactory.getLogger(TransferSpeiApiOrchestationCore.class);
	private static final String CLASS_NAME = "TransferSpeiApiOrchestationCore--->";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final String CHANNEL_REQUEST = "8";

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Reference(referenceInterface = ISpeiServiceOrchestration.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindSpeiOrchestration", unbind = "unbindSpeiOrchestration")
	protected ISpeiServiceOrchestration speiOrchestration;

	public void bindSpeiOrchestration(ISpeiServiceOrchestration service) {
		speiOrchestration = service;
	}

	public void unbindSpeiOrchestration(ISpeiServiceOrchestration service) {
		speiOrchestration = null;
	}
	
	@Override
	public void loadConfiguration(IConfigurationReader arg) {
		if (logger.isInfoEnabled())
			logger.logInfo("LOAD CONFIGUATION");
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original input
	 * parameters.
	 * 
	 * @param anOriginalRequest       - Information original sended by user's.
	 * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();

		anProcedureResponse = transferSpei(anOriginalRequest, aBagSPJavaOrchestration);

		return processResponseTransfer(anProcedureResponse);

	}

	private IProcedureResponse transferSpei(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en transferSpei");
		}

		IProcedureResponse wAccountsResp = new ProcedureResponseAS();

		wAccountsResp = getDataTransfSpeiReq(aRequest, aBagSPJavaOrchestration);
		logger.logInfo(CLASS_NAME + " zczc " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());

		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {

			IProcedureResponse wTransferResponse = new ProcedureResponseAS();
			logger.logInfo(CLASS_NAME + " XDCX " + aBagSPJavaOrchestration.get("o_prod")
					+ aBagSPJavaOrchestration.get("o_mon") + aBagSPJavaOrchestration.get("o_prod_des")
					+ aBagSPJavaOrchestration.get("o_mon_des") + aBagSPJavaOrchestration.get("o_prod_alias")
					+ aBagSPJavaOrchestration.get("o_nom_beneficiary") + aBagSPJavaOrchestration.get("o_login")
					+ aBagSPJavaOrchestration.get("o_ente_bv"));

			wTransferResponse = executeTransferApi(aRequest, aBagSPJavaOrchestration);
			return wTransferResponse;
		}

		return wAccountsResp;
	}

	private IProcedureResponse getDataTransfSpeiReq(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataTransfSpeiReq");
		}

		request.setSpName("cob_bvirtual..sp_get_data_transf_spei_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN,
				aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_origin_account_number", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_origin_account_number"));
		request.addInputParam("@i_destination_account_number", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_destination_account_number"));
		request.addInputParam("@i_amount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
		request.addInputParam("@i_bank_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_id"));
		request.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_name"));
		request.addInputParam("@i_destination_account_owner_name", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_destination_account_owner_name"));
		request.addInputParam("@i_destination_type_account", ICTSTypes.SQLINTN,
				aRequest.readValueParam("@i_destination_type_account"));
		
		request.addInputParam("@i_owner_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_owner_name"));
		request.addInputParam("@i_detail", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detail"));
		request.addInputParam("@i_commission", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_commission"));
		request.addInputParam("@i_latitude", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitude"));
		request.addInputParam("@i_longitude", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitude"));
		request.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_reference_number"));

		request.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_prod_alias", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_nom_beneficiary", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		aBagSPJavaOrchestration.put("o_prod", wProductsQueryResp.readValueParam("@o_prod"));
		aBagSPJavaOrchestration.put("o_mon", wProductsQueryResp.readValueParam("@o_mon"));
		aBagSPJavaOrchestration.put("o_prod_des", wProductsQueryResp.readValueParam("@o_prod_des"));
		aBagSPJavaOrchestration.put("o_mon_des", wProductsQueryResp.readValueParam("@o_mon_des"));
		aBagSPJavaOrchestration.put("o_prod_alias", wProductsQueryResp.readValueParam("@o_prod_alias"));
		aBagSPJavaOrchestration.put("o_nom_beneficiary", wProductsQueryResp.readValueParam("@o_nom_beneficiary"));
		aBagSPJavaOrchestration.put("o_login", wProductsQueryResp.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_ente_bv", wProductsQueryResp.readValueParam("@o_ente_bv"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getDataTransfSpeiReq DCO : "
					+ wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataTransfSpeiReq");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse executeTransferApi(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = initProcedureRequest(aRequest);
		final String METHOD_NAME = "[executeTransferSpei]";

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeTransfer");
		}

		/*
		 * curp = (String) aBagSPJavaOrchestration.get("o_curp"); beneficiary = (String)
		 * aBagSPJavaOrchestration.get("o_beneficiary"); product = (String)
		 * aBagSPJavaOrchestration.get("o_producto");
		 */

		request.addInputParam("@s_cliente", ICTSTypes.SQLINT4,
				aBagSPJavaOrchestration.get("o_ente_bv").toString().trim());
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("o_login").toString().trim());

		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_number"));
		request.addInputParam("@i_mon", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_mon").toString().trim());
		request.addInputParam("@i_prod", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_prod").toString().trim());

		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_destination_account_number"));
		request.addInputParam("@i_mon_des", ICTSTypes.SQLINT4,
				aBagSPJavaOrchestration.get("o_mon_des").toString().trim());
		request.addInputParam("@i_prod_des", ICTSTypes.SQLINT4,
				aBagSPJavaOrchestration.get("o_prod_des").toString().trim());
		request.addInputParam("@i_nombre_benef", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("o_nom_beneficiary").toString().trim());
		// request.addInputParam("@i_nom_cliente", ICTSTypes.SQLINT4,
		// aBagSPJavaOrchestration.get("o_prod_alias").toString().trim());

		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
		// request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,
		// aRequest.readValueParam("@i_concept"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detail"));// poner en el CWC
		request.addInputParam("@i_banco_ben", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_id"));
		// request.addInputParam("@i_nom_banco_des", ICTSTypes.SQLVARCHAR,
		// aRequest.readValueParam("@i_bank_name"));
		request.addInputParam("@i_nombre_cta_dest", ICTSTypes.SQLVARCHAR,
				aRequest.readValueParam("@i_destination_account_owner_name"));
		request.addInputParam("@i_detail", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detail"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_commission"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitude"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitude"));
		request.addInputParam("@i_reference_number", ICTSTypes.SQLINTN,
				aRequest.readValueParam("@i_reference_number"));
		
		
		request.addInputParam("@s_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_sesn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_sesn"));
		request.addInputParam("@t_ssn_corr", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_ssn_corr"));
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_lsrv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_lsrv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_date"));
		request.addInputParam("@s_ofi", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@s_rol", ICTSTypes.SQLVARCHAR,  aRequest.readValueParam("@s_rol"));
		request.addInputParam("@s_sev", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_sev"));
		request.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("U"));
		request.addInputParam("@t_filial", ICTSTypes.SQLVARCHAR, "1");
		request.addInputParam("@t_corr", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
		request.addInputParam("@t_debug", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1870013");
		request.addInputParam("@s_servicio", ICTSTypes.SQLVARCHAR, "8");
		
		// 18500115
		logger.logInfo(METHOD_NAME + " Datos Cabecera");
		// Date fecha = new Date();
		request.setSpName("cob_procesador..sp_tr04_transferencia_ob");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format("01/05/2023"));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=SPITransferOrchestrationCore)");
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
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, aRequest.readValueParam("@s_sesn"));
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_term"));
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Transfers.Service.Transfer.TransferSPI");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE,
				"cob_procesador..sp_tr04_transferencia_ob");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1870013");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_srv"));
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_lsrv"));
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_user"));

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "1870013");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo(request);

		//IProcedureResponse responseTransferSpei = executeCoreBanking(request);
		
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPI");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, request);
		try {
			executeStepsTransactionsBase(request, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processResponse(request, aBagSPJavaOrchestration);
		
		//IProcedureResponse responseTransferSpei = executeTransfer(aBagSPJavaOrchestration);

		/*if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO API: " + responseTransferSpei.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeTransfer");
		}

		return responseTransferSpei;*/
	}

	public IProcedureResponse processResponseTransfer(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
			logger.logInfo("xdcxv --->" + anOriginalProcedureRes.readValueParam("@o_referencia"));
			// logger.logInfo("xdcxv --->" +
			// anOriginalProcedureRes.readValueParam("@o_tracking_key") );
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		String code = null, message, success, referenceCode = null;
		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		referenceCode = anOriginalProcedureRes.readValueParam("@o_referencia");
		// trackingKey = anOriginalProcedureRes.readValueParam("@o_tracking_key");

		logger.logInfo("xdcxv2 --->" + referenceCode);
		if (codeReturn == 0) {
			if (null != referenceCode) {
				code = "0";
				message = "Success";
				success = "true";
				referenceCode = anOriginalProcedureRes.readValueParam("@o_referencia").toString().trim();
				logger.logInfo("bnbn true--->" + referenceCode);
			} else {
				
				code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				
				logger.logInfo("bnbn false--->" + referenceCode);
			}

		} else {
			if (String.valueOf(codeReturn).equals("1875285")) {
				code = "400178";
				message = "The amount to be transferred exceeds the current account balance";		
				success = "false";
			} else if (String.valueOf(codeReturn).equals("400177")) {
				code = "400177";
				message = "The source account has a debit block";		
				success = "false";
			}
			else {
				code = String.valueOf(codeReturn);
				message = anOriginalProcedureRes.getMessage(1).getMessageText();
				success = "false";
			}
			
		}

		// Agregar Header y data 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header y data 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar info 1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, code));
		row.addRowData(2, new ResultSetRowColumnData(false, message));
		data.addRow(row);

		// Agregar info 2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, success));
		data2.addRow(row2);

		// Agregar resulBlock
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		// Agregar Header y data 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		// Agregar resulBlock
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);

		if (referenceCode != null) {

			metaData3.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SQLINTN, 5));

			// Agregar info 3
			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false,
					anOriginalProcedureRes.readValueParam("@o_referencia").toString().trim()));
			data3.addRow(row3);
		}

		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);

		return anOriginalProcedureResponse;
	}

	@Override
	protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeTransfer");
		}
		IProcedureResponse responseTransfer = null;
		String idTransaccion = "";
		String idMovement = "";
		String refBranch = "";
		try {
			IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			
			if (originalRequest != null) {
				logger.logDebug("Inicia originalRequest no es null");
				
				 if (originalRequest.readValueParam(S_USER) != null) {
					 logger.logDebug(S_USER + " no es null");
				 }
				 
				 if (originalRequest.readValueParam(S_TERM) != null) {
					 logger.logDebug(S_TERM + " no es null");
				 }
				 
				if (originalRequest.readValueParam(S_ROL) != null) {
					logger.logDebug(S_ROL + " no es null");				 
				}
				
				if (originalRequest.readValueParam(S_SRV) != null) {
					logger.logDebug(S_SRV + " no es null");
				}
				
				if (originalRequest.readValueParam(S_DATE_LOCAL) != null) {
					logger.logDebug(S_DATE_LOCAL + " no es null");
				}
				
				if (originalRequest.readValueParam(S_OFI) != null) {
					logger.logDebug(S_OFI + " no es null");
				}
				
				if (originalRequest.readValueParam(S_SRV) != null) {
					logger.logDebug(S_SRV + " no es null");
				}
				
				if (originalRequest.readValueParam(T_EJEC) != null) {
					logger.logDebug(T_EJEC + " no es null");
				}
				
				if (originalRequest.readValueParam(T_RTY) != null) {
					logger.logDebug(T_RTY + " no es null");
				}
			}
			
			ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
			IProcedureRequest originalRequestClone = originalRequest.clone();
			// SE EJECUTA LA NOTA DE DEBITO CENTRAL

			logger.logDebug("Aplicando Transacción " + idTransaccion);

			if (aBagSPJavaOrchestration.containsKey("origin_spei")
					&& aBagSPJavaOrchestration.get("origin_spei") != null) {
				logger.logDebug("On Origin Spei ");
				String appliedOrigin = aBagSPJavaOrchestration.get("origin_spei").toString();
				logger.logDebug("On Origin Spei do " + appliedOrigin);
				if (appliedOrigin.equals("MASSIVE")) {
					logger.logDebug("On massive function");
					idTransaccion = "040";
				}
			}

			responseTransfer = this.executeTransferSPI(originalRequestClone, aBagSPJavaOrchestration);

			if (!(idTransaccion != null && idTransaccion.equals("040"))) {
				logger.logDebug("Normal transacction");

				if (responseTransfer.readValueParam("@o_referencia") != null
						&& responseTransfer.readValueParam("@o_referencia") != null)
					idMovement = responseTransfer.readValueParam("@o_referencia");

				if (responseTransfer.readValueParam("@o_ref_branch") != null
						&& responseTransfer.readValueParam("@o_ref_branch") != null) {
					refBranch = responseTransfer.readValueParam("@o_ref_branch");
				}

				if (logger.isDebugEnabled()) {
					logger.logDebug("ref_branch" + refBranch);
				}

				responseTransfer = transformToProcedureResponse(responseTransfer, aBagSPJavaOrchestration,
						idTransaccion);
			} else {
				logger.logDebug("On massive transacction");
				idMovement = aBagSPJavaOrchestration.get("ssn_operation").toString();
				;
			}

			originalRequestClone.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, refBranch);
			aBagSPJavaOrchestration.put("@i_ssn_branch", refBranch);

			// JCOS VALIDACION PARA FL
			if (serverResponse.getOnLine()) {

				IProcedureResponse tran = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
				idTransaccion = idMovement;

				if (logger.isDebugEnabled()) {
					logger.logDebug(":::: Referencia XDX " + idTransaccion);
				}

				if (logger.isDebugEnabled()) {
					logger.logDebug(":::: Se aplicara transaccion reetry o on line SPEI ");
				}

				if ((originalRequestClone.readValueParam("@i_type_reentry") == null
						|| !originalRequestClone.readValueParam("@i_type_reentry").equals(TYPE_REENTRY_OFF))) {// VALIDACION
																												// DE
																												// REENTRY

					if (idTransaccion != null && !"".equals(idTransaccion)) {
						if (logger.isDebugEnabled()) {
							logger.logDebug(":::: Ahorros OK Transfer Banpay " + idTransaccion);
						}

						aBagSPJavaOrchestration.put("APPLY_DATE", originalRequestClone.readValueParam("@o_fecha_tran"));

						int transacctionApplied = Integer.parseInt(idTransaccion.trim());
						if (transacctionApplied > 0) {

							originalRequestClone.addInputParam("@i_transaccion_spei", ICTSTypes.SQLVARCHAR,
									String.valueOf(transacctionApplied));
							aBagSPJavaOrchestration.put("@i_transaccion_spei", String.valueOf(transacctionApplied));

							if (logger.isDebugEnabled()) {
								logger.logDebug("Spei Armed");
							}
							SpeiMappingRequest requestSpei = mappingBagToSpeiRequest(aBagSPJavaOrchestration,
									responseTransfer, originalRequestClone);

							if (logger.isDebugEnabled()) {
								logger.logDebug("Spei do it");
							}
							SpeiMappingResponse responseSpei = speiOrchestration.sendSpei(requestSpei);

							responseTransfer = mappingResponseSpeiToProcedure(responseSpei, responseTransfer,
									aBagSPJavaOrchestration);
						} else
							logger.logDebug(":::: No Aplica Transaccion no valida " + idTransaccion);
					} else {

						if (logger.isDebugEnabled()) {
							logger.logDebug(":::: No Aplica Transaccion Cancel jcos " + idTransaccion);
						}
					}
				}

			} else if (originalRequestClone.readValueParam("@i_type_reentry") == null && !serverResponse.getOnLine()) {

				if (logger.isDebugEnabled()) {
					logger.logDebug("Se envia a reentry por fuera de linea JCOS");
				}
				// si el saldo disponible le alcanza se aplica transaccion con el proveedor JCOS
				// TODO
				IProcedureResponse validationData = (IProcedureResponse) aBagSPJavaOrchestration
						.get(RESPONSE_LOCAL_VALIDATION);

				if (validationData != null) {

					if (validationData != null && originalRequestClone.readValueParam(T_RTY).equals("N")
							&& validationData.readValueParam("@o_aplica_tran").equals("S")) {

						if (logger.isDebugEnabled()) {
							logger.logDebug(":::: Se aplicara servicio spei por que tiene saldo en local");
						}

						SpeiMappingRequest requestSpei = mappingBagToSpeiRequest(aBagSPJavaOrchestration,
								responseTransfer, originalRequestClone);

						SpeiMappingResponse responseSpei = speiOrchestration.sendSpeiOffline(requestSpei);

						mappingResponseSpeiToProcedureOffline(responseSpei, responseTransfer, aBagSPJavaOrchestration);

						responseTransfer.addParam("@i_type_reentry", ICTSTypes.SQLVARCHAR, 1, TYPE_REENTRY_OFF_SPI);
					}
				} else {

					if (logger.isDebugEnabled()) {
						logger.logDebug("DATA VALIDATE IS NULL!!!");
					}
				}

			}

		} catch (CTSServiceException e) {

			logger.logError(e);
		} catch (CTSInfrastructureException e) {
			logger.logError(e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Fin executeTransfer");
			}

		}

		if (idTransaccion != null && idTransaccion != "") {

			if (logger.isDebugEnabled()) {
				logger.logDebug("Almacenadox !!! " + idTransaccion);
			}
			responseTransfer.addParam("@o_idTransaccion", ICTSTypes.SQLVARCHAR, idTransaccion.length(), idTransaccion);
		}

		return responseTransfer;
	}

	public IProcedureResponse executeTransferSPI(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeTransferSPI");
		}
		IProcedureResponse response = new ProcedureResponseAS();
		IProcedureResponse responseBank = executeCoreBanking(this.getRequestBank(anOriginalRequest));
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Bank --> " + responseBank.getProcedureResponseAsString());
		}

		response.setReturnCode(responseBank.getReturnCode());
		if (responseBank.getReturnCode() != 0) {
			response = Utils.returnException(Utils.returnArrayMessage(responseBank));

		}

		if (responseBank.getReturnCode() == 0 && responseBank.getResultSetListSize() > 0) {

			IResultSetRow[] rows = responseBank.getResultSet(responseBank.getResultSetListSize()).getData()
					.getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
					.get(RESPONSE_LOCAL_VALIDATION);
			IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest, responseLocalValidation);

			requestTransfer.addInputParam("@i_nom_banco_des", ICTSTypes.SYBVARCHAR, columns[0].getValue());
			aBagSPJavaOrchestration.put("@i_banco_dest", columns[0].getValue());
			requestTransfer.addInputParam("@i_ruta_trans", ICTSTypes.SYBVARCHAR, columns[2].getValue());
			requestTransfer.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
			if (aBagSPJavaOrchestration.containsKey("origin_spei") && aBagSPJavaOrchestration.get("origin_spei") != null
					&& aBagSPJavaOrchestration.get("origin_spei").equals("MASSIVE")) {

				logger.logDebug("go to exit Spei Transaction");

				return response;
			}
			response = executeCoreBanking(requestTransfer);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Request accountTransfer: " + anOriginalRequest.getProcedureRequestAsString());
				logger.logDebug("aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response accountTransfer:" + response.getProcedureResponseAsString());
			logger.logDebug("Fin executeTransferSPI");
		}

		return response;
	}
	
	
	
	/**
	 * Permite obtener el request para obtener los datos del banco
	 *
	 * @param anOriginalRequest
	 * @return
	 */
	private IProcedureRequest getRequestBank(IProcedureRequest anOriginalRequest) {
		IProcedureRequest requestBank = new ProcedureRequestAS();

		requestBank.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
		requestBank.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		requestBank.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		requestBank.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		requestBank.setSpName("cob_bvirtual..sp_mant_ifis");
		requestBank.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
		requestBank.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1870009");

		requestBank.addInputParam("@i_cod_ban", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_banco_ben"));
		requestBank.addInputParam("@i_grupo", ICTSTypes.SQLINT4, "1");
		requestBank.addInputParam("@i_tip_tran", ICTSTypes.SQLVARCHAR, "S");
		requestBank.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

		return requestBank;
	}
	
	/**
	 * Método que permite crear un request para ser enviado al Corebanking
	 *
	 * @param anOriginalRequest       Request original
	 * @param lastResponse            Último response recibido.
	 * @param aBagSPJavaOrchestration Objetos que son resultado de la ejecución de
	 *                                los métodos.
	 */
	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest,
			IProcedureResponse responseLocalValidation) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Inicia transfer SPI");
		}

		IProcedureRequest requestTransfer = new ProcedureRequestAS();

		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
		requestTransfer.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		requestTransfer.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		requestTransfer.setSpName("cob_ahorros..sp_tr04_transferencia_ob");
		requestTransfer.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
		requestTransfer.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18340");

		requestTransfer.addInputParam(S_USER, anOriginalRequest.readParam(S_USER).getDataType(),
				anOriginalRequest.readValueParam(S_USER));
		requestTransfer.addInputParam(S_TERM, anOriginalRequest.readParam(S_TERM).getDataType(),
				anOriginalRequest.readValueParam(S_TERM));
		requestTransfer.addInputParam(S_ROL, anOriginalRequest.readParam(S_ROL).getDataType(),
				anOriginalRequest.readValueParam(S_ROL));
		requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
				anOriginalRequest.readValueParam(S_SRV));
		requestTransfer.addInputParam(S_DATE_LOCAL, anOriginalRequest.readParam(S_DATE_LOCAL).getDataType(),
				anOriginalRequest.readValueParam(S_DATE_LOCAL));
		requestTransfer.addInputParam(S_OFI, anOriginalRequest.readParam(S_OFI).getDataType(),
				anOriginalRequest.readValueParam(S_OFI));
		requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
				anOriginalRequest.readValueParam(S_SRV));
		requestTransfer.addInputParam(T_EJEC, anOriginalRequest.readParam(T_EJEC).getDataType(),
				anOriginalRequest.readValueParam(T_EJEC));
		requestTransfer.addInputParam(T_RTY, anOriginalRequest.readParam(T_RTY).getDataType(),
				anOriginalRequest.readValueParam(T_RTY));

		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isInfoEnabled())
			logger.logInfo("PRE COMISION --->   RECUPERADA");

		if (anOriginalRequest != null && anOriginalRequest.readValueParam("@i_comision") != null) {

			logger.logInfo("ENTRA VALIDACION COMISION");

			if (logger.isInfoEnabled())
				logger.logInfo(
						"Llegada de comisiom 3.1416 SPEIDO ---> " + anOriginalRequest.readValueParam("@i_comision"));

			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY,
					anOriginalRequest.readValueParam("@i_comision"));
		} else {
			logger.logInfo("NO ENTRA VALIDACION COMISION > 0");
			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		}

		// jcos recuperacion de SSN TRANSACCIONAL
		requestTransfer.addOutputParam("@o_referencia", ICTSTypes.SYBINT4, "0");
		requestTransfer.addOutputParam("@o_ref_branch", ICTSTypes.SYBINT4, "0");

		if ("1".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "8".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "10".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {

			logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 1,8,10");
			// CUENTA ORIGEN
			requestTransfer.addInputParam(I_CTA_LOCAL, anOriginalRequest.readParam(I_CTA_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_LOCAL));
			requestTransfer.addInputParam(I_PROD_LOCAL, anOriginalRequest.readParam(I_PROD_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_PROD_LOCAL));
			requestTransfer.addInputParam(I_MON_LOCAL, anOriginalRequest.readParam(I_MON_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_MON_LOCAL));

			// CUENTA DESTINO
			requestTransfer.addInputParam(I_CTA_DES_LOCAL, anOriginalRequest.readParam(I_CTA_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_DES_LOCAL));

			if (anOriginalRequest.readValueParam(I_PROD_DES_LOCAL) != null) {
				logger.logInfo("ENTRA VALIDACION I_PROD_DES_LOCAL");
				requestTransfer.addInputParam(I_PROD_DES_LOCAL,
						anOriginalRequest.readParam(I_PROD_DES_LOCAL).getDataType(),
						anOriginalRequest.readValueParam(I_PROD_DES_LOCAL));
			}

			requestTransfer.addInputParam(I_MON_DES_LOCAL, anOriginalRequest.readParam(I_MON_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_MON_DES_LOCAL));

			// VALORES DE TRANSACCION
			requestTransfer.addInputParam(I_VAL_LOCAL, anOriginalRequest.readParam(I_VAL_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_VAL_LOCAL));
			requestTransfer.addInputParam(I_CONCEPTO_LOCAL, anOriginalRequest.readParam(I_CONCEPTO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CONCEPTO_LOCAL));
			requestTransfer.addInputParam(I_NOMBRE_BENEF, anOriginalRequest.readParam(I_NOMBRE_BENEF).getDataType(),
					anOriginalRequest.readValueParam(I_NOMBRE_BENEF));
			/*
			 * requestTransfer.addInputParam("@i_ced_ruc_ben",
			 * anOriginalRequest.readParam(I_DOC_BENEF).getDataType(),
			 * anOriginalRequest.readValueParam(I_DOC_BENEF));
			 */
			requestTransfer.addInputParam(I_BANCO_BEN, anOriginalRequest.readParam(I_BANCO_BEN).getDataType(),
					anOriginalRequest.readValueParam(I_BANCO_BEN));
			requestTransfer.addInputParam("@i_servicio", anOriginalRequest.readParam(S_SERVICIO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
		}

		if ("6".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "7".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {
			logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 6,7");
			requestTransfer.addInputParam(I_MON_LOCAL, responseLocalValidation.readParam("@o_mon").getDataType(),
					responseLocalValidation.readValueParam("@o_mon"));
			requestTransfer.addInputParam("@i_prod_org", responseLocalValidation.readParam("@o_prod").getDataType(),
					responseLocalValidation.readValueParam("@o_prod"));
			requestTransfer.addInputParam("@i_cta_org", responseLocalValidation.readParam("@o_cta").getDataType(),
					responseLocalValidation.readValueParam("@o_cta"));
			requestTransfer.addInputParam(I_PROD_DES_LOCAL,
					responseLocalValidation.readParam("@o_prod_des").getDataType(),
					responseLocalValidation.readValueParam("@o_prod_des"));
			requestTransfer.addInputParam(I_CTA_DES_LOCAL,
					responseLocalValidation.readParam("@o_cta_des").getDataType(),
					responseLocalValidation.readValueParam("@o_cta_des"));
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo("Fin transfer SPI");
		}
		return requestTransfer;
	}
	
	/**
	 * Arma la respuesta al servicio
	 *
	 * @param responseTransfer
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private IProcedureResponse transformToProcedureResponse(IProcedureResponse responseTransfer,
			Map<String, Object> aBagSPJavaOrchestration, String idTransaccion) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia transformToProcedureResponse");
		}

		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		response.setReturnCode(responseTransfer.getReturnCode());
		if (serverResponse.getOnLine() && responseTransfer.getReturnCode() != 0) {
			// ONLINE Y HUBO ERROR
			response = Utils.returnException(Utils.returnArrayMessage(responseTransfer));

		} else {
			response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
					String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH)));

			// response.addParam("@o_ref_branch", ICTSTypes.SYBINT4, 0,
			// String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH)));

			response.setReturnCode(responseTransfer.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

			idTransaccion = String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH));

			logger.logDebug(CLASS_NAME + "Respuesta TRANSACCION ID --> " + idTransaccion);
		}

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ response.getProcedureResponseAsString());
			logger.logDebug("Fin transformToProcedureResponse");
		}
		return response;
	}
	
	private IProcedureResponse mappingResponseSpeiToProcedure(SpeiMappingResponse response,
			IProcedureResponse responseTransfer, Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = "[SPITransferOrchestrationCore][mappingResponseSpeiToProcedure] ";
		logger.logInfo(wInfo + Constants.INIT_TASK);
		logger.logInfo(wInfo + "response de entrada spei: " + response.toString());

		if (response.getErrorCode() != null) {
			return Utils.returnException(1, ERROR_SPEI);
		}

		logger.logInfo(wInfo + Constants.END_TASK);

		return putSpeiResponseOnBag(response, responseTransfer, aBagSPJavaOrchestration);
	}

	private IProcedureResponse mappingResponseSpeiToProcedureOffline(SpeiMappingResponse response,
			IProcedureResponse responseTransfer, Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = "[SPITransferOrchestrationCore][mappingResponseSpeiToProcedure] ";
		logger.logInfo(wInfo + Constants.INIT_TASK);
		logger.logInfo(wInfo + "response de entrada: " + response.toString());

		aBagSPJavaOrchestration.put("@i_transaccion_spei", response.getCodigoAcc());

		if (response.getErrorCode() != null) {
			responseTransfer.addParam(Constants.I_FAIL_PROVIDER, ICTSTypes.SQLVARCHAR, 1, "S");
			return responseTransfer;
		}

		logger.logInfo(wInfo + Constants.END_TASK);

		return putSpeiResponseOnBag(response, responseTransfer, aBagSPJavaOrchestration);

	}

	private IProcedureResponse putSpeiResponseOnBag(SpeiMappingResponse response, IProcedureResponse responseTransfer,
			Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = "[SPITransferOrchestrationCore][putSpeiResponseOnBag] ";
		logger.logInfo(wInfo + "init task ---->");
		logger.logInfo(wInfo + "response de entrada: " + response.toString());

		responseTransfer.addParam(Constants.O_CLAVE_RASTREO, ICTSTypes.SQLVARCHAR, response.getClaveRastreo().length(),
				response.getClaveRastreo());

		aBagSPJavaOrchestration.put(Constants.I_CLAVE_RASTREO, response.getClaveRastreo());
		aBagSPJavaOrchestration.put(Constants.I_MENSAJE_ACC, response.getMensajeAcc());
		aBagSPJavaOrchestration.put(Constants.I_ID_SPEI_ACC, response.getCodigoAcc());
		aBagSPJavaOrchestration.put(Constants.I_CODIGO_ACC, response.getCodigoAcc());

		aBagSPJavaOrchestration.put(Constants.O_SPEI_REQUEST, response.getSpeiRequest());
		aBagSPJavaOrchestration.put(Constants.O_SPEI_RESPONSE, response.getSpeiResponse());

		logger.logInfo(wInfo + "end task ---->");

		return responseTransfer;
	}

	private SpeiMappingRequest mappingBagToSpeiRequest(Map<String, Object> aBagSPJavaOrchestration,
			IProcedureResponse responseTransfer, IProcedureRequest anOriginalRequest) {
		String wInfo = "[SPITransferOrchestrationCore][transformBagToSpeiRequest] ";
		logger.logInfo(wInfo + Constants.INIT_TASK);

		SpeiMappingRequest request = new SpeiMappingRequest();
		request.setConceptoPago(anOriginalRequest.readValueParam(Constants.I_CONCEPTO));
		request.setCuentaOrdenante(anOriginalRequest.readValueParam(Constants.I_CUENTA));
		request.setCuentaClabeBeneficiario(anOriginalRequest.readValueParam(Constants.I_CUENTA_DESTINO));
		request.setNombreBeneficiario(anOriginalRequest.readValueParam(Constants.I_NOMBRE_BENEFICIARIO));
		request.setInstitucionContraparte(anOriginalRequest.readValueParam(Constants.I_BANCO_BENEFICIARIO));
		request.setBancoDestino(aBagSPJavaOrchestration.get(Constants.I_BANCO_DESTINO) != null
				? aBagSPJavaOrchestration.get(Constants.I_BANCO_DESTINO).toString()
				: "");

		BigDecimal monto = new BigDecimal(anOriginalRequest.readValueParam(Constants.I_VALOR));
		request.setMonto(monto.setScale(2, RoundingMode.CEILING));
		request.setRfcCurpBeneficiario("ND");
		request.setTipoCuentaBeneficiario(anOriginalRequest.readValueParam("@i_prod_des"));
		request.setEnteBancaVirtual(anOriginalRequest.readValueParam("@s_cliente"));
		request.setLogin(anOriginalRequest.readValueParam("@i_login"));
		request.setReferenceNumber(anOriginalRequest.readValueParam("@i_reference_number"));
		request.setServicio(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));

//TRANSACCIONALIDAD
		String transaccionSpei = anOriginalRequest.readValueParam("@i_transaccion_spei");
		if (null == transaccionSpei) { // Si esta en offline no hay ssn de debito
			transaccionSpei = anOriginalRequest.readValueParam("@s_ssn"); // se obtiene ssn de CTS
		}

		request.setSsnDebito(transaccionSpei);
		request.setSsnBranchDebito(anOriginalRequest.readValueParam("@s_ssn_branch"));

//CTS VARIABLE
		request.setCtsSsn(anOriginalRequest.readValueParam("@s_ssn"));
		request.setCtsServ(anOriginalRequest.readValueParam("@s_srv"));
		request.setCtsUser(anOriginalRequest.readValueParam("@s_user"));
		request.setCtsTerm(anOriginalRequest.readValueParam("@s_term"));
		request.setCtsRol(anOriginalRequest.readValueParam("@s_rol"));
		request.setCtsDate(anOriginalRequest.readValueParam("@s_date"));

// VARIABLE DE ORIGEN
		logger.logInfo(wInfo + " trn_origen: " + anOriginalRequest.readValueFieldInHeader("trn_origen"));
		request.setTrnOrigen(anOriginalRequest.readValueFieldInHeader("trn_origen"));
		request.setUser(anOriginalRequest.readValueFieldInHeader("user"));
		request.setOffice(anOriginalRequest.readValueFieldInHeader("ofi"));
		request.setServer(anOriginalRequest.readValueFieldInHeader("srv"));
		request.setTerminal(anOriginalRequest.readValueFieldInHeader("term"));

		logger.logInfo(wInfo + Constants.END_TASK);

		return request;

	}

	@Override
	public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
		return null;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate
	 * #transformNotificationRequest(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest,
	 * com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse,
	 * java.util.Map)
	 */
	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product product = new Product();
		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (product.getProductType() == 3)
			notification.setId("N90");
		else
			notification.setId("N91");

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta_des")))
			notificationDetail.setAccountNumberCredit(anOriginalRequest.readValueParam("@i_cta_des"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon"))) {
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));
			notificationDetail.setCurrencyId2(anOriginalRequest.readValueParam("@i_mon"));
		}

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam(S_SSN_BRANCH)))
			notificationDetail.setReference(anOriginalRequest.readValueParam(S_SSN_BRANCH));
		if (!Utils.isNull(anOriginalRequest.readParam(I_NOMBRE_BENEF)))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam(I_NOMBRE_BENEF));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		return notificationRequest;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}
}
