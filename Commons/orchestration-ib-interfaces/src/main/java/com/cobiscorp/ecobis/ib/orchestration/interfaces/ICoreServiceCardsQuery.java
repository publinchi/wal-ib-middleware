package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardResponse;

/**
 * This interface contains the methods needed to get information of credit
 * cards
 * 
 * 
 *
 * @author schancay
 * @since Jun 19, 2014
 * @version 1.0.0
 */
public interface ICoreServiceCardsQuery {
	/**
	 * 
	 *   
	 *   <b>Obtiene detalle de tarjeta de cr&eacutedito.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CreditCardBalanceRequest creditCardBalanceRequest = new CreditCardBalanceRequest();</li>
		<li>creditCardBalanceRequest.setCard(aRequest.readValueParam("@i_tarjeta"));</li>
		<li>creditCardBalanceRequest.setName(aRequest.readValueParam("@i_usuario"));</li>
		<li>creditCardBalanceRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CreditCardBalanceResponse <b>creditCardBalanceResponse</b> = new CreditCardBalanceResponse();</li>
		<li>CreditCardBalance creditCardBalance = new CreditCardBalance();</li>
		<li>creditCardBalance.setAvailableInternational(500.00);</li>
		<li>creditCardBalance.setAvailableInternationalEF(500.00);</li>
		<li>creditCardBalance.setAvailableLocal(500.00);</li>
		<li>creditCardBalance.setAvailableLocalEF(500.00);</li>
		<li>creditCardBalance.setCashPaymentInternationalCurrency(500.00);</li>
		<li>creditCardBalance.setCashPaymentLocalCurrency(500.00);</li>
		<li>creditCardBalance.setDebitInternationalTransit(500.00);</li>
		<li>creditCardBalance.setDebitLocalTransit(500.00);</li>
		<li>creditCardBalance.setInternationalBalance(500.00);</li>
		<li>creditCardBalance.setInternationalMinimumPayment(500.00);</li>
		<li>creditCardBalance.setLocalBalance(500.00);</li>
		<li>creditCardBalance.setLocalMinimutmPayment(500.00);</li>
		<li>creditCardBalance.setPaymentDate("20141030");</li>
		<li>creditCardBalance.setResponseCode("0");</li>
		<li>creditCardBalance.setFechavencimiento("20141030");</li>
		<li>creditCardBalance.setFechacorte("20141030");</li>
		<li>creditCardBalance.setDescripcion("Descripcion Dummy");</li>
		<li>creditCardBalance.setPagominimolocal(500.00);</li>
		<li>creditCardBalance.setPagocontadolocal(500.00);</li>
		<li>creditCardBalance.setError(0);</li>
		<li>creditCardBalance.setPagominimoint(500.00);</li>
		<li>creditCardBalance.setPagocontadoint(500.00);</li>
		<li>creditCardBalance.setSaldolocal(500.00);</li>
		<li>creditCardBalance.setSaldointernacional(500.00);</li>
		<li>creditCardBalance.setSaltotcortelocal(500.00);</li>
		<li>creditCardBalance.setSaltotcorteinter(500.00);</li>
		<li>creditCardBalance.setDisponibleeflocal(500.00);</li>
		<li>creditCardBalance.setDisponibleefinter(500.00);</li>
		<li>creditCardBalance.setDebitotransitolocal(500.00);</li>
		<li>creditCardBalance.setDebitotransitointer(500.00);</li>
		<li>creditCardBalanceResponse.setCreditCardBalanceCollection(creditCardBalance);</li>
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
	CreditCardBalanceResponse getBalanceCreditCard(CreditCardBalanceRequest creditCardBalanceRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 *   
	 *   <b>Obtiene millas de premio de una tarjeta de cr&eacutedito.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CreditCardPrizeRequest creditCardPrizeRequest = new CreditCardPrizeRequest();</li>
		<li>creditCardPrizeRequest.setDateFormatId(aRequest.readValueParam("@i_formato_fecha"));</li>
		<li>creditCardPrizeRequest.setProductNumber(aRequest.readValueParam("@i_tarjeta"));</li>
		<li>creditCardPrizeRequest.setTransactionNumber(aRequest.readValueParam("@t_trn"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CreditCardPrizeResponse <b>creditCardPrizeResponse</b> = new CreditCardPrizeResponse();</li>
		<li>CreditCardPrize creditCardPrize = new CreditCardPrize();</li>
		<li>Card card = new Card();</li>
		<li>card.setCardName("Doomy name");</li>
		<li>card.setCardNumber("123456789");</li>
		<li>creditCardPrize.setCard(card);</li>
		<li>creditCardPrize.setCutoffDate("20141030");</li>
		<li>creditCardPrize.setTotalPrize(3500);</li>
		<li>creditCardPrizeResponse.setaCreditCardPrizeResponse(creditCardPrize);</li>
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
	CreditCardPrizeResponse getPrize(CreditCardPrizeRequest creditCardPrizeRequest) throws CTSServiceException, CTSInfrastructureException;

	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>SummaryCreditCardRequest summaryCreditCardRequest = new SummaryCreditCardRequest();</li>
		<li>Client client = new Client();</li>
		<li>client.setId(wOriginalRequest.readValueParam("@i_cliente"));</li>
		<li>summaryCreditCardRequest.setClient(client);	</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li><b>No Implementado</b></li>
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
	SummaryCreditCardResponse getSummaryCreditCard(SummaryCreditCardRequest summaryCreditCardRequest) throws CTSServiceException, CTSInfrastructureException;

}
