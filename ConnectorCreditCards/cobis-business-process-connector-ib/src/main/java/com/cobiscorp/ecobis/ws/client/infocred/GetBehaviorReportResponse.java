
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
 *         &lt;element name="GetBehaviorReportResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getBehaviorReportResult"
})
@XmlRootElement(name = "GetBehaviorReportResponse")
public class GetBehaviorReportResponse {

    @XmlElement(name = "GetBehaviorReportResult")
    protected String getBehaviorReportResult;

    /**
     * Gets the value of the getBehaviorReportResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetBehaviorReportResult() {
        return getBehaviorReportResult;
    }

    /**
     * Sets the value of the getBehaviorReportResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetBehaviorReportResult(String value) {
        this.getBehaviorReportResult = value;
    }

}
