
package cobiscorp.ecobis.bankingservicesoperations.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PayOrderDetailRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PayOrderDetailRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataFrame" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayOrderDetailRequest", propOrder = {
    "dataFrame"
})
public class PayOrderDetailRequest
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected String dataFrame;

    /**
     * Obtiene el valor de la propiedad dataFrame.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataFrame() {
        return dataFrame;
    }

    /**
     * Define el valor de la propiedad dataFrame.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataFrame(String value) {
        this.dataFrame = value;
    }

}
