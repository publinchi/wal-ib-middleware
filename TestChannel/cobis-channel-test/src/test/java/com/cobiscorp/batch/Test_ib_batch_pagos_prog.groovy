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
import com.sun.jna.platform.win32.Wdm;

import javax.xml.parsers.*;

import java.io.IOException;

import org.xml.sax.InputSource
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.http.client.methods.HttpPost

class Test_ib_batch_pagos_prog {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	
	
	@Test
	void testBatchScheduledPayments() {
		
		def wFinalRequest = ''
		def tipoPago = ''
		def batch = '18427'
		def siguiente = 0
		def rowcount = 0
		def cont = 0
			
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def wParametroRegistroConsulta = sql.rows("select isnull(pa_tinyint,20) from cobis"+CTSEnvironment.DB_SEPARATOR+"cl_parametro where pa_producto = 'BVI' and pa_nemonico = 'PAGBAT' ")	
		def num = wParametroRegistroConsulta.toString()		
		rowcount = wParametroRegistroConsulta.toString().substring(3,(num.size() - 2)).toInteger()	
		println ('Parametro rowcount ==> ' + rowcount)
		
		
		/*
		println ('=============PASO 1==========================')		
		println ('=============Ejecutando SP que Cargar tabla bv_procesar_pagos==========================')
		def wRequestJavaOrchSpGenPag = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
					<CTSMessage>
						<CTSHeader>							
							<Field name="target" type="S">SPExecutor</Field>
							<Field name="msgType" type="S">ProcedureRequest</Field>
							<Field name="serviceName" type="S">cob_bvirtual..sp_bv_gen_pago_prg_ej</Field>
							<Field name="dbms" type="S">SQLCTS</Field>
							<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>					
						</CTSHeader>
						<Data>
							<ProcedureRequest>
								<SpName>cob_bvirtual..sp_bv_gen_pago_prg_ej</SpName>
								<Param name="@t_trn" type="56" io="0" len="7">1890005</Param>
								<Param name="@i_fecha_proceso" type="61" io="0" len="10">02/01/2014</Param>
								<Param name="@i_sarta" type="56" io="0" len="7">18011</Param>
								<Param name="@i_batch" type="56" io="0" len="7">[BATCH]</Param>
								<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								<Param name="@i_corrida" type="56" io="0" len="1">1</Param>
								<Param name="@i_intento" type="56" io="0" len="1">1</Param>
							</ProcedureRequest>
						</Data>
					</CTSMessage>
							'''
		wRequestJavaOrchSpGenPag = wRequestJavaOrchSpGenPag.replace("[BATCH]",batch)
							
		println 'Request---->>>> ' + wRequestJavaOrchSpGenPag
		DefaultHttpClient httpclientSp = new DefaultHttpClient()
		HttpPost httpPostSp = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		
		Map wAuthRespGenPag =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSp, httpPostSp, true, 20)
		assert wAuthRespGenPag.get("response")
		
		def wXmlGenPag = new XmlParser().parseText(wRequestJavaOrchSpGenPag)
		
		def wXmlSerializedGenPag = groovy.xml.XmlUtil.serialize( wXmlGenPag )
		def wXmlResponseGenPag  = CTSTestServletClient.execute(httpclientSp, httpPostSp, wXmlSerializedGenPag)
		println "RESPONSE Genenerar Pagos nvite===>>>: "+ wXmlResponseGenPag

		if(Assert.assertTrue(wXmlResponseGenPag.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP sp_bv_gen_pago_prg_ej---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP sp_bv_gen_pago_prg_ej ')
		
		wXmlResponseGenPag = wXmlResponseGenPag.replace("<text>", "text");
		def wDocXmlGenPag = new XmlParser().parseText(wXmlResponseGenPag)

		println('****Termiana el proceso 18427*****')
		*/
			
											
	    println ('=============PASO 2==========================')
		
		//Node dataList = result.getChildNodes().item(1);
		def mymap = ["18429":"T", "18430":"P", "18456":"S"]
		
		//def mymap = ["18430":"P"]
		//def x = mymap.find{ it.key == "1807" }.value
		
		for ( i in mymap ) {
			cont = 0		
		println ("HENRY :" +i.key+" : "+i.value);
		batch = i.key
		tipoPago = i.value
		  
		def wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
		<CTSMessage>
			<CTSHeader>				
				<Field name="target" type="S">SPExecutor</Field>
				<Field name="msgType" type="S">ProcedureRequest</Field>
				<Field name="serviceName" type="S">cob_bvirtual..sp_batch_pagos_recur_bv</Field>
				<Field name="dbms" type="S">SQLCTS</Field>
				<Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field>
			</CTSHeader>
			<Data>
				<ProcedureRequest>
					<SpName>cob_bvirtual..sp_batch_pagos_recur_bv</SpName>
					<Param name="@t_trn" type="56" io="0" len="7">1890001</Param>
					<Param name="@i_operacion" type="39" io="0" len="4">Q</Param>
					<Param name="@i_siguiente" type="56" io="0" len="7">0</Param>					
					<Param name="@i_tipo_pago" type="39" io="0" len="4">[TIPO_PAGO]</Param>					
				</ProcedureRequest>
			</Data>
		</CTSMessage>
		'''
		
		wRequestJavaOrch= wRequestJavaOrch.replace("[TIPO_PAGO]",tipoPago)

		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");		
		
		Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)		
		assert wAuthResp.get("response")

		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		
		//def wXmlResponse = '''<?xml version="1.0" encoding="ISO-8859-1" ?><CTSMessage><CTSHeader><Field name="MULTIPLEXER_ORCHESTRATOR_FILTER" type="S">(service.callingSource=0)</Field><Field name="t_rol" type="N">20</Field><Field name="user" type="S">admuser</Field><Field name="lsrv" type="S">CTSSRV</Field><Field name="ssn" type="N">661242385</Field><Field name="rol" type="N">20</Field><Field name="target" type="S">SPExecutor</Field><Field name="sessionId" type="S">ID:414d5120514d2e42524f4b45523035206165f8542004d512</Field><Field name="ReplyToQueueClient" type="S">WRH_RESP_MF</Field><Field name="msgType" type="S">ProcedureRequest</Field><Field name="SPExecutorServiceFactoryFilter" type="S">(service.impl=object)</Field><Field name="ncsSchema" type="S">S</Field><Field name="serviceName" type="S">cob_bvirtual..sp_batch_pagos_recur_bv</Field><Field name="MULTIPLEXER_QUEUE_NAME" type="S">WRH_RESP_MF</Field><Field name="dbms" type="S">SQLCTS</Field><Field name="org" type="S">U</Field><Field name="localSource" type="C">S</Field><Field name="contextId" type="S">COBIS</Field><Field name="term" type="S">PC01TEC42</Field><Field name="sesn" type="N">26383</Field><Field name="date" type="D">01/02/2014</Field><Field name="backEndId" type="S">backend2</Field><Field name="trn" type="N">1890001</Field><Field name="ofi" type="N">1</Field><Field name="spType" type="S">Sybase</Field><Field name="srv" type="S">CTSSRV</Field></CTSHeader><Data><ProcedureResponse><ResultSet><Header><col name="pp_pago_id" type="56" len="11"/><col name="pp_tipo" type="47" len="1"/><col name="pp_estado" type="47" len="1"/><col name="pp_mensaje_error" type="39" len="100"/></Header><rw><cd>31</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>32</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>33</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>34</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>35</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>42</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>48</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>62</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>69</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>72</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>73</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>84</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>86</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>87</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>88</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>101</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2123</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2124</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2125</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2126</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2127</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2128</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2129</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2130</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2131</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2132</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2133</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2134</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2135</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2136</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2137</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2138</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2139</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2140</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2141</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2142</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2143</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2144</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2145</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2146</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2147</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2148</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2149</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2150</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2151</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2152</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2153</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2154</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2155</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2156</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2157</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2158</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2159</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2160</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2161</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2162</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2163</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2164</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2165</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2166</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2167</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2168</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2169</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2170</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2171</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2172</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2173</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2174</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2175</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2176</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2177</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2178</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2179</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2180</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2181</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2182</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2183</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2184</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2185</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2186</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2187</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2188</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2189</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2190</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2191</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2192</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2193</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2194</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2195</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2196</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2197</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2198</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2199</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw><rw><cd>2200</cd><cd>T</cd><cd>N</cd><cd>PENDIENTE DE PROCESAR</cd></rw></ResultSet><OutputParams></OutputParams><return>0</return></ProcedureResponse></Data></CTSMessage>'''		
		
		println "RESPONSE PRIMER PASO===>>>: "+ wXmlResponse

		/*if(Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 ))
			println ('--->>>>>ERROR AL EJECUTAR SP CONSOLIDADO---->>>>>')
		else
			println ('--->>>>>EJECUCION CORRECTA DE SP CONSOLIDADO')
		*/
		
		wXmlResponse = wXmlResponse.replace("<text>", "text");		
		def wDocXml = new XmlParser().parseText(wXmlResponse)
		
		/**Proceso para mapear data**/
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(wXmlResponse));
		Document doc = db.parse(is);
  
		Element OutputParams = doc.getDocumentElement();
		NodeList param = OutputParams.getElementsByTagName("ResultSet");
		Element result = (Element) param.item(0);		
		//siguiente = Integer.valueOf(result.getTextContent())
		//wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
		result.removeChild(result.getChildNodes().item(0));//eliminar la cabecera
		NodeList dataList = result.getChildNodes();		
		//Node dataList = result.getChildNodes().item(1);
		
		println ('--->>>>>ATRIBUTOS'+dataList.getLength());
		
		//println ('--->>>>>ATRIBUTOS'+wDocXml.children().get());
		//siguiente = Integer.valueOf(result.getTextContent())
		//wXmlResponseSP = wXmlResponseSP.replace("<text>", "text");
		/*****fin proceso *****/
		
		
		println ('===============<< Ejecutando de Orquestacion >>========================')
		//def sqlCent = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)		
		
		def totalRecords = dataList.getLength()
		def wLastRecord = rowcount
		
		Map<String, String> aMapDesProducto = new HashMap<String, String>();
		println('---->>>>> Antes de la ejecucion de la orquestacion')		
		println ('total de registros ==> ' + totalRecords)
		
		Date fechaEjec = null			
		
		//while(siguiente <= totalRecords)
		if(totalRecords>0){
		
			while(cont <= totalRecords)
			{
				fechaEjec = new Date()
				println ('Fecha ==> '+ fechaEjec)
				println ('Siguiente ==> ' + siguiente)
				println ('Tipo de pago ==> ' + tipoPago)
				println ('Batch ==> ' + batch)
							
				siguiente = dataList.item(cont).getChildNodes().item(0).getFirstChild().getNodeValue()//getNodeValue()//.nodeValue;						
				println ('SIGUIENTE ID ==> '+ siguiente)
				
				
				def wRequestJavaOrchCentral = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
						<CTSMessage>
							<CTSHeader>
							</CTSHeader>
							<Data>
								<ProcedureRequest>
									<SpName>cob_procesador..sp_batch_pagos_recur_bv</SpName>
									<Param name="@t_trn" type="56" io="0" len="7">1890002</Param>								
									<Param name="@i_operacion" type="39" io="0" len="4">S</Param>
									<Param name="@i_siguiente" type="56" io="0" len="7">[SIGUIENTE]</Param>					
									<Param name="@i_tipo_pago" type="39" io="0" len="4">[TIPO_PAGO]</Param>
									<Param name="@i_sarta" type="56" io="0" len="7">18011</Param>
									<Param name="@i_batch" type="56" io="0" len="7">[BATCH]</Param>
									<Param name="@i_secuencial" type="56" io="0" len="1">1</Param>
								</ProcedureRequest>
							</Data>
						</CTSMessage>'''	
				
				//INICIO REPLACE DE VALORES
			    //wRequestJavaOrchCentral = wRequestJavaOrchCentral.replace("[SP_NAME]","cob_bvirtual..sp_consulta_pagos_prog_bv")			
			    //wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[ENTE]",siguiente.toString())
			    wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[SIGUIENTE]",siguiente.toString())
				wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[BATCH]",batch)
				wRequestJavaOrchCentral= wRequestJavaOrchCentral.replace("[TIPO_PAGO]",tipoPago)						
				println 'Request hsalazar---->>>> ' + wRequestJavaOrchCentral			
				//FIN REPLACE DE VALORES
				
				DefaultHttpClient httpclientSP = new DefaultHttpClient()
				HttpPost httpPostSP = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
												
				Map wAuthRespSP =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclientSP, httpPostSP, true, 20)
				assert wAuthRespSP.get("response")
				
				def wXmlSP = new XmlParser().parseText(wRequestJavaOrchCentral)
				def wXmlSerializedSP = groovy.xml.XmlUtil.serialize( wXmlSP )
				
				println "Antes de enviar===>>>: "+ wXmlSerializedSP
				
				
				def wXmlResponseSP  = CTSTestServletClient.execute(httpclientSP, httpPostSP, wXmlSerializedSP)
				println "RESPONSE===>>>: "+ wXmlResponseSP
				def wDocXmlSP = new XmlParser().parseText(wXmlResponseSP)		
				
				/*if(Assert.assertTrue(wXmlResponseSP.indexOf("<return>0</return>") > -1 ))
					println ('--->>>>>ERROR AL EJECUTAR ORQUESTACION---->>>>>')
				else
					println ('--->>>>>EJECUCION CORRECTA DE LA ORQUESTACION')
				*/
					
				//siguiente+=rowcount
				cont += rowcount								
				
			}	//fin while	
		}//fin if	
	  } //fin for				
	}	 
}