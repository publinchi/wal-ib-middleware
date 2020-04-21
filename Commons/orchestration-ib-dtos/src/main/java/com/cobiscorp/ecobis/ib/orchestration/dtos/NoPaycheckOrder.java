/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jchonillo
 * @since Nov 11, 2014
 * @version 1.0.0
 */
public class NoPaycheckOrder {

	private Integer initialCheck;
	private Integer finalCheck;
	private String account;
	private String reason;
	private String suspensionDate;
	private Integer reference;
	private Double commission;
	
	/**
	 * @return the initialCheck
	 */
	public Integer getInitialCheck() {
		return initialCheck;
	}
	public void setInitialCheck(Integer initialCheck) {
		this.initialCheck = initialCheck;
	}
	
	/**
	 * @return the finalCheck
	 */
	public Integer getFinalCheck() {
		return finalCheck;
	}
	public void setFinalCheck(Integer finalCheck) {
		this.finalCheck = finalCheck;
	}
	
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * @return the suspensionDate
	 */
	public String getSuspensionDate() {
		return suspensionDate;
	}
	public void setSuspensionDate(String suspensionDate) {
		this.suspensionDate = suspensionDate;
	}
	
	/**
	 * @return the reference
	 */
	public Integer getReference() {
		return reference;
	}
	public void setReference(Integer reference) {
		this.reference = reference;
	}
	
	/**
	 * @return the commission
	 */
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	
	
	@Override
	public String toString() {
		return "NoPaycheckOrder [initialCheck=" + initialCheck
				+ ", finalCheck=" + finalCheck + ", account=" + account
				+ ", reason=" + reason + ", suspensionDate=" + suspensionDate
				+ ", reference=" + reference + ", commission=" + commission
				+ "]";
	}
	
}
