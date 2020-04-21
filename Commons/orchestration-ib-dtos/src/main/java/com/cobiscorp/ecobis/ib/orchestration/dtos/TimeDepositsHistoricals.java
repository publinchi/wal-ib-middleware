/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jchonillo
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class TimeDepositsHistoricals {
	
	private Integer sequence;
	private Integer coupon;
	private String transactionDate;
	private Integer transactionCode;
	private String description;
	private Double value;
	private String observation;
	private String functionary;
	private Double rate;
	
	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}
	/**
	 * set the sequence (Integer)
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the coupon
	 */
	public Integer getCoupon() {
		return coupon;
	}
	/**
	 * set the coupon (Integer)
	 */
	public void setCoupon(Integer coupon) {
		this.coupon = coupon;
	}
	/**
	 * @return the transactionDate
	 */
	public String getTransactionDate() {
		return transactionDate;
	}
	/**
	 * set the transactionDate (String)
	 */
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	/**
	 * @return the transactionCode
	 */
	public Integer getTransactionCode() {
		return transactionCode;
	}
	/**
	 * set the transactionCode (Integer)
	 */
	public void setTransactionCode(Integer transactionCode) {
		this.transactionCode = transactionCode;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * set the description (String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	/**
	 * set the value (Double)
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	/**
	 * @return the observation
	 */
	public String getObservation() {
		return observation;
	}
	/**
	 * set the observation (String)
	 */
	public void setObservation(String observation) {
		this.observation = observation;
	}
	/**
	 * @return the functionary
	 */
	public String getFunctionary() {
		return functionary;
	}
	/**
	 * set the functionary (String)
	 */
	public void setFunctionary(String functionary) {
		this.functionary = functionary;
	}
	/**
	 * @return the rate
	 */
	public Double getRate() {
		return rate;
	}
	/**
	 * set the rate (Double)
	 */
	public void setRate(Double rate) {
		this.rate = rate;
	}
	@Override
	public String toString() {
		return "TimeDepositsHistoricals [sequence=" + sequence + ", coupon="
				+ coupon + ", transactionDate=" + transactionDate
				+ ", transactionCode=" + transactionCode + ", description="
				+ description + ", value=" + value + ", observation="
				+ observation + ", functionary=" + functionary + ", rate="
				+ rate + "]";
	}
	
}
