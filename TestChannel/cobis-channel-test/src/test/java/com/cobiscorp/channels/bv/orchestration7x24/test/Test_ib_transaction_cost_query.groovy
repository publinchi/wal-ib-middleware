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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Transaction
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_transaction_cost_query {

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
	void testExecuteTrnCostQuery() {
		String ServiceName='TransactionCost'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Transaction.GetThirdPartyTransactionCost')

			//DTO IN
			Transaction wTransaction = new Transaction()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()
			User wUser= new User()
			
			wTransaction.id = 18340  //18862
			wTransaction.serviceId = '1'
			wTransaction.accountNumber = CTSEnvironment.bvAccCtaCteNumber
			wTransaction.accountType = CTSEnvironment.bvAccCtaCteType
			wTransaction.entryId = 'CTRP'
			wTransaction.money = CTSEnvironment.bvAccCtaCteCurrencyId
			wTransaction.clientId = CTSEnvironment.bvEnteMis;
						
			wTransactionContextCIB.transactionId = wTransaction.id
			wTransactionContextCIB.authenticationRequired='N';
			wTransactionContextCIB.costType = 'T'
			wTransactionContextCIB.costRequired = 'S'
			
			wUser.entityId= CTSEnvironment.bvEnteMis;

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransaction', wTransaction)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo costo
			Transaction[] oResponseTran= serviceResponseTO.data.get('returnTran').collect().toArray()
			if (oResponseTran.length > 0){				
				println ('RESPUESTA: costo----->' + oResponseTran[0].cost)				
			}
			else			
				Assert.assertNotNull("No se obtuvo costo")

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	
}
