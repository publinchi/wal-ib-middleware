/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Resultado Cuenta (Sintesis)
 * 
 * @author itorres
 * @since Jul 14, 2015
 * @version 1.0.0
 */
public class AccountHeader {
	private Integer codError;
	private String messageError;
	private Integer numOperation;
	private Integer operationDate;
	
	/**
	 * @return the codError
	 */
	public Integer getCodError() {
		return codError;
	}
	/**
	 * @param codError the codError to set
	 */
	public void setCodError(Integer codError) {
		this.codError = codError;
	}
	/**
	 * @return the messageError
	 */
	public String getMessageError() {
		return messageError;
	}
	/**
	 * @param messageError the messageError to set
	 */
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}	
	/**
	 * @return the numOperation
	 */
	public Integer getNumOperation() {
		return numOperation;
	}
	/**
	 * @param numOperation the numOperation to set
	 */
	public void setNumOperation(Integer numOperation) {
		this.numOperation = numOperation;
	}
	/**
	 * @return the operationDate
	 */
	public Integer getOperationDate() {
		return operationDate;
	}
	/**
	 * @param operationDate the operationDate to set
	 */
	public void setOperationDate(Integer operationDate) {
		this.operationDate = operationDate;
	}	
	
}
