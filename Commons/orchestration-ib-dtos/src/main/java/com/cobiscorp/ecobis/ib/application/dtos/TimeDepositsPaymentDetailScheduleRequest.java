/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentDetailSchedule;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TimeDepositsPaymentDetailScheduleRequest";
	var tipoDato          = ["TimeDepositsPaymentDetailSchedule","Integer","Product","Integer","Secuential","String","String"];
	var nombreAtributo    = ["timeDepositsPaymentDetailSchedule","dateFormat","product","productId","secuential","userName","next"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 7;
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
 * @since Oct 28, 2014
 * @version 1.0.0
 */
public class TimeDepositsPaymentDetailScheduleRequest extends BaseRequest {

	private TimeDepositsPaymentDetailSchedule timeDepositsPaymentDetailSchedule;
	private Integer dateFormat;
	private Product product;
	private Integer productId;
	private Secuential secuential;
	private String userName;
	private String next;
	/**
	 * @return the timeDepositsPaymentDetailSchedule
	 */
	public TimeDepositsPaymentDetailSchedule getTimeDepositsPaymentDetailSchedule() {
		return timeDepositsPaymentDetailSchedule;
	}
	/**
	 * @param timeDepositsPaymentDetailSchedule the timeDepositsPaymentDetailSchedule to set
	 */
	public void setTimeDepositsPaymentDetailSchedule(
			TimeDepositsPaymentDetailSchedule timeDepositsPaymentDetailSchedule) {
		this.timeDepositsPaymentDetailSchedule = timeDepositsPaymentDetailSchedule;
	}
	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
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
	/**
	 * @return the next
	 */
	public String getNext() {
		return next;
	}
	/**
	 * @param next the next to set
	 */
	public void setNext(String next) {
		this.next = next;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDepositsPaymentDetailScheduleRequest [timeDepositsPaymentDetailSchedule="
				+ timeDepositsPaymentDetailSchedule
				+ ", dateFormat="
				+ dateFormat
				+ ", product="
				+ product
				+ ", productId="
				+ productId
				+ ", secuential="
				+ secuential
				+ ", userName="
				+ userName + ", next=" + next + "]";
	}
	
		
}
