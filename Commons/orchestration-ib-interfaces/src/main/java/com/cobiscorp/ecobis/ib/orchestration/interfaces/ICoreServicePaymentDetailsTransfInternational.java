package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalResponse;

public interface ICoreServicePaymentDetailsTransfInternational {
	/**  
	 * 
	 * 
	 * <b>Consulta informacion de una transferencia internacional, segun el modo dispara, </b>
	 * <b>modo 1 informacion de la operacion, Modo 2 Informacion del pago de la transferencia, </b>
	 * <b>Modo 17 la fecha de pago de la transferencia.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>PaymentDetailsTransfInternationalRequest aPaymentDetailsTransfInternationalRequest = new PaymentDetailsTransfInternationalRequest();</li>
		<li>aPaymentDetailsTransfInternationalRequest.setProductNumber(aRequest.readValueParam("@i_opeban") );</li>
		<li>aPaymentDetailsTransfInternationalRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_fdate")));</li>
		<li>aPaymentDetailsTransfInternationalRequest.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));</li>
		<li>aPaymentDetailsTransfInternationalRequest.setTypeOperation(aRequest.readValueParam("@i_tope"));</li>
		<li>aPaymentDetailsTransfInternationalRequest.setTypeTransaction(aRequest.readValueParam("@i_ttrn"));</li>
		<li>aPaymentDetailsTransfInternationalRequest.setTransaction(Integer.parseInt(aRequest.readValueParam("@t_trn")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>PaymentDetailsTransfInternational aPaymentDetailsTransfInternational = null;</li>
		<li>AccountOperation aAccountOperation = null;</li>
		<li>List<PaymentDetailsTransfInternational> aPaymentDetailsTransfInternationalList = new ArrayList<PaymentDetailsTransfInternational>();</li>
		<li>List<AccountOperation> aAccountOperationList = new ArrayList<AccountOperation>();</li>
		<li>PaymentDetailsTransfInternationalResponse aPaymentDetailsTransfInternationalResponse = new PaymentDetailsTransfInternationalResponse();</li>	
			
		<li>if(MODE == 17){</li>
		<ul>
			<li>aPaymentDetailsTransfInternationalResponse.setPaymentDate("02/13/2008");</li>
		</ul>
		<li>}else if(MODE == 2){</li>
		<ul>
			<li>aPaymentDetailsTransfInternational = new PaymentDetailsTransfInternational();</li>
			<li>aPaymentDetailsTransfInternational.setTransactionSeuqential(1);</li>
			<li>aPaymentDetailsTransfInternational.setNumber(0);</li>
			<li>aPaymentDetailsTransfInternational.setTerm("   ");</li>
			<li>aPaymentDetailsTransfInternational.setPaymentDetailSequential(1);</li>
			<li>aPaymentDetailsTransfInternational.setPaymentType("H03");</li>
			<li>aPaymentDetailsTransfInternational.setPaymentTypeDetail("CUENTA SUSPENSO CAJA");</li>
			<li>aPaymentDetailsTransfInternational.setExtraAmount(306526.00);</li>
			<li>aPaymentDetailsTransfInternational.setCurrency("CRC");</li>
			<li>aPaymentDetailsTransfInternational.setCurrencyType(1.0);</li>
			<li>aPaymentDetailsTransfInternational.setLocalAmount(306526.00);</li>
			<li>aPaymentDetailsTransfInternational.setDetail("CTE:01202000052 NTRA REF TRR00108000022 SU REF 042412295");</li>
			<li>aPaymentDetailsTransfInternational.setPaymentDateSequential(0);</li>
			<li>aPaymentDetailsTransfInternationalList.add(aPaymentDetailsTransfInternational);</li>
		</ul>
		<li>}else if(MODE == 1){</li>
		<ul>
			<li>aAccountOperation = new AccountOperation();</li>
			<li>aAccountOperation.setParameter("I09");</li>
			<li>aAccountOperation.setParameterDescription("IMPORTE BANCO CORRESPONSAL");</li>
			<li>aAccountOperation.setFactor(0.0);</li>
			<li>aAccountOperation.setConcept("IMPORTE BANCO CORRESPONSAL I09");</li>
			<li>aAccountOperation.setAmount(306526.00);</li>
			<li>aAccountOperation.setSequentialOperation(1);</li>
			<li>aAccountOperation.setNumber(0);</li>
			<li>aAccountOperation.setTerm(" ");</li>
			<li>aAccountOperation.setDetailSequentialTransaction(1);</li>
			<li>aAccountOperation.setDetailSequentialPaymentDate(" ");</li>
			<li>aAccountOperationList.add(aAccountOperation);</li>
		</ul>
		<li>}</li>
		
		<li>if(MODE == 2){</li>
		<ul>
			aPaymentDetailsTransfInternationalResponse.setPaymentDetailsCollection(aPaymentDetailsTransfInternationalList);</li>
		</ul>
		<li>}</li>
		<li>else if(MODE == 1){</li>
		<ul>
			<li>aPaymentDetailsTransfInternationalResponse.setAccountOperationCollection(aAccountOperationList);</li>
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
	PaymentDetailsTransfInternationalResponse getPaymentDetailsTransfInternational(PaymentDetailsTransfInternationalRequest aPaymentDetailsTransfInternationalRequest) throws CTSServiceException, CTSInfrastructureException;
}
