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
	var nombreClase       = "InternationalTransferResponse";
	var tipoDato          = ["String","String","String","String","String","String","String"];
	var nombreAtributo    = ["transactionFee","beneficiaryCountryName","beneficiaryBankAddress",
							 "intermediaryBankAddress","intermediaryBankCountryName","intermediaryBankName","transactionTotalAmount"];
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
 * @author wsanchez
 * @since Oct 8, 2014
 * @version 1.0.0
 */
public class InternationalTransferResponse extends TransferResponse{

	private String transactionFee;
	private String beneficiaryCountryName;
	private String beneficiaryBankAddress;
	private String intermediaryBankAddress;
	private String intermediaryBankCountryName;
	private String intermediaryBankName;
	private String transactionTotalAmount;
	
	/**
	 * @return the transactionFee
	 */
	public String getTransactionFee() {
		return transactionFee;
	}

	/**
	 * @param transactionFee the transactionFee to set
	 */
	public void setTransactionFee(String transactionFee) {
		this.transactionFee = transactionFee;
	}

	/**
	 * @return the beneficiaryCountryName
	 */
	public String getBeneficiaryCountryName() {
		return beneficiaryCountryName;
	}

	/**
	 * @param beneficiaryCountryName the beneficiaryCountryName to set
	 */
	public void setBeneficiaryCountryName(String beneficiaryCountryName) {
		this.beneficiaryCountryName = beneficiaryCountryName;
	}

	/**
	 * @return the beneficiaryBankAddress
	 */
	public String getBeneficiaryBankAddress() {
		return beneficiaryBankAddress;
	}

	/**
	 * @param beneficiaryBankAddress the beneficiaryBankAddress to set
	 */
	public void setBeneficiaryBankAddress(String beneficiaryBankAddress) {
		this.beneficiaryBankAddress = beneficiaryBankAddress;
	}

	/**
	 * @return the intermediaryBankAddress
	 */
	public String getIntermediaryBankAddress() {
		return intermediaryBankAddress;
	}

	/**
	 * @param intermediaryBankAddress the intermediaryBankAddress to set
	 */
	public void setIntermediaryBankAddress(String intermediaryBankAddress) {
		this.intermediaryBankAddress = intermediaryBankAddress;
	}

	/**
	 * @return the intermediaryBankCountryName
	 */
	public String getIntermediaryBankCountryName() {
		return intermediaryBankCountryName;
	}

	/**
	 * @param intermediaryBankCountryName the intermediaryBankCountryName to set
	 */
	public void setIntermediaryBankCountryName(String intermediaryBankCountryName) {
		this.intermediaryBankCountryName = intermediaryBankCountryName;
	}

	/**
	 * @return the intermediaryBankName
	 */
	public String getIntermediaryBankName() {
		return intermediaryBankName;
	}

	/**
	 * @param intermediaryBankName the intermediaryBankName to set
	 */
	public void setIntermediaryBankName(String intermediaryBankName) {
		this.intermediaryBankName = intermediaryBankName;
	}

	/**
	 * @return the transactionTotalAmount
	 */
	public String getTransactionTotalAmount() {
		return transactionTotalAmount;
	}

	/**
	 * @param transactionTotalAmount the transactionTotalAmount to set
	 */
	public void setTransactionTotalAmount(String transactionTotalAmount) {
		this.transactionTotalAmount = transactionTotalAmount;
	}

}
