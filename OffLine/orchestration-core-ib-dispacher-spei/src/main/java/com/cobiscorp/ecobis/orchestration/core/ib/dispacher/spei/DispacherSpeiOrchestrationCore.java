package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ValidaSpei;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ordenpago;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.enumeration.SpeiCategoria;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.DispatcherSpeiOfflineTemplate;



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


		String processResponse = "";

		logInfo("JCOS DispacherSpeiOrchestrationCore: executeJavaOrchestration");
		logInfo(anOriginalRequest);

		// METODO GUARDAR XML
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "SPEI DISPACHER");
		aBagSPJavaOrchestration.put(XML_REQUEST, anOriginalRequest.readValueParam("@i_pay_order"));
		mensaje message = null;
		DispatcherUtil dispatcherUtil = new DispatcherUtil();
		ValidaSpei resultado = new ValidaSpei();
		// TRANSFORMACION Y VALIDACION DEL MENSAJE
		try {
			String xmls = anOriginalRequest.readValueParam("@i_pay_order");
			message = dispatcherUtil.getDataMessage(xmls);
			if (message != null) {
				aBagSPJavaOrchestration.put(SPEI_TRANSACTION, message);
				boolean validacionFirma = false;
				if (message.getOrdenpago().getOpFirmaDig() != null) {
					validacionFirma = this.doSignature(anOriginalRequest, aBagSPJavaOrchestration);
				}
				resultado = validaMensajeSpei(message, validacionFirma);
			}
		} catch (Exception e) {
			logger.logError("Error en Mensaje Spei IN");
			// 2 - XML Mal Formado detalle
			resultado.setResultado(false);
			resultado.setCodigoError(2);
			resultado.setDescripcionError("XML Mal Formado detalle");
		}

		try {
			aBagSPJavaOrchestration.put(RESULT_VALIDACION_SPEI, resultado);
			logInfo("[executeJavaOrchestration] Spei Validacion: " + resultado.isResultado());
			logInfo("[executeJavaOrchestration] Spei Codigo Error: " + resultado.getCodigoError());
			logInfo("[executeJavaOrchestration] Spei Descripcion Error: " + resultado.getDescripcionError());
			// GUARDO EL RESPONSE
			processResponse = dispatcherUtil.getDataReponse(aBagSPJavaOrchestration);
			logInfo("[executeJavaOrchestration] Spei xml Request: " + aBagSPJavaOrchestration.get(XML_REQUEST));
			logInfo("[executeJavaOrchestration] Spei xml Response: " + aBagSPJavaOrchestration.get(XML_RESPONSE));

			executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		} catch (Exception xe) {
			logger.logError(xe);
		}

		IProcedureResponse valuesOutput = new ProcedureResponseAS();
		valuesOutput.addParam("@o_result", ICTSTypes.SQLVARCHAR, 5000, processResponse);
		return valuesOutput;
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

	private void logInfo(Object aMessage) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(aMessage);
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
		final String METHOD_NAME = "[doSignature]";
		logInfo(METHOD_NAME + "[INI]");
		String sign = "";
		mensaje message = (mensaje) aBagSPJavaOrchestration.get(SPEI_TRANSACTION);
		try {
			DispatcherUtil util = new DispatcherUtil();
			sign = util.doSignature(request, aBagSPJavaOrchestration);
			logInfo(METHOD_NAME + "Validacion Firma: " + sign.equals(message.getOrdenpago().getOpFirmaDig()));

			return sign.equals(message.getOrdenpago().getOpFirmaDig());
		} catch (Exception e) {
			logger.logError("Error en Validación: ", e);
			return false;
		} finally {
			logInfo(METHOD_NAME + "[FIN]");
		}
	}
	


	@Override
	protected IProcedureResponse invokeNotifyDeposit(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		final String METHOD_NAME = "[invokeNotifyDeposit]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;
		mensaje message = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		ordenpago pago = message.getOrdenpago();

		logInfo(METHOD_NAME + "OrdenPago: " + pago.toString());

		request.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");

		// DATOS DE TRANSACCION
		request.addInputParam("@i_fechaOperacion", ICTSTypes.SQLVARCHAR, pago.getOpFechaOper());
		request.addInputParam("@i_folioOrigen", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpFolio()));
		request.addInputParam("@i_institucionOrdenante", ICTSTypes.SQLINTN, String.valueOf(pago.getOpInsClave()));
		request.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SQLINTN, "90715"); // Institucion Beneficiario
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, String.valueOf(pago.getOpMonto()));
		request.addInputParam("@i_idTipoPago", ICTSTypes.SQLINTN, "1");
		request.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, pago.getOpCveRastreo());
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, pago.getOpEstado());
		request.addInputParam("@i_fechaRecepcion", ICTSTypes.SQLVARCHAR, pago.getOpFechaCap());
		// DATOS ORDENANTE
		request.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpNomOrd());
		request.addInputParam("@i_tipoCuentaOrdenante", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpTcClaveOrd()));
		request.addInputParam("@i_cuentaOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpCuentaOrd());
		request.addInputParam("@i_rfcCurpOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpRfcCurpOrd());
		// DATOS BENEFICIARIO
		request.addInputParam("@i_nombreBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpNomBen());
		request.addInputParam("@i_tipoCuentaBeneficiario", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpTcClaveBen()));
		request.addInputParam("@i_cuentaBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpCuentaBen());
		request.addInputParam("@i_rfcCurpBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpRfcCurpBen());
		// DATOS COMPROBANTE
		request.addInputParam("@i_conceptoPago", ICTSTypes.SQLVARCHAR, pago.getOpConceptoPag2());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpConceptoPag2()));
		request.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLINTN, String.valueOf(pago.getOpRefNumerica()));
		request.addInputParam("@i_firma", ICTSTypes.SQLVARCHAR, pago.getOpFirmaDig());
		request.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpClave()));
		request.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(XML_REQUEST));
		//request.addInputParam("@i_causaDevolucion", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// DATOS ADICIONALES
		request.addInputParam("@i_string_request", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(XML_REQUEST));
		request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18500069");
		// request.addInputParam("@i_producto", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_cuenta_cobis", ICTSTypes.SQLVARCHAR, pago.getOpCuentaBen());
		// request.addInputParam("@i_tipoNotificacion", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_username", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_codigo_cliente", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));


		// VARIABLES DE SALIDA
		logDebug(METHOD_NAME + " Datos Salida");
		request.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_folio", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_resultado", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_idCausaDevolucion", ICTSTypes.SQLVARCHAR, "X");

		logDebug(METHOD_NAME + " Datos Cabecera");
		Date fecha = new Date();
		SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
		request.setSpName("cob_procesador..sp_bv_spei_transaction");
		request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE, ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=SpeiInTransferOrchestrationCore)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); //*
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); //*
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE, "InternetBanking.WebApp.Admin.Service.Spei.NotifyDeposit");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		//request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE, "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_bv_spei_transaction");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
		//request.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(missingData.getClientCodeMis()));
		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "18500069");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("serviceExecutionId", ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500069");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "SPEI_IN");

		logger.logInfo(request);

		logger.logInfo(METHOD_NAME + "JCOS Replay from orchestrator ");

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR SPEI");

		logger.logInfo(METHOD_NAME + "CONNECTOR: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "INVOCANDO ORQUESTRATOR SPEI");

		logger.logInfo(METHOD_NAME + "SALIDAEJECUCION ORQUESTADOR");

		return connectorSpeiResponse;
	}

	@Override
	protected IProcedureResponse invokeNotifyStatus(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		final String METHOD_NAME = "[invokeNotifyStatus]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;
		mensaje message = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		ordenpago pago = message.getOrdenpago();

		logInfo(METHOD_NAME + "OrdenPago: " + pago.toString());

		request.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");

		// DATOS DE TRANSACCION
		request.addInputParam("@i_fechaOperacion", ICTSTypes.SQLVARCHAR, pago.getOpFechaOper());
		request.addInputParam("@i_folioOrigen", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpFolio()));
		request.addInputParam("@i_institucionOrdenante", ICTSTypes.SQLINTN, String.valueOf(pago.getOpInsClave()));
		request.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SQLINTN, "90715"); // Institucion Beneficiario
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, String.valueOf(pago.getOpMonto()));
		request.addInputParam("@i_idTipoPago", ICTSTypes.SQLINTN, "1");
		request.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, pago.getOpCveRastreo());
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, pago.getOpEstado());
		request.addInputParam("@i_fechaRecepcion", ICTSTypes.SQLVARCHAR, pago.getOpFechaCap());
		// DATOS ORDENANTE
		request.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpNomOrd());
		request.addInputParam("@i_tipoCuentaOrdenante", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpTcClaveOrd()));
		request.addInputParam("@i_cuentaOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpCuentaOrd());
		request.addInputParam("@i_rfcCurpOrdenante", ICTSTypes.SQLVARCHAR, pago.getOpRfcCurpOrd());
		// DATOS BENEFICIARIO
		request.addInputParam("@i_nombreBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpNomBen());
		request.addInputParam("@i_tipoCuentaBeneficiario", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpTcClaveBen()));
		request.addInputParam("@i_cuentaBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpCuentaBen());
		request.addInputParam("@i_rfcCurpBeneficiario", ICTSTypes.SQLVARCHAR, pago.getOpRfcCurpBen());
		// DATOS COMPROBANTE
		request.addInputParam("@i_conceptoPago", ICTSTypes.SQLVARCHAR, pago.getOpConceptoPag2());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpConceptoPag2()));
		request.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLINTN, String.valueOf(pago.getOpRefNumerica()));
		request.addInputParam("@i_firma", ICTSTypes.SQLVARCHAR, pago.getOpFirmaDig());
		request.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpClave()));
		request.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(XML_REQUEST));
		request.addInputParam("@i_xml_response", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(XML_RESPONSE));
		// ERROR Y LIQUIDACION
		if (pago.getOpErrClave() != null) {
			request.addInputParam("@i_causaDevolucion", ICTSTypes.SQLVARCHAR, "" + pago.getOpErrClave());
			request.addInputParam("@i_descripcion_error", ICTSTypes.SQLVARCHAR, pago.getOpRazonRechazo());
		}
		request.addInputParam("@i_estado_act", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(STATUS_TRANSACTION));
		// DATOS ADICIONALES
		request.addInputParam("@i_string_request", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get(XML_REQUEST));
		request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18500068");
		// request.addInputParam("@i_producto", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_cuenta_cobis", ICTSTypes.SQLVARCHAR, pago.getOpCuentaBen());
		// request.addInputParam("@i_tipoNotificacion", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_username", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));
		// request.addInputParam("@i_codigo_cliente", ICTSTypes.SQLVARCHAR, String.valueOf(pago.getOpMonto()));


		// VARIABLES DE SALIDA
		logDebug(METHOD_NAME + " Datos Salida");
		request.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_folio", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_resultado", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_idCausaDevolucion", ICTSTypes.SQLVARCHAR, "X");

		logDebug(METHOD_NAME + " Datos Cabecera");
		Date fecha = new Date();
		SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
		request.setSpName("cob_procesador..sp_bv_spei_transaction");
		request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE, ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=SpeiInTransferOrchestrationCore)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); //*
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); //*
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE, "InternetBanking.WebApp.Admin.Service.Spei.NotifyDeposit");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		//request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE, "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_bv_spei_transaction");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
		//request.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(missingData.getClientCodeMis()));
		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "18500068");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("serviceExecutionId", ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500068");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "SPEI_IN");

		logger.logInfo(request);

		logger.logInfo(METHOD_NAME + "JCOS Replay from orchestrator ");

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR SPEI");

		logger.logInfo(METHOD_NAME + "CONNECTOR: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "INVOCANDO ORQUESTRATOR SPEI");

		logger.logInfo(METHOD_NAME + "SALIDAEJECUCION ORQUESTADOR");

		return connectorSpeiResponse;
	}

	@Override
	protected IProcedureResponse updateSpeiInDevolutionExecution(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, boolean isError) {
		final String METHOD_NAME = "[updateSpeiInDevolutionExecution]";
		logInfo(METHOD_NAME + " [INI]");

		logDebug("Ejecutando metodo updateSpeiInDevolutionExecution: " + anOriginalRequest.toString());

		mensaje aMensaje = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");

		String nuevoEstado = ESTADO_SPEI_ERROR; // SPEI CON ERROR
		if (isError) {
			if (aMensaje.getCategoria().equals("ODPS_LIQUIDADAS_ABONOS"))
				nuevoEstado = ESTADO_SPEI_PENDIENTE_DEVOLUCION; // ERROR EN DEVOLUCION
		} else {
			nuevoEstado = ESTADO_SPEI_PROCESADO; // ORDEN PROCESADA
		}

		IProcedureRequest request = initProcedureRequest(anOriginalRequest);

		ServerResponse responseServer = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
		request.setSpName("cob_bvirtual..sp_spei_operation_in");

		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
		request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
		if (anOriginalRequest.readValueParam("@s_cliente") == null || "".equals(anOriginalRequest.readValueParam("@s_cliente"))) {
			request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_codigo_cliente"));
		}

		// OPERACION  PARA ACTUALIZAR LAS ORDENES ENTRANTES
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "U");
		logInfo(METHOD_NAME + " CLAVE RASTREO: " + aMensaje.getOrdenpago().getOpCveRastreo());
		logInfo(METHOD_NAME + " CON ERROR: " + isError);
		logInfo(METHOD_NAME + " NUEVO ESTADO: " + nuevoEstado);
		request.addInputParam("@i_cve_rastreo", ICTSTypes.SQLCHAR, aMensaje.getOrdenpago().getOpCveRastreo());
		request.addInputParam("@i_estado_job", ICTSTypes.SQLCHAR, nuevoEstado);


		logDebug(METHOD_NAME + ", request: " + request.getProcedureRequestAsString());

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		logDebug(METHOD_NAME + ", response: " + pResponse.getProcedureResponseAsString());
		logInfo(METHOD_NAME + " [FIN]");
		return pResponse;
	}

	private ValidaSpei validaMensajeSpei(mensaje aMensaje, boolean aValidacionFirma) {
		final String METHOD_NAME = "[validaMensajeSpei]";
		logInfo(METHOD_NAME + "[INI]");

		List<Integer> listTipoCuenta = new ArrayList<Integer>();
		listTipoCuenta.add(3); // Tarjeta Debito
		listTipoCuenta.add(4); // Cuenta Vostro
		listTipoCuenta.add(5); // Custodia Valores
		listTipoCuenta.add(6); // Cuenta Vostro 1
		listTipoCuenta.add(7); // Cuenta Vostro 2
		listTipoCuenta.add(8); // Cuenta Vostro 3
		listTipoCuenta.add(9); // Cuenta Vostro 4
		listTipoCuenta.add(10); // Numero Telefono
		listTipoCuenta.add(40); // Cuenta Clabe

		List<Integer> listPrioridad = new ArrayList<Integer>();
		listPrioridad.add(0); // Prioridad Baja
		listPrioridad.add(1); // Prioridad Alta

		List<String> listTipoOrden = new ArrayList<String>();
		listTipoOrden.add("E"); // Enviada
		listTipoOrden.add("R"); // Recepcion

		List<String> listTopologia = new ArrayList<String>();
		listTopologia.add("V"); // Topologia "V"

		List<Integer> listMedios = new ArrayList<Integer>();
		listMedios.add(2); // 2 - SPEI ENTRANTE SOLO PARA MENSAJES DE ABONO
		listMedios.add(9); // 9 - VIA INTERFAZ XML

		ValidaSpei resultado = new ValidaSpei();
		try {
			//********* ERRORES INTERFAZ XML **********
			logInfo(METHOD_NAME + "********* ERRORES INTERFAZ XML **********");
			// 1 - Categoria Incorrecta
			if (DispatcherUtil.validaEnumerador(SpeiCategoria.class, aMensaje.getCategoria()) == null) {
				resultado.setResultado(false);
				resultado.setCodigoError(1);
				resultado.setDescripcionError("Categoría incorrecta");
				return resultado;
			}
			// 2 - XML Mal Formado detalle
			//3 - ERROR INTERNO detalle
			//4 - ERROR Base de Datos
			//440 - Por el momento nuestro sistema se encuentra fuera de servicio. Para más información llama al 01 800 220 9000
			//5 - La clave de institución ordenante es obligatoria para este Tipo de Pago
			if (aMensaje.getOrdenpago().getOpInsClave() <= 0) {
				resultado.setResultado(false);
				resultado.setCodigoError(5);
				resultado.setDescripcionError("La clave de institución ordenante es obligatoria para este Tipo de Pago");
				return resultado;
			}
			//6 - Clave de institución ordenante no catalogada -- REVISAR CON BASE
			//7 - El Tipo de Pago es obligatorio
			if (aMensaje.getOrdenpago().getOpTpClave() >= 0) {
				//8 - Tipo de Pago no catalogado
				if (aMensaje.getOrdenpago().getOpTpClave() < 0 || aMensaje.getOrdenpago().getOpTpClave() > 24) {
					resultado.setResultado(false);
					resultado.setCodigoError(8);
					resultado.setDescripcionError("Tipo de Pago no catalogado");
					return resultado;
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(7);
				resultado.setDescripcionError("El Tipo de Pago es obligatorio");
				return resultado;
			}
			// 115 - El Tipo de pago no es válido para la institución
			if (aMensaje.getOrdenpago().getOpTpClave() != 1) {
				resultado.setResultado(false);
				resultado.setCodigoError(115);
				resultado.setDescripcionError("El Tipo de pago no es válido para la institución");
			}
			logInfo(METHOD_NAME + "Mensaje: " + aMensaje.getCategoria());
			if (SpeiCategoria.ODPS_LIQUIDADAS_ABONOS == SpeiCategoria.valueOf(aMensaje.getCategoria())) {
				// VALIDO LA FIRMA
				// 436- La firma digital recibida no es válida.
				if(!aValidacionFirma){
					resultado.setResultado(false);
					resultado.setCodigoError(436);
					resultado.setDescripcionError("La firma digital recibida no es válida.");
				}
				//********* NOMBRE DEL ORDENANTE **********
				logInfo(METHOD_NAME + "********* NOMBRE DEL ORDENANTE **********");
				// 11 - El nombre del ordenante es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpNomOrd() != null) {
					// 12 - El nombre del ordenante excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpNomOrd().length() > 40) {
						resultado.setResultado(false);
						resultado.setCodigoError(11);
						resultado.setDescripcionError("El nombre del ordenante excede la longitud permitida.");
						return resultado;
					}
					// 13 - El nombre del ordenante contiene caracteres no válidos. (ver definición Banxico) (FALTA)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpNomOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(13);
						resultado.setDescripcionError("El nombre del ordenante contiene caracteres no válidos.");
						return resultado;
					}
					// 14 - El nombre del ordenante no puede contener solo caracteres blancos.
					if (DispatcherUtil.isEmpty(aMensaje.getOrdenpago().getOpNomOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(14);
						resultado.setDescripcionError("El nombre del ordenante no puede contener solo caracteres blancos.");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(11);
					resultado.setDescripcionError("El nombre del ordenante es obligatorio para este Tipo de Pago");
					return resultado;
				}
				//********* TIPO DE CUENTA DEL ORDENANTE **********
				logInfo(METHOD_NAME + "********* TIPO DE CUENTA DEL ORDENANTE **********");
				// 15 - El tipo de cuenta del ordenante es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpTcClaveOrd() > 0) {
					// 16 - Tipo de cuenta del ordenante no-catalogado
					if (!listTipoCuenta.contains(aMensaje.getOrdenpago().getOpTcClaveOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(16);
						resultado.setDescripcionError("Tipo de cuenta del ordenante no-catalogado");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(15);
					resultado.setDescripcionError("El tipo de cuenta del ordenante es obligatorio para este Tipo de Pago");
					return resultado;
				}
				//********* CUENTA DEL ORDENANTE **********
				logInfo(METHOD_NAME + "********* CUENTA DEL ORDENANTE **********");
				// 17 - La cuenta del ordenante es obligatoria para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpCuentaOrd() != null) {
					// 18 - La cuenta del ordenante solo puede ser numérica
					if (!DispatcherUtil.isNumeric(aMensaje.getOrdenpago().getOpCuentaOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(18);
						resultado.setDescripcionError("La cuenta del ordenante solo puede ser numérica");
						return resultado;
					}
					// 19 - La cuenta del ordenante excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpCuentaOrd().length() > 20) {
						resultado.setResultado(false);
						resultado.setCodigoError(19);
						resultado.setDescripcionError("La cuenta del ordenante excede la longitud permitida.");
						return resultado;
					}
					// 20 - La cuenta del ordenante no puede contener solo ceros.
					if (DispatcherUtil.validaCuentaVacia(aMensaje.getOrdenpago().getOpCuentaOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(20);
						resultado.setDescripcionError("La cuenta del ordenante no puede contener solo ceros.");
						return resultado;
					}
					// 21 - Para tipo de cuenta clabe la cuenta del ordenante tiene que ser de 18 dígitos.
					if (aMensaje.getOrdenpago().getOpTcClaveOrd() == 40 && aMensaje.getOrdenpago().getOpCuentaOrd().length() != 18) {
						resultado.setResultado(false);
						resultado.setCodigoError(21);
						resultado.setDescripcionError("Para tipo de cuenta clabe la cuenta del ordenante tiene que ser de 18 dígitos.");
						return resultado;
					}
					// 22 - Para tipo de cuenta Tarjeta de Débito la cuenta del ordenante tiene que ser de 16 dígitos.
					if (aMensaje.getOrdenpago().getOpTcClaveOrd() == 3 && aMensaje.getOrdenpago().getOpCuentaOrd().length() != 16) {
						resultado.setResultado(false);
						resultado.setCodigoError(21);
						resultado.setDescripcionError("Para tipo de cuenta Tarjeta de Débito la cuenta del ordenante tiene que ser de 16 dígitos.");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(17);
					resultado.setDescripcionError("La cuenta del ordenante es obligatoria para este Tipo de Pago");
					return resultado;
				}
				//********* RFC O CURP DEL ORDENANTE **********
				logInfo(METHOD_NAME + "********* RFC O CURP DEL ORDENANTE **********");
				if (aMensaje.getOrdenpago().getOpRfcCurpOrd() != null) {
					// 23 - El RFC o CURP del ordenante no cumple con el formato - (SE DESCONOCE EL FORMATO)
					// 24 - El RFC o CURP del ordenante excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpRfcCurpOrd().length() > 18) {
						resultado.setResultado(false);
						resultado.setCodigoError(24);
						resultado.setDescripcionError("El RFC o CURP del ordenante excede la longitud permitida.");
						return resultado;
					}
					// 25 - El RFC o CURP del ordenante contienen caracteres no válidos. (ver definición Banxico) (FALTA)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpRfcCurpOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(25);
						resultado.setDescripcionError("El RFC o CURP del ordenante contienen caracteres no válidos.");
						return resultado;
					}
					// 26 - El RFC o CURP del ordenante no puede contener solo caracteres blancos.
					if (DispatcherUtil.isEmpty(aMensaje.getOrdenpago().getOpRfcCurpOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(26);
						resultado.setDescripcionError("El RFC o CURP del ordenante no puede contener solo caracteres blancos.");
						return resultado;
					}
				}
				//********* NOMBRE DEL BENEFICIARIO **********
				logInfo(METHOD_NAME + "********* NOMBRE DEL BENEFICIARIO **********");
				// 27 - El nombre del beneficiario es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpNomBen() != null) {
					// 28 - El nombre del beneficiario excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpNomBen().length() > 40) {
						resultado.setResultado(false);
						resultado.setCodigoError(28);
						resultado.setDescripcionError("El nombre del beneficiario excede la longitud permitida.");
						return resultado;
					}
					// 29 - El nombre del beneficiario contiene caracteres no válidos. (ver definición Banxico)(FALTA)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpNomBen())) {
						resultado.setResultado(false);
						resultado.setCodigoError(29);
						resultado.setDescripcionError("El nombre del beneficiario contiene caracteres no válidos.");
						return resultado;
					}
					// 30 - El nombre del beneficiario no puede contener solo caracteres blancos.
					if (DispatcherUtil.isEmpty(aMensaje.getOrdenpago().getOpNomBen())) {
						resultado.setResultado(false);
						resultado.setCodigoError(30);
						resultado.setDescripcionError("El nombre del beneficiario no puede contener solo caracteres blancos.");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(27);
					resultado.setDescripcionError("El nombre del beneficiario es obligatorio para este Tipo de Pago");
					return resultado;
				}
				//********* TIPO DE CUENTA DEL BENEFICIARIO **********
				logInfo(METHOD_NAME + "********* TIPO DE CUENTA DEL BENEFICIARIO **********");
				// 31 - El tipo de cuenta del beneficiario es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpTcClaveBen() > 0) {
					// 32 - Tipo de cuenta del beneficiario no-catalogado
					if (!listTipoCuenta.contains(aMensaje.getOrdenpago().getOpTcClaveBen())) {
						resultado.setResultado(false);
						resultado.setCodigoError(32);
						resultado.setDescripcionError("Tipo de cuenta del beneficiario no-catalogado");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(31);
					resultado.setDescripcionError("El tipo de cuenta del beneficiario es obligatorio para este Tipo de Pago");
					return resultado;
				}
				//********* CUENTA DEL BENEFICIARIO **********
				logInfo(METHOD_NAME + "********* CUENTA DEL BENEFICIARIO **********");
				// 33 - La cuenta del beneficiario es obligatoria para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpCuentaBen() != null) {
					// 34 - La cuenta del beneficiario solo puede ser numérica
					if (!DispatcherUtil.isNumeric(aMensaje.getOrdenpago().getOpCuentaBen())) {
						resultado.setResultado(false);
						resultado.setCodigoError(34);
						resultado.setDescripcionError("La cuenta del beneficiario solo puede ser numérica");
						return resultado;
					}
					// 35 - La cuenta del beneficiario excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpCuentaBen().length() > 20) {
						resultado.setResultado(false);
						resultado.setCodigoError(35);
						resultado.setDescripcionError("La cuenta del beneficiario excede la longitud permitida.");
						return resultado;
					}
					// 36 - La cuenta del beneficiario no puede contener solo ceros.
					if (DispatcherUtil.validaCuentaVacia(aMensaje.getOrdenpago().getOpCuentaBen())) {
						resultado.setResultado(false);
						resultado.setCodigoError(36);
						resultado.setDescripcionError("La cuenta del beneficiario no puede contener solo ceros.");
						return resultado;
					}
					// 37 - Para tipo de cuenta clabe la cuenta del beneficiario debe ser de 18 dígitos.
					if (aMensaje.getOrdenpago().getOpTcClaveBen() == 40 && aMensaje.getOrdenpago().getOpCuentaBen().length() != 18) {
						resultado.setResultado(false);
						resultado.setCodigoError(37);
						resultado.setDescripcionError("Para tipo de cuenta clabe la cuenta del beneficiario debe ser de 18 dígitos.");
						return resultado;
					}
					// 38 - Para tipo de cuenta Tarjeta de Débito la cuenta del beneficiario debe ser de 16 dígitos.
					if (aMensaje.getOrdenpago().getOpTcClaveBen() == 3 && aMensaje.getOrdenpago().getOpCuentaBen().length() != 16) {
						resultado.setResultado(false);
						resultado.setCodigoError(38);
						resultado.setDescripcionError("Para tipo de cuenta Tarjeta de Débito la cuenta del beneficiario debe ser de 16 dígitos.");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(33);
					resultado.setDescripcionError("La cuenta del beneficiario es obligatoria para este Tipo de Pago");
					return resultado;
				}
				//********* RFC O CURP DEL BENEFICIARIO ********** (ES OPCIONAL)
				logInfo(METHOD_NAME + "********* RFC O CURP DEL BENEFICIARIO **********");
				if (aMensaje.getOrdenpago().getOpRfcCurpOrd() != null) {
					// 39 - El RFC o CURP del beneficiario no cumple con el formato (SE DESCONOCE EL FORMATO)
					// 40 - El RFC o CURP del beneficiario excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpRfcCurpOrd().length() > 18) {
						resultado.setResultado(false);
						resultado.setCodigoError(40);
						resultado.setDescripcionError("El RFC o CURP del beneficiario excede la longitud permitida.");
						return resultado;
					}
					// 41 - El RFC o CURP del beneficiario contienen caracteres no válidos. (ver definición Banxico) (FALTA)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpRfcCurpOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(41);
						resultado.setDescripcionError("El RFC o CURP del beneficiario contienen caracteres no válidos.");
						return resultado;
					}
					// 42 - El RFC o CURP del beneficiario no puede contener solo caracteres blancos.
					if (DispatcherUtil.isEmpty(aMensaje.getOrdenpago().getOpRfcCurpOrd())) {
						resultado.setResultado(false);
						resultado.setCodigoError(42);
						resultado.setDescripcionError("El RFC o CURP del beneficiario no puede contener solo caracteres blancos");
						return resultado;
					}
				}
				//********* CONCEPTO DEL PAGO **********
				// 43 - El concepto de pago es obligatorio para este Tipo de Pago
				// 44 - El concepto de pago excede la longitud permitida. (ver definición Banxico)
				//45 - El concepto de pago contiene caracteres no válidos. (ver definición Banxico)
				// 46 - El concepto de pago no puede contener solo caracteres blancos.
				//********* IVA ********** (ES OPCIONAL)
				// 47 - El campo IVA es obligatorio para este Tipo de Pago
				// 48 - El campo IVA debe ser mayor que cero
				// 49 - El campo IVA debe ser menor que 9999999999999999.99
				//********* REFERENCIA NUMERICA **********
				logInfo(METHOD_NAME + "********* REFERENCIA NUMERICA **********");
				// 50 - La referencia numérica es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpRefNumerica() >= 0) {
					// 51 - La referencia numérica debe ser solo dígitos mayores a cero
					if (aMensaje.getOrdenpago().getOpRefNumerica() == 0) {
						resultado.setResultado(false);
						resultado.setCodigoError(51);
						resultado.setDescripcionError("La referencia numérica debe ser solo dígitos mayores a cero");
						return resultado;
					}
					// 52 - La referencia numérica excede la longitud permitida. (ver definición Banxico)
					if (Integer.valueOf(aMensaje.getOrdenpago().getOpRefNumerica()).toString().length() > 7) {
						resultado.setResultado(false);
						resultado.setCodigoError(52);
						resultado.setDescripcionError("La referencia numérica excede la longitud permitida. ");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(50);
					resultado.setDescripcionError("La referencia numérica es obligatorio para este Tipo de Pago");
					return resultado;
				}
				//********* REFERENCIA DE COBRANZA ********** (ES OPCIONAL)
				logInfo(METHOD_NAME + "********* REFERENCIA DE COBRANZA **********");
				// 53 - La referencia cobranza es obligatoria para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpRefCobranza() != null) {
					// 54 - La referencia cobranza contiene caracteres no válidos. (ver definición Banxico)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpRefCobranza())) {
						resultado.setResultado(false);
						resultado.setCodigoError(54);
						resultado.setDescripcionError("La referencia cobranza contiene caracteres no válidos.");
						return resultado;
					}
					// 55 - La referencia cobranza excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpRefCobranza().length() > 40) {
						resultado.setResultado(false);
						resultado.setCodigoError(55);
						resultado.setDescripcionError("La referencia cobranza excede la longitud permitida.");
						return resultado;
					}
					// 56 - La referencia cobranza no puede contener solo ceros.
					if (DispatcherUtil.validaCuentaVacia(aMensaje.getOrdenpago().getOpRefCobranza())) {
						resultado.setResultado(false);
						resultado.setCodigoError(56);
						resultado.setDescripcionError("La referencia cobranza no puede contener solo ceros.");
						return resultado;
					}
				}
				// NO APLICA PARA ESTE TIPO DE PAGO
				//else {
				//	resultado.setResultado(false);
				//	resultado.setCodigoError(53);
				//	resultado.setDescripcionError("La referencia cobranza es obligatoria para este Tipo de Pago");
				//	return resultado;
				//}
				//********* CONCEPTO DEL PAGO 2 **********
				logInfo(METHOD_NAME + "********* CONCEPTO DEL PAGO 2 **********");
				// 77 - El concepto de pago 2 es obligatorio para este Tipo de Pago
				if (aMensaje.getOrdenpago().getOpTpClave() == 1 && aMensaje.getOrdenpago().getOpConceptoPag2() != null) {
					// 78 - El concepto de pago 2 excede la longitud permitida. (ver definición Banxico)
					if (aMensaje.getOrdenpago().getOpConceptoPag2().length() > 40) {
						resultado.setResultado(false);
						resultado.setCodigoError(78);
						resultado.setDescripcionError("El concepto de pago 2 excede la longitud permitida.");
						return resultado;
					}
					// 79 - El concepto de pago 2 contiene caracteres no válidos. (ver definición Banxico)(FALTA)
					if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpConceptoPag2())) {
						resultado.setResultado(false);
						resultado.setCodigoError(79);
						resultado.setDescripcionError("El concepto de pago 2 contiene caracteres no válidos.");
						return resultado;
					}
					// 80 - El concepto de pago no puede contener solo caracteres blancos.
					if (aMensaje.getOrdenpago().getOpConceptoPag2().length() > 40) {
						resultado.setResultado(false);
						resultado.setCodigoError(80);
						resultado.setDescripcionError("El concepto de pago 2 no puede contener solo caracteres blancos.");
						return resultado;
					}
				} else {
					resultado.setResultado(false);
					resultado.setCodigoError(77);
					resultado.setDescripcionError("El concepto de pago 2 es obligatorio para este Tipo de Pago");
					return resultado;
				}
			}
			logInfo(METHOD_NAME + "********* MONTO **********");
			//9 - El Monto es obligatorio
			if (aMensaje.getOrdenpago().getOpMonto() != null) {
				//10 - El Monto debe ser mayor que cero y menor al MAXIMO definido por Banxico
				if (aMensaje.getOrdenpago().getOpMonto().doubleValue() <= 0) {
					resultado.setResultado(false);
					resultado.setCodigoError(10);
					resultado.setDescripcionError("El Monto debe ser mayor que cero y menor al MAXIMO definido por Banxico");
					return resultado;
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(9);
				resultado.setDescripcionError("El Monto es obligatorio");
				return resultado;
			}
			//CLAVE DE PAGO
			//NOMBRE DEL BENEFICIARIO 2
			//TIPO DE CUENTA DEL BENEFICIARIO 2
			//CUENTA DEL BENEFICIARIO 2
			//RFC O CURP DEL BENEFICIARIO 2
			//TIPO DE OPERACIÓN
			//********* MEDIO DE ENTREGA **********
			logInfo(METHOD_NAME + "********* MEDIO DE ENTREGA **********");
			// 83 - El medio de Entrega es obligatorio para este Tipo de Pago
			if (aMensaje.getOrdenpago().getOpMeClave() > 0) {
				// 84 - Medio de Entrega no-catalogado
				if (!listMedios.contains(aMensaje.getOrdenpago().getOpMeClave())) {
					resultado.setResultado(false);
					resultado.setCodigoError(84);
					resultado.setDescripcionError("Medio de Entrega no-catalogado");
					return resultado;
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(83);
				resultado.setDescripcionError("El medio de Entrega es obligatorio para este Tipo de Pago");
				return resultado;
			}
			//********* PRIORIDAD **********
			logInfo(METHOD_NAME + "********* PRIORIDAD **********");
			// 85 - La prioridad de la orden es un dato obligatorio
			if (aMensaje.getOrdenpago().getOpPrioridad() >= 0) {
				// 86 - Prioridad de la orden no-catalogada
				if (!listPrioridad.contains(aMensaje.getOrdenpago().getOpPrioridad())) {
					resultado.setResultado(false);
					resultado.setCodigoError(86);
					resultado.setDescripcionError("Prioridad de la orden no-catalogada");
					return resultado;
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(85);
				resultado.setDescripcionError("La prioridad de la orden es un dato obligatorio");
				return resultado;
			}
			//********* TOPOLOGIA **********
			logInfo(METHOD_NAME + "********* TOPOLOGIA **********");
			// 87 - La Topología de la orden es obligatorio
			if (aMensaje.getOrdenpago().getOpTopologia() != null) {
				// 88 - Topología de la orden no-catalogada
				if (!listTopologia.contains(aMensaje.getOrdenpago().getOpTopologia())) {
					resultado.setResultado(false);
					resultado.setCodigoError(88);
					resultado.setDescripcionError("Topología de la orden no-catalogada");
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(87);
				resultado.setDescripcionError("La Topología de la orden es obligatorio");
			}
			//********* CLAVE DE RASTREO **********
			logInfo(METHOD_NAME + "********* CLAVE DE RASTREO **********");
			// 92 - La clave de rastreo es obligatoria
			if (aMensaje.getOrdenpago().getOpCveRastreo() != null) {
				// 89 - La clave de rastreo excede la longitud permitida. (MAX. 30 carac.)
				if (aMensaje.getOrdenpago().getOpCveRastreo().length() > 30) {
					resultado.setResultado(false);
					resultado.setCodigoError(89);
					resultado.setDescripcionError("La clave de rastreo excede la longitud permitida.");
				}
				// 90 - La clave de rastreo contiene caracteres no válidos. (ver definición Banxico)
				if (!DispatcherUtil.validaCaracteres(aMensaje.getOrdenpago().getOpCveRastreo())) {
					resultado.setResultado(false);
					resultado.setCodigoError(90);
					resultado.setDescripcionError("La clave de rastreo contiene caracteres no válidos.");
				}
				// 91 - La clave de rastreo no puede contener solo caracteres blancos.
				if (DispatcherUtil.isEmpty(aMensaje.getOrdenpago().getOpCveRastreo())) {
					resultado.setResultado(false);
					resultado.setCodigoError(91);
					resultado.setDescripcionError("La clave de rastreo no puede contener solo caracteres blancos.");
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(92);
				resultado.setDescripcionError("La clave de rastreo es obligatoria");
			}
			// ********* OTROS **********
			logInfo(METHOD_NAME + "********* OTROS **********");
			// 93 - La fecha de operación es obligatoria
			if (aMensaje.getOrdenpago().getOpFechaOper() != null) {
				// 108 - la fecha de operación mal formada (debe ser: yyyyMMdd)
				if (DispatcherUtil.convertStringToDate(aMensaje.getOrdenpago().getOpFechaOper()) == null) {
					resultado.setResultado(false);
					resultado.setCodigoError(108);
					resultado.setDescripcionError("la fecha de operación mal formada (debe ser: yyyyMMdd)");
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(93);
				resultado.setDescripcionError("La fecha de operación es obligatoria");
			}
			// 106 - Tipo de orden es requerido.
			if (aMensaje.getOrdenpago().getOpTipoOrden() != null) {
				// 107 - Tipo orden no valido.
				if (!listTipoOrden.contains(aMensaje.getOrdenpago().getOpTipoOrden())) {
					resultado.setResultado(false);
					resultado.setCodigoError(107);
					resultado.setDescripcionError("Tipo orden no valido.");
				}
			} else {
				resultado.setResultado(false);
				resultado.setCodigoError(106);
				resultado.setDescripcionError("Tipo de orden es requerido.");
			}
			// CP ORDENANTE
			//FECHA CONSTITUCION ORDENANTE
			// DEVOLUCION EXTEMPORANEA
			// CLASIFICACION DE LA OPERACIÓN
			// DIRECCION IP
			// FECHA DE INSTRUCCIÓN
			// HORA DE INSTRUCCIÓN
			// FECHA DE ACEPTACION
			// HORA DE ACEPTACION
			// CLAVE DEL BANCO USUARIO
			// TIPO DE CUENTA DEL BANCO USUARIO
			// CUENTA BANCO USUARIO
			// PAGO FACTURA
			// CoDi
			// Devolución Acreditada

		} catch (Exception e) {
			logger.logError(METHOD_NAME + "Error en validacion de transaccion spei: ", e);
			//3 - ERROR INTERNO detalle
			resultado.setResultado(false);
			resultado.setCodigoError(3);
			resultado.setDescripcionError("ERROR INTERNO detalle");
		} finally {
			logInfo(METHOD_NAME + "[FIN]");
		}
		return resultado;
	}
}


	



