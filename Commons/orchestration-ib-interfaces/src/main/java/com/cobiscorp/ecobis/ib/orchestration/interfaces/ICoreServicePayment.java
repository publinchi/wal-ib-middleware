package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;
import java.util.Properties;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceResponse;

public interface ICoreServicePayment {
	
	/**  
	 * 
	 * 
	 * <b>Valida el saldo a pagar, cuando el tipo de convenio es BaseFacturacion('B').</b>
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>User inUser = new User();</li>
		<li>Product inProduct = new Product();</li>
		<li>PaymentServices inPaymentServices = new PaymentServices();</li>
		<li>TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();</li>
		<li>inTransactionContextCIB.setAuthenticationRequired("S");</li>
		<li>//datos del usuario</li>
		<li>inUser.setEntityId(CTSEnvironment.bvEnteMis);</li>
		<li>inUser.setName(CTSEnvironment.bvLogin);</li>
		<li>inUser.setServiceName("1");</li>
		<li>inProduct.setProductNumber("10410108275249013");</li>
		<li>inProduct.setCurrencyId(0);</li>
		<li>inProduct.setProductId(4);</li>
		<li>//datos del pago de servicio</li>
		<li>inPaymentServices.setAmount(1234.45);</li>
		<li>inPaymentServices.setContractName("CABLETICA");</li>
		<li>inPaymentServices.setContractId(60);</li>
		<li>inPaymentServices.setDocumentId("0106800940");</li>
		<li>inPaymentServices.setRef1("");</li>
		<li>inPaymentServices.setRef2("");</li>
		<li>inPaymentServices.setRef3("");</li>
		<li>inPaymentServices.setRef4("");</li>
		<li>inPaymentServices.setRef5("");</li>
		<li>inPaymentServices.setRef6("");</li>
		<li>inPaymentServices.setRef7("");</li>
		<li>inPaymentServices.setRef8("");</li>
		<li>inPaymentServices.setRef10("");</li>
		<li>inPaymentServices.setRef11("");</li>
		<li>inPaymentServices.setRef12("");</li>
		<li>inPaymentServices.setInvoicingBaseId(2);</li>
		<li>inPaymentServices.setInterface_type("B".charAt(0));</li>
		<li>inPaymentServices.setDocumentType("1.1");</li>
		<li>inPaymentServices.setThridPartyServiceKey("1");</li>
		<li>inPaymentServices.setNeedsQuery("N");</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IProcedureResponse iProcedureResponse = new IProcedureResponse ();</li>
		<li>iProcedureResponse.setReturnCode(0);</li>
		<li>iProcedureResponse.setText("Coment");</li>
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
	IProcedureResponse validationAmmount(PaymentServiceRequest aPaymentServiceRequest)throws CTSServiceException, CTSInfrastructureException;
	
	
	
	
	/** 
	 * 
	 *  
	 *  <b>Registra el pago del servicio.</b>
	 *  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>User inUser = new User();</li>
		<li>Product inProduct = new Product();</li>
		<li>PaymentServices inPaymentServices = new PaymentServices();</li>
		<li>TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();</li>
		<li>inTransactionContextCIB.setAuthenticationRequired("S");</li>
		<li>//datos del usuario</li>
		<li>inUser.setEntityId(CTSEnvironment.bvEnteMis);</li>
		<li>inUser.setName(CTSEnvironment.bvLogin);</li>
		<li>inUser.setServiceName("1");</li>
		<li>inProduct.setProductNumber("10410108275249013");</li>
		<li>inProduct.setCurrencyId(0);</li>
		<li>inProduct.setProductId(4);</li>
		<li>//datos del pago de servicio</li>
		<li>inPaymentServices.setAmount(1234.45);</li>
		<li>inPaymentServices.setContractName("CABLETICA");</li>
		<li>inPaymentServices.setContractId(60);</li>
		<li>inPaymentServices.setDocumentId("0106800940");</li>
		<li>inPaymentServices.setRef1("");</li>
		<li>inPaymentServices.setRef2("");</li>
		<li>inPaymentServices.setRef3("");</li>
		<li>inPaymentServices.setRef4("");</li>
		<li>inPaymentServices.setRef5("");</li>
		<li>inPaymentServices.setRef6("");</li>
		<li>inPaymentServices.setRef7("");</li>
		<li>inPaymentServices.setRef8("");</li>
		<li>inPaymentServices.setRef10("");</li>
		<li>inPaymentServices.setRef11("");</li>
		<li>inPaymentServices.setRef12("");</li>
		<li>inPaymentServices.setInvoicingBaseId(2);</li>
		<li>inPaymentServices.setInterface_type("B".charAt(0));</li>
		<li>inPaymentServices.setDocumentType("1.1");</li>
		<li>inPaymentServices.setThridPartyServiceKey("1");</li>
		<li>inPaymentServices.setNeedsQuery("N");</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Message message = new Message();</li>
		<li>message.setCode("0");</li>
		<li>message.setDescription("ok");</li>
		
		<li>PaymentServiceResponse paymentServiceResponse =  new PaymentServiceResponse();</li>  
		<li>paymentServiceResponse.setAuthorizationRequired("N");</li>
		<li>paymentServiceResponse.setBranchSSN(660703894);</li>
		<li>paymentServiceResponse.setReference(012345);</li>
		
		<li>paymentServiceResponse.setChannelId("1");</li>
		<li>paymentServiceResponse.setMessage(message);</li>
		<li>paymentServiceResponse.setReturnCode(0000);</li>
		<li>paymentServiceResponse.setSessionIdCore("564651135");</li>
		<li>paymentServiceResponse.setSuccess(true);</li>
		<li>paymentServiceResponse.setSessionIdIB("151548458");</li>
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
	PaymentServiceResponse payService(PaymentServiceRequest aPaymentServiceRequest, Properties properties, Map<String, Object> aBagSPJavaOrchestration)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Pago del Servicio En Linea cuando el tipo de convenio es OnLine ('L') - Dummy.</b>
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>User inUser = new User();</li>
		<li>Product inProduct = new Product();</li>
		<li>PaymentServices inPaymentServices = new PaymentServices();</li>
		<li>TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();</li>
		<li>inTransactionContextCIB.setAuthenticationRequired("S");</li>
		<li>//datos del usuario</li>
		<li>inUser.setEntityId(CTSEnvironment.bvEnteMis);</li>
		<li>inUser.setName(CTSEnvironment.bvLogin);</li>
		<li>inUser.setServiceName("1");</li>
		<li>inProduct.setProductNumber("10410108275249013");</li>
		<li>inProduct.setCurrencyId(0);</li>
		<li>inProduct.setProductId(4);</li>
		<li>//datos del pago de servicio</li>
		<li>inPaymentServices.setAmount(1234.45);</li>
		<li>inPaymentServices.setContractName("CABLETICA");</li>
		<li>inPaymentServices.setContractId(60);</li>
		<li>inPaymentServices.setDocumentId("0106800940");</li>
		<li>inPaymentServices.setRef1("");</li>
		<li>inPaymentServices.setRef2("");</li>
		<li>inPaymentServices.setRef3("");</li>
		<li>inPaymentServices.setRef4("");</li>
		<li>inPaymentServices.setRef5("");</li>
		<li>inPaymentServices.setRef6("");</li>
		<li>inPaymentServices.setRef7("");</li>
		<li>inPaymentServices.setRef8("");</li>
		<li>inPaymentServices.setRef10("");</li>
		<li>inPaymentServices.setRef11("");</li>
		<li>inPaymentServices.setRef12("");</li>
		<li>inPaymentServices.setInvoicingBaseId(2);</li>
		<li>inPaymentServices.setInterface_type("B".charAt(0));</li>
		<li>inPaymentServices.setDocumentType("1.1");</li>
		<li>inPaymentServices.setThridPartyServiceKey("1");</li>
		<li>inPaymentServices.setNeedsQuery("N");</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<ul>
		<li>Message message = new Message();</li>
		<li>message.setCode("0");</li>
		<li>message.setDescription("ok");</li>
		
		<li>PaymentServiceResponse paymentServiceResponse =  new PaymentServiceResponse();</li>  
		<li>paymentServiceResponse.setAuthorizationRequired("N");</li>
		<li>paymentServiceResponse.setBranchSSN(660703894);</li>
		<li>paymentServiceResponse.setReference(012345);</li>
		
		<li>paymentServiceResponse.setChannelId("1");</li>
		<li>paymentServiceResponse.setMessage(message);</li>
		<li>paymentServiceResponse.setReturnCode(0000);</li>
		<li>paymentServiceResponse.setSessionIdCore("564651135");</li>
		<li>paymentServiceResponse.setSuccess(true);</li>
		<li>paymentServiceResponse.setSessionIdIB("151548458");</li>
	</ul>
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
	PaymentServiceResponse payOnline(PaymentServiceRequest aPaymentServiceRequest, Properties properties, Map<String, Object> aBagSPJavaOrchestration)throws CTSServiceException, CTSInfrastructureException;
	
	/**  
	 * 
	 * 
	 * <b>Ejecuta la implementacion para el pago del servicio.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>User inUser = new User();</li>
		<li>Product inProduct = new Product();</li>
		<li>PaymentServices inPaymentServices = new PaymentServices();</li>
		<li>TransactionContextCIB inTransactionContextCIB = new TransactionContextCIB();</li>
		<li>inTransactionContextCIB.setAuthenticationRequired("S");</li>
		<li>//datos del usuario</li>
		<li>inUser.setEntityId(CTSEnvironment.bvEnteMis);</li>
		<li>inUser.setName(CTSEnvironment.bvLogin);</li>
		<li>inUser.setServiceName("1");</li>
		<li>inProduct.setProductNumber("10410108275249013");</li>
		<li>inProduct.setCurrencyId(0);</li>
		<li>inProduct.setProductId(4);</li>
		<li>//datos del pago de servicio</li>
		<li>inPaymentServices.setAmount(1234.45);</li>
		<li>inPaymentServices.setContractName("CABLETICA");</li>
		<li>inPaymentServices.setContractId(60);</li>
		<li>inPaymentServices.setDocumentId("0106800940");</li>
		<li>inPaymentServices.setRef1("");</li>
		<li>inPaymentServices.setRef2("");</li>
		<li>inPaymentServices.setRef3("");</li>
		<li>inPaymentServices.setRef4("");</li>
		<li>inPaymentServices.setRef5("");</li>
		<li>inPaymentServices.setRef6("");</li>
		<li>inPaymentServices.setRef7("");</li>
		<li>inPaymentServices.setRef8("");</li>
		<li>inPaymentServices.setRef10("");</li>
		<li>inPaymentServices.setRef11("");</li>
		<li>inPaymentServices.setRef12("");</li>
		<li>inPaymentServices.setInvoicingBaseId(2);</li>
		<li>inPaymentServices.setInterface_type("B".charAt(0));</li>
		<li>inPaymentServices.setDocumentType("1.1");</li>
		<li>inPaymentServices.setThridPartyServiceKey("1");</li>
		<li>inPaymentServices.setNeedsQuery("N");</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>Message message = new Message();</li>
		<li>message.setCode("0");</li>
		<li>message.setDescription("ok");</li>
		
		<li>PaymentServiceResponse paymentServiceResponse =  new PaymentServiceResponse();</li>  
		<li>paymentServiceResponse.setAuthorizationRequired("N");</li>
		<li>paymentServiceResponse.setBranchSSN(660703894);</li>
		<li>paymentServiceResponse.setReference(012345);</li>
		
		<li>paymentServiceResponse.setChannelId("1");</li>
		<li>paymentServiceResponse.setMessage(message);</li>
		<li>paymentServiceResponse.setReturnCode(0000);</li>
		<li>paymentServiceResponse.setSessionIdCore("564651135");</li>
		<li>paymentServiceResponse.setSuccess(true);</li>
		<li>paymentServiceResponse.setSessionIdIB("151548458");</li>
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
	PaymentServiceResponse executePayService(PaymentServiceRequest aPaymentServiceRequest)throws CTSServiceException, CTSInfrastructureException;


 
 
}
