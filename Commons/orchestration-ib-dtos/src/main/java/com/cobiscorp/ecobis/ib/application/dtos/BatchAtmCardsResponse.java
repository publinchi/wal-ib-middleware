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
public class BatchAtmCardsResponse extends BaseResponse{
	
	private List<AtmCardsRequest> listAtmCards;

	private Integer totalRecords;
	private Integer maxRecord;
	private Integer next;
	private Integer rowcount;
	private Batch batch;

	public List<AtmCardsRequest> getListAtmCards() {
		return listAtmCards;
	}

	public void setListAtmCards(List<AtmCardsRequest> listAtmCards) {
		this.listAtmCards = listAtmCards;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Integer getMaxRecord() {
		return maxRecord;
	}

	public void setMaxRecord(Integer maxRecord) {
		this.maxRecord = maxRecord;
	}

	public Integer getNext() {
		return next;
	}

	public void setNext(Integer next) {
		this.next = next;
	}

	public Integer getRowcount() {
		return rowcount;
	}

	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}	
	
		
}
