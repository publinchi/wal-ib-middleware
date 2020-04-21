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
	var nombreClase       = "NoPaycheckOrderRequest";
	var tipoDato          = ["Integer","String","String","String","Integer","Integer",
							"Integer","String","Integer","String","Integer","String","String"];
	var nombreAtributo    = ["entityId","account","authorizationRequired","concept","currencyId",
							"initialCheck","numberOfChecks","productAbbreviation","productId",
							"reason","transactionId","typeNotif","userName"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 13;
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
 * @since Nov 11, 2014
 * @version 1.0.0
 */
public class NoPaycheckOrderRequest extends BaseRequest{

	private Integer entityId;
	private String account;
	private String authorizationRequired;
	private String concept;
	private Integer currencyId;
	private Integer initialCheck;
	private Integer numberOfChecks;
	private String productAbbreviation;
	private Integer productId;
	private String reason;
	private Integer transactionId;
	private String typeNotif;
	private String userName;
	private String cause;
	private String causeComi;
	private String serviceCost;
	
	/**
	 * @return the entityId
	 */
	public Integer getEntityId() {
		return entityId;
	}
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
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
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	
	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}
	
	/**
	 * @return the initialCheck
	 */
	public Integer getInitialCheck() {
		return initialCheck;
	}
	public void setInitialCheck(Integer initialCheck) {
		this.initialCheck = initialCheck;
	}
	
	/**
	 * @return the numberOfChecks
	 */
	public Integer getNumberOfChecks() {
		return numberOfChecks;
	}
	public void setNumberOfChecks(Integer numberOfChecks) {
		this.numberOfChecks = numberOfChecks;
	}
	
	/**
	 * @return the productAbbreviation
	 */
	public String getProductAbbreviation() {
		return productAbbreviation;
	}
	public void setProductAbbreviation(String productAbbreviation) {
		this.productAbbreviation = productAbbreviation;
	}
	
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * @return the transactionId
	 */
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * @return the typeNotif
	 */
	public String getTypeNotif() {
		return typeNotif;
	}
	public void setTypeNotif(String typeNotif) {
		this.typeNotif = typeNotif;
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getCauseComi() {
		return causeComi;
	}
	public void setCauseComi(String causeComi) {
		this.causeComi = causeComi;
	}
	public String getServiceCost() {
		return serviceCost;
	}
	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return "NoPaycheckOrderRequest [entityId=" + entityId + ", account="
				+ account + ", authorizationRequired=" + authorizationRequired
				+ ", concept=" + concept + ", currencyId=" + currencyId
				+ ", initialCheck=" + initialCheck + ", numberOfChecks="
				+ numberOfChecks + ", productAbbreviation="
				+ productAbbreviation + ", productId=" + productId
				+ ", reason=" + reason + ", transactionId=" + transactionId
				+ ", typeNotif=" + typeNotif + ", userName=" + userName + "]";
	}
	
}
