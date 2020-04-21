
package com.cobiscorp.channels.bv.orchestration7x24.test


import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ObjectDTO;
import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;

import com.cobiscorp.cobis.commons.serializer.Serializer;
import com.cobiscorp.cobis.commons.serializer.SerializerFactory;
import com.cobiscorp.cobis.cts.dtos.ServiceRequest;
import com.cobiscorp.cobis.serializer.impl.SerializerFactoryImpl;
import com.cobiscorp.cts.annotation.test.dto.DtoIn;
import com.cobiscorp.test.CTSEnvironment;
import com.cobiscorp.test.serviceexecutor.ServiceExecutorWsClient;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase;
import com.cobiscorp.cobis.cts.dtos.LoginResponse;


/**
 *
 *
 * @since 31/Julio/2014
 * @author Carlos Echeverría
 * @version 1.0.0
 *
 *
 */
class Test_ib_company_last_access {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	
	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession)
	}

	
	
	void LastAccess(String pLogin , String pPassword)
	{
		SerializerFactory serializerFactory = new SerializerFactoryImpl();
		ServiceRequest serviceRequest = new ServiceRequest();
		ServiceRequestTO requestTO = new ServiceRequestTO();
		requestTO.setServiceId("authenticate");
		ObjectDTO objectDTO = new ObjectDTO("LoginRequest");
		objectDTO.addData("clave", pPassword);
		objectDTO.addData("culture", CTSEnvironment.bvCulture);
		objectDTO.addData("idAplicacion", "bancaVirtual");
		objectDTO.addData("login", pLogin);
		objectDTO.addData("server", CTSEnvironment.bvServer);
		objectDTO.addData("servicio", 1);
		objectDTO.addData("terminalIP", CTSEnvironment.bvTerminalIp);
		objectDTO.addData("webserver", CTSEnvironment.bvWebServer);
		requestTO.addValue("LoginRequest", objectDTO);
		Serializer reqSerializer =   serializerFactory.getInstance(cobiscorp.ecobis.commons.dto.ServiceRequestTO);
		long t1 = System.currentTimeMillis();
		String xml = reqSerializer.serializeToXml(requestTO);
		serviceRequest.setServiceRequestTO(requestTO);
		serviceRequest.setObjectRequest(xml);
		System.out.println(xml);
		System.out.println(serviceRequest.getServiceRequestAsString());
		String wsResponse = (new ServiceExecutorWsClient()).execute(CTSEnvironment.urlServiceExecutor, serviceRequest.getServiceRequestAsString());
		int begin = wsResponse.indexOf("<ServiceResponseTO>");
		int end = wsResponse.indexOf("</ServiceResponseTO>");
		String xmlRespTO = wsResponse.substring(begin + 19, end);
		println xmlRespTO
		Serializer respSerializer = serializerFactory.getInstance(cobiscorp.ecobis.commons.dto.ServiceResponseTO);
		ServiceResponseTO responseTO = (ServiceResponseTO)respSerializer.deserializeFromXml(xmlRespTO);
		String initSession = ((ObjectDTO)responseTO.getData().get("LoginResponse")).getString("sessionID").toString();
		
		String fechaUltAcceso = ((ObjectDTO)responseTO.getData().get("LoginResponse")).getString("fecha_ult_acceso").toString();
		String terminalIp = ((ObjectDTO)responseTO.getData().get("LoginResponse")).getString("terminal_ip").toString();

		if (initSession != null)
		{
			println "!!!!!**************** Acceso Exitoso al Sistema *********!!!!!"
			
			if (fechaUltAcceso!= null)
			println "Usuario : "+pLogin+", fecha de último acceso : "+fechaUltAcceso+" desde la terminal "+terminalIp
			else
			println "Usuario : "+pLogin+",  ingresa por primera vez"
		}
		else
			println "!!!!***************** Error al ingresar al Sistema ********!!!!"

		
	}
	
	
	@Test
	void TestGetAccess()
	{
		LastAccess(CTSEnvironment.bvLoginEmpresa, CTSEnvironment.bvCompanyPassword)
		
	}
	
	
				
}
