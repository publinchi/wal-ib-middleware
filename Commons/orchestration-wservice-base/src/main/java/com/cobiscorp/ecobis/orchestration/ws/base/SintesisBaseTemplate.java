package com.cobiscorp.ecobis.orchestration.ws.base;

import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * Authentication device stock update
 * 
 * @since Jun 30, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */

public abstract class SintesisBaseTemplate extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(SintesisBaseTemplate.class);
	
	private static final String COBIS_CONTEXT = "COBIS";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
	protected static final String CLASS_NAME = " >-----> ";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
	
	protected java.util.Properties properties;

	public abstract IProcedureResponse executeWSMethod(
			Map<String, Object> aBagSPJavaOrchestration);

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		this.properties = arg0.getProperties("//property");
		if (logger.isInfoEnabled()) 
		logger.logInfo(" Connector Properties --> " + this.properties);
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */

	protected IProcedureResponse executeSteps(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseLocalExecution = null;
		IProcedureResponse wObtenerModulosResp = null;
		if (logger.isInfoEnabled()) 
		logger.logInfo("executeSteps");

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled()) 
		logger.logDebug("executeSteps anOriginalRequest: "	+ anOriginalRequest.getProcedureRequestAsString());
		/*
		 * wObtenerModulosResp = executeWSMethod(aBag); if (wObtenerModulosResp
		 * != null && wObtenerModulosResp.readValueParam("@o_coderror").equals(
		 * properties.getProperty("CODE_ERROR_SESSION"))) {
		 * 
		 * wStartSession = startSession(aBag); if (wStartSession)
		 */
		// wObtenerModulosResp.setReturnCode(FAILED);
		// wStartSession = startSession(aBag);
		// if (!wStartSession){
		//IProcedureResponse wIniciarSesionResp = (IProcedureResponse) aBag
			//	.get("wIniciarSesionResp");
		//String idOperativo = wIniciarSesionResp
			//	.readValueParam("@o_id_operativo");

		// }
		
		wObtenerModulosResp = executeWSMethod(aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wObtenerModulosResp);
		
		
		if (!(anOriginalRequest.readValueParam("@t_trn").equals("1801044")  && wObtenerModulosResp.getReturnCode() == 0))
		{
			responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError("updateLocalExecution", responseLocalExecution)) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
				return responseLocalExecution;
			}
			aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
		}
		else
		{
		if (logger.isDebugEnabled()) 
			logger.logDebug("Sin Error para trn 1801044");
		}
		
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}
	
	
	
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
		IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());
		IProcedureResponse sintesisResponse=(IProcedureResponse)bag.get(RESPONSE_TRANSACTION);
		IProcedureResponse sintesisOriginalResponse=(IProcedureResponse)bag.get(ORIGINAL_RESPONSE);
		
		IProcedureResponse pResponse = null;
		
		if (logger.isDebugEnabled()) 
		{
		logger.logDebug("Update local, anOriginalRequest: "	+ anOriginalRequest.getProcedureRequestAsString());
		logger.logDebug("Update local, request: "	+ request.getProcedureRequestAsString());
		logger.logDebug("Update local, sintesisResponse: "	+ sintesisResponse.getProcedureResponseAsString());
		}

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
		request.setSpName("cob_bvirtual..sp_bv_transaccion");
	
		request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
		request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
		
		if (anOriginalRequest.readValueParam("@s_servicio") == null )
			request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, "0");
		else
			request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
		
		if (anOriginalRequest.readValueParam("@s_servicio") == null )
			request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, "1");
		else
			request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@t_trn"));
		request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
		request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
		request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
		request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod") == null? "0" : anOriginalRequest.readValueParam("@i_prod"));		
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_causa"));

		if (logger.isInfoEnabled()) 
		logger.logInfo("Enviando la moneda del monto a consultar");
		if (anOriginalRequest.readValueParam("@i_moneda_monto") != null)
			request.addInputParam("@i_mon_2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_moneda_monto"));
		
		if (logger.isInfoEnabled()) 
		logger.logInfo("Síntesis Base Templatet --> t_trn "+ anOriginalRequest.readValueParam("@t_trn"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto") == null ? "0": anOriginalRequest.readValueParam("@i_monto") );

		if (anOriginalRequest.readParam("@i_login") == null && anOriginalRequest.readParam("@s_user") == null) 
			request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, "usuariobv");
		else
		{
			if (anOriginalRequest.readParam("@i_login") == null) 
				request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));
			else
				request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		}
		
		
		if (sintesisResponse.getReturnCode() != 0)
		{
			Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(sintesisResponse.getReturnCode())));
	    	Utils.addInputParam(request,"@s_msg", ICTSTypes.SQLVARCHAR, (sintesisResponse.getMessage(1).getMessageText()));
		}
		
		if (sintesisOriginalResponse.readValueParam("@o_coderror")!="0")
		{
			Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(sintesisOriginalResponse.readValueParam("@o_coderror"))));
			Utils.addInputParam(request,"@s_msg", ICTSTypes.SQLVARCHAR, sintesisOriginalResponse.readValueParam("@o_mensaje"));
		}
		
		request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
		request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
		request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
		request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_factor", ICTSTypes.SQLINTN, "1");									
			
	     				
		request.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR,"N");
		request.addOutputParam("@o_tipo_mensaje", ICTSTypes.SQLVARCHAR	, "F");
		request.addOutputParam("@o_numero_producto", ICTSTypes.SQLVARCHAR	, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: "	+ request.getProcedureRequestAsString());
		}
		/* Ejecuta y obtiene la respuesta */
		 pResponse = executeCoreBanking(request);
	
		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, response: "	+ pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Update local");
		}
		return pResponse;
	}
	

	private boolean startSession(Map<String, Object> aBagSPJavaOrchestration) {
		// IProcedureRequest wIniciarSesionRequest = ((IProcedureRequest)
		// aBagSPJavaOrchestration.get("anOriginalRequest"));
		IProcedureRequest wIniciarSesionRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest wIniciarSesionTMP = initProcedureRequest(wIniciarSesionRequest);
		IProcedureResponse wIniciarSesionResp;

		wIniciarSesionTMP.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "687");

		// ProcedureUtils.mapOutputParam("@o_id_operativo",
		// ICTSTypes.SQLVARCHAR, wIniciarSesionTMP, 30);

		wIniciarSesionTMP.addInputParam("@i_operacion_connector",
				ICTSTypes.SYBINT4, "687");
		wIniciarSesionTMP.addOutputParam("@o_id_operativo",
				ICTSTypes.SQLVARCHAR, "");

		wIniciarSesionTMP.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT,
				ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_TIMEOUT")));
		wIniciarSesionTMP.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER,
				ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_CATALOG_PROVIDER")));

		wIniciarSesionTMP.removeFieldInHeader("trn_virtual");
		wIniciarSesionTMP.addFieldInHeader("trn_virtual",
				ICOBISTS.HEADER_STRING_TYPE, "687");

		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) this.properties.get("CONNECTOR_TYPE")));
		wIniciarSesionResp = executeProvider(wIniciarSesionTMP,
				aBagSPJavaOrchestration);

		aBagSPJavaOrchestration.put("wIniciarSesionResp", wIniciarSesionResp);
		return wIniciarSesionResp.hasError();
	}

}
