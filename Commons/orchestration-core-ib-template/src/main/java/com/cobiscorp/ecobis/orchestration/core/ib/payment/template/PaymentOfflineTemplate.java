package com.cobiscorp.ecobis.orchestration.core.ib.payment.template;

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
import com.cobiscorp.ecobis.ib.orchestration.base.payments.PaymentBaseTemplate;

public abstract class PaymentOfflineTemplate extends PaymentBaseTemplate  {
	protected static final int CODE_OFFLINE = 40004;
	ILogger logger = (ILogger) this.getLogger();
	


	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException{
		IProcedureResponse responseValidateCentralExecution = null;
		IProcedureResponse responseExecutePayment = null;
		IProcedureResponse responseBalancesToSychronize = null;
		IProcedureResponse responseOffline = null;
		IProcedureResponse response = null;
		
		
		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String)aBagSPJavaOrchestration.get(PAYMENT_NAME));
		
		
		if (VALIDATE_PREVIOUS){
			responseValidateCentralExecution = validatePreviousExecution(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorPayment.append(" --> validateCentralExecution").toString(), responseValidateCentralExecution)){ 
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCentralExecution);
				return responseValidateCentralExecution;
			}
		}
		ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (responseServer.getOnLine()){
			
			responseExecutePayment = executePayment(anOriginalRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecutePayment);
			if (Utils.flowError(messageErrorPayment.append(" --> executePayment").toString(), responseExecutePayment)) {
				if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
					return responseExecutePayment;
			};
			responseBalancesToSychronize = new ProcedureResponseAS();
			responseBalancesToSychronize.setReturnCode(0);
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
		else{
			responseExecutePayment = buildOfflineCoreResponse(anOriginalRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecutePayment);
			
			if (!evaluateExecuteReentry(anOriginalRequest)){
				responseOffline = saveReentry(anOriginalRequest,aBagSPJavaOrchestration);
				aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseOffline);
			}
			
			if (responseServer.getOfflineWithBalances()){
				responseBalancesToSychronize = getBalancesToSynchronize(anOriginalRequest);
				if (responseBalancesToSychronize.getReturnCode()!=CODE_OFFLINE){
					if (Utils.flowError(messageErrorPayment.append(" --> getBalancesToSychronize").toString(), responseBalancesToSychronize)){ 
						if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorPayment);
						return responseBalancesToSychronize;
					}
					response = responseBalancesToSychronize;
				}
				else
					response = responseOffline;
			}			
			
		}
		aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseBalancesToSychronize);
		return response;
	}

	private IProcedureResponse buildOfflineCoreResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration){
		
		ServerResponse responseServer = (ServerResponse)aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		IProcedureResponse responseLocalValidation = (IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_VALIDATE_LOCAL);
		IProcedureResponse response = new ProcedureResponseAS();
		
		response = new ProcedureResponseAS();
		if (responseServer.getOfflineWithBalances()){
			response.setReturnCode(CODE_OFFLINE);
			//response.addMessage(CODE_OFFLINE, "OFFLINE CON SALDOS");
		}
		else{
			response.setReturnCode(CODE_OFFLINE_NO_BAL);
			//response.addMessage(CODE_OFFLINE_NO_BAL, "OFFLINE SIN SALDOS");
		}				
		response.addParam("@o_retorno", ICTSTypes.SYBINT4, 0, String.valueOf(responseLocalValidation.readValueParam("@o_retorno")));
		response.addParam("@o_condicion", ICTSTypes.SYBINT4, 0, String.valueOf(responseLocalValidation.readValueParam("@o_condicion")));
		response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, anOriginalRequest.readValueParam("@s_ssn_branch"));
		response.addParam("@o_autorizacion", ICTSTypes.SYBVARCHAR, 0, responseLocalValidation.readValueParam("@o_autorizacion"));
		response.addParam("@o_ssn_branch", ICTSTypes.SYBINT4, 0, anOriginalRequest.readValueParam("@s_ssn_branch"));
		response.addParam("@o_secuencial_pag", ICTSTypes.SYBINT4, 0, "0");

		return response;
		
	}
	
	protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())logger.logInfo("*********************Dentro de SaveReentry");
		// TODO Auto-generated method stub
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
	    IProcedureRequest request = anOriginalRequest.clone();
	    IProcedureResponse responseLocalValidation = (IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_VALIDATE_LOCAL);
	    
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

	    if (logger.isInfoEnabled()) {
	      logger.logInfo("REQUEST TO SAVE REENTRY -->" + request.getProcedureRequestAsString());
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

/*
	@Override
	public IProcedureResponse executeStepsPaymentBase(
			IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
		
		// if is not Online and if is reentryExecution , have to leave 
		if (evaluateExecuteReentry(anOriginalRequest) && !responseServer.getOnLine()){
			IProcedureResponse resp = Utils.returnException(CODE_OFFLINE, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
			return resp;
		}
		return super.executeStepsPaymentBase(anOriginalRequest, aBagSPJavaOrchestration);
	}
*/

	private boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest){

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))){
			if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	
	 
	



}
