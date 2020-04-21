
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
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Currency
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseTotalBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseBalancesProducts

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Get Summary Balances
 *
 * @since 27/Junio/2014
 * @author Carlos Echeverría
 * @version 1.0.0
 * 
 *
 */
class Test_ib_consolidated_group_orchestration {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
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
	 * Método que ejecuta Servicio GetSummaryBalances
	 * 
	 */
	void GeSummaryBalances(int product)
	{
		if (initSession==0){
			println 'No se abrio sesión!!!!!'
			return
		}
		String ServiceName='GetSummaryBalances'
		try
		{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryBalances')
			
			Client wClient=new  Client()
			EnquiryRequest wEnquiry=new  EnquiryRequest ()
			SearchOption wSearchOption = new  SearchOption()
			Currency wCurrency = new Currency()
			
			
			wClient.entityId = CTSEnvironment.bvGroupEnteMis
			wEnquiry.productId =product
			wEnquiry.operation = "A"
			wEnquiry.dateFormatId = 101
			wEnquiry.userName=CTSEnvironment.bvGroupLogin
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
			//returnResponseSummaryBalance[] oreturnResponseSummaryBalance= serviceResponseTO.data.get('returnResponseSummaryBalance').collect().toArray()
			
			ResponseTotalBalance[] oreturnResponseTotalBalance= serviceResponseTO.data.get('returnResponseTotalBalance').collect().toArray()
			ResponseBalancesProducts[] oreturnResponseBalancesProducts= serviceResponseTO.data.get('returnResponseBalancesProducts').collect().toArray()

			for (var in oreturnResponseTotalBalance) {
				println var
			}
			
			for (var in oreturnResponseBalancesProducts) {
				println var
			}
				
			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
	}
	
	
	/**
	 * Consulta Consolidad de Saldos de Todos Los Productos - Grupo
	 */
	@Test
	void testGetSummaryBalancesAllProducts() {
		if (initSession==0){
			println 'No se abrio sesión!!!!!'
			return 
		}
		println ' ****** Prueba Regresión testGetSummaryBalancesAllProducts ************* '
		GeSummaryBalances(0)
				
		
	}
		
	/**
	 * Consulta Consolidad de Saldos de Cuentas Corrientes- Grupo
	 */
	@Test
	void testGetSummaryBalancesCheckingAccount() {
		println ' ****** Prueba Regresión testGetSummaryBalancesCheckingAccount ************* '
		GeSummaryBalances(3)
	}

	
	/**
	 * Consulta Consolidad de Saldos de Cuentas de Ahorros - Grupo
	 */
	@Test
	void testGetSummaryBalancesSavingAccount() {
		println ' ****** Prueba Regresión testGetSummaryBalancesSavingAccount ************* '
		GeSummaryBalances(4)
	}
	
	/**
	 * Consulta Consolidad de Saldos de Préstamos - Grupo
	 */
	@Test
	void testGetSummaryBalancesLoan() {
		println ' ****** Prueba Regresión testGetSummaryBalancesLoan ************* '
		GeSummaryBalances(7)
	}
	
	/**
	 * Consulta Consolidad de DPF - Grupo
	 */
	@Test
	void testGetSummaryBalancesTimeDeposits() {
		println ' ****** Prueba Regresión testGetSummaryBalancesTimeDeposits ************* '
		GeSummaryBalances(14)
	}

}
