package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.SentInternationalTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SentInternationalTransferResponse;


/**
 * @author mvelez
 *
 */
public interface ICoreServiceSentInternationalTransfer {
	
	/**
	 * 
	 *  
	 *  <b>Servicio no implementado</b>
	 *  <b>Consulta transferencias internacionales enviadas</b>
	 *  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Servicio no implementado</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Servicio no implementado</li>
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
	public SentInternationalTransferResponse GetSentInternationalTransfer (SentInternationalTransferRequest aSentInternationalTransferRequest) throws CTSServiceException, CTSInfrastructureException;
}
