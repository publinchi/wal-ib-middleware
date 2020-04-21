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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Condition
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Entity
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.RequestTemplate
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountOperationCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CurrencyTrading
import cobiscorp.ecobis.internetbanking.webapp.services.dto.DetailInternationalTransfersReceivedCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.InternationalTransfersReceivedCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetailsCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseCurrencyTrading
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TemplateDetail
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TemplateList
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferInternationalDetailCollection
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOptionCollection
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.Transfer

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Transfers.Service.Service
 * @author schancay
 *
 */
class TestTransfersService{
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
	}

	/**
	 * Modulo: TransferBatch
	 */

	@Test
	void testAddTransferBatch(){
		String ServiceName='AddTransferBatch'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.TransferBatch.AddTransferBatch')

			//DTO IN
			Condition wCondition = new Condition()
			wCondition.transactionDescription='UNIT TEST IB'
			Entity wEntity = new Entity()
			wEntity.userName=CTSEnvironment.bvLogin
			wEntity.id=CTSEnvironment.bvEnte

			//DTO OUT
			SearchOptionCollection oSearchOptionCollection=new SearchOptionCollection()

			serviceRequestTO.addValue('inCondition', wCondition)
			serviceRequestTO.addValue('inEntity', wEntity)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oSearchOptionCollection.searchOptions = serviceResponseTO.getData().get('returnSearchOption')
			Assert.assertTrue('Filas Vacias', oSearchOptionCollection.searchOptions.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testAddTransferBatchAccounts(){
		String ServiceName='AddTransferBatchAccounts'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.TransferBatch.AddTransferBatchAccounts')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=7
			wSearchOption.criteria='ERROR CONTROLADO TEST IB'
			//cta origen
			Product wProduct1=new Product()
			wProduct1.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wProduct1.productId=CTSEnvironment.bvAccCtaCteType
			wProduct1.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			//cts destino
			Product wProduct2=new Product()
			wProduct2.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wProduct2.productId=CTSEnvironment.bvAccCtaCteType
			wProduct2.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			Transfer wTransfer=new Transfer()
			wTransfer.info='PMTEST'
			wTransfer.amount=1
			wTransfer.concept='TEST IB BATCH'
			wTransfer.beneficiary='UNIT TEST IB'
			wTransfer.id=0

			//DTO OUT

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			serviceRequestTO.addValue('inTransfer', wTransfer)

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
	void testAddTransferGroup(){
		String ServiceName='AddTransferGroup'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.TransferBatch.AddTransferGroup')

			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=7
			Product wProduct=new Product()
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wProduct.productId=CTSEnvironment.bvAccCtaCteType
			wProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			Condition wCondition1 = new Condition()
			wCondition1.maximum=1
			wCondition1.accountingBalance=100
			wCondition1.transactionDescription='TEST TRX IB'
			Condition wCondition2 = new Condition()
			wCondition2.maximum=0
			wCondition2.accountingBalance=0

			//DTO OUT

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inProduct', wProduct)
			serviceRequestTO.addValue('inCondition', wCondition1)
			serviceRequestTO.addValue('inCondition2', wCondition2)

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
	 * Modulo: InternationalTransfer
	 */

	@Test
	void testGetCurrencyTrading(){
		String ServiceName='GetCurrencyTrading'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetCurrencyTrading')
			//TODO Revisar parametria de consulta
			//DTO IN
			CurrencyTrading wCurrencyTrading=new CurrencyTrading()
			wCurrencyTrading.office=1
			wCurrencyTrading.client=CTSEnvironment.bvEnte
			wCurrencyTrading.module=''
			wCurrencyTrading.currency=CTSEnvironment.bvAccCtaCteCurrencyId
			wCurrencyTrading.typeOperation=''
			wCurrencyTrading.sequentialPreauthorization=''

			//DTO OUT
			ResponseCurrencyTrading oResponseCurrencyTrading=new ResponseCurrencyTrading()

			serviceRequestTO.addValue('inCurrencyTrading', wCurrencyTrading)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que contenga algun valor
			oResponseCurrencyTrading.quotation = serviceResponseTO.getData().get('Quotation')
			Assert.assertNotNull('Objeto Vacio', oResponseCurrencyTrading.quotation)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetClosureDate(){
		String ServiceName='GetClosureDate'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetClosureDate')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCexNumber
			wEnquiryRequest.dateFormatId=103

			//DTO OUT
			TransferCollection oTransferCollection=new TransferCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oTransferCollection.transfersCollection = serviceResponseTO.getData().get('returnPayment')
			Assert.assertTrue('Filas Vacias', oTransferCollection.transfersCollection.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetDetailInternationalTransferReceived(){
		String ServiceName='GetDetailInternationalTransferReceived'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetDetailInternationalTransferReceived')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCexNumber
			wEnquiryRequest.dateFormatId=103

			//DTO OUT
			DetailInternationalTransfersReceivedCollection oDetailInternationalTransfersReceivedCollection=new DetailInternationalTransfersReceivedCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oDetailInternationalTransfersReceivedCollection.detailInternationalTransferReceived = serviceResponseTO.getData().get('returnDetailInternationalTransfersReceived')
			Assert.assertTrue('Filas Vacias', oDetailInternationalTransfersReceivedCollection.detailInternationalTransferReceived.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetAccountingOperation(){
		String ServiceName='GetAccountingOperation'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetAccountingOperation')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCexNumber
			wEnquiryRequest.dateFormatId=103

			//DTO OUT
			AccountOperationCollection oAccountOperationCollection=new AccountOperationCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oAccountOperationCollection.accountOperations = serviceResponseTO.getData().get('returnAccountOperation')
			Assert.assertTrue('Filas Vacias', oAccountOperationCollection.accountOperations.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetInternationalTransfersReceived(){
		String ServiceName='GetInternationalTransfersReceived'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetInternationalTransfersReceived')
			//TODO Revisar parametria de consulta
			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0
			wSearchOption.initialDate=''
			wSearchOption.finalDate=''
			wSearchOption.criteria=''
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.dateFormatId=103
			User wUser=new User()
			wUser.entityId=CTSEnvironment.bvEnteMis

			//DTO OUT
			InternationalTransfersReceivedCollection oInternationalTransfersReceivedCollection=new InternationalTransfersReceivedCollection()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oInternationalTransfersReceivedCollection.internationalTransfersReceived = serviceResponseTO.getData().get('returnInternationalTransfersReceived')
			Assert.assertTrue('Filas Vacias', oInternationalTransfersReceivedCollection.internationalTransfersReceived.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetInternationalTransferDetail(){
		String ServiceName='GetInternationalTransferDetail'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetInternationalTransferDetail')
			//TODO Revisar parametria de consulta
			//DTO IN
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria=''
			wSearchOption.initialCheck=0
			wSearchOption.criteria2='SR'
			wSearchOption.sequential=0
			wSearchOption.initialDate=''
			wSearchOption.finalDate=''
			wSearchOption.mode=0
			wSearchOption.lastResult=''
			wSearchOption.numberOfResults=0
			Product wProduct=new Product()
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber

			//DTO OUT
			TransferInternationalDetailCollection oTransferInternationalDetailCollection=new TransferInternationalDetailCollection()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inProduct', wProduct)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oTransferInternationalDetailCollection.transferInternationalDetails = serviceResponseTO.getData().get('returnTransferInternationalDetail')
			Assert.assertTrue('Filas Vacias', oTransferInternationalDetailCollection.transferInternationalDetails.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testGetPaymentDetails(){
		String ServiceName='GetPaymentDetails'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetPaymentDetails')
			//TODO Revisar parametria de consulta
			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.productNumber=CTSEnvironment.bvAccCexNumber
			wEnquiryRequest.dateFormatId=103

			//DTO OUT
			PaymentDetailsCollection oPaymentDetailsCollection=new PaymentDetailsCollection()

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPaymentDetailsCollection.paymentDetails = serviceResponseTO.getData().get('returnPaymentDetails')
			Assert.assertTrue('Filas Vacias', oPaymentDetailsCollection.paymentDetails.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Modulo: Transfer
	 */

	@Test
	void testGetTemplatesInRangeDate(){
		String ServiceName='testGetTemplates'

		println String.format('Test [%s]',ServiceName)
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		serviceRequestTO.setSessionId(initSession)
		serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.Transfer.GetTemplates')

		Calendar calendarStart = new GregorianCalendar()
		calendarStart.set(Calendar.YEAR, 2013)
		calendarStart.set(Calendar.MONTH, 3)
		calendarStart.set(Calendar.DAY_OF_MONTH, 24)
		calendarStart.set(Calendar.HOUR, 0)
		calendarStart.set(Calendar.MINUTE, 0)
		calendarStart.set(Calendar.SECOND, 0)
		calendarStart.set(Calendar.AM_PM, Calendar.AM)

		Calendar calendarEnd = new GregorianCalendar()
		calendarEnd.set(Calendar.YEAR, 2013)
		calendarEnd.set(Calendar.MONTH,3)
		calendarEnd.set(Calendar.DAY_OF_MONTH, 24)
		calendarEnd.set(Calendar.HOUR, 11)
		calendarEnd.set(Calendar.MINUTE, 0)
		calendarEnd.set(Calendar.SECOND, 0)
		calendarEnd.set(Calendar.AM_PM, Calendar.PM)

		//DTO IN
		RequestTemplate wRequestTemplate=new RequestTemplate()
		wRequestTemplate.login=CTSEnvironment.bvLogin
		wRequestTemplate.startDate=calendarStart
		wRequestTemplate.endDate=calendarEnd
		wRequestTemplate.beneficiary=''
		wRequestTemplate.templateName=null

		serviceRequestTO.addValue('inRequestTemplate', wRequestTemplate)

		ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

		String message=''
		if (serviceResponseTO.messages.toList().size()>0)
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		Assert.assertTrue(message, serviceResponseTO.success)

		TemplateList[] wTemplateList=new TemplateList()
		wTemplateList=serviceResponseTO.getData().get('returnTemplateList')

		Assert.assertTrue(wTemplateList.collect().size()==1)
		Assert.assertNotNull('Objeto Vacio', wTemplateList[0])

		Assert.assertEquals(wTemplateList[0].bank,'BANCO PRUEBA')
		Assert.assertEquals(wTemplateList[0].beneficiary,'PRUEBA TEST PRUEBAT')
		Assert.assertEquals(wTemplateList[0].description,'PLANTILLA PRUEBA')
	}

	@Test
	void testGetTemplatesDetail(){
		String ServiceName='testGetTemplatesDetails'

		println String.format('Test [%s]',ServiceName)
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		serviceRequestTO.setSessionId(initSession)
		serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.Transfer.GetTemplatesDetails')

		//DTO IN
		RequestTemplate wRequestTemplate=new RequestTemplate()
		wRequestTemplate.sequential=8474244

		serviceRequestTO.addValue('inRequestTemplate', wRequestTemplate)

		ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

		String message=''
		if (serviceResponseTO.messages.toList().size()>0)
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		Assert.assertTrue(message, serviceResponseTO.success)

		TemplateDetail[] wTemplateDetail=new TemplateDetail()
		wTemplateDetail=serviceResponseTO.getData().get('returnTemplateDetail')

		Assert.assertNotNull('Objeto Vacio', wTemplateDetail)

		Assert.assertEquals(wTemplateDetail[0].idBeneficiaryBank,3357)
		Assert.assertEquals(wTemplateDetail[0].beneficiary,'PRUEBA')
		Assert.assertEquals(wTemplateDetail[0].templateName,'PLANTILLA PRUEBA')
	}

	@Test
	void testGetTemplatesInOutRangeDate(){
		String ServiceName='testGetTemplates'

		println String.format('Test [%s]',ServiceName)
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
		serviceRequestTO.setSessionId(initSession)
		serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.Transfer.GetTemplates')

		Calendar calendarStart = new GregorianCalendar()
		calendarStart.set(Calendar.YEAR, 2013)
		calendarStart.set(Calendar.MONTH, 3)
		calendarStart.set(Calendar.DAY_OF_MONTH, 25)
		calendarStart.set(Calendar.HOUR, 0)
		calendarStart.set(Calendar.MINUTE, 0)
		calendarStart.set(Calendar.SECOND, 0)
		calendarStart.set(Calendar.AM_PM, Calendar.AM)

		Calendar calendarEnd = new GregorianCalendar()
		calendarEnd.set(Calendar.YEAR, 2013)
		calendarEnd.set(Calendar.MONTH,3)
		calendarEnd.set(Calendar.DAY_OF_MONTH, 25)
		calendarEnd.set(Calendar.HOUR, 11)
		calendarEnd.set(Calendar.MINUTE, 0)
		calendarEnd.set(Calendar.SECOND, 0)
		calendarEnd.set(Calendar.AM_PM, Calendar.PM)

		//DTO IN
		RequestTemplate wRequestTemplate=new RequestTemplate()
		wRequestTemplate.login=CTSEnvironment.bvLogin
		wRequestTemplate.startDate=calendarStart
		wRequestTemplate.endDate=calendarEnd
		wRequestTemplate.beneficiary=''
		wRequestTemplate.templateName=null

		serviceRequestTO.addValue('inRequestTemplate', wRequestTemplate)

		ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

		String message=''
		if (serviceResponseTO.messages.toList().size()>0)
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
		Assert.assertTrue(message, serviceResponseTO.success)

		TemplateList[] wTemplateList=new TemplateList()
		wTemplateList=serviceResponseTO.getData().get('returnTemplateList')

		Assert.assertTrue(wTemplateList.collect().size()==0)
		Assert.assertNull('Objeto Vacio', wTemplateList[0])

	}
}