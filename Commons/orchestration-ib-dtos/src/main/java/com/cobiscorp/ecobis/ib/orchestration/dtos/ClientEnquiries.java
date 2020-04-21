/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since 14/08/2015
 * @version 1.0.0
 */
public class ClientEnquiries {
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
	 * @return the enquiryDate
	 */
	public String getEnquiryDate() {
		return enquiryDate;
	}
	/**
	 * @param enquiryDate the enquiryDate to set
	 */
	public void setEnquiryDate(String enquiryDate) {
		this.enquiryDate = enquiryDate;
	}
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
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	private Integer id;
	private String enquiryDate;
	private Double amount;
	private String account;
	private String state;
	private String beneficiary;
	private String quantity;
	private Integer idAux;
	private Integer currencyId;
	private String endorsementType;
	private String endorsement;
	private String subType;
	
	
	/**
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}
	/**
	 * @param subType the subType to set
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}
	/**
	 * @return the endorsementType
	 */
	public String getEndorsementType() {
		return endorsementType;
	}
	/**
	 * @param endorsementType the endorsementType to set
	 */
	public void setEndorsementType(String endorsementType) {
		this.endorsementType = endorsementType;
	}
	/**
	 * @return the endorsement
	 */
	public String getEndorsement() {
		return endorsement;
	}
	/**
	 * @param endorsement the endorsement to set
	 */
	public void setEndorsement(String endorsement) {
		this.endorsement = endorsement;
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
	 * @return the idAux
	 */
	public Integer getIdAux() {
		return idAux;
	}
	/**
	 * @param idAux the idAux to set
	 */
	public void setIdAux(Integer idAux) {
		this.idAux = idAux;
	}
	
}
