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
	var nombreClase       = "InternationalTransferRequest";
	var tipoDato          = ["String ","String ","String ","String ","String ","String ",
							"String ","String ","String ","String ","String ","Integer",
							"Integer","String ","String ","Integer","String ","String ",
							"String ","String ","Integer","String ","BigDecimal","Integer",
							"String","BigDecimal","Integer","Integer","String"];
	var nombreAtributo    = ["beneficiaryIDType","beneficiaryIDNumber","beneficiaryName",
							"beneficiaryAddress","beneficiaryFirstLastName","beneficiarySecondLastName",
							"beneficiaryBusinessName","beneficiaryDocumentType","beneficiaryDocumentNumber",
							"beneficiaryEmail1","beneficiaryEmail2","beneficiaryCountryCode",
							"intermediaryBankCode","intermediaryBankName","intermediaryBankAddressType",
							"intermediaryBankOfficeCode","intermediaryBankSwiftAbaCode","beneficiaryBankCode",
							"beneficiaryBankName","beneficiaryBankSwiftAbaCode","beneficiaryBankOfficeCode",
							"beneficiaryBankAddressType","quote","negotiationCode","negotiationDate",
							"relationDollarC2","currency","accountCurrency","date"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 29;
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
public class InternationalTransferRequest extends TransferRequest{

	//Beneficiary Info
	private String beneficiaryIDType;
	private String beneficiaryIDNumber;
	private String beneficiaryName;
	private String beneficiaryAddress;
	private String beneficiaryFirstLastName;
	private String beneficiarySecondLastName;
	private String beneficiaryBusinessName;
	private String beneficiaryDocumentType;
	private String beneficiaryDocumentNumber;
	private String beneficiaryEmail1;
	private String beneficiaryEmail2;
	private Integer beneficiaryCountryCode;

	//Intermediary Bank Info	
	private Integer intermediaryBankCode;
	private String intermediaryBankName; 
	private String intermediaryBankAddressType;
	private Integer intermediaryBankOfficeCode;
	private String intermediaryBankSwiftAbaCode;
	
	//Beneficiary Bank Info
	private String beneficiaryBankCode;
	private String beneficiaryBankName;
	private String beneficiaryBankSwiftAbaCode;
	private Integer beneficiaryBankOfficeCode; 
	private String beneficiaryBankAddressType;
	
	private BigDecimal quote;
	private Integer negotiationCode;
	private String negotiationDate;
	private BigDecimal relationDollarC2;
	private Integer currency;
	private Integer accountCurrency;
	
	private String date;
	/**
	 * @return the currency
	 */
	public Integer getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Integer currency) {
		this.currency = currency;
	}
	/**
	 * @return the accountCurrency
	 */
	public Integer getAccountCurrency() {
		return accountCurrency;
	}
	/**
	 * @param accountCurrency the accountCurrency to set
	 */
	public void setAccountCurrency(Integer accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	
	
	
	/**
	 * @return the beneficiaryIDType
	 */
	public String getBeneficiaryIDType() {
		return beneficiaryIDType;
	}
	/**
	 * @param beneficiaryIDType the beneficiaryIDType to set
	 */
	public void setBeneficiaryIDType(String beneficiaryIDType) {
		this.beneficiaryIDType = beneficiaryIDType;
	}
	/**
	 * @return the beneficiaryIDNumber
	 */
	public String getBeneficiaryIDNumber() {
		return beneficiaryIDNumber;
	}
	/**
	 * @param beneficiaryIDNumber the beneficiaryIDNumber to set
	 */
	public void setBeneficiaryIDNumber(String beneficiaryIDNumber) {
		this.beneficiaryIDNumber = beneficiaryIDNumber;
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
	 * @return the beneficiaryAddress
	 */
	public String getBeneficiaryAddress() {
		return beneficiaryAddress;
	}
	/**
	 * @param beneficiaryAddress the beneficiaryAddress to set
	 */
	public void setBeneficiaryAddress(String beneficiaryAddress) {
		this.beneficiaryAddress = beneficiaryAddress;
	}
	
	/**
	 * @param beneficiaryCountryCode the beneficiaryCountryCode to set
	 */
	public void setBeneficiaryCountryCode(Integer beneficiaryCountryCode) {
		this.beneficiaryCountryCode = beneficiaryCountryCode;
	}
	
	/**
	 * @return the beneficiaryCountryCode
	 */
	public Integer getBeneficiaryCountryCode () {
		return beneficiaryCountryCode;
	}
	
	/**
	 * @return the beneficiaryBankCodeName
	 */
	public String getBeneficiaryBankName() {
		return beneficiaryBankName;
	}
	/**
	 * @param beneficiaryBankCodeName the beneficiaryBankCodeName to set
	 */
	public void setBeneficiaryBankName(String beneficiaryBankCodeName) {
		this.beneficiaryBankName = beneficiaryBankCodeName;
	}
	/**
	 * @return the swiftOrAba
	 */
	public String getBeneficiaryBankSwiftAbaCode() {
		return beneficiaryBankSwiftAbaCode;
	}
	/**
	 * @param swiftOrAba the swiftOrAba to set
	 */
	public void setBeneficiaryBankSwiftAbaCode(String beneficiaryBankSwiftAbaCode) {
		this.beneficiaryBankSwiftAbaCode = beneficiaryBankSwiftAbaCode;
	}
	/**
	 * @return the beneficiaryBankCodeOffice
	 */
	public Integer getBeneficiaryBankOfficeCode() {
		return beneficiaryBankOfficeCode;
	}
	/**
	 * @param beneficiaryBankOfficeCode the beneficiaryBankOfficeCode to set
	 */
	public void setBeneficiaryBankOfficeCode(Integer beneficiaryBankOfficeCode) {
		this.beneficiaryBankOfficeCode = beneficiaryBankOfficeCode;
	}
	/**
	 * @return the beneficiaryIsSwiftOrAba
	 */
	public String getBeneficiaryBankAddressType() {
		return beneficiaryBankAddressType;
	}
	/**
	 * @param beneficiaryIsSwiftOrAba the beneficiaryIsSwiftOrAba to set
	 */
	public void setBeneficiaryBankAddressType ( String beneficiaryBankAddressType) {
		this.beneficiaryBankAddressType = beneficiaryBankAddressType;
	}
	/**
	 * @return the intermediaryBankAddressType
	 */
	public String getIntermediaryBankAddressType() {
		return intermediaryBankAddressType;
	}
	/**
	 * @param intermediaryBankAddressType the intermediaryBankAddressType to set
	 */
	public void setIntermediaryBankAddressType(String intermediaryBankAddressType) {
		this.intermediaryBankAddressType = intermediaryBankAddressType;
	}
	/**
	 * @return the intermediaryBankCode
	 */
	public Integer getIntermediaryBankCode() {
		return intermediaryBankCode;
	}
	/**
	 * @param intermediaryBankCode the intermediaryBankCode to set
	 */
	public void setIntermediaryBankCode(Integer intermediaryBankCode) {
		this.intermediaryBankCode = intermediaryBankCode;
	}
	
	/**
	 * @return the intermediaryBankOfficeCode
	 */
	public Integer getIntermediaryBankOfficeCode() {
		return intermediaryBankOfficeCode;
	}
	/**
	 * @param intermediaryBankOfficeCode the intermediaryBankOfficeCode to set
	 */
	public void setIntermediaryBankOfficeCode(Integer intermediaryBankOfficeCode) {
		this.intermediaryBankOfficeCode = intermediaryBankOfficeCode;
	}
	/**
	 * @return the intermediaryBankSwiftAbaCode
	 */
	public String getIntermediaryBankSwiftAbaCode() {
		return intermediaryBankSwiftAbaCode;
	}
	/**
	 * @param intermediaryBankSwiftAbaCode the intermediaryBankSwiftAbaCode to set
	 */
	public void setIntermediaryBankSwiftAbaCode(String intermediaryBankSwiftAbaCode) {
		this.intermediaryBankSwiftAbaCode = intermediaryBankSwiftAbaCode;
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
	 * @return the beneficiaryBankCode
	 */
	public String getBeneficiaryBankCode() {
		return beneficiaryBankCode;
	}
	/**
	 * @param beneficiaryBankCode the beneficiaryBankCode to set
	 */
	public void setBeneficiaryBankCode(String beneficiaryBankCode) {
		this.beneficiaryBankCode = beneficiaryBankCode;
	}
	/**
	 * @return the beneficiaryFirstLastName
	 */
	public String getBeneficiaryFirstLastName() {
		return beneficiaryFirstLastName;
	}
	/**
	 * @param beneficiaryFirstLastName the beneficiaryFirstLastName to set
	 */
	public void setBeneficiaryFirstLastName(String beneficiaryFirstLastName) {
		this.beneficiaryFirstLastName = beneficiaryFirstLastName;
	}
	/**
	 * @return the beneficiarySecondLastName
	 */
	public String getBeneficiarySecondLastName() {
		return beneficiarySecondLastName;
	}
	/**
	 * @param beneficiarySecondLastName the beneficiarySecondLastName to set
	 */
	public void setBeneficiarySecondLastName(String beneficiarySecondLastName) {
		this.beneficiarySecondLastName = beneficiarySecondLastName;
	}
	/**
	 * @return the beneficiaryBusinessName
	 */
	public String getBeneficiaryBusinessName() {
		return beneficiaryBusinessName;
	}
	/**
	 * @param beneficiaryBusinessName the beneficiaryBusinessName to set
	 */
	public void setBeneficiaryBusinessName(String beneficiaryBusinessName) {
		this.beneficiaryBusinessName = beneficiaryBusinessName;
	}
	/**
	 * @return the beneficiaryDocumentType
	 */
	public String getbeneficiaryDocumentType() {
		return beneficiaryDocumentType;
	}
	/**
	 * @param beneficiaryDocumentType the beneficiaryDocumentType to set
	 */
	public void setBeneficiaryDocumentType(String beneficiaryDocumentType) {
		this.beneficiaryDocumentType = beneficiaryDocumentType;
	}
	/**
	 * @return the beneficiaryDocumentNumber
	 */
	public String getBeneficiaryDocumentNumber() {
		return beneficiaryDocumentNumber;
	}
	/**
	 * @param beneficiaryDocumentNumber the beneficiaryDocumentNumber to set
	 */
	public void setBeneficiaryDocumentNumber(String beneficiaryDocumentNumber) {
		this.beneficiaryDocumentNumber = beneficiaryDocumentNumber;
	}
	/**
	 * @return the quote
	 */
	public BigDecimal getQuote() {
		return quote;
	}
	/**
	 * @param quote the quote to set
	 */
	public void setQuote(BigDecimal quote) {
		this.quote = quote;
	}
	/**
	 * @return the negotiationCode
	 */
	public Integer getNegotiationCode() {
		return negotiationCode;
	}
	/**
	 * @param negotiationCode the negotiationCode to set
	 */
	public void setNegotiationCode(Integer negotiationCode) {
		this.negotiationCode = negotiationCode;
	}
	/**
	 * @return the beneficiaryEmail1
	 */
	public String getBeneficiaryEmail1() {
		return beneficiaryEmail1;
	}
	/**
	 * @param beneficiaryEmail1 the beneficiaryEmail1 to set
	 */
	public void setBeneficiaryEmail1(String beneficiaryEmail1) {
		this.beneficiaryEmail1 = beneficiaryEmail1;
	}
	/**
	 * @return the beneficiaryEmail2
	 */
	public String getBeneficiaryEmail2() {
		return beneficiaryEmail2;
	}
	/**
	 * @param beneficiaryEmail2 the beneficiaryEmail2 to set
	 */
	public void setBeneficiaryEmail2(String beneficiaryEmail2) {
		this.beneficiaryEmail2 = beneficiaryEmail2;
	}
	/**
	 * @return the negotiationDate
	 */
	public String getNegotiationDate() {
		return negotiationDate;
	}
	/**
	 * @param negotiationDate the negotiationDate to set
	 */
	public void setNegotiationDate(String negotiationDate) {
		this.negotiationDate = negotiationDate;
	}
	/**
	 * @return the relationDollarC2
	 */
	public BigDecimal getRelationDollarC2() {
		return relationDollarC2;
	}
	/**
	 * @param relationDollarC2 the relationDollarC2 to set
	 */
	public void setRelationDollarC2(BigDecimal relationDollarC2) {
		this.relationDollarC2 = relationDollarC2;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
}
