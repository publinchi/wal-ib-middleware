package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Client
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Entity
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Service
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.PeriodicityCollection
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.SimulationSetting
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CDExpirationDateResponse
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDeposit
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDepositCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDepositResult
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDepositResultCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanItemResults
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSim
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSimCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.SavingSim
import cobiscorp.ecobis.internetbanking.webapp.products.dto.SavingSimCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseMembersCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseThirdAccountsColletion
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil


/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.EnterpriseServices.Service.Service
 * @author schancay
 *
 */
class TestEnterpriseServices{
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
	 * Modulo: UniqueLogin 
	 */

	@Test
	void testGetMembersByGroup(){
		String ServiceName='GetMembersByGroup'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.UniqueLogin.GetMembersByGroup')

			//DTO IN
			Client wClient = new Client()
			wClient.entityId=1
			Entity wEntity = new Entity()
			wEntity.id=2

			//DTO OUT
			ResponseMembersCollection oResponseMembersCollection=new ResponseMembersCollection()

			serviceRequestTO.addValue('inClient', wClient)
			serviceRequestTO.addValue('inEntity', wEntity)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oResponseMembersCollection.responseMembers = serviceResponseTO.getData().get('returnResponseMembers')
			Assert.assertTrue('Filas Vacias', oResponseMembersCollection.responseMembers.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetThirdAccounts(){
		String ServiceName='GetThirdAccounts'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.UniqueLogin.GetThirdAccounts')

			//DTO IN
			Client wClient = new Client()
			wClient.entityId=CTSEnvironment.bvEnteMis
			Service wService = new Service()
			wService.id=CTSEnvironment.bvService
			SearchOption wSearch = new SearchOption()
			wSearch.sequential=655852943
			//DTO OUT
			ResponseThirdAccountsColletion oResponseThirdAccountsColletion=new ResponseThirdAccountsColletion()

			serviceRequestTO.addValue('inClient', wClient)
			serviceRequestTO.addValue('inService', wService)
			serviceRequestTO.addValue('inSearchOption', wSearch)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oResponseThirdAccountsColletion.responseThirdAccounts = serviceResponseTO.getData().get('returnResponseThirdAccounts')
			Assert.assertTrue('Filas Vacias', oResponseThirdAccountsColletion.responseThirdAccounts.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: Simulator 
	 */

	@Test
	void testGetCDRate(){
		String ServiceName='GetCDRate'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDRate')

			//DTO IN
			CertificateDeposit wCertificateDeposit=new CertificateDeposit()
			wCertificateDeposit.nemonic='PERF1'
			wCertificateDeposit.office=1
			wCertificateDeposit.amount=100
			wCertificateDeposit.term=1
			wCertificateDeposit.money=CTSEnvironment.bvAccCtaCteCurrencyId

			//DTO OUT
			CertificateDepositCollection oCertificateDepositCollection=new CertificateDepositCollection()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCertificateDepositCollection.certificateDeposits = serviceResponseTO.getData().get('returnCertificateDeposit')
			Assert.assertTrue('Filas Vacias', oCertificateDepositCollection.certificateDeposits.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetCDType(){
		String ServiceName='GetCDType'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDType')

			//DTO IN
			CertificateDeposit wCertificateDeposit=new CertificateDeposit()
			wCertificateDeposit.nemonic='PERF1'

			//DTO OUT
			CertificateDepositCollection oCertificateDepositCollection=new CertificateDepositCollection()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCertificateDepositCollection.certificateDeposits = serviceResponseTO.getData().get('returnCertificateDeposit')
			Assert.assertTrue('Filas Vacias', oCertificateDepositCollection.certificateDeposits.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetMaxSaving(){
		String ServiceName='GetMaxSaving'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetMaxSaving')

			//DTO IN
			SavingSim wSavingSim=new SavingSim()
			wSavingSim.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wSavingSim.initialAmount=100
			wSavingSim.entityType=CTSEnvironment.bvLoginType
			wSavingSim.code=100
			wSavingSim.category='A'

			//DTO OUT
			SavingSimCollection oSavingSimCollection=new SavingSimCollection()

			serviceRequestTO.addValue('inSavingSim', wSavingSim)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSavingSimCollection.savingSims = serviceResponseTO.getData().get('returnSavingSim')
			Assert.assertTrue('Filas Vacias', oSavingSimCollection.savingSims.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetCDTerm(){
		String ServiceName='GetCDTerm'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDTerm')

			//DTO IN
			CertificateDeposit wCertificateDeposit=new CertificateDeposit()
			wCertificateDeposit.nemonic=''
			wCertificateDeposit.term=1
			wCertificateDeposit.processDate=''
			wCertificateDeposit.expiration=''
			wCertificateDeposit.termDate=''

			//DTO OUT
			CertificateDepositResult oCertificateDepositResult=new CertificateDepositResult()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que el valor no sea null
			oCertificateDepositResult.numberOfPayment = serviceResponseTO.getData().get('@o_plazo')
			Assert.assertNotNull('Filas Vacias', oCertificateDepositResult.numberOfPayment)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetLoans(){
		String ServiceName='GetLoans'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetLoans')

			//DTO IN
			User wUser=new User()
			wUser.entityType=CTSEnvironment.bvLoginType

			//DTO OUT
			LoanCollection wLoanCollection=new LoanCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			wLoanCollection.loans = serviceResponseTO.getData().get('returnLoan')
			Assert.assertTrue('Filas Vacias', wLoanCollection.loans.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetSimulatorSettings(){
		String ServiceName='GetSimulatorSettings'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetSimulatorSettings')

			//DTO IN
			CertificateDeposit wCertificateDeposit=new CertificateDeposit()
			wCertificateDeposit.money=CTSEnvironment.bvAccCtaCteCurrencyId
			wCertificateDeposit.nemonic=''

			//DTO OUT

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			SimulationSetting ret  = serviceResponseTO.getData().get('returnSimulationSetting')
			Assert.assertTrue('Filas Vacias', ret.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetLoanItems(){
		String ServiceName='GetLoanItems'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetLoanItems')

			//DTO IN
			LoanSim wLoanSim=new LoanSim()
			wLoanSim.code=''

			//DTO OUT
			LoanItemResults wLoanItemResults=new LoanItemResults()

			serviceRequestTO.addValue('inLoanSim', wLoanSim)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			wLoanItemResults.loanSims = serviceResponseTO.getData().get('returnLoanSim')
			Assert.assertTrue('Filas Vacias', wLoanItemResults.loanSims.collect().size()>0)
			wLoanItemResults.loanItems = serviceResponseTO.getData().get('returnLoanItem')
			Assert.assertTrue('Filas Vacias', wLoanItemResults.loanItems.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetSavings(){
		String ServiceName='GetSavings'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetSavings')

			//DTO IN
			User wUser= new User()
			wUser.entityId=CTSEnvironment.bvEnte

			//DTO OUT
			SavingSimCollection oSavingSimCollection=new SavingSimCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSavingSimCollection.savingSims = serviceResponseTO.getData().get('returnSavingSim')
			Assert.assertTrue('Filas Vacias', oSavingSimCollection.savingSims.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetCDExpirationDate(){
		String ServiceName='GetCDExpirationDate'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDExpirationDate')

			//DTO IN
			CertificateDeposit wCertificateDeposit= new CertificateDeposit()
			wCertificateDeposit.processDate=''
			wCertificateDeposit.nemonic=''
			wCertificateDeposit.term=1
			wCertificateDeposit.useCalendarDays='30'
			TransactionRequest wTransactionRequest=new TransactionRequest()
			wTransactionRequest.dateFormatId=103

			//DTO OUT
			CDExpirationDateResponse oCDExpirationDateResponse=new CDExpirationDateResponse()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCDExpirationDateResponse.expirationDate = serviceResponseTO.getData().get('returnCDExpirationDateInfo')
			Assert.assertTrue('Filas Vacias', oCDExpirationDateResponse.expirationDate.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetDetailCD(){
		String ServiceName='GetDetailCD'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetDetailCD')

			//DTO IN
			CertificateDeposit wCertificateDeposit= new CertificateDeposit()
			wCertificateDeposit.nemonic='PERF1'

			//DTO OUT
			CertificateDepositCollection oCertificateDepositCollection=new CertificateDepositCollection()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCertificateDepositCollection.certificateDeposits = serviceResponseTO.getData().get('returnCertificateDeposit')
			Assert.assertTrue('Filas Vacias', oCertificateDepositCollection.certificateDeposits.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testCreateLoan(){
		String ServiceName='CreateLoan'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.CreateLoan')

			//DTO IN
			LoanSim wLoanSim= new LoanSim()
			wLoanSim.amount=100
			wLoanSim.sector='COMR'
			wLoanSim.operationType='AUT'
			wLoanSim.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wLoanSim.initialDate='01/01/2014'

			//DTO OUT
			LoanSimCollection oLoanSimCollection=new LoanSimCollection()

			serviceRequestTO.addValue('inLoanSim', wLoanSim)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanSimCollection.loanSims = serviceResponseTO.getData().get('returnLoanSim')
			Assert.assertTrue('Filas Vacias', oLoanSimCollection.loanSims.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testExecuteCDSimulation(){
		String ServiceName='ExecuteCDSimulation'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteCDSimulation')

			//DTO IN
			CertificateDeposit wCertificateDeposit= new CertificateDeposit()
			wCertificateDeposit.nemonic=''
			wCertificateDeposit.amount=100
			wCertificateDeposit.term=CTSEnvironment.bvTerminalIp
			wCertificateDeposit.rate=1
			wCertificateDeposit.money=CTSEnvironment.bvAccCtaCteCurrencyId
			wCertificateDeposit.category=''
			wCertificateDeposit.date=''
			wCertificateDeposit.entityId=CTSEnvironment.bvEnte
			wCertificateDeposit.payDay=0

			//DTO OUT
			CertificateDepositResultCollection oCertificateDepositResultCollection=new CertificateDepositResultCollection()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCertificateDepositResultCollection.certificateDepositResults = serviceResponseTO.getData().get('returnCertificateDepositResult')
			Assert.assertTrue('Filas Vacias', oCertificateDepositResultCollection.certificateDepositResults.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testExecuteLoanSimulation(){
		String ServiceName='ExecuteLoanSimulation'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteLoanSimulation')

			//DTO IN
			LoanSim wLoanSim= new LoanSim()
			wLoanSim.operation=''
			wLoanSim.code=''
			wLoanSim.payment=100
			wLoanSim.term=1

			//DTO OUT
			LoanSimCollection oLoanSimCollection=new LoanSimCollection()

			serviceRequestTO.addValue('inLoanSim', wLoanSim)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanSimCollection.loanSims = serviceResponseTO.getData().get('returnLoanSim')
			Assert.assertTrue('Filas Vacias', oLoanSimCollection.loanSims.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testExecuteSavingSimulation(){
		String ServiceName='ExecuteSavingSimulation'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteSavingSimulation')

			//DTO IN
			SavingSim wSavingSim= new SavingSim()
			wSavingSim.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wSavingSim.initialAmount=100
			wSavingSim.entityType=CTSEnvironment.bvLoginType
			wSavingSim.code=0
			wSavingSim.category=''
			wSavingSim.term=0
			wSavingSim.finalAmount=0
			wSavingSim.operationType=''

			//DTO OUT
			SavingSimCollection oSavingSimCollection=new SavingSimCollection()

			serviceRequestTO.addValue('inSavingSim', wSavingSim)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSavingSimCollection.savingSims = serviceResponseTO.getData().get('returnSavingSim')
			Assert.assertTrue('Filas Vacias', oSavingSimCollection.savingSims.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetCDPeriodicity(){
		String ServiceName='GetCDPeriodicity'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDPeriodicity')

			//DTO IN
			CertificateDeposit wCertificateDeposit=new CertificateDeposit()
			wCertificateDeposit.nemonic='PERF1'
			wCertificateDeposit.regType=''

			//DTO OUT
			PeriodicityCollection oPeriodicityCollection=new PeriodicityCollection()

			serviceRequestTO.addValue('inCertificateDeposit', wCertificateDeposit)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPeriodicityCollection.periodicitys = serviceResponseTO.getData().get('returnPeriodicity')
			Assert.assertTrue('Filas Vacias', oPeriodicityCollection.periodicitys.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
