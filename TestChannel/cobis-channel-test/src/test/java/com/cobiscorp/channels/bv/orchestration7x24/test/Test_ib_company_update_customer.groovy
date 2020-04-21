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


class Test_ib_company_update_customer {

	
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
	void testUpdateCustomer() {
		def ServiceName='testUpdateCustomer'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Customer.Customer.UpdateCustomer')
			
			//DTO IN
			Customer wCustomer = new Customer()
			wCustomer.entityId = CTSEnvironment.bvCompanyEnteMis //137488
			wCustomer.phoneId = 1
			wCustomer.emailId = 1
			wCustomer.phone = CTSEnvironment.bvPhone
			wCustomer.email = CTSEnvironment.bvEmail
			
			Address wAddress = new Address()
			wAddress.id = 2
			wAddress.neighborhood = CTSEnvironment.bvNeighborhood
			wAddress.street = CTSEnvironment.bvStreet 
			wAddress.house = CTSEnvironment.bvHouse
			wAddress.description = "Description Company Test"
			
			serviceRequestTO.addValue('inCustomer', wCustomer)
			serviceRequestTO.addValue('inAddress', wAddress)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> Cliente-> " + wCustomer.entityId
			
			Customer[] oResponseCustomer= serviceResponseTO.data.get('returnCustomer').collect().toArray()
			
			for (var in oResponseCustomer) {
				Assert.assertNotNull("Additional Information is null", var.additionalInformation)
				Assert.assertNotNull("Name is null", var.name)
				Assert.assertNotNull("Phone is null", var.phone)
				Assert.assertNotNull("Address Information is null", var.addressInformation)
				Assert.assertNotNull("Email is null", var.email)
				
				println "**** --------- RESPUESTA ---------- ****"
				println " ***  Aditional Information ---> "+var.additionalInformation
				println " ***  Name ---> "+var.name
				println " ***  Phone ---> "+var.phone
				println " ***  Address ---> "+var.addressInformation
				println " ***  Email ---> "+var.email
			}
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
		}
	}

}
