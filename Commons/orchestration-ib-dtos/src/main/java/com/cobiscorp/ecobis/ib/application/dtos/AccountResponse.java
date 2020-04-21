/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountHeader;
/**
 <!--	Autor: Isaac Torres
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ModuleResponse";
	var tipoDato          = ["Integer","String","Integer","Integer","List < Account > "];
	var nombreAtributo    = ["codError", "messageError","numOperation", "operationDate", "accountCollection"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 5;
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
 * @since Jul 14, 2015
 * @version 1.0.0
 */
public class AccountResponse  extends BaseResponse{

	private Integer codError;
	private String messageError;
	private Integer numOperation;
	private Integer operationDate;
	private List<AccountDetail> accountDetailCollection;
		
	/**
	 * @return the codError
	 */
	public Integer getCodError() {
		return codError;
	}
	/**
	 * @param codError the codError to set
	 */
	public void setCodError(Integer codError) {
		this.codError = codError;
	}
	/**
	 * @return the messageError
	 */
	public String getMessageError() {
		return messageError;
	}
	/**
	 * @param messageError the messageError to set
	 */
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}	
	/**
	 * @return the numOperation
	 */
	public Integer getNumOperation() {
		return numOperation;
	}
	/**
	 * @param numOperation the numOperation to set
	 */
	public void setNumOperation(Integer numOperation) {
		this.numOperation = numOperation;
	}
	/**
	 * @return the operationDate
	 */
	public Integer getOperationDate() {
		return operationDate;
	}
	/**
	 * @param operationDate the operationDate to set
	 */
	public void setOperationDate(Integer operationDate) {
		this.operationDate = operationDate;
	}	
	/**
	 * @return the moduleCollection
	 */
	public List<AccountDetail> getAccountDetailCollection() {
		return accountDetailCollection;
	}
	/**
	 * @param moduleCollection the moduleCollection to set
	 */
	public void setAccountDetailCollection(List<AccountDetail> accountDetailCollection) {
		this.accountDetailCollection = accountDetailCollection;
	}
	
}
