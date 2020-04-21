package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jveloz
 *
 */
class Test_ib_group_time_deposit_query {

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
		virtualBankingBase.closeSessionGroup(initSession);
	}

	/**
	 * Metodo que ejecuta el servicio detalle de operaciones
	 */
	@Test
	void testgetTimeDepositDetail() {
		println ' ****** Prueba Regresión testgetTimeDepositDetail Grupo************* '
		def ServiceName= 'getTimeDepositDetail'
		try{
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Enquiries.FixedTermDepositBalance.GetFixedTermDepositBalance');

			EnquiryRequest wEnquiryRequest = new EnquiryRequest();
			wEnquiryRequest.transactionId = 1800022
			wEnquiryRequest.userName = CTSEnvironment.bvGroupLogin;//@i_login
			wEnquiryRequest.dateFormatId=CTSEnvironment.bvDateFormat;//@i_formato_fecha
			wEnquiryRequest.productId=CTSEnvironment.bvAccDpfType;//@i_product
			wEnquiryRequest.productNumber=CTSEnvironment.bvGroupDpfNumber;//@i_cta
			wEnquiryRequest.currencyId=CTSEnvironment.bvGroupDpfCurrencyId;//@i_mon
			
			serviceRequestTO.addValue('inEnquiryRequest', wEnquiryRequest);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);

			def message='';

			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}

			Assert.assertTrue(message, serviceResponseTO.success);
			FixedTermDepositBalance[] oResponse= serviceResponseTO.data.get('returnFixedTermDepositBalance').collect().toArray();
			println "******** Resulset *********"
			Assert.assertNotNull("Product Number is null ",oResponse[0].productNumber);
			Assert.assertNotNull("Openning Date is null ",oResponse[0].openningDate);
			Assert.assertNotNull("Expiration Date is null ",oResponse[0].expirationDate);
			Assert.assertNotNull("Capital Balance is null ",oResponse[0].capitalBalance);

			Assert.assertNotNull("Total Interest Income is null ",oResponse[0].totalInterestIncome);
			Assert.assertNotNull("Rate is null ",oResponse[0].rate);
			Assert.assertNotNull("Term is null ",oResponse[0].term);
			Assert.assertNotNull("Capital Balance Maturity is null ",oResponse[0].capitalBalanceMaturity);

			Assert.assertNotNull("Automatic Renewal is null ",oResponse[0].automaticRenewal);
			Assert.assertNotNull("Is Compounded is null ",oResponse[0].isCompounded);
			Assert.assertNotNull("Frecuency Of Payment is null ",oResponse[0].frecuencyOfPayment);
			Assert.assertNotNull("Account Officer is null ",oResponse[0].accountOfficer);

			Assert.assertNotNull("Value Date is null ",oResponse[0].valueDate);
			Assert.assertNotNull("Calculation Base is null ",oResponse[0].calculationBase);
			Assert.assertNotNull("Product Abbreviation is null ",oResponse[0].productAbbreviation);
			Assert.assertNotNull("Product Alias is null ",oResponse[0].productAlias);
			//*******************************************************
			println"Product Number is null "+ oResponse[0].productNumber;
			println"Openning Date is null "+ oResponse[0].openningDate;
			println"Expiration Date is null "+ oResponse[0].expirationDate;
			println"Capital Balance is null "+ oResponse[0].capitalBalance;

			println"Total Interest Income is null "+ oResponse[0].totalInterestIncome;
			println"Rate is null "+ oResponse[0].rate;
			println"Term is null "+ oResponse[0].term;
			println"Capital Balance Maturity is null "+ oResponse[0].capitalBalanceMaturity;

			println"Automatic Renewal is null "+ oResponse[0].automaticRenewal;
			println"Is Compounded is null "+ oResponse[0].isCompounded;
			println"Frecuency Of Payment is null "+ oResponse[0].frecuencyOfPayment;
			println"Account Officer is null "+ oResponse[0].accountOfficer;

			println"Value Date is null "+ oResponse[0].valueDate;
			println"Calculation Base is null "+ oResponse[0].calculationBase;
			println"Product Abbreviation is null "+ oResponse[0].productAbbreviation;
			println"Product Alias is null "+ oResponse[0].productAlias;


		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession);
			Assert.fail();
		}
	}
}
