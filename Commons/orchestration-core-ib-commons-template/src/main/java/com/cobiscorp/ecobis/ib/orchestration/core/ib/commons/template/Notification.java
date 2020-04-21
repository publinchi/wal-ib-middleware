/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.core.ib.commons.template;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
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
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 * @author eortega
 *
 */
@Component(name = "Notification", immediate = false)
@Service(value = { ICoreServiceSendNotification.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "Notification"), @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "Notification") })
public class Notification extends SPJavaOrchestrationBase implements ICoreServiceSendNotification {

	private static ILogger logger = LogFactory.getLogger(Notification.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotificationResponse sendNotification(NotificationRequest notificationRequest) throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest wOriginalRequest = notificationRequest.getOriginalRequest();
		IProcedureRequest wProcedureRequest = initProcedureRequest(wOriginalRequest);
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "1875053");

		Client cliente = notificationRequest.getClient();
		Product productoOrigen = notificationRequest.getOriginProduct();
		NotificationDetail detalleNotificacion = notificationRequest.getNotificationDetail();
		com.cobiscorp.ecobis.ib.orchestration.dtos.Notification notificacion = notificationRequest.getNotification();

		if (logger.isDebugEnabled())
			logger.logDebug(">>>>>>>>>>>>>>>>>DATA EJECUTAR: " + cliente + "--" + detalleNotificacion);

