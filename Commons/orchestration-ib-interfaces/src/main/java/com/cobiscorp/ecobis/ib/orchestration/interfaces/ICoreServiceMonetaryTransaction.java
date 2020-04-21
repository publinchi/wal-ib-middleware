/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.math.BigDecimal;
import java.util.Map;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 * @author cecheverria
 *
 */
public interface ICoreServiceMonetaryTransaction {
	
	/**
	 * 
	 *  
	 *  <b>Consulta par&aacutemetros contables.</b>
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>AccountingParameterRequest anAccountingParameterRequest = new AccountingParameterRequest();</li>
		<li>anAccountingParameterRequest.setTransaction(Integer.parseInt(request.readValueParam("@t_trn")));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>AccountingParameterResponse response = new AccountingParameterResponse();</li>
		<li>List<AccountingParameter> accountingParameters = new ArrayList<AccountingParameter>();</li>
		<li>AccountingParameter accountingParameter = null;</li>
		
		<li>//D&eacutebito</li>
		
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("123");</li>
		<li>accountingParameter.setProductId(3);</li>
		<li>accountingParameter.setService("SER");</li>
		<li>accountingParameter.setSign("D");</li>
		<li>accountingParameter.setTypeCost("T");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("456");</li>
		<li>accountingParameter.setProductId(4);</li>
		<li>accountingParameter.setService("SER");</li>
		<li>accountingParameter.setSign("D");</li>
		<li>accountingParameter.setTypeCost("T");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("789");</li>
		<li>accountingParameter.setProductId(3);</li>
		<li>accountingParameter.setService("COM");</li>
		<li>accountingParameter.setSign("D");</li>
		<li>accountingParameter.setTypeCost("C");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("012");</li>
		<li>accountingParameter.setProductId(4);</li>
		<li>accountingParameter.setService("COM");</li>
		<li>accountingParameter.setSign("D");</li>
		<li>accountingParameter.setTypeCost("C");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>

		<li>//Cr&eacutedito</li>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("123");</li>
		<li>accountingParameter.setProductId(3);</li>
		<li>accountingParameter.setService("SER");</li>
		<li>accountingParameter.setSign("C");</li>
		<li>accountingParameter.setTypeCost("T");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("456");</li>
		<li>accountingParameter.setProductId(4);</li>
		<li>accountingParameter.setService("SER");</li>
		<li>accountingParameter.setSign("C");</li>
		<li>accountingParameter.setTypeCost("T");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("789");</li>
		<li>accountingParameter.setProductId(3);</li>
		<li>accountingParameter.setService("COM");</li>
		<li>accountingParameter.setSign("C");</li>
		<li>accountingParameter.setTypeCost("C");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		</br>
		<li>accountingParameter = new AccountingParameter();</li>
		<li>accountingParameter.setCause("012");</li>
		<li>accountingParameter.setProductId(4);</li>
		<li>accountingParameter.setService("COM");</li>
		<li>accountingParameter.setSign("C");</li>
		<li>accountingParameter.setTypeCost("C");</li>
		<li>accountingParameter.setTransaction(trxProducts.get(String.valueOf(accountingParameter.getProductId()+accountingParameter.getSign())));</li>
		<li>accountingParameters.add(accountingParameter);</li>
		
		<li>response.setAccountingParameters(accountingParameters);</li>
		
		<li>response.setSuccess(true);</li>
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
	AccountingParameterResponse getAccountingParameter(AccountingParameterRequest anAccountingParameterRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Realiza d&eacutebito o cr&eacutedito a una cuenta.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>DTO Debito</li>
		<li>TransactionMonetaryRequest aTransactionMonetaryRequest = new TransactionMonetaryRequest();</li>
		
		<li>Product product = new Product();</li>
		<li>Currency currency = new Currency();</li>
		<li>product.setProductNumber(request.readValueParam("@i_cta"));</li>
		<li>product.setProductType(Integer.parseInt(request.readValueParam("@i_prod")));</li>
		<li>currency.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon")));</li>
		<li>product.setCurrency(currency);</li>
		
		<li>aTransactionMonetaryRequest.setProduct(product);</li>
		<li>aTransactionMonetaryRequest.setConcept(request.readValueParam("@i_concepto"));</li>
		<li>aTransactionMonetaryRequest.setAlternateCode(0);</li>
		<li>aTransactionMonetaryRequest.setChannelId("1");</li>
		<li>aTransactionMonetaryRequest.setTransaction(map.get("ACCOUNTING_PARAM").getTransaction());</li>
		<li>aTransactionMonetaryRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());</li>
		<li>aTransactionMonetaryRequest.setAmmount(new BigDecimal(request.readValueParam("@i_val")));</li>
		</br>
		<li>DTO - Valida parametrizacion de la comision</li>
		<li>aTransactionMonetaryRequest.setProduct(product);</li>
		<li>aTransactionMonetaryRequest.setConcept(request.readValueParam("@i_concepto"));</li>
		<li>aTransactionMonetaryRequest.setTransaction(map.get("ACCOUNTING_PARAM").getTransaction());</li>
		<li>aTransactionMonetaryRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());</li>
		<li>aTransactionMonetaryRequest.setAlternateCode(1);</li>
		<li>aTransactionMonetaryRequest.setAmmount(new BigDecimal(1));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>TransactionMonetaryResponse response = new TransactionMonetaryResponse();</li>
		<li>response.setReferenceNumber(aTransactionMonetaryRequest.getOriginalRequest().readValueFieldInHeader("serviceExecutionId"));</li>
		<li>response.setReferenceNumberBranch(aTransactionMonetaryRequest.getOriginalRequest().readValueFieldInHeader("ssn_branch"));</li>
		<li>response.setSuccess(true);</li>
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
	TransactionMonetaryResponse debitCreditAccount(TransactionMonetaryRequest aTransactionMonetaryRequest)throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 *   
	 *   <b>Consulta saldos a sincronizar.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ValidationAccountsRequest request = new ValidationAccountsRequest();</li>

		<li>Product originProduct = new Product();</li>
		<li>Currency originCurrency = new Currency();</li>
		<li>originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));</li>
		<li>originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));</li>

		<li>originProduct.setCurrency(originCurrency);</li>

		<li>Product destinationProduct = new Product();</li>
		<li>Currency destinationCurrency = new Currency();</li>
		<li>destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));</li>
		<li>destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));</li>
		<li>destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));</li>

		<li>destinationProduct.setCurrency(destinationCurrency);</li>

		<li>Secuential originSSn = new Secuential();</li>
		<li>originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());</li>
		<li>request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));</li>
		<li>request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));</li>

		<li>request.setSecuential(originSSn);</li>
		<li>request.setOriginProduct(originProduct);</li>
		<li>request.setDestinationProduct(destinationProduct);</li>
		<li>request.setOriginalRequest(anOriginalRequest);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>IProcedureResponse wIProcedureResponse = new IProcedureResponse();</li>
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
	IProcedureResponse getBalancesToSynchronize(ValidationAccountsRequest validationAccountsRequest);
	
	Map<String,AccountingParameter> existsAccountingParameter(AccountingParameterResponse anAccountingParameterResponse, int product, String type,String typeCa);
	
	BigDecimal getCost(AccountingParameter accountingParameter, Currency currency, Product product);

}
