package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentResponse;

public interface ICoreServiceScheduledPayments {
	ScheduledPaymentResponse executeScheduledPayment(ScheduledPaymentRequest aScheduledPaymentServiceRequest)throws CTSServiceException, CTSInfrastructureException;
	ScheduledPaymentResponse saveScheduledPayment(ScheduledPaymentRequest aPaymentServiceRequest)throws CTSServiceException, CTSInfrastructureException;
}
