package com.cobiscorp.channels.bv.sg.test

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.common.dto.ExchangeRate

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * COBISCorp.eCOBIS.COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.Service
 * @author schancay
 *
 */
class TestForeignExchangeService {
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
	 * Modulo: ForeignExchangeOperations 
	 */

	@Test
	void testGetExchangeRatesCentral(){
		String ServiceName='GetExchangeRatesCentral'
		try{
			println String.format('Test [%s]',ServiceName)
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('COBISCorp.ECOBIS.InternetBanking.WebApp.ForeignExchange.Service.ForeignExchangeOperations.GetExchangeRatesCentral')

			//DTO IN
			ExchangeRate wExchangeRate=new ExchangeRate()
			wExchangeRate.officeId=1
			wExchangeRate.currency1=0
			wExchangeRate.currency2=0

			//DTO OUT
			ExchangeRate oExchangeRate=new ExchangeRate()

			serviceRequestTO.addValue('inExchangeRate', wExchangeRate)

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)

			//Valido si fue exitoso la ejecucion
			String message=''
			if (serviceResponseTO.messages.toList().size()>0)
				message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			Assert.assertTrue(message, serviceResponseTO.success)

			//Valido que el objeto no sea Null
			Map<String, Object> objOut=serviceResponseTO.getData().get('com.cobiscorp.cobis.cts.service.response.output')
			oExchangeRate.buyingRate =objOut.get('@o_cotizacion_com')
			Assert.assertNotNull('Filas Vacias',oExchangeRate.buyingRate)

		}catch(Exception e){
			println String.format('[%s] Exception-->%s', ServiceName, e.message)
			new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		}
	}
}
