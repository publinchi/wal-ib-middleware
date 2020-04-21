/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;

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
	var nombreClase       = "TransactionMonetaryRequest";
	var tipoDato          = ["Product","String","BigDecimal","int","String","int"];
	var nombreAtributo    = ["product","concept","ammount","transaction","cause","alternateCode"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 6;
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
 * @author cecheverria
 * @since Sep 24, 2014
 * @version 1.0.0
 */
public class TransactionMonetaryRequest extends BaseRequest {

	/**
	 * @return the sourceFunds
	 */
	public String getSourceFunds() {
		return sourceFunds;
	}
	/**
	 * @param sourceFunds the sourceFunds to set
	 */
	public void setSourceFunds(String sourceFunds) {
		this.sourceFunds = sourceFunds;
	}
	private String referenceNumberBranch;
	private String referenceNumber;
	/**
	 * @return the referenceNumberBranch
	 */
	public String getReferenceNumberBranch() {
		return referenceNumberBranch;
	}
	/**
	 * @param referenceNumberBranch the referenceNumberBranch to set
	 */
	public void setReferenceNumberBranch(String referenceNumberBranch) {
		this.referenceNumberBranch = referenceNumberBranch;
	}
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	Product product;
	String concept;
	BigDecimal ammount;
	int transaction ;
	String cause;
	int alternateCode;
	String typeCost;
	public BigDecimal getAmmountCommission() {
		return ammountCommission;
	}
	public void setAmmountCommission(BigDecimal ammountCommission) {
		this.ammountCommission = ammountCommission;
	}
	String causeDes;
	String causeComi;
	BigDecimal ammountCommission;
    String correction;
    int ssnCorrection;
    int payCurrency;
    String sourceFunds;
    String useFunds;
	
	
	
	/**
	 * @return the useFunds
	 */
	public String getUseFunds() {
		return useFunds;
	}
	/**
	 * @param useFunds the useFunds to set
	 */
	public void setUseFunds(String useFunds) {
		this.useFunds = useFunds;
	}
	/**
	 * @return the payCurrency
	 */
	public int getPayCurrency() {
		return payCurrency;
	}
	/**
	 * @param payCurrency the payCurrency to set
	 */
	public void setPayCurrency(int payCurrency) {
		this.payCurrency = payCurrency;
	}
	/**
	 * @return the correction
	 */
	public String getCorrection() {
		return correction;
	}
	/**
	 * @param correction the correction to set
	 */
	public void setCorrection(String correction) {
		this.correction = correction;
	}
	/**
	 * @return the ssnCorrection
	 */
	public int getSsnCorrection() {
		return ssnCorrection;
	}
	/**
	 * @param ssnCorrection the ssnCorrection to set
	 */
	public void setSsnCorrection(int ssnCorrection) {
		this.ssnCorrection = ssnCorrection;
	}
	/**
	 * @return the causeComi
	 */
	public String getCauseComi() {
		return causeComi;
	}
	/**
	 * @return the causeDes
	 */
	public String getCauseDes() {
		return causeDes;
	}
	/**
	 * @param causeDes the causeDes to set
	 */
	public void setCauseDes(String causeDes) {
		this.causeDes = causeDes;
	}
	/**
	 * @param causeComi the causeComi to set
	 */
	public void setCauseComi(String causeComi) {
		this.causeComi = causeComi;
	}
	/**
	 * @return the typeCost
	 */
	public String getTypeCost() {
		return typeCost;
	}
	/**
	 * @param typeCost the typeCost to set
	 */
	public void setTypeCost(String typeCost) {
		this.typeCost = typeCost;
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
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	/**
	 * @return the ammount
	 */
	public BigDecimal getAmmount() {
		return ammount;
	}
	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(BigDecimal ammount) {
		this.ammount = ammount;
	}
	/**
	 * @return the transaction
	 */
	public int getTransaction() {
		return transaction;
	}
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(int transaction) {
		this.transaction = transaction;
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
	 * @return the alternateCode
	 */
	public int getAlternateCode() {
		return alternateCode;
	}
	/**
	 * @param alternateCode the alternateCode to set
	 */
	public void setAlternateCode(int alternateCode) {
		this.alternateCode = alternateCode;
	}
	
	
	
	
	
}
