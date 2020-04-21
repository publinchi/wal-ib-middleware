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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PayableInterest
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
/**
 * @author jmoreta
 *
 */
class Test_ib_company_time_deposit_payable_interests_query {

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
	 * Metodo que ejecuta el servicio Consulta de Intereses por Pagar
	 */
	@Test
	void testgetTimeDepositPayableInterests(){
		println ' ****** Prueba Regresión testgetTimeDepositPayableInterests ************* '
		def ServiceName = 'getTimeDepositsPayableInterests'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPayableInterests');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			SearchOption searchOption = new SearchOption();
			
			enquiryRequest.userName = CTSEnvironment.bvLoginEmpresa;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvCompanyDPFNumber;//@i_cuenta @i_cta
			enquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat;//@i_formato_fecha
			enquiryRequest.currencyId=CTSEnvironment.bvCompanyAccDpfPayableCurrencyid;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfType;//@i_prod
			searchOption.sequential = CTSEnvironment.bvCompanyAccDpfPayableSequence;//@i_cuota
			
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			serviceRequestTO.addValue('inSearchOption',searchOption);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			PayableInterest[] oPayableInterestResponse= serviceResponseTO.data.get('returnPayableInterest').collect().toArray();
			
			println "******** Resulset *********"
			int i = 0;
			for (var in oPayableInterestResponse) {
			
				Assert.assertNotNull("Pay Number is null ", var.payNumber);
				//Assert.assertNotNull("PrePrintNumber is null ", var.prePrintNumber);
				Assert.assertNotNull("ExpirationDate is null ", var.expirationDate);
				Assert.assertNotNull("ApproximateValue is null ", var.approximateValue);
				Assert.assertNotNull("Value is null ", var.value);
				Assert.assertNotNull("Tax is null ", var.tax);
				//Assert.assertNotNull("DateBox is null ", var.dateBox);
				Assert.assertNotNull("Status is null ", var.status);
				//Assert.assertNotNull("PrintNumber is null ", var.printNumber);
				Assert.assertNotNull("Detained is null ", var.detained);
				Assert.assertNotNull("CouponNumber is null ", var.couponNumber);
				Assert.assertNotNull("Currency is null ", var.currency);				
				Assert.assertNotNull("StartDate is null ", var.startDate);
				

				//*******************************************************
				println "******************RESULT "+ i +"*****************";
				println "Pay Number "+ var.payNumber;
				println "PrePrint Number "+ var.prePrintNumber;
				println "Expiration Date "+ var.expirationDate;
				println "ApproximateValue  "+ var.approximateValue;
				println "Value "+ var.value;
				println "Tax "+ var.tax;
				println "DateBox "+ var.dateBox;
				println "Status "+ var.status;			
				println "Print Number "+ var.printNumber;			
				println "Detained "+ var.detained;
				println "Coupon Number "+ var.couponNumber;
				println "Currency "+ var.currency;
				println "Start Date "+ var.startDate;
				i++;
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
	
}
