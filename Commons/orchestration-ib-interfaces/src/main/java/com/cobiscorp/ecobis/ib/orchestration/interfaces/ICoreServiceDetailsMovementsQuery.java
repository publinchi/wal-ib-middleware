package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsResponse;
/**
 * This interface contains the methods needed to perform basic tasks transfers.
 * 
 * @since Jul 30, 2014
 * @author gcondo
 * @version 1.0.0
 *
 */




public interface ICoreServiceDetailsMovementsQuery {
	/**
	 * 
	 * 
	 *   <b>Consulta detalle de movimiento cuentas de ahorros.</b> 
	 *   
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
	    <li>AccountStatement accountStatement = new AccountStatement();</li>
	    <li>DetailsMovementsRequest detailsMovementsRequest = new DetailsMovementsRequest();</li>
	    
	    <li>EnquiryRequest enquiryRequest = new EnquiryRequest();</li>
	    <li>enquiryRequest.setProductId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_prod"))));</li>
	    <li>enquiryRequest.setCurrencyId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_mon"))));</li>
	    <li>enquiryRequest.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
	    
	    <li>enquiryRequest.setTransactionDate(aRequest.readValueParam("@i_fecha_trn"));</li>
	    <li>enquiryRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
	    <li>enquiryRequest.setDateFormatId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha"))));</li>
	    
	    <li>accountStatement.setSequential(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_ssn"))));</li>
	    <li>accountStatement.setAlternateCode(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_alt"))));</li>
	    <li>accountStatement.setOperationType(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_trn"))));</li>
	    
	    <li>detailsMovementsRequest.setwAccountStatement(accountStatement);</li>
	    <li>detailsMovementsRequest.setwEnquiryRequest(enquiryRequest);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>AccountStatement wAccountStatement=new AccountStatement();</li>
		<li>wAccountStatement.setAccount("10410108275249013");</li>
		<li>wAccountStatement.setTransactionDate("25/10/2013");</li>
		<li>wAccountStatement.setHour("15:16:00");</li>
		<li>wAccountStatement.setDescription("PAGO ITBMS CTAAHO Dummy");</li>
		<li>wAccountStatement.setConcept("Prueba CTAAHO-Dummy");</li>
		<li>wAccountStatement.setCause("Movimiento realizado en ventanilla CTAAHO");</li>
		<li>wAccountStatement.setAmount(25.000);</li>
		<li>wAccountStatement.setDocumentNumber(655970403);</li>
		<li>wAccountStatement.setTypeDC("Debito Dummy");</li>
		<li>wAccountStatement.setOffice("BANCA VIRTUAL-Dummy");</li>
		<li>wAccountStatement.setOwnChecksBalance(0.0000);</li>
		<li>wAccountStatement.setLocalChecksBalance(0.0000);</li>
		<li>wAccountStatement.setInternationalCheckBookBalance(0.0000);</li>
		<li>wAccountStatement.setCauseId("106");</li>
		<li>AccountStatementsCollection.add(wAccountStatement);</li>
		<li>detailsMovementsResponse.setAccountStatementsCollection(AccountStatementsCollection);</li>
		<li>detailsMovementsResponse.setMessages(messages);</li>
		<li>detailsMovementsResponse.setReturnCode(aProcedureResponse.getReturnCode());</li>

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
	DetailsMovementsResponse getMovementsDetailSavingAccount(DetailsMovementsRequest detailMovementsRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 *   <b>Consulta detalle de movimiento cuentas corrientes.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
	    <li>AccountStatement accountStatement = new AccountStatement();</li>
	    <li>DetailsMovementsRequest detailsMovementsRequest = new DetailsMovementsRequest();</li>
	    
	    <li>EnquiryRequest enquiryRequest = new EnquiryRequest();</li>
	    <li>enquiryRequest.setProductId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_prod"))));</li>
	    <li>enquiryRequest.setCurrencyId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_mon"))));</li>
	    <li>enquiryRequest.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
	    
	    <li>enquiryRequest.setTransactionDate(aRequest.readValueParam("@i_fecha_trn"));</li>
	    <li>enquiryRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
	    <li>enquiryRequest.setDateFormatId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha"))));</li>
	    
	    <li>accountStatement.setSequential(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_ssn"))));</li>
	    <li>accountStatement.setAlternateCode(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_alt"))));</li>
	    <li>accountStatement.setOperationType(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_trn"))));</li>
	    
	    <li>detailsMovementsRequest.setwAccountStatement(accountStatement);</li>
	    <li>detailsMovementsRequest.setwEnquiryRequest(enquiryRequest);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>AccountStatement wAccountStatement=new AccountStatement();</li>
		<li>wAccountStatement.setAccount("10410108275249013");</li>
		<li>wAccountStatement.setTransactionDate("25/10/2013");</li>
		<li>wAccountStatement.setHour("15:16:00");</li>
		<li>wAccountStatement.setDescription("PAGO ITBMS CTAAHO Dummy");</li>
		<li>wAccountStatement.setConcept("Prueba CTAAHO-Dummy");</li>
		<li>wAccountStatement.setCause("Movimiento realizado en ventanilla CTAAHO");</li>
		<li>wAccountStatement.setAmount(25.000);</li>
		<li>wAccountStatement.setDocumentNumber(655970403);</li>
		<li>wAccountStatement.setTypeDC("Debito Dummy");</li>
		<li>wAccountStatement.setOffice("BANCA VIRTUAL-Dummy");</li>
		<li>wAccountStatement.setOwnChecksBalance(0.0000);</li>
		<li>wAccountStatement.setLocalChecksBalance(0.0000);</li>
		<li>wAccountStatement.setInternationalCheckBookBalance(0.0000);</li>
		<li>wAccountStatement.setCauseId("106");</li>
		<li>AccountStatementsCollection.add(wAccountStatement);</li>
		<li>detailsMovementsResponse.setAccountStatementsCollection(AccountStatementsCollection);</li>
		<li>detailsMovementsResponse.setMessages(messages);</li>
		<li>detailsMovementsResponse.setReturnCode(aProcedureResponse.getReturnCode());</li>

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
	DetailsMovementsResponse getMovementsDetailCheckingAccount(DetailsMovementsRequest detailMovementsRequest) throws CTSServiceException, CTSInfrastructureException;

}
