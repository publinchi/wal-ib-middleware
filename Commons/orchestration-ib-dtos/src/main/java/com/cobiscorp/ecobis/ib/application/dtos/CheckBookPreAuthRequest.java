/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CheckBookPreAuthRequest";
	var tipoDato          = ["String", "Double", "String", "Integer", 
							"String", "Integer", "String", "Integer", 
							"String", "Integer", "String", "String", 
							"Integer", "Integer", "String"];
	var nombreAtributo    = ["account", "amount", "beneficiary", "checkId",
	 						"checkState", "currencyId", "message", 
	 						"clientId", "productAlias", "productId", "login",
	 						"currencyName", "authorizationNumber", "ente", "authorizationRequired"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 15;
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
 * @since Nov 5, 2014
 * @version 1.0.0
 */
public class CheckBookPreAuthRequest extends BaseRequest {
	
	
	private String account;
	private Double amount;
	private String beneficiary;
	private Integer checkId;
	private String checkState;
	private Integer currencyId;
	private String message;
	private Integer clientId;
	private String productAlias;
	private Integer productId;
	private String login;
	private String currencyName;
	private Integer authorizationNumber;
	private Integer ente;
	private String authorizationRequired;
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the beneficiary
	 */
	public String getBeneficiary() {
		return beneficiary;
	}
	/**
	 * @param beneficiary the beneficiary to set
	 */
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}
	/**
	 * @return the checkId
	 */
	public Integer getCheckId() {
		return checkId;
	}
	/**
	 * @param checkId the checkId to set
	 */
	public void setCheckId(Integer checkId) {
		this.checkId = checkId;
	}
	/**
	 * @return the checkState
	 */
	public String getCheckState() {
		return checkState;
	}
	/**
	 * @param checkState the checkState to set
	 */
	public void setCheckState(String checkState) {
		this.checkState = checkState;
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
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the clientId
	 */
	public Integer getClientId() {
		return clientId;
	}
	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	/**
	 * @return the productAlias
	 */
	public String getProductAlias() {
		return productAlias;
	}
	/**
	 * @param productAlias the productAlias to set
	 */
	public void setProductAlias(String productAlias) {
		this.productAlias = productAlias;
	}
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}
	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	/**
	 * @return the authorizationNumber
	 */
	public Integer getAuthorizationNumber() {
		return authorizationNumber;
	}
	/**
	 * @param authorizationNumber the authorizationNumber to set
	 */
	public void setAuthorizationNumber(Integer authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}
	/**
	 * @return the ente
	 */
	public Integer getEnte() {
		return ente;
	}
	/**
	 * @param ente the ente to set
	 */
	public void setEnte(Integer ente) {
		this.ente = ente;
	}
	
	/**
	 * @return the authorizationRequired
	 */
	public String getAuthorizationRequired() {
		return authorizationRequired;
	}
	/**
	 * @param authorizationRequired the authorizationRequired to set
	 */
	public void setAuthorizationRequired(String authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckBookPreAuthRequest [account=" + account + ", amount="
				+ amount + ", beneficiary=" + beneficiary + ", checkId="
				+ checkId + ", checkState=" + checkState + ", currencyId="
				+ currencyId + ", message=" + message + ", clientId="
				+ clientId + ", productAlias=" + productAlias + ", productId="
				+ productId + ", login=" + login + ", currencyName="
				+ currencyName + ", authorizationNumber=" + authorizationNumber
				+ ", ente=" + ente + ", authorizationRequired="
				+ authorizationRequired + "]";
	}
	
	

}
