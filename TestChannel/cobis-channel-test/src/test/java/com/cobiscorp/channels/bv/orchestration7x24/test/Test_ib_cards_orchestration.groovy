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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FactoringPayments
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryCreditCard
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
/**
 * orchestration-core-ib-cards
 * @author schancay
 * @since Jun 19, 2014
 * @version 1.0.0
 */
class Test_ib_cards_orchestration {
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
	void testGetSummaryCreditCardByClient() {
		def ServiceName='testGetSummaryCreditCardByClient'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryCreditCard')

			//DTO IN
			FactoringPayments wFactoringPayments=new FactoringPayments()
			wFactoringPayments.operation="A"
			wFactoringPayments.clientId=123

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			serviceRequestTO.addValue('inFactoringPayments', wFactoringPayments)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnResponseSummaryCreditCard').collect().size()>0)
			ResponseSummaryCreditCard[] oResponseSummaryCreditCard= serviceResponseTO.data.get('returnResponseSummaryCreditCard').collect().toArray()

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
