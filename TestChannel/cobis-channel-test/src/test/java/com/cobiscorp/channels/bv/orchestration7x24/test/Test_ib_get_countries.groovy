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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Country
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * get catalog of Countries for Natural user
 * 
 * @author dguerra
 * @since Sep 1, 2014
 * @version 1.0.0
 */
public class Test_ib_get_countries {

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

	@Test
	void testGetCountriesCatalog() {
		def ServiceName='testGetCountriesCatalog'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Swift.GetCountries')

			//DTO IN

			SearchOption wSearchOption = new SearchOption()
			wSearchOption.mode = 0;
			wSearchOption.lastResult= "";

			serviceRequestTO.addValue('inSearchOption', wSearchOption)


			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> modo-> " + wSearchOption.mode
			println "Test ---> type-> " + wSearchOption.lastResult


			Country[] oResponseCountry= serviceResponseTO.data.get('returnCountry').collect().toArray()

			for (var in oResponseCountry) {
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Codigo pais ---> "+var.code
				println " ***  Nombre Pais ---> "+var.name
				println " ***  Nacionalidad ---> "+var.nationality
				println " ***  Codigo Continente ---> "+var.continentCode
				println " ***  Continente ---> "+var.continent

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}



}
