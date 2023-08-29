package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningResponse;

/** 
 * 
 * @author mmoya
 *
 */
public interface ICoreServiceBatchProductOpening {
		
	public BatchProductOpeningResponse executeBatchProductOpening(BatchProductOpeningRequest aBatchProductOpeningRequest) throws CTSServiceException, CTSInfrastructureException;
  
}