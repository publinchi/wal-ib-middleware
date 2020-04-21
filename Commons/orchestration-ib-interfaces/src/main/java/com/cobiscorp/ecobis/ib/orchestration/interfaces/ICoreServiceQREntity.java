/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QREntityResponse;

/**
 * @author mvelez
 *
 */

public interface ICoreServiceQREntity {
	/**
	 * 
	 *   
	 *   <b>Obtiene cierta informaci&oacuten del Ente por el c&oacute digo de cliente.</b>
	<b>
		@param
		-Parametros de entrada
		
	</b>
	  
	<ul>	    
	  	<li>EntityRequest entityReq = new EntityRequest();</li>
	  	<li>entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
	    <li>entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));</li>
	</ul>
	<b>
		@return
		-Parametros de Salida
	</b>
	<ul>
		<li>QREntityResponse EntityResp = new QREntityResponse();</li>
		<li>List<QREntity> fullEntityCollection = new ArrayList<QREntity>();</li>
		<li>QREntity aQREntity  = new QREntity();</li>
	    <li>aQREntity.setNombre_completo("JUAN JOSE PEREZ DUMMY");</li>
		<li>aQREntity.setApellido_casada("PEREZ DUMMY");</li>			
		<li>aQREntity.setSubtype("P");</li>
		<li>aQREntity.setCed_ruc("0916565218");</li>
		<li>aQREntity.setRetencion("S");</li>
		<li>aQREntity.setMala_referencia("N");</li>
		<li>aQREntity.setNombre_largo("JUAN JOSE PEREZ PEREZ DUMMY");</li>
		<li>aQREntity.setNombre_corto("JUAN PEREZ");</li>
		<li>aQREntity.setRazon_social("JUAN PEERZ S.A");</li>
		<li>aQREntity.setGrupo_econ("376");</li>
		<li>QREntityCollection.add(aQREntity);</li>
		<li>EntityResp.setQREntityCollection(QREntityCollection);</li>
	</ul>
	*/
	QREntityResponse GetQREntity (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
}
