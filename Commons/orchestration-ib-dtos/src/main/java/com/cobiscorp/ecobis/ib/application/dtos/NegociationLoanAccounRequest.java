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
	var nombreClase       = "NegociationLoanAccounRequest";
	var tipoDato          = ["String","String","Integer","Integer","Integer","String","Product","String","String","String","String","String","String"];
	var nombreAtributo    = ["loanNumber","operation","transactionId","currencyId","productId","userName","productNumber","completeQuota","chargeRate",
							 "reductionRate","paymentEffect","priorityRate","advancePayment"];
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
 * @author kmeza
 * @since Nov 20, 2014
 * @version 1.0.0
 */
public class NegociationLoanAccounRequest extends BaseRequest{

	private String loanNumber;
	private String operation;
	private  Integer transactionId;
	private Integer currencyId;
	private Integer productId;
	private String userName;
	private Product productNumber;
	private String completeQuota;
	private String chargeRate;
	private String reductionRate;
	private String paymentEffect;
	private String priorityRate;
	private String advancePayment;

	
	
	
	/**
	 * @return the transactionId
	 */
	public Integer getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NegociationLoanAccounRequest [loanNumber=" + loanNumber
				+ ", operation=" + operation + ", transactionId="
				+ transactionId + ", currencyId=" + currencyId + ", productId="
				+ productId + ", userName=" + userName + ", productNumber="
				+ productNumber + ", completeQuota=" + completeQuota
				+ ", chargeRate=" + chargeRate + ", reductionRate="
				+ reductionRate + ", paymentEffect=" + paymentEffect
				+ ", priorityRate=" + priorityRate + ", advancePayment="
				+ advancePayment + "]";
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
	 * @return the completeQuota
	 */
	public String getCompleteQuota() {
		return completeQuota;
	}
	/**
	 * @param completeQuota the completeQuota to set
	 */
	public void setCompleteQuota(String completeQuota) {
		this.completeQuota = completeQuota;
	}
	/**
	 * @return the chargeRate
	 */
	public String getChargeRate() {
		return chargeRate;
	}
	/**
	 * @param chargeRate the chargeRate to set
	 */
	public void setChargeRate(String chargeRate) {
		this.chargeRate = chargeRate;
	}
	/**
	 * @return the reductionRate
	 */
	public String getReductionRate() {
		return reductionRate;
	}
	/**
	 * @param reductionRate the reductionRate to set
	 */
	public void setReductionRate(String reductionRate) {
		this.reductionRate = reductionRate;
	}
	/**
	 * @return the paymentEffect
	 */
	public String getPaymentEffect() {
		return paymentEffect;
	}
	/**
	 * @param paymentEffect the paymentEffect to set
	 */
	public void setPaymentEffect(String paymentEffect) {
		this.paymentEffect = paymentEffect;
	}
	/**
	 * @return the priorityRate
	 */
	public String getPriorityRate() {
		return priorityRate;
	}
	/**
	 * @param priorityRate the priorityRate to set
	 */
	public void setPriorityRate(String priorityRate) {
		this.priorityRate = priorityRate;
	}
	/**
	 * @return the advancePayment
	 */
	public String getAdvancePayment() {
		return advancePayment;
	}
	/**
	 * @param advancePayment the advancePayment to set
	 */
	public void setAdvancePayment(String advancePayment) {
		this.advancePayment = advancePayment;
	}

	
	
	
	

}
