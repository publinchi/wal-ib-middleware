package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;

/**
 * 
 * @author gyagual
 * @version 1.0.0
 *
 */

public interface ICoreServiceCustomerProspective {
	/**  
	 * 
	 * 
	 * <b>Obtiene los clientes prospectos.</b>
	<b>
		@param
		-Parametros de entrada
		</b>

	<ul>
		<li>EntityRequest wEntityRequest = new EntityRequest();</li>
		<li>wEntityRequest.setSubtipo(aRequest.readValueParam("@i_subtipo"));</li>
		<li>wEntityRequest.setTipo(new Integer(aRequest.readValueParam("@i_tipo")));</li>
		<li>wEntityRequest.setModo(new Integer(aRequest.readValueParam("@i_modo")));</li>
		<li>wEntityRequest.setValor(aRequest.readValueParam("@i_valor"));</li>
		<li>wEntityRequest.setEnte(new Integer(aRequest.readValueParam("@i_ente")));</li>
		<li>wEntityRequest.setNombre(aRequest.readValueParam("@i_nombre"));</li>
		<li>wEntityRequest.setP_apellido(aRequest.readValueParam("@i_p_apellido"));</li>
		<li>wEntityRequest.setS_apellido(aRequest.readValueParam("@i_s_apellido"));</li>
		<li>wEntityRequest.setC_apellido(aRequest.readValueParam("@i_c_apellido"));</li>
		<li>wEntityRequest.setCed_ruc(aRequest.readValueParam("@i_ced_ruc"));</li>
		<li>wEntityRequest.setPasaporte(aRequest.readValueParam("@i_pasaporte"));</li>
		<li>wEntityRequest.setDepartamento(aRequest.readValueParam("@i_departamento"));</li>
	</ul>

	<b>
	@return
	-ParametrosDeSalida-
	</b>
	    
	<ul>
		<li>List<FullEntity> aFullEntityCollection = new ArrayList<FullEntity>();</li> 
		<li>EntityResponse aEntityResponse = new EntityResponse();</li>
		
		<li>FullEntity aFullEntity = new FullEntity();</li>
		<li>aFullEntity.setNumber("1234");</li>
		
		<li>if (aEntityRequest.getSubtipo().equals("P")){</li>
			<ul>
			<li>aFullEntity.setFirstLast("CACERES PAPELLIDO");</li>
			<li>aFullEntity.setSecondName("ORTIZ SAPELLIDO");</li>
			<li>aFullEntity.setMarriedSurname("DE GUZMAN MAPELLIDO");</li>
			<li>aFullEntity.setFirstName("MARIA");</li>
			<li>aFullEntity.setMiddleName("FERNANDA");</li>
			</ul>
		<li>}else{</li>
			<ul>
			<li>aFullEntity.setCompanyName("EMPRESA ABC");</li>
			<li>aFullEntity.setMarriedSurname("MARRIED NAME COMPANY");</li>
			<li>aFullEntity.setFirstName("FIRST NAME COMPANY");</li>
			<li>aFullEntity.setMiddleName("MIDDLE NAME COMPANY");</li>
			<li>aFullEntity.setBusinessName("EMPPRESA ABCDCED");</li>
			</ul>
		<li>}</li>
		
		<li>aFullEntity.setId("001");</li>
		<li>aFullEntity.setTypeId("");</li>
		<li>aFullEntity.setOfficial("1");</li>
		<li>aFullEntity.setOfficialName("NOMBRE DEL OFICIAL");</li>
		<li>aFullEntity.setLocked("1");</li>
		
		<li>aFullEntityCollection.add(aFullEntity);</li>
		
		<li>aEntityResponse.setEntityCollection(aFullEntityCollection);</li>
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
	EntityResponse getProspectiveCustomer(EntityRequest EntityRequest) throws CTSServiceException, CTSInfrastructureException;
}
