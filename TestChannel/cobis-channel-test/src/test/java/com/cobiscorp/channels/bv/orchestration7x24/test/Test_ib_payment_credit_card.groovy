package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CreditCardPaymentRequest;



import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_payment_credit_card {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
	}

	@Test
	void payCreditCard() {
		def ServiceName= 'payCreditCard'
		try{

			println "Test ---> ${ServiceName}"
					
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.PayCreditCard')
			
			User user = new User();
			user.name = CTSEnvironment.bvLogin
			
			TransactionContextCIB context = new TransactionContextCIB()
			context.authorizationRequired = "N"
			
			Product product = new Product()
			product.productNumber = CTSEnvironment.bvAccCtaCteNumber
			product.productAbbreviation = "CTE"
			product.currencyId = CTSEnvironment.bvAccCtaCteCurrencyId
			product.productId = CTSEnvironment.bvAccCtaCteType
			
			
			CreditCardPaymentRequest creditCard = new CreditCardPaymentRequest()
			creditCard.entityId = CTSEnvironment.bvEnteMis
			creditCard.amount = 100
			creditCard.currencyName = "USD"
			creditCard.channel = "1"
			creditCard.trxChannelId = "123"
			creditCard.messageTypeId = "PAYMENT"
			creditCard.creditCardNumber = CTSEnvironment.bvAccTarNumber
			creditCard.franchise = "COBIS"
			creditCard.location1 = "IB"
			creditCard.currency= "USD"
			creditCard.currencyId = 0
			creditCard.referenceNumber = "PRUEBA 123"
			creditCard.user = CTSEnvironment.bvLogin
			
			
			serviceRequestTO.addValue('inUser', user)
			serviceRequestTO.addValue('inTransactionContextCIB', context)
			serviceRequestTO.addValue('inProduct', product)
			serviceRequestTO.addValue('inCreditCardPaymentRequest', creditCard)
			
			
			
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			def message=''
				
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)

			
			
			
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
			Assert.fail()
		}
	}

}
