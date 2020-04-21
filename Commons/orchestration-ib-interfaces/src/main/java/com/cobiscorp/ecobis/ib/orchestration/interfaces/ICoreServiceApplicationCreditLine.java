package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineResponse;

public interface ICoreServiceApplicationCreditLine {

	public ApplicationCreditLineResponse getApplicationCreditLine(ApplicationCreditLineRequest aApplicationCreditLineRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
