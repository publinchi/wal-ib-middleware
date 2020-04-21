package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationSettingsResponse;

public interface ICoreServiceSimulatorSettings {
	
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>CertificateDepositCommonRequest certificateDepositCommonRequest=new CertificateDepositCommonRequest();</li>
		<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>
		<li>certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_money")));</li>		
		<li>certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_cd_nemonic"));</li>
		<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
		<li>certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
       <li>SimulationSettingsResponse simulationResponse = transformimulationSettingsResponse();</li>
       <li>SimulationSettings simulationSettings = new SimulationSettings();</li>
       <li>List<SimulationSettings> simulationSettings; = new ArrayList<SimulationSettings>();</li>
       <li>simulationSettings.setMinCd(1204.5);</li>
       <li>simulationSettings.setMaxDayCd(30);</li>
       <li>simulationSettings.setMinDayCd(5);</li>
       <li>simulationSettings.setMinLoan(280.75);</li>
       <li>simulationSettings.setMaxLoan(1000.5);</li>
       <li>simulationSettings.setMaxTermLoan(3);</li>
       <li>simulationSettings.setMaxTermSaving(2);</li>
       <li>simulationSettings.setMiMonPf(1.5);</li>
       <li>simulationSettings.setMinTermSaving(1.5);</li>
       <li>simulationSettings.add(simulationSettings);</li>
       <li>simulationResponse.setSimulationSettings(simulationSettings);</li>
       <li>simulationResponse.setSuccess(true);</li>
       <li>simulationResponse.setReturnCode(0);</li>
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

	public SimulationSettingsResponse getSimulatorsettings(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;	
	
}
