package com.cobiscorp.ecobis.orchestration.core.ib.creditcards.querys;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Card;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditCardBalance;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditCardPrize;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;

@Component(name = "CreditCardsQuerys", immediate = false)
@Service(value = { ICoreServiceCardsQuery.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardsQuerys"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditCardsQuerys") })
public class CreditCardsQuerys implements ICoreServiceCardsQuery {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery#
	 * getBalanceCreditCard(com.cobiscorp.ecobis.ib.application.dtos.
	 * CreditCardBalanceRequest)
	 */
	@Override
	public CreditCardBalanceResponse getBalanceCreditCard(CreditCardBalanceRequest creditCardBalanceRequest)
			throws CTSServiceException, CTSInfrastructureException {

		CreditCardBalanceResponse creditCardBalanceResponse = new CreditCardBalanceResponse();

		CreditCardBalance creditCardBalance = new CreditCardBalance();
		creditCardBalance.setAvailableInternational(500.00);
		creditCardBalance.setAvailableInternationalEF(500.00);
		creditCardBalance.setAvailableLocal(500.00);
		creditCardBalance.setAvailableLocalEF(500.00);
		creditCardBalance.setCashPaymentInternationalCurrency(500.00);
		creditCardBalance.setCashPaymentLocalCurrency(500.00);
		creditCardBalance.setDebitInternationalTransit(500.00);
		creditCardBalance.setDebitLocalTransit(500.00);
		creditCardBalance.setInternationalBalance(500.00);
		creditCardBalance.setInternationalMinimumPayment(500.00);
		creditCardBalance.setLocalBalance(500.00);
		creditCardBalance.setLocalMinimutmPayment(500.00);
		creditCardBalance.setPaymentDate("20141030");
		creditCardBalance.setResponseCode("0");
		creditCardBalance.setFechavencimiento("20141030");
		creditCardBalance.setFechacorte("20141030");
		creditCardBalance.setDescripcion("Descripcion Dummy");
		creditCardBalance.setPagominimolocal(500.00);
		creditCardBalance.setPagocontadolocal(500.00);
		creditCardBalance.setError(0);
		creditCardBalance.setPagominimoint(500.00);
		creditCardBalance.setPagocontadoint(500.00);
		creditCardBalance.setSaldolocal(500.00);
		creditCardBalance.setSaldointernacional(500.00);
		creditCardBalance.setSaltotcortelocal(500.00);
		creditCardBalance.setSaltotcorteinter(500.00);
		creditCardBalance.setDisponibleeflocal(500.00);
		creditCardBalance.setDisponibleefinter(500.00);
		creditCardBalance.setDebitotransitolocal(500.00);
		creditCardBalance.setDebitotransitointer(500.00);

		creditCardBalanceResponse.setCreditCardBalanceCollection(creditCardBalance);
		return creditCardBalanceResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery#
	 * getPrize(com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeRequest)
	 */
	@Override
	public CreditCardPrizeResponse getPrize(CreditCardPrizeRequest creditCardPrizeRequest)
			throws CTSServiceException, CTSInfrastructureException {
		CreditCardPrizeResponse creditCardPrizeResponse = new CreditCardPrizeResponse();
		CreditCardPrize creditCardPrize = new CreditCardPrize();
		Card card = new Card();
		card.setCardName("Doomy name");
		card.setCardNumber("123456789");
		creditCardPrize.setCard(card);
		creditCardPrize.setCutoffDate("20141030");
		creditCardPrize.setTotalPrize(3500);
		creditCardPrizeResponse.setaCreditCardPrizeResponse(creditCardPrize);
		return creditCardPrizeResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery#
	 * getSummaryCreditCard(com.cobiscorp.ecobis.ib.application.dtos.
	 * SummaryCreditCardRequest)
	 */
	@Override
	public SummaryCreditCardResponse getSummaryCreditCard(SummaryCreditCardRequest summaryCreditCardRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}
}
