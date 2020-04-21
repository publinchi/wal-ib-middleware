package com.cobiscorp.ecobis.orchestration.core.ib.application.bank.guarantee;

import java.math.BigDecimal;
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
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceApplicationBankGuarantee;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.application.template.ApplicationOnlineTemplate;

@Component(name = "ApplicationBankGuaranteeOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ApplicationBankGuaranteeOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1"),
		@Property(name = "service.identifier", value = "ApplicationBankGuaranteeOrchestrationCore") })
public class ApplicationBankGuaranteeOrchestrationCore extends ApplicationOnlineTemplate {

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServiceApplicationBankGuarantee.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceApplicationBankGuarantee", unbind = "unbindCoreServiceApplicationBankGuarantee")
	protected ICoreServiceApplicationBankGuarantee coreServiceApplicationBankGuarantee;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	protected ICoreServiceSendNotification coreServiceSendNotification;

	ILogger logger = LogFactory.getLogger(ApplicationBankGuaranteeOrchestrationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceApplicationBankGuarantee(ICoreServiceApplicationBankGuarantee service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceApplicationBankGuarantee**********" + service);
		coreServiceApplicationBankGuarantee = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceApplicationBankGuarantee(ICoreServiceApplicationBankGuarantee service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceApplicationBankGuarantee**********" + service);
		coreServiceApplicationBankGuarantee = null;
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

	// /////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected IProcedureResponse executeApplicationCheckbook(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		ApplicationBankGuaranteeResponse aApplicationBankGuaranteeResponse = null;
		ApplicationBankGuaranteeRequest aApplicationBankGuaranteeRequest = transformApplicationBankGuaranteeRequest(
				request.clone());
		if (logger.isInfoEnabled())
			logger.logInfo("OBJ: " + aApplicationBankGuaranteeRequest.toString());

		try {
			messageError = "get: ERROR EXECUTING SERVICE";
			aApplicationBankGuaranteeRequest.setOriginalRequest(request);
			if (logger.isInfoEnabled())
				logger.logInfo("LLAMA coreServiceApplicationBankGuarantee.executeApplication");
			aApplicationBankGuaranteeResponse = coreServiceApplicationBankGuarantee
					.executeApplication(aApplicationBankGuaranteeRequest);

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

		return transformProcedureResponse(aApplicationBankGuaranteeResponse, request);
	}

	/******************
	 * Transformación de ProcedureRequest a RequestCheckbookRequest
	 ********************/
	private ApplicationBankGuaranteeRequest transformApplicationBankGuaranteeRequest(IProcedureRequest aRequest) {
		ApplicationBankGuaranteeRequest requestApplicationBankGuarantee = new ApplicationBankGuaranteeRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_monto") == null ? " - @i_monto can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_moneda can't be null" : "";
		messageError += aRequest.readValueParam("@i_pplazo") == null ? " - @i_pplazo can't be null" : "";
		messageError += aRequest.readValueParam("@i_beneficiario") == null ? " - @i_beneficiario can't be null" : "";
		messageError += aRequest.readValueParam("@i_clase_garantia") == null ? " - @i_clase_garantia can't be null"
				: "";
		messageError += aRequest.readValueParam("@i_tipo_garantia") == null ? " - @i_tipo_garantia can't be null" : "";
		messageError += aRequest.readValueParam("@i_ente") == null ? " - @i_ente can't be null" : "";
		messageError += aRequest.readValueParam("@i_fecha_expiracion") == null ? " - @i_fecha_expiracion can't be null"
				: "";
		messageError += aRequest.readValueParam("@i_fecha_crea") == null ? " - @i_fecha_crea can't be null" : "";
		messageError += aRequest.readValueParam("@i_motivo1") == null ? " - @i_motivo1 can't be null" : "";
		messageError += aRequest.readValueParam("@i_agencia") == null ? " - @i_agencia can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		if (aRequest.readValueParam("@i_linea_credito") != null)
			requestApplicationBankGuarantee.setCreditLine(aRequest.readValueParam("@i_linea_credito"));
		requestApplicationBankGuarantee.setAmount(new BigDecimal(aRequest.readValueParam("@i_monto")));
		requestApplicationBankGuarantee.setCurrency(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		requestApplicationBankGuarantee.setGuaranteeTerm(Integer.parseInt(aRequest.readValueParam("@i_pplazo")));
		requestApplicationBankGuarantee.setBeneficiary(aRequest.readValueParam("@i_beneficiario"));
		requestApplicationBankGuarantee.setGuaranteeClass(aRequest.readValueParam("@i_clase_garantia"));
		requestApplicationBankGuarantee.setGuaranteeType(aRequest.readValueParam("@i_tipo_garantia"));
		requestApplicationBankGuarantee.setEntity(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		requestApplicationBankGuarantee.setExpirationDate(aRequest.readValueParam("@i_fecha_expiracion"));
		requestApplicationBankGuarantee.setCreationDate(aRequest.readValueParam("@i_fecha_crea"));
		requestApplicationBankGuarantee.setCause(aRequest.readValueParam("@i_motivo1"));
		if (aRequest.readValueParam("@i_plazo_fijo") != null)
			requestApplicationBankGuarantee.setFixedTerm(aRequest.readValueParam("@i_plazo_fijo"));
		if (aRequest.readValueParam("@i_tipo_gar_tr") != null)
			requestApplicationBankGuarantee.setGuaranteeTypeApp(aRequest.readValueParam("@i_tipo_gar_tr"));
		if (aRequest.readValueParam("@i_dirpcd") != null)
			requestApplicationBankGuarantee.setAddress(aRequest.readValueParam("@i_dirpcd"));
		requestApplicationBankGuarantee.setAgency(Integer.parseInt(aRequest.readValueParam("@i_agencia")));
		return requestApplicationBankGuarantee;
	}

	/*********************
	 * Transformación de Response a RequestCheckbookResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(
			ApplicationBankGuaranteeResponse aApplicationBankGuaranteeResponse, IProcedureRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo("transformProcedureResponse " + aApplicationBankGuaranteeResponse.toString());
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response ApplicationBankGuaranteeResponse");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (aApplicationBankGuaranteeResponse.getReturnCode() != 0) {
			// Si estamos en linea y hubo error
			wProcedureResponse = Utils.returnException(aApplicationBankGuaranteeResponse.getMessages());
			wProcedureResponse.setReturnCode(aApplicationBankGuaranteeResponse.getReturnCode());
			return wProcedureResponse;
		}

		if (!aApplicationBankGuaranteeResponse.getSuccess()) {

			wProcedureResponse = Utils.returnException(aApplicationBankGuaranteeResponse.getMessages());
			wProcedureResponse.setReturnCode(aApplicationBankGuaranteeResponse.getReturnCode());
		}
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ wProcedureResponse.getProcedureResponseAsString());

		wProcedureResponse.addParam("@o_siguiente", ICTSTypes.SYBVARCHAR, 1,
				aApplicationBankGuaranteeResponse.getBankGuarantee());

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final ApplicationBankGuaranteeResponse --> "
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
			logger.logDebug("executeJavaOrchestration RequestBankGuarantee");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceBankGuarantee", coreServiceApplicationBankGuarantee);
		mapInterfaces.put("coreServer", coreServer);
		Utils.validateComponentInstance(mapInterfaces);
		sync = false;
		graba_tranMonet = "N";

		try {
			executeStepsApplicationBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration ApplicationBankGuarantee");
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
		return null;
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
		if (logger.isInfoEnabled())
			logger.logInfo("transformNotificationRequest inicio " + aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION));

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

		if (!Utils.isNull(anOriginalRequest.readParam("@i_beneficiario")))
			notificationDetail.setAuxiliary2(anOriginalRequest.readValueParam("@i_beneficiario"));

		String mensaje = "";
		if (!Utils.isNull(anOriginalRequest.readParam("@i_linea_credito"))) {
			mensaje = "a una Linea de Credito ";
		} else if (!Utils.isNull(anOriginalRequest.readParam("@i_plazo_fijo"))) {
			mensaje = "a un Deposito a Plazo ";
		} else if (!Utils.isNull(anOriginalRequest.readParam("@i_tipo_gar_tr"))) {
			mensaje = "a una garantia ";
		}
		if (logger.isInfoEnabled())
			logger.logInfo("Mensaje ==> " + mensaje);

		notificationDetail.setAuxiliary3("Boleta generada en base " + mensaje);

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Notification notification = new Notification();
		Product originProduct = new Product();
		originProduct.setProductType(9);
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		notification.setId("N89");

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
