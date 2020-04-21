package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchNotificationResponse;

/** Clase que se encarga del envio de notificaciones Batch
 * 
 * @author kmeza
 *
 */
public interface ICoreServiceBatchNotification {
	/**
	 * 
	 *   
	 *   <b>Obtiene saldo m&iacutenimo de cuentas del cliente a notificar</b>
	 *   
    <b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>BatchNotificationRequest <b>wBatchNotificationRequest</b> = new BatchNotificationRequest();</li>
		
		<li>Product wProduct = new Product();</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_producto")));</li>
		
		<li>Batch wBatch = new Batch();</li>
		<li>wBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));</li>
		<li>wBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));</li>
		<li>wBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));</li>
		<li>wBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));</li>
		<li>wBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));</li>
		
		<li>wBatchNotificationRequest.setBatchInfo(wBatch);</li>
		<li>wBatchNotificationRequest.setProductInfo(wProduct);</li>
		<li>wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));</li>
		<li>wBatchNotificationRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));</li>
		<li>wBatchNotificationRequest.setCondicion(aRequest.readValueParam("@i_condicion"));</li>
		<li>wBatchNotificationRequest.setFecha_proceso(aRequest.readValueParam("@i_fecha_proceso"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>    
	<ul>
		<li>BatchNotificationResponse <b>balanceResponse</b> = new BatchNotificationResponse();</li>
		<li>balanceResponse.setNotification("S");</li>
		<li>balanceResponse.setReturnBalance(new BigDecimal(120));</li>	
		<li>balanceResponse.setReturnDays(3); </li>
		<li>balanceResponse.setSuccess(true);</li>
		<li>balanceResponse.setReturnCode(0);</li>
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
		
	public BatchNotificationResponse getMinimumBalanceNotification(BatchNotificationRequest aMinimumBalance) throws CTSServiceException, CTSInfrastructureException;
    
	/**
	 * 
	 * 
	 * <b>Obtiene fecha de vencimiento de plazo fijo del cliente a notificar.</b>
	 * 	
	<b>
	   @param
	  -ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>BatchNotificationRequest <b>wBatchNotificationRequest</b> = new BatchNotificationRequest();</li>
		<li>Batch wBatch = new Batch();</li>
		<li>Product wProduct = new Product();</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_producto")));</li>
		<li>wBatchNotificationRequest.setCondicion(aRequest.readValueParam("@i_condicion"));</li>
		<li>wBatchNotificationRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));</li>
		<li>wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));</li>
		<li>wBatchNotificationRequest.setFecha_proceso(aRequest.readValueParam("@i_fecha_proceso"));</li>
		<li>wBatch.setSarta(new Integer(aRequest.readValueParam("@i_sarta")));</li>
		<li>wBatch.setBatch(new Integer(aRequest.readValueParam("@i_batch")));</li>
		<li>wBatch.setSecuencial(new Integer(aRequest.readValueParam("@i_secuencial")));</li>
		<li>wBatch.setCorrida(new Integer(aRequest.readValueParam("@i_corrida")));</li>
		<li>wBatch.setIntento(new Integer(aRequest.readValueParam("@i_intento")));</li>
		<li>wBatchNotificationRequest.setBatchInfo(wBatch);</li>
		<li>wBatchNotificationRequest.setProductInfo(wProduct);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>BatchNotificationResponse <b>batchNotificationResponse</b> = new BatchNotificationResponse();</li>
		<li>batchNotificationResponse.setNotification("S");</li>
		<li>batchNotificationResponse.setReturnDays(3);</li>
		<li>batchNotificationResponse.setSuccess(true);</li>
		<li>batchNotificationResponse.setReturnCode(0);</li>
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
	public BatchNotificationResponse getFixedTermNotification(BatchNotificationRequest aRequest) throws CTSServiceException, CTSInfrastructureException;
	/**
	 * 
	 *
	 * <b>Obtiene fecha de vencimiento de prestamo y cuota de prestamo del cliente a notificar</b>
	 * 
	<b>
	   @param
	  -Parametros de entrada
	  </b>
	<ul>
		<li>String wSPname = new String();</li>
		<li>String wNotification = request.readValueParam("@i_notificacion");</li>
		<li>if (wNotification.equals("N7")) //Vencimiento de Cuota de PrÃ©stamo</li>
		<li>    wSPname ="cob_bvirtual..sp_bv_gen_dias_vencuotcred";</li>
		<li> if (wNotification.equals("N5")) //Vencimiento de PrÃ©stamo</li>
		<li>wSPname ="cob_bvirtual..sp_bv_gen_ven_credito";</li>
		
		<li>BatchNotificationRequest <b>wBatchNotificationRequest</b> = new BatchNotificationRequest();</li>
		
		<li>Product wProduct = new Product();</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));</li>
		
		<li>Batch wBatch = new Batch();</li>
		<li>wBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));</li>
		<li>wBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));</li>
		<li>wBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));</li>
		<li>wBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));</li>
		<li>wBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));</li>
		
		<li>wBatchNotificationRequest.setBatchInfo(wBatch);</li>
		<li>wBatchNotificationRequest.setProductInfo(wProduct);</li>
		<li>wBatchNotificationRequest.setOfficeCode(Integer.parseInt(aRequest.readValueParam("@s_ofi")));</li>
		<li>wBatchNotificationRequest.setValor_condicion(aRequest.readValueParam("@i_limite"));</li>
		<li>wBatchNotificationRequest.setCondicion(aRequest.readValueParam("@i_condicion"));</li>
		<li>wBatchNotificationRequest.setFecha_proceso(aRequest.readValueParam("@i_fecha_proceso"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>BatchNotificationResponse <b>batchNotificationResponse</b> = new BatchNotificationResponse();</li>
		<li>batchNotificationResponse.setNotification("S");</li>
		<li>batchNotificationResponse.setReturnDays(3);</li>
		<li>batchNotificationResponse.setSuccess(true);</li>
		<li>batchNotificationResponse.setReturnCode(0);</li>
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
	
    public BatchNotificationResponse getLoanExpiration(BatchNotificationRequest aLoanExpiration, String wSPname) throws CTSServiceException, CTSInfrastructureException;

}