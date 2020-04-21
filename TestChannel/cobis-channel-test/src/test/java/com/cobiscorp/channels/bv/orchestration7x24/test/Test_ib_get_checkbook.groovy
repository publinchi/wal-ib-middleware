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
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Checkbook
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.VirtualBankingBase;

class Test_ib_get_checkbook {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	/**
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testCompanyGetCheckbook(){
		String ServiceName='GetCheckbooks'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Checkbook.GetCheckbooks')

			//DTO IN
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.transactionId = 1800013
			wEnquiryRequest.productId = 3
			wEnquiryRequest.currencyId = 0
			wEnquiryRequest.productNumber = "10410108275405315" //CTSEnvironment.bvCompanyAccCtaCteNumber //bvAccCtaCteNumber
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat
			wEnquiryRequest.userName = CTSEnvironment.bvLogin
			
			
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.sequential  = 0
			wSearchOption.mode = 1
								
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
		
			println ('------------********-----------')
			Checkbook[] oResponseCheckbook= serviceResponseTO.data.get('returnCheckbook').collect().toArray()
			for (var in oResponseCheckbook) {
				Assert.assertNotNull("sequential is null ",var.sequential)
				Assert.assertNotNull("initialCheck  is null ",var.initialCheck)
				Assert.assertNotNull("numberOfChecks is null ",var.numberOfChecks)
				Assert.assertNotNull("type is null ",var.type)
				Assert.assertNotNull("creationDate is null ",var.creationDate)
				Assert.assertNotNull("deliveryDate is null ",var.deliveryDate)
				Assert.assertNotNull("creationDate is null ",var.creationDate)
				Assert.assertNotNull("status is null ",var.status)
				Assert.assertNotNull("runNumber is null ",var.runNumber)
				
				
				println ('------------ RESULTADO -----------')
				println " ***  sequential       ---> "+var.sequential
				println " ***  initialCheck     ---> "+var.initialCheck
				println " ***  numberOfChecks   ---> "+var.numberOfChecks
				println " ***  type             ---> "+var.type
				println " ***  creationDate     ---> "+var.creationDate
				println " ***  deliveryDate     ---> "+var.deliveryDate
				println " ***  status           ---> "+var.status
				println " ***  Amount           ---> "+var.printShippingDate
				println " ***  receiptPrintingDate---> "+var.receiptPrintingDate
				println " ***  receiptOfficeDate---> "+var.receiptOfficeDate
				println " ***  creationOffice   ---> "+var.creationOffice
				println " ***  receptionOffice  ---> "+var.receptionOffice
				println " ***  runNumber        ---> "+var.runNumber
			}

			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

}
