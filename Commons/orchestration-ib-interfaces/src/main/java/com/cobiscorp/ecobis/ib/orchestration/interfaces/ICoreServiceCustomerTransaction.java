package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;

/**
 * This interface contains the methods needed to Get/Update customer information.
 * 
 * @since Ago 13, 2014
 * @author GCondo
 * @version 1.0.0
 *
 */ 





public interface ICoreServiceCustomerTransaction {
	/**  
	 * 
	 * This process should execute: Update customer information
	 * 
	<b>
		@param
		-ParametrosDeEntrada
		</b>

	<ul>
		<li>AddressRequest aAddressRequest = new AddressRequest();</li>
		
		<li>Client aClient = new Client();</li>		
		<li>aClient.setIdCustomer(aRequest.readValueParam("@i_cliente"));</li>
		
		<li>Address aAddress = new Address();</li>
		<li>aAddress.setPhoneCode(Integer.parseInt(aRequest.readValueParam("@i_cod_telefono")));</li>
		<li>aAddress.setAddressCode(Integer.parseInt(aRequest.readValueParam("@i_cod_direccion")));</li>
		<li>aAddress.setEmailCode(Integer.parseInt(aRequest.readValueParam("@i_cod_email")));</li>
		<li>aAddress.setPhone(aRequest.readValueParam("@i_telefono"));</li>
		<li>aAddress.setNeighborhood(aRequest.readValueParam("@i_barrio"));</li>
		<li>aAddress.setStreet(aRequest.readValueParam("@i_calle"));</li>
		<li>aAddress.setHouse(aRequest.readValueParam("@i_casa"));</li>
		<li>aAddress.setBuilding(aRequest.readValueParam("@i_descripcion"));</li>
		<li>aAddress.setEmail(aRequest.readValueParam("@i_email"));</li>
		
		<li>aAddressRequest.setClientCollection(aClient);</li>
		<li>aAddressRequest.setAddressCollection(aAddress);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>AddressResponse aAddressResponse = new AddressResponse();</li>
		
		<li>Address aAddress = new Address();</li>
		<li>aAddress.setAdditionalInformation("V");</li>
		
		<li>Client aClient = new Client();</li>
		<li>aClient.setCompleteName("Isaac Alberto Torres Montesdeoca");</li>		
		
		<li>aAddress.setPhone("042153205");	</li>
		<li>aAddress.setBuilding("Quito 448 y Ambato");</li>
		<li>aAddress.setEmail("isaacatm@hotmail.com");</li>	
		<li>aAddressResponse.setAddressCollection(aAddress);</li>
		<li>aAddressResponse.setClientCollection(aClient);</li>
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
	AddressResponse setCustomerTransaction(AddressRequest addressRequest) throws CTSServiceException, CTSInfrastructureException;
}
