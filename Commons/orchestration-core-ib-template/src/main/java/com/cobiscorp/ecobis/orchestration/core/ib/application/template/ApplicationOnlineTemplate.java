package com.cobiscorp.ecobis.orchestration.core.ib.application.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

public abstract class ApplicationOnlineTemplate extends ApplicationsBaseTemplate{
	
	private  ILogger logger = (ILogger) this.getLogger();
	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String APPLICATION_RESPONSE = "APPLICATION_RESPONSE";
	protected static final String APPLICATION_NAME = "APPLICATION_NAME";
	protected static final int CODE_OFFLINE = 40004;
	
	public boolean sync = true;

	protected abstract IProcedureResponse executeApplicationCheckbook(IProcedureRequest request,Map<String, Object> aBagSPJavaOrchestration);
	
	@Override
	protected IProcedureResponse executeApplication(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseToSychronize = null;
		IProcedureResponse responseApplication = new ProcedureResponseAS();
		StringBuilder messageErrorApplication = new StringBuilder(); 						

		if (logger.isInfoEnabled())	logger.logInfo(CLASS_NAME + "executeTransaction START");		
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (serverResponse.getOnLine())
		{
			responseApplication = executeApplicationCheckbook(anOriginalRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseApplication);
			
			if (Utils.flowError(messageErrorApplication.append(" --> executeApplication").toString(), responseApplication))
			{ 	if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
  			    return responseApplication;
			}	
			responseToSychronize = new ProcedureResponseAS();
			responseToSychronize.setReturnCode(0);
			if (responseApplication.getResultSetListSize()>0){
				responseToSychronize.addResponseBlock(responseApplication.getResultSet(1));
			}else {
				if (sync)
				responseToSychronize = getBalancesToSynchronize(anOriginalRequest);
			}
			
		}
		
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseToSychronize); 	
		if (logger.isInfoEnabled())	logger.logInfo(CLASS_NAME + "executeTransaction FINISH");
		return responseApplication;
	}
 		
	
	
}
