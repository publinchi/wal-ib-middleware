/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;

/**
<!--	Autor: Toledo Walther -->
<script type="text/javascript">
	var nombreClase       = "BatchScheduledPaymentResponse";
	var tipoDato          = ["List < AccountBalance > "];
	var nombreAtributo    = ["AccountBalanceCollection"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 1;
</script>

<table>
  <table><tbody>
  <tr>
     <th Alignment="center" bgcolor="#CCCCFF">Nombre Clase: 
	    <script type="text/javascript">document.writeln(nombreClase);</script> 
     </th>
  </tr>
  <tr>
      <td Alignment="center" bgcolor="#CCCCFF">Tipo Dato</td>
      <td Alignment="center" bgcolor="#CCCCFF">Nombre Atributo</td>
  </tr>
  <tr>
      <td style="font-family:'Courier New', Courier, monospace; color:#906;"><script type="text/javascript">
  		for(i=0;i<numeroAtributos;i++){ 
  		document.write(tipoDato[i]);
		document.write("<br/>");
  		}</script></td>
  <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
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
</table>
 * @author wtoledo
 * @since 24/2/2015
 * @version 1.0.0
 * @category application dto
 * 
 */
public class BatchScheduledPaymentResponse extends BaseResponse{
	
	private List<ScheduledPaymentRequest> listScheduledPayment;
	private Integer totalRecords;
	private Integer maxRecord;
	private Integer next;
	private Integer rowcount;
	private Batch batch;

	/**
	 * @return the listScheduledPayment
	 */
	public List<ScheduledPaymentRequest> getListScheduledPayment() {
		return listScheduledPayment;
	}

	/**
	 * @param listScheduledPayment the listScheduledPayment to set
	 */
	public void setListScheduledPayment(
			List<ScheduledPaymentRequest> listScheduledPayment) {
		this.listScheduledPayment = listScheduledPayment;
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
	 * @return the batch
	 */
	public Batch getBatch() {
		return batch;
	}

	/**
	 * @param batch the batch to set
	 */
	public void setBatch(Batch batch) {
		this.batch = batch;
	}	
	
	
		
}
