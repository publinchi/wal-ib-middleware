/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
<!--   Autor: Morla David
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BatchChargeEtlRequest";
	var tipoDato          = ["String"];
	var nombreAtributo    = ["dateProcess"];
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
 * @author dmorla
 * @since 18/06/2015
 * @version 1.0.0
 */
public class BatchChargeEtlRequest {
	private String dateProcess;
	private Integer next;
	private Integer total;
	private String operation;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}

	/**
	 * @return the next
	 */
	public Integer getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(Integer next) {
		this.next = next;
	}

	/**
	 * @return the dateProcess
	 */
	public String getDateProcess() {
		return dateProcess;
	}

	/**
	 * @param dateProcess the dateProcess to set
	 */
	public void setDateProcess(String dateProcess) {
		this.dateProcess = dateProcess;
	}

}
