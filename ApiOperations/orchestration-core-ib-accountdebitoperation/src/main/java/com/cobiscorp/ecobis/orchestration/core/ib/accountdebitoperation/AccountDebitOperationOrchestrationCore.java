/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.accountdebitoperation;

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
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
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
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.cobis.crypt.ICobisCrypt;
import com.cobiscorp.cobis.commons.components.ComponentLocator;

import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "AccountDebitOperationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountDebitOperationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AccountDebitOperationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_debit_operation_api")
})
public class AccountDebitOperationOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private IResultSetRowColumnData[] columnsToReturn;

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AccountDebitOperation start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		/*if (true) {
			saveReentry(anOriginalRequest);
			aBagSPJavaOrchestration.clear();
			aBagSPJavaOrchestration.put("40004","Server is offline");
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		}*/
		
		queryAccountDebitOperation(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryAccountDebitOperation(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
		String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
		String referenceNumber = wQueryRequest.readValueParam("@i_referenceNumber");
		String debitConcept = wQueryRequest.readValueParam("@i_debitConcept");
		BigDecimal amount = new BigDecimal(wQueryRequest.readValueParam("@i_amount"));
		BigDecimal commission = new BigDecimal(wQueryRequest.readValueParam("@i_commission"));
		
		/*String originCode = wQueryRequest.readValueParam("@i_originCode");
		
		if (originCode == null) {
			wQueryRequest.setValueParam("@i_originCode", "");
		}*/
		
		if (amount.compareTo(new BigDecimal("0")) != 1) {
			aBagSPJavaOrchestration.put("40107", "amount must be greater than 0");
			return;
		}
		
		if (commission.compareTo(new BigDecimal("0")) != 1 && commission.compareTo(new BigDecimal("0")) != 0) {
			aBagSPJavaOrchestration.put("40108", "commission must be greater than or equal to 0");
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
		
		if (debitConcept.isEmpty()) {
			aBagSPJavaOrchestration.put("40106", "debitConcept must not be empty");
			return;
		}
				
		logger.logDebug("Begin flow, queryAccountDebitOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_debit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));	 
	    reqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_debitConcept"));
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
	
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));
				reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_referenceNumber"));
				reqTMPLocal.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_debitConcept"));
				reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						this.columnsToReturn = columns;
						return;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
					} 
					
				} else {
					
					aBagSPJavaOrchestration.put("50045", "Error account debit operation");
					return;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
			} else {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
			}
			 
		} else {
			aBagSPJavaOrchestration.put("50045", "Error account debit operation");
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
	
	public IProcedureResponse saveReentry(IProcedureRequest wQueryRequest) {
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = wQueryRequest.clone();
		ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);
	    
	    String originCode = request.readValueParam("@i_originCode");
	    logger.logDebug("@i_originCode = " + originCode);
		if (originCode == null) {
			logger.logDebug("Entre @i_originCode");
			request.addInputParam("@i_originCode",ICTSTypes.SQLINT4, "");
		}
	    
	    Utils.addInputParam(request, "@i_externalCustomerId", ICTSTypes.SQLINT4,  request.readValueParam("@i_externalCustomerId"));
        
	    reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");
        
        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
        request.removeParam("@t_rty");
        
        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
        	logger.logDebug("Ending flow, saveReentry failed");
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
        	logger.logDebug("Ending flow, saveReentry success");
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;
	}

}
