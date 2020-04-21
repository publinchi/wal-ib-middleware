package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;
import java.util.Date;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SearchOption;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TransferRequest";
	var tipoDato          = ["String","Product","Product","Client","BigDecimal","String","Date","String","String","BigDecimal","Integer","SearchOption","User","String","TransferResponse"];
	var nombreAtributo    = ["referenceNumberBranch","originProduct","destinationProduct","user","ammount","descriptionTransfer","systemDate","transactionIdentifier","referenceNumber","commisionAmmount","reference","searchOption","userTransferRequest","operation","transferResponse"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 15;
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
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class TransferRequest extends BaseRequest {
	/**
	 * Contains the number of receipt
	 */
	private String referenceNumberBranch;
	/**
	 * Object which contains information about debit account
	 */
	private Product originProduct;
	/**
	 * Object which contains information about credit account
	 */
	private Product destinationProduct;
	/**
	 * Object which contains information about web client of Virtual Banking
	 */
	private Client user;
	/**
	 * Object which contains ammount of transfer
	 */
	private BigDecimal ammount;
	/**
	 * Object which contains the detail of the transfer
	 */
	private String descriptionTransfer;

	/**
	 * SystemDate (Format mm/dd/yyyy hh:mm:ss)
	 */
	private Date systemDate;

	/**
	 * TransactionIdentifier (e.g 'TRANSFERENCIA CUENTAS PROPIAS' o '180059' o
	 * 'TCP' )
	 */
	private String transactionIdentifier;
	/**
	 * referenceNumber to identify the transfer (e.g 1234556 )
	 */
	private String referenceNumber;

	/**
	 * Object which contains ammount of transfer
	 */
	private BigDecimal commisionAmmount;
	
	private Integer reference;
	
	private SearchOption searchOption;
	
	private User userTransferRequest;
	
	private String operation;
	
	private TransferResponse transferResponse;
	
	
	private String cause;
	
	private String causeDes;
	
	private String causeComi;
	private String comissionCurrency;
	
	/**
	 * @return the comissionCurrency
	 */
	public String getComissionCurrency() {
		return comissionCurrency;
	}

	/**
	 * @param comissionCurrency the comissionCurrency to set
	 */
	public void setComissionCurrency(String comissionCurrency) {
		this.comissionCurrency = comissionCurrency;
	}

	private String originatorFunds;
	private String receiverFunds;
	private String serviceCost;
	
	public String getServiceCost() {
		return serviceCost;
	}

	public void setServiceCost(String serviceCost) {
		this.serviceCost = serviceCost;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getCauseDes() {
		return causeDes;
	}

	public void setCauseDes(String causeDes) {
		this.causeDes = causeDes;
	}

	public String getCauseComi() {
		return causeComi;
	}

	public void setCauseComi(String causeComi) {
		this.causeComi = causeComi;
	}

	public Product getOriginProduct() {
		return originProduct;
	}

	public void setOriginProduct(Product originProduct) {
		this.originProduct = originProduct;
	}

	public Product getDestinationProduct() {
		return destinationProduct;
	}

	public void setDestinationProduct(Product destinationProduct) {
		this.destinationProduct = destinationProduct;
	}

	public Client getUser() {
		return user;
	}

	public void setUser(Client user) {
		this.user = user;
	}

	public BigDecimal getAmmount() {
		return ammount;
	}

	public void setAmmount(BigDecimal ammount) {
		this.ammount = ammount;
	}

	public String getDescriptionTransfer() {
		return descriptionTransfer;
	}

	public void setDescriptionTransfer(String descriptionTransfer) {
		this.descriptionTransfer = descriptionTransfer;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the commisionAmmount
	 */
	public BigDecimal getCommisionAmmount() {
		return commisionAmmount;
	}

	/**
	 * @param commisionAmmount
	 *            the commisionAmmount to set
	 */
	public void setCommisionAmmount(BigDecimal commisionAmmount) {
		this.commisionAmmount = commisionAmmount;
	}

	public String getReferenceNumberBranch() {
		return referenceNumberBranch;
	}

	public void setReferenceNumberBranch(String referenceNumberBranch) {
		this.referenceNumberBranch = referenceNumberBranch;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	public SearchOption getSearchOption() {
		return searchOption;
	}

	public void setSearchOption(SearchOption searchOption) {
		this.searchOption = searchOption;
	}

	public User getUserTransferRequest() {
		return userTransferRequest;
	}

	public void setUserTransferRequest(User userTransferRequest) {
		this.userTransferRequest = userTransferRequest;
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
	 * @return the transferResponse
	 */
	public TransferResponse getTransferResponse() {
		return transferResponse;
	}

	/**
	 * @param transferResponse the transferResponse to set
	 */
	public void setTransferResponse(TransferResponse transferResponse) {
		this.transferResponse = transferResponse;
	}

	/**
	 * @return the originatorFunds
	 */
	public String getOriginatorFunds() {
		return originatorFunds;
	}

	/**
	 * @param originatorFunds the originatorFunds to set
	 */
	public void setOriginatorFunds(String originatorFunds) {
		this.originatorFunds = originatorFunds;
	}

	/**
	 * @return the receiverFunds
	 */
	public String getReceiverFunds() {
		return receiverFunds;
	}

	/**
	 * @param receiverFunds the receiverFunds to set
	 */
	public void setReceiverFunds(String receiverFunds) {
		this.receiverFunds = receiverFunds;
	}

	@Override
	public String toString() {
		return "TransferRequest [referenceNumberBranch="
				+ referenceNumberBranch + ", originProduct=" + originProduct
				+ ", destinationProduct=" + destinationProduct + ", user="
				+ user + ", ammount=" + ammount + ", descriptionTransfer="
				+ descriptionTransfer + ", systemDate=" + systemDate
				+ ", transactionIdentifier=" + transactionIdentifier
				+ ", referenceNumber=" + referenceNumber
				+ ", commisionAmmount=" + commisionAmmount + ", reference="
				+ reference + ", searchOption=" + searchOption
				+ ", userTransferRequest=" + userTransferRequest + "]";
	}

}
