/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author mvelez
 * @since Sep 15, 2014
 * @version 1.0.0
 */
public class LoanAmortization {
    //private Integer  operation;
    private Product  operationNumber;
    private Integer  dividend;
    private String   date;
    private Double   capital;
    private Double   interest;
    private Double   mora;
    private Double   tax;
    private Double   insurance;
    private Double   others;
    private Double   capitalAmount;
    private Double   adjustment;
    private String   state;
    private Double   payment;
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
	 * @return the dividend
	 */
	public Integer getDividend() {
		return dividend;
	}
	/**
	 * @param dividend the dividend to set
	 */
	public void setDividend(Integer dividend) {
		this.dividend = dividend;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
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
	 * @return the interest
	 */
	public Double getInterest() {
		return interest;
	}
	/**
	 * @param interest the interest to set
	 */
	public void setInterest(Double interest) {
		this.interest = interest;
	}
	/**
	 * @return the mora
	 */
	public Double getMora() {
		return mora;
	}
	/**
	 * @param mora the mora to set
	 */
	public void setMora(Double mora) {
		this.mora = mora;
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
	 * @return the insurance
	 */
	public Double getInsurance() {
		return insurance;
	}
	/**
	 * @param insurance the insurance to set
	 */
	public void setInsurance(Double insurance) {
		this.insurance = insurance;
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
	 * @return the capitalAmount
	 */
	public Double getCapitalAmount() {
		return capitalAmount;
	}
	/**
	 * @param capitalAmount the capitalAmount to set
	 */
	public void setCapitalAmount(Double capitalAmount) {
		this.capitalAmount = capitalAmount;
	}
	/**
	 * @return the adjustment
	 */
	public Double getAdjustment() {
		return adjustment;
	}
	/**
	 * @param adjustment the adjustment to set
	 */
	public void setAdjustment(Double adjustment) {
		this.adjustment = adjustment;
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
	 * @return the payment
	 */
	public Double getPayment() {
		return payment;
	}
	/**
	 * @param payment the payment to set
	 */
	public void setPayment(Double payment) {
		this.payment = payment;
	}
}
