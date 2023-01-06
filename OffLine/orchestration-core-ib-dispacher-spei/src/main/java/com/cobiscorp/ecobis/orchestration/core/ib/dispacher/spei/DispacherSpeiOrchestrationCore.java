package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.ServiceRequest;
import com.cobiscorp.cobis.cts.services.session.SessionCrypt;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.DispacherSpeiTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ordenpago;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.DispatcherSpeiOfflineTemplate;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferInOfflineTemplate;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;

import static org.mockito.Mockito.doThrow;

import java.io.Serializable;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */
@Component(name = "DispacherSpeiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DispacherSpeiOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DispacherSpeiOrchestrationCore") })
public class DispacherSpeiOrchestrationCore extends DispatcherSpeiOfflineTemplate {

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DispacherSpeiOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

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

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original input
	 * parameters.
	 *
	 * @param anOriginalRequest       - Information original sended by user's.
	 * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		IProcedureRequest originalByNotify = anOriginalRequest;

		if (logger.isInfoEnabled())
			logger.logInfo("JCOS DispacherSpeiOrchestrationCore: executeJavaOrchestration");

		logger.logInfo(anOriginalRequest);

		// METODO GUARDAR XML

		aBagSPJavaOrchestration.put(TRANSFER_NAME, "SPEI DISPACHER");
		mensaje message = null;

