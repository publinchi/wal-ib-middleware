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
class Test_ib_destination {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	def lotId

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

	/**
	 * Validate Internal Destination
	 */
	@Test
	void test0ValidateInternalDestination() {
		String ServiceName='ValidateInternalDestination'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.ValidateInternalDestination')

			//DTO IN
			DestinationAccount wDestinationAccount=new DestinationAccount()
			LotDestination wLotDestination=new LotDestination()

			wDestinationAccount.productNumber='10410108275406111'
			wDestinationAccount.currencyId=0
			wDestinationAccount.productId=CTSEnvironment.bvAccCtaCteType

			wLotDestination.userName=CTSEnvironment.bvLogin

			serviceRequestTO.addValue('inLotDestination', wLotDestination)
			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//valida filas vacias
			Assert.assertTrue('Filas Vacias', serviceResponseTO.getData().get('returnDestinationAccount').collect().size()>0)
			//obtengo la infromacion
			DestinationAccount[] obj= serviceResponseTO.data.get('returnDestinationAccount').collect().toArray()


			def wObeneficiaryName = obj[0].beneficiaryName
			Assert.assertNotNull("No se obtuvo productBalance",wObeneficiaryName)
			println ('RESPUESTA: @NOMBRE BENEFICIARIO----->' + wObeneficiaryName)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Save new Destination
	 */
	@Test
	void test1SaveNewDestination() {
		String ServiceName='RegisterLotDestination'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RegisterLotDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()


			wEntityServiceProduct.productId =CTSEnvironment.bvAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvLogin

			wLotDestination.numberOfRegistersError=0
			wLotDestination.userName = CTSEnvironment.bvLogin
			wLotDestination.fileName =''

			wDestinationAccount.productNumber='10410108275406319'
			wDestinationAccount.currencyId =0
			wDestinationAccount.productId=3
			wDestinationAccount.typeDestination='I'
			wDestinationAccount.beneficiaryId=160228984
			wDestinationAccount.beneficiaryName='GUERRERO DE LOURDES PAULA PAULA de CONTRERAS'
			wDestinationAccount.email1='erica.ortega@cobiscorp.com'
			wDestinationAccount.email2=''
			wDestinationAccount.email3=''
			wDestinationAccount.accountNumber =''
			wDestinationAccount.currencyIdISO =''


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


			//obtengo la informacion
			Integer wOLote = Integer.valueOf(serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_lote"))
			Assert.assertNotNull("No se obtuvo Numero de Lote",wOLote)
			println ('RESPUESTA: @o_Lote------------------>' + wOLote)


			wDestinationAccount.lot= (Integer)wOLote
			def lotId= (Integer)wOLote

			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.L')


			serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			//Valido si fue exitoso la ejecucion
			message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Validacion del estado
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null
			wRowDestino = sql.rows("select de_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_destino where de_lote=?",lotId)
			Assert.assertEquals("No se encontro registro en la re_tran_monet",wRowDestino.size(), 1)
			println ('RESPUESTA: wRowDestino--------->'+wRowDestino)


		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Delete Destination
	 */
	@Test
	void test2DeleteDestination() {
		String ServiceName='RemoveDestination'
		try{
			Assert.assertFalse("NO SE HA CREADO NINGUN LOTE DESTINO",lotId == 0)
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RemoveDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment.bvAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvLogin

			wDestinationAccount.productNumber='10410108275406319'
			wDestinationAccount.currencyId =0
			wDestinationAccount.productId=3
			wDestinationAccount.typeDestination='A'
			wDestinationAccount.lot=lotId

			serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Validacion del estado
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null
			wRowDestino = sql.rows("select de_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_destino where de_lote=?",lotId)
			Assert.assertEquals("ERROR No se encontro registro en la bv_destino",wRowDestino.size(), 1)
			println ('RESPUESTA: Estado del destino--------->'+wRowDestino)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	/**
	 * Save Batch Destination
	 */
	@Test
	void test3SaveBatchDestination() {
		String ServiceName='RegisterLotDestination'
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RegisterLotDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment.bvAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvLogin

			wLotDestination.numberOfRegistersError=0
			wLotDestination.numberOfRegisters=5
			wLotDestination.numberOfRegistersOK=5
			wLotDestination.userName = CTSEnvironment.bvLogin
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
			def lotIdBatch= (Integer)wOLote

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
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}



