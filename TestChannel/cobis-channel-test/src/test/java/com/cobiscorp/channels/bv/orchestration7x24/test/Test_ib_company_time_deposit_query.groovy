package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_company_time_deposit_query {
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
	 * Time Deposits Query Test
	 */
	@Test
	void testCompanyTimeDepositsQuery(){
		println ' ****** Prueba Regresión testgetTimeDepositDetail Empresa************* '
		def ServiceName= 'getTimeDepositDetail'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.FixedTermDepositBalance.GetFixedTermDepositBalance');
				 
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.transactionId = 1800022
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat
			wEnquiryRequest.productId =CTSEnvironment.bvAccDpfType;
			wEnquiryRequest.currencyId = CTSEnvironment.bvCompanyDpfCurrencyId;
			wEnquiryRequest.userName = CTSEnvironment.bvLoginEmpresa
			wEnquiryRequest.productNumber = CTSEnvironment.bvCompanyDPFNumber//					
			
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
		
			println ('------------********-----------')
			FixedTermDepositBalance[] oResponseFixedTerms= serviceResponseTO.data.get('returnFixedTermDepositBalance').collect().toArray()
			for (var in oResponseFixedTerms) {
				Assert.assertNotNull("ProductNumber is null ",var.productNumber)
				Assert.assertNotNull("OpenningDate is null ",var.openningDate)
				Assert.assertNotNull("ExpirationDate is null ",var.expirationDate)
				Assert.assertNotNull("CapitalBalance is null ",var.capitalBalance)
				Assert.assertNotNull("TotalInterestIncome is null ",var.totalInterestIncome)
				Assert.assertNotNull("Rate is null ",var.rate)
				Assert.assertNotNull("term is null ",var.term)
				Assert.assertNotNull("CapitalBalanceMaturity is null ",var.capitalBalanceMaturity)
				Assert.assertNotNull("AutomaticRenewal is null ",var.automaticRenewal)
				Assert.assertNotNull("IsCompounded is null ",var.isCompounded)
				Assert.assertNotNull("FrecuencyOfPayment is null ",var.frecuencyOfPayment)
				Assert.assertNotNull("AccountOfficer is null ",var.accountOfficer)
				Assert.assertNotNull("ValueDate is null ",var.valueDate)
				Assert.assertNotNull("CalculationBase is null ",var.calculationBase)
				Assert.assertNotNull("ProductAbbreviation is null ",var.productAbbreviation)
				Assert.assertNotNull("ProductAlias is null ",var.productAlias)
				
				println ('------------ RESULT COMPANY -----------')				
				println("ProductNumber is null " + var.productNumber)
				println("OpenningDate is null "+var.openningDate)
				println("ExpirationDate is null "+var.expirationDate)
				println("CapitalBalance is null "+var.capitalBalance)
				println("TotalInterestIncome is null "+var.totalInterestIncome)
				println("Rate is null "+var.rate)
				println("term is null "+var.term)
				println("CapitalBalanceMaturity is null "+var.capitalBalanceMaturity)
				println("AutomaticRenewal is null "+var.automaticRenewal)
				println("IsCompounded is null "+var.isCompounded)
				println("FrecuencyOfPayment is null "+var.frecuencyOfPayment)
				println("AccountOfficer is null "+var.accountOfficer)
				println("ValueDate is null "+var.valueDate)
				println("CalculationBase is null "+var.calculationBase)
				println("ProductAbbreviation is null "+var.productAbbreviation)
				println("ProductAlias is null "+var.productAlias)
				
			}

			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
		}
	}
}
