package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeResponse;

public interface ICoreServiceAuthenticationType {

	AuthenticationTypeResponse getAuthenticationTypes(AuthenticationTypeRequest wAuthenticationTypeRequest) throws CTSServiceException, CTSInfrastructureException;
	
}
