/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about catalog cl_regional
 * 
 * @author dmorla
 * @since 05/06/2015
 * @version 1.0.0
 */
public class Region {
	/**
	 * id of the region
	 */
	private String id;
	/**
	 * description of the office
	 */
	private String description;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
