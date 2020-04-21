package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataResponse;

public interface ICoreServiceBatchGenerateCustomerData {
	BatchGenerateCustomerDataResponse getGenerateCustomerData(BatchGenerateCustomerDataRequest generateCustomerDataRequest) throws CTSServiceException, CTSInfrastructureException;
}
