package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CountryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CountryResponse;

/**
 * 
 * @author dguerra
 * @since Aug 29, 2014
 * @version 1.0.0
 */

public interface ICoreServiceCountryCatalog {
	/**
	 * 
	 * Search Country.
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>    
			<li>anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4, countryRequest.getMode().toString());</li>
			<li>anOriginalRequest.addInputParam("@i_descripcion", ICTSTypes.SQLVARCHAR, countryRequest.getDescripcion());</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>CountryResponse wCountryResponse = new CountryResponse();</li>
		<li>List<Country> countryCollection = new ArrayList<Country>();</li>
	 	<li>countryCollection.add(country);</li>
		
		<li>country.setCode("123");</li>
		<li>country.setName("ECUADOR");</li>
		<li>country.setNationality("ECUATORIANO");</li>
		<li>country.setContinentCode("2");</li>
		<li>country.setContinent("AMERICA");</li>
		
		<li>wCountryResponse.setCountryCollection(countryCollection);</li>
		<li>wCountryResponse.setSuccess(response.getReturnCode() == 0);</li>
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
	CountryResponse searchCountryCatalog(CountryRequest countryRequest) throws CTSServiceException, CTSInfrastructureException;
}
