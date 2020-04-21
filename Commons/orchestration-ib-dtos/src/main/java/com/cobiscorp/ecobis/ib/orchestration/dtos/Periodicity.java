/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class Periodicity {

	private String value;
	private String description;
	private String factor;
	private String percentage;
	private String daysFactor;
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the factor
	 */
	public String getFactor() {
		return factor;
	}
	/**
	 * @param factor the factor to set
	 */
	public void setFactor(String factor) {
		this.factor = factor;
	}
	/**
	 * @return the percentage
	 */
	public String getPercentage() {
		return percentage;
	}
	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	/**
	 * @return the daysFactor
	 */
	public String getDaysFactor() {
		return daysFactor;
	}
	/**
	 * @param daysFactor the daysFactor to set
	 */
	public void setDaysFactor(String daysFactor) {
		this.daysFactor = daysFactor;
	}
	
}
