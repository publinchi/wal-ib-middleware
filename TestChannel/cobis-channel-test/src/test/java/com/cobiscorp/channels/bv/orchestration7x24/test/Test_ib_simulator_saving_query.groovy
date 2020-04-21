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
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CDExpirationDateInfo
import cobiscorp.ecobis.internetbanking.webapp.products.dto.SavingSim
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_simulator_saving_query {

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
	void testgetSimulationSaving(){
		println ' ****** Prueba Regresión testSimulationSaving ************* '
		def ServiceName = 'getSimulationSaving'
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetSavings');
			
						
			//SimulationSaving simulationSaving = new SimulationSaving();
			User user = new User();
			
			user.entityType = 'C'
			
			serviceRequestTO.addValue('inUser', user);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message='';
			def codeError='';
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message;
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code;
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			
			def wRate = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tasa_interes");
			def wPeriod = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_periodo");
			def wAmount = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_amount");
			
			println "******** Output SimulationSaving Natural*********"
			println ('RESPUESTA: @o_tasa_interes----->' + wRate);
			println ('RESPUESTA: @o_periodo----->' + wPeriod);
			println ('RESPUESTA: @o_amount----->' + wAmount);
			
			println ('------------ **** RESULT **** -----------')
			SavingSim[] aSavingSim= serviceResponseTO.data.get('returnSavingSim').collect().toArray()
			println "******** Resulset *********"
			
			
			for (var in aSavingSim) {
				
				println "*************RESULT NATURAL**************"
				println "code "+ var.code;
				println "description "+ var.description;
				println "category "+ var.category;
			
			}

		} catch (Exception e) {
							def msg=e.message;
							println "${ServiceName} Exception--> ${msg}"
							virtualBankingBase.closeSessionNatural(initSession);
							Assert.fail();
						}
					
				  }		
	
	
}
