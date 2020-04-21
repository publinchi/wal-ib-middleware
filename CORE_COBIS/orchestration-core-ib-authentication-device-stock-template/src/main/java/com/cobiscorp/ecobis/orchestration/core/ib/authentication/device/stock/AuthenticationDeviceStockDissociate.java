package com.cobiscorp.ecobis.orchestration.core.ib.authentication.device.stock;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockDissociateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockDissociateResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationDeviceStockDissociate;

@Component(name = "AuthenticationDeviceStockDissociate", immediate = false)
@Service(value = { ICoreServiceAuthenticationDeviceStockDissociate.class })
@Properties(value = { @Property(name = "service.description", value = "AuthenticationDeviceStockDissociate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticationDeviceStockDissociate") })

public class AuthenticationDeviceStockDissociate extends SPJavaOrchestrationBase
		implements ICoreServiceAuthenticationDeviceStockDissociate {

	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(AuthenticationDeviceStockDissociate.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public AuthenticationDeviceStockDissociateResponse dissociateProviderAuthenticationDeviceStock(
			AuthenticationDeviceStockDissociateRequest wAuthDeviceStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: dissociateProviderAuthenticationDeviceStock");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>AuthenticationDeviceStockRequest:" + wAuthDeviceStockRequest);
		}

		AuthenticationDeviceStockDissociateResponse authDeviceStockResp = new AuthenticationDeviceStockDissociateResponse();

		authDeviceStockResp.setReturnCode(0);
		authDeviceStockResp.setSuccess(true);

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>AuthenticationDeviceStockDissociateResponse:" + authDeviceStockResp);
		}
		return authDeviceStockResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceAuthenticationDeviceStockDissociate#
	 * dissociateAuthDeviceStockSB(com.cobiscorp.ecobis.ib.application.dtos.
	 * AuthenticationDeviceStockDissociateRequest)
	 */
	@Override
	public AuthenticationDeviceStockDissociateResponse dissociateAuthDeviceStockSB(
			AuthenticationDeviceStockDissociateRequest wAuthDevStockDissociateReq)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: dissociateAuthDeviceStockSB");
		}

		IProcedureResponse response = new ProcedureResponseAS();

		if (wAuthDevStockDissociateReq.getLostDevice().equals("S"))
			response = dissociateModifySuspension(wAuthDevStockDissociateReq);
		else
			response = dissociateRemIns(wAuthDevStockDissociateReq);

		AuthenticationDeviceStockDissociateResponse authDeviceStockResp = transformToAuthenticationDeviceStockDissociateResponse(
				response);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + authDeviceStockResp);
		}

		return authDeviceStockResp;
	}

	private IProcedureResponse dissociateRemIns(AuthenticationDeviceStockDissociateRequest wAuthDevStockDissociateReq)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: dissociateRemIns");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				wAuthDevStockDissociateReq.getReferenceNumber());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "29289");

		request.setSpName("cob_sbancarios..sp_actualizar_rem_ins");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "29289");
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, wAuthDevStockDissociateReq.getReferenceNumber());
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getUser().getName());
		request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, wAuthDevStockDissociateReq.getDate());
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(wAuthDevStockDissociateReq.getOfficeCode()));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getTerminal());

		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getDissociateType());
		request.addInputParam("@i_final", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getDissociateFinal());
		request.addInputParam("@i_duplicados", ICTSTypes.SQLVARCHAR,
				wAuthDevStockDissociateReq.getDissociateDuplicate());
		request.addInputParam("@i_grupo1", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getDissociateGroup());

		request.addOutputParam("@o_ssn", ICTSTypes.SQLINT4, "0");

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

	private IProcedureResponse dissociateModifySuspension(
			AuthenticationDeviceStockDissociateRequest wAuthDevStockDissociateReq)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: dissociateModifySuspension");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				wAuthDevStockDissociateReq.getReferenceNumber());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "29315");

		request.setSpName("cob_sbancarios..sp_modificar_suspencion");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "29315");
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, wAuthDevStockDissociateReq.getReferenceNumber());
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getUser().getName());
		request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, wAuthDevStockDissociateReq.getDate());
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(wAuthDevStockDissociateReq.getOfficeCode()));
		request.addInputParam("@s_rol", ICTSTypes.SYBINT2, String.valueOf(wAuthDevStockDissociateReq.getRole()));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getTerminal());

		request.addInputParam("@i_localizacion", ICTSTypes.SQLVARCHAR,
				wAuthDevStockDissociateReq.getDissociateLocation());
		request.addInputParam("@i_final", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getDissociateFinal());
		request.addInputParam("@i_grupo1", ICTSTypes.SQLVARCHAR, wAuthDevStockDissociateReq.getDissociateGroup());

		request.addOutputParam("@o_ssn", ICTSTypes.SQLINT4, "0");

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

	private AuthenticationDeviceStockDissociateResponse transformToAuthenticationDeviceStockDissociateResponse(
			IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToAuthenticationDeviceStockDissociateResponse");
		}

		AuthenticationDeviceStockDissociateResponse authDeviceStockResp = new AuthenticationDeviceStockDissociateResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.readValueParam("@o_ssn") != null)
			authDeviceStockResp.setSsn(Integer.parseInt(aProcedureResponse.readValueParam("@o_ssn")));

		if (aProcedureResponse.getReturnCode() != 0)
			authDeviceStockResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));

		authDeviceStockResp.setReturnCode(aProcedureResponse.getReturnCode());
		authDeviceStockResp.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>authDeviceStockResp:" + authDeviceStockResp);
		}
		return authDeviceStockResp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
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
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
