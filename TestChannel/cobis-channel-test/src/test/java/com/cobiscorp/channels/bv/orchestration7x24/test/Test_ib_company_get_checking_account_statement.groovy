/**
 * 
 */
package com.cobiscorp.channels.bv.orchestration7x24.test
import org.junit.Assert
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.CTSEnvironment;
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author mvelez
 *
 */
class Test_ib_company_get_checking_account_statement {
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
	void testGetCheckingAccountStatement() {
		def ServiceName='testGetCheckingAccountStatement'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountStatement.GetCheckingAccountStatement')

			AccountStatement wAccountStatement = new AccountStatement()      
			EnquiryRequest   wEnquiryRequest   = new EnquiryRequest()
			//SearchOption     wSearchOption     = new SearchOption()
			 
			wAccountStatement.sequential   = 0;
			wAccountStatement.alternateCode = -1;			
			
			wEnquiryRequest.productId  = 3;
			wEnquiryRequest.currencyId = 0;
			wEnquiryRequest.userName   = CTSEnvironment.bvLoginEmpresa;
			wEnquiryRequest.productNumber   = CTSEnvironment.bvCompanyAccCtaCteNumber; 
			wEnquiryRequest.daily           =1; /*0= return history*/			
			wEnquiryRequest.transactionDate =CTSEnvironment.bvTransactionDate;
			
			serviceRequestTO.addValue('inAccountStatement', wAccountStatement);
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest);
						
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='<<<Ejecucion del Servicio Fallido>>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> Product Number-> " + wEnquiryRequest.productNumber
			
			println "******************** RETURN ACCOUNT STATEMENT ********************"
			AccountStatement[] oResponseAccountStatement= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()			
			for (var in oResponseAccountStatement) {
				Assert.assertNotNull("transactionDate is null ",var.transactionDate)
				Assert.assertNotNull("reference is null ",var.reference)
				Assert.assertNotNull("description is null ",var.description)
				Assert.assertNotNull("debitsAmount is null ",var.debitsAmount)
				Assert.assertNotNull("creditsAmount is null ",var.creditsAmount)
				Assert.assertNotNull("accountingBalance is null ",var.accountingBalance)
				Assert.assertNotNull("signDC is null ",var.signDC)
				Assert.assertNotNull("hour is null ",var.hour)
				Assert.assertNotNull("operationType is null ",var.operationType)
				Assert.assertNotNull("causeId is null ",var.causeId)
				Assert.assertNotNull("sequential is null ",var.sequential)
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  transactionDate ---> "+var.transactionDate
				println " ***  reference ---> "+var.reference
				println " ***  description ---> "+var.description
				println " ***  debitsAmount ---> "+var.debitsAmount
				println " ***  creditsAmount ---> "+var.creditsAmount
				println " ***  accountingBalance---> "+var.accountingBalance
				println " ***  signDC---> "+var.signDC
				println " ***  hour---> "+var.hour
				println " ***  operationType---> "+var.operationType
				println " ***  causeId---> "+var.causeId
				println " ***  sequential---> "+var.sequential
			}
			
			println "******************** RETURN ACCOUNT BALANCE ********************"
			AccountBalance[] oResponseAccountBalance= serviceResponseTO.data.get('returnAccountBalance').collect().toArray()
			for (var in oResponseAccountBalance) {
				Assert.assertNotNull("productNumber is null ",var.productNumber)
				Assert.assertNotNull("clientName is null ",var.clientName)
				Assert.assertNotNull("currencyName is null ",var.currencyName)
				Assert.assertNotNull("executiveName is null ",var.executiveName)
				Assert.assertNotNull("deliveryAddress is null ",var.deliveryAddress)
				Assert.assertNotNull("availableBalance is null ",var.availableBalance)
				Assert.assertNotNull("accountingBalance is null ",var.accountingBalance)
				Assert.assertNotNull("lastCutoffBalance is null ",var.lastCutoffBalance)
				Assert.assertNotNull("averageBalance is null ",var.averageBalance)
				Assert.assertNotNull("lastOperationDate is null ",var.lastOperationDate)
				Assert.assertNotNull("lastCutoffDate is null ",var.lastCutoffDate)
				Assert.assertNotNull("nextCutoffDate is null ",var.nextCutoffDate)
				Assert.assertNotNull("clientPhone is null ",var.clientPhone)
				Assert.assertNotNull("clientEmail is null ",var.clientEmail)
				Assert.assertNotNull("officeName is null ",var.officeName)
				Assert.assertNotNull("toDrawBalance is null ",var.toDrawBalance)
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  transactionDate ---> "+var.productNumber
				println " ***  clientName ---> "+var.clientName
				println " ***  currencyName ---> "+var.currencyName
				println " ***  executiveName ---> "+var.executiveName
				println " ***  deliveryAddress ---> "+var.deliveryAddress
				println " ***  availableBalance ---> "+var.availableBalance
				println " ***  accountingBalance ---> "+var.accountingBalance
				println " ***  lastCutoffBalance ---> "+var.lastCutoffBalance
				println " ***  averageBalance ---> "+var.averageBalance
				println " ***  lastOperationDate ---> "+var.lastOperationDate
				println " ***  lastCutoffDate ---> "+var.lastCutoffDate
				println " ***  nextCutoffDate ---> "+var.nextCutoffDate
				println " ***  clientPhone ---> "+var.clientPhone
				println " ***  clientEmail ---> "+var.clientEmail
				println " ***  officeName ---> "+var.officeName
				println " ***  toDrawBalance ---> "+var.toDrawBalance

			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
}
