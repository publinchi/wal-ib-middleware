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
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanData
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest;
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;
class Tes_ib_Loan_Account_orchestration {
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
		@Test
	void LoanAccount(){
		String ServiceName='LoanAccount'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanAccount')
			PaymentRequest   wPaymentRequest =new PaymentRequest()
			wPaymentRequest.loanNumber='10410108232700018'
			wPaymentRequest.userName='testCts'
			wPaymentRequest.currencyId=0
			serviceRequestTO.addValue('inPaymentRequest',  wPaymentRequest)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			LoanData[] oResponseParameter= serviceResponseTO.data.get('returnLoanData').collect().toArray()
				for (var in oResponseParameter) {
					Assert.assertNotNull("Account  is null ",var.account)
					println ('------------ RESULTADO -----------')
					println " ***Account           ---> "+var.account
			}
	 }catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	

	
	
	


					
					
					
	}
