package com.cobiscorp.ecobis.ib.orchestration.interfaces;



import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailResponse;





/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion de las oparaciones activas
 * para la Boleta de Garantia.
 * 
 * */
public interface ICoreServiceACHDetail {
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta  de Detalle ACH</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String numOrden= ""</li>
		<li>num_orden = aRequest.readValueParam("@i_num_orden");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>AchDetail aAchDet = new AchDetail();</li>
		<li>List<AchDetail> aACHDEtailCollection = new ArrayList<AchDetail>(); </li>
		<li>ACHDetailResponse aACHDetailResponse = new ACHDetailResponse();</li>
		<li>aAchDet.setNumberOrder("1033201510238105598");</li>
		<li>aAchDet.setStatus("Cancelada");</li>
			
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
	public ACHDetailResponse getACHDetail(ACHDetailRequest aACHDetailRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
