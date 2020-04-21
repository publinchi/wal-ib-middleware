package com.cobiscorp.ecobis.ib.orchestration.interfaces;


import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionResponse;

/**
 * 
 * @author dguerra
 * @since Aug 22, 2014
 * @version 1.0.0
 */
public interface ICoreServiceReexecutionComponent {
	
	/** 
	 * 
	 *  Save re execution.
	 *  
    <b>
    	@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>ReExecutionRequest reExecutionRequest = new ReExecutionRequest();</li>
		<li>Product originProduct = new Product();</li>
		<li>Currency originCurrency = new Currency();</li>
		<li>Product destinationProduct = new Product();</li>
		<li>Currency destinationCurrency = new Currency();</li>
		<li>Client cliente = new Client();</li>
		<li>originProduct.setProductNumber(wOriginalRequest.readValueParam("@i_cta"));</li>
		<li>originProduct.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>originCurrency.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_mon").toString()));</li>
		<li>originProduct.setProductNemonic(wOriginalRequest.readValueParam("@i_producto"));</li>
		<li>originProduct.setCurrency(originCurrency);</li>		
		<li>destinationProduct.setProductNumber(wOriginalRequest.readValueParam("@i_cta_des"));</li>
		<li>destinationProduct.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
		<li>destinationCurrency.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_mon_des").toString()));</li>
		<li>destinationProduct.setCurrency(destinationCurrency);</li>
		<li>reExecutionRequest.setOriginProduct(originProduct);</li>
		<li>reExecutionRequest.setDestinationProduct(destinationProduct);</li>		
		<li>cliente.setLogin(wOriginalRequest.readValueParam("@i_login"));</li>
		<li>cliente.setId(wOriginalRequest.readValueParam("@i_ente"));</li>
		<li>reExecutionRequest.setCliente(cliente);</li>		
		<li>transferRequest.setAmmount(new BigDecimal(wOriginalRequest.readValueParam("@i_val").toString()));</li>
		<li>transferRequest.setDescriptionTransfer(wOriginalRequest.readValueParam("@i_concepto"));</li>
		<li>reExecutionRequest.setTransferRequest(transferRequest);</li>		
		<li>reExecutionRequest.setRty(wOriginalRequest.readValueParam("@t_rty"));</li>	
		<li>reExecutionRequest.setServiceName(wOriginalRequest.getSpName());</li>
		<li>reExecutionRequest.setPriority("5");</li>
		<li>reExecutionRequest.setSsnBranch(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));</li>
		<li>reExecutionRequest.setSsnCentral(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN));</li>
		<li>reExecutionRequest.setTrn(wOriginalRequest.readValueParam("@t_trn"));</li>
		<li>reExecutionRequest.setSrv(wOriginalRequest.readValueParam("@s_srv"));</li>
		<li>reExecutionRequest.setIn_line("N");</li>
	</ul>
	<b>
		@return
		-Parametros de Salida
	</b>
	        
	<ul>	   
		<li>wReExecutionResponse.setSuccess(true);</li>
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
	ReExecutionResponse saveReexecutionComponent(ReExecutionRequest reexecutionRequest) throws CTSServiceException, CTSInfrastructureException;

}
