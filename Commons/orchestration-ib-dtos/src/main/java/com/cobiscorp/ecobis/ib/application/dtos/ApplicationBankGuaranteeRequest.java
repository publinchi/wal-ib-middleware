/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;



/**
 <!--	Autor: Morla David
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ApplicationBankGuaranteeRequest";
	var tipoDato          = ["String","BigDecimal","Integer","Integer","String","String","String","Integer","String","String","String","String",];
	var nombreAtributo    = ["creditLine","amount","currency","guaranteeTerm","beneficiary","guaranteeClass","guaranteeType","entity","expirationDate","cause","guaranteeClassApp","address",];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 12;
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
 * @author dmorla
 * @since Sep 14, 2015
 * @version 1.0.0
 */
public class ApplicationBankGuaranteeRequest extends BaseRequest{
	
	private String creditLine;
	private BigDecimal amount;
	private Integer currency;
	private Integer guaranteeTerm;
	private String beneficiary;
	private String guaranteeClass;
	private String guaranteeType;
	private Integer entity;
	private String expirationDate;
	private String cause;
	private String fixedTerm;
	private String guaranteeTypeApp;
	private String address;
	private Integer agency;
	private String creationDate;
	
	
	
	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the agency
	 */
	public Integer getAgency() {
		return agency;
	}
	/**
	 * @param agency the agency to set
	 */
	public void setAgency(Integer agency) {
		this.agency = agency;
	}
	/**
	 * @return the fixedTerm
	 */
	public String getFixedTerm() {
		return fixedTerm;
	}
	/**
	 * @param fixedTerm the fixedTerm to set
	 */
	public void setFixedTerm(String fixedTerm) {
		this.fixedTerm = fixedTerm;
	}
	/**
	 * @return the creditLine
	 */
	public String getCreditLine() {
		return creditLine;
	}
	/**
	 * @param creditLine the creditLine to set
	 */
	public void setCreditLine(String creditLine) {
		this.creditLine = creditLine;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the currency
	 */
	public Integer getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Integer currency) {
		this.currency = currency;
	}
	/**
	 * @return the guaranteeTerm
	 */
	public Integer getGuaranteeTerm() {
		return guaranteeTerm;
	}
	/**
	 * @param guaranteeTerm the guaranteeTerm to set
	 */
	public void setGuaranteeTerm(Integer guaranteeTerm) {
		this.guaranteeTerm = guaranteeTerm;
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
	 * @return the guaranteeClass
	 */
	public String getGuaranteeClass() {
		return guaranteeClass;
	}
	/**
	 * @param guaranteeClass the guaranteeClass to set
	 */
	public void setGuaranteeClass(String guaranteeClass) {
		this.guaranteeClass = guaranteeClass;
	}
	/**
	 * @return the guaranteeType
	 */
	public String getGuaranteeType() {
		return guaranteeType;
	}
	/**
	 * @param guaranteeType the guaranteeType to set
	 */
	public void setGuaranteeType(String guaranteeType) {
		this.guaranteeType = guaranteeType;
	}
	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}
	/**
	 * @param cause the cause to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}
	/**
	 * @return the guaranteeTypeApp
	 */
	public String getGuaranteeTypeApp() {
		return guaranteeTypeApp;
	}
	/**
	 * @param guaranteeTypeApp the guaranteeTypeApp to set
	 */
	public void setGuaranteeTypeApp(String guaranteeTypeApp) {
		this.guaranteeTypeApp = guaranteeTypeApp;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
