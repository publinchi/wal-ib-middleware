package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;
import java.util.Properties;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaResponse;
 

/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion desde el WS de Sintesis,
 * informacion para armar la pantalla para el pago de servicios.
 * 
 * */
public interface IWSCriteriaModules {
	/**
	 * 
	 *   
	 *   <b>Consulta Criterios del Modulo</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Office oficina = new Office();</li>
		<li>oficina.setId(Integer.parseInt(aRequest.readValueParam("@i_oficina")));</li>
		<li>Region region  = new Region();</li>
	    <li>region.setId(aRequest.readValueParam("@i_region"));</li>
		<li>String operation  = ""</li>
		<li>operation = aRequest.readValueParam("@i_operacion");</li>
		<li>int sequential  = ""</li>
		<li>sequential = aRequest.readValueParam("@i_secuencial");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>Stock aStock = new Stock();</li>
		<li>List<Stock> aStockCollection = new ArrayList<Stock>(); </li>
		<li>StockResponse aStockResponse = new StockResponse();</li>
		<li>aStock.setsequential(1);</li>
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
	public ModuleCriteriaResponse getModuleCriteria(ModuleCriteriaRequest aModuleCriteriaRequest,Map<String, Object> aBagSPJavaOrchestration, Properties properties) throws CTSServiceException, CTSInfrastructureException;
 
}
