/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;
import java.math.BigDecimal;
import java.util.Date;
/**
 * @author gyagual
 * @since Sep 30, 2014
 * @version 1.0.0
 */
public class Check {
	private String checkNumber;
	private BigDecimal amount;
	private BigDecimal commission;
	private Currency currency;
	private String datePayment;
	private Integer officePayment;
	private String hour;
 	private String status;
	private String beneficiary;
	private String userName;
	private String statusId;
	private String descriptionOffice;
	private String nameAccount;
	/**
	 * @return the checkNumber
	 */
	public String getCheckNumber() {
		return checkNumber;
	}
	/**
	 * @param checkNumber the checkNumber to set
	 */
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
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
	 * @return the commission
	 */
	public BigDecimal getCommission() {
		return commission;
	}
	/**
	 * @param commission the commission to set
	 */
	public void setCommission(BigDecimal commission) {
		this.commission = commission;
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
	public void setCurrency (Currency currency) {
		this.currency = currency;
	}	
	/**
	 * @return the datePayment
	 */
	public String getDatePayment() {
		return datePayment;
	}
	/**
	 * @param datePayment the datePayment to set
	 */
	public void setDatePayment(String datePayment) {
		this.datePayment = datePayment;
	}
	/**
	 * @return the officePayment
	 */
	public Integer getOfficePayment() {
		return officePayment;
	}
	/**
	 * @param datePayment the datePayment to set
	 */
	public void setOfficePayment(Integer officePayment) {
		this.officePayment = officePayment;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the beneficiary
	 */
	public String getBeneficiary() {
		return beneficiary;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	/**
	 * @return the username
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setUserName(String username) {
		this.userName = username;
	}
	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}	
	/**
	 * @return the statusid
	 */
	public String getStatusId() {
		return statusId;
	}
	/**
	 * @param statusid the statusid to set
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	/**
	 * @return the descriptionOffice
	 */
	public String getDescriptionOffice() {
		return descriptionOffice;
	}
	/**
	 * @param descriptionOffice the descriptionOffice to set
	 */
	public void setDescriptionOffice(String descriptionOffice) {
		this.descriptionOffice = descriptionOffice;
	}
	/**
	 * @return the nameAccount
	 */
	public String getNameAccount() {
		return nameAccount;
	}
	/**
	 * @param nameAccount the nameAccount to set
	 */
	public void setNameAccount(String nameAccount) {
		this.nameAccount = nameAccount;
	}	
}
