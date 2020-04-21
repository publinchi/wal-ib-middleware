package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavings;
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavingsDetail;
import cobiscorp.ecobis.internetbanking.webapp.programmedsavings.service.service.impl.ProgrammedSavingsOperations;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.VirtualBankingBase;

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;
/***
 *
 * @author jlvidal
 *
 */

class Test_ib_group_ProgrammedSavings_MinimunAmount {	
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testMinimunAmaunt(){
		String ServiceName='MinimunAmaunt'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.GetMinimumAmount')

			
		    ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
		   	
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			
			println "**************************Test ---> Ejecutado sin param. de entrada**************************"
			
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_min")
			Assert.assertNotNull("No se obtuvo Monto Minimo",wOref)
			println ('RESPUESTA: @o_monto_min----->' + wOref)
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}
}
