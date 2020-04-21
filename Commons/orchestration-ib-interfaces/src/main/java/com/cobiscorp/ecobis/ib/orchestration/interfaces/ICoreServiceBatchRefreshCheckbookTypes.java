package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshCheckbookTypesResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshRequest;

public interface ICoreServiceBatchRefreshCheckbookTypes {
	
	public BatchRefreshCheckbookTypesResponse getCheckbookTypes(BatchRefreshRequest wBatchRefreshRequest)throws CTSServiceException, CTSInfrastructureException;

}
