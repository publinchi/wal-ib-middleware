package com.cobiscorp.ecobis.orchestration.core.batch.ib.minimumbalance;

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

/**
 * 
 * @author gyagual
 *
 */
public class MinimumBalanceNotificationBatch extends NotificationBatchBase {
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

	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;

		BatchNotificationResponse aMinimumBalanceResponse = null;
		BatchNotificationRequest aMinimumBalanceRequest = transformMBRequest(request.clone());
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeTransaction - MinimumBalanceNotificationBatch");
			messageError = "ERROR in Method executeTransaction.";
			aMinimumBalanceRequest.setOriginalRequest(request);
			aMinimumBalanceResponse = coreServiceBatchNotification
					.getMinimumBalanceNotification(aMinimumBalanceRequest);
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

		if (aMinimumBalanceResponse == null || aMinimumBalanceResponse.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Error en el response de la transaccion.");
		}

		return transformMBResponse(aMinimumBalanceResponse, aBagSPJavaOrchestration);

	}

	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("INICIO - executeJavaOrchestration");

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("OriginalRequest>>" + anOrginalRequest);
			executeStepsTransactionsBase(anOrginalRequest, aBagSPJavaOrchestration);
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

	private BatchNotificationRequest transformMBRequest(IProcedureRequest aRequest) {
		BatchNotificationRequest wMminimumBalanceRequest = new BatchNotificationRequest();
		Batch wBatch = new Batch();
		Product wProduct = new Product();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		if (!Utils.isNull(aRequest.readValueParam("@i_cuenta")))
			wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));
		if (!Utils.isNull(aRequest.readValueParam("@i_batch")))
			wBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));
		if (!Utils.isNull(aRequest.readValueParam("@i_sarta")))
			wBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));
		if (!Utils.isNull(aRequest.readValueParam("@i_secuencial")))
			wBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		if (!Utils.isNull(aRequest.readValueParam("@i_corrida")))
			wBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));
		if (!Utils.isNull(aRequest.readValueParam("@i_intento")))
			wBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));
		if (!Utils.isNull(aRequest.readValueParam("@i_producto")))
			wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_producto")));

		wMminimumBalanceRequest.setBatchInfo(wBatch);
		wMminimumBalanceRequest.setProductInfo(wProduct);
		// wMminimumBalanceRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));//ojo
		wMminimumBalanceRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@i_filial")));// se
																										// envia
																										// parametro
																										// desde
																										// visual
																										// batch
		wMminimumBalanceRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));
		wMminimumBalanceRequest.setCondicion(aRequest.readValueParam("@i_condicion"));

		return wMminimumBalanceRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformMBResponse(BatchNotificationResponse aMinimumBalanceResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");

		if (logger.isDebugEnabled())
			logger.logDebug("transformMBResponse");
		response.setReturnCode(aMinimumBalanceResponse.getReturnCode());

		if (aMinimumBalanceResponse.getReturnCode() != 0) {
			response = Utils.returnException(aMinimumBalanceResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		} else {
			if (aMinimumBalanceResponse.getReturnBalance() != null) {
				response.addParam("@o_saldo", ICTSTypes.SQLMONEY, 0,
						aMinimumBalanceResponse.getReturnBalance().toString());
			}

			if (aMinimumBalanceResponse.getNotification() != null) {
				response.addParam("@o_notifica", ICTSTypes.SQLCHAR, 0,
						aMinimumBalanceResponse.getNotification().toString());
			}

			aBagSPJavaOrchestration.put(SEND_NOTIFICATION, aMinimumBalanceResponse.getNotification());

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + response.getProcedureResponseAsString());

		return response;

	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {

		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();
		Client client = new Client();

		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		NotificationDetail notificationDetail = new NotificationDetail();
		Product product = new Product();
		if (logger.isInfoEnabled())
			logger.logInfo("transformNotificationReq - inicia");

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cultura")))
			notificationRequest.setCulture(anOriginalRequest.readValueParam("@i_cultura")); // s_culture

		notificationDetail.setDateNotification("2015/01/01");

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cod_cliente")))
			client.setIdCustomer(anOriginalRequest.readValueParam("@i_cod_cliente")); // i_ente_ib

		if (!Utils.isNull(anOriginalRequest.readParam("@i_notificacion")))
			notification.setId(anOriginalRequest.readValueParam("@i_notificacion")); // i_notificacion
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cuenta"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_producto")))
			product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_producto"))); // i_producto

		if (!Utils.isNull(anOriginalRequest.readParam("@i_des_producto")))
			notificationDetail.setProductId(anOriginalRequest.readValueParam("@i_des_producto")); // i_p

		if (!Utils.isNull(anOriginalRequest.readParam("@i_alias")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_alias")); // i_aux1

		if (!Utils.isNull(responseTransaction.readParam("@o_saldo")))
			notificationDetail.setValue(responseTransaction.readValueParam("@o_saldo")); // i_v1

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cuenta")); // i_c1

		notificationDetail.setTypeSend("M"); // i_tipo
		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress()); // i_oficial_cli

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress()); // i_oficial_cta

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		notificationRequest.setClient(client);

		return notificationRequest;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceSendNotification;
	}

	@Override
	public ICacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return coreCacheManager;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

}