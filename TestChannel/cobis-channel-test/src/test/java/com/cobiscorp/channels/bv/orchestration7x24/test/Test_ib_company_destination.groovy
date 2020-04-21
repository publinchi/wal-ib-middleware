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
class Test_ib_company_destination {
	static Integer lotIdBatch=0

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

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
		String ServiceName='RegisterLotDestination'
		VirtualBankingBase virtualBankingBase2= new VirtualBankingBase()
		def initSession2= virtualBankingBase2.initSessionCompany()
		try{

			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession2)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RegisterLotDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment. bvCompanyAccCtaAhoType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvCompanyAccCtaAhoNumber
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
			lotIdBatch= (Integer)wOLote

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
			println ('RESPUESTA: ------------------>')
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
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLoginEmpresa,initSession2)
			Assert.fail();
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
			Assert.assertFalse("NO SE HA CREADO NINGUN LOTE DESTINO",lotIdBatch == 0)
			println String.format('Test [%s]',ServiceName)
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

			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null
			wRowDestino = sql.rows("select ld_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_lote_destino  where ld_secuencial =?",lotIdBatch)
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 1)
			println ('RESPUESTA: estdo del lote AUTORIZADO--------->'+wRowDestino)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
			Assert.fail();
		}finally{
			virtualBankingBase2.closeSessionCompanyA(initSession2)
		}
	}

	/**
	 * Authorize  Destination con usuario no autorizado
	 */
	@Test
	void test3AutorizeDestination() {
		String ServiceName='AuthorizeDestination'
		VirtualBankingBase virtualBankingBase2= new VirtualBankingBase()
		def initSession2= virtualBankingBase2.initSessionCompanyA(false)
		try{
			Assert.assertFalse("NO SE HA CREADO NINGUN LOTE DESTINO",lotIdBatch == 0)
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession2)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.AuthorizeDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			LotDestination wLotDestination = new LotDestination()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment. bvCompanyAccCtaAhoType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvCompanyAccCtaAhoCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvCompanyAccCtaAhoNumber
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
				def codeError=''
				if (serviceResponseTO.messages.toList().size()>0)
				{
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
					codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
				}
				Assert.assertFalse('USUARIO NO AUTORIZADO-------------->COD ERROR:'+codeError, codeError="1887668");//[sp_bv_destinos] Valida el usuario con el que se realiza al autorizacion
				println ('SE AUTORIZO EL DESTINO '+i+' DESTINO ASOCIADO----->'+wDestinations[i])
			}

			//Validacion del estado del lote Autorizado
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null
			wRowDestino = sql.rows("select ld_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_destino  where de_lote =?",lotIdBatch)
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 1)
			println ('RESPUESTA: estdo del lote AUTORIZADO--------->'+wRowDestino)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLoginA,initSession2)
			Assert.fail();
		}
	}

	/**
	 * Delete Batch Destination
	 */
	@Test
	void test4DeleteBatchDestination() {
		String ServiceName='RemoveLotDestination'
		VirtualBankingBase virtualBankingBase2= new VirtualBankingBase()
		def initSession2= virtualBankingBase2.initSessionCompanyA(false)
		try{
			Assert.assertFalse("NO SE HA CREADO NINGUN LOTE DESTINO",lotIdBatch == 0)
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession2)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Admin.Service.Destination.RemoveLotDestination')

			//DTO IN
			EntityServiceProduct wEntityServiceProduct=new EntityServiceProduct()
			DestinationAccount wDestinationAccount = new DestinationAccount()
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()

			wEntityServiceProduct.productId =CTSEnvironment.bvAccCtaCteType
			wEntityServiceProduct.currencyId=CTSEnvironment.bvAccCtaCteCurrencyId
			wEntityServiceProduct.productNumber =CTSEnvironment.bvAccCtaCteNumber
			wEntityServiceProduct.userName  =CTSEnvironment.bvCompanyLoginA

			wDestinationAccount.lot=lotIdBatch

			serviceRequestTO.addValue('inEntityServiceProduct', wEntityServiceProduct)
			serviceRequestTO.addValue('inDestinationAccount', wDestinationAccount)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Validacion del estado del lote eliminado
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowDestino=null
			wRowDestino = sql.rows("select ld_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_lote_destino  where ld_secuencial =?",lotIdBatch)
			Assert.assertEquals("ERROR No se encontro registro del LOTE ",wRowDestino.size(), 1)
			println ('RESPUESTA: estdo del lote--------->'+wRowDestino)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLoginA,initSession2)
			Assert.fail();
		}
	}

}



