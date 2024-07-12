package com.cobiscorp.ecobis.ib.orchestration.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Institucion {
	@XmlElement(name = "clave_cesif")
	private String claveCesif;
	@XmlElement(name = "ins_nombre")
    private String insNombre;
	@XmlElement(name = "estado_institucion")
	private String estadoInstitucion;
	@XmlElement(name = "estado_receptivo")
	private String estadoReceptivo;



    public String getClaveCesif() {
        return claveCesif;
    }

    public void setClaveCesif(String claveCesif) {
        this.claveCesif = claveCesif;
    }

    
    public String getInsNombre() {
        return insNombre;
    }

    public void setInsNombre(String insNombre) {
        this.insNombre = insNombre;
    }
    
    public String getEstadoInstitucion() {
        return estadoInstitucion;
    }

    public void setEstadoInstitucion(String estadoInstitucion) {
        this.estadoInstitucion = estadoInstitucion;
    }

    public String getEstadoReceptivo() {
        return estadoReceptivo;
    }

    public void setEstadoReceptivo(String estadoReceptivo) {
        this.estadoReceptivo = estadoReceptivo;
    }
}
