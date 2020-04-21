/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.accounts;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountBalance;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAccountStatementQuery;
//import com.cobiscorp.ecobis.ib.utils.dtos.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * @author mvelez
 *
 */
@Component(name = "AccountStatementQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountStatementQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountStatementQueryOrchestrationCore") })

public class AccountStatementQueryOrchestrationCore extends QueryBaseTemplate {
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceAccountStatementQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceAccountStatementQuery coreService;
	private static final String TRN_SAVINGS_ACCOUNT_SG = "1800018"; // "18383";
	private static final String TRN_CHECKING_ACCOUNT_SG = "1800017"; // "18309";
	private IProcedureResponse pResponse = null;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceAccountStatementQuery service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceAccountStatementQuery service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*******************************************************************************/
	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		pResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_VALIDATE_LOCAL);

		AccountStatementBalanceResponse aAccountStatementBalanceResponse = null;

		AccountStatementRequest aAccountStatementRequest = transformAccountStatementRequest(request.clone());

		try {
			/*
			 * messageError=
			 * "<<<GetSavingsAccountStatement: ERROR EXECUTING SERVICE>>>";
			 * messageLog =
			 * "<<<GetSavingsAccountStatement>>> "+aAccountStatementRequest.
			 * getProduct().getProductNumber(); queryName =
			 * "GetSavingsAccountStatement";
			 */
			if (logger.isDebugEnabled()) {
				logger.logDebug(request.readValueParam("@t_trn"));
				logger.logDebug("<<<---------------->>>");
			}
			if (request.readValueParam("@t_trn").equals(TRN_SAVINGS_ACCOUNT_SG)) {
				if (logger.isDebugEnabled())
					logger.logDebug("<<<Exec GetSavingsAccountStatement>>>");
				messageError = "<<<GetSavingsAccountStatement: ERROR EXECUTING SERVICE>>>";
				messageLog = "<<<GetSavingsAccountStatement>>> "
						+ aAccountStatementRequest.getProduct().getProductNumber();
				queryName = "GetSavingsAccountStatement";
				aAccountStatementBalanceResponse = coreService.GetSavingsAccountStatement(aAccountStatementRequest);
			}
			if (request.readValueParam("@t_trn").equals(TRN_CHECKING_ACCOUNT_SG)) {
				if (logger.isDebugEnabled())
					logger.logDebug("<<<Exec GetCheckingAccountStatement>>>");
				messageError = "<<<GetCheckingAccountStatement: ERROR EXECUTING SERVICE>>>";
				messageLog = "<<<GetCheckingAccountStatement>>> "
						+ aAccountStatementRequest.getProduct().getProductNumber();
				queryName = "GetCheckingAccountStatement";
				aAccountStatementBalanceResponse = coreService.GetCheckingAccountStatement(aAccountStatementRequest);
			}

		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}
		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, aAccountStatementBalanceResponse);
		return processResponse(request, aBagSPJavaOrchestration);
	}

	/*******************************************************************************/
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		// Valida InyecciÃ³n de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			return executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/*******************************************************************************/
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureResponse response = transformProcedureResponse(
				(AccountStatementBalanceResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"));
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	/*******************************************************************************/
	/*
	 * Transform a Procedure Request in AccountStatementRequest
	 */
	private AccountStatementRequest transformAccountStatementRequest(IProcedureRequest aRequest) {
		AccountStatementRequest accountStatementReq = new AccountStatementRequest();
		AccountStatement accountStatement = new AccountStatement();
		Product product = new Product();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		accountStatementReq.setMon(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		accountStatementReq.setLogin(aRequest.readValueParam("@i_login"));
		product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		product.setProductNumber(aRequest.readValueParam("@i_cta"));
		accountStatementReq.setProduct(product);
		accountStatementReq.setSequential(aRequest.readValueParam("@i_sec"));
		accountStatementReq.setAlternateCode(aRequest.readValueParam("@i_sec_alt"));
		accountStatementReq.setDaily(Integer.parseInt(aRequest.readValueParam("@i_diario")));
		accountStatement.setStringDate(aRequest.readValueParam("@i_fecha"));
		accountStatementReq.setInitialDateString(pResponse.readValueParam("@o_fecha_ini"));
		accountStatementReq.setFinalDateString(pResponse.readValueParam("@o_fecha_fin"));
		accountStatement.setHour(aRequest.readValueParam("@i_hora"));
		accountStatementReq.setAccountStatement(accountStatement);

		return accountStatementReq;
	}

	/*******************************************************************************/
	private IProcedureResponse transformProcedureResponse(
			AccountStatementBalanceResponse accountStatementBalanceResponse) {
		// if (!IsValidLoanAmortizationResponse(loanAmortizationResponse))
		// return null;

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: transformProcedureResponse>>>");

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("reference", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("debitsAmount", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("creditsAmount", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("signDC", ICTSTypes.SQLVARCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("hour", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationType", ICTSTypes.SQLINT4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("causeId", ICTSTypes.SQLVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequential", ICTSTypes.SQLINT4, 0));

		if (accountStatementBalanceResponse.getReturnCode() == 0) {

			for (AccountStatement aAccountStatement : accountStatementBalanceResponse
					.getAccountStatementsCollection()) {
				// if (!IsValidAccountStatementResponse(aAccountStatement))
				// return null;
				IResultSetRow row = new ResultSetRow();
				if (aAccountStatement.getStringDate() != null) {
					row.addRowData(1, new ResultSetRowColumnData(false, aAccountStatement.getStringDate()));
				} else {
					row.addRowData(1, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getReference() != null) {
					row.addRowData(2, new ResultSetRowColumnData(false, aAccountStatement.getReference()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getDescription() != null) {
					row.addRowData(3, new ResultSetRowColumnData(false, aAccountStatement.getDescription()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getDebitsAmount() != null) {
					row.addRowData(4,
							new ResultSetRowColumnData(false, aAccountStatement.getDebitsAmount().toString()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountStatement.getCreditsAmount() != null) {
					row.addRowData(5,
							new ResultSetRowColumnData(false, aAccountStatement.getCreditsAmount().toString()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountStatement.getAccountingBalance() != null) {
					row.addRowData(6,
							new ResultSetRowColumnData(false, aAccountStatement.getAccountingBalance().toString()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountStatement.getSignDC() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false, aAccountStatement.getSignDC()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getHour() != null) {
					row.addRowData(8, new ResultSetRowColumnData(false, aAccountStatement.getHour().toString()));
				} else {
					row.addRowData(8, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getTypeTransaction() != null) {
					row.addRowData(9,
							new ResultSetRowColumnData(false, aAccountStatement.getTypeTransaction().toString()));
				} else {
					row.addRowData(9, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getCauseId() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false, aAccountStatement.getCauseId().toString()));
				} else {
					row.addRowData(10, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountStatement.getSequential() != null) {
					row.addRowData(11, new ResultSetRowColumnData(false, aAccountStatement.getSequential().toString()));
				} else {
					row.addRowData(11, new ResultSetRowColumnData(false, "0"));
				}
				data.addRow(row);
			} // for
			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);

			metaData = new ResultSetHeader();
			data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("clientName", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("executiveName", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("deliveryAddress", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("lastCutoffBalance", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("averageBalance", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("lastOperationDate", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("lastCutoffDate", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nextCutoffDate", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("clientPhone", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("clientEmail", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("officeName", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("toDrawBalance", ICTSTypes.SQLVARCHAR, 20));

			for (AccountBalance aAccountBalance : accountStatementBalanceResponse.getAccountBalanceCollection()) {
				// if (!IsValidAccountBalanceResponse(aAccountBalance)) return
				// null;
				IResultSetRow row = new ResultSetRow();
				if (aAccountBalance.getProductNumber() != null) {
					row.addRowData(1,
							new ResultSetRowColumnData(false, aAccountBalance.getProductNumber().getProductNumber()));
				} // getStringDate()));
				else {
					row.addRowData(1, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getClientName() != null) {
					row.addRowData(2,
							new ResultSetRowColumnData(false, aAccountBalance.getClientName().getCompleteName()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getCurrencyName() != null) {
					row.addRowData(3, new ResultSetRowColumnData(false,
							aAccountBalance.getCurrencyName().getCurrencyDescription()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getExecutiveName() != null) {
					row.addRowData(4, new ResultSetRowColumnData(false, aAccountBalance.getExecutiveName()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getDeliveryAdress() != null) {
					row.addRowData(5, new ResultSetRowColumnData(false, aAccountBalance.getDeliveryAdress()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getAvailableBalance() != null) {
					row.addRowData(6,
							new ResultSetRowColumnData(false, aAccountBalance.getAvailableBalance().toString()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountBalance.getAccountingBalance() != null) {
					row.addRowData(7,
							new ResultSetRowColumnData(false, aAccountBalance.getAccountingBalance().toString()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountBalance.getLastCutoffBalance() != null) {
					row.addRowData(8,
							new ResultSetRowColumnData(false, aAccountBalance.getLastCutoffBalance().toString()));
				} else {
					row.addRowData(8, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountBalance.getAverageBalance() != null) {
					row.addRowData(9,
							new ResultSetRowColumnData(false, aAccountBalance.getAverageBalance().toString()));
				} else {
					row.addRowData(9, new ResultSetRowColumnData(false, "0.00"));
				}
				if (aAccountBalance.getProductNumber() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false, aAccountBalance.getLastOperationDate()));
				} else {
					row.addRowData(10, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getLastOperationDate() != null) {
					row.addRowData(11, new ResultSetRowColumnData(false, aAccountBalance.getLastCutoffDate()));
				} else {
					row.addRowData(11, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getNextCutoffDate() != null) {
					row.addRowData(12, new ResultSetRowColumnData(false, aAccountBalance.getNextCutoffDate()));
				} else {
					row.addRowData(12, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getClientPhone() != null) {
					row.addRowData(13, new ResultSetRowColumnData(false, aAccountBalance.getClientPhone()));
				} else {
					row.addRowData(13, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getClientEmail() != null) {
					row.addRowData(14, new ResultSetRowColumnData(false, aAccountBalance.getClientEmail()));
				} else {
					row.addRowData(14, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getOfficeName() != null) {
					row.addRowData(15,
							new ResultSetRowColumnData(false, aAccountBalance.getOfficeName().getDescription()));
				} else {
					row.addRowData(15, new ResultSetRowColumnData(false, ""));
				}
				if (aAccountBalance.getToDrawBalance() != null) {
					row.addRowData(16,
							new ResultSetRowColumnData(false, aAccountBalance.getToDrawBalance().toString()));
				} else {
					row.addRowData(16, new ResultSetRowColumnData(false, "0.00"));
				}
				data.addRow(row);
			} // for
				// resultBlock = null;
			IResultSetBlock resultBlock2 = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock2);
			wProcedureResponse.addResponseBlock(resultBlock1);

		} else {
			wProcedureResponse = Utils.returnException(accountStatementBalanceResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("<<<ORCHESTRATION: Response Final >>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidAccountStatementResponse(AccountStatement aAccountStatement) {
		String messageError = null;

		messageError = aAccountStatement.getReference() == null ? " - Reference can't be null" : "";
		messageError = aAccountStatement.getStringDate() == null ? " - String Date can't be null" : "";
		messageError = aAccountStatement.getDescription() == null ? " - Description can't be null" : "";
		messageError += aAccountStatement.getDebitsAmount() == null ? " - Debits Amount can't be null" : "";
		messageError += aAccountStatement.getCreditsAmount() == null ? " - Credits Amount can't be null" : "";
		messageError += aAccountStatement.getAccountingBalance() == null ? " - Accounting Balance can't be null" : "";
		messageError += aAccountStatement.getSignDC() == null ? " - Sign DC can't be null" : "";
		messageError += aAccountStatement.getHour() == null ? " - Hour can't be null" : "";
		messageError += aAccountStatement.getTypeTransaction() == null ? " - Type Transaction can't be null" : "";
		messageError += aAccountStatement.getCauseId() == null ? " - Cause Id can't be null" : "";
		messageError += aAccountStatement.getSequential() == null ? " - Sequential can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
