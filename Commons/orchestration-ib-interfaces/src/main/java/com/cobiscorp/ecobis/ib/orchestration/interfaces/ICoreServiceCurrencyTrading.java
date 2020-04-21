package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyTradingNegotiationResponse;


public interface ICoreServiceCurrencyTrading {
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
	 	<li>CurrencyTradingNegotiationRequest currencyTradingReq = new CurrencyTradingNegotiationRequest();</li>
		<li>currencyTradingReq.setUserBv(aRequest.readValueParam("@i_login"));</li>
		<li>currencyTradingReq.setTerminal(aRequest.readValueParam("@s_term"));</li>
		<li>currencyTradingReq.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@i_oficina")));</li>
		<li>currencyTradingReq.setClient(Integer.parseInt(aRequest.readValueParam("@i_cliente")));</li>
		<li>currencyTradingReq.setModule("BVI");</li>
		<li>currencyTradingReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));</li>
		<li>currencyTradingReq.setOptionType(aRequest.readValueParam("@i_tipo_op"));</li>
		<li>currencyTradingReq.setExecutionType(aRequest.readValueParam("@i_tipo_ejecucion"));</li>
		<li>currencyTradingReq.setOption(aRequest.readValueParam("@i_opcion"));</li>
		<li>currencyTradingReq.setPreAuthorizationSecuential(Integer.parseInt(aRequest.readValueParam("@i_sec_preautori")));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>CurrencyTradingNegotiationResponse CurrencyTradingResp = new CurrencyTradingNegotiationResponse();	</li>
		<li>CurrencyTradingResp.setQuotedRate(new Float("100.25"));</li>
		<li>CurrencyTradingResp.setAmount(new BigDecimal("300.00"));</li>
		<li>CurrencyTradingResp.setFactor(new BigDecimal("15"));</li>
		<li>CurrencyTradingResp.setCurrencyId(12);</li>
		<li>CurrencyTradingResp.setObservations("COMPRA DE DIVISA PARA NEGOCIACION");</li>
		<li>CurrencyTradingResp.setNegotiationDate("01/01/2015");</li>
		<li>CurrencyTradingResp.setOtherBuyAmount(new BigDecimal("100.00"));</li>
		<li>CurrencyTradingResp.setCurrencyName("Franco Suizo");</li>		
		<li>CurrencyTradingResp.setSuccess(true);</li>
		<li>CurrencyTradingResp.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	*/
	public CurrencyTradingNegotiationResponse getCurrencyTrading(CurrencyTradingNegotiationRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;
}
