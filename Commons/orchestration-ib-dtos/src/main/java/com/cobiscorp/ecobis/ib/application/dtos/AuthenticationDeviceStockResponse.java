/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 * @author bborja
 * @since 22/05/2015
 * @version 1.0.0
 */
public class AuthenticationDeviceStockResponse extends BaseResponse{

	private String accessAuthType;
	private short accessRetry;
	private String accessState;
	private String trxAuthType;
	private short trxRetry;
	private String trxState;
	private String modDate;
	private String officer;
	private String terminal;
	private String authorized;
	private int alternateCode;
	private int originalSsn;
	private String trxSerialNumber;
	/**
	 * @return the trxRetry
	 */
	public short getTrxRetry() {
		return trxRetry;
	}
	/**
	 * @param trxRetry the trxRetry to set
	 */
	public void setTrxRetry(short trxRetry) {
		this.trxRetry = trxRetry;
	}
	/**
	 * @return the trxState
	 */
	public String getTrxState() {
		return trxState;
	}
	/**
	 * @param trxState the trxState to set
	 */
	public void setTrxState(String trxState) {
		this.trxState = trxState;
	}
	private Product product;
	/**
	 * @return the accessAuthType
	 */
	public String getAccessAuthType() {
		return accessAuthType;
	}
	/**
	 * @param accessAuthType the accessAuthType to set
	 */
	public void setAccessAuthType(String accessAuthType) {
		this.accessAuthType = accessAuthType;
	}
	/**
	 * @return the accessRetry
	 */
	public short getAccessRetry() {
		return accessRetry;
	}
	/**
	 * @param accessRetry the accessRetry to set
	 */
	public void setAccessRetry(short accessRetry) {
		this.accessRetry = accessRetry;
	}
	/**
	 * @return the accessState
	 */
	public String getAccessState() {
		return accessState;
	}
	/**
	 * @param accessState the accessState to set
	 */
	public void setAccessState(String accessState) {
		this.accessState = accessState;
	}
	/**
	 * @return the trxAuthType
	 */
	public String getTrxAuthType() {
		return trxAuthType;
	}
	/**
	 * @param trxAuthType the trxAuthType to set
	 */
	public void setTrxAuthType(String trxAuthType) {
		this.trxAuthType = trxAuthType;
	}
	/**
	 * @return the modDate
	 */
	public String getModDate() {
		return modDate;
	}
	/**
	 * @param modDate the modDate to set
	 */
	public void setModDate(String modDate) {
		this.modDate = modDate;
	}
	/**
	 * @return the officer
	 */
	public String getOfficer() {
		return officer;
	}
	/**
	 * @param officer the officer to set
	 */
	public void setOfficer(String officer) {
		this.officer = officer;
	}
	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}
	/**
	 * @param terminal the terminal to set
	 */
	public void setTerminal(String terminal) {
		this.terminal = terminal;
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
	 * @return the alternateCode
	 */
	public int getAlternateCode() {
		return alternateCode;
	}
	/**
	 * @param alternateCode the alternateCode to set
	 */
	public void setAlternateCode(int alternateCode) {
		this.alternateCode = alternateCode;
	}
	/**
	 * @return the originalSsn
	 */
	public int getOriginalSsn() {
		return originalSsn;
	}
	/**
	 * @param originalSsn the originalSsn to set
	 */
	public void setOriginalSsn(int originalSsn) {
		this.originalSsn = originalSsn;
	}
	/**
	 * @return the trxSerialNumber
	 */
	public String getTrxSerialNumber() {
		return trxSerialNumber;
	}
	/**
	 * @param trxSerialNumber the trxSerialNumber to set
	 */
	public void setTrxSerialNumber(String trxSerialNumber) {
		this.trxSerialNumber = trxSerialNumber;
	}
	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UpdateAuthenticationDeviceStockResponse [accessAuthType="
				+ accessAuthType + ", accessRetry=" + accessRetry
				+ ", accessState=" + accessState + ", trxAuthType="
				+ trxAuthType + ", trxRetry=" + trxRetry + ", trxState="
				+ trxState + ", modDate=" + modDate + ", officer=" + officer
				+ ", terminal=" + terminal + ", authorized=" + authorized
				+ ", alternateCode=" + alternateCode + ", originalSsn="
				+ originalSsn + ", trxSerialNumber=" + trxSerialNumber
				+ ", product=" + product + "]";
	}

	
}
