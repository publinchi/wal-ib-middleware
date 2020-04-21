/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;
/**
<!--	Autor: Baque H Jorge
  		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<<<<<<< .mine
<script type="text/javascript">
	var nombreClase       = "BatchHistoricRefreshBalanceResponse";
	var tipoDato          = ["no contiene atributos"];
	var nombreAtributo    = ["no contiene atributos"];
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
 		}</script></td>
 </tr>
 
 <tr>
   <td>Descripci&oacute;n:</td>
   <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
 </tr>
</tbody></table>
**/

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchHistoricRefreshBalance;


/**
 * @author gcondo
 * @since Feb 11, 2015
 * @version 1.0.0
 */
public class BatchHistoricRefreshBalanceResponse extends BaseResponse {
	
	private List<BatchHistoricRefreshBalance> BatchHistoricRefreshBalanceCollection;
	
	private int records;
	
	private Integer totalRecords;
	
	private Integer maxRecord;

	/**
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(Integer records) {
		this.records = records;
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
	 * @return the batchHistoricRefreshBalanceCollection
	 */
	public List<BatchHistoricRefreshBalance> getBatchHistoricRefreshBalanceCollection() {
		return BatchHistoricRefreshBalanceCollection;
	}

	/**
	 * @param batchHistoricRefreshBalanceCollection the batchHistoricRefreshBalanceCollection to set
	 */
	public void setBatchHistoricRefreshBalanceCollection(
			List<BatchHistoricRefreshBalance> batchHistoricRefreshBalanceCollection) {
		BatchHistoricRefreshBalanceCollection = batchHistoricRefreshBalanceCollection;
	}
	
	

}
