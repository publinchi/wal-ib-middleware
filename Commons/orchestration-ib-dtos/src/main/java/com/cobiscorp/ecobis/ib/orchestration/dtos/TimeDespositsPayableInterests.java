/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jmoreta
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class TimeDespositsPayableInterests {
	

	private Double approximateValue;
	private String couponNumber;
	/**
	 * String currency
	 */
	private Currency currency;//String  
	private String dateBox;
	private String detained;
	/**
	 * String expirationDate, String startDate, String status
	 */
	private BalanceDetailPayment balanceDetailPayment; //String  
	private Integer payNumber;
	private Integer prePrintNumber;
	private Integer printNumber;	
	/**
	 * Double tax
	 */
	private LoanAmortization tax; //Double 
	private Double value;
	/**
	 * @return the approximateValue
	 */
	public Double getApproximateValue() {
		return approximateValue;
	}
	/**
	 * @param approximateValue the approximateValue to set
	 */
	public void setApproximateValue(Double approximateValue) {
		this.approximateValue = approximateValue;
	}
	/**
	 * @return the couponNumber
	 */
	public String getCouponNumber() {
		return couponNumber;
	}
	/**
	 * @param couponNumber the couponNumber to set
	 */
	public void setCouponNumber(String couponNumber) {
		this.couponNumber = couponNumber;
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
	 * @return the dateBox
	 */
	public String getDateBox() {
		return dateBox;
	}
	/**
	 * @param dateBox the dateBox to set
	 */
	public void setDateBox(String dateBox) {
		this.dateBox = dateBox;
	}
	/**
	 * @return the detained
	 */
	public String getDetained() {
		return detained;
	}
	/**
	 * @param detained the detained to set
	 */
	public void setDetained(String detained) {
		this.detained = detained;
	}
	/**
	 * @return the balanceDetailPayment
	 */
	public BalanceDetailPayment getBalanceDetailPayment() {
		return balanceDetailPayment;
	}
	/**
	 * @param balanceDetailPayment the balanceDetailPayment to set
	 */
	public void setBalanceDetailPayment(BalanceDetailPayment balanceDetailPayment) {
		this.balanceDetailPayment = balanceDetailPayment;
	}
	/**
	 * @return the payNumber
	 */
	public Integer getPayNumber() {
		return payNumber;
	}
	/**
	 * @param payNumber the payNumber to set
	 */
	public void setPayNumber(Integer payNumber) {
		this.payNumber = payNumber;
	}
	/**
	 * @return the prePrintNumber
	 */
	public Integer getPrePrintNumber() {
		return prePrintNumber;
	}
	/**
	 * @param prePrintNumber the prePrintNumber to set
	 */
	public void setPrePrintNumber(Integer prePrintNumber) {
		this.prePrintNumber = prePrintNumber;
	}
	/**
	 * @return the printNumber
	 */
	public Integer getPrintNumber() {
		return printNumber;
	}
	/**
	 * @param printNumber the printNumber to set
	 */
	public void setPrintNumber(Integer printNumber) {
		this.printNumber = printNumber;
	}
	/**
	 * @return the tax
	 */
	public LoanAmortization getTax() {
		return tax;
	}
	/**
	 * @param tax the tax to set
	 */
	public void setTax(LoanAmortization tax) {
		this.tax = tax;
	}
	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDespositsPayableInterests [approximateValue="
				+ approximateValue + ", couponNumber=" + couponNumber
				+ ", currency=" + currency + ", dateBox=" + dateBox
				+ ", detained=" + detained + ", balanceDetailPayment="
				+ balanceDetailPayment + ", payNumber=" + payNumber
				+ ", prePrintNumber=" + prePrintNumber + ", printNumber="
				+ printNumber + ", tax=" + tax + ", value=" + value + "]";
	}
	
	
	
}
