/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.CashiersCheck;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Checkbook;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductBanking;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "RequestCheckbookRequest";
	var tipoDato          = ["Checkbook","Currency","String","String","String","String","User","Integer","String","Product","ProductBanking","String","Client","CashiersCheck"];
	var nombreAtributo    = ["checkbook","currency","authorizationRequired","checkbookArt","deliveryName","deliveyId","entityId","officeDelivery","operation","product","productId","typeId","userName","amount"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 14;
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
 * @author jmoreta
 * @since Nov 5, 2014
 * @version 1.0.0
 */
public class RequestCheckbookRequest extends BaseRequest {

	@Override
	public String toString() {
		return "RequestCheckbookRequest [checkbook=" + checkbook
				+ ", currency=" + currency + ", authorizationRequired="
				+ authorizationRequired + ", checkbookArt=" + checkbookArt
				+ ", deliveryName=" + deliveryName + ", deliveyId=" + deliveyId
				+ ", entityId=" + entityId + ", officeDelivery="
				+ officeDelivery + ", operation=" + operation + ", product="
				+ product + ", productId=" + productId + ", typeId=" + typeId
				+ ", userName=" + userName + ", amount=" + amount + ", cause="
				+ cause + ", causeComi=" + causeComi + ", serviceCost="
				+ serviceCost + "]";
	}
	/**
	 * numberOfChecks,type(typeCheckbook),deliveryDate
	*/
	private Checkbook checkbook;	
	private Currency currency;
	private String authorizationRequired;
	private String checkbookArt;
	private String deliveryName;
	private String deliveyId;
	private User entityId;
	private Integer officeDelivery;
	private String operation;	
	/*productAlias(productAbbreviation),producNumber(account)
	*/
	private Product product;
	private ProductBanking productId;//
	private String typeId;	
	private Client userName;//login
	private CashiersCheck amount;//monto
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
	private String cause;
	private String causeComi;
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	private String serviceCost;
	/**
	 * @return the checkbook: numberOfChecks,type(typeCheckbook),deliveryDate
	 */
	public Checkbook getCheckbook() {
		return checkbook;
	}
	/**
	 * @param checkbook the checkbook to set: numberOfChecks,type,deliveryDate
	 */
	public void setCheckbook(Checkbook checkbook) {
		this.checkbook = checkbook;
	}
	
	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
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
	/**
	 * @return the checkbookArt
	 */
	public String getCheckbookArt() {
		return checkbookArt;
	}
	/**
	 * @param checkbookArt the checkbookArt to set
	 */
	public void setCheckbookArt(String checkbookArt) {
		this.checkbookArt = checkbookArt;
	}
	/**
	 * @return the deliveryName
	 */
	public String getDeliveryName() {
		return deliveryName;
	}
	/**
	 * @param deliveryName the deliveryName to set
	 */
	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}
	/**
	 * @return the deliveyId
	 */
	public String getDeliveyId() {
		return deliveyId;
	}
	/**
	 * @param deliveyId the deliveyId to set
	 */
	public void setDeliveyId(String deliveyId) {
		this.deliveyId = deliveyId;
	}
	/**
	 * @return the entityId
	 */
	public User getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(User entityId) {
		this.entityId = entityId;
	}
	/**
	 * @return the officeDelivery
	 */
	public Integer getOfficeDelivery() {
		return officeDelivery;
	}
	/**
	 * @param officeDelivery the officeDelivery to set
	 */
	public void setOfficeDelivery(Integer officeDelivery) {
		this.officeDelivery = officeDelivery;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the product: productAlias(productAbbreviation),producNumber(account)
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set: productAlias(productAbbreviation),producNumber(account)
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the productId
	 */
	public ProductBanking getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(ProductBanking productId) {
		this.productId = productId;
	}
	/**
	 * @return the type_id
	 */
	public String getTypeId() {
		return typeId;
	}
	/**
	 * @param type_id the type_id to set
	 */
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	/**
	 * @return the userName
	 */
	public Client getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(Client userName) {
		this.userName = userName;
	}
	/**
	 * @return the amount
	 */
	public CashiersCheck getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(CashiersCheck amount) {
		this.amount = amount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	
	
	
}
