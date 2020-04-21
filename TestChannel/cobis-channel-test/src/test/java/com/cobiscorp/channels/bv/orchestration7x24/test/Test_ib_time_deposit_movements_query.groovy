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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Statement
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_time_deposit_movements_query {

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
	void testgetTimeDepositMovements(){
		println ' ****** Prueba Regresión testgetTimeDepositMovements ************* '
		def ServiceName = 'getTimeDepositMovements'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetStatements');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			SearchOption searchOption = new SearchOption();
			
			enquiryRequest.userName = CTSEnvironment.bvLogin;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvAccDpfNumber;//@i_num_banco
			enquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat;//@i_formato_fecha
			enquiryRequest.currencyId=CTSEnvironment.bvAccDpfCurrencyId;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfType;//@i_product
			searchOption.sequential = CTSEnvironment.bvAccDpfSequence;//@i_secuencia
			
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			serviceRequestTO.addValue('inSearchOption',searchOption);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			Statement[] oStatementResponse= serviceResponseTO.data.get('returnStatement').collect().toArray();
			//Statement oStatementResponse = ServiceResponseTO.data.get('returnStatement').collect().toArray();
			
			println "******** Resulset *********"
			
			for (var in oStatementResponse) {
				
			
			Assert.assertNotNull("Date is null ", var.date);
			//Assert.assertNotNull("Transaction Name is null ",oStatementResponse[0].);
			Assert.assertNotNull("Pay Format Date is null ", var.payFormat);
			Assert.assertNotNull("Currency is null ", var.currency);
			Assert.assertNotNull("International Amount is null ", var.internationalAmount);
			Assert.assertNotNull("Amount is null ", var.amount);
			Assert.assertNotNull("Status is null ", var.status);
			Assert.assertNotNull("Sequence is null ", var.sequence);
			//Assert.assertNotNull("Account is null ", var.account);
			Assert.assertNotNull("Beneficiary is null ", var.beneficiary);
			Assert.assertNotNull("Value Date is null ", var.valueDate);
			Assert.assertNotNull("Transaction Number is null ", var.transactionNumber);
			Assert.assertNotNull("Subsequence is null ", var.subsequence);
			

			//*******************************************************
			println "Date "+ var.date;
			println "Pay Format Date "+ var.payFormat;
			println "Currency "+ var.currency;
			println "International Amount "+ var.internationalAmount;
			println "Amount "+ var.amount;
			println "Status "+ var.status;
			println "Sequence "+ var.sequence;
			println "Account "+ var.account;
			println "Beneficiary "+ var.beneficiary;
			println "Value Date "+ var.valueDate;
			println "Transaction Number "+ var.transactionNumber;
			println "Subsequence "+ var.subsequence;
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
