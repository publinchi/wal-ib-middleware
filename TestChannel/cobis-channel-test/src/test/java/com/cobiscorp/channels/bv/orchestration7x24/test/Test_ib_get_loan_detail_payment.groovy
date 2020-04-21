package com.cobiscorp.channels.bv.orchestration7x24.test
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert

import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.SetUpTestEnvironment;
import com.cobiscorp.test.utils.VirtualBankingUtil;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSimResult
import cobiscorp.ecobis.internetbanking.webapp.services.dto.DetailLoanPayment;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest;

class Test_ib_get_loan_detail_payment {
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
	void testGetLoanDetailPayment() {
		def ServiceName='testGetLoanDetailPayment'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetDetailLoanPayment')
			PaymentRequest wPaymentRequest = new PaymentRequest();
			wPaymentRequest.loanNumber="10410108232700018";
			wPaymentRequest.transactionId= 656881419 ;
			wPaymentRequest.currencyId=0;
			wPaymentRequest.userName="testCts";
			//wPaymentRequest.loanNumber="";
			
			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> monto-> " + wPaymentRequest.amount
			
			println "Test ---> concept-> " + wPaymentRequest.concept
	
		
			println "Test ---> currencyId-> " + wPaymentRequest.currencyId
		
			DetailLoanPayment[] wDetailLoanPayment= serviceResponseTO.data.get('returnDetailLoanPayment').collect().toArray()			
//			LoanAmort[] oResponseLoanAmort= serviceResponseTO.data.get('returnAddress').collect().toArray()
			for (var in wDetailLoanPayment) {	
				Assert.assertNotNull("quota is null ",var.quota)
				Assert.assertNotNull("concept is null ",var.concept)
				Assert.assertNotNull("state is null ",var.state)
				Assert.assertNotNull("amount is null ",var.amount)
				Assert.assertNotNull("amountMN is null ",var.amountMN)
				Assert.assertNotNull("currencyId is null ",var.currencyId)

				println "**** --------- RESPUESTA ---------- ****"
				println " ***  quota		---> "+var.quota		
				println " ***  concept		---> "+var.concept		
				println " ***  state		---> "+var.state		
				println " ***  amount		---> "+var.amount		
				println " ***  amountMN	 	---> "+var.amountMN	
				println " ***  currencyId	---> "+var.currencyId	
				
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
