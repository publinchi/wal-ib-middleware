/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;

/**
<!--	Autor: Baque H Jorge

	 nombreClase	    : Se coloca el nombre de la clase java
	 tipoDato	        : Es un arreglo de tipo de datos ["String", "List", "int",...]
     nombreAtributo  	: Es un arreglo que contiene los nombre de atributos ["altura", "edad", "peso"]
     descripcionClase	: Lleva una breve descripción de la clase
     numeroAtributos : Numero total de atributos de [1,...n]-->
     
	<script type="text/javascript">
		var nombreClase       = "ACHTransferRequest";
		var tipoDato          = ["String","String","String","String","String","String","String","String","short"];
		var nombreAtributo    = ["destinationBankName","transitRoute","beneficiaryName","destinationBankId",
								 "destinationBankPhone","concept","documentIdBeneficiary","chargeAccount","chargeProduct"];
		var descripcionClase  = "Transferencias ACH";
		var numeroAtributos   = 9;
	</script>     
 
 <table>
		<table><tbody>
			<tr>
  			<th Alignment="center" bgcolor="#CCCCFF">Nombre Clase: 
	    			<script type="text/javascript">document.writeln(nombreClase);</script> 
  			</th>
			</tr>
			<tr>
   			<td Alignment="center" bgcolor="#CCCCFF">Tipo Dato</td>
   			<td Alignment="center" bgcolor="#CCCCFF">Nombre Atributo</td>
			</tr>
			<tr>
   			<td style="font-family:'Courier New', Courier, monospace; color:#906;"><script type="text/javascript">
			  		for(i=0;i<numeroAtributos;i++){ 
			  		document.write(tipoDato[i]);
					document.write("<br/>");
			  		}</script>
			  	</td>
				<td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
				  		for(i=0;i<numeroAtributos;i++){ 
				  		document.write(nombreAtributo[i]);
						document.write("<br />");
				  		}</script>
				</td>
			</tr>
			<tr>
 			<td>Descripci&oacute;n:</td>
 			<td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
			</tr>
		</tbody></table>
	</table>
     
**/


/**
 * @author bborja
 * @since 15/1/2015
 * @version 1.0.0
 */
public class ACHTransferRequest extends TransferRequest {
	private String destinationBankName;
	private String transitRoute;
	private String beneficiaryName;
	private String destinationBankId;
	private String destinationBankPhone;
	private String concept;
	private String documentIdBeneficiary;
	private String chargeAccount;
	private short chargeProduct;
	private int currencyId;
	private String reverseTransaction;
	private String reverseTaxTransaction;
	private String reverseCommission;
	
		
	
	/**
	 * @return the reverseCommission
	 */
	public String getReverseCommission() {
		return reverseCommission;
	}
	/**
	 * @param reverseCommission the reverseCommission to set
	 */
	public void setReverseCommission(String reverseCommission) {
		this.reverseCommission = reverseCommission;
	}
	/**
	 * @return the reverseTaxTransaction
	 */
	public String getReverseTaxTransaction() {
		return reverseTaxTransaction;
	}
	/**
	 * @param reverseTaxTransaction the reverseTaxTransaction to set
	 */
	public void setReverseTaxTransaction(String reverseTaxTransaction) {
		this.reverseTaxTransaction = reverseTaxTransaction;
	}
	/**
	 * @return the reverseTransaction
	 */
	public String getReverseTransaction() {
		return reverseTransaction;
	}
	/**
	 * @param reverseTransaction the reverseTransaction to set
	 */
	public void setReverseTransaction(String reverseTransaction) {
		this.reverseTransaction = reverseTransaction;
	}
	/**
	 * @return the chargeAccount
	 */
	public String getChargeAccount() {
		return chargeAccount;
	}
	/**
	 * @param chargeAccount the chargeAccount to set
	 */
	public void setChargeAccount(String chargeAccount) {
		this.chargeAccount = chargeAccount;
	}
	/**
	 * @return the chargeProduct
	 */
	public short getChargeProduct() {
		return chargeProduct;
	}
	/**
	 * @param chargeProduct the chargeProduct to set
	 */
	public void setChargeProduct(short chargeProduct) {
		this.chargeProduct = chargeProduct;
	}
	/**
	 * @return the clientCoreCode
	 */
	public int getClientCoreCode() {
		return clientCoreCode;
	}
	/**
	 * @param clientCoreCode the clientCoreCode to set
	 */
	public void setClientCoreCode(int clientCoreCode) {
		this.clientCoreCode = clientCoreCode;
	}
	private int clientCoreCode;
	
	/**
	 * @return the documentIdBeneficiary
	 */
	public String getDocumentIdBeneficiary() {
		return documentIdBeneficiary;
	}
	/**
	 * @param documentIdBeneficiary the documentIdBeneficiary to set
	 */
	public void setDocumentIdBeneficiary(String documentIdBeneficiary) {
		this.documentIdBeneficiary = documentIdBeneficiary;
	}
	/**
	 * @return the destinationBankName
	 */
	public String getDestinationBankName() {
		return destinationBankName;
	}
	/**
	 * @param destinationBankName the destinationBankName to set
	 */
	public void setDestinationBankName(String destinationBankName) {
		this.destinationBankName = destinationBankName;
	}
	/**
	 * @return the transitRoute
	 */
	public String getTransitRoute() {
		return transitRoute;
	}
	/**
	 * @param transitRoute the transitRoute to set
	 */
	public void setTransitRoute(String transitRoute) {
		this.transitRoute = transitRoute;
	}
	/**
	 * @return the beneficiaryName
	 */
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	/**
	 * @param beneficiaryName the beneficiaryName to set
	 */
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	/**
	 * @return the destinationBankId
	 */
	public String getDestinationBankId() {
		return destinationBankId;
	}
	/**
	 * @param destinationBankId the destinationBankId to set
	 */
	public void setDestinationBankId(String destinationBankId) {
		this.destinationBankId = destinationBankId;
	}
	/**
	 * @return the destinationBankPhone
	 */
	public String getDestinationBankPhone() {
		return destinationBankPhone;
	}
	/**
	 * @param destinationBankPhone the destinationBankPhone to set
	 */
	public void setDestinationBankPhone(String destinationBankPhone) {
		this.destinationBankPhone = destinationBankPhone;
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
	 * @return the currencyId
	 */
	public int getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	
}
