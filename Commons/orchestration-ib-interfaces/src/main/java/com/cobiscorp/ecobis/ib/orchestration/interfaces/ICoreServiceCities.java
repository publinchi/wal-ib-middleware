package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CityResponse;

public interface ICoreServiceCities {

	/**
	 * 
	 *   
	 *   <b>Consulta ciudades por pa&iacutes.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CityRequest cityRequest = new CityRequest();</li>
		<li>cityRequest.setContinentCode(wOriginalRequest.readValueParam("@i_cont"));</li>
		<li>cityRequest.setCountryCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_pais").toString()));</li>		
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CityResponse wCityResponse = new CityResponse();</li>
		<li>List<City> cityCollection = new ArrayList<City>();</li>

		<li>City city = new City();</li>
		<li>Country country = new Country();</li>
		<li>city.setCodeCity(1);</li>
		<li>city.setNameCity("CITY DUMMY1");</li>
		<li>country.setCode(1);</li>
		<li>country.setName("PAIS DUMMY1");</li>
		<li>country.setContinentCode("AMS");</li>
		<li>country.setContinent("CONTINENT DUMMY1");</li>
		<li>city.setCountry(country);</li>
		<li>cityCollection.add(city);</li>

		<li>city = new City();</li>
		<li>country = new Country();</li>
		<li>city.setCodeCity(2);</li>
		<li>city.setNameCity("CITY DUMMY2");</li>
		<li>country.setCode(1);</li>
		<li>country.setName("PAIS DUMMY1");</li>
		<li>country.setContinentCode("AMS");</li>
		<li>country.setContinent("CONTINENT DUMMY1");</li>
		<li>city.setCountry(country);</li>
		<li>cityCollection.add(city);</li>

		<li>wCityResponse.setCityCollection(cityCollection);</li>
		<li>wCityResponse.setSuccess(true);</li>
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
	CityResponse getCitiesByCountry(CityRequest cityRequest) throws CTSServiceException, CTSInfrastructureException;
}
