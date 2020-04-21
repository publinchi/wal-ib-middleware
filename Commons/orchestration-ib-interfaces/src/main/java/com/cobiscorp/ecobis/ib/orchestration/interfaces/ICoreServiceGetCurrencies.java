package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyResponse;

public interface ICoreServiceGetCurrencies {
	
	/**
	 * 
	 *  
	 *  <b>Consulta monedas.</b>
	 *  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CurrencyRequest aCurrencyRequest = new CurrencyRequest();</li>
		<li>aCurrencyRequest.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CurrencyResponse aCurrencyResponse = new CurrencyResponse();</li>
		<li>List<Currency> aCurrencyList = new ArrayList<Currency>();</li>
		
		<li>Currency aCurrency = new Currency();</li>
		
		<li>aCurrency.setCurrencyId(0);</li>
		<li>aCurrency.setCurrencyDescription("COLON");</li>
		<li>aCurrency.setCurrencySymbol("₡");</li>
		<li>aCurrency.setCurrencyNemonic("CRC");</li>
		<li>aCurrency.setCountryCode(41);</li>
		<li>aCurrency.setCountry("COSTA RICA");</li>
		<li>aCurrency.setState("V");</li>
		<li>aCurrency.setHasDecimal("S");</li>
		<li>aCurrencyList.add(aCurrency);</li>
		
		<li>aCurrency.setCurrencyId(1);</li>
		<li>aCurrency.setCurrencyDescription("DOLARES");</li>
		<li>aCurrency.setCurrencySymbol("$");</li>
		<li>aCurrency.setCurrencyNemonic("USD");</li>
		<li>aCurrency.setCountryCode(125);</li>
		<li>aCurrency.setCountry("PANAMA");</li>
		<li>aCurrency.setState("V");</li>
		<li>aCurrency.setHasDecimal("S");</li>
		<li>aCurrencyList.add(aCurrency);		</li>
		
	    <li>aCurrencyResponse.setCurrencyCollection(aCurrencyList);</li>
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
	CurrencyResponse GetCurrencies(CurrencyRequest aCurrencyRequest)throws CTSServiceException, CTSInfrastructureException;

}
