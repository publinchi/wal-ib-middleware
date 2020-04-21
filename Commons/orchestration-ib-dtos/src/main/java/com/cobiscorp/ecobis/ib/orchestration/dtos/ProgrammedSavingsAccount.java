/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gcondo
 * @since Sep 26, 2014
 * @version 1.0.0
 */
public class ProgrammedSavingsAccount {
	private String account;
	private Currency currencyId;
	private Client client;
	private double productBalance;
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * @return the currencyId
	 */
	public Currency getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Currency currencyId) {
		this.currencyId = currencyId;
	}
	
	/**
	 * @return the productBalance
	 */
	public double getProductBalance() {
		return productBalance;
	}
	/**
	 * @param productBalance the productBalance to set
	 */
	public void setProductBalance(double productBalance) {
		this.productBalance = productBalance;
	}
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	
	
	

}
