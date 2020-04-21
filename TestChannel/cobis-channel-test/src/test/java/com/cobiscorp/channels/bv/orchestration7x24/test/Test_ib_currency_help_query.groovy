package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSim
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanItem
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CommonData
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CommonGeneralParams;
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CurrencyDefinition;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil


class Test_ib_currency_help_query {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	
	/**
	 * Checks Query Test
	 */
	
	@Test
	void testgetCurrencyHelp(){
		println ' ****** Prueba Regresión testSimulationLoan ************* '
		def ServiceName = 'getSimulationLoanItems'
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetCurrenciesHelp');

			CommonGeneralParams commonGralParams = new CommonGeneralParams();
			commonGralParams.mode = 0
			
			serviceRequestTO.addValue('inCommonGeneralParams', commonGralParams);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message='';
			def codeError='';
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message;
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code;
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
				
			println ('------------ **** RESULT **** -----------')
			CommonData [] aCommonData = serviceResponseTO.data.get('returnCommonData').collect().toArray()
			println "******** Resulset *********"
	
			for (var in aCommonData) {
				
				println "*************RESULT NATURAL**************"
				println "code "+ var.code;
				println "description "+ var.description1;
				println "symbol "+ var.description2;
			 }
			
		   } catch (Exception e) {
		
				def msg=e.message;
				println "${ServiceName} Exception--> ${msg}"
				virtualBankingBase.closeSessionNatural(initSession);
				Assert.fail();
				}

		}
	

}
