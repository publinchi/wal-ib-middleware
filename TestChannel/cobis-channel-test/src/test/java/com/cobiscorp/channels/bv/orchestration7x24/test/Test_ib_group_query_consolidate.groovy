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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryBalance
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * 
 * @author schancay
 * @since Jul 2, 2014
 * @version 1.0.0
 */
class Test_ib_group_query_consolidate {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession)
	}

	/**
	 * Get all products by group user
	 */
	@Test
	void testGetConsolidateView() {
		String ServiceName='GetSummaryBalances'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances')

			//DTO IN
			Client wclient = new Client()
			wclient.entityId=CTSEnvironment.bvGroupEnteMis

			EnquiryRequest wEnquiryRequest= new EnquiryRequest()
			wEnquiryRequest.productId=0
			wEnquiryRequest.operation='A'
			wEnquiryRequest.userName=CTSEnvironment.bvGroupLogin

			cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency wCurrency= new cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency()
			wCurrency.id=-1

			SearchOption wSearchOption= new SearchOption()
			wSearchOption.numberOfResults = 0

			serviceRequestTO.addValue("inClient", wclient)
			serviceRequestTO.addValue("inEnquiryRequest", wEnquiryRequest)
			serviceRequestTO.addValue("inCurrency", wCurrency)
			serviceRequestTO.addValue("inSearchOption", wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			ResponseSummaryBalance wResponseSummaryBalance=new ResponseSummaryBalance()
			wResponseSummaryBalance.entityServiceProduct=serviceResponseTO.getData().get('returnEntityServiceProduct')
			wResponseSummaryBalance.responseBalanceProducts=serviceResponseTO.getData().get('returnResponseBalancesProducts')
			wResponseSummaryBalance.responseTotalBalance=serviceResponseTO.getData().get('returnResponseTotalBalance')

			def secuential=serviceResponseTO.getData().get('com.cobiscorp.cobis.cts.service.response.output').getAt('@o_ssn_branch')

			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.entityServiceProduct.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseTotalBalance.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseBalanceProducts.collect().size()>0)
			Assert.assertNotNull('No se ha devuelto secuencial',secuential)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
			Assert.fail()
		}
	}
	/**
	 * Get all products by group user
	 */
	@Test
	void testGetConsolidateViewByCurrencyCrc() {
		String ServiceName='GetSummaryBalances'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances')

			//DTO IN
			Client wclient = new Client()
			wclient.entityId=CTSEnvironment.bvGroupEnteMis

			EnquiryRequest wEnquiryRequest= new EnquiryRequest()
			wEnquiryRequest.productId=0
			wEnquiryRequest.operation='A'
			wEnquiryRequest.userName=CTSEnvironment.bvGroupLogin

			cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency wCurrency= new cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency()
			wCurrency.id=0

			SearchOption wSearchOption= new SearchOption()
			wSearchOption.numberOfResults = 0

			serviceRequestTO.addValue("inClient", wclient)
			serviceRequestTO.addValue("inEnquiryRequest", wEnquiryRequest)
			serviceRequestTO.addValue("inCurrency", wCurrency)
			serviceRequestTO.addValue("inSearchOption", wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			ResponseSummaryBalance wResponseSummaryBalance=new ResponseSummaryBalance()
			wResponseSummaryBalance.entityServiceProduct=serviceResponseTO.getData().get('returnEntityServiceProduct')
			wResponseSummaryBalance.responseBalanceProducts=serviceResponseTO.getData().get('returnResponseBalancesProducts')
			wResponseSummaryBalance.responseTotalBalance=serviceResponseTO.getData().get('returnResponseTotalBalance')

			def secuential=serviceResponseTO.getData().get('com.cobiscorp.cobis.cts.service.response.output').getAt('@o_ssn_branch')

			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.entityServiceProduct.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseTotalBalance.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseBalanceProducts.collect().size()>0)
			Assert.assertNotNull('No se ha devuelto secuencial',secuential)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * @autor dguerra
	 * Get all products filtered by Currency  by group user
	 */
	@Test
	void testGetConsolidateViewUSD() {
		String ServiceName='GetSummaryBalances'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances')

			//DTO IN
			Client wclient = new Client()
			EnquiryRequest wEnquiryRequest= new EnquiryRequest()
			cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency wCurrency= new cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency()
			SearchOption wSearchOption= new SearchOption()

			wSearchOption.numberOfResults = 0

			wEnquiryRequest.userName=CTSEnvironment.bvGroupLogin
			wEnquiryRequest.operation= 'A'
			wEnquiryRequest.dateFormatId=103

			wCurrency.id=17

			wclient.entityId=CTSEnvironment.bvGroupEnteMis

			serviceRequestTO.addValue("inClient", wclient)
			serviceRequestTO.addValue("inEnquiryRequest", wEnquiryRequest)
			serviceRequestTO.addValue("inCurrency", wCurrency)
			serviceRequestTO.addValue("inSearchOption", wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			ResponseSummaryBalance wResponseSummaryBalance=new ResponseSummaryBalance()
			wResponseSummaryBalance.entityServiceProduct=serviceResponseTO.getData().get('returnEntityServiceProduct')
			wResponseSummaryBalance.responseBalanceProducts=serviceResponseTO.getData().get('returnResponseBalancesProducts')
			wResponseSummaryBalance.responseTotalBalance=serviceResponseTO.getData().get('returnResponseTotalBalance')

			def secuential=serviceResponseTO.getData().get('com.cobiscorp.cobis.cts.service.response.output').getAt('@o_ssn_branch')

			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.entityServiceProduct.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseTotalBalance.collect().size()>0)
			Assert.assertTrue('Filas Vacias',wResponseSummaryBalance.responseBalanceProducts.collect().size()>0)
			Assert.assertNotNull('No se ha devuelto secuencial',secuential)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
			Assert.fail()
		}
	}
}
