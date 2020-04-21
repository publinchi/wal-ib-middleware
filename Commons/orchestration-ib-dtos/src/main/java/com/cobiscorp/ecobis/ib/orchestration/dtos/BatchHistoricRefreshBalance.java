/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author gcondo
 * @since Feb 11, 2015
 * @version 1.0.0
 */
public class BatchHistoricRefreshBalance {

	private int subsidiary; //filial
	private String bankAccount;//cuentaBanco
	private int currentAccount;//cuentaCorriente
	private int sequential;//secuencial	
	private BigDecimal value;//valor
	private BigDecimal lockedAmount;//montobloqueado
	private String date;//fecha
	private String expirationDate; //fechaVencimiento
	private String hour;//hora
	private String authorizing;//autorizante
	private String applicant;//solicitante
	private int officeBlock;// oficinaBloqueo
	private String cause; // cusa
	private BigDecimal balance; // saldo
	private String action;//accion
	private String raised; //levantado
	private int associatedIndustry; //sectorAsociado
	private int accountOffice;//oficinaCuenta
	
	/**
	 * @return the subsidiary
	 */
	public int getSubsidiary() {
		return subsidiary;
	}
	/**
	 * @param subsidiary the subsidiary to set
	 */
	public void setSubsidiary(int subsidiary) {
		this.subsidiary = subsidiary;
	}
	/**
	 * @return the bankAccount
	 */
	public String getBankAccount() {
		return bankAccount;
	}
	/**
	 * @param bankAccount the bankAccount to set
	 */
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	/**
	 * @return the currentAccount
	 */
	public int getCurrentAccount() {
		return currentAccount;
	}
	/**
	 * @param currentAccount the currentAccount to set
	 */
	public void setCurrentAccount(int currentAccount) {
		this.currentAccount = currentAccount;
	}
	/**
	 * @return the sequential
	 */
	public int getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(int sequential) {
		this.sequential = sequential;
	}
	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	/**
	 * @return the lockedAmount
	 */
	public BigDecimal getLockedAmount() {
		return lockedAmount;
	}
	/**
	 * @param lockedAmount the lockedAmount to set
	 */
	public void setLockedAmount(BigDecimal lockedAmount) {
		this.lockedAmount = lockedAmount;
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
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}
	/**
	 * @param hour the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
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
	 * @return the officeBlock
	 */
	public int getOfficeBlock() {
		return officeBlock;
	}
	/**
	 * @param officeBlock the officeBlock to set
	 */
	public void setOfficeBlock(int officeBlock) {
		this.officeBlock = officeBlock;
	}
	/**
	 * @return the causa
	 */
	public String getCause() {
		return cause;
	}
	/**
	 * @param causa the causa to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}
	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the raised
	 */
	public String getRaised() {
		return raised;
	}
	/**
	 * @param raised the raised to set
	 */
	public void setRaised(String raised) {
		this.raised = raised;
	}
	/**
	 * @return the associatedIndustry
	 */
	public int getAssociatedIndustry() {
		return associatedIndustry;
	}
	/**
	 * @param associatedIndustry the associatedIndustry to set
	 */
	public void setAssociatedIndustry(int associatedIndustry) {
		this.associatedIndustry = associatedIndustry;
	}
	/**
	 * @return the accountOffice
	 */
	public int getAccountOffice() {
		return accountOffice;
	}
	/**
	 * @param accountOffice the accountOffice to set
	 */
	public void setAccountOffice(int accountOffice) {
		this.accountOffice = accountOffice;
	}
	
	
	
	

}
