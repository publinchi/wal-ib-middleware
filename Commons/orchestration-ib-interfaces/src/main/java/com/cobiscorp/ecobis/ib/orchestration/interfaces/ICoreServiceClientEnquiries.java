package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;


/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion sobre las solicitudes realizadas por el cliente. 
 * 
 * */
public interface ICoreServiceClientEnquiries {
	/**
	 * 
	 *   
	 *   <b>Consulta de Solicitudes realizadas por el cliente.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>Entity entity = new Entity();</li>
		<li>entity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_cliente")));</li>
		<li>String origen  = ""</li>
		<li>origen = aRequest.readValueParam("@i_origen");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>CreditLine aCreditLine = new CreditLine();</li>
		<li>List<CreditLine> aCreditLineCollection = new ArrayList<CreditLine>(); </li>
		<li>CreditLineResponse aCreditLineResponse = new CreditLineResponse();</li>
		<li>aCreditLineResponse.setcode(1);</li>
		<li>aCreditLineResponse.setcredit("CR-0000");</li>
		<li>aStock.setmoney("DOLAR");</li>
		<li>aStock.setavailable(new BigInteger("1.00"));</li>
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
	public ClientEnquiriesResponse getClientEnquiries(ClientEnquiriesRequest aClientEnquiriesRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
	
	
}