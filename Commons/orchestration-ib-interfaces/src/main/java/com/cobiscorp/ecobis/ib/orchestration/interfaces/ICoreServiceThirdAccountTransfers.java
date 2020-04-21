package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyTransferResponse;
/***
 * 
 * @author gyagual
 * @since Sep 03, 2014
 * @version 1.0.0
 *
 */
public interface ICoreServiceThirdAccountTransfers {

	/**
	 * 
	 *   
	 *   <b>Ejecuta transferencia a terceros.</b>
	 *   
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
                    <li>ThirdPartyTransferRequest ThirdPartyTransferRequest = new ThirdPartyTransferRequest();</li>
                  
                    <li>Currency currencyProduct = new Currency();</li>
				    <li>currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>

					<li>Product originProduct = new Product();</li>
					<li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
					<li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
					<li>originProduct.setCurrency(currencyProduct);</li>

					<li>Product destinationProduct = new Product();</li>
					<li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
					<li>destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
					<li>destinationProduct.setCurrency(currencyProduct);</li>

					<li>ThirdPartyTransferRequest.setCommisionAmmount(new BigDecimal(localValidation.readValueParam("@o_comision").toString()));</li>
					<li>ThirdPartyTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));</li>
					<li>ThirdPartyTransferRequest.setOriginProduct(originProduct);</li>
					<li>ThirdPartyTransferRequest.setDestinationProduct(destinationProduct);</li>

					<li>ThirdPartyTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>

					<li>ThirdPartyTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));</li>

					<li>ThirdPartyTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));</li>

    </ul>
    <b>
                   @return
        		   -ParametrosDeSalida-
    </b>    
    <ul>
                   <li>BalanceProduct aBalanceProduct = new BalanceProduct();</li>
                   <li>BalanceProduct aBalanceProductDest = new BalanceProduct();</li>
                   <li>Product product = new Product();</li>
                   <li>Product productDest = new Product();</li>
                   <li>Office office = new Office();</li>
                   <li>Office officeDest = new Office();</li>
                   
                   <li>office.setId(1);</li>
                   <li>product.setProductType(4);</li>
                   <li>officeDest.setId(1);</li>
                   <li>productDest.setProductType(3);</li>
                   
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
                   
                   <li>thirdPartyTransferResponse.setBalanceProduct(aBalanceProduct);</li>
                   <li>thirdPartyTransferResponse.setBalanceProductDest(aBalanceProductDest);</li>
                   <li>thirdPartyTransferResponse.setDateHost("01/01/2013 00:00:00");</li>
                   <li>thirdPartyTransferResponse.setName("AAAAAAAAAAAAAAA");</li>
                   <li>thirdPartyTransferResponse.setDateLastMovement("01/01/2013 00:00:00");</li>
                   <li>thirdPartyTransferResponse.setAccountStatus("A");</li>
                   <li>thirdPartyTransferResponse.setSuccess(true);</li>
                   <li>thirdPartyTransferResponse.setReferenceNumber("651829296");</li>
                   <li>thirdPartyTransferResponse.setReturnValue(0);</li>
                   <li>thirdPartyTransferResponse.setConditionId(0);</li>
                   <li>thirdPartyTransferResponse.setBranchSSN(11);</li>
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

	
	ThirdPartyTransferResponse executeThirdAccountTransfer(ThirdPartyTransferRequest thirdAccountTransfer) throws CTSServiceException, CTSInfrastructureException;
}
