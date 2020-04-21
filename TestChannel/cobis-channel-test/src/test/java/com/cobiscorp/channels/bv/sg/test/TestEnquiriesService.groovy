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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FactoringPayments
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FactoringPaymentsCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.HistoricalCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.NoPaycheckOrder
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PayableInterestCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetailScheduleCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentScheduleCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.SearchOptionPayments
import cobiscorp.ecobis.internetbanking.webapp.services.dto.StatementCollection
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Enquiries.Service.Service
 * @author schancay
 *
 */
class TestEnquiriesService{
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
	 * Modulo: CertificateDeposit 
	 */

	@Test
	void testGetRealDays(){
		//TODO Cambiar parametros de consulta cuando se hagan las UT de orquestaciones>>RequestCheckbook
		String ServiceName='GetRealDays'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetRealDays')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber

			//DTO OUT
			PaymentDetailScheduleCollection oPaymentDetailScheduleCollection=new PaymentDetailScheduleCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPaymentDetailScheduleCollection.paymentsDetailSchedule = serviceResponseTO.getData().get('returnPaymentDetailSchedule')
			Assert.assertTrue('Filas Vacias', oPaymentDetailScheduleCollection.paymentsDetailSchedule.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetStatements(){
		String ServiceName='GetStatements'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetStatements')
			//TODO revisar ya que tenia 2 trn registrados
			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			StatementCollection oStatementCollection=new StatementCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oStatementCollection.statements = serviceResponseTO.getData().get('returnStatement')
			Assert.assertTrue('Filas Vacias', oStatementCollection.statements.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetPaymentSchedule(){
		String ServiceName='GetPaymentSchedule'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPaymentSchedule')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			PaymentScheduleCollection oPaymentScheduleCollection=new PaymentScheduleCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPaymentScheduleCollection.paymentsSchedule = serviceResponseTO.getData().get('returnPaymentSchedule')
			Assert.assertTrue('Filas Vacias', oPaymentScheduleCollection.paymentsSchedule.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetPayableInterests(){
		String ServiceName='GetPayableInterests'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPayableInterests')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			PayableInterestCollection oPayableInterestCollection=new PayableInterestCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPayableInterestCollection.payableInterests = serviceResponseTO.getData().get('returnPayableInterest')
			Assert.assertTrue('Filas Vacias', oPayableInterestCollection.payableInterests.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetHistoricals(){
		String ServiceName='GetHistoricals'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetHistoricals')
			//TODO TRN DUPLICADO 14805
			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			HistoricalCollection oHistoricalCollection=new HistoricalCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oHistoricalCollection.historicals = serviceResponseTO.getData().get('returnHistorical')
			Assert.assertTrue('Filas Vacias', oHistoricalCollection.historicals.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetPaymentDetailSchedule(){
		String ServiceName='GetPaymentDetailSchedule'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.CertificateDeposit.GetPaymentDetailSchedule')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccDpfNumber

			//DTO OUT
			PaymentDetailScheduleCollection oPaymentDetailScheduleCollection=new PaymentDetailScheduleCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPaymentDetailScheduleCollection.paymentsDetailSchedule = serviceResponseTO.getData().get('returnPaymentDetailSchedule')
			Assert.assertTrue('Filas Vacias', oPaymentDetailScheduleCollection.paymentsDetailSchedule.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: FactoringOperations
	 */

	@Test
	void testGetFactoringPayments(){
		String ServiceName='GetFactoringPayments'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.FactoringOperations.GetFactoringPayments')

			//DTO IN
			FactoringPayments wFactoringPayments=new FactoringPayments()
			wFactoringPayments.secuential=0
			wFactoringPayments.operation=CTSEnvironment.bvAccCarNumber
			Client wClient=new Client()
			wClient.entityId=CTSEnvironment.bvEnteMis
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			SearchOptionPayments wSearchOptionPayments=new SearchOptionPayments()
			wSearchOptionPayments.initialDate='01/01/2002'
			wSearchOptionPayments.finalDate='12/12/2014'

			//DTO OUT
			FactoringPaymentsCollection oFactoringPaymentsCollection=new FactoringPaymentsCollection()

			serviceRequestTO.addValue('inFactoringPayments', wFactoringPayments)
			serviceRequestTO.addValue('inClient', wClient)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOptionPayments', wSearchOptionPayments)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oFactoringPaymentsCollection.factoringPayments = serviceResponseTO.getData().get('returnFactoringPayments')
			Assert.assertTrue('Filas Vacias', oFactoringPaymentsCollection.factoringPayments.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: SearchCheck
	 */

	@Test
	void testValidateChecksState(){
		String ServiceName='ValidateChecksState'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.Service.SearchCheck.ValidateChecksState')

			//DTO IN
			NoPaycheckOrder wNoPaycheckOrder=new NoPaycheckOrder()
			wNoPaycheckOrder.initalCheck=0
			wNoPaycheckOrder.numberOfChecks=0
			wNoPaycheckOrder.state=''
			wNoPaycheckOrder.account=CTSEnvironment.bvAccCtaCteNumber

			//DTO OUT

			serviceRequestTO.addValue('inNoPaycheckOrder', wNoPaycheckOrder)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
