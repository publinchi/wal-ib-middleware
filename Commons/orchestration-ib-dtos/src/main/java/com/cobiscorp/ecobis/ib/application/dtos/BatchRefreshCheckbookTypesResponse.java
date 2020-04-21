/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;
/**
<!--	Autor: Walther Toledo Q.  		-->
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

import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchRefreshCheckbookTypes;


/**
 * @author wtoledo
 * @since Mar 13, 2015
 * @version 1.0.0
 */
public class BatchRefreshCheckbookTypesResponse extends BaseResponse {
	
	private List<BatchRefreshCheckbookTypes> batchRefreshCheckbookTypesCollection;
	
	private int records;

	/**
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(int records) {
		this.records = records;
	}

	/**
	 * @return the batchRefreshCheckbookTypesCollection
	 */
	public List<BatchRefreshCheckbookTypes> getBatchRefreshCheckbookTypesCollection() {
		return batchRefreshCheckbookTypesCollection;
	}

	/**
	 * @param batchRefreshCheckbookTypesCollection the batchRefreshCheckbookTypesCollection to set
	 */
	public void setBatchRefreshCheckbookTypesCollection(
			List<BatchRefreshCheckbookTypes> batchRefreshCheckbookTypesCollection) {
		this.batchRefreshCheckbookTypesCollection = batchRefreshCheckbookTypesCollection;
	}

}
