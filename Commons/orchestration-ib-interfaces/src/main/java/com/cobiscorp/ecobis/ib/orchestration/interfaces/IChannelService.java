package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;

public interface IChannelService {
	/**
	 * 
	 * 
	 * <b>M&eacute;todo de orquestaci&oacute;n gen&eacute;rica de Administrador de Canales</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IProcedureRequest</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IProcedureResponse</li>
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
	IProcedureResponse executeService(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException;

}
