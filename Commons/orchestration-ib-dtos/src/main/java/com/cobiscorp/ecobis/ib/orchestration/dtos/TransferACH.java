/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author kmeza
 * @since Jan 13, 2015
 * @version 1.0.0
 */
public class TransferACH {

	private String paymentDate;
	private Integer productType;
	private String accountAlias;
	private String creditAccount;
	private String entityName;
	private Double amount;
	private String notes;
	private String creationDate;
	private Integer secuential;
	private String beneficiaryName;
	private Integer beneficiaryId;
	private String beneficiaryPhone;
	private String order;
	private Integer currencyId;

	@Override
	public String toString() {
		return "TransferACH [paymentDate=" + paymentDate + ", productType="
				+ getProductType() + ", accountAlias=" + accountAlias
				+ ", creditAccount=" + creditAccount + ", entityName="
				+ entityName + ", amount=" + amount + ", notes=" + notes
				+ ", creationDate=" + creationDate + ", secuential="
				+ secuential + ", beneficiaryName=" + beneficiaryName
				+ ", beneficiaryId=" + beneficiaryId + ", beneficiaryPhone="
				+ beneficiaryPhone + ", order=" + order +  ", currencyId=" + currencyId.toString() + "]";
	}

	/**
	 * @return the paymentDate
	 */
	public String getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate
	 *            the paymentDate to set
	 */
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the productType
	 */
	public Integer getProductType() {
		return productType;
	}

	/**
	 * @param productType
	 *            the productType to set
	 */
	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	/**
	 * @return the accountAlias
	 */
	public String getAccountAlias() {
		return accountAlias;
	}

	/**
	 * @param accountAlias
	 *            the accountAlias to set
	 */
	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	/**
	 * @return the creditAccount
	 */
	public String getCreditAccount() {
		return creditAccount;
	}

	/**
	 * @param creditAccount
	 *            the creditAccount to set
	 */
	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the secuential
	 */
	public Integer getSecuential() {
		return secuential;
	}

	/**
	 * @param secuential
	 *            the secuential to set
	 */
	public void setSecuential(Integer secuential) {
		this.secuential = secuential;
	}

	/**
	 * @return the beneficiaryName
	 */
	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	/**
	 * @param beneficiaryName
	 *            the beneficiaryName to set
	 */
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	/**
	 * @return the beneficiaryId
	 */
	public Integer getBeneficiaryId() {
		return beneficiaryId;
	}

	/**
	 * @param beneficiaryId
	 *            the beneficiaryId to set
	 */
	public void setBeneficiaryId(Integer beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	/**
	 * @return the beneficiaryPhone
	 */
	public String getBeneficiaryPhone() {
		return beneficiaryPhone;
	}

	/**
	 * @param beneficiaryPhone
	 *            the beneficiaryPhone to set
	 */
	public void setBeneficiaryPhone(String beneficiaryPhone) {
		this.beneficiaryPhone = beneficiaryPhone;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
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
	 * 
	 */
	public TransferACH() {
		// TODO Auto-generated constructor stub
	}

}
