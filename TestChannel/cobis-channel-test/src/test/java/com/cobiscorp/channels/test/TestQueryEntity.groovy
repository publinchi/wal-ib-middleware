/**
 *
 */
package com.cobiscorp.channels.test

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
 * @author mvelez
 *
 */
class TestQueryEntity {
	/**
	 * setupenvironment in order to set configuration and close database connections
	 */
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();


	//@Test
	void testExecuteJavaOrch(String Person, String Tipo) {
		def wRequestJavaOrch = null;
		if (Person.equals("P")) {
		   if (Tipo.equals("5")) {
		   
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">P</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">5</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''			   
		   } // IF Tipo.equals("5")
		   if (Tipo.equals("1")) {
				wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">P</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">1</Param>
							<Param name="@i_ente"       type="39" io="0" len="50">0</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''   
		   } // IF Tipo.equals("1")
		   if (Tipo.equals("4")) {
			  
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">P</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">4</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
			}// IF Tipo.equals("4")
		   if (Tipo.equals("8")) {
				  wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">P</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">8</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   }// IF Tipo.equals("8")
		   if (Tipo.equals("9")) {
				  wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">P</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">9</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   }// IF Tipo.equals("9")
		   if (Tipo.equals("QR")) {
				wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_qrente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875060</Param>
							<Param name="@i_ente"        type="56" io="0" len="7">13036</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
			}// IF Tipo.equals("QR")
		   if (Tipo.equals("QEP")) {
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_query_ente_int</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875061</Param>
				            <Param name="@i_ente"       type="52" io="0" len="7">13036</Param>
				            <Param name="@i_formato"    type="52" io="0" len="3">101</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
			}
		}
		/**************************************************************/
		if (Person.equals("C")) {
		   if (Tipo.equals("5")) {
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">C</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">5</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   } // IF Tipo.equals("1")
		   if (Tipo.equals("1")) {
			   wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">C</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">1</Param>
			   				<Param name="@i_ente"       type="39" io="0" len="50">0</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   } // IF Tipo.equals("1")
		   if (Tipo.equals("4")) {
				wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">C</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">4</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
			}// IF Tipo.equals("4")
		   if (Tipo.equals("2")) {
				  wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">C</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">2</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   }// IF Tipo.equals("2")
		   if (Tipo.equals("9")) {
				  wRequestJavaOrch = '''<?xml version="1.0" encoding="ISO-8859-1" ?>
				<CTSMessage>
					<CTSHeader>
					</CTSHeader>
					<Data>
						<ProcedureRequest>
							<SpName>cob_procesador..sp_se_ente</SpName>
							<Param name="@t_trn"        type="56" io="0" len="7">1875059</Param>
							<Param name="@i_subtipo"    type="39" io="0" len="1">C</Param>			
							<Param name="@i_tipo"       type="52" io="0" len="1">9</Param>
							<Param name="@i_nombre"     type="39" io="0" len="50">%</Param>
							<Param name="@i_s_nombre"   type="39" io="0" len="50">%</Param>
							<Param name="@i_p_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_s_apellido" type="39" io="0" len="50">%</Param>
				            <Param name="@i_modo"       type="52" io="0" len="2">0</Param>
						</ProcedureRequest>
					</Data>
				</CTSMessage>			
						'''
		   }// IF Tipo.equals("9")
		}
		
		DefaultHttpClient httpclient = new DefaultHttpClient()
		HttpPost httpPost = new HttpPost("http://" + CTSEnvironment.CTS_WEB_IP + ":" + CTSEnvironment.CTS_WEB_PORT + "/CTSWeb/WebRequestHandler?filial=1");
		println "aaaa"
		//Map wAuthResp =  NCSAuth.authenticate(CTSEnvironment.login, CTSEnvironment.password, httpclient, httpPost, true, 20)
		Map wAuthResp =  NCSAuth.authenticate("admuser", "12345678", httpclient, httpPost, true, 20)
		println "bbbb"
		assert wAuthResp.get("response")
		println "***** GetNaturalCustomerByAlphabetical *****"
		def wXml = new XmlParser().parseText(wRequestJavaOrch)
		def wXmlSerialized = groovy.xml.XmlUtil.serialize( wXml )
		def wXmlResponse  = CTSTestServletClient.execute(httpclient, httpPost, wXmlSerialized)
		println "RESPONSE: "+ wXmlResponse
		Assert.assertTrue(wXmlResponse.indexOf("<return>0</return>") > -1 )
		wXmlResponse = wXmlResponse.replace("<text>", "text");
		def wDocXml = new XmlParser().parseText(wXmlResponse);
	}
	
	@Test
	void testEntity() {
		/**********ENTE PERSONA  (sp_se_ente)**********/
	   println "***** GetNaturalCustomerByAlphabetical *****"
	   testExecuteJavaOrch("P","5"); //GetNaturalCustomerByAlphabetical
	   println "***** GetNaturalCustomerByCode *****"
	   testExecuteJavaOrch("P","1"); //GetNaturalCustomerByCode
	   println "***** GetNaturalCustomerByMarriedName *****"
	   testExecuteJavaOrch("P","4"); //GetNaturalCustomerByMarriedName
	   println "***** GetNaturalCustomerById *****"
	   testExecuteJavaOrch("P","8"); //GetNaturalCustomerById
	   println "***** GetNaturalCustomerByStatus *****"
	   testExecuteJavaOrch("P","9"); //GetNaturalCustomerByStatus
	   
	   /**********ENTE COMPANY (sp_se_ente)**********/
	   println "***** GetCompanyByCommercialName *****"
	   testExecuteJavaOrch("C","5"); //GetCompanyByCommercialName
	   println "***** GetCompanyByCode *****"
	   testExecuteJavaOrch("C","1"); //GetCompanyByCode
	   println "***** GetCompanyByCorporateName *****"
	   testExecuteJavaOrch("C","4"); //GetCompanyByCorporateName
	   println "***** GetCompanyById *****"
	   testExecuteJavaOrch("C","2"); //GetCompanyById
	   println "***** GetCompanyByStatus *****"
	   testExecuteJavaOrch("C","9"); //GetCompanyByStatus
		

		/**********QRENTE PERSONA (sp_qrente)**********/
		println "***** GetSomeInfoByCode *****"
		testExecuteJavaOrch("P","QR"); //GetSomeInfoByCode

		/**********QUERY ENTE PERSONA (sp_query_ente_int)**********/
		println "***** GetAllInfoByCode *****"
		testExecuteJavaOrch("P","QEP"); //GetSomeInfoByCode
	}
}
