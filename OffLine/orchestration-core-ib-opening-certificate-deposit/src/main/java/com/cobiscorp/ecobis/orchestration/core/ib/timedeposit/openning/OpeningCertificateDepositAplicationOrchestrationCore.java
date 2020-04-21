/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.timedeposit.openning;

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
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.openings.OpeningsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BeneficiaryCertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Rate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceOpeningCertificateDeposit;
import com.cobiscorp.ecobis.orchestration.core.ib.opening.template.OpeningOfflineTemplate;

/**
 * @author jveloz
 *
 */
@Component(name = "OpeningCertificateDepositAplicationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "OpeningCertificateDepositAplicationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "OpeningCertificateDepositAplicationOrchestrationCore") })
public class OpeningCertificateDepositAplicationOrchestrationCore extends OpeningOfflineTemplate {

	@Reference(referenceInterface = ICoreServiceOpeningCertificateDeposit.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceOpeningCertificateDeposit", unbind = "unbindCoreServiceOpeningCertificateDeposit")
	protected ICoreServiceOpeningCertificateDeposit coreServiceOpeningCertificateDeposit;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	ILogger logger = this.getLogger();

	/**
	 * Instance ServiceOpeningCertificateDeposit Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceOpeningCertificateDeposit(ICoreServiceOpeningCertificateDeposit service) {
		coreServiceOpeningCertificateDeposit = service;
	}

	/**
	 * Deleting ServiceOpeningCertificateDeposit Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceOpeningCertificateDeposit(ICoreServiceOpeningCertificateDeposit service) {
		coreServiceOpeningCertificateDeposit = null;
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
	 * Deleting ServiceNotification Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceNotification service) {
		coreServiceNotification = null;
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
	public void unbindCoreServer(ICoreServer service) {
		coreService = null;
	}

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
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
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return coreServer;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return coreServiceNotification;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IProcedureResponse executeOpening(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;

		CertificateDepositResponse wCertificateDepositResponse = null;
		CertificateDepositRequest wCertificateDepositRequest = transformCertificateDepositRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + wCertificateDepositRequest.getAuthorizationRequired());

		try {
			if (logger.isDebugEnabled())
				logger.logInfo("executeQuery Opening Certificat Deposit");
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "aplicationOpeningCertificateDeposit: " + wCertificateDepositRequest.getUserName();
			IProcedureResponse aProcedureResponse = (IProcedureResponse) aBagSPJavaOrchestration
					.get(OpeningsBaseTemplate.RESPONSE_VALIDATE_LOCAL);
			wCertificateDepositRequest.setOriginalRequest(request);
			wCertificateDepositResponse = coreServiceOpeningCertificateDeposit
					.aplicationOpenningCertificateDeposit(wCertificateDepositRequest);
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

		return transformProcedureResponse(wCertificateDepositResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		try {
			// Valida Inyección de dependencias
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServer", coreServer);
			mapInterfaces.put("coreService", coreService);
			mapInterfaces.put("coreServiceNotification", coreServiceNotification);
			mapInterfaces.put("coreServiceOpeningCertificateDeposit", coreServiceOpeningCertificateDeposit);
			mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);

			Utils.validateComponentInstance(mapInterfaces);

			aBagSPJavaOrchestration.put("OPENING NAME", "APERTURA DE DPF");
			response = executeStepsOpeningBase(anOriginalRequest, aBagSPJavaOrchestration);
			// ogger.logInfo("Sale de executeStepsOpeningBase
			// "+response.getProcedureResponseAsString());
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	/******************
	 * Transformación de ProcedureRequest a CertificateDepositRequest
	 ********************/

	private CertificateDepositRequest transformCertificateDepositRequest(IProcedureRequest aRequest) {
		if (logger.isDebugEnabled()) {
			logger.logInfo("transformCertificateDepositRequest " + aRequest);
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		}
		String messageError = null;
		messageError = aRequest.readValueParam("@i_ente") == null ? " - @i_ente can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_doble_autorizacion") == null
				? " - @i_doble_autorizacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";// **********
		messageError += aRequest.readValueParam("@i_moneda") == null ? " - @i_moneda can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";// **********
		messageError += aRequest.readValueParam("@i_pro_debito") == null ? " - @i_pro_debito can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";// ********

		messageError += aRequest.readValueParam("@i_capitaliza") == null ? " - @i_capitaliza can't be null" : "";
		messageError += aRequest.readValueParam("@i_plazo") == null ? " - @i_plazo can't be null" : "";
		messageError += aRequest.readValueParam("@i_monto") == null ? " - @i_monto can't be null" : "";
		messageError += aRequest.readValueParam("@i_nemonico") == null ? " - @i_nemonico can't be null" : "";
		messageError += aRequest.readValueParam("@i_tasa") == null ? " - @i_tasa can't be null" : "";
		messageError += aRequest.readValueParam("@i_fecha_valor") == null ? " - @i_fecha_valor can't be null" : "";
		messageError += aRequest.readValueParam("@i_dia_pago") == null ? " - @i_dia_pago can't be null" : "";
		messageError += aRequest.readValueParam("@i_forma_pago") == null ? " - @i_forma_pago can't be null" : "";
		messageError += aRequest.readValueParam("@i_mail") == null ? " - @i_mail can't be null" : "";
		messageError += aRequest.readValueParam("@i_oficina") == null ? " - @i_oficina can't be null" : "";
		messageError += aRequest.readValueParam("@i_periodicidad") == null ? " - @i_periodicidad can't be null" : "";

		/*
		 * messageError += aRequest.readValueParam("@i_cedula1") == null ?
		 * " - @i_cedula1 can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_nombre1") == null ?
		 * " - @i_nombre1 can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_p_apellido1") == null ?
		 * " - @i_p_apellido1 can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_parentesco1") == null ?
		 * " - @i_parentesco1 can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_porcentaje1") == null ?
		 * " - @i_porcentaje1 can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_s_apellido1") == null ?
		 * " - @i_s_apellido1 can't be null":"";
		 */

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		Currency wCurrency = new Currency();
		Product wProduct = new Product();
		Entity wEntity = new Entity();
		CertificateDeposit wCertificateDeposit = new CertificateDeposit();
		Rate wRate = new Rate();
		CertificateDepositRequest wCertificateDepositRequest = new CertificateDepositRequest();
		/*
		 * BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit1= new
		 * BeneficiaryCertificateDeposit(); BeneficiaryCertificateDeposit
		 * wBeneficiaryCertificateDeposit2= new BeneficiaryCertificateDeposit();
		 * BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit3= new
		 * BeneficiaryCertificateDeposit(); BeneficiaryCertificateDeposit
		 * wBeneficiaryCertificateDeposit4= new BeneficiaryCertificateDeposit();
		 */

		wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		wCertificateDepositRequest.setEntity(wEntity);
		wCertificateDepositRequest.setUserName(aRequest.readValueParam("@i_login"));
		wCertificateDepositRequest.setAuthorizationRequired("@i_doble_autorizacion");
		//
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta_debito"));
		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));
		wProduct.setCurrency(wCurrency);
		wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_pro_debito")));
		// wProduct.setProductAlias(aRequest.readValueParam("@i_producto"));
		wCertificateDepositRequest.setProduct(wProduct);
		//
		wCertificateDeposit.setCapitalize(aRequest.readValueParam("@i_capitaliza"));
		wCertificateDeposit.setTerm(Integer.parseInt(aRequest.readValueParam("@i_plazo")));
		wCertificateDeposit.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));
		wCertificateDeposit.setNemonic(aRequest.readValueParam("@i_nemonico"));
		wRate.setRate(Double.parseDouble(aRequest.readValueParam("@i_tasa")));
		wCertificateDeposit.setRate(wRate);
		wCertificateDeposit.setProcessDate(aRequest.readValueParam("@i_fecha_valor"));
		wCertificateDeposit.setPayDay(Integer.parseInt(aRequest.readValueParam("@i_dia_pago")));
		wCertificateDeposit.setMethodOfPayment(aRequest.readValueParam("@i_forma_pago"));
		wCertificateDeposit.setMail(aRequest.readValueParam("@i_mail"));
		wCertificateDeposit.setOffice(aRequest.readValueParam("@i_oficina"));
		wCertificateDeposit.setPeriodicityId(aRequest.readValueParam("@i_periodicidad"));
		wCertificateDepositRequest.setCertificateDeposit(wCertificateDeposit);
		return wCertificateDepositRequest;

	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(CertificateDepositResponse aResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse ");

		if (aResponse.getReturnCode() != 0) {
			wProcedureResponse = Utils.returnException(aResponse.getMessages());
		} else {
			if (!IsValidCertificateDepositResponse(aResponse)) {
				wProcedureResponse.addParam("@o_retorno", ICTSTypes.SQLVARCHAR, 1, "0");
			} else {
				wProcedureResponse.addParam("@o_retorno", ICTSTypes.SQLVARCHAR, 1, aResponse.getOperationNumber());
			}
			wProcedureResponse.addParam("@o_body", ICTSTypes.SQLVARCHAR, 1, aResponse.getBody());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	/*************************
	 * Validate CertificateDepositResponse
	 ***************************/
	private boolean IsValidCertificateDepositResponse(CertificateDepositResponse aResponse) {
		String messageError = null;
		String msgErr = null;

		messageError = aResponse.getOperationNumber() == null ? " Operation Number can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);

		return true;
	}

}
