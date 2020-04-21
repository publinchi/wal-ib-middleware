/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * SubTipo de Garantias (Boleta de Garantia)
 * 
 * @author dmorla
 * @since 03/08/2015
 * @version 1.0.0
 */
public class SubTypeGuarantee {
	private String id;
	private String value;

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
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
