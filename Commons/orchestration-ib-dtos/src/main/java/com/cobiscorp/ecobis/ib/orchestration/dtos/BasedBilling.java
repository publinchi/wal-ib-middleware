/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author rperero
 * @since Feb 10, 2015
 * @version 1.0.0
 */
public class BasedBilling {
	
	private String identification = null;
	private String debtorName = null;
	private String reference1 = null;
	private String reference2 = null;
	private String reference3 = null;
	private BigDecimal amount = null;
	private String paymentDay = null;
	private Integer sequential = null;
	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}
	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	/**
	 * @return the debtorName
	 */
	public String getDebtorName() {
		return debtorName;
	}
	/**
	 * @param debtorName the debtorName to set
	 */
	public void setDebtorName(String debtorName) {
		this.debtorName = debtorName;
	}
	/**
	 * @return the reference1
	 */
	public String getReference1() {
		return reference1;
	}
	/**
	 * @param reference1 the reference1 to set
	 */
	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}
	/**
	 * @return the reference2
	 */
	public String getReference2() {
		return reference2;
	}
	/**
	 * @param reference2 the reference2 to set
	 */
	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}
	/**
	 * @return the reference3
	 */
	public String getReference3() {
		return reference3;
	}
	/**
	 * @param reference3 the reference3 to set
	 */
	public void setReference3(String reference3) {
		this.reference3 = reference3;
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
	 * @return the paymentDay
	 */
	public String getPaymentDay() {
		return paymentDay;
	}
	/**
	 * @param paymentDay the paymentDay to set
	 */
	public void setPaymentDay(String paymentDay) {
		this.paymentDay = paymentDay;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasedBilling [identification=" + identification
				+ ", debtorName=" + debtorName + ", reference1=" + reference1
				+ ", reference2=" + reference2 + ", reference3=" + reference3
				+ ", amount=" + amount + ", paymentDay=" + paymentDay
				+ ", sequential=" + sequential + "]";
	}
	
	

}
