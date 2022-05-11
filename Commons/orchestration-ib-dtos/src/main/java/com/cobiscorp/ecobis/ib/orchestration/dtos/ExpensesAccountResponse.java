package com.cobiscorp.ecobis.ib.orchestration.dtos;

import com.cobiscorp.ecobis.ib.application.dtos.BaseResponse;

import java.util.List;

public class ExpensesAccountResponse extends BaseResponse {

    private List<ExpensesAccount> expensesAccountList;

    public List<ExpensesAccount> getExpensesAccountList() {
        return expensesAccountList;
    }

    public void setExpensesAccountList(List<ExpensesAccount> expensesAccountList) {
        this.expensesAccountList = expensesAccountList;
    }
}
