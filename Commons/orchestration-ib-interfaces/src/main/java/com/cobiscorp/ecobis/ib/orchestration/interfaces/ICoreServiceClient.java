/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;

import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationResponse;

/**
 * @author schancay
 * @since Sep 4, 2014
 * @version 1.0.0
 */
public interface ICoreServiceClient {

	/** 
	 * 
	 * Get information of client for send transactions core.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ClientInformationRequest clientRequest = new ClientInformationRequest();</li>
		<li>Client clientFind = new Client();</li>
		<li>clientFind.setId(anOriginalRequest.readValueParam("@s_cliente"));</li>
		<li>clientFind.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		<li>clientRequest.setClient(clientFind);</li>
		<li>clientRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ClientInformationResponse clientInformationResponse = new ClientInformationResponse();</li>
		<li>Client client = new Client();</li>
		<li>clientInformationResponse.setClient(client);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	 */
	ClientInformationResponse getInformationClientBv(ClientInformationRequest client, Map<String, Object> aBagSPJavaOrchestration);
}
