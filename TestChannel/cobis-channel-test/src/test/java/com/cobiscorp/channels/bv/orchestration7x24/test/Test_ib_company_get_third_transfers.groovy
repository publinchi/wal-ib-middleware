package com.cobiscorp.channels.bv.orchestration7x24.test;
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.businessbanking.services.dto.Notification
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.Payment
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

/**
 * 
 * @author gyagual
 *
 */
public class Test_ib_company_get_third_transfers {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany()
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
	 * Get agreement service
	 */
	@Test
	void testGetCompanyThirdTransfers() {
		String ServiceName='CompanyGetThirdTransfers'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Transfer.GetTransfersToThird')
										   
			//DTO IN
			 EnquiryRequest wEnquiryRequest = new EnquiryRequest();
			 SearchOption wSearchOption = new SearchOption();
				wEnquiryRequest.productNumber=  CTSEnvironment.bvCompanyThirdTransfersNumber; 
				 wEnquiryRequest.dateFormatId= CTSEnvironment.bvDateFormat;
				 wSearchOption.initialDate= CTSEnvironment.bvInitialDate;
				 wSearchOption.finalDate = CTSEnvironment.bvFinalDate;
				 wSearchOption.sequential = 0
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)
			
			
			println "******************** RETURN ********************"
			Payment[] oResponsePayment= serviceResponseTO.data.get('returnPayment').collect().toArray()
			
			for (var in oResponsePayment) {
				println "*******"
				Assert.assertNotNull("Fecha is null ",var.paymentDate)
				Assert.assertNotNull("Cuenta is null ",var.creditAccount)
				Assert.assertNotNull("Monto is null ",var.ammount)
				println " ***  Fecha ---> "+var.paymentDate
				println " ***  Cuenta ---> "+var.creditAccount
				println " ***  Monto ---> "+var.ammount
				
			}				
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLoginEmpresa,initSession)
		}
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLoginEmpresa,initSession)
	}
}
