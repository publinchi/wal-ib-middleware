package com.cobiscorp.ecobis.ib.orchestration.dtos;

public class ExpensesAccount {
    private Integer expensesAccountId;
    private String masterAccountNumber;
    private String expensesAccountNumber;
    private String ownerAccountName;
    private Integer groupCode;
    private String email;
    private Double balance;
    private int debitCardCode;
    private String debitCardNumber;
    private String loginExpensesAccount;
    private Integer lotId;
    private Integer agreementCard;

    public Integer getExpensesAccountId(){
        return this.expensesAccountId;
    }
    public void setExpensesAccountId(Integer expensesAccountId ){
        this.expensesAccountId=expensesAccountId;
    }
    public String getMasterAccountNumber(){
        return this.masterAccountNumber;
    }
    public void setMasterAccountNumber(String masterAccountNumber ){
        this.masterAccountNumber=masterAccountNumber;
    }
    public String getExpensesAccountNumber(){
        return this.expensesAccountNumber;
    }
    public void setExpensesAccountNumber(String expensesAccountNumber ){
        this.expensesAccountNumber=expensesAccountNumber;
    }
    public String getOwnerAccountName(){
        return this.ownerAccountName;
    }
    public void setOwnerAccountName(String ownerAccountName ){
        this.ownerAccountName=ownerAccountName;
    }
    public Integer getGroupCode(){
        return this.groupCode;
    }
    public void setGroupCode(Integer groupCode ){
        this.groupCode=groupCode;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email ){
        this.email=email;
    }
    public Double getBalance(){
        return this.balance;
    }
    public void setBalance(Double balance ){
        this.balance=balance;
    }
    public int getDebitCardCode(){
        return this.debitCardCode;
    }
    public void setDebitCardCode(int debitCardCode ){
        this.debitCardCode=debitCardCode;
    }
    public String getDebitCardNumber(){
        return this.debitCardNumber;
    }
    public void setDebitCardNumber(String debitCardNumber ){
        this.debitCardNumber=debitCardNumber;
    }
    public String getLoginExpensesAccount() {return loginExpensesAccount;}
    public void setLoginExpensesAccount(String loginExpensesAccount) {this.loginExpensesAccount = loginExpensesAccount;}
    public Integer getLotId() {return lotId;}
    public void setLotId(Integer lotId) {this.lotId = lotId;}
    public Integer getAgreementCard() {return agreementCard;}
    public void setAgreementCard(Integer agreementCard) {this.agreementCard = agreementCard;}

    @Override
    public String toString() {
        return "ExpensesAccount{" +
                "expensesAccountId=" + expensesAccountId +
                ", masterAccountNumber='" + masterAccountNumber + '\'' +
                ", expensesAccountNumber='" + expensesAccountNumber + '\'' +
                ", ownerAccountName='" + ownerAccountName + '\'' +
                ", groupCode=" + groupCode +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", debitCardCode=" + debitCardCode +
                ", debitCardNumber='" + debitCardNumber + '\'' +
                ", loginExpensesAccount='" + loginExpensesAccount + '\'' +
                ", lotId='" + lotId + '\'' +
                ", agreementCard='" + agreementCard + '\'' +
                '}';
    }
}
