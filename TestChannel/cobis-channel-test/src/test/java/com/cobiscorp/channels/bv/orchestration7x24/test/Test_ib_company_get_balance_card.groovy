package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Address
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.services.dto.RequestCreditCard
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseCreditCard

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

import cobiscorp.ecobis.internetbanking.webapp.services.dto.RequestCreditCard


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.VirtualBankingBase;

class Test_ib_company_get_balance_card {
	
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany(initSession)
	}

	@Test
	void testGetCardBalanceCompany() {
		def ServiceName='testGetCardBalanceCompany'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.GetBalance')
			
			User wuser= new User();
			RequestCreditCard wRequestCreditCard = new RequestCreditCard();
			wuser.name=CTSEnvironment.bvCompanyLogin; //testCts
			wRequestCreditCard.card= CTSEnvironment.bvAccCompanyTarNumber;
			wRequestCreditCard.productId= Integer.parseInt(CTSEnvironment.bvAccCompanyTarType);
			
			serviceRequestTO.addValue('inRequestCreditCard', wRequestCreditCard)
			serviceRequestTO.addValue('inUser', wuser)
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> name-> " + wuser.name
			println "Test ---> card-> " + wRequestCreditCard.card
			println "Test ---> productId-> " + wRequestCreditCard.productId
			
			ResponseCreditCard[] oResponseCreditCard= serviceResponseTO.data.get('returnResponseCreditCard').collect().toArray()
			
			for (var in oResponseCreditCard) {
				
				Assert.assertNotNull("responseCode is null", var.responseCode);
				Assert.assertNotNull("paymentDate is null", var.paymentDate);
				Assert.assertNotNull("localMinimumPayment is null", var.localMinimumPayment);
				Assert.assertNotNull("localBalance is null", var.localBalance);
				Assert.assertNotNull("internationalMinimumPayment is null", var.internationalMinimumPayment);
				Assert.assertNotNull("internationalBalance is null", var.internationalBalance);
				Assert.assertNotNull("debitLocalTransit is null", var.debitLocalTransit);
				Assert.assertNotNull("debitInternationalTransit Id is null", var.debitInternationalTransit);
				Assert.assertNotNull("cashPaymentLocalCurrency is null", var.cashPaymentLocalCurrency);
				Assert.assertNotNull("cashPaymentInternationalCurrency Id is null", var.cashPaymentInternationalCurrency);
				Assert.assertNotNull("availableLocalEF is null", var.availableLocalEF);
				Assert.assertNotNull("availableLocal Id is null", var.availableLocal);
				Assert.assertNotNull("availableInternationalEF is null", var.availableInternationalEF);
				Assert.assertNotNull("availableInternational Id is null", var.availableInternational);
				
							
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  responseCode ---> "+var.responseCode
				println " ***  paymentDate ---> "+var.paymentDate
				println " ***  localMinimumPayment ---> "+var.localMinimumPayment
				println " ***  localBalance ---> "+var.localBalance
				println " ***  internationalMinimumPayment ---> "+var.internationalMinimumPayment
				println " ***  internationalBalance ---> "+var.internationalBalance
				println " ***  debitLocalTransit ---> "+var.debitLocalTransit
				println " ***  debitInternationalTransit ---> "+var.debitInternationalTransit
				println " ***  cashPaymentLocalCurrency ---> "+var.cashPaymentLocalCurrency
				println " ***  cashPaymentInternationalCurrency ---> "+var.cashPaymentInternationalCurrency
				println " ***  availableLocalEF ---> "+var.availableLocalEF
				println " ***  availableLocal ---> "+var.availableLocal
				println " ***  availableInternationalEF ---> "+var.availableInternationalEF
				println " ***  availableInternational ---> "+var.availableInternational
			}
			
			//
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
		
	}

}
