/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;


import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityIntegrated;
/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BatchGenerateCustomerDataResponse";
	var tipoDato          = ["List<EntityIntegrated>"];
	var nombreAtributo    = ["entityIntegrateList"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 1;
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
 * @author itorres
 * @since Feb 9, 2015
 * @version 1.0.0
 */
public class BatchGenerateCustomerDataResponse extends BaseResponse{
	private List<EntityIntegrated> entityIntegrateList;
	private Integer maxRecord;
	private Integer totalRecords;

	/**
	 * @return the entityIntegrateList
	 */
	public List<EntityIntegrated> getEntityIntegrateList() {
		return entityIntegrateList;
	}

	/**
	 * @param entityIntegrateList the entityIntegrateList to set
	 */
	public void setEntityIntegrateList(List<EntityIntegrated> entityIntegrateList) {
		this.entityIntegrateList = entityIntegrateList;
	}

	/**
	 * @return the maxRecord
	 */
	public Integer getMaxRecord() {
		return maxRecord;
	}

	/**
	 * @param maxRecord the maxRecord to set
	 */
	public void setMaxRecord(Integer maxRecord) {
		this.maxRecord = maxRecord;
	}

	/**
	 * @return the totalRecords
	 */
	public Integer getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords the totalRecords to set
	 */
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchGenerateCustomerDataResponse [entityIntegrateList="
				+ entityIntegrateList + ", maxRecord=" + maxRecord
				+ ", totalRecords=" + totalRecords + "]";
	}	

	
}
