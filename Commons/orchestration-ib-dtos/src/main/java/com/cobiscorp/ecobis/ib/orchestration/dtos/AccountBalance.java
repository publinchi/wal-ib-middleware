/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author mvelez
 * @since Sep 25, 2014
 * @version 1.0.0
 */
public class AccountBalance {
	private Product  productNumber;
	private Client   clientName;
	private Currency currencyName;
	private String   executiveName;
	private String   deliveryAdress;
	private Double   availableBalance;
	private Double   accountingBalance;
	private Double   lastCutoffBalance;
	private Double   averageBalance;
	private String   lastOperationDate;
	private String   lastCutoffDate;
	private String   nextCutoffDate;
	private String   clientPhone;
	private String   clientEmail;
	private Office   officeName;
	private Double   toDrawBalance;
	/**
	 * @return the productNumber
	 */
	public Product getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(Product productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @return the clientName
	 */
	public Client getClientName() {
		return clientName;
	}
	/**
	 * @param clientName the clientName to set
	 */
	public void setClientName(Client clientName) {
		this.clientName = clientName;
	}
	/**
	 * @return the currencyName
	 */
	public Currency getCurrencyName() {
		return currencyName;
	}
	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(Currency currencyName) {
		this.currencyName = currencyName;
	}
	/**
	 * @return the executiveName
	 */
	public String getExecutiveName() {
		return executiveName;
	}
	/**
	 * @param executiveName the executiveName to set
	 */
	public void setExecutiveName(String executiveName) {
		this.executiveName = executiveName;
	}
	/**
	 * @return the deliveryAdress
	 */
	public String getDeliveryAdress() {
		return deliveryAdress;
	}
	/**
	 * @param deliveryAdress the deliveryAdress to set
	 */
	public void setDeliveryAdress(String deliveryAdress) {
		this.deliveryAdress = deliveryAdress;
	}
	/**
	 * @return the availableBalance
	 */
	public Double getAvailableBalance() {
		return availableBalance;
	}
	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	/**
	 * @return the accountingBalance
	 */
	public Double getAccountingBalance() {
		return accountingBalance;
	}
	/**
	 * @param accountingBalance the accountingBalance to set
	 */
	public void setAccountingBalance(Double accountingBalance) {
		this.accountingBalance = accountingBalance;
	}
	/**
	 * @return the lastCutoffBalance
	 */
	public Double getLastCutoffBalance() {
		return lastCutoffBalance;
	}
	/**
	 * @param lastCutoffBalance the lastCutoffBalance to set
	 */
	public void setLastCutoffBalance(Double lastCutoffBalance) {
		this.lastCutoffBalance = lastCutoffBalance;
	}
	/**
	 * @return the averageBalance
	 */
	public Double getAverageBalance() {
		return averageBalance;
	}
	/**
	 * @param averageBalance the averageBalance to set
	 */
	public void setAverageBalance(Double averageBalance) {
		this.averageBalance = averageBalance;
	}
	/**
	 * @return the lastOperationDate
	 */
	public String getLastOperationDate() {
		return lastOperationDate;
	}
	/**
	 * @param lastOperationDate the lastOperationDate to set
	 */
	public void setLastOperationDate(String lastOperationDate) {
		this.lastOperationDate = lastOperationDate;
	}
	/**
	 * @return the lastCutoffDate
	 */
	public String getLastCutoffDate() {
		return lastCutoffDate;
	}
	/**
	 * @param lastCutoffDate the lastCutoffDate to set
	 */
	public void setLastCutoffDate(String lastCutoffDate) {
		this.lastCutoffDate = lastCutoffDate;
	}
	/**
	 * @return the nextCutoffDate
	 */
	public String getNextCutoffDate() {
		return nextCutoffDate;
	}
	/**
	 * @param nextCutoffDate the nextCutoffDate to set
	 */
	public void setNextCutoffDate(String nextCutoffDate) {
		this.nextCutoffDate = nextCutoffDate;
	}
	/**
	 * @return the clientPhone
	 */
	public String getClientPhone() {
		return clientPhone;
	}
	/**
	 * @param clientPhone the clientPhone to set
	 */
	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}
	/**
	 * @return the clientEmail
	 */
	public String getClientEmail() {
		return clientEmail;
	}
	/**
	 * @param clientEmail the clientEmail to set
	 */
	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}
	/**
	 * @return the officeName
	 */
	public Office getOfficeName() {
		return officeName;
	}
	/**
	 * @param officeName the officeName to set
	 */
	public void setOfficeName(Office officeName) {
		this.officeName = officeName;
	}
	/**
	 * @return the toDrawBalance
	 */
	public Double getToDrawBalance() {
		return toDrawBalance;
	}
	/**
	 * @param toDrawBalance the toDrawBalance to set
	 */
	public void setToDrawBalance(Double toDrawBalance) {
		this.toDrawBalance = toDrawBalance;
	}
}
