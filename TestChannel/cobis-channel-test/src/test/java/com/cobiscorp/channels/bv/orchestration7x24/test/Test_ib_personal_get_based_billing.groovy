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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Entity
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Contract
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.InvoicingBase
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil






class Test_ib_personal_get_based_billing {

	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		println "prueba Inicio de Sesion"
		initSession= virtualBankingBase.initSessionNatural()
		println "sesionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn ---> ${initSession}"
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
	void testGetBaseBilling() {
		def ServiceName='testGetBaseBilling'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			println "sesionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn ---> ${initSession}"
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.GetInvoicingBase')
			
			//DTO IN
			TransactionRequest inTransactionRequest = new TransactionRequest(); 
			SearchOption inSearchOption = new SearchOption();
			Entity inEntity = new Entity();
			Contract inContract = new Contract();   
			
			inTransactionRequest.setDateFormatId(103);
			inSearchOption.setCriteria("0000000009 ");
			inEntity.setIdentification("0106800940");
			inEntity.setName("TESTCTS");
			inContract.setContractId(60);
			
			
			serviceRequestTO.addValue('inTransactionRequest', inTransactionRequest)
			serviceRequestTO.addValue('inSearchOption', inSearchOption)
			serviceRequestTO.addValue('inEntity', inEntity)
			serviceRequestTO.addValue('inContract', inContract)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			
			Assert.assertTrue(serviceResponseTO.success)
			
			InvoicingBase[] oResponseInvoicingBase= serviceResponseTO.data.get('returnInvoicingBase').collect().toArray()
			
			for (var in oResponseInvoicingBase) {
				Assert.assertNotNull("identification is null", var.identification)
				Assert.assertNotNull("debtorName is null", var.debtorName)
				Assert.assertNotNull("reference1 is null", var.reference1)
				Assert.assertNotNull("reference2 is null", var.reference2)
				Assert.assertNotNull("reference3 is null", var.reference3)
				Assert.assertNotNull("amount is null", var.amount)
				Assert.assertNotNull("paymentDate is null", var.paymentDate)
				Assert.assertNotNull("sequential is null", var.sequential)
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  identification ---> "+var.identification
				println " ***  debtorName ---> "+var.debtorName
				println " ***  reference1 ---> "+var.reference1
				println " ***  reference2 ---> "+var.reference2
				println " ***  reference3 ---> "+var.reference3
				println " ***  amount ---> "+var.amount
				println " ***  paymentDate ---> "+var.paymentDate
				println " ***  sequential ---> "+var.sequential
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
}
