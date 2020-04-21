package com.cobiscorp.ecobis.orchestration.core.ib.cards.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Card;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Payment;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IServicePayCreditCard;
import com.cobiscorp.ecobis.orchestration.core.ib.payment.template.PaymentOnlineTemplate;

//import com.cobiscorp.ecobis.orchestration.core.ib.transaction.monetary.TransactionMonetaryExecutor;

@Component(name = "CreditCardPaymentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardPaymentOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.0"),
		@Property(name = "service.identifier", value = "CreditCardPaymentOrchestrationCore") })
public class CreditCardPaymentOrchestrationCore extends PaymentOnlineTemplate {
	private static ILogger logger = LogFactory.getLogger(CreditCardPaymentOrchestrationCore.class);

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceSendNotification coreServiceNotification;

	@Reference(referenceInterface = IServicePayCreditCard.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC, bind = "bindCoreSericeCardsPayment", unbind = "unbindCoreSericeCardsPayment")
	protected IServicePayCreditCard sericeCardsPayment;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void loadConfiguration(IConfigurationReader arg0) {
	}

	protected void bindCoreServer(ICoreServer service) {
		this.coreServer = service;
	}

	protected void unbindCoreServer(ICoreServer service) {
		this.coreServer = null;
	}

	protected void bindCoreService(ICoreService service) {
		this.coreService = service;
	}

	protected void unbindCoreService(ICoreService service) {
		this.coreService = null;
	}

