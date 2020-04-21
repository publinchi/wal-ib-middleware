/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavingsAditionalData;


/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ProgrammedSavingsMinimumAmountResponse";
	var tipoDato          = ["List < ProgrammedSavingsAditionalData >","String","Double"];
	var nombreAtributo    = ["ProgrammedSavingsAditionalDataCollection","expirationDate","minimumAmount"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 3;
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
 * @author jbaque
 * @since 7/11/2014
 * @version 1.0.0
 */
public class ProgrammedSavingsMinimumAmountResponse extends BaseResponse {
	private List <ProgrammedSavingsAditionalData> ProgrammedSavingsAditionalDataCollection;
	private String expirationDate;
	private Double minimumAmount;
	/**
	 * @return the minimumAmount
	 */
	public Double getMinimumAmount() {
		return minimumAmount;
	}
	/**
	 * @param minimumAmount the minimumAmount to set
	 */
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}
	/**
	 * @return the programmedSavingsAditionalDataCollection
	 */
	public List<ProgrammedSavingsAditionalData> getProgrammedSavingsAditionalDataCollection() {
		return ProgrammedSavingsAditionalDataCollection;
	}
	/**
	 * @param programmedSavingsAditionalDataCollection the programmedSavingsAditionalDataCollection to set
	 */
	public void setProgrammedSavingsAditionalDataCollection(
			List<ProgrammedSavingsAditionalData> programmedSavingsAditionalDataCollection) {
		ProgrammedSavingsAditionalDataCollection = programmedSavingsAditionalDataCollection;
	}
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @return
	 */
	

	

}
