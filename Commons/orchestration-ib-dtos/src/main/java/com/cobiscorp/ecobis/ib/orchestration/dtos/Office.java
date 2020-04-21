package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about office
 *
 * @author djarrin
 * @since Aug 19, 2014
 * @version 1.0.0
 */
public class Office {
	/**
	 * Information code as code swift, abba, ..
	 */
	private String code;
	/**
	 * Subtype of office
	 */
	private String subtype;

	/**
	 * Id of the office
	 */
	private Integer id;

	/**
	 * description of the office
	 */
	private String description;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param subtype
	 *            the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Office [code=" + code + ", subtype=" + subtype + ", id=" + id + ", description=" + description + "]";
	}

}
