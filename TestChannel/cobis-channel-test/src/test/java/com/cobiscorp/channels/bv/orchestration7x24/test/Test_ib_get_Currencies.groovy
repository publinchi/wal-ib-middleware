package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CurrencyDefinition
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_get_Currencies {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural()
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
	void testGetPaymentDetails(){
		def ServiceName = "testGetPaymentDetails"
		
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetCurrency')
			
			//DTO IN
			CurrencyDefinition wCurrencyDefinition = new CurrencyDefinition()
			
			wCurrencyDefinition.setMode(0)
			
			serviceRequestTO.addValue('inCurrencyDefinition', wCurrencyDefinition)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			println "Services Response " + serviceResponseTO
			
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Assert.assertTrue(message, serviceResponseTO.success)"
			CurrencyDefinition[] oResponseCurrencyDefinition= serviceResponseTO.data.get('returnCurrencyDefinition').collect().toArray()
			println "oResponseCurrencyDefinition.toString() ==> " + oResponseCurrencyDefinition.toString();
			for (var in oResponseCurrencyDefinition){
				Assert.assertNotNull("code is null ",var.code)
				Assert.assertNotNull("description is null ",var.description)
				Assert.assertNotNull("symbol is null ",var.symbol)
				Assert.assertNotNull("nemonic is null ",var.nemonic)
				Assert.assertNotNull("country Code is null ",var.countryCode)
				Assert.assertNotNull("country is null ",var.country)
				Assert.assertNotNull("state is null ",var.state)
				Assert.assertNotNull("hasDecimal is null ",var.hasDecimal)
								
				println ('------------ RESULTADO -----------')
				println " *** code         ---> "+var.code
				println " *** description  ---> "+var.description
				println " *** symbol       ---> "+var.symbol
				println " *** nemonic      ---> "+var.nemonic
				println " *** country Code ---> "+var.countryCode
				println " *** country      ---> "+var.country
				println " *** state        ---> "+var.state
				println " *** hasDecimal   ---> "+var.hasDecimal
			}
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
