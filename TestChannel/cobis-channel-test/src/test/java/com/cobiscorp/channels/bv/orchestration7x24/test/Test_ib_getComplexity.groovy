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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Parameter;
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.LevelComplexity;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product;




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
class Test_ib_getComplexity {



	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	 

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
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
	 * Método que ejecuta Servicio ValidateNewLogin
	 *
	 */
	
	void  GetParamValidateComplexity(String pSession,String pLogin)
	{
		
		try
		{
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Parameter.GetParameterByName')
										   
			
			
			Parameter wParameter = new Parameter()
			wParameter.parameterName= "VFCLA"
			Product wProduct = new Product()
			wProduct.productAbbreviation="BVI"
			
			serviceRequestTO.addValue('inParameter', wParameter)
			serviceRequestTO.addValue('inProduct', wProduct)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			Parameter[] oResponseParameter= serviceResponseTO.data.get('returnParameter').collect().toArray()
			
			for (var in oResponseParameter) {
				println "******** RESPUESTA *********"
				Assert.assertNotNull("Parámetro VFCLA no está definido", var.varcharData)
				println "Valida Fortaleza de Clave "+ var.varcharData
			}

			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetParamValidateComplexity', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
		
	}
	
	void  GetRangesOfComplexity(String pSession,String pLogin)
	{
		try
		{
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Complexity.GetRangeOfComplexity')
										   
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			LevelComplexity[] oResponseLevelComplexity= serviceResponseTO.data.get('returnLevelComplexity').collect().toArray()
			
			println "******** RESPUESTA *********"
			for (var in oResponseLevelComplexity) {
				Assert.assertNotNull("Type is null", var.type)
				Assert.assertNotNull("Initial Range is null", var.initalRange)
				Assert.assertNotNull("Final Range is null", var.finalRange)
				Assert.assertNotNull("Required is null", var.required)
				
				println "Tipo "+ var.type+" Rangos: "+var.initalRange.toString()+" - "+var.finalRange.toString()+" Requerido: "+var.required
				
			}

			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetRangesOfComplexity', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}

		
	}
	
	void  GetComplexityDefault(String pSession,String pLogin)
	{
		try
		{
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Parameter.GetParameterByName')
										   
			
			
			Parameter wParameter = new Parameter()
			wParameter.parameterName= "FCLA"
			Product wProduct = new Product()
			wProduct.productAbbreviation="BVI"
			
			serviceRequestTO.addValue('inParameter', wParameter)
			serviceRequestTO.addValue('inProduct', wProduct)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			Parameter[] oResponseParameter= serviceResponseTO.data.get('returnParameter').collect().toArray()
			
			for (var in oResponseParameter) {
				println "******** RESPUESTA *********"
				Assert.assertNotNull("Parámetro FCLA no está definido", var.varcharData)
				println "Fortaleza de Clave "+ var.varcharData
			}

			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetComplexityDefault', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}

		
	}

	
	/**
	 * Obtiene Complejidad de Clave
	 */
	@Test
	void testGetComplexity() {
		
		println ' ****** Prueba Regresión testGetComplexity ************* '
		GetParamValidateComplexity(initSession, CTSEnvironment.bvLogin)
		GetRangesOfComplexity(initSession, CTSEnvironment.bvLogin)
		GetComplexityDefault(initSession, CTSEnvironment.bvLogin)
	}
}

