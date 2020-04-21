package com.cobiscorp.ecobis.orchestration.core.ib.foreign.Exchange;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceForeignExchange;

@Service(value = { ICoreServiceForeignExchange.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "ForeignExchange", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "ForeignExchange"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ForeignExchange") })
public class ForeignExchange extends SPJavaOrchestrationBase implements ICoreServiceForeignExchange {

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(ForeignExchange.class);

	private static final String SP_NAME = "cobis..sp_tr42_compra_venta_div";

	@Override
	public IProcedureResponse getCurrencyTrading(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getCurrencyTrading");
		}

		IProcedureResponse procedureResponse = new ProcedureResponseAS();
		procedureResponse.addParam("@o_quotation", ICTSTypes.SQLMONEY, 21, "100.00");
		procedureResponse.addParam("@o_ammount", ICTSTypes.SQLMONEY, 21, "101.00");
		procedureResponse.addParam("@o_factor", ICTSTypes.SQLMONEY, 21, "1.00");
		procedureResponse.addParam("@o_currency_id", ICTSTypes.SQLINT1, 3,
				procedureRequest.readValueParam("@i_moneda").toString());
		procedureResponse.addParam("@o_currency_description", ICTSTypes.SQLVARCHAR, 10, "DOLAR");
		procedureResponse.addParam("@o_observation", ICTSTypes.SQLVARCHAR, 255, "COTIZACION TEST CTS");
		procedureResponse.addParam("@o_date_negotiation", ICTSTypes.SQLDATETIME, 25, "02/05/2013");
		procedureResponse.addParam("@o_ammount_other_buy", ICTSTypes.SQLMONEY, 21, "10.00");

		procedureResponse.setReturnCode(0);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getCurrencyTrading");
		return procedureResponse;
	}

	public IProcedureResponse foreignExchange(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureResponse procedureResponse = new ProcedureResponseAS();
		procedureResponse.addParam("@o_reference", ICTSTypes.SQLINT4, 21, "123456789");
		procedureResponse.addParam("@o_ammount", ICTSTypes.SQLMONEY, 21, "123.00");
		procedureResponse.addParam("@o_exchange_rate", ICTSTypes.SQLFLT8i, 21, "123.00");

		procedureResponse.setReturnCode(0);
		return procedureResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceForeignExchange#getExchangeRates(com.cobiscorp.cobis.cts.
	 * domains.IProcedureRequest)
	 */
	@Override
	public ExchangeRateResponse getExchangeRates(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801008");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801008");
		anOriginalRequest.setSpName("cobis..sp_tr42_tipo_cambio_div");
		anOriginalRequest.addInputParam("@i_moneda1", ICTSTypes.SQLINT2, "0");
		anOriginalRequest.addInputParam("@i_moneda2", ICTSTypes.SQLINT2, "0");
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "ATMSRV3");// session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "93");// session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");// session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "::1");// session.getTerminal());
		anOriginalRequest.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, "U");// session.getCulture());
		anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, "01/02/2014");// context.getProcessDate());
		anOriginalRequest.addOutputParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, "000.00");
		anOriginalRequest.addOutputParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, "000.00");

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		return transformToExchangeRateResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private ExchangeRateResponse transformToExchangeRateResponse(IProcedureResponse aProcedureResponse) {
		ExchangeRateResponse aExchangeRateResponse = new ExchangeRateResponse();
		if (aProcedureResponse.readValueParam("@o_cotizacion_com") != null) {
			aExchangeRateResponse
					.setBuyingRate(Float.parseFloat(aProcedureResponse.readValueParam("@o_cotizacion_com")));
		}
		if (aProcedureResponse.readValueParam("@o_cotizacion_ven") != null) {
			aExchangeRateResponse
					.setSalesRate(Float.parseFloat(aProcedureResponse.readValueParam("@o_cotizacion_ven")));
		}
		aExchangeRateResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aExchangeRateResponse.setMessages(message);
		return aExchangeRateResponse;
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
	public TransferResponse foreignExchange(TransferRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse pResponse = Execution(SP_NAME, procedureRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("----------RESPONSE EXECUTE FOREIGNEXCHANGE -> " + pResponse);
		TransferResponse transferResponse = transformTransferResponse(pResponse);

		return transferResponse;
	}

	private IProcedureResponse Execution(String spName, TransferRequest transferRequest) {
		IProcedureRequest request = initProcedureRequest(transferRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801005");
		request.setSpName(spName);

		request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "1801005");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "T");
		if (transferRequest.getReference() != null)
			request.addInputParam("@i_sec_preautori", ICTSTypes.SQLINT4, transferRequest.getReference().toString());

		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				transferRequest.getUserTransferRequest().getEntityId().toString());
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				transferRequest.getUserTransferRequest().getEntityId().toString());
		request.addInputParam("@i_tipo_op", ICTSTypes.SQLCHAR, transferRequest.getSearchOption().getCriteria());
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINT2,
				transferRequest.getOriginProduct().getProductId().toString());
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR,
				transferRequest.getOriginProduct().getProductNumber());
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINT2,
				transferRequest.getOriginProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_prod_cre", ICTSTypes.SQLINT2,
				transferRequest.getDestinationProduct().getProductId().toString());
		request.addInputParam("@i_cta_cre", ICTSTypes.SQLVARCHAR,
				transferRequest.getDestinationProduct().getProductNumber());
		request.addInputParam("@i_mon_cre", ICTSTypes.SQLINT2,
				transferRequest.getDestinationProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, transferRequest.getAmmount().toString());
		request.addInputParam("@i_tasa_cambio", ICTSTypes.SQLFLT8i,
				transferRequest.getSearchOption().getExchangeRate().toString());
		request.addInputParam("@i_notas", ICTSTypes.SQLVARCHAR, transferRequest.getSearchOption().getNotes());
		request.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, transferRequest.getCause());
		request.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, transferRequest.getCauseDes());

		if (transferRequest.getCauseComi() != null) {
			request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, transferRequest.getCauseComi());
			request.addInputParam("@i_notas", ICTSTypes.SQLVARCHAR, transferRequest.getServiceCost());
		}
		request.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_monto_operacion", ICTSTypes.SQLMONEY, "0");
		request.addOutputParam("@o_tasa", ICTSTypes.SQLFLT8i, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Execution Transfer: *** " + pResponse.getProcedureResponseAsString());
		}

		return pResponse;
	}

	private TransferResponse transformTransferResponse(IProcedureResponse pResponse) {

		TransferResponse transferResponse = new TransferResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** ProcedureResponse: transformTransferResponse***" + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() == 0) {
			transferResponse.setReferenceNumber(pResponse.readValueParam("@o_referencia"));
			transferResponse.setAmount(Double.parseDouble(pResponse.readValueParam("@o_monto_operacion")));
			transferResponse.setCommission(Double.parseDouble(pResponse.readValueParam("@o_tasa")));

			transferResponse.setSuccess(true);
		} else {
			transferResponse.setSuccess(false);
		}

		transferResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		transferResponse.setMessages(message);
		if (logger.isInfoEnabled())
			logger.logInfo("----------RETORNO DE EJECUCION-> " + transferResponse.toString());
		return transferResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceForeignExchange#getExchangeRatesCore(com.cobiscorp.ecobis.ib.
	 * application.dtos.ExchangeRateRequest)
	 */
	@Override
	public ExchangeRateResponse getExchangeRatesCore(ExchangeRateRequest exchangeRateRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();

		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_bv_cons_cotizacion");

		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getServer());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getUser());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2,
				String.valueOf(exchangeRateRequest.getOfficeCode()));

		if (exchangeRateRequest.getOrgCurrency() != null)
			anOriginalRequest.addInputParam("@i_moneda_origen", ICTSTypes.SQLINT1,
					exchangeRateRequest.getOrgCurrency());
		if (exchangeRateRequest.getDestCurrency() != null)
			anOriginalRequest.addInputParam("@i_moneda_destino", ICTSTypes.SQLINT1,
					exchangeRateRequest.getDestCurrency());

		anOriginalRequest.addOutputParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, "000.00");
		anOriginalRequest.addOutputParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, "000.00");

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		return transformToExchangeRateResponse(response);
	}
}
