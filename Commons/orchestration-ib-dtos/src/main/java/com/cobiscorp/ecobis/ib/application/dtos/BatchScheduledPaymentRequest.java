package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;

/**
<!--	Autor: Toledo Walther -->

<script type="text/javascript">
	var nombreClase       = "BatchScheduledPaymentRequest";
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
public class BatchScheduledPaymentRequest extends BaseRequest {
	
	private Batch batch;
	private Integer next;
	private Integer recordNumber;
	private ScheduledPaymentRequest scheduledPayment;
	private String operation; 
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
	 * @return the scheduledPayment
	 */
	public ScheduledPaymentRequest getScheduledPayment() {
		return scheduledPayment;
	}
	/**
	 * @param scheduledPayment the scheduledPayment to set
	 */
	public void setScheduledPayment(ScheduledPaymentRequest scheduledPayment) {
		this.scheduledPayment = scheduledPayment;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
}
