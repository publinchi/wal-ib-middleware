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
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.Context;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
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
class Test_ib_cashiers_check_aplication {
	
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
	void testCashiersCheckAplication() {
		println ' ****** Prueba Regresión testCashiersCheckAplication Persona Natural************* '
		def ServiceName= 'aplicationCashiersCheck'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Account.ManagerCheckRequest');
			
			TransactionRequest wTransactionRequest = new TransactionRequest();
			Product wProduct = new Product();
			ManagerCheck wManagerCheck= new ManagerCheck();
			Context wContext= new Context();
			
			wTransactionRequest.authorizationRequired="N";//S/N
			wTransactionRequest.userName=CTSEnvironment.bvLogin;//@i_login
			wProduct.productNumber=CTSEnvironment.bvAccCtaAhoNumber;//@i_cta bvAccCtaCteNumber bvAccCtaAhoNumber 
			wProduct.productAbbreviation="SB";
			wProduct.currencyId=CTSEnvironment.bvAccCtaAhoCurrencyId;//@i_mon bvAccCtaCteCurrencyId bvAccCtaAhoCurrencyId
			wProduct.productId=CTSEnvironment.bvAccCtaAhoType;//@i_product bvAccCtaCteType bvAccCtaAhoType
			wManagerCheck.amount=180.53;
			wManagerCheck.beneficiary="Jonathan Veloz Christian Natural";
			wManagerCheck.beneficiaryId="0924498892";
			wManagerCheck.beneficiaryTypeId=CTSEnvironment.bvAccCtaAhoBeneficiaryType;
			wManagerCheck.authorizedPhoneNumber=CTSEnvironment.bvPhone;
			wManagerCheck.destinationOfficeId=CTSEnvironment.bvAccCtaAhoOfficeId;
			wManagerCheck.authorizedTypeId="2";
			wManagerCheck.authorizedId="055555555";
			wManagerCheck.authorized="BENEFICIARIO CHEQUE DE GERENCIA";
			wManagerCheck.email=CTSEnvironment.bvEmail;
			wManagerCheck.purpose="Prueba Solicitud de Cheque de Gerencia Persona Natural";
			wContext.entityId=CTSEnvironment.bvEnteMis;//13036
			
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest);
			serviceRequestTO.addValue('inProduct', wProduct);
			serviceRequestTO.addValue('inManagerCheck', wManagerCheck);
			serviceRequestTO.addValue('inContext', wContext);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			//ResponseManagerCheck[] oResponse= serviceResponseTO.data.get('returnResponseManagerCheck').collect().toArray();
			def wAuthorizationRequired = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_autorizacion");
			def wBatchId = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_idlote");
			def wBranchSSN = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch");
			def wReference = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia");
			def wConditionId = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_condicion");
			
			//***********************//
			//Assert.assertNotNull("Authorization Required is null",wAuthorizationRequired);
			//Assert.assertNotNull("Batch Id Required is null",wBatchId);
			//Assert.assertNotNull("Branch SSN  Required is null",wBranchSSN);
			//Assert.assertNotNull("Reference Required is null",wReference);
			//Assert.assertNotNull("Condition Id Required is null",wConditionId);
			
			println "******** Output ManagerCheckRequest Natural*********"
			println ('RESPUESTA: @o_autorizacion----->' + wAuthorizationRequired);
			println ('RESPUESTA: @o_idlote----->' + wBatchId);
			println ('RESPUESTA: @o_ssn_branch----->' + wBranchSSN);
			println ('RESPUESTA: @o_referencia----->' + wReference);
			println ('RESPUESTA: @o_condicion----->' + wConditionId);
			
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
