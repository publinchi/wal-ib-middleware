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



class Test_ib_simulator_create_loan_query {

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
	void testCreateSimulationLoan(){
		println ' ****** Prueba Regresión testSimulationLoan ************* '
		def ServiceName = 'getSimulationCreateLoan'
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.CreateLoan');
			
			LoanSim loanSim = new LoanSim();
			
			loanSim.amount = 1000;
			loanSim.sector = 'COMR';
			loanSim.operationType = 'COMEPRE';
			loanSim.currencyId = 0;
			loanSim.initialDate = 'Aug 30 2012';
				
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
			println "******** Resulset *********" 
	
			for (var in aSimulationLoan) {
				
				println "*************RESULT NATURAL**************"
				println "codigo: "+ var.code;
							
			 }
								
		   } catch (Exception e) {
									def msg=e.message;
									println "${ServiceName} Exception--> ${msg}"
									virtualBankingBase.closeSessionNatural(initSession);
									Assert.fail();
								 }
					 }
		}
