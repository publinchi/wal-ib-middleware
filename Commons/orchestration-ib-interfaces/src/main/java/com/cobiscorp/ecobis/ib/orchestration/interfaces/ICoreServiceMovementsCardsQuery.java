package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;

/**
 * This interface contains the methods needed to get information of credit cards.
 * 
 * @author eortega
 * @since Jul 22, 2014
 * @version 1.0.0
 */
public interface ICoreServiceMovementsCardsQuery {
	/**
	 * 
	 * 
	 * Get movements of credit card.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Service Not Implemented</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Service Not Implemented</li>
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
	IProcedureResponse getMovementsCreditCards(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * 
	 * Get movements of credit card.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Service Not Implemented</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Service Not Implemented</li>
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
	IProcedureResponse getDetailMovementCreditCards(IProcedureRequest procedureRequest) throws CTSServiceException, CTSInfrastructureException;
}
