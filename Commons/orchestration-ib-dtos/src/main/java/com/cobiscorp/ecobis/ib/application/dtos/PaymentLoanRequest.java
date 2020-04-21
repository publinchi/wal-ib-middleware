/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

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
	var nombreClase       = "PaymentLoanRequest";
	var tipoDato          = ["String","String","Integer","Integer","String","String","String","String","BigDecimal","String","Integer","Integer","Integer","String","String","String","String","BigDecimal","BigDecimal","float","String","BigDecimal"];
	var nombreAtributo    = ["ReferenceNumberBranch","ReferenceNumber","productid","currencyIdconcept","userNameproductName","accountammount","loanNumber","destProduct","loanCurrencyId","entityId",,"productAbbreviation","authorizationRequired","thirdPartyAssociated","isThirdParty","loanPaymentAmount","concepta","rateValue","validateAccount","creditAmount"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 22;
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
public class PaymentLoanRequest extends BaseRequest{
	private String ReferenceNumberBranch;
	private String ReferenceNumber;
	private Integer productid;
	private Integer currencyId;
	private String userName;
	private String account;
	private String concept;
	private String productName;
	private BigDecimal ammount;
	private String loanNumber;
	private Integer destProduct;
	private Integer loanCurrencyId;
	private Integer  entityId;
	private String productAbbreviation;
	private String authorizationRequired;
	private String thirdPartyAssociated;
	private String isThirdParty;
	private BigDecimal loanPaymentAmount;
	private BigDecimal concepta;
	private float rateValue;
	private String validateAccount;
	private BigDecimal creditAmount;
	private String causa;
	private String causaDes;
	private String causaComi;
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
	private String sourceFunds;
	private String useFunds;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	/**
	 * @return the returnValue
	 */
	
	
	
	
	/**
	 * @return the causaDes
	 */
	public String getCausaDes() {
		return causaDes;
	}

	/**
	 * @param causaDes the causaDes to set
	 */
	public void setCausaDes(String causaDes) {
		this.causaDes = causaDes;
	}

	/**
	 * @return the causaComi
	 */
	public String getCausaComi() {
		return causaComi;
	}

	/**
	 * @param causaComi the causaComi to set
	 */
	public void setCausaComi(String causaComi) {
		this.causaComi = causaComi;
	}

	/**
	 * @return the causa
	 */
	public String getCausa() {
		return causa;
	}

	/**
	 * @param causa the causa to set
	 */
	public void setCausa(String causa) {
		this.causa = causa;
	}

	
	
	
	
	
	

	
	
	
	
	
	

	/**
	 * @return the productid
	 */
	public Integer getProductid() {
		return productid;
	}
	
	/**
	 * @return the referenceNumberBranch
	 */
	public String getReferenceNumberBranch() {
		return ReferenceNumberBranch;
	}

	/**
	 * @param referenceNumberBranch the referenceNumberBranch to set
	 */
	public void setReferenceNumberBranch(String referenceNumberBranch) {
		ReferenceNumberBranch = referenceNumberBranch;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return ReferenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		ReferenceNumber = referenceNumber;
	}

	/**
	 * @return the creditAmount
	 */
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}
	/**
	 * @param creditAmount the creditAmount to set
	 */
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
	/**
	 * @param productid the productid to set
	 */
	public void setProductid(Integer productid) {
		this.productid = productid;
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
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
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
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
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
	 * @return the destProduct
	 */
	public Integer getDestProduct() {
		return destProduct;
	}
	/**
	 * @param destProduct the destProduct to set
	 */
	public void setDestProduct(Integer destProduct) {
		this.destProduct = destProduct;
	}
	/**
	 * @return the loanCurrencyId
	 */
	public Integer getLoanCurrencyId() {
		return loanCurrencyId;
	}
	/**
	 * @param loanCurrencyId the loanCurrencyId to set
	 */
	public void setLoanCurrencyId(Integer loanCurrencyId) {
		this.loanCurrencyId = loanCurrencyId;
	}
	/**
	 * @return the entityId
	 */
	public Integer getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	/**
	 * @return the productAbbreviation
	 */
	public String getProductAbbreviation() {
		return productAbbreviation;
	}
	/**
	 * @param productAbbreviation the productAbbreviation to set
	 */
	public void setProductAbbreviation(String productAbbreviation) {
		this.productAbbreviation = productAbbreviation;
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
	 * @return the thirdPartyAssociated
	 */
	public String getThirdPartyAssociated() {
		return thirdPartyAssociated;
	}
	/**
	 * @param thirdPartyAssociated the thirdPartyAssociated to set
	 */
	public void setThirdPartyAssociated(String thirdPartyAssociated) {
		this.thirdPartyAssociated = thirdPartyAssociated;
	}
	/**
	 * @return the isThirdParty
	 */
	public String getIsThirdParty() {
		return isThirdParty;
	}
	/**
	 * @param isThirdParty the isThirdParty to set
	 */
	public void setIsThirdParty(String isThirdParty) {
		this.isThirdParty = isThirdParty;
	}
	/**
	 * @return the loanPaymentAmount
	 */
	public BigDecimal getLoanPaymentAmount() {
		return loanPaymentAmount;
	}
	/**
	 * @param loanPaymentAmount the loanPaymentAmount to set
	 */
	public void setLoanPaymentAmount(BigDecimal loanPaymentAmount) {
		this.loanPaymentAmount = loanPaymentAmount;
	}
	/**
	 * @return the concepta
	 */
	public BigDecimal getConcepta() {
		return concepta;
	}
	/**
	 * @param concepta the concepta to set
	 */
	public void setConcepta(BigDecimal concepta) {
		this.concepta = concepta;
	}
	/**
	 * @return the rateValue
	 */
	public float getRateValue() {
		return rateValue;
	}
	/**
	 * @param rateValue the rateValue to set
	 */
	public void setRateValue(float rateValue) {
		this.rateValue = rateValue;
	}
	/**
	 * @return the validateAccount
	 */
	public String getValidateAccount() {
		return validateAccount;
	}
	/**
	 * @param validateAccount the validateAccount to set
	 */
	public void setValidateAccount(String validateAccount) {
		this.validateAccount = validateAccount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentLoanRequest [ReferenceNumberBranch="
				+ ReferenceNumberBranch + ", ReferenceNumber="
				+ ReferenceNumber + ", productid=" + productid
				+ ", currencyId=" + currencyId + ", userName=" + userName
				+ ", account=" + account + ", concept=" + concept
				+ ", productName=" + productName + ", ammount=" + ammount
				+ ", loanNumber=" + loanNumber + ", destProduct=" + destProduct
				+ ", loanCurrencyId=" + loanCurrencyId + ", entityId="
				+ entityId + ", productAbbreviation=" + productAbbreviation
				+ ", authorizationRequired=" + authorizationRequired
				+ ", thirdPartyAssociated=" + thirdPartyAssociated
				+ ", isThirdParty=" + isThirdParty + ", loanPaymentAmount="
				+ loanPaymentAmount + ", concepta=" + concepta + ", rateValue="
				+ rateValue + ", validateAccount=" + validateAccount
				+ ", creditAmount=" + creditAmount + ", causa=" + causa
				+ ", causaDes=" + causaDes + ", causaComi=" + causaComi
				+ ", sourceFunds=" + sourceFunds + ", useFunds=" + useFunds
				+ "]";
	}
 
	
	

}
