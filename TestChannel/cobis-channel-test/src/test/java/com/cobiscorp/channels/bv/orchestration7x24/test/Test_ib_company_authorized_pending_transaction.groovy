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
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Authorizer
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.LoginPending
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.TransferResponse

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
public class Test_ib_company_authorized_pending_transaction {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
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
	 * Test to approve authorization of Self Account Transfers 
	 */
	@Test
	void testCompanyApprovePendingAuthorization(){
		String ServiceName='CompanyApprovePendingAuthorization'
		
		def IdAuthorization = virtualBankingBase.OperationNumber(CTSEnvironment.bvCompanyLoginA, 18056) // 2977
		try{

			println String.format('Test [%s]',ServiceName)
			
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Authorization.Service.PendingTransaction.AuthorizePendingTransaction')
			//DTO IN
			Authorizer wAuthorizer=new Authorizer()
			LoginPending wLoginPending=new LoginPending()
			
			wAuthorizer.id = IdAuthorization

			wLoginPending.userName = CTSEnvironment.bvCompanyLoginA 
			wLoginPending.reason = "REASON: AUTHORIZE PENDING TRANSACTION"
			
			
			serviceRequestTO.addValue('inAuthorizer', wAuthorizer)
			serviceRequestTO.addValue('inLoginPending', wLoginPending)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			def codeError=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				codeError=((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).code
			}
			Assert.assertTrue(message, serviceResponseTO.success)
		
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowAutorizaciones=null
			
			wRowAutorizaciones = sql.rows("select au_estado from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_autorizador where au_id="+IdAuthorization+" and au_estado !='P'")
			Assert.assertEquals("No se encontro registro en la tabla bv_autorizador",wRowAutorizaciones.size(), 1)
			println ('RESPUESTA: La autorizacion se encuentra en estado ---->'+wRowAutorizaciones)
			
			
			ValidaReentry(IdAuthorization)
			
			
		}catch(Exception e){
		println String.format('[%s] Exception-->%s', ServiceName, e.message)
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
	}
	}
	
	void ValidaReentry(int IdAuthorization)
	{
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def ObtieneCta = null
		
		//Verifica que se haya grabado en la tabla de reentry
		
		ObtieneCta = sql.rows("select pa_varchar from cob_bvirtual" +CTSEnvironment.DB_SEPARATOR+ "bv_parametro "+
									   "where pa_nomparam = '@i_cta_des' "+
									   "and pa_ssn_branch = (select au_ssn_branch from cob_bvirtual" +CTSEnvironment.DB_SEPARATOR+ "bv_autorizador "+
									   "where au_id =" +IdAuthorization+")")
		Assert.assertEquals("No se encontro registro en la tabla bv_autorizador",ObtieneCta.size(), 1)
		
		String Operation = ObtieneCta.toString()
		String CtaDestino = Operation.toString().substring(13, (Operation.size() - 2))
		
		int reentry = virtualBankingBase.verifyAutorizationReentry(CtaDestino, "sp_tr_transferencias")
		if (reentry == 0)
		{
			Assert.fail('No grabo reeentry ')
		}
	}
	 
	 
}