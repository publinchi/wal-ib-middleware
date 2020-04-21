package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostResponse;

/**
 * This interface contains the methods needed to perform basic tasks transfers.
 *
 * @since Jun 17, 2014
 * @author itorres
 * @version 1.0.0
 *
 */
public interface ICoreServiceCauseAndCost {	
	CauseAndCostResponse executeCauseAndCost(CauseAndCostRequest CauseAndCostRequestRequest) throws CTSServiceException, CTSInfrastructureException;
}
