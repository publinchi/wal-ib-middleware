/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since Feb 18, 2015
 * @version 1.0.0
 */
public class BlockedAccount {

	private Integer subsidiary; //filial
	private String accountNumber;
	private Integer sequential;//secuencial	
	private String blockType;//valor
	private String blockDate;//fecha
	private String blockHour;//hora
	private String authorizing;//autorizante
	private String applicant;//solicitante
	private Integer office;// oficina 
	private String cause; // cusa
	private Integer accountId; // cusa
	/**
	 * @return the subsidiary
	 */
	public Integer getSubsidiary() {
		return subsidiary;
	}
	/**
	 * @return the accountId
	 */
	public Integer getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	/**
	 * @param subsidiary the subsidiary to set
	 */
	public void setSubsidiary(Integer subsidiary) {
		this.subsidiary = subsidiary;
	}
	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	/**
	 * @return the sequential
	 */
	public Integer getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}
	/**
	 * @return the blockType
	 */
	public String getBlockType() {
		return blockType;
	}
	/**
	 * @param blockType the blockType to set
	 */
	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}
	/**
	 * @return the blockDate
	 */
	public String getBlockDate() {
		return blockDate;
	}
	/**
	 * @param blockDate the blockDate to set
	 */
	public void setBlockDate(String blockDate) {
		this.blockDate = blockDate;
	}
	/**
	 * @return the blockHour
	 */
	public String getBlockHour() {
		return blockHour;
	}
	/**
	 * @param blockHour the blockHour to set
	 */
	public void setBlockHour(String blockHour) {
		this.blockHour = blockHour;
	}
	/**
	 * @return the authorizing
	 */
	public String getAuthorizing() {
		return authorizing;
	}
	/**
	 * @param authorizing the authorizing to set
	 */
	public void setAuthorizing(String authorizing) {
		this.authorizing = authorizing;
	}
	/**
	 * @return the applicant
	 */
	public String getApplicant() {
		return applicant;
	}
	/**
	 * @param applicant the applicant to set
	 */
	public void setApplicant(String applicant) {
		this.applicant = applicant;
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
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}
	/**
	 * @param cause the cause to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
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
	private String state; // cusa
}
