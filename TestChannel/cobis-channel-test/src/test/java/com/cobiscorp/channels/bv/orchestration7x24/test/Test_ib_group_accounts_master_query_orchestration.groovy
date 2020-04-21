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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.EntityServiceProduct
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
class Test_ib_group_accounts_master_query_orchestration {
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
	void testgetSelectionMasterAccount() {
		println ' ****** Prueba Regresión testgetSelectionMasterAccount Grupo************* '
		def ServiceName= 'getSelectionMasterAccount'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Account.SetPaymentAccount');

			EntityServiceProduct wEntityServiceProduct = new EntityServiceProduct();
			//ManagerCheck wManagerCheck= new ManagerCheck();
			
			wEntityServiceProduct.entityId = 295;// CTSEnvironment.bvEnte;//@i_cliente
			wEntityServiceProduct.productId= 3;//@i_producto
			wEntityServiceProduct.productNumber=CTSEnvironment.bvGroupAccCtaCteNumber;//@i_cuenta 
			wEntityServiceProduct.productAlias='ALIAS';//@i_alias
			wEntityServiceProduct.currencyId=CTSEnvironment.bvAccCtaAhoCurrencyId;//@i_moneda
			wEntityServiceProduct.serviceId=CTSEnvironment.bvService;//2;//@i_servicio			
			wEntityServiceProduct.userName=CTSEnvironment.bvGroupLogin;//@i_login			
			//wCheckbookRequest.entityId=277;//CTSEnvironment.bvCompanyEnte;//bvEnte;//@i_ente 
			//wCheckbookRequest.productAbbreviation='SB';//@i_producto
						
			serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct);		

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			//FixedTermDepositBalance[] oResponse= serviceResponseTO.data.get('returnFixedTermDepositBalance').collect().toArray();
			
			//def wTipoCheckbook = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tipo_chequera");
			
			println "******** RESULSET *********"
			println ('RESPUESTA: OK');
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
}
