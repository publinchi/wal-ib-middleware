package com.cobiscorp.ecobis.orchestration.core.ib.accounts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.MasterAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountBalance;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAccountStatementQuery;

@Component(name = "AccountStatementQuery", immediate = false)
@Service(value = { ICoreServiceAccountStatementQuery.class })
@Properties(value = { @Property(name = "service.description", value = "AccountStatementQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountStatementQuery") })

public class AccountStatementQuery extends SPJavaOrchestrationBase implements ICoreServiceAccountStatementQuery {
	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(AccountStatementQuery.class);

	private static final String SP_SAVINGS_ACCOUNT = "cob_ahorros..sp_tr04_cons_estcuenta";
	private static final String SP_CHECKING_ACCOUNT = "cob_cuentas..sp_tr03_cons_estcuenta";
	private static final String TRN_SAVINGS_ACCOUNT = "18383";
	private static final String TRN_CHECKING_ACCOUNT = "18309";

	/* return Account Statement */
	private static final int COL_TRANSACTION_DATE = 0;
	private static final int COL_REFERENCE = 1;
	private static final int COL_DESCRIPTION = 2;
	private static final int COL_DEBITS_AMOUNT = 3;
	private static final int COL_CREDITS_AMOUNT = 4;
	private static final int COL_ACCOUNTING_BALANCE = 5;
	private static final int COL_SIGN_DC = 6;
	private static final int COL_HOUR = 7;
	private static final int COL_OPERATION_TYPE = 8;
	private static final int COL_CAUSE_ID = 9;
	private static final int COL_SEQUENTIAL = 10;

	/* return Account Balance */
	private static final int COL_PRODUCT_NUMBER = 0;
	private static final int COL_CLIENT_NAME = 1;
	private static final int COL_CURRENCY_NAME = 2;
	private static final int COL_EXECUTIVE_NAME = 3;
	private static final int COL_DELIVERY_ADDRESS = 4;
	private static final int COL_AVAILABLE_BALANCE = 5;
	private static final int COL_ACCOUNTING_BALANCE2 = 6;
	private static final int COL_LAST_CUT_OFF_BALANCE = 7;
	private static final int COL_AVERAGE_BALANCE = 8;
	private static final int COL_LAST_OPERATION_DATE = 9;
	private static final int COL_LAST_CUT_OFF_DATE = 10;
	private static final int COL_NEXT_CUT_OFF_DATE = 11;
	private static final int COL_CLIENT_PHONE = 12;
	private static final int COL_CLIENT_EMAIL = 13;
	private static final int COL_OFFICE_NAME = 14;
	private static final int COL_TO_DRAWN_BALANCE = 15;

