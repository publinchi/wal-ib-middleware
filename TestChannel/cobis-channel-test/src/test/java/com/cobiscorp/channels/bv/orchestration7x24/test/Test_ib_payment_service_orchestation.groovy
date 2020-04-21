package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentServices

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/***
 * orchestration-core-ib-query-accounts
 *
 * @author eortega
 *
 */
class Test_ib_payment_service_orchestation {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	@Before
	void setUp(){
		println "prueba Inicio de Sesion"
		initSession= virtualBankingBase.initSessionNatural()
		println "sesionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn ---> ${initSession}"
		
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
	 * Pago de servicios
	 */
//	@Test
//	void testPayService() {
//		def ServiceName='testPayService'
//		try{
//			//initSession= virtualBankingBase.initSessionNatural()
//			println "Test ---> ${ServiceName}"
//			// Preparo ejecución del servicio
//			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
//			serviceRequestTO.setSessionId(initSession)
//			println "sesionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn ---> ${initSession}"
//			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.PayService')
//
//			//DTO IN
//			User inUser = new User();
//			Product inProduct = new Product();
//			PaymentServices inPaymentServices = new PaymentServices();
//			TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();
//			inTransactionContextCIB.setAuthenticationRequired("S");
//			//datos del usuario
//			
//			inUser.setEntityId(CTSEnvironment.bvEnte);
//			inUser.setName(CTSEnvironment.bvLogin);
//			inUser.setServiceName("1");
//			
//			//datos de la cuenta
////			inProduct.setProductNumber(CTSEnvironment.bvAccCtaAhoNumber);
////			inProduct.setCurrencyId(CTSEnvironment.bvAccCtaAhoCurrencyId);
////			inProduct.setProductId(CTSEnvironment.bvAccCtaAhoType);
////
//			inProduct.setProductNumber("10410108275249013");
//			inProduct.setCurrencyId(0);
//			inProduct.setProductId(4);
//			
//			
//			
//			//datos del pago de servicio
//			
//			//inPaymentService.setNeedsQuery();
//			inPaymentServices.setAmount(1234.45);
//			inPaymentServices.setContractName("BARRAS");
//			inPaymentServices.setContractId(1000);
//			//inPaymentService.setDocumentType();
//			//inPaymentService.setThridPartyServiceKey();
//			inPaymentServices.setDocumentId("1234567890");
//			inPaymentServices.setRef1("");
//			inPaymentServices.setRef2("");
//			inPaymentServices.setRef3("");
//			inPaymentServices.setRef4("");
//			inPaymentServices.setRef5("");
//			inPaymentServices.setRef6("");
//			inPaymentServices.setRef7("");
//			inPaymentServices.setRef8("");
//			inPaymentServices.setRef10("");
//			inPaymentServices.setRef11("");
//			inPaymentServices.setRef12("");
//			inPaymentServices.setInvoicingBaseId(1000);
//			inPaymentServices.setInterface_type("B".charAt(0));
//			inPaymentServices.setDocumentType("1.1");
//			inPaymentServices.setThridPartyServiceKey("1");
//			inPaymentServices.setNeedsQuery("N");
//			
//			serviceRequestTO.addValue('inPaymentServices', inPaymentServices);
//			serviceRequestTO.addValue('inUser', inUser);
//			serviceRequestTO.addValue('inProduct', inProduct);
//			serviceRequestTO.addValue('inTransactionContextCIB', inTransactionContextCIB);
//
//			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
//
//			//Valido si fue exitoso la ejecucion
//			def message=''
//			if (serviceResponseTO.messages.toList().size()>0){
//				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
//			}
//			Assert.assertTrue(message, serviceResponseTO.success)
//
//
//			def wO_referencia_com = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch")
//			Assert.assertNotNull("No se obtuvo valor de la referencia",wO_referencia_com)
//			println ('RESPUESTA: @o_referencia----->' + wO_referencia_com)
//
////			def wO_cotizacion_ven = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion_ven")
////			Assert.assertNotNull("No se obtuvo valor de compra",wO_cotizacion_ven)
////			println ('RESPUESTA: @o_cotizacion_com----->' + wO_cotizacion_ven)
//
//		}catch(Exception e){
//			def msg=e.message
//			println "${e}"
//			println "${ServiceName} Exception--> ${msg}"
//			virtualBankingBase.closeSessionNatural(initSession)
//			Assert.fail()
//		}
//	}
	@Test
	void testPayService() {
		   def ServiceName='testPayService'
		   try{
				  //initSession= virtualBankingBase.initSessionNatural()
				  println "Test ---> ${ServiceName}"
				  // Preparo ejecución del servicio
				  ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
				  serviceRequestTO.setSessionId(initSession)
				  println "sesionnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn ---> ${initSession}"
				  serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.PayService')

				  //DTO IN
				  User inUser = new User();
				  Product inProduct = new Product();
				  PaymentServices inPaymentServices = new PaymentServices();
				  TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();
				  inTransactionContextCIB.setAuthenticationRequired("S");
				  //datos del usuario
				  
				  inUser.setEntityId(CTSEnvironment.bvEnteMis);
				  inUser.setName(CTSEnvironment.bvLogin);
				  inUser.setServiceName("1");
				  
				  //datos de la cuenta
//                   inProduct.setProductNumber(CTSEnvironment.bvAccCtaAhoNumber);
//                   inProduct.setCurrencyId(CTSEnvironment.bvAccCtaAhoCurrencyId);
//                   inProduct.setProductId(CTSEnvironment.bvAccCtaAhoType);
//
				  inProduct.setProductNumber("10410108275249013");
				  inProduct.setCurrencyId(0);
				  inProduct.setProductId(4);
				  
				  
				  
				  //datos del pago de servicio
				  
				  //inPaymentService.setNeedsQuery();
				  inPaymentServices.setAmount(1234.45);
				  inPaymentServices.setContractName("CABLETICA");
				  inPaymentServices.setContractId(60);
				  //inPaymentService.setDocumentType();
				  //inPaymentService.setThridPartyServiceKey();
				  inPaymentServices.setDocumentId("0106800940");
				  inPaymentServices.setRef1("");
				  inPaymentServices.setRef2("");
				  inPaymentServices.setRef3("");
				  inPaymentServices.setRef4("");
				  inPaymentServices.setRef5("");
				  inPaymentServices.setRef6("");
				  inPaymentServices.setRef7("");
				  inPaymentServices.setRef8("");
				  inPaymentServices.setRef10("");
				  inPaymentServices.setRef11("");
				  inPaymentServices.setRef12("");
				  inPaymentServices.setInvoicingBaseId(2);
				  inPaymentServices.setInterface_type("B".charAt(0));
				  inPaymentServices.setDocumentType("1.1");
				  inPaymentServices.setThridPartyServiceKey("1");
				  inPaymentServices.setNeedsQuery("N");
				  
				  serviceRequestTO.addValue('inPaymentServices', inPaymentServices);
				  serviceRequestTO.addValue('inUser', inUser);
				  serviceRequestTO.addValue('inProduct', inProduct);
				  serviceRequestTO.addValue('inTransactionContextCIB', inTransactionContextCIB);

				  ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);

				  //Valido si fue exitoso la ejecucion
				  def message=''
				  if (serviceResponseTO.messages.toList().size()>0){
						message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				  }
				  Assert.assertTrue(message, serviceResponseTO.success)


				  def wO_referencia_com = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch")
				  Assert.assertNotNull("No se obtuvo valor de la referencia",wO_referencia_com)
				  println ('RESPUESTA: @o_referencia----->' + wO_referencia_com)

//                   def wO_cotizacion_ven = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion_ven")
//                   Assert.assertNotNull("No se obtuvo valor de compra",wO_cotizacion_ven)
//                   println ('RESPUESTA: @o_cotizacion_com----->' + wO_cotizacion_ven)

		   }catch(Exception e){
				  def msg=e.message
				  println "${e}"
				  println "${ServiceName} Exception--> ${msg}"
				  virtualBankingBase.closeSessionNatural(initSession)
				  Assert.fail()
		   }
	}


}