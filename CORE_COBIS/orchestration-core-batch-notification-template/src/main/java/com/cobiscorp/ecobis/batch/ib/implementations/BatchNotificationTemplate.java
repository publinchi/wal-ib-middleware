package com.cobiscorp.ecobis.batch.ib.implementations;

import java.math.BigDecimal;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchNotification;

@Component(name = "BatchNotificationTemplate", immediate = false)
@Service(value = { ICoreServiceBatchNotification.class })
@Properties(value = { @Property(name = "service.description", value = "BatchNotificationTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BatchNotificationTemplate") })
public class BatchNotificationTemplate extends SPJavaOrchestrationBase implements ICoreServiceBatchNotification {
	private static ILogger logger = LogFactory.getLogger(BatchNotificationTemplate.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String SALDO_MINIMO = "M";
	protected static final String VENCIMIENTO = "D";

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

	private BatchNotificationResponse transformResponse(IProcedureResponse response, String TypeBatch) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		/* DTO */
		BatchNotificationResponse NotificationResponse = new BatchNotificationResponse();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("transformResponse --> Response null");
			return null;
		}

		if (response.getReturnCode() == 0) {
			if (TypeBatch.equals(SALDO_MINIMO))
				if (response.readValueParam("@o_saldo") != null) {
					NotificationResponse.setReturnBalance(new BigDecimal(response.readValueParam("@o_saldo")));
				}
			if (TypeBatch.equals(VENCIMIENTO))
				if (response.readValueParam("@o_val_central") != null) {
					NotificationResponse.setReturnDays(Integer.parseInt(response.readValueParam("@o_val_central")));
				}
			if (response.readValueParam("@o_notifica") != null) {
				NotificationResponse.setNotification(response.readValueParam("@o_notifica"));
			}
			NotificationResponse.setSuccess(true);

		} else {
			NotificationResponse.setMessages(Utils.returnArrayMessage(response));
			NotificationResponse.setSuccess(false);
		}

		NotificationResponse.setReturnCode(response.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + NotificationResponse);

