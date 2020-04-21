package com.cobiscorp.chanels.bv.test;

import static org.junit.Assert.*

import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.CTSTestServletClient
import com.cobiscorp.test.utils.NCSAuth



class TestStoredProcedureExec {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	/**
	 * Service Id: InternetBanking.WebApp.Admin.Swift.GetBanksByCountry
	 */
	@Test
	public void testAdminService(){

		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
<CTSMessage>
	<CTSHeader>
	</CTSHeader>
	<Data>
		<ProcedureRequest>
			<SpName>cob_procesador..sp_exec_sp_adm_canales</SpName>
			<Param name="@t_trn" type="38" io="0" len="4">18000</Param>

			<Param name="@t_orig_db_name" type="39" io="0" len="5">cob_comext</Param>
			<Param name="@t_orig_sp_name" type="39" io="0" len="9">sp_query_pais_banco</Param>
			<Param name="@t_orig_trn" type="38" io="0" len="4">1800010</Param>

			<Param name="@i_modo" type="48" io="0" len="4">0</Param>
			<Param name="@i_pais" type="52" io="0" len="4">4</Param>
		</ProcedureRequest>
	</Data>
</CTSMessage>
		'''
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler");

		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse

		def CTSMessage = new XmlParser().parseText(wXmlResponse)
		println ">>>>>>>>>>"+  CTSMessage.Data.ProcedureResponse.ResultSet.rw.findAll().size()
		//assert '0' == CTSMessage.Data.ProcedureResponse.return.text()
		assert CTSMessage.Data.ProcedureResponse.ResultSet.rw.findAll().size()==0
	}

	/**
	 * Service Id: InternetBanking.WebApp.Admin.Service.Swift.GetCities
	 */
	@Test
	void testGetCities() {

		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
<CTSMessage>
	<CTSHeader>
	</CTSHeader>
	<Data>
		<ProcedureRequest>
			<SpName>cob_procesador..sp_exec_sp_adm_canales</SpName>
			<Param name="@t_trn" type="38" io="0" len="4">18000</Param>

			<Param name="@t_orig_db_name" type="39" io="0" len="5">cob_bvirtual</Param>
			<Param name="@t_orig_sp_name" type="39" io="0" len="9">sp_bv_get_cities</Param>
			<Param name="@t_orig_trn" type="38" io="0" len="4">1800192</Param>

			<Param name="@i_tipo" type="47" io="0" len="4">T</Param>
			<Param name="@i_valor" type="39" io="0" len="4">%</Param>
			<Param name="@i_cont" type="47" io="0" len="4">AMS</Param>
			<Param name="@i_pais" type="56" io="0" len="4">12</Param>
		</ProcedureRequest>
	</Data>
</CTSMessage>
		'''
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler");

		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse

		def CTSMessage = new XmlParser().parseText(wXmlResponse)
		assert '0' == CTSMessage.Data.ProcedureResponse.return.text()
		assert CTSMessage.Data.ProcedureResponse.ResultSet.rw.findAll().size()>0
	}
}
