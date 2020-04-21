package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAccountResponse;

/**
 * 
 * @author mmoya
 * Interfaz de Servicio LoanAccount
 */
public interface ICoreServiceLoanAccount {
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>LoanAccountRequest  loanAccountReq  = new LoanAccountRequest();</li>
		
	    <li>Product product= new Product();</li>
	    <li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		
		<li>loanAccountReq.setLoanNumber(aRequest.readValueParam("@i_banco"));</li>
		<li>loanAccountReq.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>loanAccountReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>loanAccountReq.setProdutNumber(product);</li>	  
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>LoanAccountResponse loanAccountResponse = new LoanAccountResponse();</li>
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
	public LoanAccountResponse GetLoanAccount(LoanAccountRequest aLoanAccountRequest)throws CTSServiceException, CTSInfrastructureException;
		

}
