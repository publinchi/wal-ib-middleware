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
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanData
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanDataCollection
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoanSimExecResults
import cobiscorp.ecobis.internetbanking.webapp.products.dto.LoansInvoiceDetailCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.DetailLoanPaymentCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanBalanceCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.LoanStatementCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.SearchOptionPayments
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Loan.Service.Service
 * @author schancay
 *
 */
class TestLoanService {
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
	 * Modulo: Loan 
	 */

	@Test
	void testModifyNegotiation(){
		String ServiceName='ModifyNegotiation'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.ModifyNegotiation')

			//DTO IN
			PaymentRequest wPaymentRequest=new PaymentRequest()
			wPaymentRequest.loanNumber=''
			LoanData wLoanData=new LoanData()
			wLoanData.completeQuota=''
			wLoanData.chargeRate=''
			wLoanData.reductionRate=''
			wLoanData.paymentEffect=''
			wLoanData.priorityRate=''
			wLoanData.advancePayment=''

			//DTO OUT

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest)
			serviceRequestTO.addValue('inLoanData', wLoanData)

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

	@Test
	void testGetLoans(){
		String ServiceName='GetLoans'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoans')

			//DTO IN
			Client wClient=new Client()
			wClient.entityId=CTSEnvironment.bvEnte
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEnquiryRequest.dateFormat=103
			wEnquiryRequest.operation=''
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCtaCteNumber
			PaymentRequest wPaymentRequest=new PaymentRequest()
			wPaymentRequest.secuential=0
			SearchOptionPayments wSearchOptionPayments=new SearchOptionPayments()
			wSearchOptionPayments.numberOfResults=10
			wSearchOptionPayments.initialDate=''
			wSearchOptionPayments.finalDate=''

			//DTO OUT
			LoanBalanceCollection oLoanBalanceCollection=new LoanBalanceCollection()

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inClient', wClient)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanBalanceCollection.loanBalances = serviceResponseTO.getData().get('returnLoanBalance')
			Assert.assertTrue('Filas Vacias', oLoanBalanceCollection.loanBalances.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetDetailLoanInvoice(){
		String ServiceName='GetDetailLoanInvoice'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetDetailLoanInvoice')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.mode=0
			wSearchOption.criteria=''
			wSearchOption.sequential=0
			wSearchOption.numberOfResults=10

			//DTO OUT
			LoansInvoiceDetailCollection oLoansInvoiceDetailCollection=new LoansInvoiceDetailCollection()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoansInvoiceDetailCollection.loansInvoiceMaster = serviceResponseTO.getData().get('returnLoansInvoiceMaster')
			Assert.assertTrue('Filas Vacias', oLoansInvoiceDetailCollection.loansInvoiceMaster.collect().size()>0)

			//Valido que Numero de elementos sea mayor a una fila
			oLoansInvoiceDetailCollection.loansInvoice = serviceResponseTO.getData().get('returnLoansInvoice')
			Assert.assertTrue('Filas Vacias', oLoansInvoiceDetailCollection.loansInvoice.collect().size()>0)

			//Valido que Numero de elementos sea mayor a una fila
			oLoansInvoiceDetailCollection.loansInvoiceDetail = serviceResponseTO.getData().get('returnLoansInvoiceDetail')
			Assert.assertTrue('Filas Vacias', oLoansInvoiceDetailCollection.loansInvoiceDetail.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetLoanStatement(){
		String ServiceName='GetLoanStatement'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanStatement')

			//DTO IN
			LoanBalance wLoanBalance=new LoanBalance()
			wLoanBalance.productNumber=CTSEnvironment.bvAccCtaCteNumber
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			LoanStatementCollection oLoanStatementCollection=new LoanStatementCollection()

			serviceRequestTO.addValue('inLoanBalance', wLoanBalance)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanStatementCollection.loanStatements = serviceResponseTO.getData().get('returnLoanStatement')
			Assert.assertTrue('Filas Vacias', oLoanStatementCollection.loanStatements.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetLoanAmortization(){
		String ServiceName='GetLoanAmortization'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanAmortization')

			//DTO IN
			LoanBalance wLoanBalance=new LoanBalance()
			wLoanBalance.productNumber=CTSEnvironment.bvAccCtaCteNumber
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			LoanSimExecResults oLoanSimExecResults=new LoanSimExecResults()

			serviceRequestTO.addValue('inLoanBalance', wLoanBalance)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanSimExecResults.loanSimResults = serviceResponseTO.getData().get('returnLoanSimResult')
			Assert.assertTrue('Filas Vacias', oLoanSimExecResults.loanSimResults.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetDetailLoanPayment(){
		String ServiceName='GetDetailLoanPayment'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetDetailLoanPayment')

			//DTO IN
			PaymentRequest wPaymentRequest=new PaymentRequest()
			wPaymentRequest.loanNumber=CTSEnvironment.bvAccCarNumber
			wPaymentRequest.transactionId=409828

			//DTO OUT
			DetailLoanPaymentCollection oDetailLoanPaymentCollection=new DetailLoanPaymentCollection()

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oDetailLoanPaymentCollection.detailLoanPayments = serviceResponseTO.getData().get('returnDetailLoanPayment')
			Assert.assertTrue('Filas Vacias', oDetailLoanPaymentCollection.detailLoanPayments.collect().size()>=0)


		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetNegotiationData(){
		String ServiceName='GetNegotiationData'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetNegotiationData')

			//DTO IN
			PaymentRequest wPaymentRequest=new PaymentRequest()
			wPaymentRequest.loanNumber=CTSEnvironment.bvAccCarNumber

			//DTO OUT
			LoanDataCollection oLoanDataCollection=new LoanDataCollection()

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanDataCollection.loanDatas = serviceResponseTO.getData().get('returnLoanData')
			Assert.assertTrue('Filas Vacias', oLoanDataCollection.loanDatas.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetLoanAccount(){
		String ServiceName='GetLoanAccount'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Service.Loan.GetLoanAccount')

			//DTO IN
			PaymentRequest wPaymentRequest=new PaymentRequest()
			wPaymentRequest.loanNumber=CTSEnvironment.bvAccCarNumber

			//DTO OUT
			LoanDataCollection oLoanDataCollection=new LoanDataCollection()

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLoanDataCollection.loanDatas = serviceResponseTO.getData().get('returnLoanData')
			Assert.assertTrue('Filas Vacias', oLoanDataCollection.loanDatas.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
