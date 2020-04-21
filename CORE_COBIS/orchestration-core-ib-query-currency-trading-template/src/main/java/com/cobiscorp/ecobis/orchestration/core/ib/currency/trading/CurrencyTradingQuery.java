package com.cobiscorp.ecobis.orchestration.core.ib.currency.trading;

import java.math.BigDecimal;
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
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCurrencyTrading;

@Component(name = "CurrencyTradingQuery", immediate = false)
@Service(value = { ICoreServiceCurrencyTrading.class })
@Properties(value = { @Property(name = "service.description", value = "CurrencyTradingQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CurrencyTradingQuery") })

public class CurrencyTradingQuery extends SPJavaOrchestrationBase implements ICoreServiceCurrencyTrading {

	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CurrencyTradingQuery.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * getCheckbook(com.cobiscorp.ecobis.ib.application.dtos.
	 * CurrencyTradingNegotiationRequest)
	 */
	@Override
	public CurrencyTradingNegotiationResponse getCurrencyTrading(
			CurrencyTradingNegotiationRequest aCurrencyTradingNegotiationRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio getCurrencyTrading");

		IProcedureRequest request = initProcedureRequest(aCurrencyTradingNegotiationRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "1875002");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_sbancarios..sp_ins_ope_divisas");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT2, "1875002");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2,
				String.valueOf(aCurrencyTradingNegotiationRequest.getOfficeCode()));
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				aCurrencyTradingNegotiationRequest.getClient().toString());
		request.addInputParam("@i_modulo", ICTSTypes.SQLVARCHAR, aCurrencyTradingNegotiationRequest.getModule());
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
				aCurrencyTradingNegotiationRequest.getCurrencyId().toString());
		request.addInputParam("@i_tipo_op", ICTSTypes.SQLVARCHAR, aCurrencyTradingNegotiationRequest.getOptionType());
		request.addInputParam("@i_tipo_ejecucion", ICTSTypes.SQLVARCHAR, "W");
		request.addInputParam("@i_opcion", ICTSTypes.SQLVARCHAR, "C");
		request.addInputParam("@i_sec_preautori", ICTSTypes.SQLINT4,
				aCurrencyTradingNegotiationRequest.getPreAuthorizationSecuential().toString());

		request.addOutputParam("@o_cotizacion", ICTSTypes.SQLFLT8i, "0.00");
		request.addOutputParam("@o_monto", ICTSTypes.SQLMONEY, "0.00");
		request.addOutputParam("@o_factor", ICTSTypes.SQLFLT8i, "0.00");
		request.addOutputParam("@o_moneda", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_obs", ICTSTypes.SQLVARCHAR,
				"                                                                                                                                                             ");
		request.addOutputParam("@o_fecha_t_mas_n", ICTSTypes.SQLVARCHAR, "01/01/2015");
		request.addOutputParam("@o_monto_otr_c", ICTSTypes.SQLFLT8i, "0.00");
		request.addOutputParam("@o_des_moneda", ICTSTypes.SQLVARCHAR,
				"                                                                                                                                                             ");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToCurrencyTradingNegotiationResponse(response);
	}

	private CurrencyTradingNegotiationResponse transformToCurrencyTradingNegotiationResponse(
			IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		CurrencyTradingNegotiationResponse CurrencyTradingResp = new CurrencyTradingNegotiationResponse();
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE TO TRANSFORM: getReturnCode" + aProcedureResponse.getReturnCode());

		CurrencyTradingResp.setReturnCode(aProcedureResponse.getReturnCode());

		if (aProcedureResponse.getReturnCode() == 0) {

			CurrencyTradingResp.setQuotedRate(Float.valueOf(aProcedureResponse.readValueParam("@o_cotizacion")));
			CurrencyTradingResp.setAmount(new BigDecimal(aProcedureResponse.readValueParam("@o_monto")));

			if (!Utils.isNull(aProcedureResponse.readValueParam("@o_factor")))
				CurrencyTradingResp.setFactor(new BigDecimal(aProcedureResponse.readValueParam("@o_factor")));

			CurrencyTradingResp.setCurrencyId(Integer.parseInt(aProcedureResponse.readValueParam("@o_moneda")));
			if (!Utils.isNull(aProcedureResponse.readValueParam("@o_obs")))
				CurrencyTradingResp.setObservations(aProcedureResponse.readValueParam("@o_obs"));

			if (!Utils.isNull(aProcedureResponse.readValueParam("@o_fecha_t_mas_n")))
				CurrencyTradingResp.setNegotiationDate(aProcedureResponse.readValueParam("@o_fecha_t_mas_n"));

			if (!Utils.isNull(aProcedureResponse.readValueParam("@o_monto_otr_c")))
				CurrencyTradingResp
						.setOtherBuyAmount(new BigDecimal(aProcedureResponse.readValueParam("@o_monto_otr_c")));
			else {
				CurrencyTradingResp.setOtherBuyAmount(new BigDecimal(0));
			}
			if (!Utils.isNull(aProcedureResponse.readValueParam("@o_des_moneda")))
				CurrencyTradingResp.setCurrencyName(aProcedureResponse.readValueParam("@o_des_moneda"));

		} else {
			CurrencyTradingResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		CurrencyTradingResp.setSuccess(CurrencyTradingResp.getReturnCode() == 0 ? true : false);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "END OF TRANSFORM: " + CurrencyTradingResp.getReturnCode());

		return CurrencyTradingResp;

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
}