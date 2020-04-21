/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jmoreta
 * @since Nov 11, 2014
 * @version 1.0.0
 */
public class TypesOfCheckbook {

	/**idType, type*/
	private Type type;
	private Parameters name;
	private String art;
	private String customArt;
	private String quantity;
	private CheckBookPreAuth state;
	private Integer time;
	private Currency currency;
	private String amount;
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public Parameters getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(Parameters name) {
		this.name = name;
	}
	/**
	 * @return the art
	 */
	public String getArt() {
		return art;
	}
	/**
	 * @param art the art to set
	 */
	public void setArt(String art) {
		this.art = art;
	}
	/**
	 * @return the customArt
	 */
	public String getCustomArt() {
		return customArt;
	}
	/**
	 * @param customArt the customArt to set
	 */
	public void setCustomArt(String customArt) {
		this.customArt = customArt;
	}
	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the state
	 */
	public CheckBookPreAuth getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(CheckBookPreAuth state) {
		this.state = state;
	}
	/**
	 * @return the time
	 */
	public Integer getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Integer time) {
		this.time = time;
	}
	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TypesOfCheckbook [type=" + type + ", name=" + name + ", art="
				+ art + ", customArt=" + customArt + ", quantity=" + quantity
				+ ", state=" + state + ", time=" + time + ", currency="
				+ currency + ", amount=" + amount + "]";
	}
	
	
	
}
