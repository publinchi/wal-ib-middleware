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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.City
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

public class Test_ib_group_get_cities_by_country {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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

	@Test
	void testGetCitiesByCountryCompany() {
		def ServiceName='testGetCitiesByCountryCompany'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Swift.GetCities')

			//DTO IN

			SearchOption wSearchOption = new SearchOption()
			wSearchOption.criteria = 'AMC';
			wSearchOption.finalCheck= 41;

			serviceRequestTO.addValue('inSearchOption', wSearchOption)


			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> modo-> " + wSearchOption.criteria
			println "Test ---> type-> " + wSearchOption.finalCheck


			City[] oResponseCity= serviceResponseTO.data.get('returnCity').collect().toArray()

			for (var in oResponseCity) {
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Codigo Ciudad ---> "+var.code
				println " ***  Nombre Ciudad ---> "+var.name
				println " ***  Codigo Pais ---> "+var.countryCode
				println " ***  Nombre Pais ---> "+var.country
				println " ***  Codigo Continente ---> "+var.continentCode
				println " ***  Nombre Continente ---> "+var.continent

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}


}
