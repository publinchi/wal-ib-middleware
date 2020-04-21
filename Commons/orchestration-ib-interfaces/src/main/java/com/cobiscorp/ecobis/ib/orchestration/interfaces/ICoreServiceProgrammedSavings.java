package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsResponse;

public interface ICoreServiceProgrammedSavings {
	
	/**  
	 * 
	 * 
	 * 
	 * 
	 *  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ProgrammedSavingsRequest ProgrammedSavingsReq = new ProgrammedSavingsRequest();</li>
		<li>Product product  = new Product();</li>    
      	<li>ProgrammedSavingsReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta_ahoprog"));</li>		
		<li>ProgrammedSavingsReq.setProductNumber(product);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida
	</b>
	        
	<ul>
		<li>Currency oCurrency = new Currency();</li>
		<li>oCurrency.setCurrencyId(0);</li>
		<li>ProgrammedSavings ProgrammedSavings = new ProgrammedSavings();</li>
		<li>List<ProgrammedSavings> aProgrammedSavingsCollection = new  ArrayList<ProgrammedSavings>();</li>
		<li>ProgrammedSavingsResponse ProgrammedSavingsResponse = new ProgrammedSavingsResponse();</li>
		<li>ProgrammedSavings.setSequential(1);</li>
		<li>ProgrammedSavings.setSavingsTime("01/01/2014");</li>
		<li>ProgrammedSavings.setAmount(100.00);</li>
		<li>ProgrammedSavings.setCurrency(oCurrency);</li>
		<li>ProgrammedSavings.setPaymentDate("02/01/2014");</li>
		<li>ProgrammedSavings.setExecuted("V");</li>
		<li>aProgrammedSavingsCollection.add(ProgrammedSavings);</li>
		<li>ProgrammedSavingsResponse.setProgrammendSavingsCollection(aProgrammedSavingsCollection);</li>
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
	public ProgrammedSavingsResponse getProgrammedSavings(ProgrammedSavingsRequest aProgrammedSavingsRequest) 
			throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 * 
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
      	<li>ProgrammedSavingsAccountRequest ProgrammedSavingsAccountReq = new ProgrammedSavingsAccountRequest();</li>
		<li>User wUser = new User();</li>
      	<li>wUser.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_cliente")));</li>
		<li>ProgrammedSavingsAccountReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>	
		<li>ProgrammedSavingsAccountReq.setUser(wUser);</li>
		<li>ProgrammedSavingsAccountReq.setOriginalRequest(aRequest);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida
	</b>
	<ul>
		<li>ProgrammedSavingsAccount wProgrammedSavingsAccount = new ProgrammedSavingsAccount();</li>
		<li>List<ProgrammedSavingsAccount> aProgrammedSavingsAccountCollection = new  ArrayList<ProgrammedSavingsAccount>();</li>
		<li>ProgrammedSavingsAccountResponse ProgrammedSavingsAccountResponse = new ProgrammedSavingsAccountResponse();</li>
		<li>Currency wCurrency = new Currency();</li>
		<li>Client wClient = new Client();</li>   
		<li>wCurrency.setCurrencyId(0);</li>
		<li>wClient.setCompleteName("Gianni Condo");</li>		
		<li>wProgrammedSavingsAccount.setAccount("10410108275406806");</li>
		<li>wProgrammedSavingsAccount.setCurrencyId(wCurrency);</li>
		<li>wProgrammedSavingsAccount.setClient(wClient);</li>
		<li>wProgrammedSavingsAccount.setProductBalance(400.00);</li>
		<li>aProgrammedSavingsAccountCollection.add(wProgrammedSavingsAccount);</li>
		<li>ProgrammedSavingsAccountResponse.setProgrammedSavingsAccountCollection(aProgrammedSavingsAccountCollection);</li>
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
	public ProgrammedSavingsAccountResponse ProgrammedSavingsAccount(ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest)
			throws CTSServiceException, CTSInfrastructureException;
	
	/*
	 * NOV 11/2014
	 * MODIFICACION: JBA
	 * 
	 * 
	 * <b>Obtiene la fecha de expiración</b>  
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
	    <li>ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest = new ProgrammedSavingsExpirationDateRequest();</li>	    
	  	<li>aProgrammedSavingsExpirationDateRequest.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));</li>
		<li>aProgrammedSavingsExpirationDateRequest.setTerm(aRequest.readValueParam("@i_plazo"));</li>
		<li>aProgrammedSavingsExpirationDateRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida
	</b>
	<ul>	   
		<li>ProgrammedSavingsExpirationDateResponse programmedSavingsExpirationDateResponse = new ProgrammedSavingsExpirationDateResponse();</li>
		<li>programmedSavingsExpirationDateResponse.setExpirationDates("01/06/2014");</li>
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
	public ProgrammedSavingsExpirationDateResponse getExpirationDate(ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest) 
			throws CTSServiceException, CTSInfrastructureException;
	
	
	/*
	 * <b>Obtiene el monto minimo</b>
	 *
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ProgrammedSavingsMinimumAmountRequest  aProgrammedSavingsMinimumAmountRequest = new ProgrammedSavingsMinimumAmountRequest();</li>    
      	<li>aProgrammedSavingsMinimumAmountRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida
	</b>
	<ul>	   
	    <li>ProgrammedSavingsMinimumAmountResponse programmedSavingsMinimumAmountResponse = new ProgrammedSavingsMinimumAmountResponse();</li>
		<li>programmedSavingsMinimumAmountResponse.setMinimumAmount(100.00);</li>
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
	public ProgrammedSavingsMinimumAmountResponse getMinimunAmount(ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest) 
			throws CTSServiceException, CTSInfrastructureException;
}
