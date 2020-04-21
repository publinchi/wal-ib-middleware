package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRespon;

/**
 * 
 * @author kmeza
 * 
 * Clase que se encarga de consultar el formato que poseen las cuentas ACH
 *
 */
public interface ICoreSerciceAchAccountFormat {
	
	/**
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>AchAccountFormatRequest aAchAccountFormatRequest = new AchAccountFormatRequest();</li>
		<li>aAchAccountFormatRequest.setId( Integer.parseInt(aRequest.readValueParam("@i_banco") ));</li>	
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>AchAccountFormat aAchAccountFormat = new AchAccountFormat();</li>
		<li>List<AchAccountFormat> achAccountFormatCollection = new ArrayList<AchAccountFormat>();</li>
		<li>AchAccountFormatRespon <b>aAchAccountFormatRespon</b>  = new AchAccountFormatRespon();</li>
		
		<li>aAchAccountFormat.setId(1);</li>
		<li>aAchAccountFormat.setDescription("BANCO TEST");</li>
		<li>aAchAccountFormat.setSubsidiary(1);</li>
		<li>aAchAccountFormat.setStatus("V");</li>
		<li>aAchAccountFormat.setAccountTypeId(3);</li>
		<li>aAchAccountFormat.setAccountType("CORRIENTES");</li>
		<li>aAchAccountFormat.setLengthAccount(16);</li>
		<li>achAccountFormatCollection.add(aAchAccountFormat);</li>
		<li>aAchAccountFormatRespon .setAchAccountFormatCollection(achAccountFormatCollection);</li>
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
	public AchAccountFormatRespon  getACHAcoountResponse (AchAccountFormatRequest aAchAccountFormatRequest) throws CTSServiceException, CTSInfrastructureException;

}
