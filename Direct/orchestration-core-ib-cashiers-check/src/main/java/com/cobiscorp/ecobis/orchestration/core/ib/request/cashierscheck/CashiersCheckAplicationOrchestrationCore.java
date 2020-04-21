package com.cobiscorp.ecobis.orchestration.core.ib.request.cashierscheck;

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
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CashiersCheck;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCashiersCheck;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.application.template.ApplicationOfflineTemplate;

/**
 * 
 * @author jveloz
 *
 */
@Component(name = "CashiersCheckAplicationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CashiersCheckAplicationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CashiersCheckAplicationOrchestrationCore") })
public class CashiersCheckAplicationOrchestrationCore extends ApplicationOfflineTemplate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.applications.
	 * ApplicationsBaseTemplate#updateLocalExecution(com.cobiscorp.cobis.cts.
	 * domains. IProcedureRequest, java.util.Map)
	 */

	/*
	 * @Override protected IProcedureResponse updateLocalExecution(
	 * IProcedureRequest anOriginalRequest, Map<String, Object> bag) { // TODO
	 * Auto-generated method stub logger.logInfo("==================>> " +
	 * monedaMonto); anOriginalRequest.addInputParam("@i_mon_2",
	 * ICTSTypes.SQLINT4, monedaMonto.toString()); return
	 * super.updateLocalExecution(anOriginalRequest, bag); }
	 */

	@Reference(referenceInterface = ICoreServiceCashiersCheck.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCashiersCheck", unbind = "unbindCoreServiceCashiersCheck")
	protected ICoreServiceCashiersCheck coreServiceCashiersCheck;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	ILogger logger = this.getLogger();
	public String finalOffice;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceCashiersCheck(ICoreServiceCashiersCheck service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceCashiersCheck**********" + service);
		coreServiceCashiersCheck = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceCashiersCheck(ICoreServiceCashiersCheck service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceCashiersCheck**********" + service);
		coreServiceCashiersCheck = null;
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

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceSendNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = null;
	}

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeApplicationCheckbook(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		CashiersCheckResponse aCashiersCheckResponse = null;
		CashiersCheckRequest aCashiersCheckRequest = transformManagerCheckRequest(request.clone());

		if (aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER) != null) {
			AccountingParameter accountingParameter = (AccountingParameter) aBagSPJavaOrchestration
					.get(ACCOUNTING_PARAMETER);
			if (logger.isInfoEnabled())
				logger.logInfo(accountingParameter.toString());
			aCashiersCheckRequest.setCause(accountingParameter.getCause());
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException("ERROR OBTENIENDO PARAMETROS DE TRANSACCION"));
			return Utils.returnException("ERROR OBTENIENDO PARAMETROS DE TRANSACCION");
		}

		if (aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER_COMMISSION) != null) {
			AccountingParameter accountingParameterCommssion = (AccountingParameter) aBagSPJavaOrchestration
					.get(ACCOUNTING_PARAMETER_COMMISSION);
			if (logger.isInfoEnabled())
				logger.logInfo(accountingParameterCommssion.toString());
			aCashiersCheckRequest.setCauseComi(accountingParameterCommssion.getCause());
			aCashiersCheckRequest.setServiceCost(accountingParameterCommssion.getService());
		}

		try {
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "aplicationManagerCheck: " + aCashiersCheckRequest.getUserName();
			queryName = "aplicationManagerCheck";
			IProcedureResponse aProcedureResponse = (IProcedureResponse) aBagSPJavaOrchestration
					.get(ApplicationsBaseTemplate.RESPONSE_VALIDATE_LOCAL);
			aCashiersCheckRequest.setOriginalRequest(request);
			aCashiersCheckResponse = coreServiceCashiersCheck.aplicationCashiersCheck(aCashiersCheckRequest);
			finalOffice = aCashiersCheckResponse.getOffice().toString();
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

		return transformProcedureResponse(aCashiersCheckResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceCashiersCheck", coreServiceCashiersCheck);
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

	/******************
	 * Transformación de ProcedureRequest a ManagerCheckRequest
	 ********************/

	private CashiersCheckRequest transformManagerCheckRequest(IProcedureRequest aRequest) {
		CashiersCheckRequest wManagerCheckRequest = new CashiersCheckRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@i_monto") == null ? " - @i_monto can't be null" : "";
		messageError += aRequest.readValueParam("@i_doble_autorizacion") == null
				? " - @i_doble_autorizacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_beneficiario") == null ? " - @i_beneficiario can't be null" : "";
		messageError += aRequest.readValueParam("@i_ofi_destino") == null ? " - @i_ofi_destino can't be null" : "";
		messageError += aRequest.readValueParam("@i_proposito") == null ? " - @i_proposito can't be null" : "";
		messageError += aRequest.readValueParam("@i_cod_cliente") == null ? " - @i_cod_cliente can't be null" : "";
		messageError += aRequest.readValueParam("@i_ente") == null ? " - @i_ente can't be null" : "";
		messageError += aRequest.readValueParam("@i_moneda_monto") == null ? " - @i_moneda_monto can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		Currency wCurrency = new Currency();
		Product wProduct = new Product();
		CashiersCheck wManagerCkeck = new CashiersCheck();
		Entity wEntity = new Entity();
		wManagerCheckRequest.setUserName(aRequest.readValueParam("@i_login"));
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		wProduct.setCurrency(wCurrency);
		wProduct.setProductAlias(aRequest.readValueParam("@i_producto"));
		;
		wManagerCheckRequest.setProduct(wProduct);
		wManagerCkeck.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));
		wManagerCheckRequest.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));
		wManagerCkeck.setBeneficiary(aRequest.readValueParam("@i_beneficiario"));
		wManagerCkeck.setBeneficiaryId(aRequest.readValueParam("@i_code_ben"));
		wManagerCkeck.setBeneficiaryTypeId(aRequest.readValueParam("@i_tipo_id_ben"));
		wManagerCkeck.setBeneficiaryId(aRequest.readValueParam("@i_id_ben"));
		wManagerCkeck.setAuthorizedPhoneNumber(aRequest.readValueParam("@i_tel_benef"));
		wManagerCkeck.setDestinationOfficeId(Integer.parseInt(aRequest.readValueParam("@i_ofi_destino")));
		wManagerCkeck.setAuthorizedTypeId(aRequest.readValueParam("@i_retira_tipo_id"));
		wManagerCkeck.setAuthorizedPhoneNumber(aRequest.readValueParam("@i_retira_telef"));
		wManagerCkeck.setAuthorizedId(aRequest.readValueParam("@i_retira_id"));
		wManagerCkeck.setAuthorized(aRequest.readValueParam("@i_retira_nombre"));
		wManagerCkeck.setEmail(aRequest.readValueParam("@i_retira_correo"));
		wManagerCkeck.setPurpose(aRequest.readValueParam("@i_proposito"));
		wManagerCkeck.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda_monto")));
		wManagerCheckRequest.setManagerCheck(wManagerCkeck);
		wEntity.setCodCustomer(Integer.parseInt(aRequest.readValueParam("@i_cod_cliente")));
		wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		wManagerCheckRequest.setEntity(wEntity);
		return wManagerCheckRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(CashiersCheckResponse aResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response aManagerCheckResponse ");

		if (!IsValidManagerCheckResponse(aResponse))
			return null;

		if (aResponse.getReturnCode() == 0) {
			wProcedureResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 1, aResponse.getReference().toString());
			wProcedureResponse.addParam("@o_idlote", ICTSTypes.SQLINT4, 1, aResponse.getBatchId().toString());
			wProcedureResponse.addParam("@o_autorizacion", ICTSTypes.SQLVARCHAR, 1,
					aResponse.getAuthorizationRequired());
			wProcedureResponse.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 1, aResponse.getBranchSSN().toString());
			wProcedureResponse.addParam("@o_condicion", ICTSTypes.SQLINT4, 1, aResponse.getConditionId().toString());
			wProcedureResponse.addParam("@o_oficina", ICTSTypes.SQLVARCHAR, 64, aResponse.getOffice().toString());
		} else {
			wProcedureResponse = Utils.returnException(aResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private boolean IsValidManagerCheckResponse(CashiersCheckResponse aResponse) {
		String messageError = null;
		String msgErr = null;

		messageError = aResponse.getReference() == null ? " Reference can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = aResponse.getBatchId() == null ? " BatchId can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = aResponse.getAuthorizationRequired() == null ? " AuthorizationRequired can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = aResponse.getBranchSSN() == null ? " BranchSSN can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = aResponse.getOffice() == null ? " Office can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);

		return true;
	}

	@Override
	protected ICoreService getCoreService() {
		return coreService;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	protected ICoreServer getCoreServer() {
		return coreServer;
	}

	@Override
	public ICoreServiceSendNotification getCoreServiceSendNotification() {
		return coreServiceSendNotification;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		return coreServiceMonetaryTransaction;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {

		NotificationDetail notificationDetail = new NotificationDetail();
		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date")); // comun
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));// comun
		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationDetail.setTransaccionId(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));// comun

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getAcountEmailAdress());// comun
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getOfficerEmailAdress());// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyDescription1(anOriginalRequest.readValueParam("@i_mon"));// comun
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta")); // comun
		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val")); // comun
		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto")); // comun
		// propios de la transaccion

		if (!Utils.isNull(finalOffice))
			notificationDetail.setAuxiliary1(finalOffice);
		if (!Utils.isNull(anOriginalRequest.readParam("@i_beneficiario")))
			notificationDetail.setAuxiliary2(anOriginalRequest.readValueParam("@i_beneficiario"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_retira_id")))
			notificationDetail.setAuxiliary3(anOriginalRequest.readValueParam("@i_retira_id"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_retira_nombre")))
			notificationDetail.setAuxiliary4(anOriginalRequest.readValueParam("@i_retira_nombre"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_retira_correo")))
			notificationDetail.setEmail1(anOriginalRequest.readValueParam("@i_retira_correo"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_proposito")))
			notificationDetail.setAuxiliary5(anOriginalRequest.readValueParam("@i_proposito"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyDescription2(anOriginalRequest.readValueParam("@i_mon"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_monto")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_monto"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_monto")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_monto"));
		Notification notification = new Notification();
		notification.setNotificationType("F"); // comun
		notification.setId("N48");

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));// comun
		client.setLogin(anOriginalRequest.readValueParam("@i_login"));

		Product originProduct = new Product();
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));// comun
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
