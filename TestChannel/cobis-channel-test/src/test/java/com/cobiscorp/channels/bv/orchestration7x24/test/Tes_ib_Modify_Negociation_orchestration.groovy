package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanData
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

class Tes_ib_Modify_Negociation_orchestration {
	
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
	void testModifyNegociation(){
		String ServiceName='testModifyNegociation'
	
		try{
			println String.format('Test [%s]',ServiceName)
			println "ingresa al servicio"
		   ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.ModifyNegotiation')
			PaymentRequest aPaymentRequest = new PaymentRequest()
			LoanData aLoanData= new LoanData()
			aPaymentRequest.setLoanNumber('10410108232700018')
			aPaymentRequest.setUserName('testCts')
			aPaymentRequest.setCurrencyId(0)
			aLoanData.setCompleteQuota('D')
			aLoanData.setChargeRate('P')
		    aLoanData.setReductionRate('N')
			aLoanData.setPaymentEffect('M')
		    aLoanData.setPriorityRate('N')
			aLoanData.setAdvancePayment('S')
			serviceRequestTO.addValue('inPaymentRequest', aPaymentRequest)
			serviceRequestTO.addValue('inaLoanData', aLoanData)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			
			else{
				
				println "Servicio OK"
				Assert.assertTrue(message, serviceResponseTO.success)
				println ">>>>EL PAGO SE EJECUTO CON EXITO"
			}
			
								
		}
		
		 catch(Exception e){
	  def msg=e.message
	    println "${ServiceName} Exception--> ${msg}"
		virtualBankingBase.closeSessionNatural(initSession)
		Assert.fail()
			
	}
	
	}
	
	
	
}