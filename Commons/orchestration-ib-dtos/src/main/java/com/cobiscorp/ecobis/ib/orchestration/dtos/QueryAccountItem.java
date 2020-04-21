/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * Resulset Items Busqueda Cuentas
 * 
 * @author dmorla
 * @since 23/07/2015
 * @version 1.0.0
 */
public class QueryAccountItem {
	private String itemPending;
	private String itemDescription;
	private String currency;
	private String dependency;
	private String paymentMethod;
	private BigDecimal amount;
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the itemPending
	 */
	public String getItemPending() {
		return itemPending;
	}
	/**
	 * @param itemPending the itemPending to set
	 */
	public void setItemPending(String itemPending) {
		this.itemPending = itemPending;
	}
	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}
	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
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
	 * @return the dependency
	 */
	public String getDependency() {
		return dependency;
	}
	/**
	 * @param dependency the dependency to set
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
	/**
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	
}
