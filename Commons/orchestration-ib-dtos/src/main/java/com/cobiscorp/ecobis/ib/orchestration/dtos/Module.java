/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Resulset Modulo (Sintesis)
 * 
 * @author dmorla
 * @since 03/07/2015
 * @version 1.0.0
 */
public class Module {
	private Integer codModule;
	private String description;
	private String type;

	/**
	 * @return the codModule
	 */
	public Integer getCodModule() {
		return codModule;
	}

	/**
	 * @param codModule
	 *            the codModule to set
	 */
	public void setCodModule(Integer codModule) {
		this.codModule = codModule;
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
