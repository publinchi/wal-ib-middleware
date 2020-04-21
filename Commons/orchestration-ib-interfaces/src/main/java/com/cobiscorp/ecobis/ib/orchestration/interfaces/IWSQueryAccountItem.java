package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;
import java.util.Properties;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsResponse;


/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion desde el WS de Sintesis (items por cliente),
 * informacion para armar la pantalla para el pago de servicios.
 * 
 * */
public interface IWSQueryAccountItem {
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta  de Items Cliente</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String idOperativo  = ""</li>
		<li>idOperativo = aRequest.readValueParam("@i_id_opertivo");</li>
		<li>Module aModule = new Module();</li>
		<li>aModule.setcodModule(aRequest.readValueParam("@i_codModule"));</li>
		<li>Integer operation  = 0</li>
		<li>operation = Integer.parseInt(aRequest.readValueParam("@i_operacion"));</li>
		<li>Integer date  = 0</li>
		<li>date = Integer.parseInt(aRequest.readValueParam("@i_fecha_operativa"));</li>
		<li>String account  = ""</li>
		<li>account = aRequest.readValueParam("@i_cuenta");</li>
		<li>Integer service  = 0</li>
		<li>service = aRequest.readValueParam("@i_service");</li>
		
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>QueryAccountItem aQueryAccountItem = new QueryAccountItem();</li>
		<li>List<QueryAccountItem> aItemCollection = new ArrayList<QueryAccountItem>(); </li>
		<li>QueryAccountItemResponse aQueryAccountItemResponse = new QueryAccountItemResponseModuleResponse();</li>
		<li>aQueryAccountItem.setdependency(0);</li>
		<li>aQueryAccountItem.setDescription("Periodo 2015-0001");</li>
		<li>aQueryAccountItem.setPaymentMethod("NSU");</li>
		<li>aQueryAccountItem.setcurrency("Bs");</li>
		<li>aQueryAccountItem.setamount(new BigDecimal("27.0"));</li>
		<li>aQueryAccountItem.setitemPending(1);</li>
	
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
	public QueryAccountItemsResponse getAccountItems(QueryAccountItemsRequest aQueryAccountItemRequest,Map<String, Object> aBagSPJavaOrchestration, Properties properties) throws CTSServiceException, CTSInfrastructureException;
	
}
