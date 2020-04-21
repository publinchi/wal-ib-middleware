package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingResponse;

/**
 * This interface contains the methods for get data to pay service type based billing
 *
 * @author rperero
 * @since Feb 10, 2015
 * @version 1.0.0
 */
public interface ICoreBasedBillingQuery {
	
	/**
	 * Get information of pay service type based billing.
	 *
	 * @param procedureRequest
	 * @return
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	BasedBillingResponse getBasedBilling(BasedBillingRequest basedBillingRequest) throws CTSServiceException, CTSInfrastructureException;

}
