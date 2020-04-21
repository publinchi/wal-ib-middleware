package com.cobiscorp.channels.bv.orchestration7x24.test


import junit.framework.Assert

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource


import cobiscorp.businessbanking.services.dto.Person
import cobiscorp.businessbanking.services.dto.UserCompanyRole
import cobiscorp.businessbanking.services.dto.Group

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseThirdAccounts

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * 
 * @author gyagual
 *
 */

class Test_ib_group_thirds_account_from_login {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup()
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
	 *This method gets all accounts from a login (natural)
	 */
   @Test
	void testGroupGetThirdsAccounts(){
		def ServiceName='testGroupGetThirdsAccounts'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('Cobiscorp.BusinessBanking.Services.Security.Authorization.GetCompaniesAndProfilesByUser')

			//DTO IN
			def Person wPerson = new Person()
			wPerson.username = CTSEnvironment.bvGroupLogin
			 
     		 serviceRequestTO.addValue('inPerson', wPerson)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			println "Finaliza ---> ${ServiceName}"
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			UserCompanyRole[] oreturnResponse= serviceResponseTO.data.get('returnUserCompanyRole').collect().toArray()
			
			println " ***  Login ---> "+  CTSEnvironment.bvGroupLogin
			
			for (var in oreturnResponse) {
				
				println " ***  CompanyName ---> "+var.companyName
				println " ***  RoleName ---> "+var.roleName
			}
			}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}
}
