/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.LoanOperationsTypeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.LoanSimulationRequest;

/**
 * @author mvelez
 *
 */
public interface ICoreServiceLoanSimulation {
	/**
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>LoanSimulationRequest loanSimulationReq = new LoanSimulationRequest();</li>
		<li>loanSimulationReq.setOperation(aRequest.readValueParam("@i_operacion"));</li>
		<li>loanSimulationReq.setAmmount(new Double(aRequest.readValueParam("@i_monto")));</li>
		<li>loanSimulationReq.setSector(aRequest.readValueParam("@i_sector"));</li>
		<li>loanSimulationReq.setOperation_type(aRequest.readValueParam("@i_toperacion"));</li>
		<li>loanSimulationReq.setCurrency_id(new Integer(aRequest.readValueParam("@i_moneda")));</li>
		<li>loanSimulationReq.setInitial_date(aRequest.readValueParam("@i_fecha_ini"));</li> 
		<li>loanSimulationReq.setCode(aRequest.readValueParam("@i_codigo"));</li>
		<li>loanSimulationReq.setAmortization_type(aRequest.readValueParam("@i_tipo_amortizacion"));</li>
		<li>loanSimulationReq.setCuota(new Integer(aRequest.readValueParam("@i_cuota")));</li>
		<li>loanSimulationReq.setTerm(new Integer(aRequest.readValueParam("@i_plazo")));</li>
		<li>loanSimulationReq.setEntity_type(aRequest.readValueParam("@i_tipo_ente"));</li>	       
     </ul>
	  <b>
	        @return
	       -ParametrosDeSalida
	   </b>
	      <ul>
			<li>LoanOperationsTypeResponse loanOperationsTypeResponse = new LoanOperationsTypeResponse()</li>
		      <!--
		      <li>aLoanStatement.setPaymentDate(columns[1].getValue());</li>
		      <li>aLoanStatement.setNormalInterest(Double.parseDouble(columns[3].getValue()));</li>
			  <li>aLoanStatement.setArrearsInterest(Double.parseDouble(columns[4].getValue()));</li>
			  <li>aLoanStatement.setAmount(Double.parseDouble(columns[7].getValue()));</li>
			  <li>aLoanStatement.setSequential(Integer.parseInt(columns[17].getValue()));</li>
			  <li>aLoanStatement.setPaymentType("A");</li>
			  <li>aloanStatementCollection.add(aLoanStatement);</li>
			  -->	
	    </ul>
	  <b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	 *
	 */
   public LoanOperationsTypeResponse GetLoans (LoanSimulationRequest loanSimulationRequest) throws CTSServiceException, CTSInfrastructureException;
}
