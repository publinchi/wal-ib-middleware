package com.cobiscorp.chanels.bv.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.VirtualBankingBase;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment

/**
 *
 * @author mvelez
 *
 */

class TestAHUltMovementsQuery {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompanyA()
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
	void TestGetAHUltMovementsQuery () {
		def ServiceName='TestGetAHUltMovementsQuery'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountBalance.GetSavingsAccountBalance')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()

			wEnquiryRequest.productId=CTSEnvironment.bvCompanyAccCtaAhoType
			wEnquiryRequest.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId
			wEnquiryRequest.productNumber=CTSEnvironment.bvCompanyAccCtaAhoNumber
			wEnquiryRequest.userName=CTSEnvironment.bvCompanyLoginA
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat

			SearchOption wSearchOption = new SearchOption()
			wSearchOption.initialDate = CTSEnvironment.bvInitialDate
			wSearchOption.finalDate = CTSEnvironment.bvFinalDate
			
			AccountStatement wAccountStatement = new AccountStatement()
			wAccountStatement.sequential = 0
			wAccountStatement.alternateCode = 0
			wAccountStatement.type = 'T'			
			wAccountStatement.numberOfMovements = 5
			wAccountStatement.uniqueSequential = 0
						
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inEnquiryRequest', wSearchOption)
			serviceRequestTO.addValue('inEnquiryRequest', wAccountStatement)

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompanyA(initSession)
		}
	}
	
}
