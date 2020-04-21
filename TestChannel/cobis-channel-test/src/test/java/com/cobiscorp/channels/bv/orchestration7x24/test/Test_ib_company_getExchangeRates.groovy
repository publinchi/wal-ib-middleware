package com.cobiscorp.channels.bv.orchestration7x24.test

//import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ExchangeRate
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance


import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;

class Test_ib_company_getExchangeRates {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
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
	/**
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testCompanyGetExchangeRates(){
		String ServiceName='testCompanyGetExchangeRates'
		
		try{
			//println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.GetExchangeRates')
			//DTO IN
			ExchangeRate wExchangeRate= new ExchangeRate();
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			   
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
	
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			//Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnExchangeRate').collect().size()>0)
			def wBuy = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion_com")
			def wSal = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion_ven")
			
				println("*******************************************")
				println("***** RESPUESTA CONSULTA DIVISA************")
				println("*******************************************")
				println("***** COTIZACION COMPRA : " + wBuy)
				println("\n")
				println("***** COTIZACION VENTA : " + wSal)
				println("*******************************************")
			
		}catch(Exception e){
		def msg=e.message
		println "${ServiceName} Exception--> ${msg}"
		virtualBankingBase.closeSessionCompany(initSession)
		}
	}	
}
