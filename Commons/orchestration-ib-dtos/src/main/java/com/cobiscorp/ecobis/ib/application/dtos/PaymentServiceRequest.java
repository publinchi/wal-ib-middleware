/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

/**
 * <!-- Autor: Baque H Jorge nombreClase : Se coloca el nombre de la clase java
 * tipoDato : Es un arreglo de tipo de datos ["String", "List", "int",...]
 * nombreAtributo : Es un arreglo que contiene los nombre de atributos
 * ["altura", "edad", "peso"] descripcionClase: Lleva una breve descripción de
 * la clase numeroAtributos : Numero total de atributos de [1,...n]-->
 * 
 * <script type="text/javascript"> var nombreClase = "PaymentServiceRequest";
 * var tipoDato = ["String",
 * "String","User","Product","Integer","BigDecimal","String"
 * ,"String","String","String"
 * ,"String","String","String","String","String","String"
 * ,"String","String","String"
 * ,"String","String","String","String","Integer","String"
 * ,"String","String","char","int","int"]; var nombreAtributo =
 * ["ReferenceNumberBranch"
 * ,"ReferenceNumber","inUser","inProduct","contractId","amount"
 * ,"documentType","documentId"
 * ,"ref1","ref2","ref3","ref4","ref5","ref6","ref7",
 * "ref8","ref9","ref10","ref11"
 * "ref12","needsQuery","contractName","thridPartyServiceKey"
 * ,"invoicingBaseId","interfaceType"
 * ,"categoryId","authorizationRequired","authorization"
 * ,"branchSSN","reference"]; var descripcionClase = "DTO de Aplicaci&oacute;n";
 * var numeroAtributos = 29; </script>
 * 
 * <table>
 * <tbody>
 * <tr>
 * <th colspan="2" bgcolor="#CCCCFF"><div>Nombre Clase: <script
 * type="text/javascript">document.writeln(nombreClase);</script></th>
 * </tr>
 * <tr>
 * <td colspan="2"><div>Atributos</div></td>
 * </tr>
 * <tr>
 * <td width="auto" bgcolor="#CCCCFF"><div>Tipo de Dato</div></td>
 * <td width="auto" bgcolor="#CCCCFF"><div>Nombre</div></td>
 * </tr>
 * <tr>
 * <td style="font-family:'Courier New', Courier, monospace; color:#906;"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroAtributos;i++){
 * document.write(tipoDato[i]); document.write("<br />
 * "); }</script></td>
 * <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroAtributos;i++){
 * document.write(nombreAtributo[i]); document.write("<br />
 * "); }</script></td>
 * </tr>
 * 
 * <tr>
 * <td>Descripci&oacute;n:</td>
 * <td><script
 * type="text/javascript">document.writeln(descripcionClase);</script></td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author kmeza
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class PaymentServiceRequest extends BaseRequest {
	private String ReferenceNumberBranch;
	private String ReferenceNumber;
	private User inUser;
	private Product inProduct;

	private Integer contractId;
	private BigDecimal amount;
	private String documentType;
	private String documentId;
	private String ref1;
	private String ref2;
	private String ref3;
	private String ref4;
	private String ref5;
	private String ref6;
	private String ref7;
	private String ref8;
	private String ref9;
	private String ref10;
	private String ref11;
	private String ref12;
	private String needsQuery;
	private String contractName;
	private String thridPartyServiceKey;
	private Integer invoicingBaseId;
	private String interfaceType;
	private String categoryId;
	private String causaComi;

	private String authorizationRequired;
	private char authorization;
	private int branchSSN;
	private int reference;
	private String causa;
	private Double costo;
	private int paymentcurrency;
	private String fundsSource;
	private int idClient;
	private Double value;
	private String currency;
	private String fundsUse;
	private String fecha;

	private String codeLine;

	/**
	 * @return the codeLine
	 */
	public String getCodeLine() {
		return codeLine;
	}

	/**
	 * @param codeLine
	 *            the codeLine to set
	 */
	public void setCodeLine(String codeLine) {
		this.codeLine = codeLine;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the fundsSource
	 */
	public String getFundsSource() {
		return fundsSource;
	}

	/**
	 * @param fundsSource
	 *            the fundsSource to set
	 */
	public void setFundsSource(String fundsSource) {
		this.fundsSource = fundsSource;
	}

	/**
	 * @return the fundsUse
	 */
	public String getFundsUse() {
		return fundsUse;
	}

	/**
	 * @param fundsUse
	 *            the fundsUse to set
	 */
	public void setFundsUse(String fundsUse) {
		this.fundsUse = fundsUse;
	}

	/**
	 * @return the idClient
	 */
	public int getIdClient() {
		return idClient;
	}

	/**
	 * @param idClient
	 *            the idClient to set
	 */
	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the paymentcurrency
	 */
	public int getPaymentcurrency() {
		return paymentcurrency;
	}

	/**
	 * @param paymentcurrency
	 *            the paymentcurrency to set
	 */
	public void setPaymentcurrency(int paymentcurrency) {
		this.paymentcurrency = paymentcurrency;
	}

	/**
	 * @return the causa
	 */
	public String getCausa() {
		return causa;
	}

	/**
	 * @param causa
	 *            the causa to set
	 */
	public void setCausa(String causa) {
		this.causa = causa;
	}

	/**
	 * @return the causaComi
	 */
	public String getCausaComi() {
		return causaComi;
	}

	/**
	 * @param causaComi
	 *            the causaComi to set
	 */
	public void setCausaComi(String causaComi) {
		this.causaComi = causaComi;
	}

	/**
	 * @return the costo
	 */
	public Double getCosto() {
		return costo;
	}

	/**
	 * @param costo
	 *            the costo to set
	 */
	public void setCosto(Double costo) {
		this.costo = costo;
	}

	/**
	 * @return the inUser
	 */
	public User getInUser() {
		return inUser;
	}

	/**
	 * @param inUser
	 *            the inUser to set
	 */
	public void setInUser(User inUser) {
		this.inUser = inUser;
	}

	/**
	 * @return the inProduct
	 */
	public Product getInProduct() {
		return inProduct;
	}

	/**
	 * @param inProduct
	 *            the inProduct to set
	 */
	public void setInProduct(Product inProduct) {
		this.inProduct = inProduct;
	}

	/**
	 * @return the contractId
	 */
	public Integer getContractId() {
		return contractId;
	}

	/**
	 * @param contractId
	 *            the contractId to set
	 */
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType
	 *            the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * @param documentId
	 *            the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * @return the ref1
	 */
	public String getRef1() {
		return ref1;
	}

	/**
	 * @param ref1
	 *            the ref1 to set
	 */
	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}

	/**
	 * @return the ref2
	 */
	public String getRef2() {
		return ref2;
	}

	/**
	 * @param ref2
	 *            the ref2 to set
	 */
	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}

	/**
	 * @return the ref3
	 */
	public String getRef3() {
		return ref3;
	}

	/**
	 * @param ref3
	 *            the ref3 to set
	 */
	public void setRef3(String ref3) {
		this.ref3 = ref3;
	}

	/**
	 * @return the ref4
	 */
	public String getRef4() {
		return ref4;
	}

	/**
	 * @param ref4
	 *            the ref4 to set
	 */
	public void setRef4(String ref4) {
		this.ref4 = ref4;
	}

	/**
	 * @return the ref5
	 */
	public String getRef5() {
		return ref5;
	}

	/**
	 * @param ref5
	 *            the ref5 to set
	 */
	public void setRef5(String ref5) {
		this.ref5 = ref5;
	}

	/**
	 * @return the ref6
	 */
	public String getRef6() {
		return ref6;
	}

	/**
	 * @param ref6
	 *            the ref6 to set
	 */
	public void setRef6(String ref6) {
		this.ref6 = ref6;
	}

	/**
	 * @return the ref7
	 */
	public String getRef7() {
		return ref7;
	}

	/**
	 * @param ref7
	 *            the ref7 to set
	 */
	public void setRef7(String ref7) {
		this.ref7 = ref7;
	}

	/**
	 * @return the ref8
	 */
	public String getRef8() {
		return ref8;
	}

	/**
	 * @param ref8
	 *            the ref8 to set
	 */
	public void setRef8(String ref8) {
		this.ref8 = ref8;
	}

	/**
	 * @return the ref9
	 */
	public String getRef9() {
		return ref9;
	}

	/**
	 * @param ref9
	 *            the ref9 to set
	 */
	public void setRef9(String ref9) {
		this.ref9 = ref9;
	}

	/**
	 * @return the ref10
	 */
	public String getRef10() {
		return ref10;
	}

	/**
	 * @param ref10
	 *            the ref10 to set
	 */
	public void setRef10(String ref10) {
		this.ref10 = ref10;
	}

	/**
	 * @return the ref11
	 */
	public String getRef11() {
		return ref11;
	}

	/**
	 * @param ref11
	 *            the ref11 to set
	 */
	public void setRef11(String ref11) {
		this.ref11 = ref11;
	}

	/**
	 * @return the ref12
	 */
	public String getRef12() {
		return ref12;
	}

	/**
	 * @param ref12
	 *            the ref12 to set
	 */
	public void setRef12(String ref12) {
		this.ref12 = ref12;
	}

	/**
	 * @return the needsQuery
	 */
	public String getNeedsQuery() {
		return needsQuery;
	}

	/**
	 * @param needsQuery
	 *            the needsQuery to set
	 */
	public void setNeedsQuery(String needsQuery) {
		this.needsQuery = needsQuery;
	}

	/**
	 * @return the contractName
	 */
	public String getContractName() {
		return contractName;
	}

	/**
	 * @param contractName
	 *            the contractName to set
	 */
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	/**
	 * @return the thridPartyServiceKey
	 */
	public String getThridPartyServiceKey() {
		return thridPartyServiceKey;
	}

	/**
	 * @param thridPartyServiceKey
	 *            the thridPartyServiceKey to set
	 */
	public void setThridPartyServiceKey(String thridPartyServiceKey) {
		this.thridPartyServiceKey = thridPartyServiceKey;
	}

	/**
	 * @return the invoicingBaseId
	 */
	public Integer getInvoicingBaseId() {
		return invoicingBaseId;
	}

	/**
	 * @param invoicingBaseId
	 *            the invoicingBaseId to set
	 */
	public void setInvoicingBaseId(Integer invoicingBaseId) {
		this.invoicingBaseId = invoicingBaseId;
	}

	/**
	 * @return the interface_type
	 */
	public String getInterfaceType() {
		return interfaceType;
	}

	/**
	 * @param interfaceType
	 *            the interface_type to set
	 */
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	/**
	 * @return the authorizationRequired
	 */
	public String getAuthorizationRequired() {
		return authorizationRequired;
	}

	/**
	 * @param authorizationRequired
	 *            the authorizationRequired to set
	 */
	public void setAuthorizationRequired(String authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}

	/**
	 * @return the authorization
	 */
	public char getAuthorization() {
		return authorization;
	}

	/**
	 * @param authorization
	 *            the authorization to set
	 */
	public void setAuthorization(char authorization) {
		this.authorization = authorization;
	}

	/**
	 * @return the branchSSN
	 */
	public int getBranchSSN() {
		return branchSSN;
	}

	/**
	 * @param branchSSN
	 *            the branchSSN to set
	 */
	public void setBranchSSN(int branchSSN) {
		this.branchSSN = branchSSN;
	}

	/**
	 * @return the reference
	 */
	public int getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(int reference) {
		this.reference = reference;
	}

	/**
	 * @return the referenceNumberBranch
	 */
	public String getReferenceNumberBranch() {
		return ReferenceNumberBranch;
	}

	/**
	 * @param referenceNumberBranch
	 *            the referenceNumberBranch to set
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
	 * @param referenceNumber
	 *            the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		ReferenceNumber = referenceNumber;
	}

	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentServiceRequest [ReferenceNumberBranch="
				+ ReferenceNumberBranch + ", ReferenceNumber="
				+ ReferenceNumber + ", inUser=" + inUser + ", inProduct="
				+ inProduct + ", contractId=" + contractId + ", amount="
				+ amount + ", documentType=" + documentType + ", documentId="
				+ documentId + ", ref1=" + ref1 + ", ref2=" + ref2 + ", ref3="
				+ ref3 + ", ref4=" + ref4 + ", ref5=" + ref5 + ", ref6=" + ref6
				+ ", ref7=" + ref7 + ", ref8=" + ref8 + ", ref9=" + ref9
				+ ", ref10=" + ref10 + ", ref11=" + ref11 + ", ref12=" + ref12
				+ ", needsQuery=" + needsQuery + ", contractName="
				+ contractName + ", thridPartyServiceKey="
				+ thridPartyServiceKey + ", invoicingBaseId=" + invoicingBaseId
				+ ", interfaceType=" + interfaceType + ", categoryId="
				+ categoryId + ", causaComi=" + causaComi
				+ ", authorizationRequired=" + authorizationRequired
				+ ", authorization=" + authorization + ", branchSSN="
				+ branchSSN + ", reference=" + reference + ", causa=" + causa
				+ ", costo=" + costo + ", paymentcurrency=" + paymentcurrency
				+ ", codeLine=" + codeLine + ", idClient=" + idClient
				+ ", value=" + value + ", currency=" + currency + ", fecha="
				+ fecha + "]";
	}

}
