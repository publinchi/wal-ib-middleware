package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert
import com.cobiscorp.test.CTSEnvironment
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_personal_getpayment {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
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
	 * Test to approve authorization of Self Account Transfers
	 */
	//@Test
	void testNaturalGetPayment(){
		String ServiceName='GetPayment'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Payment.GetPayment')
			//DTO IN
			
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			Payment wPayment = new Payment();
			
			wEnquiryRequest.dateFormatId = 101
			wPayment.id = 1
			
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inPayment', wPayment)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			   
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnPayment').collect().size()>0)
			Payment[] wArrayPayment = serviceResponseTO.data.get('returnPayment').collect().toArray()
			for (data in wArrayPayment) {
				println("id:"+data.id+" | creditor:"+data.creditor+" | bill:"+data.bill+" | ammount:"+data.ammount+" | currency:"+data.currency+" | creationDate:"+data.creationDate+" | status:"+data.status+" | statusId:"+data.statusId+" | paymentDate:"+data.paymentDate+" | notes:"+data.notes)
			}
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	@Test
	void testNaturalGetPayments(){
		String ServiceName='GetPayments'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Payment.GetPayments')
			//DTO IN
			
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			SearchOption wSearchOption = new SearchOption() 
			
			wSearchOption.initialDate = "01/01/2012"
			wSearchOption.finalDate = "01/01/2014"
			wEnquiryRequest.status = "R"
			wEnquiryRequest.dateFormatId = 101
			wSearchOption.lastResult = 0
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			   
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnPayment').collect().size()>0)
			Payment[] wArrayPayment = serviceResponseTO.data.get('returnPayment').collect().toArray()
			for (data in wArrayPayment) {
				println("id:"+data.id+" | creditor:"+data.creditor+" | bill:"+data.bill+" | ammount:"+data.ammount+" | currency:"+data.currency+" | creationDate:"+data.creationDate+" | status:"+data.status+" | statusId:"+data.statusId+" | paymentDate:"+data.paymentDate+" | notes:"+data.notes)
			}
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

}
