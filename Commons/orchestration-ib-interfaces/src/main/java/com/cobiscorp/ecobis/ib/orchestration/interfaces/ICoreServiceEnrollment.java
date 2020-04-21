package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import java.util.Map;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EnrollmentResponse;

public interface ICoreServiceEnrollment {
	EnrollmentResponse executeEnrollment(Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;

	EnrollmentResponse validateCustomer(Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;
}
