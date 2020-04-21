
package com.cobiscorp.ecobis.ws.client.infocred;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetTitularListByNameResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getTitularListByNameResult"
})
@XmlRootElement(name = "GetTitularListByNameResponse")
public class GetTitularListByNameResponse {

    @XmlElement(name = "GetTitularListByNameResult")
    protected String getTitularListByNameResult;

    /**
     * Gets the value of the getTitularListByNameResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetTitularListByNameResult() {
        return getTitularListByNameResult;
    }

    /**
     * Sets the value of the getTitularListByNameResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetTitularListByNameResult(String value) {
        this.getTitularListByNameResult = value;
    }

}
