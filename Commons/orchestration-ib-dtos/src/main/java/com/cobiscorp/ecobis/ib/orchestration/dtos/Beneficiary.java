/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import com.cobiscorp.ecobis.ib.application.dtos.BaseRequest;

/**
 * @author jlvidal
 * @since 4/11/2014
 * @version 1.0.0
 */
public class Beneficiary extends BaseRequest {
	private String idBeneficiary;
	private String primerApellido;
	private String segundoApellido;
	private String nombre;
	private String tipoIdentificacion;
	private String identificacion;
	private String idRelacion;
	private Integer participacion;
	private String tipoDireccion;
	private String direccion;
	/**
	 * @return the idBeneficiary
	 */
	public String getIdBeneficiary() {
		return idBeneficiary;
	}
	/**
	 * @param idBeneficiary the idBeneficiary to set
	 */
	public void setIdBeneficiary(String idBeneficiary) {
		this.idBeneficiary = idBeneficiary;
	}
	/**
	 * @return the primerApellido
	 */
	public String getPrimerApellido() {
		return primerApellido;
	}
	/**
	 * @param primerApellido the primerApellido to set
	 */
	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}
	/**
	 * @return the segundoApellido
	 */
	public String getSegundoApellido() {
		return segundoApellido;
	}
	/**
	 * @param segundoApellido the segundoApellido to set
	 */
	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return the tipoIdentificacion
	 */
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	/**
	 * @param tipoIdentificacion the tipoIdentificacion to set
	 */
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}
	/**
	 * @return the identificacion
	 */
	public String getIdentificacion() {
		return identificacion;
	}
	/**
	 * @param identificacion the identificacion to set
	 */
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	/**
	 * @return the idRelacion
	 */
	public String getIdRelacion() {
		return idRelacion;
	}
	/**
	 * @param idRelacion the idRelacion to set
	 */
	public void setIdRelacion(String idRelacion) {
		this.idRelacion = idRelacion;
	}
	/**
	 * @return the participacion
	 */
	public Integer getParticipacion() {
		return participacion;
	}
	/**
	 * @param participacion the participacion to set
	 */
	public void setParticipacion(Integer participacion) {
		this.participacion = participacion;
	}
	/**
	 * @return the tipoDireccion
	 */
	public String getTipoDireccion() {
		return tipoDireccion;
	}
	/**
	 * @param tipoDireccion the tipoDireccion to set
	 */
	public void setTipoDireccion(String tipoDireccion) {
		this.tipoDireccion = tipoDireccion;
	}
	/**
	 * @return the direccion
	 */
	public String getDireccion() {
		return direccion;
	}
	/**
	 * @param direccion the direccion to set
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
}
