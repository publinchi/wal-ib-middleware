
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
 *         &lt;element name="TarCred_ClienteResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "tarCredClienteResult"
})
@XmlRootElement(name = "TarCred_ClienteResponse")
public class TarCredClienteResponse {

    @XmlElementRef(name = "TarCred_ClienteResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> tarCredClienteResult;

    /**
     * Gets the value of the tarCredClienteResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTarCredClienteResult() {
        return tarCredClienteResult;
    }

    /**
     * Sets the value of the tarCredClienteResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTarCredClienteResult(JAXBElement<String> value) {
        this.tarCredClienteResult = ((JAXBElement<String> ) value);
    }

}
