
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
 *         &lt;element name="GetIndividualReport3Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getIndividualReport3Result"
})
@XmlRootElement(name = "GetIndividualReport3Response")
public class GetIndividualReport3Response {

    @XmlElement(name = "GetIndividualReport3Result")
    protected String getIndividualReport3Result;

    /**
     * Gets the value of the getIndividualReport3Result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetIndividualReport3Result() {
        return getIndividualReport3Result;
    }

    /**
     * Sets the value of the getIndividualReport3Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetIndividualReport3Result(String value) {
        this.getIndividualReport3Result = value;
    }

}
