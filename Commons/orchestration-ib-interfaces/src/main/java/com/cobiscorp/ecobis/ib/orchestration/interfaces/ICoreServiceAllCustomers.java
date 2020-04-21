package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;

/** Clase que consulta todos los clientes
 * 
 * @author kmeza
 * @version 1.0.0
 *
 */
public interface ICoreServiceAllCustomers {
	
	/**
	 * 
	 *   
	 *   <b>Consulta  todos los clientes .</b>
	 *   
   <b>
	   @param
	  -ParametrosDeEntrada
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
		<li>wEntityRequest.setNombre_completo(aRequest.readValueParam("@i_nombre_completo"));</li>
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
		
		<li>aFullEntity.setNumber("333");</li>
		<li>if (aEntityRequest.getSubtipo().equals("P")){</li>
		<ul>
			<li>	aFullEntity.setFirstLast("CACERES PAPELLIDO");</li>
			<li>	aFullEntity.setSecondName("ORTIZ SAPELLIDO");</li>
			<li>	aFullEntity.setMarriedSurname("DE GUZMAN MAPELLIDO");</li>
			<li>	aFullEntity.setFirstName("MARIA");</li>
			<li>	aFullEntity.setMiddleName("FERNANDA");</li>
		</ul>
		<li>}else{</li>
		<ul>
			<li>aFullEntity.setCompanyName("EMPRESA ABC");</li></li>
			<li>aFullEntity.setMarriedSurname("MARRIEDNAME COMPANY");</li>
			<li>aFullEntity.setFirstName("FIRST NAME COMPANY");</li>
			<li>aFullEntity.setMiddleName("MIDDLENAME COMPANY");</li>
			<li>aFullEntity.setBusinessName("EMPPRESA ABCDCED");</li>
		</ul>
		<li>}</li>
		
		<li>aFullEntity.setId("1");</li>
		<li>aFullEntity.setTypeId("22");</li>
		<li>aFullEntity.setOfficial("202");</li>
		<li>aFullEntity.setOfficialName("CAROLINA ALMEIDA");</li>
		<li>aFullEntity.setStatus("V");</li>
		<li>aFullEntity.setDescriptionStatus("VIGENTE");</li>
		<li>aFullEntity.setPersonType("A");</li>
		
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
	
	
	
	EntityResponse getAllCustomers(EntityRequest EntityRequest) throws CTSServiceException, CTSInfrastructureException;


}
