package com.cobiscorp.channels.bv.orchestration7x24.test
import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.transfers.service.service.impl.Transfer
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;

class Tes_ib_TransferACH_Details_orchestration {
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

	@Test
	void testGetTransferACH(){
		
		
		String ServiceName='testGetTransferACH'
		print("Ingresa al servicio")
		
		try{

			println String.format('Test [%s]',ServiceName)
				// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.GetTransfersACH')
			print("LEEGADA 1")
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			SearchOption wSearchOption=new SearchOption()
			wEnquiryRequest.productNumber="01202000052"//CTSEnvironment.bvLoanNumber
			wEnquiryRequest.dateFormatId= 103
		    wSearchOption.initialDate="01/02/2014"
			wSearchOption.finalDate= "01/03/2014"
			wSearchOption.sequential=0
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			print("LEEGADA 2")
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				
				print("LEEGADA 3")
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			print("LEEGADA 4")
			Assert.assertTrue(message, serviceResponseTO.success)
			Payment[] oResponseParameter= serviceResponseTO.data.get('returnPayment').collect().toArray()
			print("LEEGADA 5")
				for (var in oResponseParameter) {
					print("ingresa al for")
			Assert.assertNotNull("payment Date is null ",var.paymentDate)
			Assert.assertNotNull("product Type  is null ",var.productType)
			Assert.assertNotNull("Aditional Data  is null ",var.accountAlias)
			Assert.assertNotNull("account Alias is null ",var.creditAccount)
			Assert.assertNotNull("entity Name is null ",var.entityName)
			Assert.assertNotNull("ammount  is null ",var.ammount)
			Assert.assertNotNull("notes  is null ",var.notes)
			Assert.assertNotNull("creation Date is null ",var.creationDate)
			Assert.assertNotNull("id ",var.id)
			Assert.assertNotNull("beneficiary Name is null ",var.beneficiaryName)
			Assert.assertNotNull("beneficiary Id is null ",var.beneficiaryId)
			Assert.assertNotNull("beneficiary Phone  is null ",var.beneficiaryPhone)
					
			println ('------------ RESULTADO -----------')
			println " ***  payment Date           ---> "+var.paymentDate
			println " ***  product Type           ---> "+var.productType
			println " ***  account Alias           ---> "+var.accountAlias
			println " ***  credit Account           ---> "+var.creditAccount
			println " ***  entity Name             ---> "+var.entityName
			println " ***  ammount                 ---> "+var.ammount
			println " ***  notes                  ---> "+var.notes
			println " ***  creation Date          ---> "+var.creationDate
			println " ***  id                        ---> "+var.id
			println " ***  beneficiary Name           ---> "+var.beneficiaryName
			println " ***  beneficiary Id          ---> "+var.beneficiaryId
			println " *** beneficiary Phone           ---> "+var.beneficiaryPhone
			
				}	
		}
		
		catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			
		}
}
}