	/**** Output: getSelectionMasterAccount *****/
	private static final String SP_NAME_SELECTION_MASTER = "cob_bvirtual..sp_asocia_producto_bv";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceAccountStatementQuery#GetSavingsAccountStatement(com.
	 * cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest)
	 ********************************************************************************************************/
	@Override
	public AccountStatementBalanceResponse GetSavingsAccountStatement(AccountStatementRequest aAccountStatementRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<INICIANDO SERVICIO: GetSavingsAccountStatement>>>");
			logger.logInfo("<<<RESPUESTA DUMMY COBIS GENERADA>>>");
		}

		IProcedureResponse pResponse = Execution(SP_SAVINGS_ACCOUNT, aAccountStatementRequest, TRN_SAVINGS_ACCOUNT);
		AccountStatementBalanceResponse accountStatementBalanceResponse = transformToAccountStatementBalanceResponse(
				pResponse);
		return accountStatementBalanceResponse;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceAccountStatementQuery#GetCheckingAccountStatement(com.
	 * cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest)
	 ********************************************************************************************************/
	@Override
	public AccountStatementBalanceResponse GetCheckingAccountStatement(AccountStatementRequest aAccountStatementRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<INICIANDO SERVICIO: GetCheckingAccountStatement>>>");
			logger.logInfo("<<<RESPUESTA DUMMY COBIS GENERADA>>>");
		}

		IProcedureResponse pResponse = Execution(SP_CHECKING_ACCOUNT, aAccountStatementRequest, TRN_CHECKING_ACCOUNT);
		AccountStatementBalanceResponse accountStatementBalanceResponse = transformToAccountStatementBalanceResponse(
				pResponse);
		return accountStatementBalanceResponse;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 */
	private IProcedureResponse Execution(String SpName, AccountStatementRequest aAccountStatementRequest, String Trn)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = new ProcedureRequestAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: Execution>>>");

		// IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, Trn);

		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, Trn);
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1,
				aAccountStatementRequest.getProduct().getProductType().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aAccountStatementRequest.getMon().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aAccountStatementRequest.getLogin());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aAccountStatementRequest.getProduct().getProductNumber());
		request.addInputParam("@i_sec", ICTSTypes.SQLINT4, aAccountStatementRequest.getSequential());
		request.addInputParam("@i_sec_alt", ICTSTypes.SQLINT4, aAccountStatementRequest.getAlternateCode());
		request.addInputParam("@i_diario", ICTSTypes.SQLINT4, aAccountStatementRequest.getDaily().toString());
		// request.addInputParam("@i_fecha" , ICTSTypes.SQLVARCHAR,
		// aAccountStatementRequest.getAccountStatement().getStringDate());
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, aAccountStatementRequest.getInitialDateString());
		request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, aAccountStatementRequest.getFinalDateString());
		// request.addInputParam("@i_hora" , ICTSTypes.SQLVARCHAR,
		// aAccountStatementRequest.getAccountStatement().getHour());

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request>>>");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<IMPLEMENTATION: Response >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response>>>");
		}
		return pResponse;
	}

	/*
	 * 
	 */
	private AccountStatementBalanceResponse transformToAccountStatementBalanceResponse(
			IProcedureResponse aProcedureResponse) {
		AccountStatementBalanceResponse AccountStatementBalanceResp = new AccountStatementBalanceResponse();
		List<AccountStatement> accountStatementCollection = new ArrayList<AccountStatement>();
		List<AccountBalance> accountBalanceCollection = new ArrayList<AccountBalance>();
		AccountStatement aAccountStatement = null;
		AccountBalance aAccountBalance = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<ProcedureResponse: >>>" + aProcedureResponse.getProcedureResponseAsString());
		}
		// ITO Correccion Validacion 01/14/2015 - Se añade el if
		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsAccountStatement = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();
			IResultSetRow[] rowsAccountBalance = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			aAccountStatement = new AccountStatement();
			aAccountBalance = new AccountBalance();

			for (IResultSetRow iResultSetRow : rowsAccountStatement) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAccountStatement = null;
				aAccountStatement = new AccountStatement();

				aAccountStatement.setStringDate(columns[COL_TRANSACTION_DATE].getValue());
				aAccountStatement.setReference(columns[COL_REFERENCE].getValue());
				if (columns[COL_DESCRIPTION].getValue() == null) {
					aAccountStatement.setDescription("");
				}
				if (columns[COL_DESCRIPTION].getValue() != null) {
					aAccountStatement.setDescription(columns[COL_DESCRIPTION].getValue());
				}
				aAccountStatement.setDebitsAmount(new BigDecimal(columns[COL_DEBITS_AMOUNT].getValue()));
				aAccountStatement.setCreditsAmount(new BigDecimal(columns[COL_CREDITS_AMOUNT].getValue()));
				aAccountStatement.setAccountingBalance(new BigDecimal(columns[COL_ACCOUNTING_BALANCE].getValue()));
				aAccountStatement.setSignDC(columns[COL_SIGN_DC].getValue());
				aAccountStatement.setHour(columns[COL_HOUR].getValue());
				aAccountStatement.setTypeTransaction(columns[COL_OPERATION_TYPE].getValue());
				aAccountStatement.setCauseId(columns[COL_CAUSE_ID].getValue());
				aAccountStatement.setSequential(Integer.parseInt(columns[COL_SEQUENTIAL].getValue()));

				accountStatementCollection.add(aAccountStatement);
			}
			AccountStatementBalanceResp.setAccountStatementsCollection(accountStatementCollection);

			Product product = new Product();
			Client client = new Client();
			Currency currency = new Currency();
			Office office = new Office();

			for (IResultSetRow iResultSetRow : rowsAccountBalance) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAccountBalance = new AccountBalance();
				product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue());
				client.setCompleteName(columns[COL_CLIENT_NAME].getValue());
				currency.setCurrencyDescription(columns[COL_CURRENCY_NAME].getValue());
				if (columns[COL_OFFICE_NAME].getValue() == null) {
					office.setDescription("");
				}
				if (columns[COL_OFFICE_NAME].getValue() != null) {
					office.setDescription(columns[COL_OFFICE_NAME].getValue());
				}

				aAccountBalance.setProductNumber(product);
				aAccountBalance.setClientName(client);
				aAccountBalance.setCurrencyName(currency);
				aAccountBalance.setExecutiveName(columns[COL_EXECUTIVE_NAME].getValue());
				aAccountBalance.setDeliveryAdress(columns[COL_DELIVERY_ADDRESS].getValue());
				if (columns[COL_AVAILABLE_BALANCE].getValue() == null) {
					aAccountBalance.setAvailableBalance(0.00);
				}
				if (columns[COL_AVAILABLE_BALANCE].getValue() != null) {
					aAccountBalance.setAvailableBalance(Double.parseDouble(columns[COL_AVAILABLE_BALANCE].getValue()));
				}
				if (columns[COL_ACCOUNTING_BALANCE2].getValue() == null) {
					aAccountBalance.setAccountingBalance(0.00);
				}
				if (columns[COL_ACCOUNTING_BALANCE2].getValue() != null) {
					aAccountBalance
							.setAccountingBalance(Double.parseDouble(columns[COL_ACCOUNTING_BALANCE2].getValue()));
				}
				if (columns[COL_LAST_CUT_OFF_BALANCE].getValue() == null) {
					aAccountBalance.setLastCutoffBalance(0.00);
				}
				if (columns[COL_LAST_CUT_OFF_BALANCE].getValue() != null) {
					aAccountBalance
							.setLastCutoffBalance(Double.parseDouble(columns[COL_LAST_CUT_OFF_BALANCE].getValue()));
				}
				if (columns[COL_AVERAGE_BALANCE].getValue() == null) {
					aAccountBalance.setAverageBalance(0.00);
				}
				if (columns[COL_AVERAGE_BALANCE].getValue() != null) {
					aAccountBalance.setAverageBalance(Double.parseDouble(columns[COL_AVERAGE_BALANCE].getValue()));
				}
				if (columns[COL_LAST_OPERATION_DATE].getValue() == null) {
					aAccountBalance.setLastOperationDate(" ");
				}
				if (columns[COL_LAST_OPERATION_DATE].getValue() != null) {
					aAccountBalance.setLastOperationDate(columns[COL_LAST_OPERATION_DATE].getValue());
				}
				aAccountBalance.setLastCutoffDate(columns[COL_LAST_CUT_OFF_DATE].getValue());
				aAccountBalance.setNextCutoffDate(columns[COL_NEXT_CUT_OFF_DATE].getValue());
				if (columns[COL_CLIENT_PHONE].getValue() == null) {
					aAccountBalance.setClientPhone("");
				}
				if (columns[COL_CLIENT_PHONE].getValue() != null) {
					aAccountBalance.setClientPhone(columns[COL_CLIENT_PHONE].getValue());
				}
				if (columns[COL_CLIENT_EMAIL].getValue() == null) {
					aAccountBalance.setClientEmail("");
				}
				if (columns[COL_CLIENT_EMAIL].getValue() != null) {
					aAccountBalance.setClientEmail(columns[COL_CLIENT_EMAIL].getValue());
				}
				aAccountBalance.setOfficeName(office);
				if (columns[COL_TO_DRAWN_BALANCE].getValue() == null) {
					aAccountBalance.setToDrawBalance(0.00);
				}
				if (columns[COL_TO_DRAWN_BALANCE].getValue() != null) {
					aAccountBalance.setToDrawBalance(Double.parseDouble(columns[COL_TO_DRAWN_BALANCE].getValue()));
				}

				accountBalanceCollection.add(aAccountBalance);
			}

			AccountStatementBalanceResp.setAccountBalanceCollection(accountBalanceCollection);
		} else {
			// Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			AccountStatementBalanceResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		AccountStatementBalanceResp.setReturnCode(aProcedureResponse.getReturnCode());
		return AccountStatementBalanceResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * jmoreta
	 */
	@Override
	public IProcedureResponse getSelectionMasterAccount(MasterAccountRequest aMasterAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSelectionMasterAccount");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SELECTION_MASTER, aMasterAccountRequest,
				"getSelectionMasterAccount");
		// IProcedureResponse ppProcedureResponse =
		// transformSelectionMasterAccountResponse(pResponse,
		// "getSelectionMasterAccount");

		return pResponse;

	}

	/**
	 * @autor jmoreta
	 * @param spNameSelectionMaster
	 * @param aMasterAccountRequest
	 * @param trn
	 * @return
	 */
	private IProcedureResponse Execution(String spNameSelectionMaster, MasterAccountRequest aMasterAccountRequest,
			String trn) {

		IProcedureRequest request = initProcedureRequest(aMasterAccountRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18522");
		request.setSpName(spNameSelectionMaster);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18522");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "U");
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "V");
		request.addInputParam("@i_cuenta_cobro", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_autorizado", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_modo_dblaut", ICTSTypes.SQLINT4, "0");

		request.addInputParam("@i_moneda", ICTSTypes.SQLINT2,
				(aMasterAccountRequest.getCurrencyId().getCurrencyId() == null ? "0"
						: aMasterAccountRequest.getCurrencyId().getCurrencyId()).toString());
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, (aMasterAccountRequest.getEntityId().getEnte() == null
				? "0" : aMasterAccountRequest.getEntityId().getEnte()).toString());
		request.addInputParam("@i_alias", ICTSTypes.SQLVARCHAR, aMasterAccountRequest.getProduct().getProductAlias());
		request.addInputParam("@i_producto", ICTSTypes.SQLINT1,
				(aMasterAccountRequest.getProduct().getProductId() == null ? "0"
						: aMasterAccountRequest.getProduct().getProductId()).toString());
		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
				(aMasterAccountRequest.getProduct().getProductNumber() == null ? "0"
						: aMasterAccountRequest.getProduct().getProductNumber()).toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1,
				(aMasterAccountRequest.getServiceId() == null ? "0" : aMasterAccountRequest.getServiceId()).toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aMasterAccountRequest.getUserName().getName());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_moneda: " + aMasterAccountRequest.getCurrencyId().getCurrencyId());
			logger.logDebug("@i_cliente: " + aMasterAccountRequest.getEntityId().getEnte().toString());
			logger.logDebug("@i_alias: " + aMasterAccountRequest.getProduct().getProductAlias());
			logger.logDebug("@i_producto: " + aMasterAccountRequest.getProduct().getProductId());
			logger.logDebug("@i_cuenta: " + aMasterAccountRequest.getProduct().getProductNumber());
			logger.logDebug("@i_servicio: " + aMasterAccountRequest.getServiceId());
			logger.logDebug("@i_login: " + aMasterAccountRequest.getUserName().getName());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking MasterAccountRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response MasterAccount: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response MasterAccount *** ");
		}
		return pResponse;
	}
}
