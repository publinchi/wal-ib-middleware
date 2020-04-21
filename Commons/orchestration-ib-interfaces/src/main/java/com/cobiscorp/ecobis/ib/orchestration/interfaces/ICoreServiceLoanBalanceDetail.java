package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentResponse;

public interface ICoreServiceLoanBalanceDetail {
	
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>BalanceDetailPaymentRequest  balanceDetailPaymentReq  = new BalanceDetailPaymentRequest();</li>
		<li>Product product  = new Product();</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>balanceDetailPaymentReq.setProductNumber(product);</li>
		<li>balanceDetailPaymentReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));</li>
		<li>balanceDetailPaymentReq.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>balanceDetailPaymentReq.setCurrencyID(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>balanceDetailPaymentReq.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>balanceDetailPaymentReq.setValidateAccount(aRequest.readValueParam("@i_valida_des"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>BalanceDetailPayment  balanceDetailPayment = new BalanceDetailPayment();</li>
		<li>List<BalanceDetailPayment> balanceDetailPaymentCollection = new ArrayList<BalanceDetailPayment>();</li>
		<li>BalanceDetailPaymentResponse balanceDetailPaymentRespon  = new BalanceDetailPaymentResponse();</li>
		<li>balanceDetailPayment.setAditionalData("PRUEBA NO ACOPLADO");</li>
		<li>Product producto = new Product();</li>
		<li>producto.setProductNumber("10410000041700201");</li>
		<li>balanceDetailPayment.setProductNumber(producto);</li>
		<li>balanceDetailPayment.setEntityName("Karen Meza");</li>
		<li>balanceDetailPayment.setOperationType("HIPOTECARIO DESACOPLADO");</li>
		<li>balanceDetailPayment.setInitialAmount(new BigDecimal(1234567.0));</li>
		<li>balanceDetailPayment.setMonthlyPaymentDay(5);</li>
		<li>balanceDetailPayment.setStatus("vigente");</li>
		<li>balanceDetailPayment.setLastPaymentDate("10/10/2014");</li>
		<li>balanceDetailPayment.setExpirationDate("10/10/2015");</li>
		<li>balanceDetailPayment.setExecutive("Carlos Espinosa");</li>
		<li>balanceDetailPayment.setInitialDate("10/02/2013");</li>
		<li>balanceDetailPayment.setArrearsDays(4);</li>
		<li>balanceDetailPayment.setOverdueCapital(new BigDecimal(0.0));</li>
		<li>balanceDetailPayment.setOverdueInterest(new BigDecimal(0.0));</li>
		<li>balanceDetailPayment.setOverdueArrearsValue(new BigDecimal(0.0));</li>
		<li>balanceDetailPayment.setOverdueAnotherItems(new BigDecimal(0.0));</li>
		<li>balanceDetailPayment.setOverdueTotal(new BigDecimal(0.0));</li>
		<li>balanceDetailPayment.setNextPaymentDate("10/10/2015");</li>
		<li>balanceDetailPayment.setNextPaymentValue(new BigDecimal( 150.50));</li>
	    <li>balanceDetailPayment.setOrdinaryInterestRate(new BigDecimal(10.20));</li>
		<li>balanceDetailPayment.setArrearsInterestRate(new BigDecimal(5.2));</li>
		<li>balanceDetailPayment.setCapitalBalance(new BigDecimal( 1234567.45));</li>
		<li>balanceDetailPayment.setTotalBalance(new BigDecimal(1234567.29));</li>
		<li>balanceDetailPayment.setOriginalTerm("361 M");</li>
		<li>balanceDetailPayment.setSector("BHVI");</li>
		<li>balanceDetailPayment.setOperationDescription("HVI");</li>
		<li>balanceDetailPayment.setTax(new BigDecimal(5.50));</li>
		<li>balanceDetailPaymentCollection.add(balanceDetailPayment);</li>
		<li>balanceDetailPaymentRespon.setBalanceDetailList(balanceDetailPaymentCollection);</li>
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
	
	
	/*
	 * 
	 * */
	
	
public BalanceDetailPaymentResponse  getBalanceDetail (BalanceDetailPaymentRequest  aLoanDetailRequest) throws CTSServiceException, CTSInfrastructureException;
}
