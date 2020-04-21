/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about catalog cl_regional
 * 
 * @author dmorla
 * @since 12/10/2015
 * @version 1.0.0
 */
public class GRB {
	/**
	 * id of the GRB
	 */
	private Integer id;
	/**
	 * description of the operation
	 */
	private String operation;
	private String name;
	private String currency;
	private Integer currencyCode;
	private String launchingdate;
	private String expirationdate;
	private String amount;
	
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
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
	 * @return the launchingdate
	 */
	public String getLaunchingdate() {
		return launchingdate;
	}
	/**
	 * @param launchingdate the launchingdate to set
	 */
	public void setLaunchingdate(String launchingdate) {
		this.launchingdate = launchingdate;
	}
	/**
	 * @return the expirationdate
	 */
	public String getExpirationdate() {
		return expirationdate;
	}
	/**
	 * @param expirationdate the expirationdate to set
	 */
	public void setExpirationdate(String expirationdate) {
		this.expirationdate = expirationdate;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the currencyCode
	 */
	public Integer getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(Integer currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	

}
