package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedResponse;
public interface ICoreServiceInternationalTransfersReceived {
	/**
	 * 
	 *   
	 *   <b>Consulta transferencias internacionales recibidas</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>InternationalTransfersReceivedRequest aInternationalTransfersReceivedRequest = new InternationalTransfersReceivedRequest();</li>		
		<li>aInternationalTransfersReceivedRequest.setOperation(aRequest.readValueParam("@i_opeban"));		</li>
		<li>aInternationalTransfersReceivedRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_fdate")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>InternationalTransfersReceived aInternationalTransfersReceived = new InternationalTransfersReceived();</li>
		<li>List<InternationalTransfersReceived> transfersReceivedList =  new ArrayList<InternationalTransfersReceived>();</li>
		<li>InternationalTransfersReceivedResponse aInternationalTransfersReceivedResponse = new InternationalTransfersReceivedResponse();</li>
		
		<li>logger.logInfo("INICIO ===================================>> searchInternationalTransfersReceived - Implementacion Desacoplada");</li>
		
		<li>aInternationalTransfersReceived.setOfficeNumber(1);</li>
		<li>aInternationalTransfersReceived.setOfficeDescription("SAN JOSE-CENTRAL 101");</li>
		<li>aInternationalTransfersReceived.setReference("042412295");</li>
		<li>aInternationalTransfersReceived.setConcept(" ");</li>
		<li>aInternationalTransfersReceived.setLastBeneficiary("CAPITAL FACTORING & FINANCE INC,");</li>
		<li>aInternationalTransfersReceived.setVerificationDate("13/02/2008");</li>
		<li>aInternationalTransfersReceived.setAgreement("N");</li>
		<li>aInternationalTransfersReceived.setAccountType("CTE");</li>
		<li>aInternationalTransfersReceived.setAccount("01202000052");</li>
		<li>aInternationalTransfersReceived.setPreliminaryAgreementDate(" ");</li>
		<li>aInternationalTransfersReceived.setOfficial(102);</li>
		<li>aInternationalTransfersReceived.setOfficialDescription(" ");</li>
		<li>aInternationalTransfersReceived.setCategory("N");</li>
		<li>aInternationalTransfersReceived.setCategoryDescription("NORMAL");</li>
		<li>aInternationalTransfersReceived.setOperationNumber(1);</li>
		<li>aInternationalTransfersReceived.setOperationDescription(" ");</li>
		<li>aInternationalTransfersReceived.setOfficeBelongNumber(1);</li>
		<li>aInternationalTransfersReceived.setOfficeName("SAN JOSE-CENTRAL 101");</li>
		<li>aInternationalTransfersReceived.setOriginatorNumber(108229);</li>
		<li>aInternationalTransfersReceived.setOriginatorName("COMPA-IA PRUEBA SA");</li>
		<li>aInternationalTransfersReceived.setIdNumber("1237732-0001-590383");</li>
		<li>aInternationalTransfersReceived.setOriginatorAddressNumber(1);</li>
		<li>aInternationalTransfersReceived.setOriginatorAddress("CALLE 2 545");</li>
		<li>aInternationalTransfersReceived.setBeneficiaryName("ALMACENES EXITO, S.A.");</li>
		<li>aInternationalTransfersReceived.setBeneficiaryAddress("MEDELLIN, COLOMBIA");</li>
		<li>aInternationalTransfersReceived.setContinent("AMS");</li>
		<li>aInternationalTransfersReceived.setBeneficiaryContinent("AMERICA DEL SUR");</li>
		<li>aInternationalTransfersReceived.setBeneficiaryCountryNumber(170);</li>
		<li>aInternationalTransfersReceived.setBeneficiaryCountryName("TRINIDAD Y TOBAGO");</li>
		<li>aInternationalTransfersReceived.setBeneficiaryCityNumber(2105);</li>
		<li>aInternationalTransfersReceived.setBeneficiaryCityName(" ");</li>
		<li>aInternationalTransfersReceived.setAmount(306526.00);</li>
		<li>aInternationalTransfersReceived.setCurrencyNumber(0);</li>
		<li>aInternationalTransfersReceived.setCurrencyName("COLON");</li>
		<li>aInternationalTransfersReceived.setPriority(" ");</li>
		<li>aInternationalTransfersReceived.setMessage(" ");</li>
		<li>aInternationalTransfersReceived.setMessageName(" ");</li>
		<li>aInternationalTransfersReceived.setIssueDate("13/02/2008");</li>
		<li>aInternationalTransfersReceived.setTerm(0);</li>
		<li>aInternationalTransfersReceived.setDueDate("13/02/2008");</li>
		<li>aInternationalTransfersReceived.setNotification("CORRESP PAGO FACT NO.255 CONTR  FACTORING NO.171-1");</li>
		<li>aInternationalTransfersReceived.setOnBehalfOfAddress("REM");</li>
		<li>aInternationalTransfersReceived.setLastShipmentDate("INFO");</li>
		<li>aInternationalTransfersReceived.setIssueReference("448561");</li>
		<li>aInternationalTransfersReceived.setTransactionDate("508861");	</li>
		<li>transfersReceivedList.add(aInternationalTransfersReceived);		</li>
		
		<li>aInternationalTransfersReceivedResponse.setInternationalTransfersReceivedCollection(transfersReceivedList);</li>
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
	InternationalTransfersReceivedResponse searchInternationalTransfersReceived(InternationalTransfersReceivedRequest internationalTransfersReceivedRequest) throws CTSServiceException, CTSInfrastructureException;	

}
