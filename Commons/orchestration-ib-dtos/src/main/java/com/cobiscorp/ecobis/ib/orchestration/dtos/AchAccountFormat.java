/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author kmeza
 * @since Jan 16, 2015
 * @version 1.0.0
 */
public class AchAccountFormat {

	private Integer id;
	private String description;
	
	private Integer  subsidiary;
	private String status;
	private Integer accountTypeId;
	
	private String accountType;
	private Integer lengthAccount;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AchAccountFormat [id=" + id + ", description=" + description
				+ ", subsidiary=" + subsidiary + ", status=" + status
				+ ", accountTypeId=" + accountTypeId + ", accountType="
				+ accountType + ", lengthAccount=" + lengthAccount + "]";
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the subsidiary
	 */
	public Integer getSubsidiary() {
		return subsidiary;
	}
	/**
	 * @param subsidiary the subsidiary to set
	 */
	public void setSubsidiary(Integer subsidiary) {
		this.subsidiary = subsidiary;
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
	 * @return the accountTypeId
	 */
	public Integer getAccountTypeId() {
		return accountTypeId;
	}
	/**
	 * @param accountTypeId the accountTypeId to set
	 */
	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}
	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}
	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	/**
	 * @return the lengthAccount
	 */
	public Integer getLengthAccount() {
		return lengthAccount;
	}
	/**
	 * @param lengthAccount the lengthAccount to set
	 */
	public void setLengthAccount(Integer lengthAccount) {
		this.lengthAccount = lengthAccount;
	}
}
