package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementResponse;

/**
 * This interface contains the methods needed to perform basic tasks transfers.
 *
 * @since Jul 22, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */

public interface ICoreServiceMovementsQuery {
	/**
	 * 
	 * 
	 * <b>Consulta los movimientos de una cuenta de Ahorro</b>
	 * 
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>AccountStatementRequest accountStatementRequest = new AccountStatementRequest();</li>
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>Product product = new Product();</li>
		<li>Currency c = new Currency();</li>
		<li>product.setCurrency(c);</li>

		<li>product.getCurrency().setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>product.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod")));</li>
		<li>product.setProductNumber(wOriginalRequest.readValueParam("@i_cta"));</li>
		<li>accountStatementRequest.setLogin(wOriginalRequest.readValueParam("@i_login"));</li>
		<li>accountStatementRequest.setDateFormatId(wOriginalRequest.readValueParam("@i_formato_fecha"));</li>
		<li>accountStatementRequest.setInitialDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_ini")));</li>
		<li>accountStatementRequest.setFinalDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_fin")));</li>
		<li>accountStatementRequest.setSequential(wOriginalRequest.readValueParam("@i_sec"));</li>
		<li>accountStatementRequest.setAlternateCode(wOriginalRequest.readValueParam("@i_sec_alt"));</li>
		<li>accountStatementRequest.setType(wOriginalRequest.readValueParam("@i_tipo"));</li>
		<li>accountStatementRequest.setNumberOfMovements(wOriginalRequest.readValueParam("@i_nro_registros"));</li>
		<li>accountStatementRequest.setUniqueSequential(wOriginalRequest.readValueParam("@i_sec_unico"));</li>
		<li>accountStatementRequest.setOperationLastMovement(Boolean.valueOf(wOriginalRequest.readValueParam("@i_operacion").equals("L")));</li>

		<li>accountStatementRequest.setAccountStatement(accountStatement);</li>
		<li>accountStatementRequest.setProduct(product);</li>
		<li>accountStatementRequest.setOriginalRequest(wOriginalRequest);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>AccountStatementResponse accountStatementResponse = new AccountStatementResponse();</li>
		<li>List<AccountStatement> accountStatements = new ArrayList<AccountStatement>();</li>
		<li>accountStatementResponse.setNumberOfResult(5);</li>
		
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>accountStatement.setTransactionDate(Utils.formatDate("4/10/2013"));</li>
		<li>accountStatement.setDescription(wType + "DESACOPLADO--POR: B.LINEA N/D AHORROS BANCA EN LINEA");</li>
		<li>accountStatement.setTypeOperation(1);</li>
		<li>accountStatement.setReference("referencia DUMMY " + i);</li>
		<li>accountStatement.setTypeTransaction("D");</li>
		<li>BigDecimal valor = new BigDecimal(i * 100);</li>
		<li>accountStatement.setAmount((new BigDecimal("250")).add(valor));</li>
		<li>accountStatement.setAccountingBalance(new BigDecimal(0));</li>
		<li>accountStatement.setAvailableBalance(new BigDecimal(1990.00));</li>
		<li>accountStatement.setSequential(i + 1);</li>
		<li>accountStatement.setAlternateCode(i + 1);</li>
		<li>accountStatement.setHour("1" + i + ":00");</li>
		<li>accountStatement.setUniqueSequential(10);</li>

		<li>accountStatements.add(accountStatement);</li>
		
		</br>
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>accountStatement.setTransactionDate(Utils.formatDate("4/10/2013"));</li>
		<li>accountStatement.setDescription(wType + "Nota de crédito ahorros S/L  pagos masivos BC Dummy");</li>
		<li>accountStatement.setTypeOperation(253);</li>
		<li>accountStatement.setReference("8");</li>
		<li>accountStatement.setTypeTransaction("C");</li>
		<li>BigDecimal valor = new BigDecimal(i * 100);</li>
		<li>accountStatement.setAmount((new BigDecimal("15400")).add(valor));</li>
		<li>accountStatement.setAccountingBalance(new BigDecimal(15400));</li>
		<li>accountStatement.setAvailableBalance(new BigDecimal(15400));</li>
		<li>accountStatement.setSequential(i + 10000);</li>
		<li>accountStatement.setAlternateCode(i + 10);</li>
		<li>accountStatement.setHour("05/14/2013 05:05PM");</li>
		<li>accountStatement.setUniqueSequential(1);</li>

		<li>accountStatements.add(accountStatement);</li>
		</br>
		<li>accountStatementResponse.setSuccess(true);</li>
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
	AccountStatementResponse getMovementsSavingAccount(AccountStatementRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * 
	 * <b>Consulta los movimientos de una cuenta corriente.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>AccountStatementRequest accountStatementRequest = new AccountStatementRequest();</li>
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>Product product = new Product();</li>
		<li>Currency c = new Currency();</li>
		<li>product.setCurrency(c);</li>

		<li>product.getCurrency().setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>product.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod")));</li>
		<li>product.setProductNumber(wOriginalRequest.readValueParam("@i_cta"));</li>
		<li>accountStatementRequest.setLogin(wOriginalRequest.readValueParam("@i_login"));</li>
		<li>accountStatementRequest.setDateFormatId(wOriginalRequest.readValueParam("@i_formato_fecha"));</li>
		<li>accountStatementRequest.setInitialDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_ini")));</li>
		<li>accountStatementRequest.setFinalDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_fin")));</li>
		<li>accountStatementRequest.setSequential(wOriginalRequest.readValueParam("@i_sec"));</li>
		<li>accountStatementRequest.setAlternateCode(wOriginalRequest.readValueParam("@i_sec_alt"));</li>
		<li>accountStatementRequest.setType(wOriginalRequest.readValueParam("@i_tipo"));</li>
		<li>accountStatementRequest.setNumberOfMovements(wOriginalRequest.readValueParam("@i_nro_registros"));</li>
		<li>accountStatementRequest.setUniqueSequential(wOriginalRequest.readValueParam("@i_sec_unico"));</li>
		<li>accountStatementRequest.setOperationLastMovement(Boolean.valueOf(wOriginalRequest.readValueParam("@i_operacion").equals("L")));</li>

		<li>accountStatementRequest.setAccountStatement(accountStatement);</li>
		<li>accountStatementRequest.setProduct(product);</li>
		<li>accountStatementRequest.setOriginalRequest(wOriginalRequest);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Service Not Implemented</li>
		<li>AccountStatementResponse accountStatementResponse = new AccountStatementResponse();</li>
		<li>List<AccountStatement> accountStatements = new ArrayList<AccountStatement>();</li>
		
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>accountStatement.setTransactionDate(Utils.formatDate("4/10/2013"));</li>
		<li>accountStatement.setDescription(wType + "DESACOPLADO--POR: B.LINEA N/D TRANSFERENCIA ENTRE CUENTAS BANCA EN LINEA");</li>
		<li>accountStatement.setTypeOperation(1);</li>
		<li>accountStatement.setReference("referencia DUMMY" + String.valueOf(i));</li>
		<li>accountStatement.setTypeTransaction("D");</li>
		<li>accountStatement.setAmount(new BigDecimal("250" + String.valueOf(i) + ".00"));</li>
		<li>accountStatement.setAccountingBalance(new BigDecimal(0));</li>
		<li>accountStatement.setAvailableBalance(new BigDecimal(1990.00));</li>
		<li>accountStatement.setSequential(1);</li>
		<li>accountStatement.setAlternateCode(1);</li>
		<li>accountStatement.setHour("15:00");</li>
		<li>accountStatement.setUniqueSequential(10);</li>
		<li>accountStatements.add(accountStatement);</li>
		</br>
		
		<li>AccountStatement accountStatement = new AccountStatement();</li>
		<li>accountStatement.setTransactionDate(Utils.formatDate("4/10/2013"));</li>
		<li>accountStatement.setDescription(wType + "DESACOPLADO--POR: B.LINEA N/D TRANSFERENCIA ENTRE CUENTAS BANCA EN LINEA");</li>
		<li>accountStatement.setTypeOperation(1);</li>
		<li>accountStatement.setReference("referencia DUMMY");</li>
		<li>accountStatement.setTypeTransaction("D");</li>
		<li>BigDecimal valor = new BigDecimal(i * 100);</li>
		<li>accountStatement.setAmount((new BigDecimal("250")).add(valor));</li>
		<li>accountStatement.setAccountingBalance(new BigDecimal(0));</li>
		<li>accountStatement.setAvailableBalance(new BigDecimal(1990.00));</li>
		<li>accountStatement.setSequential(i + 1);</li>
		<li>accountStatement.setAlternateCode(i + 1);</li>
		<li>accountStatement.setHour("1" + i + ":00");</li>
		<li>accountStatement.setUniqueSequential(10);</li>
		<li>accountStatements.add(accountStatement);</li>
		</br>
		<li>accountStatementResponse.setSuccess(true);</li>
		<li>accountStatementResponse.setAccountStatements(accountStatements);</li>
		<li>accountStatementResponse.setNumberOfResult(5);</li>
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
	AccountStatementResponse getMovementsCheckingAccount(AccountStatementRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;

}
