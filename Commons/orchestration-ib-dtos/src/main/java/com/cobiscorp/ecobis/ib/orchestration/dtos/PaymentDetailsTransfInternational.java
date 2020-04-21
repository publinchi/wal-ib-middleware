/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author itorres
 * @since Nov 12, 2014
 * @version 1.0.0
 */
public class PaymentDetailsTransfInternational {
	private Integer transactionSeuqential;
	private Integer number;
	private String term;
	private Integer paymentDetailSequential;
	private String paymentType;
	private String paymentTypeDetail;
	private Double extraAmount;
	private String currency;
	private Double currencyType;
	private Double localAmount;
	private String detail;
	private Integer paymentDateSequential;
	/**
	 * @return the transactionSeuqential
	 */
	public Integer getTransactionSeuqential() {
		return transactionSeuqential;
	}
	/**
	 * @param transactionSeuqential the transactionSeuqential to set
	 */
	public void setTransactionSeuqential(Integer transactionSeuqential) {
		this.transactionSeuqential = transactionSeuqential;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
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
	 * @return the paymentDetailSequential
	 */
	public Integer getPaymentDetailSequential() {
		return paymentDetailSequential;
	}
	/**
	 * @param paymentDetailSequential the paymentDetailSequential to set
	 */
	public void setPaymentDetailSequential(Integer paymentDetailSequential) {
		this.paymentDetailSequential = paymentDetailSequential;
	}
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
	 * @return the paymentTypeDetail
	 */
	public String getPaymentTypeDetail() {
		return paymentTypeDetail;
	}
	/**
	 * @param paymentTypeDetail the paymentTypeDetail to set
	 */
	public void setPaymentTypeDetail(String paymentTypeDetail) {
		this.paymentTypeDetail = paymentTypeDetail;
	}
	/**
	 * @return the extraAmount
	 */
	public Double getExtraAmount() {
		return extraAmount;
	}
	/**
	 * @param extraAmount the extraAmount to set
	 */
	public void setExtraAmount(Double extraAmount) {
		this.extraAmount = extraAmount;
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
	 * @return the currencyType
	 */
	public Double getCurrencyType() {
		return currencyType;
	}
	/**
	 * @param currencyType the currencyType to set
	 */
	public void setCurrencyType(Double currencyType) {
		this.currencyType = currencyType;
	}
	/**
	 * @return the localAmount
	 */
	public Double getLocalAmount() {
		return localAmount;
	}
	/**
	 * @param localAmount the localAmount to set
	 */
	public void setLocalAmount(Double localAmount) {
		this.localAmount = localAmount;
	}
	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	/**
	 * @return the paymentDateSequential
	 */
	public Integer getPaymentDateSequential() {
		return paymentDateSequential;
	}
	/**
	 * @param paymentDateSequential the paymentDateSequential to set
	 */
	public void setPaymentDateSequential(Integer paymentDateSequential) {
		this.paymentDateSequential = paymentDateSequential;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentDetails [transactionSeuqential=" + transactionSeuqential
				+ ", number=" + number + ", term=" + term
				+ ", paymentDetailSequential=" + paymentDetailSequential
				+ ", paymentType=" + paymentType + ", paymentTypeDetail="
				+ paymentTypeDetail + ", extraAmount=" + extraAmount
				+ ", currency=" + currency + ", currencyType=" + currencyType
				+ ", localAmount=" + localAmount + ", detail=" + detail
				+ ", paymentDateSequential=" + paymentDateSequential + "]";
	}
	
}
