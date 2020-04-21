package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckResponse;

/**
 * 
 * @author gyagual
 *
 */
public interface ICoreServiceChecksQuery {
	
	/**
	 * 
	 *   
	 *   <b>Consulta cheques por rango o por chequera</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CheckRequest wCheckRequest = new CheckRequest();</li>
		<li>Product wProduct  = new Product();</li>
		<li>Currency wCurrency = new Currency();</li>
		<li>Check wCheck = new Check();</li>
		
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wCheck.setCheckNumber(aRequest.readValueParam("@i_chq"));</li>
		
		<li>wCheckRequest.setEjec(aRequest.readValueParam("@t_ejec"));</li>
		<li>wCheckRequest.setRty(aRequest.readValueParam("@t_rty"));</li>
		<li>wCheckRequest.setCodeTransactionalIdentifier("18328");</li>
		<li>wCheckRequest.setProductId(wProduct);</li>
		<li>wCheckRequest.setCurrency(wCurrency);</li>
		<li>wCheckRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wCheckRequest.setProductNumber(wProduct);</li>
		<li>wCheckRequest.setCriteria(aRequest.readValueParam("@i_opcion"));</li>
		<li>if (aRequest.readValueParam("@i_chq").toString().equals("0"))</li>
		<li>{</li>
		<il>
		<ul>
			<li>if (aRequest.readValueParam("@i_monto_ini") != null){</li>
				<ul><li>wCheckRequest.setInitialAmount(new BigDecimal(aRequest.readValueParam("@i_monto_ini")));</li></ul>
			<li>}</li>
			<li>if (aRequest.readValueParam("@i_monto_fin") != null){</li>
				<ul><li>wCheckRequest.setFinalAmount(new BigDecimal(aRequest.readValueParam("@i_monto_fin")));</li></ul>
			<li>}</li>
			<li>wCheckRequest.setStringInitialDate(aRequest.readValueParam("@i_fecha_ini"));</li>
			<li>wCheckRequest.setStringFinalDate(aRequest.readValueParam("@i_fecha_fin"));</li>
			<li>wCheckRequest.setInitialCheck(aRequest.readValueParam("@i_chq_ini"));</li>
			<li>wCheckRequest.setFinalCheck(aRequest.readValueParam("@i_chq_fin"));</li>
		</ul>
		</il>
		<li>}</li>
		<li>wCheckRequest.setStatusCheck(aRequest.readValueParam("@i_chq_estado"));</li>
		<li>wCheckRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>wCheckRequest.setCheckNumber(wCheck);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CheckResponse aCheckResponse = new CheckResponse();</li>
		<li>Check aCheck = null;</li>
		
		<li>aCheck = new Check();</li>
		<li>aCheck.setAmount(new BigDecimal(15000));</li>
		<li>aCheck.setCheckNumber("00" + var);</li>
		<li>aCheck.setDatePayment(String.valueOf(var) +"/01/2014");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("15:02");</li>
		<li>aCheck.setStatus("PREAUTORIZADO");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheckCollection.add(aCheck);</li>
		
		<li>aCheck = new Check();</li>
		<li>aCheck.setAmount(new BigDecimal(1000));</li>
		<li>aCheck.setCheckNumber("14");</li>
		<li>aCheck.setDatePayment("20/01/2014");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("12:10");</li>
		<li>aCheck.setStatus("PAGADO");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheckCollection.add(aCheck);</li>
		
		<li>aCheck = new Check();</li>
		<li>aCheck.setAmount(new BigDecimal(1100));</li>
		<li>aCheck.setCheckNumber("15");</li>
		<li>aCheck.setDatePayment("");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("12:20");</li>
		<li>aCheck.setStatus("VIGENTE");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheckCollection.add(aCheck);</li>
		
		<li>aCheck = new Check();</li>
		<li>aCheck.setAmount(new BigDecimal(1200));</li>
		<li>aCheck.setCheckNumber("16");</li>
		<li>aCheck.setDatePayment("20/01/2014");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("12:30");</li>
		<li>aCheck.setStatus("SUSPENDIDO");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheckCollection.add(aCheck);</li>
		
		<li>aCheckResponse.setReturnCode(0);</li>
		<li>aCheckResponse.setCheckCollection(aCheckCollection);</li>
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
	public CheckResponse getChecks(CheckRequest aCheckRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
	
	/**
	 * 
	 *   
	 *   <b>Consulta cheque por n&uacutemero.</b> 
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CheckRequest wCheckRequest = new CheckRequest();</li>
		<li>Product wProduct  = new Product();</li>
		<li>Currency wCurrency = new Currency();</li>
		<li>Check wCheck = new Check();</li>
		
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wCheck.setCheckNumber(aRequest.readValueParam("@i_chq"));</li>
		
		<li>wCheckRequest.setEjec(aRequest.readValueParam("@t_ejec"));</li>
		<li>wCheckRequest.setRty(aRequest.readValueParam("@t_rty"));</li>
		<li>wCheckRequest.setCodeTransactionalIdentifier("18328");</li>
		<li>wCheckRequest.setProductId(wProduct);</li>
		<li>wCheckRequest.setCurrency(wCurrency);</li>
		<li>wCheckRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wCheckRequest.setProductNumber(wProduct);</li>
		<li>wCheckRequest.setCriteria(aRequest.readValueParam("@i_opcion"));</li>
		<li>if (aRequest.readValueParam("@i_chq").toString().equals("0"))</li>
		<li>{</li>
		<il>
		<ul>
			<li>if (aRequest.readValueParam("@i_monto_ini") != null){</li>
				<ul><li>wCheckRequest.setInitialAmount(new BigDecimal(aRequest.readValueParam("@i_monto_ini")));</li></ul>
			<li>}</li>
			<li>if (aRequest.readValueParam("@i_monto_fin") != null){</li>
				<ul><li>wCheckRequest.setFinalAmount(new BigDecimal(aRequest.readValueParam("@i_monto_fin")));</li></ul>
			<li>}</li>
			<li>wCheckRequest.setStringInitialDate(aRequest.readValueParam("@i_fecha_ini"));</li>
			<li>wCheckRequest.setStringFinalDate(aRequest.readValueParam("@i_fecha_fin"));</li>
			<li>wCheckRequest.setInitialCheck(aRequest.readValueParam("@i_chq_ini"));</li>
			<li>wCheckRequest.setFinalCheck(aRequest.readValueParam("@i_chq_fin"));</li>
		</ul>
		</il>
		<li>}</li>
		<li>wCheckRequest.setStatusCheck(aRequest.readValueParam("@i_chq_estado"));</li>
		<li>wCheckRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
		<li>wCheckRequest.setCheckNumber(wCheck);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Check aCheck = new Check();</li>
		<li>List<Check> aCheckCollection = new ArrayList<Check>(); </li>
		<li>CheckResponse aCheckResponse = new CheckResponse();</li>
		
		<li>aCheck.setAmount(new BigDecimal(8900));</li>
		<li>aCheck.setCheckNumber(aCheckRequest.getCheckNumber().getCheckNumber());</li>
		<li>aCheck.setDatePayment("10/04/2014");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("10/05/2014");</li>
		<li>aCheck.setStatusId("A");</li>
		<li>aCheck.setStatus("PREAUTORIZADO");</li>
		<li>aCheck.setBeneficiary("DANNA SAENZ");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheck.setDescriptionOffice("OFICINA MATRIZ");</li>
		<li>aCheck.setNameAccount("DANNA SAENZ YAGUAL");</li>
		
		<li>aCheckCollection.add(aCheck);</li>
		<li>aCheckResponse.setReturnCode(0);</li>
		<li>aCheckResponse.setCheckCollection(aCheckCollection);</li>
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
	public CheckResponse getChecksbyNumber(CheckRequest aCheckRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Valida el estado de los cheques</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CheckRequest wCheckRequest = new CheckRequest();</li>
		<li>Product wProduct  = new Product();</li>
		<li>Check wCheck = new Check();</li>
		
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>wCheck.setCheckNumber(aRequest.readValueParam("@i_desde"));</li>
		<li>wCheck.setStatus(aRequest.readValueParam("@i_chq_estado"));</li>
		<li>wCheckRequest.setFinalCheck(aRequest.readValueParam("@i_num_cheque"));</li>
		<li>wCheckRequest.setProductId(wProduct);</li>
		<li>wCheckRequest.setCheckNumber(wCheck);</li>

	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CheckResponse aCheckResponse = new CheckResponse();</li>
		<li>Check aCheck = null;</li>
		
		<li>aCheck = new Check();</li>
		<li>aCheck.setAmount(new BigDecimal(15000));</li>
		<li>aCheck.setCheckNumber("00" + var);</li>
		<li>aCheck.setDatePayment(String.valueOf(var) +"/01/2014");</li>
		<li>aCheck.setOfficePayment(4);</li>
		<li>aCheck.setHour("15:02");</li>
		<li>aCheck.setStatus("PREAUTORIZADO");</li>
		<li>aCheck.setUserName("usuariobv");</li>
		<li>aCheckCollection.add(aCheck);</li>
		
		
		<li>aCheckResponse.setReturnCode(0);</li>
		<li>aCheckResponse.setCheckCollection(aCheckCollection);</li>
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
	public CheckResponse validateCheckStatus(CheckRequest aCheckRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
}
