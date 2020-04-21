/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountSubitem;
import java.math.BigDecimal;

/**
<!--	Autor: Wendy Sanchez
  		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "QueryAccountItemsResponse";
	var tipoDato          = ["Integer", "String", "List < AccountSubitem > ", BigDecimal"];
	var nombreAtributo    = ["codError", "messageError", "subitemsCollection", "totalItem"];
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

/**
 * @author wsanchez
 * @since 28/07/2015
 * @version 1.0.0
 */
public class AccountSubitemsResponse extends BaseResponse{

	private Integer codError;
	private String messageError;
	private List<AccountSubitem> subitemsCollection;
	private BigDecimal totalItem;
	/**
	 * @return the codError
	 */
	public Integer getCodError() {
		return codError;
	}
	/**
	 * @param codError the codError to set
	 */
	public void setCodError(Integer codError) {
		this.codError = codError;
	}
	/**
	 * @return the messageError
	 */
	public String getMessageError() {
		return messageError;
	}
	/**
	 * @param messageError the messageError to set
	 */
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}
	/**
	 * @return the subitemsCollection
	 */
	public List<AccountSubitem> getSubitemsCollection() {
		return subitemsCollection;
	}
	/**
	 * @param subitemsCollection the itemsCollection to set
	 */
	public void setSubitemsCollection(List<AccountSubitem> subitemsCollection) {
		this.subitemsCollection = subitemsCollection;
	}
	/**
	 * @return the totalItem
	 */
	public BigDecimal getTotalItem() {
		return totalItem;
	}
	/**
	 * @param totalItem the totalItem to set
	 */
	public void setTotalItem(BigDecimal totalItem) {
		this.totalItem = totalItem;
	}
	
}
