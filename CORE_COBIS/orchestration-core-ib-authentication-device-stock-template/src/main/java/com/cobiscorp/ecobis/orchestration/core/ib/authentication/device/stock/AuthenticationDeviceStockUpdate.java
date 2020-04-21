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
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationDeviceStockUpdate;

@Component(name = "AuthenticationDeviceStockUpdate", immediate = false)
@Service(value = { ICoreServiceAuthenticationDeviceStockUpdate.class })
@Properties(value = { @Property(name = "service.description", value = "AuthenticationDeviceStockUpdate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticationDeviceStockUpdate") })

public class AuthenticationDeviceStockUpdate extends SPJavaOrchestrationBase
		implements ICoreServiceAuthenticationDeviceStockUpdate {

	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(AuthenticationDeviceStockUpdate.class);

	private AuthenticationDeviceStockResponse transformToAuthenticationDeviceStockUpdateResponse(
			IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToAuthenticationDeviceStockUpdateResponse");
		}

		AuthenticationDeviceStockResponse authDeviceStockResp = new AuthenticationDeviceStockResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.readValueParam("@o_access_auth_type") != null)
			authDeviceStockResp.setAccessAuthType(aProcedureResponse.readValueParam("@o_access_auth_type"));
		if (aProcedureResponse.readValueParam("@o_access_retry") != null)
			authDeviceStockResp.setAccessRetry(Short.parseShort(aProcedureResponse.readValueParam("@o_access_retry")));
		if (aProcedureResponse.readValueParam("@o_access_state") != null)
			authDeviceStockResp.setAccessState(aProcedureResponse.readValueParam("@o_access_state"));
		if (aProcedureResponse.readValueParam("@o_trx_auth_type") != null)
			authDeviceStockResp.setTrxAuthType(aProcedureResponse.readValueParam("@o_trx_auth_type"));
		if (aProcedureResponse.readValueParam("@o_trx_retry") != null)
			authDeviceStockResp.setTrxRetry(Short.parseShort(aProcedureResponse.readValueParam("@o_trx_retry")));
		if (aProcedureResponse.readValueParam("@o_trx_state") != null)
			authDeviceStockResp.setTrxState(aProcedureResponse.readValueParam("@o_trx_state"));
		if (aProcedureResponse.readValueParam("@o_mod_date") != null)
			authDeviceStockResp.setModDate(aProcedureResponse.readValueParam("@o_mod_date"));
		if (aProcedureResponse.readValueParam("@o_officer") != null)
			authDeviceStockResp.setOfficer(aProcedureResponse.readValueParam("@o_officer"));
		if (aProcedureResponse.readValueParam("@o_terminal") != null)
			authDeviceStockResp.setTerminal(aProcedureResponse.readValueParam("@o_terminal"));
		if (aProcedureResponse.readValueParam("@o_authorized") != null)
			authDeviceStockResp.setAuthorized(aProcedureResponse.readValueParam("@o_authorized"));
		if (aProcedureResponse.readValueParam("@o_cod_alterno") != null)
			authDeviceStockResp.setAlternateCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_cod_alterno")));
		if (aProcedureResponse.readValueParam("@o_ssn_original") != null)
			authDeviceStockResp.setOriginalSsn(Integer.parseInt(aProcedureResponse.readValueParam("@o_ssn_original")));
		if (aProcedureResponse.readValueParam("@o_trx_serial_number") != null)
			authDeviceStockResp.setTrxSerialNumber(aProcedureResponse.readValueParam("@o_trx_serial_number"));

		if (aProcedureResponse.readValueParam("@o_producto") != null
				&& aProcedureResponse.readValueParam("@o_cuenta") != null) {
			Product product = new Product();
			product.setProductType(Integer.parseInt(aProcedureResponse.readValueParam("@o_producto")));
			product.setProductNumber(aProcedureResponse.readValueParam("@o_cuenta"));
			Currency currency = new Currency();
			currency.setCurrencyId(Integer.parseInt(aProcedureResponse.readValueParam("@o_moneda")));
			product.setCurrency(currency);
			authDeviceStockResp.setProduct(product);
		}

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
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

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

	@Override
	public AuthenticationDeviceStockResponse updateAuthDeviceStockSB(
			AuthenticationDeviceStockRequest wAuthenticationDeviceStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: updateAuthDeviceStockSB");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				wAuthenticationDeviceStockRequest.getReferenceNumber());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18209");

		request.setSpName("cobis..sp_cons_cobro_token");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18209");
		request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, wAuthenticationDeviceStockRequest.getReferenceNumber());
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, wAuthenticationDeviceStockRequest.getUser().getName());
		request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, wAuthenticationDeviceStockRequest.getDate());
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT4,
				String.valueOf(wAuthenticationDeviceStockRequest.getOfficeCode()));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, wAuthenticationDeviceStockRequest.getTerminal());
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, wAuthenticationDeviceStockRequest.getSessionIdIB());

		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, wAuthenticationDeviceStockRequest.getClient().getId());
		request.addInputParam("@i_login", ICTSTypes.SQLCHAR, wAuthenticationDeviceStockRequest.getUserBv());
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4, "29350");
		request.addInputParam("@i_instrumento", ICTSTypes.SQLINT2,
				String.valueOf(wAuthenticationDeviceStockRequest.getAuthenticationType().getInstrumentCode()));
		request.addInputParam("@i_subtipo", ICTSTypes.SQLINT4,
				String.valueOf(wAuthenticationDeviceStockRequest.getAuthenticationType().getSubTypeCode()));
		request.addInputParam("@i_serie_desde", ICTSTypes.SQLMONEY,
				String.valueOf(wAuthenticationDeviceStockRequest.getAuthenticationType().getSeriesFrom()));
		request.addInputParam("@i_serie_hasta", ICTSTypes.SQLMONEY,
				String.valueOf(wAuthenticationDeviceStockRequest.getAuthenticationType().getSeriesTo()));
		request.addInputParam("@i_prod_destino", ICTSTypes.SQLINT1, "18");
		request.addInputParam("@i_prod_org", ICTSTypes.SQLINT1,
				String.valueOf(wAuthenticationDeviceStockRequest.getProduct().getProductType()));
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
				String.valueOf(wAuthenticationDeviceStockRequest.getProduct().getCurrency().getCurrencyId()));
		request.addInputParam("@i_cta_org", ICTSTypes.SQLVARCHAR,
				wAuthenticationDeviceStockRequest.getProduct().getProductNumber());

		if (wAuthenticationDeviceStockRequest.getAccountingParameter() != null) {
			request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR,
					wAuthenticationDeviceStockRequest.getAccountingParameter().getCause());
			request.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR,
					wAuthenticationDeviceStockRequest.getAccountingParameter().getService());
		}

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

		AuthenticationDeviceStockResponse wAuthenticationDeviceStockResponse = transformToAuthenticationDeviceStockUpdateResponse(
				pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + wAuthenticationDeviceStockResponse);
		}
		return wAuthenticationDeviceStockResponse;
	}

	@Override
	public AuthenticationDeviceStockResponse updateProviderAuthenticationDeviceStock(
			AuthenticationDeviceStockRequest wAuthDeviceStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: updateProviderAuthenticationDeviceStock");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>AuthenticationDeviceStockRequest:" + wAuthDeviceStockRequest);
		}

		AuthenticationDeviceStockResponse authDeviceStockResp = new AuthenticationDeviceStockResponse();

		authDeviceStockResp.setReturnCode(0);
		authDeviceStockResp.setSuccess(true);

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>AuthenticationDeviceStockResponse:" + authDeviceStockResp);
		}
		return authDeviceStockResp;
	}

}
