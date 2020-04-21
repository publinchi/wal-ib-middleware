package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.Assert
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Customer
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Address


class Test_ib_company_get_addres {

	
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
	void testGetAddress() {
		def ServiceName='testGetAddress'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Customer.Customer.GetAddress')
			
			//DTO IN
			Customer wCustomer = new Customer()
			wCustomer.entityId = CTSEnvironment.bvCompanyEnteMis //137488
			
			serviceRequestTO.addValue('inCustomer', wCustomer)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> Cliente-> " + wCustomer.entityId
			
			Address[] oResponseAddress= serviceResponseTO.data.get('returnAddress').collect().toArray()
			
			for (var in oResponseAddress) {
				Assert.assertNotNull("phone is null", var.phone)
				Assert.assertNotNull("neighborhood is null", var.neighborhood)
				Assert.assertNotNull("street is null", var.street)
				Assert.assertNotNull("description is null", var.description)
				Assert.assertNotNull("house is null", var.house)
				Assert.assertNotNull("email is null", var.email)
				Assert.assertNotNull("phoneId is null", var.phoneId)
				Assert.assertNotNull("id is null", var.id)
				Assert.assertNotNull("emailId is null", var.emailId)
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Phone ---> "+var.phone
				println " ***  Neighborhood ---> "+var.neighborhood
				println " ***  Street ---> "+var.street
				println " ***  Description ---> "+var.description
				println " ***  House ---> "+var.house
				println " ***  Email ---> "+var.email
				println " ***  PhoneId ---> "+var.phoneId
				println " ***  Id ---> "+var.id
				println " ***  Email Id ---> "+var.emailId
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}
}
