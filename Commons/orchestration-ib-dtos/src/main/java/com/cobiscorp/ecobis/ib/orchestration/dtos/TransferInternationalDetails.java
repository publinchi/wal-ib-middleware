/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author itorres
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class TransferInternationalDetails {
	private String dateTransaction;
	private String idReference;
	private String accountDebit;
	private String accountType;
	private String accountName;
	private Double ammount;
	private String money;
	private String referency;
	private String beneficiaryName;
	private String beneficiaryAddressComplete;
	private String beneficiaryCountry;
	private String beneficiaryCity;
	private String beneficiaryAddress;
	private String beneficiaryAccount;
	private String bankBeneficiaryCountry;
	private String bankBeneficiaryName;
	private String bankBeneficiaryDescription;
	private String bankBeneficiaryAddress;
	private String bankBeneficiarySwift;
	private String typeAddress;
	private String bankIntermediaryCountry;
	private String bankIntermediaryName;
	private String bankIntermediaryDescription;
	private String bankIntermediaryAddress;
	private String bankIntermediarySwift;
	private String typeAddressIntermediary;
	private Double costTransaction;
	private String beneficiaryContinentCode;
	private String beneficiaryContinent;
	private String transactionCode;
	private String messageType;
	private Integer sucursalCode;
	private String sucursal;
	private Integer bankBeneficiaryId;
	private Integer beneficiaryCountryId;
	private Integer beneficiaryCityId;
	private String payerCity;
	private String payerName;
	private Integer id;
	private Integer benCountryId;
	private Integer benCityId;
	private String bcoSwiftBen;
	private String bcoSwiftInter;
	private Integer bcoPaisBen;
	private Integer bcoPaisInter;
	private Integer bcoBenId;
	private Integer bcoInterId;
	private Integer bcoDirBenId;
	private Integer bcoDirInterId;
	private String beneficiaryFirstLastName;
	private String beneficiarySecondLastName;
	private String beneficiaryBusinessName;
	private String beneficiaryTypeDocument;
	private String beneficiaryDocumentNumber;
	private Integer currencyIdUSD;
	private Double quote;
	private String beneficiaryTypeDocumentName;
	private Integer codeNegotiation;
	private String beneficiaryEmail1;
	private String beneficiaryEmail2;
	
	
	
	/**
	 * @return the dateTransaction
	 */
	public String getDateTransaction() {
		return dateTransaction;
	}



	/**
	 * @param dateTransaction the dateTransaction to set
	 */
	public void setDateTransaction(String dateTransaction) {
		this.dateTransaction = dateTransaction;
	}



	/**
	 * @return the idReference
	 */
	public String getIdReference() {
		return idReference;
	}



	/**
	 * @param idReference the idReference to set
	 */
	public void setIdReference(String idReference) {
		this.idReference = idReference;
	}



	/**
	 * @return the accountDebit
	 */
	public String getAccountDebit() {
		return accountDebit;
	}



	/**
	 * @param accountDebit the accountDebit to set
	 */
	public void setAccountDebit(String accountDebit) {
		this.accountDebit = accountDebit;
	}



	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}



	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}



	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}



	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}



	/**
	 * @return the ammount
	 */
	public Double getAmmount() {
		return ammount;
	}



	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(Double ammount) {
		this.ammount = ammount;
	}



	/**
	 * @return the money
	 */
	public String getMoney() {
		return money;
	}



	/**
	 * @param money the money to set
	 */
	public void setMoney(String money) {
		this.money = money;
	}



	/**
	 * @return the referency
	 */
	public String getReferency() {
		return referency;
	}



	/**
	 * @param referency the referency to set
	 */
	public void setReferency(String referency) {
		this.referency = referency;
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
	 * @return the beneficiaryAddressComplete
	 */
	public String getBeneficiaryAddressComplete() {
		return beneficiaryAddressComplete;
	}



	/**
	 * @param beneficiaryAddressComplete the beneficiaryAddressComplete to set
	 */
	public void setBeneficiaryAddressComplete(String beneficiaryAddressComplete) {
		this.beneficiaryAddressComplete = beneficiaryAddressComplete;
	}



	/**
	 * @return the beneficiaryCountry
	 */
	public String getBeneficiaryCountry() {
		return beneficiaryCountry;
	}



	/**
	 * @param beneficiaryCountry the beneficiaryCountry to set
	 */
	public void setBeneficiaryCountry(String beneficiaryCountry) {
		this.beneficiaryCountry = beneficiaryCountry;
	}



	/**
	 * @return the beneficiaryCity
	 */
	public String getBeneficiaryCity() {
		return beneficiaryCity;
	}



	/**
	 * @param beneficiaryCity the beneficiaryCity to set
	 */
	public void setBeneficiaryCity(String beneficiaryCity) {
		this.beneficiaryCity = beneficiaryCity;
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
	 * @return the beneficiaryAccount
	 */
	public String getBeneficiaryAccount() {
		return beneficiaryAccount;
	}



	/**
	 * @param beneficiaryAccount the beneficiaryAccount to set
	 */
	public void setBeneficiaryAccount(String beneficiaryAccount) {
		this.beneficiaryAccount = beneficiaryAccount;
	}



	/**
	 * @return the bankBeneficiaryCountry
	 */
	public String getBankBeneficiaryCountry() {
		return bankBeneficiaryCountry;
	}



	/**
	 * @param bankBeneficiaryCountry the bankBeneficiaryCountry to set
	 */
	public void setBankBeneficiaryCountry(String bankBeneficiaryCountry) {
		this.bankBeneficiaryCountry = bankBeneficiaryCountry;
	}



	/**
	 * @return the bankBeneficiaryName
	 */
	public String getBankBeneficiaryName() {
		return bankBeneficiaryName;
	}



	/**
	 * @param bankBeneficiaryName the bankBeneficiaryName to set
	 */
	public void setBankBeneficiaryName(String bankBeneficiaryName) {
		this.bankBeneficiaryName = bankBeneficiaryName;
	}



	/**
	 * @return the bankBeneficiaryDescription
	 */
	public String getBankBeneficiaryDescription() {
		return bankBeneficiaryDescription;
	}



	/**
	 * @param bankBeneficiaryDescription the bankBeneficiaryDescription to set
	 */
	public void setBankBeneficiaryDescription(String bankBeneficiaryDescription) {
		this.bankBeneficiaryDescription = bankBeneficiaryDescription;
	}



	/**
	 * @return the bankBeneficiaryAddress
	 */
	public String getBankBeneficiaryAddress() {
		return bankBeneficiaryAddress;
	}



	/**
	 * @param bankBeneficiaryAddress the bankBeneficiaryAddress to set
	 */
	public void setBankBeneficiaryAddress(String bankBeneficiaryAddress) {
		this.bankBeneficiaryAddress = bankBeneficiaryAddress;
	}



	/**
	 * @return the bankBeneficiarySwift
	 */
	public String getBankBeneficiarySwift() {
		return bankBeneficiarySwift;
	}



	/**
	 * @param bankBeneficiarySwift the bankBeneficiarySwift to set
	 */
	public void setBankBeneficiarySwift(String bankBeneficiarySwift) {
		this.bankBeneficiarySwift = bankBeneficiarySwift;
	}



	/**
	 * @return the typeAddress
	 */
	public String getTypeAddress() {
		return typeAddress;
	}



	/**
	 * @param typeAddress the typeAddress to set
	 */
	public void setTypeAddress(String typeAddress) {
		this.typeAddress = typeAddress;
	}



	/**
	 * @return the bankIntermediaryCountry
	 */
	public String getBankIntermediaryCountry() {
		return bankIntermediaryCountry;
	}



	/**
	 * @param bankIntermediaryCountry the bankIntermediaryCountry to set
	 */
	public void setBankIntermediaryCountry(String bankIntermediaryCountry) {
		this.bankIntermediaryCountry = bankIntermediaryCountry;
	}



	/**
	 * @return the bankIntermediaryName
	 */
	public String getBankIntermediaryName() {
		return bankIntermediaryName;
	}



	/**
	 * @param bankIntermediaryName the bankIntermediaryName to set
	 */
	public void setBankIntermediaryName(String bankIntermediaryName) {
		this.bankIntermediaryName = bankIntermediaryName;
	}



	/**
	 * @return the bankIntermediaryDescription
	 */
	public String getBankIntermediaryDescription() {
		return bankIntermediaryDescription;
	}



	/**
	 * @param bankIntermediaryDescription the bankIntermediaryDescription to set
	 */
	public void setBankIntermediaryDescription(String bankIntermediaryDescription) {
		this.bankIntermediaryDescription = bankIntermediaryDescription;
	}



	/**
	 * @return the bankIntermediaryAddress
	 */
	public String getBankIntermediaryAddress() {
		return bankIntermediaryAddress;
	}



	/**
	 * @param bankIntermediaryAddress the bankIntermediaryAddress to set
	 */
	public void setBankIntermediaryAddress(String bankIntermediaryAddress) {
		this.bankIntermediaryAddress = bankIntermediaryAddress;
	}



	/**
	 * @return the bankIntermediarySwift
	 */
	public String getBankIntermediarySwift() {
		return bankIntermediarySwift;
	}



	/**
	 * @param bankIntermediarySwift the bankIntermediarySwift to set
	 */
	public void setBankIntermediarySwift(String bankIntermediarySwift) {
		this.bankIntermediarySwift = bankIntermediarySwift;
	}



	/**
	 * @return the typeAddressIntermediary
	 */
	public String getTypeAddressIntermediary() {
		return typeAddressIntermediary;
	}



	/**
	 * @param typeAddressIntermediary the typeAddressIntermediary to set
	 */
	public void setTypeAddressIntermediary(String typeAddressIntermediary) {
		this.typeAddressIntermediary = typeAddressIntermediary;
	}



	/**
	 * @return the costTransaction
	 */
	public Double getCostTransaction() {
		return costTransaction;
	}



	/**
	 * @param costTransaction the costTransaction to set
	 */
	public void setCostTransaction(Double costTransaction) {
		this.costTransaction = costTransaction;
	}



	/**
	 * @return the beneficiaryContinentCode
	 */
	public String getBeneficiaryContinentCode() {
		return beneficiaryContinentCode;
	}



	/**
	 * @param beneficiaryContinentCode the beneficiaryContinentCode to set
	 */
	public void setBeneficiaryContinentCode(String beneficiaryContinentCode) {
		this.beneficiaryContinentCode = beneficiaryContinentCode;
	}



	/**
	 * @return the beneficiaryContinent
	 */
	public String getBeneficiaryContinent() {
		return beneficiaryContinent;
	}



	/**
	 * @param beneficiaryContinent the beneficiaryContinent to set
	 */
	public void setBeneficiaryContinent(String beneficiaryContinent) {
		this.beneficiaryContinent = beneficiaryContinent;
	}



	/**
	 * @return the transactionCode
	 */
	public String getTransactionCode() {
		return transactionCode;
	}



	/**
	 * @param transactionCode the transactionCode to set
	 */
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}



	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}



	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}



	/**
	 * @return the sucursalCode
	 */
	public Integer getSucursalCode() {
		return sucursalCode;
	}



	/**
	 * @param sucursalCode the sucursalCode to set
	 */
	public void setSucursalCode(Integer sucursalCode) {
		this.sucursalCode = sucursalCode;
	}



	/**
	 * @return the sucursal
	 */
	public String getSucursal() {
		return sucursal;
	}



	/**
	 * @param sucursal the sucursal to set
	 */
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}



	/**
	 * @return the bankBeneficiaryId
	 */
	public Integer getBankBeneficiaryId() {
		return bankBeneficiaryId;
	}



	/**
	 * @param bankBeneficiaryId the bankBeneficiaryId to set
	 */
	public void setBankBeneficiaryId(Integer bankBeneficiaryId) {
		this.bankBeneficiaryId = bankBeneficiaryId;
	}



	/**
	 * @return the beneficiaryCountryId
	 */
	public Integer getBeneficiaryCountryId() {
		return beneficiaryCountryId;
	}



	/**
	 * @param beneficiaryCountryId the beneficiaryCountryId to set
	 */
	public void setBeneficiaryCountryId(Integer beneficiaryCountryId) {
		this.beneficiaryCountryId = beneficiaryCountryId;
	}



	/**
	 * @return the beneficiaryCityId
	 */
	public Integer getBeneficiaryCityId() {
		return beneficiaryCityId;
	}



	/**
	 * @param beneficiaryCityId the beneficiaryCityId to set
	 */
	public void setBeneficiaryCityId(Integer beneficiaryCityId) {
		this.beneficiaryCityId = beneficiaryCityId;
	}



	/**
	 * @return the payerCity
	 */
	public String getPayerCity() {
		return payerCity;
	}



	/**
	 * @param payerCity the payerCity to set
	 */
	public void setPayerCity(String payerCity) {
		this.payerCity = payerCity;
	}



	/**
	 * @return the payerName
	 */
	public String getPayerName() {
		return payerName;
	}



	/**
	 * @param payerName the payerName to set
	 */
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}



	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}



	/**
	 * @return the benCountryId
	 */
	public Integer getBenCountryId() {
		return benCountryId;
	}



	/**
	 * @param benCountryId the benCountryId to set
	 */
	public void setBenCountryId(Integer benCountryId) {
		this.benCountryId = benCountryId;
	}



	/**
	 * @return the benCityId
	 */
	public Integer getBenCityId() {
		return benCityId;
	}



	/**
	 * @param benCityId the benCityId to set
	 */
	public void setBenCityId(Integer benCityId) {
		this.benCityId = benCityId;
	}



	/**
	 * @return the bcoSwiftBen
	 */
	public String getBcoSwiftBen() {
		return bcoSwiftBen;
	}



	/**
	 * @param bcoSwiftBen the bcoSwiftBen to set
	 */
	public void setBcoSwiftBen(String bcoSwiftBen) {
		this.bcoSwiftBen = bcoSwiftBen;
	}



	/**
	 * @return the bcoSwiftInter
	 */
	public String getBcoSwiftInter() {
		return bcoSwiftInter;
	}



	/**
	 * @param bcoSwiftInter the bcoSwiftInter to set
	 */
	public void setBcoSwiftInter(String bcoSwiftInter) {
		this.bcoSwiftInter = bcoSwiftInter;
	}



	/**
	 * @return the bcoPaisBen
	 */
	public Integer getBcoPaisBen() {
		return bcoPaisBen;
	}



	/**
	 * @param bcoPaisBen the bcoPaisBen to set
	 */
	public void setBcoPaisBen(Integer bcoPaisBen) {
		this.bcoPaisBen = bcoPaisBen;
	}



	/**
	 * @return the bcoPaisInter
	 */
	public Integer getBcoPaisInter() {
		return bcoPaisInter;
	}



	/**
	 * @param bcoPaisInter the bcoPaisInter to set
	 */
	public void setBcoPaisInter(Integer bcoPaisInter) {
		this.bcoPaisInter = bcoPaisInter;
	}



	/**
	 * @return the bcoBenId
	 */
	public Integer getBcoBenId() {
		return bcoBenId;
	}



	/**
	 * @param bcoBenId the bcoBenId to set
	 */
	public void setBcoBenId(Integer bcoBenId) {
		this.bcoBenId = bcoBenId;
	}



	/**
	 * @return the bcoInterId
	 */
	public Integer getBcoInterId() {
		return bcoInterId;
	}



	/**
	 * @param bcoInterId the bcoInterId to set
	 */
	public void setBcoInterId(Integer bcoInterId) {
		this.bcoInterId = bcoInterId;
	}



	/**
	 * @return the bcoDirBenId
	 */
	public Integer getBcoDirBenId() {
		return bcoDirBenId;
	}



	/**
	 * @param bcoDirBenId the bcoDirBenId to set
	 */
	public void setBcoDirBenId(Integer bcoDirBenId) {
		this.bcoDirBenId = bcoDirBenId;
	}



	/**
	 * @return the bcoDirInterId
	 */
	public Integer getBcoDirInterId() {
		return bcoDirInterId;
	}



	/**
	 * @param bcoDirInterId the bcoDirInterId to set
	 */
	public void setBcoDirInterId(Integer bcoDirInterId) {
		this.bcoDirInterId = bcoDirInterId;
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
	 * @return the beneficiaryTypeDocument
	 */
	public String getBeneficiaryTypeDocument() {
		return beneficiaryTypeDocument;
	}



	/**
	 * @param beneficiaryTypeDocument the beneficiaryTypeDocument to set
	 */
	public void setBeneficiaryTypeDocument(String beneficiaryTypeDocument) {
		this.beneficiaryTypeDocument = beneficiaryTypeDocument;
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
	 * @return the currencyIdUSD
	 */
	public Integer getCurrencyIdUSD() {
		return currencyIdUSD;
	}



	/**
	 * @param currencyIdUSD the currencyIdUSD to set
	 */
	public void setCurrencyIdUSD(Integer currencyIdUSD) {
		this.currencyIdUSD = currencyIdUSD;
	}



	/**
	 * @return the quote
	 */
	public Double getQuote() {
		return quote;
	}



	/**
	 * @param quote the quote to set
	 */
	public void setQuote(Double quote) {
		this.quote = quote;
	}



	/**
	 * @return the beneficiaryTypeDocumentName
	 */
	public String getBeneficiaryTypeDocumentName() {
		return beneficiaryTypeDocumentName;
	}



	/**
	 * @param beneficiaryTypeDocumentName the beneficiaryTypeDocumentName to set
	 */
	public void setBeneficiaryTypeDocumentName(String beneficiaryTypeDocumentName) {
		this.beneficiaryTypeDocumentName = beneficiaryTypeDocumentName;
	}



	/**
	 * @return the codeNegotiation
	 */
	public Integer getCodeNegotiation() {
		return codeNegotiation;
	}



	/**
	 * @param codeNegotiation the codeNegotiation to set
	 */
	public void setCodeNegotiation(Integer codeNegotiation) {
		this.codeNegotiation = codeNegotiation;
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



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransferInternationalDetails [dateTransaction="
				+ dateTransaction + ", idReference=" + idReference
				+ ", accountDebit=" + accountDebit + ", accountType="
				+ accountType + ", accountName=" + accountName + ", ammount="
				+ ammount + ", money=" + money + ", referency=" + referency
				+ ", beneficiaryName=" + beneficiaryName
				+ ", beneficiaryAddressComplete=" + beneficiaryAddressComplete
				+ ", beneficiaryCountry=" + beneficiaryCountry
				+ ", beneficiaryCity=" + beneficiaryCity
				+ ", beneficiaryAddress=" + beneficiaryAddress
				+ ", beneficiaryAccount=" + beneficiaryAccount
				+ ", bankBeneficiaryCountry=" + bankBeneficiaryCountry
				+ ", bankBeneficiaryName=" + bankBeneficiaryName
				+ ", bankBeneficiaryDescription=" + bankBeneficiaryDescription
				+ ", bankBeneficiaryAddress=" + bankBeneficiaryAddress
				+ ", bankBeneficiarySwift=" + bankBeneficiarySwift
				+ ", typeAddress=" + typeAddress + ", bankIntermediaryCountry="
				+ bankIntermediaryCountry + ", bankIntermediaryName="
				+ bankIntermediaryName + ", bankIntermediaryDescription="
				+ bankIntermediaryDescription + ", bankIntermediaryAddress="
				+ bankIntermediaryAddress + ", bankIntermediarySwift="
				+ bankIntermediarySwift + ", typeAddressIntermediary="
				+ typeAddressIntermediary + ", costTransaction="
				+ costTransaction + ", beneficiaryContinentCode="
				+ beneficiaryContinentCode + ", beneficiaryContinent="
				+ beneficiaryContinent + ", transactionCode=" + transactionCode
				+ ", messageType=" + messageType + ", sucursalCode="
				+ sucursalCode + ", sucursal=" + sucursal
				+ ", bankBeneficiaryId=" + bankBeneficiaryId
				+ ", beneficiaryCountryId=" + beneficiaryCountryId
				+ ", beneficiaryCityId=" + beneficiaryCityId + ", payerCity="
				+ payerCity + ", payerName=" + payerName + ", id=" + id
				+ ", benCountryId=" + benCountryId + ", benCityId=" + benCityId
				+ ", bcoSwiftBen=" + bcoSwiftBen + ", bcoSwiftInter="
				+ bcoSwiftInter + ", bcoPaisBen=" + bcoPaisBen
				+ ", bcoPaisInter=" + bcoPaisInter + ", bcoBenId=" + bcoBenId
				+ ", bcoInterId=" + bcoInterId + ", bcoDirBenId=" + bcoDirBenId
				+ ", bcoDirInterId=" + bcoDirInterId
				+ ", beneficiaryFirstLastName=" + beneficiaryFirstLastName
				+ ", beneficiarySecondLastName=" + beneficiarySecondLastName
				+ ", beneficiaryBusinessName=" + beneficiaryBusinessName
				+ ", beneficiaryTypeDocument=" + beneficiaryTypeDocument
				+ ", beneficiaryDocumentNumber=" + beneficiaryDocumentNumber
				+ ", currencyIdUSD=" + currencyIdUSD + ", quote=" + quote
				+ ", beneficiaryTypeDocumentName="
				+ beneficiaryTypeDocumentName + ", codeNegotiation="
				+ codeNegotiation + ", beneficiaryEmail1=" + beneficiaryEmail1
				+ ", beneficiaryEmail2=" + beneficiaryEmail2 + "]";
	}	
	
}
