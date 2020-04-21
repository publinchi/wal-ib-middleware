/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BeneficiaryCertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
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
	var nombreClase       = "CertificateDepositRequest";
	var tipoDato          = ["String", "Entity", "String", "Product", 
							"CertificateDeposit", "BeneficiaryCertificateDeposit", 
							"BeneficiaryCertificateDeposit", "BeneficiaryCertificateDeposit", 
							"BeneficiaryCertificateDeposit"];
	var nombreAtributo    = ["userName", "entity", "authorizationRequired", "product", 
							"certificateDeposit", "beneficiaryCertificateDeposit1", 
							"beneficiaryCertificateDeposit2", "beneficiaryCertificateDeposit3", 
							"beneficiaryCertificateDeposit4"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 9;
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
 * @since Oct 28, 2014
 * @version 1.0.0
 */
public class CertificateDepositRequest extends BaseRequest{

	private String userName;
	private Entity entity;
	private String authorizationRequired;
	private Product product;
	private CertificateDeposit certificateDeposit;
	private BeneficiaryCertificateDeposit beneficiaryCertificateDeposit1;
	private BeneficiaryCertificateDeposit beneficiaryCertificateDeposit2;
	private BeneficiaryCertificateDeposit beneficiaryCertificateDeposit3;
	private BeneficiaryCertificateDeposit beneficiaryCertificateDeposit4;
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
	 * @return the certificateDeposit
	 */
	public CertificateDeposit getCertificateDeposit() {
		return certificateDeposit;
	}
	/**
	 * @param certificateDeposit the certificateDeposit to set
	 */
	public void setCertificateDeposit(CertificateDeposit certificateDeposit) {
		this.certificateDeposit = certificateDeposit;
	}
	/**
	 * @return the beneficiaryCertificateDeposit1
	 */
	public BeneficiaryCertificateDeposit getBeneficiaryCertificateDeposit1() {
		return beneficiaryCertificateDeposit1;
	}
	/**
	 * @param beneficiaryCertificateDeposit1 the beneficiaryCertificateDeposit1 to set
	 */
	public void setBeneficiaryCertificateDeposit1(
			BeneficiaryCertificateDeposit beneficiaryCertificateDeposit1) {
		this.beneficiaryCertificateDeposit1 = beneficiaryCertificateDeposit1;
	}
	/**
	 * @return the beneficiaryCertificateDeposit2
	 */
	public BeneficiaryCertificateDeposit getBeneficiaryCertificateDeposit2() {
		return beneficiaryCertificateDeposit2;
	}
	/**
	 * @param beneficiaryCertificateDeposit2 the beneficiaryCertificateDeposit2 to set
	 */
	public void setBeneficiaryCertificateDeposit2(
			BeneficiaryCertificateDeposit beneficiaryCertificateDeposit2) {
		this.beneficiaryCertificateDeposit2 = beneficiaryCertificateDeposit2;
	}
	/**
	 * @return the beneficiaryCertificateDeposit3
	 */
	public BeneficiaryCertificateDeposit getBeneficiaryCertificateDeposit3() {
		return beneficiaryCertificateDeposit3;
	}
	/**
	 * @param beneficiaryCertificateDeposit3 the beneficiaryCertificateDeposit3 to set
	 */
	public void setBeneficiaryCertificateDeposit3(
			BeneficiaryCertificateDeposit beneficiaryCertificateDeposit3) {
		this.beneficiaryCertificateDeposit3 = beneficiaryCertificateDeposit3;
	}
	/**
	 * @return the beneficiaryCertificateDeposit4
	 */
	public BeneficiaryCertificateDeposit getBeneficiaryCertificateDeposit4() {
		return beneficiaryCertificateDeposit4;
	}
	/**
	 * @param beneficiaryCertificateDeposit4 the beneficiaryCertificateDeposit4 to set
	 */
	public void setBeneficiaryCertificateDeposit4(
			BeneficiaryCertificateDeposit beneficiaryCertificateDeposit4) {
		this.beneficiaryCertificateDeposit4 = beneficiaryCertificateDeposit4;
	}
	
	
}
