/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Dec 2, 2014
 * @version 1.0.0
 */
public class SimulationSaving {

	private Integer code;
	private String description;
	private String category;
	private Double maxAmount;
	private Double finalAmount;
	private Double rate;
	private Integer term;
	private String interestRate;
	private Integer period;
	private Double amount;
	private Integer currencyId;
	private Double initialAmount;
	private String entityType;
	private String operationType;
	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the maxAmount
	 */
	public Double getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return the finalAmount
	 */
	public Double getFinalAmount() {
		return finalAmount;
	}
	/**
	 * @param finalAmount the finalAmount to set
	 */
	public void setFinalAmount(Double finalAmount) {
		this.finalAmount = finalAmount;
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
	 * @return the term
	 */
	public Integer getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(Integer term) {
		this.term = term;
	}
	/**
	 * @return the interestRate
	 */
	public String getInterestRate() {
		return interestRate;
	}
	/**
	 * @param interestRate the interestRate to set
	 */
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}
	/**
	 * @return the period
	 */
	public Integer getPeriod() {
		return period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(Integer period) {
		this.period = period;
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
	/**
	 * @return the initialAmount
	 */
	public Double getInitialAmount() {
		return initialAmount;
	}
	/**
	 * @param initialAmount the initialAmount to set
	 */
	public void setInitialAmount(Double initialAmount) {
		this.initialAmount = initialAmount;
	}
	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}
	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimulationSaving [code=" + code + ", description="
				+ description + ", category=" + category + ", maxAmount="
				+ maxAmount + ", finalAmount=" + finalAmount + ", rate=" + rate
				+ ", term=" + term + ", interestRate=" + interestRate
				+ ", period=" + period + ", amount=" + amount + ", currencyId="
				+ currencyId + ", initialAmount=" + initialAmount
				+ ", entityType=" + entityType + ", operationType="
				+ operationType + "]";
	}
	
		
}
