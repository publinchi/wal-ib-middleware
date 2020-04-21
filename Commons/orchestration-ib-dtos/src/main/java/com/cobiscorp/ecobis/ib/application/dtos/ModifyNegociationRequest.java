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
	var nombreClase       = "ModifyNegociationRequest";
	var tipoDato          = ["String","String","String","String","String","String","String"];
	var nombreAtributo    = ["completeQuota","chargeRate","reductionRate","paymentEffect",
							"priorityRate","advancePayment","loanNumber"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 7;
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
 * @since Nov 19, 2014
 * @version 1.0.0
 */
public class ModifyNegociationRequest extends BaseRequest{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModifyNegociationRequest [completeQuota=" + completeQuota
				+ ", chargeRate=" + chargeRate + ", reductionRate="
				+ reductionRate + ", paymentEffect=" + paymentEffect
				+ ", priorityRate=" + priorityRate + ", advancePayment="
				+ advancePayment + ", loanNumber=" + loanNumber + "]";
	}
	private String completeQuota;
	private String chargeRate;
	private String reductionRate;
	private String paymentEffect;
	private String priorityRate;
	private String advancePayment;
	private String loanNumber;
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

}