		return NotificationResponse;
	}

	@Override
	public BatchNotificationResponse getMinimumBalanceNotification(BatchNotificationRequest aMinimumBalance)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		BatchNotificationResponse wMinimumBalanceResponse = transformResponse(getMinimumBalance(aMinimumBalance), "M");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wMinimumBalanceResponse;
	}

	private IProcedureResponse getMinimumBalance(BatchNotificationRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Consulta de saldo de cliente y si requiere notificacion");
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		anOriginalRequest.setSpName("cobis..sp_bv_notif_sald_ctas");

		// logger.logInfo("session" + session);
		anOriginalRequest.addInputParam("@i_filial", ICTSTypes.SQLINT2, String.valueOf(request.getOfficeCode()));
		if (request.getBatchInfo().getBatch() != null)
			anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
					request.getBatchInfo().getBatch().toString());
		if (request.getBatchInfo().getSarta() != null)
			anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
					request.getBatchInfo().getSarta().toString());
		if (request.getBatchInfo().getSecuencial() != null)
			anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
					request.getBatchInfo().getSecuencial().toString());
		if (request.getBatchInfo().getCorrida() != null)
			anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT2,
					request.getBatchInfo().getCorrida().toString());
		if (request.getBatchInfo().getIntento() != null)
			anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT2,
					request.getBatchInfo().getIntento().toString());

		if (request.getProductInfo().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
					request.getProductInfo().getProductNumber());
		if (request.getProductInfo().getProductType() != null)
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT2,
					request.getProductInfo().getProductType().toString());
		if (request.getValor_condicion() != null)
			anOriginalRequest.addInputParam("@i_limite", ICTSTypes.SQLVARCHAR, request.getValor_condicion().toString());
		if (request.getCondicion() != null)
			anOriginalRequest.addInputParam("@i_condicion", ICTSTypes.SQLVARCHAR, request.getCondicion().toString());

		anOriginalRequest.addOutputParam("@o_saldo", ICTSTypes.SQLMONEY, " ");
		anOriginalRequest.addOutputParam("@o_notifica", ICTSTypes.SQLCHAR, " ");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	@Override
	public BatchNotificationResponse getLoanExpiration(BatchNotificationRequest aLoanExpiration, String wSPname)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		anOriginalRequest.setSpName(wSPname);

		anOriginalRequest.addInputParam("@i_filial", ICTSTypes.SQLINT2,
				String.valueOf(aLoanExpiration.getOfficeCode()));
		if (aLoanExpiration.getBatchInfo().getBatch() != null)
			anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
					aLoanExpiration.getBatchInfo().getBatch().toString());
		if (aLoanExpiration.getBatchInfo().getSarta() != null)
			anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
					aLoanExpiration.getBatchInfo().getSarta().toString());
		if (aLoanExpiration.getBatchInfo().getSecuencial() != null)
			anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
					aLoanExpiration.getBatchInfo().getSecuencial().toString());
		if (aLoanExpiration.getBatchInfo().getCorrida() != null)
			anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT2,
					aLoanExpiration.getBatchInfo().getCorrida().toString());
		if (aLoanExpiration.getBatchInfo().getIntento() != null)
			anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT2,
					aLoanExpiration.getBatchInfo().getIntento().toString());
		if (aLoanExpiration.getFecha_proceso() != null)
			anOriginalRequest.addInputParam("@i_fecha_proceso", ICTSTypes.SQLDATETIME,
					aLoanExpiration.getFecha_proceso().toString());
		if (aLoanExpiration.getProductInfo().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
					aLoanExpiration.getProductInfo().getProductNumber());
		if (aLoanExpiration.getValor_condicion() != null)
			anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLVARCHAR,
					aLoanExpiration.getValor_condicion().toString());
		if (aLoanExpiration.getCondicion() != null)
			anOriginalRequest.addInputParam("@i_condicion", ICTSTypes.SQLVARCHAR,
					aLoanExpiration.getCondicion().toString());

		anOriginalRequest.addOutputParam("@o_val_central", ICTSTypes.SQLINT2, " ");
		anOriginalRequest.addOutputParam("@o_notifica", ICTSTypes.SQLCHAR, " ");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		BatchNotificationResponse wLoanResponse = transformResponse(response, VENCIMIENTO);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wLoanResponse;
	}

	@Override
	public BatchNotificationResponse getFixedTermNotification(BatchNotificationRequest aRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio getFixedTermNotification-implementacion");

		// IProcedureRequest request =
		// initProcedureRequest(aRequest.getOriginalRequest());
		/*
		 * Context context = ContextManager.getContext(); CobisSession session =
		 * (CobisSession) context.getSession();
		 */

		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName("cobis..sp_bv_gen_dias_vendpf");

		if (aRequest.getProductInfo().getProductNumber() != null)
			request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, aRequest.getProductInfo().getProductNumber());
		if (aRequest.getCondicion() != null)
			request.addInputParam("@i_condicion", ICTSTypes.SQLVARCHAR, aRequest.getCondicion().toString());
		if (aRequest.getValor_condicion() != null)
			request.addInputParam("@i_val", ICTSTypes.SQLVARCHAR, aRequest.getValor_condicion().toString());

		request.addInputParam("@i_filial", ICTSTypes.SQLINT2, String.valueOf(aRequest.getOfficeCode()));

		if (aRequest.getFecha_proceso() != null)
			request.addInputParam("@i_fecha_proceso", ICTSTypes.SQLDATETIME, aRequest.getFecha_proceso());
		if (aRequest.getBatchInfo().getSarta() != null)
			request.addInputParam("@i_sarta", ICTSTypes.SQLINT4, aRequest.getBatchInfo().getSarta().toString());
		if (aRequest.getBatchInfo().getBatch() != null)
			request.addInputParam("@i_batch", ICTSTypes.SQLINT4, aRequest.getBatchInfo().getBatch().toString());
		if (aRequest.getBatchInfo().getSecuencial() != null)
			request.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
					aRequest.getBatchInfo().getSecuencial().toString());
		if (aRequest.getBatchInfo().getCorrida() != null)
			request.addInputParam("@i_corrida", ICTSTypes.SQLINT4, aRequest.getBatchInfo().getCorrida().toString());
		if (aRequest.getBatchInfo().getIntento() != null)
			request.addInputParam("@i_intento", ICTSTypes.SQLINT4, aRequest.getBatchInfo().getIntento().toString());

		request.addOutputParam("@o_val_central", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_notifica", ICTSTypes.SQLVARCHAR, " ");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformResponse(response, "D");
	}

}
