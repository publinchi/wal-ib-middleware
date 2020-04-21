package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.*
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 *
 *
 * @author eortega
 *
 */
class Test_ib_show_banks_by_country {

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
	 * Consulta de los bancos por pais
	 */
	@Test
	void testGetBanksByCountrys() {
		String ServiceName='GetBanksByCountry'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Swift.GetBanksByCountry')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			Country wCountry=new Country()

			wCountry.code=24
			wSearchOption.lastResult=""
			wSearchOption.mode=1

			serviceRequestTO.addValue('inCountry', wCountry)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)


			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//validar que la respuesta no este vacia
			Assert.assertTrue('Filas Vacias',serviceResponseTO.getData().get('returnBank').collect().size()>0)
			//Se imprime el primer valor
			Bank [] oBank= serviceResponseTO.data.get('returnBank').collect().toArray()
			println String.format('------------------>Primer BANCO consultado: '+ oBank[0].description)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}

