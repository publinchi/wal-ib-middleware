/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author cecheverria
 * @since 07/01/2016
 * @version 1.0.0
 */

public class BatchEtlTotal  {
int company ;
public int getCompany() {
	return company;
}
public void setCompany(int company) {
	this.company = company;
}
public String getProcessDate() {
	return processDate;
}
public void setProcessDate(String processDate) {
	this.processDate = processDate;
}
public int getModule() {
	return module;
}
public void setModule(int module) {
	this.module = module;
}
public String getTable() {
	return table;
}
public void setTable(String table) {
	this.table = table;
}
public String getCriteria() {
	return criteria;
}
public void setCriteria(String criteria) {
	this.criteria = criteria;
}
public String getCriteriaValue() {
	return criteriaValue;
}
public void setCriteriaValue(String criteriaValue) {
	this.criteriaValue = criteriaValue;
}
public int getNumberOfRecords() {
	return numberOfRecords;
}
public void setNumberOfRecords(int numberOfRecords) {
	this.numberOfRecords = numberOfRecords;
}
public BigDecimal getValue() {
	return value;
}
public void setValue(BigDecimal value) {
	this.value = value;
}
String processDate;
int module;
String table;
String  criteria;
String  criteriaValue;
int numberOfRecords;
BigDecimal value;



	
}
