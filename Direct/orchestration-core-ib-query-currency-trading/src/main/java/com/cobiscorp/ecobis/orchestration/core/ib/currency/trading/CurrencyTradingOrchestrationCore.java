package com.cobiscorp.ecobis.orchestration.core.ib.currency.trading;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCurrencyTrading;

/**
 * @author bborja
 * @since Nov 19, 2014
 * @version 1.0.0
 */
@Component(name = "CurrencyTradingOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CurrencyTradingOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CurrencyTradingOrchestrationCore") })

public class CurrencyTradingOrchestrationCore extends QueryBaseTemplate {
	@Reference(referenceInterface = ICoreServiceCurrencyTrading.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")

	// protected ICoreServiceForeignExchange coreServiceCurrencyTrading;
	protected ICoreServiceCurrencyTrading coreServiceCurrencyTrading;

	String OPERACION;
	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceCurrencyTrading service) {
		coreServiceCurrencyTrading = service;
	}

	public void unbindCoreService(ICoreServiceCurrencyTrading service) {
		coreServiceCurrencyTrading = null;
	}

	private CurrencyTradingNegotiationRequest transformcurrencyTradingRequest(IProcedureRequest aRequest) {

		CurrencyTradingNegotiationRequest currencyTradingReq = new CurrencyTradingNegotiationRequest();

		currencyTradingReq.setUserBv(aRequest.readValueParam("@i_login"));
		currencyTradingReq.setTerminal(aRequest.readValueParam("@s_term"));

		currencyTradingReq.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@i_oficina")));
		currencyTradingReq.setClient(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		currencyTradingReq.setModule("BVI");
		currencyTradingReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));
		currencyTradingReq.setOptionType(aRequest.readValueParam("@i_tipo_op"));
		currencyTradingReq.setExecutionType(aRequest.readValueParam("@i_tipo_ejecucion"));
		currencyTradingReq.setOption(aRequest.readValueParam("@i_opcion"));
		currencyTradingReq.setPreAuthorizationSecuential(Integer.parseInt(aRequest.readValueParam("@i_sec_preautori")));

		return currencyTradingReq;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		CurrencyTradingNegotiationResponse aCurrencyTradingResponse = null;
		CurrencyTradingNegotiationRequest acurrencyTradingRequest = transformcurrencyTradingRequest(request.clone());

		try {
			messageError = "getCurrencyTrading: ERROR EXECUTING SERVICE";
			messageLog = "getCurrencyTrading " + acurrencyTradingRequest.getClient();
			queryName = "getCurrencyTrading";

			acurrencyTradingRequest.setOriginalRequest(request);
			aCurrencyTradingResponse = coreServiceCurrencyTrading.getCurrencyTrading(acurrencyTradingRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		return transformProcedureResponse(aCurrencyTradingResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformProcedureResponse(CurrencyTradingNegotiationResponse aCurrencyTradingResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		wProcedureResponse.setReturnCode(aCurrencyTradingResponse.getReturnCode());

		if (!(wProcedureResponse.getReturnCode() == 0)) {
			wProcedureResponse = Utils.returnException(aCurrencyTradingResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);
			if (logger.isDebugEnabled())
				logger.logDebug(
						"Procedure Response Final ERROR-->" + wProcedureResponse.getProcedureResponseAsString());
		} else {

			wProcedureResponse.addParam("@o_cotizacion", ICTSTypes.SYBMONEY, 0,
					String.valueOf(aCurrencyTradingResponse.getQuotedRate()));
			wProcedureResponse.addParam("@o_monto", ICTSTypes.SYBMONEY, 0,
					String.valueOf(aCurrencyTradingResponse.getAmount()));
			wProcedureResponse.addParam("@o_factor", ICTSTypes.SYBMONEY, 0,
					String.valueOf(aCurrencyTradingResponse.getFactor()));
			wProcedureResponse.addParam("@o_moneda", ICTSTypes.SYBINT1, 0,
					String.valueOf(aCurrencyTradingResponse.getCurrencyId()));
			wProcedureResponse.addParam("@o_obs", ICTSTypes.SYBVARCHAR, 0, aCurrencyTradingResponse.getObservations());
			wProcedureResponse.addParam("@o_fecha_t_mas_n", ICTSTypes.SYBVARCHAR, 0,
					aCurrencyTradingResponse.getNegotiationDate());
			wProcedureResponse.addParam("@o_monto_otr_c", ICTSTypes.SYBMONEY, 0,
					String.valueOf(aCurrencyTradingResponse.getOtherBuyAmount()));
			wProcedureResponse.addParam("@o_des_moneda", ICTSTypes.SYBVARCHAR, 0,
					aCurrencyTradingResponse.getCurrencyName());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageErrorQuery = null;
		messageErrorQuery = (String) aBagSPJavaOrchestration.get(QUERY_NAME);

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceCurrencyTrading", coreServiceCurrencyTrading);

		Utils.validateComponentInstance(mapInterfaces);

		try {

			IProcedureResponse responseExecuteQuery = executeQuery(anOrginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorQuery + " --> executeQuery", responseExecuteQuery)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + messageErrorQuery);
				if (logger.isDebugEnabled())
					logger.logDebug("transformProcedureResponse Final -->"
							+ responseExecuteQuery.getProcedureResponseAsString());
			}
			;
			return responseExecuteQuery;

		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = transformProcedureResponse(
				(CurrencyTradingNegotiationResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"),
				aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}
}
