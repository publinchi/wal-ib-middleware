package com.cobiscorp.ecobis.ib.orchestration.interfaces;



import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ReceivingPrintingArchiveRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReceivingPrintingArchiveResponse;



/** 
 * Esta interfaz contiene metodos necesarios para obtener informacion de los subtipos
 * para la Boleta de Garantia.
 * 
 * */
public interface ICoreServiceReceivingPrintingArchive {
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta  de Subtipos Boleta Garantia</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>String condition  = ""</li>
		<li>condition = aRequest.readValueParam("@i_condition");</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>SubTypeBankGuarantee aSubTypeBankGuarantee = new SubTypeBankGuarantee();</li>
		<li>List<SubTypeBankGuarantee> aSubTypeBankGuaranteeCollection = new ArrayList<SubTypeBankGuarantee>(); </li>
		<li>BankGuaranteeResponse aBankGuaranteeResponse = new BankGuaranteeResponse();</li>
		<li>aSubTypeBankGuarantee.setId("BGA1");</li>
		<li>aSubTypeBankGuarantee.setvalue("Boleta de Garantía 1");</li>
			
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>		
	**/
	public ReceivingPrintingArchiveResponse getBatchProcessing(ReceivingPrintingArchiveRequest aReceivingPrintingArchiveRequest) throws CTSServiceException, CTSInfrastructureException;
	public InventoryLotResponse getGenerateBatch(InventoryLotRequest aInventarioLoteRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
}
