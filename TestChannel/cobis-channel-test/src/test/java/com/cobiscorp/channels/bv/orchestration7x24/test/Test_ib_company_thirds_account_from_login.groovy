package com.cobiscorp.channels.bv.orchestration7x24.test

import junit.framework.Assert

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Service
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseThirdAccounts
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * 
 * @author gyagual
 *
 */
class Test_ib_company_thirds_account_from_login {
	
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
	
	/**
	 *This method gets all accounts from a login (company)
	 */
   @Test
	void testCompanyGetThirdsAccounts(){
		def ServiceName='testCompanyGetThirdsAccounts'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.UniqueLogin.GetThirdAccounts')

			//DTO IN
			def Client wClient = new Client()
			def Service wService = new Service()
			def SearchOption wSearchOption = new SearchOption()
			

			wClient.entityId = CTSEnvironment.bvCompanyEnte
			wService.id = CTSEnvironment.bvService
			wSearchOption.sequential = 656368073
			 
			 serviceRequestTO.addValue('inClient', wClient)
			 serviceRequestTO.addValue('inService', wService)
			 serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnResponseThirdAccounts').collect().size()==1)
			ResponseThirdAccounts[] oreturnResponse= serviceResponseTO.data.get('returnResponseThirdAccounts').collect().toArray()
			
			println " ***  Cod. Cliente ---> "+  CTSEnvironment.bvCompanyEnte
			println " ***  Cliente ---> "+  CTSEnvironment.bvCompanyLogin
			
			for (var in oreturnResponse) {
				println " ***  Login asociado ---> "+var.name
			}
			}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
}


