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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentCreditCardResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 * @author cplua
 *
 */
public interface IServicePayCreditCard {
	
	/**
	 * 
	 *   
	 *   <b>Pago de tarjeta de cr&eacutedito</b>
	*/
	boolean payCreditCard(Map<String, Object> aBagSPJavaOrchestration)throws CTSServiceException, CTSInfrastructureException;

}
