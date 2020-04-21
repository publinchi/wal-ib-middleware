package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Bank
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.BankCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.CityCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Country
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.CountryCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DestinationAccount
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DestinationAccountCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.ExecutiveCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.IdentificationCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LotDestination
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.NotificationDetail
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.OfficeCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.OfficeLocationCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Parameter
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Transaction
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil


/***
 * Namespace:COBISCorp.eCOBIS.InternetBanking.WebApp.Admin.Service.Service
 * @author schancay
 *
 */
class TestAdminService {
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

		println "cerrando........."
		//BDD.close();

	}

	/**
	 * Modulo: Swift
	 */

	@Test
	void testGetBanksByCountry(){
		String ServiceName='GetBanksByCountry'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Swift.GetBanksByCountry')

			//DTO IN
			Country wCountry = new Country()
			wCountry.code=4
			SearchOption wSearch = new SearchOption()
			wSearch.mode=0

			//DTO OUT
			BankCollection oBankCollection=new BankCollection()

			serviceRequestTO.addValue('inCountry', wCountry)
			serviceRequestTO.addValue('inSearchOption', wSearch)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oBankCollection.banks = serviceResponseTO.getData().get('returnBank')
			Assert.assertTrue('Filas Vacias', oBankCollection.banks.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetCities(){
		String ServiceName='GetCities'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Swift.GetCities')

			//DTO IN
			SearchOption wSearch = new SearchOption()
			wSearch.criteria ='AMS'
			wSearch.finalCheck=12

			//DTO OUT
			CityCollection oCityCollection=new CityCollection()

			serviceRequestTO.addValue('inSearchOption', wSearch)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCityCollection.cities = serviceResponseTO.getData().get('returnCity')
			Assert.assertTrue('Filas Vacias', oCityCollection.cities.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetCountries(){
		String ServiceName='GetCountries'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Swift.GetCountries')

			//DTO IN
			SearchOption wSearch = new SearchOption()
			wSearch.lastResult='AFGANISTAN'

			//DTO OUT
			CountryCollection oCountryCollection=new CountryCollection()

			serviceRequestTO.addValue('inSearchOption', wSearch)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCountryCollection.country = serviceResponseTO.getData().get('returnCountry')
			Assert.assertTrue('Filas Vacias', oCountryCollection.country.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetOfficeBySwiftCode(){
		String ServiceName='GetOfficeBySwiftCode'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			cobiscorp.ecobis.commons.dto.ServiceRequestTO serviceRequestTO = new cobiscorp.ecobis.commons.dto.ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Swift.GetOfficeBySwiftCode')

			//DTO IN
			Parameter wParameter = new Parameter()
			wParameter.parameterName ='SWIFT'
			wParameter.name='SCBLAFKAXXX'

			//DTO OUT
			OfficeLocationCollection oOfficeLocationCollection=new OfficeLocationCollection()
			serviceRequestTO.addValue('inParameter', wParameter)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oOfficeLocationCollection.country = serviceResponseTO.getData().get('returnCountry')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.country.collect().size()>0)
			oOfficeLocationCollection.bank = serviceResponseTO.getData().get('returnBank')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.bank.collect().size()>0)
			oOfficeLocationCollection.office = serviceResponseTO.getData().get('returnOffice')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.office.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetOfficeByABACode(){
		String ServiceName='GetOfficeBySwiftCode'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			cobiscorp.ecobis.commons.dto.ServiceRequestTO serviceRequestTO = new cobiscorp.ecobis.commons.dto.ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Swift.GetOfficeBySwiftCode')

			//DTO IN
			Parameter wParameter = new Parameter()
			wParameter.parameterName ='ABA'
			wParameter.name='33'

			//DTO OUT
			OfficeLocationCollection oOfficeLocationCollection=new OfficeLocationCollection()
			serviceRequestTO.addValue('inParameter', wParameter)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oOfficeLocationCollection.country = serviceResponseTO.getData().get('returnCountry')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.country.collect().size()>0)
			oOfficeLocationCollection.bank = serviceResponseTO.getData().get('returnBank')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.bank.collect().size()>0)
			oOfficeLocationCollection.office = serviceResponseTO.getData().get('returnOffice')
			Assert.assertTrue('Filas Vacias', oOfficeLocationCollection.office.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetOfficesByBank(){
		String ServiceName='GetOfficesByBank'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Swift.GetOfficesByBank')

			//DTO IN
			SearchOption wSearch = new SearchOption()
			wSearch.criteria ='S'
			wSearch.lastResult='1'
			Country wCountry = new Country()
			wCountry.code =4
			Bank wBank = new Bank()
			wBank.id =13

			//DTO OUT
			OfficeCollection oOfficeCollection=new OfficeCollection()

			serviceRequestTO.addValue('inSearchOption', wSearch)
			serviceRequestTO.addValue('inCountry', wCountry)
			serviceRequestTO.addValue('inBank', wBank)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oOfficeCollection.office = serviceResponseTO.getData().get('returnOffice')
			Assert.assertTrue('Filas Vacias', oOfficeCollection.office.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: Transaction
	 */

	@Test
	void testGetThirdPartyTransactionCost(){
		String ServiceName='GetThirdPartyTransactionCost'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Transaction.GetThirdPartyTransactionCost')

			//DTO IN
			Transaction wTransaction = new Transaction()
			wTransaction.accountNumber=CTSEnvironment.bvAccCtaCteNumber
			wTransaction.accountType=CTSEnvironment.bvAccCtaCteType
			wTransaction.money=CTSEnvironment.bvAccCtaCteCurrencyId
			wTransaction.id=18862
			wTransaction.serviceId=CTSEnvironment.bvService
			wTransaction.entryId=CTSEnvironment.bvEnte
			wTransaction.clientId=0

			//DTO OUT

			serviceRequestTO.addValue('inTransaction', wTransaction)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: Identification
	 */

	@Test
	void testGetMasksCompany(){
		String ServiceName='GetMasksCompany'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Identification.GetMasksCompany')

			//DTO OUT
			IdentificationCollection oIdentificationCollection=new IdentificationCollection();

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oIdentificationCollection.identifications = serviceResponseTO.getData().get('returnIdentification')
			Assert.assertTrue('Filas Vacias', oIdentificationCollection.identifications.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetMasks(){
		String ServiceName='GetMasks'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Identification.GetMasks')

			//DTO OUT
			IdentificationCollection oIdentificationCollection=new IdentificationCollection();

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oIdentificationCollection.identifications = serviceResponseTO.getData().get('returnIdentification')
			Assert.assertTrue('Filas Vacias', oIdentificationCollection.identifications.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: Destination
	 */

	@Test
	void testValidateInternalDestination(){
		String ServiceName='ValidateInternalDestination'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.ValidateInternalDestination')

			//DTO IN
			DestinationAccount wDestinationAccount = new DestinationAccount()
			wDestinationAccount.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wDestinationAccount.productId=CTSEnvironment.bvAccCtaCteType
			wDestinationAccount.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			LotDestination wLotDestination = new LotDestination()
			wLotDestination.userName=CTSEnvironment.bvLogin

			//DTO OUT
			DestinationAccountCollection oDestinationAccountCollection=new DestinationAccountCollection();

			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)
			serviceRequestTO.addValue('inLotDestination', wLotDestination)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oDestinationAccountCollection.destinationAccounts = serviceResponseTO.getData().get('returnDestinationAccount')
			Assert.assertTrue('Filas Vacias', oDestinationAccountCollection.destinationAccounts.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}
	/**
	 * Modulo: Notification
	 */

	@Test
	void testRegisterNotification(){
		String ServiceName='RegisterNotification'
		try{
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null

			sql.execute("delete from cob_bvirtual..bv_notificaciones_despacho where nd_var5 like '%Comprobante: 1234567890%'")

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Notification.RegisterNotification')

			//DTO IN
			User wUser= new User()
			wUser.customerId=13036
			wUser.serviceId=1
			wUser.name='testCts'

			Notification wNotification=new Notification()
			wNotification.productId=4
			wNotification.productNumber='10410000005233616'
			wNotification.type='F'

			NotificationDetail wNotificationDetail=new NotificationDetail()
			wNotificationDetail.transaccionId=1800009
			wNotificationDetail.emailClient='eortega@cobiscorp.com'
			wNotificationDetail.emailOficial='schancay@cobiscorp.com'
			wNotificationDetail.dateNotification='05/02/2013'
			wNotificationDetail.reference='1234567890'
			wNotificationDetail.accountNumberDebit='10410000005233616'
			wNotificationDetail.accountNumberCredit='10410108275407019'
			wNotificationDetail.currencyId1='0'
			wNotificationDetail.cost1='0'
			wNotificationDetail.value='100'
			wNotificationDetail.auxiliary1='CTA TESTPRUEBA'
			wNotificationDetail.note='PRUEBA TEST SERVICIO DESACOPLADO'

			//DTO OUT
			ExecutiveCollection oExecutiveCollection=new ExecutiveCollection();

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inNotification', wNotification)
			serviceRequestTO.addValue('inNotificationDetail', wNotificationDetail)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			wRowDestino = sql.rows("select * from cob_bvirtual..bv_notificaciones_despacho where nd_var5 like '%Comprobante: 1234567890%'")
			println '>>>>>>>>>>>>>>>>REGISTROS:'+wRowDestino
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 1)


		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: NotificationExecutive
	 */

	@Test
	void testGetExecutives(){
		String ServiceName='GetExecutives'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.NotificationExecutive.GetExecutives')

			//DTO IN
			Client wClient = new Client()
			wClient.entityId=CTSEnvironment.bvEnte

			//DTO OUT
			ExecutiveCollection oExecutiveCollection=new ExecutiveCollection();

			serviceRequestTO.addValue('inClient', wClient)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oExecutiveCollection.executives = serviceResponseTO.getData().get('returnExecutive')
			Assert.assertTrue('Filas Vacias', oExecutiveCollection.executives.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}
}
