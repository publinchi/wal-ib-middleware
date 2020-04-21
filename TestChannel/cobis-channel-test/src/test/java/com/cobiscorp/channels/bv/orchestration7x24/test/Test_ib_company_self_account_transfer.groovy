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
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * orchestration7x24-ib-self-account-transfer
 * 
 * @author schancay
 *
 */
class Test_ib_company_self_account_transfer {

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
	 * Transferencia entre cuentas propias con fondos insuficientes Empresa
	 */
	@Test
	void testCompanyExecuteTransferBetweenOwnAccountsOverdraft() {
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

			wProduct.productNumber= CTSEnvironment.bvCompanyAccCtaAhoNumber
			wProduct.productId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoType)
			wProduct.currencyId= Integer.valueOf(CTSEnvironment.bvCompanyAccCtaAhoCurrencyId)
			wProduct.productAbbreviation ='AHO'

			wProductDestino.productNumber= CTSEnvironment.bvCompanyAccCtaCteNumber
			wProductDestino.productId= CTSEnvironment.bvCompanyAccCtaCteType
			wProductDestino.currencyId= CTSEnvironment.bvCompanyAccCtaCteCurrencyId

			wTransferRequest.userName = CTSEnvironment.bvCompanyLogin
			wTransferRequest.amount=100
			wTransferRequest.concept='TRANSFERENCIA ENTRE CUENTAS PROPIAS DESACOPLADO'

			wTransactionContextCIB.authenticationRequired='N';

			wUser.entityId= CTSEnvironment.bvCompanyEnteMis

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
			Assert.assertTrue(message, serviceResponseTO.success)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
		}
	}
}

