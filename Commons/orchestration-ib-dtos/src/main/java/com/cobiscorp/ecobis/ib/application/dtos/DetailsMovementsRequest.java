package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
 
/**
 
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "DetailsMovementsRequest";
	var tipoDato          = ["EnquiryRequest","AccountStatement"];
	var nombreAtributo    = ["wEnquiryRequest", "wAccountStatement"];
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
 * @author eortega
 * @since september 23, 2014
 * @version 1.0.0
 */
public class DetailsMovementsRequest extends BaseRequest {
	
	private EnquiryRequest wEnquiryRequest;
	private AccountStatement wAccountStatement;
	/**
	 * @return the wEnquiryRequest
	 */
	public EnquiryRequest getwEnquiryRequest() {
		return wEnquiryRequest;
	}
	/**
	 * @param wEnquiryRequest the wEnquiryRequest to set
	 */
	public void setwEnquiryRequest(EnquiryRequest wEnquiryRequest) {
		this.wEnquiryRequest = wEnquiryRequest;
	}
	/**
	 * @return the wAccountStatement
	 */
	public AccountStatement getwAccountStatement() {
		return wAccountStatement;
	}
	/**
	 * @param wAccountStatement the wAccountStatement to set
	 */
	public void setwAccountStatement(AccountStatement wAccountStatement) {
		this.wAccountStatement = wAccountStatement;
	}
	
}
