/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class CertificateDepositResult {

	private Double rate;
    private String expirationDate;
    private String interestPayDay;
    private Double interestEstimatedTotal;
    private Double interestEstimated;
    private String taxPayable;
    private String payDay;
    private Double interestEstimatedHold;
    private Double interestEstimatedTotalHold;
    private Double currentInterestEarned;
    private String newAmounTtype; 
    private String newTermType;
    private Double totalInterestEarned;
    private String amortizationPeriod;
    private Integer numberOfPayment;
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
	 * @return the interestPayDay
	 */
	public String getInterestPayDay() {
		return interestPayDay;
	}
	/**
	 * @param interestPayDay the interestPayDay to set
	 */
	public void setInterestPayDay(String interestPayDay) {
		this.interestPayDay = interestPayDay;
	}
	/**
	 * @return the interestEstimatedTotal
	 */
	public Double getInterestEstimatedTotal() {
		return interestEstimatedTotal;
	}
	/**
	 * @param interestEstimatedTotal the interestEstimatedTotal to set
	 */
	public void setInterestEstimatedTotal(Double interestEstimatedTotal) {
		this.interestEstimatedTotal = interestEstimatedTotal;
	}
	/**
	 * @return the interestEstimated
	 */
	public Double getInterestEstimated() {
		return interestEstimated;
	}
	/**
	 * @param interestEstimated the interestEstimated to set
	 */
	public void setInterestEstimated(Double interestEstimated) {
		this.interestEstimated = interestEstimated;
	}
	/**
	 * @return the taxPayable
	 */
	public String getTaxPayable() {
		return taxPayable;
	}
	/**
	 * @param taxPayable the taxPayable to set
	 */
	public void setTaxPayable(String taxPayable) {
		this.taxPayable = taxPayable;
	}
	
	
	/**
	 * @return the payDay
	 */
	public String getPayDay() {
		return payDay;
	}
	/**
	 * @param payDay the payDay to set
	 */
	public void setPayDay(String payDay) {
		this.payDay = payDay;
	}
	/**
	 * @return the interestEstimatedHold
	 */
	public Double getInterestEstimatedHold() {
		return interestEstimatedHold;
	}
	/**
	 * @param interestEstimatedHold the interestEstimatedHold to set
	 */
	public void setInterestEstimatedHold(Double interestEstimatedHold) {
		this.interestEstimatedHold = interestEstimatedHold;
	}
	/**
	 * @return the interestEstimatedTotalHold
	 */
	public Double getInterestEstimatedTotalHold() {
		return interestEstimatedTotalHold;
	}
	/**
	 * @param interestEstimatedTotalHold the interestEstimatedTotalHold to set
	 */
	public void setInterestEstimatedTotalHold(Double interestEstimatedTotalHold) {
		this.interestEstimatedTotalHold = interestEstimatedTotalHold;
	}
	/**
	 * @return the currentInterestEarned
	 */
	public Double getCurrentInterestEarned() {
		return currentInterestEarned;
	}
	/**
	 * @param currentInterestEarned the currentInterestEarned to set
	 */
	public void setCurrentInterestEarned(Double currentInterestEarned) {
		this.currentInterestEarned = currentInterestEarned;
	}
	/**
	 * @return the newAmounTtype
	 */
	public String getNewAmounTtype() {
		return newAmounTtype;
	}
	/**
	 * @param newAmounTtype the newAmounTtype to set
	 */
	public void setNewAmounTtype(String newAmounTtype) {
		this.newAmounTtype = newAmounTtype;
	}
	/**
	 * @return the newTermType
	 */
	public String getNewTermType() {
		return newTermType;
	}
	/**
	 * @param newTermType the newTermType to set
	 */
	public void setNewTermType(String newTermType) {
		this.newTermType = newTermType;
	}
	/**
	 * @return the totalInterestEarned
	 */
	public Double getTotalInterestEarned() {
		return totalInterestEarned;
	}
	/**
	 * @param totalInterestEarned the totalInterestEarned to set
	 */
	public void setTotalInterestEarned(Double totalInterestEarned) {
		this.totalInterestEarned = totalInterestEarned;
	}
	/**
	 * @return the amortizationPeriod
	 */
	public String getAmortizationPeriod() {
		return amortizationPeriod;
	}
	/**
	 * @param amortizationPeriod the amortizationPeriod to set
	 */
	public void setAmortizationPeriod(String amortizationPeriod) {
		this.amortizationPeriod = amortizationPeriod;
	}
	/**
	 * @return the numberOfPayment
	 */
	public Integer getNumberOfPayment() {
		return numberOfPayment;
	}
	/**
	 * @param numberOfPayment the numberOfPayment to set
	 */
	public void setNumberOfPayment(Integer numberOfPayment) {
		this.numberOfPayment = numberOfPayment;
	}
    
    
}
