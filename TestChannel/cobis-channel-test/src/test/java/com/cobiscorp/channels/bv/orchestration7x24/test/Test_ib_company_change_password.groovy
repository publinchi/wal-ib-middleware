package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Password

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

/**
 *
 * @author cecheverria
 *
 */

//  CREATE NEW PASSWORD

class Test_ib_company_change_password {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany(initSession)
	}

	@Test
	void TestChangeNewPassword () {
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		
		def rows = sql.executeUpdate("delete cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_historico_clave where hp_ente="+CTSEnvironment.bvCompanyEnte.toString()+" and hp_login = '"+CTSEnvironment.bvCompanyLogin+"'")
		
		
		//def ServiceName='TestGroupGetCCUltMovementsQuery'
		def ServiceName='TestChangeNewPassword'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Security.Security.ChangePassword')

			//DTO IN
			User wUser = new User()
			wUser.name = CTSEnvironment.bvCompanyLogin
			

			Password wPassword = new Password()
			wPassword.oldValue = CTSEnvironment.bvCompanyPassword
			wPassword.currentValue = 'testCts2' //Password Nuevo

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inPassword', wPassword)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "*** PASSWORD CAMBIADO CORRECTAMENTE ***"

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}
	
}
