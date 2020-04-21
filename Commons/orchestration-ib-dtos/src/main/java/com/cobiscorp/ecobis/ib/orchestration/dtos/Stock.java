/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Resulset Stock Double Autentication
 * 
 * @author dmorla
 * @since 05/06/2015
 * @version 1.0.0
 */
public class Stock {
	private Integer sequential;
	private String cod_region;
	private String region;
	private Integer cod_city;
	private String city;
	private Integer cod_office;
	private String office;
	private Integer no_assigned;
	private Integer assigned;
	private Integer stock;
	private String fecha;
	private String tipo;

	/**
	 * @return the stock
	 */
	public Integer getStock() {
		return stock;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock(Integer stock) {
		this.stock = stock;
	}

	 
	/**
	 * @return the sequential
	 */
	public Integer getSequential() {
		return sequential;
	}

	/**
	 * @param sequential
	 *            the sequential to set
	 */
	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}

	/**
	 * @return the cod_region
	 */
	public String getCod_region() {
		return cod_region;
	}

	/**
	 * @param cod_region
	 *            the cod_region to set
	 */
	public void setCod_region(String cod_region) {
		this.cod_region = cod_region;
	}

	/**
	 * @return the cod_city
	 */
	public Integer getCod_city() {
		return cod_city;
	}

	/**
	 * @param cod_city the cod_city to set
	 */
	public void setCod_city(Integer cod_city) {
		this.cod_city = cod_city;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the cod_office
	 */
	public Integer getCod_office() {
		return cod_office;
	}

	/**
	 * @param cod_office
	 *            the cod_office to set
	 */
	public void setCod_office(Integer cod_office) {
		this.cod_office = cod_office;
	}

	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}

	/**
	 * @return the no_assigned
	 */
	public Integer getNo_assigned() {
		return no_assigned;
	}

	/**
	 * @param no_assigned
	 *            the no_assigned to set
	 */
	public void setNo_assigned(Integer no_assigned) {
		this.no_assigned = no_assigned;
	}

	/**
	 * @return the assigned
	 */
	public Integer getAssigned() {
		return assigned;
	}

	/**
	 * @param assigned
	 *            the assigned to set
	 */
	public void setAssigned(Integer assigned) {
		this.assigned = assigned;
	}

	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
