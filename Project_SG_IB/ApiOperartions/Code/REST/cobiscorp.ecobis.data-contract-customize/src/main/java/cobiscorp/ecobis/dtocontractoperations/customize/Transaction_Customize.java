/**
 * 
 * @author JC Olmos
 *
 */
package cobiscorp.ecobis.dtocontractoperations.customize;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import cobiscorp.ecobis.datacontractoperations.dto.Transaction;

/**
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction_Customize extends  Transaction{

}
