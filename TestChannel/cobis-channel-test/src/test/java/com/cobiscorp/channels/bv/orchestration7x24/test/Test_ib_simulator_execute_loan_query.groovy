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
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_simulator_execute_loan_query {
	
	
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
	void testExecutionSimulationLoan(){
		println ' ****** Prueba Regresión testSimulationLoan ************* '
		def ServiceName = 'getSimulationExecuteLoan'
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteLoanSimulation');
			
			
			LoanSim loanSim = new LoanSim();
			
			loanSim.operation = 'T';
			loanSim.code = '1940';
			loanSim.payment = 3000;
			loanSim.term =90;
	
			
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
			LoanSim[] aSimulatorLoan= serviceResponseTO.data.get('returnLoanSim').collect().toArray()
			println "******** Resulset *********"
	
			for (var in aSimulatorLoan) {
				
				println "*************RESULT NATURAL**************"
				println "endDate "+ var.endDate;
				println "amount "+ var.amount;
				println "term " + var.term;
				println "operationType" + var.operationType;
				println "operation" + var.operation;
				println "sector" + var.sector;
				
										
			 }
							
		   } catch (Exception e) {		
									def msg=e.message;
									println "${ServiceName} Exception--> ${msg}"
									virtualBankingBase.closeSessionNatural(initSession);
									Assert.fail();
								 }
					 }
}
