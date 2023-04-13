/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.accountcreditoperation;

import java.math.BigDecimal;
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
@Component(name = "AccountCreditOperationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountCreditOperationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AccountCreditOperationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_credit_operation_api")
})
public class AccountCreditOperationOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private IResultSetRowColumnData[] columnsToReturn;

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AccountCreditOperation start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		queryAccountCreditOperation(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryAccountCreditOperation(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
		String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
		String referenceNumber = wQueryRequest.readValueParam("@i_referenceNumber");
		String creditConcept = wQueryRequest.readValueParam("@i_creditConcept");
		BigDecimal amount = new BigDecimal(wQueryRequest.readValueParam("@i_amount"));
		BigDecimal commission = new BigDecimal(wQueryRequest.readValueParam("@i_commission"));
		
		if (amount.compareTo(new BigDecimal("0")) != 1) {
			aBagSPJavaOrchestration.put("50043", "amount must be greater than 0");
			return;
		}
		
		if (commission.compareTo(new BigDecimal("0")) != 1) {
			aBagSPJavaOrchestration.put("50044", "commission must be greater than 0");
			return;
		}
		
		if (accountNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
			return;
		}
		
		if (referenceNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40092", "referenceNumber must not be empty");
			return;
		}
		
		if (referenceNumber.length() != 6) {
			aBagSPJavaOrchestration.put("40104", "referenceNumber must have 6 digits");
			return;
		}
		
		if (creditConcept.isEmpty()) {
			aBagSPJavaOrchestration.put("40093", "creditConcept must not be empty");
			return;
		}
				
		logger.logDebug("Begin flow, queryAccountCreditOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_credit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));	 
	    reqTMPCentral.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_creditConcept"));
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
	
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_credit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));
				reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_referenceNumber"));
				reqTMPLocal.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_creditConcept"));
				reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						this.columnsToReturn = columns;
						return;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
					} 
					
				} else {
					
					aBagSPJavaOrchestration.put("50041", "Error account credit operation");
					return;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40080")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40081")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50042")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
			} 
			 
		} else {
			aBagSPJavaOrchestration.put("50041", "Error account credit operation");
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
		metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SYBVARCHAR, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, this.columnsToReturn[3].getValue()));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
}
