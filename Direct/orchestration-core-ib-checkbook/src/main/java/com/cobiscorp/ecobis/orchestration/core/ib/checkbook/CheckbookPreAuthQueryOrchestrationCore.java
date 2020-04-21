package com.cobiscorp.ecobis.orchestration.core.ib.checkbook;

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
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CheckBookPreAuth;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * 
 * @author areinoso
 *
 */
@Component(name = "CheckbookPreAuthQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CheckbookPreAuthQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookPreAuthQueryOrchestrationCore") })
public class CheckbookPreAuthQueryOrchestrationCore extends ApplicationsBaseTemplate {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCheckbook", unbind = "unbindCoreServiceCheckbook")
	protected ICoreServiceCheckbook coreServiceCheckBook;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	protected ICoreServiceSendNotification coreServiceSendNotification;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceCheckBook**********" + service);
		coreServiceCheckBook = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceCheckBook**********" + service);
		coreServiceCheckBook = null;
	}

	/**
	 * Instance ServiceNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceNotification service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceNotification**********" + service);
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceNotification service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceNotification**********" + service);
		coreServiceNotification = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreService**********" + service);
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreService**********" + service);
		coreService = null;
	}

	/**
	 * Instance CoreServer Interface
	 * 
	 * @param service
	 */
	public void bindCoreServer(ICoreServer service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServer**********" + service);
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServer**********" + service);
		coreServer = null;
	}

	/**
	 * Instance ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	/**
	 * Deleting ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance ServiceSendNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = service;
	}

	/**
	 * Deleting ServiceSendNotification Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected ICoreService getCoreService() {
		// TODO Auto-generated method stub
		return coreService;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return coreServiceNotification;
	}

	@Override
	protected IProcedureResponse executeApplication(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		CheckBookPreAuthResponse aCheckBookPreAuthResponse = null;
		CheckBookPreAuthRequest aCheckBookPreAuthRequest = transformManagerCheckRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aCheckBookPreAuthRequest.getAccount());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeApplication");
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "aplicationManagerCheck: " + aCheckBookPreAuthRequest.getAccount();
			queryName = "aplicationManagerCheck";
			IProcedureResponse aProcedureResponse = (IProcedureResponse) aBagSPJavaOrchestration
					.get(ApplicationsBaseTemplate.RESPONSE_VALIDATE_LOCAL);
			aCheckBookPreAuthRequest.setOriginalRequest(request);
			aCheckBookPreAuthResponse = coreServiceCheckBook.getCheckBookPreAuth(aCheckBookPreAuthRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		// aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		return transformProcedureResponse(aCheckBookPreAuthResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceCashiersCheck", coreServiceCheckBook);
		Utils.validateComponentInstance(mapInterfaces);
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO> anOrginalRequest" + anOrginalRequest);
			executeStepsApplicationBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	@Override
	public IProcedureResponse sendNotification(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		IProcedureResponse pr = new ProcedureResponseAS();
		pr.setReturnCode(0);
		return pr;
	}

	private CheckBookPreAuthRequest transformManagerCheckRequest(IProcedureRequest aRequest) {

		CheckBookPreAuthRequest aCheckBookPreAuthRequest = new CheckBookPreAuthRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		// messageError += aRequest.readValueParam("@i_login") == null ? " -
		// @i_login can't be null":"";
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@i_cheque") == null ? " - @i_cheque can't be null" : "";
		messageError += aRequest.readValueParam("@i_valor") == null ? " - @i_valor can't be null" : "";
		// messageError += aRequest.readValueParam("@i_operacion") == null ? " -
		// @i_lote can't be null":"";
		messageError += aRequest.readValueParam("@i_beneficiario") == null ? " - @i_beneficiario can't be null" : "";

		// messageError += aRequest.readValueParam("@i_prod") == null ? " -
		// @i_prod can't be null":"";
		// messageError += aRequest.readValueParam("@i_num_autoriza") == null ?
		// " - @i_num_autoriza can't be null":"";
		// messageError += aRequest.readValueParam("@i_lote") == null ? " -
		// @i_lote can't be null":"";
		// messageError += aRequest.readValueParam("@i_ente") == null ? " -
		// @i_ente can't be null":"";
		// messageError += aRequest.readValueParam("@i_doble_autorizacion") ==
		// null ? " - @i_doble_autorizacion can't be null":"";
		// messageError += aRequest.readValueParam("@i_monto") == null ? " -
		// @i_monto can't be null":"";

		if (!messageError.equals(""))

			throw new IllegalArgumentException(messageError);

		CheckBookPreAuth wManagerCkeck = new CheckBookPreAuth();

		aCheckBookPreAuthRequest.setAccount(aRequest.readValueParam("@i_cta"));
		aCheckBookPreAuthRequest.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		aCheckBookPreAuthRequest.setCheckId(Integer.parseInt(aRequest.readValueParam("@i_cheque")));
		aCheckBookPreAuthRequest.setAmount(Double.parseDouble(aRequest.readValueParam("@i_valor")));
		// aCheckBookPreAuthRequest.(aRequest.readValueParam("@i_operacion"));
		aCheckBookPreAuthRequest.setBeneficiary(aRequest.readValueParam("@i_beneficiario"));

		// aCheckBookPreAuthRequest.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));
		// aCheckBookPreAuthRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		// aCheckBookPreAuthRequest.setLogin(aRequest.readValueParam("@i_login"));
		// aCheckBookPreAuthRequest.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		// wManagerCkeck.setStatus(aRequest.readValueParam("@i_login"));

		return aCheckBookPreAuthRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(CheckBookPreAuthResponse aResponse) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");
		if (!IsValidManagerCheckBookPreAuthResponse(aResponse))
			return null;

		for (CheckBookPreAuth obj : aResponse.getList())
			wProcedureResponse.addParam("@o_estado_cheque", ICTSTypes.SQLCHAR, 1, obj.getStatus());

		wProcedureResponse.setReturnCode(aResponse.getReturnCode());

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aResponse.getReturnCode() != 0) {
			// wProcedureResponse.addMessage(201171, "201171-CHEQUES HAN SIDO
			// SUSPENDIDOS PREVIAMENTE");
			wProcedureResponse = Utils.returnException(aResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private boolean IsValidManagerCheckBookPreAuthResponse(CheckBookPreAuthResponse aResponse) {
		String messageError = null;
		String msgErr = null;

		messageError = aResponse.getList() == null ? " Status can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);

		return true;

	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return coreServer;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	public ICoreServiceSendNotification getCoreServiceSendNotification() {
		// TODO Auto-generated method stub
		return coreServiceSendNotification;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getAcountEmailAdress());// comun

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getOfficerEmailAdress());// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyDescription1(anOriginalRequest.readValueParam("@i_mon"));// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationDetail.setTransaccionId(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));// comun

		Notification notification = new Notification();
		notification.setNotificationType("F"); // comun

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product originProduct = new Product();
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));

		NotificationRequest notificationRequest = new NotificationRequest();
		if (!Utils.isNull(anOriginalRequest.readParam("@s_culture")))
			notificationRequest.setCulture(anOriginalRequest.readValueParam("@s_culture"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ofi")))
			notificationRequest.setOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@s_ofi")));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_rol")))
			notificationRequest.setRole(Integer.parseInt(anOriginalRequest.readValueParam("@s_rol")));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn")))
			notificationRequest.setSessionIdCore(anOriginalRequest.readValueParam("@s_ssn"));

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("sessionId")))
			notificationRequest.setSessionIdIB(anOriginalRequest.readValueFieldInHeader("sessionId"));

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("term")))
			notificationRequest.setTerminal(anOriginalRequest.readValueFieldInHeader("term"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_user")))
			notificationRequest.setUserBv(anOriginalRequest.readValueParam("@s_user"));

		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(anOriginalRequest);
		notificationRequest.setOriginProduct(originProduct);

		return notificationRequest;
	}
}
