/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.getbalancesdetail;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

import com.cobiscorp.cobis.crypt.ICobisCrypt;
import com.cobiscorp.cobis.commons.components.ComponentLocator;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "GetBalancesDetailOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetBalancesDetailOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "GetBalancesDetailOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_get_balances_detail_api")
})
public class GetBalancesDetailOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, GetBalancesDetailOrchestrationCore start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		queryGetBalancesDetail(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryGetBalancesDetail(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
		String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
		
		if (accountNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
			return;
		}
				
		logger.logDebug("Begin flow, queryGetBalancesDetail with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));
		reqTMPCentral.setSpName("cobis..sp_get_balances_detail_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500102");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
		IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryGetBalancesDetail with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
				logger.logDebug("The accountName is: " + columns[3]);
				
				
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put("40012", "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40080")) {
				
				aBagSPJavaOrchestration.put("40080", "accountNumber does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40081")) {
				
				aBagSPJavaOrchestration.put("40081", "The account number does not correspond to the customer id");
				return;
			}
			 
			
		} else {
			aBagSPJavaOrchestration.put("50007", "Error get balances detail");
			return;
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		
			
		return wProcedureResponse;
	}
}
