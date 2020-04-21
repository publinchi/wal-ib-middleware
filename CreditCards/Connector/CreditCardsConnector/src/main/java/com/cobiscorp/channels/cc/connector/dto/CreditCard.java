package com.cobiscorp.channels.cc.connector.dto;
/**
 * Class that define the response object in a query for showing a list movements detail
 * @author djarrin
 *
 */
public class CreditCard {
	/*
	"productId":83,
	"productName":"TARCRE_4567",
	"productNumber":"4567456745674567",
	"productAlias":"",
	"aliasName":"",
	------------------------------------
	"currencyId":2,
	"currencySymbol":"USD",
	"currencyName":"DOLAR",
	"accountingBalance":5000,
	"availableBalance":10,
	"drawBalance":4990,
	-------------------------------------
	"productAbbreviation":"83",
	"expirationDate":"29/05/2016",
	"ownerName":null,
	"ownerId":null,
	"idLocalBank":null,
	"rate":"0"		 
	 */	
	private String productId;
	private String productName;
	private String productNumber;
	private String productAlias;
	private String aliasName;
	private String currencyId;
	private String currencySymbol;
	private String currencyName;
	private Double accountingBalance;
	private Double availableBalance;
	private Double drawBalance;
	private String productAbbreviation;
	private String expirationDate;
	private String ownerName;
	private String ownerId;
	private String idLocalBank;
	private int rate;	
	
	public CreditCard (String productId, String productName,  String productNumber, String productAlias, 
			String aliasName, String currencyId, String currencySymbol, String currencyName, Double accountingBalance, 
			Double availableBalance, Double drawBalance, String productAbbreviation, String expirationDate, String ownerName, 
			String ownerId, String idLocalBank, int rate) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productNumber = productNumber;
		this.productAlias = productAlias;
		this.aliasName = aliasName;
		this.currencyId = currencyId;
		this.currencySymbol = currencySymbol;
		this.currencyName = currencyName;
		this.accountingBalance = accountingBalance;
		this.availableBalance = availableBalance;
		this.drawBalance = drawBalance;
		this.productAbbreviation = productAbbreviation;
		this.expirationDate = expirationDate;
		this.ownerName = ownerName;
		this.ownerId = ownerId;
		this.idLocalBank = idLocalBank;
		this.rate = rate;		
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getProductAlias() {
		return productAlias;
	}

	public void setProductAlias(String productAlias) {
		this.productAlias = productAlias;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Double getAccountingBalance() {
		return accountingBalance;
	}

	public void setAccountingBalance(Double accountingBalance) {
		this.accountingBalance = accountingBalance;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Double getDrawBalance() {
		return drawBalance;
	}

	public void setDrawBalance(Double drawBalance) {
		this.drawBalance = drawBalance;
	}

	public String getProductAbbreviation() {
		return productAbbreviation;
	}

	public void setProductAbbreviation(String productAbbreviation) {
		this.productAbbreviation = productAbbreviation;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getIdLocalBank() {
		return idLocalBank;
	}

	public void setIdLocalBank(String idLocalBank) {
		this.idLocalBank = idLocalBank;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

}


