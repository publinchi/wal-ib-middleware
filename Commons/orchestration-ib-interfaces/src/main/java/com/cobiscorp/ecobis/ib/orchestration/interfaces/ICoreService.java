package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SignerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SignerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;

/**
 * This interface contains the methods needed to perform basic tasks transfers.
 *
 * @since Jun 17, 2014
 * @author schancay
 * @version 1.0.0
 *
 */
public interface ICoreService {
	/**
	 * 
	 * 
	 *   <b>Valida cuentas y devuelve los saldos para actualizar en el local.</b> 
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ValidationAccountsRequest request = new ValidationAccountsRequest();</li>
		
		<li>Currency originCurrency = new Currency();</li>
		<li>originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>Product originProduct = new Product();</li>
		<li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>originProduct.setCurrency(originCurrency);</li>
		</br>
		<li>Currency destinationCurrency = new Currency();</li>
		<li>destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));</li>
		<li>Product destinationProduct = new Product();</li>
		<li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
		<li>destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
		<li>destinationProduct.setCurrency(destinationCurrency);</li>
		</br>
		<li>Secuential originSSn = new Secuential();</li>
		<li>originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());</li>
		</br>
		<li>request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		<li>request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));</li>
		<li>request.setSecuential(originSSn);</li>
		<li>request.setOriginProduct(originProduct);</li>
		<li>request.setDestinationProduct(destinationProduct);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ValidationAccountsResponse validationAccountsResponse = new ValidationAccountsResponse();</li>
		</br>
		<li>// LastBalanceProduct</li>
		<li>BalanceProduct lastBalanceProduct = new BalanceProduct();</li>
		<li>lastBalanceProduct.setAvailableBalance(new BigDecimal(10000001.0000));</li>
		<li>lastBalanceProduct.setEquityBalance(new BigDecimal(10000002.0000));</li>
		<li>lastBalanceProduct.setRotateBalance(new BigDecimal(10000003.0000));</li>
		<li>lastBalanceProduct.setBalance12H(new BigDecimal(0.0000));</li>
		<li>lastBalanceProduct.setBalance24H(new BigDecimal(0.0000));</li>
		<li>lastBalanceProduct.setRemittancesBalance(new BigDecimal(0.0000));</li>
		<li>lastBalanceProduct.setBlockedAmmount(new BigDecimal(0.0000));</li>
		<li>lastBalanceProduct.setBlockedNumber(0);</li>
		<li>Office officeLastBalanceProduct = new Office();</li>
		<li>officeLastBalanceProduct.setId(1);</li>
		<li>officeLastBalanceProduct.setDescription("");</li>
		<li>lastBalanceProduct.setOfficeAccount(officeLastBalanceProduct);</li>
		<li>lastBalanceProduct.setState("A");</li>
		<li>lastBalanceProduct.setSsnHost(123456789);</li>
		<li>lastBalanceProduct.setSurplusAmmount(new BigDecimal(0.0000));</li>
		<li>lastBalanceProduct.setIdClosed(0);</li>
		<li>lastBalanceProduct.setCashBalance(new BigDecimal(0.0000));</li>
		</br>
		<li>// OldBalanceProduct</li>
		<li>BalanceProduct oldBalanceProduct = new BalanceProduct();</li>
		<li>oldBalanceProduct.setAvailableBalance(new BigDecimal(22900.0000));</li>
		<li>oldBalanceProduct.setEquityBalance(new BigDecimal(22900.0000));</li>
		<li>oldBalanceProduct.setRotateBalance(new BigDecimal(22900.0000));</li>
		<li>oldBalanceProduct.setBalance12H(new BigDecimal(0.0000));</li>
		<li>oldBalanceProduct.setBalance24H(new BigDecimal(0.0000));</li>
		<li>oldBalanceProduct.setRemittancesBalance(new BigDecimal(0.0000));</li>
		<li>oldBalanceProduct.setBlockedAmmount(new BigDecimal(0.0000));</li>
		<li>oldBalanceProduct.setBlockedNumber(0);</li>
		<li>Office officeOldBalanceProduct = new Office();</li>
		<li>officeOldBalanceProduct.setId(1);</li>
		<li>officeOldBalanceProduct.setDescription("");</li>
		<li>oldBalanceProduct.setOfficeAccount(officeOldBalanceProduct);</li>
		<li>oldBalanceProduct.setState("A");</li>
		<li>oldBalanceProduct.setSurplusAmmount(new BigDecimal(0.0000));</li>
		
		<li>Date dateLastMovent = new SimpleDateFormat("MM/dd/yyyy hh:mma").parse("05/06/2014 02:00PM");</li>
		<li>oldBalanceProduct.setDateLastMovent("01/01/2015");</li>
		<li>lastBalanceProduct.setDateLastMovent("01/01/2015");</li>
		<li>ProductBanking productBanking = new ProductBanking();
		<li>productBanking.setId(33);</li>
		<li>productBanking.setDescription("Product Test");</li>
		<li>lastBalanceProduct.setProductBanking(productBanking);</li>
		<li>productBanking.setId(2);</li>
		<li>oldBalanceProduct.setProductBanking(productBanking);</li>

		<li>oldBalanceProduct.setCashBalance(new BigDecimal(0.0000));</li>
		
	    <li>Product originProduct = validationAccountsRequest.getOriginProduct();</li>
	    <li>lastBalanceProduct.setProduct(originProduct);</li>
				
		<li>validationAccountsResponse.setOriginBalanceProduct(lastBalanceProduct);</li>
		<li>validationAccountsResponse.setDestinationLastBalanceProduct(lastBalanceProduct);</li>
		<li>validationAccountsResponse.setDestinationOldBalanceProduct(oldBalanceProduct);</li>
		<li>Message messageResponse = new Message();</li>
		<li>messageResponse.setCode("0");</li>
		<li>messageResponse.setDescription("");</li>
		<li>validationAccountsResponse.setMessage(messageResponse);</li>
		<li>validationAccountsResponse.setSuccess(true);</li>
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
	ValidationAccountsResponse executeValidationAccounts(ValidationAccountsRequest validationAccountsRequest) throws CTSServiceException, CTSInfrastructureException;

	/** 
	 * 
	 *  
	 *  <b>Obtiene los mails de las cuentas para las notificaciones .</b>
	 *  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>OfficerByAccountRequest request = new OfficerByAccountRequest();</li>
		<li>Product product = new Product();</li>
		<li>product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>request.setProduct(product);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>OfficerByAccountResponse officerByAccountResponse = new OfficerByAccountResponse();</li>
		<li>Officer officer = new Officer();</li>
		<li>officer.setAcountEmailAdress("funcional_test_ib@cobiscorp.com");</li>
		<li>officer.setOfficerEmailAdress("funcional.testib@cobiscorp.com");</li>
		<li>officerByAccountResponse.setOfficer(officer);</li>
		<li>officerByAccountResponse.setSuccess(true);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException : Exception of Core</li>
		<li>CTSInfrastructureException : Exception of Infrastructure Core</li>
	</ul>
	 */
	OfficerByAccountResponse getOfficerByAccount(OfficerByAccountRequest transferRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/** 
	 * 
	 * 
	 * <b>Valida firmantes de la cuenta .</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>SignerRequest signerRequest = new SignerRequest();</li>
		<li>Product product = new Product();</li>
		<li>Client client = new Client();</li>
		<li>client.setId(originalRequest.readValueParam("@i_ente"));</li>
		<li>product.setProductNumber(originalRequest.readValueParam("@i_cta").toString());</li>
		<li>signerRequest.setUser(client);</li>
		<li>signerRequest.setOriginProduct(product);</li>
		<li>signerRequest.setAmmount(new BigDecimal(originalRequest.readValueParam("@i_val").toString()));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Signer signer = new Signer();</li>
		<li>signer.setCondition("");</li>
		<li>SignerResponse signerResponse = new SignerResponse();</li>
		<li>signerResponse.setSigner(signer);</li>
		<li>signerResponse.setSuccess(true);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException : Exception of Core</li>
		<li>CTSInfrastructureException : Exception of Infrastructure Core</li>
	</ul>
	 */
	SignerResponse getSignatureCondition(SignerRequest transferRequest) throws CTSServiceException, CTSInfrastructureException;

}
