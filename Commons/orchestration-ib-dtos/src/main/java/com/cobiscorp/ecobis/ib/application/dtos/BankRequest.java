package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BankRequest";
	var tipoDato          = ["Bank", "Integer", "String"];
	var nombreAtributo    = ["banco", "modo", "descripcionBanco"];
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
 * @since september 16, 2014
 * @version 1.0.0
 */
public class BankRequest extends BaseRequest {
	/**
	 * Contains the information about the banks
	 */
	private Bank banco;

	/**
	 * Contains the mode of execution
	 */
	private Integer modo;

	/**
	 * Contains the description the last result
	 */
	private String descripcionBanco;

	@Override
	public String toString() {
		return "BankRequest [banco=" + banco + ", modo=" + modo + ", descripcionBanco=" + descripcionBanco + "]";
	}

	public Bank getBanco() {
		return banco;
	}

	public void setBanco(Bank banco) {
		this.banco = banco;
	}

	public Integer getModo() {
		return modo;
	}

	public void setModo(Integer modo) {
		this.modo = modo;
	}

	public String getDescripcionBanco() {
		return descripcionBanco;
	}

	public void setDescripcionBanco(String descripcionBanco) {
		this.descripcionBanco = descripcionBanco;
	}

}
