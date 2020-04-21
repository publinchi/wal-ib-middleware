package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailResponse;


/** 
 * Esta interfaz contiene metodos necesarios para obtener informaci&oacuten de detalle de solicitudes.
 * 
 * */
public interface ICoreServiceEnquiriesDetail {
	/**
	 * 
	 *   
	 *   <b>Detalle de Solicitudes</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String operation  = ""</li>
		<li>operation = aRequest.readValueParam("@i_operacion");</li>
		<li>String id  = ""</li>
		<li>id = aRequest.readValueParam("@i_id");</li>
		<li>String account  = ""</li>
		<li>account = aRequest.readValueParam("@i_account");</li>
		<li>Integer checkbook  = 0</li>
		<li>checkbook = Integer.parseInt(aRequest.readValueParam("@i_checkbook"));</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>EnquiriesDetail aEnquiriesDetail = new EnquiriesDetail();</li>
		<li>List<EnquiriesDetail> aEnquiriesDetailCollection = new ArrayList<EnquiriesDetail>(); </li>
		<li>EnquiriesDetailResponse aEnquiriesDetailResponse = new EnquiriesDetailResponse();</li>
		<li>aEnquiriesDetail.setAccount("495358");</li>
		<li>aEnquiriesDetail.setCheckbookTipe("1156456");</li>
		<li>aEnquiriesDetail.setChecks(1);</li>
		<li>aEnquiriesDetail.setDelivery(1);</li>
		<li>aEnquiriesDetail.setState("V");</li>
		<li>aEnquiriesDetail.setAmount(new BigDecimal("200.50"));</li>
		<li>aEnquiriesDetail.setPurpose("Proposed");</li>
		<li>aEnquiriesDetail.setBeneficiary("COBIS");</li>
		<li>aEnquiriesDetail.setThirdIdentification("COBIS");</li>
		<li>aEnquiriesDetail.setName("COBIS");</li>
		<li>aEnquiriesDetail.setApplicationNumber(89);</li>
	
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
	public EnquiriesDetailResponse getDetail(EnquiriesDetailRequest aEnquiriesDetailRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
	
}