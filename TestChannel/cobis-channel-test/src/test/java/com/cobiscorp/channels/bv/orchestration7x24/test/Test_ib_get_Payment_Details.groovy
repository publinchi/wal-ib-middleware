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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetails
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_get_Payment_Details {
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
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetPaymentDetails')
			
			//DTO IN
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			
			wEnquiryRequest.setProductNumber("TRR00108000022")
			wEnquiryRequest.setDateFormatId(101)			
			wEnquiryRequest.setCurrencyId(0)
			wEnquiryRequest.setProductId(9)
			wEnquiryRequest.setUserName("testCts")	
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			
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
			PaymentDetails[] oResponsePaymentDetails= serviceResponseTO.data.get('returnPaymentDetails').collect().toArray()
			println "oResponsePaymentDetails.toString() ==> " + oResponsePaymentDetails.toString();
			for (var in oResponsePaymentDetails){
				Assert.assertNotNull("transaction Sequential is null ",var.transactionSequential)
				Assert.assertNotNull("number is null ",var.number)
				Assert.assertNotNull("term is null ",var.term)
				Assert.assertNotNull("payment Detail Sequential is null ",var.paymentDetailSequential)
				Assert.assertNotNull("payment Type is null ",var.paymentType)
				Assert.assertNotNull("payment Type Detail is null ",var.paymentTypeDetail)
				Assert.assertNotNull("extra Amount is null ",var.extraAmount)
				Assert.assertNotNull("currency is null ",var.currency)
				Assert.assertNotNull("currency Type is null ",var.currencyType)
				Assert.assertNotNull("local Amount is null ",var.localAmount)
				Assert.assertNotNull("detail is null ",var.detail)
				Assert.assertNotNull("payment Date Sequential is null ",var.paymentDateSequential)
								
				println ('------------ RESULTADO -----------')
				println " *** transaction Sequential    ---> "+var.transactionSequential
				println " *** number                    ---> "+var.number
				println " *** term                      ---> "+var.term
				println " *** payment Detail Sequential ---> "+var.paymentDetailSequential
				println " *** payment Type              ---> "+var.paymentType
				println " *** payment Type Detail       ---> "+var.paymentTypeDetail
				println " *** extra Amount              ---> "+var.extraAmount
				println " *** currency                  ---> "+var.currency
				println " *** currency Type             ---> "+var.currencyType
				println " *** local Amount              ---> "+var.localAmount
				println " *** detail                    ---> "+var.detail
				println " *** payment Date Sequential   ---> "+var.paymentDateSequential
			}
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}	
}
