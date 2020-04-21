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

import org.xml.sax.InputSource
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.http.client.methods.HttpPost

class Test_ib_batch_generate_data_productOpening {

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	@Test
	void testGenerateDataProduct(){
		println ('====================================================<< Ejecutando SP sp_bv_afiliacion_prod_ej - @i_operacion D >>====================================================')
		def wRequestJavaOrchSpDel = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>							
							<Field name="target" type="S">SPExecutor</Field>
							<Field name="msgType" type="S">ProcedureRequest</Field>
							<Field name="serviceName" type="S">cob_bvirtual..sp_bv_afiliacion_prod_ej</Field>
							<Field name="dbms" type="S">SQLCTS</Field>
							<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>					
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_bvirtual..sp_bv_afiliacion_prod_ej</SpName>
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
			println ('--->>>>>EJECUCION CORRECTA DE SP sp_bv_afiliacion_prod_ej Opcion D')
				
		wXmlResponseDel = wXmlResponseDel.replace("<text>", "text");
				
		def wDocXmlDel = new XmlParser().parseText(wXmlResponseDel)
		
		println (' ')
		println ('====================================================<< Ejecutando de Orquestacion >>====================================================')
		
		def sqlCent = BDD.getInstance(CTSEnvironment.TARGETID_CENTRAL)
		println('---->>>>> INICIO OBTENIENDO DESCRIPCION DE PRODUCTOS')
		def customer = null
		def product = null
		def currency = null
		def account = null
		def numReg = null
		def wLastCustomer = null
		def wLastProduct = null
		def wLastCurrency = null
		def wLastAccount = null
		def rowcount = 50
		numReg = rowcount
		
		Map<String, String> aMapDesProducto = new HashMap<String, String>();
		println('---->>>>> Antes de la ejecucion de la orquestacion')
	
		//while (customer < wLastCustomer && product < wLastProduct && currency < wLastCurrency && account < wLastAccount )
		while (numReg == rowcount)
		{
			def wRequestJavaOrchCentral = ""
			if (product != null){
				wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
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
                                <Param name="@i_servicio"      type="56" io="0" len="1">1</Param>
								<Param name="@i_intento"       type="56" io="0" len="1">1</Param>
								<Param name="@i_fecha_ini"     type="39" io="0" len="13">01/01/2013</Param>
                                <Param name="@i_fecha_fin"     type="39" io="0" len="13">01/01/2015</Param>
								<Param name="@i_fecha_ingreso" type="39" io="0" len="13">01/01/2013</Param>
								<Param name="@i_numRows"       type="56" io="0" len="7">[ROWCOUNT]</Param>
								<Param name="@i_cliente"       type="56" io="0" len="7">[CLIENTE]</Param>
                                <Param name="@i_producto"      type="56" io="0" len="7">[PRODUCTO]</Param>
                                <Param name="@i_moneda"        type="56" io="0" len="7">[MONEDA]</Param>
                                <Param name="@i_cuenta"        type="39" io="0" len="17">[CUENTA]</Param>
								<Param name="@o_cliente"       type="56" io="1" len="7">[CLIENTE]</Param>
                                <Param name="@o_producto"      type="56" io="1" len="7">[PRODUCTO]</Param>
                                <Param name="@o_moneda"        type="56" io="1" len="7">[MONEDA]</Param>
                                <Param name="@o_cuenta"        type="39" io="1" len="17">[CUENTA]</Param>
                                <Param name="@o_secuencial"    type="56" io="1" len="7">00000000</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
			}else{
				wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
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
                                <Param name="@i_servicio"      type="56" io="0" len="1">1</Param>
								<Param name="@i_intento"       type="56" io="0" len="1">1</Param>
								<Param name="@i_fecha_ini"     type="39" io="0" len="13">01/01/2013</Param>
                                <Param name="@i_fecha_fin"     type="39" io="0" len="13">01/01/2015</Param>
								<Param name="@i_fecha_ingreso" type="39" io="0" len="13">01/01/2013</Param>
								<Param name="@i_numRows"       type="56" io="0" len="7">[ROWCOUNT]</Param>								
								<Param name="@o_cliente"       type="56" io="1" len="7">000000</Param>
                                <Param name="@o_producto"      type="56" io="1" len="7">00000</Param>
                                <Param name="@o_moneda"        type="56" io="1" len="7">00000</Param>
                                <Param name="@o_cuenta"        type="39" io="1" len="17">xxxxxxxxxxxxxxxxx</Param>
                                <Param name="@o_secuencial"    type="56" io="1" len="7">00000000</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>			
							'''
			}
			
			//INICIO REPLACE DE VALORES
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SP_NAME]","cob_procesador..sp_bv_gen_dataprod_ej")
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[TRX]","1801023")
		    wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[CLIENTE]",customer.toString())
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[PRODUCTO]",product.toString())
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[MONEDA]",currency.toString())
			wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[CUENTA]",account.toString())
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
				
			/*if (wLastCustomer == null){
				def wAux = sqlCent.rows("select top 1 pp_ente_mis from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_procesar_producto order by  pp_ente_mis,pp_producto, pp_moneda, pp_cuenta  desc")
				def wAux1 = sqlCent.rows("select top 1 pp_producto from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_procesar_producto order by  pp_ente_mis,pp_producto, pp_moneda, pp_cuenta  desc")
				def wAux2 = sqlCent.rows("select top 1 pp_moneda from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_procesar_producto order by  pp_ente_mis,pp_producto, pp_moneda, pp_cuenta  desc")
				def wAux3= sqlCent.rows("select top 1 pp_cuenta from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_procesar_producto order by  pp_ente_mis,pp_producto, pp_moneda, pp_cuenta  desc")
				
				String Num =  wAux.toString()
				String Num1 =  wAux.toString()
				String Num2 =  wAux.toString()
				String Num3 =  wAux.toString()
				wLastCustomer = wAux.toString().substring(3,(Num.size() - 2)).toInteger()
				wLastProduct = wAux1.toString().substring(3,(Num.size() - 2)).toInteger()
				wLastCurrency = wAux2.toString().substring(3,(Num.size() - 2)).toInteger()
				wLastAccount = wAux3.toString().substring(3,(Num.size() - 2)).toInteger()
					
			}*/
					
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(wXmlResponseSP));
			Document doc = db.parse(is);
			 
			
			Element OutputParams = doc.getDocumentElement();
			NodeList param = OutputParams.getElementsByTagName("OutputParams");
			Element result = (Element) param.item(0);
			customer = Integer.valueOf(result.getChildNodes().item(0).getTextContent())
			product = Integer.valueOf(result.getChildNodes().item(1).getTextContent())
			currency = Integer.valueOf(result.getChildNodes().item(2).getTextContent())
			account = String.valueOf(result.getChildNodes().item(3).getTextContent())
			numReg = Integer.valueOf(result.getChildNodes().item(4).getTextContent())
			
			println("customer " + customer)
			println("product " + product)
			println("currency " + currency)
			println("account " + account)
			println("numReg " + numReg)
			
			
			wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");			
		}
		
		println (' ')
		println ('====================================================<< Ejecutando SP sp_bv_afiliacion_prod_ej - @i_operacion I >>===================================================')
		def wRequestJavaOrchSpIns = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>							
							<Field name="target" type="S">SPExecutor</Field>
							<Field name="msgType" type="S">ProcedureRequest</Field>
							<Field name="serviceName" type="S">cob_bvirtual..sp_bv_afiliacion_prod_ej</Field>
							<Field name="dbms" type="S">SQLCTS</Field>
							<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>					
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_bvirtual..sp_bv_afiliacion_prod_ej</SpName>
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
		println ('--->>>>>EJECUCION CORRECTA DE SP sp_bv_afiliacion_prod_ej Opcion I')
			
		wXmlResponseIns = wXmlResponseIns.replace("<text>", "text");
			
		def wDocXmlIns = new XmlParser().parseText(wXmlResponseIns)
	}
}
