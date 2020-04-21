
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

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.NotificationsCollection
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption


import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * GetLastNotification y GetNotificationNumber
 *
 * @since 2/Julio/2014
 * @author Carlos Echeverr�a
 * @version 1.0.0
 *
 *
 */
class Test_ib_alert_notification {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	def initSessionC
	def initSessionG
	

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
		initSessionC= virtualBankingBase.initSessionCompany();
		initSessionG= virtualBankingBase.initSessionGroup();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
		virtualBankingBase.closeSessionCompany(initSessionC)
		virtualBankingBase.closeSessionGroup(initSessionG)
	}

	/**
	 * M�todo que ejecuta Servicio GetLastNotification
	 * 
	 *
	 */
	void GetLastNotification(String pSession, String pLogin)
	{
		
		String ServiceName='GetLastNotification'
		println "***** "+ServiceName +" Login: "+ pLogin+" *******"
		try
		{
			// Preparo ejecuci�n del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetLastNotification')
			
			
			def wTransactionRequest = new TransactionRequest()
			wTransactionRequest.userName=pLogin
			wTransactionRequest.dateFormatId=101
			
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)
			
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias returnNotification'+pLogin, serviceResponseTO.getData().get('returnNotification').collect().size()>0)
			
			
			Notification[] oreturnNotification= serviceResponseTO.data.get('returnNotification').collect().toArray()
			

			for (var in oreturnNotification) {
				println " ***  �ltima Notificaci�n ---> "+var.description
			}
			
				
			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
	}
	
	/**
	 * M�todo que ejecuta Servicio GetNotificationNumber
	 *
	 *
	 */
	void GetNotificationNumber(String pSession, String pLogin)
	{
		
		String ServiceName='GetNotificationNumber'
		println "***** "+ServiceName +" Login: "+ pLogin+" *******"
		try
		{
			// Preparo ejecuci�n del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(pSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetNotificationNumber')
			
			
			def wTransactionRequest = new TransactionRequest()
			wTransactionRequest.userName=pLogin
			wTransactionRequest.dateFormatId=101
			
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)
			
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias returnSearchOption '+ pLogin, serviceResponseTO.getData().get('returnSearchOption').collect().size()>0)
			
			
			SearchOption[] oreturnSearchOption= serviceResponseTO.data.get('returnSearchOption').collect().toArray()
			

			for (var in oreturnSearchOption) {
				println " *** "+pLogin +" N�mero de Notificaciones---> "+var.numberOfResults
			}
			
				
			
		}
		catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(pLogin,pSession)
		}
	}
	
	
	/**
	 * Consulta de �ltima notificaci�n Person
	 */
	@Test
	void testGetLastNotificationPerson() {
		GetLastNotification(initSession, CTSEnvironment.bvLogin)
	}
	
	/**
	 * Consulta de �ltima notificaci�n Company
	 */
	@Test
	void testGetLastNotificationCompany() {
		GetLastNotification(initSessionC, CTSEnvironment.bvCompanyLogin)

	}
	
	
	/**
	 * Consulta de �ltima notificaci�n Group
	 */
	@Test
	void testGetLastNotificationGroup () {
		GetLastNotification(initSessionG, CTSEnvironment.bvGroupLogin)

	}

	
	/**
	 * Consulta de n�mero de notificaciones Person
	 */
	@Test
	void testGetNotificationNumberPerson() {
		GetNotificationNumber(initSession, CTSEnvironment.bvLogin)
	}
	
	/**
	 * Consulta de n�mero de notificaciones Company
	 */
	@Test
	void testGetNotificationNumberCompany() {
		GetNotificationNumber(initSessionC, CTSEnvironment.bvCompanyLogin)

	}
	
	
	/**
	 * Consulta de n�mero de notificaciones Group
	 */
	@Test
	void testGetNotificationNumberGroup () {
		GetNotificationNumber(initSessionG, CTSEnvironment.bvGroupLogin)

	}

				
}
