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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetailSchedule;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_company_time_deposit_payment_detail_schedule_query {

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
	void testgetTimeDepositsPaymentDetailSchedule(){
		println ' ****** Prueba Regresión testgetTimeDepositsPaymentDetailSchedule ************* '
		def ServiceName = 'getTimeDepositsPaymentDetailSchedule'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPaymentDetailSchedule');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			
			enquiryRequest.userName = CTSEnvironment.bvLoginEmpresa;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvCompanyAccDpfNumberPaysched;//@i_op_num_banco
			enquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat;//@i_formato_fecha
			enquiryRequest.currencyId=CTSEnvironment.bvCompanyAccDpfCurrencyidHist;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvCompanyAccDpfProductidHist;//@i_product
			
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			PaymentDetailSchedule[] oPaymentDetailSchedule= serviceResponseTO.data.get('returnPaymentDetailSchedule').collect().toArray();
			
			println "******** Resulset TimeDepositsPaymentDetailSchedule*********"
			
			int i = 0;
			for (var in oPaymentDetailSchedule) {
				
				println "******** TimeDepositsPaymentDetailSchedule "+i+ "*********"
				//*******************************************************
				println "Entity " + var.entity;
				println "Days Number " + var.daysNumber;
				println "Amount Paied Interest " + var.amountPaiedInterest;
				println "Rate " + var.rate;
				println "Earned Interest " + var.earnedInterest;
				println "Compounded " + var.compounded;
				println "Payment Type " + var.paymentType;
				println "Status " + var.status;
				println "Value Date " + var.valueDate;
				println "Expirate Date " + var.expirateDate;
				println "Pay Day " + var.payDay;
				println "Month " + var.month;
				println "Base Calculate " + var.baseCalculate;
				i++;
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
