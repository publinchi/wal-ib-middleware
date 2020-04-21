package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferInternationalDetail
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_get_Transfer_International_Details {
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
	
	@Test
	void testGetTransferInternationalDetails(){
		def ServiceName = "testGetTransferInternationalDetails"
		
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.InternationalTransfer.GetInternationalTransferDetail')
			
			//DTO IN
			SearchOption wSearchOption = new SearchOption()
			Product wProduct = new Product()
			
			wSearchOption.setCriteria("TRR00108000011")
			wSearchOption.setInitialCheck(1000)
			wSearchOption.setCriteria2("NR")			
			wSearchOption.setSequential(0)
			wSearchOption.setInitialDate("01/01/2008")
			wSearchOption.setFinalDate("09/09/2008")
			wSearchOption.setMode(101)
			wSearchOption.setLastResult("PO")
			wSearchOption.setNumberOfResults(1)
			wSearchOption.setNotes("testCts")
			wProduct.setProductNumber("01202000052") //("10410108275406111")
			wProduct.setCurrencyId(0)
			wProduct.setProductId(3)
			
			serviceRequestTO.addValue('inSearchOption', wSearchOption)
			serviceRequestTO.addValue('inProduct', wProduct)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			println "Services Response " + serviceResponseTO
			
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println "Assert.assertTrue(message, serviceResponseTO.success)"
			TransferInternationalDetail[] oResponseTransferInternationalDetail= serviceResponseTO.data.get('returnTransferInternationalDetail').collect().toArray()
			println "oResponseInternationalTransfersReceived.toString() ==> " + oResponseTransferInternationalDetail.toString();
			for (var in oResponseTransferInternationalDetail){
				Assert.assertNotNull("date_transaction is null ",var.date_transaction)
				Assert.assertNotNull("id_referency is null ",var.id_referency)
				Assert.assertNotNull("account_debit is null ",var.account_debit)
				Assert.assertNotNull("account_type is null ",var.account_type)
				Assert.assertNotNull("account_name is null ",var.account_name)
				Assert.assertNotNull("ammount is null ",var.ammount)
				Assert.assertNotNull("money is null ",var.money)
				Assert.assertNotNull("referency is null ",var.referency)
				Assert.assertNotNull("beneficiary_name is null ",var.beneficiary_name)
				Assert.assertNotNull("beneficiary_address_complete is null ",var.beneficiary_address_complete)
				Assert.assertNotNull("beneficiary_country is null ",var.beneficiary_country)
				Assert.assertNotNull("beneficiary_city is null ",var.beneficiary_city)
				Assert.assertNotNull("beneficiary_address is null ",var.beneficiary_address)
				Assert.assertNotNull("beneficiary_account is null ",var.beneficiary_account)
				Assert.assertNotNull("bank_beneficiary_country is null ",var.bank_beneficiary_country)
				Assert.assertNotNull("bank_beneficiary_name is null ",var.bank_beneficiary_name)
				Assert.assertNotNull("bank_beneficiary_description is null ",var.bank_beneficiary_description)
				Assert.assertNotNull("bank_beneficiary_address is null ",var.bank_beneficiary_address)
				Assert.assertNotNull("bank_beneficiary_swift is null ",var.bank_beneficiary_swift)
				Assert.assertNotNull("type_address is null ",var.type_address)
				Assert.assertNotNull("bank_intermediary_country is null ",var.bank_intermediary_country)
				Assert.assertNotNull("bank_intermediary_name is null ",var.bank_intermediary_name)
				Assert.assertNotNull("bank_intermediary_description is null ",var.bank_intermediary_description)
				Assert.assertNotNull("bank_intermediary_address is null ",var.bank_intermediary_address)
				Assert.assertNotNull("bank_intermediary_swift is null ",var.bank_intermediary_swift)
				Assert.assertNotNull("type_address_intermediary is null ",var.type_address_intermediary)
				Assert.assertNotNull("cost_transaction is null ",var.cost_transaction)
				Assert.assertNotNull("beneficiary_continent_code is null ",var.beneficiary_continent_code)
				Assert.assertNotNull("beneficiary_continent is null ",var.beneficiary_continent)
				Assert.assertNotNull("transaction_code is null ",var.transaction_code)
				Assert.assertNotNull("message_type is null ",var.message_type)
				Assert.assertNotNull("sucursal_code is null ",var.sucursal_code)
				Assert.assertNotNull("sucursal is null ",var.sucursal)
				Assert.assertNotNull("bank_beneficiary_id is null ",var.bank_beneficiary_id)
				Assert.assertNotNull("beneficiary_country_id is null ",var.beneficiary_country_id)
				Assert.assertNotNull("beneficiary_city_id is null ",var.beneficiary_city_id)
				Assert.assertNotNull("payer_city is null ",var.payer_city)
				Assert.assertNotNull("payer_name is null ",var.payer_name)
				Assert.assertNotNull("id is null ",var.id)
				Assert.assertNotNull("ben_country_id is null ",var.ben_country_id)
				Assert.assertNotNull("ben_city_id is null ",var.ben_city_id)
				/*
				Assert.assertNotNull("bco_swift_ben is null ",var.bco_swift_ben)
				Assert.assertNotNull("bco_swift_inter is null ",var.bco_swift_inter)
				Assert.assertNotNull("bco_pais_ben is null ",var.bco_pais_ben)
				Assert.assertNotNull("bco_pais_int is null ",var.bco_pais_int)
				Assert.assertNotNull("bco_ben_id is null ",var.bco_ben_id)
				Assert.assertNotNull("bco_int_id is null ",var.bco_int_id)
				Assert.assertNotNull("bco_dir_ben_id is null ",var.bco_dir_ben_id)
				Assert.assertNotNull("bco_dir_int_id is null ",var.bco_dir_int_id)
				Assert.assertNotNull("beneficiaryFirstLastName is null ",var.beneficiaryFirstLastName)
				Assert.assertNotNull("beneficiarySecondLastName is null ",var.beneficiarySecondLastName)
				Assert.assertNotNull("beneficiaryBusinessName is null ",var.beneficiaryBusinessName)
				Assert.assertNotNull("beneficiaryTypeDocument is null ",var.beneficiaryTypeDocument)
				Assert.assertNotNull("beneficiaryDocumentNumber is null ",var.beneficiaryDocumentNumber)
				Assert.assertNotNull("currencyIdUSD is null ",var.currencyIdUSD)
				Assert.assertNotNull("quote is null ",var.quote)
				Assert.assertNotNull("beneficiaryTypeDocumentName is null ",var.beneficiaryTypeDocumentName)
				Assert.assertNotNull("codeNegotiation is null ",var.codeNegotiation)
				Assert.assertNotNull("beneficiaryEmail1 is null ",var.beneficiaryEmail1)
				Assert.assertNotNull("beneficiaryEmail2 is null ",var.beneficiaryEmail2)*/

				
				println ('------------ RESULTADO -----------')
				println " *** date_transaction              ---> "+var.date_transaction
				println " *** id_referency                  ---> "+var.id_referency
				println " *** account_debit                 ---> "+var.account_debit
				println " *** account_type                  ---> "+var.account_type
				println " *** account_name                  ---> "+var.account_name
				println " *** ammount                       ---> "+var.ammount
				println " *** money                         ---> "+var.money
				println " *** referency                     ---> "+var.referency
				println " *** beneficiary_name              ---> "+var.beneficiary_name
				println " *** beneficiary_address_complete  ---> "+var.beneficiary_address_complete
				println " *** beneficiary_country           ---> "+var.beneficiary_country
				println " *** beneficiary_city              ---> "+var.beneficiary_city
				println " *** beneficiary_address           ---> "+var.beneficiary_address
				println " *** beneficiary_account           ---> "+var.beneficiary_account
				println " *** bank_beneficiary_country      ---> "+var.bank_beneficiary_country
				println " *** bank_beneficiary_name         ---> "+var.bank_beneficiary_name
				println " *** bank_beneficiary_description  ---> "+var.bank_beneficiary_description
				println " *** bank_beneficiary_address      ---> "+var.bank_beneficiary_address
				println " *** bank_beneficiary_swift        ---> "+var.bank_beneficiary_swift
				println " *** type_address                  ---> "+var.type_address
				println " *** bank_intermediary_country     ---> "+var.bank_intermediary_country
				println " *** bank_intermediary_name        ---> "+var.bank_intermediary_name
				println " *** bank_intermediary_description ---> "+var.bank_intermediary_description
				println " *** bank_intermediary_address     ---> "+var.bank_intermediary_address
				println " *** bank_intermediary_swift       ---> "+var.bank_intermediary_swift
				println " *** type_address_intermediary     ---> "+var.type_address_intermediary
				println " *** cost_transaction              ---> "+var.cost_transaction
				println " *** beneficiary_continent_code    ---> "+var.beneficiary_continent_code
				println " *** beneficiary_continent         ---> "+var.beneficiary_continent
				println " *** transaction_code              ---> "+var.transaction_code
				println " *** message_type                  ---> "+var.message_type
				println " *** sucursal_code                 ---> "+var.sucursal_code
				println " *** sucursal                      ---> "+var.sucursal
				println " *** bank_beneficiary_id           ---> "+var.bank_beneficiary_id
				println " *** beneficiary_country_id        ---> "+var.beneficiary_country_id
				println " *** beneficiary_city_id           ---> "+var.beneficiary_city_id
				println " *** payer_city                    ---> "+var.payer_city
				println " *** payer_name                    ---> "+var.payer_name
				println " *** id                            ---> "+var.id
				println " *** ben_country_id                ---> "+var.ben_country_id
				println " *** ben_city_id                   ---> "+var.ben_city_id
				println " *** bco_swift_ben                 ---> "+var.bco_swift_ben
				println " *** bco_swift_inter               ---> "+var.bco_swift_inter
				println " *** bco_pais_ben                  ---> "+var.bco_pais_ben
				println " *** bco_pais_int                  ---> "+var.bco_pais_int
				println " *** bco_ben_id                    ---> "+var.bco_ben_id
				println " *** bco_int_id                    ---> "+var.bco_int_id
				println " *** bco_dir_ben_id                ---> "+var.bco_dir_ben_id
				println " *** bco_dir_int_id                ---> "+var.bco_dir_int_id
				println " *** beneficiaryFirstLastName      ---> "+var.beneficiaryFirstLastName
				println " *** beneficiarySecondLastName     ---> "+var.beneficiarySecondLastName
				println " *** beneficiaryBusinessName       ---> "+var.beneficiaryBusinessName
				println " *** beneficiaryTypeDocument       ---> "+var.beneficiaryTypeDocument
				println " *** beneficiaryDocumentNumber     ---> "+var.beneficiaryDocumentNumber
				println " *** currencyIdUSD                 ---> "+var.currencyIdUSD
				println " *** quote                         ---> "+var.quote
				println " *** beneficiaryTypeDocumentName   ---> "+var.beneficiaryTypeDocumentName
				println " *** codeNegotiation               ---> "+var.codeNegotiation
				println " *** beneficiaryEmail1             ---> "+var.beneficiaryEmail1
				println " *** beneficiaryEmail2             ---> "+var.beneficiaryEmail2
			}			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}

}
