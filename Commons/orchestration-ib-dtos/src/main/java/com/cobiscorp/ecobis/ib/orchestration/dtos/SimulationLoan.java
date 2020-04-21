/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Dec 18, 2014
 * @version 1.0.0
 */
public class SimulationLoan {
	
	private String operation;
	private String code;
	private Double payment;
	private Integer term;
	
	private Double amount;
	private String sector;
	private String operationType;
	private Integer currencyId;
	private String inicialDate;
	
	//return
	private String endDate;
	private Double percentage;
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the payment
	 */
	public Double getPayment() {
		return payment;
	}
	/**
	 * @param payment the payment to set
	 */
	public void setPayment(Double payment) {
		this.payment = payment;
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
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}
	/**
	 * @param sector the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}
	/**
	 * @return the operationTyoe
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * @param operationTyoe the operationTyoe to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
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
	 * @return the inicialDate
	 */
	public String getInicialDate() {
		return inicialDate;
	}
	/**
	 * @param inicialDate the inicialDate to set
	 */
	public void setInicialDate(String inicialDate) {
		this.inicialDate = inicialDate;
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
	 * @return the percentaje
	 */
	public Double getPercentage() {
		return percentage;
	}
	/**
	 * @param percentage the percentaje to set
	 */
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimulationLoan [operation=" + operation + ", code=" + code
				+ ", payment=" + payment + ", term=" + term + ", amount="
				+ amount + ", sector=" + sector + ", operationType="
				+ operationType + ", currencyId=" + currencyId
				+ ", inicialDate=" + inicialDate + ", endDate=" + endDate
				+ ", percentage=" + percentage + "]";
	}
	
	
	

}
