package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * contain information about bank
 *
 * @author eortega
 * @since september 16, 2014
 * @version 1.0.0
 */
public class Bank {

	/**
	 * code of the bank
	 */
	private Integer id;

	/**
	 * name of the bank
	 */
	private String description;

	/**
	 *
	 */
	private String convenio;

	/**
	 *
	 */
	private String bancoEnte;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getBancoEnte() {
		return bancoEnte;
	}

	public void setBancoEnte(String bancoEnte) {
		this.bancoEnte = bancoEnte;
	}

	@Override
	public String toString() {
		return "Bank [id=" + id + ", description=" + description + ", convenio=" + convenio + ", bancoEnte=" + bancoEnte + "]";
	}

}
