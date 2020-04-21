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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryCreditCard

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Pck:orchestration-core-ib-query-accounts
 * Class:AhAccountBalanceQueryOrchestationCore
 * 
 * @author eortega
 * @since Jun 26, 2014
 * @version 1.0.0
 */
class Test_ib_cc_accounts_query_orchestration {
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
	void testGetCcBalanceAccountQueryByAccount() {
		def ServiceName='GetCheckingAccountBalance'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountBalance.GetCheckingAccountBalance')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()

			wEnquiryRequest.productId=CTSEnvironment.bvAccCtaCteType   
			wEnquiryRequest.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wEnquiryRequest.userName=CTSEnvironment.bvLogin

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnAccountBalance').collect().size()==1)
			ResponseSummaryCreditCard[] oResponseSummaryCreditCard= serviceResponseTO.data.get('returnAccountBalance').collect().toArray()

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
