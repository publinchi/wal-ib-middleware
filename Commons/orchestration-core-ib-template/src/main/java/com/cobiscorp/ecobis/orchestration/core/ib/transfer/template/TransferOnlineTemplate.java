package com.cobiscorp.ecobis.orchestration.core.ib.transfer.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate;

public abstract class TransferOnlineTemplate extends TransferBaseTemplate  {

	private  ILogger logger = (ILogger) this.getLogger();
	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
	protected static final String TRANSFER_NAME = "TRANSFER_NAME";
	
	protected abstract IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseToSychronize = null;
		IProcedureResponse responseTransfer = new ProcedureResponseAS();
		StringBuilder messageErrorTransfer = new StringBuilder(); 						

		if (logger.isInfoEnabled())	logger.logInfo(CLASS_NAME + "executeTransaction START");

		messageErrorTransfer.append((String)aBagSPJavaOrchestration.get(TRANSFER_NAME));	
		
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (serverResponse.getOnLine())
		{
			responseTransfer = executeTransfer(aBagSPJavaOrchestration);
			
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);
			if (Utils.flowError(messageErrorTransfer.append(" --> executeTransfer").toString(), responseTransfer))
			{ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorTransfer);
				
  			    return responseTransfer;
			}	
			responseToSychronize = new ProcedureResponseAS();
			responseToSychronize.setReturnCode(0);
			if (responseTransfer.getResultSetListSize()>0)
				responseToSychronize.addResponseBlock(responseTransfer.getResultSet(1));
		}
		
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseToSychronize); 		
		return responseTransfer;
	}

	
	/* (non-Javadoc)
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate#executeStepsTransactionsBase(com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	/*
	@Override
	protected IProcedureResponse executeStepsTransactionsBase(
			IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration)
					throws CTSServiceException, CTSInfrastructureException {
		
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (!SUPPORT_OFFLINE && !serverResponse.getOnLine()){
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("PLUGIN NO SOPORTA OFFLINE!!!"));
			return null;			
		}
		return super.executeStepsTransactionsBase(anOriginalRequest,
				aBagSPJavaOrchestration);
	}
*/

}
