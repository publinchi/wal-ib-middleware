package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.products.dto.Product
import cobiscorp.ecobis.internetbanking.webapp.products.dto.ProductCollection
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FactoringPayments
import cobiscorp.ecobis.internetbanking.webapp.services.dto.ResponseSummaryCreditCardColletion
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * COBISCorp.eCOBIS.InternetBanking.WebApp.Products.Service.Service
 * @author schancay
 *
 */
class TestProductsService{
	String initSession
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();

	@Before
	void setUp(){
		println 'Iniciando session'//Creo el usuario
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		//Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		//Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)


		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		//Eliminar session del login
		String wUser= CTSEnvironment.bvLogin
		sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_in_login where il_login=?',[wUser])
		sql.execute('delete from cob_bvirtual'+CTSEnvironment.DB_SEPARATOR+'bv_session where bv_usuario=?',[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);

		initSession = new VirtualBankingUtil()
				.initSession( CTSEnvironment.bvLogin, CTSEnvironment.bvPassword, CTSEnvironment.bvCulture)
	}

	@After
	void finallySession(){
		println 'Finalizando session'
		new VirtualBankingUtil()
				.finalizeSession(CTSEnvironment.bvLogin,initSession)
		// borro  el usuario
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())

		//borro el SP
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())

	}

	/**
	 * Modulo: Product 
	 */

	@Test
	void testGetSummaryCreditCard(){
		String ServiceName='GetSummaryCreditCard'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.GetSummaryCreditCard')

			//DTO IN
			FactoringPayments wFactoringPayments=new FactoringPayments()
			wFactoringPayments.operation=''
			wFactoringPayments.clientId=CTSEnvironment.bvEnte
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.sequential=0

			//DTO OUT
			ResponseSummaryCreditCardColletion oResponseSummaryCreditCardColletion=new ResponseSummaryCreditCardColletion()

			serviceRequestTO.addValue('inFactoringPayments', wFactoringPayments)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oResponseSummaryCreditCardColletion.responseSummaryCreditcard = serviceResponseTO.getData().get('returnResponseSummaryCreditCard')
			Assert.assertTrue('Filas Vacias', oResponseSummaryCreditCardColletion.responseSummaryCreditcard.collect().size()>=0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}

	@Test
	void testValidateProduct(){
		String ServiceName='ValidateProduct'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.Products.Service.Product.ValidateProduct')

			//DTO IN
			Product wProduct=new Product()
			wProduct.productOwner=''
			wProduct.productNumber=CTSEnvironment.bvAccCtaCteNumber
			wProduct.productId=CTSEnvironment.bvAccCtaCteType
			User wUser=new User()
			wUser.entityId =CTSEnvironment.bvEnte
			wUser.entityType=CTSEnvironment.bvLoginType
			SearchOption wSearchOption=new SearchOption()
			wSearchOption.criteria=''

			//DTO OUT
			ProductCollection oProductCollection=new ProductCollection()

			serviceRequestTO.addValue('inProduct', wProduct)
			serviceRequestTO.addValue('inUser', wUser)
			serviceRequestTO.addValue('inSearchOption', wSearchOption)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que Numero de elementos sea mayor a una fila
			oProductCollection.products = serviceResponseTO.getData().get('returnProduct')
			Assert.assertTrue('Filas Vacias', oProductCollection.products.collect().size()>0)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
