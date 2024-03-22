package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
@XmlRootElement(name = "respuesta")
@XmlAccessorType(XmlAccessType.FIELD)
 public  class Respuesta  implements Serializable {

	@XmlElement
	private String FechaOper;
	@XmlElement
	private String Id;
	@XmlElement
	private int ErrCodigo;
	@XmlElement
	private String ErrDescripcion;	

	public Respuesta(){}

	/**
	 * @param fechaOper
	 * @param id
	 * @param errCodigo
	 * @param errDescripcion
	 */
	public Respuesta(String fechaOper, String Id, int errCodigo, String errDescripcion) {
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
