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
class Test_ib_group_checkbook_request_checkbook_query {
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
	 * Metodo que ejecuta el servicio de solicitud de chequera
	 */
	@Test
	void testgetRequestCheckbook() {
		println ' ****** Prueba Regresión testgetRequestCheckbook Grupo************* '
		def ServiceName= 'getRequestCheckbook'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Checkbook.RequestCheckbook');

			CheckbookRequest wCheckbookRequest = new CheckbookRequest();
			//ManagerCheck wManagerCheck= new ManagerCheck();
			
			wCheckbookRequest.transactionId = 1800005; //t_trn
			wCheckbookRequest.operation= 'S';//@i_operacion
			wCheckbookRequest.account=CTSEnvironment.bvAccCtaCteUsdNumber;//bvAccCtaCteNumber;//@i_cta
			wCheckbookRequest.currency=CTSEnvironment.bvCompanyDpfCurrencyId;//bvAccDpfCurrencyId;//@i_mon
			wCheckbookRequest.typeCheckbook='0122';//@i_tchq
			wCheckbookRequest.numberOfChecks=50;//@i_nchq
			wCheckbookRequest.officeDelivery=1;//@i_ofientr
			wCheckbookRequest.userName=CTSEnvironment.bvGroupLogin;//@i_login
			wCheckbookRequest.productId=3;//CTSEnvironment.bvAccDpfPayableProductid;//@i_prod
			wCheckbookRequest.deliveryDay='10/10/2014';//@i_dia_entrega
			wCheckbookRequest.deliveyId='12321';//@i_id_entrega
			wCheckbookRequest.deliveryName='JEFFERSON';//@i_nombre_entrega
			wCheckbookRequest.checkbookArt='PRUEBA';//@i_nombre_arte
			wCheckbookRequest.type_id='1.2';//@i_tipo_id
			//wCheckbookRequest.entityId=277;//CTSEnvironment.bvCompanyEnte;//bvEnte;//@i_ente 
			//wCheckbookRequest.productAbbreviation='SB';//@i_producto
			//wCheckbookRequest.authorizationRequired='N';//S/N//@i_doble_autorizacion
			//wManagerCheck.amount = 0.0;
			
			serviceRequestTO.addValue('inCheckbookRequest', wCheckbookRequest);
			//serviceRequestTO.addValue('inManagerCheck', wManagerCheck);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			//FixedTermDepositBalance[] oResponse= serviceResponseTO.data.get('returnFixedTermDepositBalance').collect().toArray();
			
			def wTipoCheckbook = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tipo_chequera");
			def wReference = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia");
			/*def wAuthorizationRequired = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_autorizacion");			
			def wBranchSSN = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch");			
			def wConditionId = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_condicion");
			*/
			println "******** RESULSET *********"
			println ('RESPUESTA: @o_tipo_chequera----->' + wTipoCheckbook);
			println ('RESPUESTA: @o_tipo_chequera----->' + wReference);
			/*
			println ('RESPUESTA: @o_autorizacion----->' + wAuthorizationRequired);
			println ('RESPUESTA: @o_ssn_branch----->' + wBranchSSN);			
			println ('RESPUESTA: @o_condicion----->' + wConditionId);
			*/
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
}
