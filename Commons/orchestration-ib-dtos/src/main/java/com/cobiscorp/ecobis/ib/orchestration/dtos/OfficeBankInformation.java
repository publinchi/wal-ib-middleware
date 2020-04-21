/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Information general about of office banking with detail of city, office and bank.
 * 
 * @author schancay
 * @since Sep 17, 2014
 * @version 1.0.0
 */
public class OfficeBankInformation {
	/**
	 * Information of country about bank
	 */
	private Country country;
	/**
	 * Information of bank
	 */
	private Bank bank;
	/**
	 * Information of office
	 */
	private Office office;

	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @return the office
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * @param bank
	 *            the bank to set
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @param office
	 *            the office to set
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OfficeBankInformation [country=" + country + ", bank=" + bank + ", office=" + office + "]";
	}
}
