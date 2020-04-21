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

class Test_ib_group_sign_out {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession
	
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup();
	}
	
	void Sign_out(String pLogin , String sessionID)
	{
		SerializerFactory serializerFactory = new SerializerFactoryImpl();
		ServiceRequest serviceRequest = new ServiceRequest();
		ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
		serviceRequestTO.setServiceId("finalizeSession");
		serviceRequestTO.setSessionId(sessionID);
		ObjectDTO objectDTO = new ObjectDTO("SessionRequest");
		objectDTO.addData("servicio", "1");
		objectDTO.addData("terminal", CTSEnvironment.bvTerminalIp);
		objectDTO.addData("terminal_ip", CTSEnvironment.bvTerminalIp);
		objectDTO.addData("login", pLogin);
		serviceRequestTO.addValue("SessionRequest", objectDTO);
		Serializer reqSerializer = serializerFactory.getInstance(cobiscorp.ecobis.commons.dto.ServiceRequestTO);
		long t1 = System.currentTimeMillis();
		String xml = reqSerializer.serializeToXml(serviceRequestTO);
		serviceRequest.setServiceRequestTO(serviceRequestTO);
		serviceRequest.setObjectRequest(xml);
		String wsResponse = (new ServiceExecutorWsClient()).execute(CTSEnvironment.urlServiceExecutor, serviceRequest.getServiceRequestAsString());
		int begin = wsResponse.indexOf("<ServiceResponseTO>");
		int end = wsResponse.indexOf("</ServiceResponseTO>");
		String xmlRespTO = wsResponse.substring(begin + 19, end);
		Serializer respSerializer = serializerFactory.getInstance(cobiscorp.ecobis.commons.dto.ServiceResponseTO);
		ServiceResponseTO responseTO = (ServiceResponseTO)respSerializer.deserializeFromXml(xmlRespTO);
		long t2 = System.currentTimeMillis();
		println(pLogin + (new StringBuilder(" Finalize Session time: ")).append(t2 - t1).toString());
		
	}
	
	@Test
	void testGroupSignOut()
	{
		Sign_out(CTSEnvironment.bvGroupLogin, initSession)
	}
	
}
