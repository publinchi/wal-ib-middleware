package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.VirtualBankingBase;
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CreditCard
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


class Test_ib_company_get_prize_card {

	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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

	@Test
	void testGetCardPrizeCompany() {
		def ServiceName='testGetCardPrizeCompany'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.GetPrize')
			
			TransactionRequest wTransactionRequest = new TransactionRequest();
			wTransactionRequest.dateFormatId= CTSEnvironment.bvDateFormat;
			wTransactionRequest.productNumber= CTSEnvironment.bvAccCompanyTarNumber;
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> name-> " + wTransactionRequest.dateFormatId
			println "Test ---> card-> " + wTransactionRequest.productNumber
			
			CreditCard[] oResponseCreditCard= serviceResponseTO.data.get('returnCreditCard').collect().toArray()
			
			for (var in oResponseCreditCard) {
				Assert.assertNotNull("card is null", var.card);
				Assert.assertNotNull("totalPrize is null", var.totalPrize);
				Assert.assertNotNull("cutoffDate is null", var.cutoffDate);
				
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  card ---> "+var.card
				println " ***  totalPrize ---> "+var.totalPrize
				println " ***  cutoffDate ---> "+var.cutoffDate
				
			}
			
			//
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
		
	}
}
