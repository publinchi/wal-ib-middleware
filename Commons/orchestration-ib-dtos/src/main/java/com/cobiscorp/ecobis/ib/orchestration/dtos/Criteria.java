/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since 02/07/2015
 * @version 1.0.0
 */
public class Criteria {
private String criteriaCode;
private String description;
private Label label;
/**
 * @return the criteriaCode
 */
public String getCriteriaCode() {
	return criteriaCode;
}
/**
 * @param criteriaCode the criteriaCode to set
 */
public void setCriteriaCode(String criteriaCode) {
	this.criteriaCode = criteriaCode;
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
 * @return the label
 */
public Label getLabel() {
	return label;
}
/**
 * @param label the label to set
 */
public void setLabel(Label label) {
	this.label = label;
}
}
