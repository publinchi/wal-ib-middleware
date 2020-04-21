package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationDeviceStockResponse;

public interface ICoreServiceAuthenticationDeviceStockUpdate {

	AuthenticationDeviceStockResponse updateProviderAuthenticationDeviceStock(AuthenticationDeviceStockRequest aAuthenticationDeviceStocUpdRequest) throws CTSServiceException, CTSInfrastructureException;
	
	AuthenticationDeviceStockResponse updateAuthDeviceStockSB(AuthenticationDeviceStockRequest aAuthenticationDeviceStocUpdRequest) throws CTSServiceException, CTSInfrastructureException;
	


	
}
