package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * contain information about bank
 *
 * @author eortega
 * @since september 23, 2014
 * @version 1.0.0
 */
public class AccountStatement {

	/**
	 * Indicate the process of the transaction
	 */
	private String account;
	
	
	private Date transactionDate;
	/**
	 * Indicate the description of the transaction
	 */
	private String description;
	/**
	 * Indicate the type of the transaction
	 */
	private Integer typeOperation;
	/**
	 * Indicate the id of the transaction
	 */
	private String reference;
	/**
	 * Indicate if the transaction was a debit or credit
	 */
	private String typeTransaction;
	/**
	 * Indicate the amount of the account
	 */
	private BigDecimal amount;
	private BigDecimal debitsAmount;
	private BigDecimal creditsAmount;
	private String     signDC;
	private String     causeId;
	private String     cause;
	private String     concept;
	private String     stringDate;
	private Integer operationType;
	private String documentNumber;
	private String image;
	private String internationalCheckBookBalance;
	private String localChecksBalance;
	private String office;
	private String ownChecksBalance;
	private String totalChecksBalance;
	private String typeDC;
	private String rastreo;
	private String tarjetNumber;
	
	private String montoTran;
	private String beneficiario;
	private String cuentaDest;
	private String cuentaOrig;
	private String comisionTran;
	private String ivaTran;
	private String mensajeTran;
	private String originAccountProp;
	private String currencySymbol;
	private String referenceNumber;
	private String destinationAccountType;
	private String originAccountType;
	private String bank;
	private String dataComprobante;
	
	public String getOriginAccountProp() {
		return originAccountProp;
	}

