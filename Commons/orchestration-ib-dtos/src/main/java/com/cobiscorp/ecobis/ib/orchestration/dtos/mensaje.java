package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cobiscorp.ecobis.ib.application.dtos.respuesta;

@XmlRootElement  
public class mensaje implements Serializable{
	
	 
	private  ordenpago ordenpago;
	private String categoria;
	private respuesta respuesta;
	
	
	public mensaje() {}

	public mensaje(ordenpago ordenpago, String categoria) {
		super();
		this.ordenpago = ordenpago;
		this.categoria = categoria;
	}
	@XmlElement 
	public synchronized ordenpago getOrdenpago() {
		return ordenpago;
	}

	public synchronized void setOrdenpago(ordenpago ordenpago) {
		this.ordenpago = ordenpago;
	}
	@XmlAttribute  
	public synchronized String getCategoria() {
		return categoria;
	}

	public synchronized void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	/**
	 * @return the respuesta
	 */
	@XmlElement 
	public synchronized respuesta getRespuesta() {
		return respuesta;
	}

	/**
	 * @param respuesta the respuesta to set
	 */
	public synchronized void setRespuesta(respuesta respuesta) {
		this.respuesta = respuesta;
	}
	
    
    

}
