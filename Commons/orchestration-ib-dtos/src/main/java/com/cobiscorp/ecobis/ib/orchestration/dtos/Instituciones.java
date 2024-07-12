package com.cobiscorp.ecobis.ib.orchestration.dtos;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Instituciones
{
	@XmlElement(name = "institucion")
	private List<Institucion> instBancarias= new ArrayList<Institucion>();
	
    public List<Institucion> getListInstBancarias() {
        return instBancarias;
    }

    public void setListInstBancarias(List<Institucion> instBancarias) {
        this.instBancarias = instBancarias;
    }
}
