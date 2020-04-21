package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshForeignExchangeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshForexRequest;
/** 
 * Esta interfaz contiene metodos necesarios para ejecutar el batch de Compra y Venta de divisas.
 * 
 * */
public interface ICoreServiceBatchRefreshForeignExchange {
	/**
	 * 
	 *   
	 *   <b>Consulta tabla cob_tesoreria..te_posicion_divisa en Central.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>BatchRefreshRequest batchRefreshRequest = new BatchRefreshRequest();</li>
		<li></li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>BatchRefreshForexPositionResponse batchRefreshForexPositionResponse = new BatchRefreshForexPositionResponse();</li>
		
		<li>List<BatchRefreshForexPosition> batchRefreshForexPositionCollection = new ArrayList<BatchRefreshForexPosition>(); </li>
		<li>BatchRefreshForexPosition batchRefreshForexPosition = new BatchRefreshForexPosition();</li>
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
	public BatchRefreshForeignExchangeResponse getForexPosition(BatchRefreshForexRequest wBatchRefreshRequestForex)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Consulta tabla cob_tesoreria..te_relacion_dolar en Central.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>BatchRefreshRequest batchRefreshRequest = new BatchRefreshRequest();</li>
		<li></li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>BatchRefreshForexPositionResponse batchRefreshForexPositionResponse = new BatchRefreshForexPositionResponse();</li>
		
		<li>List<BatchRefreshRelationshipWithDollar> batchRefreshRelationshipWithDollarCollection = new ArrayList<BatchRefreshRelationshipWithDollar>();</li>
		<li>BatchRefreshRelationshipWithDollar batchRefreshRelationshipWithDollar = new BatchRefreshRelationshipWithDollar();</li>
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
	public BatchRefreshForeignExchangeResponse getRelationshipWithDollar(BatchRefreshForexRequest wBatchRefreshRequestForex)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Consulta tabla cob_tesoreria..te_tasas_divisas en Central.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>BatchRefreshRequest batchRefreshRequest = new BatchRefreshRequest();</li>
		<li></li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>BatchRefreshForexPositionResponse batchRefreshForexPositionResponse = new BatchRefreshForexPositionResponse();</li>
		
		<li>List<BatchRefreshForexRates> batchRefreshForexRatesCollection = new ArrayList<BatchRefreshForexRates>();</li>
		<li>BatchRefreshForexRates batchRefreshForexRates = new BatchRefreshForexRates();</li>
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
	public BatchRefreshForeignExchangeResponse getForexRates(BatchRefreshForexRequest wBatchRefreshRequestForex)throws CTSServiceException, CTSInfrastructureException;
}