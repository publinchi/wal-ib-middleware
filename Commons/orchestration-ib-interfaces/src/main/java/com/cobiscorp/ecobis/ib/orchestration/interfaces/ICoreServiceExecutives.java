package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesResponse;

/**
 * This interface contains the methods needed to Get/Update customer information.
 * 
 * @since Nov 20, 2014
 * @author GCondo
 * @version 1.0.0
 *
 */ 

public interface ICoreServiceExecutives {
	/**
	 * 
	 *   
	 *   <b>Consulta Oficiales de Banca Virtual.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ExecutivesRequest wExecutivesRequest = new ExecutivesRequest();</li>
		<li>Client wClient = new Client();</li>
		<li>wClient.setId(aRequest.readValueParam("@i_ente"));</li>
		<li>wExecutivesRequest.setClient(wClient);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>ExecutivesResponse wExecutivesResponse = new ExecutivesResponse();</li>
		<li>List<Executives> executivesCollection = new ArrayList<Executives>();</li>
		
		<li>Executives wExecutives = new Executives();</li>
		<li>wExecutives.setName("Nombre Oficial TEST");</li>
		<li>wExecutives.setEmail("oficial@ejemplo.com");</li>
		
		<li>Executives wExecutives2 = new Executives();</li>
		<li>wExecutives2.setName("Nombre Oficial TEST_2");</li>
		<li>wExecutives2.setEmail("oficial_2@ejemplo.com");</li>
		
		<li>executivesCollection.add(wExecutives);</li>
		<li>executivesCollection.add(wExecutives2);</li>
		
		<li>wExecutivesResponse.setReturnCode(0);</li>
		<li>wExecutivesResponse.setExecutivesCollection(executivesCollection);</li>
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
	ExecutivesResponse GetExecutives(ExecutivesRequest wExecutivesRequest)throws CTSServiceException, CTSInfrastructureException;

}
