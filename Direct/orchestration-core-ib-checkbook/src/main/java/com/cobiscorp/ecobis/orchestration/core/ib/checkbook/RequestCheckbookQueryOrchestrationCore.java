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
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CashiersCheck;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Checkbook;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductBanking;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.application.template.ApplicationOfflineTemplate;

//import com.cobiscorp.ecobis.orchestration.core.ib.application.template.ApplicationOnlineTemplate;
/**
 * 
 * @author jmoreta
 *
 */
@Component(name = "RequestCheckbookQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "RequestCheckbookQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "RequestCheckbookQueryOrchestrationCore") })

public class RequestCheckbookQueryOrchestrationCore extends ApplicationOfflineTemplate {

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCheckbook", unbind = "unbindCoreServiceCheckbook")
	protected ICoreServiceCheckbook coreServiceCheckbook;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	protected ICoreServiceSendNotification coreServiceSendNotification;

	ILogger logger = LogFactory.getLogger(RequestCheckbookQueryOrchestrationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		coreServiceCheckbook = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		coreServiceCheckbook = null;
	}

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

	/**
	 * Instance ServiceNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceNotification service) {
		coreServiceNotification = null;
	}

	/**
	 * Instance CoreServer Interface
	 * 
	 * @param service
	 */
	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
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
	protected IProcedureResponse executeApplicationCheckbook(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		RequestCheckbookResponse aRequestCheckbookResponse = null;
		RequestCheckbookRequest aRequestCheckbookRequest = transformRequestCheckbookRequest(request.clone());

		try {
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "getRequestCheckbook: " + aRequestCheckbookRequest.getAuthorizationRequired();
			queryName = "getRequestCheckbook";

			aRequestCheckbookRequest.setOriginalRequest(request);
			aRequestCheckbookResponse = coreServiceCheckbook.getRequestCheckbook(aRequestCheckbookRequest);

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
		aBagSPJavaOrchestration.put(APPLICATION_NAME, queryName);

		return transformProcedureResponse(aRequestCheckbookResponse, request);
	}

	/******************
	 * Transformación de ProcedureRequest a RequestCheckbookRequest
	 ********************/
	private RequestCheckbookRequest transformRequestCheckbookRequest(IProcedureRequest aRequest) {
		RequestCheckbookRequest requestCheckbook = new RequestCheckbookRequest();
		Checkbook checkbook = new Checkbook();
		// User user = new User();
		Product product = new Product();
		ProductBanking productBanking = new ProductBanking();
		Client client = new Client();
		Currency currency = new Currency();
		CashiersCheck cashiersCheck = new CashiersCheck();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@i_tchq") == null ? " - @i_tchq can't be null" : "";
		messageError += aRequest.readValueParam("@i_nchqs") == null ? " - @i_nchqs can't be null" : "";
		// messageError += aRequest.readValueParam("@i_ofientr") == null ? " -
		// @i_ofientr can't be null":"";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";
		// messageError += aRequest.readValueParam("@i_dia_entrega") == null ? "
		// - @i_dia_entrega can't be null":"";
		// messageError += aRequest.readValueParam("@i_id_entrega") == null ? "
		// - @i_id_entrega can't be null":"";
		// messageError += aRequest.readValueParam("@i_nombre_entrega") == null
		// ? " - @i_nombre_entrega can't be null":"";
		// messageError += aRequest.readValueParam("@i_nombre_arte") == null ? "
		// - @i_nombre_arte can't be null":"";
		// messageError += aRequest.readValueParam("@i_tipo_id") == null ? " -
		// @i_tipo_id can't be null":"";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		// numberOfChecks,type,deliveryDate
		checkbook.setNumberOfChecks(Integer.parseInt(aRequest.readValueParam("@i_nchqs")));
		checkbook.setType(aRequest.readValueParam("@i_tchq"));
		checkbook.setDeliveryDate(aRequest.readValueParam("@i_dia_entrega"));

		// user.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		// productAlias(productAbbreviation),producNumber(account)
		// product.setProductAlias(aRequest.readValueParam("@i_producto"));
		product.setProductNumber(aRequest.readValueParam("@i_cta"));

		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		productBanking.setId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		client.setLogin(aRequest.readValueParam("@i_login"));
		cashiersCheck.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));//
		cashiersCheck.setPurpose(aRequest.readValueParam("@i_proposito"));//
		requestCheckbook.setCheckbook(checkbook);
		requestCheckbook.setCurrency(currency);
		// requestCheckbook.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));

