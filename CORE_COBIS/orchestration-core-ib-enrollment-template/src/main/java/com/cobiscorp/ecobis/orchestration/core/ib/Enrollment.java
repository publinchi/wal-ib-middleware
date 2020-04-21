package com.cobiscorp.ecobis.orchestration.core.ib;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EnrollmentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnrollmentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnrollment;

@Component(name = "Enrollment", immediate = false)
@Service(value = { ICoreServiceEnrollment.class })
@Properties(value = { @Property(name = "service.description", value = "Enrollment"), @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "Enrollment") })
public class Enrollment extends SPJavaOrchestrationBase implements ICoreServiceEnrollment {

	private static final String TRN_1890022 = "1890022";
	private static final String TRN_1890023 = "1890023";
	private static ILogger logger = LogFactory.getLogger(Enrollment.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";

	@Override
	public EnrollmentResponse executeEnrollment(Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia executeEnrollment");
		}

		// VALIDACION CENTRAL
		EnrollmentResponse wResponse = this.validateCustomer(aBagSPJavaOrchestration);
		if (wResponse.getSuccess()) {
			// VALIDACION Y GUARDAR EN LOCAL
			wResponse = this.execute(aBagSPJavaOrchestration);
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza executeEnrollment");
		}
		return wResponse;
	}

	/**
	 * Ejecuta proceso de enrolamiento en servidor local
	 * 
	 * @param request
	 * @return
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	public EnrollmentResponse execute(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia execute");
		}

		IProcedureRequest anOriginalRequest = initProcedureRequest((IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest"));
		EnrollmentResponse wResponseValidateCustomer = (EnrollmentResponse) aBagSPJavaOrchestration.get("wResponseValidateCustomer");
		EnrollmentRequest wEnrollmentRequest = (EnrollmentRequest) aBagSPJavaOrchestration.get("wEnrollmentRequest");

		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, TRN_1890023);
		this.setParametersCommons(anOriginalRequest, wEnrollmentRequest);
		anOriginalRequest.setSpName("cob_bvirtual..sp_enrolamiento_bv");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, TRN_1890023);
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, wEnrollmentRequest.getOperation());

		// MAPEO DE DATOS DE LA RESPUESTA DE LA VALIDACION DEL CENTRAL
		if (wResponseValidateCustomer.getClient() != null) {
			anOriginalRequest.addInputParam("@i_enente", ICTSTypes.SQLINT4, wResponseValidateCustomer.getClient().getId());
			anOriginalRequest.addInputParam("@i_nomlar", ICTSTypes.SQLVARCHAR, wResponseValidateCustomer.getClient().getCompleteName());
			anOriginalRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR, wResponseValidateCustomer.getClient().getIdentification());
		}

		if (wEnrollmentRequest.getClient() != null) {
			anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getLogin());
			anOriginalRequest.addInputParam("@i_password", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getPassword());
			anOriginalRequest.addInputParam("@i_fechanac", ICTSTypes.SQLDATETIME, wEnrollmentRequest.getClient().getBirthDate());
			anOriginalRequest.addInputParam("@i_correo", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getMail());
			anOriginalRequest.addInputParam("@i_telefono", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getPhone());
			anOriginalRequest.addInputParam("@i_correo_op", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getMailOp());
			anOriginalRequest.addInputParam("@i_telefono_op", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getClient().getPhoneOp());
		}

		if (wEnrollmentRequest.getProduct() != null) {
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getProduct().getProductNumber());
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, String.valueOf(wEnrollmentRequest.getProduct().getProductType()));
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.getProcedureRequestAsString());
		}

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());
		}

		EnrollmentResponse wEnrollmentResponse = this.transformExecute(response, wResponseValidateCustomer);
		aBagSPJavaOrchestration.put("wResponseExecute", wEnrollmentResponse);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza execute");
		}
		return wEnrollmentResponse;
	}

	/**
	 * Ejecuta validacion en el central
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public EnrollmentResponse validateCustomer(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia validateCustomer");
		}

		EnrollmentRequest wEnrollmentRequest = (EnrollmentRequest) aBagSPJavaOrchestration.get("wEnrollmentRequest");
		IProcedureRequest anOriginalRequest = initProcedureRequest((IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest"));
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, TRN_1890022);
		this.setParametersCommons(anOriginalRequest, wEnrollmentRequest);
		anOriginalRequest.setSpName("cobis..sp_valida_enrolamiento_bv");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, TRN_1890022);
		if (wEnrollmentRequest.getClient() != null) {
			anOriginalRequest.addInputParam("@i_fechanac", ICTSTypes.SQLDATETIME, wEnrollmentRequest.getClient().getBirthDate());
		}
		if (wEnrollmentRequest.getProduct() != null) {
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, wEnrollmentRequest.getProduct().getProductNumber());
			anOriginalRequest.addInputParam("@i_tipo_producto", ICTSTypes.SQLINT4, String.valueOf(wEnrollmentRequest.getProduct().getProductType()));
		}

		anOriginalRequest.addOutputParam("@o_ente_mis", ICTSTypes.SQLINT4, "0");
		anOriginalRequest.addOutputParam("@o_nomlar", ICTSTypes.SQLVARCHAR, "0");
		anOriginalRequest.addOutputParam("@o_id", ICTSTypes.SQLVARCHAR, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.getProcedureRequestAsString());
		}

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());
		}

		EnrollmentResponse wResponseValidateCustomer = this.transformValidate(response);
		aBagSPJavaOrchestration.put("wResponseValidateCustomer", wResponseValidateCustomer);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza validateCustomer");
		}
		return wResponseValidateCustomer;
	}

	/**
	 * Establece parametros @s_ y cabecera
	 * 
	 * @param anOriginalRequest
	 * @param request
	 */
	private void setParametersCommons(IProcedureRequest anOriginalRequest, EnrollmentRequest request) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia setParametersCommons");
		}

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");

		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(request.getOfficeCode()));
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, request.getUserBv());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, request.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT2, String.valueOf(request.getRole()));
		anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, request.getCreationDate());

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza setParametersCommons");
		}
	}

	/**
	 * Transforma la respuesta de la validacion del central o local
	 * 
	 * @param response
	 * @return
	 */
	private EnrollmentResponse transformValidate(IProcedureResponse response) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia transformValidate");
		}

		EnrollmentResponse wResponse = new EnrollmentResponse();
		wResponse.setReturnCode(response.getReturnCode());
		if (response.getReturnCode() == 0) {
			wResponse.setSuccess(true);
			Client wClient = new Client();
			wClient.setId(response.readValueParam("@o_ente_mis"));
			wClient.setCompleteName(response.readValueParam("@o_nomlar"));
			wClient.setIdentification(response.readValueParam("@o_id"));
			wResponse.setClient(wClient);
		} else {
			wResponse.setSuccess(false);
			wResponse.setMessages(Utils.returnArrayMessage(response));
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza transformValidate");
		}
		return wResponse;
	}

	/**
	 * Transforma la respuesta de la ejecucion de enrolamiento
	 * 
	 * @param response
	 * @return
	 */
	private EnrollmentResponse transformExecute(IProcedureResponse response, EnrollmentResponse wResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia transformExecute");
		}

		wResponse.setReturnCode(response.getReturnCode());
		if (response.getReturnCode() == 0) {
			wResponse.setReferenceNumber(response.readValueParam("@o_referencia"));
			wResponse.setSuccess(true);
		} else {
			wResponse.setSuccess(false);
			wResponse.setMessages(Utils.returnArrayMessage(response));
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza transformExecute");
		}
		return wResponse;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " loadConfiguration");
		}
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
