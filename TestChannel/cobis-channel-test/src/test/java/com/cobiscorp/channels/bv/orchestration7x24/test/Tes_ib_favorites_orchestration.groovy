package com.cobiscorp.channels.bv.orchestration7x24.test;

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.commons.admin.dto.Favorites
import cobiscorp.ecobis.internetbanking.commons.admin.dto.Menu

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Insert, Select, Delete of Menu in the Favorites option (Natural)
 * @author dguerra
 * @since Jul 31, 2014
 * @version 1.0.0
 */

public class Tes_ib_favorites_orchestration {


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
	 * Insert
	 */

	@Test
	void testAddFavoritesNatural() {
		def ServiceName= 'testAddFavoritesNatural'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('AddFavorites')


			//DTO IN
			Menu  wMenu  =  new Menu()
			wMenu.login = CTSEnvironment.bvLogin
			wMenu.operation = 'I'
			wMenu.trn = 1800032

			serviceRequestTO.addValue('inMenu', wMenu)

			println("Consulta antes de ejecutar la insercion del Menu")
			testGetFavoritesNatural()

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)


			//Valido si fue exitoso la ejecucion
			def message=''

			if(serviceResponseTO.success){
				println "ExecuteService---> OK"
				Assert.assertTrue(message, serviceResponseTO.success)
				println "Test --->Ejecutado insercion menu favoritos con param. de entrada:"
				println "Test --->login---> " + wMenu.login
				println "Test --->Id Menu---> " + wMenu.trn

				println("Consulta despues de ejecutar la insercion del Menu")
				testGetFavoritesNatural()

			}
			else{
				println "ExecuteService---> ERROR"

				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}

	/**
	 * Select
	 */
	@Test
	void testGetFavoritesNatural() {
		def ServiceName= 'testGetFavoritesNatural'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('GetFavorites')


			//DTO IN
			Menu  wMenu  =  new Menu()
			wMenu.login = CTSEnvironment.bvLogin
			wMenu.operation = 'S'

			serviceRequestTO.addValue('inMenu', wMenu)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)


			//Valido si fue exitoso la ejecucion
			def message=''

			if(serviceResponseTO.success){
				println "ExecuteService---> OK"
				Assert.assertTrue(message, serviceResponseTO.success)
				println "Test --->Consulta Menús atados a favoritos para login: "+ wMenu.login

				Favorites[] oFavorites= serviceResponseTO.data.get('returnFavorites').collect().toArray()
				for (var in oFavorites)
				{
					println " ***  id. Menu ---> "+ var.trn
				}

			}
			else{
				println "ExecuteService---> ERROR"

				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}

	/**
	 * delete
	 */
	@Test
	void testDeleteFavoritesNatural() {
		def ServiceName= 'testDeleteFavoritesNatural'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('DeleteFavorites')


			//DTO IN
			Menu  wMenu  =  new Menu()
			wMenu.login = CTSEnvironment.bvLogin
			wMenu.operation = 'D'
			wMenu.trn = 1800032

			serviceRequestTO.addValue('inMenu', wMenu)

			println("Consulta antes de ejecutar la eliminación del Menu")
			testGetFavoritesNatural()

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)


			//Valido si fue exitoso la ejecucion
			def message=''

			if(serviceResponseTO.success){
				println "ExecuteService---> OK"
				Assert.assertTrue(message, serviceResponseTO.success)
				println "Test --->Ejecutado delete menu  de favoritos con param. de entrada:"
				println "Test --->login---> " + wMenu.login
				println "Test --->login---> " + wMenu.trn

				println("Consulta despues de ejecutar la eliminación del Menu")
				testGetFavoritesNatural()

			}
			else{
				println "ExecuteService---> ERROR"

				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}


}
