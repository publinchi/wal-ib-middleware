package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavingsAccount
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavingsAccountCollection
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavingsResponse
import cobiscorp.ecobis.internetbanking.webapp.common.dto.SavingsDetailCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.ProgrammedSavings.Service.Service
 * @author schancay
 *
 */
class TestProgrammedSavingsService {
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
	 * Modulo: ProgrammedSavingsOperations 
	 */

	@Test
	void testProgrammedSavingsAccount(){
		String ServiceName='ProgrammedSavingsAccount'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.ProgrammedSavingsAccount')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.mode=0
			User wUser=new User()
			wUser.customerId=CTSEnvironment.bvEnte

			//DTO OUT
			ProgrammedSavingsAccountCollection oProgrammedSavingsAccountCollection=new ProgrammedSavingsAccountCollection()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oProgrammedSavingsAccountCollection.programmedSavingsAccount = serviceResponseTO.getData().get('returnProgrammedSavingsAccount')
			Assert.assertTrue('Filas Vacias', oProgrammedSavingsAccountCollection.programmedSavingsAccount.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testExpirationDate(){
		String ServiceName='ExpirationDate'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.ExpirationDate')

			//DTO IN
			ProgrammedSavingsAccount wProgrammedSavingsAccount=new ProgrammedSavingsAccount()

			//DTO OUT
			ProgrammedSavingsResponse oProgrammedSavingsResponse=new ProgrammedSavingsResponse()

			serviceRequestTO.addValue('inProgrammedSavingsAccount', wProgrammedSavingsAccount)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			/*
			 //Valido que el objeto no sea null
			 Map<String, Object> objOut=serviceResponseTO.getData().get('com.cobiscorp.cobis.cts.service.response.output')
			 oProgrammedSavingsResponse.expirationDateCalc = objOut.get('@o_fecha_ven')
			 Assert.assertNotNull('Objeto Vacio', oProgrammedSavingsResponse.expirationDateCalc)
			 */
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetMinimumAmount(){
		String ServiceName='GetMinimumAmount'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.GetMinimumAmount')

			//DTO IN

			//DTO OUT
			ProgrammedSavingsResponse oProgrammedSavingsResponse=new ProgrammedSavingsResponse()

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que el objeto no sea null
			oProgrammedSavingsResponse.minimumAmount = serviceResponseTO.getData().get('@o_monto_min')
			Assert.assertNotNull('Objeto Vacio', oProgrammedSavingsResponse.minimumAmount)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetProgrammedSavings(){
		String ServiceName='GetProgrammedSavings'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.GetProgrammedSavings')

			//DTO IN
			Product wProduct=new Product()
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber

			//DTO OUT
			SavingsDetailCollection oSavingsDetailCollection=new SavingsDetailCollection()

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSavingsDetailCollection.savingsDetail = serviceResponseTO.getData().get('returnProgrammedSavingsDetail')
			Assert.assertTrue('Filas Vacias', oSavingsDetailCollection.savingsDetail.collect().size()>=0)

			//Valido que el objeto no sea null
			oSavingsDetailCollection.currencyDescription = serviceResponseTO.getData().get('@o_descripcion_moneda')
			/*
			 //Valido que el objeto no sea null
			 oSavingsDetailCollection.accountName = serviceResponseTO.getData().get('@o_nombre_cta')
			 Assert.assertNotNull('Objeto Vacio', oSavingsDetailCollection.accountName)
			 //Valido que el objeto no sea null
			 oSavingsDetailCollection.accountType = serviceResponseTO.getData().get('@o_tipo_cta')
			 Assert.assertNotNull('Objeto Vacio', oSavingsDetailCollection.accountType)
			 */
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
