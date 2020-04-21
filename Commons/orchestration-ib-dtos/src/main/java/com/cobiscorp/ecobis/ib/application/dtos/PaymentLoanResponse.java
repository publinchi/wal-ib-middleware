/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;

/**
   <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "PaymentLoanResponse";
	var tipoDato          = ["BalanceProduct","Integer","Integer","Integer","Integer","Integer"];
	var nombreAtributo    = ["BalanceProduct","returnValue","conditionId","reference","authorizationRequeried","branchSsn"];
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
 * @author kmeza
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class PaymentLoanResponse extends BaseResponse{
	
	
	private BalanceProduct BalanceProduct;
	private Integer returnValue;
	private Integer conditionId;
	private Integer reference;
	private Integer authorizationRequeried;
	private Integer branchSsn;
	private String dateHost;
	/**
	 * @return the dateHost
	 */
	public String getDateHost() {
		return dateHost;
	}
	/**
	 * @param dateHost the dateHost to set
	 */
	public void setDateHost(String dateHost) {
		this.dateHost = dateHost;
	}
	/**
	 * @return the returnValue
	 */
	public Integer getReturnValue() {
		return returnValue;
	}
	/**
	 * @param returnValue the returnValue to set
	 */
	public void setReturnValue(Integer returnValue) {
		this.returnValue = returnValue;
	}
	/**
	 * @return the conditionId
	 */
	public Integer getConditionId() {
		return conditionId;
	}
	/**
	 * @param conditionId the conditionId to set
	 */
	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}
	/**
	 * @return the reference
	 */
	public Integer getReference() {
		return reference;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(Integer reference) {
		this.reference = reference;
	}
	/**
	 * @return the authorizationRequeried
	 */
	public Integer getAuthorizationRequeried() {
		return authorizationRequeried;
	}
	/**
	 * @param authorizationRequeried the authorizationRequeried to set
	 */
	public void setAuthorizationRequeried(Integer authorizationRequeried) {
		this.authorizationRequeried = authorizationRequeried;
	}
	/**
	 * @return the branchSsn
	 */
	public Integer getBranchSsn() {
		return branchSsn;
	}
	/**
	 * @param branchSsn the branchSsn to set
	 */
	public void setBranchSsn(Integer branchSsn) {
		this.branchSsn = branchSsn;
	}
	/**
	 * @return the balanceProduct
	 */
	public BalanceProduct getBalanceProduct() {
		return BalanceProduct;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	/**
	 * @param balanceProduct the balanceProduct to set
	 */
	public void setBalanceProduct(BalanceProduct balanceProduct) {
		BalanceProduct = balanceProduct;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentLoanResponse [BalanceProduct=" + BalanceProduct
				+ ", returnValue=" + returnValue + ", conditionId="
				+ conditionId + ", reference=" + reference
				+ ", authorizationRequeried=" + authorizationRequeried
				+ ", branchSsn=" + branchSsn + ", dateHost=" + dateHost + "]";
	}	

}
