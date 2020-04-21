package com.cobiscorp.chanels.bv.test;

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

class TestInitSessionBV {

	@Before
	void crearUsuario(){
		System.setProperty("testPropertyFile","sch.properties");
		System.out.println("setting up environment with: " + System.getProperty("testPropertyFile"));
		println 'Iniciando session'//Creo el usuario
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		//Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		//Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)

	}


	@Test
	void testInitSessionBV(){
		println("Data Login-->"+CTSEnvironment.bvLogin +":"+ CTSEnvironment.bvPassword)
		def initSession =  new VirtualBankingUtil()
				.initSession( CTSEnvironment.bvLogin, CTSEnvironment.bvPassword, CTSEnvironment.bvCulture)
		println("SessionID: --> " + initSession)
		Assert.assertNotNull(initSession);

		//
	}

	@After
	void borrarUsuario(){
		// inserto el usuario
		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\eliminar_Login.sql", CTSEnvironment.getSqlDataBaseInformation());
	}
}
