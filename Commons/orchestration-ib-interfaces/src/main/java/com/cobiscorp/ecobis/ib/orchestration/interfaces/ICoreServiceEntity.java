package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;

/**
*
* @author mvelez
* @since Nov 18, 2014
*
*/
public interface ICoreServiceEntity {
	
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>EntityRequest entityReq = new EntityRequest();</li>		   	        
	    <li>entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));</li>
	    <li>entityReq.setSubtipo(aRequest.readValueParam("@i_subtipo"));</li>
	    <li>entityReq.setTipo(Integer.parseInt(aRequest.readValueParam("@i_tipo")));</li>
	    <li>entityReq.setModo(Integer.parseInt(aRequest.readValueParam("@i_modo")));</li>
	    <li>entityReq.setValor(aRequest.readValueParam("@i_valor"));</li>
	    <li>entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
	    <li>entityReq.setNombre(aRequest.readValueParam("@i_nombre"));</li>
	    <li>entityReq.setS_nombre(aRequest.readValueParam("@i_s_nombre"));</li>
	    <li>entityReq.setP_apellido(aRequest.readValueParam("@i_p_apellido"));</li>
	    <li>entityReq.setS_apellido(aRequest.readValueParam("@i_s_apellido"));</li>
	    <li>entityReq.setC_apellido(aRequest.readValueParam("@i_c_apellido"));</li>
	    <li>entityReq.setCed_ruc(aRequest.readValueParam("@i_ced_ruc"));</li>
	    <li>entityReq.setOficina(Integer.parseInt(aRequest.readValueParam("@i_oficina")));</li>
	    <li>entityReq.setNombre_completo(aRequest.readValueParam("@i_nombre_completo"));</li>
	    <li>entityReq.setPasaporte(aRequest.readValueParam("@i_pasaporte"));</li>
	    <li>entityReq.setEs_cliente(aRequest.readValueParam("@i_es_cliente"));</li>
	    <li>entityReq.setStatus_ente(aRequest.readValueParam("@i_status_ente"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>EntityResponse EntityResp = new EntityResponse();</li>
		<li>List<FullEntity> fullEntityCollection = new ArrayList<FullEntity>();</li>
		
		<li>FullEntity aFullEntity = null;</li>
		<li>aFullEntity = new FullEntity();</li>
		
		<li>if (aEntityRequest.getSubtipo().equals("P")) {</li>
		<ul>
			<li>aFullEntity.setNumber("0");</li>
			<li>aFullEntity.setFirstLast("ENTE GetNaturalCustomerByAlphabetical");	</li>		
			<li>aFullEntity.setSecondName("SEGUNDO APELLIDO PRUEBA DUMMY");</li>
			<li>aFullEntity.setMarriedSurname("APELLIDO CASADA PRUEBA DUMMY");</li>
			<li>aFullEntity.setFirstName("PRIMER NOMBRE PRUEBA DUMMY");</li>
			<li>aFullEntity.setMiddleName("SEGUNDO NOMBRE PRUEBA DUMMY");</li>
			<li>aFullEntity.setId("01-0724-0230");</li>
			<li>aFullEntity.setTypeId("1.1");</li>
			<li>aFullEntity.setOfficial("202");</li>
			<li>aFullEntity.setOfficialName("OFICIAL PRUEBA");</li>
			<li>aFullEntity.setLocked("N");</li>
			<li>aFullEntity.setStatus("V");</li>
			<li>aFullEntity.setCustomer("S");</li>
			<li>aFullEntity.setDescriptionStatus("ACTIVO");</li>
			
			<li>fullEntityCollection.add(aFullEntity);</li>
		</ul>
		<li>}</li>
		
		<li>if (aEntityRequest.getSubtipo().equals("C")) {</li>
		<ul>			
				<li>aFullEntity.setNumber("1");</li>
				<li>aFullEntity.setCompanyName("COMPANY GetCompanyByCommercialName");</li>			
				<li>aFullEntity.setMarriedSurname("MY COMPANY S.A");</li>
				<li>aFullEntity.setFirstName("XXX");</li>
				<li>aFullEntity.setMiddleName("COMPANY XXX");</li>
				<li>aFullEntity.setBusinessName("XXXXX");</li>
				<li>aFullEntity.setId("0-653-231212");</li>
				<li>aFullEntity.setTypeId("2.1");</li>
				<li>aFullEntity.setOfficial("200");</li>
				<li>aFullEntity.setOfficialName("OFICIAL PRUEBA DUMMY");</li>
				<li>aFullEntity.setLocked("V");</li>
				<li>aFullEntity.setCustomer("S");</li>
				<li>aFullEntity.setStatus("V");			</li>
				<li>aFullEntity.setDescriptionStatus("ACTIVO");</li>
	
				<li>fullEntityCollection.add(aFullEntity);</li>
		<li>}</li>
		<li>EntityResp.setEntityCollection(fullEntityCollection);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	 */
	EntityResponse GetEntity (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	EntityResponse GetEntityId (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	/*
	EntityResponse GetNaturalCustomerByAlphabetical (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	EntityResponse GetNaturalCustomerByCode         (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	EntityResponse GetNaturalCustomerByMarriedName  (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	EntityResponse GetNaturalCustomerById           (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	EntityResponse GetNaturalCustomerByStatus       (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
	*/
}
