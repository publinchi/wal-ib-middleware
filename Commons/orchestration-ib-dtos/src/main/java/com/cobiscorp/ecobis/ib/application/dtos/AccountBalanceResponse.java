/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountBalance;

/**
<!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "AccountBalanceResponse";
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
 * 
 * 
 * @author mvelez
 * @since Sep 25, 2014
 * @version 1.0.0
 * @category application dto
 */
public class AccountBalanceResponse extends BaseResponse {
	private List<AccountBalance> AccountBalanceCollection;

	/**
	 * Getter:
	 * Metodo que devuelve la colecci&oacute;n de objetos de tipo AccountBalance
	 * @return Devuelve un accountBalanceCollection
	 */
	public List<AccountBalance> getAccountBalanceCollection() {
		return AccountBalanceCollection;
	}

	/**
	 * Setter:
	 * Metodo que setea una lista de AccountBalance
	 * @param El par&aacute;metro accountBalanceCollection es la lista de Objetos de tipo AccountBalance
	 */
	public void setAccountBalanceCollection(List<AccountBalance> accountBalanceCollection) {
		AccountBalanceCollection = accountBalanceCollection;
	}
}
