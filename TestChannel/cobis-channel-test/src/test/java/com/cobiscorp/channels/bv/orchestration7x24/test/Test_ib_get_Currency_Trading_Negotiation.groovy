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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CurrencyTrading
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_get_Currency_Trading_Negotiation {
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
	void testGetCurrencyTrading(){
		def ServiceName = "testGetCurrencyTrading"
		
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetCurrencyTrading')
			
			//DTO IN			
			CurrencyTrading wCurrencyTradingNegotiation = new CurrencyTrading()
			wCurrencyTradingNegotiation.office = 0
			wCurrencyTradingNegotiation.client = 277
			wCurrencyTradingNegotiation.currency = 17
			wCurrencyTradingNegotiation.module = "BVI"
			wCurrencyTradingNegotiation.typeOperation = "C"
			wCurrencyTradingNegotiation.sequentialPreauthorization = 288 //código de servicios bancarios
			
			serviceRequestTO.addValue('inCurrencyTrading', wCurrencyTradingNegotiation)
			
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
			
			println ('------------ RESULTADO -----------')			
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion")
			println " *** cotizacion   ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto")
			println " *** monto        ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_factor")
			println " *** factor       ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_moneda")
			println " *** moneda       ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_obs")
			println " *** observacion  ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fecha_t_mas_n")
			println " *** fecha nego   ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_otr_c")
			println " *** monto otr    ---> "+wOref
			wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_des_moneda")
			println " *** desc moneda  ---> "+wOref
				
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
