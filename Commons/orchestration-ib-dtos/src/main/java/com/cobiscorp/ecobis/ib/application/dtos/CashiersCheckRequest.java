/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CashiersCheck;
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
	var nombreClase       = "CashiersCheckRequest";
	var tipoDato          = ["String", "String", "Product", "CashiersCheck", "Entity"];
	var nombreAtributo    = ["userName", "authorizationRequired", "product", "cashiersCheck", "entity"];
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
 * @author jveloz
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class CashiersCheckRequest extends BaseRequest{

	private String userName;
	private String authorizationRequired;
	private Product product;
	private CashiersCheck cashiersCheck;
	private Entity entity;
	private String cause;
	
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getCauseDes() {
		return causeDes;
	}
	public void setCauseDes(String causeDes) {
		this.causeDes = causeDes;
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
	private String causeDes;
	private String causeComi;
	private String serviceCost;
	
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
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	
	
	/**
	 * @return the managerCheck
	 */
	public CashiersCheck getManagerCheck() {
		return cashiersCheck;
	}
	/**
	 * @param managerCheck the managerCheck to set
	 */
	public void setManagerCheck(CashiersCheck managerCheck) {
		this.cashiersCheck = managerCheck;
	}
	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	
}
