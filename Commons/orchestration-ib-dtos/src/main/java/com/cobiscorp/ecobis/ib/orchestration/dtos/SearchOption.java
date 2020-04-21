/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;


/**
 * @author jchonillo
 * @since Jan 17, 2015
 * @version 1.0.0
 */
public class SearchOption {

	private String criteria;
	private Double exchangeRate;
	private String notes;
	private String initialDate;
	private String finalDate;
	
	/**
	 * @return the finalDate
	 */
	public String getFinalDate() {
		return finalDate;
	}
	/**
	 * @param finalDate the finalDate to set
	 */
	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}
	/**
	 * @return the initialDate
	 */
	public String getInitialDate() {
		return initialDate;
	}
	/**
	 * @param initialDate the initialDate to set
	 */
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	public Double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		return "SearchOption [criteria=" + criteria + ", exchangeRate="
				+ exchangeRate + ", notes=" + notes + "]";
	}
	
}
