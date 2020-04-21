package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementResponse;

/**
 * This interface contains the methods needed for obtain the Loans Statement.
 * @author wsanchez
 * @since Sep 11, 2014
 * @version 1.0.0
 */
public interface ICoreServiceLoanStatement {

	/**
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>LoanStatementRequest LoanStatementReq = new LoanStatementRequest();</li>
		<li>Product product  = new Product();</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_banco"));</li>
			
		<li>LoanStatementReq.setProductNumber(product);</li>
		<li>LoanStatementReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
		<li>LoanStatementReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>LoanStatementReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_siguiente")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>
	<ul>
		<li>LoanStatement aLoanStatement = new LoanStatement();</li>
		<li>List<LoanStatement> aloanStatementCollection = new ArrayList<LoanStatement>();</li>
		<!--
		<li>aLoanStatement.setPaymentDate(columns[1].getValue());</li>
		<li>aLoanStatement.setNormalInterest(Double.parseDouble(columns[3].getValue()));</li>
		<li>aLoanStatement.setArrearsInterest(Double.parseDouble(columns[4].getValue()));</li>
		<li>aLoanStatement.setAmount(Double.parseDouble(columns[7].getValue()));</li>
		<li>aLoanStatement.setSequential(Integer.parseInt(columns[17].getValue()));</li>
		<li>aLoanStatement.setPaymentType("A");</li>
		
		<li>aloanStatementCollection.add(aLoanStatement);</li>
		<li>LoanStatementResp.setLoanStatementCollection(aloanStatementCollection);</li>
		-->
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
	public LoanStatementResponse getLoanStatement(LoanStatementRequest aLoanStatementRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
}
