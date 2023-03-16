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
	private IResultSetRowColumnData[] columnsToReturn;

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
		

		if (!wProcedureResponseCentral.hasError()) {
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				this.columnsToReturn = columns;
				return;
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40080")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40081")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
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
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountStatus", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SYBDECIMAL, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("averageBalance", ICTSTypes.SYBDECIMAL, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("deliveryAddress", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("freezingsNumber", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("frozenAmount", ICTSTypes.SYBDECIMAL, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("lastCutoffBalance", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("lastOperationDate", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("openingDate", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("overdraftAmount", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("toDrawBalance", ICTSTypes.SYBDECIMAL, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SYBDECIMAL, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ofical", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("clabeAccountNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("idDebitCard", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("debitCardNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("stateDebitCard", ICTSTypes.SYBVARCHAR, 255));

		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, this.columnsToReturn[3].getValue()));
			row.addRowData(5, new ResultSetRowColumnData(false, this.columnsToReturn[4].getValue()));
			row.addRowData(6, new ResultSetRowColumnData(false, this.columnsToReturn[5].getValue()));
			row.addRowData(7, new ResultSetRowColumnData(false, this.columnsToReturn[6].getValue()));
			row.addRowData(8, new ResultSetRowColumnData(false, this.columnsToReturn[7].getValue()));
			row.addRowData(9, new ResultSetRowColumnData(false, this.columnsToReturn[8].getValue()));
			row.addRowData(10, new ResultSetRowColumnData(false, this.columnsToReturn[9].getValue()));
			row.addRowData(11, new ResultSetRowColumnData(false, this.columnsToReturn[10].getValue()));
			row.addRowData(12, new ResultSetRowColumnData(false, this.columnsToReturn[11].getValue()));
			row.addRowData(13, new ResultSetRowColumnData(false, this.columnsToReturn[12].getValue()));
			row.addRowData(14, new ResultSetRowColumnData(false, this.columnsToReturn[13].getValue()));
			row.addRowData(15, new ResultSetRowColumnData(false, this.columnsToReturn[14].getValue()));
			row.addRowData(16, new ResultSetRowColumnData(false, this.columnsToReturn[15].getValue()));
			row.addRowData(17, new ResultSetRowColumnData(false, this.columnsToReturn[16].getValue()));
			row.addRowData(18, new ResultSetRowColumnData(false, this.columnsToReturn[17].getValue()));
			row.addRowData(19, new ResultSetRowColumnData(false, this.columnsToReturn[18].getValue()));
			row.addRowData(20, new ResultSetRowColumnData(false, this.columnsToReturn[19].getValue()));
			row.addRowData(21, new ResultSetRowColumnData(false, this.columnsToReturn[20].getValue()));
			row.addRowData(22, new ResultSetRowColumnData(false, this.columnsToReturn[21].getValue()));
			row.addRowData(23, new ResultSetRowColumnData(false, this.columnsToReturn[22].getValue()));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, this.columnsToReturn[3].getValue()));
			row.addRowData(5, new ResultSetRowColumnData(false, this.columnsToReturn[4].getValue()));
			row.addRowData(6, new ResultSetRowColumnData(false, this.columnsToReturn[5].getValue()));
			row.addRowData(7, new ResultSetRowColumnData(false, this.columnsToReturn[6].getValue()));
			row.addRowData(8, new ResultSetRowColumnData(false, this.columnsToReturn[7].getValue()));
			row.addRowData(9, new ResultSetRowColumnData(false, this.columnsToReturn[8].getValue()));
			row.addRowData(10, new ResultSetRowColumnData(false, this.columnsToReturn[9].getValue()));
			row.addRowData(11, new ResultSetRowColumnData(false, this.columnsToReturn[10].getValue()));
			row.addRowData(12, new ResultSetRowColumnData(false, this.columnsToReturn[11].getValue()));
			row.addRowData(13, new ResultSetRowColumnData(false, this.columnsToReturn[12].getValue()));
			row.addRowData(14, new ResultSetRowColumnData(false, this.columnsToReturn[13].getValue()));
			row.addRowData(15, new ResultSetRowColumnData(false, this.columnsToReturn[14].getValue()));
			row.addRowData(16, new ResultSetRowColumnData(false, this.columnsToReturn[15].getValue()));
			row.addRowData(17, new ResultSetRowColumnData(false, this.columnsToReturn[16].getValue()));
			row.addRowData(18, new ResultSetRowColumnData(false, this.columnsToReturn[17].getValue()));
			row.addRowData(19, new ResultSetRowColumnData(false, this.columnsToReturn[18].getValue()));
			row.addRowData(20, new ResultSetRowColumnData(false, this.columnsToReturn[19].getValue()));
			row.addRowData(21, new ResultSetRowColumnData(false, this.columnsToReturn[20].getValue()));
			row.addRowData(22, new ResultSetRowColumnData(false, this.columnsToReturn[21].getValue()));
			row.addRowData(23, new ResultSetRowColumnData(false, this.columnsToReturn[22].getValue()));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
}
