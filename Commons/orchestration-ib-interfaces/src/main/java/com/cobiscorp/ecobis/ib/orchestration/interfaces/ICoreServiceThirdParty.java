package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyResponse;

/**
 * This interface contains the methods needed to validate a Third
 * cards.
 * 
 * @author cecheverria
 * @since Sept, 2 2014
 * @version 1.0.0
 */
public interface ICoreServiceThirdParty {
	
	/**
	 * 
	 *   
	 *   <b>Valida terceros del banco</b>
	 *   
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>		
    			   <li>Client client = new Client();</li>
    			   <li>Product product = new Product();</li>
    			   <li>Currency currency = new Currency();</li>
				   <li>currency.setCurrencyId(1);</li>
    			   <li>client.setLogin("wtoledo");</li>
    			   <li>product.setProductNumber("1234567890");</li>
    			   <li>product.setCurrency(currency);</li>
    			   <li>product.setProductType(3);</li>
    			   
    			   <li>ThirdParty thirdParty = new ThirdParty();</li>
				   <li>thirdParty.setClient(client);</li>	   
				   <li>thirdParty.setProduct(product);</li>
				   <li>aThirdPartyRequest.setThirdParty(thirdParty);</li>
    			  
    </ul>
    <b>
                   @return
        		   -ParametrosDeSalida-
    </b>    
    <ul>
                   	<li>ThirdParty thirdParty = new ThirdParty();</li>
					<li>Product product = new Product();</li>
					<li>Currency currency = new Currency();</li>
					<li>currency.setCurrencyId(Integer.parseInt(columns[2].getValue()));</li>
					<li>currency.setCurrencyDescription(columns[7].getValue());</li>
			
					<li>product.setCurrency(currency);</li>
			
					<li>product.setProductNemonic(columns[5].getValue());</li>
					<li>product.setProductType(Integer.parseInt(columns[3].getValue()));</li>
					<li>product.setProductNumber(columns[4].getValue());</li>
					<li>product.setProductDescription(columns[6].getValue());</li>
					<li>thirdParty.setIdBeneficiary(columns[0].getValue());</li>
					<li>thirdParty.setBeneficiary(columns[1].getValue());</li>
					<li>thirdParty.setProduct(product);</li>
					<li>response.setThirdParty(thirdParty);</li>


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

	ThirdPartyResponse validateInternalThirdParty(ThirdPartyRequest aThirdPartyRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Valida terceros de otros bancos</b>
	 *   
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>		
    			   <li>Client client = new Client();</li>
    			   <li>Product product = new Product();</li>
    			   <li>client.setLogin("wtoledo");</li>
    			   <li>product.setProductNumber("1234567890");</li>
    			   <li>product.setCurrency(currency);</li>
    			   <li>product.setProductType(3);</li>
    			   
    			   <li>ThirdParty thirdParty = new ThirdParty();</li>
				   <li>thirdParty.setClient(client);</li>	   
				   <li>thirdParty.setProduct(product);</li>
				   <li>aThirdPartyRequest.setThirdParty(thirdParty);</li>
    			  
    </ul>
    <b>
                   @return
        		   -ParametrosDeSalida-
    </b>    
    <ul>
                	<li>ThirdPartyResponse response = new ThirdPartyResponse();</li>
					<li>ThirdParty thirdParty = new ThirdParty();</li>
					<li>Client client = new Client();</li>
					<li>Product product = new Product();</li>
					<li>Currency currency = new Currency();</li>
					<li>currency.setCurrencyId(0);</li>
					<li>currency.setCurrencyDescription("DOLARES");</li>
					<li>currency.setCurrencyNemonic("$");</li>
		
					<li>product.setCurrency(currency);</li>
					<li>product.setProductNemonic("CTE");</li>
					<li>product.setProductType(3);</li>
					<li>product.setProductNumber(aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());</li>
					<li>product.setProductDescription("Propietario de "+aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());</li>
					<li>client.setLogin(aThirdPartyRequest.getThirdParty().getClient().getLogin());</li>
					<li>thirdParty.setClient(client);</li>
					<li>thirdParty.setIdBeneficiary("0917583775");</li>
					<li>thirdParty.setBeneficiary("Propietario de "+aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());</li>
					<li>thirdParty.setProduct(product);</li>
					<li>response.setThirdParty(thirdParty);</li>
					<li>response.setResponseCodeExternalSystem("0");</li>


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

	ThirdPartyResponse validateExternalThirdParty(ThirdPartyRequest aThirdPartyRequest) throws CTSServiceException, CTSInfrastructureException;

}
