package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CdPeriodicityResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdSimulationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdTypeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;
import com.cobiscorp.ecobis.ib.application.dtos.DetailCdResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationResponse;

public interface ICoreServiceTimeDepositConfig {
	
	/**  
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
                   	<li>CertificateDeposit certificateDeposit = new CertificateDeposit();</li>
					<li>CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();</li>
					<li>Rate rate = new Rate();</li>
					<li>Entity entity = new Entity();</li>
		
					<li>rate.setRate(10.00);</li>
					<li>entity.setCodCustomer(277);</li>
					<li>certificateDeposit.setNemonic("CTACTE");</li>
		
					<li>if(method=="getCertificateDepositPeriodicity")</li>
					<ul>
						<li>certificateDeposit.setRegType("Prueba");</li>
					</ul>
					<li>if(method=="executeSimulation")</li>
					<ul>	
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setRate(rate);</li>
						<li>certificateDeposit.setMoney(0);</li>
						<li>certificateDeposit.setCategory("A");</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setPayDay(30);</li>
					</ul>
					<li>if (method=="getCertificateDepositTerm")</li>
					<ul>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setExpiration("mensual");</li>
						<li>certificateDeposit.setTermDate("31/01/2010");</li>
					</ul>
					<li>if (method=="getCertificateDepositRate")</li>
					<ul>
						<li>certificateDeposit.setOffice("1");</li>
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setMoney(0);</li>
					</ul>
					<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
					<li>certificateDepositCommonRequest.setEntity(entity);</li>


    </ul>
    <b>
                   @return
        			-ParametrosDeSalida-
    </b>    
    <ul>
                   
                   	<li>DetailCdResponse detailResponse= new DetailCdResponse();</li>
                   	<li>List<CertificateDeposit> listCertificateDeposit=new ArrayList<CertificateDeposit>();</li>
                   	<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>
					<li>IResultSetRow iResultSetRow =rowsSimulation[i];</li>
					<li>IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();</li>
				
					<li>certificateDeposit.setType(columns[COL_TYPE_DPF].getValue());</li>
					<li>certificateDeposit.setNemonic(columns[COL_DESCRIPTION1].getValue());</li>
					<li>certificateDeposit.setMethodOfPayment(columns[COL_PAY_WAY].getValue());</li>
					<li>certificateDeposit.setCapitalize(columns[COL_CAPITALIZE].getValue());</li>
					<li>certificateDeposit.setCalculationBase(columns[COL_CAL_BASE].getValue());</li>
					<li>certificateDeposit.setExtendedAut(columns[COL_AUTO_EXTENSION].getValue());</li>
					<li>certificateDeposit.setGraceDays(columns[COL_GRACE_DAYS].getValue());</li>
					<li>certificateDeposit.setGraceDaysNum(columns[COL_NUM_GRACE_DAYS].getValue());</li>
					<li>certificateDeposit.setTaxRetention(columns[COL_TAX_RETENTION].getValue());</li>
				
					<li>listCertificateDeposit.add(certificateDeposit);</li>
					<li>detailResponse.setListCertificateDeposit(listCertificateDeposit);</li>
					<li>detailResponse.setCertificateDepositResponse(new CertificateDepositResponse());</li>
					<li>detailResponse.getCertificateDepositResponse().setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));</li>
					<li>detailResponse.getCertificateDepositResponse().setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));</li>
					<li>detailResponse.setSuccess(true);</li>
					<li>detailResponse.setReturnCode(procedureResponse.getReturnCode());</li>
                   
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

	public DetailCdResponse getDetailCertificateDeposit(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	/**  
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
                   <li>CertificateDeposit certificateDeposit = new CertificateDeposit();</li>
					<li>CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();</li>
					<li>Rate rate = new Rate();</li>
					<li>Entity entity = new Entity();</li>
		
					<li>rate.setRate(10.00);</li>
					<li>entity.setCodCustomer(277);</li>
					<li>certificateDeposit.setNemonic("CTACTE");</li>
		
					<li>if(method=="getCertificateDepositPeriodicity")</li>
					<ul>
						<li>certificateDeposit.setRegType("Prueba");</li>
					</ul>
					<li>if(method=="executeSimulation")</li>
					<ul>	
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setRate(rate);</li>
						<li>certificateDeposit.setMoney(0);</li>
						<li>certificateDeposit.setCategory("A");</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setPayDay(30);</li>
					</ul>
					<li>if (method=="getCertificateDepositTerm")</li>
					<ul>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setExpiration("mensual");</li>
						<li>certificateDeposit.setTermDate("31/01/2010");</li>
					</ul>
					<li>if (method=="getCertificateDepositRate")</li>
					<ul>
						<li>certificateDeposit.setOffice("1");</li>
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setMoney(0);</li>
					</ul>
					<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
					<li>certificateDepositCommonRequest.setEntity(entity);</li>

    </ul>
    <b>
                   @return
        			-ParametrosDeSalida-
    </b>    
    <ul>
                   
                   	<li>CdTypeResponse cdTypeResponse=new CdTypeResponse();</li>
					<li>List<Type> listType=new ArrayList<Type>();</li>
					<li>List<Parameters> listParameters=new ArrayList<Parameters>();</li>
					<li>List<Category> listCategory=new ArrayList<Category>();</li>
					
					<li>if(procedureResponse.getReturnCode()==0)</li>
					<ul>	
						<li>Type type=new Type();</li>
						<li>type.setType(columns[COL_TYPE_DPF].getValue());</li>	
						<li>listType.add(type);</li>
					
						<li>Parameters parameter=new Parameters();</li>
						<li>parameter.setName(columns[COL_VALUE].getValue());</li>					
						<li>istParameters.add(parameter);</li>
					
						<li>Category category=new Category();</li>
						<li>category.setName(columns[COL_VALUE].getValue());</li>					
						<li>listCategory.add(category);</li>
					
						<li>cdTypeResponse.setListCategory(listCategory);</li>
						<li>cdTypeResponse.setListCdType(listType);</li>
						<li>cdTypeResponse.setListParameters(listParameters);</li>
			
						<li>cdTypeResponse.setCertificateDepositResponse(new CertificateDepositResponse());</li>
						<li>cdTypeResponse.getCertificateDepositResponse().setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));</li>
						<li>cdTypeResponse.getCertificateDepositResponse().setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));</li>
						<li>cdTypeResponse.setSuccess(true);</li>
					</ul>
					<li>else</li>	
					<ul>	
						<li>cdTypeResponse.setSuccess(false);</li>
					</ul>	
					<li>cdTypeResponse.setReturnCode(procedureResponse.getReturnCode());</li>
					<li>Message[] message = Utils.returnArrayMessage(procedureResponse);</li>
					<li>cdTypeResponse.setMessages(message);</li>
					
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

	public CdTypeResponse getCertificateDepositType(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	
	/**  
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
                   <li>CertificateDeposit certificateDeposit = new CertificateDeposit();</li>
					<li>CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();</li>
					<li>Rate rate = new Rate();</li>
					<li>Entity entity = new Entity();</li>
		
					<li>rate.setRate(10.00);</li>
					<li>entity.setCodCustomer(277);</li>
					<li>certificateDeposit.setNemonic("CTACTE");</li>
		
					<li>if(method=="getCertificateDepositPeriodicity")</li>
					<ul>
						<li>certificateDeposit.setRegType("Prueba");</li>
					</ul>
					<li>if(method=="executeSimulation")</li>
					<ul>	
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setRate(rate);</li>
						<li>certificateDeposit.setMoney(0);</li>
						<li>certificateDeposit.setCategory("A");</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setPayDay(30);</li>
					</ul>
					<li>if (method=="getCertificateDepositTerm")</li>
					<ul>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setProcessDate("01/01/2010");</li>
						<li>certificateDeposit.setExpiration("mensual");</li>
						<li>certificateDeposit.setTermDate("31/01/2010");</li>
					</ul>
					<li>if (method=="getCertificateDepositRate")</li>
					<ul>
						<li>certificateDeposit.setOffice("1");</li>
						<li>certificateDeposit.setAmount(100.00);</li>
						<li>certificateDeposit.setTerm(01);</li>
						<li>certificateDeposit.setMoney(0);</li>
					</ul>
					<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
					<li>certificateDepositCommonRequest.setEntity(entity);</li>

    </ul>
    <b>
                   @return
        			-ParametrosDeSalida-
    </b>    
    <ul>
                   
                   	<li>CdPeriodicityResponse cdPeriodicityResponse=new CdPeriodicityResponse();</li>
					<li>List<Periodicity> listPeriodicity=new ArrayList<Periodicity>();</li>
					<li>if(procedureResponse.getReturnCode()==0)</li>
					<ul>
						<li>Periodicity periodicity=new Periodicity();</li>
						<li>periodicity.setValue(columns[COL_VALUE].getValue());</li>
						<li>periodicity.setDescription(columns[COL_DESCRIPTION2].getValue());</li>
						<li>periodicity.setFactor(columns[COL_FACTOR].getValue());</li>
						<li>periodicity.setPercentage(columns[COL_PERCENTAJE].getValue());</li>
						<li>periodicity.setDaysFactor(columns[COL_DAY_FACTOR].getValue());</li>
				
						<li>listPeriodicity.add(periodicity);</li>
						
						<li>cdPeriodicityResponse.setListPeriodicity(listPeriodicity);</li>
			
						<li>cdPeriodicityResponse.setCertificateDepositResponse(new CertificateDepositResponse());</li>
						<li>cdPeriodicityResponse.getCertificateDepositResponse().setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));</li>
						<li>cdPeriodicityResponse.getCertificateDepositResponse().setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));</li>
						<li>cdPeriodicityResponse.setSuccess(true);</li>
					</ul>
					<li>else</li>
					<ul>	
						<li>cdPeriodicityResponse.setSuccess(false);</li>
					</ul>	
					<li>cdPeriodicityResponse.setReturnCode(procedureResponse.getReturnCode());</li>
					<li>Message[] message = Utils.returnArrayMessage(procedureResponse);</li>		
					<li>cdPeriodicityResponse.setMessages(message);</li>
					
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

	public CdPeriodicityResponse getCertificateDepositPeriodicity(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;

	/**  
    <b>
                   @param
                   -ParametrosDeEntrada
    </b>        
    <ul>
        <li>CertificateDepositCommonRequest certificateDepositCommonRequest=new CertificateDepositCommonRequest();</li>
		<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>
		<li>Rate rate=new Rate();</li>
		<li>Entity entity=new Entity();</li>
		<li>certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));</li>
		<li>certificateDeposit.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));</li>
		<li>certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));</li>
		<li>rate.setRate(Double.valueOf(wOriginalRequest.readValueParam("@i_tasa")));</li>
		<li>certificateDeposit.setRate(rate);</li>
		<li>certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));</li>
		<li>certificateDeposit.setCategory(wOriginalRequest.readValueParam("@i_categoria"));</li>
		<li>certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha_valor"));</li>
		<li>entity.setCodCustomer(Integer.parseInt(wOriginalRequest.readValueParam("@i_ente")));</li>
		<li>certificateDepositCommonRequest.setEntity(entity);</li>
		<li>certificateDeposit.setPayDay(Integer.parseInt(wOriginalRequest.readValueParam("@i_dia_pago")));</li>
		<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
		<li>certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
                   @return
        -ParametrosDeSalida-
    </b>    
    <ul>
        <li>CdSimulationResponse wSimulationResponse=new CdSimulationResponse();</li>
		<li>List<CertificateDepositResult> listCertificateDeposit=new ArrayList<CertificateDepositResult>();</li>
		<li>CertificateDepositResult wCertificateDeposit=new CertificateDepositResult();</li>
		<li>wCertificateDeposit.setInterestEstimated(11.11);</li>
		<li>wCertificateDeposit.setInterestEstimatedTotal(11.11);</li>
		<li>wCertificateDeposit.setInterestPayDay("14/12/2015");</li>
		<li>wCertificateDeposit.setNumberOfPayment(1);</li>
		<li>wCertificateDeposit.setRate(1.0);</li>
		<li>listCertificateDeposit.add(wCertificateDeposit);</li>
		<li>wSimulationResponse.setCertificateDepositResponse(new CertificateDepositResponse());</li>
		<li>wSimulationResponse.getCertificateDepositResponse().setTerm(0);</li>
		<li>wSimulationResponse.getCertificateDepositResponse().setExpirationDate(null);</li>
		<li>wSimulationResponse.setListCertificateDepositResult(listCertificateDeposit);</li>
		<li>wSimulationResponse.setReturnCode(0);</li>
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


	public CdSimulationResponse executeSimulation(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>CertificateDepositCommonRequest certificateDepositCommonRequest=new CertificateDepositCommonRequest();</li>
		<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>
		<li>certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));</li>
		<li>certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));</li>
		<li>certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha_valor"));</li>
		<li>certificateDeposit.setExpiration(wOriginalRequest.readValueParam("@i_fecha_ven"));</li>
		<li>certificateDeposit.setTermDate(wOriginalRequest.readValueParam("@i_fecha_plazo"));</li>
		<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
		<li>certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
        @return
        -ParametrosDeSalida-
    </b>    
    <ul>
        <li>CertificateDepositResponse wCertificateDepositResponse = new CertificateDepositResponse();</li>
        <li>wCertificateDepositResponse.setTerm(0);</li>
		<li>wCertificateDepositResponse.setExpirationDate("06/11/2014");</li>
		<li>wCertificateDepositResponse.setReturnCode(0);</li>
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

	public CertificateDepositResponse getCertificateDepositTerm(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>CertificateDepositCommonRequest certificateDepositCommonRequest=new CertificateDepositCommonRequest();</li>
		<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>
		<li>certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));</li>
		<li>certificateDeposit.setOffice(wOriginalRequest.readValueParam("@i_oficina"));</li>
		<li>certificateDeposit.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));</li>
		<li>certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));</li>
		<li>certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));</li>
		<li>certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);</li>
		<li>certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
       @return
       -ParametrosDeSalida-
    </b>    
    <ul>
        <li>CdRateResponse wCdRateResponse = new CdRateResponse();</li>
		<li>List<Rate> listRate=new ArrayList<Rate>();</li>
		<li>Rate wRate=new Rate();</li>
		<li>wRate.setRate(5.0);</li>
		<li>wRate.setMaxRate(5.5);</li>
		<li>wRate.setMinRate(5.0);</li>
		<li>wRate.setRateDesc("0.0");</li>
		<li>wRate.setRateAuthorization(null);</li>
		<li>listRate.add(wRate);</li>
		<li>wCdRateResponse.setListRate(listRate);</li>
		<li>wCdRateResponse.setCertificateDepositResponse(new CertificateDepositResponse());</li>
		<li>wCdRateResponse.getCertificateDepositResponse().setTerm(0);</li>
		<li>wCdRateResponse.getCertificateDepositResponse().setExpirationDate(null);</li>
		<li>wCdRateResponse.setReturnCode(0);</li>
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

	public CdRateResponse getCertificateDepositRate(CertificateDepositCommonRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	//public  ParameterByNameResponse getParameterByName(ParameterByNameRequest certificateDeposit)throws CTSServiceException, CTSInfrastructureException;
	/**  
    <b>
       @param
       -ParametrosDeEntrada
    </b>        
    <ul>
        <li>SimulationExpirationRequest simulationExpiration= new SimulationExpirationRequest();</li>
		<li>CertificateDeposit certificateDeposit=new CertificateDeposit();</li>	
		<li>certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_toperacion"));</li>
		<li>certificateDeposit.setCalendarDays(wOriginalRequest.readValueParam("@i_dias_reales"));</li>
		<li>certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));</li>
		<li>certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha"));</li>
		<li>simulationExpiration.setDateFormat(Integer.parseInt(wOriginalRequest.readValueParam("@i_formato_fecha")));</li>
		<li>simulationExpiration.setCertificateDeposit(certificateDeposit);</li>
		<li>simulationExpiration.setOriginalRequest(wOriginalRequest);</li>
    </ul>
    <b>
        @return
        -ParametrosDeSalida-
    </b>    
    <ul>
        <li>SimulationExpiration aSimulationExpiration = new SimulationExpiration();</li>
        <li>SimulationExpirationResponse expirationResponse = new SimulationExpirationResponse();</li>
        <li>aSimulationExpiration.setResult("A");</li>
		<li>aSimulationExpiration.setAdditionalDays(0);</li>
		<li>aSimulationExpiration.setProcessDate("11/01/2014");</li>
		<li>aSimulationExpiration.setExpirationDate("11/04/2014");</li>
		<li>aSimulationExpiration.setProcessDateHold("11/01/2014");</li>
		<li>aSimulationExpiration.setExpirationDateHold("11/04/2014");</li>
		<li>aSimulationExpiration.setTermHold(3);</li>
		<li>aSimulationExpiration.setNumberOfLaborsDays(0);</li>
		<li>expirationResponse.setExpirationDate("11/04/2014");</li>
		<li>expirationResponse.setSimulationExpiration(aSimulationExpiration);</li>
		<li>expirationResponse.setReturnCode(0);</li>
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

	public SimulationExpirationResponse getSimulationExpiration(SimulationExpirationRequest expirationRequest)throws CTSServiceException, CTSInfrastructureException;
	
}

