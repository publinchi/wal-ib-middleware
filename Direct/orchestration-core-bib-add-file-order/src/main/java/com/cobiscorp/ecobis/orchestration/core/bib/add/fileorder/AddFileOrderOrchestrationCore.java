package com.cobiscorp.ecobis.orchestration.core.bib.add.fileorder;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.orchestration.core.ib.common.AccountCoreSignersValidation;

/**
 * 
 * @author gyagual
 *
 */
@Component(name = "AddFileOrderOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AddFileOrderOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AddFileOrderOrchestrationCore") })
public class AddFileOrderOrchestrationCore extends SPJavaOrchestrationBase {
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String ORIGINAL = "ORIGINAL";
	protected static final String RESPONSE_SIGNERS = "RESPONSE_SIGNERS";
	protected static final String RESPONSE_LOCAL_VALIDATION = "RESPONSE_LOCAL_VALIDATION";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected final String RESPONSE_ADD_FILE = "RESPONSE_ADD_FILE";
	private static ILogger logger = LogFactory.getLogger(AddFileOrderOrchestrationCore.class);

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		IProcedureRequest newRequest = initProcedureRequest(anOriginalRequest);
		ServerRequest serverRequest = new ServerRequest();
		Boolean activeCondition = false;

		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		if (logger.isDebugEnabled()) {
			logger.logDebug("executeJavaOrchestration");
			logger.logDebug("INICIO> anOriginalRequest" + anOriginalRequest);
		}

		newRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_account"));
		newRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, anOriginalRequest.readValueParam("@i_product"));
		newRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, anOriginalRequest.readValueParam("@i_currency"));
		newRequest.addInputParam("@i_val", ICTSTypes.SQLDECIMAL, anOriginalRequest.readValueParam("@i_total_ammount"));
		newRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_cliente"));
		newRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_file_type"));
		newRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueFieldInHeader("login"));
		newRequest.addInputParam("@i_total_ammount", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_total_ammount"));
		newRequest.addInputParam("@i_val", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_total_ammount"));
		newRequest.addInputParam("@i_authorized", ICTSTypes.SQLCHAR, anOriginalRequest.readValueParam("@i_authorized"));

		if (anOriginalRequest.readValueParam("@i_authorized") != null
				&& anOriginalRequest.readValueParam("@i_authorized").equals("S")) // requiere
																					// doble
																					// autorizacion
			newRequest.addInputParam("@i_doble_autorizacion", ICTSTypes.SQLCHAR, "S");
		else
			newRequest.addInputParam("@i_doble_autorizacion", ICTSTypes.SQLCHAR, "N");

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, newRequest);
		aBagSPJavaOrchestration.put(ORIGINAL, anOriginalRequest);

		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = new ServerResponse();
		try {
			responseServer = coreServer.getServerStatus(serverRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError("ERROR EN EJECUCION DEL SERVICIO");
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError("ERROR EN EJECUCION DEL SERVICIO");
			return null;
		}

		// no debe validar firmantes en el central
		/*
		 * IProcedureResponse responseSignersValidation = new
		 * ProcedureResponseAS(); if (responseServer.getOnLine()){
		 * responseSignersValidation = validateSigners(aBagSPJavaOrchestration);
		 * if (Utils.flowError("validateSigners", responseSignersValidation)) {
		 * aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
		 * responseSignersValidation); return responseSignersValidation; } }
		 * else{ responseSignersValidation.setReturnCode(0); }
		 * aBagSPJavaOrchestration.put(RESPONSE_SIGNERS,
		 * responseSignersValidation);
		 */

		// Validaciones locales
		IProcedureResponse responseLocalValidation = validateLocalExecution(aBagSPJavaOrchestration, false);
		aBagSPJavaOrchestration.put(RESPONSE_LOCAL_VALIDATION, responseLocalValidation);

		if (Utils.flowError("validateLocal", responseLocalValidation)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalValidation);
			return responseLocalValidation;
		}
		// si el trn tiene doble autorizacion y no se parametrizo las
		// condiciones se autoriza el archivo
		if ("0".equals(responseLocalValidation.readValueParam("@o_condicion")))
			activeCondition = false;
		else
			activeCondition = true;

		IProcedureResponse responseAddFile = addFileOrder(aBagSPJavaOrchestration, activeCondition);

		if (Utils.flowError("addFileOrder", responseAddFile)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseAddFile);
			return responseAddFile;
		}

		// actualizo el id del lote en las autorizaciones si requiere doble
		// autorizacion y no es reentry
		if ("S".equals(anOriginalRequest.readValueParam("@i_authorized"))
				&& !("Y").equals(anOriginalRequest.readValueParam("@t_rty")))
			;
		{
			aBagSPJavaOrchestration.put(RESPONSE_ADD_FILE, responseAddFile);
			IProcedureResponse responseValidation = validateLocalExecution(aBagSPJavaOrchestration, true);

			if (Utils.flowError("updateAutorization", responseValidation)) {
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidation);
				return responseValidation;
			}
		}
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseAddFile);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	private IProcedureResponse addFileOrder(Map<String, Object> aBagSPJavaOrchestration, Boolean activeCondition) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL);

		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800232");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_SSN,
				anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN));
		request.setValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH,
				anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));

		request.setSpName("cob_bvirtual..sp_bc_insert_file");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1800232");
		Utils.copyParam("@s_cliente", anOriginalRequest, request);
		Utils.copyParam("@s_culture", anOriginalRequest, request);
		request.addInputParam("@s_ssn_branch", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));
		request.addInputParam("@s_ssn", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN));

		Utils.copyParam("@s_servicio", anOriginalRequest, request);
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
		request.addInputParam("@i_username", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_username"));
		request.addInputParam("@i_server_file_name", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_server_file_name"));
		request.addInputParam("@i_user_file_name", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_user_file_name"));
		request.addInputParam("@i_file_type", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_file_type"));
		request.addInputParam("@i_planned_date", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_planned_date"));
		request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_description"));
		request.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_account"));
		request.addInputParam("@i_product", ICTSTypes.SQLINT2, anOriginalRequest.readValueParam("@i_product"));
		request.addInputParam("@i_currency", ICTSTypes.SQLINT2, anOriginalRequest.readValueParam("@i_currency"));
		request.addInputParam("@i_registered_records", ICTSTypes.SQLINT2,
				anOriginalRequest.readValueParam("@i_registered_records"));
		request.addInputParam("@i_total_ammount", ICTSTypes.SQLMONEY,
				anOriginalRequest.readValueParam("@i_total_ammount"));

		if ("Y".equals(anOriginalRequest.readValueParam("@t_rty")))
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "U");
		else
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");

		if (anOriginalRequest.readValueParam("@i_authorized") != null
				&& anOriginalRequest.readValueParam("@i_authorized").equals("S") && activeCondition) // requiere
																										// doble
																										// autorizacion
																										// y
																										// tiene
																										// condiciones
																										// parametrizadas
			request.addInputParam("@i_authorized", ICTSTypes.SQLCHAR, "N"); // lote
																			// no
																			// autorizado
		else
			request.addInputParam("@i_authorized", ICTSTypes.SQLCHAR, "A");

		// request.addInputParam("@i_authorized", ICTSTypes.SQLCHAR,
		// anOriginalRequest.readValueParam("@i_authorized"));

		request.addInputParam("@i_md5hash", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_md5hash"));
		request.addInputParam("@i_clientId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_clientId"));
		request.addOutputParam("@o_file_lote", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_ssn", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_referencia", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SYBVARCHAR, "N");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SYBINT4, "0");

		IProcedureResponse response = executeCoreBanking(request);
		if (logger.isInfoEnabled())
			logger.logInfo("response de addFileOrder  " + response);
		return response;
	}

	protected IProcedureResponse validateSigners(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse responseSigner = new ProcedureResponseAS();
		try {
			IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

			if (logger.isInfoEnabled())
				logger.logInfo("validateSigners" + originalRequest);

			responseSigner = AccountCoreSignersValidation.validateCoreSigners(coreService, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Utils.flowError("querySigners", responseSigner)) {
			aBagSPJavaOrchestration.put(RESPONSE_SIGNERS, responseSigner);
		}
		return responseSigner;
	}

	protected IProcedureResponse validateLocalExecution(Map<String, Object> aBagSPJavaOrchestration,
			Boolean actualizaAutorizacion) {
		if (logger.isInfoEnabled())
			logger.logInfo("Inicia validacion local");

		IProcedureRequest original = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL);
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		// IProcedureResponse responseSigners = (IProcedureResponse)
		// aBagSPJavaOrchestration.get(RESPONSE_SIGNERS);
		IProcedureResponse responseAddFile = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_ADD_FILE);

		IProcedureRequest request = initProcedureRequest(originalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("originalRequest" + originalRequest);
		// if (logger.isInfoEnabled()) logger.logInfo("responseSigners" +
		// responseSigners);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800048");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_SSN, original.readValueFieldInHeader(ICOBISTS.HEADER_SSN));
		request.setValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH,
				original.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));

		request.setSpName("cob_bvirtual..sp_bv_validacion");

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, originalRequest.readValueParam("@t_trn"));
		Utils.copyParam("@s_cliente", originalRequest, request);
		Utils.copyParam("@s_servicio", originalRequest, request);
		Utils.copyParam("@s_date", originalRequest, request);
		Utils.copyParam("@i_ente", originalRequest, request);
		Utils.copyParam("@s_culture", originalRequest, request);
		Utils.copyParam("@t_rty", originalRequest, request);

		request.addInputParam("@s_ssn_branch", ICTSTypes.SYBINT4,
				original.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));
		request.addInputParam("@s_ssn", ICTSTypes.SYBINT4, original.readValueFieldInHeader(ICOBISTS.HEADER_SSN));

		request.addInputParam("@i_concepto", ICTSTypes.SYBCHAR, "N");
		request.addInputParam("@i_login", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_login"));
		request.addInputParam("@i_valida_limites", ICTSTypes.SYBCHAR, "N");
		request.addInputParam("@i_valida_des", ICTSTypes.SYBVARCHAR, "N");
		request.addInputParam("@i_doble_autorizacion", ICTSTypes.SYBVARCHAR,
				originalRequest.readValueParam("@i_doble_autorizacion"));

		if (actualizaAutorizacion) {
			request.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "U");
			request.addInputParam("@i_file_lote", ICTSTypes.SYBINT4, responseAddFile.readValueParam("@o_file_lote"));
		}

		String servicio = originalRequest.readValueFieldInHeader("servicio");

		if ("1".equals(servicio)) {
			request.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, originalRequest.readValueParam("@i_cta"));
			request.addInputParam("@i_mon", ICTSTypes.SYBINT2, originalRequest.readValueParam("@i_mon"));
			request.addInputParam("@i_prod", ICTSTypes.SYBINT2, originalRequest.readValueParam("@i_prod"));
		}

		request.addInputParam("@i_val", ICTSTypes.SYBDECIMAL, originalRequest.readValueParam("@i_val"));

		/*
		 * if
		 * (!Utils.isNull(responseSigners.readParam("@o_condiciones_firmantes"))
		 * ) { request.addInputParam("@i_cond_firmas",
		 * responseSigners.readParam("@o_condiciones_firmantes").getDataType(),
		 * responseSigners.readValueParam("@o_condiciones_firmantes")); }
		 */

		request.addOutputParam("@o_cliente_mis", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_prod", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_mon", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cta_des", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_mon_des", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_srv_host", ICTSTypes.SYBVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_cta_cobro", ICTSTypes.SYBVARCHAR, "0000000000000000000000000000000");
		request.addOutputParam("@o_prod_cobro", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_cod_mis", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_clave_bv", ICTSTypes.SYBINT4, "0");

		if (logger.isDebugEnabled())
			logger.logDebug("Validacion local, request: " + request.getProcedureRequestAsString());

		// Ejecuta validacion
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug("Validacion local, response: " + pResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo("Finaliza validacion local");

		return pResponse;
	}

}
