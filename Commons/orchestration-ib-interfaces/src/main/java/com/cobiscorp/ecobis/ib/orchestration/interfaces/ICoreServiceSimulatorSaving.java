package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSavingResponse;



public interface ICoreServiceSimulatorSaving {
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationSavingRequest simulationSavingRequest= new SimulationSavingRequest();</li>
		<li>SimulationSaving simulationSaving = new SimulationSaving();</li>
		<li>simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
		<li>simulationSavingRequest.setSimulationSaving(simulationSaving);</li>
		<li>simulationSavingRequest.setEntityType(wOriginalRequest.readValueParam("@i_tipo_ente"));</li>
		<li>simulationSavingRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
       @return
        -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationSavingResponse savingResponse = new SimulationSavingResponse();</li>
		<li>SimulationSaving simulationSaving = new SimulationSaving();</li>
		<li>simulationSaving.setCode(1);</li>
		<li>simulationSaving.setDescription("Regular");</li>
		<li>simulationSaving.setCategory("A");</li>
		<li>savingResponse.setSimulationSaving(simulationSaving);</li>
		<li>savingResponse.setReturnCode(0);</li>
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

	public SimulationSavingResponse getSimulationSaving(SimulationSavingRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationSavingRequest simulationSavingRequest= new SimulationSavingRequest();</li>
		<li>SimulationSaving simulationSaving= new SimulationSaving();</li>
		<li>simulationSaving.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));</li>
		<li>simulationSaving.setInitialAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_ini")));</li>
		<li>simulationSaving.setEntityType(wOriginalRequest.readValueParam("@i_tipocta"));</li>
		<li>simulationSaving.setCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod_banc")));</li>
		<li>simulationSaving.setCategory(wOriginalRequest.readValueParam("@i_categoria"));</li>
		<li>simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
		<li>simulationSavingRequest.setSimulationSaving(simulationSaving);</li>
		<li>simulationSavingRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationSavingResponse savingResponse = new SimulationSavingResponse();</li>
		<li>SimulationSaving simulationSaving = new SimulationSaving();</li>
		<li>simulationSaving.setMaxAmount(10000.00);</li>
		<li>savingResponse.setSimulationSaving(simulationSaving);</li>
		<li>savingResponse.setReturnCode(0);</li>
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

	public SimulationSavingResponse getSimulationMaxSaving(SimulationSavingRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationSavingRequest simulationSavingRequest= new SimulationSavingRequest();</li>
		<li>SimulationSaving simulationSaving= new SimulationSaving();</li>
		<li>simulationSaving.setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));</li>
		<li>simulationSaving.setInitialAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_ini")));</li>
		<li>simulationSaving.setEntityType(wOriginalRequest.readValueParam("@i_tipocta"));</li>
		<li>simulationSaving.setCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod_banc")));</li>
		<li>simulationSaving.setCategory(wOriginalRequest.readValueParam("@i_categoria"));</li>
		<li>simulationSaving.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_periodo")));</li>
		<li>simulationSaving.setFinalAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto_aprox")));</li>
		<li>simulationSaving.setOperationType(wOriginalRequest.readValueParam("@i_operacion"));</li>
		<li>simulationSavingRequest.setSimulationSaving(simulationSaving);</li>
		<li>simulationSavingRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationSavingResponse savingResponse = new SimulationSavingResponse();</li>
		<li>SimulationSaving simulationSaving = new SimulationSaving();</li>
		<li>simulationSaving.setFinalAmount(9037963.82);</li>
		<li>simulationSaving.setRate(1.0);</li>
		<li>simulationSaving.setTerm(9);</li>
		<li>savingResponse.setSimulationSaving(simulationSaving);</li>
		<li>savingResponse.setReturnCode(0);</li>
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

	public SimulationSavingResponse getSimulationExecuteSaving(SimulationSavingRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;

}
