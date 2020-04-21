
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author wsanchez
 * @since Sep 12, 2014
 * @version 1.0.0
 */
public class LoanStatement {

	private Product operationNumber;
	private String paymentDate;
	private String movementType;
	private Double normalInterest;
	private Double arrearsInterest;
	private Double capital;
	private Double others;
	private Double amount;
	private Double capitalBalance;
	private Integer period;
	private Double tax;
	private Double assured;
	private String description;
	private Double previousRate;
	private String previousStatus; 
	private String currentStatus;
	private Double currentRate;
	private Integer sequential;
	private String paymentType;
	
	//private paymentType;
	
	
	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}
	/**
	 * @param paymentType the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	/**
	 * @return the operationNumber
	 */
	public Product getOperationNumber() {
		return operationNumber;
	}
	/**
	 * @param operationNumber the operationNumber to set
	 */
	public void setOperationNumber(Product operationNumber) {
		this.operationNumber = operationNumber;
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
	 * @return the movementType
	 */
	public String getMovementType() {
		return movementType;
	}
	/**
	 * @param movementType the movementType to set
	 */
	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}
	/**
	 * @return the normalInterest
	 */
	public Double getNormalInterest() {
		return normalInterest;
	}
	/**
	 * @param normalInterest the normalInterest to set
	 */
	public void setNormalInterest(Double normalInterest) {
		this.normalInterest = normalInterest;
	}
	/**
	 * @return the arrearsInterest
	 */
	public Double getArrearsInterest() {
		return arrearsInterest;
	}
	/**
	 * @param arrearsInterest the arrearsInterest to set
	 */
	public void setArrearsInterest(Double arrearsInterest) {
		this.arrearsInterest = arrearsInterest;
	}
	/**
	 * @return the capital
	 */
	public Double getCapital() {
		return capital;
	}
	/**
	 * @param capital the capital to set
	 */
	public void setCapital(Double capital) {
		this.capital = capital;
	}
	/**
	 * @return the others
	 */
	public Double getOthers() {
		return others;
	}
	/**
	 * @param others the others to set
	 */
	public void setOthers(Double others) {
		this.others = others;
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
	 * @return the capitalBalance
	 */
	public Double getCapitalBalance() {
		return capitalBalance;
	}
	/**
	 * @param capitalBalance the capitalBalance to set
	 */
	public void setCapitalBalance(Double capitalBalance) {
		this.capitalBalance = capitalBalance;
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
	 * @return the tax
	 */
	public Double getTax() {
		return tax;
	}
	/**
	 * @param tax the tax to set
	 */
	public void setTax(Double tax) {
		this.tax = tax;
	}
	/**
	 * @return the assured
	 */
	public Double getAssured() {
		return assured;
	}
	/**
	 * @param assured the assured to set
	 */
	public void setAssured(Double assured) {
		this.assured = assured;
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
	 * @return the previousRate
	 */
	public Double getPreviousRate() {
		return previousRate;
	}
	/**
	 * @param previousRate the previousRate to set
	 */
	public void setPreviousRate(Double previousRate) {
		this.previousRate = previousRate;
	}
	
	/**
	 * @param previousStatus the previousStatus to set
	 */
	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}
	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	/**
	 * @return the previousStatus
	 */
	public String getPreviousStatus() {
		return previousStatus;
	}
	/**
	 * @return the currentStatus
	 */
	public String getCurrentStatus() {
		return currentStatus;
	}
	/**
	 * @return the currentRate
	 */
	public Double getCurrentRate() {
		return currentRate;
	}
	/**
	 * @param currentRate the currentRate to set
	 */
	public void setCurrentRate(Double currentRate) {
		this.currentRate = currentRate;
	}
	/**
	 * @return the sequential
	 */
	public Integer getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}
	
	

}
