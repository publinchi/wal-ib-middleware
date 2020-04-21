package com.cobiscorp.channels.bv.orchestration7x24.test;



import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
/**
 * 
 * 
 * 
 * @author gyagual
 *
 */
public class Test_ib_company_cards_movements_query_orchestration {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase();
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
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
	void testCompanyGetCardsMovementsQuery() {
	def ServiceName= 'GetCreditCardOperationsCompany'
	try{

		println "Test ---> ${ServiceName}"
		// Preparo ejecución del servicio
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		serviceRequestTO.setSessionId(initSession)
		serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.Enquiries.GetCreditCardOperations')

		//DTO IN
		AccountStatement wAccountStatement=new AccountStatement()
		EnquiryRequest wEnquiryRequest = new EnquiryRequest()
		SearchOption wSearchOption = new SearchOption()
		User wUser = new User()
		

		wAccountStatement.numberOfMovements= 10
		wAccountStatement.uniqueSequential=0
		
		wEnquiryRequest.productNumber =CTSEnvironment.bvAccCtaCteNumber
		wEnquiryRequest.dateFormatId =CTSEnvironment.bvDateFormat
		wEnquiryRequest.productAlias = "1"
		wEnquiryRequest.currencyId = CTSEnvironment.bvAccCtaCteCurrencyId
		wEnquiryRequest.productId = CTSEnvironment.bvAccCtaCteType

		wSearchOption.initialDate = CTSEnvironment.bvInitialDate
		wSearchOption.finalDate = CTSEnvironment.bvFinalDate
		wSearchOption.criteria = 'S'
		
		wUser.name = CTSEnvironment.bvCompanyLogin
		wUser.serviceId = 11
		
		
		serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
		serviceRequestTO.addValue('inAccountStatement', wAccountStatement)
		serviceRequestTO.addValue('inSearchOption', wSearchOption)
		serviceRequestTO.addValue('inUser', wUser)

		ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

		//Valido si fue exitoso la ejecucion
		def message=''
		if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		}

		Assert.assertTrue(message, serviceResponseTO.success)
				
		//Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnAccountStatement').collect().size()==1)
		AccountStatement[] oResponseCheckingAccount= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
		for (var in oResponseCheckingAccount) {
			println "**** ------------------ ****"
			println " ***  Fecha ---> "+var.transactionDate
			println " ***  Descripcion---> "+var.description
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
