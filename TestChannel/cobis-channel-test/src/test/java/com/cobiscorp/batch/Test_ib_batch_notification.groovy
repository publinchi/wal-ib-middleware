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

class Test_ib_batch_notification {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	
	@Test
	void testExecuteNotification() {
		
		def wFinalRequest = ''
		
		 
		println ('=============Ejecutando SP que Consolida Datos a Procesar==========================')
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
<CTSMessage>
	<CTSHeader>
		
		<Field name="target" type="S">SPExecutor</Field>
		<Field name="msgType" type="S">ProcedureRequest</Field>
		<Field name="serviceName" type="S">cob_bvirtual..sp_pr_consolida_notif</Field>
		<Field name="dbms" type="S">SQLCTS</Field>
		<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>

	</CTSHeader>
	<Data>
		<ProcedureRequest>
			<SpName>cob_bvirtual..sp_pr_consolida_notif</SpName>
			<Param name="@t_trn" type="56" io="0" len="7">1889993</Param>
			<Param name="@i_sarta" type="56" io="0" len="7">18010</Param>
			<Param name="@i_batch" type="56" io="0" len="7">18416</Param>
			<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
			<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
			<Param name="@i_intento" type="56" io="0" len="1">1</Param>
		</ProcedureRequest>
	</Data>
</CTSMessage>
		'''
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");

		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE===>>>: "+ wXmlResponse

		if(Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP CONSOLIDADO---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP CONSOLIDADO')
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
	
		
		
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def siguiente = 0
		def wLastRecord=null
		long t1 = System.currentTimeMillis();
		wLastRecord = sql.rows("select max(pn_codigo) from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_procesar_notificacion where pn_id in ('N2','N4','N6','N5','N7')")
		String Num =  wLastRecord.toString()
		//println('----Num ' +  Num)
		//println('----numero de registros ' +  wRowCount.toString().substring(3,(Num.size() - 2)))
		def wIntLastRecord = wLastRecord.toString().substring(3,(Num.size() - 2)).toInteger()
		
				while (siguiente < wIntLastRecord){
					println('----siguiente ' +  siguiente.toString())
					println('----ultimo registro a procesar ' +  wIntLastRecord.toString())
					def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_bv_cons_not_batch</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1875051</Param>
								<Param name="@i_numero_registros" type="56" io="0" len="4">100</Param>
								<Param name="@i_fecha_proceso" type="56" io="0" len="4">01/01/2014</Param>
								<Param name="@o_siguiente" type="39" io="1" len="20">[SIGUIENTE]</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
							//INICIO REPLACE DE VALORES
							wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())
							//println 'Request---->>>>' + wRequestJavaOrchCentral
							//FIN REPLACE DE VALORES
							
							DefaultHttpClient httpclientSP = new DefaultHttpClient()
							HttpPost httpPostSP = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
					
							Map wAuthRespSP =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSP, httpPostSP, true, 20)
							assert wAuthRespSP.get("response")
					
							def wXmlSP = new XmlParser().parseText(wRequestJavaOrchCentral)
							
							def wXmlSerializedSP = groovy.xml.XmlUtil.serialize( wXmlSP )
							def wXmlResponseSP  = CTSTestServletClient.execute(httpclientSP, httpPostSP, wXmlSerializedSP)
							//println "RESPONSE===>>>: "+ wXmlResponseSP
							def wDocXmlSP = new XmlParser().parseText(wXmlResponseSP)
							
							if(Assert.assertTrue(wXmlResponseSP.indexOf("<return>0</return>") > -1 ))
								println ('--->>>>>ERROR AL EJECUTAR SP DEL CENTRAL---->>>>>')
							else
								println ('--->>>>>EJECUCION CORRECTA DE SP CENTRAL')
							
							
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							DocumentBuilder db = dbf.newDocumentBuilder();
							InputSource is = new InputSource(new StringReader(wXmlResponseSP));
							Document doc = db.parse(is);
					  
							Element OutputParams = doc.getDocumentElement();
							NodeList param = OutputParams.getElementsByTagName("OutputParams");
							  Element result = (Element) param.item(0);
							  //System.out.println(result.getTextContent());
							siguiente = Integer.valueOf(result.getTextContent())
							wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
							
				}
				
				
				long t2 = System.currentTimeMillis();
				long dif = t2-t1;
				long tiempo = dif/60000;
				
				println ('tiempo:' + tiempo)
				
	}
		
	
		 
	}




