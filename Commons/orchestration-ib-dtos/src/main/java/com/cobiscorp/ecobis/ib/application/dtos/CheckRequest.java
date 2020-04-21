/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;

import java.util.Date;
import java.math.BigDecimal;


/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CheckRequest";
	var tipoDato          = ["String", "String", "Product", "Currency",
							"String", "Product", "Check", "Integer",
							"String", String", "String", "BigDecimal",
							"BigDecimal", "String", "String", "String"];
	var nombreAtributo    = ["ejec", "rty", "productId", "currency",
							"userName", "productNumber", "checkNumber", "dateFormatId",
							"criteria", "StringInitialDate", "StringFinalDate", "initialAmount",
							"finalAmount", "initialCheck", "finalCheck", "statusCheck"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 16;
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
 * @author gyagual
 * @since Sep 30, 2014
 * @version 1.0.0
 */
public class CheckRequest extends BaseRequest{
	
	private String ejec;
	private String rty;
	private Product productId;
	private Currency currency;
	private String userName;
	private Product productNumber;
	private Check checkNumber;
	private Integer dateFormatId;
	private String criteria;
	private String StringInitialDate;
	private String StringFinalDate;
	private BigDecimal initialAmount;
	private BigDecimal finalAmount;
	private String initialCheck;
	private String finalCheck;
	private String statusCheck;
	
	
	/**
	 * @return the ejec
	 */
	public String getEjec() {
		return ejec;
	}
	/**
	 * @param ejec the ejec to set
	 */
	public void setEjec(String ejec) {
		this.ejec = ejec;
	}
	/**
	 * @return the rty
	 */
	public String getRty() {
		return rty;
	}
	/**
	 * @param rty the rty to set
	 */
	public void setRty(String rty) {
		this.rty = rty;
	}
	/**
	 * @return the productId
	 */
	public Product getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Product productId) {
		this.productId = productId;
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
	 * @return the checkNumber
	 */
	public Check getCheckNumber() {
		return checkNumber;
	}
	/**
	 * @param checkNumber the checkNumber to set
	 */
	public void setCheckNumber(Check checkNumber) {
		this.checkNumber = checkNumber;
	}
	/**
	 * @return the dateFormatId
	 */
	public Integer getDateFormatId() {
		return dateFormatId;
	}
	/**
	 * @param dateFormatId the dateFormatId to set
	 */
	public void setDateFormatId(Integer dateFormatId) {
		this.dateFormatId = dateFormatId;
	}	 
	/**
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}
	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}	 

	/**
	 * @return the initialDate
	 */
	public String getStringInitialDate() {
		return StringInitialDate;
	}
	/**
	 * @param StringInitialDate the initialDate to set
	 */
	public void setStringInitialDate(String StringInitialDate) {
		this.StringInitialDate = StringInitialDate;
	}	
	/**
	 * @return the finalDate
	 */
	public String getStringFinalDate() {
		return StringFinalDate;
	}
	/**
	 * @param finalDate the finalDate to set
	 */
	public void setStringFinalDate(String StringFinalDate) {
		this.StringFinalDate = StringFinalDate;
	}
	/**
	 * @return the initialAmount
	 */
	public BigDecimal getInitialAmount() {
		return initialAmount;
	}
	/**
	 * @param initialDate the initialAmount to set
	 */
	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}	
	/**
	 * @return the finalAmount
	 */
	public BigDecimal getFinalAmount() {
		return finalAmount;
	}
	/**
	 * @param finalAmount the finalAmount to set
	 */
	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}
	/**
	 * @return the initialCheck
	 */
	public String getInitialCheck() {
		return initialCheck;
	}
	/**
	 * @param initialCheck the initialCheck to set
	 */
	public void setInitialCheck(String initialCheck) {
		this.initialCheck = initialCheck;
	}	
	/**
	 * @return the finalCheck
	 */
	public String getFinalCheck() {
		return finalCheck;
	}
	/**
	 * @param finalCheck the finalCheck to set
	 */
	public void setFinalCheck(String finalCheck) {
		this.finalCheck = finalCheck;
	}	
	/**
	 * @return the statusCheck
	 */
	public String getStatusCheck() {
		return statusCheck;
	}
	/**
	 * @param statusCheck the statusCheck to set
	 */
	public void setStatusCheck(String statusCheck) {
		this.statusCheck = statusCheck;
	}
}
