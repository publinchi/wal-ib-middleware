package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostResponse;

public interface ICoreServiceTransactionCost {
	TransactionCostResponse getTransactionCost(TransactionCostRequest aCostRequest) throws CTSServiceException, CTSInfrastructureException;
}
