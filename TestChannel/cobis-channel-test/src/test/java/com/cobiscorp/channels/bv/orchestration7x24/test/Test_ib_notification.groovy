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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * 
 * 
 * @author eortega
 *
 */
class Test_ib_notification {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
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

	/**
	 * Get Notification Number
	 */
	@Test
	void testGetNotificationNumber() {
		String ServiceName='GetNotificationNumber'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetNotificationNumber')
			                               
			//DTO IN
			 TransactionRequest wTransactionRequest = new TransactionRequest();
				 wTransactionRequest.userName=  CTSEnvironment.bvLogin
				 wTransactionRequest.dateFormat= CTSEnvironment.bvDateFormat
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			
			//Valido que traiga data
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnSearchOption').collect().size()>0)
			
			SearchOption[] oNotification= serviceResponseTO.data.get('returnSearchOption').collect().toArray()
			println String.format('------------------>Numero de Notificaciones: '+ oNotification[0].numberOfResults)
			

			
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	
	/*
	 * Obtener la ultima notificacion 
	 */
	@Test
	void testGetLastNotification() {
		String ServiceName='GetLastNotification'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetLastNotification')
										   
			//DTO IN
			 TransactionRequest wTransactionRequest = new TransactionRequest();
				 wTransactionRequest.userName=  CTSEnvironment.bvLogin
				 wTransactionRequest.dateFormat= CTSEnvironment.bvDateFormat
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			
			//Valido que traiga data
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnNotification').collect().size()>0)
			
			Notification[] oNotification= serviceResponseTO.data.get('returnNotification').collect().toArray()
			println String.format('------------------>Mensaje a msotrar: '+ oNotification[0].description)
			
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}


}


