package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert;
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseBalanceAccount;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseBalanceDestinationAccount;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequestACH
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_personal_Transfer_ACH {

	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void test_ib_personal_Transfer_ACH(){		
		String ServiceName='test_ib_personal_Transfer_ACH'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.TransferACH')
			//DTO IN
			Product wProduct=new Product();
			Product wProduct1=new Product();
			TransferRequestACH wTransferRequestACH = new TransferRequestACH();
			User wUser= new User();
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB();
			
			wUser.entityId= CTSEnvironment.bvEnteMis;
			
			wTransferRequestACH.userName=CTSEnvironment.bvLogin;
			wTransferRequestACH.amount=25.36;
			wTransferRequestACH.destinationBankName="BANCO DESTINO";
			wTransferRequestACH.transitRoute="000001591";
			wTransferRequestACH.beneficiaryName="JOSE PEREZ";
			wTransferRequestACH.beneficiaryId="1234567890";
			wTransferRequestACH.beneficiaryPhone="625478963";
			wTransferRequestACH.concept="TRANSFERENCIA ACH REGRESION";
			
			wTransferRequestACH.authorizationRequired="N"
			
			wProduct1.productId=3;
			wProduct1.currencyId=0;
			wProduct1.productNumber="1234567890";
			
			wProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId;
			wProduct.productId=CTSEnvironment.bvAccCtaCteType;
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber;
			
			serviceRequestTO.addValue("inUser", wUser);
			serviceRequestTO.addValue("inTransferRequestACH", wTransferRequestACH);
			serviceRequestTO.addValue("inTransactionContextCIB", wTransactionContextCIB);
			serviceRequestTO.addValue("inProduct", wProduct);
			serviceRequestTO.addValue("inProduct2", wProduct1);
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			
			ResponseBalanceAccount [] oResponseBalanceAccount=serviceResponseTO.data.get('returnResponseBalanceAccount').collect().toArray()
			for(var in oResponseBalanceAccount){
				Assert.assertNotNull("result_submit_rpc is null ",var.result_submit_rpc)
				Assert.assertNotNull("availableBalance is null ",var.availableBalance)
				Assert.assertNotNull("accountingBalance is null ",var.accountingBalance)
				Assert.assertNotNull("toDrawBalance is null ",var.toDrawBalance)
				Assert.assertNotNull("balance12Hours is null ",var.balance12Hours)
				Assert.assertNotNull("balance24Hours is null ",var.balance24Hours)
				Assert.assertNotNull("balanceRemittances is null ",var.balanceRemittances)
				Assert.assertNotNull("blockedAmount is null ",var.blockedAmount)
				Assert.assertNotNull("numberOfLocks is null ",var.numberOfLocks)
				Assert.assertNotNull("numberOfBlocksPerAmount is null ",var.numberOfBlocksPerAmount)
				Assert.assertNotNull("officeAccount is null ",var.officeAccount)
				Assert.assertNotNull("bankingProduct is null ",var.bankingProduct)
				Assert.assertNotNull("status is null ",var.status)
				Assert.assertNotNull("ssnHost is null ",var.ssnHost)
				Assert.assertNotNull("overdrawnAmount is null ",var.overdrawnAmount)
				Assert.assertNotNull("dateHost is null ",var.dateHost)
				Assert.assertNotNull("nameAccount is null ",var.nameAccount)
				Assert.assertNotNull("dateLastMovement is null ",var.dateLastMovement)
				Assert.assertNotNull("statusAccount is null ",var.statusAccount)

				
				println ('------------ RESULTADO -----------')
				println " *** result_submit_rpc		    ---> "+var.result_submit_rpc
				println " *** availableBalance		    ---> "+var.availableBalance
				println " *** accountingBalance		    ---> "+var.accountingBalance
				println " *** toDrawBalance		    ---> "+var.toDrawBalance
				println " *** balance12Hours		    ---> "+var.balance12Hours
				println " *** balance24Hours		    ---> "+var.balance24Hours
				println " *** balanceRemittances		    ---> "+var.balanceRemittances
				println " *** blockedAmount		    ---> "+var.blockedAmount
				println " *** numberOfLocks		    ---> "+var.numberOfLocks
				println " *** numberOfBlocksPerAmount		    ---> "+var.numberOfBlocksPerAmount
				println " *** officeAccount		    ---> "+var.officeAccount
				println " *** bankingProduct		    ---> "+var.bankingProduct
				println " *** status		    ---> "+var.status
				println " *** ssnHost		    ---> "+var.ssnHost
				println " *** overdrawnAmount		    ---> "+var.overdrawnAmount
				println " *** dateHost		    ---> "+var.dateHost
				println " *** nameAccount		    ---> "+var.nameAccount
				println " *** dateLastMovement		    ---> "+var.dateLastMovement
				println " *** statusAccount		    ---> "+var.statusAccount

			}
			
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
			def wOretorno = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_retorno")
			def wOcondicion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_condicion")
			def wOautorizacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_autorizacion")
			def wOssnBranch= serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch")
			
			println "Test ---> Ejecutado con param. de salida:"
			println "Test ---> o_referencia-> " + wOref
			println "Test ---> o_retorno-> " + wOretorno
			println "Test ---> o_condicion-> " + wOcondicion
			println "Test ---> o_autorizacion-> " + wOautorizacion
			println "Test ---> o_ssn_branch-> " + wOssnBranch

			
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
