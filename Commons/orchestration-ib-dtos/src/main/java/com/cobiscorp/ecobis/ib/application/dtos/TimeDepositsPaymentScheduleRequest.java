/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentSchedule;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TimeDepositsPaymentScheduleRequest";
	var tipoDato          = ["TimeDepositsPaymentSchedule","Product","Integer","Secuential","String"];
	var nombreAtributo    = ["timeDepositsPaymentSchedule","product","productId","secuential","userName"];
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
 * @author areinoso
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class TimeDepositsPaymentScheduleRequest extends BaseRequest{
	  
	private TimeDepositsPaymentSchedule timeDepositsPaymentSchedule;
	private Product product;
	private Integer productId;
	private Secuential secuential;
	private String userName;
	
	
	/**
	 * @return the timeDepositsPaymentSchedule
	 */
	public TimeDepositsPaymentSchedule getTimeDepositsPaymentSchedule() {
		return timeDepositsPaymentSchedule;
	}
	/**
	 * @param timeDepositsPaymentSchedule the timeDepositsPaymentSchedule to set
	 */
	public void setTimeDepositsPaymentSchedule(
			TimeDepositsPaymentSchedule timeDepositsPaymentSchedule) {
		this.timeDepositsPaymentSchedule = timeDepositsPaymentSchedule;
	}
	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the secuential
	 */
	public Secuential getSecuential() {
		return secuential;
	}
	/**
	 * @param secuential the secuential to set
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
		/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDepositsPaymentScheduleRequest [timeDepositsPaymentSchedule="
				+ timeDepositsPaymentSchedule
				+ ", product="
				+ product
				+ ", productId="
				+ productId
				+ ", secuential="
				+ secuential
				+ ", userName=" + userName + "]";
	}
	
	
	
}
