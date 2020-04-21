package com.cobiscorp.channels.atm.test;


import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.CTSTestServletClient
import com.cobiscorp.test.utils.NCSAuth
import com.cobiscorp.test.utils.SqlExecutorUtils

/**
 * Test Adminstrator, display complete name and rol of a party
 * 
 * @author fabad
 *
 */
class Test_sp_exec_atm_activacion {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();

	@BeforeClass
	static void setUp(){
		println 'Configuracion de Tarjetas Test'
		SqlExecutorUtils.executeSqlFile('ATM\\Cards_Operations.sql', CTSEnvironment.TARGETID_LOCAL)
		SqlExecutorUtils.unauthorizeTransaction("cob_procesador","sp_exec_atm_activacion",16061,20,1,0,  CTSEnvironment.TARGETID_CENTRAL)
		SqlExecutorUtils.authorizeTransaction("cob_procesador","sp_exec_atm_activacion",16061,20,1,0, CTSEnvironment.TARGETID_CENTRAL)
	}

	/***
	 * Tarjeta Princiapal con costo 0
	 */
	@Test
	void testExecuteJavaOrch_PrincipalCard() {

		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
		    <CTSHeader>
		    </CTSHeader>
		    <Data>
		        <ProcedureRequest>
		            <SpName>cob_procesador..sp_exec_atm_activacion</SpName>
		            <Param name="@t_trn" type="52" io="0" len="2">16061</Param>
		            
					<Param name="@i_proceso" type="47" io="0" len="3">ENA</Param>
		            <Param name="@i_motivo" type="47" io="0" len="3">EMI</Param>
		            <Param name="@i_servicio" type="47" io="0" len="3">MAN</Param>
		            <Param name="@i_banco" type="48" io="0" len="1">1</Param>
		            <Param name="@i_tarjeta" type="56" io="0" len="4">41</Param>
		            <Param name="@i_oficina" type="52" io="0" len="2">1</Param>

		            <Param name="@i_util_sobre" type="47" io="0" len="1">S</Param>
		            <Param name="@i_comentario" type="39" io="0" len="0"/>
		            <Param name="@i_operacion_pin" type="48" io="0" len="1">2</Param>

		            <Param name="@s_srv" type="39" io="0" len="6">CTSSRV</Param>
		            <Param name="@s_user" type="39" io="0" len="7">admuser</Param>
		            <Param name="@s_term" type="39" io="0" len="13">PC01SOLBAN197</Param>
		            <Param name="@s_ofi" type="56" io="0" len="1">1</Param>
		            <Param name="@s_rol" type="56" io="0" len="2">20</Param>
		            <Param name="@s_ssn" type="56" io="0" len="9">655944696</Param>
		            <Param name="@s_lsrv" type="39" io="0" len="6">CTSSRV</Param>
		            <Param name="@s_date" type="61" io="0" len="10">10/25/2013</Param>
		            <Param name="@s_sesn" type="56" io="0" len="4">7644</Param>
		            <Param name="@s_org" type="39" io="0" len="1">U</Param>
		            <Param name="@s_backEndId" type="39" io="0" len="8">backend2</Param>
		        </ProcedureRequest>
		    </Data>
		</CTSMessage>
		'''
		/*
		 * en la filial se debe colocar el reference en lugar del id
		 * Por ejemplo si se tiene 
		 * 
		 *        <backEndId id="backend2" reference="1" description="BANCO Canales" branch="false" default-tenant="false" offline="false">
		 *        <targets>
		 *              <target name="central" dbms-name="SQLCTS" ssnserver-name="SSNSERVER_LOCAL"/>
		 *                  <target name="local" dbms-name="SQLCTS" ssnserver-name="SSNSERVER_LOCAL"/>
		 *                  <target name="cis-central" dbms-name="SYBCIS" ssnserver-name="SERVER1"/>
		 *                </targets>
		 *                <role id="1" description="Administrator"/>
		 *              </backEndId>
		 */
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");

		Boolean dReclogin = true;
		Integer wRol = 20
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, dReclogin, wRol)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)

		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse

		Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )

		//wXmlResponse = wXmlResponse.replace("<text>", "text");

		def wDocXml = new XmlParser().parseText(wXmlResponse)
		//def wSsnBranch = wDocXml.'**'.'Field'.find{it.'@name' == 'ssn_branch'}
		//println "ssn_branch: " + wSsnBranch.text()
		//assert wSsnBranch.text() != 0
	}

	/***
	 * Tarjeta Adicional con costo 0
	 */
	@Test
	void testExecuteJavaOrch_AditionalCard() {

		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
		    <CTSHeader>
		    </CTSHeader>
		    <Data>
		        <ProcedureRequest>
		            <SpName>cob_procesador..sp_exec_atm_activacion</SpName>
		            <Param name="@t_trn" type="52" io="0" len="2">16061</Param>
		            <Param name="@i_proceso" type="47" io="0" len="3">ENA</Param>
		            <Param name="@i_motivo" type="47" io="0" len="3">EMI</Param>
		            <Param name="@i_servicio" type="47" io="0" len="3">MAN</Param>
		            <Param name="@i_banco" type="48" io="0" len="1">1</Param>
		            <Param name="@i_tarjeta" type="56" io="0" len="4">44</Param>
		            <Param name="@i_oficina" type="52" io="0" len="2">1</Param>

		            <Param name="@i_util_sobre" type="47" io="0" len="1">S</Param>
		            <Param name="@i_comentario" type="39" io="0" len="0"/>
		            <Param name="@i_operacion_pin" type="48" io="0" len="1">2</Param>

		            <Param name="@s_srv" type="39" io="0" len="6">CTSSRV</Param>
		            <Param name="@s_user" type="39" io="0" len="7">admuser</Param>
		            <Param name="@s_term" type="39" io="0" len="13">PC01SOLBAN197</Param>
		            <Param name="@s_ofi" type="56" io="0" len="1">1</Param>
		            <Param name="@s_rol" type="56" io="0" len="2">20</Param>
		            <Param name="@s_ssn" type="56" io="0" len="9">655944696</Param>
		            <Param name="@s_lsrv" type="39" io="0" len="6">CTSSRV</Param>
		            <Param name="@s_date" type="61" io="0" len="10">10/25/2013</Param>
		            <Param name="@s_sesn" type="56" io="0" len="4">7644</Param>
		            <Param name="@s_org" type="39" io="0" len="1">U</Param>
		            <Param name="@s_backEndId" type="39" io="0" len="8">backend2</Param>
		        </ProcedureRequest>
		    </Data>
		</CTSMessage>
		'''
		/*
		 * en la filial se debe colocar el reference en lugar del id
		 * Por ejemplo si se tiene 
		 * 
		 *        <backEndId id="backend2" reference="1" description="BANCO Canales" branch="false" default-tenant="false" offline="false">
		 *        <targets>
		 *              <target name="central" dbms-name="SQLCTS" ssnserver-name="SSNSERVER_LOCAL"/>
		 *                  <target name="local" dbms-name="SQLCTS" ssnserver-name="SSNSERVER_LOCAL"/>
		 *                  <target name="cis-central" dbms-name="SYBCIS" ssnserver-name="SERVER1"/>
		 *                </targets>
		 *                <role id="1" description="Administrator"/>
		 *              </backEndId>
		 */
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");

		Boolean dReclogin = true;
		Integer wRol = 20
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, dReclogin, wRol)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)

		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse

		Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )

		//wXmlResponse = wXmlResponse.replace("<text>", "text");

		def wDocXml = new XmlParser().parseText(wXmlResponse)
		//def wSsnBranch = wDocXml.'**'.'Field'.find{it.'@name' == 'ssn_branch'}
		//println "ssn_branch: " + wSsnBranch.text()
		//assert wSsnBranch.text() != 0
	}
}