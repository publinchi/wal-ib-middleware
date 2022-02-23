package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferInOfflineTemplate;
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
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * Plugin of between accounts transfers
 *
 * @since Dec 05, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */
@Component(name = "SpeiInTransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SpeiInTransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SpeiInTransferOrchestrationCore") })
public class SpeiInTransferOrchestrationCore extends TransferInOfflineTemplate {

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(SpeiInTransferOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}


	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
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
		IProcedureResponse response = null;
		IProcedureRequest originalByNotify = anOriginalRequest;

		if (logger.isInfoEnabled())
			logger.logInfo("SpeiInTransferOrchestrationCore: executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceSelfAccountTransfers", coreServiceSelfAccountTransfers);

		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPEI IN");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
		try {
			response = executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		if (response != null && !response.hasError() && response.getReturnCode() == 0) {
			String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
			if(null == idDevolucion || "0".equals(idDevolucion)){
				notifySpei(anOriginalRequest, aBagSPJavaOrchestration);
			}
		}

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	public IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {// throws
																							// CTSServiceException,
																							// CTSInfrastructureException
		IProcedureResponse response = null;
		try {
			IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			response = mappingResponse(executeTransferSpeiIn(anOriginalRequest, aBagSPJavaOrchestration), aBagSPJavaOrchestration);
		} catch (Exception e){
			logger.logError("AN ERROR OCURRED: ", e);
		}

		return response;
	}

	private IProcedureResponse mappingResponse(IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration){
		String wInfo = CLASS_NAME+"[mappingResponse] ";
		logger.logInfo(wInfo + INIT_TASK);

		if (aResponse != null && aResponse.getMessageListSize() != 0 && aResponse.readValueParam("@o_descripcion_error") != null) {
			aBagSPJavaOrchestration.put("@s_error", String.valueOf(aResponse.getReturnCode()));
			String causaDevolucion = aResponse.readValueParam("@o_id_causa_devolucion");

			if (null != causaDevolucion && !"0".equals(causaDevolucion)) {
				return Util.returnCorrectResponse(aResponse);
			}

			return  Util.returnException(aResponse.getReturnCode(), aResponse.readValueFieldInHeader("messageError").split(":")[1]);

		}

		aResponse.addParam("@o_resultado", ICTSTypes.SQLINT4, 50, String.valueOf(aResponse.getReturnCode()));
		aResponse.addParam("@o_folio", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_id_interno"));
		aResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_descripcion"));
		aResponse.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_id_causa_devolucion"));
		aBagSPJavaOrchestration.put("@s_error", aResponse.getReturnCode());

		return aResponse;
	}

	private IProcedureResponse executeTransferSpeiIn(IProcedureRequest anOriginalRequest,
													 Map<String, Object> aBagSPJavaOrchestration){
		String wInfo = CLASS_NAME+"[executeTransferSpeiIn] ";
		logger.logInfo(wInfo+INIT_TASK);
		IProcedureResponse response = new ProcedureResponseAS();

		IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo+ "Request accountTransfer: " + requestTransfer.getProcedureRequestAsString());
		}

		response = executeCoreBanking(requestTransfer);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo+ "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
			logger.logDebug(wInfo+ "response de central: " + response);
		}

		logger.logInfo(wInfo+END_TASK);

		return response;


	}

	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest) {
		String wInfo = CLASS_NAME+"[getRequestTransfer] ";
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		procedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_val", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "249");
		procedureRequest.addInputParam("@i_causa_comi", ICTSTypes.SYBVARCHAR, "250");
		procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
		procedureRequest.addInputParam("@i_fecha", ICTSTypes.SYBDATETIME, anOriginalRequest.readValueParam("@i_fechaOperacion"));
		procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "9");

		anOriginalRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		anOriginalRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		anOriginalRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
		anOriginalRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_monto"));
		anOriginalRequest.addInputParam("@i_institucion_ordenante", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionOrdenante"));
		anOriginalRequest.addInputParam("@i_institucion_beneficiaria", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
		anOriginalRequest.addInputParam("@i_id_spei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_idSpei"));
		anOriginalRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
		anOriginalRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
		anOriginalRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_rfcCurpOrdenante"));
		anOriginalRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
		anOriginalRequest.addInputParam("@i_tipo", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_idTipoPago"));

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_institucion_ordenante", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionOrdenante"));
		procedureRequest.addInputParam("@i_institucion_beneficiaria", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
		procedureRequest.addInputParam("@i_id_spei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_idSpei"));
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
		procedureRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
		procedureRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_rfcCurpOrdenante"));
		procedureRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
		procedureRequest.addInputParam("@i_tipo", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_idTipoPago"));
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuenta_cobis"));
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "I");
		procedureRequest.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_string_request"));
		procedureRequest.addInputParam("@i_tipo_ejecucion", ICTSTypes.SQLVARCHAR, isReentryExecution ? "F" : "L");

		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");


		logger.logInfo(wInfo + END_TASK);

		return procedureRequest;
	}

	private void notifySpei (IProcedureRequest anOriginalRequest, java.util.Map map) {

		try {
			ServerResponse serverResponse = (ServerResponse) map.get(RESPONSE_SERVER);

			//Por definicion funcional no se notifica en modo offline
			if(Boolean.FALSE.equals(serverResponse.getOnLine())){
				return;
			}

			logger.logInfo(CLASS_NAME + "Enviando notificacion spei");

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			String cuentaClave=anOriginalRequest.readValueParam("@i_cuenta_beneficiario");

			logger.logInfo(CLASS_NAME + "using clabe account account "+cuentaClave);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S',"local");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1800195");

			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			// procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N145");
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "spei");
			procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaClave);
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
			procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, String.valueOf(  anOriginalRequest.readValueParam("@i_monto")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
			procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
			procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));


			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

			logger.logInfo("jcos proceso de notificaciom terminado");

		}catch(Exception xe) {
			logger.logError("Error en la notficación de spei recibida", xe);
		}
	}

	private void logDebug(Object aMessage){
		if(logger.isDebugEnabled()){
			logger.logDebug(aMessage);
		}
	}

	@Override
	public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

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
			notification.setId("N19");
		else
			notification.setId("N20");

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
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_nom_cliente_benef")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_nom_cliente_benef"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);



		return notificationRequest;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}

}

