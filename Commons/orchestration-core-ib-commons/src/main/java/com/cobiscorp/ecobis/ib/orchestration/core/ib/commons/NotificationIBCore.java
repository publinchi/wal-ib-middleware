/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.core.ib.commons;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * This plugin is used to notification
 *
 * @since Agu 15, 2014
 * @author eortega
 * @version 1.0.0
 *
 */
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class, ICoreServiceNotification.class })
@Component(name = "NotificationIBCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "NotificationIBCore"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "NotificationIBCore") })
public class NotificationIBCore extends SPJavaOrchestrationBase implements ICoreServiceNotification {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";
	static final String OPERATION2_RESPONSE = "OPERATION2_RESPONSE";
	private static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(NotificationIBCore.class);

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	private ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando solicitud de ejecucion del servicio");
		return executeNotification(anOriginalRequest, aBagSPJavaOrchestration);
	}

	protected IProcedureResponse executeNotification(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1;
		try {
			wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);

			Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
			IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
			IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1.get("ErrorProcedureResponse");

			if (wSuccessExecutionOperation1) {
				return wIProcedureResponse1;
			} else {
				return wErrorProcedureResponse;
			}
		} catch (CTSServiceException e) {
				logger.logError("Notification ERROR:" + e.getMessage());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "ERROR en ejecución del servicio");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;

		} catch (CTSInfrastructureException e) {
				logger.logError("Notification ERROR:" + e.getMessage());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "ERROR de Infrestructura");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Consumiendo Servicio");

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("IProcedureResponse", null);
		returnMap.put("SuccessExecutionOperation", false);
		returnMap.put("ErrorProcedureResponse", null);

		IProcedureResponse wProcedureResponse = sendNotification(transformRequestToDto(aBagSPJavaOrchestration), aBagSPJavaOrchestration);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta devuelta del servicio:" + wProcedureResponse.getProcedureResponseAsString());

		if ((wProcedureResponse == null) || wProcedureResponse.hasError()) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Error en servicio" + wProcedureResponse.getProcedureResponseAsString());
			returnMap.put("ErrorProcedureResponse", wProcedureResponse);
			return returnMap;
		}
		returnMap.put("SuccessExecutionOperation", true);
		returnMap.put("IProcedureResponse", wProcedureResponse);
		return returnMap;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse registerSendNotification(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando solicitud de ejecucion del servicio solicitado por otro modulo");
		return executeNotification(request, aBagSPJavaOrchestration);
	}

	private IProcedureResponse sendNotification(NotificationRequest notificationRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data de consulta:" + notificationRequest.toString());
		NotificationResponse notificationResponse = coreServiceNotification.sendNotification(notificationRequest);
		IProcedureResponse pResponse = transformDtoToResponse(notificationResponse, aBagSPJavaOrchestration);
		return pResponse;
	}

	private IProcedureResponse transformDtoToResponse(NotificationResponse notificationResponse, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + notificationResponse);
		IProcedureResponse pResponse = initProcedureResponse((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		com.cobiscorp.ecobis.ib.utils.dtos.Utils.transformBaseResponseToIprocedureResponse(notificationResponse, pResponse);
		return pResponse;
	}

	private NotificationRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		NotificationRequest notificationRequest = new NotificationRequest();
		Client cliente = new Client();
		Notification notificacion = new Notification();
		NotificationDetail detalle = new NotificationDetail();
		Product producto = new Product();

		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_ente_mis")))
			cliente.setId(wOriginalRequest.readValueParam("@i_ente_mis"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_ente_ib")))
			cliente.setIdCustomer(wOriginalRequest.readValueParam("@i_ente_ib"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_servicio")))
			notificationRequest.setChannelId(wOriginalRequest.readValueParam("@i_servicio"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_notificacion")))
			notificacion.setId(wOriginalRequest.readValueParam("@i_notificacion"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_producto")))
			producto.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_producto")));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_num_producto")))
			producto.setProductNumber(wOriginalRequest.readValueParam("@i_num_producto"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_batch")))
			notificacion.setIsBatch(wOriginalRequest.readValueParam("@i_batch"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_tipo_mensaje")))
			notificacion.setNotificationType(wOriginalRequest.readValueParam("@i_tipo_mensaje"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_transaccion_id")))
			detalle.setTransaccionId(Integer.parseInt(wOriginalRequest.readValueParam("@i_transaccion_id")));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_oficial_cli")))
			detalle.setEmailClient(wOriginalRequest.readValueParam("@i_oficial_cli"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_oficial_cta")))
			detalle.setEmailOficial(wOriginalRequest.readValueParam("@i_oficial_cta"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_tercero")))
			detalle.setEmailBeneficiary(wOriginalRequest.readValueParam("@i_tercero"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_titulo")))
			detalle.setTitle(wOriginalRequest.readValueParam("@i_titulo"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_subject")))
			detalle.setSubject(wOriginalRequest.readValueParam("@i_subject"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_body_buzon")))
			detalle.setBody(wOriginalRequest.readValueParam("@i_body_buzon"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_formato_fecha")))
			detalle.setDateFormat(Integer.parseInt(wOriginalRequest.readValueParam("@i_formato_fecha")));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_#")))
			detalle.setQuotaNumber(Integer.parseInt(wOriginalRequest.readValueParam("@i_#")));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_mensaje")))
			detalle.setMessage(wOriginalRequest.readValueParam("@i_mensaje"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_ad")))
			detalle.setDeferredContribution(wOriginalRequest.readValueParam("@i_ad"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux1")))
			detalle.setAuxiliary1(wOriginalRequest.readValueParam("@i_aux1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux2")))
			detalle.setAuxiliary2(wOriginalRequest.readValueParam("@i_aux2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux3")))
			detalle.setAuxiliary3(wOriginalRequest.readValueParam("@i_aux3"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux4")))
			detalle.setAuxiliary4(wOriginalRequest.readValueParam("@i_aux4"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux5")))
			detalle.setAuxiliary5(wOriginalRequest.readValueParam("@i_aux5"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux6")))
			detalle.setAuxiliary6(wOriginalRequest.readValueParam("@i_aux6"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux7")))
			detalle.setAuxiliary7(wOriginalRequest.readValueParam("@i_aux7"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux8")))
			detalle.setAuxiliary8(wOriginalRequest.readValueParam("@i_aux8"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux9")))
			detalle.setAuxiliary9(wOriginalRequest.readValueParam("@i_aux9"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux10")))
			detalle.setAuxiliary10(wOriginalRequest.readValueParam("@i_aux10"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux11")))
			detalle.setAuxiliary11(wOriginalRequest.readValueParam("@i_aux11"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux12")))
			detalle.setAuxiliary12(wOriginalRequest.readValueParam("@i_aux12"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux13")))
			detalle.setAuxiliary13(wOriginalRequest.readValueParam("@i_aux13"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux14")))
			detalle.setAuxiliary14(wOriginalRequest.readValueParam("@i_aux14"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux15")))
			detalle.setAuxiliary15(wOriginalRequest.readValueParam("@i_aux15"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux16")))
			detalle.setAuxiliary16(wOriginalRequest.readValueParam("@i_aux16"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux17")))
			detalle.setAuxiliary17(wOriginalRequest.readValueParam("@i_aux17"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux18")))
			detalle.setAuxiliary18(wOriginalRequest.readValueParam("@i_aux18"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux19")))
			detalle.setAuxiliary19(wOriginalRequest.readValueParam("@i_aux19"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux20")))
			detalle.setAuxiliary20(wOriginalRequest.readValueParam("@i_aux20"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux21")))
			detalle.setAuxiliary21(wOriginalRequest.readValueParam("@i_aux21"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux22")))
			detalle.setAuxiliary22(wOriginalRequest.readValueParam("@i_aux22"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux23")))
			detalle.setAuxiliary23(wOriginalRequest.readValueParam("@i_aux23"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux24")))
			detalle.setAuxiliary24(wOriginalRequest.readValueParam("@i_aux24"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux25")))
			detalle.setAuxiliary25(wOriginalRequest.readValueParam("@i_aux25"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux26")))
			detalle.setAuxiliary26(wOriginalRequest.readValueParam("@i_aux26"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux27")))
			detalle.setAuxiliary27(wOriginalRequest.readValueParam("@i_aux27"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_aux28")))
			detalle.setAuxiliary28(wOriginalRequest.readValueParam("@i_aux28"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_c1")))
			detalle.setAccountNumberDebit(wOriginalRequest.readValueParam("@i_c1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_c2")))
			detalle.setAccountNumberCredit(wOriginalRequest.readValueParam("@i_c2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_d")))
			detalle.setCondition(wOriginalRequest.readValueParam("@i_d"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_f")))
			detalle.setDateNotification(wOriginalRequest.readValueParam("@i_f"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_i")))
			detalle.setInactivate(wOriginalRequest.readValueParam("@i_i"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_m")))
			detalle.setCurrencyDescription1(wOriginalRequest.readValueParam("@i_m"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_m1")))
			detalle.setCurrencyId1(wOriginalRequest.readValueParam("@i_m1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_m2")))
			detalle.setCost1(wOriginalRequest.readValueParam("@i_m2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_m3")))
			detalle.setCurrencyId2(wOriginalRequest.readValueParam("@i_m3"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_md")))
			detalle.setCurrencyDescription2(wOriginalRequest.readValueParam("@i_md"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_md2")))
			detalle.setCost2(wOriginalRequest.readValueParam("@i_md2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_p")))
			detalle.setProductId(wOriginalRequest.readValueParam("@i_p"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v1")))
			detalle.setDays(wOriginalRequest.readValueParam("@i_v1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v2")))
			detalle.setValue(wOriginalRequest.readValueParam("@i_v2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v3")))
			detalle.setCost(wOriginalRequest.readValueParam("@i_v3"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v4")))
			detalle.setTaxes(wOriginalRequest.readValueParam("@i_v4"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v5")))
			detalle.setQuote(wOriginalRequest.readValueParam("@i_v5"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_v6")))
			detalle.setAditionalValue(wOriginalRequest.readValueParam("@i_v6"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_chq")))
			detalle.setCheck(wOriginalRequest.readValueParam("@i_chq"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_r")))
			detalle.setNote(wOriginalRequest.readValueParam("@i_r"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_s")))
			detalle.setReference(wOriginalRequest.readValueParam("@i_s"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_mail2")))
			detalle.setEmail2(wOriginalRequest.readValueParam("@i_mail2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_mail1")))
			detalle.setEmail1(wOriginalRequest.readValueParam("@i_mail1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_tipo")))
			detalle.setTypeSend(wOriginalRequest.readValueParam("@i_tipo"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_login")))
			cliente.setLogin(wOriginalRequest.readValueParam("@i_login"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_adjunto1")))
			detalle.setAttached1(wOriginalRequest.readValueParam("@i_adjunto1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_adjunto2")))
			detalle.setAttached2(wOriginalRequest.readValueParam("@i_adjunto2"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_mensaje1")))
			detalle.setMessage1(wOriginalRequest.readValueParam("@i_mensaje1"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_mensaje2")))
			detalle.setMessage2(wOriginalRequest.readValueParam("@i_mensaje2"));

		notificationRequest.setClient(cliente);
		notificationRequest.setNotification(notificacion);
		notificationRequest.setNotificationDetail(detalle);
		notificationRequest.setOriginProduct(producto);

		notificationRequest.setOriginalRequest(wOriginalRequest);
		return notificationRequest;
	}

}
