package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;

/**
 * This interface contains the methods for get server operations.
 * 
 * @author schancay
 * @since Jul 3, 2014
 * @version 1.0.0
 */
public interface ICoreServer {
	/**
	 * 
	 *   
	 *   <b>Obtiene estado del servidor para indicar si estamos en l&iacutenea o en fuera de l&iacutenea.</b>
	 *   
	<b>
	@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ServerRequest serverRequest = new ServerRequest();</li>
		<li>serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ServerResponse serverResponse = new ServerResponse();</li>
		<li>serverResponse.setOnLine(true);</li>
		<li>Date fechaProceso = new Date("07/01/2014");</li>
		<li>serverResponse.setProcessDate(fechaProceso);</li>
		<li>Message message = new Message();</li>
		<li>serverResponse.setMessage(message);</li>
		<li>serverResponse.setSuccess(true);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException : Exception of Core</li>
		<li>CTSInfrastructureException : Exception of Infrastructure Core</li>
	</ul>
	 */
	
	ServerResponse getServerStatus(ServerRequest serverRequest) throws CTSServiceException, CTSInfrastructureException;
}
