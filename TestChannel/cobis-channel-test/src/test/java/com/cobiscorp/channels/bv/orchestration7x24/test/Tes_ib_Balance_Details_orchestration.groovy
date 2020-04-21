package com.cobiscorp.channels.bv.orchestration7x24.test

import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
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
import com.cobiscorp.test.VirtualBankingBase;
class Tes_ib_Balance_Details_orchestration {
			@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	/**
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testCompanyGetBalanceDetail(){
		String ServiceName='testCompanyGetBalanceDetail'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.LoanBalance.GetLoanBalance')
			//DTO IN
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.productId =  CTSEnvironment.bvLoanType
			wEnquiryRequest.currencyId = CTSEnvironment.bvLoanCurrencyId
			wEnquiryRequest.productNumber= CTSEnvironment.bvLoanNumber
			wEnquiryRequest.userName= CTSEnvironment.bvLogin
			wEnquiryRequest.validateAccount='N'
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
		    ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
		   	//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		    codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
		Assert.assertTrue(message, serviceResponseTO.success)
		LoanBalance [] oResponseParameter= serviceResponseTO.data.get('returnLoanBalance').collect().toArray()
			for (var in oResponseParameter) {
				
				Assert.assertNotNull("Aditional Data  is null ",var.result_submit_rpc)
				Assert.assertNotNull("product number is null ",var.productNumber)
				Assert.assertNotNull("entity name is null ",var.entityName)
				Assert.assertNotNull("operation Type  is null ",var.operationType)
				Assert.assertNotNull("initial Amount is null ",var.initialAmount)
				Assert.assertNotNull("monthly Payment Day is null ",var.monthlyPaymentDay)
				Assert.assertNotNull("status  is null ",var.status)
				Assert.assertNotNull("lastPaymentDate is null ",var.lastPaymentDate)
				Assert.assertNotNull("expirationDate is null ",var.expirationDate)
				Assert.assertNotNull("executive  is null ",var.executive)
				Assert.assertNotNull("initial Date is null ",var.initialDate)
				Assert.assertNotNull("arrears Days is null ",var.arrearsDays)
			    Assert.assertNotNull("overdue Capital is null ",var.overdueCapital)
				Assert.assertNotNull("overdue Interest is null ",var.overdueInterest)
				Assert.assertNotNull("overdue Arrears Value  is null ",var.overdueArrearsValue)
				Assert.assertNotNull("overdue Another Items is null ",var.overdueAnotherItems)
				Assert.assertNotNull("overdue Total is null ",var.overdueTotal)
				Assert.assertNotNull("next Payment is null ",var.nextPaymentDate)
				Assert.assertNotNull("next Payment Value is null ",var.nextPaymentValue)
			
				Assert.assertNotNull("ordinary Interest Rate Value  is null ",var.ordinaryInterestRate)
				Assert.assertNotNull("arrears Interest Rate is null ",var.arrearsInterestRate)
				Assert.assertNotNull("capital Balance is null ",var.capitalBalance)
				Assert.assertNotNull("total Balance is null ",var.totalBalance)
				Assert.assertNotNull("original Term is null ",var.originalTerm)
				Assert.assertNotNull("sector is null ",var.sector)
				Assert.assertNotNull("operation Descriptionis null ",var.operationDescription)
				Assert.assertNotNull("feci is null ",var.feci)
				
				println ('------------ RESULTADO -----------')
				println " ***  Aditional Data           ---> "+var.result_submit_rpc
				println " ***  product number ---> "+var.productNumber
				println " ***  entity name   ---> "+var.entityName
				println " ***  operation Type      ---> "+var.operationType
				println " ***  initial Amount    ---> "+var.initialAmount
				println " ***  monthly Payment Day ---> "+var.monthlyPaymentDay
				println " ***  status  ---> "+var.status
				println " ***  last Payment Date     ---> "+var.lastPaymentDate
				println " ***  expiration Date   ---> "+var.expirationDate
				println " ***  executive           ---> "+var.executive
				println " ***  initial Date ---> "+var.initialDate
				println " ***  arrears Days   ---> "+var.arrearsDays
				println " ***  overdue Capital    ---> "+var.overdueCapital
				println " ***  overdue Interest   ---> "+var.overdueInterest
				println " ***  overdue Arrears Value ---> "+var.overdueArrearsValue
				println " ***  overdue Another Items  ---> "+var.overdueAnotherItems
				println " ***  overdue Total    ---> "+var.overdueTotal
				println " ***  next Payment Date   ---> "+var.nextPaymentDate
				println " *** next Payment Value   ---> "+var.nextPaymentValue
			
				println " ***  ordinary Interest Rate    ---> "+var.ordinaryInterestRate
				println " ***  arrears Interest Rate  ---> "+var.arrearsInterestRate
				println " ***  capital Balance Value ---> "+var.capitalBalance
				println " ***  total Balance  ---> "+var.totalBalance
				println " ***  original Term    ---> "+var.originalTerm
				println " ***  sector  ---> "+var.sector
				println " *** operation Description  ---> "+var.operationDescription
				println " ***  feci   ---> "+var.feci
							
				
			}
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
