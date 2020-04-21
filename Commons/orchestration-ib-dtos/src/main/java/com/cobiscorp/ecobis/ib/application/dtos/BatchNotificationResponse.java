
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;
/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BatchNotificationResponse";
	var tipoDato          = ["BigDecimal","Integer","String"];
	var nombreAtributo    = ["returnBalance","returnDays","notification"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 3;
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
	 		}</script>
	</td>
 </tr>
 
 <tr>
   <td>Descripci&oacute;n:</td>
   <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
 </tr>
</tbody></table>
**/

/**
 * @author gyagual
 * @since Jan 27, 2015
 * @version 1.0.0
 */
public class BatchNotificationResponse extends BaseResponse {
	private BigDecimal returnBalance;
	private Integer returnDays;
	private String notification;
	private Integer nextRecord;
	/**
	 * @return the nextRecord
	 */
	public Integer getNextRecord() {
		return nextRecord;
	}
	/**
	 * @param nextRecord the nextRecord to set
	 */
	public void setNextRecord(Integer nextRecord) {
		this.nextRecord = nextRecord;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchNotificationResponse [returnBalance=" + returnBalance
				+ ", returnDays=" + returnDays + ", notification="
				+ notification + "]";
	}
	/**
	 * @return the returnDays
	 */
	public Integer getReturnDays() {
		return returnDays;
	}
	/**
	 * @param returnDays the returnDays to set
	 */
	public void setReturnDays(Integer returnDays) {
		this.returnDays = returnDays;
	}
	/**
	 * @return the returnBalance
	 */
	public BigDecimal getReturnBalance() {
		return returnBalance;
	}
	/**
	 * @param returnBalance the returnBalance to set
	 */
	public void setReturnBalance(BigDecimal returnBalance) {
		this.returnBalance = returnBalance;
	}
	/**
	 * @return the notification
	 */
	public String getNotification() {
		return notification;
	}
	/**
	 * @param notificacion the notification to set
	 */
	public void setNotification(String notificacion) {
		notification = notificacion;
	}
}
