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

import com.cobiscorp.test.utils.BDD

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_payment_loan_desacoplado {
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
			def message=''
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
		
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			else{
				
				println "Servicio OK"
			Assert.assertTrue(message, serviceResponseTO.success)
					
				String wOReferencia = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
				String wOSecuencial = Integer.valueOf(serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_secuencial_pag"))
				
				
				if(wOReferencia!=null){
					println "=========================================="
					println "*****EJECUCION DE PAGO DE PRESTAMO"
					
					 println ("SECUENCIAL DE PAGO----->"+wOSecuencial+ "\n")
					println ("NUMERO DE REFERENCIA----->"+ wOReferencia+ "\n")
			   				}
				
			}
					
			println ">>>>EL PAGO SE EJECUTO CON EXITO"
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
			Assert.fail()
		}
	}

}
