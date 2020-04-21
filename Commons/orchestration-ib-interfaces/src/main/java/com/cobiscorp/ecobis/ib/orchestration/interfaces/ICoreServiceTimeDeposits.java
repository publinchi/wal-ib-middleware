package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleResponse;

/**
 * 
 * @author promero
 *
 */
public interface ICoreServiceTimeDeposits {
	
	/**
	 * 
	 * 
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>TimeDepositRequest wTimeDepositRequest=new TimeDepositRequest();</li>
		<li>TimeDeposit wTimeDeposit=new TimeDeposit();</li>
		<li>Product wProduct=new Product();</li>
		<li>Currency wCurrency=new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>wTimeDeposit.setProduct(wProduct);</li>
		<li>wTimeDepositRequest.setTimeDeposit(wTimeDeposit);</li>
		<li>wTimeDepositRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wTimeDepositRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>wTimeDepositRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>wTimeDepositRequest.setOriginalRequest(request);</li>
		<li>wTimeDepositRequest.setCustomerCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_cliente_mis")));</li>
			
    </ul>
    <b>
        @return
        -ParametrosDeSalida-
    </b>
    <ul>
        <li>TimeDepositResponse aTimeDepositResponse = new TimeDepositResponse();</li>
		<li>TimeDeposit aTimeDeposit=new TimeDeposit();</li>
		<li>Product aProduct= new Product();</li>
		<li>aProduct.setProductNumber("01414024150");</li>
		<li>aTimeDeposit.setProduct(aProduct);</li>
		<li>aTimeDeposit.setOpeningDate("10/10/2012");</li>
		<li>aTimeDeposit.setExpirationDate("10/10/2015");</li>
		<li>aTimeDeposit.setAmount(50000.00);</li>
		<li>aTimeDeposit.setTotalRateEstimed(800.00);</li>
		<li>aTimeDeposit.setRate("1.5");</li>
		<li>aTimeDeposit.setTerm(12);</li>
		<li>aTimeDeposit.setAmountEstimed(45000.00);</li>
		<li>aTimeDeposit.setAutomaticRenewal("S");</li>
		<li>aTimeDeposit.setIsCompounded("S");</li>
		<li>aTimeDeposit.setFrecuencyOfPayment("S");</li>
		<li>aTimeDeposit.setAccountOfficer("0215478562");</li>
		<li>aTimeDeposit.setValueDate("");</li>
		<li>aTimeDeposit.setCalculationBase(1200);</li>
		<li>aTimeDeposit.getProduct().setProductNemonic("PFI");</li>
		<li>aTimeDeposit.getProduct().setProductAlias("");</li>
		<li>aTimeDepositResponse.setTimeDeposit(aTimeDeposit);</li>
		<li>aTimeDepositResponse.setReturnCode(0);</li>
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
	public TimeDepositResponse getTimeDepositDetail(TimeDepositRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * Devuelve los movimientos de los depositos a plazo fijo
	 * 
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>TimeDepositsMovementsRequest timeDeposMoveRequest = new TimeDepositsMovementsRequest();</li>
		<li>TimeDepositsMovements timeDeposMove = new TimeDepositsMovements();</li>
		<li>Bank bank = new Bank();</li>
		<li>Secuential secuential = new Secuential();</li>
		<li>Product wProduct=new Product();</li>
		<li>Currency wCurrency=new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>timeDeposMove.setProduct(wProduct);</li>
		<li>secuential.setSecuential(aRequest.readValueParam("@i_secuencia"));</li>
		<li>timeDeposMoveRequest.setBank(bank);</li>
		<li>timeDeposMoveRequest.setSecuential(secuential);</li>
		<li>timeDeposMoveRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>timeDeposMoveRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>timeDeposMoveRequest.setTimeDepositsMovements(timeDeposMove);</li>
    </ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
        <li>TimeDepositsMovements aTimeDeposit = new TimeDepositsMovements();</li>
		<li>List<TimeDepositsMovements> depositsMovements = new ArrayList<TimeDepositsMovements>();</li> 
		<li>TimeDepositsMovementsResponse wTimeDepositsResponse = new TimeDepositsMovementsResponse();</li>
		<li>aTimeDeposit.setDate("10/25/2013");</li>
		<li>aTimeDeposit.setTransactionName("APERTURA DE DEPOSITO - VUELTO");</li>
		<li>aTimeDeposit.setPayFormat("CHEQUE GERENCIA");</li>
		<li>aTimeDeposit.setCurrency("COLON");</li>
		<li>aTimeDeposit.setInternationalAmount(0.00);</li>
		<li>aTimeDeposit.setAmount(1048968.11);</li>
		<li>aTimeDeposit.setStatus("AUTORIZADA");</li>
		<li>aTimeDeposit.setSequence(1);</li>		
		<li>aTimeDeposit.setAccount("null");</li>
		<li>aTimeDeposit.setBeneficiary("NOMBRE COMPLETO 13036 ");</li>
		<li>aTimeDeposit.setValueDate("10/25/2013");</li>
		<li>aTimeDeposit.setTransactionNumber(14901);</li>
		<li>aTimeDeposit.setSubsequence(1);</li>
		<li>depositsMovements.add(aTimeDeposit);</li>
		<li>wTimeDepositsResponse.setDepositsMovements(depositsMovements);</li>
		<li>wTimeDepositsResponse.setReturnCode(0);</li>
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
	public TimeDepositsMovementsResponse getTimeDepositMovements(TimeDepositsMovementsRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * Return Historicals of Time Deposits
	 *
	 *  
    <b>
       @param
       -ParametrosDeEntrada
    </b>
    <ul>
        <li>TimeDepositsHistoricalsRequest timeDeposHistRequest = new TimeDepositsHistoricalsRequest();</li>
        <li>Currency wCurrency = new Currency();</li>
		<li>Product wProduct = new Product();</li>
		<li>Secuential secuential = new Secuential();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>secuential.setSecuential(aRequest.readValueParam("@i_secuencial"));</li>
		<li>timeDeposHistRequest.setSecuential(secuential);</li>
		<li>timeDeposHistRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>timeDeposHistRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>timeDeposHistRequest.setProduct(wProduct);
	</ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>
    <ul>
        <li>TimeDepositsHistoricalsResponse aTimeDepositsHistResponse = new TimeDepositsHistoricalsResponse();</li>
		<li>TimeDepositsHistoricals aTimeDepsitsHist = new TimeDepositsHistoricals();</li>
		<li>List<TimeDepositsHistoricals> listdepositsHistoricals = new ArrayList<TimeDepositsHistoricals>();</li> 
		<li>aTimeDepsitsHist.setSequence(1);</li>
		<li>aTimeDepsitsHist.setCoupon(100);</li>
		<li>aTimeDepsitsHist.setTransactionDate("02/02/2004");</li>
		<li>aTimeDepsitsHist.setTransactionCode(14901);</li>
		<li>aTimeDepsitsHist.setDescription("APERTURA DPF");</li>
		<li>aTimeDepsitsHist.setValue(402029.48);</li>
		<li>aTimeDepsitsHist.setObservation("MIGRACION A COBIS");</li>
		<li>aTimeDepsitsHist.setFunctionary("sa");</li>
		<li>aTimeDepsitsHist.setRate(0.0);</li>
		<li>listdepositsHistoricals.add(aTimeDepsitsHist);</li>
		<li>listdepositsHistoricals.add(aTimeDepsitsHist);</li>
		<li>aTimeDepositsHistResponse.setDepositsHistoricals(listdepositsHistoricals);</li>
		<li>aTimeDepositsHistResponse.setReturnCode(0);</li>
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
	public TimeDepositsHistoricalsResponse getTimeDepositHistoricals(TimeDepositsHistoricalsRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 * Obtiene Interes a Pagar de Dep&oacutesitos a Plazo Fijo
	 *
	<b>
       @param
       -ParametrosDeEntrada
    </b>
	<ul>
		<li>TimeDepositsPayableInterestsRequest timeDeposPayRequest = new TimeDepositsPayableInterestsRequest();</li>
		<li>Product wProduct=new Product();</li>
		<li>Secuential secuential = new Secuential();</li>				
		<li>Currency wCurrency=new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>secuential.setSecuential(aRequest.readValueParam("@i_cuota"));</li>
		<li>timeDeposPayRequest.setProductNumber(wProduct);</li>
		<li>timeDeposPayRequest.setSequential(secuential);</li>
		<li>timeDeposPayRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>timeDeposPayRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>timeDeposPayRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>TimeDepositsPayableInterestsResponse payableInterestsResponse = new  TimeDepositsPayableInterestsResponse();</li>
		<li>TimeDepositsPayableInterests payableInterests = new TimeDepositsPayableInterests();</li>
		<li>List<TimeDepositsPayableInterests> payableInterestsList = new ArrayList<TimeDepositsPayableInterests>();</li>
		<li>BalanceDetailPayment detailPayment = new BalanceDetailPayment();</li>
		<li>LoanAmortization loanAmortization = new LoanAmortization();</li>
		<li>Currency currency = new Currency();</li>
		<li>detailPayment.setExpirationDate("02/03/2013");</li>
		<li>detailPayment.setStatus("P");</li>
		<li>detailPayment.setInitialDate("02/02/2013");</li>
		<li>loanAmortization.setTax(218.49);</li>
		<li>currency.setCurrencyDescription("COLON");</li>
		<li>payableInterests.setPayNumber(1);</li>
		<li>payableInterests.setPrePrintNumber(0);</li>
		<li>payableInterests.setBalanceDetailPayment(detailPayment);</li>
		<li>payableInterests.setApproximateValue(2512.63);</li>
		<li>payableInterests.setValue(2731.12);</li>
		<li>payableInterests.setTax(loanAmortization);</li>
		<li>payableInterests.setDateBox("11/09/2013");</li>
		<li>payableInterests.setPrintNumber(0);</li>
		<li>payableInterests.setDetained("S");</li>
		<li>payableInterests.setCouponNumber("01414052458-00001");</li>
		<li>payableInterests.setCurrency(currency);</li>
		<li>payableInterestsList.add(payableInterests);</li>
		<li>payableInterestsResponse.setPayableInterests(payableInterestsList);</li>
		<li>payableInterestsResponse.setReturnCode(0);</li>
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
	public TimeDepositsPayableInterestsResponse getTimeDepositsPayableInterests(TimeDepositsPayableInterestsRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	/** 
	 * 
	 * 
	 * Obtiene  los pagos programados de los Dep&oacutesitos a Plazo Fijo
	 *
	<b>
       @param
       -ParametrosDeEntrada
    </b> 
	<ul>
		<li>TimeDepositsPaymentScheduleRequest timeDeposPaymentScheduleRequest = new TimeDepositsPaymentScheduleRequest();</li>
		<li>Secuential secuential = new Secuential();</li>
		<li>Product wProduct=new Product();</li>
		<li>Currency wCurrency=new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>secuential.setSecuential(aRequest.readValueParam("@i_cuota"));</li>
		<li>timeDeposPaymentScheduleRequest.setProduct(wProduct);</li>
		<li>timeDeposPaymentScheduleRequest.setSecuential(secuential);</li>
		<li>timeDeposPaymentScheduleRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
	</ul>
	<b>
        @return
        -ParametrosDeSalida-
    </b>
	<ul>
		<li>TimeDepositsPaymentSchedule aTimeDepositsPaymentSchedule = new TimeDepositsPaymentSchedule();</li>
		<li>List<TimeDepositsPaymentSchedule> depositsPaymentSchedule = new ArrayList<TimeDepositsPaymentSchedule>();</li> 
		<li>TimeDepositsPaymentScheduleResponse timeDepositsPaymentScheduleResponse = new TimeDepositsPaymentScheduleResponse();</li>
		<li>aTimeDepositsPaymentSchedule.setQuota(1);</li>
		<li>aTimeDepositsPaymentSchedule.setPaymentDate("02/03/2013");</li>
		<li>aTimeDepositsPaymentSchedule.setQuotaAmount(2731.12);</li>
		<li>aTimeDepositsPaymentSchedule.setEntity(5295);</li>
		<li>aTimeDepositsPaymentSchedule.setOperationDescription("EUGENIO ARAGON CARAZO ");</li>
		<li>aTimeDepositsPaymentSchedule.setAddressDescription("");</li>
		<li>aTimeDepositsPaymentSchedule.setOfficeName("SAN JOSE");</li>
		<li>aTimeDepositsPaymentSchedule.setBankNumberOperation("01414052458");</li>
		<li>aTimeDepositsPaymentSchedule.setDepositTypeDescription("CERT. PERSONA FISICA");</li>
		<li>aTimeDepositsPaymentSchedule.setAmount(402029.48);</li>
		<li>aTimeDepositsPaymentSchedule.setPaymentDescription("M");</li>
		<li>aTimeDepositsPaymentSchedule.setCurrency(0);</li>
		<li>aTimeDepositsPaymentSchedule.setRate(8.152);</li>
		<li>aTimeDepositsPaymentSchedule.setExpirationDate("03/02/2014");</li>
		<li>aTimeDepositsPaymentSchedule.setStatus("P");</li>
		<li>aTimeDepositsPaymentSchedule.setOperationDaysNumber(361);</li>
		<li>aTimeDepositsPaymentSchedule.setInsertDate("02/02/2013");</li>
		<li>aTimeDepositsPaymentSchedule.setQuotaValue("02/02/2013");</li>
		<li>aTimeDepositsPaymentSchedule.setQuotaDaysNumber(30);</li>
		<li>aTimeDepositsPaymentSchedule.setLastPaymentDate("02/02/2013");</li>
		<li>aTimeDepositsPaymentSchedule.setInterestEarned(0.0);</li>
		<li>depositsPaymentSchedule.add(aTimeDepositsPaymentSchedule);</li>
		<li>depositsPaymentSchedule.add(aTimeDepositsPaymentSchedule);</li>
		<li>timeDepositsPaymentScheduleResponse.setDepositsPaymentSchedule(depositsPaymentSchedule);</li>
		<li>timeDepositsPaymentScheduleResponse.setReturnCode(0);</li>
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
	public TimeDepositsPaymentScheduleResponse getTimeDepositsPaymentSchedule(TimeDepositsPaymentScheduleRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;

	/**
	 * 
	 * Consulta el detalle de los pagramados de los Dep&oacutesitos a Plazo Fijo
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>TimeDepositsPaymentScheduleRequest timeDeposPaymentScheduleRequest = new TimeDepositsPaymentScheduleRequest();</li>
		<li>Product wProduct=new Product();</li>
		<li>Currency wCurrency=new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>timeDeposPaymentScheduleRequest.setProduct(wProduct);</li>
		<li>timeDeposPaymentScheduleRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>timeDepositsPaymentDetailScheduleRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
	    <li>timeDepositsPaymentDetailScheduleRequest.setNext(aRequest.readValueParam("@i_siguientes"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
	</b>
	<ul>
		<li>TimeDepositsPaymentDetailSchedule aTimeDepositsPaymentDetailSchedule = new TimeDepositsPaymentDetailSchedule();</li>
		<li>List<TimeDepositsPaymentDetailSchedule> depositsPaymentDetailSchedule = new ArrayList<TimeDepositsPaymentDetailSchedule>(); </li>
		<li>TimeDepositsPaymentDetailScheduleResponse timeDepositsPaymentDetailScheduleResponse = new TimeDepositsPaymentDetailScheduleResponse();</li>
		<li>aTimeDepositsPaymentDetailSchedule.setMonth(1);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setRate(8.152);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setCompounded("N");</li>
		<li>aTimeDepositsPaymentDetailSchedule.setPaymentType("PER");</li>
		<li>aTimeDepositsPaymentDetailSchedule.setPayDay(0);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setStatus("ACT");</li>
		<li>aTimeDepositsPaymentDetailSchedule.setBaseCalculate(360);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setEarnedInterest(0.00);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setAmountPaiedInterest(402029.48);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setExpirateDate("03/02/2014");</li>
		<li>aTimeDepositsPaymentDetailSchedule.setEntity(13036);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setValueDate("02/02/2013");</li>
		<li>aTimeDepositsPaymentDetailSchedule.setDaysNumber(361);</li>
		<li>aTimeDepositsPaymentDetailSchedule.setRealDays("LOCALES");</li>
		<li>depositsPaymentDetailSchedule.add(aTimeDepositsPaymentDetailSchedule);</li>
		<li>timeDepositsPaymentDetailScheduleResponse.setDepositsPaymentDetailSchedule(depositsPaymentDetailSchedule);</li>
		<li>timeDepositsPaymentDetailScheduleResponse.setReturnCode(0);</li>
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
	public TimeDepositsPaymentDetailScheduleResponse getTimeDepositsPaymentDetailSchedule(TimeDepositsPaymentDetailScheduleRequest timeDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	public TimeDepositCatalogResponse getTimeDepositCatalog(TimeDepositCatalogRequest timeDepositCatalogRequest)throws CTSServiceException, CTSInfrastructureException;
	
}
