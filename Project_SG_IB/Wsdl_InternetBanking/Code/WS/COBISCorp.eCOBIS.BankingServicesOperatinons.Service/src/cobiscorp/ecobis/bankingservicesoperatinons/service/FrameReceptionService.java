
package cobiscorp.ecobis.bankingservicesoperatinons.service;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import cobiscorp.ecobis.bankingservicesoperation.dto.PayOrderDetailRequest;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inPayOrderDetailRequest" type="{http://dto.bankingservicesoperation.ecobis.cobiscorp}PayOrderDetailRequest"/>
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
    "inPayOrderDetailRequest"
})
@XmlRootElement(name = "FrameReceptionService")
public class FrameReceptionService
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected PayOrderDetailRequest inPayOrderDetailRequest;

    /**
     * Obtiene el valor de la propiedad inPayOrderDetailRequest.
     * 
     * @return
     *     possible object is
     *     {@link PayOrderDetailRequest }
     *     
     */
    public PayOrderDetailRequest getInPayOrderDetailRequest() {
        return inPayOrderDetailRequest;
    }

    /**
     * Define el valor de la propiedad inPayOrderDetailRequest.
     * 
     * @param value
     *     allowed object is
     *     {@link PayOrderDetailRequest }
     *     
     */
    public void setInPayOrderDetailRequest(PayOrderDetailRequest value) {
        this.inPayOrderDetailRequest = value;
    }

}
