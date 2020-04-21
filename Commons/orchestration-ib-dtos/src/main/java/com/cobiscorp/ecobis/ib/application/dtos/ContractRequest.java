/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;


/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ContractRequest";
	var tipoDato          = ["String","String"];
	var nombreAtributo    = ["contractServiceId","contractCategoryId"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 2;
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
 * @author jveloz
 * @since Jan 22, 2015
 * @version 1.0.0
 */
public class ContractRequest extends BaseRequest{

	private String contractServiceId;
	private String contractCategoryId;
	
	
	/**
	 * @return the contractServiceId
	 */
	public String getContractServiceId() {
		return contractServiceId;
	}
	/**
	 * @param contractServiceId the contractServiceId to set
	 */
	public void setContractServiceId(String contractServiceId) {
		this.contractServiceId = contractServiceId;
	}
	/**
	 * @return the contractCategory
	 */
	public String getContractCategoryId() {
		return contractCategoryId;
	}
	/**
	 * @param contractCategory the contractCategory to set
	 */
	public void setContractCategoryId(String contractCategory) {
		this.contractCategoryId = contractCategory;
	}
	
	
}