	public void setOriginAccountProp(String originAccountProp) {
		this.originAccountProp = originAccountProp;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getDestinationAccountType() {
		return destinationAccountType;
	}

	public void setDestinationAccountType(String destinationAccountType) {
		this.destinationAccountType = destinationAccountType;
	}

	public String getOriginAccountType() {
		return originAccountType;
	}

	public void setOriginAccountType(String originAccountType) {
		this.originAccountType = originAccountType;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
	
	public String getMontoTran() {
		return montoTran;
	}

	public void setMontoTran(String montoTran) {
		this.montoTran = montoTran;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getCuentaDest() {
		return cuentaDest;
	}

	public void setCuentaDest(String cuentaDest) {
		this.cuentaDest = cuentaDest;
	}

	public String getCuentaOrig() {
		return cuentaOrig;
	}

	public void setCuentaOrig(String cuentaOrig) {
		this.cuentaOrig = cuentaOrig;
	}

	public String getComisionTran() {
		return comisionTran;
	}

	public void setComisionTran(String comisionTran) {
		this.comisionTran = comisionTran;
	}

	public String getIvaTran() {
		return ivaTran;
	}

	public void setIvaTran(String ivaTran) {
		this.ivaTran = ivaTran;
	}

	public String getMensajeTran() {
		return mensajeTran;
	}

	public void setMensajeTran(String mensajeTran) {
		this.mensajeTran = mensajeTran;
	}

	/**
	 * Indicate the accounting Balance of the account
	 */
	private BigDecimal accountingBalance;
	/**
	 * Indicate the available Balance of the account
	 */
	private BigDecimal availableBalance;
	/**
	 * Indicate
	 */
	private Integer sequential;
	/**
	 * Indicate
	 */
	private Integer alternateCode;
	/**
	 * Indicate the hour of the transaction
	 */
	private String hour;
	/**
	 * Indicate the process of the transaction
	 */
	private Integer uniqueSequential;
	

	/**
	 * @return the operationType
	 */
	public Integer getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTypeOperation() {
		return typeOperation;
	}

	public void setTypeOperation(Integer typeOperation) {
		this.typeOperation = typeOperation;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getTypeTransaction() {
		return typeTransaction;
	}

	public void setTypeTransaction(String typeTransaction) {
		this.typeTransaction = typeTransaction;
	}

	public BigDecimal getAccountingBalance() {
		return accountingBalance;
	}

	public void setAccountingBalance(BigDecimal accountingBalance) {
		this.accountingBalance = accountingBalance;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Integer getSequential() {
		return sequential;
	}

	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}

	public Integer getAlternateCode() {
		return alternateCode;
	}

	public void setAlternateCode(Integer alternateCode) {
		this.alternateCode = alternateCode;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public Integer getUniqueSequential() {
		return uniqueSequential;
	}

	public void setUniqueSequential(Integer uniqueSequential) {
		this.uniqueSequential = uniqueSequential;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the debitsAmount
	 */
	public BigDecimal getDebitsAmount() {
		return debitsAmount;
	}

	/**
	 * @param debitsAmount the debitsAmount to set
	 */
	public void setDebitsAmount(BigDecimal debitsAmount) {
		this.debitsAmount = debitsAmount;
	}

	/**
	 * @return the creditsAmount
	 */
	public BigDecimal getCreditsAmount() {
		return creditsAmount;
	}

	/**
	 * @param creditsAmount the creditsAmount to set
	 */
	public void setCreditsAmount(BigDecimal creditsAmount) {
		this.creditsAmount = creditsAmount;
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
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * @param cause the cause to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
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
	 * @return the documentNumber
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}

	/**
	 * @param documentNumber the documentNumber to set
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the internationalCheckBookBalance
	 */
	public String getInternationalCheckBookBalance() {
		return internationalCheckBookBalance;
	}

	/**
	 * @param internationalCheckBookBalance the internationalCheckBookBalance to set
	 */
	public void setInternationalCheckBookBalance(
			String internationalCheckBookBalance) {
		this.internationalCheckBookBalance = internationalCheckBookBalance;
	}

	/**
	 * @return the localChecksBalance
	 */
	public String getLocalChecksBalance() {
		return localChecksBalance;
	}

	/**
	 * @param localChecksBalance the localChecksBalance to set
	 */
	public void setLocalChecksBalance(String localChecksBalance) {
		this.localChecksBalance = localChecksBalance;
	}

	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}

	/**
	 * @param office the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}

	/**
	 * @return the ownChecksBalance
	 */
	public String getOwnChecksBalance() {
		return ownChecksBalance;
	}

	/**
	 * @param ownChecksBalance the ownChecksBalance to set
	 */
	public void setOwnChecksBalance(String ownChecksBalance) {
		this.ownChecksBalance = ownChecksBalance;
	}

	/**
	 * @return the totalChecksBalance
	 */
	public String getTotalChecksBalance() {
		return totalChecksBalance;
	}

	/**
	 * @param totalChecksBalance the totalChecksBalance to set
	 */
	public void setTotalChecksBalance(String totalChecksBalance) {
		this.totalChecksBalance = totalChecksBalance;
	}

	/**
	 * @return the typeDC
	 */
	public String getTypeDC() {
		return typeDC;
	}

	/**
	 * @param typeDC the typeDC to set
	 */
	public void setTypeDC(String typeDC) {
		this.typeDC = typeDC;
	}

	/**
	 * @return the signDC
	 */
	public String getSignDC() {
		return signDC;
	}

	/**
	 * @param signDC the signDC to set
	 */
	public void setSignDC(String signDC) {
		this.signDC = signDC;
	}

	/**
	 * @return the causeId
	 */
	public String getCauseId() {
		return causeId;
	}

	/**
	 * @param causeId the causeId to set
	 */
	public void setCauseId(String causeId) {
		this.causeId = causeId;
	}

	/**
	 * @return the stringDate
	 */
	public String getStringDate() {
		return stringDate;
	}

	public String getRastreo() {
		return rastreo;
	}

	public void setRastreo(String rastreo) {
		this.rastreo = rastreo;
	}

	public String getTarjetNumber() {
		return tarjetNumber;
	}

	public void setTarjetNumber(String tarjetNumber) {
		this.tarjetNumber = tarjetNumber;
	}

	/**
	 * @param stringDate the stringDate to set
	 */
	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}
	
	public String getDataComprobante() {
		return this.dataComprobante;
	}
	public void setDataComprobante(String dataComprobante) {
		this.dataComprobante = dataComprobante;
	}
	

	@Override
	public String toString() {
		return "AccountStatement [account=" + account + ", transactionDate=" + transactionDate + ", description="
				+ description + ", typeOperation=" + typeOperation + ", reference=" + reference + ", typeTransaction="
				+ typeTransaction + ", amount=" + amount + ", debitsAmount=" + debitsAmount + ", creditsAmount="
				+ creditsAmount + ", signDC=" + signDC + ", causeId=" + causeId + ", cause=" + cause + ", concept="
				+ concept + ", stringDate=" + stringDate + ", operationType=" + operationType + ", documentNumber="
				+ documentNumber + ", image=" + image + ", internationalCheckBookBalance="
				+ internationalCheckBookBalance + ", localChecksBalance=" + localChecksBalance + ", office=" + office
				+ ", ownChecksBalance=" + ownChecksBalance + ", totalChecksBalance=" + totalChecksBalance + ", typeDC="
				+ typeDC + ", rastreo=" + rastreo + ", tarjetNumber=" + tarjetNumber + ", accountingBalance="
				+ accountingBalance + ", availableBalance=" + availableBalance + ", sequential=" + sequential
				+ ", alternateCode=" + alternateCode + ", hour=" + hour + ", uniqueSequential=" + uniqueSequential
				
				+ ", montoTran=" + montoTran + ", beneficiario=" + beneficiario + ", cuentaDest=" + cuentaDest
				+ ", cuentaOrig=" + cuentaOrig + ", comisionTran=" + comisionTran + ", ivaTran=" + ivaTran
				+ ", referenceNumber=" + referenceNumber + ", destinationAccountType=" + destinationAccountType + ", originAccountType=" + originAccountType
				+ ", montoTran=" + montoTran + ", beneficiario=" + beneficiario + ", cuentaDest=" + cuentaDest
				+ ", montoTran=" + montoTran + ", beneficiario=" + beneficiario + ", cuentaDest=" + cuentaDest
				+ ", bank=" + bank + ", dataComprobante=" + dataComprobante 
				
				+ "]";
	}

	
	

}
