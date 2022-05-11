package com.cobiscorp.ecobis.orchestration.ib.expenses.accounts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IExpensesAccounts;
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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnquiriesDetail;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "ExpensesAccountsQuery", immediate = false)
@Service(value = { IExpensesAccounts.class })
@Properties(value = { @Property(name = "service.description", value = "ExpensesAccountsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ExpensesAccountsQuery") })
public class ExpensesAccountsQuery extends SPJavaOrchestrationBase implements IExpensesAccounts {
	private static ILogger logger = LogFactory.getLogger(ExpensesAccountsQuery.class);
	private static final String SP_NAME_LOCAL = "cob_bvirtual..sp_bv_cta_gastos_local";
	private static final String SP_NAME_CENTRAL = "cob_bvirtual..sp_bv_cta_gastos_central";

	@Override
	public ExpensesAccountResponse getExpensesAccounts(ExpensesAccountRequest requestDto) {
		String wInfo = "[ExpensesAccountsQuery][getExpensesAccounts] ";
		logger.logInfo(wInfo + "---------> init task");

		IProcedureRequest request = initProcedureRequest(requestDto.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "19500111");
		request.setSpName(SP_NAME_LOCAL);


		if(null != requestDto.getExpensesAccountId()){
			request.addInputParam("@i_cta_gasto_id", ICTSTypes.SQLINT4, String.valueOf(requestDto.getExpensesAccountId()));
		}

		if(null != requestDto.getGroupCode()){
			request.addInputParam("@i_codigo_grupo", ICTSTypes.SQLINT4, String.valueOf(requestDto.getGroupCode()));
		}

		if(null != requestDto.getMasterAccount()){
			request.addInputParam("@i_cuenta_principal", ICTSTypes.SQLVARCHAR, requestDto.getMasterAccount());
		}

		if(null != requestDto.getCardNumber()){
			request.addInputParam("@i_numero_tarjeta", ICTSTypes.SQLVARCHAR, requestDto.getCardNumber());
		}

		if(null != requestDto.getExpensesAccount()){
			request.addInputParam("@i_cuenta_gasto", ICTSTypes.SQLVARCHAR, requestDto.getExpensesAccount());
		}

		if(null != requestDto.getOperation()){
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, requestDto.getOperation());
		}else{
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}


