package com.cobiscorp.channels.cc.connector.dto;
/**
 * Class that define the response object in a query for showing a list movements detail
 * @author djarrin
 *
 */
public class Movement {
	private String account;
	private String cause;
	private String causeId;
	private String concept;
	private Double creditsAmount;
	private Double debitsAmount;
	private int alternateCode;
	private String description;
	private String reference;
	private Double amount;
	private Double accountingBalance;
	private Double availableBalance;
	private int documentNumber;
	private String processDate;
	private String transactionDate;
	private String hour;
	private String image;
	private int internationalChecksBalance;
	private int localChecksBalance;
	private int numberOfMovements;
	private int office;
	private int operationType;
	private int ownChecksBalance;
	private int sequential;
	private String signDC;
	private int totalChecksBalance;
	private String type;
	private String typeDC;
	private int uniqueSequential;

	public Movement (
			String account, String cause, String causeId, String concept, Double creditsAmount,
			Double debitsAmount, int alternateCode, String description, String reference, Double amount,
			Double accountingBalance, Double availableBalance, int documentNumber, String processDate, String transactionDate,
			String hour, String image, int internationalChecksBalance, int localChecksBalance, int numberOfMovements,
			int office, int operationType, int ownChecksBalance, int sequential, String signDC,
			int totalChecksBalance, String type, String typeDC, int uniqueSequential
			) {
		super();
		this.account = account;
		this.accountingBalance = accountingBalance;
		this.alternateCode = alternateCode;
		this.amount = amount;
		this.availableBalance = availableBalance;
		this.cause = cause;
		this.causeId = causeId;
		this.concept = concept; 
		this.creditsAmount = creditsAmount;
		this.debitsAmount = debitsAmount;
		this.description = description;
		this.documentNumber = documentNumber;
		this.hour = hour;
		this.image = image; 
		this.internationalChecksBalance = internationalChecksBalance;
		this.localChecksBalance = localChecksBalance;
		this.numberOfMovements = numberOfMovements;
		this.office = office;
		this.operationType = operationType;
		this.ownChecksBalance = ownChecksBalance;
		this.reference = reference;
		this.sequential = sequential;
		this.signDC = signDC;
		this.totalChecksBalance = totalChecksBalance;
		this.transactionDate = transactionDate;
		this.type = type;
		this.typeDC = typeDC;
		this.uniqueSequential = uniqueSequential;
		this.processDate = processDate;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getCauseId() {
		return causeId;
	}

	public void setCauseId(String causeId) {
		this.causeId = causeId;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Double getCreditsAmount() {
		return creditsAmount;
	}

	public void setCreditsAmount(Double creditsAmount) {
		this.creditsAmount = creditsAmount;
	}

	public Double getDebitsAmount() {
		return debitsAmount;
	}

	public void setDebitsAmount(Double debitsAmount) {
		this.debitsAmount = debitsAmount;
	}

	public int getAlternateCode() {
		return alternateCode;
	}

	public void setAlternateCode(int alternateCode) {
		this.alternateCode = alternateCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getAccountingBalance() {
		return accountingBalance;
	}

	public void setAccountingBalance(Double accountingBalance) {
		this.accountingBalance = accountingBalance;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public int getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(int documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getInternationalChecksBalance() {
		return internationalChecksBalance;
	}

	public void setInternationalChecksBalance(int internationalChecksBalance) {
		this.internationalChecksBalance = internationalChecksBalance;
	}

	public int getLocalChecksBalance() {
		return localChecksBalance;
	}

	public void setLocalChecksBalance(int localChecksBalance) {
		this.localChecksBalance = localChecksBalance;
	}

	public int getNumberOfMovements() {
		return numberOfMovements;
	}

	public void setNumberOfMovements(int numberOfMovements) {
		this.numberOfMovements = numberOfMovements;
	}

	public int getOffice() {
		return office;
	}

	public void setOffice(int office) {
		this.office = office;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public int getOwnChecksBalance() {
		return ownChecksBalance;
	}

	public void setOwnChecksBalance(int ownChecksBalance) {
		this.ownChecksBalance = ownChecksBalance;
	}

	public int getSequential() {
		return sequential;
	}

	public void setSequential(int sequential) {
		this.sequential = sequential;
	}

	public String getSignDC() {
		return signDC;
	}

	public void setSignDC(String signDC) {
		this.signDC = signDC;
	}

	public int getTotalChecksBalance() {
		return totalChecksBalance;
	}

	public void setTotalChecksBalance(int totalChecksBalance) {
		this.totalChecksBalance = totalChecksBalance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDC() {
		return typeDC;
	}

	public void setTypeDC(String typeDC) {
		this.typeDC = typeDC;
	}

	public int getUniqueSequential() {
		return uniqueSequential;
	}

	public void setUniqueSequential(int uniqueSequential) {
		this.uniqueSequential = uniqueSequential;
	}

}
