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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.AccountStatement
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 *
 * @author dguerra
 * @since Jul 24, 2014
 * @version 1.0.0
 */

public class Test_ib_detail_ah_accounts_query_orchestration {
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
	void testGetSavingsAccountOperationDetail() {
		def ServiceName= 'testGetSavingsAccountOperationDetail'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.AccountOperations.GetSavingsAccountOperationDetail')


			//DTO IN
			EnquiryRequest wEnquiryRequest = new EnquiryRequest()
			wEnquiryRequest.currencyId = CTSEnvironment.bvAccCtaAhoCurrencyId
			wEnquiryRequest.productId = CTSEnvironment.bvAccCtaAhoType
			wEnquiryRequest.productNumber = CTSEnvironment.bvAccCtaAhoNumber
			wEnquiryRequest.userName = CTSEnvironment.bvLogin
			wEnquiryRequest.transactionDate = CTSEnvironment.bvTransactionDate
			wEnquiryRequest.dateFormatId = CTSEnvironment.bvDateFormat

			AccountStatement wAccountStatement = new AccountStatement()
			wAccountStatement.sequential = 655970888
			wAccountStatement.alternateCode = 200
			wAccountStatement.operationType = 253

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inAccountStatement', wAccountStatement)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)


			//Valido si fue exitoso la ejecucion
			def message=''

			if(serviceResponseTO.success){
				println "ExecuteService---> OK"
				println ('RESPUESTA:------------------>' )
				Assert.assertTrue(message, serviceResponseTO.success)
				println "Test --->Ejecutado con param. de entrada:"
				println "Test --->Producto---> " + wEnquiryRequest.productId
				println "Test --->Numero de Cuenta----> " + wEnquiryRequest.productNumber


				AccountStatement[] oResponseSavingAccount= serviceResponseTO.data.get('returnAccountStatement').collect().toArray()
				for (var in oResponseSavingAccount) {

					println "**** Informacion del Movimiento ****"
					println " ***  No. de Solicitud ---> "+var.documentNumber
					println " ***  Fecha ---> "+var.transactionDate
					println " ***  Hora ---> "+var.hour
					println " ***  Tipo Cuenta ---> "+var.typeDC
					println " ***  Descripcion ---> "+var.description
					println " ***  Concepto ---> "+var.concept
					println " ***  Codigo de causa ---> "+var.causeId
					println " ***  Causa ---> "+var.cause
					println " ***  Monto ---> "+var.amount
					println " ***  Cheques propios ---> "+var.ownChecksBalance
					println " ***  Cheques locales ---> "+var.localChecksBalance
					println " ***  Cheques de otras plazas ---> "+var.internationalChecksBalance
					println " ***  Cheques totales ---> "+var.totalChecksBalance
				}

			}
			else{
				println "ExecuteService---> ERROR"

				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}

}
