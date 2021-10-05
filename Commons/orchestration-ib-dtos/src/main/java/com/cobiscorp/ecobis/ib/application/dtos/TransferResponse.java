package com.cobiscorp.ecobis.ib.application.dtos;



import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Officer;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Signer;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TransferResponse";
	var tipoDato          = ["String","String","BalanceProduct","BalanceProduct","Signer","Officer","String","String","BalanceProduct","BalanceProduct","String","String","Integer","Integer","String","Integer","String,"Integer","Double","Double"
	var nombreAtributo    = ["referenceNumber","information","lastBalanceProduct","oldBalanceProduct","signer","officer","Name","dateLastMovement","BalanceProduct","BalanceProductDest","AccountStatus",,"DateHost","returnValue","conditionId","authorizationRequired","branchSSN","body","amount","commission"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 20;
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
public class TransferResponse extends BaseResponse {
	/**
	 * Contains the number of receipt
	 */
	private String referenceNumber;
	/**
	 * Field used to save general information.
	 */
	private String information;
	/**
	 * Object used to save the last detail's balances
	 */
	private BalanceProduct lastBalanceProduct;
	/**
	 * Object used to save the old detail's balances
	 */
	private BalanceProduct oldBalanceProduct;

	/**
	 * Condition's Signers to send a transaction
	 */
	private Signer signer;

	/**
	 * Contains Email-Address of the officer
	 */
	private Officer officer;
	
	private String Name;  
	private String dateLastMovement;
	private BalanceProduct BalanceProduct;
	private BalanceProduct BalanceProductDest;
	private String AccountStatus;
	private String DateHost;
	
	private Integer returnValue;
	private Integer conditionId;
	private String authorizationRequired;
	private Integer branchSSN;
	
	private String body;

	private Integer reference;
	private Double amount;
	private Double commission;
	private String applyDate;
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Contains message of the officer
	 */
	private String transferMessage;

	/**
	 * Contains message of the officer
	 */
	private String productNumber;
	
	/**
	 * Contains return code of the execution of the transference
	 */
	private Integer returnCode;


	/**
	 * @return the productNumber
	 */
	public String getProductNumber() {
		return productNumber;
	}

	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber
	 *            the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * @param information
	 *            the information to set
	 */
	public void setInformation(String information) {
		this.information = information;
	}

	/**
	 * @return the lastBalanceProduct
	 */
	public BalanceProduct getLastBalanceProduct() {
		return lastBalanceProduct;
	}

	/**
	 * @param lastBalanceProduct
	 *            the lastBalanceProduct to set
	 */
	public void setLastBalanceProduct(BalanceProduct lastBalanceProduct) {
		this.lastBalanceProduct = lastBalanceProduct;
	}

	/**
	 * @return the oldBalanceProduct
	 */
	public BalanceProduct getOldBalanceProduct() {
		return oldBalanceProduct;
	}

	/**
	 * @param oldBalanceProduct
	 *            the oldBalanceProduct to set
	 */
	public void setOldBalanceProduct(BalanceProduct oldBalanceProduct) {
		this.oldBalanceProduct = oldBalanceProduct;
	}

	/**
	 * @return the signer
	 */
	public Signer getSigner() {
		return signer;
	}

	/**
	 * @return the transferMessage
	 */
	public String getTransferMessage() {
		return transferMessage;
	}

	/**
	 * @param transferMessage the transferMessage to set
	 */
	public void setTransferMessage(String transferMessage) {
		this.transferMessage = transferMessage;
	}
	/**
	 * @param officer the officer to set
	 */
	public void setOfficer(Officer officer) {
		this.officer = officer;
	}

	/**
	 * @param signer
	 *            the signer to set
	 */
	public void setSigner(Signer signer) {
		this.signer = signer;
	}

	/**
	 * @return the officer
	 */
	public Officer getOfficer() {
		return officer;
	}

	/**
	 * @param officer
	 *            the officer to set
	 */
	public void rOfficer(Officer officer) {
		this.officer = officer;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the dateLastMovement
	 */
	public String getDateLastMovement() {
		return dateLastMovement;
	}

	/**
	 * @param dateLastMovement the dateLastMovement to set
	 */
	public void setDateLastMovement(String dateLastMovement) {
		this.dateLastMovement = dateLastMovement;
	}

	/**
	 * @return the balanceProduct
	 */
	public BalanceProduct getBalanceProduct() {
		return BalanceProduct;
	}

	/**
	 * @param balanceProduct the balanceProduct to set
	 */
	public void setBalanceProduct(BalanceProduct balanceProduct) {
		BalanceProduct = balanceProduct;
	}

	/**
	 * @return the balanceProductDest
	 */
	public BalanceProduct getBalanceProductDest() {
		return BalanceProductDest;
	}

	/**
	 * @param balanceProductDest the balanceProductDest to set
	 */
	public void setBalanceProductDest(BalanceProduct balanceProductDest) {
		BalanceProductDest = balanceProductDest;
	}

	/**
	 * @return the accountStatus
	 */
	public String getAccountStatus() {
		return AccountStatus;
	}

	/**
	 * @param accountStatus the accountStatus to set
	 */
	public void setAccountStatus(String accountStatus) {
		AccountStatus = accountStatus;
	}

	/**
	 * @return the dateHost
	 */
	public String getDateHost() {
		return DateHost;
	}

	/**
	 * @param dateHost the dateHost to set
	 */
	public void setDateHost(String dateHost) {
		DateHost = dateHost;
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
	 * @return the authorizationRequired
	 */
	public String getAuthorizationRequired() {
		return authorizationRequired;
	}

	/**
	 * @param authorizationRequired the authorizationRequired to set
	 */
	public void setAuthorizationRequired(String authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}

	/**
	 * @return the branchSSN
	 */
	public Integer getBranchSSN() {
		return branchSSN;
	}

	/**
	 * @param branchSSN the branchSSN to set
	 */
	public void setBranchSSN(Integer branchSSN) {
		this.branchSSN = branchSSN;
	}

	/**
	 * @return the returnCode
	 */
	public Integer getReturnCode() {
		return returnCode;
	}

	/**
	 * @param code the returnCode to set
	 */
	public void setReturnCode(Integer code) {
		returnCode = code;
	}

	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
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

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	@Override
	public String toString() {
		return "TransferResponse{" +
				"referenceNumber='" + referenceNumber + '\'' +
				", information='" + information + '\'' +
				", lastBalanceProduct=" + lastBalanceProduct +
				", oldBalanceProduct=" + oldBalanceProduct +
				", signer=" + signer +
				", officer=" + officer +
				", Name='" + Name + '\'' +
				", dateLastMovement='" + dateLastMovement + '\'' +
				", BalanceProduct=" + BalanceProduct +
				", BalanceProductDest=" + BalanceProductDest +
				", AccountStatus='" + AccountStatus + '\'' +
				", DateHost='" + DateHost + '\'' +
				", returnValue=" + returnValue +
				", conditionId=" + conditionId +
				", authorizationRequired='" + authorizationRequired + '\'' +
				", branchSSN=" + branchSSN +
				", body='" + body + '\'' +
				", reference=" + reference +
				", amount=" + amount +
				", commission=" + commission +
				", applyDate='" + applyDate + '\'' +
				", transferMessage='" + transferMessage + '\'' +
				", productNumber='" + productNumber + '\'' +
				", returnCode=" + returnCode +
				'}';
	}
}
