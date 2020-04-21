package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.StockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.StockResponse;

/** 
 * Esta interfaz contiene metodos necesarios para obtener informaci&oacuten de stock para Factor Autententicaci&oacuten.
 * 
 * */
public interface ICoreServiceStock {
	/**
	 * 
	 *   
	 *   <b>Consulta Stock</b>
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
		<li>aStock.setcod_region("1");</li>
		<li>aStock.setregion("Region 1");</li>
		<li>aStock.setcod_office(1);</li>
		<li>aStock.setoffice("Matriz");</li>
		<li>aStock.setno_assigned(2);</li>
		<li>aStock.setassigned(26);</li>
	
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
	public StockResponse getStock(StockRequest aStockRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Consulta Stock por Fecha</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>string date = ""</li>
		<li>string = aRequest.readValueParam("@i_fecha");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>Stock aStock = new Stock();</li>
		<li>List<Stock> aStockCollection = new ArrayList<Stock>(); </li>
		<li>StockResponse aStockResponse = new StockResponse();</li>
		<li>aStock.setcod_region("1");</li>
		<li>aStock.setregion("Region 1");</li>
		<li>aStock.setcod_office(1);</li>
		<li>aStock.setoffice("Matriz");</li>
		<li>aStock.setStock(5);</li>
		<li>aStock.setStock_mes(2);</li>
		<li>aStock.setStock_mes(5);</li>		
	
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
	public StockResponse getStockbyDate(StockRequest aStockRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
}