
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
 *         &lt;element name="GetIndividualReport2Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getIndividualReport2Result"
})
@XmlRootElement(name = "GetIndividualReport2Response")
public class GetIndividualReport2Response {

    @XmlElement(name = "GetIndividualReport2Result")
    protected String getIndividualReport2Result;

    /**
     * Gets the value of the getIndividualReport2Result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetIndividualReport2Result() {
        return getIndividualReport2Result;
    }

    /**
     * Sets the value of the getIndividualReport2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetIndividualReport2Result(String value) {
        this.getIndividualReport2Result = value;
    }

}
