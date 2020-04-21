package com.cobiscorp.batch

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
import java.util.concurrent.ConcurrentLinkedQueue.Node;

import org.xml.sax.InputSource
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.http.client.methods.HttpPost

class Test_ib_batch_generate_data_customer {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	@Test
	void testGenerateDataCustomer(){
		println ('====================================================<< Ejecutando SP sp_bv_afiliacion_cliente_ej - @i_operacion D >>====================================================')
		def wRequestJavaOrchSpDel = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>							
							<Field name="target" type="S">SPExecutor</Field>
							<Field name="msgType" type="S">ProcedureRequest</Field>
							<Field name="serviceName" type="S">cob_bvirtual..sp_bv_afiliacion_cliente_ej</Field>
							<Field name="dbms" type="S">SQLCTS</Field>
							<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>					
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_bvirtual..sp_bv_afiliacion_cliente_ej</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1801099</Param>
								<Param name="@i_fecha_proceso" type="61" io="0" len="10">01/01/2014</Param>
								<Param name="@i_operacion" type="39" io="0" len="1">D</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18010</Param>
								<Param name="@i_batch" type="56" io="0" len="7">18416</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>
							'''
		
							
		println 'Request---->>>> ' + wRequestJavaOrchSpDel
		DefaultHttpClient httpclientSpDel = new DefaultHttpClient()
		HttpPost httpPostSpDel = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		
		Map wAuthRespDel =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSpDel, httpPostSpDel, true, 20)
		assert wAuthRespDel.get("response")
		
		def wXmlDel = new XmlParser().parseText(wRequestJavaOrchSpDel)
				
		def wXmlSerializedDel = groovy.xml.XmlUtil.serialize( wXmlDel )
		def wXmlResponseDel  = CTSTestServletClient.execute(httpclientSpDel, httpPostSpDel, wXmlSerializedDel)
		println "RESPONSE===>>>: "+ wXmlResponseDel
		
		if(Assert.assertTrue(wXmlResponseDel.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP CONSOLIDADO---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP sp_bv_afiliacion_cliente_ej Opcion D')
				
		wXmlResponseDel = wXmlResponseDel.replace("<text>", "text");
				
		def wDocXmlDel = new XmlParser().parseText(wXmlResponseDel)
		
		
		println (' ')
		println ('====================================================<< Ejecutando de Orquestacion >>====================================================')
		def sqlCent = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		println('---->>>>> INICIO OBTENIENDO DESCRIPCION DE PRODUCTOS')
		def siguiente = 0
		def rowcount = 16
		def wLastRecord = rowcount
		Map<String, String> aMapDesProducto = new HashMap<String, String>();
		println('---->>>>> Antes de la ejecucion de la orquestacion')
		println ('siguiente ==> ' + siguiente)
		println ('wLastRecord ==> ' + wLastRecord)
		//while (siguiente < 629568)
		while (wLastRecord == rowcount)
		{
			def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>[SP_NAME]</SpName>
								<Param name="@t_trn"           type="56" io="0" len="7">[TRX]</Param>
								<Param name="@i_sarta"         type="56" io="0" len="7">18010</Param>
								<Param name="@i_batch"         type="56" io="0" len="7">18416</Param>
								<Param name="@i_secuencial"    type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida"       type="56" io="0" len="1">1</Param>
								<Param name="@i_intento"       type="56" io="0" len="1">1</Param>
								<Param name="@i_fecha_proceso" type="39" io="0" len="13">01/01/2013</Param>
								<Param name="@i_fecha_ingreso" type="39" io="0" len="13">01/01/2013</Param>
								<Param name="@i_rowcount"      type="56" io="0" len="7">[ROWCOUNT]</Param>
								<Param name="@i_ente_mis"      type="56" io="0" len="7">[ENTE]</Param>
								<Param name="@o_siguiente"     type="56" io="1" len="7">[SIGUIENTE]</Param>
								<Param name="@o_rowcount"      type="56" io="1" len="7">0</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''

			//INICIO REPLACE DE VALORES
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SP_NAME]","cob_procesador..sp_bv_gen_datclient")
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[TRX]","1889999")
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[ENTE]",siguiente.toString())
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]",siguiente.toString())
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[ROWCOUNT]",rowcount.toString())
			
			println 'Request---->>>> ' + wRequestJavaOrchCentral
			//FIN REPLACE DE VALORES
			DefaultHttpClient httpclientSP = new DefaultHttpClient()
			HttpPost httpPostSP = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
											
			Map wAuthRespSP =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSP, httpPostSP, true, 20)
			assert wAuthRespSP.get("response")
			
			def wXmlSP = new XmlParser().parseText(wRequestJavaOrchCentral)
			def wXmlSerializedSP = groovy.xml.XmlUtil.serialize( wXmlSP )
			def wXmlResponseSP  = CTSTestServletClient.execute(httpclientSP, httpPostSP, wXmlSerializedSP)
			println "RESPONSE===>>>: "+ wXmlResponseSP
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
			siguiente = Integer.valueOf(result.getChildNodes().item(0).getTextContent())
			wLastRecord = Integer.valueOf(result.getChildNodes().item(1).getTextContent())
			
			println('Siguiente ==> ' + siguiente)			
			println('rowcount ==> ' + rowcount)
			println ('wLastRecord ==> ' + wLastRecord)
			wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");			
		}
		
		
		println (' ')
		println ('====================================================<< Ejecutando SP sp_bv_afiliacion_cliente_ej - @i_operacion I >>===================================================')
		def wRequestJavaOrchSpIns = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>							
							<Field name="target" type="S">SPExecutor</Field>
							<Field name="msgType" type="S">ProcedureRequest</Field>
							<Field name="serviceName" type="S">cob_bvirtual..sp_bv_afiliacion_cliente_ej</Field>
							<Field name="dbms" type="S">SQLCTS</Field>
							<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>					
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_bvirtual..sp_bv_afiliacion_cliente_ej</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1801099</Param>
								<Param name="@i_fecha_proceso" type="61" io="0" len="10">01/01/2014</Param>
								<Param name="@i_operacion" type="39" io="0" len="1">I</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18010</Param>
								<Param name="@i_batch" type="56" io="0" len="7">18416</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>
							'''

				
		println 'Request---->>>> ' + wRequestJavaOrchSpIns
		DefaultHttpClient httpclientSpIns = new DefaultHttpClient()
		HttpPost httpPostSpIns = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		
		Map wAuthRespIns =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSpIns, httpPostSpIns, true, 20)
		assert wAuthRespIns.get("response")
		
		def wXmlIns = new XmlParser().parseText(wRequestJavaOrchSpIns)
			
		def wXmlSerializedIns = groovy.xml.XmlUtil.serialize( wXmlIns )
		def wXmlResponseIns  = CTSTestServletClient.execute(httpclientSpIns, httpPostSpIns, wXmlSerializedIns)
		println "RESPONSE===>>>: "+ wXmlResponseIns
		
		if(Assert.assertTrue(wXmlResponseIns.indexOf("<return>0</return>") > -1 ))
		println ('--->>>>>ERROR AL EJECUTAR SP CONSOLIDADO---->>>>>')
		else
		println ('--->>>>>EJECUCION CORRECTA DE SP sp_bv_afiliacion_cliente_ej Opcion I')
			
		wXmlResponseIns = wXmlResponseIns.replace("<text>", "text");
			
		def wDocXmlIns = new XmlParser().parseText(wXmlResponseIns)
		
	}
}
