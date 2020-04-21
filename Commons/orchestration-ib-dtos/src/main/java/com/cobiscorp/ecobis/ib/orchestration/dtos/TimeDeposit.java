/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author promero
 * @since 09/10/2014
 * @version 1.0.0
 */
public class TimeDeposit {
	private Product product;
	private String openingDate;
	private String expirationDate;
	private Double amount;
	private Double totalRateEstimed;
	private String rate;
	private Integer term;
	private Double amountEstimed;
	private String automaticRenewal;
	private String isCompounded;
	private String frecuencyOfPayment;
	private String accountOfficer;
	private String valueDate;
	private Integer calculationBase;
	/**
	 * @return the productNumber
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the openingDate
	 */
	public String getOpeningDate() {
		return openingDate;
	}
	/**
	 * @param openingDate the openingDate to set
	 */
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
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
	 * @return the totalRateEstimed
	 */
	public Double getTotalRateEstimed() {
		return totalRateEstimed;
	}
	/**
	 * @param totalRateEstimed the totalRateEstimed to set
	 */
	public void setTotalRateEstimed(Double totalRateEstimed) {
		this.totalRateEstimed = totalRateEstimed;
	}
	/**
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
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
	 * @return the amountEstimed
	 */
	public Double getAmountEstimed() {
		return amountEstimed;
	}
	/**
	 * @param amountEstimed the amountEstimed to set
	 */
	public void setAmountEstimed(Double amountEstimed) {
		this.amountEstimed = amountEstimed;
	}
	/**
	 * @return the automaticRenewal
	 */
	public String getAutomaticRenewal() {
		return automaticRenewal;
	}
	/**
	 * @param automaticRenewal the automaticRenewal to set
	 */
	public void setAutomaticRenewal(String automaticRenewal) {
		this.automaticRenewal = automaticRenewal;
	}
	/**
	 * @return the isCompounded
	 */
	public String getIsCompounded() {
		return isCompounded;
	}
	/**
	 * @param isCompounded the isCompounded to set
	 */
	public void setIsCompounded(String isCompounded) {
		this.isCompounded = isCompounded;
	}
	/**
	 * @return the frecuencyOfPayment
	 */
	public String getFrecuencyOfPayment() {
		return frecuencyOfPayment;
	}
	/**
	 * @param frecuencyOfPayment the frecuencyOfPayment to set
	 */
	public void setFrecuencyOfPayment(String frecuencyOfPayment) {
		this.frecuencyOfPayment = frecuencyOfPayment;
	}
	/**
	 * @return the accountOfficer
	 */
	public String getAccountOfficer() {
		return accountOfficer;
	}
	/**
	 * @param accountOfficer the accountOfficer to set
	 */
	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
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
	 * @return the calculationBase
	 */
	public Integer getCalculationBase() {
		return calculationBase;
	}
	/**
	 * @param calculationBase the calculationBase to set
	 */
	public void setCalculationBase(Integer calculationBase) {
		this.calculationBase = calculationBase;
	}	
	
	
}
