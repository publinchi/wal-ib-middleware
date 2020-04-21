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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.Context;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.BeneficiaryCertificateDeposit;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDeposit;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ManagerCheck;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseManagerCheck;

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jveloz
 *
 */
class Test_ib_group_certificate_deposit_opening {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	/**
	 *
	 */
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession);
	}

	/**
	 * Metodo que ejecuta el la solicitud de cheque de gerencia
	 */
	@Test
	void testCertificateDepositOpening() {
		println ' ****** Prueba Regresión testCertificateDepositOpening Grupo************* '
		def ServiceName= 'aplicationOpenningCertificateDeposit'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.Payment.AddCertificateDeposit');
			
			User wUser= new User();
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB();
			Product wProduct = new Product();
			CertificateDeposit wCertificateDeposit= new CertificateDeposit();
			BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit=new BeneficiaryCertificateDeposit();
			
			wUser.entityId=CTSEnvironment.bvGroupEnteMis;//137488;
			wUser.name=CTSEnvironment.bvGroupLogin;//@i_login;
			wTransactionContextCIB.authenticationRequired="N";
			
			wProduct.productNumber=CTSEnvironment.bvGroupAccCtaAhoNumber;//@i_cta 10410000005233616
			wProduct.currencyId=CTSEnvironment.bvGroupAccCtaAhoCurrencyId;//@i_mon  0
			wProduct.productId=CTSEnvironment.bvGroupAccCtaAhoType;//@i_prod  4
			
			wCertificateDeposit.capitalize="MENSUAL";
			wCertificateDeposit.payDay=15;
			Calendar fecha = new GregorianCalendar(2014,05,05,00,00,00);
			wCertificateDeposit.processDate=fecha;
			wCertificateDeposit.methodOfPayment="TRJ";
			wCertificateDeposit.mail=CTSEnvironment.bvEmail;;
			wCertificateDeposit.amount=100000;
			wCertificateDeposit.nemonic="PERF1";
			wCertificateDeposit.office=CTSEnvironment.bvGroupAccCtaAhoOfficeId;// 1
			wCertificateDeposit.periodicityId="M";
			wCertificateDeposit.term=120;
			wCertificateDeposit.rate=8.0;
			
			wBeneficiaryCertificateDeposit.cedula1="0924498892";
			wBeneficiaryCertificateDeposit.name1="JONATHAN";
			wBeneficiaryCertificateDeposit.firstSurname1="VELOZ";
			wBeneficiaryCertificateDeposit.relation1="AMIGO";
			wBeneficiaryCertificateDeposit.percentage1=3.12;
			wBeneficiaryCertificateDeposit.lastSurname1="JORDAN";
			
			serviceRequestTO.addValue('inUser', wUser);
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB);
			serviceRequestTO.addValue('inProduct', wProduct);
			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit);
			serviceRequestTO.addValue('inBeneficiaryCertificateDeposit', wBeneficiaryCertificateDeposit);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			//ResponseManagerCheck[] oResponse= serviceResponseTO.data.get('returnResponseManagerCheck').collect().toArray();
			def wNumberOfPayment = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_retorno");
			
			//***********************//
			Assert.assertNotNull("Number Of Payment Required is null",wNumberOfPayment);
			//
			println "******** Output ManagerCheckRequest Grupo*********"
			println ('RESPUESTA: @o_retorno ----->' + wNumberOfPayment);
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
