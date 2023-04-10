
package cobiscorp.ecobis.bankingservicesoperations.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.cobiscorp.cobis.cts.sdf.dto2.CTSServiceResponseTO;


/**
 * <p>Clase Java para PayOrderDetailResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PayOrderDetailResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://dto2.sdf.cts.cobis.cobiscorp.com}CTSServiceResponseTO">
 *       &lt;sequence>
 *         &lt;element name="replay" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayOrderDetailResponse", propOrder = {
    "replay"
})
public class PayOrderDetailResponse
    extends CTSServiceResponseTO
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String replay;

    /**
     * Obtiene el valor de la propiedad replay.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplay() {
        return replay;
    }

    /**
     * Define el valor de la propiedad replay.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplay(String value) {
        this.replay = value;
    }

}
