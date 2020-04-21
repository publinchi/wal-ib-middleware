/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.base.interfaces.transfers;

import java.util.Map;

import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;

/**
 * @author schancay
 * @since Aug 27, 2014
 * @version 1.0.0
 */
public interface ITransferExecution {
	TransferResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);

	void executeOffline(Map<String, Object> aBagSPJavaOrchestration);

}
