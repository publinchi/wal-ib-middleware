package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;

/**
 * This interface contains the methods needed to Get/Update customer information.
 * 
 * @since Ago 06, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */ 

public interface ICoreServiceCustomerInformation {
	/**
	 * 
	 * <b>Obtiene informaci&oacuten de la direcci&oacuten del cliente.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
		</b>

	<ul>
		<li>AddressRequest aAddressRequest = new AddressRequest();</li>
		<li>Client aClient = new Client();</li>
		<li>aClient.setIdCustomer(aRequest.readValueParam("@i_cliente"));</li>		
		<li>aAddressRequest.setClientCollection(aClient);</li>		
	</ul>

	<b>
	@return
	-ParametrosDeSalida-
	</b>
	    
	<ul>
		<li>Address aAddress = new Address();</li>
		<li>AddressResponse aAddressResponse = new AddressResponse();</li>
		<li>aAddress.setAdditionalInformation("V");</li>
		<li>aAddress.setPhone("042153205");	</li>
		<li>aAddress.setNeighborhood("Cerro de las Cabras");</li>
		<li>aAddress.setStreet("Quito 448 y Ambato");</li>
		<li>aAddress.setBuilding("nnn");</li>
		<li>aAddress.setHouse("Casa color Melon");</li>
		<li>aAddress.setEmail("isaacatm@hotmail.com");</li>
		<li>aAddress.setPhoneCode(1);</li>
		<li>aAddress.setAddressCode(123);</li>
		<li>aAddress.setEmailCode(25);</li>
		<li>aAddressResponse.setAddressCollection(aAddress);</li>
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
	AddressResponse getCustomerInformation(AddressRequest addressRequest) throws CTSServiceException, CTSInfrastructureException;	
	
}
