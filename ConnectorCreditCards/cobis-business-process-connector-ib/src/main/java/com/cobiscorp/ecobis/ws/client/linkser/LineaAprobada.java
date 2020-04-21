
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
 *         &lt;element name="pNro_Operacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pOficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pMoneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pMonto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pIdTitular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pCodigoCobis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pNombreTitular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pDireccion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pEvaluador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pActividad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pFechaAprobacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "pNroOperacion",
    "pOficina",
    "pMoneda",
    "pMonto",
    "pIdTitular",
    "pCodigoCobis",
    "pNombreTitular",
    "pDireccion",
    "pEvaluador",
    "pActividad",
    "pFechaAprobacion"
})
@XmlRootElement(name = "Linea_Aprobada")
public class LineaAprobada {

    @XmlElementRef(name = "pNro_Operacion", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pNroOperacion;
    @XmlElementRef(name = "pOficina", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pOficina;
    @XmlElementRef(name = "pMoneda", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pMoneda;
    @XmlElementRef(name = "pMonto", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pMonto;
    @XmlElementRef(name = "pIdTitular", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdTitular;
    @XmlElementRef(name = "pCodigoCobis", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pCodigoCobis;
    @XmlElementRef(name = "pNombreTitular", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pNombreTitular;
    @XmlElementRef(name = "pDireccion", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pDireccion;
    @XmlElementRef(name = "pEvaluador", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pEvaluador;
    @XmlElementRef(name = "pActividad", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pActividad;
    @XmlElementRef(name = "pFechaAprobacion", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pFechaAprobacion;

    /**
     * Gets the value of the pNroOperacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPNroOperacion() {
        return pNroOperacion;
    }

    /**
     * Sets the value of the pNroOperacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPNroOperacion(JAXBElement<String> value) {
        this.pNroOperacion = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pOficina property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPOficina() {
        return pOficina;
    }

    /**
     * Sets the value of the pOficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPOficina(JAXBElement<String> value) {
        this.pOficina = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPMoneda() {
        return pMoneda;
    }

    /**
     * Sets the value of the pMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPMoneda(JAXBElement<String> value) {
        this.pMoneda = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pMonto property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPMonto() {
        return pMonto;
    }

    /**
     * Sets the value of the pMonto property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPMonto(JAXBElement<String> value) {
        this.pMonto = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pIdTitular property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdTitular() {
        return pIdTitular;
    }

    /**
     * Sets the value of the pIdTitular property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdTitular(JAXBElement<String> value) {
        this.pIdTitular = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pCodigoCobis property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPCodigoCobis() {
        return pCodigoCobis;
    }

    /**
     * Sets the value of the pCodigoCobis property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPCodigoCobis(JAXBElement<String> value) {
        this.pCodigoCobis = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pNombreTitular property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPNombreTitular() {
        return pNombreTitular;
    }

    /**
     * Sets the value of the pNombreTitular property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPNombreTitular(JAXBElement<String> value) {
        this.pNombreTitular = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pDireccion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPDireccion() {
        return pDireccion;
    }

    /**
     * Sets the value of the pDireccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPDireccion(JAXBElement<String> value) {
        this.pDireccion = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pEvaluador property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPEvaluador() {
        return pEvaluador;
    }

    /**
     * Sets the value of the pEvaluador property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPEvaluador(JAXBElement<String> value) {
        this.pEvaluador = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pActividad property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPActividad() {
        return pActividad;
    }

    /**
     * Sets the value of the pActividad property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPActividad(JAXBElement<String> value) {
        this.pActividad = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pFechaAprobacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPFechaAprobacion() {
        return pFechaAprobacion;
    }

    /**
     * Sets the value of the pFechaAprobacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPFechaAprobacion(JAXBElement<String> value) {
        this.pFechaAprobacion = ((JAXBElement<String> ) value);
    }

}