		requestCheckbook.setCheckbookArt(aRequest.readValueParam("@i_nombre_arte"));
		requestCheckbook.setDeliveryName(aRequest.readValueParam("@i_nombre_entrega"));
		requestCheckbook.setDeliveyId(aRequest.readValueParam("@i_id_entrega"));
		// requestCheckbook.setEntityId(user);
		requestCheckbook.setOfficeDelivery(Integer.parseInt(aRequest.readValueParam("@i_ofientr")));
		requestCheckbook.setOperation(aRequest.readValueParam("@i_operacion"));
		requestCheckbook.setProduct(product);
		requestCheckbook.setProductId(productBanking);
		requestCheckbook.setTypeId(aRequest.readValueParam("@i_tipo_id"));
		requestCheckbook.setAmount(cashiersCheck);
		requestCheckbook.setUserName(client);

		return requestCheckbook;

	}

	/*********************
	 * Transformación de Response a RequestCheckbookResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(RequestCheckbookResponse aRequestCheckbookResponse,
			IProcedureRequest request) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response RequestCheckbookResponse");

		wProcedureResponse.addParam("@o_tipo_chequera", ICTSTypes.SQLVARCHAR, 1,
				aRequestCheckbookResponse.getTypeCheckbook());
		wProcedureResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 1,
				request.readValueParam("@s_ssn_branch") == null ? "0" : request.readValueParam("@s_ssn_branch"));// @s_ssn_branch

		// Retorno Código ERROR
		wProcedureResponse.setReturnCode(aRequestCheckbookResponse.getReturnCode());

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aRequestCheckbookResponse.getReturnCode() != 0) {
			// wProcedureResponse.addMessage(aRequestCheckbookResponse.getReturnCode(),
			// "351540-NO EXISTE SERVICIO PERSONALIZABLE");

			wProcedureResponse = Utils.returnException(aRequestCheckbookResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final RequestCheckbook --> "
					+ wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration RequestCheckbook");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceCheckbook", coreServiceCheckbook);
		mapInterfaces.put("coreServer", coreServer);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			// aBagSPJavaOrchestration.put(LOG_MESSAGE, "SOLICITUD DE CHEQUERAS
			// ");
			executeStepsApplicationBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration RequestCheckbook");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.logDebug("RequestCheckBookQueryOrchestrationCore --> getCoreServer");
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
		// TODO Auto-generated method stub
		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
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

		// parametros de la transaccion

		if (!Utils.isNull(responseTransaction.readParam("@o_tipo_chequera")))
			notificationDetail.setAuxiliary1(responseTransaction.readValueParam("@o_tipo_chequera"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_nchqs")))
			notificationDetail.setAuxiliary2(anOriginalRequest.readValueParam("@i_nchqs"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_dia_entrega")))
			notificationDetail.setAuxiliary3(anOriginalRequest.readValueParam("@i_dia_entrega"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_nombre_entrega")))
			notificationDetail.setAuxiliary4(anOriginalRequest.readValueParam("@i_nombre_entrega"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_id_entrega")))
			notificationDetail.setAuxiliary5(anOriginalRequest.readValueParam("@i_id_entrega"));

		Notification notification = new Notification();
		notification.setNotificationType("F"); // comun
		notification.setId("N47");

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
