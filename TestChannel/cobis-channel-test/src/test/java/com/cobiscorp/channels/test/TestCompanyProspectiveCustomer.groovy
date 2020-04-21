package com.cobiscorp.channels.test;


import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.junit.Assert
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.CTSTestServletClient
import com.cobiscorp.test.utils.NCSAuth

/**
 * @author gyagual                                                                      
 */
class TestCompanyProspectiveCustomer {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();


	@Test
	void TestCompanyProspectiveCustomerbyCode() {
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
			<CTSHeader>
			</CTSHeader>
			<Data>
				<ProcedureRequest>
					<SpName>cob_procesador..sp_prospectos_ofi</SpName>
					<Param name="@t_trn" type="56" io="0" len="7">1875057</Param>
					<Param name="@i_subtipo" type="47" io="0" len="1">C</Param>
					<Param name="@i_tipo" type="56" io="0" len="1">1</Param>
					<Param name="@i_ente" type="47" io="0" len="1">108232</Param>
					<Param name="@i_modo" type="56" io="0" len="1">0</Param>
				</ProcedureRequest>
			</Data>
		</CTSMessage>'''

		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		print httpclient
		print httpPost
		print wXmlSerialized 
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)

		 Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
		def wDocXmlSerialized =groovy.xml.XmlUtil.serialize( wDocXml )
		println wDocXmlSerialized
	}
	
	@Test
	void TestCompanyProspectiveCustomerbyAlphabetical() {
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
			<CTSHeader>
			</CTSHeader>
			<Data>
				<ProcedureRequest>
					<SpName>cob_procesador..sp_prospectos_ofi</SpName>
					<Param name="@t_trn" type="56" io="0" len="7">1875057</Param>
					<Param name="@i_subtipo" type="47" io="0" len="1">C</Param>
					<Param name="@i_tipo" type="56" io="0" len="1">5</Param>
					<Param name="@i_nombre" type="47" io="0" len="1">NOMBRE</Param>
					<Param name="@i_p_apellido" type="47" io="0" len="1">APELLIDO</Param>
					<Param name="@i_modo" type="56" io="0" len="1">0</Param>
				</ProcedureRequest>
			</Data>
		</CTSMessage>'''

		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		print httpclient
		print httpPost
		print wXmlSerialized
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)

		 Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
		def wDocXmlSerialized =groovy.xml.XmlUtil.serialize( wDocXml )
		println wDocXmlSerialized
	}
	

	@Test
	void TestCompanyProspectiveCustomerbyMarriedName() {
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
			<CTSHeader>
			</CTSHeader>
			<Data>
				<ProcedureRequest>
					<SpName>cob_procesador..sp_prospectos_ofi</SpName>
					<Param name="@t_trn" type="56" io="0" len="7">1875057</Param>
					<Param name="@i_subtipo" type="47" io="0" len="1">C</Param>
					<Param name="@i_tipo" type="56" io="0" len="1">4</Param>
					<Param name="@i_c_apellido" type="47" io="0" len="1">APELLIDO</Param>
					<Param name="@i_modo" type="56" io="0" len="1">0</Param>
				</ProcedureRequest>
			</Data>
		</CTSMessage>'''

		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		print httpclient
		print httpPost
		print wXmlSerialized
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)

		 Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
		def wDocXmlSerialized =groovy.xml.XmlUtil.serialize( wDocXml )
		println wDocXmlSerialized
	}
	
	@Test
	void TestCompanyProspectiveCustomerbyId() {
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
			<CTSHeader>
			</CTSHeader>
			<Data>
				<ProcedureRequest>
					<SpName>cob_procesador..sp_prospectos_ofi</SpName>
					<Param name="@t_trn" type="56" io="0" len="7">1875057</Param>
					<Param name="@i_subtipo" type="47" io="0" len="1">C</Param>
					<Param name="@i_tipo" type="56" io="0" len="1">2</Param>
					<Param name="@i_ced_ruc" type="47" io="0" len="1">0923837934</Param>
					<Param name="@i_modo" type="56" io="0" len="1">0</Param>
				</ProcedureRequest>
			</Data>
		</CTSMessage>'''

		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		print httpclient
		print httpPost
		print wXmlSerialized
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)

		 Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
		def wDocXmlSerialized =groovy.xml.XmlUtil.serialize( wDocXml )
		println wDocXmlSerialized
	}

}