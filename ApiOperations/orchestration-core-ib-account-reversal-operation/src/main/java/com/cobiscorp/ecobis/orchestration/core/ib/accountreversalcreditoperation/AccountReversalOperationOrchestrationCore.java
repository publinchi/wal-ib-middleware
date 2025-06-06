package com.cobiscorp.ecobis.orchestration.core.ib.accountreversalcreditoperation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.Constants;

/**
 * @author jolmos
 * @since Sep 2, 2025
 * @version 2.0.0
 */
@Component(name = "AccountReversalOperationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountReversalOperationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AccountReversalOperationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_reverse_operation")
})
public class AccountReversalOperationOrchestrationCore extends OfflineApiTemplate {// SPJavaOrchestrationBase

	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AccountReversalOperationOrchestrationCore --->";
	protected static final String COLUMNS_RETURN = "columnsToReturn";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][executeJavaOrchestration]");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
		}

		aBagSPJavaOrchestration.put(IS_ONLINE, false);
		aBagSPJavaOrchestration.put(IS_ERRORS, false);
		aBagSPJavaOrchestration.put(IS_REENTRY, evaluateExecuteReentry(anOriginalRequest));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response flowRty: " + aBagSPJavaOrchestration.get(IS_REENTRY));
		}

		if (!(Boolean)aBagSPJavaOrchestration.get(IS_REENTRY)) {
			aBagSPJavaOrchestration.put("process", "REVERSAL_CREDIT_OPERATION");
			IProcedureResponse potency = logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);
			IResultSetRow resultSetRow = potency.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			if (columns[0].getValue().equals("false") ) {
				setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
				return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
			}
		}

		if (validateParameters(aBagSPJavaOrchestration, anOriginalRequest))
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		try {
			ServerResponse serverResponse = serverStatus();
			aBagSPJavaOrchestration.put(IS_ONLINE, serverResponse.getOnLine());
			aBagSPJavaOrchestration.put(PROCESS_DATE, serverResponse.getProcessDate());
		} catch (CTSServiceException | CTSInfrastructureException e) {
			if (logger.isErrorEnabled()){
				logger.logError("Error getting server status: " + e.toString());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Online: " + aBagSPJavaOrchestration.get(IS_ONLINE));
		}

		aBagSPJavaOrchestration.put(IS_REENTRY, evaluateExecuteReentry(anOriginalRequest));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response flowRty: " + aBagSPJavaOrchestration.get(IS_REENTRY));
		}

		return processTransaction(aBagSPJavaOrchestration, anOriginalRequest);
	}

	@Override
	protected void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

	}

	private boolean validateParameters(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][validateParameters]");
		}

		String reversalConcept = anOriginalRequest.readValueParam("@i_reversalConcept");
		String referenceNumber = anOriginalRequest.readValueParam("@i_referenceNumber");
		Integer externalCustomerIdOrigin = new Integer(anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
		String accountNumberOrigin = anOriginalRequest.readValueParam("@i_accountNumber_ori");
		String referenceNumberOrigin = anOriginalRequest.readValueParam("@i_referenceNumber_ori");
		String movementIdOrigin = anOriginalRequest.readValueParam("@i_movementId_ori");
		String reversalReasonOrigin = anOriginalRequest.readValueParam("@i_reversalReason_ori");
		BigDecimal amountCommission = new BigDecimal(anOriginalRequest.readValueParam("@i_amount_com"));
		String reasonCommision = anOriginalRequest.readValueParam("@i_reason_com");
		String movementIdComOri = anOriginalRequest.readValueParam("@i_movementId_com_ori");
		String referenceNumberComOri = anOriginalRequest.readValueParam("@i_referenceNumber_com_ori");

		aBagSPJavaOrchestration.put("@i_originMovementId", movementIdOrigin);
		aBagSPJavaOrchestration.put("@i_originReferenceNumber", referenceNumberOrigin);

		if (reversalConcept.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40128", "reversalConcept must not be empty.");
			return true;
		}

		if (referenceNumber.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40092", "referenceNumber must not be empty.");
			return true;
		}

		if (externalCustomerIdOrigin <= 0) {
			setError(aBagSPJavaOrchestration, "40129", "originalTransactionData.externalCustomerId must be greater than 0.");
			return true;
		}

		if (accountNumberOrigin.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40130", "originalTransactionData.accountNumber must not be empty.");
			return true;
		}

		if (referenceNumberOrigin.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40131", "originalTransactionData.referenceNumber must not be empty.");
			return true;
		}

		if (referenceNumberOrigin.length() != 6) {
			setError(aBagSPJavaOrchestration, "40132", "originalTransactionData.referenceNumber must have 6 digits.");
			return true;
		}

		if (movementIdOrigin.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40133", "originalTransactionData.movementId must not be empty.");
			return true;
		}

		if (reversalReasonOrigin.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40134", "originalTransactionData.reversalReason must not be empty.");
			return true;
		}

		if (amountCommission.compareTo(BigDecimal.ZERO) <= 0) {
			setError(aBagSPJavaOrchestration, "40135", "commission.amount must not be empty.");
			return true;
		}

		if (reasonCommision.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40136", "commission.reason must not be empty.");
			return true;
		}

		if (movementIdComOri.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40137", "commission.originalTransactionData.movementId must not be empty.");
			return true;
		}

		if (referenceNumberComOri.isEmpty()) {
			setError(aBagSPJavaOrchestration, "40138", "commission.originalTransactionData.referenceNumber must not be empty.");
			return true;
		}

		return false;
	}

	private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][processTransaction]");
		}
		if (!(Boolean)aBagSPJavaOrchestration.get(IS_ONLINE)) {
			if (!(Boolean)aBagSPJavaOrchestration.get(IS_REENTRY)) {
				processOffline(aBagSPJavaOrchestration, anOriginalRequest);
			} else {
				setError(aBagSPJavaOrchestration, "50041", "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
			}
		} else {
			processOnline(aBagSPJavaOrchestration, anOriginalRequest);
		}

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	private void processOffline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][processOffline]");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
		}

		IProcedureResponse wProcedureResponseVal;

		wProcedureResponseVal = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response [saveReentry]: " + wProcedureResponseVal.getProcedureResponseAsString());
		}

		wProcedureResponseVal = getValAccount(anOriginalRequest, aBagSPJavaOrchestration);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response [getValAccount]: " + wProcedureResponseVal.getProcedureResponseAsString());
		}
		if (!wProcedureResponseVal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			setError(aBagSPJavaOrchestration, wProcedureResponseVal.getResultSetRowColumnData(2, 1, 1).getValue(), wProcedureResponseVal.getResultSetRowColumnData(2, 1, 2).getValue());
			return;
		}

		IProcedureRequest reqTMPOffline = (initProcedureRequest(anOriginalRequest));
		reqTMPOffline.setSpName("cob_bvirtual..sp_reversal_credit_operation_offline");
		reqTMPOffline.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', IMultiBackEndResolverService.TARGET_LOCAL);
		reqTMPOffline.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18700138");

		reqTMPOffline.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18700138");
		reqTMPOffline.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");

		reqTMPOffline.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
		reqTMPOffline.addInputParam("@i_externalCustomerId_ori", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
		reqTMPOffline.addInputParam("@i_accountNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber_ori"));
		reqTMPOffline.addInputParam("@i_referenceNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_ori"));
		reqTMPOffline.addInputParam("@i_movementId_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_ori"));
		reqTMPOffline.addInputParam("@i_reversalReason_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalReason_ori"));
		reqTMPOffline.addInputParam("@i_amount_com", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount_com"));
		reqTMPOffline.addInputParam("@i_reason_com", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reason_com"));
		reqTMPOffline.addInputParam("@i_movementId_com_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_com_ori"));
		reqTMPOffline.addInputParam("@i_referenceNumber_com_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_com_ori"));

		reqTMPOffline.addOutputParam("@o_causa_rev", ICTSTypes.SQLVARCHAR, "X");
		reqTMPOffline.addOutputParam("@o_causa_com", ICTSTypes.SQLVARCHAR, "X");
		reqTMPOffline.addOutputParam("@o_amount_ori", ICTSTypes.SQLMONEY, "0");

		wProcedureResponseVal = executeCoreBanking(reqTMPOffline);

		aBagSPJavaOrchestration.put("ssn", anOriginalRequest.readValueFieldInHeader("ssn"));
		aBagSPJavaOrchestration.put("ssn_branch", anOriginalRequest.readValueFieldInHeader("ssn_branch"));
		aBagSPJavaOrchestration.put("causa_rev", wProcedureResponseVal.readValueParam("@o_causa_rev"));
		aBagSPJavaOrchestration.put("causa_com", wProcedureResponseVal.readValueParam("@o_causa_com"));
		aBagSPJavaOrchestration.put("amount_ori", wProcedureResponseVal.readValueParam("@o_amount_ori"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("OFFLINE [executeOfflineTransacction][cob_bvirtual..sp_reversal_credit_operation_offline][Local] wProcedureResponseVal: " + wProcedureResponseVal.getProcedureResponseAsString());
		}

		if (!wProcedureResponseVal.hasError()) {
			int resultSetCount = wProcedureResponseVal.getResultSetListSize();
			if (logger.isDebugEnabled()) {
				logger.logDebug("resultSetCount [wProcedureResponseVal.getResultSetListSize()]: " + resultSetCount);
			}
			IResultSetRow resultSetRow = wProcedureResponseVal.getResultSet(resultSetCount).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			if (columns[0].getValue().equals("true")) {
				aBagSPJavaOrchestration.put(COLUMNS_RETURN, columns);
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				setError(aBagSPJavaOrchestration, columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId_ori") + " does not exist");
			} else {
				setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
			}
		} else {
			setError(aBagSPJavaOrchestration, "50060", "Error account reversal credit operation.");
		}
	}

	private void processOnline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][processOnline]");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
		}

		String reentryCode = anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX");
		IProcedureRequest reqTMPCentral = anOriginalRequest;

		if (reentryCode != null) {
			logger.logDebug("Flow: " + reentryCode);
			reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_SSN, reentryCode);
		}

		reqTMPCentral.setSpName("cob_bvirtual..sp_reversal_credit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18700138");
		reqTMPCentral.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		reqTMPCentral.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
		reqTMPCentral.addInputParam("@i_externalCustomerId_ori", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
		reqTMPCentral.addInputParam("@i_accountNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber_ori"));
		reqTMPCentral.addInputParam("@i_referenceNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_ori"));
		reqTMPCentral.addInputParam("@i_movementId_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_ori"));
		reqTMPCentral.addInputParam("@i_reversalReason_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalReason_ori"));
		reqTMPCentral.addInputParam("@i_amount_com", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount_com"));
		reqTMPCentral.addInputParam("@i_reason_com", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reason_com"));
		reqTMPCentral.addInputParam("@i_movementId_com_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_com_ori"));
		reqTMPCentral.addInputParam("@i_referenceNumber_com_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_com_ori"));

		reqTMPCentral.addOutputParam("@o_causa_rev", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_causa_com", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_amount_ori", ICTSTypes.SQLMONEY, "0");

		aBagSPJavaOrchestration.put("ssn", anOriginalRequest.readValueFieldInHeader("ssn"));
		aBagSPJavaOrchestration.put("ssn_branch", anOriginalRequest.readValueFieldInHeader("ssn_branch"));

		IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);

		aBagSPJavaOrchestration.put("causa_rev", wProcedureResponseCentral.readValueParam("@o_causa_rev"));
		aBagSPJavaOrchestration.put("causa_com", wProcedureResponseCentral.readValueParam("@o_causa_com"));
		aBagSPJavaOrchestration.put("amount_ori", wProcedureResponseCentral.readValueParam("@o_amount_ori"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response executeCoreBanking cobis..sp_reversal_credit_operation_central_api: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}

		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

			if (columns[0].getValue().equals("true")) {
				aBagSPJavaOrchestration.put(COLUMNS_RETURN, columns);
				IProcedureRequest reqTMPLocal = (initProcedureRequest(anOriginalRequest));

				reqTMPLocal.setSpName("cob_bvirtual..sp_reversal_credit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18700138");
				reqTMPLocal.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
				reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber_ori"));
				reqTMPLocal.addInputParam("@i_amount", ICTSTypes.SQLMONEY, (String) aBagSPJavaOrchestration.get("amount_ori"));
				reqTMPLocal.addInputParam("@i_commission", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount_com"));
				reqTMPLocal.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalReason_ori"));

				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, processOnline with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					if (columns[0].getValue().equals("true")) {
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50060")) {
						setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
					}
				} else {
					setError(aBagSPJavaOrchestration, "50060", "Error account debit operation.");
				}
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				setError(aBagSPJavaOrchestration, columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId") + " does not exist");
			} else {
				setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
			}
		} else {
			setError(aBagSPJavaOrchestration, "50060", "Error account debit operation.");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][processResponse]");
		}

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("key", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value", ICTSTypes.SYBVARCHAR, 255));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Valida errores [isErrors]: " + aBagSPJavaOrchestration.get(IS_ERRORS).toString());
		}

		if (!(Boolean)aBagSPJavaOrchestration.get(IS_ERRORS)) {
			String causa_rev = "";
			String causa_com = "";
			
			IResultSetRowColumnData[] columnsToReturn = (IResultSetRowColumnData[]) aBagSPJavaOrchestration.get(COLUMNS_RETURN);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Ending flow, processResponse success.");
				logger.logDebug("success: " +  columnsToReturn[0].getValue());
				logger.logDebug("code: " +  columnsToReturn[1].getValue());
				logger.logDebug("message: " +  columnsToReturn[2].getValue());
				logger.logDebug("movementId: " +  columnsToReturn[3].getValue());
				logger.logDebug("key: " +  columnsToReturn[4].getValue());
				logger.logDebug("value: " +  columnsToReturn[5].getValue());
			}

			row.addRowData(1, new ResultSetRowColumnData(false, columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, columnsToReturn[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, columnsToReturn[3].getValue()));
			row.addRowData(5, new ResultSetRowColumnData(false, columnsToReturn[4].getValue()));
			row.addRowData(6, new ResultSetRowColumnData(false, columnsToReturn[5].getValue()));
			data.addRow(row);

			if ( aBagSPJavaOrchestration.get("causa_rev") != null) {
				causa_rev = aBagSPJavaOrchestration.get("causa_rev").toString();
			}
			
			if ( aBagSPJavaOrchestration.get("causa_com") != null) {
				causa_com = aBagSPJavaOrchestration.get("causa_com").toString();
			}
			
			registerAllTransactionSuccess("AccountReversalOperationOrchestrationCore", anOriginalRequest, causa_rev, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("@i_debitReason", Constants.FALSE_CHARGEBACK);
			registerAllTransactionSuccess("AccountDebitOperationOrchestrationCore", anOriginalRequest, causa_com, aBagSPJavaOrchestration);
		} else {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Ending flow, processResponse failed.");
				logger.logDebug("success: false");
				logger.logDebug("code: " +  aBagSPJavaOrchestration.get("error_code"));
				logger.logDebug("message: " +  aBagSPJavaOrchestration.get("error_message"));
				logger.logDebug("movementId: null");
				logger.logDebug("key: null");
				logger.logDebug("value: null");
			}

			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_code")));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_message")));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			data.addRow(row);
			
			//Validamos que el error no se un mensaje de reentry 
			if (aBagSPJavaOrchestration.get("error_message") != null && !aBagSPJavaOrchestration.get("error_message").equals("NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!")) {
				//Consulta datos de la transaccion original
				searchDataTransactionOrigin(anOriginalRequest, aBagSPJavaOrchestration);
				
				aBagSPJavaOrchestration.put("message_error", aBagSPJavaOrchestration.get("error_message"));
				aBagSPJavaOrchestration.put("code_error", aBagSPJavaOrchestration.get("error_code"));
				
				aBagSPJavaOrchestration.put("causal", aBagSPJavaOrchestration.get("causa_rev"));
				registerTransactionFailed("AccountReversalOperationOrchestrationCore", "", anOriginalRequest, aBagSPJavaOrchestration);
	
				aBagSPJavaOrchestration.put("@i_debitReason", Constants.FALSE_CHARGEBACK);
				aBagSPJavaOrchestration.put("causal", aBagSPJavaOrchestration.get("causa_com"));
				registerTransactionFailed("AccountDebitOperationOrchestrationCore", "", anOriginalRequest, aBagSPJavaOrchestration);
			}
			
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		wProcedureResponse.addResponseBlock(resultBlock);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " +  wProcedureResponse.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.replace("process", "FINISH_OPERATION");
		logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);

		return wProcedureResponse;
	}

	public void searchDataTransactionOrigin(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		String amountOri = "";
		Double valOrigin = 0.00;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][searchDataTransactionOrigin]");
		}

		try{
			IProcedureRequest reqTMPCentral = new ProcedureRequestAS();

			reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
			reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
			reqTMPCentral.setSpName("cob_bvirtual..sp_bv_cons_val_webhook_central");
			reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
			reqTMPCentral.addInputParam("@i_movementId", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("@i_originMovementId").toString());
			reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
			reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_accountNumber_ori"));
			reqTMPCentral.addOutputParam("@o_amount", ICTSTypes.SQLMONEY, "0");

			IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response executeCoreBanking cob_bvirtual..sp_bv_cons_val_webhook_central: " + wProcedureResponseCentral.getProcedureResponseAsString());
			}
			
			amountOri =  wProcedureResponseCentral.readValueParam("@o_amount");
						
			if (amountOri != null) {
				valOrigin = Double.parseDouble(amountOri);
			}
			
			if (valOrigin.equals(0.00) || valOrigin < 0.00) {
				IProcedureRequest reqTMPLocal = new ProcedureRequestAS();

				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
				reqTMPLocal.setSpName("cob_bvirtual..sp_bv_cons_val_webhook_local");
				reqTMPLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
				reqTMPLocal.addInputParam("@i_movementId", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("@i_originMovementId").toString());
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
				reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_accountNumber_ori"));
				reqTMPLocal.addOutputParam("@o_amount", ICTSTypes.SQLMONEY, "0");

				IProcedureResponse wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				
				if (logger.isDebugEnabled()) {
					logger.logDebug("Response executeCoreBanking cob_bvirtual..sp_bv_cons_val_webhook_local: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}
				
				amountOri =  wProcedureResponseLocal.readValueParam("@o_amount");				
			}
						
			aBagSPJavaOrchestration.put("amount_ori", amountOri);
			
		} catch (NumberFormatException e) {
			if (logger.isErrorEnabled()) {
				logger.logError(CLASS_NAME + " Numero no es valido: " + e.getMessage(), e);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.logError(CLASS_NAME + " Error al obtener datos de transaccion de origen: " + e.getMessage(), e);
			}			
			throw new RuntimeException("Error al obtener datos:", e);
		} finally{
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Saliendo de searchDataTransactionOrigin");
			}
		}

	}
	
	@Override
    public ICoreServer getCoreServer() {
        return null;
    }


}
