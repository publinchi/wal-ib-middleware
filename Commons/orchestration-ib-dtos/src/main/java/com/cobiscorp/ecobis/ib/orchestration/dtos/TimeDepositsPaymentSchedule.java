/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class TimeDepositsPaymentSchedule {
	
	private Integer quota;
	private String paymentDate;
	private Double quotaAmount;
	private Integer entity;
	private String operationDescription;
	private String addressDescription;
	private String officeName;
	private String bankNumberOperation;
	private String depositTypeDescription;
	private Double amount;
	private String paymentDescription;
	private Integer currency;
	private Double rate;
	private String expirationDate;
	private String status;
	private Integer operationDaysNumber;
	private String insertDate;
	private String quotaValue;
	private Integer quotaDaysNumber;
	private String lastPaymentDate;	
	private Double interestEarned;
	/**
	 * @return the quota
	 */
	public Integer getQuota() {
		return quota;
	}
	/**
	 * @param quota the quota to set
	 */
	public void setQuota(Integer quota) {
		this.quota = quota;
	}
	/**
	 * @return the paymentDate
	 */
	public String getPaymentDate() {
		return paymentDate;
	}
	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	/**
	 * @return the quotaAmount
	 */
	public Double getQuotaAmount() {
		return quotaAmount;
	}
	/**
	 * @param quotaAmount the quotaAmount to set
	 */
	public void setQuotaAmount(Double quotaAmount) {
		this.quotaAmount = quotaAmount;
	}
	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}
	/**
	 * @return the operationDescription
	 */
	public String getOperationDescription() {
		return operationDescription;
	}
	/**
	 * @param operationDescription the operationDescription to set
	 */
	public void setOperationDescription(String operationDescription) {
		this.operationDescription = operationDescription;
	}
	/**
	 * @return the addressDescription
	 */
	public String getAddressDescription() {
		return addressDescription;
	}
	/**
	 * @param addressDescription the addressDescription to set
	 */
	public void setAddressDescription(String addressDescription) {
		this.addressDescription = addressDescription;
	}
	/**
	 * @return the officeName
	 */
	public String getOfficeName() {
		return officeName;
	}
	/**
	 * @param officeName the officeName to set
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	/**
	 * @return the bankNumberOperation
	 */
	public String getBankNumberOperation() {
		return bankNumberOperation;
	}
	/**
	 * @param bankNumberOperation the bankNumberOperation to set
	 */
	public void setBankNumberOperation(String bankNumberOperation) {
		this.bankNumberOperation = bankNumberOperation;
	}
	/**
	 * @return the depositTypeDescription
	 */
	public String getDepositTypeDescription() {
		return depositTypeDescription;
	}
	/**
	 * @param depositTypeDescription the depositTypeDescription to set
	 */
	public void setDepositTypeDescription(String depositTypeDescription) {
		this.depositTypeDescription = depositTypeDescription;
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
	 * @return the paymentDescription
	 */
	public String getPaymentDescription() {
		return paymentDescription;
	}
	/**
	 * @param paymentDescription the paymentDescription to set
	 */
	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}
	/**
	 * @return the currency
	 */
	public Integer getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Integer currency) {
		this.currency = currency;
	}
	/**
	 * @return the rate
	 */
	public Double getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(Double rate) {
		this.rate = rate;
	}
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
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
	 * @return the operationDaysNumber
	 */
	public Integer getOperationDaysNumber() {
		return operationDaysNumber;
	}
	/**
	 * @param operationDaysNumber the operationDaysNumber to set
	 */
	public void setOperationDaysNumber(Integer operationDaysNumber) {
		this.operationDaysNumber = operationDaysNumber;
	}
	/**
	 * @return the insertDate
	 */
	public String getInsertDate() {
		return insertDate;
	}
	/**
	 * @param insertDate the insertDate to set
	 */
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}
	/**
	 * @return the quotaValue
	 */
	public String getQuotaValue() {
		return quotaValue;
	}
	/**
	 * @param quotaValue the quotaValue to set
	 */
	public void setQuotaValue(String quotaValue) {
		this.quotaValue = quotaValue;
	}
	/**
	 * @return the quotaDaysNumber
	 */
	public Integer getQuotaDaysNumber() {
		return quotaDaysNumber;
	}
	/**
	 * @param quotaDaysNumber the quotaDaysNumber to set
	 */
	public void setQuotaDaysNumber(Integer quotaDaysNumber) {
		this.quotaDaysNumber = quotaDaysNumber;
	}
	/**
	 * @return the lastPaymentDate
	 */
	public String getLastPaymentDate() {
		return lastPaymentDate;
	}
	/**
	 * @param lastPaymentDate the lastPaymentDate to set
	 */
	public void setLastPaymentDate(String lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}
	/**
	 * @return the interestEarned
	 */
	public Double getInterestEarned() {
		return interestEarned;
	}
	/**
	 * @param interestEarned the interestEarned to set
	 */
	public void setInterestEarned(Double interestEarned) {
		this.interestEarned = interestEarned;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDepositsPaymentSchedule [quota=" + quota + ", paymentDate="
				+ paymentDate + ", quotaAmount=" + quotaAmount + ", entity="
				+ entity + ", operationDescription=" + operationDescription
				+ ", addressDescription=" + addressDescription
				+ ", officeName=" + officeName + ", bankNumberOperation="
				+ bankNumberOperation + ", depositTypeDescription="
				+ depositTypeDescription + ", amount=" + amount
				+ ", paymentDescription=" + paymentDescription + ", currency="
				+ currency + ", rate=" + rate + ", expirationDate="
				+ expirationDate + ", status=" + status
				+ ", operationDaysNumber=" + operationDaysNumber
				+ ", insertDate=" + insertDate + ", quotaValue=" + quotaValue
				+ ", quotaDaysNumber=" + quotaDaysNumber + ", lastPaymentDate="
				+ lastPaymentDate + ", interestEarned=" + interestEarned + "]";
	}
	
}
