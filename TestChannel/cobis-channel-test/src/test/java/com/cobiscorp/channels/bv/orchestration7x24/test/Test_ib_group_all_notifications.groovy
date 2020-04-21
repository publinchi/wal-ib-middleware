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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_group_all_notifications {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession)
	}
	/**
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testGroupGetAllNotifications(){
		String ServiceName='GetAllNotifications'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetAllNotifications')
			//DTO IN
			
			Notification wNotification = new Notification()
			User wUser = new User();
			TransactionRequest wTransactionRequest = new TransactionRequest();
			SearchOption wSearchOption = new SearchOption();
			
			
			wNotification.setId("17494");
			wUser.setName("testCtsGrupo");
			wTransactionRequest.setDateFormatId(103);
			wSearchOption.setNumberOfResults(2);
			wSearchOption.setLastResult("17494");
			serviceRequestTO.addValue('inNotification', wNotification)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransactionRequest',  wTransactionRequest)
			serviceRequestTO.addValue('inSearchOption',  wSearchOption)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnNotification').collect().size()>0)
			println("\n")
			println("******  ********************************** ******** ")
			println("******  RESPUESTA TODAS LAS NOTIFICACIONES ******** ")
			println("******  ********************************** ******** ")
			Notification[] wArrayPayment = serviceResponseTO.data.get('returnNotification').collect().toArray()
			for (data in wArrayPayment) {
				println("id:"+data.id+" | account:"+data.account+" | productAbbreviation:"+data.productAbbreviation+" | creationDate:"+data.creationDate+" | selfClearingDate:"+data.selfClearingDate +"| messageId:"+data.messageId +" | status:"+data.status+" | url:"+data.url+" | idStatus:"+data.idStatus )
			}
		}catch(Exception e){
		def msg=e.message
		println "${ServiceName} Exception--> ${msg}"
		virtualBankingBase.closeSessionGroup(initSession)
		}
	}	
}
