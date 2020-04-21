package com.cobiscorp.channels.bv.orchestration7x24.test



import junit.framework.Assert

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.ClassRule
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runners.MethodSorters

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DestinationAccount
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.EntityServiceProduct
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LotDestination
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

/***
 * 
 * @author eortega
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Test_ib_company_destination_authorize {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	static Integer lotIdBatch

	@Before
	void setUp(){
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
	 * Save Batch Destination
	 */
	@Test
	void test1SaveBatchDestination() {
		VirtualBankingBase virtualBankingBase1= new VirtualBankingBase()
		def initSession1= virtualBankingBase1.initSessionCompany()
		String ServiceName='RegisterLotDestination'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession1)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RegisterLotDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment.bvCompanyAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvCompanyAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvCompanyAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvCompanyLogin

			wLotDestination.numberOfRegistersError=0
			wLotDestination.numberOfRegisters=5
			wLotDestination.numberOfRegistersOK=5
			wLotDestination.userName = CTSEnvironment.bvCompanyLogin
			wLotDestination.fileName ='PRB destinos SINPE DYCM.csv'

			serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
			serviceRequestTO.addValue('inLotDestination',wLotDestination )
			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)


			//obtengo el numero del lote
			Integer wOLote = Integer.valueOf(serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_lote"))
			Assert.assertNotNull("No se obtuvo Numero de Lote",wOLote)
			println ('RESPUESTA: @o_Lote------------------>' + wOLote)
			lotIdBatch = wOLote

			DestinationAccount[] wDestinations = new DestinationAccount[5]
			for(int i=0; i < 5 ;i++){
				wDestinations[i] = new DestinationAccount()
				wDestinations[i].productNumber='1520100101717588'+i
				wDestinations[i].currencyId =0
				wDestinations[i].productId=3
				wDestinations[i].typeDestination='E'
				wDestinations[i].beneficiaryId='01-2345-678'+i
				wDestinations[i].beneficiaryName='PRUEBA SINPE'
				wDestinations[i].email1='erica.ortega@cobiscorp.com'
				wDestinations[i].email2=''
				wDestinations[i].email3=''
				wDestinations[i].accountNumber =''
				wDestinations[i].currencyIdISO ='CRC'
				wDestinations[i].estado='Pendiente'
				wDestinations[i].lot=lotIdBatch
			}

			//inserto los detalles de los destinos
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.L')
			for(int i=0; i < wDestinations.collect().size() ;i++){

				serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
				serviceRequestTO.addValue('inDestinationAccount', wDestinations[i])
				serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

				serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
				//Valido si fue exitoso la ejecucion
				//1875261  Destino ya se encuentra autorizado
				//1875262 No se pudo actualizar el destino
				//1875264  1875265  No se pudo registrar el destino
				message=''
				if (serviceResponseTO.messages.toList().size()>0)
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				Assert.assertTrue(message, serviceResponseTO.success)
				println ('SE INSERTO registro DE DESTINO '+i+' DESTINO ASOCIADO----->'+wDestinations[i])
			}

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession1)
			Assert.fail()
		}
	}

	/**
	 * Authorize Batch Destination
	 */
	@Test
	void test2AutorizeBatchDestination() {
		String ServiceName='AuthorizeLotDestination'
		VirtualBankingBase virtualBankingBase2= new VirtualBankingBase()
		def initSession2= virtualBankingBase2.initSessionCompanyA(false)
		try{
			println String.format('Test [%s]',ServiceName)
			//Obtengo el ultimo lote
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			//def ultLote= sql.rows("select max(ld_secuencial) as lote from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_lote_destino")
			//lotIdBatch= Integer.valueOf(ultLote.collect().get(0).getAt("lote"))
			//Assert.assertFalse("NO SE HA CREADO NINGUN LOTE DESTINO",lotIdBatch == 0)

			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession2)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.AuthorizeLotDestination')

			//DTO IN
			LotDestination wLotDestination= new LotDestination()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wLotDestination.id = lotIdBatch
			wLotDestination.userName = CTSEnvironment.bvCompanyLoginA

			serviceRequestTO.addValue('inLotDestination', wLotDestination)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			// asegurar que el lote tiene el estado correcto
			//sql("update cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_lote_destino set  ld_estado = 'I'  where ld_secuencial =?",lotIdBatch)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitosa la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			println ('SE AUTORIZO LOTE ----->'+lotIdBatch)


			//Validacion del estado del lote Autorizado
			def wRowDestino=null
			wRowDestino = sql.rows("select ld_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_lote_destino  where ld_secuencial =?",lotIdBatch)
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 1)
			println ('RESPUESTA: estado del lote AUTORIZADO--------->'+wRowDestino)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLoginA,initSession2)
			Assert.fail();
		}finally{
			virtualBankingBase2.closeSessionCompanyA(initSession2)
		}
	}

	/**
	 * Authorize Destination con Usuario Autorizado
	 */
	@Test
	void test3AutorizeDestination() {
		String ServiceName='AuthorizeDestination'
		VirtualBankingBase virtualBankingBase2= new VirtualBankingBase()
		def initSession2= virtualBankingBase2.initSessionCompanyA(false)
		try{
			println String.format('Test [%s]',ServiceName)
			//Obtengo el ultimo lote
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null

			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession2)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.AuthorizeDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()

			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()
			wEntityServiceProduct.productId =CTSEnvironment. bvCompanyAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvCompanyAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvCompanyAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvCompanyLoginA

			DestinationAccount[] wDestinations = new DestinationAccount[5]
			for(int i=0; i < 5 ;i++){
				wDestinations[i] = new DestinationAccount()
				wDestinations[i].productId=3
				wDestinations[i].status='I - INCLUIDO'
				wDestinations[i].productNumber='1520100101717588'+i
				wDestinations[i].currencyId =0
				wDestinations[i].beneficiaryId='01-2345-678'+i
				wDestinations[i].beneficiaryName='PRUEBA SINPE'
				wDestinations[i].email1='erica.ortega@cobiscorp.com'
				wDestinations[i].email2=''
				wDestinations[i].email3=''
				wDestinations[i].accountNumber =''
				wDestinations[i].currencyIdISO =''
				wDestinations[i].lot=lotIdBatch
			}

			ServiceResponseTO serviceResponseTO
			for(int i=0; i < wDestinations.collect().size() ;i++){
				serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
				serviceRequestTO.addValue('inDestinationAccount', wDestinations[i])
				serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)
				serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
				//Valido si fue exitoso la ejecucion
				//1875261  Destino ya se encuentra autorizado
				//1875262 No se pudo actualizar el destino
				//1875264  1875265  No se pudo registrar el destino
				String message=''
				if (serviceResponseTO.messages.toList().size()>0)
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				Assert.assertTrue(message, serviceResponseTO.success)
				println ('SE AUTORIZO EL DESTINO '+i+' DESTINO ASOCIADO----->'+wDestinations[i])
			}
			//Validacion del estado del lote Autorizado
			wRowDestino = sql.rows("select de_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_destino  where de_lote =?",lotIdBatch)
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 5)
			println ('RESPUESTA: estado de los destinos es:--------->'+wRowDestino)

		}catch(Exception e){
			e.printStackTrace()
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
			Assert.fail();
		}finally{
			virtualBankingBase2.closeSessionCompanyA(initSession2)
		}
	}
}




