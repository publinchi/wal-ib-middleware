package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * orchestration7x24-ib-self-account-transfer
 * 
 * @author schancay
 *
 */
class Test_ib_self_account_transfer {

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
	 * Transferencias entre cuentas propias normal
	 */
	@Test
	void testExecuteTransferBetweenOwnAccounts() {
		String ServiceName='TransferBetweenOwnAccounts'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferBetweenOwnAccounts')

			//DTO IN
			Product wProduct=new Product()
			Product wProductDestino=new Product()
			TransferRequest wTransferRequest = new TransferRequest()
			User wUser= new User()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()

			wProduct.productNumber= '10410108275406111'
			wProduct.productId=CTSEnvironment.bvAccCtaCteType
			wProduct.currencyId= CTSEnvironment.bvAccCtaCteCurrencyId
			wProduct.productAbbreviation ='CTE'

			wProductDestino.productNumber= CTSEnvironment.bvAccCtaAhoNumber
			wProductDestino.productId=CTSEnvironment.bvAccCtaAhoType
			wProductDestino.currencyId= CTSEnvironment.bvAccCtaAhoCurrencyId

			wTransferRequest.userName = CTSEnvironment.bvLogin
			wTransferRequest.amount=5.36
			wTransferRequest.concept='TRANSFERENCIA ENTRE CUENTAS PROPIAS ACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.entityId= CTSEnvironment.bvEnteMis;

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct', wProduct)
			serviceRequestTO.addValue('inProduct2', wProductDestino)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Obteniendo SSN
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			Assert.assertNotNull("No se obtuvo Numero de Comprobante",wOref)
			println ('RESPUESTA: @o_referencia----->' + wOref)

			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowTranMonet=null
			wRowTranMonet = sql.rows("select * from cob_remesas"+CTSEnvironment.DB_SEPARATOR+"re_tran_monet where tm_ssn_local=?",wOref)
			Assert.assertEquals("No se encontro registro en la re_tran_monet",wRowTranMonet.size(), 1)
			println ('RESPUESTA: wRowTranMonet----->'+wRowTranMonet)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Transferencia entre cuentas propias con fondos insuficientes
	 */
	//@Test
	void testExecuteTransferBetweenOwnAccountsOverdraft() {
		String ServiceName='TransferBetweenOwnAccounts'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferBetweenOwnAccounts')

			//DTO IN
			Product wProduct=new Product()
			Product wProductDestino=new Product()
			TransferRequest wTransferRequest = new TransferRequest()
			User wUser= new User()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()

			wProduct.productNumber= CTSEnvironment.bvAccCtaCteNumber
			wProduct.productId=CTSEnvironment.bvAccCtaCteType
			wProduct.currencyId= CTSEnvironment.bvAccCtaCteCurrencyId
			wProduct.productAbbreviation ='CTE'

			wProductDestino.productNumber= CTSEnvironment.bvAccCtaAhoNumber
			wProductDestino.productId=CTSEnvironment.bvAccCtaAhoType
			wProductDestino.currencyId= CTSEnvironment.bvAccCtaAhoCurrencyId

			wTransferRequest.userName = CTSEnvironment.bvLogin
			wTransferRequest.amount=9999999900
			wTransferRequest.concept='TRANSFERENCIA ENTRE CUENTAS PROPIAS DESACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.entityId= CTSEnvironment.bvEnteMis;

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct', wProduct)
			serviceRequestTO.addValue('inProduct2', wProductDestino)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertFalse(message, serviceResponseTO.success)
			Assert.assertEquals(codeError, "1875147");//[sp_valida_limites_bv] EL MONTO DE LA TRANSACCION ES SUPERIOR AL LIMITE MAXIMO PERMITIDO

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Transferencia entre cuentas propias con validacion de Firmas
	 */
	//@Test
	void testExecuteTransferBetweenOwnAccountsValidateSignatures() {
		String ServiceName='TransferBetweenOwnAccounts'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferBetweenOwnAccounts')

			//DTO IN
			Product wProduct=new Product()
			Product wProductDestino=new Product()
			TransferRequest wTransferRequest = new TransferRequest()
			User wUser= new User()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()

			wProduct.productNumber= CTSEnvironment.bvAccCtaAhoNumber
			wProduct.productId=CTSEnvironment.bvAccCtaAhoType
			wProduct.currencyId= CTSEnvironment.bvAccCtaAhoCurrencyId
			wProduct.productAbbreviation ='AHO'

			wProductDestino.productNumber= CTSEnvironment.bvAccCtaCteNumber
			wProductDestino.productId=CTSEnvironment.bvAccCtaCteType
			wProductDestino.currencyId= CTSEnvironment.bvAccCtaCteCurrencyId

			wTransferRequest.userName = CTSEnvironment.bvLogin
			wTransferRequest.amount=100
			wTransferRequest.concept='TRANSFERENCIA ENTRE CUENTAS PROPIAS DESACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.entityId= CTSEnvironment.bvEnteMis;

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransferRequest', wTransferRequest)
			serviceRequestTO.addValue('inProduct', wProduct)
			serviceRequestTO.addValue('inProduct2', wProductDestino)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError1=''
			def codeError2=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError1=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				codeError2=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(1)).code
			}
			if(serviceResponseTO.success)
			{
				Assert.assertTrue(message, serviceResponseTO.success)
			}else{
				Assert.assertFalse(message, serviceResponseTO.success)
				Assert.assertEquals(codeError1, "201004");//[sp_resultados_bv]  CUENTA NO EXISTE
				Assert.assertEquals(codeError2, "1875131");//[sp_consulta_firmantes_bv]  CLIENTE NO TIENE RELACION CON LA CUENTA
			}
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}

