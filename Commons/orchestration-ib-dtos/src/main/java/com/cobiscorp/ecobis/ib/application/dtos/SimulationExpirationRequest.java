/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.Date;

import com.cobiscorp.ecobis.ib.orchestration.dtos.CashiersCheck;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Type;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "SimulationExpirationRequest";
	var tipoDato          = ["CertificateDeposit", "Integer"];
	var nombreAtributo    = ["certificateDeposit", "dateFormatId"];
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
 * @author areinoso
 * @since Nov 18, 2014
 * @version 1.0.0
 */
public class SimulationExpirationRequest extends BaseRequest {
	

	
	private CertificateDeposit certificateDeposit;
	private Integer dateFormatId;
	/**
	 * @return the certificateDeposit
	 */
	public CertificateDeposit getCertificateDeposit() {
		return certificateDeposit;
	}
	/**
	 * @param certificateDeposit the certificateDeposit to set
	 */
	public void setCertificateDeposit(CertificateDeposit certificateDeposit) {
		this.certificateDeposit = certificateDeposit;
	}
	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormatId;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormatId = dateFormat;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimulationExpirationRequest [certificateDeposit="
				+ certificateDeposit + ", dateFormat=" + dateFormatId + "]";
	}
		
}
