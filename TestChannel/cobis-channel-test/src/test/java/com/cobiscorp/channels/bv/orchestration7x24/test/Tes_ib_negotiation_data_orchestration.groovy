package com.cobiscorp.channels.bv.orchestration7x24.test
import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.products.dto.Loan
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanData
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest

import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.VirtualBankingBase;

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;

class Tes_ib_negotiation_data_orchestration {
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
	@Test
	void testNegotiationData(){
		String ServiceName='testNegotiationData'
		try{
		println String.format('Test [%s]',ServiceName)
		println "ingresa al servicio"
				// Preparo ejecución del servicio
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		serviceRequestTO.setSessionId(initSession)
		serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetNegotiationData')
		PaymentRequest aPaymentRequest = new PaymentRequest()
		println "Antes envio parametros"
		aPaymentRequest.setLoanNumber('10410000028700900')
	
		aPaymentRequest.setCurrencyId(0)
		aPaymentRequest.setUserName("testCts")
		
		println "Antes envio parametros"
				
		
		serviceRequestTO.addValue('inPaymentRequest', aPaymentRequest)
		
		
		ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
		
		//Valido si fue exitoso la ejecucion
		String message=''
		def codeError=''
		if (serviceResponseTO.messages.toList().size()>0){
		message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
		}
		println "ANTES"
		Assert.assertTrue(message, serviceResponseTO.success)
		println "DESPUES"
				LoanData [] oResponseParameter= serviceResponseTO.data.get('returnLoanData').collect().toArray()
				println "DESPUES DE"
		for (var in oResponseParameter) {
			
			println "ingresa al for"
			
			Assert.assertNotNull("chargeRate  is null ",var.chargeRate)
			Assert.assertNotNull("advancePayment is null ",var.advancePayment)
			Assert.assertNotNull("reductionRate  is null ",var.reductionRate)
			Assert.assertNotNull("aplicationRate  is null ",var.aplicationRate)
			Assert.assertNotNull("completeQuota  is null ",var.completeQuota)
			Assert.assertNotNull("priorityRate   is null ",var.priorityRate)
			Assert.assertNotNull("paymentEffect  is null ",var.paymentEffect)
			Assert.assertNotNull("currencyId   is null ",var.currencyId)
			Assert.assertNotNull("currencyName  is null ",var.currencyName)
			
			println ('------------ RESULTADO -----------')
			println " ***   chargeRate     ---> "+var.chargeRate
			println " ***   advancePayment   ---> "+var.advancePayment
			println " ***  reductionRate    ---> "+var.reductionRate
			println " ***  aplicationRate  ---> "+var.aplicationRate
			println " ***  completeQuota  ---> "+var.completeQuota
			println " ***  priorityRate    ---> "+var.priorityRate
			println " ***  paymentEffect ---> "+var.paymentEffect
			println " ***  currencyId  ---> "+var.currencyId
			println " ***  currencyName ---> "+var.currencyName
					
		}
		}catch(Exception e){
		def msg=e.message
		println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
	}
	
	}
	
	
}
