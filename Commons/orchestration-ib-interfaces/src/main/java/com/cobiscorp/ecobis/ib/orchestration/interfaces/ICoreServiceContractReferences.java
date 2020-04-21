/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesResponse;

/**
 * @author jveloz
 *
 */
public interface ICoreServiceContractReferences {
	/**  
	<b>
   	@param
  	-ParametrosDeEntrada
  	</b>
    <ul>
		<li>ContractReferencesRequest wContractReferencesRequest=new ContractReferencesRequest();</li>
		<li>wContractReferencesRequest.setContractId(Integer.parseInt(aRequest.readValueParam("@i_convenio")));</li>
		<li>wContractReferencesRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
    </ul>
    <b>
    @return
   -ParametrosDeSalida-
    </b>
    <ul>
    	<li>List<ContractReferences> <b>listContractReferences</b> = new ArrayList<ContractReferences>();</li>
		<li>wContractReferences.setCampo(columns[COL_CAMPO].getValue());</li>
		<li>wContractReferences.setEtiqueta(columns[COL_ETIQUETA].getValue());</li>
		<li>wContractReferences.setTipo(columns[COL_TIPO].getValue());</li>
		<li>wContractReferences.setHabilitado(columns[COL_HABILITADO].getValue());</li>
		<li>wContractReferences.setObligatorio(columns[COL_OBLIGATORIO].getValue());</li>
		<li>wContractReferences.setTipoDato(columns[COL_TIPO_DATO].getValue());</li>
		<li>wContractReferences.setLongitud(columns[COL_LONGITUD].getValue());</li>
		<li>wContractReferences.setCampoDefault(columns[COL_CAMPO_DEFAULT].getValue());</li>
		<li>wContractReferences.setCatalogo(columns[COL_CATALOGO].getValue());</li>
		<li>wContractReferences.setRedigitar(columns[COL_REDIGITAR].getValue());</li>
		<li>wContractReferences.setVisible(columns[COL_VISIBLE].getValue());</li>
		<li>wContractReferences.setOrden(columns[COL_ORDEN].getValue());</li>
		<li>listContractReferences.add(wContractReferences);</li>
		<li>wContractReferencesResponse.setListContractLabel(listContractReferences);</li>
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
	public ContractReferencesResponse getContractReference(ContractReferencesRequest aContractReferencesRequest) throws CTSServiceException, CTSInfrastructureException;
}
