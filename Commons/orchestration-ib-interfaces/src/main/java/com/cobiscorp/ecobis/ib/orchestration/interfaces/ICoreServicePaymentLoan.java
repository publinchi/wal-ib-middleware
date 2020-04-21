package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanResponse;

public interface ICoreServicePaymentLoan {

	/**  
	 * 
	 * 
	 * <b>Ejecuta pago de pr&eacutestamo.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>PaymentLoanRequest apaymentLoanReq = new PaymentLoanRequest();</li>
			<li>apaymentLoanReq.setProductid(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
			<li>apaymentLoanReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
			<li>apaymentLoanReq.setUserName(aRequest.readValueParam("@i_login"));</li>
			<li>apaymentLoanReq.setAccount(aRequest.readValueParam("@i_cta"));</li>
			<li>apaymentLoanReq.setConcept(aRequest.readValueParam("@i_concepto"));</li>
			<li>apaymentLoanReq.setProductName(aRequest.readValueParam("@i_nom_cliente_benef"));</li>
			<li>apaymentLoanReq.setAmmount(new BigDecimal(aRequest.readValueParam("@i_val")));</li>
			<li>apaymentLoanReq.setLoanNumber(aRequest.readValueParam("@i_cta_des"));</li>
			<li>apaymentLoanReq.setDestProduct(Integer.parseInt(aRequest.readValueParam("@i_prod_des")));</li>
			<li>apaymentLoanReq.setLoanCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon_des")));</li>
			<li>apaymentLoanReq.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
			<li>apaymentLoanReq.setProductAbbreviation(aRequest.readValueParam("@i_producto"));</li>
			<li>apaymentLoanReq.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));</li>
			<li>apaymentLoanReq.setThirdPartyAssociated(aRequest.readValueParam("@i_tercero_asociado"));</li>
			<li>apaymentLoanReq.setIsThirdParty(aRequest.readValueParam("@i_tercero"));</li>
			<li>apaymentLoanReq.setLoanPaymentAmount(new BigDecimal(aRequest.readValueParam("@i_monto_mpg")));</li>
			<li>apaymentLoanReq.setCreditAmount(new BigDecimal(aRequest.readValueParam("@i_monto")));</li>
			<li>apaymentLoanReq.setRateValue(Float.parseFloat(aRequest.readValueParam("@i_cotizacion").toString()));</li>
			<li>apaymentLoanReq.setValidateAccount(aRequest.readValueParam("@i_valida_des"));</li>
			<li>apaymentLoanReq.setReferenceNumberBranch(aRequest.readValueParam("@s_ssn_branch"));</li>
		 	<li>apaymentLoanReq.setReferenceNumber(aRequest.readValueParam("@s_ssn"));</li>
		 	<li>apaymentLoanReq.setOriginalRequest(aRequest);</li>
	</ul>	
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>    
		<li>PaymentLoanResponse paymentLoanRespon  = new PaymentLoanResponse();</li>
		<li>paymentLoanRespon.setReference(108);</li>
		<li>paymentLoanRespon.setReturnValue(458);</li>
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
	public PaymentLoanResponse  executePaymentLoan (PaymentLoanRequest  aPaymentLoanRequest) throws CTSServiceException, CTSInfrastructureException;
}
