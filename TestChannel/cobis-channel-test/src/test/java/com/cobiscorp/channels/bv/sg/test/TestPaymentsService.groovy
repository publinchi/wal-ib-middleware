package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Entity
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Contract
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ContractCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CreditCardCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.DataReceiptPrinterCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.InvoicingBaseCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentServices
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ScheduledPayment
import cobiscorp.ecobis.internetbanking.webapp.services.dto.SubscribedContract
import cobiscorp.ecobis.internetbanking.webapp.services.dto.SubscribedContractCollection
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Payments.Service.Service
 * @author schancay
 *
 */
class TestPaymentsService {
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
	 * Modulo: Payment 
	 */

	@Test
	void testGetCreditCard(){
		String ServiceName='GetCreditCard'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.Payment.GetCreditCard')

			//DTO IN
			User wUser=new User()
			wUser.entityId=CTSEnvironment.bvEnteMis

			//DTO OUT
			CreditCardCollection oCreditCardCollection=new CreditCardCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCreditCardCollection.creditCard = serviceResponseTO.getData().get('returnCreditCard')
			Assert.assertTrue('Filas Vacias', oCreditCardCollection.creditCard.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetPaidServiceReceiptReprint(){
		String ServiceName='GetPaidServiceReceiptReprint'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.Payment.GetPaidServiceReceiptReprint')

			//DTO IN
			PaymentServices wPaymentServices=new PaymentServices()
			wPaymentServices.documentId=''
			wPaymentServices.paymentDate=''
			wPaymentServices.contractId=0
			User wUser=new User()
			wUser.name=CTSEnvironment.bvLogin

			//DTO OUT
			DataReceiptPrinterCollection oDataReceiptPrinterCollection=new DataReceiptPrinterCollection()
			DataReceiptPrinterCollection oDataReceiptPrinterCollection1=new DataReceiptPrinterCollection()
			PaymentServices oPaymentServices=new PaymentServices()

			serviceRequestTO.addValue('inPaymentServices', wPaymentServices)
			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oDataReceiptPrinterCollection.dataReceiptPrinters = serviceResponseTO.getData().get('returnDataReceiptPrinter')
			Assert.assertTrue('Filas Vacias', oDataReceiptPrinterCollection.dataReceiptPrinters.collect().size()>0)

			//Valido que Numero de elementos sea mayor a una fila
			oDataReceiptPrinterCollection1.dataReceiptPrinters = serviceResponseTO.getData().get('returnDataReceiptPrinter')
			Assert.assertTrue('Filas Vacias', oDataReceiptPrinterCollection1.dataReceiptPrinters.collect().size()>0)

			//Valido que el objeto no sea null
			oPaymentServices.contractId = serviceResponseTO.getData().get('@o_colector')
			Assert.assertNotNull('Objeto Vacio', oPaymentServices.contractId)

			//Valido que el objeto no sea null
			oPaymentServices.documentType = serviceResponseTO.getData().get('@o_num_doc')
			Assert.assertNotNull('Objeto Vacio', oPaymentServices.documentType)

			//Valido que el objeto no sea null
			oPaymentServices.paymentDate = serviceResponseTO.getData().get('@o_fecha')
			Assert.assertNotNull('Objeto Vacio', oPaymentServices.paymentDate)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: CreditCard 
	 */

	@Test
	void testGetPrize(){
		String ServiceName='GetPrize'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.CreditCard.GetPrize')

			//DTO IN
			TransactionRequest wTransactionRequest=new TransactionRequest()
			wTransactionRequest.dateFormatId=103
			wTransactionRequest.productNumber=CTSEnvironment.bvAccTarNumber

			//DTO OUT
			CreditCardCollection oCreditCardCollection=new CreditCardCollection()

			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCreditCardCollection.creditCard = serviceResponseTO.getData().get('returnCreditCard')
			Assert.assertTrue('Filas Vacias', oCreditCardCollection.creditCard.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: ContractPayment 
	 */

	@Test
	void testAddSubscribedContract(){
		String ServiceName='AddSubscribedContract'
		try{
			//
			//			def sql = BDD.getInstance(CTSEnvironment.TARGETID_CENTRAL)
			//			sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_convenio_inscrito where ci_login=?',[
			//				CTSEnvironment.bvLogin
			//			])
			//			sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_convenio_inscrito_param where cip_num_doc=?',[
			//				CTSEnvironment.bvLoginNumDoc
			//			])
			//
			//			sql.close()
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.AddSubscribedContract')

			//DTO IN
			SubscribedContract wSubscribedContract=new SubscribedContract()
			wSubscribedContract.categoryId='07'
			wSubscribedContract.contractId=1001
			wSubscribedContract.numDoc=CTSEnvironment.bvLoginNumDoc
			wSubscribedContract.description='XXXXXXXXXXXXXXX'
			wSubscribedContract.iSecuencial=1001
			wSubscribedContract.entity=CTSEnvironment.bvEnteMis
			wSubscribedContract.login=CTSEnvironment.bvLogin
			wSubscribedContract.entityBv=CTSEnvironment.bvEnte
			wSubscribedContract.key='XX'
			wSubscribedContract.typeDoc='I'
			wSubscribedContract.reference1='XX'
			wSubscribedContract.reference2='XX'
			wSubscribedContract.reference3='XX'
			wSubscribedContract.reference4='XX'
			wSubscribedContract.reference5='XX'
			wSubscribedContract.reference6='XX'
			wSubscribedContract.reference7='XX'
			wSubscribedContract.reference8='XX'
			wSubscribedContract.reference8='XX'
			wSubscribedContract.reference10='XX'
			wSubscribedContract.reference11='XX'
			wSubscribedContract.reference12='XX'

			//DTO OUT

			serviceRequestTO.addValue('inSubscribedContract', wSubscribedContract)

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
	void testGetSubscribedContracts(){
		String ServiceName='GetSubscribedContracts'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.GetSubscribedContracts')

			//DTO IN
			SubscribedContract wSubscribedContract=new SubscribedContract()
			wSubscribedContract.categoryId='07'
			wSubscribedContract.modeSearch=100
			wSubscribedContract.sequential=1
			wSubscribedContract.entity=CTSEnvironment.bvEnteMis
			wSubscribedContract.login=CTSEnvironment.bvLogin

			//DTO OUT
			SubscribedContractCollection oSubscribedContractCollection=new SubscribedContractCollection()

			serviceRequestTO.addValue('inSubscribedContract', wSubscribedContract)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSubscribedContractCollection.subscribedContracts = serviceResponseTO.getData().get('returnSubscribedContract')
			Assert.assertTrue('Filas Vacias', oSubscribedContractCollection.subscribedContracts.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testUpdateSubscribedContract(){
		String ServiceName='UpdateSubscribedContract'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.UpdateSubscribedContract')

			//DTO IN
			SubscribedContract wSubscribedContract=new SubscribedContract()
			wSubscribedContract.categoryId=''
			wSubscribedContract.contractId=0
			wSubscribedContract.numDoc=CTSEnvironment.bvLoginNumDoc
			wSubscribedContract.description='XXXXXXXXX'
			wSubscribedContract.iSecuencial=1000
			wSubscribedContract.entity=CTSEnvironment.bvEnteMis
			wSubscribedContract.login=CTSEnvironment.bvLogin
			wSubscribedContract.entityBv=CTSEnvironment.bvEnte
			wSubscribedContract.key=''
			wSubscribedContract.typeDoc=''
			wSubscribedContract.reference1=''
			wSubscribedContract.reference2=''
			wSubscribedContract.reference3=''
			wSubscribedContract.reference4=''
			wSubscribedContract.reference5=''
			wSubscribedContract.reference6=''
			wSubscribedContract.reference7=''
			wSubscribedContract.reference8=''
			wSubscribedContract.reference8=''
			wSubscribedContract.reference10=''
			wSubscribedContract.reference11=''
			wSubscribedContract.reference12=''

			//DTO OUT

			serviceRequestTO.addValue('inSubscribedContract', wSubscribedContract)

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
	void testGetContracts(){
		String ServiceName='GetContracts'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.GetContracts')

			//DTO IN
			Contract wContract=new Contract()
			wContract.contractServiceId=CTSEnvironment.bvService
			wContract.contractCategoryId=''

			//DTO OUT
			ContractCollection oContractCollection=new ContractCollection()

			serviceRequestTO.addValue('inContract', wContract)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oContractCollection.contracts = serviceResponseTO.getData().get('returnContract')
			Assert.assertTrue('Filas Vacias', oContractCollection.contracts.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetInvoicingBase(){
		String ServiceName='GetInvoicingBase'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.GetInvoicingBase')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria='1'
			Entity wEntity=new Entity()
			wEntity.identification=CTSEnvironment.bvLoginNumDoc
			wEntity.name=CTSEnvironment.bvLogin
			Contract wContract=new Contract()
			wContract.contractId=1000
			TransactionRequest wTransactionRequest=new TransactionRequest()
			wTransactionRequest.dateFormatId=103

			//DTO OUT
			InvoicingBaseCollection oInvoicingBaseCollection=new InvoicingBaseCollection()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inEntity', wEntity)
			serviceRequestTO.addValue('inContract', wContract)
			serviceRequestTO.addValue('inTransactionRequest', wTransactionRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oInvoicingBaseCollection.invoicingBases = serviceResponseTO.getData().get('returnInvoicingBase')
			Assert.assertTrue('Filas Vacias', oInvoicingBaseCollection.invoicingBases.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testDeleteSubscribedContract(){
		String ServiceName='DeleteSubscribedContract'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ContractPayment.DeleteSubscribedContract')

			//DTO IN
			SubscribedContract wSubscribedContract=new SubscribedContract()
			wSubscribedContract.categoryId=''
			wSubscribedContract.contractId=0
			wSubscribedContract.iSecuencial=1000
			wSubscribedContract.entity=CTSEnvironment.bvEnteMis
			wSubscribedContract.login=CTSEnvironment.bvLogin

			//DTO OUT

			serviceRequestTO.addValue('inSubscribedContract', wSubscribedContract)

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

	/**
	 * Modulo: ScheduledPayment 
	 */

	@Test
	void testGetInstallment(){
		String ServiceName='GetInstallment'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Payments.Service.ScheduledPayment.GetInstallment')

			//DTO IN
			Product wProduct=new Product()
			wProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber

			//DTO OUT
			ScheduledPayment oScheduledPayment=new ScheduledPayment()

			serviceRequestTO.addValue('inProduct', wProduct)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que el objeto no sea null
			oScheduledPayment.amount = serviceResponseTO.getData().get('@o_cuota')
			Assert.assertNotNull('Objeto Vacio', oScheduledPayment.amount)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
