package com.cobiscorp.ecobis.orchestration.core.ib.direct.transfer;

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
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
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

//import com.cobiscorp.ecobis.orchestration7x24.commons.utils.ProcedureUtils;

@Component(name = "DirectTransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DirectTransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DirectTransferOrchestrationCore") })
public class DirectTransferOrchestrationCore extends TransferOfflineTemplate {

	private static final String S_SSN_BRANCH = "@s_ssn_branch";
	private static final String S_SSN = "@s_ssn";
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
	private static final String I_BANCO_BEN = "@i_banco";
	private static final String I_DOC_BENEF = "@i_doc_benef";
	private static final String I_CONCEPTO_LOCAL = "@i_concepto";
	private static final String I_VAL_LOCAL = "@i_val";
	private static final String I_MON_LOCAL = "@i_mon";
	private static final String I_CTA_LOCAL = "@i_cta";
	private static final String S_SERVICIO_LOCAL = "@s_servicio";
	private static final String I_PROD_LOCAL = "@i_prod";
	private static final String I_OPERACION = "@i_operacion";
	private static final String W_REGISTER_ACCOUNT_INQUIRY_RESP = "wRegisterAccountInquiryResp";
	private static final String W_VALIDATE_ACCOUNT_WS_RESP = "wValidateAccountWSResp";
	private static final String W_UPDATE_ACCOUNT_INQUIRY_RESP = "wUpdateAccountInquiryResp";

