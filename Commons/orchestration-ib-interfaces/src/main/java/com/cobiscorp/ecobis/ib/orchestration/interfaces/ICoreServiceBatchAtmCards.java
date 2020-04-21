package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.BatchAtmCardsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchAtmCardsResponse;

/** 
 * 
 * @author itorres
 *
 */
public interface ICoreServiceBatchAtmCards {
		
	public BatchAtmCardsResponse executeBatchAtmCards(BatchAtmCardsRequest aBatchAtmCardsRequest) throws CTSServiceException, CTSInfrastructureException;
  
}
