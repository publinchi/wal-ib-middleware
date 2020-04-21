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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_group_mean_notification_query {

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
		println ' ****** Prueba Regresión testMeanNotifications ************* '
		def ServiceName = 'MeanNotifications'
		
		try {
			String wOperacion= new String();
			wOperacion="I"//--> insert;
			//wOperacion="U"//--> update;
			//wOperacion="D"//--> delete;

			String message='';
			def codeError='';
		
			if(wOperacion.equals("I")){
				
				/*****DeleteDeliveryMethod****/
				ServiceRequestTO serviceRequestTO_INSERT = new ServiceRequestTO();
				serviceRequestTO_INSERT.setSessionId(initSession);
				serviceRequestTO_INSERT.setServiceId('InternetBanking.WebApp.Admin.DeliveryMethod.AddDeliveryMethod');
		
				User iUser = new User();		
				DeliveryMethod iDeliveryMethod = new DeliveryMethod();
				
				iUser.name ='testCtsGrupo';
				iDeliveryMethod.typeId='MAIL';
				iDeliveryMethod.value='joel.chonillo@cobiscorp.com'
				//iDeliveryMethod. = ;

				serviceRequestTO_INSERT.addValue('inDeliveryMethod', iDeliveryMethod);
				serviceRequestTO_INSERT.addValue('inUser', iUser);
				
				ServiceResponseTO serviceResponseTO_INSERT = new VirtualBankingUtil().executeService(serviceRequestTO_INSERT);
		
				//Valido si fue exitoso la ejecucion
				if (serviceResponseTO_INSERT.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_INSERT.messages.toList().get(0)).message
					codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_INSERT.messages.toList().get(0)).code
				}
		
				Assert.assertTrue(message, serviceResponseTO_INSERT.success);

			}	
			
			if(wOperacion.equals("D")){
					
			/*****DeleteDeliveryMethod****/
			ServiceRequestTO serviceRequestTO_DELETE = new ServiceRequestTO();
			serviceRequestTO_DELETE.setSessionId(initSession);
			serviceRequestTO_DELETE.setServiceId('InternetBanking.WebApp.Admin.DeliveryMethod.DeleteDeliveryMethod');
			
			
			DeliveryMethod iDeliveryMethod = new DeliveryMethod();	
			iDeliveryMethod.id = 60331;

			serviceRequestTO_DELETE.addValue('inDeliveryMethod', iDeliveryMethod);
					
			ServiceResponseTO serviceResponseTO_DELETE = new VirtualBankingUtil().executeService(serviceRequestTO_DELETE);
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO_DELETE.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_DELETE.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_DELETE.messages.toList().get(0)).code
			}
			
			Assert.assertTrue(message, serviceResponseTO_DELETE.success)
	
			}
			
		if(wOperacion.equals("U")){
				
		/*****DeleteDeliveryMethod****/
		ServiceRequestTO serviceRequestTO_UPDATE = new ServiceRequestTO();
		serviceRequestTO_UPDATE.setSessionId(initSession);
		serviceRequestTO_UPDATE.setServiceId('InternetBanking.WebApp.Admin.DeliveryMethod.UpdateDeliveryMethod');
		
		DeliveryMethod iDeliveryMethod = new DeliveryMethod();
		User iUser = new User();
		
		iDeliveryMethod.id = 60330;
		iDeliveryMethod.typeId='MAIL';
		iDeliveryMethod.value='joel.chonillo2@cobiscorp.com'
		iUser.name ='testCtsGrupo';
		
		serviceRequestTO_UPDATE.addValue('inDeliveryMethod', iDeliveryMethod);
		serviceRequestTO_UPDATE.addValue('inUser', iUser);
				
		ServiceResponseTO serviceResponseTO_UPDATE = new VirtualBankingUtil().executeService(serviceRequestTO_UPDATE);
		
		//Valido si fue exitoso la ejecucion
		if (serviceResponseTO_UPDATE.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_UPDATE.messages.toList().get(0)).message
			codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO_UPDATE.messages.toList().get(0)).code
		}
		
		Assert.assertTrue(message, serviceResponseTO_UPDATE.success)

		}
		
			
 		} catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
	
}
