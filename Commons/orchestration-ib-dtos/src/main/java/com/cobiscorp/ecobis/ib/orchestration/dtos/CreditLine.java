/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * Resulset Stock Double Autentication
 * 
 * @author dmorla
 * @since 05/06/2015
 * @version 1.0.0
 */
public class CreditLine {
	private Integer code;
	private String credit;
	private Integer currency;
	private String openingDate;
	private String expirationDate;
	private BigDecimal amount;
	/**
	 * @return the currency
	 */
	public Integer getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Integer currency) {
		this.currency = currency;
	}

	private String money;
	private BigDecimal available;

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the credit
	 */
	public String getCredit() {
		return credit;
	}

	/**
	 * @param credit
	 *            the credit to set
	 */
	public void setCredit(String credit) {
		this.credit = credit;
	}

	/**
	 * @return the money
	 */
	public String getMoney() {
		return money;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setMoney(String money) {
		this.money = money;
	}

	/**
	 * @return the available
	 */
	public BigDecimal getAvailable() {
		return available;
	}

	/**
	 * @param available
	 *            the available to set
	 */
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	
	
	/**
	 * @return the openingDate
	 */
	public String getOpeningDate() {
		return openingDate;
	}

	/**
	 * @param openingDate the openingDate to set
	 */
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}

	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

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
	
	
	

}
