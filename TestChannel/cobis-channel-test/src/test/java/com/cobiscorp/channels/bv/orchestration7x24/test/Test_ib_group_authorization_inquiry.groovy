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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Authorizer
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/**
 *
 * @author wsanchez
 *
 */

class Test_ib_group_authorization_inquiry {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
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
	void testGroupAuthorization() {
		String ServiceName='GetPendingTransactions'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Authorization.PendingTransaction.GetPendingTransactions')

			//DTO IN
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.sequential = 0
			wSearchOption.criteria = 'T'
			wSearchOption.initialDate = '04/02/2013'
			wSearchOption.finalDate = '05/02/2013'
			wSearchOption.numberOfResults = 20
			
			TransactionRequest wTransactionRequest = new TransactionRequest()
			wTransactionRequest.dateFormatId = 103
			wTransactionRequest.userName =  CTSEnvironment.bvGroupLogin 
			
			
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)
		
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			
			Authorizer[] oResponseAuthorizer= serviceResponseTO.data.get('returnAuthorizer').collect().toArray()
			
			for (var in oResponseAuthorizer) {
				Assert.assertNotNull("transaction Description is null ",var.transactionDescription)
				Assert.assertNotNull("Status is null ",var.status)
				Assert.assertNotNull("Transaccion Id is null ",var.transactionId)
				Assert.assertNotNull("Id is null ",var.id)
				Assert.assertNotNull("userName is null ",var.userName)
				
				println ('RESPUESTA:------------------>' )
				println " ***  Descripcion ---> "+var.transactionDescription
				println " ***  Status ---> "+var.status
				println " ***  Transaccion Id ---> "+var.transactionId
				println " ***  Id ---> "+var.id
				println " ***  userName ---> "+var.userName
			}

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
	}
	
}