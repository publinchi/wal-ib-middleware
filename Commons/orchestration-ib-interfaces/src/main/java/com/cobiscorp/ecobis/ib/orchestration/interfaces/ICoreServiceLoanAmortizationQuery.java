package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationResponse;

/**
 * This interface contains the methods needed to validate a Third
 * cards.
 * 
 * @author mvelez
 * @since Sept, 11 2014
 * @version 1.0.0
 */
public interface ICoreServiceLoanAmortizationQuery {
	/**
	 * 
	 * 
	 * <b>Consulta amortizaci&oacuten del pr&eacutestamo.</b>
	 *
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>LoanAmortizationRequest loanAmortizationReq = new LoanAmortizationRequest();</li>
	    <li>Product product  = new Product();</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_banco"));</li>
		<li>loanAmortizationReq.setProduct(product);</li>
		<li>loanAmortizationReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
		<li>loanAmortizationReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>loanAmortizationReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_dividendo")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>
	<ul>
		<li>LoanAmortization aLoanAmortization = new LoanAmortization();</li>
		<li>product.setProductNumber(columns[COL_OPERATION_NUMBER].getValue());</li>
		<li>aLoanAmortization.setOperationNumber(product);</li>
		<li>aLoanAmortization.setDividend(Integer.parseInt(columns[COL_DIVIDEND].getValue()));</li>
		<li>aLoanAmortization.setDate(columns[COL_DATE].getValue());</li>
		<li>aLoanAmortization.setCapital(Double.parseDouble(columns[COL_CAPITAL].getValue()));</li>
		<li>aLoanAmortization.setInterest(Double.parseDouble(columns[COL_INTEREST].getValue()));</li>
		<li>aLoanAmortization.setMora(Double.parseDouble(columns[COL_MORA].getValue()));</li>
		<li>aLoanAmortization.setTax(Double.parseDouble(columns[COL_TAX].getValue()));</li>
		<li>aLoanAmortization.setInsurance(Double.parseDouble(columns[COL_ASSURED].getValue()));</li>
		<li>aLoanAmortization.setOthers(Double.parseDouble(columns[COL_OTHER].getValue()));</li>
		<li>aLoanAmortization.setCapitalAmount(Double.parseDouble(columns[COL_CAPITAL_AMOUNT].getValue()));</li>
		<li>aLoanAmortization.setAdjustment(Double.parseDouble(columns[ADJUSTMENT].getValue()));</li>
		<li>aLoanAmortization.setState(columns[STATE].getValue());</li>
		<li>aLoanAmortization.setPayment(Double.parseDouble(columns[PAYMENT].getValue()));</li>
		<li>aloanAmortizationCollection.add(aLoanAmortization);</li>
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
	public LoanAmortizationResponse GetLoanAmortization(LoanAmortizationRequest aLoanAmortizationRequest) throws CTSServiceException, CTSInfrastructureException;	
}
