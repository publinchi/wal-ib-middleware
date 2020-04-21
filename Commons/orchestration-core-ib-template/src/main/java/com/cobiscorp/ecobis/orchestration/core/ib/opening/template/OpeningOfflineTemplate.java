package com.cobiscorp.ecobis.orchestration.core.ib.opening.template;

import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.openings.OpeningsBaseTemplate;

public abstract class OpeningOfflineTemplate extends OpeningsBaseTemplate {	
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
		
		ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		// if is Online and if is reentryExecution , have to leave
		if (evaluateExecuteReentry(anOriginalRequest)){ 
			if (!responseServer.getOnLine()){
				IProcedureResponse resp = Utils.returnException(CODE_OFFLINE, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
				return resp;					
			}				
		}		
		
		if (VALIDATE_CENTRAL){
			responseValidateCentralExecution = validateCentralExecution(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorPayment.append(" --> validateCentralExecution").toString(), responseValidateCentralExecution)){ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCentralExecution);
				return null;
			}
		}
		
		logger.logInfo("ResponseServer executeTransaction "+responseServer);
		if (responseServer.getOnLine()||(!responseServer.getOnLine() && responseServer.getOfflineWithBalances())){
			
			logger.logInfo("Condición Online u Offline sin saldos");
			responseExecuteOpening = executeOpening(anOriginalRequest, aBagSPJavaOrchestration);//estaba dentro de la condicion
			
			if (Utils.flowError(messageErrorPayment.append(" --> executePayment").toString(), responseExecuteOpening)) {
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
					return responseExecuteOpening;
			};
			responseBalancesToSychronize = new ProcedureResponseAS();
			responseBalancesToSychronize.setReturnCode(0);
			if (responseExecuteOpening.getResultSetListSize()>0)
				responseBalancesToSychronize.addResponseBlock(responseExecuteOpening.getResultSet(1));
			else{//si no trae el result set se obtiene saldos mediante la llamada al metodo getBalancesToSynchronize
				responseBalancesToSychronize = getBalancesToSynchronize(anOriginalRequest);				
			}
			aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseBalancesToSychronize);
			response = responseExecuteOpening;			 
			
		}else{
			logger.logInfo("Offline sin saldos se setea returnCode en response");			
			response = new ProcedureResponseAS();
			response.setReturnCode(CODE_OFFLINE_WITHOUT_BALANCE);

			if (response.readParam("@o_referencia")==null || response.readValueParam("@o_referencia")==null){
					response.addParam("@o_referencia", ICTSTypes.SQLVARCHAR, 1,anOriginalRequest.readValueParam("@s_ssn_branch"));
			}
		}		
		
		if(!responseServer.getOnLine()){
			if (!evaluateExecuteReentry(anOriginalRequest)){
				responseOffline = saveReentry(anOriginalRequest,aBagSPJavaOrchestration);
				aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseOffline);
			}
		}	
		
		logger.logInfo("Response executeTransaction "+response);
		
		return response;
	}

	
	protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
	    IProcedureRequest request = anOriginalRequest.clone();

	    
	    ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);

	    Utils.addInputParam(request, "@i_en_linea", 39, "N");
	    Utils.addOutputParam(request, "@o_clave", 56, "0");

	    reentryPersister = (IReentryPersister)componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
	    if (reentryPersister == null) throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

	    request.addFieldInHeader("reentryPriority", 'S', "5");
	    request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
	    request.addFieldInHeader("targetId", 'S', "local");
	    request.removeFieldInHeader("serviceMethodName");
	    request.addFieldInHeader("trn", 'N', request.readValueFieldInHeader("trn"));

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


	@Override
	public IProcedureResponse executeStepsOpeningBase(
			IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		
		// if is not Online and if is reentryExecution , have to leave 
		if (evaluateExecuteReentry(anOriginalRequest) && responseServer.getOnLine()){
			IProcedureResponse resp = Utils.returnException("NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
			resp.setReturnCode(CODE_OFFLINE);
			return resp;
		}
		return super.executeStepsOpeningBase(anOriginalRequest, aBagSPJavaOrchestration);
	}
}
