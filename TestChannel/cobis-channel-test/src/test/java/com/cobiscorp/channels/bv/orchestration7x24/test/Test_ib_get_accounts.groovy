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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.EntityServiceProduct
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil


/***
 * 
 *
 * @author cecheverria
 *
 */
class Test_ib_get_accounts {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural()
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
	 * Consulta de Detalles de Cuenta de un cliente
	 */
	@Test
	void testGetInformationAccountByClient() {
		def ServiceName='testGetInformationAccountByClient'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetAccounts')

			//DTO IN
			EnquiryRequest wEnquiryRequest=new EnquiryRequest()
			wEnquiryRequest.operation="A"
			wEnquiryRequest.forInternetBankingNet="S"
			wEnquiryRequest.userName=CTSEnvironment.bvLogin
			wEnquiryRequest.productId =4;
			wEnquiryRequest.currencyId = 0;
			wEnquiryRequest.roleId =  0;
			
			
			//wEnquiryRequest.productId=CTSEnvironment.bvAccCtaCteType

			SearchOption wSearchOption=new SearchOption()
			wSearchOption.numberOfResults=0
			wSearchOption.lastResult=0
			wSearchOption.criteria2="N"

			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success)

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnEntityServiceProduct').collect().size()>0)
			EntityServiceProduct[] oEntityServiceProduct= serviceResponseTO.data.get('returnEntityServiceProduct').collect().toArray()

			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnProduct').collect().size()>0)
			Product[] oProduct= serviceResponseTO.data.get('returnProduct').collect().toArray()
			for (prod in oProduct) {
				println( "ProductNumber:"+prod.productNumber+" - Moneda: "+String.valueOf(prod.currencyId))
			}

		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}