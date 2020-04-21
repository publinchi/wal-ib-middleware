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
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Historical;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_time_deposit_historicals_query {

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
	void testgetTimeDepositHistoricals(){
		println ' ****** Prueba Regresión testgetTimeDepositHistoricals ************* '
		def ServiceName = 'getTimeDepositHistoricals'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetHistoricals');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			SearchOption searchOption = new SearchOption();
			
			enquiryRequest.userName = CTSEnvironment.bvLogin;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvAccDpfNumberHist;//@i_num_banco
			enquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat;//@i_formato_fecha
			enquiryRequest.currencyId=CTSEnvironment.bvAccDpfCurrencyId;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfType;//@i_prod
			searchOption.sequential = CTSEnvironment.bvAccDpfSequentialHist;//@i_secuencial
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			serviceRequestTO.addValue('inSearchOption',searchOption);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			Historical[] oHistoricalResponse= serviceResponseTO.data.get('returnHistorical').collect().toArray();
			
			println "******** Resulset *********"
			
			for (var in oHistoricalResponse) {
				
			/*
			Assert.assertNotNull("Sequence is null ", var.sequence);
			Assert.assertNotNull("Coupon is null ", var.coupon);
			Assert.assertNotNull("Transaction Date is null ", var.transactionDate);
			Assert.assertNotNull("Transaction Code is null ", var.transactionCode);
			Assert.assertNotNull("Description is null ", var.description);
			Assert.assertNotNull("Value is null ", var.value);
			Assert.assertNotNull("Observation is null ", var.observation);
			Assert.assertNotNull("Functionary is null ", var.funcionary);
			Assert.assertNotNull("Rate is null ", var.rate);
			*/

			//*******************************************************
			println "Sequence "+ var.sequence;
			println "Coupon "+ var.coupon;
			println "Transaction Date "+ var.transactionDate;
			println "Transaction Code "+ var.transactionCode;
			println "Description "+ var.description;
			println "Value "+ var.value;
			println "Observation "+ var.observation;
			println "Functionary "+ var.funcionary;
			println "Rate "+ var.rate;
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
