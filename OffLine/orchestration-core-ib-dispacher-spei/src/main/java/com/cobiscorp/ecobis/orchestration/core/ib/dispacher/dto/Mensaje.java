package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement(name = "mensaje")
public class Mensaje implements Serializable{
	

	private String categoria;
	private Respuesta respuesta;
	
	public Mensaje() {}

	public Mensaje(Respuesta respuesta, String categoria) {
		super();
		this.respuesta = respuesta;
		this.categoria = categoria;
	}
	@XmlElement 
	public synchronized Respuesta getRespuesta() {
		return respuesta;
	}

	public synchronized void setRespuesta(Respuesta respuesta) {
		this.respuesta = respuesta;
	}
	
	@XmlAttribute  
	public synchronized String getCategoria() {
		return categoria;
	}

	public synchronized void setCategoria(String categoria) {
		this.categoria = categoria;
	}

}
