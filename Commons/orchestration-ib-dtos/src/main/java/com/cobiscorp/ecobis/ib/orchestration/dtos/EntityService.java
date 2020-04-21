/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author dmorla
 * @since 18/06/2015
 * @version 1.0.0
 */
public class EntityService {
	private Integer company;
	private Integer entity;
	private Integer service;
	private String state;
	private String creator;
	private String date;
	private Integer office;
	private Integer aux;
	private Integer category;

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

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
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}

	/**
	 * @return the service
	 */
	public Integer getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Integer service) {
		this.service = service;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}

	/**
	 * @return the aux
	 */
	public Integer getAux() {
		return aux;
	}

	/**
	 * @param aux
	 *            the aux to set
	 */
	public void setAux(Integer aux) {
		this.aux = aux;
	}
}
