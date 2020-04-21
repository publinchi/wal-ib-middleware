/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;


/**
 * @author mmoya
 * @since Jan 29, 2015
 * @version 1.0.0
 */
public class BatchProductOpening {
	private Integer customerId;
	private Integer productId;
	private Integer officeId;
	private Integer currencyId;
	private String date;
	private String status;
	private String typeSignature;
	private String statusproduct;
	private String destAccount;
	private String account;
	private String typeAccount;
	
	
	
	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	/**
	 * @return the officeId
	 */
	public Integer getOfficeId() {
		return officeId;
	}
	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(Integer officeId) {
		this.officeId = officeId;
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
	 * @return the typeSignature
	 */
	public String getTypeSignature() {
		return typeSignature;
	}
	/**
	 * @param typeSignature the typeSignature to set
	 */
	public void setTypeSignature(String typeSignature) {
		this.typeSignature = typeSignature;
	}
	/**
	 * @return the statusproduct
	 */
	public String getStatusproduct() {
		return statusproduct;
	}
	/**
	 * @param statusproduct the statusproduct to set
	 */
	public void setStatusproduct(String statusproduct) {
		this.statusproduct = statusproduct;
	}
	/**
	 * @return the destAccount
	 */
	public String getDestAccount() {
		return destAccount;
	}
	/**
	 * @param destAccount the destAccount to set
	 */
	public void setDestAccount(String destAccount) {
		this.destAccount = destAccount;
	}
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}	
	
	/**
	 * @return the typeAccount
	 */
	public String getTypeAccount() {
		return typeAccount;
	}
	/**
	 * @param typeAccount the typeAccount to set
	 */
	public void setTypeAccount(String typeAccount) {
		this.typeAccount = typeAccount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchProductOpening [customerId=" + customerId + ", productId="
				+ productId + ", officeId=" + officeId + ", currencyId="
				+ currencyId + ", date=" + date + ", status=" + status
				+ ", typeSignature=" + typeSignature + ", statusproduct="
				+ statusproduct + ", destAccount=" + destAccount + ", account="
				+ account + "]";
	}
	
	




}