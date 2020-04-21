package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.businessbanking.services.dto.Notification
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AgreementService
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD



/***
 * 
 * 
 * @author eortega
 *
 */
class Test_ib_get_agreement {

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
	 * Get agreement service
	 */
	@Test
	void test1GetAgreementService() {
		String ServiceName='GetAgreementService'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Customer.Customer.GetAgreementService')
			                               
			//DTO IN
			 AgreementService wAgreementService = new AgreementService();
				 wAgreementService.entityId=  277
				 wAgreementService.login= CTSEnvironment.bvLogin
				 wAgreementService.agreement='IBSER1'
			
			serviceRequestTO.addValue('inAgreementService', wAgreementService)
			
			// se elimina el registro ya que en la parametria se esta insertando.
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			sql.execute("delete cob_bvirtual..bv_ente_acuerdo_servicio where as_ente = 277 and as_login ='testCts' and as_servicio = 1 and as_acuerdo  = 'IBSER1' ")
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			
			
			def wEstado = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_estado")
			Assert.assertNotNull("No se obtuvo Numero de Comprobante",wEstado)
			println ('RESPUESTA: ------------------>Estado Acuerdo de servicio:' + wEstado)
						
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	
	/**
	 * Get Set Agreement Service
	 */
	@Test
	void test2SaveAgreementService() {
		String ServiceName='SetAgreementService'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Customer.Customer.SetAgreementService')
										   
			//DTO IN
			 AgreementService wAgreementService = new AgreementService();
				 wAgreementService.entityId=  277
				 wAgreementService.login= CTSEnvironment.bvLogin
				 wAgreementService.agreement='IBSER1'
			
			serviceRequestTO.addValue('inAgreementService', wAgreementService)
			
			// se elimina el registro ya que en la parametria se esta insertando.
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			sql.execute("delete cob_bvirtual..bv_ente_acuerdo_servicio where as_ente = 277 and as_login ='testCts' and as_servicio = 1 and as_acuerdo  = 'IBSER1' ")
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido--->>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			
			def resultado= null
			resultado= sql.rows("select as_estado, as_acuerdo from cob_bvirtual..bv_ente_acuerdo_servicio where as_ente = 277 and as_login  = 'testCts' and as_servicio = 1 and as_acuerdo  = 'IBSER1' ")
			Assert.assertEquals("No se encontro registro en la re_tran_monet",resultado.size(), 1)
			println ('RESPUESTA: Insert acuerdo de servicio ----------->'+resultado)
			
						
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
	
	

}


