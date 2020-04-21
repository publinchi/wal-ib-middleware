package com.cobiscorp.test;

import java.sql.SQLException

import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

public class VirtualBankingBase {
	public String initSessionNatural() throws SQLException {
		executeLoadParameterNatural()
		return new VirtualBankingUtil().initSession(CTSEnvironment.bvLogin, CTSEnvironment.bvPassword, CTSEnvironment.bvCulture)
	}

	public String initSessionCompany() throws SQLException {
		executeLoadParameterCompany()
		return new VirtualBankingUtil().initSession(CTSEnvironment.bvCompanyLogin, CTSEnvironment.bvCompanyPassword, CTSEnvironment.bvCulture)
	}
	public String initSessionGroup() throws SQLException {
		executeLoadParameterGroup()
		return new VirtualBankingUtil().initSession(CTSEnvironment.bvGroupLogin, CTSEnvironment.bvGroupPassword, CTSEnvironment.bvCulture)
	}

	public String initSessionCompanyA() throws SQLException {
		executeLoadParameterCompanyA()
		return new VirtualBankingUtil().initSession(CTSEnvironment.bvCompanyLoginA, CTSEnvironment.bvCompanyPasswordA, CTSEnvironment.bvCulture)
	}
	public String initSessionCompanyA(Boolean executeLoadParameters) throws SQLException {
		if(executeLoadParameters) executeLoadParameterCompanyA()
		return new VirtualBankingUtil().initSession(CTSEnvironment.bvCompanyLoginA, CTSEnvironment.bvCompanyPasswordA, CTSEnvironment.bvCulture)
	}

	
	void executeLoadDobleAutorizacionCompany(){
		println 'EJECUTANDO executeLoadDobleAutorizacionCompany'
		// Creo el usuario
		println 'Iniciando parametrización de  doble autorización'

		// Carga de datos para doble autorización
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\doble_autorizacion.sql', CTSEnvironment.TARGETID_LOCAL)

	}
	
	
	void executeLoadParameterCompany(){
		println 'EJECUTANDO executeLoadParameterCompany'
		// Creo el usuario
		println 'Iniciando session'
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		// Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		// Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)

		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		// Eliminar session del login
		String wUser = CTSEnvironment.bvCompanyLogin
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_in_login where il_login=?' ,[wUser])
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_session where bv_usuario=?' ,[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql)
	}
	
	
	
	void executeLoadParameterGroup(){
		println 'EJECUTANDO executeLoadParameterGroup'
		// Creo el usuario
		println 'Iniciando session'
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		// Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		// Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)

		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		// Eliminar session del login
		String wUser = CTSEnvironment.bvGroupLogin
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_in_login where il_login=?' ,[wUser])
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_session where bv_usuario=?' ,[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql)
	}
	void executeLoadParameterCompanyA(){
		println 'EJECUTANDO executeLoadParameterCompanyA'
		// Creo el usuario
		println 'Iniciando session'
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		// Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		// Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)

		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		// Eliminar session del login
		String wUser = CTSEnvironment.bvCompanyLoginA
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_in_login where il_login=?' ,[wUser])
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_session where bv_usuario=?' ,[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql)
	}

	void executeLoadParameterNatural(){
		println 'EJECUTANDO executeLoadParameterNatural'
		// Creo el usuario
		println 'Iniciando session'
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\create_login.sql', CTSEnvironment.TARGETID_LOCAL)
		// Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\autoriza_transaccion.sql', CTSEnvironment.TARGETID_LOCAL)
		// Carga de parametria inicial del Test
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_central.sql', CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\parametria_test_bv_local.sql', CTSEnvironment.TARGETID_LOCAL)

		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		// Eliminar session del login
		String wUser = CTSEnvironment.bvLogin
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_in_login where il_login=?' ,[wUser])
		sql.execute('delete from cob_bvirtual' + CTSEnvironment.DB_SEPARATOR + 'bv_session where bv_usuario=?' ,[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql)
	}

	void closeSessionNatural(String initSession) {
		println 'Finalizando session'
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvLogin,initSession)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())
	}

	void closeSessionCompany(String initSession) {
		println 'Finalizando session'
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLogin,initSession)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())
	}

	void closeSessionGroup(String initSession) {
		println 'Finalizando session'
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvGroupLogin,initSession)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())
	}

	void closeSessionCompanyA(String initSession) {
		println 'Finalizando session'
		new VirtualBankingUtil().finalizeSession(CTSEnvironment.bvCompanyLoginA,initSession)
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\eliminar_Login.sql', CTSEnvironment.getSqlDataBaseInformation())
		SqlExecutorUtils.executeSqlFile('Virtual_Banking\\drop_sp_test_bv.sql',CTSEnvironment.getSqlDataBaseInformation())
	}

	int verifyAutorizationReentry(String codOperacion, String ProcessName)
	{
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def transactionId=null
		
		transactionId = sql.rows(
						"select pr_proc  from cobis" + CTSEnvironment.DB_SEPARATOR  +"re_procedure pr"+
						" where pr_nom_proc = '"+ ProcessName+"' "+ //sp_tr_pago_prestamo_cca 
						" and pr_usuario = 'usuariobv'" +
						" and pr_base = 'cob_procesador'" +
						" and exists (select  1 from cobis" + CTSEnvironment.DB_SEPARATOR  +"re_parametro"+
									 " where pa_proc    = pr.pr_proc"+
									 " and pa_nomparam = '@i_cta_des'"+
									 " and pa_varchar   = "+codOperacion +")" + //'10410000041700201')--nro prestamo
						" order by 1 desc")
		
		if (transactionId.size() > 0)
		{
			return 1
		}
		else
		{
			return 0
		}
	}
	
	
	int OperationNumber(String login, int transaction)
	{
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def transactionId = null
		
		transactionId = sql.rows("select top 1 au_id from cob_bvirtual" + CTSEnvironment.DB_SEPARATOR + "bv_autorizador au"+
							" where au_estado = 'P' "+
							" and au_trn_ejecutada ="+ transaction +
							" and au_login <> '"+ login + "' "+
							" and exists (select 1 from cob_bvirtual" + CTSEnvironment.DB_SEPARATOR + "bv_login_pendiente "+
											" where au.au_id = lp_au_id"+
											" and   lp_login = '" + login + "') order by 1"
							)
		
		if (transactionId.size() > 0)
		{
			String Operation = transactionId.toString()
			int operation = Operation.toString().substring(8, (Operation.size() - 2)).toInteger()
			return operation
		}
		else
		{
			return 0
		}
	}
	
	
	void closeConnections() {
		BDD.close();
	}
}
