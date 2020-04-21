package com.cobiscorp.ecobis.ib.orchestration.base.query;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * @author cecheverria
 * @since Sept 3, 2014
 * @version 1.0.0
 */

public abstract class QueryBaseTemplate extends SPJavaOrchestrationBase{
	protected static final String CLASS_NAME = " >-----> ";
	private static final String COBIS_CONTEXT = "COBIS";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_VALIDATE_LOCAL= "RESPONSE_VALIDATE_LOCAL";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
	protected static final String QUERY_NAME = "QUERY_NAME";
	protected static final String LOG_MESSAGE = "LOG_MESSAGE";
	protected static final int TRN_CHECKING_ACCOUNT_STATEMENT = 1800017;
	protected static final int TRN_SAVING_ACCOUNT_STATEMENT = 1800018;
	
	
	
	private static ILogger logger = LogFactory.getLogger(QueryBaseTemplate.class);
	
	/**
	 * This method has to be override to implement call of service
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected abstract IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)  ;	
	/**
	 * Contains primary steps for execution of Query.
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse executeStepsQueryBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		String messageErrorQuery = null;
		messageErrorQuery =(String)aBagSPJavaOrchestration.get(QUERY_NAME);
		
		IProcedureResponse responseValidateLocalExecution = validateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorQuery +" --> validateLocalExecution", responseValidateLocalExecution)) {
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorQuery);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateLocalExecution);
			return responseValidateLocalExecution;
		};
		
			
		aBagSPJavaOrchestration.put(RESPONSE_VALIDATE_LOCAL, responseValidateLocalExecution);
		
		IProcedureResponse responseExecuteQuery = executeQuery(anOriginalRequest, aBagSPJavaOrchestration);
		
		if (Utils.flowError(messageErrorQuery +" --> executeQuery", responseExecuteQuery)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);
			if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final Base-->"+ responseExecuteQuery.getProcedureResponseAsString());
			return responseExecuteQuery;
		};
		IProcedureResponse responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
		if (Utils.flowError(messageErrorQuery +" --> updateLocalExecution", responseLocalExecution)){ 
			if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorQuery);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
			return responseLocalExecution;
		}
		if (logger.isInfoEnabled()) logger.logInfo("RESPONSE_TRANSACTION --> "+responseExecuteQuery.getProcedureResponseAsString());
		if (logger.isInfoEnabled()) logger.logInfo("RESPONSE_UPDATE_LOCAL --> "+responseLocalExecution.getProcedureResponseAsString());
		
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);
		aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}
	
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
		
		
		request.setSpName("cob_bvirtual..sp_bv_validacion");
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta")))
		Utils.copyParam("@i_cta", anOriginalRequest, request);
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_tarjeta")))
			request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tarjeta"));
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_valida_des")))
			request.addInputParam("@i_valida_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_valida_des"));

		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_tercero")))
			request.addInputParam("@i_tercero", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tercero"));
		
		//en caso de que no se envíe la moneda va 0 
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_mon")))
			Utils.copyParam("@i_mon", anOriginalRequest, request);
		else
			request.addInputParam("@i_mon", ICTSTypes.SQLINT1, "0");
		
		Utils.copyParam("@i_prod", anOriginalRequest, request);
		request.addInputParam("@i_concepto",ICTSTypes.SQLVARCHAR,"CONSULTA");
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, "1");
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(),anOriginalRequest.readValueParam("@i_login"));
		else{
			if (!Utils.isNull(anOriginalRequest.readValueParam("@i_usuario")))
				request.addInputParam("@i_login", anOriginalRequest.readParam("@i_usuario").getDataType(),anOriginalRequest.readValueParam("@i_usuario"));
		}
		
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_fecha")))
			request.addInputParam("@i_fecha", anOriginalRequest.readParam("@i_fecha").getDataType(),anOriginalRequest.readValueParam("@i_fecha"));
			
		
		if ((Integer.parseInt(request.readValueParam("@t_trn")) == TRN_CHECKING_ACCOUNT_STATEMENT) ||
			(Integer.parseInt(request.readValueParam("@t_trn")) == TRN_SAVING_ACCOUNT_STATEMENT))
		request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "S");
		
		request.addOutputParam("@o_cliente_mis", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_prod", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINT2, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_cta_des", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT2, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_fecha_ini", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_fecha_fin", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_ult_fecha", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_srv_host", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SQLCHAR, "X");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_cta_cobro", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_prod_cobro", ICTSTypes.SQLINT1	, "0");
		request.addOutputParam("@o_cod_mis", ICTSTypes.SQLINT4	, "0");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, request: "
					+ request.getProcedureRequestAsString());
		}
		
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse response = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, response: "
					+ response.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Validate local");
		}
		
		return response;
		
	}
/**
	 * Save transaction log .
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
		IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, request.readValueParam("@t_trn"));
		
		request.setSpName("cob_bvirtual..sp_bv_transaccion");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@t_trn"));
		request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
		request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
		request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "N");
		
		if (bag.get(LOG_MESSAGE) != null) {
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,bag.get(LOG_MESSAGE).toString());
		}
		
		if (anOriginalRequest.readParam("@i_login") == null) {
			request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));
		}
		else
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: "
					+ request.getProcedureRequestAsString());
		}
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, response: "
					+ pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Update local");
		}
		return pResponse;
	}


}
