/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since 02/07/2015
 * @version 1.0.0
 */
public class Label {
	private String codeCriteria;
	private String Label;
	private String criteriaType;
	/**
	 * @return the criteriaType
	 */
	public String getCriteriaType() {
		return criteriaType;
	}
	/**
	 * @param criteriaType the criteriaType to set
	 */
	public void setCriteriaType(String criteriaType) {
		this.criteriaType = criteriaType;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return Label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		Label = label;
	}
	/**
	 * @return the codeCriteria
	 */
	public String getCodeCriteria() {
		return codeCriteria;
	}
	/**
	 * @param codeCriteria the codeCriteria to set
	 */
	public void setCodeCriteria(String codeCriteria) {
		this.codeCriteria = codeCriteria;
	}
	

}