		if (!Utils.isNullOrEmpty(cliente.getId()))
			wProcedureRequest.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, cliente.getId());

		if (!Utils.isNullOrEmpty(cliente.getIdCustomer()))
			wProcedureRequest.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, cliente.getIdCustomer());

		if (!Utils.isNullOrEmpty(notificationRequest.getChannelId()))
			wProcedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINTN, notificationRequest.getChannelId());

		if (!Utils.isNullOrEmpty(notificacion.getId()))
			wProcedureRequest.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, notificacion.getId().toString());

		if (!Utils.isNullOrEmpty(productoOrigen.getProductType()))
			wProcedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINTN, productoOrigen.getProductType().toString());

		if (!Utils.isNullOrEmpty(productoOrigen.getProductNumber()))
			wProcedureRequest.addInputParam("@i_num_producto", ICTSTypes.SQLVARCHAR, productoOrigen.getProductNumber());

		if (!Utils.isNullOrEmpty(notificacion.getIsBatch()))
			wProcedureRequest.addInputParam("@i_batch", ICTSTypes.SQLCHAR, notificacion.getIsBatch());

		if (!Utils.isNullOrEmpty(notificacion.getNotificationType()))
			wProcedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, notificacion.getNotificationType());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getTransaccionId()))
			wProcedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINTN, detalleNotificacion.getTransaccionId().toString());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getEmailClient()))
			wProcedureRequest.addInputParam("@i_oficial_cli", ICTSTypes.SQLVARCHAR, detalleNotificacion.getEmailClient());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getEmailOficial()))
			wProcedureRequest.addInputParam("@i_oficial_cta", ICTSTypes.SQLVARCHAR, detalleNotificacion.getEmailOficial());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getEmailBeneficiary()))
			wProcedureRequest.addInputParam("@i_tercero", ICTSTypes.SQLVARCHAR, detalleNotificacion.getEmailBeneficiary());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getTitle()))
			wProcedureRequest.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, detalleNotificacion.getTitle());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getSubject()))
			wProcedureRequest.addInputParam("@i_subject", ICTSTypes.SQLVARCHAR, detalleNotificacion.getSubject());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getBody()))
			wProcedureRequest.addInputParam("@i_body_buzon", ICTSTypes.SQLVARCHAR, detalleNotificacion.getBody());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getDateFormat()))
			wProcedureRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLINTN, detalleNotificacion.getDateFormat().toString());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getQuotaNumber()))
			wProcedureRequest.addInputParam("@i_#", ICTSTypes.SQLVARCHAR, detalleNotificacion.getQuotaNumber().toString());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getMessage()))
			wProcedureRequest.addInputParam("@i_mensaje", ICTSTypes.SQLVARCHAR, detalleNotificacion.getMessage());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getDeferredContribution()))
			wProcedureRequest.addInputParam("@i_ad", ICTSTypes.SQLVARCHAR, detalleNotificacion.getDeferredContribution());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary1()))
			wProcedureRequest.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary2()))
			wProcedureRequest.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary3()))
			wProcedureRequest.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary3());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary4()))
			wProcedureRequest.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary4());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary5()))
			wProcedureRequest.addInputParam("@i_aux5", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary5());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary6()))
			wProcedureRequest.addInputParam("@i_aux6", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary6());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary7()))
			wProcedureRequest.addInputParam("@i_aux7", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary7());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary8()))
			wProcedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary8());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary9()))
			wProcedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary9());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary10()))
			wProcedureRequest.addInputParam("@i_aux10", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary10());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary11()))
			wProcedureRequest.addInputParam("@i_aux11", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary11());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary12()))
			wProcedureRequest.addInputParam("@i_aux12", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary12());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary13()))
			wProcedureRequest.addInputParam("@i_aux13", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary13());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary14()))
			wProcedureRequest.addInputParam("@i_aux14", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary14());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary15()))
			wProcedureRequest.addInputParam("@i_aux15", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary15());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary16()))
			wProcedureRequest.addInputParam("@i_aux16", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary16());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary17()))
			wProcedureRequest.addInputParam("@i_aux17", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary17());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary18()))
			wProcedureRequest.addInputParam("@i_aux18", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary18());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary19()))
			wProcedureRequest.addInputParam("@i_aux19", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary19());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary20()))
			wProcedureRequest.addInputParam("@i_aux20", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary20());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary21()))
			wProcedureRequest.addInputParam("@i_aux21", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary21());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary22()))
			wProcedureRequest.addInputParam("@i_aux22", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary22());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary23()))
			wProcedureRequest.addInputParam("@i_aux23", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary23());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary24()))
			wProcedureRequest.addInputParam("@i_aux24", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary24());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary25()))
			wProcedureRequest.addInputParam("@i_aux25", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary25());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary26()))
			wProcedureRequest.addInputParam("@i_aux26", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary26());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary27()))
			wProcedureRequest.addInputParam("@i_aux27", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary27());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAuxiliary28()))
			wProcedureRequest.addInputParam("@i_aux28", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAuxiliary28());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAccountNumberDebit()))
			wProcedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAccountNumberDebit());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAccountNumberCredit()))
			wProcedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAccountNumberCredit());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCondition()))
			wProcedureRequest.addInputParam("@i_d", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCondition());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getDateNotification()))
			wProcedureRequest.addInputParam("@i_f", ICTSTypes.SQLVARCHAR, detalleNotificacion.getDateNotification());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getInactivate()))
			wProcedureRequest.addInputParam("@i_i", ICTSTypes.SQLVARCHAR, detalleNotificacion.getInactivate());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCurrencyDescription1()))
			wProcedureRequest.addInputParam("@i_m", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCurrencyDescription1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCurrencyId1()))
			wProcedureRequest.addInputParam("@i_m1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCurrencyId1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCost1()))
			wProcedureRequest.addInputParam("@i_m2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCost1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCurrencyId2()))
			wProcedureRequest.addInputParam("@i_m3", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCurrencyId2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCurrencyDescription2()))
			wProcedureRequest.addInputParam("@i_md", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCurrencyDescription2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCost2()))
			wProcedureRequest.addInputParam("@i_md2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCost2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getProductId()))
			wProcedureRequest.addInputParam("@i_p", ICTSTypes.SQLVARCHAR, detalleNotificacion.getProductId());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getDays()))
			wProcedureRequest.addInputParam("@i_v1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getDays());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getValue()))
			wProcedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getValue());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCost()))
			wProcedureRequest.addInputParam("@i_v3", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCost());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getTaxes()))
			wProcedureRequest.addInputParam("@i_v4", ICTSTypes.SQLVARCHAR, detalleNotificacion.getTaxes());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getQuote()))
			wProcedureRequest.addInputParam("@i_v5", ICTSTypes.SQLVARCHAR, detalleNotificacion.getQuote());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAditionalValue()))
			wProcedureRequest.addInputParam("@i_v6", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAditionalValue());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getCheck()))
			wProcedureRequest.addInputParam("@i_chq", ICTSTypes.SQLVARCHAR, detalleNotificacion.getCheck());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getNote()))
			wProcedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, detalleNotificacion.getNote());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getReference()))
			wProcedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, detalleNotificacion.getReference());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getEmail1()))
			wProcedureRequest.addInputParam("@i_mail1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getEmail1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getEmail2()))
			wProcedureRequest.addInputParam("@i_mail2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getEmail2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getTypeSend()))
			wProcedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, detalleNotificacion.getTypeSend());

		if (!Utils.isNullOrEmpty(cliente.getLogin()))
			wProcedureRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, cliente.getLogin());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAttached1()))
			wProcedureRequest.addInputParam("@i_adjunto1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAttached1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getAttached2()))
			wProcedureRequest.addInputParam("@i_adjunto2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getAttached2());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getMessage1()))
			wProcedureRequest.addInputParam("@i_mensaje1", ICTSTypes.SQLVARCHAR, detalleNotificacion.getMessage1());

		if (!Utils.isNullOrEmpty(detalleNotificacion.getMessage2()))
			wProcedureRequest.addInputParam("@i_mensaje2", ICTSTypes.SQLVARCHAR, detalleNotificacion.getMessage2());

		/* Agrega el nombre del SP */
		wProcedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");

		if (logger.isDebugEnabled())
			logger.logDebug("obtener parametros, request: " + wProcedureRequest.getProcedureRequestAsString());

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) 
			logger.logInfo("Finaliza ejecucion sendNotification");
		
		NotificationResponse obj = new NotificationResponse();

		Utils.transformIprocedureResponseToBaseResponse(obj, pResponse);

		return obj;
	}
}
