/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

/**
 * @author bborja
 * @since 22/05/2015
 * @version 1.0.0
 */
public class AuthenticationDeviceStockRequest extends BaseRequest{
	private AuthenticationTypeRequest authenticationType;
	private Product product;
	private String referenceNumber;	
	private String date;
	private User user;
	private String operation;
	private String modAutTrx;
	private String modEstAutTrx;
	private String motiveTrx;
	private Client client;
	private String provider;
	private String accessAuthType;
	private short accessRetry;
	private String accessState;
	private String trxAuthType;
	private short trxRetry;
	private String trxState;
	private String trxSerialNumber;
	private String officer;
	private String authorized;
	private int alternateCode;
	private int originalSsn;
	private short dateFormat;
	private AccountingParameter accountingParameter;
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @return the authenticationType
	 */
	public AuthenticationTypeRequest getAuthenticationType() {
		return authenticationType;
	}
	/**
	 * @param authenticationType the authenticationType to set
	 */
	public void setAuthenticationType(AuthenticationTypeRequest authenticationType) {
		this.authenticationType = authenticationType;
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
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	/**
	 * @return the motiveTrx
	 */
	public String getMotiveTrx() {
		return motiveTrx;
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
	 * @param motiveTrx the motiveTrx to set
	 */
	public void setMotiveTrx(String motiveTrx) {
		this.motiveTrx = motiveTrx;
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
	 * @return the dateFormat
	 */
	public short getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(short dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the modAutTrx
	 */
	public String getModAutTrx() {
		return modAutTrx;
	}
	/**
	 * @param modAutTrx the modAutTrx to set
	 */
	public void setModAutTrx(String modAutTrx) {
		this.modAutTrx = modAutTrx;
	}
	/**
	 * @return the modEstAutTrx
	 */
	public String getModEstAutTrx() {
		return modEstAutTrx;
	}
	/**
	 * @param modEstAutTrx the modEstAutTrx to set
	 */
	public void setModEstAutTrx(String modEstAutTrx) {
		this.modEstAutTrx = modEstAutTrx;
	}
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	/**
	 * @return the provider
	 */
	public String getProvider() {
		return provider;
	}
	/**
	 * @param provider the provider to set
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthenticationDeviceStockRequest [referenceNumber="
				+ referenceNumber + ", user=" + user + ", operation="
				+ operation + ", modAutTrx=" + modAutTrx + ", modEstAutTrx="
				+ modEstAutTrx + ", motiveTrx=" + motiveTrx + ", client="
				+ client + ", provider=" + provider + ", accessAuthType="
				+ accessAuthType + ", accessRetry=" + accessRetry
				+ ", accessState=" + accessState + ", trxAuthType="
				+ trxAuthType + ", trxRetry=" + trxRetry + ", trxState="
				+ trxState + ", trxSerialNumber=" + trxSerialNumber
				+ ", officer=" + officer + ", authorized=" + authorized
				+ ", alternateCode=" + alternateCode + ", originalSsn="
				+ originalSsn + ", dateFormat=" + dateFormat + ", term=" + term
				+ ", officeCode=" + officeCode + ", role=" + role + ", userBv="
				+ userBv + "]";
	}

	/**
	 * @return the accountingParameter
	 */
	public AccountingParameter getAccountingParameter() {
		return accountingParameter;
	}

	/**
	 * @param accountingParameter the accountingParameter to set
	 */
	public void setAccountingParameter(AccountingParameter accountingParameter) {
		this.accountingParameter = accountingParameter;
	}
					
}
