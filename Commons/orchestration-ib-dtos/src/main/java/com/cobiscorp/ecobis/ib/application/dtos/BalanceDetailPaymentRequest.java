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
	var nombreClase       = "BalanceDetailPaymentRequest";
	var tipoDato          = ["Integer","Integer","String","Product","String"];
	var nombreAtributo    = ["productId","currencyID","userName","productNumber","validateAccount"];
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
 * @author kmeza
 * @since Oct 9, 2014
 * @version 1.0.0
 */
public class BalanceDetailPaymentRequest extends BaseRequest {
	private Integer productId;
	private Integer currencyID;
	private String userName;
	private  Product productNumber;
	private String validateAccount;
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId)  {
		this.productId = productId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	public Integer getCurrencyID() {
		return currencyID;
	}
	/**
	 * @param currencyID the currencyID to set
	 */
	public void setCurrencyID(Integer currencyID) {
		this.currencyID = currencyID;
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
	 * @return the productNumber
	 */
	
	/**
	 * @return the validateAccount
	 */
	public String getValidateAccount() {
		return validateAccount;
	}
	/**
	 * @return the productNumber
	 */
	public Product getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(Product productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @param validateAccount the validateAccount to set
	 */
	public void setValidateAccount(String validateAccount) {
		this.validateAccount = validateAccount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BalanceDetailPaymentRequest [productId=" + productId
				+ ", currencyID=" + currencyID + ", userName=" + userName
				+ ", productNumber=" + productNumber + ", validateAccount="
				+ validateAccount + "]";
	}
	
	
	
	
	
	
	
	

}
