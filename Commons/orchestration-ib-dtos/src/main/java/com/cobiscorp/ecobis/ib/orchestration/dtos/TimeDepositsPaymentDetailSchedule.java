/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Oct 28, 2014
 * @version 1.0.0
 */
public class TimeDepositsPaymentDetailSchedule {

	private Integer month;
	private Double rate;
	private String compounded;
	private String paymentType;
	private Integer payDay;
	private String status;
	private Integer baseCalculate;
	private Double earnedInterest;
	private Double amountPaiedInterest;
	private String expirateDate;
	private Integer entity;
	private String valueDate;
	private Integer daysNumber;
	private String realDays;
	/**
	 * @return the month
	 */
	public Integer getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(Integer month) {
		this.month = month;
	}
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
	 * @return the compounded
	 */
	public String getCompounded() {
		return compounded;
	}
	/**
	 * @param compounded the compounded to set
	 */
	public void setCompounded(String compounded) {
		this.compounded = compounded;
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
	 * @return the payDay
	 */
	public Integer getPayDay() {
		return payDay;
	}
	/**
	 * @param payDay the payDay to set
	 */
	public void setPayDay(Integer payDay) {
		this.payDay = payDay;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the baseCalculate
	 */
	public Integer getBaseCalculate() {
		return baseCalculate;
	}
	/**
	 * @param baseCalculate the baseCalculate to set
	 */
	public void setBaseCalculate(Integer baseCalculate) {
		this.baseCalculate = baseCalculate;
	}
	/**
	 * @return the earnedInterest
	 */
	public Double getEarnedInterest() {
		return earnedInterest;
	}
	/**
	 * @param earnedInterest the earnedInterest to set
	 */
	public void setEarnedInterest(Double earnedInterest) {
		this.earnedInterest = earnedInterest;
	}
	/**
	 * @return the amountPaiedInterest
	 */
	public Double getAmountPaiedInterest() {
		return amountPaiedInterest;
	}
	/**
	 * @param amountPaiedInterest the amountPaiedInterest to set
	 */
	public void setAmountPaiedInterest(Double amountPaiedInterest) {
		this.amountPaiedInterest = amountPaiedInterest;
	}
	/**
	 * @return the expirateDate
	 */
	public String getExpirateDate() {
		return expirateDate;
	}
	/**
	 * @param expirateDate the expirateDate to set
	 */
	public void setExpirateDate(String expirateDate) {
		this.expirateDate = expirateDate;
	}
	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
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
	 * @return the daysNumber
	 */
	public Integer getDaysNumber() {
		return daysNumber;
	}
	/**
	 * @param daysNumber the daysNumber to set
	 */
	public void setDaysNumber(Integer daysNumber) {
		this.daysNumber = daysNumber;
	}
	/**
	 * @return the realDays
	 */
	public String getRealDays() {
		return realDays;
	}
	/**
	 * @param realDays the realDays to set
	 */
	public void setRealDays(String realDays) {
		this.realDays = realDays;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDepositsPaymentDetailSchedule [month=" + month + ", rate="
				+ rate + ", compounded=" + compounded + ", paymentType="
				+ paymentType + ", payDay=" + payDay + ", status=" + status
				+ ", baseCalculate=" + baseCalculate + ", earnedInterest="
				+ earnedInterest + ", amountPaiedInterest="
				+ amountPaiedInterest + ", expirateDate=" + expirateDate
				+ ", entity=" + entity + ", valueDate=" + valueDate
				+ ", daysNumber=" + daysNumber + ", realDays=" + realDays + "]";
	}
	
	

}
