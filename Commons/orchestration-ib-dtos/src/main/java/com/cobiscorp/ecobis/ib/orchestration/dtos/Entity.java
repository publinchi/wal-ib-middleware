/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class Entity {
	private Integer codCustomer;
	private Integer ente;
	private Integer company;
	private String name;

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
	 * @return the codCustomer
	 */
	public Integer getCodCustomer() {
		return codCustomer;
	}

	/**
	 * @param codCustomer
	 *            the codCustomer to set
	 */
	public void setCodCustomer(Integer codCustomer) {
		this.codCustomer = codCustomer;
	}

	/**
	 * @return the ente
	 */
	public Integer getEnte() {
		return ente;
	}

	/**
	 * @param ente
	 *            the ente to set
	 */
	public void setEnte(Integer ente) {
		this.ente = ente;
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

}
