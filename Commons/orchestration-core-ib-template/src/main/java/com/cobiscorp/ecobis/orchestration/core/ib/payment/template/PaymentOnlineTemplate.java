package com.cobiscorp.ecobis.orchestration.core.ib.payment.template;

import java.util.Map;
/*
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.interfaces.transfers.ITransferExecution;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.BaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.UtilsTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
*/













import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.interfaces.transfers.ITransferExecution;
import com.cobiscorp.ecobis.ib.orchestration.base.payments.PaymentBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.UtilsTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

public abstract class PaymentOnlineTemplate extends PaymentBaseTemplate  {
	protected static final int CODE_OFFLINE = 40004;
	ILogger logger = (ILogger) this.getLogger();
	


	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException{
		IProcedureResponse responseValidateCentralExecution = null;
		IProcedureResponse responseExecutePayment = null;
		IProcedureResponse responseBalancesToSychronize = null;
		IProcedureResponse response = null;
		TransactionMonetaryResponse onMonetary ;
		
		
		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String)aBagSPJavaOrchestration.get(PAYMENT_NAME));
		
		
		if (VALIDATE_PREVIOUS){
			responseValidateCentralExecution = validatePreviousExecution(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorPayment.append(" --> validateCentralExecution").toString(), responseValidateCentralExecution)){ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCentralExecution);
				return null;
			}
		}
		ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (responseServer.getOnLine() || (this.evaluateExecuteReentry(anOriginalRequest)&& responseServer.getOnLine() )){
			
			responseExecutePayment = executePayment(anOriginalRequest, aBagSPJavaOrchestration);
			
	
		     if(this.evaluateExecuteReentry(anOriginalRequest) 
		    		 && (anOriginalRequest.readValueParam("@i_type_reentry")!=null 
						&&	anOriginalRequest.readValueParam("@i_type_reentry").equals(TYPE_REENTRY_OFF))) {	 
		    		if (logger.isInfoEnabled()) logger.logInfo(":::: RETURN DEFAULT RESPONSE POR REENTRY DE OFFLINE SOBRE PROVEEDOR");		    		
		    		onMonetary =  (TransactionMonetaryResponse) aBagSPJavaOrchestration.get(ONLY_MONETARY);		    		
		    	 response= new ProcedureResponseAS(); 
		    	 if(onMonetary.getSuccess())
		    	 response.setReturnCode(0);
				 return response;
				}
			
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecutePayment);
			if (Utils.flowError(messageErrorPayment.append(" --> executePayment").toString(), responseExecutePayment)) {
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
					return responseExecutePayment;
			};
			responseBalancesToSychronize = new ProcedureResponseAS();
			
			if (responseExecutePayment.getResultSetListSize()>0)
				responseBalancesToSychronize.addResponseBlock(responseExecutePayment.getResultSet(1));
			else{
				responseBalancesToSychronize = getBalancesToSynchronize(anOriginalRequest);
				if (responseBalancesToSychronize.getReturnCode()!=CODE_OFFLINE){
					if (Utils.flowError(messageErrorPayment.append(" --> getBalancesToSychronize").toString(), responseBalancesToSychronize)){ 
						if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
						return responseBalancesToSychronize;
					}
				}
			}
			response = responseExecutePayment;
			
		}
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseBalancesToSychronize);
		return response;
	}

	
	

	@Override
	public IProcedureResponse executeStepsPaymentBase(
			IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		//responseServer.setOnLine(false);//borrar mientras pruebo el fuera de línea
		if (!SUPPORT_OFFLINE && !responseServer.getOnLine()){
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("PLUGIN NO SOPORTA OFFLINE!!!"));
			return Utils.returnException("PLUGIN NO SOPORTA OFFLINE!!!");
		}
			
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		
		return super.executeStepsPaymentBase(anOriginalRequest, aBagSPJavaOrchestration);
	}


		



}
