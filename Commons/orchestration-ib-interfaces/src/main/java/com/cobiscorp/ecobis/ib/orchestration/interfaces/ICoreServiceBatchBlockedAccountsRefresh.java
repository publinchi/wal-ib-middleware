package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchBlockedAccountsRefreshResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshRequest;

public interface ICoreServiceBatchBlockedAccountsRefresh {
	public BatchBlockedAccountsRefreshResponse getBlockedSavingAccounts(BatchRefreshRequest aRequest) throws CTSServiceException, CTSInfrastructureException;
    public BatchBlockedAccountsRefreshResponse getBlockedCheckingAccounts(BatchRefreshRequest aRequest) throws CTSServiceException, CTSInfrastructureException;

}
