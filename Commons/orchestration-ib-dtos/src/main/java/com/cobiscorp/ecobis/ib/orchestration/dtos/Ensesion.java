package com.cobiscorp.ecobis.ib.orchestration.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)

public class Ensesion {
	@XmlElement(name = "fecha_operacion_banxico")
	private String fechaOperacionBanxico;
	
	@XmlElement(name = "inst_bancarias")
	private Instituciones instituciones;
    

    public String getFechaOperacionBanxico() {
        return fechaOperacionBanxico;
    }

    public void setFechaOperacionBanxico(String fechaOperacionBanxico) {
        this.fechaOperacionBanxico = fechaOperacionBanxico;
    }

   
    public Instituciones getTnstituciones() {
        return instituciones;
    }

    public void setInstituciones(Instituciones instituciones) {
        this.instituciones = instituciones;
    }
}