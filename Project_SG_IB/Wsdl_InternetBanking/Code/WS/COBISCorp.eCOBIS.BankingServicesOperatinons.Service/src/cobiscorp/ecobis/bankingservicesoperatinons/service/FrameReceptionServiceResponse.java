
package cobiscorp.ecobis.bankingservicesoperatinons.service;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.cobiscorp.cobis.cts.sdf.dto2.CTSServiceResponseTO;


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
 *         &lt;element name="outFrameReceptionServiceResponse" type="{http://dto2.sdf.cts.cobis.cobiscorp.com}CTSServiceResponseTO"/>
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
    "outFrameReceptionServiceResponse"
})
@XmlRootElement(name = "FrameReceptionServiceResponse")
public class FrameReceptionServiceResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected CTSServiceResponseTO outFrameReceptionServiceResponse;

    /**
     * Obtiene el valor de la propiedad outFrameReceptionServiceResponse.
     * 
     * @return
     *     possible object is
     *     {@link CTSServiceResponseTO }
     *     
     */
    public CTSServiceResponseTO getOutFrameReceptionServiceResponse() {
        return outFrameReceptionServiceResponse;
    }

    /**
     * Define el valor de la propiedad outFrameReceptionServiceResponse.
     * 
     * @param value
     *     allowed object is
     *     {@link CTSServiceResponseTO }
     *     
     */
    public void setOutFrameReceptionServiceResponse(CTSServiceResponseTO value) {
        this.outFrameReceptionServiceResponse = value;
    }

}
