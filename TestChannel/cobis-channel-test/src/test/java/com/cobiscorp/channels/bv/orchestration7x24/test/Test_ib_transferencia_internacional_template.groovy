package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runners.MethodSorters

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.RequestTemplate

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil




/***
 *
 *
 * @author eortega
 * Esta funcionalidad no varia para grupo o empresa.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Test_ib_transferencia_internacional_template {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	def creditAccount='123456789'
	def wRowTemplate=null

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
	 * Insert de Template para transferencias Internacionales
	 */

	@Test
	void testInsertTemplate() {
		String ServiceName='AddTemplate'
		try{
			// borro data a insertar
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			sql.execute('delete cob_bvirtual..bv_plantilla where pl_cta_credito = ?',creditAccount)

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.Transfer.AddTemplate')


			//DTO IN
			RequestTemplate wRequestTemplate=new RequestTemplate()

			wRequestTemplate.mail_1='eortega@cobiscorp.com'
			wRequestTemplate.mail_2='eortega@cobiscorp.com'
			wRequestTemplate.creditAccount=creditAccount
			wRequestTemplate.beneficiaryType= '1.1'
			wRequestTemplate.beneficiary='Erica Ortega'
			wRequestTemplate.firstName='Erica'
			wRequestTemplate.lastName='Ortega'
			wRequestTemplate.recipientAddress='Cumbaya'

			wRequestTemplate.beneficiaryBank='BANCO PRUEBA'
			wRequestTemplate.idBeneficiaryBank=2914
			wRequestTemplate.idbeneficiaryBankAddress=1
			wRequestTemplate.idbeneficiaryBankCountry=188

			wRequestTemplate.idintermediaryBankAddres=2
			wRequestTemplate.idintermediaryBankCountry=188
			wRequestTemplate.idIntermediaryBank=95
			wRequestTemplate.sequential= 0
			wRequestTemplate.login=CTSEnvironment.bvLogin
			wRequestTemplate.templateName='Template Prueba de Regresion'
			wRequestTemplate.beneficiaryBusinessName='razon social prueba de regresion'
			wRequestTemplate.beneficiaryDocumentNumber='123456789'
			wRequestTemplate.swiftOrAba='BALYCRS1XXX'
			wRequestTemplate.swiftOrAbaInt='BALYCRS1XXX'
			wRequestTemplate.idBeneficiaryCountry=46
			wRequestTemplate.idBeneficiaryCity=915

			serviceRequestTO.addValue('inRequestTemplate', wRequestTemplate)


			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//validar que se actualizo el registro
			wRowTemplate = sql.rows("select * from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_plantilla where pl_cta_credito =?",creditAccount)
			Assert.assertEquals("No se encontro registro en la bv_plantilla",wRowTemplate.size(), 1)
			println ('TEMPLATE INSERTADO ---------------------->'+wRowTemplate)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}


	/**
	 * Actualizacion de Template para transferencias Internacionales
	 **/
	@Test
	void testUpdateTemplate() {
		String ServiceName='UpdateTemplate'
		try{

			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			wRowTemplate = sql.rows("select pl_secuencial from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_plantilla where pl_cta_credito =?",creditAccount)
			int secuencial = wRowTemplate[0][0]

			println String.format('Test [%s]',ServiceName)

			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.Transfer.UpdateTemplate')

			//DTO IN
			RequestTemplate wRequestTemplate=new RequestTemplate()

			wRequestTemplate.mail_1='eortega@cobiscorp.com'
			wRequestTemplate.mail_2='eortega@cobiscorp.com'
			wRequestTemplate.creditAccount='123456789'
			wRequestTemplate.beneficiaryType= '1.1'
			wRequestTemplate.beneficiary='Erica Ortega Actualizado'
			wRequestTemplate.firstName='Erica Actualizado'
			wRequestTemplate.lastName='Ortega Actualizado'
			wRequestTemplate.recipientAddress='Cumbaya'

			wRequestTemplate.beneficiaryBank='BANCO PRUEBA'
			wRequestTemplate.idBeneficiaryBank=2914
			wRequestTemplate.idbeneficiaryBankAddress=1
			wRequestTemplate.idbeneficiaryBankCountry=188

			wRequestTemplate.idintermediaryBankAddres=2
			wRequestTemplate.idintermediaryBankCountry=188
			wRequestTemplate.idIntermediaryBank=95
			wRequestTemplate.sequential= secuencial
			wRequestTemplate.login=CTSEnvironment.bvLogin
			wRequestTemplate.templateName='Template Prueba de Regresion'
			wRequestTemplate.beneficiaryBusinessName='razon social prueba de regresion'
			wRequestTemplate.beneficiaryDocumentNumber='123456789'
			wRequestTemplate.swiftOrAba='BALYCRS1XXX'
			wRequestTemplate.swiftOrAbaInt='BALYCRS1XXX'

			serviceRequestTO.addValue('inRequestTemplate', wRequestTemplate)


			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//validar que se actualizo el registro
			def wRowTemplate=null

			wRowTemplate = sql.rows("select * from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_plantilla where pl_secuencial =?",secuencial)
			Assert.assertEquals("No se encontro registro en la bv_plantilla",wRowTemplate.size(), 1)
			println ('RESPUESTA: nueva data actualizada -------------->'+wRowTemplate)


		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}


