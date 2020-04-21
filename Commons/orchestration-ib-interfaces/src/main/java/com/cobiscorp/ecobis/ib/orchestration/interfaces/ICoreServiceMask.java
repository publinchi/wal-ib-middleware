/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationResponse;

/**
 * @author jveloz
 *
 */
public interface ICoreServiceMask {
	/**
	 * 
	 *   
	 *   <b>Consulta m&aacutescaras de tipos de identificaci&oaccuten  de cliente</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>IdentificationRequest identificationRequest=new IdentificationRequest();</li>
		<li>identificationRequest.setTypePerson(wOriginalRequest.readValueParam("@i_tpersona"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IdentificationResponse wIdentificationResponse = new IdentificationResponse();</li>
		<li>List<Identification> listIdentification = new ArrayList<Identification>();</li>
		<li>Identification wIdentification = new Identification();</li>
		<li>wIdentification.setType("1.1");</li>
		<li>wIdentification.setName("PERSONA FISICA NACIONAL");</li>
		<li>wIdentification.setMask("999999999999");</li>
		<li>wIdentification.setCustomerType("P");</li>
		<li>wIdentification.setProvinceValidate("N");</li>
		<li>wIdentification.setQuickOpening("S");</li>
		<li>wIdentification.setLockCustomer("N");</li>
		<li>wIdentification.setNationality("0");</li>
		<li>wIdentification.setCheckSum("N");</li>
		<li>listIdentification.add(wIdentification);</li>

		<li>wIdentificationResponse.setListIdentification(listIdentification);</li>
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
	public IdentificationResponse getMask(IdentificationRequest request) throws CTSServiceException, CTSInfrastructureException;
}
