package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

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
 * @author eortega
 * @since september 23, 2014
 * @version 1.0.0
 */
public class AccountStatementResponse extends BaseResponse {
 
	/**
	 * Contains the information about the account
	 */
	private List<AccountStatement> accountStatements;
	/**
	 * Contains the number Of Result
	 */
	private Integer numberOfResult;

	public List<AccountStatement> getAccountStatements() {
		return accountStatements;
	}

	public void setAccountStatements(List<AccountStatement> accountStatements) {
		this.accountStatements = accountStatements;
	}

	public Integer getNumberOfResult() {
		return numberOfResult;
	}

	public void setNumberOfResult(Integer numberOfResult) {
		this.numberOfResult = numberOfResult;
	}

	@Override
	public String toString() {
		return "AccountStatementResponse [accountStatements=" + accountStatements + ", numberOfResult=" + numberOfResult + "]";
	}

}
