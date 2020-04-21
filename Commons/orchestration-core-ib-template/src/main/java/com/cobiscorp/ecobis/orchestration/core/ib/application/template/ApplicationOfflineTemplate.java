package com.cobiscorp.ecobis.orchestration.core.ib.application.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

public abstract class ApplicationOfflineTemplate extends ApplicationsBaseTemplate{
	
	private  ILogger logger = (ILogger) this.getLogger();
	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String APPLICATION_RESPONSE = "APPLICATION_RESPONSE";
	protected static final String APPLICATION_NAME = "APPLICATION_NAME";
	public boolean sync = true;

	
	protected abstract IProcedureResponse executeApplicationCheckbook(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration);
	
	@Override
	protected IProcedureResponse executeApplication(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseToSychronize = null;
		IProcedureResponse responseOffline = null;
		IProcedureResponse responseApplicationCheckbook = null;
		if (logger.isInfoEnabled())	logger.logInfo(CLASS_NAME + "executeApplication START");
		
		
		StringBuilder messageErrorTransfer = new StringBuilder(); 						
		messageErrorTransfer.append((String)aBagSPJavaOrchestration.get(APPLICATION_NAME));
		
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		// if is Online and if is reentryExecution , have to leave
		if (evaluateExecuteReentry(anOriginalRequest)){ 
			if (!serverResponse.getOnLine()){
				IProcedureResponse resp = Utils.returnException(CODE_OFFLINE, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
				return resp;					
			}				
		}
				
		
		
		if(serverResponse.getOnLine() || (!serverResponse.getOnLine() && (serverResponse.getOfflineWithBalances()&& sync))){
			responseApplicationCheckbook = executeApplicationCheckbook(anOriginalRequest,aBagSPJavaOrchestration);
			if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME +" getReturnCode "+ responseApplicationCheckbook.getReturnCode());
			logger.logInfo(CLASS_NAME +" getProcedureResponseAsString "+ responseApplicationCheckbook.getProcedureResponseAsString());
			}
		}else{
		if (logger.isInfoEnabled())
			logger.logInfo("fuera de linea sin saldo");
			
			responseApplicationCheckbook = new ProcedureResponseAS();
			responseApplicationCheckbook.setReturnCode(CODE_OFFLINE_WITHOUT_BAL);
			responseApplicationCheckbook.addParam("@o_referencia", ICTSTypes.SQLINT4, 1,anOriginalRequest.readValueParam("@s_ssn_branch"));
		}
		
		
		
		
		
		
		if (serverResponse.getOnLine()){
			if (Utils.flowError(messageErrorTransfer.append(" --> executeApplication").toString(), responseApplicationCheckbook))
			{ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorTransfer);				
				return responseApplicationCheckbook;
			}
		}else {			
			//Si no es ejecucion de reentry, grabar en reentry
			if (!evaluateExecuteReentry(anOriginalRequest)){ 
				responseOffline = saveReentry(anOriginalRequest,aBagSPJavaOrchestration);
				aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseOffline);
			}
		}
		
		
		if (serverResponse.getOnLine() || (!serverResponse.getOnLine() && (serverResponse.getOfflineWithBalances()&& sync)))
		{			
			responseToSychronize = new ProcedureResponseAS();
			responseToSychronize.setReturnCode(responseApplicationCheckbook.getReturnCode());
			if (responseApplicationCheckbook.getResultSetListSize()>0){
				responseToSychronize.addResponseBlock(responseApplicationCheckbook.getResultSet(1));
			}else { 
				if (sync)
					responseToSychronize = getBalancesToSynchronize(anOriginalRequest); 
	        } 
		}
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseToSychronize);
		
		return responseApplicationCheckbook;
	}

    protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
	    IProcedureRequest request = anOriginalRequest.clone();
	    IProcedureResponse responseLocalValidation = (IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_VALIDATE_LOCAL);
	    
	    logger.logDebug("responseLocalValidation -->" + responseLocalValidation);
	    
	    ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);

	    Utils.addInputParam(request, "@i_clave_bv", 56, responseLocalValidation.readValueParam("@o_clave_bv"));
	    Utils.addInputParam(request, "@i_en_linea", 39, "N");
	    Utils.addOutputParam(request, "@o_clave", 56, "0");

	    reentryPersister = (IReentryPersister)componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
	    if (reentryPersister == null) throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

	    request.removeFieldInHeader("sessionId");
	    request.addFieldInHeader("reentryPriority", 'S', "5");
	    request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
	    request.addFieldInHeader("targetId", 'S', "local");
	    request.removeFieldInHeader("serviceMethodName");
	    request.addFieldInHeader("trn", 'N', request.readValueFieldInHeader("trn"));

	    request.removeParam("@t_rty");
	    
	    if (logger.isDebugEnabled()) {
	      logger.logDebug("REQUEST TO SAVE REENTRY -->" + request.getProcedureRequestAsString());
	    }
	    Boolean reentryResponse = reentryPersister.addTransaction(request);

	    IProcedureResponse response = initProcedureResponse(request);
	    if (!reentryResponse.booleanValue()) {
	      response.addFieldInHeader("executionResult", 'S', "1");
	      response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
	    }
	    else {
	      response.addFieldInHeader("executionResult", 'S', "0");
	    }

	    return response;
	
	}
		
	
	
}
