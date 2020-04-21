package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;

/**
 * This interface contains the methods for get consolidate information accounts by client
 *
 * @author schancay
 * @since Jul 3, 2014
 * @version 1.0.0
 */
public interface ICoreConsolidateAccountsQuery {
	/**
	 * 
	 * <b>Consulta cuentas de corrientes del Core por cliente.</b>
	 * 
	 * 	
 	<div>

	<b>
		@param
		-ParametrosDeEntrada
	</b>
		  
	<ul>
		<li>int transaccion = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>int idProducto = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));</li>
		
		<li>ConsolidateRequest <b>consolidateRequest</b> = new ConsolidateRequest();</li>
		
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		
		<li>ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);</li>
		
		<li>Client client = new Client();</li>
		<li>client.setId(informationClient.getClient().getIdCustomer());</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));</li>
		
		<li>consolidateRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn").toString());</li>
		<li>consolidateRequest.setClient(client);</li>
		<li>consolidateRequest.setCurrency(currency);</li>
		<li>consolidateRequest.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ConsolidateResponse <b>consolidateResponse</b> = new ConsolidateResponse();</li>
		
		<li>Currency currencyCRC = new Currency();</li>
		<li>currencyCRC.setCurrencyId(0);</li>
		<li>currencyCRC.setCurrencyDescription("COLON");</li>
		<li>currencyCRC.setCurrencyNemonic("CRC");</li>
		<li>Currency currencyDOL = new Currency();</li>
		
		<li>Currency currencyDOL = new Currency();</li>
		<li>currencyDOL.setCurrencyId(0);</li>
		<li>currencyDOL.setCurrencyDescription("DOLOR");</li>
		<li>currencyDOL.setCurrencyNemonic("DOL");</li>
		
		<li>BalanceProduct balance = new BalanceProduct();</li>
		<li>balance.setRotateBalance(new BigDecimal(200001.00));</li>
		<li>balance.setEquityBalance(new BigDecimal(200002.00));</li>
		<li>balance.setAvailableBalance(new BigDecimal(10001.00));</li>
		
		<li>BalanceProduct balancePrevious = new BalanceProduct();</li>
		<li>balancePrevious.setRotateBalance(new BigDecimal(200003.00));</li>
		<li>balancePrevious.setEquityBalance(new BigDecimal(200004.00));</li>
		
		<li>ProductConsolidate productCollection1 = new ProductConsolidate();</li>
		<li>Product product1 = new Product();</li>
		<li>product1.setProductType(3);</li>
		<li>product1.setProductDescription("CUENTA CORRIENTE");</li>
		<li>product1.setProductNemonic("CTE");</li>
		<li>product1.setProductNumber("10410108275405315");</li>
		<li>product1.setProductAlias("CTA CTE TESTPRUEBA 1");</li>
		<li>productCollection1.setCurrency(currencyCRC);</li>
		<li>productCollection1.setProduct(product1);</li>
		<li>productCollection1.setBalance(balance);</li>
		<li>productCollection1.setPreviousBalance(balancePrevious);</li>
		
		<li>ProductConsolidate productCollection2 = new ProductConsolidate();</li>
		<li>Product product2 = new Product();</li>
		<li>product2.setProductType(3);</li>
		<li>product2.setProductDescription("CUENTA CORRIENTE");</li>
		<li>product2.setProductNemonic("CTE");</li>
		<li>product2.setProductNumber("10410108640405011");</li>
		<li>product2.setProductAlias("CTA CTE TESTPRUEBA 2");</li>
		<li>productCollection2.setCurrency(currencyDOL);</li>
		<li>productCollection2.setProduct(product2);</li>
		<li>productCollection2.setBalance(balance);</li>
		<li>productCollection2.setPreviousBalance(balancePrevious);</li>
		
		<li>List<ProductConsolidate> products = new ArrayList<ProductConsolidate>();</li>
		<li>products.add(productCollection1);</li>
		<li>products.add(productCollection2);</li>
		
		<li>consolidateResponse.setProductCollection(products);</li>
		<li>consolidateResponse.setSuccess(true);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	</div>
	 */
	ConsolidateResponse getConsolidateCheckingAccountByClient(ConsolidateRequest consolidateRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * <b>Consulta cuentas de ahorros del Core por cliente.</b>
	 * 
	 * 	
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>int transaccion = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>int idProducto = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));</li>
		
		<li>ConsolidateRequest <b>consolidateRequest</b> = new ConsolidateRequest();</li>
		
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		
		<li>ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);</li>
		
		<li>Client client = new Client();</li>
		<li>client.setId(informationClient.getClient().getIdCustomer());</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));</li>
		
		<li>consolidateRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn").toString());</li>
		<li>consolidateRequest.setClient(client);</li>
		<li>consolidateRequest.setCurrency(currency);</li>
		<li>consolidateRequest.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ConsolidateResponse <b>consolidateResponse</b> = new ConsolidateResponse();</li>

		<li>Currency currencyCRC = new Currency();</li>
		<li>currencyCRC.setCurrencyId(0);</li>
		<li>currencyCRC.setCurrencyDescription("COLON");</li>
		<li>currencyCRC.setCurrencyNemonic("CRC");</li>
		
		<li>Currency currencyDOL = new Currency();</li>
		<li>currencyDOL.setCurrencyId(0);</li>
		<li>currencyDOL.setCurrencyDescription("DOLOR");</li>
		<li>currencyDOL.setCurrencyNemonic("DOL");</li>
		
		<li>BalanceProduct balance = new BalanceProduct();</li>
		<li>balance.setRotateBalance(new BigDecimal(200001.00));</li>
		<li>balance.setEquityBalance(new BigDecimal(200002.00));</li>
		<li>balance.setAvailableBalance(new BigDecimal(10001.00));</li>
		
		<li>BalanceProduct balancePrevious = new BalanceProduct();</li>
		<li>balancePrevious.setRotateBalance(new BigDecimal(200003.00));</li>
		<li>balancePrevious.setEquityBalance(new BigDecimal(200004.00));</li>
		
		<li>ProductConsolidate productCollection1 = new ProductConsolidate();</li>
		<li>Product product1 = new Product();</li>
		<li>product1.setProductType(4);</li>
		<li>product1.setProductDescription("CUENTA AHORRO");</li>
		<li>product1.setProductNemonic("AHO");</li>
		<li>product1.setProductNumber("10410000005233616");</li>
		<li>product1.setProductAlias("CTA AHO TESTPRUEBA 1");</li>
		<li>productCollection1.setCurrency(currencyCRC);</li>
		<li>productCollection1.setProduct(product1);</li>
		<li>productCollection1.setBalance(balance);</li>
		<li>productCollection1.setPreviousBalance(balancePrevious);</li>

		<li>ProductConsolidate productCollection2 = new ProductConsolidate();</li>
		<li>Product product2 = new Product();</li>
		<li>product2.setProductType(4);</li>
		<li>product2.setProductDescription("CUENTA AHORRO");</li>
		<li>product2.setProductNemonic("AHO");</li>
		<li>product2.setProductNumber("10410000005233617");</li>
		<li>product2.setProductAlias("CTA AHO TESTPRUEBA 2");</li>
		<li>productCollection2.setCurrency(currencyDOL);</li>
		<li>productCollection2.setProduct(product2);</li>
		<li>productCollection2.setBalance(balance);</li>
		<li>productCollection2.setPreviousBalance(balancePrevious);</li>

		<li>List<ProductConsolidate> products = new ArrayList<ProductConsolidate>();</li>
		<li>products.add(productCollection1);</li>
		<li>products.add(productCollection2);</li>
		
		<li>consolidateResponse.setProductCollection(products);</li>
		<li>consolidateResponse.setSuccess(true);</li>
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
	ConsolidateResponse getConsolidateSavingAccountByClient(ConsolidateRequest consolidateRequest) throws CTSServiceException, CTSInfrastructureException;
		
	/**
	 * 
	 * 
	 * 	<b>Consulta cuentas de pr&eacutestamos del Core por cliente.</b>
	 * 
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>int transaccion = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>int idProducto = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));</li>
		
		<li>ConsolidateRequest <b>consolidateRequest</b> = new ConsolidateRequest();</li>
		
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		
		<li>ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);</li>
		
		<li>Client client = new Client();</li>
		<li>client.setId(informationClient.getClient().getIdCustomer());</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));</li>
		
		<li>consolidateRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn").toString());</li>
		<li>consolidateRequest.setClient(client);</li>
		<li>consolidateRequest.setCurrency(currency);</li>
		<li>consolidateRequest.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ConsolidateResponse consolidateResponse = new ConsolidateResponse();</li>

		<li>Currency currencyCRC = new Currency();</li>
		<li>currencyCRC.setCurrencyId(0);</li>
		<li>currencyCRC.setCurrencyDescription("COLON");</li>
		<li>currencyCRC.setCurrencyNemonic("CRC");</li>
		
		<li>Currency currencyDOL = new Currency();</li>
		<li>currencyDOL.setCurrencyId(0);</li>
		<li>currencyDOL.setCurrencyDescription("DOLAR");</li>
		<li>currencyDOL.setCurrencyNemonic("DOL");</li>
		
		<li>BalanceProduct balance = new BalanceProduct();</li>
		<li>balance.setRotateBalance(new BigDecimal(200001.00));</li>
		<li>balance.setEquityBalance(new BigDecimal(200002.00));</li>
		<li>balance.setTotalBalance(new BigDecimal(200002.00));</li>
		<li>balance.setAvailableBalance(new BigDecimal(10001.00));</li>

		<li>BalanceProduct balancePrevious = new BalanceProduct();</li>
		<li>balancePrevious.setRotateBalance(new BigDecimal(200003.00));</li>
		<li>balancePrevious.setEquityBalance(new BigDecimal(200004.00));</li>
		<li>balancePrevious.setTotalBalance(new BigDecimal(200002.00));</li>
		
		<li>ProductConsolidate productCollection1 = new ProductConsolidate();</li>
		<li>Product product1 = new Product();</li>
		<li>product1.setProductType(7);</li>
		<li>product1.setProductDescription("PRESTAMO");</li>
		<li>product1.setProductNemonic("CAR");</li>
		<li>product1.setProductNumber("10407740700943818");</li>
		<li>product1.setProductAlias("PRESTAMO TESTPRUEBA 1");</li>
		<li>productCollection1.setCurrency(currencyCRC);</li>
		<li>productCollection1.setProduct(product1);</li>
		<li>productCollection1.setBalance(balance);</li>
		<li>productCollection1.setPreviousBalance(balancePrevious);</li>

		<li>ProductConsolidate productCollection2 = new ProductConsolidate();</li>
		<li>Product product2 = new Product();</li>
		<li>product2.setProductType(7);</li>
		<li>product2.setProductDescription("PRESTAMO");</li>
		<li>product2.setProductNemonic("CAR");</li>
		<li>product2.setProductNumber("10407740700943819");</li>
		<li>product2.setProductAlias("PRESTAMO TESTPRUEBA 2");</li>
		<li>productCollection2.setCurrency(currencyDOL);</li>
		<li>productCollection2.setProduct(product2);</li>
		<li>productCollection2.setBalance(balance);</li>
		<li>productCollection2.setPreviousBalance(balancePrevious);</li>

		<li>List<ProductConsolidate> products = new ArrayList<ProductConsolidate>();</li>
		<li>products.add(productCollection1);</li>
		<li>products.add(productCollection2);</li>
		
		<li>consolidateResponse.setProductCollection(products);</li>
		<li>consolidateResponse.setSuccess(true);</li>
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
	ConsolidateResponse getConsolidateLoanAccountByClient(ConsolidateRequest consolidateRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * <b>Consulta cuentas de plazo fijos del Core por cliente.</b>
	 * 
	 * 	
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>int transaccion = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>int idProducto = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));</li>
		
		<li>ConsolidateRequest <b>consolidateRequest</b> = new ConsolidateRequest();</li>
		
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		
		<li>ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);</li>
		
		<li>Client client = new Client();</li>
		<li>client.setId(informationClient.getClient().getIdCustomer());</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));</li>
		
		<li>consolidateRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn").toString());</li>
		<li>consolidateRequest.setClient(client);</li>
		<li>consolidateRequest.setCurrency(currency);</li>
		<li>consolidateRequest.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ConsolidateResponse <b>consolidateResponse</b> = new ConsolidateResponse();</li>

		<li>Currency currencyCRC = new Currency();</li>
		<li>currencyCRC.setCurrencyId(0);</li>
		<li>currencyCRC.setCurrencyDescription("COLON");</li>
		<li>currencyCRC.setCurrencyNemonic("CRC");</li>
		
		<li>Currency currencyDOL = new Currency();</li>
		<li>currencyDOL.setCurrencyId(0);</li>
		<li>currencyDOL.setCurrencyDescription("DOLOR");</li>
		<li>currencyDOL.setCurrencyNemonic("DOL");</li>
		
		<li>BalanceProduct balance = new BalanceProduct();</li>
		<li>balance.setRotateBalance(new BigDecimal(200001.00));</li>
		<li>balance.setEquityBalance(new BigDecimal(200002.00));</li>
		<li>balance.setAvailableBalance(new BigDecimal(10001.00));</li>
		
		<li>BalanceProduct balancePrevious = new BalanceProduct();</li>
		<li>balancePrevious.setRotateBalance(new BigDecimal(200003.00));</li>
		<li>balancePrevious.setEquityBalance(new BigDecimal(200004.00));</li>
		
		<li>ProductConsolidate productCollection1 = new ProductConsolidate();</li>
		<li>Product product1 = new Product();</li>
		<li>product1.setProductType(14);</li>
		<li>product1.setProductDescription("DEPOSITO PLAZO FIJO");</li>
		<li>product1.setProductNemonic("DPF");</li>
		<li>product1.setProductNumber("01414052458");</li>
		<li>product1.setProductAlias("CTA DEPOSITO PLAZO FIJO 1");</li>
		<li>productCollection1.setCurrency(currencyCRC);</li>
		<li>productCollection1.setProduct(product1);</li>
		<li>productCollection1.setBalance(balance);</li>
		<li>productCollection1.setPreviousBalance(balancePrevious);</li>

		<li>ProductConsolidate productCollection2 = new ProductConsolidate();</li>
		<li>Product product2 = new Product();</li>
		<li>product2.setProductType(14);</li>
		<li>product2.setProductDescription("DEPOSITO PLAZO FIJO");</li>
		<li>product2.setProductNemonic("DPF");</li>
		<li>product2.setProductNumber("01414052459");</li>
		<li>product2.setProductAlias("CTA DEPOSITO PLAZO FIJO 2");</li>
		<li>productCollection2.setCurrency(currencyDOL);</li>
		<li>productCollection2.setProduct(product2);</li>
		<li>productCollection2.setBalance(balance);</li>
		<li>productCollection2.setPreviousBalance(balancePrevious);</li>

		<li>List<ProductConsolidate> products = new ArrayList<ProductConsolidate>();</li>
		<li>products.add(productCollection1);</li>
		<li>products.add(productCollection2);</li>
		
		<li>consolidateResponse.setProductCollection(products);
		<li>consolidateResponse.setSuccess(true);
		
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
	ConsolidateResponse getConsolidateFixedTermDepositAccountByClient(ConsolidateRequest consolidateRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * <b>Consulta cuentas de tarjetas de Cr&eacutedito del Core por cliente.</b>
	 * 
	 * 	
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>int transaccion = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>int idProducto = Integer.parseInt(anOriginalRequest.readValueParam("@i_prod"));</li>
		
		<li>ConsolidateRequest <b>consolidateRequest</b> = new ConsolidateRequest();</li>
		
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		
		<li>ClientInformationResponse informationClient = coreServiceClient.getInformationClientBv(clientRequest, map);</li>
		
		<li>Client client = new Client();</li>
		<li>client.setId(informationClient.getClient().getIdCustomer());</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moneda")));</li>
		
		<li>consolidateRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn").toString());</li>
		<li>consolidateRequest.setClient(client);</li>
		<li>consolidateRequest.setCurrency(currency);</li>
		<li>consolidateRequest.setNumberRegister(Integer.parseInt(anOriginalRequest.readValueParam("@i_nregistros").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ConsolidateResponse <b>consolidateResponse</b> = new ConsolidateResponse();</li>

		<li>Currency currencyCRC = new Currency();</li>
		<li>currencyCRC.setCurrencyId(0);</li>
		<li>currencyCRC.setCurrencyDescription("COLON")</li>;
		<li>currencyCRC.setCurrencyNemonic("CRC");</li>
		
		<li>BalanceProduct balance = new BalanceProduct();</li>
		<li>balance.setRotateBalance(new BigDecimal(200001.00));</li>
		<li>balance.setEquityBalance(new BigDecimal(200002.00));</li>
		<li>balance.setAvailableBalance(new BigDecimal(10001.00) );</li>
		
		<li>BalanceProduct balancePrevious = new BalanceProduct();</li>
		<li>balancePrevious.setRotateBalance(new BigDecimal(200003.00));</li>
		<li>balancePrevious.setEquityBalance(new BigDecimal(200004.00));</li>
		
		<li>ProductConsolidate productCollection1 = new ProductConsolidate();</li>
		<li>Product product1 = new Product();</li>
		<li>product1 = product;</li>
		<li>product1.setProductNumber("5041960000000013");</li>
		<li>product1.setProductAlias("CTA CTE TESTPRUEBA 1");</li>
		<li>product1.setProductType(83);</li>
		<li>product1.setProductNemonic("TCR");</li>
		<li>product1.setProductDescription("CREDIT CARD");</li>
		<li>productCollection1.setCurrency(currencyCRC);</li>
		<li>productCollection1.setProduct(product1);</li>
		<li>productCollection1.setBalance(balance);</li>
		<li>productCollection1.setPreviousBalance(balancePrevious);</li>

		<li>List<ProductConsolidate> products = new ArrayList<ProductConsolidate>();</li>
		<li>products.add(productCollection1);</li>

		<li>consolidateResponse.setProductCollection(products);</li>
		<li>consolidateResponse.setSuccess(true);</li>
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
	ConsolidateResponse getConsolidateCreditCardByClient(ConsolidateRequest consolidateRequest) throws CTSServiceException, CTSInfrastructureException;

}
