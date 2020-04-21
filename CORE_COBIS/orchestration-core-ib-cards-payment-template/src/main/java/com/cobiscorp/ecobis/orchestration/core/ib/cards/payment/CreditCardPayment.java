package com.cobiscorp.ecobis.orchestration.core.ib.cards.payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsPayment;

@Component(name = "CreditCardPayment", immediate = false)
@Service(value = { ICoreServiceCardsPayment.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardPayment"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditCardPayment") })
public class CreditCardPayment extends SPJavaOrchestrationBase implements ICoreServiceCardsPayment {
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(CreditCardPayment.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public PaymentCreditCardResponse payCreditCard(PaymentCreditCardRequest aPaymentCreditCardRequest)
			throws CTSServiceException, CTSInfrastructureException {

		PaymentCreditCardResponse aPaymentCreditCardResponse = new PaymentCreditCardResponse();
		aPaymentCreditCardResponse.setConvertValue(aPaymentCreditCardRequest.getPayment().getPaymentAmmount());
		aPaymentCreditCardResponse.setReference((new Random(123131)).nextInt());
		aPaymentCreditCardResponse.setExchangeRate((new BigDecimal(500.5)).floatValue());
		aPaymentCreditCardResponse.setSuccess(true);
		return aPaymentCreditCardResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
