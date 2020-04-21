package com.cobiscorp.ecobis.ib.orchestration.base.notification.batch;

import java.util.Map;

import com.cobiscorp.cobis.cache.ICache;
import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Officer;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

public abstract class NotificationBatchBase extends SPJavaOrchestrationBase {
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String SEND_NOTIFICATION = "SEND_NOTIFICATION";
	protected static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(NotificationBatchBase.class);
	
	/**
	 * Methods for Dependency Injection.
	 *
	 * @return ICoreServiceNotification
	 */
	protected abstract ICoreServiceSendNotification getCoreServiceNotification();
	public abstract ICoreService getCoreService();
	public abstract ICacheManager getCacheManager();
	
	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);
	public abstract NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration);
	
	
	/**
	 * Contains primary steps of  transaction execution.
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START-executeStepsTransactionsBase" + anOriginalRequest);
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		IProcedureResponse responseNotification = null;		

		responseNotification = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);
		if (responseNotification == null || responseNotification.getReturnCode() != 0){
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "::Fin prematuro del flujo. No se ejecuto la transferencia.");
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseNotification);
			return responseNotification;
		}			
		if (logger.isInfoEnabled()) 
		logger.logInfo(CLASS_NAME + "BANDERA DE SEND NOTIFICATION: " + aBagSPJavaOrchestration.get(SEND_NOTIFICATION));
		
		if (aBagSPJavaOrchestration.get(SEND_NOTIFICATION).equals("S"))
		{
			
		//Envia notificacion
		IProcedureResponse responseSendNotification = sendNotification(anOriginalRequest, aBagSPJavaOrchestration);
		if (Utils.flowError("sendNotification", responseSendNotification)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseSendNotification);
			return responseSendNotification;
			}
		}
		if (logger.isInfoEnabled()) logger.logInfo(new StringBuilder(CLASS_NAME).append("FINISH").toString());
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		
	}
	
	
	
	protected IProcedureResponse sendNotification(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration)throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START-findOfficers");
		
		IProcedureResponse response = new ProcedureResponseAS();
		OfficerByAccountResponse findOfficersExecutionResponse = findOfficers(anOriginalRequest.clone(), aBagSPJavaOrchestration);
		NotificationRequest notificationRequest =  transformNotificationRequest(anOriginalRequest, findOfficersExecutionResponse, aBagSPJavaOrchestration);
				  
		notificationRequest.setChannelId("1");
		notificationRequest.getNotification().setNotificationType("F");
		
		NotificationResponse notificationResponse =  getCoreServiceNotification().sendNotification(notificationRequest);
			
		  if (!notificationResponse.getSuccess()){
		   if (logger.isDebugEnabled()){
		    logger.logDebug(" Error enviando notificación: "+notificationResponse.getMessage().getCode()+" - "+notificationResponse.getMessage().getDescription());
		   }
		   response.setReturnCode(notificationResponse.getReturnCode());
		  }else	  
				response.setReturnCode(0);
		
		return response;
		
	}
	
	/**
	 * Finds account officers for notification.
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private OfficerByAccountResponse findOfficers(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START-findOfficers");
		if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "request: " + anOriginalRequest.getProcedureRequestAsString());
		OfficerByAccountResponse response = new OfficerByAccountResponse();
		Officer officer = null;
		
	    ICache cacheClient = null;
	    ICache cacheAccount = null;
	    String respCacheEmailClient = null;
	    String respCacheEmailAccount = null;
	    
	    
	    if (getCacheManager() != null) {
	      cacheClient = getCacheManager().getCache("CTSBVEmailClient");
	      cacheAccount = getCacheManager().getCache("CTSBVEmailAccount");
	    }
	    
	    if (logger.isInfoEnabled()) logger.logInfo("key"+ anOriginalRequest.readValueParam("@i_cuenta"));
	    
	    
	    if (cacheClient!= null && cacheClient.isKeyInCache(anOriginalRequest.readValueParam("@i_cuenta")))
	    	{
			respCacheEmailClient = (String)cacheClient.get(anOriginalRequest.readValueParam("@i_cuenta"));
	    	 
	    	 if (logger.isInfoEnabled()) 
			 logger.logInfo(cacheClient.getKeys().toString());
	    	 }
	    
	    if (cacheAccount!= null && cacheAccount.isKeyInCache(anOriginalRequest.readValueParam("@i_cuenta")))
	    	{respCacheEmailAccount = (String)cacheAccount.get(anOriginalRequest.readValueParam("@i_cuenta"));
	    	 
	    	 if (logger.isInfoEnabled()) logger.logInfo(cacheAccount.getKeys().toString());
	    	 }
	    
	    if (respCacheEmailClient!= null && respCacheEmailAccount!= null){
	    	officer = new Officer();
	    	officer.setAcountEmailAdress(respCacheEmailAccount);
	    	officer.setOfficerEmailAdress(respCacheEmailClient);
	    	response.setOfficer(officer);
	    	return  response;
	    }
		try {
			OfficerByAccountRequest request = new OfficerByAccountRequest();

			Product product = new Product();
			if (anOriginalRequest.readValueParam("@i_cuenta") != null) product.setProductNumber(anOriginalRequest.readValueParam("@i_cuenta"));
			if (anOriginalRequest.readValueParam("@i_producto") != null) product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_producto").toString()));
			request.setProduct(product);

			response = getCoreService().getOfficerByAccount(request);

			if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "response: " + response);
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "END");
			
			if (getCacheManager()!=null){
				if (cacheClient!=null)
					{cacheClient.put(anOriginalRequest.readValueParam("@i_cuenta"), response.getOfficer().getOfficerEmailAdress());
					if (logger.isDebugEnabled())
					 logger.logDebug("cacheClientKeys A---> "+ cacheClient.getKeys().toString());
					}
				if (cacheAccount!=null)	
					{cacheAccount.put(anOriginalRequest.readValueParam("@i_cuenta"), response.getOfficer().getAcountEmailAdress());
					if (logger.isDebugEnabled())
					 logger.logDebug("cacheAccountKeys B---> "+ cacheAccount.getKeys().toString());
					}
			}
			return response;
		}
		catch (CTSServiceException e) {
			e.printStackTrace();
			logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");		

			return null;
		}
		catch (CTSInfrastructureException e) {
			e.printStackTrace();
			logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");			

			return null;
		}
	}
	

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0,
			Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0,
			Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
