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

class Test_ib_time_deposit_realdays_query {

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
	void testgetGetRealDays(){
		println ' ****** Prueba Regresión testgetRealDays ************* '
		def ServiceName = 'getGetRealDays'
		
		try {
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetRealDays');
			
			EnquiryRequest enquiryRequest = new EnquiryRequest();
			
			enquiryRequest.userName = CTSEnvironment.bvLogin;//@i_login
			enquiryRequest.productNumber = CTSEnvironment.bvAccDpfNumberPaysched;//@i_op_num_banco
			enquiryRequest.currencyId=CTSEnvironment.bvAccDpfPayableCurrencyid;//@i_mon
			enquiryRequest.productId=CTSEnvironment.bvAccDpfPayableProductid;//@i_product
			
			serviceRequestTO.addValue('inEnquiryRequest',enquiryRequest);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success);
			PaymentDetailSchedule[] oPaymentDetailSchedule= serviceResponseTO.data.get('returnPaymentDetailSchedule').collect().toArray();
			
			println "******** Resulset RealDays*********"
			
			for (var in oPaymentDetailSchedule) {
				
			println "******** RealDays*********"
			//*******************************************************
			println "Real Days: " + var.realDays
			}
		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
	
}
