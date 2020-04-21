/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.Date;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsMovements;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TimeDepositsMovementsRequest";
	var tipoDato          = ["TimeDepositsMovements", "Integer", "Bank","Secuential","Integer"];
	var nombreAtributo    = ["timeDepositsMovements", "productId", "bank","secuential","dateFormat"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 5;
</script>
	
<table><tbody>
  <tr>
    <th colspan="2" bgcolor="#CCCCFF"><div>Nombre Clase:
	<script type="text/javascript">document.writeln(nombreClase);</script> 
    </th>
  </tr>
  <tr>
    <td colspan="2"><div>Atributos</div></td>
  </tr>
  <tr>
    <td width="auto" bgcolor="#CCCCFF"><div>Tipo de Dato</div></td>
    <td width="auto" bgcolor="#CCCCFF"><div>Nombre</div></td>
  </tr>
  <tr>
  <td style="font-family:'Courier New', Courier, monospace; color:#906;"><div align="left"><script type="text/javascript">
  		for(i=0;i<numeroAtributos;i++){ 
  		document.write(tipoDato[i]);
		document.write("<br />");
  		}</script></td>
  <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><div align="left"><script type="text/javascript">
  		for(i=0;i<numeroAtributos;i++){ 
  		document.write(nombreAtributo[i]);
		document.write("<br />");
  		}</script></td>
  </tr>
  
  <tr>
    <td>Descripci&oacute;n:</td>
    <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
  </tr>
</tbody></table>
 * @author jmoreta
 * @since Oct 9, 2014
 * @version 1.0.0
 */
public class TimeDepositsMovementsRequest extends BaseRequest {

	private TimeDepositsMovements timeDepositsMovements;
	private Integer productId;
    private Bank bank;
    private Secuential secuential;
    private Integer dateFormat;
  
    
	/**
	 * @return the TimeDepositsMovements
	 */
	public TimeDepositsMovements getTimeDepositsMovements() {
		return timeDepositsMovements;
	}

	/**
	 * @param TimeDepositsMovements the TimeDepositsMovements to set
	 */
	public void setTimeDepositsMovements(TimeDepositsMovements timeDepositsMovements) {
		this.timeDepositsMovements = timeDepositsMovements;
	}
	
	/**
	 * @return the Date
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param Date the Date to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the Bank
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * @param Bank the Bank to set
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

	/**
	 * @return the Secuential
	 */
	public Secuential getSecuential() {
		return secuential;
	}

	/**
	 * @param Secuential the Secuential to set
	 */
	public void setSecuential(Secuential secuential) {
		this.secuential = secuential;
	}

	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "TimeDepositsMovementsRequest [timeDepositsMovements="
				+ timeDepositsMovements + ", productId=" + productId
				+ ", bank=" + bank + ", secuential=" + secuential
				+ ", dateFormat=" + dateFormat + "]";
	}
	

}
