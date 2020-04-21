package com.cobiscorp.ecobis.orchestration.core.batch.ib.fixedterm;

import java.util.Map;

import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.notification.batch.NotificationBatchBase;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.batch.ib.minimumbalance.MinimumBalanceNotificationBatch;

/**
 * 
 * @author gyagual
 *
 */

public class FixedTermBatchQueryOrchestrationCore extends NotificationBatchBase {
	private ICoreServiceBatchNotification coreServiceBatchNotification;
	private ICoreServiceSendNotification coreServiceSendNotification;
	private ICoreService coreService;
	private ICacheManager coreCacheManager;

	private static ILogger logger = LogFactory.getLogger(MinimumBalanceNotificationBatch.class);

	public void setBatchNotificationService(ICoreServiceBatchNotification batchNotification) {
		coreServiceBatchNotification = batchNotification;
	}

	public void setNotificationService(ICoreServiceSendNotification notification) {
		coreServiceSendNotification = notification;
	}

	public void setCoreService(ICoreService core) {
		coreService = core;
	}

	public void setCacheManager(ICacheManager core) {
		coreCacheManager = core;
	}

	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse wFixedTermResponse = null;

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO> anOrginalRequest" + anOrginalRequest);
			// executeTransaction(anOrginalRequest, aBagSPJavaOrchestration);
			wFixedTermResponse = executeStepsTransactionsBase(anOrginalRequest, aBagSPJavaOrchestration);

			aBagSPJavaOrchestration.put("RESPONSE_TRANSACTION", wFixedTermResponse);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a CheckbookRequest
	 ********************/

