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
import cobiscorp.ecobis.internetbanking.webapp.services.dto.*

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * 
 * @author dguerra
 * @since Aug 5, 2014
 * @version 1.0.0
 */

public class Test_ib_company_detail_loan_orchestration {

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
	 * this method return the details of loan for a user  type Company
	 */

	@Test
	void testGetDetailLoanQueryCompany() {
		def ServiceName= 'testGetDetailLoanQueryCompany'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecuci�n del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.LoanBalance.GetLoanBalance')


			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.currencyId = CTSEnvironment.bvLoanCurrencyId
			wEnquiryRequest.userName = CTSEnvironment.bvCompanyLogin
			wEnquiryRequest.productNumber = CTSEnvironment.bvCompanyLoanNumber
			wEnquiryRequest.productId = CTSEnvironment.bvLoanType

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			Assert.assertTrue('Filas Vacias returnResponseLoanBalance', serviceResponseTO.getData().get('returnLoanBalance').collect().size()>0)

			println "ExecuteService---> OK"
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Test --->Ejecutado detalle de prestamo (Group) con param. de entrada:"
			println "Test --->login---> " + wEnquiryRequest.userName
			println "Test --->Numero Pr�stamo---> " + wEnquiryRequest.productNumber


			LoanBalance[] oResponseLoanBalance= serviceResponseTO.data.get('returnLoanBalance').collect().toArray()
			for (var in oResponseLoanBalance) {

				println "****  Detalle del Pr�stamo ****"

				println " ***  N�mero de Operaci�n ---> "+var.productNumber
				println " ***  Nombre del Cliente ---> "+var.entityName
				println " ***  Tipo de Pr�stamo ---> "+var.operationType
				println " ***  D�a de Pago ---> "+var.monthlyPaymentDay
				println " ***  Estado de la Deuda ---> "+var.status
				println " ***  Fecha de �ltimo Pago ---> "+var.lastPaymentDate
				println " ***  Fecha de Vencimiento ---> "+var.expirationDate
				println " ***  Oficial de Cr�dito ---> "+var.executive
				println " ***  Fecha de Inicio de Operaci�n ---> "+var.initialDate

				println " ***  Tasa de Inter�s Corriente ---> "+var.ordinaryInterestRate
				println " ***  Tasa de Inter�s Moratorio ---> "+var.arrearsInterestRate
				println " ***  Saldo Capital ---> "+var.capitalBalance
				println " ***  Saldo Total de la Deuda ---> "+var.totalBalance

				println " ***  Monto Original ---> "+var.initialAmount

				println " ***  D�as de Atraso ---> "+var.arrearsDays
				println " ***  Capital Vencido ---> "+var.overdueCapital
				println " ***  Inter�s Vencido ---> "+var.overdueInterest
				println " ***  Valor de Mora Vencido ---> "+var.overdueArrearsValue
				println " ***  Otros Rublos Vencido ---> "+var.overdueAnotherItems
				println " ***  Total Vencidos ---> "+var.overdueTotal

				println " ***  Pr�ximo Pago ---> "+var.nextPaymentDate
				println " ***  Valor Pr�ximo Pago ---> "+var.nextPaymentValue

			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
	}
}
