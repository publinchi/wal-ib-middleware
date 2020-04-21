/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ReceivingPrintingArchive;
/**
 <!--	Autor: Isaac Torres
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "StockResponse";
	var tipoDato          = ["List < Stock > ", "int"];
	var nombreAtributo    = ["stockCollection", "rows"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 2;
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
 * @author itorres
 * @since Oct 31, 2015
 * @version 1.0.0
 */
public class ReceivingPrintingArchiveResponse  extends BaseResponse{

	private List<ReceivingPrintingArchive> receivingPrintingArchiveCollection;
	private Integer numReg;

	/**
	 * @return the receivingPrintingArchiveCollection
	 */
	public List<ReceivingPrintingArchive> getReceivingPrintingArchiveCollection() {
		return receivingPrintingArchiveCollection;
	}

	/**
	 * @param receivingPrintingArchiveCollection the receivingPrintingArchiveCollection to set
	 */
	public void setReceivingPrintingArchiveCollection(
			List<ReceivingPrintingArchive> receivingPrintingArchiveCollection) {
		this.receivingPrintingArchiveCollection = receivingPrintingArchiveCollection;
	}

	/**
	 * @return the numReg
	 */
	public Integer getNumReg() {
		return numReg;
	}

	/**
	 * @param numReg the numReg to set
	 */
	public void setNumReg(Integer numReg) {
		this.numReg = numReg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReceivingPrintingArchiveResponse [receivingPrintingArchiveCollection="
				+ receivingPrintingArchiveCollection + "]";
	}

}
