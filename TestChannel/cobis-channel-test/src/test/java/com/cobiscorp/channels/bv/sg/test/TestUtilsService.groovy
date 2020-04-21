package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Bank
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.BankCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Entity
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.PublicityCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.Catalog
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CurrencyDefinition
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.CurrencyResponse
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.LaborDayCollection
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.LaborDayRequest

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
/***
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Utils.Service.Service
 * @author schancay
 *
 */
class TestUtilsService {
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
	 * Modulo: Format
	 */

	@Test
	void testGetACHAccountFormat(){
		String ServiceName='GetACHAccountFormat'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Transfers.Service.TransferBatch.AddTransferBatch')

			//DTO IN
			Bank wBank = new Bank()
			wBank.id=1

			//DTO OUT
			BankCollection oBankCollection=new BankCollection()

			serviceRequestTO.addValue('inBank', wBank)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			/*
			 //Valido si fue exitoso la ejecucion
			 String message=''
			 if (serviceResponseTO.messages.toList().size()>0)
			 message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			 Assert.assertTrue(message, serviceResponseTO.success)
			 //Valido que Numero de elementos sea mayor a una fila
			 oBankCollection.banks = serviceResponseTO.getData().get('returnBank')
			 Assert.assertTrue('Filas Vacias', oBankCollection.banks.collect().size()>0)
			 */
		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: Utils
	 */

	@Test
	void testGetCatalogCulture(){
		String ServiceName='GetCatalogCulture'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetCatalogCulture')

			//DTO IN
			Catalog wCatalog=new Catalog()
			wCatalog.table ='cl_moneda'
			wCatalog.code='40'

			//DTO OUT
			Catalog oCatalog=new Catalog()

			serviceRequestTO.addValue('inCatalog', wCatalog)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCatalog = serviceResponseTO.getData().get('returnCatalog')
			Assert.assertTrue('Filas Vacias', oCatalog.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetNextLaborDay(){
		String ServiceName='GetNextLaborDay'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetNextLaborDay')

			//DTO IN
			LaborDayRequest wLaborDayRequest=new LaborDayRequest()
			wLaborDayRequest.date=''
			wLaborDayRequest.numberOfDays=0
			wLaborDayRequest.officeId=1
			wLaborDayRequest.commercial=''

			//DTO OUT
			LaborDayCollection oLaborDayCollection=new LaborDayCollection()

			serviceRequestTO.addValue('inLaborDayRequest', wLaborDayRequest)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oLaborDayCollection.laborDays = serviceResponseTO.getData().get('returnLaborDay')
			Assert.assertTrue('Filas Vacias', oLaborDayCollection.laborDays.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetSubsidiaryOffice(){
		String ServiceName='GetSubsidiaryOffice'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetSubsidiaryOffice')

			//DTO IN
			User wUser=new User()
			wUser.entityId=CTSEnvironment.bvEnte

			//DTO OUT
			Entity oEntity=new Entity()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oEntity = serviceResponseTO.getData().get('returnEntity')
			Assert.assertTrue('Filas Vacias', oEntity.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetCurrencies(){
		String ServiceName='GetCurrency'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Service.Utils.GetCurrency')

			//DTO IN
			CurrencyDefinition wCurrencyDefinition=new CurrencyDefinition()
			wCurrencyDefinition.mode =0

			//DTO OUT
			CurrencyResponse oCurrencyResponse=new CurrencyResponse()

			serviceRequestTO.addValue('inCurrencyDefinition', wCurrencyDefinition)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oCurrencyResponse.currencies = serviceResponseTO.getData().get('returnCurrencyDefinition')
			Assert.assertTrue('Filas Vacias', oCurrencyResponse.currencies.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	/**
	 * Modulo: Publicity
	 */

	@Test
	void testGetNews(){
		String ServiceName='GetNews'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Publicity.GetNews')

			//DTO IN
			User wUser = new User()
			wUser.id= CTSEnvironment.bvEnte
			wUser.serviceId=CTSEnvironment.bvService

			//DTO OUT
			PublicityCollection oPublicityCollection=new PublicityCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPublicityCollection.publicities = serviceResponseTO.getData().get('returnPublicity')
			Assert.assertTrue('Filas Vacias', oPublicityCollection.publicities.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			Assert.fail()
		}
	}

	@Test
	void testGetNews_Company(){
		String ServiceName='testGetNews_Company'
		VirtualBankingBase virtualBankingBase1= new VirtualBankingBase()
		def initSession1= virtualBankingBase1.initSessionCompany()

		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession1)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Publicity.GetNews')

			//DTO IN
			User wUser = new User()
			wUser.id= CTSEnvironment.bvCompanyEnte
			wUser.serviceId=CTSEnvironment.bvService

			//DTO OUT
			PublicityCollection oPublicityCollection=new PublicityCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPublicityCollection.publicities = serviceResponseTO.getData().get('returnPublicity')
			Assert.assertTrue('Filas Vacias', oPublicityCollection.publicities.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			virtualBankingBase1.closeSessionCompany(initSession1)
			Assert.fail()
		}
	}

	@Test
	void testGetNews_Group(){
		String ServiceName='testGetNews_Group'
		VirtualBankingBase virtualBankingBase1= new VirtualBankingBase()
		def initSession1= virtualBankingBase1.initSessionGroup()

		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession1)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Utils.Publicity.GetNews')

			//DTO IN
			User wUser = new User()
			wUser.id= CTSEnvironment.bvGroupEnte
			wUser.serviceId=CTSEnvironment.bvService

			//DTO OUT
			PublicityCollection oPublicityCollection=new PublicityCollection()

			serviceRequestTO.addValue('inUser', wUser)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oPublicityCollection.publicities = serviceResponseTO.getData().get('returnPublicity')
			Assert.assertTrue('Filas Vacias', oPublicityCollection.publicities.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			virtualBankingBase1.closeSessionGroup(initSession1)
			Assert.fail()
		}
	}
}
