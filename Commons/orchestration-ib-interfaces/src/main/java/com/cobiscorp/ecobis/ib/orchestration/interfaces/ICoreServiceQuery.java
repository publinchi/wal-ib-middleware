package com.cobiscorp.ecobis.ib.orchestration.interfaces;


import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductResponse;

/**
 * This interface contains the methods needed to perform see about balances accounts.
 *
 * @since Jun 17, 2014
 * @author schancay
 * @version 1.0.0
 *
 */
public interface ICoreServiceQuery {
	
	/**  
	 * 
	 * 
	 * <b>Obtener los saldos de la cuenta de ahorro del cliente.</b>
	 * 
    <b>
    	@param
		-Parametros de entrada
	</b>
	  
	<ul>
	    <li>BalanceProductRequest balanceProductRequest = new BalanceProductRequest();</li>
        <li>Product product = new Product();</li>
		<li>Currency currency = new Currency();</li>   
      	<li>product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>product.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>product.setCurrency(currency);</li>
		<li>balanceProductRequest.setProduct(product);</li>
		<li>balanceProductRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio").toString());</li>
	</ul>
	<b>
		@return
		-Parametros de Salida
	</b>
	        
	<ul>
	    <li>BalanceProductResponse balanceProductResponse = new BalanceProductResponse();</li>
		<li>BalanceProduct balanceProduct = new BalanceProduct();</li>
        <li>Product product = new Product();</li>
		<li>Client client = new Client();</li>
		<li>product.setProductName("ACCOUNT TEST 1");</li>
		<li>client.setIdentification("1234567890");</li>
		<li>balanceProduct.setDateLastMovent("02/05/2013");</li>
		<li>balanceProduct.setState("ACTIVA");</li>
		<li>balanceProduct.setAccountingBalance(new BigDecimal(100001.00));</li>
		<li>balanceProduct.setInExchangeBalance(new BigDecimal(100002.00));</li>
		<li>balanceProduct.setAvailableBalance(new BigDecimal(100003.00));</li>
		<li>balanceProduct.setBalance24H(new BigDecimal(100004.00));</li>
		<li>balanceProduct.setBalance12H(new BigDecimal(100005.00));</li>
		<li>balanceProduct.setRotateBalance(new BigDecimal(100006.00));</li>
		<li>balanceProduct.setOpeningDate("02/01/2000");</li>
		<li>balanceProduct.setOverdraftBalance(new BigDecimal(100007.00));</li>
		<li>balanceProduct.setBlockedNumber(123);</li>
		<li>balanceProduct.setBlockedAmmount(new BigDecimal(100008.00));</li>
		<li>balanceProduct.setDeliveryAddress("ADDRESS DUMMY");</li>
		<li>balanceProduct.setCheckBalance(new BigDecimal(100009.00));</li>
		<li>balanceProduct.setEmbargoedBalance(new BigDecimal(100010.00));</li>
		<li>balanceProduct.setClient(client);</li>
		<li>balanceProduct.setProduct(product);</li>
		<li>balanceProductResponse.setBalanceProduct(balanceProduct);</li>
		<li>balanceProductResponse.setSuccess(true);</li>
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
	BalanceProductResponse getSavingAccountBalanceByAccount(BalanceProductRequest balanceProductRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *  
	 *  <b>Obtener los saldos de la cuenta corriente del cliente.</b>
	 *  
    <b>
    	@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
	    <li>BalanceProductRequest balanceProductRequest = new BalanceProductRequest();</li>
	    <li>Product product = new Product();</li>
		<li>Currency currency = new Currency();</li>	    
      	<li>product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>product.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>product.setCurrency(currency);</li>
		<li>balanceProductRequest.setProduct(product);</li>
		<li>balanceProductRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio").toString());</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	        
	<ul>
	    <li>BalanceProductResponse balanceProductResponse = new BalanceProductResponse();</li>
		<li>BalanceProduct balanceProduct = new BalanceProduct();</li>
		<li>Product product = new Product();</li>
		<li>Client client = new Client();</li>
		<li>product.setProductName("ACCOUNT TEST 1");</li>
		<li>client.setIdentification("1234567890");</li>
		<li>balanceProduct.setDateLastMovent("02/05/2013");</li>
		<li>balanceProduct.setState("ACTIVA");</li>
		<li>balanceProduct.setAccountingBalance(new BigDecimal(100001.00));</li>
		<li>balanceProduct.setInExchangeBalance(new BigDecimal(100002.00));</li>
		<li>balanceProduct.setAvailableBalance(new BigDecimal(100003.00));</li>
		<li>balanceProduct.setBalance24H(new BigDecimal(100004.00));</li>
		<li>balanceProduct.setBalance12H(new BigDecimal(100005.00));</li>
		<li>balanceProduct.setRotateBalance(new BigDecimal(100006.00));</li>
		<li>balanceProduct.setOpeningDate("02/01/2000");</li>
		<li>balanceProduct.setOverdraftBalance(new BigDecimal(100007.00));</li>
		<li>balanceProduct.setBlockedNumber(123);</li>
		<li>balanceProduct.setBlockedAmmount(new BigDecimal(100008.00));</li>
		<li>balanceProduct.setDeliveryAddress("ADDRESS DUMMY");</li>
		<li>balanceProduct.setCheckBalance(new BigDecimal(100009.00));</li>
		<li>balanceProduct.setEmbargoedBalance(new BigDecimal(100010.00));</li>
		<li>balanceProduct.setClient(client);</li>
		<li>balanceProduct.setProduct(product);</li>
		<li>balanceProductResponse.setBalanceProduct(balanceProduct);</li>
		<li>balanceProductResponse.setSuccess(true);</li>
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
	BalanceProductResponse getCheckingAccountBalanceByAccount(BalanceProductRequest balanceProductRequest) throws CTSServiceException, CTSInfrastructureException;

}
