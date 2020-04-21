package com.cobiscorp.ecobis.orchestration.core.ib.opening.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.openings.OpeningsBaseTemplate;

public abstract class OpeningOnlineTemplate extends OpeningsBaseTemplate {
	protected static final int CODE_OFFLINE = 40004;
	ILogger logger = (ILogger) this.getLogger();

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException{
		IProcedureResponse responseValidateCentralExecution = null;
		IProcedureResponse responseExecuteOpening = null;
		IProcedureResponse responseBalancesToSychronize = null;
		IProcedureResponse responseOffline = null;
		IProcedureResponse response = null;		
		
		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String)aBagSPJavaOrchestration.get("OPENING NAME"));		
		
		if (VALIDATE_CENTRAL){
			responseValidateCentralExecution = validateCentralExecution(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorPayment.append(" --> validateCentralExecution").toString(), responseValidateCentralExecution)){ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCentralExecution);
				return null;
			}
		}
		ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (responseServer.getOnLine()){
			
			responseExecuteOpening = executeOpening(anOriginalRequest, aBagSPJavaOrchestration);
			
			if (Utils.flowError(messageErrorPayment.append(" --> executePayment").toString(), responseExecuteOpening)) {
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
					return responseExecuteOpening;
			};
			responseBalancesToSychronize = new ProcedureResponseAS();
			responseBalancesToSychronize.setReturnCode(0);
			if (responseExecuteOpening.getResultSetListSize()>0)
				responseBalancesToSychronize.addResponseBlock(responseExecuteOpening.getResultSet(1));
			
			response = responseExecuteOpening;
			 
		}		
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseBalancesToSychronize);
		return response;
	}	
	
	@Override
	public IProcedureResponse executeStepsOpeningBase(
			IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		if (!SUPPORT_OFFLINE && !responseServer.getOnLine())
			return Utils.returnException("PLUGIN NO SOPORTA OFFLINE!!!");
			
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		return super.executeStepsOpeningBase(anOriginalRequest, aBagSPJavaOrchestration);
	}	
}
