package com.cobiscorp.channels.bv.orchestration7x24.test;
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.EntityServiceProduct
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

/**
 * 
 * @author gyagual
 *
 */

public class Test_ib_company_changeAlias_orchestration {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase();
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
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
	void testCompanyCCChangeAlias() {
		def ServiceName= 'testCompanyCCChangeAlias'
		try{
			println "Test ---> ${ServiceName}"
			
			GetInformationByClient(CTSEnvironment.bvService, CTSEnvironment.bvCompanyEnte, CTSEnvironment.bvCompanyAccCtaCteNumber, CTSEnvironment.bvCompanyLogin  )
			ChangeAlias(CTSEnvironment.bvCompanyAccCtaCteType, CTSEnvironment.bvCompanyAccCtaCteNumber, CTSEnvironment.bvCompanyAccCtaCteCurrencyId, CTSEnvironment.bvCompanyLogin, "CTA TESTPRUEBA_CC" )
			GetInformationByClient(CTSEnvironment.bvService, CTSEnvironment.bvCompanyEnte, CTSEnvironment.bvCompanyAccCtaCteNumber, CTSEnvironment.bvCompanyLogin  )
			
			}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
	
	@Test
	void testCompanyAhChangeAlias() {
		def ServiceName= 'testCompanyAhChangeAlias'
		try{
			println "Test ---> ${ServiceName}"
			
			GetInformationByClient(CTSEnvironment.bvService, CTSEnvironment.bvCompanyEnte, CTSEnvironment.bvCompanyAccCtaAhoNumber, CTSEnvironment.bvCompanyLogin )
			ChangeAlias(CTSEnvironment.bvCompanyAccCtaAhoType, CTSEnvironment.bvCompanyAccCtaAhoNumber, CTSEnvironment.bvCompanyAccCtaAhoCurrencyId, CTSEnvironment.bvCompanyLogin, "CTA TESTPRUEBA_AH" )
			GetInformationByClient(CTSEnvironment.bvService, CTSEnvironment.bvCompanyEnte, CTSEnvironment.bvCompanyAccCtaAhoNumber, CTSEnvironment.bvCompanyLogin )
			
			}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
	
	void ChangeAlias(Integer type, String AccountNumber, Integer AccountCurrency, String Login, String Alias){
		def MethodName= 'ChangeAlias'
	 
	try{
		
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.ChangeAlias')
	
			//DTO IN
			 
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			 
			wEnquiryRequest.productId = type
			wEnquiryRequest.productNumber = AccountNumber
			wEnquiryRequest.productAlias = Alias
			wEnquiryRequest.currencyId = AccountCurrency
			wEnquiryRequest.userName =  Login
			
		 
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
	 
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
	
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
	
			Assert.assertTrue(message, serviceResponseTO.success)
		}catch(Exception e){
			def msg=e.message
			println "${MethodName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}


	void GetInformationByClient(Integer Service, Integer Client, String AccountNumber, String Login) {
		def MethodName='GetInformationByClient'
		try{
				println "SERVICIO ->"+ Service
				println "CLIENTE ->"+ Client
				println "ACCOUNTNUMBER ->"+ AccountNumber
				println "LOGIN ->"+ Login
	
				def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
				def wAlias=null
				wAlias = sql.rows("select ep_alias from cob_bvirtual" + CTSEnvironment.DB_SEPARATOR +"bv_ente_servicio_producto where ep_cuenta= " +  AccountNumber +
										 " and ep_ente = " + Client + " and ep_servicio = " + Service+ " and ep_login = '" + Login + "'")
				Assert.assertEquals("No se encontro registro en la bv_ente_servicio_producto",wAlias.size(), 1)
				
				println ('RESPUESTA: wAlias----->'+ wAlias)
				
			}catch(Exception e){
			def msg=e.message
			println "${MethodName} Exception--> ${msg}"
			}
	}
}

 
