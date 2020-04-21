package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentResponse;

/** 
 * 
 * @author itorres
 *
 */
public interface ICoreServiceBatchScheduledPayment {
		
	public BatchScheduledPaymentResponse executeBatchScheduledPayment(BatchScheduledPaymentRequest aBatchScheduledPaymentRequest) throws CTSServiceException, CTSInfrastructureException;
  
}