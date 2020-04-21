/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author itorres
 * @since Nov 25, 2014
 * @version 1.0.0
 */
public class AccountOperation {
	private String parameter;
	private String parameterDescription;
	private Double factor;
	private String concept;
	private Double amount;
	private Integer sequentialOperation;
	private Integer number;
	private String term;
	private Integer detailSequentialTransaction;
	private String detailSequentialPaymentDate;
	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}
	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	/**
	 * @return the parameterDescription
	 */
	public String getParameterDescription() {
		return parameterDescription;
	}
	/**
	 * @param parameterDescription the parameterDescription to set
	 */
	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}
	/**
	 * @return the factor
	 */
	public Double getFactor() {
		return factor;
	}
	/**
	 * @param factor the factor to set
	 */
	public void setFactor(Double factor) {
		this.factor = factor;
	}
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
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
	 * @return the sequentialOperation
	 */
	public Integer getSequentialOperation() {
		return sequentialOperation;
	}
	/**
	 * @param sequentialOperation the sequentialOperation to set
	 */
	public void setSequentialOperation(Integer sequentialOperation) {
		this.sequentialOperation = sequentialOperation;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	/**
	 * @return the detailSequentialTransaction
	 */
	public Integer getDetailSequentialTransaction() {
		return detailSequentialTransaction;
	}
	/**
	 * @param detailSequentialTransaction the detailSequentialTransaction to set
	 */
	public void setDetailSequentialTransaction(Integer detailSequentialTransaction) {
		this.detailSequentialTransaction = detailSequentialTransaction;
	}
	/**
	 * @return the detailSequentialPaymentDate
	 */
	public String getDetailSequentialPaymentDate() {
		return detailSequentialPaymentDate;
	}
	/**
	 * @param detailSequentialPaymentDate the detailSequentialPaymentDate to set
	 */
	public void setDetailSequentialPaymentDate(String detailSequentialPaymentDate) {
		this.detailSequentialPaymentDate = detailSequentialPaymentDate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccountOperation [parameter=" + parameter
				+ ", parameterDescription=" + parameterDescription
				+ ", factor=" + factor + ", concept=" + concept + ", amount="
				+ amount + ", sequentialOperation=" + sequentialOperation
				+ ", number=" + number + ", term=" + term
				+ ", detailSequentialTransaction="
				+ detailSequentialTransaction
				+ ", detailSequentialPaymentDate="
				+ detailSequentialPaymentDate + "]";
	}

}