		try {

			String xmls = anOriginalRequest.readValueParam("@i_pay_order");
			DispatcherUtil plot = new DispatcherUtil();
			message = plot.getDataMessage(xmls);
			if (message != null) {
				aBagSPJavaOrchestration.put("speiTransaction", message);
				if(message.getOrdenpago().getOpFirmaDig()!=null) {
					this.doSignature(anOriginalRequest, aBagSPJavaOrchestration);
				}
				executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
			}
			

		}catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}catch (Exception xe) {
			logger.logError(xe);
		}

	/*	if (response != null && !response.hasError() && response.getReturnCode() == 0) {
			String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
			if (null == idDevolucion || "0".equals(idDevolucion)) {
				notifySpei(anOriginalRequest, aBagSPJavaOrchestration);
			}
		}*/
		
		IProcedureResponse valuesOutput = new ProcedureResponseAS();
		valuesOutput.addParam("@replay", 39, 1, aBagSPJavaOrchestration.get("returnMessage").toString());

		return valuesOutput;//processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	public IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {// throws
																							// CTSServiceException,
																							// CTSInfrastructureException
		IProcedureResponse response = null;
		try {
			IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			response = mappingResponse(executeTransferSpeiIn(anOriginalRequest, aBagSPJavaOrchestration),
					aBagSPJavaOrchestration);
		} catch (Exception e) {
			logger.logError("AN ERROR OCURRED: ", e);
		}

		return response;
	}

	private IProcedureResponse mappingResponse(IProcedureResponse aResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = CLASS_NAME + "[mappingResponse] ";
		logger.logInfo(wInfo + INIT_TASK);

		return null;
	}

	private IProcedureResponse executeTransferSpeiIn(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = CLASS_NAME + "[executeTransferSpeiIn] ";
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureResponse response = new ProcedureResponseAS();

		IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "Request accountTransfer: " + requestTransfer.getProcedureRequestAsString());
		}

		response = executeCoreBanking(requestTransfer);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
			logger.logDebug(wInfo + "response de central: " + response);
		}

		logger.logInfo(wInfo + END_TASK);

		return response;

	}

	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest) {
		String wInfo = CLASS_NAME + "[getRequestTransfer] ";
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		procedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_val", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "249");
		procedureRequest.addInputParam("@i_causa_comi", ICTSTypes.SYBVARCHAR, "250");
		procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
		procedureRequest.addInputParam("@i_fecha", ICTSTypes.SYBDATETIME,
				anOriginalRequest.readValueParam("@i_fechaOperacion"));
		procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "9");

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_conceptoPago"));
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_institucion_ordenante", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueParam("@i_institucionOrdenante"));
		procedureRequest.addInputParam("@i_institucion_beneficiaria", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
		procedureRequest.addInputParam("@i_id_spei", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_idSpei"));
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_claveRastreo"));
		procedureRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_nombreOrdenante"));
		procedureRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_rfcCurpOrdenante"));
		procedureRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_referenciaNumerica"));
		procedureRequest.addInputParam("@i_tipo", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_idTipoPago"));
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuenta_cobis"));
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "I");
		procedureRequest.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_string_request"));
		procedureRequest.addInputParam("@i_tipo_ejecucion", ICTSTypes.SQLVARCHAR, isReentryExecution ? "F" : "L");

		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");

		logger.logInfo(wInfo + END_TASK);

		return procedureRequest;
	}

	private void notifySpei(IProcedureRequest anOriginalRequest, java.util.Map map) {

		try {
			ServerResponse serverResponse = (ServerResponse) map.get(RESPONSE_SERVER);

			// Por definicion funcional no se notifica en modo offline
			if (Boolean.FALSE.equals(serverResponse.getOnLine())) {
				return;
			}

			logger.logInfo(CLASS_NAME + "Enviando notificacion spei");

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			String cuentaClave = anOriginalRequest.readValueParam("@i_cuenta_beneficiario");

			logger.logInfo(CLASS_NAME + "using clabe account account " + cuentaClave);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "1800195");

			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			// procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N145");
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "spei");
			procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaClave);
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
			procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR,
					String.valueOf(anOriginalRequest.readValueParam("@i_monto")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_conceptoPago"));
			procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_claveRastreo"));
			procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_nombreOrdenante"));

			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

			logger.logInfo("jcos proceso de notificaciom terminado");

		} catch (Exception xe) {
			logger.logError("Error en la notficación de spei recibida", xe);
		}
	}

	private void logDebug(Object aMessage) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(aMessage);
		}
	}





	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void executeCreditTransferOrchest(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		
		
		
		
	}

	@Override
	protected Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
	
		Boolean isValid=false;		
	
		
        DispatcherUtil util=new DispatcherUtil();
        String sign=  util.doSignature(request, aBagSPJavaOrchestration);
		
		
		return isValid;
		
	}
	


	@Override
	protected Object invokeNotifyDeposit(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		
	/*	mensaje message=(mensaje)aBagSPJavaOrchestration.get("speiTransaction");
		ordenpago pago=message.getOrdenpago();
		
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
		String sessionId = session.getSessionId();
		
		sessionId = SessionCrypt.encriptSessionID(sessionId, "address", "hostname");

		String serviceId = "InternetBanking.WebApp.Admin.Service.Spei.NotifyDeposit";
		
		ServiceRequest header = new ServiceRequest();
		header.addFieldInHeader(ICOBISTS.HEADER_SESSION_ID, ICOBISTS.HEADER_STRING_TYPE, sessionId);
		ServiceRequestTO requestTO = new ServiceRequestTO();
		requestTO.addValue(ServiceRequestTO.SERVICE_HEADER, header);
		requestTO.setSessionId(sessionId);
		requestTO.setServiceId(serviceId);
		
		cobiscorp.ecobis.internetbanking.webapp.admin.dto.Spei inSpei = new cobiscorp.ecobis.internetbanking.webapp.admin.dto.Spei();
		inSpei.setIdSpei(String.valueOf(pago.getOpFolio()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		inSpei.setFechaOperacion(DispatcherUtil.getCalendarFromStringAndFormat(pago.getOpFechaOper(), sdf));

		inSpei.setInstitucionOrdenante(aSpeiRequest.getInstitucionOrdenante()!=null?Integer.parseInt(aSpeiRequest.getInstitucionOrdenante()):null);
		inSpei.setInstitucionBeneficiaria(aSpeiRequest.getInstitucionBeneficiaria()!=null?Integer.parseInt(aSpeiRequest.getInstitucionBeneficiaria()):null);
		inSpei.setClaveRastreo(aSpeiRequest.getClaveRastreo());
		inSpei.setMonto(Double.parseDouble(aSpeiRequest.getMonto()));
		inSpei.setNombreOrdenante(aSpeiRequest.getNombreOrdenante());
		inSpei.setTipoCuentaOrdenante(aSpeiRequest.getTipoCuentaOrdenante() != null ? Integer.parseInt(aSpeiRequest.getTipoCuentaOrdenante()) : 40);
		inSpei.setCuentaOrdenante(aSpeiRequest.getCuentaOrdenante());
		if(aSpeiRequest.getRfcCurpOrdenante() != null) {
			inSpei.setRfcCurpOrdenante(aSpeiRequest.getRfcCurpOrdenante());
		}
		inSpei.setNombreBeneficiario(aSpeiRequest.getNombreBeneficiario());
		inSpei.setTipoCuentaBeneficiario(aSpeiRequest.getTipoCuentaBeneficiario()!=null?Integer.parseInt(aSpeiRequest.getTipoCuentaBeneficiario()):null);
		inSpei.setCuentaBeneficiario(aSpeiRequest.getCuentaBeneficiario());
		if(aSpeiRequest.getRfcCurpBeneficiario() != null) {
			inSpei.setRfcCurpBeneficiario(aSpeiRequest.getRfcCurpBeneficiario());
		}
		inSpei.setConceptoPago(aSpeiRequest.getConceptoPago());
		inSpei.setReferenciaNumerica(aSpeiRequest.getReferenciaNumerica()!=null?Integer.parseInt(aSpeiRequest.getReferenciaNumerica()):null);
		if(aSpeiRequest.getTipoPago()!=null){
			inSpei.setIdTipoPago(Integer.parseInt(aSpeiRequest.getTipoPago()));
		}
		inSpei.setCuentaCobis(notifyDepositResponse.getCuentaCobis());
		inSpei.setStringPeticion(aSpeiRequest.toString());
		inSpei.setCodigoCliente(notifyDepositResponse.getCodigoCliente());
		inSpei.setProductoCuenta(String.valueOf(notifyDepositResponse.getProductoCuenta()));

		requestTO.addValue("inSpei", inSpei);
		
		*/
		
		return null;
	}

}
