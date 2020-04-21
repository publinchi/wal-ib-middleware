package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchBalanceRefreshResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshRequest;

public interface ICoreServiceBatchRefreshBalance {

	public BatchBalanceRefreshResponse getBalanceAccount(BatchRefreshRequest wBatchRefreshrequest, String producto)throws CTSServiceException, CTSInfrastructureException;
	
}
