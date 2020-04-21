/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author rperero
 * @since Feb 12, 2015
 * @version 1.0.0
 */
public class OnlinePaymentDetail {
	private Integer collectorId = null;
	private Integer office = null;
	private String Identification = null;
	private String name = null;
	private Integer numberService = null;
	private Integer state = null;
	private String response = null;
	private String contractId = null;
	private String number = null;
	private Integer receipts = null;
	private BigDecimal totalPay = null;
	private String period  = null;
	private String expirationDate = null;
	private String receiptNumber = null;
	private BigDecimal totalPayment = null;
	private String self = null;
	private String thridPartyPaymentKey = null;
	/**
	 * @return the contractId
	 */
	public String getContractId() {
		return contractId;
	}
	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the receipts
	 */
	public Integer getReceipts() {
		return receipts;
	}
	/**
	 * @param receipts the receipts to set
	 */
	public void setReceipts(Integer receipts) {
		this.receipts = receipts;
	}
	/**
	 * @return the totalPay
	 */
	public BigDecimal getTotalPay() {
		return totalPay;
	}
	/**
	 * @param totalPay the totalPay to set
	 */
	public void setTotalPay(BigDecimal totalPay) {
		this.totalPay = totalPay;
	}
	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
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
	 * @return the receiptNumber
	 */
	public String getReceiptNumber() {
		return receiptNumber;
	}
	/**
	 * @param receiptNumber the receiptNumber to set
	 */
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	/**
	 * @return the totalPayment
	 */
	public BigDecimal getTotalPayment() {
		return totalPayment;
	}
	/**
	 * @param totalPayment the totalPayment to set
	 */
	public void setTotalPayment(BigDecimal totalPayment) {
		this.totalPayment = totalPayment;
	}
	/**
	 * @return the self
	 */
	public String getSelf() {
		return self;
	}
	/**
	 * @param self the self to set
	 */
	public void setSelf(String self) {
		this.self = self;
	}
	/**
	 * @return the thridPartyPaymentKey
	 */
	public String getThridPartyPaymentKey() {
		return thridPartyPaymentKey;
	}
	/**
	 * @param thridPartyPaymentKey the thridPartyPaymentKey to set
	 */
	public void setThridPartyPaymentKey(String thridPartyPaymentKey) {
		this.thridPartyPaymentKey = thridPartyPaymentKey;
	}
	
	/**
	 * @return the collectorId
	 */
	public Integer getCollectorId() {
		return collectorId;
	}
	/**
	 * @param collectorId the collectorId to set
	 */
	public void setCollectorId(Integer collectorId) {
		this.collectorId = collectorId;
	}
	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}
	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return Identification;
	}
	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		Identification = identification;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the numberService
	 */
	public Integer getNumberService() {
		return numberService;
	}
	/**
	 * @param numberService the numberService to set
	 */
	public void setNumberService(Integer numberService) {
		this.numberService = numberService;
	}
	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	/**
	 * @return the response
	 */
	public String getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(String response) {
		this.response = response;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OnlinePaymentDetail [collectorId=" + collectorId + ", office="
				+ office + ", Identification=" + Identification + ", name="
				+ name + ", numberService=" + numberService + ", state="
				+ state + ", response=" + response + ", contractId="
				+ contractId + ", number=" + number + ", receipts=" + receipts
				+ ", totalPay=" + totalPay + ", period=" + period
				+ ", expirationDate=" + expirationDate + ", receiptNumber="
				+ receiptNumber + ", totalPayment=" + totalPayment + ", self="
				+ self + ", thridPartyPaymentKey=" + thridPartyPaymentKey
				+ ", getContractId()=" + getContractId() + ", getNumber()="
				+ getNumber() + ", getReceipts()=" + getReceipts()
				+ ", getTotalPay()=" + getTotalPay() + ", getPeriod()="
				+ getPeriod() + ", getExpirationDate()=" + getExpirationDate()
				+ ", getReceiptNumber()=" + getReceiptNumber()
				+ ", getTotalPayment()=" + getTotalPayment() + ", getSelf()="
				+ getSelf() + ", getThridPartyPaymentKey()="
				+ getThridPartyPaymentKey() + ", getCollectorId()="
				+ getCollectorId() + ", getOffice()=" + getOffice()
				+ ", getIdentification()=" + getIdentification()
				+ ", getName()=" + getName() + ", getNumberService()="
				+ getNumberService() + ", getState()=" + getState()
				+ ", getResponse()=" + getResponse() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	
	
}
