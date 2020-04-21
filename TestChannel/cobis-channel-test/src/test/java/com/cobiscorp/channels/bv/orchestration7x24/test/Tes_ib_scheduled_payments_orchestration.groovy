/**
 * 
 */
package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Identification;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jveloz
 *
 */
class Tes_ib_scheduled_payments_orchestration {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	/**
	 *
	 */
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
		virtualBankingBase.closeSessionNatural(initSession);
	}

	/**
	 * Metodo que ejecuta el la solicitud de cheque de gerencia
	 */
	@Test
	void testMask() {
		println ' ****** Prueba Regresión testMask Persona Natural************* '
		def ServiceName= 'testMask'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Identification.GetMasks');

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			Identification[] oResponse= serviceResponseTO.data.get('returnIdentification').collect().toArray();
			println "******** Resulset *********"
			for (var in oResponse) {
				
			
			Assert.assertNotNull("Type is null ",var.type);
			Assert.assertNotNull("Name is null ",var.name);
			Assert.assertNotNull("Mask is null ",var.mask);
			Assert.assertNotNull("Customer Type is null ",var.customerType);
			Assert.assertNotNull("Province Validate is null ",var.provinceValidate);
			Assert.assertNotNull("Quick Opening is null ",var.quickOpening);
			Assert.assertNotNull("Lock Customer is null ",var.lockCustomer);
			Assert.assertNotNull("Nationality is null ",var.nationality);
			Assert.assertNotNull("Check Sum is null ",var.checkSum);
			//*******************************************************
			println "Type ",+var.type;
			println "Name ",+var.name;
			println "Mask ",+var.mask;
			println "Customer Type ",+var.customerType;
			println "Province Validate ",+var.provinceValidate;
			println "Quick Opening ",+var.quickOpening;
			println "Lock Customer ",+var.lockCustomer;
			println "Nationality ",+var.nationality;
			println "Check Sum ",+var.checkSum;
			}
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
