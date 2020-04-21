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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.LaborDay
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.LaborDayRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jmoreta
 *
 */
class Test_ib_checkbook_nextlabor_day_query {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	/**
	 * 
	 */
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
		virtualBankingBase.closeSessionNatural(initSession);
	}

	/**
	 * Metodo que ejecuta el servicio siguiente día feriado
	 */
	@Test
	void testgetNexLaborDay() {
		println ' ****** Prueba Regresión testgetNexLaborDay Persona Natural************* '
		def ServiceName= 'getNextLaborDay'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetNextLaborDay');

			LaborDayRequest wLaborDayRequest = new LaborDayRequest();
			wLaborDayRequest.date = CTSEnvironment.bvProcessDate;//@i_fecha
			wLaborDayRequest.numberOfDays = 1;//@i_dias
			wLaborDayRequest.officeId=CTSEnvironment.bvCheNextDayOfficeId;//@i_oficina
			wLaborDayRequest.commercial='N';//@i_comercial			
			
			serviceRequestTO.addValue('inLaborDayRequest', wLaborDayRequest);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success);
			LaborDay[] oLaborDayResponse = serviceResponseTO.data.get('returnLaborDay').collect().toArray();
			
			println "******** RESULSET *********";
			for (var in oLaborDayResponse) {
				Assert.assertNotNull("Date Number is null ",var.date);
				Assert.assertNotNull("Day Date is null ",var.day);
				Assert.assertNotNull("Month Date is null ",var.month);
				Assert.assertNotNull("Year Balance is null ",var.year);
			
			//*******************************************************
				println "******** RESULSET getNextLaborDay *********";
				println "DATE: "+ var.date;
				println "DAY: "+ var.day;
				println "MONTH: "+ var.month;
				println "YEAR: "+ var.year;
			}
			
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
}