	/** Instancia del Logger */
	private static ILogger logger = LogFactory.getLogger(DirectTransferOrchestrationCore.class);

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

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logInfo("Inicia executeJavaOrchestration");
		}

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA PAGO DIRECTO");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
		boolean wValidateAccountWS;
		boolean wUpdateAccountInquiry;

		wValidateAccountWS = invokeDirectPaymentWS(aBagSPJavaOrchestration);
		if (wValidateAccountWS) {
			if (logger.isDebugEnabled())
				logger.logDebug("VALIDATE ACCOUNT WS. Continue flow.");
		} else {
			if (logger.isDebugEnabled())
				logger.logDebug("VALIDATE ACCOUNT WS without errors.");
		}
		/*
		 * Compara los datos del beneficiario wUpdateAccountInquiry =
		 * updateAccountInquiry(aBagSPJavaOrchestration); if
		 * (wUpdateAccountInquiry) {
		 * logger.logDebug("UPDATE ACCOUNT INQUIRY failed. Ending flow.");
		 * return processResponse(anOriginalRequest, aBagSPJavaOrchestration); }
		 * else { logger.logDebug("UPDATE ACCOUNT INQUIRY without errors.");
		 * return processResponse(anOriginalRequest, aBagSPJavaOrchestration); }
		 */

		try {
			executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			logger.logError(e);
		} catch (CTSInfrastructureException e) {
			logger.logError(e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logInfo("Fin executeJavaOrchestration");
			}
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeTransfer");
		}

		try {
			IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			boolean wInvokeDirectPaymentWS = invokeDirectPaymentWS(aBagSPJavaOrchestration);
			if (wInvokeDirectPaymentWS == true) {
				IProcedureResponse responseTransfer = this.executeDirectTransfer(originalRequest,
						aBagSPJavaOrchestration);
				return transformToProcedureResponse(responseTransfer, aBagSPJavaOrchestration);
			} else {
				return transformToServiceResponse(wInvokeDirectPaymentWS, aBagSPJavaOrchestration);
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
		return null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg) {
		if (logger.isDebugEnabled())
			logger.logInfo("LOAD CONFIGUATION");
	}

	public IProcedureResponse executeDirectTransfer(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeDirectTransfer");
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

			requestTransfer.addInputParam("@i_banco_des", ICTSTypes.SYBVARCHAR, columns[0].getValue());
			requestTransfer.addInputParam("@i_ruta_trans", ICTSTypes.SYBVARCHAR, columns[2].getValue());

			response = executeCoreBanking(requestTransfer);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Request accountTransfer: " + anOriginalRequest.getProcedureRequestAsString());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response accountTransfer:" + response.getProcedureResponseAsString());
			logger.logDebug("Fin executeDirectTransfer");
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

		requestBank.addInputParam("@i_cod_ban", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam(I_BANCO_BEN));
		requestBank.addInputParam("@i_grupo", ICTSTypes.SQLINT4, "1");
		requestBank.addInputParam("@i_tip_tran", ICTSTypes.SQLVARCHAR, "P");
		requestBank.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

		return requestBank;
	}

	/**
	 * Método que permite crear un request para ser enviado al Corebanking
	 * 
	 * @param anOriginalRequest
	 *            Request original
	 * @param lastResponse
	 *            Último response recibido.
	 * @param aBagSPJavaOrchestration
	 *            Objetos que son resultado de la ejecución de los métodos.
	 */
	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest,
			IProcedureResponse responseLocalValidation) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Inicia getRequestTransfer");
		}

		IProcedureRequest requestTransfer = new ProcedureRequestAS();

		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870001");
		requestTransfer.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		requestTransfer.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		requestTransfer.setSpName("cobis..sp_pago_directo");
		requestTransfer.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
		requestTransfer.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1870001");
		requestTransfer.addInputParam(I_OPERACION, ICTSTypes.SQLCHAR, "I");

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

		if (responseLocalValidation.readParam("@o_comision") == null) {
			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		} else {
			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY,
					responseLocalValidation.readValueParam("@o_comision"));
		}

		if ("1".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "8".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {

			// CUENTA ORIGEN
			requestTransfer.addInputParam("@i_ente", anOriginalRequest.readParam("@i_ente").getDataType(),
					anOriginalRequest.readValueParam("@i_ente"));
			requestTransfer.addInputParam(I_CTA_LOCAL, anOriginalRequest.readParam(I_CTA_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_LOCAL));
			requestTransfer.addInputParam(I_PROD_LOCAL, anOriginalRequest.readParam(I_PROD_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_PROD_LOCAL));
			requestTransfer.addInputParam(I_MON_LOCAL, anOriginalRequest.readParam(I_MON_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_MON_LOCAL));

			// CUENTA DESTINO
			requestTransfer.addInputParam(I_CTA_DES_LOCAL, anOriginalRequest.readParam(I_CTA_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_DES_LOCAL));
			requestTransfer.addInputParam(I_PROD_DES_LOCAL, anOriginalRequest.readParam(I_PROD_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_PROD_DES_LOCAL));
			if (anOriginalRequest.readValueParam(I_MON_DES_LOCAL) != null) {
				requestTransfer.addInputParam(I_MON_DES_LOCAL,
						anOriginalRequest.readParam(I_MON_DES_LOCAL).getDataType(),
						anOriginalRequest.readValueParam(I_MON_DES_LOCAL));
			}

			// VALORES DE TRANSACCION
			requestTransfer.addInputParam(I_VAL_LOCAL, anOriginalRequest.readParam(I_VAL_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_VAL_LOCAL));
			requestTransfer.addInputParam(I_CONCEPTO_LOCAL, anOriginalRequest.readParam(I_CONCEPTO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CONCEPTO_LOCAL));
			requestTransfer.addInputParam(I_NOMBRE_BENEF, anOriginalRequest.readParam(I_NOMBRE_BENEF).getDataType(),
					anOriginalRequest.readValueParam(I_NOMBRE_BENEF));
			requestTransfer.addInputParam(I_DOC_BENEF, anOriginalRequest.readParam(I_DOC_BENEF).getDataType(),
					anOriginalRequest.readValueParam(I_DOC_BENEF));
			requestTransfer.addInputParam(I_BANCO_BEN, anOriginalRequest.readParam(I_BANCO_BEN).getDataType(),
					anOriginalRequest.readValueParam(I_BANCO_BEN));
			requestTransfer.addInputParam("@i_canal", anOriginalRequest.readParam(S_SERVICIO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
			requestTransfer.addInputParam("@i_trans_codigo",
					anOriginalRequest.readParam("@i_trans_codigo").getDataType(),
					anOriginalRequest.readValueParam("@i_trans_codigo"));
			requestTransfer.addInputParam("@i_serv_codigo", anOriginalRequest.readParam("@i_serv_codigo").getDataType(),
					anOriginalRequest.readValueParam("@i_serv_codigo"));
			if (anOriginalRequest.readValueParam("@i_email") != null) {
				requestTransfer.addInputParam("@i_email", anOriginalRequest.readParam("@i_email").getDataType(),
						anOriginalRequest.readValueParam("@i_email"));
			}
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo("Fin getRequestTransfer");
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
			Map<String, Object> aBagSPJavaOrchestration) {
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
					String.valueOf(originalRequest.readValueParam(S_SSN)));
			response.setReturnCode(responseTransfer.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		}

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ response.getProcedureResponseAsString());
			logger.logDebug("Fin transformToProcedureResponse");
		}
		return response;
	}

	/**
	 * Arma la respuesta al servicio
	 * 
	 * @param responseTransfer
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private IProcedureResponse transformToServiceResponse(boolean responseTransfer,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia transformToServiceResponse");
		}

		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		response.setReturnCode(responseTransfer == true ? 0 : 1);
		if (serverResponse.getOnLine() && responseTransfer != true) {
			// ONLINE Y HUBO ERROR
			response = Utils.returnException("El servicio no devuelve datos");

		} else {
			response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
					String.valueOf(originalRequest.readValueParam(S_SSN)));
			response.setReturnCode(responseTransfer == true ? 0 : 1);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		}

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					CLASS_NAME + "Respuesta transformToServiceResponse -->" + response.getProcedureResponseAsString());
			logger.logDebug("Fin transformToServiceResponse");
		}
		return response;
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
			notification.setId("N92");
		else
			notification.setId("N93");

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
		if (!Utils.isNull(anOriginalRequest.readParam(S_SSN)))
			notificationDetail.setReference(anOriginalRequest.readValueParam(S_SSN));
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

	private Boolean invokeDirectPaymentWS(java.util.Map<String, Object> aBagSPJavaOrchestration) {
		// Aqui se debe incluir la llamada al WS para
		return true;
	}

	private Boolean updateAccountInquiry(java.util.Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("---updateAccountInquiry beginning");
		IProcedureRequest wUpdateAccountInquiryRequest = ((IProcedureRequest) (aBagSPJavaOrchestration
				.get("anOriginalRequest")));
		IProcedureRequest wUpdateAccountInquiryTMP = (initProcedureRequest(wUpdateAccountInquiryRequest));

		wUpdateAccountInquiryTMP.setSpName("cob_bvirtual..sp_cons_cta_beneficiario");

		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		IProcedureResponse wRegisterAccountInquiryResp = (IProcedureResponse) aBagSPJavaOrchestration
				.get(W_REGISTER_ACCOUNT_INQUIRY_RESP);
		IProcedureResponse wValidateAccountWSResp = (IProcedureResponse) aBagSPJavaOrchestration
				.get(W_VALIDATE_ACCOUNT_WS_RESP);

		wUpdateAccountInquiryTMP.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870006");

		// Setting DB as in orchestration7x24-loanPaymet from Pablo Villamar
		wUpdateAccountInquiryTMP.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		wUpdateAccountInquiryTMP.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		Utils.copyParam("@i_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_tipo_cuenta_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_cuenta_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_monto", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_fecha", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_filial", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_canal", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_oficina", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_tipo_cuenta_benef", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_cuenta_benef", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_banco", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_beneficiario", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_nombre_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_identificacion_benef", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_concepto", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		// Comes from FRONT END and it is used for flow selection
		Utils.copyParam("@i_trans_codigo", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_serv_codigo", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);

		wUpdateAccountInquiryTMP.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "U");
		if (logger.isDebugEnabled())
			logger.logDebug("wValidateAccountWSResp.readValueParam(\"@o_sec_host\") = "
					+ wValidateAccountWSResp.readValueParam("@o_sec_host"));
		// Sent in UPDATE ACCOUNT INQUIRY case
		wUpdateAccountInquiryTMP.addInputParam("@i_sec_host", ICTSTypes.SYBINTN,
				wValidateAccountWSResp.readValueParam("@o_sec_host"));
		if (logger.isDebugEnabled())
			logger.logDebug("wValidateAccountWSResp.readValueParam(\"@o_response_code\") = "
					+ wValidateAccountWSResp.readValueParam("@o_response_code"));
		// Sent in UPDATE ACCOUNT INQUIRY case
		wUpdateAccountInquiryTMP.addInputParam("@i_resp_host", ICTSTypes.SYBINTN,
				wValidateAccountWSResp.readValueParam("@o_response_code"));
		if (logger.isDebugEnabled())
			logger.logDebug("wRegisterAccountInquiryResp.readValueParam(\"@o_id_pago\") = "
					+ wRegisterAccountInquiryResp.readValueParam("@o_id_pago"));

		wUpdateAccountInquiryTMP.addInputParam("@i_id_pago", ICTSTypes.SYBINTN,
				wRegisterAccountInquiryResp.readValueParam("@o_id_pago"));

		Utils.copyParam("@i_moneda", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);

		Utils.copyParam("@i_contacto_beneficiario", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_nombre_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);
		Utils.copyParam("@i_identificacion_cliente", wUpdateAccountInquiryRequest, wUpdateAccountInquiryTMP);

		responseTransaction.readValueParam("@o_respuesta");

		IProcedureResponse wUpdateAccountInquiryResp = executeCoreBanking(wUpdateAccountInquiryTMP);
		aBagSPJavaOrchestration.put(W_UPDATE_ACCOUNT_INQUIRY_RESP, wUpdateAccountInquiryResp);
		return wUpdateAccountInquiryResp.hasError();
	}
}
