package com.cobiscorp.ecobis.ib.orchestration.dtos;

import com.cobiscorp.ecobis.ib.application.dtos.BaseRequest;

public class ExpensesAccountRequest extends BaseRequest {
    private Integer expensesAccountId;
    private String login;
    private String ente;
    private Integer groupCode;
    private String masterAccount;
    private String cardNumber;
    private Double balance;
    private String operation;
    private String expensesAccount;

    public Integer getExpensesAccountId(){
        return this.expensesAccountId;
    }
    public void setExpensesAccountId(Integer expensesAccountId ){
        this.expensesAccountId=expensesAccountId;
    }
    public String getLogin(){
        return this.login;
    }
    public void setLogin(String login ){
        this.login=login;
    }
    public String getEnte(){
        return this.ente;
    }
    public void setEnte(String ente ){
        this.ente=ente;
    }
    public Integer getGroupCode(){
        return this.groupCode;
    }
    public void setGroupCode(Integer groupCode ){
        this.groupCode=groupCode;
    }
    public String getMasterAccount(){
        return this.masterAccount;
    }
    public void setMasterAccount(String masterAccount ){
        this.masterAccount=masterAccount;
    }
    public String getCardNumber(){
        return this.cardNumber;
    }
    public void setCardNumber(String cardNumber ){
        this.cardNumber=cardNumber;
    }
    public Double getBalance(){
        return this.balance;
    }
    public void setBalance(Double balance ){
        this.balance=balance;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getExpensesAccount() {return expensesAccount;}
    public void setExpensesAccount(String expensesAccount) {this.expensesAccount = expensesAccount;}
}
