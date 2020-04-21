
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
 *         &lt;element name="Consulta_Tarjetas_CreResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "consultaTarjetasCreResult"
})
@XmlRootElement(name = "Consulta_Tarjetas_CreResponse")
public class ConsultaTarjetasCreResponse {

    @XmlElementRef(name = "Consulta_Tarjetas_CreResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> consultaTarjetasCreResult;

    /**
     * Gets the value of the consultaTarjetasCreResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getConsultaTarjetasCreResult() {
        return consultaTarjetasCreResult;
    }

    /**
     * Sets the value of the consultaTarjetasCreResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setConsultaTarjetasCreResult(JAXBElement<String> value) {
        this.consultaTarjetasCreResult = ((JAXBElement<String> ) value);
    }

}
