package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about productBanking
 * 
 * @author djarrin
 * @since Aug 19, 2014
 * @version 1.0.0
 */
public class ProductBanking {
	
	/**
	 * id of Product Banking
	 */ 
	private Integer id;
	/**
	 * Description of Product Banking
	 */
	private String description;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
