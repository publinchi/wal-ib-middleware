/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jmoreta
 * @since Oct 9, 2014
 * @version 1.0.0
 */
public class TimeDepositsMovements {
	
	private Product product; 
	private String account;
	private Double amount;
	private Integer subsequence;
	private String currency;
	private String date;
	private Double internationalAmount;
	private String payFormat;
	private Integer sequence;
	private String status;
	private String transactionName;
	private Integer transactionNumber;
	private String valueDate;
	private String beneficiary;
	private Integer currencyId;
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
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the subsequence
	 */
	public Integer getSubsequence() {
		return subsequence;
	}
	/**
	 * @param subsequence the subsequence to set
	 */
	public void setSubsequence(Integer subsequence) {
		this.subsequence = subsequence;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the internationalAmount
	 */
	public Double getInternationalAmount() {
		return internationalAmount;
	}
	/**
	 * @param internationalAmount the internationalAmount to set
	 */
	public void setInternationalAmount(Double internationalAmount) {
		this.internationalAmount = internationalAmount;
	}
	/**
	 * @return the payFormat
	 */
	public String getPayFormat() {
		return payFormat;
	}
	/**
	 * @param payFormat the payFormat to set
	 */
	public void setPayFormat(String payFormat) {
		this.payFormat = payFormat;
	}
	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the transactionName
	 */
	public String getTransactionName() {
		return transactionName;
	}
	/**
	 * @param transactionName the transactionName to set
	 */
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	/**
	 * @return the transactionNumber
	 */
	public Integer getTransactionNumber() {
		return transactionNumber;
	}
	/**
	 * @param transactionNumber the transactionNumber to set
	 */
	public void setTransactionNumber(Integer transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	/**
	 * @return the valueDate
	 */
	public String getValueDate() {
		return valueDate;
	}
	/**
	 * @param valueDate the valueDate to set
	 */
	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}
	/**
	 * @return the beneficiary
	 */
	public String getBeneficiary() {
		return beneficiary;
	}
	/**
	 * @param beneficiary the beneficiary to set
	 */
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	@Override
	public String toString() {
		return "TimeDepositsMovements [product=" + product + ", account="
				+ account + ", amount=" + amount + ", subsequence="
				+ subsequence + ", currency=" + currency + ", date=" + date
				+ ", internationalAmount=" + internationalAmount
				+ ", payFormat=" + payFormat + ", sequence=" + sequence
				+ ", status=" + status + ", transactionName=" + transactionName
				+ ", transactionNumber=" + transactionNumber + ", valueDate="
				+ valueDate + ", beneficiary=" + beneficiary + ", currencyId="
				+ currencyId + "]";
	}
	
	
	
	
}
