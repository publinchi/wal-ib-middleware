package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource;
 
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Check


import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_group_checks_query {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession)
	}
	/**
	 * Checks Query Test
	 */
	@Test
	void testGroupChecks()
	{
		GroupChecksQueryByNumber()
		GroupChecksQuery()
	}
	
	
	void GroupChecksQuery(){
		String ServiceName='ChecksQuery'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.SearchCheck.SearchChecks')
			//DTO IN
			 
			Check wCheck = new Check()
					 
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat
			wEnquiryRequest.productId = 3
			wEnquiryRequest.currencyId = 0
			wEnquiryRequest.transactionId = 1800026
			wEnquiryRequest.userName = CTSEnvironment.bvGroupLogin
			wEnquiryRequest.productNumber = "10410108275405315"
 
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.criteria = "2"
			wSearchOption.initialDate = "02/04/2012"
			wSearchOption.finalDate = "01/06/2013"
			wSearchOption.initialCheck = 1
			wSearchOption.finalCheck = 150
			//wSearchOption.statusCheck = 'P'
			
			serviceRequestTO.addValue('inCheck', wCheck)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
		
			println ('------------ **** RESULT **** -----------')
			Check[] oResponseChecks= serviceResponseTO.data.get('returnCheck').collect().toArray()
			for (var in oResponseChecks) {
				Assert.assertNotNull("Amount is null ",var.amount)
				Assert.assertNotNull("Date Payment is null ",var.datePayment)
				Assert.assertNotNull("Check Number is null ",var.checkNumber)
				Assert.assertNotNull("Status is null ",var.status)
				
				println("Amount: " + var.amount)
				println("Date Payment:" + var.datePayment)
				println("Check Number:" + var.checkNumber)
				println("Status:" + var.hour)
				println("Status:" + var.status)
				println ('-----------------------------------')
			}

			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
	}
	
	/**
	 * Checks Query ByNumber Test
	 */
	
	void GroupChecksQueryByNumber(){
		String ServiceName='ChecksQueryByNumber'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.SearchCheck.SearchCheckByNumber')
			//DTO IN

			Check wCheck = new Check()
						
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat
			wEnquiryRequest.productId = 3
			wEnquiryRequest.currencyId = 0
			wEnquiryRequest.transactionId = 1800026
			wEnquiryRequest.userName = CTSEnvironment.bvGroupLogin
			wEnquiryRequest.productNumber = "10410108275405315"
			wCheck.checkNumber = 9
			
		  
			serviceRequestTO.addValue('inCheck', wCheck)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
  
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
		
			println ('------------ **** RESULT **** -----------')
			Check[] oResponseChecks= serviceResponseTO.data.get('returnCheck').collect().toArray()
			for (var in oResponseChecks) {
				Assert.assertNotNull("Amount is null ",var.amount)
				Assert.assertNotNull("Date Payment is null ",var.datePayment)
				Assert.assertNotNull("Check Number is null ",var.checkNumber)
				Assert.assertNotNull("Status is null ",var.status)
				
				println("Amount: " + var.amount)
				println("Date Payment:" + var.datePayment)
				println("Check Number:" + var.checkNumber)
				println("Status:" + var.status)
				println ('-----------------------------------')
			}

			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
	}

}
