/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Oct 28, 2014
 * @version 1.0.0
 */
public class BeneficiaryCertificateDeposit {

	private String cedula;
	private String name;
	private String firstSurname;
	private String relation;
	private Double percentage;
	private String lastSurname;
	/**
	 * @return the cedula
	 */
	public String getCedula() {
		return cedula;
	}
	/**
	 * @param cedula the cedula to set
	 */
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
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
	 * @return the firstSurname
	 */
	public String getFirstSurname() {
		return firstSurname;
	}
	/**
	 * @param firstSurname the firstSurname to set
	 */
	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}
	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	/**
	 * @return the percentage
	 */
	public Double getPercentage() {
		return percentage;
	}
	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	/**
	 * @return the lastSurname
	 */
	public String getLastSurname() {
		return lastSurname;
	}
	/**
	 * @param lastSurname the lastSurname to set
	 */
	public void setLastSurname(String lastSurname) {
		this.lastSurname = lastSurname;
	}
	
	
	
}
