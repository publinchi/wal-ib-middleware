package com.cobiscorp.ecobis.ib.orchestration.scheduledpayment;

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
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceScheduledPayments;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.payment.template.PaymentOnlineTemplate;

@Component(name = "ScheduledPaymentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ScheduledPaymentOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ScheduledPaymentOrchestrationCore") })
public class ScheduledPaymentOrchestrationCore extends PaymentOnlineTemplate {

	private static ILogger logger = LogFactory.getLogger(ScheduledPaymentOrchestrationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	@Override
	protected ICoreServer getCoreServer() {
		return coreServer;
	}

	@Override
	protected ICoreService getCoreService() {
		return coreService;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected IProcedureResponse validatePreviousExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	@Override
	protected NotificationRequest transformNotificationRequest(IProcedureRequest aProcedureRequest,
			OfficerByAccountResponse anOfficer) {

		NotificationRequest notificationRequest = new NotificationRequest();
		Notification notification = new Notification();
		NotificationDetail notificationDetail = new NotificationDetail();
		Product originProduct = new Product();
		Currency currency = new Currency();

		notification.setNotificationType("N1");
		notification.setMessageType("F");

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());
		notificationDetail.setProductId("18");

		if (!Utils.isNull(aProcedureRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(aProcedureRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyId1(aProcedureRequest.readValueParam("@i_mon"));

		if (!Utils.isNull(aProcedureRequest.readParam("@t_trn")))
			notificationRequest.setCodeTransactionalIdentifier(aProcedureRequest.readValueParam("@t_trn"));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_culture")))
			notificationRequest.setCulture(aProcedureRequest.readValueParam("@s_culture"));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_ofi")))
			notificationRequest.setOfficeCode(Integer.parseInt(aProcedureRequest.readValueParam("@s_ofi")));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_rol")))
			notificationRequest.setRole(Integer.parseInt(aProcedureRequest.readValueParam("@s_rol")));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_ssn")))
			notificationRequest.setSessionIdCore(aProcedureRequest.readValueParam("@s_ssn"));

		if (!Utils.isNull(aProcedureRequest.readValueFieldInHeader("sessionId")))
			notificationRequest.setSessionIdIB(aProcedureRequest.readValueFieldInHeader("sessionId"));

		if (!Utils.isNull(aProcedureRequest.readValueFieldInHeader("term")))
			notificationRequest.setTerminal(aProcedureRequest.readValueFieldInHeader("term"));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_user")))
			notificationRequest.setUserBv(aProcedureRequest.readValueParam("@s_user"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_prod")))
			originProduct.setProductType(Integer.parseInt(aProcedureRequest.readValueParam("@i_prod")));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_cta")))
			originProduct.setProductNumber(aProcedureRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_mon"))) {
			currency.setCurrencyId(Integer.parseInt(aProcedureRequest.readValueParam("@i_mon")));
			originProduct.setCurrency(currency);
		}

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(aProcedureRequest);
		notificationRequest.setOriginProduct(originProduct);
		return notificationRequest;
	}

	@Override
	protected IProcedureResponse payDestinationProduct(IProcedureRequest aProcedureRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		return null;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
	}

	/***** inyeccion de dependencia para notificacion ******/
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	/***** inyeccion de dependencia coreservicemonetary ******/
	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/******* inyeccion dependencias server *****/
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/******* inyeccion dependencias servicio *****/
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	/******* inyeccion dependencias interfaz scheduled payments *****/
	@Reference(referenceInterface = ICoreServiceScheduledPayments.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceScheduledPayment", unbind = "unbindCoreServiceScheduledPayment")
	protected ICoreServiceScheduledPayments coreServiceScheduledPayments;

	public void bindCoreServiceScheduledPayment(ICoreServiceScheduledPayments service) {
		coreServiceScheduledPayments = service;
	}

	public void unbindCoreServiceScheduledPayment(ICoreServiceScheduledPayments service) {
		coreServiceScheduledPayments = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		@SuppressWarnings("unused")
		IProcedureResponse response = null;
		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServer", coreServer);
			mapInterfaces.put("coreService", coreService);
			mapInterfaces.put("coreServiceNotification", coreServiceNotification);
			mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);
			mapInterfaces.put("coreServiceScheduledPayments", coreServiceScheduledPayments);
			Utils.validateComponentInstance(mapInterfaces);
			SUPPORT_OFFLINE = false;
			aBagSPJavaOrchestration.put(PAYMENT_NAME, "PAGOS PROGRAMADOS");
			// se agrega un parametro para que no grabe en la tranmonet
			anOriginalRequest.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "N");
			// se agrega un parametro para que no grabe en la bv_pagos
			anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "N");

			response = executeStepsPaymentBase(anOriginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOriginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE FINAL -->" + response.getProcedureResponseAsString());
		if (response.readValueParam("@o_ssn_branch") == null) {
			response.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
		}
		if (response.readValueParam("@o_referencia") == null) {
			response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.base.payments.PaymentBaseTemplate#
	 * executePayment(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse executePayment(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {

		ScheduledPaymentResponse aScheduledPaymentResponse = coreServiceScheduledPayments
				.executeScheduledPayment(this.transformToScheduledPaymentRequest(request));// payService(transformToPaymentServiceRequest(aProcedureRequest));
		return this.transformToProcedureResponse(aScheduledPaymentResponse, aBagSPJavaOrchestration);
	}

	private ScheduledPaymentRequest transformToScheduledPaymentRequest(IProcedureRequest aProcedureRequest) {
		ScheduledPaymentRequest aScheduledPaymentRequest = new ScheduledPaymentRequest();
		User user = new User();
		Client client = new Client();
		Product debitProduct = new Product();
		Product creditProduct = new Product();
		Currency debitCurrency = new Currency();
		Currency creditCurrency = new Currency();
		PaymentServiceRequest paymentService = new PaymentServiceRequest();

		if (aProcedureRequest.readValueParam("@s_cliente") != null) {
			client.setIdCustomer(aProcedureRequest.readValueParam("@s_cliente"));
		}
		if (aProcedureRequest.readValueParam("@t_trn") != null) {
			aScheduledPaymentRequest.setTransaction(Integer.parseInt(aProcedureRequest.readValueParam("@t_trn")));
		}
		if (aProcedureRequest.readValueParam("@s_ssn_branch") != null) {
			aScheduledPaymentRequest.setReferenceNumberBranch(aProcedureRequest.readValueParam("@s_ssn_branch"));
		}
		if (aProcedureRequest.readValueParam("@s_ssn") != null) {
			aScheduledPaymentRequest.setReferenceNumber(aProcedureRequest.readValueParam("@s_ssn"));
		}
		if (aProcedureRequest.readValueParam("@i_ente") != null) {
			user.setEntityId(Integer.parseInt(aProcedureRequest.readValueParam("@i_ente")));
		}
		if (aProcedureRequest.readValueParam("@i_login") != null) {
			client.setLogin(aProcedureRequest.readValueParam("@i_login"));
		}
		if (aProcedureRequest.readValueParam("@i_id") != null) {
			aScheduledPaymentRequest.setId(Integer.parseInt(aProcedureRequest.readValueParam("@i_id")));
		}
		if (aProcedureRequest.readValueParam("@i_cta") != null) {
			debitProduct.setProductNumber(aProcedureRequest.readValueParam("@i_cta"));
		}
		if (aProcedureRequest.readValueParam("@i_mon") != null) {
			debitCurrency.setCurrencyId(Integer.parseInt(aProcedureRequest.readValueParam("@i_mon")));
		}
		if (aProcedureRequest.readValueParam("@i_prod") != null) {
			debitProduct.setProductId(Integer.parseInt(aProcedureRequest.readValueParam("@i_prod")));
		}
		if (aProcedureRequest.readValueParam("@i_tipo") != null) {
			aScheduledPaymentRequest.setType(aProcedureRequest.readValueParam("@i_tipo"));
		}
		if (aProcedureRequest.readValueParam("@i_codigo") != null) {
			aScheduledPaymentRequest.setCode(Integer.parseInt(aProcedureRequest.readValueParam("@i_codigo")));
		}
		if (aProcedureRequest.readValueParam("@i_cuenta_cr") != null) {
			creditProduct.setProductNumber(aProcedureRequest.readValueParam("@i_cuenta_cr"));
		}
		if (aProcedureRequest.readValueParam("@i_moneda_cr") != null) {
			creditCurrency.setCurrencyId(Integer.parseInt(aProcedureRequest.readValueParam("@i_moneda_cr")));
		}
		if (aProcedureRequest.readValueParam("@i_producto_cr") != null) {
			creditProduct.setProductId(Integer.parseInt(aProcedureRequest.readValueParam("@i_producto_cr")));
		}
		if (aProcedureRequest.readValueParam("@i_val") != null) {
			aScheduledPaymentRequest.setAmount(Double.parseDouble(aProcedureRequest.readValueParam("@i_val")));
		}
		if (aProcedureRequest.readValueParam("@i_fecha_ini") != null) {
			aScheduledPaymentRequest.setInitialDate(aProcedureRequest.readValueParam("@i_fecha_ini"));
		}
		if (aProcedureRequest.readValueParam("@i_num_pagos") != null) {
			aScheduledPaymentRequest
					.setPaymentsNumber(Integer.parseInt(aProcedureRequest.readValueParam("@i_num_pagos")));
		}
		if (aProcedureRequest.readValueParam("@i_num_dias") != null) {
			aScheduledPaymentRequest.setFrecuencyId(aProcedureRequest.readValueParam("@i_num_dias"));
		}
		if (aProcedureRequest.readValueParam("@i_concepto") != null) {
			aScheduledPaymentRequest.setConcept(aProcedureRequest.readValueParam("@i_concepto"));
		}
		if (aProcedureRequest.readValueParam("@i_item") != null) {
			aScheduledPaymentRequest.setItem(aProcedureRequest.readValueParam("@i_item"));
		}
		if (aProcedureRequest.readValueParam("@i_login") != null) {
			aScheduledPaymentRequest.setLogin(aProcedureRequest.readValueParam("@i_login"));
		}
		if (aProcedureRequest.readValueParam("@i_opcion") != null) {
			aScheduledPaymentRequest.setOption(aProcedureRequest.readValueParam("@i_opcion"));
		}
		if (aProcedureRequest.readValueParam("@i_notificar") != null) {
			aScheduledPaymentRequest.setReceiveNotification(aProcedureRequest.readValueParam("@i_notificar"));
		}
		if (aProcedureRequest.readValueParam("@i_dias_notif") != null) {
			aScheduledPaymentRequest
					.setDayToNotify(Integer.parseInt(aProcedureRequest.readValueParam("@i_dias_notif")));
		}
		if (aProcedureRequest.readValueParam("@i_reint_cobro") != null) {
			aScheduledPaymentRequest.setRecoveryRetryFailed(aProcedureRequest.readValueParam("@i_reint_cobro"));
		}
		if (aProcedureRequest.readValueParam("@i_fech_prox_cobr") != null) {
			aScheduledPaymentRequest.setNextPaymentDate(aProcedureRequest.readValueParam("@i_fech_prox_cobr"));
		}
		if (aProcedureRequest.readValueParam("@i_beneficiario") != null) {
			aScheduledPaymentRequest.setBeneficiaryName(aProcedureRequest.readValueParam("@i_beneficiario"));
		}
		if (aProcedureRequest.readValueParam("@i_operacion") != null) {
			aScheduledPaymentRequest.setOperation(aProcedureRequest.readValueParam("@i_operacion"));
		}
		if (aProcedureRequest.readValueParam("@i_origen_fondos") != null) {
			aScheduledPaymentRequest.setFundsSource(aProcedureRequest.readValueParam("@i_origen_fondos"));
		}
		if (aProcedureRequest.readValueParam("@i_dest_fondos") != null) {
			aScheduledPaymentRequest.setFundsUse(aProcedureRequest.readValueParam("@i_dest_fondos"));
		}
		/** parametros adicionales para pago programado pago de servicios */
		if (aProcedureRequest.readValueParam("@i_convenio") != null) {
			paymentService.setContractId(Integer.parseInt(aProcedureRequest.readValueParam("@i_convenio")));
		}
		if (aProcedureRequest.readValueParam("@i_tipo_interfaz") != null) {
			paymentService.setInterfaceType(aProcedureRequest.readValueParam("@i_tipo_interfaz"));
		}
		if (aProcedureRequest.readValueParam("@i_tipo_doc") != null) {
			paymentService.setDocumentType(aProcedureRequest.readValueParam("@i_tipo_doc"));
		}
		if (aProcedureRequest.readValueParam("@i_llave") != null) {
			aScheduledPaymentRequest.setKey(aProcedureRequest.readValueParam("@i_llave"));
		}
		if (aProcedureRequest.readValueParam("@i_num_doc") != null) {
			paymentService.setDocumentId(aProcedureRequest.readValueParam("@i_num_doc"));
		}
		if (aProcedureRequest.readValueParam("@i_ref1") != null) {
			paymentService.setRef1(aProcedureRequest.readValueParam("@i_ref1"));
		}
		if (aProcedureRequest.readValueParam("@i_ref2") != null) {
			paymentService.setRef2(aProcedureRequest.readValueParam("@i_ref2"));
		}
		if (aProcedureRequest.readValueParam("@i_ref3") != null) {
			paymentService.setRef3(aProcedureRequest.readValueParam("@i_ref3"));
		}
		if (aProcedureRequest.readValueParam("@i_ref4") != null) {
			paymentService.setRef4(aProcedureRequest.readValueParam("@i_ref4"));
		}
		if (aProcedureRequest.readValueParam("@i_ref5") != null) {
			paymentService.setRef5(aProcedureRequest.readValueParam("@i_ref5"));
		}
		if (aProcedureRequest.readValueParam("@i_ref6") != null) {
			paymentService.setRef6(aProcedureRequest.readValueParam("@i_ref6"));
		}
		if (aProcedureRequest.readValueParam("@i_ref7") != null) {
			paymentService.setRef7(aProcedureRequest.readValueParam("@i_ref7"));
		}
		if (aProcedureRequest.readValueParam("@i_ref8") != null) {
			paymentService.setRef8(aProcedureRequest.readValueParam("@i_ref8"));
		}
		if (aProcedureRequest.readValueParam("@i_ref9") != null) {
			paymentService.setRef9(aProcedureRequest.readValueParam("@i_ref9"));
		}
		if (aProcedureRequest.readValueParam("@i_ref10") != null) {
			paymentService.setRef10(aProcedureRequest.readValueParam("@i_ref10"));
		}
		if (aProcedureRequest.readValueParam("@i_ref11") != null) {
			paymentService.setRef11(aProcedureRequest.readValueParam("@i_ref11"));
		}
		if (aProcedureRequest.readValueParam("@i_ref12") != null) {
			paymentService.setRef12(aProcedureRequest.readValueParam("@i_ref12"));
		}
		if (aProcedureRequest.readValueParam("@i_categoria") != null) {
			paymentService.setCategoryId(aProcedureRequest.readValueParam("@i_categoria"));
		}

		debitProduct.setCurrency(debitCurrency);
		creditProduct.setCurrency(creditCurrency);
		aScheduledPaymentRequest.setUser(user);
		aScheduledPaymentRequest.setClient(client);
		aScheduledPaymentRequest.setDebitProduct(debitProduct);
		aScheduledPaymentRequest.setCreditProduct(creditProduct);
		aScheduledPaymentRequest.setPaymentService(paymentService);

		return aScheduledPaymentRequest;
	}

	private IProcedureResponse transformToProcedureResponse(ScheduledPaymentResponse aScheduledPaymentResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse aProcedureResponse = new ProcedureResponseAS();
		aProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");

		if (aScheduledPaymentResponse.getReturnCode() == 0) {
			if (!IsValidServiceSatatementResponse(aScheduledPaymentResponse))
				return null;

			if (aScheduledPaymentResponse.getReference() != null) {
				aProcedureResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 0,
						aScheduledPaymentResponse.getReference().toString());
			}
			if (aScheduledPaymentResponse.getBranchSSN() != null) {
				aProcedureResponse.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0,
						aScheduledPaymentResponse.getBranchSSN().toString());
			}
		} else {
			aProcedureResponse = Utils.returnException(aScheduledPaymentResponse.getMessages());
			aProcedureResponse.setReturnCode(aScheduledPaymentResponse.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, aProcedureResponse);
		}

		return aProcedureResponse;
	}

	private boolean IsValidServiceSatatementResponse(ScheduledPaymentResponse aPaymentServiceResponse) {
		String messageError = null;
		messageError = aPaymentServiceResponse.getBranchSSN() == null ? "SsnBranch can't be null" : "OK";
		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);
		return true;
	}

}
