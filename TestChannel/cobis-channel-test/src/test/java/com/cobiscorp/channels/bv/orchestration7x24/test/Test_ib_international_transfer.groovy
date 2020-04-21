package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.InternationalTransferRequest
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil


class Test_ib_international_transfer {

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
	
	@Test
	void testExecuteTransferToThird() {
		String ServiceName='InternationalTransfer'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.InternationalTransfer')

			//DTO IN
			
			InternationalTransferRequest wInternationalTransferRequest = new InternationalTransferRequest()
			TransactionContextCIB wTransactionContextCIB= new TransactionContextCIB()
			User wUser= new User()
			
			wInternationalTransferRequest.productId=CTSEnvironment.bvAccCtaCteType
			wInternationalTransferRequest.currencyIdUSD= CTSEnvironment.bvAccCtaCteCurrencyId
			wInternationalTransferRequest.productNumber= CTSEnvironment.bvAccCtaCteNumber
			
			wInternationalTransferRequest.destinationAccount = CTSEnvironment.bvDestinationAccCtaCteNumber
			wInternationalTransferRequest.currencyId= CTSEnvironment.bvAccCtaCteCurrencyId
			wInternationalTransferRequest.beneficiaryOffice = 1

			wInternationalTransferRequest.amount=17.88
			wInternationalTransferRequest.concept='TRANSFERENCIA INTERNACIONAL '
			wInternationalTransferRequest.negotiationDate = "01/01/2013"

			wInternationalTransferRequest.beneficiaryTypeDocument = "P"
			wInternationalTransferRequest.beneficiaryDocumentNumber = "12547896635"
			wInternationalTransferRequest.beneficiaryName = "JOHN SMITH"
			wInternationalTransferRequest.beneficiaryFirstLastName = "SMITH"
			wInternationalTransferRequest.beneficiarySecondLastName = "JOHNSON"
			wInternationalTransferRequest.beneficiaryBusinessName = ""
			wInternationalTransferRequest.beneficiaryCountry = 49
			wInternationalTransferRequest.beneficiaryAddress = "EEUU, NYC, FIRST AVE. 45, NY, NY, 25887"
			wInternationalTransferRequest.destinationAccount = "5478998551"
			
			wInternationalTransferRequest.beneficiaryBank = 21625
			wInternationalTransferRequest.beneficiaryOffice = 1
			wInternationalTransferRequest.beneficiaryBankName = "WELLS FARGO"
			wInternationalTransferRequest.swiftOrAba = "WFBIUS33XXX"
			wInternationalTransferRequest.beneficiaryIsSwiftOrAba = "S"
			
			wInternationalTransferRequest.intermediaryBank = 162
			wInternationalTransferRequest.intermediaryOffice = 55
			wInternationalTransferRequest.swiftOrAbaIntermediary = "CITIUS33ADR"
			wInternationalTransferRequest.intermediaryIsSwiftOrAba = "S"
			
			wTransactionContextCIB.authenticationRequired='N';
			
			wUser.entityId= CTSEnvironment.bvEnteMis;

			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inInternationalTransferRequest', wInternationalTransferRequest)
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

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
