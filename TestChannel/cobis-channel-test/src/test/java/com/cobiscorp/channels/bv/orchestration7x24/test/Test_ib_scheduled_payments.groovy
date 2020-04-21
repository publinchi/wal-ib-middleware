package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionEnvironment;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentServices;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ScheduledPayment
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

class Test_ib_scheduled_payments {
	@ClassRule
    public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
    static VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
    def initSession    
    @Before
    void setUp(){
	    println "prueba Inicio de Sesion"
	    initSession= virtualBankingBase.initSessionNatural()
	    println "**** sesion **** ---> ${initSession}"                    
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
    * Execute Payment
    */
	@Test
	void testPayService() {
	   def ServiceName='testPayService'
	   try{                                 
	      println "Test ---> ${ServiceName}"
	      // Preparo ejecución del servicio
	      ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
	      serviceRequestTO.setSessionId(initSession)
	      println "sesion ---> ${initSession}"
	      //serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.SchedulePayment.AddScheduledPaymentToThirdParty')
		  //serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.SchedulePayment.AddScheduledPaymentToLoan')
		  serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.SchedulePayment.AddScheduledServicePayment')
	
		  //DTO IN
		  User inUser = new User();
		  ScheduledPayment inScheduledPayment = new ScheduledPayment();
		  TransactionEnvironment inTransactionEnvironment = new TransactionEnvironment();
		  Product inProduct = new Product();
		  PaymentServices inPaymentServices = new PaymentServices();	  
	      		  
		  inUser.setEntityId(277);
	      inTransactionEnvironment.setUserName("testCts");
		  inTransactionEnvironment.setAuthorizationRequired("S".charAt(0));
		  //inScheduledPayment.setId(1);
		  inScheduledPayment.setProductId(4);
		  inScheduledPayment.setProductId(4);
		  inScheduledPayment.setCurrencyId(0);
		  inScheduledPayment.setCreditAccount("10410108275249013");
		  inScheduledPayment.setProductNumber("10410108275249013");
		  inScheduledPayment.setCreditCurrencyId(0);
		  inScheduledPayment.setCreditProductId(4);
		  inScheduledPayment.setAmount(200.36);
		  inScheduledPayment.setInitialDate("03/05/2015");
		  //inScheduledPayment.setInitialDay(3);
		  //inScheduledPayment.setInitialMonth(5);
		  //inScheduledPayment.setInitialYear(2015);
		  inScheduledPayment.setPaymentsNumber(3);
		  inScheduledPayment.setFrequency("90");
		  inScheduledPayment.setConcept("Pago programado hsalazar");
		  //inScheduledPayment.setStatusId("V");
		  inScheduledPayment.setThirdPartyName("Diana Valverde");
		  inScheduledPayment.setBeneficiaryName("Henry Salazar Lopez");
		  inScheduledPayment.setCode(1);
		  inScheduledPayment.setReceiveNotification("S");
		  inScheduledPayment.setDayToNotify(5);
		  inScheduledPayment.setRecoveryRetryFailed("S");
		  inScheduledPayment.setNextPaymentDate("03/05/2015");
		  
		  inProduct.setProductAbbreviation("AHO");
		  
		  /****inicio parametros pago programado de servicios****/
		  inProduct.setProductNumber("10410108275249013");		  
          //inPaymentServices.setAmount(1234.45);
          inPaymentServices.setContractName("CABLETICA");
          inPaymentServices.setContractId(60);
          //inPaymentServices.setDocumentType();
          //inPaymentServices.setThridPartyServiceKey();
          inPaymentServices.setDocumentId("0106800940");
          inPaymentServices.setRef1("001");
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
		  
		  /****fin parametros pago programado de servicios****/
		  
		  serviceRequestTO.addValue('inUser', inUser);
		  serviceRequestTO.addValue('inTransactionEnvironment', inTransactionEnvironment);
		  serviceRequestTO.addValue('inScheduledPayment', inScheduledPayment);          
          serviceRequestTO.addValue('inProduct', inProduct);
		  

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

	   }catch(Exception e){
	      def msg=e.message
	      println "${e}"
		  println "${ServiceName} Exception--> ${msg}"
	      virtualBankingBase.closeSessionNatural(initSession)
	      Assert.fail()
	  }
   }

}
