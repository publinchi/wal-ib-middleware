package com.cobiscorp.ecobis.orchestration.core.batch.ib.loans;

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

public class LoanExpirationQueryOrchestrationCore extends NotificationBatchBase {
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
		String wSPname = new String();
		String wNotification = new String();

		BatchNotificationResponse aResponse = null;
		BatchNotificationRequest aRequest = transformRequest(request.clone());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeTransaction - LoanExpirationQueryOrchestrationCore");
			messageError = "ERROR in Method executeTransaction.";
			aRequest.setOriginalRequest(request);
			wNotification = request.readValueParam("@i_notificacion");

			if (wNotification.equals("N7")) { // Vencimiento de Cuota de
												// PrÃ©stamo
				wSPname = "cobis..sp_bv_gen_dias_vencuotcred";
			}
			if (wNotification.equals("N5")) { // Vencimiento de PrÃ©stamo
				wSPname = "cobis..sp_bv_gen_ven_credito";
			}

			aResponse = coreServiceBatchNotification.getLoanExpiration(aRequest, wSPname);

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

		return transformResponse(aResponse, aBagSPJavaOrchestration);

	}

	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
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

	/**********************************************************************************/

	private BatchNotificationRequest transformRequest(IProcedureRequest aRequest) {
		BatchNotificationRequest wRequest = new BatchNotificationRequest();
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

		wRequest.setBatchInfo(wBatch);
		wRequest.setProductInfo(wProduct);
		// wRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));//ojo
		wRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@i_filial")));
		wRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));
		wRequest.setCondicion(aRequest.readValueParam("@i_condicion"));
		if (!Utils.isNull(aRequest.readValueParam("@i_fecha_proceso")))
			wRequest.setFecha_proceso(aRequest.readValueParam("@i_fecha_proceso"));

		return wRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformResponse(BatchNotificationResponse aResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");

		if (logger.isDebugEnabled())
			logger.logDebug("transformResponse");
		response.setReturnCode(aResponse.getReturnCode());

		if (aResponse.getReturnCode() != 0) {
			response = Utils.returnException(aResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		} else {
			if (aResponse.getReturnDays() != null) {
				response.addParam("@o_val_central", ICTSTypes.SYBINT4, 0, aResponse.getReturnDays().toString());
			}
			logger.logInfo("DIAS: " + "" + aResponse.getReturnDays());

			if (aResponse.getNotification() != null) {
				response.addParam("@o_notifica", ICTSTypes.SQLCHAR, 0, aResponse.getNotification().toString());
			}
			logger.logInfo("NOTIFICA (S/N): " + "" + aResponse.getNotification());

			aBagSPJavaOrchestration.put(SEND_NOTIFICATION, aResponse.getNotification());

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
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
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {

		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();
		Client client = new Client();
		int valor_central = 0;

		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		if (responseTransaction != null) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Response TransactionCentral isNotNull");
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Response TransactionCentral isNull");
		}

		NotificationDetail notificationDetail = new NotificationDetail();
		Product product = new Product();
		if (logger.isInfoEnabled())
			logger.logInfo("transformNotificationReq - inicia");

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cultura")))
			notificationRequest.setCulture(anOriginalRequest.readValueParam("@i_cultura")); // s_culture

		notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@i_fecha_proceso"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cod_cliente")))
			client.setIdCustomer(anOriginalRequest.readValueParam("@i_cod_cliente")); // i_ente_ib

		if (!Utils.isNull(anOriginalRequest.readParam("@i_notificacion")))
			notification.setId(anOriginalRequest.readValueParam("@i_notificacion")); // i_notificacion

		if (!Utils.isNull(anOriginalRequest.readParam("@i_producto")))
			product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_producto"))); // i_producto

		if (!Utils.isNull(anOriginalRequest.readParam("@i_des_producto")))
			product.setProductDescription(anOriginalRequest.readValueParam("@i_des_producto")); // i_p

		if (!Utils.isNull(anOriginalRequest.readParam("@i_alias")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_alias")); // i_aux1

		if (!Utils.isNull(responseTransaction.readParam("@o_val_central"))) {
			valor_central = Math.abs(Integer.parseInt(responseTransaction.readValueParam("@o_val_central")));
			notificationDetail.setDays(String.valueOf(valor_central)); // i_v1
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Orchestration->transformNotificationRequest Central Value isNUL");
		}
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cuenta")); // i_c1

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cuenta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cuenta")); // i_c1

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress()); // i_oficial_cli

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress()); // i_oficial_cta

		notificationDetail.setTypeSend("M");

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		notificationRequest.setClient(client);

		return notificationRequest;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	@Override
	public ICacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return coreCacheManager;
	}

}
