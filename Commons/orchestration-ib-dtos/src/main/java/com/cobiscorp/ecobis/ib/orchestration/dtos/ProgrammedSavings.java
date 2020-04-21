/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gcondo
 * @since Sep 24, 2014
 * @version 1.0.0
 */
public class ProgrammedSavings {
	
	private Integer sequential;
	private String savingsTime;
	private String paymentDate;
	private Double amount;
	private Currency currency;
	private String executed;
	private String mail;
	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}
	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	private String frequency;
	
	private String initialDate;
	private String term;
	
	private String concept;
	private String expirationDate;
	private String idBeneficiary;	
	
	private Integer branch;
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the initialDate
	 */
	public String getInitialDate() {
		return initialDate;
	}
	/**
	 * @param initialDate the initialDate to set
	 */
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
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
	 * @return the idBeneficiary
	 */
	public String getIdBeneficiary() {
		return idBeneficiary;
	}
	/**
	 * @param idBeneficiary the idBeneficiary to set
	 */
	public void setIdBeneficiary(String idBeneficiary) {
		this.idBeneficiary = idBeneficiary;
	}
	/**
	 * @return the branch
	 */
	public Integer getBranch() {
		return branch;
	}
	/**
	 * @param branch the branch to set
	 */
	public void setBranch(Integer branch) {
		this.branch = branch;
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
	/**
	 * @return the savingsTime
	 */
	public String getSavingsTime() {
		return savingsTime;
	}
	/**
	 * @param savingsTime the savingsTime to set
	 */
	public void setSavingsTime(String savingsTime) {
		this.savingsTime = savingsTime;
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
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	/**
	 * @return the executed
	 */
	public String getExecuted() {
		return executed;
	}
	/**
	 * @param executed the executed to set
	 */
	public void setExecuted(String executed) {
		this.executed = executed;
	}
	
	

}
