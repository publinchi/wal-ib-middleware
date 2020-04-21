/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since 01/07/2015
 * @version 1.0.0
 */
public class ModuleCriteria {
private Integer errorCode;
private String message;
private Criteria criteria;
private Label label;
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
/**
 * @return the errorCode
 */
public Integer getErrorCode() {
	return errorCode;
}
/**
 * @param errorCode the errorCode to set
 */
public void setErrorCode(Integer errorCode) {
	this.errorCode = errorCode;
}
/**
 * @return the message
 */
public String getMessage() {
	return message;
}
/**
 * @param message the message to set
 */
public void setMessage(String message) {
	this.message = message;
}
/**
 * @return the criteria
 */
public Criteria getCriteria() {
	return criteria;
}
/**
 * @param criteria the criteria to set
 */
public void setCriteria(Criteria criteria) {
	this.criteria = criteria;
}
}
