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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DeliveryMethod;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.NotificationDelivery;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.ProductNotification;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jveloz
 *
 */
class Test_ib_group_management_notification_query {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections();
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup();
	}
	
	/**
	 * Metodo que ejecuta el servicio Consulta de Movimientos
	 */
	@Test
	void testMeanNotifications(){
		println ' ****** Prueba Regresión testManagementNotifications Grupo ************* '
		def ServiceName = 'ManagementNotifications'
		String method=new String();
		method='ProductNotificationDelivery';
		//ProductNotification
		//ProductNotificationDelivery
		//Edit
		try {
			String wOperacion= new String();
			//A-->Asociado
			//D-->Disponible
			wOperacion="D";
			//I-->Insert
			//D-->Delete

			String message='';
			def codeError='';
			/*-----------------*/
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			NotificationDelivery wNotificationDelivery= new NotificationDelivery();
			ProductNotification wProductNotification=new  ProductNotification();
			Notification wNotification = new Notification();
			if (method.equals("Edit")){
				serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.ProductNotificationDelivery.EditParam');
				wProductNotification.productId=CTSEnvironment.bvGroupAccCtaAhoType;//@i_prod;
				wProductNotification.userName=CTSEnvironment.bvGroupLogin;//@i_login;
				wProductNotification.productNumber=CTSEnvironment.bvGroupAccCtaAhoNumber;//@i_cta;
				wProductNotification.currencyId=CTSEnvironment.bvGroupAccCtaAhoCurrencyId;//@i_moneda;
				wProductNotification.notificationId="N19";//N30
				wNotification.moneyValue=589.00;
				wNotification.condition=">=";
			}else{
				if(wOperacion.equals("I")){
					/*****AddProduct****/
					if (method.equals('ProductNotification'))
					{
						serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.ProductNotificationDelivery.AddProductNotification');
					}
					if (method.equals('ProductNotificationDelivery'))
					{
						serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.ProductNotificationDelivery.AddProductNotificationDelivery');
						wNotificationDelivery.deliveryMethodId=CTSEnvironment.bvGroupEnte;//277 natural - 21 empresa
					}
				}
				if(wOperacion.equals("D")){
					/*****DeleteProduct****/
					if (method.equals('ProductNotification'))
					{
						serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.ProductNotificationDelivery.DeleteProductNotifications');
					}
					
					if (method.equals('ProductNotificationDelivery'))
					{
						serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.ProductNotificationDelivery.DeleteProductNotificationDelivery');
						wNotificationDelivery.deliveryMethodId=CTSEnvironment.bvGroupEnte;
					}
				}
				wNotificationDelivery.productId=CTSEnvironment.bvGroupAccCtaAhoType;//@i_prod; 4
				wNotificationDelivery.userName=CTSEnvironment.bvGroupLogin;//@i_login;
				wNotificationDelivery.productNumber=CTSEnvironment.bvGroupAccCtaAhoNumber;//@i_cta;
				wNotificationDelivery.notificationId="N19";//N19  N32
				wNotificationDelivery.currencyId=CTSEnvironment.bvGroupAccCtaAhoCurrencyId;//@i_moneda; 0
				
			}
			/*********************************************/
			println "userName "+CTSEnvironment.bvCompanyLogin;
			println "productId "+CTSEnvironment.bvCompanyAccCtaAhoType;
			println "productNumber "+CTSEnvironment.bvCompanyAccCtaAhoNumber;
			println "currencyId "+CTSEnvironment.bvCompanyAccCtaAhoCurrencyId;
			println "notificationId "+wNotificationDelivery.notificationId;
			println "deliveryMethodId "+wNotificationDelivery.deliveryMethodId;
			println "moneyValue "+wNotification.moneyValue;
			println "condition "+wNotification.condition;
			ServiceResponseTO serviceResponseTO;
			if(method.equals("Edit"))
				{
					serviceRequestTO.addValue('inProductNotification', wProductNotification);
					serviceRequestTO.addValue('inNotification', wNotification);
					serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
				}else{
				serviceRequestTO.addValue('inNotificationDelivery', wNotificationDelivery);
				serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
				}
			//Valido si fue exitoso la ejecucion
			Assert.assertTrue(message, serviceResponseTO.success);
			println "EJECUTADO CON EXITO"
			
		 } catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
