package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ScheduledPaymentRequest";
	var tipoDato          = ["String","String","Integer","Integer","User","Client","Product","Product","String","Integer","Double","String","Integer","String","String",
							 "String","String","String","String","Integer","String","String","String","String","String","PaymentServiceRequest"];
	var nombreAtributo    = ["ReferenceNumberBranch","ReferenceNumber","id","transaction","user","client","debitProduct","creditProduct","type","code","amount",
							"initialDate","paymentsNumber","frecuencyId","concept","item","login","option","receiveNotification","dayToNotify","recoveryRetryFailed",
							"nextPaymentDate","beneficiaryName",
							"operation","key","paymentService"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 26;
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
	 		}</script>
	</td>
 </tr>
 
 <tr>
   <td>Descripci&oacute;n:</td>
   <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
 </tr>
</tbody></table>
**/

public class AtmCardsRequest extends BaseRequest {

	private Integer sequential;
	private Integer bank;
	private Integer cardId;
	private String maskCode;	
	private String typeCard;
	private String nameCard;
	private String statusCard;
	private Integer customer;
	private Integer owner;
	private String customerName;
	private String identification;
	private String expirationDate;
	private Integer cobisProduct;
	private String accountNumber;
	
	public Integer getSequential() {
		return sequential;
	}
	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}
	public String getAReferenceNumberBranch() {
		return AReferenceNumberBranch;
	}
	public void setAReferenceNumberBranch(String aReferenceNumberBranch) {
		AReferenceNumberBranch = aReferenceNumberBranch;
	}
	public String getAReferenceNumber() {
		return AReferenceNumber;
	}
	public void setAReferenceNumber(String aReferenceNumber) {
		AReferenceNumber = aReferenceNumber;
	}
	public Integer getAtransaction() {
		return Atransaction;
	}
	public void setAtransaction(Integer atransaction) {
		Atransaction = atransaction;
	}
	public Integer getBank() {
		return bank;
	}
	public void setBank(Integer bank) {
		this.bank = bank;
	}

	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	public String getMaskCode() {
		return maskCode;
	}
	public void setMaskCode(String maskCode) {
		this.maskCode = maskCode;
	}
	public String getTypeCard() {
		return typeCard;
	}
	public void setTypeCard(String typeCard) {
		this.typeCard = typeCard;
	}
	public String getNameCard() {
		return nameCard;
	}
	public void setNameCard(String nameCard) {
		this.nameCard = nameCard;
	}
	public String getStatusCard() {
		return statusCard;
	}
	public void setStatusCard(String statusCard) {
		this.statusCard = statusCard;
	}
	public Integer getCustomer() {
		return customer;
	}
	public void setCustomer(Integer customer) {
		this.customer = customer;
	}
	public Integer getOwner() {
		return owner;
	}
	public void setOwner(Integer owner) {
		this.owner = owner;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Integer getCobisProduct() {
		return cobisProduct;
	}
	public void setCobisProduct(Integer cobisProduct) {
		this.cobisProduct = cobisProduct;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	private String AReferenceNumberBranch;
	private String AReferenceNumber;	
	private Integer Atransaction; 
	

	
	
	
}

