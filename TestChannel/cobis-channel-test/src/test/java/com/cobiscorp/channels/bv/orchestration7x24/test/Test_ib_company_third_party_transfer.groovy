package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
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

/**
 * Transfers to third
 * 
 * @since May 13, 2014
 * @author schancay
 * @version 1.0.0
 *
 */
class Test_ib_company_third_party_transfer {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionCompany(initSession)
	}

	/**
	 * Transferencias a terceros - transferencia normal
	 */
	@Test
	void testCompanyExecuteTransferToThird() {
		String ServiceName='TransferToThird'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferToThird')

			//DTO IN
			Product wProduct=new Product()
			Product wProductDestino=new Product()
			TransferRequest wTransferRequest = new TransferRequest()
			User wUser= new User()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()

			wProduct.productNumber= CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct.productId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoType)
			wProduct.currencyId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoCurrencyId)
			wProduct.productAbbreviation ='AHO'

			wProductDestino.productNumber= CTSEnvironment.bvDestinationAccCtaCteNumber
			wProductDestino.productId=CTSEnvironment.bvDestinationAccCtaCteType
			wProductDestino.currencyId= CTSEnvironment.bvDestinationAccCtaCteCurrencyId
			wProductDestino.productAlias="PEPITO PEREZ TEST"

			wTransferRequest.amount=500
			wTransferRequest.concept='TRANSFERENCIA A TERCEROS DESACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.name = CTSEnvironment.bvCompanyLogin
			wUser.entityId= CTSEnvironment.bvCompanyEnteMis;

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
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
		}
	}
	/**
	 * Transferencias a terceros - transferencia con sobregiro
	 */
	@Test
	void testCompanyExecuteTransferToThirdOverdraft() {
		String ServiceName='TransferToThird'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferToThird')

			//DTO IN
			Product wProduct=new Product()
			Product wProductDestino=new Product()
			TransferRequest wTransferRequest = new TransferRequest()
			User wUser= new User()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()

			wProduct.productNumber= CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct.productId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoType)
			wProduct.currencyId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoCurrencyId)
			wProduct.productAbbreviation ='AHO'

			wProductDestino.productNumber= CTSEnvironment.bvDestinationAccCtaCteNumber
			wProductDestino.productId=CTSEnvironment.bvDestinationAccCtaCteType
			wProductDestino.currencyId= CTSEnvironment.bvDestinationAccCtaCteCurrencyId
			wProductDestino.productAlias="PEPITO PEREZ TEST"

			wTransferRequest.amount=90000000
			wTransferRequest.concept='TRANSFERENCIA A TERCEROS DESACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.name = CTSEnvironment.bvCompanyLogin
			wUser.entityId= CTSEnvironment.bvCompanyEnteMis;

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
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
		}
	}
}
