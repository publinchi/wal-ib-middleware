package com.cobiscorp.ecobis.ib.orchestration.interfaces;



import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.GRBOperationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.GRBOperationResponse;




/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion de las oparaciones activas
 * para la Boleta de Garantia.
 * 
 * */
public interface ICoreServiceGRBOperation {
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta  de Subtipos Boleta Garantia</b>
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
	public GRBOperationResponse getOperation(GRBOperationRequest aGRBOperatioRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
