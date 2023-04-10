
package com.cobiscorp.cobis.cts.sdf.dto2;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CTSServiceResponseTO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CTSServiceResponseTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="messages" type="{http://dto2.commons.ecobis.cobiscorp}MessageTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CTSServiceResponseTO", propOrder = {
    "success",
    "messages"
})
public class CTSServiceResponseTO
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected Boolean success;
    @XmlElement(nillable = true)
    protected cobiscorp.ecobis.commons.dto2.MessageTO[] messages;

    /**
     * Obtiene el valor de la propiedad success.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * Define el valor de la propiedad success.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuccess(Boolean value) {
        this.success = value;
    }

    /**
     * 
     * 
     * @return
     *     array of
     *     {@link cobiscorp.ecobis.commons.dto2.MessageTO }
     *     
     */
    public cobiscorp.ecobis.commons.dto2.MessageTO[] getMessages() {
        if (this.messages == null) {
            return new cobiscorp.ecobis.commons.dto2.MessageTO[ 0 ] ;
        }
        cobiscorp.ecobis.commons.dto2.MessageTO[] retVal = new cobiscorp.ecobis.commons.dto2.MessageTO[this.messages.length] ;
        System.arraycopy(this.messages, 0, retVal, 0, this.messages.length);
        return (retVal);
    }

    /**
     * 
     * 
     * @return
     *     one of
     *     {@link cobiscorp.ecobis.commons.dto2.MessageTO }
     *     
     */
    public cobiscorp.ecobis.commons.dto2.MessageTO getMessages(int idx) {
        if (this.messages == null) {
            throw new IndexOutOfBoundsException();
        }
        return this.messages[idx];
    }

    public int getMessagesLength() {
        if (this.messages == null) {
            return  0;
        }
        return this.messages.length;
    }

    /**
     * 
     * 
     * @param values
     *     allowed objects are
     *     {@link cobiscorp.ecobis.commons.dto2.MessageTO }
     *     
     */
    public void setMessages(cobiscorp.ecobis.commons.dto2.MessageTO[] values) {
        int len = values.length;
        this.messages = ((cobiscorp.ecobis.commons.dto2.MessageTO[]) new cobiscorp.ecobis.commons.dto2.MessageTO[len] );
        for (int i = 0; (i<len); i ++) {
            this.messages[i] = values[i];
        }
    }

    /**
     * 
     * 
     * @param value
     *     allowed object is
     *     {@link cobiscorp.ecobis.commons.dto2.MessageTO }
     *     
     */
    public cobiscorp.ecobis.commons.dto2.MessageTO setMessages(int idx, cobiscorp.ecobis.commons.dto2.MessageTO value) {
        return this.messages[idx] = value;
    }

}
