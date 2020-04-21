package com.cobiscorp.batch

import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;

//imports para ejecutar sentencia sql

import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.SqlExecutorUtils


//imports basico para ejecutar prueba de regresion

import org.junit.Assert
import org.junit.rules.ExternalResource;

import com.cobiscorp.test.SetUpTestEnvironment
//imports para ejecutar trama XML

import org.apache.http.impl.client.DefaultHttpClient

import com.cobiscorp.test.utils.NCSAuth
import com.cobiscorp.test.utils.CTSTestServletClient

import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.InputSource
import org.xml.sax.SAXException;
import org.w3c.dom.*;

import org.apache.http.client.methods.HttpPost

class Test_ib_credit_line {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();


	@Test
	void testCreditLine() {

		def num_registros = 33;
		def siguiente= 0;
		def registros= num_registros;
		long t1 = System.currentTimeMillis();


		//while (registros != 0){

		def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_line_credit</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1800035</Param>
								<Param name="@i_cliente" type="56" io="0" len="7">44</Param>
								<Param name="@i_origen" type="52" io="0" len="7">AD</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
		//INICIO REPLACE DE VALORES
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_REGISTRO]", num_registros.toString())
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())


		println 'Request---->>>>' + wRequestJavaOrchCentral
		DefaultHttpClient httpclientSP = new DefaultHttpClient()
		HttpPost httpPostSP = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler");
		println 'abajo---->>>>'
		println 'LOGIN---->>>>' +CTSEnvironment.login
		println 'pass---->>>>' +CTSEnvironment.password
		println 'httpclientSP---->>>>' +httpclientSP
		println 'httpPostSP---->>>>' +httpPostSP
		Map wAuthRespSP =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSP, httpPostSP, true, 3)
		println 'aut---->>>>'
		assert wAuthRespSP.get("response")
		println 'aca---->>>>'
		def wXmlSP = new XmlParser().parseText(wRequestJavaOrchCentral)
		println 'parse---->>>>'
		def wXmlSerializedSP = groovy.xml.XmlUtil.serialize( wXmlSP )
		println 'medio---->>>>'
		def wXmlResponseSP  = CTSTestServletClient.execute(httpclientSP, httpPostSP, wXmlSerializedSP)
		println "RESPONSE===>>>: "+ wXmlResponseSP

		if(Assert.assertTrue(wXmlResponseSP.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP DEL CENTRAL---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP CENTRAL')

		wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");

		def wDocXmlSP = new XmlParser().parseText(wXmlResponseSP)

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(wXmlResponseSP));
		Document doc = db.parse(is);

		Element OutputParams = doc.getDocumentElement();
	//	NodeList param = OutputParams.getElementsByTagName("OutputParams");
		Element result = (Element) param.item(0);

		//siguiente = Integer.valueOf(result.getChildNodes().item(0).getTextContent())
		//cuenta = Integer.valueOf(result.getChildNodes().item(1).getTextContent())
	//	siguiente = Integer.valueOf(result.getChildNodes().item(0).getTextContent())
		wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");

		//}
		long t2 = System.currentTimeMillis();
		long dif = t2-t1;
		long tiempo = dif/60000;

		println ('tiempo:' + tiempo)
	}







}



