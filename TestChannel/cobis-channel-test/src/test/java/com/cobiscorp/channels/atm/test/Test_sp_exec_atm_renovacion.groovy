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
 * 
 * 
 * @author fabad
 *
 */
class Test_sp_exec_atm_renovacion {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();

	@BeforeClass
	static void setUp(){
		SqlExecutorUtils.unauthorizeTransaction("cob_procesador","sp_exec_atm_renovacion",16063,20,1,0,  CTSEnvironment.TARGETID_CENTRAL)
		//authorice transaccion  (String databaseName, String spName,int trn,int rol, int producto, int moneda, DataBaseInformation dbInformation)
		SqlExecutorUtils.authorizeTransaction("cob_procesador","sp_exec_atm_renovacion",16063,20,1,0, CTSEnvironment.TARGETID_CENTRAL)
	}

	@Test
	void testExecuteJavaOrch() {

		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
<CTSMessage>
	<CTSHeader>
	</CTSHeader>
	<Data>
        <ProcedureRequest>
            <SpName>cob_procesador..sp_exec_atm_renovacion</SpName>
            <Param name="@t_trn" type="52" io="0" len="2">16063</Param>
            <Param name="@i_proceso" type="39" io="0" len="3">ETR</Param>
            <Param name="@i_motivo" type="47" io="0" len="3">RAU</Param>
            <Param name="@i_banco" type="48" io="0" len="1">1</Param>
            <Param name="@i_origen" type="47" io="0" len="1">F</Param>
            <Param name="@i_tarjeta_ren" type="56" io="0" len="4">44</Param>
            <Param name="@i_observaciones" type="39" io="0" len="0"/>
            <Param name="@o_tarjeta" type="48" io="0" len="1">0</Param>
            <Param name="@s_srv" type="39" io="0" len="6">CTSSRV</Param>
            <Param name="@s_user" type="39" io="0" len="7">admuser</Param>
            <Param name="@s_term" type="39" io="0" len="13">PC01SOLBAN197</Param>
            <Param name="@s_ofi" type="56" io="0" len="1">1</Param>
            <Param name="@s_rol" type="56" io="0" len="2">20</Param>
            <Param name="@s_ssn" type="56" io="0" len="9">655952718</Param>
            <Param name="@s_lsrv" type="39" io="0" len="6">CTSSRV</Param>
            <Param name="@s_date" type="61" io="0" len="10">10/25/2013</Param>
            <Param name="@s_sesn" type="56" io="0" len="5">15843</Param>
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

		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)

		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse

		Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )

		wXmlResponse = wXmlResponse.replace("<text>", "text");

		def wDocXml = new XmlParser().parseText(wXmlResponse)
		//def wSsnBranch = wDocXml.'**'.'Field'.find{it.'@name' == 'ssn_branch'}
		//println "ssn_branch: " + wSsnBranch.text()
		//assert wSsnBranch.text() != 0
	}
}