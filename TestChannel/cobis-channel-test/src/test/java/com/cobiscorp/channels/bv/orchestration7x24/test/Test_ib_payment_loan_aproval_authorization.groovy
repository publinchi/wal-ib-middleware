package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Authorizer
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LoginPending

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

class Test_ib_payment_loan_aproval_authorization {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompanyA();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompanyA(initSession)
	}

	/**
	 * this method return the details of loan for a user  type Company
	 */

	@Test
	void testPaymentLoansApprovalAuthorizacion() {
		def ServiceName= 'testPaymentLoans'
		try{

			println "Test ---> ${ServiceName}"
					
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Authorization.Service.PendingTransaction.AuthorizePendingTransaction')
			
			Authorizer wAuthorizer = new Authorizer()
			wAuthorizer.id =  virtualBankingBase.OperationNumber(CTSEnvironment.bvCompanyLoginA, 1800025) // (int)2660
			wAuthorizer.status='PENDIENTE'
			wAuthorizer.transactionDescription='PAGO DE PRESTAMOS POR BANCA EN LINEA'
			wAuthorizer.transactionId=1800025 //Transacción de pago 
			wAuthorizer.userName= CTSEnvironment.bvCompanyLogin//'testCtsEmp'
			wAuthorizer.statusId='P'			
			
			LoginPending wLoginPending = new LoginPending()
			wLoginPending.userName=CTSEnvironment.bvCompanyLoginA //'testCtsEmpA'
			wLoginPending.reason='Aprobacion Regresion'
			wLoginPending.status='P'
			wLoginPending.newStatus='A'

			serviceRequestTO.addValue('inAuthorizer', wAuthorizer)
			serviceRequestTO.addValue('inLoginPending', wLoginPending)
			
			
			println ">>>>antes de ejecutar servicio"
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			println ">>>>despues de ejecutar servicio"
			def message=''		
				
				//Valido si fue exitoso la ejecucion
				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}

				Assert.assertTrue(message, serviceResponseTO.success)

			
			int reentry = virtualBankingBase.verifyAutorizationReentry(CTSEnvironment.bvCompanyLoanNumber, "sp_tr_pago_prestamo_cca")
			
			if (reentry == 0)
			{
				Assert.fail('No grabo reeentry ')
			} 
			
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompanyA(initSession)
			Assert.fail()
		}
	}

}
