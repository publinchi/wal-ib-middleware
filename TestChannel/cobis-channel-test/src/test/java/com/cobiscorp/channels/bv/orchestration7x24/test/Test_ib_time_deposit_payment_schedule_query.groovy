package com.cobiscorp.channels.bv.orchestration7x24.test;

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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentSchedule
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author areinoso
 *
 */
public class Test_ib_time_deposit_payment_schedule_query {

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
	 * Metodo que ejecuta el servicio Consulta de 
	 */
	@Test
	void testgetTimeDepositsPaymentSchedule(){
		println ' ****** Prueba Regresión testgetTimeDepositsPaymentSchedule ************* '
		def ServiceName = 'getTimeDepositsPaymentSchedule'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPaymentSchedule');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			SearchOption searchOption = new SearchOption();
			
			enquiryRequest.userName = CTSEnvironment.bvLogin;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvAccDpfNumberPaysched;//@i_op_num_banco
			enquiryRequest.currencyId=CTSEnvironment.bvAccDpfPayableCurrencyid;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfPayableProductid;//@i_product
			searchOption.sequential = CTSEnvironment.bvAccDpfQuota;//@i_cuota
			
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			serviceRequestTO.addValue('inSearchOption',searchOption);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			PaymentSchedule[] oPaymentScheduleResponse= serviceResponseTO.data.get('returnPaymentSchedule').collect().toArray();
			//Statement oStatementResponse = ServiceResponseTO.data.get('returnStatement').collect().toArray();
			
			println "******** Resulset *********"
			
			for (var in oPaymentScheduleResponse) {
				
			/*
			Assert.assertNotNull("Quota is null ", var.quota);
			Assert.assertNotNull("Payment Date is null ", var.paymentDate);
			Assert.assertNotNull("Pay Format Date is null ", var.paymentDate);
			Assert.assertNotNull("Quota Amount is null ", var.quotaAmount);
			Assert.assertNotNull("Entity is null ", var.entity);
			Assert.assertNotNull("Operation Description is null ", var.operationDescription);
			Assert.assertNotNull("Address Description is null ", var.addressDescription);
			Assert.assertNotNull("Office Name is null ", var.officeName);
			Assert.assertNotNull("Bank Number Operation is null ", var.bankNumberOperation);
			Assert.assertNotNull("Deposit Type Description is null ", var.depositTypeDescription);
			Assert.assertNotNull("Amount is null ", var.amount);
			Assert.assertNotNull("Payment Description is null ", var.paymentDescription);
			Assert.assertNotNull("Currency is null ", var.currency);
			Assert.assertNotNull("Rate is null ", var.rate);
			Assert.assertNotNull("Expiration Date is null ", var.expirationDate);
			Assert.assertNotNull("Status is null ", var.status);
			Assert.assertNotNull("Operation Days Number is null ", var.operationDaysNumber);
			Assert.assertNotNull("Insert Date is null ", var.insertDate);
			Assert.assertNotNull("Quota Value is null ", var.quotaValue);
			Assert.assertNotNull("Quota Date Number is null ", var.quotaDaysNumber);
			Assert.assertNotNull("Last Payment Date is null ", var.lastPaymentDate);
			Assert.assertNotNull("Interest Earned is null ", var.interestEarned);
			*/
					
			//*******************************************************
			println "*************RESULT NATURAL**************"
	        println "Quota "+ var.quota;
			println "Payment Date "+ var.paymentDate;
			println "Pay Format Date "+ var.paymentDate;
			println "Quota Amount "+ var.quotaAmount;
			println "Entity "+ var.entity;
			println "Operation Description "+ var.operationDescription;
			println "Address Description "+ var.addressDescription;
			println "Office Name "+ var.officeName;
			println "Bank Number Operation "+ var.bankNumberOperation;
			println "Deposit Type Description "+ var.depositTypeDescription;
			println "Amount "+ var.amount;
			println "Payment Description "+ var.paymentDescription;
			println "Currency "+ var.currency;
			println "Rate "+ var.rate;
			println "Expiration Date "+ var.expirationDate;
			println "Status "+ var.status;
			println "Operation Days Number "+ var.operationDaysNumber;
			println "Insert Date "+ var.insertDate;
			println "Quota Value "+ var.quotaValue;
			println "Quota Date Number "+ var.quotaDaysNumber;
			println "Last Payment Date "+ var.lastPaymentDate;
			println "Interest Earned " + var.interestEarned;
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
