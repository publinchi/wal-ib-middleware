package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferResponse;


/**
 * Clase que realiza las tranferencias ACH
 * 
 * 
 * **/
public interface ICoreServiceACHTransfer {
	
	/** 
	 * 
	 * 
	 * <b>Pago de transferencias ACH (Aun no se ha realizado la implementacion)</b>
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>Client client = new Client();</li>
		<li>client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));</li>
		
		<li>Product product = new Product();</li>
		<li>product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));</li>
		
	  	<li>if (product.getProductType() == 3)</li>
	  	<ul><li>notification.setId("N28");</li></ul>
		<li>else</li>
		<ul><li>notification.setId("N29");</li></ul>
		<li>NotificationDetail notificationDetail = new NotificationDetail();</li>
		<li>notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());</li>
		<li>notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());</li>
		<li>notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>notificationDetail.setAccountNumberCredit(anOriginalRequest.readValueParam("@i_cta_des"));	</li>
		<li>notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto"));</li>
		<li>notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));</li>	
		<li>notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));</li>
		<li>notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));</li>
		<li>notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_nombre_benef"));	</li>
		<li>notificationRequest.setClient(client);</li>
		<li>notificationRequest.setNotification(notification);</li>
		<li>notificationRequest.setNotificationDetail(notificationDetail);</li>
		<li>notificationRequest.setOriginProduct(product);</li>
	</ul>
	<b>
    	@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>ACHTransferResponse aACHTransferResponse = new ACHTransferResponse();</li>
		<li>BalanceProduct aBalanceProduct = new BalanceProduct();</li>
		<li>BalanceProduct aBalanceProductDest = new BalanceProduct();</li>
		
		<li>Office aOffice = new Office();</li>
		<li>aOffice.setId(1);</li>
		<li>Office aOfficeDest = new Office();</li>
		<li>aOfficeDest.setId(1);</li>
		
		<li>Product aProduct = new Product();</li>
		<li>aProduct.setProductType(4);</li>		
		<li>Product aProductDest = new Product();</li>		
		<li>aProductDest.setProductType(3);</li>
		 
		<li>aBalanceProduct.setAvailableBalance(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setAccountingBalance(new BigDecimal(225.00));</li>
		<li>aBalanceProduct.setRotateBalance(new BigDecimal(325.00));</li>
		<li>aBalanceProduct.setBalance12H(new BigDecimal(425.00));</li>
		<li>aBalanceProduct.setBalance24H(new BigDecimal(325.00));</li>
		<li>aBalanceProduct.setRemittancesBalance(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setBlockedAmmount(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setBlockedNumber(300);</li>
		<li>aBalanceProduct.setBlockedNumberAmmount(250);</li>
		<li>aBalanceProduct.setOfficeAccount(aOffice);</li>
		<li>aBalanceProduct.setProduct(aProduct);</li>
		<li>aBalanceProduct.setState("A");</li>
		<li>aBalanceProduct.setSsnHost(1234568);</li>
		<li>aBalanceProduct.setSurplusAmmount(new BigDecimal(125.00));</li>
		
		<li>aBalanceProductDest.setProduct(aProductDest);</li>
		<li>aBalanceProductDest.setOfficeAccount(aOfficeDest);</li>
		
		<li>aACHTransferResponse.setBalanceProduct(aBalanceProduct);</li>
		<li>aACHTransferResponse.setBalanceProductDest(aBalanceProductDest);</li>
		<li>aACHTransferResponse.setSuccess(true);</li>
		<li>aACHTransferResponse.setReturnCode(0);</li>

		<li>aACHTransferResponse.setReferenceNumber("651829296");</li>
		<li>aACHTransferResponse.setReturnValue(0);</li>
		<li>aACHTransferResponse.setConditionId(0);</li>
		<li>aACHTransferResponse.setBranchSSN(987654321);</li>
		
		<li>aACHTransferResponse.setDateHost("01/01/2015");</li>
		<li>aACHTransferResponse.setName("Alberto Benjamin Torres Granja");</li>
		<li>aACHTransferResponse.setDateLastMovement("01/01/2015");</li>
		<li>aACHTransferResponse.setAccountStatus("A");</li>
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
	ACHTransferResponse executeACHPayLoanTransfer(ACHTransferRequest aACHTransferRequest ) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 * <b>Ejecuta transferencia ACH de cuentas.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>ACHTransferRequest aACHTransferRequest = new ACHTransferRequest();

        <li>IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);</li>
        <li>IProcedureResponse anLocalValidationResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);</li>

        <li>Currency currencyProduct = new Currency();</li>
        <li>currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>
        
        <li>Currency destinationCurrency = new Currency();</li>
        <li>destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>

        <li>Product originProduct = new Product();</li>
        <li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());</li>
        <li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
        <li>originProduct.setCurrency(currencyProduct);</li>

        <li>Product destinationProduct = new Product();</li>
        <li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
        <li>destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
        <li>destinationProduct.setCurrency(destinationCurrency);</li>
		
        <li>aACHTransferRequest.setOriginProduct(originProduct);</li>
        <li>aACHTransferRequest.setDestinationProduct(destinationProduct);</li>
        
        <li>aACHTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val")));</li>
        <li>aACHTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_concepto"));</li>
        <li>aACHTransferRequest.setTransitRoute(anOriginalRequest.readValueParam("@i_ruta_transito"));</li>
        <li>aACHTransferRequest.setDestinationBankName(anOriginalRequest.readValueParam("@i_nom_banco_des"));</li>
        <li>aACHTransferRequest.setBeneficiaryName(anOriginalRequest.readValueParam("@i_nombre_benef"));</li>
        <li>aACHTransferRequest.setDocumentIdBeneficiary(anOriginalRequest.readValueParam("@i_doc_benef"));</li>
        <li>aACHTransferRequest.setDestinationBankPhone(anOriginalRequest.readValueParam("@i_telefono_benef"));</li>
        
        <li>aACHTransferRequest.setChargeAccount(anLocalValidationResponse.readValueParam("@o_cta_cobro"));</li>
        <li>aACHTransferRequest.setChargeProduct(Short.parseShort(anLocalValidationResponse.readValueParam("@o_prod_cobro")));</li>
        <li>aACHTransferRequest.setClientCoreCode(Integer.parseInt(anLocalValidationResponse.readValueParam("@o_cliente_mis")));</li>
        <li>aACHTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
        <li>aACHTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));</li>
        <li>aACHTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));</li>
	</ul>
	<b>
    	@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>ACHTransferResponse aACHTransferResponse = new ACHTransferResponse();</li>
		<li>BalanceProduct aBalanceProduct = new BalanceProduct();</li>
		<li>BalanceProduct aBalanceProductDest = new BalanceProduct();</li>
				
		<li>Product aProduct = new Product();</li>
		<li>Product aProductDest = new Product();</li>
		<li>Office aOffice = new Office();</li>
		<li>Office aOfficeDest = new Office();</li>		
		
		<li>aOffice.setId(1);</li>
		<li>aProduct.setProductType(4);</li>
		<li>aOfficeDest.setId(1);</li>
		<li>aProductDest.setProductType(3);</li>
		 
		<li>aBalanceProduct.setAvailableBalance(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setAccountingBalance(new BigDecimal(225.00));</li>
		<li>aBalanceProduct.setRotateBalance(new BigDecimal(325.00));</li>
		<li>aBalanceProduct.setBalance12H(new BigDecimal(425.00));</li>
		<li>aBalanceProduct.setBalance24H(new BigDecimal(325.00));</li>
		<li>aBalanceProduct.setRemittancesBalance(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setBlockedAmmount(new BigDecimal(125.00));</li>
		<li>aBalanceProduct.setBlockedNumber(300);</li>
		<li>aBalanceProduct.setBlockedNumberAmmount(250);</li>
		<li>aBalanceProduct.setOfficeAccount(aOffice);</li>
		<li>aBalanceProduct.setProduct(aProduct);</li>
		<li>aBalanceProduct.setState("A");</li>
		<li>aBalanceProduct.setSsnHost(1234568);</li>
		<li>aBalanceProduct.setSurplusAmmount(new BigDecimal(125.00));</li>
		
		<li>aBalanceProductDest.setProduct(aProductDest);</li>
		<li>aBalanceProductDest.setOfficeAccount(aOfficeDest);</li>
		
		<li>aACHTransferResponse.setBalanceProduct(aBalanceProduct);</li>
		<li>aACHTransferResponse.setBalanceProductDest(aBalanceProductDest);</li>
		<li>aACHTransferResponse.setSuccess(true);</li>
		<li>aACHTransferResponse.setReturnCode(0);</li>

		<li>aACHTransferResponse.setReferenceNumber("651829296");</li>
		<li>aACHTransferResponse.setReturnValue(0);</li>
		<li>aACHTransferResponse.setConditionId(0);</li>
		<li>aACHTransferResponse.setBranchSSN(987654321);</li>
		
		<li>aACHTransferResponse.setDateHost("01/01/2015");</li>
		<li>aACHTransferResponse.setName("Alberto Benjamin Torres Granja");</li>
		<li>aACHTransferResponse.setDateLastMovement("01/01/2015");</li>
		<li>aACHTransferResponse.setAccountStatus("A");</li>
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
	ACHTransferResponse executeACHAccountTransfer(ACHTransferRequest aACHTransferRequest) throws CTSServiceException, CTSInfrastructureException;
		
}
