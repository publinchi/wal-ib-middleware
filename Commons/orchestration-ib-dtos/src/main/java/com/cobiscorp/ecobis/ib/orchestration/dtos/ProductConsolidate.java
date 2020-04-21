/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author schancay
 * @since Sep 2, 2014
 * @version 1.0.0
 */
public class ProductConsolidate {
	/**
	 * Account balance previous
	 *
	 * @return
	 */
	private BalanceProduct previousBalance;

	/**
	 * Account balance
	 */
	private BalanceProduct balance;

	/**
	 * Product account
	 */
	private Product product;

	/**
	 * Currency account
	 */
	private Currency currency;

	/**
	 * @return the balance
	 */
	public BalanceProduct getBalance() {
		return balance;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @return the previousBalance
	 */
	public BalanceProduct getPreviousBalance() {
		return previousBalance;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(BalanceProduct balance) {
		this.balance = balance;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @param previousBalance
	 *            the previousBalance to set
	 */
	public void setPreviousBalance(BalanceProduct previousBalance) {
		this.previousBalance = previousBalance;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductCollection [previousBalance=" + previousBalance + ", balance=" + balance + ", product=" + product + ", currency=" + currency + "]";
	}
}
