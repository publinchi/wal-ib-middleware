package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounResponse;

public interface ICoreServiceNegociationLoanAccount {
	
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>NegociationLoanAccounRequest loanAccountReq = new NegociationLoanAccounRequest();</li>

		<li>Product product = new Product();</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		
		<li>loanAccountReq.setOperation(aRequest.readValueParam("@i_operacion"));</li>
		<li>loanAccountReq.setLoanNumber(aRequest.readValueParam("@i_banco"));</li>
		<li>loanAccountReq.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>loanAccountReq.setCurrencyId(0);// (Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>loanAccountReq.setCompleteQuota(aRequest.readValueParam("@i_cuota_completa"));</li>
		<li>loanAccountReq.setChargeRate(aRequest.readValueParam("@i_tipo_cobro"));</li>
		<li>loanAccountReq.setReductionRate(aRequest.readValueParam("@i_tipo_reduccion"));</li>
		<li>loanAccountReq.setPaymentEffect(aRequest.readValueParam("@i_efecto_pago"));</li>
		<li>loanAccountReq.setPriorityRate(aRequest.readValueParam("@i_tipo_prioridad"));</li>
		<li>loanAccountReq.setAdvancePayment(aRequest.readValueParam("@i_aceptar_anticipos"));</li>
		<li>loanAccountReq.setTransactionId(Integer.parseInt(aRequest.readValueParam("@i_transaction_id")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>NegociationLoanAccounResponse aNegociationLoanAccounResponse = new NegociationLoanAccounResponse();</li>
		<li>NegotiationLoanAccount aNegotiationLoanAccount = new NegotiationLoanAccount();</li>
		<li>List<NegotiationLoanAccount> aNegotiationLoanAccountList = new ArrayList<NegotiationLoanAccount>();</li>

		<li>if (aNegociationLoanAccounRequest.getOperation().equals("N")) {</li>
		<ul>
			<li>aNegociationLoanAccounResponse.setAccount("696969696969");
		</ul>
		<li>} else if (aNegociationLoanAccounRequest.getOperation().equals("P")) {</li>
		<ul>
			<li>aNegotiationLoanAccount.setChargeRate("15");</li>
			<li>aNegotiationLoanAccount.setAdvancePayment("500");</li>
			<li>aNegotiationLoanAccount.setReductionRate("5");</li>
			<li>aNegotiationLoanAccount.setAplicationRate("2");</li>
			<li>aNegotiationLoanAccount.setCompleteQuota("300");</li>
			<li>aNegotiationLoanAccount.setPriorityRate("2");</li>
			<li>aNegotiationLoanAccount.setPaymentEffect("PaymentE");</li>
			<li>aNegotiationLoanAccount.setCurrencyId(1);</li>
			<li>aNegotiationLoanAccount.setCurrencyName("Dolar");</li>
			<li>aNegotiationLoanAccountList.add(aNegotiationLoanAccount);</li>
			<li>aNegociationLoanAccounResponse.setNegotiationDateList(aNegotiationLoanAccountList);</li>
		</ul>
		<li>}else if (aNegociationLoanAccounRequest.getOperation().equals("M")) {</li>
		<ul>
			<li>aNegociationLoanAccounResponse.setSuccess(true);</li>
		</ul>
		<li>}else if(aNegociationLoanAccounRequest.getOperation().equals("D")){</li>
		<ul>
	      	<li>aNegotiationLoanAccount.setQuota(new BigDecimal(5.00));</li>
	        <li>aNegotiationLoanAccount.setConcept("Concepto DUMMY");</li>
	        <li>aNegotiationLoanAccount.setState("ACTIVADO");</li>
	        <li>aNegotiationLoanAccount.setAmount(new BigDecimal(500.00));</li>
	        <li>aNegotiationLoanAccount.setAmountMN(new BigDecimal(250.00));</li>
	        <li>aNegotiationLoanAccount.setCurrencyId(1);</li>
	        <li>aNegotiationLoanAccountList.add(aNegotiationLoanAccount);</li>
	        <li>aNegociationLoanAccounResponse.setNegotiationDateList(aNegotiationLoanAccountList);</li>
	    </ul>
		<li>}</li>
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
	public NegociationLoanAccounResponse GetNegociationLoanAccount(NegociationLoanAccounRequest aNegociationLoanAccounRequest)throws CTSServiceException, CTSInfrastructureException;
	
}