	private BatchNotificationRequest transformFixedTermRequest(IProcedureRequest aRequest) {
		BatchNotificationRequest wBatchNotificationRequest = new BatchNotificationRequest();
		Batch wBatch = new Batch();
		Product wProduct = new Product();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		if (aRequest.readValueParam("@i_cuenta") != null)
			wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));
		if (aRequest.readValueParam("@i_producto") != null)
			wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_producto")));
		if (aRequest.readValueParam("@i_condicion") != null)
			wBatchNotificationRequest.setCondicion(aRequest.readValueParam("@i_condicion"));
		if (aRequest.readValueParam("@i_limite") != null)
			wBatchNotificationRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));
		// if (aRequest.readValueParam("@i_filial") != null)
		// wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));
		if (aRequest.readValueParam("@i_filial") != null)
			wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@i_filial")));
		if (aRequest.readValueParam("@i_fecha_proceso") != null)
			wBatchNotificationRequest.setFecha_proceso(aRequest.readValueParam("@i_fecha_proceso"));
		if (aRequest.readValueParam("@i_sarta") != null)
			wBatch.setSarta(new Integer(aRequest.readValueParam("@i_sarta")));
		if (aRequest.readValueParam("@i_batch") != null)
			wBatch.setBatch(new Integer(aRequest.readValueParam("@i_batch")));
		if (aRequest.readValueParam("@i_secuencial") != null)
			wBatch.setSecuencial(new Integer(aRequest.readValueParam("@i_secuencial")));
		if (aRequest.readValueParam("@i_corrida") != null)
			wBatch.setCorrida(new Integer(aRequest.readValueParam("@i_corrida")));
		if (aRequest.readValueParam("@i_intento") != null)
			wBatch.setIntento(new Integer(aRequest.readValueParam("@i_intento")));

		wBatchNotificationRequest.setBatchInfo(wBatch);
		wBatchNotificationRequest.setProductInfo(wProduct);

		/*
		 * wBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch"))
		 * );
		 * wBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta"))
		 * ); wBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam(
		 * "@i_secuencial")));
		 * wBatch.setCorrida(Integer.parseInt(aRequest.readValueParam(
		 * "@i_corrida")));
		 * wBatch.setIntento(Integer.parseInt(aRequest.readValueParam(
		 * "@i_intento")));
		 * wProduct.setProductType(Integer.parseInt(aRequest.readValueParam(
		 * "@i_producto")));
		 * wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));
		 * wBatchNotificationRequest.setBatchInfo(wBatch);
		 * wBatchNotificationRequest.setProductInfo(wProduct);
		 * wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.
		 * readValueParam("@s_ofi")));
		 */

		return wBatchNotificationRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformFixedTermResponse(BatchNotificationResponse aFixedTermResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");

		if (logger.isDebugEnabled())
			logger.logDebug("transformMBResponse");
		response.setReturnCode(aFixedTermResponse.getReturnCode());

		if (aFixedTermResponse.getReturnCode() != 0) {
			response = Utils.returnException(aFixedTermResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		} else {
			if (aFixedTermResponse.getReturnDays() != null) {
				response.addParam("@o_val_central", ICTSTypes.SQLINT4, 0,
						aFixedTermResponse.getReturnDays().toString());
			}

			if (aFixedTermResponse.getNotification() != null) {
				response.addParam("@o_notifica", ICTSTypes.SQLINT4, 0, aFixedTermResponse.getNotification().toString());
			}
			aBagSPJavaOrchestration.put(SEND_NOTIFICATION, aFixedTermResponse.getNotification());

		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + response.getProcedureResponseAsString());

		return response;

	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceSendNotification;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.logDebug("Inicia executeTransaction-FixedTermBatchQueryOrchestrationCore --->");
		String messageError = null;
		IProcedureResponse responseTransaction = null;

		BatchNotificationResponse aFixedTermResponse = null;
		BatchNotificationRequest afixedTermRequest = transformFixedTermRequest(request.clone());
		if (afixedTermRequest != null)
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "TransformRequest existoso-inicia invocacion a implementacion");
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeTransaction - FixedTermNotificationBatch");
			messageError = "get: ERROR EXECUTING SERVICE";
			// afixedTermRequest.setOriginalRequest(request);
			aFixedTermResponse = coreServiceBatchNotification.getFixedTermNotification(afixedTermRequest);// cambiar
																											// el
																											// nombre
																											// del
																											// metodo
																											// en
																											// la
																											// interface

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

		if (aFixedTermResponse == null || aFixedTermResponse.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Error en la ejecución de la transaccion.");
		}

		responseTransaction = transformFixedTermResponse(aFixedTermResponse, aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransaction);
		return responseTransaction;
		// return null;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		if (responseTransaction != null)
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Response TransactionCentral isNotNull");

		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();
		Client client = new Client();
		NotificationDetail notificationDetail = new NotificationDetail();
		Product product = new Product();

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cultura")))
			notificationRequest.setChannelId(anOriginalRequest.readValueParam("@i_cultura")); // s_culture

		notificationDetail.setDateNotification("2015/01/01");

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cod_cliente")))
			client.setIdCustomer(anOriginalRequest.readValueParam("@i_cod_cliente")); // i_ente_ib

		if (!Utils.isNull(anOriginalRequest.readParam("@i_notificacion")))
			notification.setId(anOriginalRequest.readValueParam("@i_notificacion")); // i_notificacion

		if (!Utils.isNull(anOriginalRequest.readParam("@i_producto")))
			product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_producto"))); // i_producto

		if (!Utils.isNull(anOriginalRequest.readParam("@i_des_producto")))
			notificationDetail.setProductId(anOriginalRequest.readValueParam("@i_des_producto")); // i_p

		if (!Utils.isNull(anOriginalRequest.readParam("@i_alias")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_alias")); // i_aux1

		if (!Utils.isNull(responseTransaction.readParam("@o_val_central")))
			notificationDetail.setDays(responseTransaction.readValueParam("@o_val_central")); // i_v1
		else if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Orchestration->transformNotificationRequest Central Value isNUL");

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cuenta")); // i_c1

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cuenta")); // i_c1

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress()); // i_oficial_cli

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress()); // i_oficial_cta

		notificationDetail.setTypeSend("M");// i_tipo
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		notificationRequest.setClient(client);

		return notificationRequest;
	}

	@Override
	public ICacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return coreCacheManager;
	}

}