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

class Test_ib_group_details_notification {
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
	void testNaturalGetDetailsNotification(){
		String ServiceName='GetDetailsNotification'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecuci�n del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Notification.GetDetailsNotification')
			//DTO IN
			
			Notification wNotification = new Notification()
			User wUser = new User();
			TransactionRequest wTransactionRequest = new TransactionRequest();
			
			wNotification.setId("11490");
			wUser.setName("testCtsGrupo");
			wTransactionRequest.setDateFormatId(103);
			
			serviceRequestTO.addValue('inNotification', wNotification)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransactionRequest',  wTransactionRequest)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			   
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecuci�n del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnNotification').collect().size()>0)
			println("\n")
			println("******  *********************************** ******** ")
			println("******  RESPUESTA DETALLE DE NOTIFICACIONES ******** ")
			println("******  *********************************** ******** ")
			Notification[] wArrayPayment = serviceResponseTO.data.get('returnNotification').collect().toArray()
			for (data in wArrayPayment) {
				println("id:"+data.id+" | account:"+data.account+" | productAbbreviation:"+data.productAbbreviation+" | name:"+data.varcharValue+" | name:"+data.varcharValue+" | creationDate:"+data.creationDate +" | selfClearingDate:"+data.selfClearingDate+" | messageId:"+data.messageId +" | url:"+data.url+" | status:"+data.status+" | detail:"+data.detail)
			}
		}catch(Exception e){
		def msg=e.message
		println "${ServiceName} Exception--> ${msg}"
		virtualBankingBase.closeSessionGroup(initSession)
		}
	}	
}
