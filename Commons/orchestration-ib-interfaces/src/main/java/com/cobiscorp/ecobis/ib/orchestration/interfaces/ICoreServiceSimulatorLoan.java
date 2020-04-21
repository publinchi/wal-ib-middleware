package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationLoanResponse;



public interface ICoreServiceSimulatorLoan {
	/**  
    <b>
        @param
        -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationLoanRequest simulationLoanRequest= new SimulationLoanRequest();</li>
        <li>SimulationLoan simulationLoan = new SimulationLoan();</li>
        <li>simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
        <li>simulationLoanRequest.setSimulationLoan(simulationLoan);</li>
        <li>simulationLoanRequest.setEntityType(wOriginalRequest.readValueParam("@i_tipo_ente"));</li>
        <li>simulationLoanRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
        @return
        -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationLoanResponse loanResponse = new SimulationLoanResponse();</li>
        <li>SimuladorLoanItem loanItem = new SimuladorLoanItem();</li>
        <li>List<SimuladorLoanItem> listLoanItem = new ArrayList<SimuladorLoanItem>();</li>
        <li>loanItem.setOperationType("PCREFA");</li>
        <li>loanItem.setProductName("CONSUMO PERSONAL CERO ESTRES");</li>
        <li>loanItem.setSector("BCON");</li>
        <li>listLoanItem.add(loanItem);</li>
        <li>loanResponse.setSimuladorLoanItem(listLoanItem);</li>
        <li>loanResponse.setReturnCode(0);</li>
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

	public SimulationLoanResponse getSimulationLoans(SimulationLoanRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
      @param
      -ParametrosDeEntrada
    </b>        
    <ul>
       <li>SimulationLoanRequest simulationLoanRequest= new SimulationLoanRequest();</li>
       <li>SimulationLoan simulationLoan= new SimulationLoan();</li>
       <li>simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>		
	   <li>simulationLoan.setCode(wOriginalRequest.readValueParam("@i_codigo"));</li>
	   <li>simulationLoanRequest.setSimulationLoan(simulationLoan);</li>
	   <li>simulationLoanRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
      @return
      -ParametrosDeSalida-
    </b>    
    <ul>
       <li>SimulationLoanResponse loanResponse = new SimulationLoanResponse();</li>
	   <li>SimulationLoan simLoan = new SimulationLoan();</li>
	   <li>List<SimulationLoan> listSimulationLoans = new ArrayList<SimulationLoan>();</li>
	   <li>SimuladorLoanItem simLoanItem = new SimuladorLoanItem();</li>
	   <li>List<SimuladorLoanItem> listLoanitem = new ArrayList<SimuladorLoanItem>();</li>
	   <li>simLoan.setPercentage(15.0);</li>
	   <li>listSimulationLoans.add(simLoan);</li>
	   <li>simLoanItem.setConcept("CAP");</li>
	   <li>simLoanItem.setDescription("CAPITAL");</li>
	   <li>simLoanItem.setItemType("C");</li>
	   <li>simLoanItem.setPercentage(0.0);</li>
	   <li>listLoanitem.add(simLoanItem);</li>
	   <li>loanResponse.setSimulationLoan(listSimulationLoans);</li>
	   <li>loanResponse.setSimuladorLoanItem(listLoanitem);</li>
	   <li>loanResponse.setReturnCode(0);</li>
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

	public SimulationLoanResponse getSimulationLoanItems(SimulationLoanRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
        @param
        -ParametrosDeEntrada
    </b>        
    <ul>
      <li>SimulationLoanRequest simulationLoanRequest= new SimulationLoanRequest();</li>
      <li>SimulationLoan simulationLoan= new SimulationLoan();</li>
      <li>simulationLoan.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));</li>
	  <li>simulationLoan.setSector(wOriginalRequest.readValueParam("@i_sector"));</li>
	  <li>simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
	  <li>simulationLoan.setOperation(wOriginalRequest.readValueParam("@i_toperacion"));</li>
	  <li>simulationLoan.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));</li>
	  <li>simulationLoan.setInicialDate(wOriginalRequest.readValueParam("@i_fecha_ini"));</li>
	  <li>simulationLoanRequest.setOriginalRequest(wOriginalRequest);</li>
	  <li>simulationLoanRequest.setSimulationLoan(simulationLoan);</li>
    </ul>
    <b>
      @return
      -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationLoanResponse loanResponse = new SimulationLoanResponse();</li>
		<li>SimulationLoan simLoan = new SimulationLoan();</li>
		<li>List<SimulationLoan> listSimulationLoans = new ArrayList<SimulationLoan>();</li>
		<li>simLoan.setCode("1940");</li>
		<li>listSimulationLoans.add(simLoan);</li>
		<li>loanResponse.setSimulationLoan(listSimulationLoans);</li>
		<li>loanResponse.setReturnCode(0);</li>
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

	public SimulationLoanResponse getSimulationLoanCreate(SimulationLoanRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
      @param
      -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationLoanRequest simulationLoanRequest= new SimulationLoanRequest();</li>
		<li>SimulationLoan simulationLoan= new SimulationLoan();</li>
		<li>simulationLoan.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
		<li>simulationLoan.setCode(wOriginalRequest.readValueParam("@i_codigo"));</li>
		<li>simulationLoan.setPayment(Double.parseDouble(wOriginalRequest.readValueParam("@i_cuota")));</li>
		<li>simulationLoan.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));</li>
		<li>simulationLoanRequest.setOriginalRequest(wOriginalRequest);</li>
		<li>simulationLoanRequest.setSimulationLoan(simulationLoan);</li>		
	</ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationLoanResponse loanResponse = new SimulationLoanResponse();</li>
		<li>SimulationLoan simLoan = new SimulationLoan();</li>
		<li>List<SimulationLoan> listSimulationLoans = new ArrayList<SimulationLoan>();</li>
		<li>simLoan.setEndDate("30/09/2012");</li>
		<li>simLoan.setAmount(3000.0);</li>
		<li>simLoan.setTerm(1);</li>
		<li>simLoan.setOperationType("M");</li>
		<li>simLoan.setOperation("MENSUAL");</li>
		<li>simLoan.setSector("1");</li>
		<li>listSimulationLoans.add(simLoan);</li>
		<li>loanResponse.setSimulationLoan(listSimulationLoans);</li>
		<li>loanResponse.setReturnCode(0);</li>
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

	public SimulationLoanResponse getSimulationLoanExecute(SimulationLoanRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;

}
