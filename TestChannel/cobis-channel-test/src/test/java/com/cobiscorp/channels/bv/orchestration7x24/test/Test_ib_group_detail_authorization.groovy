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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LoginPending
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/**
 *
 * @author wsanchez
 *
 */

class Test_ib_group_detail_authorization {
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
	
	void testGroupDetailTransfer()
	{
		int TransaccionCtasPropias = virtualBankingBase.OperationNumber('testCtsGrupo2', 18056)
		testDetailLoginAuthorization(TransaccionCtasPropias, 'Cuentas Propias')
				
		int TransaccionPago =virtualBankingBase.OperationNumber('testCtsGrupo2', 1800025)
		testDetailLoginAuthorization(TransaccionPago,'Pagos')				//detalle de  pagos
		
		//testDetailLoginAuthorization(2327,'Cuentas Propias')	//detalle de transferecia cuentas propias
		//testDetailLoginAuthorization(2839,'terceros')			//detalle de transferecia a terceros
		//testDetailLoginAuthorization(2814,'Pagos')				//detalle de  pagos
	}
	
	
	void testDetailLoginAuthorization(int transaccion, String tipoconsulta) {
		String ServiceName='GetPendingLogins'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Authorization.PendingTransaction.GetPendingLogins')

			//DTO IN
			Authorizer wAuthorizer = new Authorizer()
			wAuthorizer.id = transaccion //2775 //2193
						
			TransactionRequest wTransactionRequest =  new TransactionRequest()
			wTransactionRequest.dateFormatId = 103
			
			
			serviceRequestTO.addValue('inAuthorizer', wAuthorizer)
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
			
			LoginPending[] oResponseParameter= serviceResponseTO.data.get('returnLoginPending').collect().toArray()
			for (var in oResponseParameter) {
				Assert.assertNotNull("User Name is null ",var.userName)
				Assert.assertNotNull("Description is null ",var.description)
				Assert.assertNotNull("Status is null ",var.status)
				Assert.assertNotNull("Reason is null ",var.reason)
				
				println ('RESPUESTA:------------------>'+ tipoconsulta)
				println " ***  userName ---> "+var.userName
				println " ***  description ---> "+var.description
				println " ***  Status ---> "+var.status
				println " ***  reason ---> "+var.reason
			}

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
	}
}
