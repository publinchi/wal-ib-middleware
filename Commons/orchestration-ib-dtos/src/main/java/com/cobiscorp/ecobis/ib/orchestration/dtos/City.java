/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author dguerra
 * @since Sep 18, 2014
 * @version 1.0.0
 */
public class City {

	/**
	 * code of city
	 */
	private Integer codeCity;

	/**
	 * name of city
	 */
	private String nameCity;

	/**
	 * object type country
	 */
	private Country country;

	public Integer getCodeCity() {
		return codeCity;
	}

	public void setCodeCity(Integer codeCity) {
		this.codeCity = codeCity;
	}

	public String getNameCity() {
		return nameCity;
	}

	public void setNameCity(String nameCity) {
		this.nameCity = nameCity;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
