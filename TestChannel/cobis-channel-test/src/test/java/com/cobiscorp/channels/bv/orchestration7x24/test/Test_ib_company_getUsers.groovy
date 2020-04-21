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
import cobiscorp.ecobis.internetbanking.webapp.hsbc.dto.CustomLogin




import com.cobiscorp.cobis.plugin.activator.HttpServiceActivator.InternalResource;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Get Users
 *
 * @since 7/Agosto/2014
 * @author Carlos Echeverría
 * @version 1.0.0
 *
 *
 */
class Test_ib_company_getUsers {



	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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
	 * Método que ejecuta Servicio ValidateNewLogin
	 *
	 */
	
	void GetUsers(String pSession,  String pLogin)
	{
		
		try
		{
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Security.Service.Security.ValidateNewLogin')
										   
			
			
			CustomLogin wcustomLogin = new CustomLogin()
			wcustomLogin.userName= pLogin
			
			serviceRequestTO.addValue('inCustomLogin', wcustomLogin)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			println "********  Logines *************"
			for(int i =1;i<=5;i++)
			{
				println "Login opción "+i.toString()+": "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_login_op"+i.toString()).toString()
			}

			println "Login opción new : "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_login_new").toString()
			println "Login opción old : "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_login_old").toString()

			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetUsers', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
		
	}

	
	/**
	 * Consulta de Usuarios
	 */
	@Test
	void testGetUsers() {
		
		println ' ****** Prueba Regresión testGetUsers ************* '
		GetUsers(initSession, CTSEnvironment.bvLoginEmpresa)
	}
}

