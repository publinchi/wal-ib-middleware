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

class Test_ib_batch_historic_refresh_balance {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	
	@Test
	void testExecuteNotification() {
		
		
		deleteLocalTmpTables();
		
		
		def num_registros = 10;
		def siguiente= 0;


		def sql = BDD.getInstance(CTSEnvironment.TARGETID_CENTRAL)
		
		
		println('---->>>>> INICIA ORQUESTACION DE REFRESH DE SALDOS HISTORICOS PARA CUENTAS CORRIENTES')
			
		def wRowCount=null
		def cont = 1;
		def nVeces;	
		def fecha_proceso ='01/10/2013';
		/*
		//wRowTemplate = sql.rows("select * from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_plantilla where pl_cta_credito =?",creditAccount)
		wRowCount = sql.rows("select count(*)/"+num_registros+" as valor, count(*) as total from cob_ahorros"+CTSEnvironment.DB_SEPARATOR+"ah_his_bloqueo where  hb_fecha >= '"+fecha_proceso+"' ")
		println('numero de veces a procesar'+ wRowCount.get(0))
		
		nVeces= Integer.valueOf(wRowCount.get(0).getAt(0))
		def totalReg= Integer.valueOf(wRowCount.get(0).getAt(1))
		
		if((num_registros*nVeces)<totalReg)
			nVeces=nVeces+1;
			
		println('numero de registros'+ num_registros)
		println('lazos a Procesar'+ nVeces)
		println('total de Registros'+ totalReg)


		
		while(cont<=nVeces){
			println('--->lazo:' + cont + "->> SIGUIENTE:" +siguiente)
			*/
		//-->
				
			def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_refresh_historico</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1875064</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18008</Param>
								<Param name="@i_batch" type="56" io="0" len="7">18405</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
								<Param name="@i_producto" type="52" io="0" len="1">4</Param>
								<Param name="@i_filial" type="52" io="0" len="1">1</Param>
								<Param name="@i_numero_registros" type="52" io="0" len="1">[NUM_REGISTRO]</Param>
								<Param name="@i_siguiente" type="52" io="0" len="1">[SIGUIENTE]</Param>
								<Param name="@i_fecha_proceso" type="56" io="0" len="4">[FECHA_PROCESO]</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
					//INICIO REPLACE DE VALORES
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[NUM_REGISTRO]", num_registros.toString())
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]", siguiente.toString())
					wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[FECHA_PROCESO]", fecha_proceso.toString())

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
						println ('--->>>>>ERROR AL EJECUTAR ORQUESTACION---->>>>>')
					else
						println ('--->>>>>EJECUCION CORRECTA DE ORQUESTACION')
					
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
			
	/*				
					
			cont++;
		}
*/
		///////////////////////////////////////////////////////////////////////////////////////////
		
		println('---->>>>> INICIA ORQUESTACION DE REFRESH DE SALDOS HISTORICOS PARA CUENTAS CORRIENTES')
			
		def wRowCountCte=null
		cont = 1;	
		num_registros = 13;
		siguiente = 0;
		fecha_proceso = '10/01/2013'
		/*
		//wRowTemplate = sql.rows("select * from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_plantilla where pl_cta_credito =?",creditAccount)
		wRowCountCte = sql.rows("select count(*)/"+num_registros+" as valor, count(*) as total from cob_cuentas"+CTSEnvironment.DB_SEPARATOR+"cc_his_bloqueo where  hb_fecha >='"+fecha_proceso+"' ")
		println('Procesamiento para CuentasCorrientes'+ wRowCountCte)
		
		nVeces= Integer.valueOf(wRowCountCte.get(0).getAt(0))
		totalReg= Integer.valueOf(wRowCountCte.get(0).getAt(1))
		
		if((num_registros*nVeces)<totalReg)
			nVeces=nVeces+1;
			
		println('numero de registros'+ num_registros)
		println('lazos a Procesar'+ nVeces)
		println('total de Registros'+ totalReg)

		
		while(cont<=nVeces){
			println('--->lazo:' + cont + "->> SIGUIENTE:" +siguiente)
		*/	
			def wRequestJavaOrchCentralCTE = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>				
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_procesador..sp_refresh_historico</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1875064</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18008</Param>
								<Param name="@i_batch" type="56" io="0" len="7">18405</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
								<Param name="@i_producto" type="52" io="0" len="1">3</Param>
								<Param name="@i_filial" type="52" io="0" len="1">4</Param>
								<Param name="@i_numero_registros" type="52" io="0" len="1">[NUM_REGISTRO]</Param>
								<Param name="@i_siguiente" type="52" io="0" len="1">[SIGUIENTE]</Param>
								<Param name="@i_fecha_proceso" type="56" io="0" len="4">[FECHA_PROCESO]</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
					//INICIO REPLACE DE VALORES
					wRequestJavaOrchCentralCTE= wRequestJavaOrchCentralCTE.replace("[NUM_REGISTRO]", num_registros.toString())
					wRequestJavaOrchCentralCTE= wRequestJavaOrchCentralCTE.replace("[SIGUIENTE]", siguiente.toString())
					wRequestJavaOrchCentralCTE= wRequestJavaOrchCentralCTE.replace("[FECHA_PROCESO]", fecha_proceso.toString())

					println 'Request---->>>>' + wRequestJavaOrchCentralCTE
					DefaultHttpClient httpclientSPCTE = new DefaultHttpClient()
					HttpPost httpPostSPCTE = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
			
					Map wAuthRespSPCTE =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSPCTE, httpPostSPCTE, true, 20)
					assert wAuthRespSPCTE.get("response")
			
					def wXmlSPCTE = new XmlParser().parseText(wRequestJavaOrchCentralCTE)
					
					def wXmlSerializedSPCTE = groovy.xml.XmlUtil.serialize( wXmlSPCTE )
					def wXmlResponseSPCTE  = CTSTestServletClient.execute(httpclientSPCTE, httpPostSPCTE, wXmlSerializedSPCTE)
					println "RESPONSE===>>>: "+ wXmlResponseSPCTE
			
					if(Assert.assertTrue(wXmlResponseSPCTE.indexOf("<return>0</return>") > -1 ))
						println ('--->>>>>ERROR AL EJECUTAR ORQUESTACION CTACTE---->>>>>')
					else
						println ('--->>>>>EJECUCION CORRECTA DE ORQUESTACION CTACTE')
					
					wXmlResponseSPCTE = wXmlResponseSPCTE.replace("<text>", "text");
					
					def wDocXmlSPCTE = new XmlParser().parseText(wXmlResponseSPCTE)
					
					DocumentBuilderFactory dbfCTE = DocumentBuilderFactory.newInstance();
					DocumentBuilder dbCTE = dbfCTE.newDocumentBuilder();
					InputSource isCTE = new InputSource(new StringReader(wXmlResponseSPCTE));
					Document docCTE = dbCTE.parse(isCTE);
			  
					Element OutputParamsCTE = docCTE.getDocumentElement();
					NodeList paramCTE = OutputParamsCTE.getElementsByTagName("OutputParams");
					  Element resultCTE = (Element) paramCTE.item(0);
					  
					  //System.out.println(result.getTextContent());
					siguiente = Integer.valueOf(resultCTE.getChildNodes().item(0).getTextContent())
					println ('o_isguiente:->'+siguiente)
					wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
			
			/*		
					
			cont++;
		}
		*/
		
		

		
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
                                    <Param name="@i_operacion" type="56" io="0" len="1">3</Param>
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


}





