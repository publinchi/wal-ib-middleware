package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransferResponse;


public interface ICoreServiceInternationalAccountTransfers {
	/**
	 * 
	 *   
	 *   <b>Ejecuta transferencia internacional</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>InternationalTransferRequest aInternationalTransferRequest = new InternationalTransferRequest();</li>

        <li>Currency currencyProduct = new Currency();</li>
        <li>currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
        
        <li>Currency destinationCurrency = new Currency();</li>
        <li>destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moncta").toString()));</li>

        <li>Product originProduct = new Product();</li>
        <li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
        <li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
        <li>originProduct.setCurrency(currencyProduct);</li>

        <li>Product destinationProduct = new Product();</li>
        <li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
        <li>destinationProduct.setCurrency(destinationCurrency);</li>

        <li>//Beneficiary Info        </li>
        <li>aInternationalTransferRequest.setBeneficiaryIDNumber(anOriginalRequest.readValueParam("@i_dniruc"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryName(anOriginalRequest.readValueParam("@i_benefi"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryFirstLastName(anOriginalRequest.readValueParam("@i_papellido"));</li>
        <li>aInternationalTransferRequest.setBeneficiarySecondLastName(anOriginalRequest.readValueParam("@i_sapellido"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryBusinessName(anOriginalRequest.readValueParam("@i_razon_social"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryCountryCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_paiben")));</li>
        <li>aInternationalTransferRequest.setBeneficiaryAddress(anOriginalRequest.readValueParam("@i_dirben"));</li>
        
        <li>//Beneficiary Bank Info           </li>
        <li>aInternationalTransferRequest.setBeneficiaryBankCode(anOriginalRequest.readValueParam("@i_bcoben"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryBankOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_ofiben")));</li>
        <li>aInternationalTransferRequest.setBeneficiaryBankName(anOriginalRequest.readValueParam("@i_nomben"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryBankSwiftAbaCode(anOriginalRequest.readValueParam("@i_swtben"));</li>
        <li>aInternationalTransferRequest.setBeneficiaryBankAddressType(anOriginalRequest.readValueParam("@i_tdirben"));     </li>         
        
        <li>//Intermediary Bank Info   </li>
        <li>aInternationalTransferRequest.setIntermediaryBankCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_bcoint")));</li>
        aInternationalTransferRequest.setIntermediaryBankOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_ofiint")));</li>
        aInternationalTransferRequest.setIntermediaryBankSwiftAbaCode(anOriginalRequest.readValueParam("@i_swtint"));</li>
        aInternationalTransferRequest.setIntermediaryBankAddressType(anOriginalRequest.readValueParam("@i_tdirint"));</li>

        <li>aInternationalTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));</li>
        <li>aInternationalTransferRequest.setOriginProduct(originProduct);</li>
        <li>aInternationalTransferRequest.setDestinationProduct(destinationProduct);</li>
        <li>aInternationalTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_observacion"));</li>
        <li>aInternationalTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
        <li>aInternationalTransferRequest.setDate(anOriginalRequest.readValueParam("@s_date"));</li>
        
        <li>aInternationalTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));</li>
        <li>aInternationalTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		
		<li>InternationalTransferResponse InternationalTransferResponse = new InternationalTransferResponse();</li>
		<li>BalanceProduct aBalanceProduct = new BalanceProduct();</li>
		<li>Product product = new Product();</li>
		<li>Office office = new Office();</li>

		<li>office.setId(1);</li>
		<li>product.setProductType(4);</li>
		
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
		<li>aBalanceProduct.setSsnHost(1111);</li>
		<li>aBalanceProduct.setSurplusAmmount(new BigDecimal(9000.00));</li>
		
		<li>InternationalTransferResponse.setBalanceProduct(aBalanceProduct);</li>
		<li>InternationalTransferResponse.setSuccess(true);</li>
		<li>InternationalTransferResponse.setReturnCode(0);</li>

		<li>InternationalTransferResponse.setReferenceNumber("651829296");</li>
		<li>InternationalTransferResponse.setReturnValue(0);</li>
		<li>InternationalTransferResponse.setConditionId(0);</li>
		<li>InternationalTransferResponse.setBranchSSN(11);</li>
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
	InternationalTransferResponse executeInternationalAccountTransfer(InternationalTransferRequest internationalAccountTransfer) throws CTSServiceException, CTSInfrastructureException;
}
