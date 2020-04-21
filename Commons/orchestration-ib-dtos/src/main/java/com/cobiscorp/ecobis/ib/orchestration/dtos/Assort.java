/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author dmorla
 * @since 18/06/2015
 * @version 1.0.0
 */
public class Assort {

	private Integer company;
	private String dateProcess;
	private Integer module;
	private String sourceTable;
	private String criteriaField;
	private String criteriaValue;
	private Integer registerNumber;
	private BigDecimal value;

	/**
	 * @return the company
	 */
	public Integer getCompany() {
		return company;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(Integer company) {
		this.company = company;
	}

	/**
	 * @return the dateProcess
	 */
	public String getDateProcess() {
		return dateProcess;
	}

	/**
	 * @param dateProcess
	 *            the dateProcess to set
	 */
	public void setDateProcess(String dateProcess) {
		this.dateProcess = dateProcess;
	}

	/**
	 * @return the module
	 */
	public Integer getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(Integer module) {
		this.module = module;
	}

	/**
	 * @return the sourceTable
	 */
	public String getSourceTable() {
		return sourceTable;
	}

	/**
	 * @param sourceTable
	 *            the sourceTable to set
	 */
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	/**
	 * @return the criteriaField
	 */
	public String getCriteriaField() {
		return criteriaField;
	}

	/**
	 * @param criteriaField
	 *            the criteriaField to set
	 */
	public void setCriteriaField(String criteriaField) {
		this.criteriaField = criteriaField;
	}

	/**
	 * @return the criteriaValue
	 */
	public String getCriteriaValue() {
		return criteriaValue;
	}

	/**
	 * @param criteriaValue
	 *            the criteriaValue to set
	 */
	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}

	/**
	 * @return the registerNumber
	 */
	public Integer getRegisterNumber() {
		return registerNumber;
	}

	/**
	 * @param registerNumber
	 *            the registerNumber to set
	 */
	public void setRegisterNumber(Integer registerNumber) {
		this.registerNumber = registerNumber;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
