
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
 *         &lt;element name="pIdLineaCreditoCobis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pIdClienteCobis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pIdDocumentoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pMonto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pIdMoneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pIdFecha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "pIdLineaCreditoCobis",
    "pIdClienteCobis",
    "pIdDocumentoCliente",
    "pMonto",
    "pIdMoneda",
    "pIdFecha",
    "pModPago"
})
@XmlRootElement(name = "WSGETONLINEPAYMENT")
public class WSGETONLINEPAYMENT {

    @XmlElementRef(name = "pIdLineaCreditoCobis", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdLineaCreditoCobis;
    @XmlElementRef(name = "pIdClienteCobis", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdClienteCobis;
    @XmlElementRef(name = "pIdDocumentoCliente", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdDocumentoCliente;
    @XmlElementRef(name = "pMonto", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pMonto;
    @XmlElementRef(name = "pIdMoneda", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdMoneda;
    @XmlElementRef(name = "pIdFecha", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pIdFecha;
    @XmlElementRef(name = "pModPago", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> pModPago;

    /**
     * Gets the value of the pIdLineaCreditoCobis property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdLineaCreditoCobis() {
        return pIdLineaCreditoCobis;
    }

    /**
     * Sets the value of the pIdLineaCreditoCobis property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdLineaCreditoCobis(JAXBElement<String> value) {
        this.pIdLineaCreditoCobis = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pIdClienteCobis property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdClienteCobis() {
        return pIdClienteCobis;
    }

    /**
     * Sets the value of the pIdClienteCobis property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdClienteCobis(JAXBElement<String> value) {
        this.pIdClienteCobis = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pIdDocumentoCliente property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdDocumentoCliente() {
        return pIdDocumentoCliente;
    }

    /**
     * Sets the value of the pIdDocumentoCliente property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdDocumentoCliente(JAXBElement<String> value) {
        this.pIdDocumentoCliente = ((JAXBElement<String> ) value);
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
     * Gets the value of the pIdMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdMoneda() {
        return pIdMoneda;
    }

    /**
     * Sets the value of the pIdMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdMoneda(JAXBElement<String> value) {
        this.pIdMoneda = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pIdFecha property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPIdFecha() {
        return pIdFecha;
    }

    /**
     * Sets the value of the pIdFecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPIdFecha(JAXBElement<String> value) {
        this.pIdFecha = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the pModPago property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
	public JAXBElement<String> getPModPago() {
		return pModPago;
	}

	/**
     * Sets the value of the pIdFecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
	public void setPModPago(JAXBElement<String> value) {
		this.pModPago = ((JAXBElement<String> ) value);
	}
    
    

}
