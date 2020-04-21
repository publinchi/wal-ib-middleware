package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.AfterClass
import org.junit.Assert
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.CurrencyTrading
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * 
 * @author schancay
 * @since Jul 17, 2014
 * @version 1.0.0
 */
class Test_ib_company_exchange_rates_orchestation {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession= virtualBankingBase.initSessionCompany()

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	/**
	 * Verificar codigo de autorizacion de compra y venta de divisas
	 */
	@Test
	void testGetCurrencyTradingBuy() {
		def ServiceName='testGetCurrencyTradingBuy'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetCurrencyTrading')

			//DTO IN
			CurrencyTrading wCurrencyTrading=new CurrencyTrading()
			wCurrencyTrading.office=1
			wCurrencyTrading.client=CTSEnvironment.bvCompanyEnteMis
			wCurrencyTrading.module='BVI'
			wCurrencyTrading.currency=17
			wCurrencyTrading.typeOperation='C'
			wCurrencyTrading.sequentialPreauthorization=276

			serviceRequestTO.addValue('inCurrencyTrading', wCurrencyTrading)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)


			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_quotation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_quotation)
			println ('RESPUESTA: @o_cotizacion----->' + wO_quotation)

			def wO_ammount = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_ammount)
			println ('RESPUESTA: @o_monto----->' + wO_ammount)

			def wO_factor = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_factor")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_factor)
			println ('RESPUESTA: @o_factor----->' + wO_factor)

			def wO_currency_id = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_moneda")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_currency_id)
			println ('RESPUESTA: @o_moneda----->' + wO_currency_id)

			def wO_currency_description = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_des_moneda")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_currency_description)
			println ('RESPUESTA: @o_des_moneda----->' + wO_currency_description)

			def wO_observation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_obs")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_observation)
			println ('RESPUESTA: @o_obs----->' + wO_observation)

			def wO_date_negotiation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fecha_t_mas_n")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_date_negotiation)
			println ('RESPUESTA: @o_fecha_t_mas_n----->' + wO_date_negotiation)

			def wO_ammount_other_buy = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_otr_c")
			println ('RESPUESTA: @o_monto_otr_c----->' + wO_ammount_other_buy)


		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}
	/**
	 * Verificar codigo de autorizacion de compra y venta de divisas
	 */
	@Test
	void testGetCurrencyTradingSale() {
		def ServiceName='testGetCurrencyTradingSale'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetCurrencyTrading')

			//DTO IN
			CurrencyTrading wCurrencyTrading=new CurrencyTrading()
			wCurrencyTrading.office=1
			wCurrencyTrading.client=CTSEnvironment.bvCompanyEnteMis
			wCurrencyTrading.module='BVI'
			wCurrencyTrading.currency=17
			wCurrencyTrading.typeOperation='V'
			wCurrencyTrading.sequentialPreauthorization=277

			serviceRequestTO.addValue('inCurrencyTrading', wCurrencyTrading)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)


			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_quotation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cotizacion")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_quotation)
			println ('RESPUESTA: @o_cotizacion----->' + wO_quotation)

			def wO_ammount = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto")
			println ('RESPUESTA: @o_monto----->' + wO_ammount)

			def wO_factor = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_factor")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_factor)
			println ('RESPUESTA: @o_factor----->' + wO_factor)

			def wO_currency_id = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_moneda")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_currency_id)
			println ('RESPUESTA: @o_moneda----->' + wO_currency_id)

			def wO_currency_description = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_des_moneda")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_currency_description)
			println ('RESPUESTA: @o_des_moneda----->' + wO_currency_description)

			def wO_observation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_obs")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_observation)
			println ('RESPUESTA: @o_obs----->' + wO_observation)

			def wO_date_negotiation = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fecha_t_mas_n")
			Assert.assertNotNull("No se obtuvo valor de compra",wO_date_negotiation)
			println ('RESPUESTA: @o_fecha_t_mas_n----->' + wO_date_negotiation)

			def wO_ammount_other_buy = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_otr_c")
			println ('RESPUESTA: @o_monto_otr_c----->' + wO_ammount_other_buy)


		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}
	/**
	 * Compra de Divisas sin codigo de autorizacion 
	 */
	@Test
	void testGetExchangeBuyWithOutCodeAuthorization() {
		def ServiceName='testGetExchangeBuyWithOutCodeAuthorization'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.ForeignExchange')

			//DTO IN
			Product wProduct1=new Product()
			wProduct1.productId=CTSEnvironment.bvCompanyAccCtaCteType
			wProduct1.productNumber=CTSEnvironment.bvCompanyAccCtaCteNumber
			wProduct1.currencyId=CTSEnvironment.bvCompanyAccCtaCteCurrencyId
			wProduct1.productAlias='CTA TESTPRUEBA'

			Product wProduct2=new Product()
			wProduct2.productId=CTSEnvironment.bvCompanyAccCtaAhoUsdType
			wProduct2.productNumber=CTSEnvironment.bvCompanyAccCtaAhoUsdNumber
			wProduct2.currencyId=CTSEnvironment.bvCompanyAccCtaAhoUsdCurrencyId

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria='C'
			wSearchOption.exchangeRate=100
			wSearchOption.notes='C'

			TransferRequest wTransferRequest=new TransferRequest()
			wTransferRequest.amount=100
			wTransferRequest.reference=0

			User wUser=new User()
			wUser.name=CTSEnvironment.bvLogin
			wUser.entityId=CTSEnvironment.bvEnteMis
			wUser.serviceId=CTSEnvironment.bvService

			TransactionContextCIB wTransactionContextCIB=new TransactionContextCIB()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_referencia = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			Assert.assertNotNull("No se obtuvo referencia de la transaccion",wO_referencia)
			println ('RESPUESTA: @o_referencia----->' + wO_referencia)

			def wO_monto_operacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_operacion")
			Assert.assertNotNull("No se obtuvo monto de la operacion",wO_monto_operacion)
			println ('RESPUESTA: @o_monto_operacion----->' + wO_monto_operacion)

			def wO_tasa = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tasa")
			Assert.assertNotNull("No se obtuvo valor de tasa",wO_tasa)
			println ('RESPUESTA: @o_tasa----->' + wO_tasa)

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}

	/**
	 * Venta de Divisas sin codigo de autorizacion 
	 */
	@Test
	void testGetExchangeSaleWithOutCodeAuthorization() {
		def ServiceName='testGetExchangeSaleWithOutCodeAuthorization'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.ForeignExchange')

			//DTO IN
			Product wProduct1=new Product()
			wProduct1.productId=CTSEnvironment.bvCompanyAccCtaAhoType
			wProduct1.productNumber=CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct1.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId
			wProduct1.productAlias='CTA TESTPRUEBA'

			Product wProduct2=new Product()
			wProduct2.productId=CTSEnvironment.bvCompanyAccCtaAhoUsdType
			wProduct2.productNumber=CTSEnvironment.bvCompanyAccCtaAhoUsdNumber
			wProduct2.currencyId=CTSEnvironment.bvCompanyAccCtaAhoUsdCurrencyId

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria='V'
			wSearchOption.exchangeRate=493
			wSearchOption.notes='V'

			TransferRequest wTransferRequest=new TransferRequest()
			wTransferRequest.amount=400
			wTransferRequest.reference=0

			User wUser=new User()
			wUser.name=CTSEnvironment.bvCompanyLogin
			wUser.entityId=CTSEnvironment.bvCompanyEnteMis
			wUser.serviceId=CTSEnvironment.bvService

			TransactionContextCIB wTransactionContextCIB=new TransactionContextCIB()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_referencia = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			Assert.assertNotNull("No se obtuvo referencia de la transaccion",wO_referencia)
			println ('RESPUESTA: @o_referencia----->' + wO_referencia)

			def wO_monto_operacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_operacion")
			Assert.assertNotNull("No se obtuvo monto de la operacion",wO_monto_operacion)
			println ('RESPUESTA: @o_monto_operacion----->' + wO_monto_operacion)

			def wO_tasa = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tasa")
			Assert.assertNotNull("No se obtuvo valor de tasa",wO_tasa)
			println ('RESPUESTA: @o_tasa----->' + wO_tasa)

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}

	/**
	 * Compra de Divisas con codigo de autorizacion 
	 */
	@Test
	void testGetExchangeBuyWithCodeAuthorization() {
		def ServiceName='testGetExchangeBuyWithOutCodeAuthorization'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.ForeignExchange')

			//DTO IN
			Product wProduct1=new Product()
			wProduct1.productId=CTSEnvironment.bvCompanyAccCtaAhoUsdType
			wProduct1.productNumber=CTSEnvironment.bvCompanyAccCtaAhoUsdNumber
			wProduct1.currencyId=CTSEnvironment.bvCompanyAccCtaAhoUsdCurrencyId
			wProduct1.productAlias='CTA TESTPRUEBA'

			Product wProduct2=new Product()
			wProduct2.productId=CTSEnvironment.bvCompanyAccCtaAhoType
			wProduct2.productNumber=CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct2.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria='C'
			wSearchOption.exchangeRate=493
			wSearchOption.notes='C'

			TransferRequest wTransferRequest=new TransferRequest()
			wTransferRequest.amount=400
			wTransferRequest.reference=276

			User wUser=new User()
			wUser.name=CTSEnvironment.bvCompanyLogin
			wUser.entityId=CTSEnvironment.bvCompanyEnteMis
			wUser.serviceId=CTSEnvironment.bvService

			TransactionContextCIB wTransactionContextCIB=new TransactionContextCIB()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_referencia = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			Assert.assertNotNull("No se obtuvo referencia de la transaccion",wO_referencia)
			println ('RESPUESTA: @o_referencia----->' + wO_referencia)

			def wO_monto_operacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_operacion")
			Assert.assertNotNull("No se obtuvo monto de la operacion",wO_monto_operacion)
			println ('RESPUESTA: @o_monto_operacion----->' + wO_monto_operacion)

			def wO_tasa = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tasa")
			Assert.assertNotNull("No se obtuvo valor de tasa",wO_tasa)
			println ('RESPUESTA: @o_tasa----->' + wO_tasa)

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
			Assert.fail()
		}
	}

	/**
	 * Venta de Divisas con codigo de autorizacion 
	 */
	@Test
	void testGetExchangeSaleWithCodeAuthorization() {
		def ServiceName='testGetExchangeSaleWithCodeAuthorization'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.ForeignExchange')

			//DTO IN
			Product wProduct1=new Product()
			wProduct1.productId=CTSEnvironment.bvCompanyAccCtaAhoType
			wProduct1.productNumber=CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct1.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId
			wProduct1.productAlias='CTA TESTPRUEBA'

			Product wProduct2=new Product()
			wProduct2.productId=CTSEnvironment.bvCompanyAccCtaAhoUsdType
			wProduct2.productNumber=CTSEnvironment.bvCompanyAccCtaAhoUsdNumber
			wProduct2.currencyId=CTSEnvironment.bvCompanyAccCtaAhoUsdCurrencyId

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria='V'
			wSearchOption.exchangeRate=493
			wSearchOption.notes='V'

			TransferRequest wTransferRequest=new TransferRequest()
			wTransferRequest.amount=100
			wTransferRequest.reference=277

			User wUser=new User()
			wUser.name=CTSEnvironment.bvCompanyLogin
			wUser.entityId=CTSEnvironment.bvCompanyEnteMis
			wUser.serviceId=CTSEnvironment.bvService

			TransactionContextCIB wTransactionContextCIB=new TransactionContextCIB()

			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo datos de cotizacion.
			println( serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output"));

			def wO_referencia = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			Assert.assertNotNull("No se obtuvo referencia de la transaccion",wO_referencia)
			println ('RESPUESTA: @o_referencia----->' + wO_referencia)

			def wO_monto_operacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_monto_operacion")
			Assert.assertNotNull("No se obtuvo monto de la operacion",wO_monto_operacion)
			println ('RESPUESTA: @o_monto_operacion----->' + wO_monto_operacion)

			def wO_tasa = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_tasa")
			Assert.assertNotNull("No se obtuvo valor de tasa",wO_tasa)
			println ('RESPUESTA: @o_tasa----->' + wO_tasa)

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}
}
