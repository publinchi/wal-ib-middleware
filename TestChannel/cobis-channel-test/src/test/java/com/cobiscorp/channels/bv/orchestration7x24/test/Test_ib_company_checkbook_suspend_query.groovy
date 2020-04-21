package com.cobiscorp.channels.bv.orchestration7x24.test
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Statement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.NoPaycheckOrder;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.NoPaycheckOrderResponse;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_company_checkbook_suspend_query {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections();
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany();
	}
	
	/**
	 * Metodo que ejecuta el servicio Consulta de Movimientos
	 */
	@Test
	void testSuspendChecks(){
		println ' ****** Prueba Regresión testSuspendChecks ************* '
		def ServiceName = 'SuspendChecks'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Checkbook.SuspendChecks');
			
			NoPaycheckOrder noPaycheckOrder = new NoPaycheckOrder();
			
			noPaycheckOrder.currencyId  = 0;
			noPaycheckOrder.productId   = 3;
			noPaycheckOrder.userName    = 'testCtsEmp';
			noPaycheckOrder.account	    = '10410108275405315';
			noPaycheckOrder.initalCheck = 24;			
			noPaycheckOrder.numberOfChecks = 1;
			noPaycheckOrder.reason 		= '15';
			noPaycheckOrder.typeNotif	= 'A';
			
			serviceRequestTO.addValue('inNoPaycheckOrder', noPaycheckOrder);
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message=''
			def codeError=''
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			
			println ('------------ **** RESULT **** -----------')
			NoPaycheckOrderResponse[] oNoPaycheckOrderResponse = serviceResponseTO.data.get('returnNoPaycheckOrderResponse').collect().toArray()
			def wReference = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia");
			
			
			println "******** Resulset *********"
			
			println " Referencia " + wReference
			for (var in oNoPaycheckOrderResponse) {
				
				println "*************RESULT **************"
				println "Initial Check "+ var.initialCheck;
				println "Final Check"+ var.finalCheck;
				println "Account "+ var.account;
				println "Reason" + var.reason;
				println "Suspension Date "+ var.suspensionDate;
				println "Reference" + var.reference;
				println "Commission"+ var.commission;
			}
 		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession);
			Assert.fail();
		}
	}
	
}
