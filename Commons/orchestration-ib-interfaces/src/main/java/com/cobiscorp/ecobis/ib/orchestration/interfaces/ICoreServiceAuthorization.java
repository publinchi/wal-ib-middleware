/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
import com.cobiscorp.ecobis.ib.application.dtos.UnblockedFundsResponse;

/**
 * @author tbaidal
 *
 */
public interface ICoreServiceAuthorization {
	
	PendingTransactionResponse changeTransactionStatus(PendingTransactionRequest rendingTransactionRequest) throws CTSServiceException, CTSInfrastructureException;

	PayrollResponse getPaymentAccounts(PayrollRequest paymentAccountRequest) throws CTSServiceException, CTSInfrastructureException;

	BlockedAccountResponse saveBlockedAccountTmp(BlockedAccountRequest blockedAccountRequest)  throws CTSServiceException, CTSInfrastructureException;

	UnblockedFundsResponse unblockFunds(PayrollRequest payrollRequest)  throws CTSServiceException, CTSInfrastructureException;

}
