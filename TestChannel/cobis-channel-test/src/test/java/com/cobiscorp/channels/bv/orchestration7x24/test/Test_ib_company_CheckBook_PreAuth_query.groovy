package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Check
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PreAuthCheckOrder
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author areinoso
 *
 */

class Test_ib_company_CheckBook_PreAuth_query {
	
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
	void testgetTimeDepositsPaymentSchedule(){
		println ' ****** Prueba Regresión testCheckBookPreAuth ************* '
		def ServiceName = 'getCheckBookPreAuth'
	
			
		try{
			
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Checkbook.PreAuthCheckService')
										   
			
			PreAuthCheckOrder preAuthCheckOrder = new PreAuthCheckOrder();
			
			TransactionContextCIB transactionContextCIB = new TransactionContextCIB();
			TransactionRequest wTransactionRequest = new TransactionRequest();

			wTransactionRequest.authorizationRequired="N";//S/N
			preAuthCheckOrder.account = '10410108275405315';
			preAuthCheckOrder.currencyId = 0;
			preAuthCheckOrder.checkId = 64;
			preAuthCheckOrder.beneficiary= 'Amparo';
			preAuthCheckOrder.amount = 3100.00;
			//preAuthCheckOrder.clientId = null;
			//preAuthCheckOrder.productAlias= "";
			preAuthCheckOrder.productId= 3;
			preAuthCheckOrder.login="testCtsEmp";
			
			//preAuthCheckOrder.currencyName="";
			//preAuthCheckOrder.authorizationNumber=null;
			//preAuthCheckOrder.ente=null;
			//preAuthCheckOrder.message="";
		
			
			/*enquiryRequest.userName = CTSEnvironment.bvLogin;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvAccDpfNumberPaysched;//@i_op_num_banco
			enquiryRequest.currencyId=CTSEnvironment.bvAccDpfPayableCurrencyid;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfPayableProductid;//@i_product
			searchOption.sequential = CTSEnvironment.bvAccDpfQuota;//@i_cuota*/
			
			//serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			//serviceRequestTO.addValue('inSearchOption',searchOption);
			
			serviceRequestTO.addValue('inPreAuthCheckOrder',preAuthCheckOrder);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			def wAuthorizationRequired = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_estado_cheque");
				
			println "******** Output ManagerCheckRequest Company*********"
			println ('RESPUESTA: @o_estado_cheque----->' + wAuthorizationRequired);
			
			println ('------------ **** RESULT **** -----------')
				Check[] oCheckBookPreAuth= serviceResponseTO.data.get('returnCheckBookPreAuth').collect().toArray()
			println "******** Resulset *********"
			
			for (var in oCheckBookPreAuth) {
							
				
				/*preAuthCheckOrder.account = '10410000005405100';
				preAuthCheckOrder.currencyId = 0;
				preAuthCheckOrder.checkId = 8;
				preAuthCheckOrder.beneficiary= 'Amparo';
				preAuthCheckOrder.amount = 3100;
				//preAuthCheckOrder.clientId= null;
				//preAuthCheckOrder.productAlias= "";
				preAuthCheckOrder.productId= null;
				preAuthCheckOrder.login="";
				//preAuthCheckOrder.currencyName="";
				//preAuthCheckOrder.authorizationNumber=null;
				//preAuthCheckOrder.ente=null;
				//preAuthCheckOrder.message="";*/
				
				
				println "*************RESULT NATURAL**************"
				println "Account "+ var.accountingBalance;
				println "CurrencyId"+ var.currencyId;
				println "CheckId "+ var.checkNumber;
				println "Beneficiary" + var.beneficiary;
				println "Amount "+ var.amount;
				println "Status "+ var.status;
		}
				
			} catch (Exception e) {
				def msg=e.message;
				println "${ServiceName} Exception--> ${msg}"
				virtualBankingBase.closeSessionCompany(initSession);
				Assert.fail();
			}
		
	}

}
