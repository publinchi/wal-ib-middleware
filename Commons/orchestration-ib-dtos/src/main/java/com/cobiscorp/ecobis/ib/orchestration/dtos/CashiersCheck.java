/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;


/**
 * @author jveloz
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class CashiersCheck {

	private Double amount;
	private String beneficiary;
	private String beneficiaryId;
	private String beneficiaryTypeId;
	private String office;
	//beneficiaryId
	private String authorizedPhoneNumber;
	private Integer destinationOfficeId;
	private String authorizedTypeId;
	//authorizedPhoneNumber
	private String authorizedId;
	private String authorized;
	private String email;
	private String purpose;
	private Integer currencyId;
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the beneficiary
	 */
	public String getBeneficiary() {
		return beneficiary;
	}
	/**
	 * @param beneficiary the beneficiary to set
	 */
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	/**
	 * @return the beneficiaryId
	 */
	public String getBeneficiaryId() {
		return beneficiaryId;
	}
	/**
	 * @param beneficiaryId the beneficiaryId to set
	 */
	public void setBeneficiaryId(String beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}
	/**
	 * @return the beneficiaryTypeId
	 */
	public String getBeneficiaryTypeId() {
		return beneficiaryTypeId;
	}
	/**
	 * @param beneficiaryTypeId the beneficiaryTypeId to set
	 */
	public void setBeneficiaryTypeId(String beneficiaryTypeId) {
		this.beneficiaryTypeId = beneficiaryTypeId;
	}
	/**
	 * @return the authorizedPhoneNumber
	 */
	public String getAuthorizedPhoneNumber() {
		return authorizedPhoneNumber;
	}
	/**
	 * @param authorizedPhoneNumber the authorizedPhoneNumber to set
	 */
	public void setAuthorizedPhoneNumber(String authorizedPhoneNumber) {
		this.authorizedPhoneNumber = authorizedPhoneNumber;
	}
	/**
	 * @return the destinationOfficeId
	 */
	public Integer getDestinationOfficeId() {
		return destinationOfficeId;
	}
	/**
	 * @param destinationOfficeId the destinationOfficeId to set
	 */
	public void setDestinationOfficeId(Integer destinationOfficeId) {
		this.destinationOfficeId = destinationOfficeId;
	}
	/**
	 * @return the authorizedTypeId
	 */
	public String getAuthorizedTypeId() {
		return authorizedTypeId;
	}
	/**
	 * @param authorizedTypeId the authorizedTypeId to set
	 */
	public void setAuthorizedTypeId(String authorizedTypeId) {
		this.authorizedTypeId = authorizedTypeId;
	}
	/**
	 * @return the authorizedId
	 */
	public String getAuthorizedId() {
		return authorizedId;
	}
	/**
	 * @param authorizedId the authorizedId to set
	 */
	public void setAuthorizedId(String authorizedId) {
		this.authorizedId = authorizedId;
	}
	/**
	 * @return the authorized
	 */
	public String getAuthorized() {
		return authorized;
	}
	/**
	 * @param authorized the authorized to set
	 */
	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the purpose
	 */
	public String getPurpose() {
		return purpose;
	}
	/**
	 * @param purpose the purpose to set
	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
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
	
}
