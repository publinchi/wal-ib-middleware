
package com.cobiscorp.ecobis.ws.client.linkser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pCodigoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pCodigoLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pFechaInicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pFechaFin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pNo_mov" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pCodigoCliente",
    "pCodigoLinea",
    "pFechaInicio",
    "pFechaFin",
    "pNoMov"
})
@XmlRootElement(name = "Consulta_mov")
public class ConsultaMov {

    @XmlElementRef(name = "pCodigoCliente", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pCodigoCliente;
    @XmlElementRef(name = "pCodigoLinea", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pCodigoLinea;
    @XmlElementRef(name = "pFechaInicio", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pFechaInicio;
    @XmlElementRef(name = "pFechaFin", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pFechaFin;
    @XmlElementRef(name = "pNo_mov", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pNoMov;

    /**
     * Gets the value of the pCodigoCliente property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPCodigoCliente() {
        return pCodigoCliente;
    }

    /**
     * Sets the value of the pCodigoCliente property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPCodigoCliente(JAXBElement<String> value) {
        this.pCodigoCliente = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pCodigoLinea property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPCodigoLinea() {
        return pCodigoLinea;
    }

    /**
     * Sets the value of the pCodigoLinea property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPCodigoLinea(JAXBElement<String> value) {
        this.pCodigoLinea = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pFechaInicio property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPFechaInicio() {
        return pFechaInicio;
    }

    /**
     * Sets the value of the pFechaInicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPFechaInicio(JAXBElement<String> value) {
        this.pFechaInicio = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pFechaFin property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPFechaFin() {
        return pFechaFin;
    }

    /**
     * Sets the value of the pFechaFin property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPFechaFin(JAXBElement<String> value) {
        this.pFechaFin = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pNoMov property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPNoMov() {
        return pNoMov;
    }

    /**
     * Sets the value of the pNoMov property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPNoMov(JAXBElement<String> value) {
        this.pNoMov = ((JAXBElement<String> ) value);
    }

}
