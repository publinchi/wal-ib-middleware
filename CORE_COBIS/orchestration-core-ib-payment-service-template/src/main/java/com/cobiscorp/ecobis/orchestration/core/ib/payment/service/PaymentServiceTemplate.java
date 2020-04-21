package com.cobiscorp.ecobis.orchestration.core.ib.payment.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment;

@Component(name = "PaymentServiceTemplate", immediate = false)
@Service(value = { ICoreServicePayment.class })
@Properties(value = { @Property(name = "service.description", value = "PaymentServiceTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PaymentServiceTemplate") })

public class PaymentServiceTemplate extends SPJavaOrchestrationBase implements ICoreServicePayment {
	private static final String CLASS_NAME = " >-----> PaymentServiceTemplate";
	private static ILogger logger = LogFactory.getLogger(PaymentServiceTemplate.class);
	private String codeError;
	boolean errorWS = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment#
	 * payService(com.cobiscorp.ecobis.ib.application.dtos.
	 * PaymentServiceRequest)
	 */
	@Override
	public PaymentServiceResponse payService(PaymentServiceRequest aPaymentServiceRequest,
			java.util.Properties properties, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {

		PaymentServiceResponse paymentServiceResponse = new PaymentServiceResponse();
		PaymentServiceResponse paymentServiceResponseRec = new PaymentServiceResponse();
		IProcedureResponse response = null;
		if (logger.isInfoEnabled())
			logger.logInfo(
					CLASS_NAME + "Iniciando Validacion del tipo de convenio payService" + aPaymentServiceRequest);
		if ("B".equalsIgnoreCase(String.valueOf(aPaymentServiceRequest.getInterfaceType()))) {
			response = validationAmmount(aPaymentServiceRequest);
			if (response.getReturnCode() == 0) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "ejecuta el metodo executePayService");
				paymentServiceResponse = this.executePayService(aPaymentServiceRequest);
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "respuesta del  metodo executePayService: "
							+ paymentServiceResponse.toString());
			} else {
				Message[] messages = Utils.returnArrayMessage(response);
				paymentServiceResponse.setMessages(messages);
				paymentServiceResponse.setReturnCode(response.getReturnCode());
				paymentServiceResponse.setSuccess(false);
			}
		} else if ("L".equalsIgnoreCase(String.valueOf(aPaymentServiceRequest.getInterfaceType()))) {
			paymentServiceResponse = this.payOnline(aPaymentServiceRequest, properties, aBagSPJavaOrchestration);
			if (paymentServiceResponse.getReturnCode() == 0) {
				paymentServiceResponseRec = this.executePayService(aPaymentServiceRequest);
				paymentServiceResponse = paymentServiceResponseRec;
			}
		} else if ("N".equalsIgnoreCase(String.valueOf(aPaymentServiceRequest.getInterfaceType()))) {
			paymentServiceResponse = this.executePayService(aPaymentServiceRequest);
		}
		if (logger.isInfoEnabled())
			logger.logInfo("Respuesta de la orquestacion payService: " + paymentServiceResponse.getReturnCode());
		return paymentServiceResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment#
	 * validationAmmount(com.cobiscorp.ecobis.ib.application.dtos.
	 * PaymentServiceRequest)
	 */
	@Override
	public IProcedureResponse validationAmmount(PaymentServiceRequest aPaymentServiceRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio validationAmmount");

		// CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest executionRequest = new ProcedureRequestAS();
		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801035");
		executionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentServiceRequest.getReferenceNumber());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentServiceRequest.getReferenceNumberBranch());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		executionRequest.setSpName("cob_remesas..sp_valida_monto_bv");
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1801035");
		executionRequest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
				aPaymentServiceRequest.getContractId().toString());
		executionRequest.addInputParam("@i_identificacion", ICTSTypes.SQLVARCHAR,
				aPaymentServiceRequest.getDocumentType());
		executionRequest.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getInUser().getName());
		executionRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				aPaymentServiceRequest.getInvoicingBaseId().toString());
		executionRequest.addInputParam("@i_busqueda1", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getDocumentId());
		executionRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, aPaymentServiceRequest.getAmount().toString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + executionRequest.getProcedureRequestAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("************************* Request a enviar: " + executionRequest.toString());
		IProcedureResponse response = executeCoreBanking(executionRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment#
	 * payOnline(com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest)
	 */
	@Override
	public PaymentServiceResponse payOnline(PaymentServiceRequest aPaymentServiceRequest,
			java.util.Properties properties, Map<String, Object> aBag)
			throws CTSServiceException, CTSInfrastructureException {
		
		//inicio código Dummy de pago onnline, , se debe de borrar para que se ejecute contra el webservice
		PaymentServiceResponse response = new PaymentServiceResponse();
		response.setSuccess(true);
		response.setReference(123445);
		response.setBranchSSN(123445);
		response.setReturnCode(0);
		
		return response;
		
		//fin código Dummy de pago onnline

		//se debe de descomentar el código de abajo para que funcione contra el webservice
/*		IProcedureResponse wObtenerCriteriosParaModuloResp;
		IProcedureRequest wPayOnlineRequest = initProcedureRequest(aPaymentServiceRequest.getOriginalRequest());
		wPayOnlineRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "693");
		wPayOnlineRequest.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "693");
		wPayOnlineRequest.addInputParam("@i_id_operativo", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef12());
		wPayOnlineRequest.addInputParam("@i_nrooperacion", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef2());
		wPayOnlineRequest.addInputParam("@i_fechaoperativa", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef10());
		wPayOnlineRequest.addInputParam("@i_codmodulo", ICTSTypes.SYBVARCHAR,
				aPaymentServiceRequest.getContractId().toString());
		wPayOnlineRequest.addInputParam("@i_cuenta", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef3().toString());
		wPayOnlineRequest.addInputParam("@i_servicio", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef4());
		wPayOnlineRequest.addInputParam("@i_nombrefac", ICTSTypes.SYBVARCHAR,
				aPaymentServiceRequest.getRef5().toString());
		wPayOnlineRequest.addInputParam("@i_nitfac", ICTSTypes.SYBVARCHAR, aPaymentServiceRequest.getRef8().toString());
		wPayOnlineRequest.addInputParam("@i_direnvio", ICTSTypes.SYBVARCHAR,
				aPaymentServiceRequest.getRef7().toString());
		wPayOnlineRequest.addInputParam("@i_itempago", ICTSTypes.SYBVARCHAR,
				aPaymentServiceRequest.getRef6().toString());
		wPayOnlineRequest.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		wPayOnlineRequest.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "            ");

		wPayOnlineRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) properties.get("HEADER_TIMEOUT")));
		wPayOnlineRequest.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) properties.get("HEADER_CATALOG_PROVIDER")));
		codeError = ((String) properties.get("CODE_ERROR_SESSION"));
		aBag.put(ICISSPBaseOrchestration.CONNECTOR_TYPE, ((String) properties.get("CONNECTOR_TYPE")));
		wObtenerCriteriosParaModuloResp = executeProvider(wPayOnlineRequest, aBag);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + wObtenerCriteriosParaModuloResp.getProcedureResponseAsString());
		}

		if (!wObtenerCriteriosParaModuloResp.readValueParam("@o_coderror").equals("0")) {
			wObtenerCriteriosParaModuloResp
					.setReturnCode(Integer.valueOf(wObtenerCriteriosParaModuloResp.readValueParam("@o_coderror")));
			if (logger.isInfoEnabled())
				logger.logInfo("*** error mensaje*** " + wObtenerCriteriosParaModuloResp.readValueParam("@o_mensaje"));
			aBag.put("ERROR_PROVEEDOR", wObtenerCriteriosParaModuloResp.readValueParam("@o_mensaje"));
			errorWS = true;
		} else
			errorWS = false;

		PaymentServiceResponse mcResponse = transformToPaymentServiceResponse(wObtenerCriteriosParaModuloResp);
		return mcResponse;
*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment#
	 * executePayService(com.cobiscorp.ecobis.ib.application.dtos.
	 * PaymentServiceRequest)
	 */
	@Override
	public PaymentServiceResponse executePayService(PaymentServiceRequest aPaymentServiceRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Iniciando Servicio executePayService"
					+ aPaymentServiceRequest.getReferenceNumberBranch());
			logger.logInfo(
					CLASS_NAME + "Iniciando Servicio executePayService " + aPaymentServiceRequest.getReferenceNumber());
		}
		IProcedureRequest executionRequest = new ProcedureRequestAS();
		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801035");
		executionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentServiceRequest.getReferenceNumber());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentServiceRequest.getReferenceNumberBranch());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		executionRequest.setSpName("cob_remesas..sp_recaudo_bv");
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1801035");
		executionRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				aPaymentServiceRequest.getInProduct().getCurrency().getCurrencyId().toString());
		executionRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1,
				aPaymentServiceRequest.getInUser().getServiceId().toString());
		executionRequest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
				aPaymentServiceRequest.getContractId().toString());
		executionRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				aPaymentServiceRequest.getInUser().getEntityId().toString());
		executionRequest.addInputParam("@i_nom_cliente", ICTSTypes.SQLVARCHAR,
				aPaymentServiceRequest.getInUser().getName());
		executionRequest.addInputParam("@i_cta_debito", ICTSTypes.SQLVARCHAR,
				aPaymentServiceRequest.getInProduct().getProductNumber());
		executionRequest.addInputParam("@i_prod_cta_debito", ICTSTypes.SQLVARCHAR,
				aPaymentServiceRequest.getInProduct().getProductNemonic());
		executionRequest.addInputParam("@i_val_cta", ICTSTypes.SQLMONEY, aPaymentServiceRequest.getAmount().toString());
		executionRequest.addInputParam("@i_tipo_doc", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getDocumentType());
		executionRequest.addInputParam("@i_llave", ICTSTypes.SQLVARCHAR,
				aPaymentServiceRequest.getThridPartyServiceKey());
		executionRequest.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getDocumentId());
		executionRequest.addInputParam("@i_ref_1", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef1());
		executionRequest.addInputParam("@i_ref_2", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef2());
		executionRequest.addInputParam("@i_ref_3", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef3());
		executionRequest.addInputParam("@i_ref_4", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef4());
		executionRequest.addInputParam("@i_ref_5", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef5());
		executionRequest.addInputParam("@i_ref_6", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef6());
		executionRequest.addInputParam("@i_ref_7", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef7());
		executionRequest.addInputParam("@i_ref_8", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef8());
		executionRequest.addInputParam("@i_ref_9", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef9());
		executionRequest.addInputParam("@i_ref_10", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef10());
		executionRequest.addInputParam("@i_ref_11", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef11());
		executionRequest.addInputParam("@i_ref_12", ICTSTypes.SQLVARCHAR, aPaymentServiceRequest.getRef12());
		executionRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				aPaymentServiceRequest.getInvoicingBaseId().toString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + executionRequest.getProcedureRequestAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("************************* Request a enviar: " + executionRequest.toString());
		IProcedureResponse response = executeCoreBanking(executionRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		if (logger.isInfoEnabled())
			logger.logInfo("Response antes de transformarlo ==> " + response.getReturnCode());
		return transformToPaymentServiceResponse(response);

	}

	private PaymentServiceResponse transformToPaymentServiceResponse(IProcedureResponse aProcedureResponse) {
		PaymentServiceResponse aPaymentServiceResponse = new PaymentServiceResponse();
		if (!errorWS) {
			if (aProcedureResponse.readFieldInHeader("ssn_branch") != null) {
				if (logger.isInfoEnabled())
					logger.logInfo("=============>>>>> INGRESO A transformToPaymentServiceResponse dentro de IF "
							+ Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn_branch").toString()));
				aPaymentServiceResponse.setBranchSSN(
						Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn_branch").toString()));
				aPaymentServiceResponse.setReference(
						Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn_branch").toString()));
			}
		} else {
			aPaymentServiceResponse.setReturnCode(aProcedureResponse.getReturnCode());
		}

		if (aProcedureResponse.getReturnCode() != 0) {
			Message[] messages = Utils.returnArrayMessage(aProcedureResponse);
			aPaymentServiceResponse.setMessages(messages);
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			aPaymentServiceResponse.setSuccess(true);
		} else {
			aPaymentServiceResponse.setSuccess(false);
			Message[] messages = Utils.returnArrayMessage(aProcedureResponse);
			aPaymentServiceResponse.setMessages(messages);
		}
		aPaymentServiceResponse.setReturnCode(aProcedureResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo(" aPaymentServiceResponse" + aPaymentServiceResponse.toString());
		return aPaymentServiceResponse;
	}
}
