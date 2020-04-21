package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author wsanchez
 * @since Sep 15, 2014
 * @version 1.0.0
 */

class Test_ib_company_get_loan_statement {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany(initSession)
	}
	/**
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testCompanyGetLoanStatement(){
		String ServiceName='GetLoanStatement'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanStatement')
			//DTO IN
			LoanBalance wLoanBalance = new LoanBalance()
			wLoanBalance.productNumber =  "10410000041700201"
			
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat
			
			
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.sequential  =0
					
			serviceRequestTO.addValue('inLoanBalance', wLoanBalance)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
		
			println ('------------********-----------')
			LoanStatement[] oResponseParameter= serviceResponseTO.data.get('returnLoanStatement').collect().toArray()
			for (var in oResponseParameter) {
				Assert.assertNotNull("Amount is null ",var.amount)
				Assert.assertNotNull("Arrears Interest is null ",var.arrearsInterest)
				Assert.assertNotNull("Normal Interest is null ",var.normalInterest)
				Assert.assertNotNull("Payment Date is null ",var.paymentDate)
				Assert.assertNotNull("Payment Type is null ",var.paymentType)
				
				println ('------------ RESULTADO -----------')
				println " ***  Amount           ---> "+var.amount
				println " ***  Arrears Interest ---> "+var.arrearsInterest
				println " ***  Normal Interest  ---> "+var.normalInterest
				println " ***  Payment Date     ---> "+var.paymentDate
				println " ***  Payment Type     ---> "+var.paymentType
				
			}

			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
	}
	}
}
