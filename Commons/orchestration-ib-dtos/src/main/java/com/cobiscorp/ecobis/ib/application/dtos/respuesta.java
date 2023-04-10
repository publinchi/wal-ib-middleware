package com.cobiscorp.ecobis.ib.application.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
 public  class respuesta  implements Serializable {

	@XmlElement
	private String FechaOper;
	@XmlElement
	private String Id;
	@XmlElement
	private int ErrCodigo;
	@XmlElement
	private String ErrDescripcion;	

	public respuesta(){}

	/**
	 * @param fechaOper
	 * @param id
	 * @param errCodigo
	 * @param errDescripcion
	 */
	public respuesta(String fechaOper, String Id, int errCodigo, String errDescripcion) {
		super();
		FechaOper = fechaOper;
		this.Id = Id;
		ErrCodigo = errCodigo;
		ErrDescripcion = errDescripcion;
	}
	/**
	 * @return the fechaOper
	 */
	public synchronized String getFechaOper() {
		return FechaOper;
	}
	/**
	 * @param fechaOper the fechaOper to set
	 */
	public synchronized void setFechaOper(String fechaOper) {
		FechaOper = fechaOper;
	}
	/**
	 * @return the id
	 */
	public synchronized String getId() {
		return Id;
	}
	/**
	 * @param Id the id to set
	 */
	public synchronized void setId(String Id) {
		this.Id = Id;
	}
	/**
	 * @return the errCodigo
	 */
	public synchronized int getErrCodigo() {
		return ErrCodigo;
	}
	/**
	 * @param errCodigo the errCodigo to set
	 */
	public synchronized void setErrCodigo(int errCodigo) {
		ErrCodigo = errCodigo;
	}
	/**
	 * @return the errDescripcion
	 */
	public synchronized String getErrDescripcion() {
		return ErrDescripcion;
	}
	/**
	 * @param errDescripcion the errDescripcion to set
	 */
	public synchronized void setErrDescripcion(String errDescripcion) {
		ErrDescripcion = errDescripcion;
	}
}