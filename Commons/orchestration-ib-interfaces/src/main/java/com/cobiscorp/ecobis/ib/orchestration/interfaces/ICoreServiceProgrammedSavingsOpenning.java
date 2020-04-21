package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsResponse;


public interface ICoreServiceProgrammedSavingsOpenning {
	
		
	/**
	 * Agrega Cuenta
	 *
	 *  
    <b>
    	@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>	    
      	<li>ProgrammedSavingsAddProgrammedSavingsRequest  aProgrammedSavingsAddProgrammedSavingsRequest = new ProgrammedSavingsAddProgrammedSavingsRequest();</li>
		<li>User user = new User();</li>
		<li>ProgrammedSavings programmedSavings = new ProgrammedSavings();</li>
		<li>Product product = new Product();</li>
		<li>Product product2 = new Product();</li>
		<li>Currency currency = new Currency();</li>
		<li>Currency currency2 = new Currency();</li>
      	<li>user.setEntityId(new Integer(aRequest.readValueParam("@i_cliente")));</li>
		<li>user.setName(aRequest.readValueParam("@i_login"));</li>
		<li>programmedSavings.setFrequency(aRequest.readValueParam("@i_frecuencia"));</li>
		<li>programmedSavings.setAmount(new Double(aRequest.readValueParam("@i_monto")));</li>
		<li>currency.setCurrencyId(new Integer(aRequest.readValueParam("@i_moneda")));</li>
		<li>programmedSavings.setCurrency(currency);</li>
		<li>programmedSavings.setConcept(aRequest.readValueParam("@i_concepto"));</li>
		<li>programmedSavings.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));</li>
		<li>programmedSavings.setTerm(aRequest.readValueParam("@i_plazo"));</li>
		<li>programmedSavings.setExpirationDate(aRequest.readValueParam("@i_fecha_ven"));</li>
		<li>programmedSavings.setMail(aRequest.readValueParam("@i_mail"));</li>
		<li>programmedSavings.setBranch(new Integer(aRequest.readValueParam("@i_sucursal")));</li>
		<li>programmedSavings.setIdBeneficiary(aRequest.readValueParam("@i_id_beneficiary"));</li>
		<li>product2.setProductNumber(aRequest.readValueParam("@i_cta_deb"));</li>
		<li>product2.setProductId(new Integer(aRequest.readValueParam("@i_prod_deb")));</li>
		<li>currency2.setCurrencyId(new Integer(aRequest.readValueParam("@i_mon_deb")));</li>
		<li>product2.setCurrency(currency2);</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta_ahoprog"));</li>
		<li>aProgrammedSavingsAddProgrammedSavingsRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
		<li>aProgrammedSavingsAddProgrammedSavingsRequest.setUser(user);</li>
		<li>aProgrammedSavingsAddProgrammedSavingsRequest.setProduct1(product);</li>
		<li>aProgrammedSavingsAddProgrammedSavingsRequest.setProduct2(product2);</li>
		<li>aProgrammedSavingsAddProgrammedSavingsRequest.setProgrammedSavings(programmedSavings);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida
	</b>
	        
	<ul>
	    <li>ProgrammedSavingsAddProgrammedSavingsResponse programmedSavingsAddProgrammedSavingsResponse = new ProgrammedSavingsAddProgrammedSavingsResponse();</li>
		<li>TransferResponse transferResponse = new TransferResponse();</li>	   
		<li>transferResponse.setProductNumber("99");</li>
		<li>programmedSavingsAddProgrammedSavingsResponse.setTransferResponse(transferResponse);</li>
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
	public ProgrammedSavingsAddProgrammedSavingsResponse addProgrammedSavings(ProgrammedSavingsAddProgrammedSavingsRequest 
			aProgrammedSavingsAddProgrammedSavingsRequest ) 
			throws CTSServiceException, CTSInfrastructureException;
	
}
