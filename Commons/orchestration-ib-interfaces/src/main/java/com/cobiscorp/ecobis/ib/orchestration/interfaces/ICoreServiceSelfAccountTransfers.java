package com.cobiscorp.ecobis.ib.orchestration.interfaces;


import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferResponse;

/**
 *
 * @author schancay
 * @since Aug 26, 2014
 * @version 1.0.0
 */
public interface ICoreServiceSelfAccountTransfers {

	/**
	 * Execute the transfer between accounts
	 *
	 *  
    <b>
    	@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
	    <li>Currency currencyProduct = new Currency();</li>
	    <li>Currency currencyDestProduct = new Currency();</li>
	    <li>Product originProduct = new Product();<li>
	    <li>Product destinationProduct = new Product();</li>
	    <li>SelfAccountTransferRequest selfAccountTransferRequest = new SelfAccountTransferRequest();</li>	    
      	<li>currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>currencyDestProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));</li>
		<li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
		<li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>originProduct.setCurrency(currencyProduct);</li>
		<li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
		<li>destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
		<li>destinationProduct.setCurrency(currencyDestProduct);</li>
		<li>selfAccountTransferRequest.setCommisionAmmount(new BigDecimal(localValidation.readValueParam("@o_comision").toString()));</li>
		<li>selfAccountTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));</li>
		<li>selfAccountTransferRequest.setOriginProduct(originProduct);</li>
		<li>selfAccountTransferRequest.setDestinationProduct(destinationProduct);</li>
		<li>selfAccountTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		<li>selfAccountTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));</li>
		<li>selfAccountTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	        
	<ul>
		<li>Office office = new Office();</li> 
		<li>Product product = new Product();</li>
		<li>Office officeDest = new Office();</>
		<li>Product productDest = new Product();</li>
		<li>BalanceProduct aBalanceProduct = new BalanceProduct();</li>
		<li>BalanceProduct aBalanceProductDest = new BalanceProduct();</li>
		<li>SelfAccountTransferResponse wSelfAccountTransferResponse = new SelfAccountTransferResponse();</li>
		  
		<li>office.setId(1);</li>
		<li>product.setProductType(4);</li>
		<li>officeDest.setId(1);</li>
		<li>productDest.setProductType(3);</li>
		<li/>
		<li>******Producto Origen******</li> 
		<li>aBalanceProduct.setAvailableBalance(new BigDecimal(5000.00));</li>
		<li>aBalanceProduct.setAccountingBalance(new BigDecimal(500.00));</li>
		<li>aBalanceProduct.setRotateBalance(new BigDecimal(50.00));</li>
		<li>aBalanceProduct.setBalance12H(new BigDecimal(50.00));</li>
		<li>aBalanceProduct.setBalance24H(new BigDecimal(50.00));</li>
		<li>aBalanceProduct.setRemittancesBalance(new BigDecimal(80.00));</li>
		<li>aBalanceProduct.setBlockedAmmount(new BigDecimal(0.00));</li>
		<li>aBalanceProduct.setBlockedNumber(0);</li>
		<li>aBalanceProduct.setBlockedNumberAmmount(0);</li>
		<li>aBalanceProduct.setOfficeAccount(office);</li>
		<li>aBalanceProduct.setProduct(product);</li>
		<li>aBalanceProduct.setState("A");</li>
		<li>aBalanceProduct.setSsnHost(111);</li>
		<li>aBalanceProduct.setSurplusAmmount(new BigDecimal(0.00));</li>
		<li>aBalanceProduct.setIdClosed(0);</li>
		<li>aBalanceProduct.setCashBalance(new BigDecimal(10.00));</li>
		
		<li>******Producto Destino******</li>
		<li>aBalanceProductDest.setAvailableBalance(new BigDecimal(10000.00));</li>
		<li>aBalanceProductDest.setAccountingBalance(new BigDecimal(1500.00));</li>
		<li>aBalanceProductDest.setRotateBalance(new BigDecimal(5000.00));</li>
		<li>aBalanceProductDest.setBalance12H(new BigDecimal(150.00));</li>
		<li>aBalanceProductDest.setBalance24H(new BigDecimal(150.00));</li>
		<li>aBalanceProductDest.setRemittancesBalance(new BigDecimal(50.00));</li>
		<li>aBalanceProductDest.setBlockedAmmount(new BigDecimal(0.00));</li>
		<li>aBalanceProductDest.setBlockedNumber(0);</li>
		<li>aBalanceProductDest.setBlockedNumberAmmount(0);</li>
		<li>aBalanceProductDest.setOfficeAccount(officeDest);</li>
		<li>aBalanceProductDest.setProduct(productDest);</li>
		<li>aBalanceProductDest.setState("A");</li>
		<li>aBalanceProductDest.setSurplusAmmount(new BigDecimal(0.00));</li>

		<li>******SelfAccountTransferResponse******</li>
		<li>wSelfAccountTransferResponse.setBalanceProduct(aBalanceProduct);</li>
		<li>wSelfAccountTransferResponse.setBalanceProductDest(aBalanceProductDest);</li>
		<li>wSelfAccountTransferResponse.setDateHost("01/01/2013 00:00:00");</li>
		<li>wSelfAccountTransferResponse.setName("AAAAAAAAAAAAAAA");</li>
		<li>wSelfAccountTransferResponse.setDateLastMovement("01/01/2013 00:00:00");</li>
		<li>wSelfAccountTransferResponse.setAccountStatus("A");</li>

		<li>wSelfAccountTransferResponse.setSuccess(true);</li>		
		<li>wSelfAccountTransferResponse.setReferenceNumber("651829296");</li>
		<li>wSelfAccountTransferResponse.setReturnValue(0);</li>
		<li>wSelfAccountTransferResponse.setConditionId(0);</li>
		<li>wSelfAccountTransferResponse.setBranchSSN(11);</li>		
		<li>wSelfAccountTransferResponse.setReturnCode(0);</li>
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
	SelfAccountTransferResponse executeSelfAccountTransfer(SelfAccountTransferRequest selfAccountTransfer) throws CTSServiceException, CTSInfrastructureException;
}
