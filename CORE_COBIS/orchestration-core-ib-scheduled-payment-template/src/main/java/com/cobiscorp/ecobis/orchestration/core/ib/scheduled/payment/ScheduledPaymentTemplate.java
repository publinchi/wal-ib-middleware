package com.cobiscorp.ecobis.orchestration.core.ib.scheduled.payment;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceScheduledPayments;

@Component(name = "ScheduledPaymentTemplate", immediate = false)
@Service(value = { ICoreServiceScheduledPayments.class })
@Properties(value = { @Property(name = "service.description", value = "ScheduledPaymentTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ScheduledPaymentTemplate") })

public class ScheduledPaymentTemplate extends SPJavaOrchestrationBase implements ICoreServiceScheduledPayments {
	private static final String CLASS_NAME = " >-----> ScheduledPaymentTemplate";
	private static ILogger logger = LogFactory.getLogger(ScheduledPaymentTemplate.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

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
		return null;
	}

	public ScheduledPaymentResponse executeScheduledPayment(ScheduledPaymentRequest aScheduledPaymentServiceRequest)
			throws CTSServiceException, CTSInfrastructureException {
		/** Guardar los datos de pagos programados **/
		if (logger.isInfoEnabled()) {
			logger.logInfo("Valores que llegan a metodo executeScheduledPayment en implementacion : "
					+ aScheduledPaymentServiceRequest);
			logger.logInfo("Se inicia metodo executeSchedulePayment en implementacion");
		}
		return this.saveScheduledPayment(aScheduledPaymentServiceRequest);
	}

