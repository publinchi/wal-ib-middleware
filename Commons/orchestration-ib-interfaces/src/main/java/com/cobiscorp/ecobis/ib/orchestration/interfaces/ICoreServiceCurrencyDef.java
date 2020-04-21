package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionResponse;

public interface ICoreServiceCurrencyDef {
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>    
		<li>CurrencyDefinitionRequest currencyDefinitionRequest= new CurrencyDefinitionRequest();</li>		
		<li>currencyDefinitionRequest.setMode(Integer.parseInt(wOriginalRequest.readValueParam("@i_modo")));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>CurrencyDefinitionResponse currencyDefResponse = new CurrencyDefinitionResponse();</li>
		<li>CurrencyDefinition currencyDefinition = null;</li>
		<li>List<CurrencyDefinition> listCurrencyDef = new ArrayList<CurrencyDefinition>();</li>
	    
	    <li>currencyDefinition = new CurrencyDefinition();</li>
	    <li>currencyDefinition.setCode(0);</li>
	    <li>currencyDefinition.setDescription("COLON");</li>
	    <li>currencyDefinition.setSimbol("C");</li>

	    <li>listCurrencyDef.add(currencyDefinition);</li>
	    
	    <li>currencyDefinition = new CurrencyDefinition();</li>
	    <li>currencyDefinition.setCode(1);</li>
	    <li>currencyDefinition.setDescription("DOLAR");</li>
	    <li>currencyDefinition.setSimbol("$");</li>

	    <li>listCurrencyDef.add(currencyDefinition);</li>
	    
	    <li>currencyDefinition = new CurrencyDefinition();</li>
	    <li>currencyDefinition.setCode(4);</li>
	    <li>currencyDefinition.setDescription("YEN");</li>
	    <li>currencyDefinition.setSimbol("*");</li>

	    <li>listCurrencyDef.add(currencyDefinition);</li>
	    
	    <li>currencyDefResponse.setListCurrencyDef(listCurrencyDef);</li>
	    <li>currencyDefResponse.setReturnCode(0);</li>
	</ul>
	*/
	public CurrencyDefinitionResponse getCurrencyHelp(CurrencyDefinitionRequest currencyDefinitionRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
