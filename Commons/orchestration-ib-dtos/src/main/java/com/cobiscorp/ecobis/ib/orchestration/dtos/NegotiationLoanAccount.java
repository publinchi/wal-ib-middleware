/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

import com.cobiscorp.ecobis.ib.application.dtos.BaseResponse;

/**
 * @author kmeza
 * @since Nov 7, 2014
 * @version 1.0.0
 */
public class NegotiationLoanAccount  extends BaseResponse{
	
	private String chargeRate;
	private String advancePayment;
	private String reductionRate;
	private String aplicationRate;
	private String completeQuota;
	private String priorityRate;
	private String paymentEffect;
	private Integer currencyId;
	private String account;
    private BigDecimal	quota;
	private String	concept;
	private String state;
	private BigDecimal amount;
	private BigDecimal amountMN;

	
	
	/**
	 * @return the transactionId
	 */
	
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @return the quota
	 */
	public BigDecimal getQuota() {
		return quota;
	}
	/**
	 * @param quota the quota to set
	 */
	public void setQuota(BigDecimal quota) {
		this.quota = quota;
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
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the amountMN
	 */
	public BigDecimal getAmountMN() {
		return amountMN;
	}
	/**
	 * @param amountMN the amountMN to set
	 */
	public void setAmountMN(BigDecimal amountMN) {
		this.amountMN = amountMN;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NegotiationLoanAccount [chargeRate=" + chargeRate
				+ ", advancePayment=" + advancePayment + ", reductionRate="
				+ reductionRate + ", aplicationRate=" + aplicationRate
				+ ", completeQuota=" + completeQuota + ", priorityRate="
				+ priorityRate + ", paymentEffect=" + paymentEffect
				+ ", currencyId=" + currencyId + ", account=" + account
				+ ", quota=" + quota + ", concept=" + concept + ", state="
				+ state + ", amount=" + amount + ", amountMN=" + amountMN
				+ ", currencyName=" + currencyName + "]";
	}
	/**
	 * @return the chargeRate
	 */
	public String getChargeRate() {
		return chargeRate;
	}
	/**
	 * @param chargeRate the chargeRate to set
	 */
	public void setChargeRate(String chargeRate) {
		this.chargeRate = chargeRate;
	}
	/**
	 * @return the advancePayment
	 */
	public String getAdvancePayment() {
		return advancePayment;
	}
	/**
	 * @param advancePayment the advancePayment to set
	 */
	public void setAdvancePayment(String advancePayment) {
		this.advancePayment = advancePayment;
	}
	/**
	 * @return the reductionRate
	 */
	public String getReductionRate() {
		return reductionRate;
	}
	/**
	 * @param reductionRate the reductionRate to set
	 */
	public void setReductionRate(String reductionRate) {
		this.reductionRate = reductionRate;
	}
	/**
	 * @return the aplicationRate
	 */
	public String getAplicationRate() {
		return aplicationRate;
	}
	/**
	 * @param aplicationRate the aplicationRate to set
	 */
	public void setAplicationRate(String aplicationRate) {
		this.aplicationRate = aplicationRate;
	}
	/**
	 * @return the completeQuota
	 */
	public String getCompleteQuota() {
		return completeQuota;
	}
	/**
	 * @param completeQuota the completeQuota to set
	 */
	public void setCompleteQuota(String completeQuota) {
		this.completeQuota = completeQuota;
	}
	/**
	 * @return the priorityRate
	 */
	public String getPriorityRate() {
		return priorityRate;
	}
	/**
	 * @param priorityRate the priorityRate to set
	 */
	public void setPriorityRate(String priorityRate) {
		this.priorityRate = priorityRate;
	}
	/**
	 * @return the paymentEffect
	 */
	public String getPaymentEffect() {
		return paymentEffect;
	}
	/**
	 * @param paymentEffect the paymentEffect to set
	 */
	public void setPaymentEffect(String paymentEffect) {
		this.paymentEffect = paymentEffect;
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
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}
	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	private String currencyName;

	/**
	 * 
	 */
	
	

}
