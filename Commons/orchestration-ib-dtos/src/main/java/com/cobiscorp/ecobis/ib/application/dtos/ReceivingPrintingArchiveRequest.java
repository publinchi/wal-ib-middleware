/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;



/**
 <!--	Autor: Isaac Torres
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BankGuaranteeRequest";
	var tipoDato          = ["String"];
	var nombreAtributo    = ["condition"];
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
 * @author itorres
 * @since Oct 31, 2015
 * @version 1.0.0
 */
public class ReceivingPrintingArchiveRequest extends BaseRequest{
	
	private Integer lote;
	private Integer nextCard;
	private String operation;
	private String observation;
	/**
	 * @return the lote
	 */
	public Integer getLote() {
		return lote;
	}
	/**
	 * @param lote the lote to set
	 */
	public void setLote(Integer lote) {
		this.lote = lote;
	}
	/**
	 * @return the nextCard
	 */
	public Integer getNextCard() {
		return nextCard;
	}
	/**
	 * @param nextCard the nextCard to set
	 */
	public void setNextCard(Integer nextCard) {
		this.nextCard = nextCard;
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
	/**
	 * @return the observation
	 */
	public String getObservation() {
		return observation;
	}
	/**
	 * @param observation the observation to set
	 */
	public void setObservation(String observation) {
		this.observation = observation;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReceivingPrintingArchiveRequest [lote=" + lote + ", nextCard="
				+ nextCard + ", operation=" + operation + ", observation="
				+ observation + "]";
	} 	
}
