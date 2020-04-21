package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;

/**
 * This interface contains the methods needed to get information and execute transaction of Foreign Exchange.
 * 
 * @author eortega
 * @since July 02, 2014
 * @version 1.0.0
 */
public interface ICoreServiceForeignExchange {
	/**
	 * 
	 * 
	 * Verify code authorization for pay and sale Foreign Exchange.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IProcedureRequest anOriginalRequest = new ProcedureRequestAS();</li>
		<li>anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801008");</li>
		<li>anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);</li>
		<li>anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801008");</li>
		<li>anOriginalRequest.setSpName("cob_bvirtual..sp_tr42_tipo_cambio_div");</li>
		<li>anOriginalRequest.addInputParam("@i_moneda1", ICTSTypes.SQLINT2, "0");</li>
		<li>anOriginalRequest.addInputParam("@i_moneda2", ICTSTypes.SQLINT2, "0");</li>
		<li>anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "ATMSRV3");//session.getServer());</li>
		<li>anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "93");//session.getOffice());</li>
		<li>anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");//session.getUser());</li>
		<li>anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "::1");//session.getTerminal());</li>
		<li>anOriginalRequest.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, "U");//session.getCulture());</li>
		<li>anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, "01/02/2014");//context.getProcessDate());</li>
		<li>anOriginalRequest.addOutputParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, "000.00");</li>
		<li>anOriginalRequest.addOutputParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, "000.00");</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();</li>
		<li>exchangeRateResponse.setSalesRate(new Float(200.00));</li>
		<li>exchangeRateResponse.setBuyingRate(new Float(493.00));</li>
		<li>exchangeRateResponse.setSuccess(true);</li>
		<li>exchangeRateResponse.setReturnCode(0);</li>
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
	public ExchangeRateResponse getExchangeRates(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
	/**
	 * 
	 * 
	 * Execute Foreign Exchange.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Currency currencyDeb = new Currency();</li>
		<li>currencyDeb.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon_deb")));</li>
		
		<li>Product productDeb = new Product();</li>
		<li>productDeb.setProductId(Integer.parseInt(request.readValueParam("@i_prod_deb")));</li>
		<li>productDeb.setProductNumber(request.readValueParam("@i_cta_deb"));		</li>
		<li>productDeb.setCurrency(currencyDeb);</li>
		<li>productDeb.setProductAlias(request.readValueParam("@i_alias"));</li>	
		
		<li>Currency currencyCre = new Currency();</li>
		<li>currencyCre.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon_cre")));</li>
		
		<li>Product productCre = new Product();</li>
		<li>productCre.setProductId(Integer.parseInt(request.readValueParam("@i_prod_cre")));</li>
		<li>productCre.setProductNumber(request.readValueParam("@i_cta_cre"));		</li>
		<li>productCre.setCurrency(currencyCre);</li>

		<li>SearchOption searchOption = new SearchOption();</li>
		<li>searchOption.setCriteria(request.readValueParam("@i_tipo_op"));</li>
		<li>searchOption.setExchangeRate(Double.parseDouble(request.readValueParam("@i_tasa_cambio")));</li>
		<li>searchOption.setNotes(request.readValueParam("@i_notas"));</li>
		
		<li>User transferUser = new User();</li>
		<li>transferUser.setEntityId(Integer.parseInt(request.readValueParam("@i_cliente")));</li>
		<li>transferUser.setName(request.readValueParam("@i_login"));</li>
		<li>transferUser.setServiceId(Integer.parseInt(request.readValueParam("@i_servicio")));</li>
		
		<li>TransferResponse transferResponse = new TransferResponse();</li>
		<li>transferResponse.setReference(Integer.parseInt(request.readValueParam("@o_referencia")));</li>
		<li>transferResponse.setAmount(Double.parseDouble(request.readValueParam("@o_monto_operacion")));</li>
		<li>transferResponse.setCommission(Double.parseDouble(request.readValueParam("@o_tasa")));</li>
		
		<li>BigDecimal bdAmount = new BigDecimal(request.readValueParam("@i_monto"));</li>
		
		<li>TransferRequest transferRequest = new TransferRequest();</li>
		<li>transferRequest.setReference(Integer.parseInt(request.readValueParam("@i_sec_preautori")));</li>
		<li>transferRequest.setAmmount(bdAmount);</li>
		<li>transferRequest.setOperation(request.readValueParam("@i_operacion"));</li>		
		<li>transferRequest.setOriginProduct(productDeb);</li>
		<li>transferRequest.setDestinationProduct(productCre);</li>
		<li>transferRequest.setSearchOption(searchOption);</li>
		<li>transferRequest.setUserTransferRequest(transferUser);</li>
		<li>transferRequest.setTransferResponse(transferResponse);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>TransferResponse transferResponse = new TransferResponse();</li>
		<li>transferResponse.setReferenceNumber("660192125");</li>
		<li>transferResponse.setAmount(1507.0);</li>
		<li>transferResponse.setCommission(15.07);</li>
		<li>transferResponse.setReturnCode(0);</li>
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
	public TransferResponse foreignExchange(TransferRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IProcedureRequest procedureRequest = new IProcedureRequest();</li>	
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IProcedureResponse procedureResponse  = new ProcedureResponseAS();</li>
		<li>procedureResponse.addParam("@o_reference", ICTSTypes.SQLINT4, 21, "123456789");</li>
		<li>procedureResponse.addParam("@o_ammount", ICTSTypes.SQLMONEY, 21, "123.00");</li>
		<li>procedureResponse.addParam("@o_exchange_rate", ICTSTypes.SQLFLT8i, 21, "123.00");</li>
		<li>procedureResponse.setReturnCode(0);</li>
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
	public IProcedureResponse foreignExchange(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;
	
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
		<li>IProcedureResponse procedureResponse = new ProcedureResponseAS();</li>
		<li>procedureResponse.addParam("@o_quotation", ICTSTypes.SQLMONEY, 21, "100.00");</li>
		<li>procedureResponse.addParam("@o_ammount", ICTSTypes.SQLMONEY, 21, "101.00");</li>
		<li>procedureResponse.addParam("@o_factor", ICTSTypes.SQLMONEY, 21, "1.00");</li>
		<li>procedureResponse.addParam("@o_currency_id", ICTSTypes.SQLINT1, 3, procedureRequest.readValueParam("@i_moneda").toString());</li>
		<li>procedureResponse.addParam("@o_currency_description", ICTSTypes.SQLVARCHAR, 10, "DOLAR");</li>
		<li>procedureResponse.addParam("@o_observation", ICTSTypes.SQLVARCHAR, 255, "COTIZACION TEST CTS");</li>
		<li>procedureResponse.addParam("@o_date_negotiation", ICTSTypes.SQLDATETIME, 25, "02/05/2013");</li>
		<li>procedureResponse.addParam("@o_ammount_other_buy", ICTSTypes.SQLMONEY, 21, "10.00");</li>
		<li>procedureResponse.setReturnCode(0);</li>
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
	public IProcedureResponse getCurrencyTrading(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;

	public ExchangeRateResponse getExchangeRatesCore(ExchangeRateRequest exchangeRateRequest) throws CTSServiceException, CTSInfrastructureException;
}
