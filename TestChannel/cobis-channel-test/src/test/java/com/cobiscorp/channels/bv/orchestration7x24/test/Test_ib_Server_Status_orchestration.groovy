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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;

import com.cobiscorp.cobis.cts.ws.dtos.RequestTO;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;
class Test_ib_Server_Status_orchestration {
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
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testServerStatusGetServerStatus(){
		String ServiceName='testServerStatus'
		
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Server.GetServerStatus')
			//DTO IN
		    ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
		   	//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_en_linea")
			println "Test ---> " + wOref.toString();
			
			def wOref2 = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fecha_proceso")
			println "Test ---> " + wOref2.toString();
			
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
