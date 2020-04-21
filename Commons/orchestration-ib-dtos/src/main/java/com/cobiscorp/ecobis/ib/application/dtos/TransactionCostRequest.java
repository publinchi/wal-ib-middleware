/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 * @author bborja
 * @since 27/2/2015
 * @version 1.0.0
 */
public class TransactionCostRequest extends BaseRequest {
	private Product account;
	private Client client;
	
	private int trnId;
	private String serviceId;
	private String entryId;
	private String operation;

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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionCostRequest [account=" + account + ", client="
				+ client + ", trnId=" + trnId
				+ ", serviceId=" + serviceId + ", entryId=" + entryId + "]";
	}
	/**
	 * @return the account
	 */
	public Product getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(Product account) {
		this.account = account;
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
	 * @return the trnId
	 */
	public int getTrnId() {
		return trnId;
	}
	/**
	 * @param trnId the trnId to set
	 */
	public void setTrnId(int trnId) {
		this.trnId = trnId;
	}
	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the entryId
	 */
	public String getEntryId() {
		return entryId;
	}
	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}
	
}
