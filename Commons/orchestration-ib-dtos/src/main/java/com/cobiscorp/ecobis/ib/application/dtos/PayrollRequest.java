/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author tbaidal
 *
 */
public class PayrollRequest {
	private String operation;
	private String fileId;
	private String accountNumber;
	private String pageRows;
	private String pendingTransaction;
	private String ssn;
	private String channel;
	private String massive;
	
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
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}



	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
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
	 * @return the pageRows
	 */
	public String getPageRows() {
		return pageRows;
	}



	/**
	 * @param pageRows the pageRows to set
	 */
	public void setPageRows(String pageRows) {
		this.pageRows = pageRows;
	}


	/**
	 * @return the pendingTransaction
	 */
	public String getPendingTransaction() {
		return pendingTransaction;
	}



	/**
	 * @param pendingTransaction the pendingTransaction to set
	 */
	public void setPendingTransaction(String pendingTransaction) {
		this.pendingTransaction = pendingTransaction;
	}



	/**
	 * @return the ssn
	 */
	public String getSsn() {
		return ssn;
	}



	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}



	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}



	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}



	/**
	 * @return the massive
	 */
	public String getMassive() {
		return massive;
	}



	/**
	 * @param massive the massive to set
	 */
	public void setMassive(String massive) {
		this.massive = massive;
	}



	@Override
	public String toString() {
		return "PaymentAccountRequest [operation=" + operation + ", fileId=" + fileId + ", accountNumber="
				+ accountNumber + ", pageRows=" + pageRows + "]";
	}
	
	

}