	public ScheduledPaymentResponse saveScheduledPayment(ScheduledPaymentRequest aScheduledPaymentServiceRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Valores que llegan a metodo saveScheduledPayment en implementacion : "
					+ aScheduledPaymentServiceRequest);
			logger.logInfo(CLASS_NAME + " Iniciando Servicio saveScheduledPayment");
		}
		IProcedureRequest executionRequest = new ProcedureRequestAS();
		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN,
				aScheduledPaymentServiceRequest.getTransaction().toString());
		executionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				aScheduledPaymentServiceRequest.getReferenceNumber());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				aScheduledPaymentServiceRequest.getReferenceNumberBranch());
		executionRequest.setSpName("cob_bvirtual..sp_pago_recurrente_bv");

		/**
		 * setear el numero del servicio de acuerdo al tipo (T -> Transferencia,
		 * P->Prestamo, S-> Sevicio)
		 **/
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4,
				aScheduledPaymentServiceRequest.getTransaction().toString());
		executionRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4,
				aScheduledPaymentServiceRequest.getClient().getIdCustomer());

		if (aScheduledPaymentServiceRequest.getId() != null) {
			executionRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getId().toString());
		}
		executionRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getClient().getLogin());
		executionRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getDebitProduct().getProductNumber());
		executionRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				aScheduledPaymentServiceRequest.getDebitProduct().getCurrency().getCurrencyId().toString());
		executionRequest.addInputParam("@i_prod", ICTSTypes.SQLINT1,
				aScheduledPaymentServiceRequest.getDebitProduct().getProductId().toString());
		executionRequest.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, aScheduledPaymentServiceRequest.getType());
		executionRequest.addInputParam("@i_codigo", ICTSTypes.SQLINT4,
				aScheduledPaymentServiceRequest.getCode().toString());
		executionRequest.addInputParam("@i_cuenta_cr", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getCreditProduct().getProductNumber());
		executionRequest.addInputParam("@i_moneda_cr", ICTSTypes.SQLINT2,
				aScheduledPaymentServiceRequest.getCreditProduct().getCurrency().getCurrencyId().toString());
		executionRequest.addInputParam("@i_producto_cr", ICTSTypes.SQLINT2,
				aScheduledPaymentServiceRequest.getCreditProduct().getProductId().toString());
		executionRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY,
				aScheduledPaymentServiceRequest.getAmount().toString());
		executionRequest.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getInitialDate());
		executionRequest.addInputParam("@i_num_pagos", ICTSTypes.SQLINT4,
				aScheduledPaymentServiceRequest.getPaymentsNumber().toString());
		executionRequest.addInputParam("@i_num_dias", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getFrecuencyId());
		executionRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getConcept());
		executionRequest.addInputParam("@i_item", ICTSTypes.SQLVARCHAR, aScheduledPaymentServiceRequest.getItem());
		executionRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aScheduledPaymentServiceRequest.getLogin());
		executionRequest.addInputParam("@i_opcion", ICTSTypes.SQLVARCHAR, aScheduledPaymentServiceRequest.getOption());
		executionRequest.addInputParam("@i_notificar", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getReceiveNotification());
		executionRequest.addInputParam("@i_dias_notif", ICTSTypes.SQLINT2,
				aScheduledPaymentServiceRequest.getDayToNotify().toString());
		executionRequest.addInputParam("@i_reint_cobro", ICTSTypes.SQLCHAR,
				aScheduledPaymentServiceRequest.getRecoveryRetryFailed());
		executionRequest.addInputParam("@i_fech_prox_cobr", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getNextPaymentDate());
		executionRequest.addInputParam("@i_beneficiario", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getBeneficiaryName());
		executionRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getOperation());
		executionRequest.addInputParam("@i_llave", ICTSTypes.SQLVARCHAR, aScheduledPaymentServiceRequest.getKey());
		executionRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getFundsSource());
		executionRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR,
				aScheduledPaymentServiceRequest.getFundsUse());

		/* para pago de servicio se agregan otros parametros **/
		if ("S".equals(aScheduledPaymentServiceRequest.getType())) {
			executionRequest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
					aScheduledPaymentServiceRequest.getPaymentService().getContractId().toString());
			executionRequest.addInputParam("@i_tipo_interfaz", ICTSTypes.SQLCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getInterfaceType());
			executionRequest.addInputParam("@i_tipo_doc", ICTSTypes.SQLCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getDocumentType());
			executionRequest.addInputParam("@i_num_doc", ICTSTypes.SQLINT4,
					aScheduledPaymentServiceRequest.getPaymentService().getDocumentId());
			executionRequest.addInputParam("@i_ref1", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef1());
			executionRequest.addInputParam("@i_ref2", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef2());
			executionRequest.addInputParam("@i_ref3", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef3());
			executionRequest.addInputParam("@i_ref4", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef4());
			executionRequest.addInputParam("@i_ref5", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef5());
			executionRequest.addInputParam("@i_ref6", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef6());
			executionRequest.addInputParam("@i_ref7", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef7());
			executionRequest.addInputParam("@i_ref8", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef8());
			executionRequest.addInputParam("@i_ref9", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef9());
			executionRequest.addInputParam("@i_ref10", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef10());
			executionRequest.addInputParam("@i_ref11", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef11());
			executionRequest.addInputParam("@i_ref12", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getRef12());
			executionRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR,
					aScheduledPaymentServiceRequest.getPaymentService().getCategoryId());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + executionRequest.getProcedureRequestAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("*** Request a enviar: " + executionRequest.toString());
		IProcedureResponse response = executeCoreBanking(executionRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finalizando Servicio saveScheduledPayment en implementacion");
		return transformToScheduledPaymentResponse(response);
	}

	private ScheduledPaymentResponse transformToScheduledPaymentResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo("*** INGRESO A transformToScheduledPaymentResponse ***");
		ScheduledPaymentResponse aScheduledPaymentResponse = new ScheduledPaymentResponse();

		if (aProcedureResponse.readFieldInHeader("ssn_branch") != null) {
			aScheduledPaymentResponse
					.setBranchSSN(Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn_branch").toString()));
			// en la referencia se setea tambien el ssn_branch
			aScheduledPaymentResponse
					.setReference(Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn_branch").toString()));
		}
		if (logger.isInfoEnabled())
			logger.logInfo(aScheduledPaymentResponse.toString());

		Message[] messages = Utils.returnArrayMessage(aProcedureResponse);
		aScheduledPaymentResponse.setMessages(messages);
		aScheduledPaymentResponse.setReturnCode(aProcedureResponse.getReturnCode());
		if (aProcedureResponse.getReturnCode() == 0) {
			aScheduledPaymentResponse.setSuccess(true);
		} else {
			aScheduledPaymentResponse.setSuccess(false);
		}

		return aScheduledPaymentResponse;
	}
}
