package com.cobiscorp.ecobis.orchestration.core.ib.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Dto for set information accounts.
 *
 * @author schancay
 * @since Jul 4, 2014
 * @version 1.0.0
 */
public class ConsolidateAccountsDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productId;
	private String productDescription;
	private String productNemonic;
	private String productNumber;
	private BigDecimal balanceRotate;
	private BigDecimal equityBalance;
	private BigDecimal balanceRotatePrevious;
	private BigDecimal equityBalancePrevious;
	private Integer currencyId;
	private String currencyNemonic;
	private String currencyDescription;
	private String accountAlias;

	/**
	 * @return the accountAlias
	 */
	public String getAccountAlias() {
		return accountAlias;
	}

	/**
	 * @return the balanceRotate
	 */
	public BigDecimal getBalanceRotate() {
		return balanceRotate;
	}

	/**
	 * @return the balanceRotatePrevious
	 */
	public BigDecimal getBalanceRotatePrevious() {
		return balanceRotatePrevious;
	}

	/**
	 * @return the currencyDescription
	 */
	public String getCurrencyDescription() {
		return currencyDescription;
	}

	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}

	/**
	 * @return the currencyNemonic
	 */
	public String getCurrencyNemonic() {
		return currencyNemonic;
	}

	/**
	 * @return the equityBalance
	 */
	public BigDecimal getEquityBalance() {
		return equityBalance;
	}

	/**
	 * @return the equityBalancePrevious
	 */
	public BigDecimal getEquityBalancePrevious() {
		return equityBalancePrevious;
	}

	/**
	 * @return the productDescription
	 */
	public String getProductDescription() {
		return productDescription;
	}

	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @return the productNemonic
	 */
	public String getProductNemonic() {
		return productNemonic;
	}

	/**
	 * @return the productNumber
	 */
	public String getProductNumber() {
		return productNumber;
	}

	/**
	 * @param accountAlias
	 *            the accountAlias to set
	 */
	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	/**
	 * @param balanceRotate
	 *            the balanceRotate to set
	 */
	public void setBalanceRotate(BigDecimal balanceRotate) {
		this.balanceRotate = balanceRotate;
	}

	/**
	 * @param balanceRotatePrevious
	 *            the balanceRotatePrevious to set
	 */
	public void setBalanceRotatePrevious(BigDecimal balanceRotatePrevious) {
		this.balanceRotatePrevious = balanceRotatePrevious;
	}

	/**
	 * @param currencyDescription
	 *            the currencyDescription to set
	 */
	public void setCurrencyDescription(String currencyDescription) {
		this.currencyDescription = currencyDescription;
	}

	/**
	 * @param currencyId
	 *            the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * @param currencyNemonic
	 *            the currencyNemonic to set
	 */
	public void setCurrencyNemonic(String currencyNemonic) {
		this.currencyNemonic = currencyNemonic;
	}

	/**
	 * @param equityBalance
	 *            the equityBalance to set
	 */
	public void setEquityBalance(BigDecimal equityBalance) {
		this.equityBalance = equityBalance;
	}

	/**
	 * @param equityBalancePrevious
	 *            the equityBalancePrevious to set
	 */
	public void setEquityBalancePrevious(BigDecimal equityBalancePrevious) {
		this.equityBalancePrevious = equityBalancePrevious;
	}

	/**
	 * @param productDescription
	 *            the productDescription to set
	 */
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	/**
	 * @param productNemonic
	 *            the productNemonic to set
	 */
	public void setProductNemonic(String productNemonic) {
		this.productNemonic = productNemonic;
	}

	/**
	 * @param productNumber
	 *            the productNumber to set
	 */
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConsolidateAccountsDto [productId=" + productId + ", productDescription=" + productDescription
				+ ", productNemonic=" + productNemonic + ", productNumber=" + productNumber + ", balanceRotate="
				+ balanceRotate + ", equityBalance=" + equityBalance + ", balanceRotatePrevious="
				+ balanceRotatePrevious + ", equityBalancePrevious=" + equityBalancePrevious + ", currencyId="
				+ currencyId + ", currencyNemonic=" + currencyNemonic + ", currencyDescription=" + currencyDescription
				+ ", accountAlias=" + accountAlias + "]";
	}
}
