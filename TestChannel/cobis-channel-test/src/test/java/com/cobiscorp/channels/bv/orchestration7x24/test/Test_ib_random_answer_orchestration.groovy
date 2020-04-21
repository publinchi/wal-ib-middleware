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
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SurveyAnswer

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 *
 * @author mvelez
 *
 */

//  RESPUESTAS ALEATORIAS

class Test_ib_random_answer_orchestration {
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

	void TestGetRandomAnswer () {
		//def ServiceName='TestGroupGetCCUltMovementsQuery'
		def ServiceName='TestGetRandomAnswer'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Survey.GetRandomAswersQuestions')

			//DTO IN
			SearchOption wSearchOption = new SearchOption()
			wSearchOption.numberOfResults = 20//CTSEnvironment.bvInitialDate
			wSearchOption.criteria = 'NEUTRAL'//CTSEnvironment.bvFinalDate

			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			//AccountStatement[] oResponse= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
			SurveyAnswer[] oResponse= serviceResponseTO.data.get('returnSurveyAnswer').collect().toArray()
			println "COD. PREGUNTA   COD. RESPUESTA   DESCRIPCION"
			println "-------------   --------------   ------------------------------------"
			for (var in oResponse) {
				println var.questionId+"               "+var.answerid+"                  "+var.description
			}
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}

}
