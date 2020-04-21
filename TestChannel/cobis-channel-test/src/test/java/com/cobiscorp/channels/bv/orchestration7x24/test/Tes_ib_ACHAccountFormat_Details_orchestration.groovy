package com.cobiscorp.channels.bv.orchestration7x24.test

import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Bank
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.transfers.service.service.impl.Transfer
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;

class Tes_ib_ACHAccountFormat_Details_orchestration {
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
	void testGetACHAccountFormat(){
		String ServiceName='testGetACHAccountFormat'
		try{
			
		println String.format('Test [%s]',ServiceName)
		// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Format.GetACHAccountFormat')
				Bank wbank= new Bank()
				wbank.id=3
				serviceRequestTO.addValue('inBank', wbank)
				ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
				String message=''
				def codeError=''
				if (serviceResponseTO.messages.toList().size()>0){
					
					print("LEEGADA 3")
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				}
				
				Assert.assertTrue(message, serviceResponseTO.success)
				Bank[] oResponseParameter= serviceResponseTO.data.get('returnBank').collect().toArray()
				
				for (var in oResponseParameter) {
					print("ingresa al for")
					
					Assert.assertNotNull("id is null ",var.id)
					Assert.assertNotNull("description is null ",var.description)
					Assert.assertNotNull("subsidiary is null ",var.subsidiary)
					Assert.assertNotNull("status is null ",var.status)
					Assert.assertNotNull("accountTypeId is null ",var.accountTypeId)
					Assert.assertNotNull("accountType is null ",var.accountType)
					Assert.assertNotNull("lengthAccount is null ",var.lengthAccount)
					println ('------------ RESULTADO -----------')
					println " ***  id           ---> "+var.id
					println " ***  description           ---> "+var.description
					println " ***  subsidiary           ---> "+var.subsidiary
					println " ***  status           ---> "+var.status
					println " ***  accountTypeId           ---> "+var.accountTypeId
					println " ***  accountType           ---> "+var.accountType
					println " ***  lengthAccount           ---> "+var.lengthAccount
					
										}
		}
		
		catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		
				
				
	}
		

	}
	
}
