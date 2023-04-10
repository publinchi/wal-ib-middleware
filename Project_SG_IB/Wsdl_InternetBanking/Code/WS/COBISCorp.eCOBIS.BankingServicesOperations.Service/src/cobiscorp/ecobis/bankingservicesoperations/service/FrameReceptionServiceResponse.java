
package cobiscorp.ecobis.bankingservicesoperations.service;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import cobiscorp.ecobis.bankingservicesoperations.dto.PayOrderDetailResponse;


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
 *         &lt;element name="outPayOrderDetailResponse" type="{http://dto.bankingservicesoperations.ecobis.cobiscorp}PayOrderDetailResponse"/>
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
    "outPayOrderDetailResponse"
})
@XmlRootElement(name = "FrameReceptionServiceResponse")
public class FrameReceptionServiceResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected PayOrderDetailResponse outPayOrderDetailResponse;

    /**
     * Obtiene el valor de la propiedad outPayOrderDetailResponse.
     * 
     * @return
     *     possible object is
     *     {@link PayOrderDetailResponse }
     *     
     */
    public PayOrderDetailResponse getOutPayOrderDetailResponse() {
        return outPayOrderDetailResponse;
    }

    /**
     * Define el valor de la propiedad outPayOrderDetailResponse.
     * 
     * @param value
     *     allowed object is
     *     {@link PayOrderDetailResponse }
     *     
     */
    public void setOutPayOrderDetailResponse(PayOrderDetailResponse value) {
        this.outPayOrderDetailResponse = value;
    }

}
