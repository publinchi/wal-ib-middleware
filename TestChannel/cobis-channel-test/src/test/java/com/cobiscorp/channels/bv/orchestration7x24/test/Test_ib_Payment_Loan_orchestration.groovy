package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.internetbanking.commons.admin.dto.TransactionContext
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CreditCardPaymentRequest;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentResponse

import com.cobiscorp.test.utils.BDD

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_Payment_Loan_orchestration {

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
	void payLoan() {

		println "PRUEBA 1"
		def ServiceName= 'payLoan'
		try{
			println "Test ---> ${ServiceName}"
			println "PRUEBA 2"

			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Loan.PayLoan')
			PaymentRequest wPaymentRequet = new PaymentRequest()
			wPaymentRequet.productId= CTSEnvironment.bvGroupAccCtaAhoType //4
			wPaymentRequet.currencyId=0
			wPaymentRequet.userName= CTSEnvironment.bvLogin
			wPaymentRequet.account= '10410108275249013'//'10410000005233616'
			wPaymentRequet.concept='Pago Prestamo regresion persona'
			wPaymentRequet.amount= Double.parseDouble("150")
			wPaymentRequet.loanNumber= '10410108232700018' //'10410108275249013'
			wPaymentRequet.destProduct = 7
			wPaymentRequet.productAbbreviation= CTSEnvironment.bvAbbreviationSavingAccount
			wPaymentRequet.productName= 'PAGO BANCA VIRTUAL'
			wPaymentRequet.thirdPartyAssociated='N'
			wPaymentRequet.isThirdParty='N'
			wPaymentRequet.loanPaymentAmount=Double.parseDouble('150')
			wPaymentRequet.creditAmount=Double.parseDouble('10')
			wPaymentRequet.rateValue=Double.parseDouble('0')
			wPaymentRequet.validateAccount='N'
			println "datos double parseados"
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()
			wTransactionContextCIB.authorizationRequired='N'
			User wUser = new User()
			//wUser.entityId = 'testCtsEmp'
			wUser.entityId = CTSEnvironment.bvEnteMis //13036

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequet)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)
			println ">>>>antes de ejecutar servicio"
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			println ">>>>despues de ejecutar servicio"
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
		
	       message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		    codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
						
				println "Servicio OK"
			Assert.assertTrue(message, serviceResponseTO.success)
			PaymentResponse [] oResponseParameter= serviceResponseTO.data.get('returnPaymentResponse').collect().toArray()
			for (var in oResponseParameter) {
				
							Assert.assertNotNull("reference is null ",var.reference)
				Assert.assertNotNull("return Value is null ",var.returnValue)
				
				println ('------------ RESULTADO -----------')
				println " ***  REFERENCIA         ---> "+var.reference
				println " ***  SECUENCIAL        ---> "+var.returnValue
				
				}
						
			println "El pago se ejecuto con exito"
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
			Assert.fail()
		}
	}














}
