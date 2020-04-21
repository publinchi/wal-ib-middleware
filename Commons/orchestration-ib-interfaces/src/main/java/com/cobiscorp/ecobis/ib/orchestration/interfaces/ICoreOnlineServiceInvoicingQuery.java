package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceResponse;


/**
 * This interface contains the methods for get data to pay service type based billing
 *
 * @author rperero
 * @since Feb 12, 2015
 * @version 1.0.0
 */
public interface ICoreOnlineServiceInvoicingQuery {
	
	/**
	 * Get online service
	 *
	 * @param OnlineServiceRequest
	 * @return OnlineServiceResponse
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	OnlineServiceResponse getOnlineService(OnlineServiceRequest onlineServiceRequest) throws CTSServiceException, CTSInfrastructureException;

}
