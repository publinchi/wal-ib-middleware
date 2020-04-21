



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
 * @author kmeza
 *
 */
class TestNotification {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();


	//@Test
	void testExecuteJavaOrch(String Tipo) {
		def wRequestJavaOrch = null;
		
		   if (Tipo.equals("E")) {
			     
			   
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
		    <CTSHeader>
			   <Field name="serviceId" 		   type="S"> InternetBanking.WebApp.Transfers.Transfer.TransferBetweenOwnAccounts
			   </Field>
			   <Field name="sessionId" 		   type="S">ID:414d5120514d2e42524f4b45523033208363c954201dd208 </Field>
			   <Field name="supportOffline"    type="C">N</Field>
			   <Field name="contextId" 		   type="S">COBISBV</Field>
			   <Field name="source" 		   type="N">13</Field>
                <Field name="reentryExecution" Type="S">Y</Field>
			  </CTSHeader>
					<Data>
				<ProcedureRequest>
			   	<SpName>cob_procesador..sp_tr_transferencias</SpName>
			   <Param name="@i_cta" 			type="39" io="0" len="17">10410108275406111</Param>
			   <Param name="@i_prod" 			type="48" io="0" len="1">3</Param>
			   <Param name="@i_mon" 			type="52" io="0" len="2">0</Param>
			   <Param name="@i_producto" 		type="39" io="0" len="3">CTE</Param>
			   <Param name="@i_prod_des" 		type="52" io="0" len="2">4</Param>
			   <Param name="@i_mon_des" 		type="52" io="0" len="2">0</Param>
			   <Param name="@i_cta_des" 		type="39" io="0" len="17">10410108275249013</Param>
			   <Param name="@i_login" 			type="39" io="0" len="7">testCts</Param>
			   <Param name="@i_val" 			type="60" io="0" len="8">5.33</Param>
			   <Param name="@i_concepto" 		type="39" io="0" len="44">TRANSFERENCIA ENTRE CUENTAS PROPIAS ACOPLADO</Param>
			   <Param name="@i_ente" 			type="56" io="0" len="4">13036</Param>
			   <Param name="@t_trn" 			type="56" io="0" len="4">1800009</Param>
			   <Param name="@t_ejec" 			type="47" io="0" len="1">R</Param>
			   <Param name="@t_rty" 			type="47" io="0" len="1">N</Param>
			   <Param name="@o_referencia" 		type="56" io="1" len="0">0</Param>
			   <Param name="@o_retorno" 		type="56" io="1" len="0">0</Param>
			   <Param name="@o_condicion" 		type="56" io="1" len="0">0</Param>
			   <Param name="@o_autorizacion" 	type="47" io="1" len="0">N</Param>
			   <Param name="@o_ssn_branch" 		type="56" io="1" len="0">0</Param>
			   	</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''			   
		   } 
		 		 		
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		println "aaaa"
	   Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		
		assert wAuthResp.get("response")
		
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse
		Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		def wDocXml = new XmlParser().parseText(wXmlResponse);
		def wDocXmlSerialized =groovy.xml.XmlUtil.serialize( wDocXml )
		println wDocXmlSerialized
	}
	
	
	
}

