package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeResponse;


/* metodo de consulta para el grid  de la posicion consolidada*/
public interface ICoreServiceQueryBankGuarantee {

	/**
	 * 
	 *   
	 *   <b>Consulta  de Boletas Garantia</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String entity= ""</li>
		<li>condition = aRequest.readValueParam("@i_entity");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>GRB aGRB = new GRB();</li>
		<li>List<GRB> aSubTypeBankGuaranteeCollection = new ArrayList<GRB>(); </li>
		<li>GRBOPerationResponse aGRBOPerationResponse = new GRBOPerationResponse();</li>
		<li>aGRBOPerationResponse.setId("1");</li>
		<li>aGRBOPerationResponse.setoperation("GRB0001");</li>
		<li>aGRBOPerationResponse.setcurrency("dolar");</li>
		<li>aGRBOPerationResponse.setcurrencyCode("0");</li>
			
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
	public QueryBankGuaranteeResponse getBankGuarantees(QueryBankGuaranteeRequest aBankGuaranteeRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
