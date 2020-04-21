package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CreditCard
import cobiscorp.ecobis.internetbanking.webapp.services.dto.RequestCreditCard;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseCreditCard;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User;




import com.cobiscorp.cobis.plugin.activator.HttpServiceActivator.InternalResource;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * Get Balance y Get Prize
 *
 * @since 7/Agosto/2014
 * @author Carlos Echeverría
 * @version 1.0.0
 *
 *
 */
class Test_ib_information_credit_card {



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
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
	}


	/**
	 * Método que ejecuta Servicio ValidateNewLogin
	 *
	 */
	
	void  GetBalance(String pSession,String pLogin)
	{
		
		try
		{
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.GetBalance')
										   
			
			
			RequestCreditCard wCreditCard = new RequestCreditCard()
			wCreditCard.card = CTSEnvironment.bvAccTarNumber
			wCreditCard.productId = Integer.parseInt(CTSEnvironment.bvAccTarType)
			
			User wUser = new User()
			wUser.name = pLogin
			
			
			serviceRequestTO.addValue('inRequestCreditCard', wCreditCard)
			serviceRequestTO.addValue('inUser', wUser)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			ResponseCreditCard[] oResponse= serviceResponseTO.data.get('returnResponseCreditCard').collect().toArray()
			
			for (var in oResponse) {
				println "******** RESPUESTA *********"
				Assert.assertNotNull("Local Balance is null", var.localBalance)
				Assert.assertNotNull("Local Minimum Payment is null", var.localMinimumPayment)
				Assert.assertNotNull("Cash Payment Local Currency is null", var.cashPaymentLocalCurrency)
				Assert.assertNotNull("Available local is null", var.availableLocal)
				
				Assert.assertNotNull("International Balance is null", var.internationalBalance)
				Assert.assertNotNull("International Minimum Payment is null", var.internationalMinimumPayment)
				Assert.assertNotNull("Cash Payment international Currency is null", var.cashPaymentInternationalCurrency)
				Assert.assertNotNull("Available international is null", var.availableInternational)
				
				Assert.assertNotNull("Payment Date international is null", var.paymentDate)
				
				Assert.assertNotNull("Available Local EF is null", var.availableLocalEF)
				Assert.assertNotNull("Available International EF is null", var.availableInternationalEF)
				
				Assert.assertNotNull("Debit Local Transit is null", var.debitLocalTransit)
				
				Assert.assertNotNull("Debit International Transit is null", var.debitInternationalTransit)
				
				Assert.assertNotNull("Response Code is null", var.responseCode)
				
				println("Local Balance "+ var.localBalance.toString())
				println("Local Minimum Payment "+ var.localMinimumPayment.toString())
				println("Cash Payment Local Currency "+ var.cashPaymentLocalCurrency.toString())
				println("Available local "+ var.availableLocal.toString())
				
				println("International Balance "+ var.internationalBalance.toString())
				println("International Minimum Payment "+ var.internationalMinimumPayment.toString())
				println("Cash Payment international Currency "+ var.cashPaymentInternationalCurrency.toString())
				println("Available international "+ var.availableInternational.toString())
				
				println("Payment Date international "+ var.paymentDate.toString())
				
				println("Available Local EF "+ var.availableLocalEF)
				println("Available International EF "+ var.availableInternationalEF.toString())
				
				println("Debit Local Transit "+ var.debitLocalTransit.toString())
				
				println("Debit International Transit "+ var.debitInternationalTransit.toString())
				
				println("Response Code "+ var.responseCode.toString())
				
			}

			
			//HashMap$Entry wOuts =  serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").collect().toSet()
			
			println "Outputs"
			//
			
			Assert.assertNotNull("@o_fechavencimiento  is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fechavencimiento"))
			Assert.assertNotNull("@o_pagominimolocal   is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagominimolocal"))
			Assert.assertNotNull("@o_pagocontadolocal  is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagocontadolocal"))
			Assert.assertNotNull("@o_pagominimoint     is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagominimoint"))
			Assert.assertNotNull("@o_pagocontadoint    is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagocontadoint"))
			Assert.assertNotNull("@o_saldolocal        is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saldolocal"))
			Assert.assertNotNull("@o_saldointernacional is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saldointernacional"))
			Assert.assertNotNull("@o_saltocortelocal   is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saltocortelocal"))
			Assert.assertNotNull("@o_saltocorteinter   is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saltocorteinter"))
			Assert.assertNotNull("@o_fechacorte        is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fechacorte"))
			Assert.assertNotNull("@o_error             is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_error"))
			Assert.assertNotNull("@o_descripcion       is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_descripcion"))
			Assert.assertNotNull("@o_disponibleeflocal is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_disponibleeflocal"))
			Assert.assertNotNull("@o_disponibleefinter is null",serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_disponibleefinter"))
			
			
			println("@o_fechavencimiento  "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fechavencimiento"))
			println("@o_pagominimolocal   "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagominimolocal"))
			println("@o_pagocontadolocal  "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagocontadolocal"))
			println("@o_pagominimoint     "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagominimoint"))
			println("@o_pagocontadoint    "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_pagocontadoint"))
			println("@o_saldolocal        "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saldolocal"))
			println("@o_saldointernacional "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saldointernacional"))
			println("@o_saltocortelocal   "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saltocortelocal"))
			println("@o_saltocorteinter   "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_saltocorteinter"))
			println("@o_fechacorte        "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fechacorte"))
			println("@o_error             "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_error"))
			println("@o_descripcion       "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_descripcion"))
			println("@o_disponibleeflocal "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_disponibleeflocal"))
			println("@o_disponibleefinter "+serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_disponibleefinter"))
			
			
			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetBalance', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
		
	}
	
	void  GetPrize(String pSession,String pLogin)
	{
		try
		{
			
			//Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.GetPrize')
										   
			
			
			TransactionRequest wTransactionRequest = new TransactionRequest()
			wTransactionRequest.dateFormatId= 103
			wTransactionRequest.productNumber = CTSEnvironment.bvAccTarNumber
			
			
			
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)
			
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)			
			CreditCard[] oResponse= serviceResponseTO.data.get('returnCreditCard').collect().toArray()
			
			println "******** RESPUESTA *********"
			for (var in oResponse) {
				Assert.assertNotNull("Number of Credit Card is null ",var.card)
				Assert.assertNotNull("Total prize is null ",var.totalPrize)
				Assert.assertNotNull("Cut off Date is null ",var.cutoffDate)
				println "Credit Card "+ var.card
				println "Total Prize "+ var.totalPrize.toString()
				println "Cut Off Date"+ var.cutoffDate
				
				
			}

			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', 'GetPrize', e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}

		
	}
	
	
	
	/**
	 * Obtiene Información de Clave
	 */
	@Test
	void testGetInformationCreditCard() {
		
		println ' ****** Prueba Regresión testGetInformationCreditCard ************* '
		GetBalance(initSession, CTSEnvironment.bvLogin)
		GetPrize(initSession, CTSEnvironment.bvLogin)
		
	}
}

