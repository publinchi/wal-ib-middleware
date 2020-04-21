package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.Assert
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentResponse
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.utils.BDD
/*
 * Gcondo
 * */

class Test_ib_payment_loan {
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionCompany();
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
	 * this method return the details of loan for a user  type Company
	 */

	@Test
	void testPaymentLoans() {
		def ServiceName= 'testPaymentLoans'
		try{

			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Loan.Loan.PayLoan')
			
			PaymentRequest wPaymentRequet = new PaymentRequest()
			wPaymentRequet.productId=4
			wPaymentRequet.currencyId=0
			wPaymentRequet.userName= CTSEnvironment.bvCompanyLogin 
			wPaymentRequet.account= CTSEnvironment.bvGroupAccCtaAhoNumber//'10410000005233616'
			wPaymentRequet.concept='Pago Prestamo regresion'
			wPaymentRequet.amount= Double.parseDouble("100")
			wPaymentRequet.loanNumber=CTSEnvironment.bvCompanyLoanNumber //'10410000041700201'
			wPaymentRequet.destProduct = 7
			wPaymentRequet.productAbbreviation=CTSEnvironment.bvAbbreviationSavingAccount 
			wPaymentRequet.thirdPartyAssociated='N'
			wPaymentRequet.isThirdParty='N'
			wPaymentRequet.loanPaymentAmount=Double.parseDouble('100')
			wPaymentRequet.creditAmount=Double.parseDouble('200')
			wPaymentRequet.rateValue=Double.parseDouble('1')
			wPaymentRequet.validateAccount='N'
			println "datos double parseados"
			
			
			TransactionContextCIB wTransactionContextCIB = new TransactionContextCIB()
			wTransactionContextCIB.authorizationRequired='S'
			
			
			User wUser = new User()
			wUser.entityId = CTSEnvironment.bvCompanyEnteMis

			serviceRequestTO.addValue('inPaymentRequest', wPaymentRequet)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inTransactionContextCIB', wTransactionContextCIB)
			
			println ">>>>antes de ejecutar servicio"
			
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			println ">>>>despues de ejecutar servicio"
			def message=''			
		
				
				//Valido si fue exitoso la ejecucion				
				if (serviceResponseTO.messages.toList().size()>0){
					message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
				}else{
					println "Servicio OK"
					Assert.assertTrue(message, serviceResponseTO.success)
					
					String wOAutorizacion = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_autorizacion")
					Integer wOSSNBranch = Integer.valueOf(serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_ssn_branch"))
					println "--->Doble Autorización ${wOAutorizacion}"
					println "--->SSN Branch  ${wOSSNBranch}"
					
					if(wOAutorizacion.equals("S")){						
						println "=========================================="
						println "*****Verificando Validación de doble Autorización"
						def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
						def wAlias=null
						wAlias = sql.rows("select au_id  from cob_bvirtual" + CTSEnvironment.DB_SEPARATOR +"bv_autorizador where au_ssn_branch= " +  wOSSNBranch +
							" and au_login = '" + wPaymentRequet.userName + "'")
						   Assert.assertEquals("No se encontro registro en la bv_autorizador",wAlias.size(), 1)
						   
						   println ('RESPUESTA: wAlias----->'+ wAlias)
					}	
					
				}
		
			println "----->despues de hacer validacion del OK"


		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionCompany(initSession)
			Assert.fail()
		}
		
	}

}
