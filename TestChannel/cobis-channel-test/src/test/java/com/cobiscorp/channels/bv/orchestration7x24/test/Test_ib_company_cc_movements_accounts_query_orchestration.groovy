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
 * @author gyagual
 * @since Jul 22, 2014
 * @version 1.0.0
 *
 */
public class Test_ib_company_cc_movements_accounts_query_orchestration {

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
	
	/**
	 *This method gets movements from Checking Accounts(company)
	 */
   @Test
   void testCompanyGetCcMovementsAccountQuery() {
	   def ServiceName= 'GetCheckingAccountOperations'
	   try{

		   println "Test ---> ${ServiceName}"
		   // Preparo ejecución del servicio
		   ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		   serviceRequestTO.setSessionId(initSession)
		   serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountOperations.GetCheckingAccountOperations')

		   //DTO IN
		   AccountStatement wAccountStatement=new AccountStatement()
		   EnquiryRequest wEnquiryRequest = new EnquiryRequest()
		   SearchOption wSearchOption = new SearchOption()
		   

		   wAccountStatement.alternateCode= 0
		   wAccountStatement.hour= null
		   wAccountStatement.sequential=0
		   wAccountStatement.type="T"
		   wAccountStatement.uniqueSequential=0
		   
		   wEnquiryRequest.currencyId = CTSEnvironment.bvCompanyAccCtaCteCurrencyId
		   wEnquiryRequest.execution = CTSEnvironment.bvDateFormat
		   wEnquiryRequest.productId = CTSEnvironment.bvCompanyAccCtaCteType
		   wEnquiryRequest.productNumber =CTSEnvironment.bvCompanyAccCtaCteNumber
		   wEnquiryRequest.userName = CTSEnvironment.bvCompanyLogin
		   
		   wSearchOption.initialDate = CTSEnvironment.bvInitialDate
		   wSearchOption.finalDate = CTSEnvironment.bvFinalDate
		   
		   
		   serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
		   serviceRequestTO.addValue('inAccountStatement', wAccountStatement)
		   serviceRequestTO.addValue('inSearchOption', wSearchOption)

		   ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

		   //Valido si fue exitoso la ejecucion
		   def message=''
		   if (serviceResponseTO.messages.toList().size()>0){
			   message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		   }

		   Assert.assertTrue(message, serviceResponseTO.success)
		   println "Test --->Ejecutado con param. de entrada:"
		   println "Test --->Numero de Cuenta->" + wEnquiryRequest.productNumber
		   println "Test --->Tipo de transaccion->" + wAccountStatement.type
		   println "Test --->Fecha desde->" + wSearchOption.initialDate
		   println "Test --->Fecha Hasta->" + wSearchOption.finalDate
		   
		   //Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnAccountStatement').collect().size()==1)
		   AccountStatement[] oResponseCheckingAccount= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
		   for (var in oResponseCheckingAccount) {
			   println "**** ------------------ ****"
			   println " ***  Fecha ---> "+var.transactionDate
			   println " ***  Descripcion---> "+var.accountingBalance
			   println " ***  Db. Cr. ---> "+var.signDC
			   println " ***  Monto ---> "+var.amount
			   println " ***  Debito ---> "+var.debitsAmount
			   println " ***  Saldo Contable ---> "+var.accountingBalance
			   
		   }
	   }catch(Exception e){
		   def msg=e.message
		   println "${ServiceName} Exception--> ${msg}"
		   virtualBankingBase.closeSessionCompany(initSession)
	   }
   }
}
