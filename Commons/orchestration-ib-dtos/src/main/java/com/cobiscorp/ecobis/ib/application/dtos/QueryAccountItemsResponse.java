/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryAccountItem;


/**
 <!--	Autor: Morla David
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "QueryAccountItemsResponse";
	var tipoDato          = ["Integer", "String", "String", "String", "String", "String", "List < QueryAccountItem > "];
	var nombreAtributo    = ["codError", "messageError", "nitFac", "nameFac", "changeNitFac", "requirement",  "itemsCollection"];
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
 * @author dmorla
 * @since Jun 30, 2015
 * @version 1.0.0
 */
public class QueryAccountItemsResponse  extends BaseResponse{

	private Integer codError;
	private String messageError;
	private String nitFac;
	private String nameFac;
	private String changeNitFac;
	private String requirement;
	private List<QueryAccountItem> itemsCollection;
	
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
	 * @return the nitFac
	 */
	public String getNitFac() {
		return nitFac;
	}
	/**
	 * @param nitFac the nitFac to set
	 */
	public void setNitFac(String nitFac) {
		this.nitFac = nitFac;
	}
	/**
	 * @return the nameFac
	 */
	public String getNameFac() {
		return nameFac;
	}
	/**
	 * @param nameFac the nameFac to set
	 */
	public void setNameFac(String nameFac) {
		this.nameFac = nameFac;
	}
	/**
	 * @return the changeNitFac
	 */
	public String getChangeNitFac() {
		return changeNitFac;
	}
	/**
	 * @param changeNitFac the changeNitFac to set
	 */
	public void setChangeNitFac(String changeNitFac) {
		this.changeNitFac = changeNitFac;
	}
	/**
	 * @return the requirement
	 */
	public String getRequirement() {
		return requirement;
	}
	/**
	 * @param requirement the requirement to set
	 */
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
	/**
	 * @return the itemsCollection
	 */
	public List<QueryAccountItem> getItemsCollection() {
		return itemsCollection;
	}
	/**
	 * @param itemsCollection the itemsCollection to set
	 */
	public void setItemsCollection(List<QueryAccountItem> itemsCollection) {
		this.itemsCollection = itemsCollection;
	}
	
	

	
}
