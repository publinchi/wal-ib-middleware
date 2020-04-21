package com.cobiscorp.channels.bv.orchestration7x24.test





import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert
import org.springframework.test.AssertThrows;

import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DestinationAccount
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DestinationAccountCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LotDestination
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Address

class Test_ib_validation_third {

	
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
	void testValidateInternalDestination() {
		def ServiceName='testValidateInternalDestination'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.ValidateInternalDestination')
			
			//DTO IN
			
			def wLot = new LotDestination()
			def wDestination = new DestinationAccount()
			wLot.userName = CTSEnvironment.bvLogin
			wDestination.productNumber = CTSEnvironment.bvAccCtaAhoNumber
			wDestination.productId  = CTSEnvironment.bvAccCtaAhoType
			wDestination.currencyId = CTSEnvironment.bvAccCtaAhoCurrencyId
			
			serviceRequestTO.addValue('inLotDestination', wLot)
			serviceRequestTO.addValue('inDestinationAccount', wDestination)
			
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			
			DestinationAccount[] oDestinationAccount= serviceResponseTO.data.get('returnDestinationAccount').collect().toArray()
			
			for (var in oDestinationAccount) {
				
				Assert.assertNotNull("Beneficiary Id is null",var.beneficiaryId)
				Assert.assertNotNull("Beneficiary Name  is null",var.beneficiaryName)
				Assert.assertNotNull("Currency Id is null",var.currencyId)
				Assert.assertNotNull("Product Id is null",var.productId)
				Assert.assertNotNull("Account Number is null",var.accountNumber)
				Assert.assertNotNull("Product Abbreviation is null",var.productAbbreviation)
				Assert.assertNotNull("Product Description is null",var.productDescription)
				Assert.assertNotNull("Currency Description is null",var.currencyDescription)
				
				
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Beneficiary Id ---> "+var.beneficiaryId
				println " ***  beneficiaryName ---> "+var.beneficiaryName
				println " ***  currencyId ---> "+var.currencyId
				println " ***  productId ---> "+var.productId
				println " ***  accountNumber ---> "+var.accountNumber
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
	

}
