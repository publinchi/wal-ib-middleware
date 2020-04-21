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
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ProgrammedSavings;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
/***
 *
 * @author jlvidal
 *
 */

class Test_ib_addProgrammedSaving {	
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	 * Test to approve authorization of Self Account Transfers
	 */
	@Test
	void testAddProgrammedSaving(){
		String ServiceName='AddProgrammedSaving'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			User wUser= new User()
			Product wProduct1=new Product()
			Product wProduct2=new Product()
			ProgrammedSavings wProgrammedSavings=new ProgrammedSavings()
			
			wUser.entityId=217
			wUser.name="User Test"
			
			wProgrammedSavings.frequency="30"
			wProgrammedSavings.amount=100
			wProgrammedSavings.currency="1"
			wProgrammedSavings.concept="concepto test"
			wProgrammedSavings.initialDate="11112014"
			wProgrammedSavings.term="9"
			wProgrammedSavings.expirationDate="12122014"
			wProgrammedSavings.mail="asdasda@asdasd.com"
			wProgrammedSavings.branch="1"
			wProgrammedSavings.idBeneficiary="345"
			
			wProduct2.productNumber="2"
			wProduct2.productId=14
			wProduct2.currencyId=1
			wProduct1.productNumber="4"
			
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.AddProgrammedSavings')
			
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inProduct', wProduct1)
			serviceRequestTO.addValue('inProgrammedSavings', wProgrammedSavings)
			serviceRequestTO.addValue('inProduct2', wProduct2)
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
		   	
			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecución del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			
			println "**************************Test ---> Ejecutado con param. de entrada**************************"
			println "Test ---> wUser.entityId-> " + wUser.entityId
			println "Test ---> wUser.name-> " + wUser.name
			println "Test ---> wProgrammedSavings.frequency-> " + wProgrammedSavings.frequency
			println "Test ---> wProgrammedSavings.amount-> " + wProgrammedSavings.amount
			println "Test ---> wProgrammedSavings.currency-> " + wProgrammedSavings.currency
			println "Test ---> wProgrammedSavings.concept-> " + wProgrammedSavings.concept
			println "Test ---> wProgrammedSavings.initialDate-> " + wProgrammedSavings.initialDate
			println "Test ---> wProgrammedSavings.term-> " + wProgrammedSavings.term
			println "Test ---> wProgrammedSavings.expirationDate-> " + wProgrammedSavings.expirationDate
			println "Test ---> wProgrammedSavings.mail-> " + wProgrammedSavings.mail
			println "Test ---> wProgrammedSavings.branch-> " + wProgrammedSavings.branch
			println "Test ---> wProgrammedSavings.idBeneficiary-> " + wProgrammedSavings.idBeneficiary
			println "Test ---> wProductNew.productNumber-> " + wProduct2.productNumber
			println "Test ---> wProductNew.productId-> " + wProduct2.productId
			println "Test ---> wProductNew.currencyId-> " + wProduct2.currencyId
			println "Test ---> wProduct.productNumber-> " + wProduct1.productNumber
			
//			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_referencia")
//			def wOret = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@i_retorno")
			def wOcta = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_cta_ahoprog")
			
			Assert.assertNotNull("No se obtuvo Monto o_cta_ahoprog",wOcta)
			
//			println ('RESPUESTA: @o_referencia----->' + wOref)
//			println ('RESPUESTA: @i_retorno----->' + wOret)
			println ('RESPUESTA: @o_cta_ahoprog----->' + wOcta)
			
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			virtualBankingBase.closeSessionNatural(initSession)
		}
	}
}
