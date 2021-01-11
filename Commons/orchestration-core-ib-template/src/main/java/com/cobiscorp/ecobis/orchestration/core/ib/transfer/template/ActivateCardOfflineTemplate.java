package com.cobiscorp.ecobis.orchestration.core.ib.transfer.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.activations.ActivateCardBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;

public abstract class ActivateCardOfflineTemplate extends ActivateCardBaseTemplate {

	protected static String CORE_SERVER = "CORE_SERVER";
	protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
	protected static final String TRANSFER_NAME = "TRANSFER_NAME";
	protected static final int CODE_OFFLINE = 40004;
	
	private static ILogger logger = LogFactory.getLogger(ActivateCardOfflineTemplate.class);

	protected abstract IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);
	
	public abstract ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent();
	
	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse responseTransfer = null;

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "rfl--> Ejecutando metodo executeTransaction Request: " + anOriginalRequest);

		StringBuilder messageErrorTransfer = new StringBuilder();
		messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

		responseTransfer = executeTransfer(aBagSPJavaOrchestration);

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);

		return responseTransfer;
	}
	
}
