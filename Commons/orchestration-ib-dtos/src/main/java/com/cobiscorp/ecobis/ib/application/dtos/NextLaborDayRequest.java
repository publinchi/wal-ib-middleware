/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.LaborDay;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;

/**
   <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "NextLaborDayRequest";
	var tipoDato          = ["LaborDay","Office","String"];
	var nombreAtributo    = ["laborDay","officeId","commercial"];
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
 * @author jmoreta
 * @since Nov 5, 2014
 * @version 1.0.0
 */
public class NextLaborDayRequest  extends BaseRequest {

	/**
	 * day and date
	 */
	
	private LaborDay laborDay;
	/**
	 * Id of the office Integer
	 */
	private Office officeId;	
	private String commercial;
	/**
	 * @return the day
	 */
	public LaborDay getLaborDay() {
		return laborDay;
	}
	/**
	 * @param day the day to set
	 */
	public void setLaborDay(LaborDay laborDay) {
		this.laborDay = laborDay;
	}
	/**
	 * @return the officeId
	 */
	public Office getOfficeId() {
		return officeId;
	}
	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(Office officeId) {
		this.officeId = officeId;
	}
	/**
	 * @return the commercial
	 */
	public String getCommercial() {
		return commercial;
	}
	/**
	 * @param commercial the commercial to set
	 */
	public void setCommercial(String commercial) {
		this.commercial = commercial;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NextLaborDayRequest [laborDay=" + laborDay + ", officeId=" + officeId
				+ ", commercial=" + commercial + "]";
	}
	
	
}
