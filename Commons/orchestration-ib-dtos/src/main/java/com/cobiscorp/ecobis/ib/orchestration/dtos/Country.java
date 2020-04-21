package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * contain information abount countries
 *
 * @author dguerra
 * @since Aug 29, 2014
 * @version 1.0.0
 */
public class Country {

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Country [code=" + code + ", name=" + name + ", nationality=" + nationality + "]";
	}

	/**
	 * code of country
	 */
	private Integer code;

	/**
	 * name of country
	 */
	private String name;

	/**
	 * nacionality description
	 */
	private String nationality;

	/*
	 * the code of continent
	 */
	private String ContinentCode;

	/*
	 * the name of continent
	 */
	private String Continent;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getContinentCode() {
		return ContinentCode;
	}

	public void setContinentCode(String continentCode) {
		ContinentCode = continentCode;
	}

	public String getContinent() {
		return Continent;
	}

	public void setContinent(String continent) {
		Continent = continent;
	}

}
