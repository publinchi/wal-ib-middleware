package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsResponse;

public interface ICoreServicelTransfersInternationaDetails {
	
	/**  
	 * 
	 * 
	 * <b>Consulta de todas las transferencias enviadas o recibidas, segun criterios de busqueda.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>TransferInternationalDetailsRequest aTransferInternationalDetailsRequest = new TransferInternationalDetailsRequest();</li>
		<li>aTransferInternationalDetailsRequest.setCriteria2( aRequest.readValueParam("@i_group") );		</li>
		<li>aTransferInternationalDetailsRequest.setSequential( aRequest.readValueParam("@i_secuencial") );</li>
		<li>aTransferInternationalDetailsRequest.setInitialDate( aRequest.readValueParam("@i_fecha_ini") );</li>
		<li>aTransferInternationalDetailsRequest.setFinalDate( aRequest.readValueParam("@i_fecha_fin") );</li>
		<li>aTransferInternationalDetailsRequest.setMode( aRequest.readValueParam("@i_formato_fecha") );</li>
		<li>aTransferInternationalDetailsRequest.setProductNumber( aRequest.readValueParam("@i_account") );</li>
		<li>aTransferInternationalDetailsRequest.setNumberOfResults( aRequest.readValueParam("@i_siguiente") );</li>
		<li>aTransferInternationalDetailsRequest.setProductNumber( aRequest.readValueParam("@i_cta") );</li>
		<li>aTransferInternationalDetailsRequest.setProductId( aRequest.readValueParam("@i_prod") );</li>
		<li>aTransferInternationalDetailsRequest.setLogin(aRequest.readValueParam("@i_login"));</li>
		<li>aTransferInternationalDetailsRequest.setCurrencyId( aRequest.readValueParam("@i_mon") );</li>
		<li>if (aRequest.readValueParam("@i_group").equals("NE") || aRequest.readValueParam("@i_group").equals("SE")){</li>
		<ul>
			<li>aTransferInternationalDetailsRequest.setLastResult( aRequest.readValueParam("@i_operacion") );</li>
		</ul>
		<li>}</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>TransferInternationalDetails aTransferInternationalDetails = new TransferInternationalDetails();</li>
		<li>List<TransferInternationalDetails> aTransferInternationalDetailsList = new ArrayList<TransferInternationalDetails>();</li>
		<li>TransferInternationalDetailsResponse aTransferInternationalDetailsResponse = new TransferInternationalDetailsResponse();</li>
		
		<li>if (transferInternationalDetailsRequest.getCriteria2().equals("NR") || transferInternationalDetailsRequest.getCriteria2().equals("SR")){</li>
		<ul>
			<li>aTransferInternationalDetails.setDateTransaction("02/13/2008");</li>
			<li>aTransferInternationalDetails.setIdReference("0");</li>
			<li>aTransferInternationalDetails.setAccountDebit("01202000052");</li>
			<li>aTransferInternationalDetails.setAccountType("CUENTA CORRIENTE");</li>
			<li>aTransferInternationalDetails.setAccountName("xxxx");</li>
			<li>aTransferInternationalDetails.setAmmount(306526.00);</li>
			<li>aTransferInternationalDetails.setMoney("COLON");</li>
			<li>aTransferInternationalDetails.setReferency("042412295");</li>
			<li>aTransferInternationalDetails.setBeneficiaryName("ALMACENES EXITO, S.A.");</li>
			<li>aTransferInternationalDetails.setBeneficiaryAddressComplete("MEDELLIN, COLOMBIA");</li>
			<li>aTransferInternationalDetails.setBeneficiaryCountry("SRI LANKA");</li>
			<li>aTransferInternationalDetails.setBeneficiaryCity("CANTON NO DEFINIDO 1");</li>
			<li>aTransferInternationalDetails.setBeneficiaryAddress("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiaryAccount("01202000052");</li>
			<li>aTransferInternationalDetails.setBankBeneficiaryCountry("PANAMA");</li>
			<li>aTransferInternationalDetails.setBankBeneficiaryName("PANAMA");</li>
			<li>aTransferInternationalDetails.setBankBeneficiaryDescription("EDIFICIO WORLD TRADE CENTER PLAZA COMERCIAL PLANTA BAJA");</li>
			<li>aTransferInternationalDetails.setBankBeneficiaryAddress("HSBC BANK (PANAMA); S.A.");</li>
			<li>aTransferInternationalDetails.setBankBeneficiarySwift("MIDLPAPAXXX");</li>
			<li>aTransferInternationalDetails.setTypeAddress("SWIFT");</li>
			<li>aTransferInternationalDetails.setBankIntermediaryCountry("xxxx");</li>
			<li>aTransferInternationalDetails.setBankIntermediaryName("xxxx");</li>
			<li>aTransferInternationalDetails.setBankIntermediaryDescription("xxxx");</li>
			<li>aTransferInternationalDetails.setBankIntermediaryAddress("xxxx");</li>
			<li>aTransferInternationalDetails.setBankIntermediarySwift("xxxx");</li>
			<li>aTransferInternationalDetails.setTypeAddressIntermediary("xxxx");</li>
			<li>aTransferInternationalDetails.setCostTransaction(0.00);</li>
			<li>aTransferInternationalDetails.setBeneficiaryContinentCode("AMS");</li>
			<li>aTransferInternationalDetails.setBeneficiaryContinent("xxxx");</li>
			<li>aTransferInternationalDetails.setTransactionCode("TRR00108000022");</li> 
			<li>aTransferInternationalDetails.setMessageType("TRR");</li>
			<li>aTransferInternationalDetails.setSucursalCode(1);</li>
			<li>aTransferInternationalDetails.setSucursal("SAN JOSE");</li>
			<li>aTransferInternationalDetails.setBankBeneficiaryId(12417);</li>
			<li>aTransferInternationalDetails.setBeneficiaryCountryId(591);</li>
			<li>aTransferInternationalDetails.setBeneficiaryCityId(808);</li>
			<li>aTransferInternationalDetails.setPayerCity("CENTRAL 101");</li>
			<li>aTransferInternationalDetails.setPayerName("COMPA-IA PRUEBA SA");</li>
			<li>aTransferInternationalDetails.setId(1);</li>
			<li>aTransferInternationalDetails.setBenCountryId(41);</li>
			<li>aTransferInternationalDetails.setBenCityId(101);</li>
											
			<li>aTransferInternationalDetails.setBcoSwiftBen("xxxx");</li>
			<li>aTransferInternationalDetails.setBcoSwiftInter("xxxx");</li>
			<li>aTransferInternationalDetails.setBcoPaisBen(0);</li>
			<li>aTransferInternationalDetails.setBcoPaisInter(0);</li>
			<li>aTransferInternationalDetails.setBcoBenId(0);</li>
			<li>aTransferInternationalDetails.setBcoInterId(0);</li>
			<li>aTransferInternationalDetails.setBcoDirBenId(0);</li>
			<li>aTransferInternationalDetails.setBcoDirInterId(0);	</li>
		</ul>
		<li>}</li>
		
		<li>if (transferInternationalDetailsRequest.getCriteria2().equals("NR") || transferInternationalDetailsRequest.getCriteria2().equals("SR")){</li>
		<ul>						
			<li>aTransferInternationalDetails.setBeneficiaryFirstLastName("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiarySecondLastName("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiaryBusinessName("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiaryTypeDocument("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiaryDocumentNumber("xxxx");</li>
			<li>aTransferInternationalDetails.setCurrencyIdUSD(0);</li>
			<li>aTransferInternationalDetails.setQuote(0.0);</li>
			<li>aTransferInternationalDetails.setBeneficiaryTypeDocumentName("xxxx");</li>
			<li>aTransferInternationalDetails.setCodeNegotiation(0);</li>
			<li>aTransferInternationalDetails.setBeneficiaryEmail1("xxxx");</li>
			<li>aTransferInternationalDetails.setBeneficiaryEmail2("xxxx");</li>
		</ul>				
		<li>}</li>
			 
		<li>aTransferInternationalDetailsResponse.setColumns(wColumns);</li>
		<li>aTransferInternationalDetailsList.add(aTransferInternationalDetails);</li>
			
		<li>aTransferInternationalDetailsResponse.setTransferInternationalDetailsCollection(aTransferInternationalDetailsList);</li>	
		
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
	TransferInternationalDetailsResponse searchTransferInternationalDetails(TransferInternationalDetailsRequest transferInternationalDetailsRequest) throws CTSServiceException, CTSInfrastructureException;

}
