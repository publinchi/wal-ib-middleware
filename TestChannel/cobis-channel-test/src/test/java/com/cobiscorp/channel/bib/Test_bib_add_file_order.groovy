package com.cobiscorp.channel.bib;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import java.text.SimpleDateFormat
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource
import cobiscorp.businessbanking.services.dto.FileOrder
import cobiscorp.businessbanking.services.dto.PaymentAccount
import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD

public class Test_bib_add_file_order {
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
	 
	@Test
	void testAddFileOrder(){
		String ServiceName='AddFileOrder'

		def IdOrder = 4
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('Cobiscorp.BusinessBanking.Services.Batch.Orders.AddOrder')
			//DTO IN
			FileOrder wFileOrder=new FileOrder()
			PaymentAccount wPaymentAccount=new PaymentAccount()
			
			wFileOrder.username = "testCtsEmp"
			wFileOrder.serverFileName = "137488_PMNOM_6c26a445bca14c478e739f8ffbf92fca.txt"
			wFileOrder.userFileName = "NOMINA_2.txt"
			wFileOrder.fileType = 1875009
			String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
			SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
			wFileOrder.plannedDate =new GregorianCalendar(pdt)
			wFileOrder.description = "TEST REGRESION ADDORDER gise"
			wFileOrder.clientId = 137488
			wFileOrder.md5Hash = "6c26a445bca14c478e739f8ffbf92fca"

			wPaymentAccount.account = "10410108275405315"
			wPaymentAccount.product = 3
			wPaymentAccount.currency = 0
			wPaymentAccount.registeredRecords = 2
			wPaymentAccount.totalAmount = 2
			wPaymentAccount.authorized = "S"

		
			
			serviceRequestTO.addValue('inFileOrder', wFileOrder)
			serviceRequestTO.addValue('inPaymentAccount', wPaymentAccount)
			println("serviceRequestTO")
			println (serviceRequestTO)
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
			/*def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowAutorizaciones=null
			
			wRowAutorizaciones = sql.rows("select fo_file_id from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_file_order where fo_file_id=?",IdOrder)
		
			
			Assert.assertEquals("No se encontro registro en la tabla fo_file_id",wRowAutorizaciones.size(), 1)
			println ('RESPUESTA: Se encuentra el registro ---->'+wRowAutorizaciones)*/
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
	}
	}
	
}
