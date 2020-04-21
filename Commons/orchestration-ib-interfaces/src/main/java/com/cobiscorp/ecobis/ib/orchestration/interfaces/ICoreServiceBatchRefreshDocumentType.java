package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshDocumentTypeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchRefreshRequest;

public interface ICoreServiceBatchRefreshDocumentType {
	
	public BatchRefreshDocumentTypeResponse getDocumentType(BatchRefreshRequest BatchRefreshRequest)throws CTSServiceException, CTSInfrastructureException;

}
