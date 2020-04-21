package com.cobiscorp.channels.bv.orchestration7x24.test

import junit.framework.Assert

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDeposit
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetailSchedule

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

import java.util.Calendar;

class Test_ib_simulation_opening_cd {
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
		virtualBankingBase.closeConnections();
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural();
	}
	
	/**
	 * Metodo que ejecuta el servicio Consulta de Movimientos
	 */
	@Test
	void testgetGetRealDays(){
		println ' ****** Prueba Regresión testgetRealDays ************* '
		def ServiceName = 'getGetRealDays'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteCDSimulation');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			CertificateDeposit wcertificateDeposit=new CertificateDeposit();
			
			wcertificateDeposit.nemonic=""
			wcertificateDeposit.amount=1500
			wcertificateDeposit.term=4
			wcertificateDeposit.rate=1;
			wcertificateDeposit.money =0;
			wcertificateDeposit.category="hh";
			Calendar fecha =new Date("10/11/2015");
			wcertificateDeposit.date =fecha
			wcertificateDeposit.entityId=13036
			wcertificateDeposit.payDay="4"
			
			serviceRequestTO.addValue('inCertificateDeposit',wcertificateDeposit);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			PaymentDetailSchedule[] oPaymentDetailSchedule= serviceResponseTO.data.get('returnPaymentDetailSchedule').collect().toArray();
			
			println "******** Resulset RealDays*********"
			
			for (var in oPaymentDetailSchedule) {
				
			println "******** RealDays*********"
			//*******************************************************
			println "Real Days: " + var.realDays
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
