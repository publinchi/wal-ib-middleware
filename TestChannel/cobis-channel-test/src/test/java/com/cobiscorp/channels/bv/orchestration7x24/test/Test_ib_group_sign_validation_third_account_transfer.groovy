package com.cobiscorp.channels.bv.orchestration7x24.test;
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
import com.cobiscorp.test.utils.BDD

public class Test_ib_group_sign_validation_third_account_transfer {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession)
	}

	/**
	 * Transferencias a terceros con doble autorizacion
	 */
	@Test
	void testGroupExecuteTransferToThird() {
	def SsnBranch = "0"
	
			SsnBranch = TransferToThirdAuthorization()
			println "OJO" + SsnBranch
			if (SsnBranch == "0") {
				println "No se obtuvo SSN: No se registró autorizacion por aprobar"
			}
			else
			GetAuthorizationInfo(SsnBranch)
	}
	 
	 def TransferToThirdAuthorization() {
		String ServiceName='GroupTransferToThirdAuthorization'
		def SsnBranch = "0"
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

			wProduct.productNumber= CTSEnvironment.bvGroupAccCtaAhoNumber
			wProduct.productId= Integer.valueOf(CTSEnvironment.bvGroupAccCtaAhoType)
			wProduct.currencyId= Integer.valueOf(CTSEnvironment.bvGroupAccCtaAhoCurrencyId)
			wProduct.productAbbreviation ='AHO'

			wProductDestino.productNumber= CTSEnvironment.bvDestinationAccCtaCteNumber
			wProductDestino.productId=CTSEnvironment.bvDestinationAccCtaCteType
			wProductDestino.currencyId= CTSEnvironment.bvDestinationAccCtaCteCurrencyId
			wProductDestino.productAlias="Grupo Producto Alias Destino"

			wTransferRequest.amount=500
			wTransferRequest.concept='TRANSFERENCIA A TERCEROS DESACOPLADOCON DOBLE AUTORIZACION'

			wTransactionContextCIB.authorizationRequired='S';

			wUser.name = CTSEnvironment.bvGroupLogin
			wUser.entityId= CTSEnvironment.bvGroupEnteMis;

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
			SsnBranch = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch")
			Assert.assertNotNull("No se obtuvo Numero de Secuencial",SsnBranch)
			println ('RESPUESTA: @o_ssn_branch----->' + SsnBranch)

		
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		}
		return SsnBranch
	}
	 
	 void GetAuthorizationInfo(def ssnBranch){
		 
		 def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		 def wRowAutorizaciones=null
		 
		 wRowAutorizaciones = sql.rows("select * from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_autorizador where au_ssn_branch=?",ssnBranch)
		 
		 Assert.assertEquals("No se encontro registro en la tabla bv_autorizador",wRowAutorizaciones.size(), 1)
		 println ('RESPUESTA: Se creo la autorizacion pendiente de aprobar ---->'+wRowAutorizaciones)
		 
	 }

}
