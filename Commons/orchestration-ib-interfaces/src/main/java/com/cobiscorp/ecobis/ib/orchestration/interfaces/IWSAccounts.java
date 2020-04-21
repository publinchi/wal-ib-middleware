package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;
import java.util.Properties;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountResponse;


/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion desde el WS de Sintesis,
 * informacion para armar la pantalla para el pago de servicios.
 * 
 * */
public interface IWSAccounts {
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta  del Modulo</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String idOperation  = ""</li>
		<li>idOperation = aRequest.readValueParam("@i_operation");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>Module aModule = new Module();</li>
		<li>List<Module> aModuleCollection = new ArrayList<Module>(); </li>
		<li>ModuleResponse aModuleResponse = new ModuleResponse();</li>
		<li>aModule.setcodeModule(1);</li>
		<li>aModule.setDescription(Modulo");</li>
		<li>aModule.setType("P");</li>

	
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>		
	**/
	public AccountResponse getAccountByCriteria(AccountRequest aAccountRequest,Map<String, Object> aBagSPJavaOrchestration, Properties properties) throws CTSServiceException, CTSInfrastructureException;
	
}
