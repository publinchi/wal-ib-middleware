package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ClientEnquiries;

/**
 <!--	Autor: Gisella Yagual Ortiz.
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "AccountStatementResponse";
	var tipoDato          = ["List < AccountStatement >", "Integer"];
	var nombreAtributo    = ["accountStatements", "numberOfResult"];
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
 *
 * @author gyagual
 * @since Aug 14, 2015
 * @version 1.0.0
 */
public class ClientEnquiriesResponse extends BaseResponse {
 
	/**
	 * Contains the information about the account
	 */
	private List<ClientEnquiries> clientEnquiriesCollection;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClientEnquiriesResponse [clientEnquiriesCollection=" + clientEnquiriesCollection
				+ ", numberOfResult=" + numberOfResult + "]";
	}
	/**
	 * @return the clientEnquiries
	 */
	public List<ClientEnquiries> getClientEnquiriesCollection() {
		return clientEnquiriesCollection;
	}
	/**
	 * @param clientEnquiries the clientEnquiries to set
	 */
	public void setClientEnquiriesCollection(List<ClientEnquiries> clientEnquiriesCollection) {
		this.clientEnquiriesCollection = clientEnquiriesCollection;
	}
	/**
	 * @return the numberOfResult
	 */
	public Integer getNumberOfResult() {
		return numberOfResult;
	}
	/**
	 * @param numberOfResult the numberOfResult to set
	 */
	public void setNumberOfResult(Integer numberOfResult) {
		this.numberOfResult = numberOfResult;
	}
	private Integer numberOfResult;

	 

}
