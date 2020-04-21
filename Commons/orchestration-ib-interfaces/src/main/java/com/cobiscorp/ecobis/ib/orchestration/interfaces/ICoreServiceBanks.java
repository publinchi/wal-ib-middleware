package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BankResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankResponse;

/**
 *
 * @author eortega
 * @since Sep 17, 2014
 *
 */
public interface ICoreServiceBanks {

	/**
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>BankRequest <b>bankRequest</b> = new BankRequest();</li>
		<li>Bank banco = new Bank();</li>
		<li>bankRequest.setDescripcionBanco(wOriginalRequest.readValueParam("@i_banco"));</li>
		<li>banco.setId(Integer.parseInt(wOriginalRequest.readValueParam("@i_pais")));</li>
		<li>bankRequest.setModo(Integer.parseInt(wOriginalRequest.readValueParam("@i_modo")));</li>
		<li>bankRequest.setBanco(banco);</li>
		<li>bankRequest.setOriginalRequest(wOriginalRequest);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>BankResponse <b>wBankResponse</b> = new BankResponse();</li>
		<li>List<Bank> bankCollection = new ArrayList<Bank>();</li>

		<li>Bank bank = new Bank();</li>
		<li>bank.setId(1);</li>
		<li>bank.setDescription("BANK DUMMY1");</li>
		<li>bank.setBancoEnte("12345");</li>
		<li>bank.setConvenio("N");</li>
		<li>bankCollection.add(bank);</li>

		<li>bank = new Bank();</li>
		<li>bank.setId(2);</li>
		<li>bank.setDescription("BANK DUMMY2");</li>
		<li>bank.setBancoEnte("12345");</li>
		<li>bank.setConvenio("N");</li>
		<li>bankCollection.add(bank);</li>

		<li>bank = new Bank();</li>
		<li>bank.setId(3);</li>
		<li>bank.setDescription("BANK DUMMY3");</li>
		<li>bank.setBancoEnte("12345");</li>
		<li>bank.setConvenio("N");</li>
		<li>bankCollection.add(bank);</li>

		<li>wBankResponse.setBankCollection(bankCollection);</li>
		<li>wBankResponse.setSuccess(true);</li>
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
	BankResponse executeGetBanksByCountry(BankRequest bankRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>OfficeBankRequest <b>officeBankRequest</b> = new OfficeBankRequest();</li>
		<li>officeBankRequest.setCodeTransactionalIdentifier(request.readValueParam("@t_trn"));</li>
		<li>Office office = new Office();</li>
		<li>office.setSubtype(request.readValueParam("@i_swift_code"));</li>
		<li>office.setCode(request.readValueParam("@i_code_type"));</li>

		<li>officeBankRequest.setOffice(office);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>OfficeBankResponse <b>officeBankResponse</b> = new OfficeBankResponse();</li>
		<li>Bank bank = new Bank();</li>
		<li>bank.setId(1);</li>
		<li>bank.setDescription("BANCO TEST");</li>

		<li>Office office = new Office();</li>
		<li>office.setId(1);</li>
		<li>office.setDescription("OFICINA TEST");</li>
		<li>office.setCode(bankRequest.getOffice().getCode());</li>

		<li>Country country = new Country();</li>
		<li>country.setCode(1);</li>
		<li>country.setName("COUNTRY TEST");</li>

		<li>OfficeBankInformation officeBankInformation = new OfficeBankInformation();</li>
		<li>officeBankInformation.setBank(bank);</li>
		<li>officeBankInformation.setCountry(country);</li>
		<li>officeBankInformation.setOffice(office);</li>

		<li>officeBankResponse.setOfficeBankInformation(officeBankInformation);</li>
		<li>officeBankResponse.setSuccess(true);</li>
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
	OfficeBankResponse getOfficeByCodeSWIFT(OfficeBankRequest bankRequest) throws CTSServiceException, CTSInfrastructureException;

	/**
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>OfficeBankRequest <b>officeBankRequest</b> = new OfficeBankRequest();</li>
		<li>officeBankRequest.setCodeTransactionalIdentifier(request.readValueParam("@t_trn"));</li>
		<li>Office office = new Office();</li>
		<li>office.setSubtype(request.readValueParam("@i_swift_code"));</li>
		<li>office.setCode(request.readValueParam("@i_code_type"));</li>

		<li>officeBankRequest.setOffice(office);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>OfficeBankResponse <b>officeBankResponse</b> = new OfficeBankResponse();</li>
		<li>Bank bank = new Bank();</li>
		<li>bank.setId(1);</li>
		<li>bank.setDescription("BANCO TEST");</li>

		<li>Office office = new Office();</li>
		<li>office.setId(1);</li>
		<li>office.setDescription("OFICINA TEST");</li>
		<li>office.setCode(bankRequest.getOffice().getCode());</li>

		<li>Country country = new Country();</li>
		<li>country.setCode(1);</li>
		<li>country.setName("COUNTRY TEST");</li>

		<li>OfficeBankInformation officeBankInformation = new OfficeBankInformation();</li>
		<li>officeBankInformation.setBank(bank);</li>
		<li>officeBankInformation.setCountry(country);</li>
		<li>officeBankInformation.setOffice(office);</li>

		<li>officeBankResponse.setOfficeBankInformation(officeBankInformation);</li>
		<li>officeBankResponse.setSuccess(true);</li>
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
	OfficeBankResponse getOfficeByCodeABA(OfficeBankRequest bankRequest) throws CTSServiceException, CTSInfrastructureException;
}
