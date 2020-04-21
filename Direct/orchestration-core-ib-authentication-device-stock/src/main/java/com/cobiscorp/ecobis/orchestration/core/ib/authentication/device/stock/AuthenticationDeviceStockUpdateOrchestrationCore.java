package com.cobiscorp.ecobis.orchestration.core.ib.authentication.device.stock;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockDissociateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockDissociateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationDeviceStockDissociate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationDeviceStockUpdate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;

/**
 * Authentication device stock update
 *
 * @since Dec 05, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */
@Component(name = "AuthenticationDeviceStockUpdateOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "AuthenticationDeviceStockUpdateOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticationDeviceStockUpdateOrchestrationCore") })
public class AuthenticationDeviceStockUpdateOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(AuthenticationDeviceStockUpdateOrchestrationCore.class);
	private static final String CLASS_NAME = "AuthenticationDeviceStockUpdateOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String RESPONSE_LOCAL_UPDATE = "RESPONSE_LOCAL_UPDATE";
	static final String RESPONSE_PROVIDER = "RESPONSE_PROVIDER";
	private static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceAuthenticationDeviceStockUpdate.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAuthenticationDeviceStock", unbind = "unbindCoreServiceAuthenticationDeviceStock")
	protected ICoreServiceAuthenticationDeviceStockUpdate coreServiceAuthenticationDeviceStockUpdate;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceAuthenticationDeviceStock(ICoreServiceAuthenticationDeviceStockUpdate service) {
		coreServiceAuthenticationDeviceStockUpdate = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceAuthenticationDeviceStock(ICoreServiceAuthenticationDeviceStockUpdate service) {
		coreServiceAuthenticationDeviceStockUpdate = null;
	}

	@Reference(referenceInterface = ICoreServiceAuthenticationDeviceStockDissociate.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAuthenticationDeviceStockDis", unbind = "unbindCoreServiceAuthenticationDeviceStockDis")
	protected ICoreServiceAuthenticationDeviceStockDissociate coreServiceAuthenticationDeviceStockDis;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceAuthenticationDeviceStockDis(ICoreServiceAuthenticationDeviceStockDissociate service) {
		coreServiceAuthenticationDeviceStockDis = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceAuthenticationDeviceStockDis(ICoreServiceAuthenticationDeviceStockDissociate service) {
		coreServiceAuthenticationDeviceStockDis = null;
	}

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
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
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceAuthenticationDeviceStockUpdate", coreServiceAuthenticationDeviceStockUpdate);
			mapInterfaces.put("coreServiceAuthenticationDeviceStockDis", coreServiceAuthenticationDeviceStockDis);
			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			IProcedureResponse response = executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	private IProcedureResponse executeSteps(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);
		try {
			if (anOriginalRequest.readValueParam("@i_operacion").equals("U")) // Asocia
			{

				// Actualiza inventario de proveedor
				if (logger.isInfoEnabled())
					logger.logInfo("Actualiza inventario de proveedor");
				IProcedureResponse responseProc = updateProviderTransaction(anOriginalRequest, aBag);
				aBag.put(RESPONSE_PROVIDER, responseProc);
				if (Utils.flowError("updateProviderTransaction", responseProc)) {
					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}

				// Actualiza inventario en IB
				responseProc = null;
				responseProc = updateAuthDeviceStockIB(anOriginalRequest);
				aBag.put(RESPONSE_LOCAL_UPDATE, responseProc);
				if (Utils.flowError("updateAuthDeviceStockIB", responseProc)) {
					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}

				// Actualiza inventario en SB
				responseProc = null;
				if (logger.isInfoEnabled())
					logger.logInfo("Actualiza inventario SB ");

				responseProc = updateCoreTransaction(anOriginalRequest, aBag);
				aBag.put(RESPONSE_TRANSACTION, responseProc);
				if (Utils.flowError("updateCoreTransaction", responseProc)) {
					// Si hubo error en Servicios Bancarios, deshacer asignacion
					// en IB
					anOriginalRequest.removeParam("@i_operacion");
					anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "R");
					IProcedureResponse updLocalIBResp = updateAuthDeviceStockIB(anOriginalRequest);

					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}
			}

			if (anOriginalRequest.readValueParam("@i_operacion").equals("D")) // Desasocia
			{
				// Actualiza inventario de proveedor
				if (logger.isInfoEnabled())
					logger.logInfo("Actualiza inventario de proveedor");
				IProcedureResponse responseProc = dissociateProviderTransaction(anOriginalRequest, aBag);
				aBag.put(RESPONSE_PROVIDER, responseProc);
				if (Utils.flowError("dissociateProviderTransaction", responseProc)) {
					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}

				// Actualiza inventario en SB
				responseProc = null;
				if (logger.isInfoEnabled())
					logger.logInfo("Actualiza inventario SB ");
				responseProc = dissociateCoreTransaction(anOriginalRequest, aBag);
				aBag.put(RESPONSE_TRANSACTION, responseProc);
				if (Utils.flowError("dissociateCoreTransaction", responseProc)) {
					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}

				// Actualiza inventario en IB
				responseProc = null;
				if (logger.isInfoEnabled())
					logger.logInfo("Actualiza inventario IB ");
				responseProc = updateAuthDeviceStockIB(anOriginalRequest);
				aBag.put(RESPONSE_LOCAL_UPDATE, responseProc);
				if (Utils.flowError("updateAuthDeviceStockIB", responseProc)) {
					aBag.put(RESPONSE_TRANSACTION, responseProc);
					return responseProc;
				}

			}

			return (IProcedureResponse) aBag.get(RESPONSE_TRANSACTION);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	/**
	 * name executeCoreTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse updateCoreTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		AuthenticationDeviceStockResponse wAuthDeviceStockResp = new AuthenticationDeviceStockResponse();
		AuthenticationDeviceStockRequest waAuthDeviceStockRequest = transformAuthenticationDeviceStockRequest(
				anOriginalRequest.clone(), aBag);

		try {
			AccountingParameterRequest anAccountingParameterRequest = new AccountingParameterRequest();
			anAccountingParameterRequest.setOriginalRequest(anOriginalRequest);
			anAccountingParameterRequest.setTransaction(
					Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))));
			AccountingParameterResponse response = coreServiceMonetaryTransaction
					.getAccountingParameter(anAccountingParameterRequest);
			Map<String, AccountingParameter> accountingParam = existsAccountingParameter(response,
					waAuthDeviceStockRequest.getProduct().getProductType(), "C");

			if (accountingParam != null)
				waAuthDeviceStockRequest.setAccountingParameter(accountingParam.get("ACCOUNTING_PARAM"));

			wAuthDeviceStockResp = this.coreServiceAuthenticationDeviceStockUpdate
					.updateAuthDeviceStockSB(waAuthDeviceStockRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformUpdateToProcedureResponse(wAuthDeviceStockResp, aBag);
	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse updateProviderTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBag) {

		AuthenticationDeviceStockResponse wAuthDeviceStockResp = new AuthenticationDeviceStockResponse();
		AuthenticationDeviceStockRequest waAuthDeviceStockRequest = transformAuthenticationDeviceStockRequest(
				anOriginalRequest.clone(), aBag);

		try {
			wAuthDeviceStockResp = coreServiceAuthenticationDeviceStockUpdate
					.updateProviderAuthenticationDeviceStock(waAuthDeviceStockRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformUpdateToProcedureResponse(wAuthDeviceStockResp, aBag);
	}

	/**
	 * name executeCoreTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse dissociateCoreTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBag) {

		AuthenticationDeviceStockDissociateResponse wAuthDeviceStockResp = null;
		AuthenticationDeviceStockDissociateRequest waAuthDeviceStockRequest = transformAuthenticationDeviceStockDissociateRequest(
				anOriginalRequest.clone());
		try {
			wAuthDeviceStockResp = this.coreServiceAuthenticationDeviceStockDis
					.dissociateAuthDeviceStockSB(waAuthDeviceStockRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformDissociateToProcedureResponse(wAuthDeviceStockResp, aBag);
	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse dissociateProviderTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBag) {
		AuthenticationDeviceStockDissociateRequest waAuthDeviceStockRequest = transformAuthenticationDeviceStockDissociateRequest(
				anOriginalRequest.clone());
		AuthenticationDeviceStockDissociateResponse wAuthDeviceStockResp = null;
		try {
			wAuthDeviceStockResp = this.coreServiceAuthenticationDeviceStockDis
					.dissociateProviderAuthenticationDeviceStock(waAuthDeviceStockRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformDissociateToProcedureResponse(wAuthDeviceStockResp, aBag);
	}

	/**
	 * name updateAuthDeviceStockIB
	 * 
	 * @param wAuthenticationDeviceStockRequest
	 * @return AuthenticationDeviceStockResponse
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	public IProcedureResponse updateAuthDeviceStockIB(IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: updateAuthDeviceStockIB");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				anOriginalRequest.readValueParam("@s_ssn"));
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1850002");

		request.setSpName("cob_bvirtual..sp_bv_manage_authentication");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1850002");
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_user"));
		request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, anOriginalRequest.readValueParam("@s_date"));
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_ofi"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_term"));

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, anOriginalRequest.readValueParam("@i_operacion"));
		if (anOriginalRequest.readValueParam("@i_mod_aut_trx") != null)
			request.addInputParam("@i_mod_aut_trx", ICTSTypes.SQLCHAR,
					anOriginalRequest.readValueParam("@i_mod_aut_trx"));

		if (anOriginalRequest.readValueParam("@i_mod_est_aut_trx") != null)
			request.addInputParam("@i_mod_est_aut_trx", ICTSTypes.SQLCHAR,
					anOriginalRequest.readValueParam("@i_mod_est_aut_trx"));

		if (anOriginalRequest.readValueParam("@i_login") != null)
			request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_login"));

		if (anOriginalRequest.readValueParam("@i_trx_motivo") != null)
			request.addInputParam("@i_trx_motivo", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_trx_motivo"));

		if (anOriginalRequest.readValueParam("@i_channel_id") != null)
			request.addInputParam("@i_channel_id", ICTSTypes.SQLINT1,
					anOriginalRequest.readValueParam("@i_channel_id"));

		if (anOriginalRequest.readValueParam("@i_provider") != null)
			request.addInputParam("@i_provider", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_provider"));

		if (anOriginalRequest.readValueParam("@i_access_auth_type") != null)
			request.addInputParam("@i_access_auth_type", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_access_auth_type"));

		if (anOriginalRequest.readValueParam("@i_access_retry") != null)
			request.addInputParam("@i_access_retry", ICTSTypes.SQLINT1,
					anOriginalRequest.readValueParam("@i_access_retry"));

		if (anOriginalRequest.readValueParam("@i_access_state") != null)
			request.addInputParam("@i_access_state", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_access_state"));

		if (anOriginalRequest.readValueParam("@i_trx_auth_type") != null)
			request.addInputParam("@i_trx_auth_type", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_trx_auth_type"));

		if (anOriginalRequest.readValueParam("@i_trx_retry") != null)
			request.addInputParam("@i_trx_retry", ICTSTypes.SQLINT1, anOriginalRequest.readValueParam("@i_trx_retry"));

		if (anOriginalRequest.readValueParam("@i_trx_state") != null)
			request.addInputParam("@i_trx_state", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_trx_state"));

		if (anOriginalRequest.readValueParam("@i_officer") != null)
			request.addInputParam("@i_officer", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_officer"));

		if (anOriginalRequest.readValueParam("@i_terminal") != null)
			request.addInputParam("@i_terminal", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_terminal"));

		if (anOriginalRequest.readValueParam("@i_authorized") != null)
			request.addInputParam("@i_authorized", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_authorized"));

		if (anOriginalRequest.readValueParam("@i_cod_alterno") != null)
			request.addInputParam("@i_cod_alterno", ICTSTypes.SQLINT4,
					anOriginalRequest.readValueParam("@i_cod_alterno"));

		if (anOriginalRequest.readValueParam("@i_ssn_original") != null)
			request.addInputParam("@i_ssn_original", ICTSTypes.SQLINT4,
					anOriginalRequest.readValueParam("@i_ssn_original"));

		if (anOriginalRequest.readValueParam("@i_formato_fecha") != null)
			request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT1,
					anOriginalRequest.readValueParam("@i_formato_fecha"));

		if (anOriginalRequest.readValueParam("@i_trx_serial_number") != null)
			request.addInputParam("@i_trx_serial_number", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_trx_serial_number"));

		if (anOriginalRequest.readValueParam("@i_trx_pin") != null)
			request.addInputParam("@i_trx_pin", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_trx_pin"));

		if (anOriginalRequest.readValueParam("@i_cliente") != null)
			request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_cliente"));

		if (anOriginalRequest.readValueParam("@i_user_name") != null)
			request.addInputParam("@i_user_name", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_user_name"));

		request.addOutputParam("@o_access_auth_type", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_access_retry", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_access_state", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_trx_auth_type", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_trx_retry", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_trx_state", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_mod_date", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_officer", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_terminal", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_authorized", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_cod_alterno", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_ssn_original", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_authorized", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_trx_serial_number", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		request.addOutputParam("@o_producto", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_moneda", ICTSTypes.SQLINT2, "0");
		request.addOutputParam("@o_cuenta", ICTSTypes.SQLVARCHAR,
				"                                                                ");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		return pResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST), wProcedureResponse);
		return wProcedureResponse;
	}

	/**
	 * @name transformAuthenticationDeviceStockDissociateRequest
	 * @param aRequest
	 * @return AuthenticationDeviceStockDissociateRequest
	 */
	private AuthenticationDeviceStockDissociateRequest transformAuthenticationDeviceStockDissociateRequest(
			IProcedureRequest aRequest) {
		AuthenticationDeviceStockDissociateRequest aAuthDeviceStockReq = new AuthenticationDeviceStockDissociateRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		aAuthDeviceStockReq.setReferenceNumber(aRequest.readValueParam("@s_ssn"));
		aAuthDeviceStockReq.setDate(aRequest.readValueParam("@s_date"));
		aAuthDeviceStockReq.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));
		aAuthDeviceStockReq.setRole(Integer.parseInt(aRequest.readValueParam("@s_rol")));
		aAuthDeviceStockReq.setTerminal(aRequest.readValueParam("@s_term"));
		User user = new User();
		user.setName(aRequest.readValueParam("@s_user"));
		aAuthDeviceStockReq.setUser(user);

		// Parametros para desasociar
		if (aRequest.readValueParam("@i_tipo") != null)
			aAuthDeviceStockReq.setDissociateType(aRequest.readValueParam("@i_tipo"));
		if (aRequest.readValueParam("@i_final") != null)
			aAuthDeviceStockReq.setDissociateFinal(aRequest.readValueParam("@i_final"));
		if (aRequest.readValueParam("@i_duplicados") != null)
			aAuthDeviceStockReq.setDissociateDuplicate(aRequest.readValueParam("@i_duplicados"));
		if (aRequest.readValueParam("@i_localizacion") != null)
			aAuthDeviceStockReq.setDissociateLocation(aRequest.readValueParam("@i_localizacion"));
		if (aRequest.readValueParam("@i_grupo1") != null)
			aAuthDeviceStockReq.setDissociateGroup(aRequest.readValueParam("@i_grupo1"));
		if (aRequest.readValueParam("@i_disp_perdido") != null)
			aAuthDeviceStockReq.setLostDevice(aRequest.readValueParam("@i_disp_perdido"));
		else
			aAuthDeviceStockReq.setLostDevice("N");

		return aAuthDeviceStockReq;
	}

	/**
	 * name transformAuthenticationDeviceStockRequest
	 * 
	 * @param aRequest
	 * @return AuthenticationDeviceStockRequest
	 */
	private AuthenticationDeviceStockRequest transformAuthenticationDeviceStockRequest(IProcedureRequest aRequest,
			Map<String, Object> aBag) {
		AuthenticationDeviceStockRequest aAuthDeviceStockReq = new AuthenticationDeviceStockRequest();
		AuthenticationTypeRequest aAuthenticationTypeReq = new AuthenticationTypeRequest();

		ProcedureResponseAS responseUpdLocal = (ProcedureResponseAS) aBag.get(RESPONSE_LOCAL_UPDATE);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		aAuthDeviceStockReq.setOperation(aRequest.readValueParam("@i_operacion"));
		aAuthDeviceStockReq.setReferenceNumber(aRequest.readValueParam("@s_ssn"));
		aAuthDeviceStockReq.setDate(aRequest.readValueParam("@s_date"));
		aAuthDeviceStockReq.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));

		if (aRequest.readValueParam("@i_access_auth_type") != null)
			aAuthDeviceStockReq.setAccessAuthType(aRequest.readValueParam("@i_access_auth_type"));

		if (aRequest.readValueParam("@i_mod_aut_trx") != null)
			aAuthDeviceStockReq.setModAutTrx(aRequest.readValueParam("@i_mod_aut_trx"));

		if (aRequest.readValueParam("@i_mod_est_aut_trx") != null)
			aAuthDeviceStockReq.setModEstAutTrx(aRequest.readValueParam("@i_mod_est_aut_trx"));

		if (aRequest.readValueParam("@i_login") != null)
			aAuthDeviceStockReq.setUserBv(aRequest.readValueParam("@i_login"));

		if (aRequest.readValueParam("@i_trx_motivo") != null)
			aAuthDeviceStockReq.setMotiveTrx(aRequest.readValueParam("@i_trx_motivo"));

		if (aRequest.readValueParam("@i_channel_id") != null)
			aAuthDeviceStockReq.setChannelId(aRequest.readValueParam("@i_channel_id"));

		if (aRequest.readValueParam("@i_provider") != null)
			aAuthDeviceStockReq.setProvider(aRequest.readValueParam("@i_provider"));

		if (aRequest.readValueParam("@i_access_auth_type") != null)
			aAuthDeviceStockReq.setAccessAuthType(aRequest.readValueParam("@i_access_auth_type"));

		if (aRequest.readValueParam("@i_access_retry") != null)
			aAuthDeviceStockReq.setAccessRetry(Short.valueOf(aRequest.readValueParam("@i_access_retry")));

		if (aRequest.readValueParam("@i_access_state") != null)
			aAuthDeviceStockReq.setAccessState(aRequest.readValueParam("@i_access_state"));

		if (aRequest.readValueParam("@i_trx_auth_type") != null)
			aAuthDeviceStockReq.setTrxAuthType(aRequest.readValueParam("@i_trx_auth_type"));

		if (aRequest.readValueParam("@i_trx_retry") != null)
			aAuthDeviceStockReq.setTrxRetry(Short.valueOf(aRequest.readValueParam("@i_trx_retry")));

		if (aRequest.readValueParam("@i_trx_state") != null)
			aAuthDeviceStockReq.setTrxState(aRequest.readValueParam("@i_trx_state"));

		if (aRequest.readValueParam("@i_officer") != null)
			aAuthDeviceStockReq.setOfficer(aRequest.readValueParam("@i_officer"));

		if (aRequest.readValueParam("@i_terminal") != null)
			aAuthDeviceStockReq.setTerminal(aRequest.readValueParam("@i_terminal"));
		else
			aAuthDeviceStockReq.setTerminal(aRequest.readValueParam("@s_term"));

		if (aRequest.readValueParam("@i_authorized") != null)
			aAuthDeviceStockReq.setAuthorized(aRequest.readValueParam("@i_authorized"));

		if (aRequest.readValueParam("@i_cod_alterno") != null)
			aAuthDeviceStockReq.setAlternateCode(Integer.parseInt(aRequest.readValueParam("@i_cod_alterno")));

		if (aRequest.readValueParam("@i_ssn_original") != null)
			aAuthDeviceStockReq.setOriginalSsn(Integer.parseInt(aRequest.readValueParam("@i_ssn_original")));

		if (aRequest.readValueParam("@i_formato_fecha") != null)
			aAuthDeviceStockReq.setDateFormat(Short.parseShort(aRequest.readValueParam("@i_formato_fecha")));

		if (aRequest.readValueParam("@i_trx_serial_number") != null)
			aAuthDeviceStockReq.setTrxSerialNumber(aRequest.readValueParam("@i_trx_serial_number"));

		if (aRequest.readValueParam("@i_cliente") != null) {
			Client cliente = new Client();
			cliente.setId(aRequest.readValueParam("@i_cliente"));
			aAuthDeviceStockReq.setClient(cliente);
		}

		User user = new User();
		if (aRequest.readValueParam("@i_user_name") != null)
			user.setName(aRequest.readValueParam("@i_user_name"));
		else
			user.setName(aRequest.readValueParam("@s_user"));
		aAuthDeviceStockReq.setUser(user);

		// Parametros para actualizacion token en SB
		if (aRequest.readValueParam("@i_instrumento") != null && aRequest.readValueParam("@i_subtipo") != null) {
			aAuthenticationTypeReq.setInstrumentCode(Integer.parseInt(aRequest.readValueParam("@i_instrumento")));
			aAuthenticationTypeReq.setSubTypeCode(Integer.parseInt(aRequest.readValueParam("@i_subtipo")));
			aAuthenticationTypeReq.setSeriesFrom(Double.parseDouble(aRequest.readValueParam("@i_serie_desde")));
			aAuthenticationTypeReq.setSeriesTo(Double.parseDouble(aRequest.readValueParam("@i_serie_hasta")));
			aAuthDeviceStockReq.setAuthenticationType(aAuthenticationTypeReq);
		}

		if (responseUpdLocal != null)
			if (responseUpdLocal.readValueParam("@o_cuenta") != null
					&& responseUpdLocal.readValueParam("@o_producto") != null) {
				Product product = new Product();
				Currency currency = new Currency();
				product.setProductNumber(responseUpdLocal.readValueParam("@o_cuenta"));
				product.setProductType(Integer.parseInt(responseUpdLocal.readValueParam("@o_producto")));
				currency.setCurrencyId(Integer.parseInt(responseUpdLocal.readValueParam("@o_moneda")));
				product.setCurrency(currency);
				aAuthDeviceStockReq.setProduct(product);
			}

		return aAuthDeviceStockReq;
	}

	/**
	 * name transformDissociateToProcedureResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	private IProcedureResponse transformDissociateToProcedureResponse(
			AuthenticationDeviceStockDissociateResponse wAuthDeviceDisResp,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response>>");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (wAuthDeviceDisResp.getReturnCode() != 0) {
			// Si hubo error
			wProcedureResponse = Utils.returnException(wAuthDeviceDisResp.getMessages());
			wProcedureResponse.setReturnCode(wAuthDeviceDisResp.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Respuesta transformUpdateToProcedureResponse -->"
						+ wProcedureResponse.getProcedureResponseAsString());

			return wProcedureResponse;
		}

		if (!Utils.isNull(wAuthDeviceDisResp.getSsn()))
			wProcedureResponse.addParam("@o_ssn", ICTSTypes.SYBVARCHAR, 0, String.valueOf(wAuthDeviceDisResp.getSsn()));

		wProcedureResponse.setReturnCode(wAuthDeviceDisResp.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformDissociateToProcedureResponse -->"
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	/**
	 * name transformUpdateToProcedureResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	private IProcedureResponse transformUpdateToProcedureResponse(AuthenticationDeviceStockResponse wAuthDeviceUpdResp,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response>>");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (wAuthDeviceUpdResp.getReturnCode() != 0) {
			// Si hubo error
			wProcedureResponse = Utils.returnException(wAuthDeviceUpdResp.getMessages());
			wProcedureResponse.setReturnCode(wAuthDeviceUpdResp.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Respuesta transformUpdateToProcedureResponse -->"
						+ wProcedureResponse.getProcedureResponseAsString());

			return wProcedureResponse;
		}

		if (wAuthDeviceUpdResp.getAccessAuthType() != null)
			wProcedureResponse.addParam("@o_access_auth_type", ICTSTypes.SYBVARCHAR, 0,
					wAuthDeviceUpdResp.getAccessAuthType());

		if (!Utils.isNull(wAuthDeviceUpdResp.getAccessRetry()))
			wProcedureResponse.addParam("@o_access_retry", ICTSTypes.SYBINT1, 0,
					String.valueOf(wAuthDeviceUpdResp.getAccessRetry()));

		if (wAuthDeviceUpdResp.getAccessState() != null)
			wProcedureResponse.addParam("@o_access_state", ICTSTypes.SYBCHAR, 0, wAuthDeviceUpdResp.getAccessState());

		if (wAuthDeviceUpdResp.getTrxAuthType() != null)
			wProcedureResponse.addParam("@o_trx_auth_type", ICTSTypes.SYBVARCHAR, 0,
					wAuthDeviceUpdResp.getTrxAuthType());

		if (!Utils.isNull(wAuthDeviceUpdResp.getTrxRetry()))
			wProcedureResponse.addParam("@o_trx_retry", ICTSTypes.SYBINT1, 0,
					String.valueOf(wAuthDeviceUpdResp.getTrxRetry()));

		if (wAuthDeviceUpdResp.getTrxState() != null)
			wProcedureResponse.addParam("@o_trx_state", ICTSTypes.SYBVARCHAR, 0, wAuthDeviceUpdResp.getTrxState());

		if (wAuthDeviceUpdResp.getModDate() != null)
			wProcedureResponse.addParam("@o_mod_date", ICTSTypes.SYBVARCHAR, 0, wAuthDeviceUpdResp.getModDate());

		if (wAuthDeviceUpdResp.getOfficer() != null)
			wProcedureResponse.addParam("@o_officer", ICTSTypes.SYBVARCHAR, 0, wAuthDeviceUpdResp.getOfficer());

		if (wAuthDeviceUpdResp.getTerminal() != null)
			wProcedureResponse.addParam("@o_terminal", ICTSTypes.SYBVARCHAR, 0, wAuthDeviceUpdResp.getTerminal());

		if (wAuthDeviceUpdResp.getAuthorized() != null)
			wProcedureResponse.addParam("@o_authorized", ICTSTypes.SYBVARCHAR, 0, wAuthDeviceUpdResp.getAuthorized());

		if (!Utils.isNull(wAuthDeviceUpdResp.getAlternateCode()))
			wProcedureResponse.addParam("@o_cod_alterno", ICTSTypes.SYBINT4, 0,
					String.valueOf(wAuthDeviceUpdResp.getAlternateCode()));

		if (!Utils.isNull(wAuthDeviceUpdResp.getOriginalSsn()))
			wProcedureResponse.addParam("@o_ssn_original", ICTSTypes.SYBINT4, 0,
					String.valueOf(wAuthDeviceUpdResp.getOriginalSsn()));

		if (wAuthDeviceUpdResp.getTrxSerialNumber() != null)
			wProcedureResponse.addParam("@o_trx_serial_number", ICTSTypes.SYBVARCHAR, 0,
					wAuthDeviceUpdResp.getTrxSerialNumber());

		if (wAuthDeviceUpdResp.getProduct() != null) {
			if (!Utils.isNull(wAuthDeviceUpdResp.getProduct().getProductType()))
				wProcedureResponse.addParam("@o_producto", ICTSTypes.SYBINT1, 0,
						String.valueOf(wAuthDeviceUpdResp.getProduct().getProductType()));

			if (!Utils.isNull(wAuthDeviceUpdResp.getProduct().getCurrency().getCurrencyId()))
				wProcedureResponse.addParam("@o_moneda", ICTSTypes.SYBINT2, 0,
						String.valueOf(wAuthDeviceUpdResp.getProduct().getCurrency().getCurrencyId()));

			if (wAuthDeviceUpdResp.getProduct().getProductNumber() != null)
				wProcedureResponse.addParam("@o_cuenta", ICTSTypes.SYBVARCHAR, 0,
						wAuthDeviceUpdResp.getProduct().getProductNumber());
		}

		wProcedureResponse.setReturnCode(wAuthDeviceUpdResp.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformUpdateToProcedureResponse -->"
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private Map<String, AccountingParameter> existsAccountingParameter(
			AccountingParameterResponse anAccountingParameterResponse, int product, String type) {

		Map<String, AccountingParameter> map = null;

		if (anAccountingParameterResponse.getAccountingParameters().size() == 0)
			return map;

		for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {
			if (logger.isDebugEnabled())
				logger.logDebug(" TRN: " + String.valueOf(parameter.getTransaction()) + " CAUSA: "
						+ parameter.getCause() + " TIPO :" + parameter.getTypeCost());
			if (parameter.getTypeCost().equals(type) && parameter.getProductId() == product) {
				map = new HashMap<String, AccountingParameter>();
				map.put("ACCOUNTING_PARAM", parameter);
				break;
			}
		}
		return map;
	}

}
