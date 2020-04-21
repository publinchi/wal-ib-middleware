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

class Test_ib_batch_refresh_account {
	def fecha_proceso ='01/10/2013';
	def sp_exec_local = null;
	def num_registros = 455;
	def siguiente= 0;
	def sql = BDD.getInstance(CTSEnvironment.TARGETID_CENTRAL)
	def wRowCount=null
	def cont = 1;
	def cont_aux;
	
	def num_trn      = null;
	def num_trn_act  = null;
	def num_batch    = null;
	def num_producto = null;

	def registros = num_registros;
	long t1 = System.currentTimeMillis();

	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	
	@Test
	void testExecuteNotification() {

    	deleteLocalTmpTables();
				
		///////////////////////////////////////////////////////////////////////////////////////////		
		println('---->>>>> INICIA ORQUESTACION DE REFRESH DE SALDOS PARA CUENTAS CORRIENTES')
		//wRowCount = sql.rows("select count(*)/"+num_registros+" as valor from cob_cuentas"+CTSEnvironment.DB_SEPARATOR+"cc_ctacte where cc_estado !='C'")
		num_trn      = 1875065;
		num_batch    = 18402;
		num_producto = 3;

		executeOrchestration();
	
       ///////////////////////////////////////////////////////////////////////////////////////////
		println('---->>>>> INICIA ORQUESTACION DE REFRESH DE SALDOS PARA CUENTAS DE AHORROS')
		//wRowCount = sql.rows("select count(*)/"+num_registros+" as valor from cob_ahorros"+CTSEnvironment.DB_SEPARATOR+"ah_cuenta where ah_estado !='C'")
				
		num_trn      = 1875065;
		num_batch    = 18401;
		num_producto = 4;

		executeOrchestration();
		
		println ('=============Ejecutando SP actualiza_cta_ej=====================')
		sp_exec_local = "cob_ahorros..sp_ah_actualiza_cta_ej";
		num_trn_act   = 1890006;
		UpdateAccountBalance();
		
		sp_exec_local = "cob_cuentas..sp_cc_actualiza_cta_ej";
		num_trn_act   = 1890007;
		UpdateAccountBalance();
	}
	void deleteLocalTmpTables(){
		println ('=============Ejecutando SP que elimina las tablas temporales del local=====================')
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
            <CTSMessage>
                <CTSHeader>
					<Field name="target" type="S">SPExecutor</Field>
					<Field name="msgType" type="S">ProcedureRequest</Field>
					<Field name="serviceName" type="S">cob_bvirtual..sp_borra_datos_por_refresh</Field>
					<Field name="dbms" type="S">SQLCTS</Field>
					<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>

                </CTSHeader>
                <Data>
					<ProcedureRequest>
						<SpName>cob_bvirtual..sp_borra_datos_por_refresh</SpName>
						<Param name="@t_trn" type="56" io="0" len="7">1875066</Param>
						<Param name="@i_sarta" type="56" io="0" len="7">18010</Param>
						<Param name="@i_batch" type="56" io="0" len="7">18416</Param>
						<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
						<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
						<Param name="@i_intento" type="56" io="0" len="1">1</Param>
						<Param name="@i_operacion" type="56" io="0" len="1">1</Param>
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
			println ('--->>>>>ERROR AL EJECUTAR SP local---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP local')
		
		
    }
	
    void executeOrchestration(){
			
		registros = -1;
		//println('numero de veces a procesar'+ wRowCount)

		//while(cont<=cont_aux){
		while (registros != 0){
			//println('lazo:' + cont)
			
			def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_refresh_account</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">[NUM_TRN]</Param>                                
								<Param name="@i_sarta" type="56" io="0" len="7">18008</Param>
								<Param name="@i_batch" type="56" io="0" len="7">[NUM_BATCH]</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
								<Param name="@i_producto" type="52" io="0" len="1">[PRODUCTO]</Param>
								<Param name="@i_filial" type="52" io="0" len="1">1</Param>
								<Param name="@i_numero_registros" type="52" io="0" len="1">[NUM_REGISTRO]</Param>								
                                <Param name="@i_siguiente" type="52" io="1" len="20">[SIGUIENTE]</Param>
								<Param name="@o_siguiente" type="52" io="1" len="20">[SIGUIENTE]</Param>
                                <Param name="@o_registros" type="52" io="1" len="30">[REGISTROS]</Param>

							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
					//INICIO REPLACE DE VALORES
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())					
					///////// Registros obtenidos en la consulta
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[REGISTROS]", registros.toString())
							
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_TRN]", num_trn.toString())
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_BATCH]", num_batch.toString())
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[PRODUCTO]", num_producto.toString())

					////////// Número de registros a obtener
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_REGISTRO]", num_registros.toString()) 
					//wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())

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
					  
					  siguiente = Integer.valueOf(result.getChildNodes().item(0).getTextContent())
					  registros = Integer.valueOf(result.getChildNodes().item(1).getTextContent())
					  
					  println ('Siguiente Test ---->' +  siguiente)
					  println ('Registros Test ---->' +  registros)
					  
					  //System.out.println(result.getTextContent());
					////siguiente = Integer.valueOf(result.getTextContent())
					
					wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
					
			cont++;
		}	
	}
	
	void UpdateAccountBalance(){
		println ('=============Ejecutando SP que Actualiza maestro de cuenta del local=====================')
		
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
            <CTSMessage>
                <CTSHeader>
					<Field name="target" type="S">SPExecutor</Field>
					<Field name="msgType" type="S">ProcedureRequest</Field>
					<Field name="serviceName" type="S">[SP_NAME]</Field>
					<Field name="dbms" type="S">SQLCTS</Field>
					<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>

                </CTSHeader>
                <Data>
					<ProcedureRequest>
						<SpName>[SP_NAME]</SpName>
						<Param name="@t_trn" type="56" io="0" len="7">[NUM_TRN_ACT]</Param>
                        <Param name="@i_filial" type="52" io="0" len="1">4</Param>
                        <Param name="@i_fecha_proceso" type="61" io="0" len="4">[FECHA_PROCESO]</Param>
						<Param name="@i_sarta" type="56" io="0" len="7">18010</Param>
						<Param name="@i_batch" type="56" io="0" len="7">18416</Param>
						<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
						<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
						<Param name="@i_intento" type="56" io="0" len="1">1</Param>
					</ProcedureRequest>
                </Data>
            </CTSMessage>
                                '''
		//INICIO REPLACE DE VALORES		
		//<Param name="@t_trn" type="56" io="0" len="7">1890006</Param>
		wRequestJavaOrch= wRequestJavaOrch.replace("[FECHA_PROCESO]", fecha_proceso.toString())
		wRequestJavaOrch= wRequestJavaOrch.replace("[SP_NAME]", sp_exec_local)
		wRequestJavaOrch= wRequestJavaOrch.replace("[NUM_TRN_ACT]", num_trn_act.toString())
			
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");

		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE===>>>: "+ wXmlResponse

		if(Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP local---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP local')

	}
}



