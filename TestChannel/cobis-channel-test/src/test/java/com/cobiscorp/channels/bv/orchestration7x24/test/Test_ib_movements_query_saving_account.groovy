package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert
import com.cobiscorp.test.VirtualBankingBase;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryCreditCard
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption


import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_movements_query_saving_account {
	
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
		//virtualBankingBase.closeSessionNatural(initSession)
		virtualBankingBase.closeSessionNatural(initSession)
	}

	@Test
	void testGetMovementQuerySavingAccount() {
		def ServiceName='testGetMovementQuerySavingAccount'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountOperations.GetSavingsAccountOperations')
			
			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()

			wEnquiryRequest.productId=CTSEnvironment.bvAccCtaAhoType
			wEnquiryRequest.currencyId=CTSEnvironment.bvAccCtaAhoCurrencyId
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCtaAhoNumber
			wEnquiryRequest.userName=CTSEnvironment.bvLogin
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat

			SearchOption wSearchOption = new SearchOption()
			wSearchOption.initialDate = CTSEnvironment.bvInitialDate
			wSearchOption.finalDate = CTSEnvironment.bvFinalDate
			
			AccountStatement wAccountStatement = new AccountStatement()
			wAccountStatement.sequential = 0
			wAccountStatement.alternateCode = 0
			wAccountStatement.type = 'T'
			wAccountStatement.hour = null
			wAccountStatement.uniqueSequential = 0
						
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inAccountStatement', wAccountStatement)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			println ('RESPUESTA:------------------>' )
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test --->Ejecutado con param. de entrada:"
			println "Test --->Numero de Cuenta->" + wEnquiryRequest.productNumber
			println "Test --->Tipo de transaccion->" + wAccountStatement.type
			println "Test --->Fecha desde->" + wSearchOption.initialDate
			println "Test --->Fecha Hasta->" + wSearchOption.finalDate
			
			AccountStatement[] oResponseSavingAccount= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
			for (var in oResponseSavingAccount) {
				println "**** ------------------ ****"
				println " ***  Descripcion ---> "+var.description
				println " ***  Fecha ---> "+var.transactionDate
				println " ***  Db. Cr. ---> "+var.signDC
				println " ***  Monto ---> "+var.amount
				println " ***  Debito ---> "+var.debitsAmount
				println " ***  Saldo Contable ---> "+var.accountingBalance
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
	
	

}
