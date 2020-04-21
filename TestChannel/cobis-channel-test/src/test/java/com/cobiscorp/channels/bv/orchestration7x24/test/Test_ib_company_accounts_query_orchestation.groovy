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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.EntityServiceProduct
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/***
 * orchestration-core-ib-query-accounts
 *
 * @author schancay
 *
 */
class Test_ib_company_accounts_query_orchestation {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompanyA();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompanyA(initSession)
	}

	/**
	 * Consulta de Detalles de Cuenta de un cliente
	 */
	@Test
	void testGetInformationAccountByClient() {
		String ServiceName='testGetInformationAccountByClient'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetAccountsDet')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.operation=null
			wEnquiryRequest.forInternetBankingNet=null
			wEnquiryRequest.userName=CTSEnvironment.bvCompanyLoginA
			wEnquiryRequest.productNumber =CTSEnvironment.bvCompanyAccCtaAhoNumber
			wEnquiryRequest.productId=CTSEnvironment.bvCompanyAccCtaAhoType

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.numberOfResults=0
			wSearchOption.lastResult=0

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnEntityServiceProduct').collect().size()>0)
			EntityServiceProduct[] oEntityServiceProduct= serviceResponseTO.data.get('returnEntityServiceProduct').collect().toArray()

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnProduct').collect().size()>0)
			Product[] oProduct= serviceResponseTO.data.get('returnProduct').collect().toArray()

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}