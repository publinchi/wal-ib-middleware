/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;


import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;

/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BatchGenerateCustomerDataRequest";
	var tipoDato          = ["String","String","Integer","Batch","Entity"];
	var nombreAtributo    = ["dateProcess","dateAdmission","rowcount","batchCollection","entityCollection"];
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
 * @author itorres
 * @since Feb 9, 2015
 * @version 1.0.0
 */

public class BatchGenerateCustomerDataRequest extends BaseRequest{
	private String dateProcess;
	private String dateAdmission;
	private Integer rowcount;
	private Batch batchCollection;
	private Entity entityCollection;
	private Integer next;
	private Integer recordNumber;
	
	
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
	/**
	 * @return the dateAdmission
	 */
	public String getDateAdmission() {
		return dateAdmission;
	}
	/**
	 * @param dateAdmission the dateAdmission to set
	 */
	public void setDateAdmission(String dateAdmission) {
		this.dateAdmission = dateAdmission;
	}
	/**
	 * @return the rowcount
	 */
	public Integer getRowcount() {
		return rowcount;
	}
	/**
	 * @param rowcount the rowcount to set
	 */
	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}
	/**
	 * @return the batchCollection
	 */
	public Batch getBatchCollection() {
		return batchCollection;
	}
	/**
	 * @param batchCollection the batchCollection to set
	 */
	public void setBatchCollection(Batch batchCollection) {
		this.batchCollection = batchCollection;
	}
	/**
	 * @return the entityCollection
	 */
	public Entity getEntityCollection() {
		return entityCollection;
	}
	/**
	 * @param entityCollection the entityCollection to set
	 */
	public void setEntityCollection(Entity entityCollection) {
		this.entityCollection = entityCollection;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchGenerateCustomerDataRequest [dateProcess=" + dateProcess
				+ ", dateAdmission=" + dateAdmission + ", rowcount=" + rowcount
				+ ", batchCollection=" + batchCollection
				+ ", entityCollection=" + entityCollection + ", next=" + next
				+ ", recordNumber=" + recordNumber + "]";
	}

	
	
}
