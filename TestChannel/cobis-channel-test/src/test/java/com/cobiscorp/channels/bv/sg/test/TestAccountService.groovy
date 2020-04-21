package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CheckCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CheckbookTypeCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ManagerCheckCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.NoPaycheckOrder

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Account.Service.Service
 * @author schancay
 *
 */
class TestAccountService{
	String initSession
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();

	@Before
	void setUp(){
		println 'Iniciando session'//Creo el usuario
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		//Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		//Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)


		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		//Eliminar session del login
		String wUser= CTSEnvironment.bvLogin
		sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_in_login where il_login=?',[wUser])
		sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_session where bv_usuario=?',[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);
		initSession = new VirtualBankingUtil()
				.initSession( CTSEnvironment.bvLogin, CTSEnvironment.bvPassword, CTSEnvironment.bvCulture)
	}
	@AfterClass
	static void   closeResources(){
		BDD.close();
	}

	@After
	void finallySession(){
		println 'Finalizando session'
		new VirtualBankingUtil()
				.finalizeSession(CTSEnvironment.bvLogin,initSession)
		// borro  el usuario
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())

		//borro el SP
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())

	}

	/**
	 * Modulo: Checkbook
	 */

	@Test
	void testGetTypesOfCheckbook(){
		String ServiceName='GetTypesOfCheckbook'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Checkbook.GetTypesOfCheckbook')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.transactionId=0
			wEnquiryRequest.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId

			//DTO OUT
			CheckbookTypeCollection oCheckbookTypeCollection=new CheckbookTypeCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCheckbookTypeCollection.checkbookTypes = serviceResponseTO.getData().get('returnCheckbookType')
			Assert.assertTrue('Filas Vacias', oCheckbookTypeCollection.checkbookTypes.collect().size()>0)


		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testValidateSuspendChecks(){
		//TODO Cambiar parametros de consulta cuando se hagan las UT de orquestaciones>>RequestCheckbook
		String ServiceName='ValidateSuspendChecks'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Checkbook.ValidateSuspendChecks')

			//DTO IN
			NoPaycheckOrder wNoPaycheckOrder=new NoPaycheckOrder()
			wNoPaycheckOrder.account=CTSEnvironment.bvAccCtaCteNumber
			wNoPaycheckOrder.initalCheck=0
			wNoPaycheckOrder.numberOfChecks=10

			//DTO OUT
			CheckCollection oCheckCollection=new CheckCollection()

			serviceRequestTO.addValue('inCheckCollection', wNoPaycheckOrder)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCheckCollection.checks = serviceResponseTO.getData().get('returnCheck')
			Assert.assertTrue('Filas Vacias', oCheckCollection.checks.collect().size()==0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: Account 
	 */

	@Test
	void testGetManagerCheckDetail(){
		//TODO Cambiar parametros de consulta cuando se hagan las UT de orquestaciones>>RequestCheckbook
		String ServiceName='GetManagerCheckDetail'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Account.Service.Account.GetManagerCheckDetail')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.generatesSequential='3234500'
			wEnquiryRequest.transactionDate=CTSEnvironment.bvProcessDate
			wEnquiryRequest.dateFormatId=103

			//DTO OUT
			ManagerCheckCollection oManagerCheckCollection=new ManagerCheckCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oManagerCheckCollection.managerChecks = serviceResponseTO.getData().get('returnManagerCheck')
			Assert.assertTrue('Filas Vacias', oManagerCheckCollection.managerChecks.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
