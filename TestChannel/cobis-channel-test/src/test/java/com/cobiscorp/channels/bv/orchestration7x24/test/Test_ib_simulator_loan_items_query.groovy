package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSim
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanItem
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil


class Test_ib_simulator_loan_items_query {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
   
		
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural()
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
	 * Checks Query Test
	 */
	
	@Test
	void testgetSimulationLoanItem(){
		println ' ****** Prueba Regresión testSimulationLoan ************* '
		def ServiceName = 'getSimulationLoanItems'
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetLoanItems');
			
			
			LoanSim loanSim = new LoanSim();
			
			loanSim.code = '1940';//'584';
	
			
			serviceRequestTO.addValue('inLoanSim', loanSim);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message='';
			def codeError='';
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message;
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code;
				
			}
			Assert.assertTrue(message, serviceResponseTO.success)
				
			println ('------------ **** RESULT **** -----------')
			LoanSim[] aSimulationLoan= serviceResponseTO.data.get('returnLoanSim').collect().toArray()
			LoanItem[] aSimulationLoanItems= serviceResponseTO.data.get('returnLoanItem').collect().toArray()
			println "******** Resulset *********"
	
			for (var in aSimulationLoan) {
				
				println "*************RESULT NATURAL**************"
				println "percentage "+ var.percentage;
							
			 }
			
			for (var in aSimulationLoanItems) {
				
				println "*************RESULT NATURAL**************"
				println "concepto "+ var.concept;
				println "descripcion "+ var.description;
				println "tipo_rubro "+ var.itemType;
				println "porcentaje "+ var.percentage;
				
			 }
			
		   } catch (Exception e) {
		
				def msg=e.message;
				println "${ServiceName} Exception--> ${msg}"
				virtualBankingBase.closeSessionNatural(initSession);
				Assert.fail();
				}

		}
	

}
