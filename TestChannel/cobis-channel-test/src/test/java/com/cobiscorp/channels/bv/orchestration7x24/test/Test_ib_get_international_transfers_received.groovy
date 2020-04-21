package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.DetailInternationalTransfersReceived
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.InternationalTransfersReceived
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil


class Test_ib_get_international_transfers_received {
	
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
	void testGetInternationalTransfersReceived(){
		def ServiceName = "testGetInternationalTransfersReceived"
		
		try{			
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			println("sssssssssssssssss")
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetDetailInternationalTransferReceived')
			println("sssssssssssssssss")
			//DTO IN			
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
						
			wEnquiryRequest.setDateFormatId(101)
			wEnquiryRequest.setProductNumber("TRR00108000022")
						
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
			DetailInternationalTransfersReceived[] oResponseDetailInternationalTransfersReceived= serviceResponseTO.data.get('returnDetailInternationalTransfersReceived').collect().toArray()
			println "oResponseInternationalTransfersReceived.toString() ==> " + oResponseDetailInternationalTransfersReceived.toString();
			for (var in oResponseDetailInternationalTransfersReceived){
				/*Assert.assertNotNull("issueDate is null ",var.issueDate)
				Assert.assertNotNull("accountType is null ",var.accountType)
				Assert.assertNotNull("account is null ",var.account)
				Assert.assertNotNull("beneficiary is null ",var.beneficiary)
				Assert.assertNotNull("clientNumber is null ",var.clientNumber)
				Assert.assertNotNull("branch is null ",var.branch)
				Assert.assertNotNull("originatorName is null ",var.originatorName)
				Assert.assertNotNull("originCountry is null ",var.originCountry)
				Assert.assertNotNull("originatorBank is null ",var.originatorBank)
				Assert.assertNotNull("correspondentBank is null ",var.correspondentBank)
				Assert.assertNotNull("amount is null ",var.amount)
				Assert.assertNotNull("official is null ",var.official)
				Assert.assertNotNull("operation is null ",var.operation)*/
				
				println ('------------ RESULTADO -----------')
				println " ***  sequential       ---> "+var.issueDate
				println " ***  sequential       ---> "+var.accountType
				println " ***  sequential       ---> "+var.account
				println " ***  sequential       ---> "+var.originatorName
				println " ***  sequential       ---> "+var.amount
				println " ***  sequential       ---> "+var.official
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
