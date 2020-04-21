/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.Date;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ServerResponse";
	var tipoDato          = ["Boolean", "Date","Boolean"];
	var nombreAtributo    = ["onLine", "processDate","offlineWithBalances"];
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
 * @author eortega
 *
 */
public class ServerResponse extends BaseResponse {
	/**
	 * 
	 */
	private Boolean onLine;
	/**
	 * Date of Process
	 */
	private Date processDate;
	
	private Boolean offlineWithBalances;

	/**
	 * @return the offlineWithBalances
	 */
	public Boolean getOfflineWithBalances() {
		return offlineWithBalances;
	}

	/**
	 * @param offlineWithBalances the offlineWithBalances to set
	 */
	public void setOfflineWithBalances(Boolean offlineWithBalances) {
		this.offlineWithBalances = offlineWithBalances;
	}

	public Boolean getOnLine() {
		return onLine;
	}

	public void setOnLine(Boolean onLine) {
		this.onLine = onLine;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerResponse [onLine=" + onLine + ", processDate="
				+ processDate + ", offlineWithBalances=" + offlineWithBalances
				+ "]";
	}

}
