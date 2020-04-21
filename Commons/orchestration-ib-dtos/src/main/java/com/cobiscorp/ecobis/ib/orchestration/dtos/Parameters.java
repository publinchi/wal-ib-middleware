/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Nov 5, 2014
 * @version 1.0.0
 */
public class Parameters {

	private String name;
	private String description;
	private String factor;
	private String percentage;
	private String factorDays;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the factorDays
	 */
	public String getFactorDays() {
		return factorDays;
	}
	/**
	 * @param factorDays the factorDays to set
	 */
	public void setFactorDays(String factorDays) {
		this.factorDays = factorDays;
	}
	
}
