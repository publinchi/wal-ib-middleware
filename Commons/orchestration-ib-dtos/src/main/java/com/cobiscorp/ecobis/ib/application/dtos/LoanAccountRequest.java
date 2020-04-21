/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				  ["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "LoanAccountRequest";
	var tipoDato          = ["String","Integer","String","Product"];
	var nombreAtributo    = ["loanNumber","currencyId","userName","produtNumber"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 4;
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
 * @author kmeza
 * @since Nov 18, 2014
 * @version 1.0.0
 */
public class LoanAccountRequest extends BaseRequest {

	private String loanNumber;
	private Integer currencyId;

	private String   userName;
	private Product produtNumber;
	
	

	/**
	 * @return the produtNumber
	 */
	public Product getProdutNumber() {
		return produtNumber;
	}

	/**
	 * @param produtNumber the produtNumber to set
	 */
	public void setProdutNumber(Product produtNumber) {
		this.produtNumber = produtNumber;
	}

	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}

	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the loanNumber
	 */
	public String getLoanNumber() {
		return loanNumber;
	}

	/**
	 * @param loanNumber the loanNumber to set
	 */
	public void setLoanNumber(String loanNumber) {
		this.loanNumber = loanNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoanAccountRequest [loanNumber=" + loanNumber + ", currencyId="
				+ currencyId + ", userName=" + userName + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	
	

}