		ExpensesAccountResponse expensesAccountResponse = new ExpensesAccountResponse();
		List<ExpensesAccount> detailCollection = new ArrayList<ExpensesAccount>();
		ExpensesAccount expensesAccount = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsDetail = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsDetail) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				expensesAccount = new ExpensesAccount();
				expensesAccount.setExpensesAccountId(Integer.parseInt(columns[0].getValue()));
				expensesAccount.setMasterAccountNumber(columns[1].getValue());
				expensesAccount.setExpensesAccountNumber(columns[2].getValue());
				expensesAccount.setOwnerAccountName(columns[3].getValue());
				expensesAccount.setBalance(new Double(columns[4].getValue()));
				expensesAccount.setEmail(columns[5].getValue());
				if(null != columns[6].getValue()){
					expensesAccount.setGroupCode(Integer.parseInt(columns[6].getValue()));
				}
				expensesAccount.setDebitCardNumber(columns[7].getValue());
				if(null != columns[8].getValue()){
					expensesAccount.setDebitCardCode(Integer.parseInt(columns[8].getValue()));
				}
				expensesAccount.setLoginExpensesAccount(columns[9].getValue());
				if(null != columns[10].getValue()) {
					expensesAccount.setLotId(Integer.parseInt(columns[10].getValue()));
				}
				if(null != columns[11].getValue()) {
					expensesAccount.setAgreementCard(Integer.parseInt(columns[11].getValue()));
				}

				detailCollection.add(expensesAccount);
			}
			expensesAccountResponse.setExpensesAccountList(detailCollection);
		} else {
			expensesAccountResponse.setMessages(Utils.returnArrayMessage(pResponse));
		}
		expensesAccountResponse.setReturnCode(pResponse.getReturnCode());

		logger.logInfo(wInfo + "---------> end task");
		return expensesAccountResponse;
	}

	@Override
	public ExpensesAccountResponse getAccountsBalance(ExpensesAccountResponse aListOfAccounts, ExpensesAccountRequest requestDto) {
		String wInfo = "[ExpensesAccountsQuery][getAccountsBalance] ";
		logger.logInfo(wInfo + "---------> init task");

		IProcedureRequest request = initProcedureRequest(requestDto.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "19500111");
		request.setSpName(SP_NAME_CENTRAL);
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");

		StringBuilder wAccounts = new StringBuilder();
		for(ExpensesAccount account : aListOfAccounts.getExpensesAccountList()){
			wAccounts.append(account.getExpensesAccountNumber()).append(",");
		}
		if(wAccounts.length() > 0){
			wAccounts = new StringBuilder(wAccounts.substring(0, wAccounts.length() - 1));
		}

		request.addInputParam("@i_cuentas", ICTSTypes.SQLVARCHAR, wAccounts.toString());

		IProcedureResponse pResponse = executeCoreBanking(request);

		List<ExpensesAccount> aTempAccounts =  new ArrayList<ExpensesAccount>();

		if (pResponse.getReturnCode() == 0) {
			IResultSetBlock resulsetProductBalance = pResponse.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetProductBalance.getData().getRowsAsArray();
			if (rowsTemp.length > 0) {
				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					ExpensesAccount aNewExpense = new ExpensesAccount();
					if (rows[0].getValue() != null){
						aNewExpense.setExpensesAccountNumber(rows[0].getValue());
					}
					if (rows[1].getValue() != null){
						aNewExpense.setOwnerAccountName(rows[1].getValue());
					}
					if (rows[2].getValue() != null){
						aNewExpense.setBalance(new Double(rows[2].getValue()));
					}
					aTempAccounts.add(aNewExpense);
				}
			}

			if(Boolean.FALSE.equals(aTempAccounts.isEmpty())){
				for(ExpensesAccount account : aListOfAccounts.getExpensesAccountList()){
					for (int i = 0; i < aTempAccounts.size(); i++) {
						if(aTempAccounts.get(i).getExpensesAccountNumber().equals(account.getExpensesAccountNumber())){
							account.setBalance(aTempAccounts.get(i).getBalance());
							account.setOwnerAccountName(aTempAccounts.get(i).getOwnerAccountName());
							aTempAccounts.remove(i);
							break;
						}
					}

				}
			}
		}else{
			aListOfAccounts.setMessages(Utils.returnArrayMessage(pResponse));
		}

		aListOfAccounts.setReturnCode(pResponse.getReturnCode());

		logger.logInfo(wInfo + "---------> end task");

		return aListOfAccounts;
	}

	@Override
	public ExpensesAccountResponse getExpensesAccountsOffline(ExpensesAccountResponse aListOfAccounts, ExpensesAccountRequest requestDto) {
		String wInfo = "[ExpensesAccountsQuery][getAccountsBalanceOffline] ";
		logger.logInfo(wInfo + "---------> init task");

		IProcedureRequest request = initProcedureRequest(requestDto.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "19500111");
		request.setSpName(SP_NAME_LOCAL);


		if(null != requestDto.getExpensesAccountId()){
			request.addInputParam("@i_cta_gasto_id", ICTSTypes.SQLINT4, String.valueOf(requestDto.getExpensesAccountId()));
		}

		if(null != requestDto.getGroupCode()){
			request.addInputParam("@i_codigo_grupo", ICTSTypes.SQLINT4, String.valueOf(requestDto.getGroupCode()));
		}

		if(null != requestDto.getMasterAccount()){
			request.addInputParam("@i_cuenta_principal", ICTSTypes.SQLVARCHAR, requestDto.getMasterAccount());
		}

		if(null != requestDto.getCardNumber()){
			request.addInputParam("@i_numero_tarjeta", ICTSTypes.SQLVARCHAR, requestDto.getCardNumber());
		}

		if(null != requestDto.getExpensesAccount()){
			request.addInputParam("@i_cuenta_gasto", ICTSTypes.SQLVARCHAR, requestDto.getExpensesAccount());
		}

		if(null != requestDto.getOperation()){
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, requestDto.getOperation());
		}else{
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "F");
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}


		ExpensesAccountResponse expensesAccountResponse = new ExpensesAccountResponse();
		List<ExpensesAccount> detailCollection = new ArrayList<ExpensesAccount>();
		ExpensesAccount expensesAccount = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsDetail = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsDetail) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				expensesAccount = new ExpensesAccount();
				expensesAccount.setExpensesAccountId(Integer.parseInt(columns[0].getValue()));
				expensesAccount.setMasterAccountNumber(columns[1].getValue());
				expensesAccount.setExpensesAccountNumber(columns[2].getValue());
				expensesAccount.setOwnerAccountName(columns[3].getValue());
				expensesAccount.setBalance(new Double(columns[4].getValue()));
				expensesAccount.setEmail(columns[5].getValue());
				if(null != columns[6].getValue()){
					expensesAccount.setGroupCode(Integer.parseInt(columns[6].getValue()));
				}
				expensesAccount.setDebitCardNumber(columns[7].getValue());
				if(null != columns[8].getValue()){
					expensesAccount.setDebitCardCode(Integer.parseInt(columns[8].getValue()));
				}
				expensesAccount.setLoginExpensesAccount(columns[9].getValue());
				if(null != columns[10].getValue()) {
					expensesAccount.setLotId(Integer.parseInt(columns[10].getValue()));
				}
				if(null != columns[11].getValue()) {
					expensesAccount.setAgreementCard(Integer.parseInt(columns[11].getValue()));
				}
				detailCollection.add(expensesAccount);
			}
			expensesAccountResponse.setExpensesAccountList(detailCollection);
		} else {
			expensesAccountResponse.setMessages(Utils.returnArrayMessage(pResponse));
		}
		expensesAccountResponse.setReturnCode(pResponse.getReturnCode());

		logger.logInfo(wInfo + "---------> end task");
		return expensesAccountResponse;

	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}


}
