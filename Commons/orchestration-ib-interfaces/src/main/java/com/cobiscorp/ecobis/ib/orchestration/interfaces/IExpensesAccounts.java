package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountResponse;

public interface IExpensesAccounts {

    ExpensesAccountResponse getExpensesAccounts(ExpensesAccountRequest request) throws CTSServiceException, CTSInfrastructureException;

    ExpensesAccountResponse getAccountsBalance(ExpensesAccountResponse aListOfAccounts, ExpensesAccountRequest requestDto);

    ExpensesAccountResponse getExpensesAccountsOffline(ExpensesAccountResponse aListOfAccounts, ExpensesAccountRequest requestDto);

}
