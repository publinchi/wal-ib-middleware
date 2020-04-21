/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementBalanceResponse;
//import com.cobiscorp.ecobis.ib.application.dtos.AccountBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.MasterAccountRequest;


/**
* 
 * 
 * Clase consulta declaración de las cuentas
*/

public interface ICoreServiceAccountStatementQuery {
	
	/**
	 * 
	 * 
	 * 
	 * <b>Obtiene el estado de cuenta de ahorros de un cliente.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>AccountStatementRequest accountStatementReq = new AccountStatementRequest();</li>
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		
		<li>Product product = new Product();</li>
		<li>product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		    
		<li>accountStatementReq.setMon(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>accountStatementReq.setLogin(aRequest.readValueParam("@i_login"));</li>
		
		<li>accountStatementReq.setProduct(product);</li>
		<li>accountStatementReq.setSequential(aRequest.readValueParam("@i_sec"));</li>
		<li>accountStatementReq.setAlternateCode(aRequest.readValueParam("@i_sec_alt"));</li>
		<li>accountStatementReq.setDaily(Integer.parseInt(aRequest.readValueParam("@i_diario")));</li>
		<li>accountStatement.setStringDate(aRequest.readValueParam("@i_fecha"));</li>
		<li>accountStatementReq.setInitialDateString(pResponse.readValueParam("@o_fecha_ini"));</li>
		<li>accountStatementReq.setFinalDateString(pResponse.readValueParam("@o_fecha_fin"));</li>
		<li>accountStatement.setHour(aRequest.readValueParam("@i_hora"));</li>
		<li>accountStatementReq.setAccountStatement(accountStatement);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>        
	<ul>
		<li>AccountStatementBalanceResponse AccountStatementBalanceResp = new AccountStatementBalanceResponse();</li>
		<li>List<AccountStatement>  AccountStatementCollection = new ArrayList<AccountStatement>();</li>
		<li>List<AccountBalance>    AccountBalanceCollection   = new ArrayList<AccountBalance>();</li>
		<li>wSeq = new Integer(aAccountStatementRequest.getSequential());</li>
		
		<li>Product  product  = new Product();</li>
		<li>product.setProductNumber("10410108275406111");  </li>
		<li>Client   client   = new Client();</li>
		<li>client.setCompleteName("Jose Manuel Perez Perez");</li>
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyDescription("DOLAR");</li>
		<li>Office   office   = new Office();</li>
		<li>office.setDescription("OFICE NAME");</li>
		
		<li>AccountBalance   accountbalance   = new AccountBalance();</li>
		<li>accountbalance.setProductNumber(product);</li>
		<li>accountbalance.setClientName(client);</li>
		<li>accountbalance.setCurrencyName(currency);</li>
		<li>accountbalance.setExecutiveName("NOMBRE FUNCIONARIO EJECUTIVO");</li>
		<li>accountbalance.setDeliveryAdress("DIRECCION AV. ABCD Y LA 6ta.");</li>
		<li>accountbalance.setAvailableBalance(2000.00);</li>
		<li>accountbalance.setAccountingBalance(1000.00);</li>
		<li>accountbalance.setLastCutoffBalance(300.00);</li>
		<li>accountbalance.setAverageBalance(250.00);</li>
		<li>accountbalance.setLastOperationDate("10/10/2013");</li>
		<li>accountbalance.setLastCutoffDate("09/09/2013");</li>
		<li>accountbalance.setNextCutoffDate("10/10/2013");</li>
		<li>accountbalance.setClientPhone("042156153");</li>
		<li>accountbalance.setClientEmail("jose.perez@yourcompany.com");</li>
		<li>accountbalance.setOfficeName(office);</li>
		<li>accountbalance.setToDrawBalance(600.00);</li>
		<li>AccountStatementBalanceResp.setAccountBalanceCollection(AccountBalanceCollection);</li>
		<li>AccountStatementBalanceResp.setAccountStatementsCollection(AccountStatementCollection);</li>
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
	public AccountStatementBalanceResponse GetSavingsAccountStatement  (AccountStatementRequest aAccountStatementRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 * <b>Obtiene el estado de cuenta de clientes de un cliente.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>AccountStatementRequest accountStatementReq = new AccountStatementRequest();</li>
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		
		<li>Product product = new Product();</li>
		<li>product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		    
		<li>accountStatementReq.setMon(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>accountStatementReq.setLogin(aRequest.readValueParam("@i_login"));</li>
		
		<li>accountStatementReq.setProduct(product);</li>
		<li>accountStatementReq.setSequential(aRequest.readValueParam("@i_sec"));</li>
		<li>accountStatementReq.setAlternateCode(aRequest.readValueParam("@i_sec_alt"));</li>
		<li>accountStatementReq.setDaily(Integer.parseInt(aRequest.readValueParam("@i_diario")));</li>
		<li>accountStatement.setStringDate(aRequest.readValueParam("@i_fecha"));</li>
		<li>accountStatementReq.setInitialDateString(pResponse.readValueParam("@o_fecha_ini"));</li>
		<li>accountStatementReq.setFinalDateString(pResponse.readValueParam("@o_fecha_fin"));</li>
		<li>accountStatement.setHour(aRequest.readValueParam("@i_hora"));</li>
		<li>accountStatementReq.setAccountStatement(accountStatement);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>        
	<ul>
		<li>AccountStatementBalanceResponse <b>AccountStatementBalanceResp</b> = new AccountStatementBalanceResponse();</li>		
		
		<li>List<AccountStatement>  AccountStatementCollection = new ArrayList<AccountStatement>();</li>
		<li>List<AccountBalance>    AccountBalanceCollection   = new ArrayList<AccountBalance>();</li>
		
	    <li>AccountStatement accountstatement = new AccountStatement();</li>
	 	
		</br>
		<li>accountstatement.setStringDate("09/09/2014");</li>
		<li>accountstatement.setReference("34343");      //operation</li>
		<li>accountstatement.setDescription("REVERSO");  //operation description</li>
		<li>accountstatement.setDebitsAmount(new BigDecimal(1500.00));</li>
		<li>accountstatement.setCreditsAmount(new BigDecimal(1300.00));</li>
		<li>accountstatement.setAccountingBalance(new BigDecimal(200.00));</li>
		<li>accountstatement.setSignDC("D");</li>
		<li>accountstatement.setHour("15:00");</li>
		<li>accountstatement.setTypeOperation(314);     //transaction type</li>
		<li>accountstatement.setCauseId("106");</li>
		<li>accountstatement.setSequential(cont);</li>
		</br>
		<li>AccountStatementCollection.add(accountstatement);</li>
		<li>AccountStatementBalanceResp.setAccountStatementsCollection(AccountStatementCollection);</li>
		</br>
		<li>Product  product  = new Product();</li>
		<li>product.setProductNumber("10410000005405100");</li>
		<li>Client   client   = new Client();</li>
		<li>client.setCompleteName("Juan Jose Perez Perez");</li>
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyDescription("DOLAR");</li>
		<li>Office   office   = new Office();</li>
		<li>office.setDescription("OFICE NAME");</li>
		</br>
		<li>AccountBalance   accountbalance   = new AccountBalance();</li>
		<li>accountbalance.setProductNumber(product);</li>
		<li>accountbalance.setClientName(client);</li>
		<li>accountbalance.setCurrencyName(currency);</li>
		<li>accountbalance.setExecutiveName("NOMBRE FUNCIONARIO EJECUTIVO");</li>
		<li>accountbalance.setDeliveryAdress("DIRECCION AV. WXYZ Y LA 3era.");</li>
		<li>accountbalance.setAvailableBalance(2000.00);</li>
		<li>accountbalance.setAccountingBalance(1000.00);</li>
		<li>accountbalance.setLastCutoffBalance(300.00);</li>
		<li>accountbalance.setAverageBalance(250.00);</li>
		<li>accountbalance.setLastOperationDate("10/10/2013");</li>
		<li>accountbalance.setLastCutoffDate("09/09/2013");</li>
		<li>accountbalance.setNextCutoffDate("10/10/2013");</li>
		<li>accountbalance.setClientPhone("042156153");</li>
		<li>accountbalance.setClientEmail("juan.perez@yourcompany.com");</li>
		<li>accountbalance.setOfficeName(office);</li>
		<li>accountbalance.setToDrawBalance(600.00);</li>
		
		<li>AccountBalanceCollection.add(accountbalance);</li>
		 </br>
		<li>AccountStatementBalanceResp.setAccountBalanceCollection(AccountBalanceCollection);</li>
		<li>AccountStatementBalanceResp.setReturnCode(0);</li>
		
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
	
	public AccountStatementBalanceResponse GetCheckingAccountStatement (AccountStatementRequest aAccountStatementRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>MasterAccountRequest masterAccountRequest = new MasterAccountRequest();</li>
		</br>
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));</li>
		</br>
		<li>Entity entity = new Entity();</li>
		<li>entity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_cliente")));</li>		
		</br>
		<li>Product product = new Product();</li>
		<li>product.setProductAlias(aRequest.readValueParam("@i_alias"));</li>
		<li>product.setProductId(Integer.parseInt(aRequest.readValueParam("@i_producto")));</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cuenta"));</li>
		</br>
		<li>User user = new User();</li>
		<li>user.setName(aRequest.readValueParam("@i_login"));</li>
		</br>
		<li>masterAccountRequest.setCurrencyId(currency);</li>
		<li>masterAccountRequest.setEntityId(entity);</li>
		<li>masterAccountRequest.setProduct(product);</li>
		<li>masterAccountRequest.setServiceId(Integer.parseInt(aRequest.readValueParam("@i_servicio")));</li>
		<li>masterAccountRequest.setUserName(user);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>        
	<ul>
		<li>IProcedureResponse aIProcedureResponse = new ProcedureResponseAS();</li>
		<li>aIProcedureResponse.setReturnCode(0);</li>
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
	public IProcedureResponse getSelectionMasterAccount(MasterAccountRequest aMasterAccountRequest) throws CTSServiceException, CTSInfrastructureException;
}
