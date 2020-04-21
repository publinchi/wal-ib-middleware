package com.cobiscorp.ecobis.ib.orchestration.interfaces;



import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeResponse;


/** 
 * Esta interfaz contiene metodos necesarios para realizar la solicitud
 * de Boleta de Garantia.
 * 
 * */
public interface ICoreServiceApplicationBankGuarantee {
	
	
	/**
	 * 
	 *   
	 *   <b>Ejecucion Solicitud Boleta Garantia</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String creditLine  = ""</li>
		<li>creditLine = aRequest.readValueParam("@i_credit_line");</li>
		<li>BigDecimal amount= new BigDecimal("0")</li>
		<li>amount = new BigDecimal (aRequest.readValueParam("@i_amount"));</li>
		<li>Integer currency  = 0</li>
		<li>currency = Integer.parseInt(aRequest.readValueParam("@i_currency"));</li>
		<li>Integer guaranteeTerm  = 0</li>
		<li>guaranteeTerm = aRequest.readValueParam("@i_guarantee_term");</li>
		<li>String beneficiary  = ""</li>
		<li>beneficiary = aRequest.readValueParam("@i_beneficiary");</li>
		<li>String guaranteeClass  = ""</li>
		<li>guaranteeClass = aRequest.readValueParam("@i_guarantee_class");</li>
		<li>String guaranteeType  = ""</li>
		<li>guaranteeType = aRequest.readValueParam("@i_guarantee_type");</li>
		<li>Integer entity  = 0</li>
		<li>entity = Integer.parseInt(aRequest.readValueParam("@i_entity"));</li>
		<li>String expirationDate  = ""</li>
		<li>expirationDate = aRequest.readValueParam("@i_expiration_date");</li>
		<li>String cause  = ""</li>
		<li>cause = aRequest.readValueParam("@i_cause");</li>
		<li>String guaranteeClassApp  = ""</li>
		<li>guaranteeClassApp = aRequest.readValueParam("@i_guarantee_class_app");</li>
		<li>String address  = ""</li>
		<li>address = aRequest.readValueParam("@i_address");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>ApplicationBankGuaranteeResponse aApplicationBankGuaranteeResponse = new ApplicationBankGuaranteeResponse();</li>
		<li>aSubTypeBankGuarantee.setBankGuarantee("GRB00113000612");</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>		
	**/
	public ApplicationBankGuaranteeResponse executeApplication(ApplicationBankGuaranteeRequest aApplicationBankGuaranteeRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
