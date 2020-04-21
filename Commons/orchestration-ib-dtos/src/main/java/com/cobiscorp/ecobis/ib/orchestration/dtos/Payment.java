/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author schancay
 * @since Sep 3, 2014
 * @version 1.0.0
 */
public class Payment {
	/**
	 * Date payment
	 */
	private Date paymentDate;
	/**
	 * Ammount payment
	 */
	private BigDecimal paymentAmmount;
	/**
	 * AP
	 */

	private BigDecimal quota;
	private String concept;
	private String state;
	private BigDecimal amount;
	private BigDecimal amounMn;
	private Integer currencyId;

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
	 * @return the amounMn
	 */
	public BigDecimal getAmounMn() {
		return amounMn;
	}

	/**
	 * @param amounMn the amounMn to set
	 */
	public void setAmounMn(BigDecimal amounMn) {
		this.amounMn = amounMn;
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

	private String paymentAP;

	/**
	 * @return the paymentammount
	 */
	public BigDecimal getPaymentAmmount() {
		return paymentAmmount;
	}

	/**
	 * @return the paymentAP
	 */
	public String getPaymentAP() {
		return paymentAP;
	}

	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentAmmount
	 *            the paymentammount to set
	 */
	public void setPaymentAmmount(BigDecimal paymentAmmount) {
		this.paymentAmmount = paymentAmmount;
	}

	/**
	 * @param paymentAP
	 *            the paymentAP to set
	 */
	public void setPaymentAP(String paymentAP) {
		this.paymentAP = paymentAP;
	}

	/**
	 * @param paymentDate
	 *            the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	
	@Override
	public String toString() {
		return "Payment [paymentDate=" + paymentDate + ", paymentAmmount="
				+ paymentAmmount + ", quota=" + quota + ", concept=" + concept
				+ ", state=" + state + ", amount=" + amount + ", amounMn="
				+ amounMn + ", currencyId=" + currencyId + ", paymentAP="
				+ paymentAP + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	
}
