package com.cobiscorp.ecobis.orchestration.core.ib.model;

public class DetailsAccountDto {
	private Integer enteBv;

	private Integer productId;

	private Integer currencyId;

	private String accountNumber;

	private String login;

	private Integer enteMis;

	public Integer getEnteBv() {
		return enteBv;
	}

	public void setEnteBv(Integer enteBv) {
		this.enteBv = enteBv;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Integer getEnteMis() {
		return enteMis;
	}

	public void setEnteMis(Integer enteMis) {
		this.enteMis = enteMis;
	}

}
