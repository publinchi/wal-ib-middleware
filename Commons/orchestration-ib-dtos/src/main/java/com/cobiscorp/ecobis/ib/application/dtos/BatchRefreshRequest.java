/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;

/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BatchRefreshRequest";
	var tipoDato          = ["Batch","String","int","int","int"];
	var nombreAtributo    = ["batchInfo","fecha_proceso","filial","siguiente","numeroRegistros"];
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
 * @author jbaque
 * @since Feb 11, 2015
 * @version 1.0.0
 */
public class BatchRefreshRequest extends BaseRequest{
	private Batch batchInfo;
	private String processDate;
	private Integer subsidiary; 
	private Integer next;
	private Integer recordNumber;
	private Integer accountId;
	private Integer rowCount;
	/**
	 * @return the accountNumber
	 */
	public Integer getAccountId() {
		return accountId;
	}
	/**
	 * @return the rowCount
	 */
	public Integer getRowCount() {
		return rowCount;
	}
	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the batchInfo
	 */
	public Batch getBatchInfo() {
		return batchInfo;
	}
	/**
	 * @param batchInfo the batchInfo to set
	 */
	public void setBatchInfo(Batch batchInfo) {
		this.batchInfo = batchInfo;
	}
	/**
	 * @return the processDate
	 */
	public String getProcessDate() {
		return processDate;
	}
	/**
	 * @param processDate the processDate to set
	 */
	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}
	/**
	 * @return the subsidiary
	 */
	public Integer getSubsidiary() {
		return subsidiary;
	}
	/**
	 * @param subsidiary the subsidiary to set
	 */
	public void setSubsidiary(Integer subsidiary) {
		this.subsidiary = subsidiary;
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
	 * @return the recordNumber
	 */
	public Integer getRecordNumber() {
		return recordNumber;
	}
	/**
	 * @param recordNumber the recordNumber to set
	 */
	public void setRecordNumber(Integer recordNumber) {
		this.recordNumber = recordNumber;
	}
	/**
	 * @return the product
	 */
	public int getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(int product) {
		this.product = product;
	}
	private int product;
	

	 
}
