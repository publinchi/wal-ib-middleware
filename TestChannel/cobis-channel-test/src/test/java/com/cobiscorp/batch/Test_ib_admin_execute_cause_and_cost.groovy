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

class Test_ib_admin_execute_cause_and_cost {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	@Test
	void testGenerateDataCustomer(){
		println (' ')
		println ('=====================================<< Ejecutando de Orquestacion >>=====================================')
		println('---->>>>> INICIO OBTENIENDO DESCRIPCION DE PRODUCTOS')
		//Variables a ser usadas
		def operacion = "S" //Opciones "R"= Insert; "B"= Delete; "S" = Busqueda, "ST" = Busqueda
		def wRequestJavaOrchCentral = ""
		def transaccion = 18059
				
		Map<String, String> aMapDesProducto = new HashMap<String, String>();
		println('---->>>>> Antes de la ejecucion de la orquestacion')
		//while (siguiente < 629568)
		if (operacion == "R")
		{
			wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>[SP_NAME]</SpName>
								<Param name="@t_trn"                type="56" io="0" len="7">[TRX]</Param>
								<Param name="@i_operacion"          type="39" io="0" len="7">[OPERACION]</Param>
								<Param name="@i_transaccion"        type="56" io="0" len="7">[TRANSACCION]</Param>
								<Param name="@i_producto"           type="56" io="0" len="1">1</Param>
								<Param name="@i_servicio"           type="39" io="0" len="1">1</Param>
								<Param name="@i_causa"              type="39" io="0" len="1">1</Param>
								<Param name="@i_costo_transaccion"  type="39" io="0" len="13">01/01/2013</Param>
								<Param name="@i_tran_ant"           type="56" io="0" len="13">01/01/2013</Param>
								<Param name="@i_tipo"               type="39" io="0" len="7">[ROWCOUNT]</Param>
								<Param name="@i_formato_fecha"      type="56" io="0" len="7">[ENTE]</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
		}
		if (operacion == "D")
		{
			wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>[SP_NAME]</SpName>
								<Param name="@t_trn"                type="56" io="0" len="7">[TRX]</Param>
								<Param name="@i_operacion"          type="39" io="0" len="7">[OPERACION]</Param>
								<Param name="@i_transaccion"        type="56" io="0" len="7">18416</Param>
								<Param name="@i_producto"           type="56" io="0" len="1">1</Param>
								<Param name="@i_servicio"           type="39" io="0" len="1">1</Param>
								<Param name="@i_causa"              type="39" io="0" len="1">1</Param>
								<Param name="@i_costo_transaccion"  type="39" io="0" len="13">01/01/2013</Param>								
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
		}
		if (operacion == "S" || operacion == "ST")
		{
			wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>[SP_NAME]</SpName>
								<Param name="@t_trn"                type="56" io="0" len="7">[TRX]</Param>
								<Param name="@i_operacion"          type="39" io="0" len="7">[OPERACION]</Param>
								<Param name="@i_transaccion"        type="56" io="0" len="7">18416</Param>															
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
		}

		//INICIO REPLACE DE VALORES
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SP_NAME]","cob_procesador..sp_bv_ing_serv")
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[TRX]","1800111")
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[OPERACION]",operacion.toString())
		wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[TRANSACCION]",transaccion.toString())
		
			
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
			
		wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
	}
}
