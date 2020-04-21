/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.ArrayList;
import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NoPaycheckOrder;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CheckbookSuspendResponse";
	var tipoDato          = ["List < NoPaycheckOrder >", "List < BalanceAccount >", "Integer", "String", "Integer"];
	var nombreAtributo    = ["listNoPaycheckOrder", "listBalanceAccount", "reference", "authorizationRequired", "branchSSN"];
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
 * @author jchonillo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
public class CheckbookSuspendResponse extends BaseResponse{

	private List<NoPaycheckOrder> listNoPaycheckOrder = new ArrayList<NoPaycheckOrder>();
	private List<BalanceAccount> listBalanceAccount = new ArrayList<BalanceAccount>();
	
	private Integer reference;
	private String authorizationRequired;
	private Integer branchSSN;
	
	/**
	 * @return the list of NoPaycheckOrder
	 */
	public List<NoPaycheckOrder> getListNoPaycheckOrder() {
		return listNoPaycheckOrder;
	}

	public void setListNoPaycheckOrder(List<NoPaycheckOrder> listNoPaycheckOrder) {
		this.listNoPaycheckOrder = listNoPaycheckOrder;
	}

	/**
	 * @return the list of BalanceAccount
	 */
	public List<BalanceAccount> getListBalanceAccount() {
		return listBalanceAccount;
	}

	public void setListBalanceAccount(List<BalanceAccount> listBalanceAccount) {
		this.listBalanceAccount = listBalanceAccount;
	}
	
	/**
	 * @return the reference
	 */
	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	/**
	 * @return the authorizationRequired
	 */
	public String getAuthorizationRequired() {
		return authorizationRequired;
	}

	public void setAuthorizationRequired(String authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}

	/**
	 * @return the branchSSN
	 */
	public Integer getBranchSSN() {
		return branchSSN;
	}

	public void setBranchSSN(Integer branchSSN) {
		this.branchSSN = branchSSN;
	}

	@Override
	public String toString() {
		return "CheckbookSuspendResponse [listNoPaycheckOrder="
				+ listNoPaycheckOrder + ", listBalanceAccount="
				+ listBalanceAccount + "]";
	}

}
