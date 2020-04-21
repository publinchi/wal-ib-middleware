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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance;

class Test_ib_get_loan_amortization {
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
	void testGetLoanAmortization() {
		def ServiceName='testGetLoanAmortization'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanAmortization')
			//println "Test Mario1"
			//DTO IN
			LoanBalance    wLoanBalance    = new LoanBalance()
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			SearchOption   wSearchOption   = new SearchOption()
			//println "Test Mario2"
			wLoanBalance.productNumber   = "10410000041700201";
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat;
			wSearchOption.sequential     = "0";
			//println "Test Mario3"
			serviceRequestTO.addValue('inLoanBalance', wLoanBalance);
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest);
			serviceRequestTO.addValue('inSearchOption', wSearchOption);
			//println "Test Mario4"
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//println "Test Mario5"
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> Product Number-> " + wLoanBalance.productNumber
			LoanSimResult[] oResponseLoanAmort= serviceResponseTO.data.get('returnLoanSimResult').collect().toArray()			
//			LoanAmort[] oResponseLoanAmort= serviceResponseTO.data.get('returnAddress').collect().toArray()
			for (var in oResponseLoanAmort) {	
				Assert.assertNotNull("Dividend is null ",var.dividend)
				Assert.assertNotNull("Date is null ",var.date)
				Assert.assertNotNull("Capital is null ",var.capital)
				Assert.assertNotNull("Interest is null ",var.interest)
				Assert.assertNotNull("feci is null ",var.feci)
				Assert.assertNotNull("CapitalAmount is null ",var.capitalAmount)
				Assert.assertNotNull("State is null ",var.state)
				Assert.assertNotNull("Payment is null ",var.payment)

				println "**** --------- RESPUESTA ---------- ****"
				println " ***  dividend ---> "+var.dividend
				println " ***  date ---> "+var.date
				println " ***  capital ---> "+var.capital
				println " ***  interest ---> "+var.interest
				println " ***  feci ---> "+var.feci
				println " ***  capitalAmount---> "+var.capitalAmount
				println " ***  state---> "+var.state
				println " ***  payment---> "+var.payment
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
