package com.cobiscorp.ecobis.orchestration.core.ib.application.credit.line;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

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
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceApplicationCreditLine;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
//import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceApplicationCreditLine;
//import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.application.template.ApplicationOfflineTemplate;

@Component(name = "ApplicationDisbursementCreditLineOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "ApplicationDisbursementCreditLineOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1"),
		@Property(name = "service.identifier", value = "ApplicationDisbursementCreditLineOrchestrationCore") })

public class ApplicationDisbursementCreditLineOrchestrationCore extends ApplicationOfflineTemplate {

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServiceApplicationCreditLine.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceApplicationCreditLine", unbind = "unbindCoreServiceApplicationCreditLine")
	protected ICoreServiceApplicationCreditLine coreServiceApplicationCreditLine;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	protected ICoreServiceSendNotification coreServiceSendNotification;

	ILogger logger = LogFactory.getLogger(ApplicationDisbursementCreditLineOrchestrationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceApplicationCreditLine(ICoreServiceApplicationCreditLine service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceApplicationCreditLine**********" + service);
		coreServiceApplicationCreditLine = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceApplicationCreditLine(ICoreServiceApplicationCreditLine service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceApplicationCreditLine**********" + service);
		coreServiceApplicationCreditLine = null;
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected IProcedureResponse executeApplicationCheckbook(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		ApplicationCreditLineResponse aApplicationCreditLineResponse = null;
		ApplicationCreditLineRequest aApplicationCreditLineRequest = transformApplicationCreditLineRequest(
				request.clone());
		if (logger.isInfoEnabled())
			logger.logInfo("OBJ: " + aApplicationCreditLineRequest.toString());

		try {
			messageError = "get: ERROR EXECUTING SERVICE";
			aApplicationCreditLineRequest.setOriginalRequest(request);
			if (logger.isInfoEnabled())
				logger.logInfo("LLAMA coreServiceCheckbook.getRequestCheckbook");
			aApplicationCreditLineResponse = coreServiceApplicationCreditLine
					.getApplicationCreditLine(aApplicationCreditLineRequest);

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

		return transformProcedureResponse(aApplicationCreditLineResponse, request);
	}

	/******************
	 * Transformación de ProcedureRequest a RequestCheckbookRequest
	 ********************/
	private ApplicationCreditLineRequest transformApplicationCreditLineRequest(IProcedureRequest aRequest) {
		ApplicationCreditLineRequest requestApplicationCreditLine = new ApplicationCreditLineRequest();
		Product product = new Product();
		Currency currency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_ente") == null ? " - @i_cliente can't be null" : "";
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_creditLine can't be null" : ""; // CreditLine
		messageError = aRequest.readValueParam("@i_mon") == null ? " - @i_moneda can't be null" : "";
		messageError = aRequest.readValueParam("@i_monto") == null ? " - @i_monto_solicitado can't be null" : "";// monto
																													// solicitado
		messageError = aRequest.readValueParam("@i_monto_disponible") == null ? " - @i_monto_disponible can't be null"
				: "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		product.setProductNumber(aRequest.readValueParam("@i_cta"));
		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		product.setCurrency(currency);

		requestApplicationCreditLine.setProductCreditLine(product);
		requestApplicationCreditLine.setMontoSolicitado(new BigDecimal(aRequest.readValueParam("@i_monto")));
		requestApplicationCreditLine.setMontoDisponible(new BigDecimal(aRequest.readValueParam("@i_monto_disponible")));
		requestApplicationCreditLine.setBeneficiaryId(aRequest.readValueParam("@i_ente"));

		return requestApplicationCreditLine;
	}

	/*********************
	 * Transformación de Response a RequestCheckbookResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(ApplicationCreditLineResponse aApplicationCreditLineResponse,
			IProcedureRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo("transformProcedureResponse " + aApplicationCreditLineResponse.toString());
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response ApplicationCreditLineResponse");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (aApplicationCreditLineResponse.getReturnCode() != 0
				&& aApplicationCreditLineResponse.getReturnCode() != 40002) {
			// Si estamos en linea y hubo error
			wProcedureResponse = Utils.returnException(aApplicationCreditLineResponse.getMessages());
			wProcedureResponse.setReturnCode(aApplicationCreditLineResponse.getReturnCode());
			return wProcedureResponse;
		}

		if (!aApplicationCreditLineResponse.getSuccess()) {

			wProcedureResponse = Utils.returnException(aApplicationCreditLineResponse.getMessages());
			wProcedureResponse.setReturnCode(aApplicationCreditLineResponse.getReturnCode());
		}
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ wProcedureResponse.getProcedureResponseAsString());

		wProcedureResponse.addParam("@o_siguiente", ICTSTypes.SQLINT1, 1,
				aApplicationCreditLineResponse.getIdSolicitud().toString());

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final ApplicationCreditLineResponse --> "
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
		mapInterfaces.put("coreServiceCheckbook", coreServiceApplicationCreditLine);
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreServiceMonetaryTransaction", this.coreServiceMonetaryTransaction);
		Utils.validateComponentInstance(mapInterfaces);
		sync = false;
		graba_tranMonet = "N";

		try {
			executeStepsApplicationBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration ApplicationCreditLine");
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
		if (logger.isInfoEnabled())
			logger.logInfo("transformNotificationRequest inicio");

		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());// comun

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon"))) // @i_m
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_monto")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_monto")); // comun
																						// i_val

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationDetail.setTransaccionId(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));// comun

		if (!Utils.isNull(responseTransaction.readParam("@o_siguiente")))
			notificationDetail.setAuxiliary1(responseTransaction.readValueParam("@o_siguiente"));

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Notification notification = new Notification();
		Product originProduct = new Product();
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (originProduct.getProductType() == 21)
			notification.setId("N88");

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
		if (logger.isInfoEnabled())
			logger.logInfo("transformNotificationRequest final response " + notificationRequest);

		return notificationRequest;
	}

}
