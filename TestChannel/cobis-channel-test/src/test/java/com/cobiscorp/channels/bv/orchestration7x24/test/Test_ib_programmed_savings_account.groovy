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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavingsAccount
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author gcondo
 * @since Sep 30, 2014
 * @version 1.0.0
 */

class Test_ib_programmed_savings_account {
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
	@Test
	void testProgrammedSavingsAccount(){
		String ServiceName='ProgrammedSavingsAccount'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.ProgrammedSavingsAccount')
			
			User wUser = new User()
			wUser.customerId = 277
			
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.mode = 1
			
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inSearchOption', SearchOption)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println ('------------********-----------')
			ProgrammedSavingsAccount[] oResponseParameter= serviceResponseTO.data.get('returnProgrammedSavingsAccount').collect().toArray()
			for (var in oResponseParameter) {
				Assert.assertNotNull("Account is null ",var.account)
				Assert.assertNotNull("CurrencyId is null ",var.currencyId)
				Assert.assertNotNull("ClientName is null ",var.clientName)
				Assert.assertNotNull("ProductBalance is null ",var.productBalance)
				println ('------------ RESULTADO -----------')
				println " ***  Account           ---> "+var.account
				println " ***  CurrencyId        ---> "+var.currencyId
				println " ***  ClientName        ---> "+var.clientName
				println " ***  ProductBalance    ---> "+var.productBalance				
			}
			
			
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
		
	}

}
