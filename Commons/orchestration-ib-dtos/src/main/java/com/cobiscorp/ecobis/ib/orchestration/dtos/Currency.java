/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author schancay
 * @since Aug 27, 2014
 * @version 1.0.0
 */
public class Currency {
	/**
	 * Nemonic currency (Eg. DOL,EUR)
	 */
	private String currencyNemonic;

	/**
	 * Currency Code (Eg. 1=DOLAR)
	 */
	private Integer CurrencyId;

	/**
	 * Currency Name (Eg. DOLAR)
	 */
	private String currencyDescription;

	/**
	 * Currency symbol (Eg. $)
	 */
	private String currencySymbol;
	
	private Integer countryCode;
	
	private String country;
	
	private String state;
	
	private String hasDecimal;
	
	public String getCurrencyDescription() {
		return currencyDescription;
	}
	public Integer getCurrencyId() {
		return CurrencyId;
	}

	/**
	 * @return the currencyNemonic
	 */
	public String getCurrencyNemonic() {
		return currencyNemonic;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencyDescription(String currencyDescription) {
		this.currencyDescription = currencyDescription;
	}

	public void setCurrencyId(Integer currencyId) {
		CurrencyId = currencyId;
	}

	/**
	 * @param currencyNemonic
	 *            the currencyNemonic to set
	 */
	public void setCurrencyNemonic(String currencyNemonic) {
		this.currencyNemonic = currencyNemonic;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	
	

	/**
	 * @return the countryCode
	 */
	public Integer getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the hasDecimal
	 */
	public String getHasDecimal() {
		return hasDecimal;
	}
	/**
	 * @param hasDecimal the hasDecimal to set
	 */
	public void setHasDecimal(String hasDecimal) {
		this.hasDecimal = hasDecimal;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Currency [currencyNemonic=" + currencyNemonic + ", CurrencyId="
				+ CurrencyId + ", currencyDescription=" + currencyDescription
				+ ", currencySymbol=" + currencySymbol + ", countryCode="
				+ countryCode + ", country=" + country + ", state=" + state
				+ ", hasDecimal=" + hasDecimal + "]";
	}
}
