/**
 * 
 */
package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert
import com.cobiscorp.test.CTSEnvironment;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author mvelez
 *
 */
class Test_ib_group_get_own_transfers {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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
	
	@Test
	void testGetOwnTransfers() {
		def ServiceName='testGetOwnTransfers'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.GetTransfers')

			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			SearchOption   wSearchOption   = new SearchOption()
			
			wEnquiryRequest.productNumber = CTSEnvironment.bvGroupAccCtaCteNumber;
			wEnquiryRequest.dateFormatId  = CTSEnvironment.bvDateFormat;
			wSearchOption.initialDate     = "01/01/2013"//CTSEnvironment.bvInitialDate;
			wSearchOption.finalDate       = "01/01/2014"//CTSEnvironment.bvFinalDate;
			wSearchOption.sequential      = 0;

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest);
			serviceRequestTO.addValue('inSearchOption', wSearchOption);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> Product Number -> " + wEnquiryRequest.productNumber
			println "Test ---> date Format    -> " + wEnquiryRequest.dateFormatId
			println "Test ---> Initial Date   -> " + wSearchOption.initialDate
			println "Test ---> Final Date     -> " + wSearchOption.finalDate
			println "Test ---> Sequential     -> " + wSearchOption.sequential
			
			Payment[] oResponseOwnTransfers = serviceResponseTO.data.get('returnPayment').collect().toArray()
			
			for (var in oResponseOwnTransfers) {
				Assert.assertNotNull("Payment Date is null ",var.paymentDate)
				Assert.assertNotNull("Credit Account is null ",var.creditAccount)
				//Assert.assertNotNull("Creditor is null ",var.creditor)
				Assert.assertNotNull("Ammount is null ",var.ammount)
				//Assert.assertNotNull("Notes is null ",var.notes)
				Assert.assertNotNull("Creation Date is null ",var.creationDate)
				Assert.assertNotNull("Id is null ",var.id)

				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Payment Date   ---> "+var.paymentDate
				println " ***  Credit Account ---> "+var.creditAccount
				println " ***  Creditor       ---> "+var.creditor
				println " ***  Ammount        ---> "+var.ammount
				println " ***  Notes          ---> "+var.notes
				println " ***  Creation Date  ---> "+var.creationDate
				println " ***  Id             ---> "+var.id

			}
		}catch(Exception e){
		def msg=e.message
		println "${ServiceName} Exception--> ${msg}"
		virtualBankingBase.closeSessionGroup(initSession)
		}
   }
}
