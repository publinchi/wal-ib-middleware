package com.cobiscorp.channels.bv.orchestration7x24.test

import java.lang.ProcessEnvironment.CheckedEntry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CheckbookType
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CheckbookRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ManagerCheck;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jmoreta
 *
 */
class Test_ib_checkbook_types_checkbook_query {
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
	 * Metodo que ejecuta el servicio de solicitud de chequera
	 */
	@Test
	void testGetTypesOfCheckbook() {
		println ' ****** Prueba Regresión testGetTypesOfCheckbook Persona Natural************* '
		def ServiceName= 'getTypesOfCheckbook'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Checkbook.GetTypesOfCheckbook');

			EnquiryRequest wEnquiryRequest = new EnquiryRequest();
			//ManagerCheck wManagerCheck= new ManagerCheck();
			
			wEnquiryRequest.currencyId = CTSEnvironment.bvAccDpfCurrencyId;//@i_moneda
			//wEnquiryRequest.operation= 'S';//@i_operacion
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success);
			CheckbookType[] oCheckbookTypeResponse = serviceResponseTO.data.get('returnCheckbookType').collect().toArray();
			
			println "******** RESULSET *********";
			for (var in oCheckbookTypeResponse) {
			/*	Assert.assertNotNull("IdType is null ",var.idType);
				Assert.assertNotNull("Name is null ",var.name);
				Assert.assertNotNull("Type is null ",var.type);
				Assert.assertNotNull("Art is null ",var.art);
				Assert.assertNotNull("CustomArt is null ",var.customArt);
				Assert.assertNotNull("Quantity is null ",var.quantity);
				Assert.assertNotNull("State is null ",var.state);
				Assert.assertNotNull("Time is null ",var.time);
				Assert.assertNotNull("CurrencyId is null ",var.currencyId);
				Assert.assertNotNull("Amount is null ",var.amount);
			*/		
				println "******** RESULSET getTypesOfCheckbook *********";
				println "IDTYPE: "+  var.idType;
				println "NAME: "+   var.name;
				println "TYPE: "+ var.type;
				println "ART: "+  var.art;
				println "CUSTOMART: "+  var.customArt;
				println "QUANTITY: "+  var.quantity;
				println "STATE: "+  var.state;
				println "TIME: "+  var.time;
				println "CURRENCYID: "+  var.currencyId;
				println "AMOUNT: "+  var.amount;
			}
			
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
