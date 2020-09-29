package com.cobiscorp.ecobis.ib.orchestration.servicepayment;

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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.payment.template.PaymentOfflineTemplate;
//import com.cobiscorp.ecobis.orchestration.core.ib.payment.template.PaymentOnlineTemplate;

@Component(name = "ServicePaymentOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ServicePaymentOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ServicePaymentOrchestationCore") })

public class ServicePaymentOrchestationCore extends PaymentOfflineTemplate {
	protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String TRN_PAGO_SERVICIOS_PUBLICOS = "1801024";
	private java.util.Properties properties;

	private static ILogger logger = LogFactory.getLogger(ServicePaymentOrchestationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		this.properties = arg0.getProperties("//property");
		if (logger.isInfoEnabled())
			logger.logInfo(" Connector Properties --> " + this.properties);
	}

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	// inyecta codigo para la validacion de la cuenta
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	// inyecta codigo para notificacion
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	///
	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}
	//

	// inyecta codigo para pago de servicio
	@Reference(referenceInterface = ICoreServicePayment.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreSericePayment", unbind = "unbindCoreSericePayment")
	protected ICoreServicePayment coreServicePayment;

	public void bindCoreSericePayment(ICoreServicePayment service) {
		coreServicePayment = service;
	}

	public void unbindCoreSericePayment(ICoreServicePayment service) {
		coreServicePayment = null;
	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return coreServer;
	}

	@Override
	protected ICoreService getCoreService() {
		// TODO Auto-generated method stub
		return coreService;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return coreServiceNotification;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected IProcedureResponse validatePreviousExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
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

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());
		notificationDetail.setProductId("18");
		if (logger.isInfoEnabled())
			logger.logInfo(aProcedureRequest.readValueParam("@i_val").toString());
		if (logger.isDebugEnabled())
			logger.logDebug(Double.parseDouble(aProcedureRequest.readValueParam("@i_val")));
		if (!Utils.isNull(aProcedureRequest.readParam("@i_val")))
			notificationDetail.setValue(aProcedureRequest.readValueParam("@i_val").toString());

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

		if (!Utils.isNull(aProcedureRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(aProcedureRequest.readValueParam("@s_ssn_branch"));

		if (!Utils.isNull(aProcedureRequest.readParam("@s_user")))
			notificationRequest.setUserBv(aProcedureRequest.readValueParam("@s_user"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_concepto")))
			notificationDetail.setNote(aProcedureRequest.readValueParam("@i_concepto"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_prod")))
			originProduct.setProductType(Integer.parseInt(aProcedureRequest.readValueParam("@i_prod")));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_cta")))
			originProduct.setProductNumber(aProcedureRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(aProcedureRequest.readParam("@i_mon"))) {
			currency.setCurrencyId(Integer.parseInt(aProcedureRequest.readValueParam("@i_mon")));
			originProduct.setCurrency(currency);
		}

		if (originProduct.getProductType() == 4)
			notification.setId("N39");
		else if (originProduct.getProductType() == 3)
			notification.setId("N30");

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(aProcedureRequest);
		notificationRequest.setOriginProduct(originProduct);
		if (logger.isInfoEnabled()) {
			logger.logInfo("ingreso en el metodo transformNotificationRequest:");
			logger.logInfo(aProcedureRequest.toString());
			logger.logInfo("logeo el objeto anOfficer:");
			logger.logInfo(anOfficer.toString());
		}
		return notificationRequest;
	}

	@Override
	protected IProcedureResponse payDestinationProduct(IProcedureRequest aProcedureRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse aPaymentServiceResponse = null;
		try {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Request payDestinationProduct GESTOPAGO");
				logger.logInfo("@i_ssn: " + aProcedureRequest.readValueParam("@i_ssn"));
				logger.logInfo("@i_ref_1: " + aProcedureRequest.readValueParam("@i_ref_1"));
				logger.logInfo("@i_ref_2: " + aProcedureRequest.readValueParam("@i_ref_2"));
				logger.logInfo("@i_ref_3: " + aProcedureRequest.readValueParam("@i_ref_3"));
				logger.logInfo("@i_ref_4: " + aProcedureRequest.readValueParam("@i_ref_4"));
				logger.logInfo("@i_ref_5: " + aProcedureRequest.readValueParam("@i_ref_5"));
				logger.logInfo("@s_date: " + aProcedureRequest.readValueParam("@s_date"));			
			}
			//PARAMETROS DE ENTRADA
			aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT1, TRN_PAGO_SERVICIOS_PUBLICOS);
			aProcedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");
			aProcedureRequest.addInputParam("@i_transaccion_ID", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ssn"));
			aProcedureRequest.addInputParam("@i_telefono", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ref_1"));
			aProcedureRequest.addInputParam("@i_id_servicio", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ref_2"));
			aProcedureRequest.addInputParam("@i_id_producto", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ref_3"));
			aProcedureRequest.addInputParam("@i_id_sucursal", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ref_4"));
			aProcedureRequest.addInputParam("@i_hora", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@s_date"));
			aProcedureRequest.addInputParam("@i_referencia", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_ref_5"));
			aProcedureRequest.addInputParam("@i_monto", ICTSTypes.SQLVARCHAR, aProcedureRequest.readValueParam("@i_val"));
		
			//PARAMETROS DE ENTRADA
			aProcedureRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_msj_respuesta", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_transaccion_ID", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_num_autorizacion", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_saldo", ICTSTypes.SQLMONEY, "0");
			aProcedureRequest.addOutputParam("@o_comision", ICTSTypes.SQLMONEY, "0");
			aProcedureRequest.addOutputParam("@o_saldo_final", ICTSTypes.SQLMONEY, "0");
			aProcedureRequest.addOutputParam("@o_comision_final", ICTSTypes.SQLMONEY, "0");
			aProcedureRequest.addOutputParam("@o_fecha", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_monto", ICTSTypes.SQLMONEY, "0");
			aProcedureRequest.addOutputParam("@o_pin", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_instrucciones", ICTSTypes.SQLVARCHAR, "X");
			aProcedureRequest.addOutputParam("@o_saldo_cliente", ICTSTypes.SQLVARCHAR, "X");
		
			//SE HACE LA LLAMADA AL CONECTOR DE GESTOPAGO
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorGestopago)");
			aProcedureRequest.setSpName("cob_procesador..sp_orq_gestopago");
			aProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, TRN_PAGO_SERVICIOS_PUBLICOS);
			aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, TRN_PAGO_SERVICIOS_PUBLICOS);
        
			//SE EJECUTA
			aPaymentServiceResponse = executeProvider(aProcedureRequest, aBagSPJavaOrchestration);	
			
			if (logger.isInfoEnabled()) {
				logger.logInfo("Response payDestinationProduct GESTOPAGO");
				logger.logInfo("@o_cod_respuesta: " + aPaymentServiceResponse.readValueParam("@o_cod_respuesta"));
				logger.logInfo("@o_msj_respuesta: " + aPaymentServiceResponse.readValueParam("@o_msj_respuesta"));
				logger.logInfo("@o_transaccion_ID: " + aPaymentServiceResponse.readValueParam("@o_transaccion_ID"));
				logger.logInfo("@o_num_autorizacion: " + aPaymentServiceResponse.readValueParam("@o_num_autorizacion"));
				logger.logInfo("@o_saldo: " + aPaymentServiceResponse.readValueParam("@o_saldo"));
				logger.logInfo("@o_comision: " + aPaymentServiceResponse.readValueParam("@o_comision"));
				logger.logInfo("@o_saldo_final: " + aPaymentServiceResponse.readValueParam("@o_saldo_final"));
				logger.logInfo("@o_fecha: " + aPaymentServiceResponse.readValueParam("@o_fecha"));
				logger.logInfo("@o_monto: " + aPaymentServiceResponse.readValueParam("@o_monto"));
				logger.logInfo("@o_pin: " + aPaymentServiceResponse.readValueParam("@o_pin"));
				logger.logInfo("@o_instrucciones: " + aPaymentServiceResponse.readValueParam("@o_instrucciones"));
				logger.logInfo("@o_saldo_cliente: " + aPaymentServiceResponse.readValueParam("@o_saldo_cliente"));
			}
			
			aPaymentServiceResponse.addParam("@o_xml_resp", ICTSTypes.SQLVARCHAR, 0, aPaymentServiceResponse.getProcedureResponseAsString());
			aPaymentServiceResponse.addParam("@o_xml_req", ICTSTypes.SQLVARCHAR, 0, aProcedureRequest.getProcedureRequestAsString());			
			aPaymentServiceResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, (String)aBagSPJavaOrchestration.get(SSN_BRANCH));
			aPaymentServiceResponse.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, (String)aBagSPJavaOrchestration.get(SSN_BRANCH));
			
			if (logger.isInfoEnabled()) {
				logger.logInfo("Response payDestinationProduct GESTOPAGO");
				logger.logInfo("@o_xml_resp: " + aPaymentServiceResponse.readValueParam("@o_xml_resp"));
				logger.logInfo("@o_xml_req: " + aPaymentServiceResponse.readValueParam("@o_xml_req"));
				logger.logInfo("@o_referencia: " + aPaymentServiceResponse.readValueParam("@o_referencia"));
				logger.logInfo("@o_ssn_branch: " + aPaymentServiceResponse.readValueParam("@o_ssn_branch"));
			}
		}
		catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("ERROR payDestinationProduct GESTOPAGO");
			}
			e.printStackTrace();
			aPaymentServiceResponse = null;			
		}
		finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("SALIENDO payDestinationProduct GESTOPAGO");
			}
		}
		//SE REGRESA RESPUESTA
		return aPaymentServiceResponse;
		/*
		PaymentServiceResponse  aPaymentServiceResponse = new PaymentServiceResponse();
		aPaymentServiceResponse.setReturnCode(0);
		aPaymentServiceResponse.setBranchSSN(Integer.parseInt((String)aBagSPJavaOrchestration.get(SSN_BRANCH)));
		aPaymentServiceResponse.setReference(Integer.parseInt((String)aBagSPJavaOrchestration.get(SSN_BRANCH)));
																												 
		if (logger.isInfoEnabled()) {
			logger.logInfo("SALE payService" + aPaymentServiceResponse.getReturnCode());
			logger.logInfo("SALE payService" + aPaymentServiceResponse.getMessage());
		}
		return transformToProcedureResponse(aPaymentServiceResponse, aBagSPJavaOrchestration);
		*/
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		@SuppressWarnings("unused")
		IProcedureResponse response = null;
		try {
			
			if (logger.isInfoEnabled())	logger.logInfo("*******CARAGANDO ORQUESTACION PAGO SERVICIOS JCOS");
			
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServer", coreServer);
			mapInterfaces.put("coreService", coreService);
			mapInterfaces.put("coreServiceNotification", coreServiceNotification);
			mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);
			mapInterfaces.put("coreServicePayment", coreServicePayment);
			Utils.validateComponentInstance(mapInterfaces);
			if (logger.isInfoEnabled())
				logger.logInfo("**************validandointerfaces");
			aBagSPJavaOrchestration.put(PAYMENT_NAME, "PAGO DE SERVICIOS");
			if (logger.isInfoEnabled())
				logger.logInfo("ORIGINAL_REQUEST" + aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
			response = executeStepsPaymentBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		IProcedureResponse responseVL = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_VALIDATE_LOCAL);

		if (response != null) {

			if (logger.isDebugEnabled())
				logger.logDebug("RESPONSE FINAL -->" + response.getProcedureResponseAsString());
			if (response.readValueParam("@o_ssn_branch") == null) {
				response.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
			}

			if (response.readValueParam("@o_referencia") == null) {
				response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
			}

			if (response.readValueParam("@o_autorizacion") == null && responseVL != null) {
				response.addParam("@o_autorizacion", ICTSTypes.SQLCHAR, 0,
						responseVL.readValueParam("@o_autorizacion"));
			}
		}else {
			response= new ProcedureResponseAS(); 
			response.setReturnCode(0);
			response.addParam("@o_autorizacion", ICTSTypes.SQLCHAR, 0,responseVL.readValueParam("@o_autorizacion"));
			response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, responseVL.readValueFieldInHeader("ssn_branch"));
			response.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, responseVL.readValueFieldInHeader("ssn_branch"));
		}

		return response;
	}

	private boolean IsValidServiceSatatementResponse(PaymentServiceResponse aPaymentServiceResponse) {
		String messageError = null;

		// messageError= aPaymentServiceResponse.getReference() == null ?
		// "Reference can't be null":"OK";
		messageError = aPaymentServiceResponse.getBranchSSN() == null ? "SsnBranch can't be null" : "OK";
		// messageError= aPaymentServiceResponse.getAuthorizationRequired() ==
		// null ? "Autorization can't be null":"OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);
		return true;
	}

	private IProcedureResponse transformToProcedureResponse(PaymentServiceResponse aPaymentServiceResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		if (logger.isInfoEnabled())
			logger.logInfo("returncode en orquesr: " + aPaymentServiceResponse.getReturnCode());

		if (aPaymentServiceResponse.getReturnCode() == 0) {

			if (!IsValidServiceSatatementResponse(aPaymentServiceResponse))
				return null;

			if (aPaymentServiceResponse.getReference() != null) {
				response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0,
						aPaymentServiceResponse.getReference().toString());
				if (logger.isInfoEnabled())
					logger.logInfo("REFERENCIA: " + "" + aPaymentServiceResponse.getReference());
			}

			if (aPaymentServiceResponse.getBranchSSN() != null) {
				response.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0,
						aPaymentServiceResponse.getBranchSSN().toString());
				if (logger.isInfoEnabled())
					logger.logInfo("SSNBRANCH: " + "" + aPaymentServiceResponse.getBranchSSN());
			}

			// if(aPaymentServiceResponse.getAuthorizationRequired() != null){
			// response.addParam("@o_autorizacion", ICTSTypes.SQLCHAR, 0,
			// aPaymentServiceResponse.getAuthorizationRequired());
			// logger.logInfo("AUTORIZACION: " + ""+
			// aPaymentServiceResponse.getBranchSSN());
			// }

		} else {
			if (aBagSPJavaOrchestration.get("ERROR_PROVEEDOR").toString() != null) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(response.getReturnCode(),
						aBagSPJavaOrchestration.get("ERROR_PROVEEDOR").toString()));
				response = Utils.returnException(response.getReturnCode(),
						aBagSPJavaOrchestration.get("ERROR_PROVEEDOR").toString());
			} else {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
						Utils.returnException(aPaymentServiceResponse.getMessages()));
				response = Utils.returnException(aPaymentServiceResponse.getMessages());
			}

			if (logger.isInfoEnabled())
				logger.logInfo("Utils.returnException(aPaymentServiceResponse.getMessages()) "
						+ Utils.returnException(aPaymentServiceResponse.getMessages()));
			// response = (IProcedureResponse)
			// aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		}

		response.setReturnCode(aPaymentServiceResponse.getReturnCode());
		if (logger.isInfoEnabled()) {
			logger.logInfo("RESPUESTA SECUENCIAL" + "" + response.getProcedureResponseAsString());
			logger.logInfo("response.setReturnCode: " + response.getReturnCode());
			logger.logInfo("response.getMessages: " + response.getMessages());
		}
		return response;
	}

	private PaymentServiceRequest transformToPaymentServiceRequest(IProcedureRequest aRequest) {
		User inUser = new User();
		Product inProduct = new Product();
		PaymentServiceRequest apaymentServiceReq = new PaymentServiceRequest();
		Currency inCurrency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug(" aRequest" + aRequest);

		if (aRequest.readValueParam("@i_prod") != null) {
			inProduct.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		}
		if (aRequest.readValueParam("@i_mon") != null) {

			inCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		}
		if (aRequest.readValueParam("@i_login") != null) {
			inUser.setName(aRequest.readValueParam("@i_login"));
		}
		if (aRequest.readValueParam("@i_cta") != null) {
			inProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		}
		// Envio de moneda del monto a pagar para compra venta implicita
		if (aRequest.readValueParam("@i_mon_pag") != null) {
			apaymentServiceReq.setReference(Integer.parseInt(aRequest.readValueParam("@i_mon_pag")));
		} else {
			apaymentServiceReq.setReference(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		}
		if (aRequest.readValueParam("@i_concepto") != null) {
			apaymentServiceReq.setContractName(aRequest.readValueParam("@i_concepto"));
		}

		if (aRequest.readValueParam("@i_canal") != null) {
			inUser.setServiceId(Integer.parseInt(aRequest.readValueParam("@i_canal")));
		}

		if (aRequest.readValueParam("@i_val") != null) {
			apaymentServiceReq.setAmount(new BigDecimal(aRequest.readValueParam("@i_val")));
		}
		if (aRequest.readValueParam("@i_convenio") != null) {
			apaymentServiceReq.setContractId(Integer.parseInt(aRequest.readValueParam("@i_convenio")));
		}
		if (aRequest.readValueParam("@i_doble_autorizacion") != null) {
			apaymentServiceReq.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));
		}
		if (aRequest.readValueParam("@i_monenda_desc") != null) {
			inCurrency.setCurrencyDescription(aRequest.readValueParam("@i_monenda_desc"));
		}
		if (aRequest.readValueParam("@i_ente") != null) {
			inUser.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		}
		if (aRequest.readValueParam("@i_producto") != null) {
			inProduct.setProductNemonic(aRequest.readValueParam("@i_producto"));
		}
		if (aRequest.readValueParam("@i_ejecuta_consulta") != null) {
			apaymentServiceReq.setNeedsQuery(aRequest.readValueParam("@i_ejecuta_consulta"));
		}
		if (aRequest.readValueParam("@i_llave") != null) {
			apaymentServiceReq.setThridPartyServiceKey(aRequest.readValueParam("@i_llave"));
		}
		if (aRequest.readValueParam("@i_num_doc") != null) {
			apaymentServiceReq.setDocumentId(aRequest.readValueParam("@i_num_doc"));
		}
		if (aRequest.readValueParam("@i_tipo_interfaz") != null) {
			apaymentServiceReq.setInterfaceType(aRequest.readValueParam("@i_tipo_interfaz"));
		}
		if (aRequest.readValueParam("@i_ref_1") != null) {
			apaymentServiceReq.setRef1(aRequest.readValueParam("@i_ref_1"));
		}
		if (aRequest.readValueParam("@i_ref_2") != null) {
			apaymentServiceReq.setRef2(aRequest.readValueParam("@i_ref_2"));
		}
		if (aRequest.readValueParam("@i_ref_3") != null) {
			apaymentServiceReq.setRef3(aRequest.readValueParam("@i_ref_3"));
		}
		if (aRequest.readValueParam("@i_ref_4") != null) {
			apaymentServiceReq.setRef4(aRequest.readValueParam("@i_ref_4"));
		}
		if (aRequest.readValueParam("@i_ref_5") != null) {
			apaymentServiceReq.setRef5(aRequest.readValueParam("@i_ref_5"));
		}

		if (logger.isDebugEnabled())
			logger.logDebug(" ref 6 " + aRequest.readValueParam("@i_ref_6"));
		if ("L".equalsIgnoreCase(String.valueOf(apaymentServiceReq.getInterfaceType()))) {

			String itemPago = aRequest.readValueParam("@i_ref_6") + "," + aRequest.readValueParam("@i_val");
			if (logger.isDebugEnabled())
				logger.logDebug("itemPago" + itemPago.toString());
			apaymentServiceReq.setRef6(itemPago);
		} else {
			if (aRequest.readValueParam("@i_ref_6") != null) {
				apaymentServiceReq.setRef6(aRequest.readValueParam("@i_ref_6"));
			}
		}

		if (aRequest.readValueParam("@i_ref_7") != null) {
			apaymentServiceReq.setRef7(aRequest.readValueParam("@i_ref_7"));
		}
		if (aRequest.readValueParam("@i_ref_8") != null) {
			apaymentServiceReq.setRef8(aRequest.readValueParam("@i_ref_8"));
		}
		if (aRequest.readValueParam("@i_ref_9") != null) {
			apaymentServiceReq.setRef9(aRequest.readValueParam("@i_ref_9"));
		}
		if (aRequest.readValueParam("@i_ref_10") != null) {
			apaymentServiceReq.setRef10(aRequest.readValueParam("@i_ref_10"));
		}
		if (aRequest.readValueParam("@i_ref_11") != null) {
			apaymentServiceReq.setRef11(aRequest.readValueParam("@i_ref_11"));
		}
		if (aRequest.readValueParam("@i_ref_12") != null) {
			apaymentServiceReq.setRef12(aRequest.readValueParam("@i_ref_12"));
		}
		if (aRequest.readValueParam("@i_secuencial") != null) {
			apaymentServiceReq.setInvoicingBaseId(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		}
		if (aRequest.readValueParam("@i_secuencial") != null) {
			apaymentServiceReq.setInvoicingBaseId(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		}
		if (aRequest.readValueParam("@i_tipo_doc") != null) {
			apaymentServiceReq.setDocumentType(aRequest.readValueParam("@i_tipo_doc"));
		}

		if (aRequest.readValueParam("@s_ssn_branch") != null) {
			apaymentServiceReq.setReferenceNumberBranch(aRequest.readValueParam("@s_ssn_branch"));
		}

		if (aRequest.readValueParam("@s_ssn") != null) {
			apaymentServiceReq.setReferenceNumber(aRequest.readValueParam("@s_ssn"));
		}

		inProduct.setCurrency(inCurrency);
		apaymentServiceReq.setOriginalRequest(aRequest);
		apaymentServiceReq.setInProduct(inProduct);
		apaymentServiceReq.setInUser(inUser);
		if (logger.isDebugEnabled())
			logger.logDebug("apaymentServiceReq" + apaymentServiceReq);

		return apaymentServiceReq;
	}

}
