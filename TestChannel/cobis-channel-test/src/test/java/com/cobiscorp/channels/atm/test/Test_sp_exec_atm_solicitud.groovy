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
class Test_sp_exec_atm_solicitud {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	@BeforeClass
	static void setUp(){
		SqlExecutorUtils.unauthorizeTransaction("cob_procesador","sp_exec_atm_solicitud",16062,20,1,0,  CTSEnvironment.TARGETID_CENTRAL)
		//authorice transaccion  (String databaseName, String spName,int trn,int rol, int producto, int moneda, DataBaseInformation dbInformation)
		SqlExecutorUtils.authorizeTransaction("cob_procesador","sp_exec_atm_solicitud",16062,20,1,0, CTSEnvironment.TARGETID_CENTRAL)
	}

	@Test
	void testExecuteJavaOrch() {
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
<CTSMessage>
	<CTSHeader>
	</CTSHeader>
	<Data>
		<ProcedureRequest>
			<SpName>cob_procesador..sp_exec_atm_solicitud</SpName>
			<Param name="@t_trn" type="56" io="0" len="4">16062</Param>
			<Param name="@i_numero" type="56" io="0" len="4">129</Param>
			<Param name="@i_num_detalles" type="52" io="0" len="2">1</Param>
			<Param name="@i_num_producto" type="52" io="0" len="2"> 1</Param>
			<Param name="@i_num_excepcion" type="52" io="0" len="2"> 0</Param>
			<Param name="@i_oficial_neg" type="39" io="0" len="7">admuser</Param>
			<Param name="@i_ofi_entrega" type="52" io="0" len="2">1</Param>
			<Param name="@i_autorizado" type="39" io="0" len="7">admuser</Param>
			<Param name="@i_tipo_ent" type="47" io="0" len="1">O</Param>
			<Param name="@i_chip" type="39" io="0" len="1">N</Param>
			<Param name="@i_motivo" type="39" io="0" len="3">EMI</Param>
			<Param name="@o_numero" type="56" io="1" len="4">0</Param>
			<Param name="@i_oficial_neg" type="39" io="0" len="7">admuser</Param>
			<Param name="@o_numero" type="56" io="1" len="4">0</Param>

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