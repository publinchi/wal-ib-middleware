package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchHistoricRefreshBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshRequest;

public interface ICoreServiceBatchHistoricRefreshBalance {
	
	public BatchHistoricRefreshBalanceResponse getHistoricBalanceCurrentAccount(BatchRefreshRequest wBatchRefreshrequest)throws CTSServiceException, CTSInfrastructureException;
	
	public BatchHistoricRefreshBalanceResponse getHistoricBalanceSavingAccount(BatchRefreshRequest wBatchRefreshrequest)throws CTSServiceException, CTSInfrastructureException;

}
