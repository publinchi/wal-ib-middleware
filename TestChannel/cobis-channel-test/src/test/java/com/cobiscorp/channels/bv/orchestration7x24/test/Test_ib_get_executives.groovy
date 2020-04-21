package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Assert
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Executive
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_get_executives {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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
	void testGetExecutives() {
		def ServiceName='testGetExecutives'
		
		try{
			println "Test ---> ${ServiceName}"
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.NotificationExecutive.GetExecutives')
			
			//DTO
			Client wClient = new Client();
			wClient.entityId= 13036;
			
			serviceRequestTO.addValue('inClient', wClient)			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
						
			println("Tamaño respuesta>>" + serviceResponseTO.getData().get('returnExecutive').collect().size())
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnExecutive').collect().size()>0)
			Executive[] oExecutive= serviceResponseTO.data.get('returnExecutive').collect().toArray()
			for (executive in oExecutive) {
				println( "Oficial:"+executive.name+" - Email: "+executive.email )
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
	

}
