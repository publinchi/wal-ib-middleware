package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
   <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "NotificationRequest";
	var tipoDato          = ["Product","Client","NotificationDetail","Notification"];
	var nombreAtributo    = ["originProduct","client","notificationDetail","notification"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 4;
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
 * 
 * @author eortega
 * @since Aug 15, 2014
 * @version 1.0.0
 */
public class NotificationRequest extends BaseRequest {
	/**
	 * Object which contains information about account
	 */
	private Product originProduct;

	/**
	 * Object which contains information about web client of Virtual Banking
	 */
	private Client client;
	/**
	 * Object which contains information about detail of notification
	 */
	private NotificationDetail notificationDetail;
	/**
	 * Object which contains information about of notification
	 */
	private Notification notification;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Product getOriginProduct() {
		return originProduct;
	}

	public void setOriginProduct(Product originProduct) {
		this.originProduct = originProduct;
	}

	public NotificationDetail getNotificationDetail() {
		return notificationDetail;
	}

	public void setNotificationDetail(NotificationDetail notificationDetail) {
		this.notificationDetail = notificationDetail;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	@Override
	public String toString() {
		return "NotificationRequest [originProduct=" + originProduct + ", client=" + client + ", notificationDetail=" + notificationDetail + ", notification=" + notification + "]";
	}

}
