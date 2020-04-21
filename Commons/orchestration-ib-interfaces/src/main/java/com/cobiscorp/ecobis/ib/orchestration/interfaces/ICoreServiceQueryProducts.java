package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsResponse;


public interface ICoreServiceQueryProducts {
	/**  
    <b>
    	@param
		-Parametros de entrada
	</b>
	  
	<ul>	    
      	<li>queryProductsReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));</li>
	    <li>queryProductsReq.setOperation(aRequest.readValueParam("@i_operacion"));</li>
	    <li>queryProductsReq.setProduct(Integer.parseInt(aRequest.readValueParam("@i_producto")));</li>
	    <li>queryProductsReq.setClientType(aRequest.readValueParam("@i_tipo_cliente"));</li>
	    <li>queryProductsReq.setOrigen(Integer.parseInt(aRequest.readValueParam("@i_origen")));</li>
	    <li>queryProductsReq.setCliente(Integer.parseInt(aRequest.readValueParam("@i_cliente")));</li>
	</ul>
	<b>
		@return
		-Parametros de Salida
	</b>
	        
	<ul>	   
		<li>wQueryProductsRequest.getProduct()</li>
		<li>wQueryProductsRequest.getOrigen()</li>
		<li>wQueryProductsResponse.setRowCount("10");
		
		<li>--------AHORROS Y CORRIENTE-----</li>
		<li>wProduct.setProductNumber("10410108280249815");</li>
     	<li>wCurrency.setCurrencyId(0);</li>
     	<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductName("MARIA CATALINA CC AHO");</li>
		<li>wQueryProduct.setCode(3001);</li>
		<li>wQueryProduct.setOfficial("1");</li>
		<li>wProduct.setProductDescription("product descript");</li>
		<li>wProduct.setProductNemonic("AHO");</li> 
		<li>wQueryProduct.setProduct(wProduct);</li>		
		
		<li>--------ATM y TARJETA DE CREDITO-----</li>
		<li>wProduct.setProductNumber("10410108280249815");</li>
     	<li>wCurrency.setCurrencyId(0);</li>
     	<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductName("MARIA CATALINA CARDS");</li>
		<li>wQueryProduct.setCode(16830);</li>
		<li>wQueryProduct.setOfficial("1");</li>
		<li>wQueryProduct.setProduct(wProduct);</li>
			
		<li>--------CARTERA-----</li>
		<li>wProduct.setProductNumber("10410108280249815");</li>
     	<li>wCurrency.setCurrencyId(0);</li>
     	<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductName("MARIA CATALINA LOAN");</li>
		<li>wQueryProduct.setCode(7001);</li>
		<li>wProduct.setProductAlias("PROD ALIAS");</li>		                 
		<li>wProduct.setProductDescription("PROD DESC");</li>   
		<li>wProduct.setProductNemonic("NEM");</li>		
		<li>wQueryProduct.setProduct(wProduct);</li>
		
		<li>--------DEPOSITOS A PLAZO FIJO-----</li>
		<li>wProduct.setProductNumber("10410108280249815");</li>
     	<li>wCurrency.setCurrencyId(0);</li>
     	<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductName("MARIA CATALINA CC");</li>
		<li>wQueryProduct.setCode(1401);</li>			
		<li>wQueryProduct.setProduct(wProduct);</li>
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
	QueryProductsResponse getQueryProducts(QueryProductsRequest wQueryProductsRequest) throws CTSServiceException, CTSInfrastructureException;

}
