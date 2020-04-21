package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;

/**
 * This interface contains the methods needed for obtain the bdetails of Loans.
 * 
 * @author dguerra
 * @since Aug 1, 2014
 * @version 1.0.0
 */
public interface ICoreServiceLoanBalance {
	/**
	 * 
	 * 
	 * Get details of loans.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IProcedureRequest iProcedureRequest = new IProcedureRequest();</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "RESULT_SUBMIT", ICTSTypes.SQLINT4, 6);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "PRODUCT_NUMBER", ICTSTypes.SQLVARCHAR, 24);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "ENTITY_NAME", ICTSTypes.SQLVARCHAR, 45);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OPERATION_TYPE", ICTSTypes.SQLVARCHAR, 35);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "INITIAL_AMOUNT", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "MONTHLY_PAYMENT_DAY", ICTSTypes.SQLVARCHAR, 16);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "STATUS", ICTSTypes.SQLVARCHAR, 255);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "LAST_PAYMENT_DATE", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "EXPIRATION_DATE", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "EXECUTIVE", ICTSTypes.SQLVARCHAR, 60);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "INITIAL_DATE", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "ARREARS_DAYS", ICTSTypes.SQLINT4, 6);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_CAPITAL", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_INTEREST", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_ARREARS_VALUE", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_ANOTHER_ITEMS", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_TOTAL", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "NEXT_PAYMENT_DATE", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "NEXT_PAYMENT_VALUE", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "ORDINARY_INTEREST_RATE", ICTSTypes.SQLDECIMAL, 85);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "ARREARS_INTEREST_RATE", ICTSTypes.SQLDECIMAL, 85);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "CAPITAL_BALANCE", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "TOTAL_BALANCE", ICTSTypes.SQLDECIMAL, 21);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "ORIGINAL_TERM", ICTSTypes.SQLVARCHAR, 64);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "SECTOR", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "OPERATION_DESCRIPTION", ICTSTypes.SQLVARCHAR, 10);</li>
		<li>SpUtilitario.crearColumna(procedureResponse, 1, "FECI", ICTSTypes.SQLDECIMAL, 21);</li>
		
		<li>SpUtilitario.crearFilaDato(procedureResponse, 1, new Object[] {</b></li> 
		<li>	0.00, 										<b>//RESULT_SUBMIT</b></li>
		<li>	"10407740700943818",						<b>//PRODUCT_NUMBER</b></li>
		<li>	"CLIENTE (TEST_REGRESION_DESACOPLADO)",		<b>//ENTITY_NAME</b></li>
		<li>	"HIPOTECARIO DESACOPLADO", 					<b>//OPERATION_TYPE</b></li>
		<li>	1234567.0000, 								<b>//INITIAL_AMOUNT</b></li>
		<li>	"5",  										<b>//MONTHLY_PAYMENT_DAY</b></li>
		<li>	null,  										<b>//MONTHLY_PAYMENT_DAY</b></li>
		<li>	null,  										<b>//STATUS</b></li>
		<li>	"10/05/2037",  								<b>//LAST_PAYMENT_DATE</b></li>
		<li>	null,  										<b>//EXECUTIVE</b></li>
		<li>	"10/08/2007",  								<b>//INITIAL_DATE</b></li>
		<li>	0,  										<b>//ARREARS_DAYS</b></li>
		<li>	0.00,  										<b>//OVERDUE_CAPITAL</b></li>
		<li>	0.00,  										<b>//OVERDUE_INTEREST</b></li>
		<li>	0.00,  										<b>//OVERDUE_ARREARS_VALUE</b></li>
		<li>	0.00,  										<b>//OVERDUE_ANOTHER_ITEMS</b></li>
		<li>	0.0000,  									<b>//OVERDUE_TOTAL</b></li>
		<li>	"05/21/2014",  								<b>//NEXT_PAYMENT_DATE</b></li>
		<li>	1234567.0100,  								<b>//NEXT_PAYMENT_VALUE</b></li>
		<li>	10.0,  										<b>//ORDINARY_INTEREST_RATE</b></li>
		<li>	5.0,  										<b>//ARREARS_INTEREST_RATE</b></li>
		<li>	1234567.4500,  								<b>//CAPITAL_BALANCE</b></li>
		<li>	1234567.2900,  								<b>//TOTAL_BALANCE</b></li>
		<li>	"123 M",  									<b>//ORIGINAL_TERM</b></li>
		<li>	"BHVI",  									<b>//SECTOR</b></li>
		<li>	"HVI",  									<b>//OPERATION_DESCRIPTION</b></li>
		<li>	0.0000 										<b>//FECI</b></li>
		});</li>
	
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
	IProcedureResponse getDetailsLoanBalance(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;

}
