
package com.cobiscorp.ecobis.ws.client.linkser;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SecurityServiceContextUsername" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ThreadCurrentPrincipalUsername" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserInfo", namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", propOrder = {
    "securityServiceContextUsername",
    "threadCurrentPrincipalUsername"
})
public class UserInfo {

    @XmlElementRef(name = "SecurityServiceContextUsername", namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", type = JAXBElement.class)
    protected JAXBElement<String> securityServiceContextUsername;
    @XmlElementRef(name = "ThreadCurrentPrincipalUsername", namespace = "http://schemas.datacontract.org/2004/07/TsmBoServicio", type = JAXBElement.class)
    protected JAXBElement<String> threadCurrentPrincipalUsername;

    /**
     * Gets the value of the securityServiceContextUsername property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSecurityServiceContextUsername() {
        return securityServiceContextUsername;
    }

    /**
     * Sets the value of the securityServiceContextUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSecurityServiceContextUsername(JAXBElement<String> value) {
        this.securityServiceContextUsername = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the threadCurrentPrincipalUsername property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getThreadCurrentPrincipalUsername() {
        return threadCurrentPrincipalUsername;
    }

    /**
     * Sets the value of the threadCurrentPrincipalUsername property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setThreadCurrentPrincipalUsername(JAXBElement<String> value) {
        this.threadCurrentPrincipalUsername = ((JAXBElement<String> ) value);
    }

}
