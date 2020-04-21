package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesResponse;

public interface ICoreServiceCustomServices {
	CustomServicesResponse searchCustomServicesAdmin(CustomServicesRequest aCustomServicesRequest) throws CTSServiceException, CTSInfrastructureException;
}
