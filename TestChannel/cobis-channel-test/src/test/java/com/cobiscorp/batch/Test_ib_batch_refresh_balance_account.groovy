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

class Test_ib_batch_refresh_balance_account {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	
	@Test
	void testExecuteNotification() {
		
		def num_registros = 1000;
		def siguiente= 0;


		def sql = BDD.getInstance(CTSEnvironment.TARGETID_CENTRAL)
		
		
		println('---->>>>> VERIFICANDO REGISTROS EN TABLA bv_procesar_notificacion')
			
		def wRowCount=null
		def cont = 1;
		def cont_aux;
		
		wRowCount = sql.rows("select count(*)/"+num_registros+" as valor from cob_ahorros"+CTSEnvironment.DB_SEPARATOR+"ah_cuenta")
		println('numero de veces a procesar'+ wRowCount)
				
		for(var in wRowCount.toArray()){
			cont_aux = var.getAt("valor")
		}	

		
		while(cont<=cont_aux){
			println('lazo:' + cont)
			
			def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_refresh_account</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1875065</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18008</Param>
								<Param name="@i_batch" type="56" io="0" len="7">18401</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
								<Param name="@i_producto" type="52" io="0" len="1">4</Param>
								<Param name="@i_filial" type="52" io="0" len="1">4</Param>
								<Param name="@i_num_registros" type="52" io="0" len="1">[NUM_REGISTRO]</Param>
								<Param name="@i_siguiente" type="52" io="0" len="1">[SIGUIENTE]</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
					//INICIO REPLACE DE VALORES
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_REGISTRO]", num_registros.toString())
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())

					println 'Request---->>>>' + wRequestJavaOrchCentral
					DefaultHttpClient httpclientSP = new DefaultHttpClient()
					HttpPost httpPostSP = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
			
					Map wAuthRespSP =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSP, httpPostSP, true, 20)
					assert wAuthRespSP.get("response")
			
					def wXmlSP = new XmlParser().parseText(wRequestJavaOrchCentral)
					
					def wXmlSerializedSP = groovy.xml.XmlUtil.serialize( wXmlSP )
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
					NodeList param = OutputParams.getElementsByTagName("OutputParams");
					  Element result = (Element) param.item(0);
					  //System.out.println(result.getTextContent());
					siguiente = Integer.valueOf(result.getTextContent())
					wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
			
					
					
			cont++;
		}
	
		

		
	}

}



