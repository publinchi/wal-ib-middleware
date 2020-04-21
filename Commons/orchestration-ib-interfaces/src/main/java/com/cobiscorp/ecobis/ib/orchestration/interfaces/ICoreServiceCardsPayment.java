package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardResponse;

/**Clase que se encarga del pago de tarjetas
 * 
 * */

public interface ICoreServiceCardsPayment {
	
	/**
	 * 
	 *   
	 *   <b>Pago de tarjeta de cr&eacutedito</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		PaymentCreditCardRequest aPaymentCreditCardRequest = new PaymentCreditCardRequest();

		Card card = new Card();
		Payment payment = new Payment();
		
		card.setCardNumber(aProcedureRequest.readValueParam("@i_numero_tarjeta"));
		
		aPaymentCreditCardRequest.setCard(card);
		aPaymentCreditCardRequest.setConcept(aProcedureRequest.readValueParam("@i_concepto"));
		aPaymentCreditCardRequest.setLocation(aProcedureRequest.readValueParam("@i_localidad1"));
		aPaymentCreditCardRequest.setMessageType(aProcedureRequest.readValueParam("@i_codtipomsg"));
		aPaymentCreditCardRequest.setFranchise(aProcedureRequest.readValueParam("@i_superfranquicia"));
		aPaymentCreditCardRequest.setReferenceNumber(aProcedureRequest.readValueParam("@i_numreferencia"));
		aPaymentCreditCardRequest.setTransactionId(aProcedureRequest.readValueParam("@i_idtransaccion"));
		aPaymentCreditCardRequest.setTrxChannelId(aProcedureRequest.readValueParam("@i_id_trancanal"));
		payment.setPaymentAmmount(new BigDecimal(aProcedureRequest.readValueParam("@i_val")));
		aPaymentCreditCardRequest.setPayment(payment);
	</ul>
    <b>
		@return
		-Parametros de Salida-
    </b>
    <ul>    
		<li>PaymentCreditCardResponse aPaymentCreditCardResponse = new PaymentCreditCardResponse();</li>
		<li>aPaymentCreditCardResponse.setConvertValue(aPaymentCreditCardRequest.getPayment().getPaymentAmmount());</li>
		<li>aPaymentCreditCardResponse.setReference((new Random(123131)).nextInt());<li>
		<li>aPaymentCreditCardResponse.setExchangeRate((new BigDecimal(500.5)).floatValue());</li>
		<li>aPaymentCreditCardResponse.setSuccess(true);</li>
	</ul>
	 */
	PaymentCreditCardResponse payCreditCard(PaymentCreditCardRequest aPaymentCreditCardRequest)throws CTSServiceException, CTSInfrastructureException;
	
}
