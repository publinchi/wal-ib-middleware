package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ChannelContext;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BaseResponse";
	var tipoDato          = ["Boolean", "Message", "Message[]", "Integer"];
	var nombreAtributo    = ["success", "message", "messages", "returnCode"];
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
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class BaseResponse extends ChannelContext {
	/**
	 *  Result of service execution.
	 */
	private Boolean success;
	/**
	 * Contains detail of service execution.
	 */
	private Message message;
	
	private Message[] messages;
	
	private Integer returnCode;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}	
	/**
	 * @return the messages
	 */
	public Message[] getMessages() {
		return messages;
	}
	
	/**
	 * @return the returnCode
	 */
	public Integer getReturnCode() {
		return returnCode;
	}
	/**
	 * @param returnCode the returnCode to set
	 */
	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(Message[] messages) {
		this.messages = messages;
	}
	@Override
	public String toString() {
		return "BaseResponse [success=" + success + ", message=" + message
				+ "]";
	}
	
	

}