	protected void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		this.coreServiceNotification = service;
	}

	protected void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		this.coreServiceNotification = null;
	}

	protected void bindCoreSericeCardsPayment(IServicePayCreditCard service) {
		if (logger.isInfoEnabled())
			logger.logInfo("SETTING CARDS" + service);
		this.sericeCardsPayment = service;
	}

	protected void unbindCoreSericeCardsPayment(IServicePayCreditCard service) {
		this.sericeCardsPayment = null;
	}

	protected void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		this.coreServiceMonetaryTransaction = service;
	}

	protected void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		this.coreServiceMonetaryTransaction = null;
	}

	protected ICoreServer getCoreServer() {
		return this.coreServer;
	}

	protected ICoreService getCoreService() {
		return this.coreService;
	}

	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return this.coreServiceNotification;
	}

	/*
	 * protected IServicePayCreditCard getServiceCardsPayment() {
	 * logger.logInfo("GETTING SERVICE CARDS"); return this.sericeCardsPayment;
	 * }
	 */

	protected NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer) {
		NotificationRequest notificationRequest = new NotificationRequest();
		Notification notification = new Notification();
		notification.setId("N42");

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()).booleanValue())
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()).booleanValue())
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")).booleanValue()) {
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (!Utils.isNull(anOriginalRequest.readParam("@i_numtarjeta")).booleanValue())
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_numtarjeta"));
		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")).booleanValue())
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")).booleanValue()) {
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));
		}
		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")).booleanValue()) {
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));
		}
		if (!Utils.isNull(anOriginalRequest.readParam("@i_moneda_desc")).booleanValue())
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_moneda_desc"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon_des")).booleanValue()) {
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon_des"));
		}

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Product product = new Product();
		product.setProductType(Integer.valueOf(18));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")).booleanValue()) {
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		notificationRequest.setOriginProduct(product);
		return notificationRequest;
	}

	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		try {
			if (logger.isInfoEnabled())
				logger.logInfo("Loading parameters dependency 1:" + this.sericeCardsPayment);
			Map mapInterfaces = new HashMap();
			mapInterfaces.put("coreServer", this.coreServer);
			mapInterfaces.put("coreService", this.coreService);
			mapInterfaces.put("coreServiceNotification", this.coreServiceNotification);
			mapInterfaces.put("sericeCardsPayment", this.sericeCardsPayment);

			Utils.validateComponentInstance(mapInterfaces);

			aBagSPJavaOrchestration.put("PAYMENT_NAME", "PAGO DE TARJETA DE CREDITO");
			response = executeStepsPaymentBase(anOriginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {
		}
		return response;
	}

	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE FINAL -->" + response.getProcedureResponseAsString());
		return response;
	}

	protected IProcedureResponse payDestinationProduct(IProcedureRequest aProcedureRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse responseValidateLocal = null;

		// PaymentCreditCardResponse aPaymentCreditCardResponse =
		// this.sericeCardsPayment.payCreditCard(transformToPaymentCreditCardRequest(aProcedureRequest));
		boolean ejecucionTercero = this.sericeCardsPayment.payCreditCard(aBagSPJavaOrchestration);

		responseValidateLocal = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_VALIDATE_LOCAL");
		IProcedureResponse iprocedure = (IProcedureResponse) aBagSPJavaOrchestration.get("OPERATION1_RESPONSE");
		if (Utils.isNull(responseValidateLocal).booleanValue()) {
			iprocedure.addParam("@o_autorizacion", 39, 0, "N");
			// aPaymentCreditCardResponse.setAuthorizationRequired("N");
		} else if (!Utils.isNull(responseValidateLocal.readValueParam("@o_autorizacion")).booleanValue())
			iprocedure.addParam("@o_autorizacion", 39, 0, responseValidateLocal.readValueParam("@o_autorizacion"));
		// aPaymentCreditCardResponse.setAuthorizationRequired(responseValidateLocal.readValueParam("@o_autorizacion"));
		else {
			// aPaymentCreditCardResponse.setAuthorizationRequired("S");
			iprocedure.addParam("@o_autorizacion", 39, 0, "S");
		}
		return (IProcedureResponse) aBagSPJavaOrchestration.get("OPERATION1_RESPONSE");
		// return transformToProcedureResponse(aPaymentCreditCardResponse);
	}

	private PaymentCreditCardRequest transformToPaymentCreditCardRequest(IProcedureRequest aProcedureRequest) {
		PaymentCreditCardRequest aPaymentCreditCardRequest = new PaymentCreditCardRequest();

		Card card = new Card();
		Payment payment = new Payment();

		card.setCardNumber(aProcedureRequest.readValueParam("@i_numero_tarjeta"));

		aPaymentCreditCardRequest.setCard(card);
		aPaymentCreditCardRequest.setConcept(aProcedureRequest.readValueParam("@i_concepto"));
		aPaymentCreditCardRequest.setLocation(aProcedureRequest.readValueParam("@i_localidad1"));
		aPaymentCreditCardRequest.setMessageType(aProcedureRequest.readValueParam("@i_codtipomsg"));
		aPaymentCreditCardRequest.setFranchise(aProcedureRequest.readValueParam("@i_superfranquicia"));
		aPaymentCreditCardRequest.setReferenceNumber(aProcedureRequest.readValueParam("@i_numreferencia"));
		aPaymentCreditCardRequest.setTransactionId(aProcedureRequest.readValueParam("@i_idtransaccion"));
		aPaymentCreditCardRequest.setTrxChannelId(aProcedureRequest.readValueParam("@i_id_trancanal"));
		payment.setPaymentAmmount(new BigDecimal(aProcedureRequest.readValueParam("@i_val")));
		aPaymentCreditCardRequest.setPayment(payment);

		return aPaymentCreditCardRequest;
	}

	private IProcedureResponse transformToProcedureResponse(PaymentCreditCardResponse aPaymentCreditCardResponse) {
		IProcedureResponse response = new ProcedureResponseAS();

		response.addFieldInHeader("executionResult", 'S', "1");
		response.setReturnCode(0);

		response.addParam("@o_autorizacion", 39, 0, aPaymentCreditCardResponse.getAuthorizationRequired());
		response.addParam("@o_ssn_branch", 56, 0, String.valueOf(aPaymentCreditCardResponse.getSsnBranch()));
		response.addParam("@o_referencia", 56, 0, String.valueOf(aPaymentCreditCardResponse.getReference()));
		response.addParam("@o_valor_convertido", 60, 0, aPaymentCreditCardResponse.getConvertValue().toString());
		response.addParam("@o_cotizacion", 62, 0, String.valueOf(aPaymentCreditCardResponse.getExchangeRate()));

		return response;
	}

	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		aProcedureRequest.addInputParam("@i_cta_des", anOriginalRequest.readParam("@i_numtarjeta").getDataType(),
				anOriginalRequest.readValueParam("@i_numtarjeta"));
		Utils.copyParam("@i_mon_des", aProcedureRequest, anOriginalRequest);
		aProcedureRequest.addInputParam("@i_prod_des", 48, "83");
	}

	protected IProcedureResponse validatePreviousExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

}