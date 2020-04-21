/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ContractRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractResponse;

/**
 * @author jveloz
 *
 */

public interface ICoreServiceContract {
	/**  
	<b>
   		@param
  		-ParametrosDeEntrada
  	</b>
  
    <ul>
    	<li>ContractRequest contractRequest=new ContractRequest();</li>
		<li>contractRequest.setContractServiceId(wOriginalRequest.readValueParam("@i_canal"));</li>
  		<li>contractRequest.setContractCategoryId(wOriginalRequest.readValueParam("@i_categoria"));</li>
		<li>contractRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
		@return
		-ParametrosDeSalida-
    </b>        
    <ul>
		
       ContractResponse wContractResponse = new ContractResponse();
        List<Contract> listContract = new ArrayList<Contract>();
       
		<li>wContract.setCodigoConvenio(columns[COL_CODIGO_CONVENIO].getValue());</li>
		<li>wContract.setNombreConvenio(columns[COL_NOMBRE_CONVENIO].getValue());</li>
		<li>wContract.setNemonicoConvenio(columns[COL_NEMONICO_CONVENIO].getValue());</li>
		<li>wContract.setDescripcionConvenio(columns[COL_DESCRIPCION_CONVENIO].getValue());</li>
		<li>wContract.setEstadoConvenio(columns[COL_ESTADO_CONVENIO].getValue());</li>
		<li>wContract.setClienteConvenio(columns[COL_CLIENTE_CONVENIO].getValue());</li>
		<li>wContract.setNombreLargo(columns[COL_NOMBRE_LARGO].getValue());</li>
		<li>wContract.setTipoInterfaz(columns[COL_TIPO_INTERFAZ].getValue());</li>
		<li>wContract.setFechaMod(columns[COL_FECHA_MOD].getValue());</li>
		<li>wContract.setUsuarioMod(columns[COL_USUARIO_MOD].getValue());</li>
		<li>wContract.setIdMoneda(columns[COL_ID_MONEDA].getValue());</li>
		<li>wContract.setMoneda(columns[COL_MONEDA].getValue());</li>
		<li>listContract.add(wContract);<li>
		<li>wContractResponse.setListContract(listContract);<li>
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
	public ContractResponse getContracts(ContractRequest aContractRequest) throws CTSServiceException, CTSInfrastructureException;
}
