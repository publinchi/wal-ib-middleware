package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchEtlTotalResponse;

/**
 * Esta interfaz contiene metodos necesarios para obtener informaci&oacuten de
 * fecha de proceso para carga batch ETL.
 * 
 * */
public interface ICoreServiceBatchChargeEtl {
	/**
	 * 
	 * 
	 * <b>Batch carga Etl</b>
	 * 
	 * <b>
	 * 
	 * @param -ParametrosDeEntrada </b>
	 *        <ul>
	 *        <li>String dateProcess = ""</li>
	 *        <li>sequential = aRequest.readValueParam("@i_fecha_proceso");</li>
	 *        </ul>
	 *        <b>
	 * @throws -ManejoDeErrores </b>
	 *         <ul>
	 *         <li>CTSServiceException</li>
	 *         <li>CTSInfrastructureException</li>
	 *         </ul>
	 **/
	public BatchChargeEtlResponse chargeEtl(
			BatchChargeEtlRequest aBatchChargeEtlRequest)
			throws CTSServiceException, CTSInfrastructureException;

	
	/**
	 * 
	 * 
	 * <b>Batch Totales Etl</b>
	 * 
	 * <b>
	 * 
	 * @param -ParametrosDeEntrada </b>
	 *        <ul>
	 *        <li>String dateProcess = ""</li>
	 *        <li>sequential = aRequest.readValueParam("@i_fecha_proceso");</li>
	 *        </ul>
	 *        <b>
	 * @throws -ManejoDeErrores </b>
	 *         <ul>
	 *         <li>CTSServiceException</li>
	 *         <li>CTSInfrastructureException</li>
	 *         </ul>
	 **/
	public BatchEtlTotalResponse generateTotalEtl(
			BatchChargeEtlRequest aBatchChargeEtlRequest)
			throws CTSServiceException, CTSInfrastructureException;

}