package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/** 
 *
 * @author mvelez
 *
 */

// PRODUCT: SAVINGS ACCOUNT,  TYPE USER: NATURAL USER 
class Test_ib_ah_ultmovements_accounts_query_orchestration {
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
	void TestGetAHUltMovementsQuery () {
		def ServiceName='TestGetAHUltMovementsQuery'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountOperations.GetSavingsAccountLastOperations')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()

			wEnquiryRequest.productId=CTSEnvironment.bvAccCtaAhoType
			wEnquiryRequest.currencyId=CTSEnvironment.bvAccCtaAhoCurrencyId
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCtaAhoNumber
			wEnquiryRequest.userName=CTSEnvironment.bvLogin
			wEnquiryRequest.dateFormatId = 103 //CTSEnvironment.bvDateFormat

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
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inAccountStatement', wAccountStatement)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test --->Ejecutado con param. de entrada:"
			println "Test --->Numero de Cuenta ->" + wEnquiryRequest.productNumber
			println "Test --->Usuario          ->" + wEnquiryRequest.userName
			
			//Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnAccountStatement').collect().size()==1)
			AccountStatement[] oResponse= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
			for (var in oResponse) {
				println "**** ------------------ ****"
				println " ***  Fecha        ---> "+var.transactionDate
				println " ***  Transaccion  ---> "+var.description
				println " ***  Cod. Transac.---> "+var.operationType
				println " ***  Referencia   ---> "+var.reference
				println " ***  D/C          ---> "+var.signDC
				println " ***  Valor        ---> "+var.amount
				println " ***  Contable     ---> "+var.accountingBalance
				println " ***  Disponible   ---> "+var.availableBalance
				println " ***  Secuencial   ---> "+var.sequential
				println " ***  Cod Alterno  ---> "+var.alternateCode
				println " ***  Hora         ---> "+var.hour
				println " ***  Sec Unico    ---> "+var.uniqueSequential
				println " ***  Imagen       ---> "+var.image
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			//virtualBankingBase.closeSessionCompanyA(initSession)
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
