/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * Resulset Detalle Solicitudes
 * 
 * @author dmorla
 * @since 19/08/2015
 * @version 1.0.0
 */
public class EnquiriesDetail {
	private String account;
	private String checkbookTipe;
	private Integer checks;
	private String delivery;
	private String state;
	private BigDecimal amount;
	private String purpose;
	private String beneficiary;
	private String thirdIdentification;
	private String name;
	private Integer applicationNumber;
	private String type;
	private String subtype;
	private Integer term;
	private String endDate;
	private String guarantee;
	private String endorsementType;
	private String endorsement;
	
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}
	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	/**
	 * @return the term
	 */
	public Integer getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(Integer term) {
		this.term = term;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the guarantee
	 */
	public String getGuarantee() {
		return guarantee;
	}
	/**
	 * @param guarantee the guarantee to set
	 */
	public void setGuarantee(String guarantee) {
		this.guarantee = guarantee;
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
	 * @return the applicationNumber
	 */
	public Integer getApplicationNumber() {
		return applicationNumber;
	}
	/**
	 * @param applicationNumber the applicationNumber to set
	 */
	public void setApplicationNumber(Integer applicationNumber) {
		this.applicationNumber = applicationNumber;
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
	 * @return the checkbookTipe
	 */
	public String getCheckbookTipe() {
		return checkbookTipe;
	}
	/**
	 * @param checkbookTipe the checkbookTipe to set
	 */
	public void setCheckbookTipe(String checkbookTipe) {
		this.checkbookTipe = checkbookTipe;
	}
	/**
	 * @return the checks
	 */
	public Integer getChecks() {
		return checks;
	}
	/**
	 * @param checks the checks to set
	 */
	public void setChecks(Integer checks) {
		this.checks = checks;
	}
	/**
	 * @return the delivery
	 */
	public String getDelivery() {
		return delivery;
	}
	/**
	 * @param delivery the delivery to set
	 */
	public void setDelivery(String delivery) {
		this.delivery = delivery;
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
	 * @return the thirdIdentification
	 */
	public String getThirdIdentification() {
		return thirdIdentification;
	}
	/**
	 * @param thirdIdentification the thirdIdentification to set
	 */
	public void setThirdIdentification(String thirdIdentification) {
		this.thirdIdentification = thirdIdentification;
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


	
}
