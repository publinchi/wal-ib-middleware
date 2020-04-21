/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;
/**
<!--	Autor: Walther Toledo Q.  		-->
<script type="text/javascript">
	var nombreClase       = "BatchRefreshRelationshipWithDollarResponse";
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
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchRefreshForexPosition;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchRefreshForexRates;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchRefreshRelationshipWithDollar;


/**
 * @author wtoledo
 * @since Mar 24, 2015
 * @version 1.0.0
 */
public class BatchRefreshForeignExchangeResponse extends BaseResponse {
	private List<BatchRefreshForexPosition> batchRefreshForexPositionCollection;
	private int records;

	private List<BatchRefreshRelationshipWithDollar> batchRefreshRelationshipWithDollarCollection;
	private List<BatchRefreshForexRates> batchRefreshForexRatesCollection;
	
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
	 * @return the batchRefreshForexPositionCollection
	 */
	public List<BatchRefreshForexPosition> getBatchRefreshForexPositionCollection() {
		return batchRefreshForexPositionCollection;
	}

	/**
	 * @param batchRefreshForexPositionCollection the batchRefreshForexPositionCollection to set
	 */
	public void setBatchRefreshForexPositionCollection(
			List<BatchRefreshForexPosition> batchRefreshForexPositionCollection) {
		this.batchRefreshForexPositionCollection = batchRefreshForexPositionCollection;
	}

	/**
	 * @return the batchRefreshRelationshipWithDollarCollection
	 */
	public List<BatchRefreshRelationshipWithDollar> getBatchRefreshRelationshipWithDollarCollection() {
		return batchRefreshRelationshipWithDollarCollection;
	}

	/**
	 * @param batchRefreshRelationshipWithDollarCollection the batchRefreshRelationshipWithDollarCollection to set
	 */
	public void setBatchRefreshRelationshipWithDollarCollection(
			List<BatchRefreshRelationshipWithDollar> batchRefreshRelationshipWithDollarCollection) {
		this.batchRefreshRelationshipWithDollarCollection = batchRefreshRelationshipWithDollarCollection;
	}

	/**
	 * @return the batchRefreshForexRatesCollection
	 */
	public List<BatchRefreshForexRates> getBatchRefreshForexRatesCollection() {
		return batchRefreshForexRatesCollection;
	}

	/**
	 * @param batchRefreshForexRatesCollection the batchRefreshForexRatesCollection to set
	 */
	public void setBatchRefreshForexRatesCollection(
			List<BatchRefreshForexRates> batchRefreshForexRatesCollection) {
		this.batchRefreshForexRatesCollection = batchRefreshForexRatesCollection;
	}

	

}
