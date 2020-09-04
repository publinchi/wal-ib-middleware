/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author tbaidal
 *
 */
public class PaymentAccountRequest {
	private String operation;
	private String fileId;
	private String accountNumber;
	private String pageRows;

	
	
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



	@Override
	public String toString() {
		return "PaymentAccountRequest [operation=" + operation + ", fileId=" + fileId + ", accountNumber="
				+ accountNumber + ", pageRows=" + pageRows + "]";
	}
	
	

}
