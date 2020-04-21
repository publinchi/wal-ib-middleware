
package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After
import org.junit.AfterClass
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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryProducts


import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Get Summary Products
 *
 * @since 7/Julio/2014
 * @author Carlos Echeverría
 * @version 1.0.0
 * 
 *
 */
class Test_ib_consolidated_detailed_orchestration {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	 

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
	}


	/**
	 * Método que ejecuta Servicio GetSummaryBalances
	 * 
	 */
	int  GeSummaryBalances(String pSession, Integer pEntity, String pLogin, Integer pProduct,Integer pEntityMis)
	{
		Integer wOssnBranch = 0
		String ServiceName='GetSummaryBalances'
		try
		{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances')
			
			Client wClient=new  Client()
			EnquiryRequest wEnquiry=new  EnquiryRequest ()
			SearchOption wSearchOption = new  SearchOption()
			Currency wCurrency = new Currency()
			
			
			wClient.entityId = pEntityMis
			wEnquiry.productId =pProduct
			wEnquiry.operation = "A"
			wEnquiry.dateFormatId = 101
			wEnquiry.userName=pLogin
			wSearchOption.numberOfResults = 0
			wCurrency.id =0
			
			serviceRequestTO.addValue('inClient', wClient)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiry)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inCurrency', wCurrency)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias returnResponseTotalBalance', serviceResponseTO.getData().get('returnResponseTotalBalance').collect().size()>0)
			Assert.assertTrue('Filas Vacias returnResponseBalancesProducts', serviceResponseTO.getData().get('returnResponseBalancesProducts').collect().size()>0)

			wOssnBranch = Integer.valueOf(serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch"))
					
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
		return wOssnBranch;
	}
	
	
	void GetSummaryProducts(String pSession, Integer pEntity, String pLogin, Integer pProduct, Integer pEntityMis)
	{
		Integer wSnnBranch =GeSummaryBalances(pSession, pEntity, pLogin, pProduct,pEntityMis)
		try
		{
			println ' SsnBranch ${wSnnBranch} -'+ wSnnBranch
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryProducts')
			                               
			
			
			EnquiryRequest wEnquiry=new  EnquiryRequest ()
			SearchOption wSearchOption = new  SearchOption()
			
			wSearchOption.sequential = wSnnBranch
			wSearchOption.numberOfResults=20
			wSearchOption.lastResult = 0
			
			
			
			wEnquiry.productId =pProduct
			wEnquiry.operation = "N"
			wEnquiry.dateFormatId = 101
			wEnquiry.userName=pLogin
			wSearchOption.numberOfResults = 20
			
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiry)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			
			
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias returnResponseSummaryProducts', serviceResponseTO.getData().get('returnResponseSummaryProducts').collect().size()>0)
			ResponseSummaryProducts[] oreturnResponseSummaryProducts= serviceResponseTO.data.get('returnResponseSummaryProducts').collect().toArray()
			
			for (var in oreturnResponseSummaryProducts) {
				println "Product Number: " +var.productNumber+ " AccountName: "+var.accountName+" AvailableBalance: "+var.availableBalance.toString()
			}
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetSummaryProducts', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
		
	}

	
	/**
	 * Consulta detallada de Corrientes y Ahorros - Persona
	 */
	@Test
	void testGetSummaryProductsAllAccountsPerson() {
		
		println ' ****** Prueba Regresión testGetSummaryProductsAllAccountsPerson ************* '
		GetSummaryProducts(initSession, CTSEnvironment.bvEnte, CTSEnvironment.bvLogin, 0,CTSEnvironment.bvEnteMis)
	}
	/**
	 * Consulta detallada de Corrientes - Persona
	 */
	@Test
	void testGetSummaryProductsCheckingAccountPerson() {
		
		println ' ****** Prueba Regresión testGetSummaryProductsCheckingAccountPerson ************* '
		GetSummaryProducts(initSession, CTSEnvironment.bvEnte, CTSEnvironment.bvLogin, 3,CTSEnvironment.bvEnteMis)
	}

	/**
	 * Consulta detallada de Ahorros - Persona
	 */
	@Test
	void testGetSummaryProductsSavingAccountPerson() {
		
		println ' ****** Prueba Regresión testGetSummaryProductsSavingAccountPerson ************* '
		GetSummaryProducts(initSession, CTSEnvironment.bvEnte, CTSEnvironment.bvLogin, 4,CTSEnvironment.bvEnteMis)
	}

	
	/**
	 * Consulta detallada de Prestamos - Persona
	 */
	@Test
	void testGetSummaryProductsLoanPerson() {
		
		println ' ****** Prueba Regresión testGetSummaryProductsLoanPerson ************* '
		GetSummaryProducts(initSession, CTSEnvironment.bvEnte, CTSEnvironment.bvLogin, 7,CTSEnvironment.bvEnteMis)
	}

	/**
	 * Consulta detallada de DPF - Persona
	 */
	@Test
	void testGetSummaryProductsTimeDepositPerson() {
		
		println ' ****** Prueba Regresión testGetSummaryProductsTimeDepositPerson ************* '
		GetSummaryProducts(initSession, CTSEnvironment.bvEnte, CTSEnvironment.bvLogin, 14,CTSEnvironment.bvEnteMis)
	}
}
