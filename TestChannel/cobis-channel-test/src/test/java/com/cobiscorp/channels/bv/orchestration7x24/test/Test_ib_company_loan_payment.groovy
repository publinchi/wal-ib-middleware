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



import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_company_loan_payment {
	
		@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany(initSession)
	}
	
	@Test
	void payLoan() {
		println "ingresa al servicio 1 "
		
		def ServiceName= 'payLoan'
		println "ingresa al servicio 2"
		try{
			println "Test ---> ${ServiceName}"
					
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Loan.PayLoan')
			PaymentRequest wPaymentRequest=new PaymentRequest()
			TransactionContext 	wTransactionContext  = new TransactionContext()
			User wuser = new User()
			wPaymentRequest.currencyId =CTSEnvironment.bvLoanCurrencyId
			wPaymentRequest.userName =CTSEnvironment.bvLogin
			wPaymentRequest.account =CTSEnvironment.bvLoanNumber
			wPaymentRequest.concept ="PAGO PRESTAMO"
			wPaymentRequest.productName ="CABR"
			wPaymentRequest.productId= 1
			wPaymentRequest.amount =5000
			wPaymentRequest.loanNumber =CTSEnvironment.bvLoanNumber
			wPaymentRequest.destProduct ="CCA"
			wPaymentRequest.loanCurrencyId =CTSEnvironment.bvLoanCurrencyId
			wuser.entityId = CTSEnvironment.bvEnteMis
			wPaymentRequest.productAbbreviation ="CTE"
			wPaymentRequest.authorizationRequired ="N"
			wPaymentRequest.thirdPartyAssociated ="N"
			wPaymentRequest.isThirdParty ="N"
			wPaymentRequest.loanPaymentAmount =5000
			wPaymentRequest.creditAmount =2000
			wPaymentRequest.rateValue =5
			wPaymentRequest.validateAccount ="N"
			serviceRequestTO.addValue('inUser', wuser)
			serviceRequestTO.addValue('inwTransactionContext', wTransactionContext)
			serviceRequestTO.addValue('wPaymentRequest', wPaymentRequest)
			
					 
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
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
				println "no grabo en el reentry"
			}
	
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}

}
