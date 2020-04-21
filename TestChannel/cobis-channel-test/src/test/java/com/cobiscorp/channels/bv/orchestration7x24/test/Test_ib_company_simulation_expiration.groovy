package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.Assert

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
//import cobiscorp.ecobis.internetbanking.webapp.enquiries.service.service.impl.CertificateDeposit
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CDExpirationDateInfo
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDeposit


import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/**
 * @author areinoso
 *
 */

class Test_ib_company_simulation_expiration {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
   
	
	
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany()
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
	 * Checks Query Test
	 */
	
	@Test
	void testgetSimulationExpiration(){
		println ' ****** Prueba Regresión testSimulationExpiration Company************* '
		def ServiceName = 'getSimulationExpiration'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDExpirationDate');

			CertificateDeposit wcertificateDeposit=new CertificateDeposit();
			TransactionRequest trequest = new TransactionRequest();
			
			trequest.dateFormatId = 103;
			
			Calendar wProcessDate = new GregorianCalendar(2014,10,01,00,00,00);
			wcertificateDeposit.processDate=wProcessDate;
			wcertificateDeposit.login = CTSEnvironment.bvLogin;//@i_login
			//wcertificateDeposit.processDate = '01/01/2013';
			wcertificateDeposit.term=180;
			//wcertificateDeposit.nemonic="ff"
			//----
			wcertificateDeposit.useCalendarDays='N';
		
			serviceRequestTO.addValue('inCertificateDeposit',wcertificateDeposit);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message='';
			def codeError='';
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message;
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code;
				
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			def wDaysLabor = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_num_dias_labor");
			
			println "******** Output ManagerCheckRequest Natural*********"
			println ('RESPUESTA: @o_num_dias_labor----->' + wDaysLabor);
			
			println ('------------ **** RESULT **** -----------')
				CDExpirationDateInfo[] aCDExpirationDateInfo= serviceResponseTO.data.get('returnCDExpirationDateInfo').collect().toArray()
			println "******** Resulset *********"
			
			for (var in aCDExpirationDateInfo) {
	
				println "*************RESULT NATURAL**************"
				println "expirationDateHold "+ var.expirationDateHold;

			}
			
			CDExpirationDateInfo[] aCDExpirationDateInfo2= serviceResponseTO.data.get('returnCDExpirationDateInfo2').collect().toArray()
			for (var in aCDExpirationDateInfo2) {	
				println "*************RESULT 2 NATURAL**************"
				println "ExpirationDateHold "+ var.expirationDateHold;
				println "Eesult"+ var.result;
				println "ProcessDate "+ var.processDate;
				println "ExpirationDate" + var.expirationDate;
				println "ProcessDateHold "+ var.processDateHold;
				println "ExpirationDateHold "+ var.expirationDateHold;
				println "TermHolm" + var.termHold;
			}
			
			
	} catch (Exception e) {
				def msg=e.message;
				println "${ServiceName} Exception--> ${msg}"
				virtualBankingBase.closeSessionCompany(initSession);
				Assert.fail();
			}
		
	  }
	
}
