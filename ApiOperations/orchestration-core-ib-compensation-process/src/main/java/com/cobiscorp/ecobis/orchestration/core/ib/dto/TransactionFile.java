package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionFile {

	@JsonProperty("FILE_ID")
	private String fileId;

	@JsonProperty("ISSUER_ID")
	private String issuerId;

	@JsonProperty("CLIENT_ID")
	private String clientId;

	@JsonProperty("ID_SUBEMISSOR")
	private String idSubemissor;

	@JsonProperty("BRAND")
	private String brand;

	@JsonProperty("FILENAME_BASE2")
	private String filenameBase2;

	@JsonProperty("FILENAME")
	private String filename;

	@JsonProperty("SEQUENCE")
	private int sequence;

	@JsonProperty("FILE_NUMBER")
	private int fileNumber;

	@JsonProperty("TOTAL_FILES")
	private int totalFiles;

	@JsonProperty("REFERENCE_DATE")
	private String referenceDate;

	@JsonProperty("RECORDS_TOTAL")
	private int recordsTotal;

	@JsonProperty("RECORDS_AMNT")
	private double recordsAmnt;

	@JsonProperty("CREDIT_TOTAL")
	private int creditTotal;

	@JsonProperty("CREDIT_AMNT")
	private double creditAmnt;

	@JsonProperty("DEBIT_TOTAL")
	private int debitTotal;

	@JsonProperty("DEBIT_AMNT")
	private double debitAmnt;

	@JsonProperty("UNKNOWN_TOTAL")
	private int unknownTotal;

	@JsonProperty("UNKNOWN_AMNT")
	private double unknownAmnt;

	@JsonProperty("REJECTED_TOTAL")
	private int rejectedTotal;

	@JsonProperty("REJECTED_AMNT")
	private double rejectedAmnt;

	@JsonProperty("OCCURRENCE_TOTAL")
	private int occurrenceTotal;

	@JsonProperty("OCCURRENCE_AMNT")
	private double occurrenceAmnt;

	@JsonProperty("EXPIRED_TOTAL")
	private int expiredTotal;

	@JsonProperty("CONTENT")
	private List<TransactionContent> content;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getIdSubemissor() {
		return idSubemissor;
	}

	public void setIdSubemissor(String idSubemissor) {
		this.idSubemissor = idSubemissor;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getFilenameBase2() {
		return filenameBase2;
	}

	public void setFilenameBase2(String filenameBase2) {
		this.filenameBase2 = filenameBase2;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public String getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(String referenceDate) {
		this.referenceDate = referenceDate;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public double getRecordsAmnt() {
		return recordsAmnt;
	}

	public void setRecordsAmnt(double recordsAmnt) {
		this.recordsAmnt = recordsAmnt;
	}

	public int getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(int creditTotal) {
		this.creditTotal = creditTotal;
	}

	public double getCreditAmnt() {
		return creditAmnt;
	}

	public void setCreditAmnt(double creditAmnt) {
		this.creditAmnt = creditAmnt;
	}

	public int getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(int debitTotal) {
		this.debitTotal = debitTotal;
	}

	public double getDebitAmnt() {
		return debitAmnt;
	}

	public void setDebitAmnt(double debitAmnt) {
		this.debitAmnt = debitAmnt;
	}

	public int getUnknownTotal() {
		return unknownTotal;
	}

	public void setUnknownTotal(int unknownTotal) {
		this.unknownTotal = unknownTotal;
	}

	public double getUnknownAmnt() {
		return unknownAmnt;
	}

	public void setUnknownAmnt(double unknownAmnt) {
		this.unknownAmnt = unknownAmnt;
	}

	public int getRejectedTotal() {
		return rejectedTotal;
	}

	public void setRejectedTotal(int rejectedTotal) {
		this.rejectedTotal = rejectedTotal;
	}

	public double getRejectedAmnt() {
		return rejectedAmnt;
	}

	public void setRejectedAmnt(double rejectedAmnt) {
		this.rejectedAmnt = rejectedAmnt;
	}

	public int getOccurrenceTotal() {
		return occurrenceTotal;
	}

	public void setOccurrenceTotal(int occurrenceTotal) {
		this.occurrenceTotal = occurrenceTotal;
	}

	public double getOccurrenceAmnt() {
		return occurrenceAmnt;
	}

	public void setOccurrenceAmnt(double occurrenceAmnt) {
		this.occurrenceAmnt = occurrenceAmnt;
	}

	public int getExpiredTotal() {
		return expiredTotal;
	}

	public void setExpiredTotal(int expiredTotal) {
		this.expiredTotal = expiredTotal;
	}

	public List<TransactionContent> getContent() {
		return content;
	}

	public void setContent(List<TransactionContent> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TransactionFile {").append("fileId='").append(fileId).append('\'').append(", issuerId='")
				.append(issuerId).append('\'').append(", clientId='").append(clientId).append('\'')
				.append(", idSubemissor='").append(idSubemissor).append('\'').append(", brand='").append(brand)
				.append('\'').append(", filenameBase2='").append(filenameBase2).append('\'').append(", filename='")
				.append(filename).append('\'').append(", sequence=").append(sequence).append(", fileNumber=")
				.append(fileNumber).append(", totalFiles=").append(totalFiles).append(", referenceDate='")
				.append(referenceDate).append('\'').append(", recordsTotal=").append(recordsTotal)
				.append(", recordsAmnt=").append(recordsAmnt).append(", creditTotal=").append(creditTotal)
				.append(", creditAmnt=").append(creditAmnt).append(", debitTotal=").append(debitTotal)
				.append(", debitAmnt=").append(debitAmnt).append(", unknownTotal=").append(unknownTotal)
				.append(", unknownAmnt=").append(unknownAmnt).append(", rejectedTotal=").append(rejectedTotal)
				.append(", rejectedAmnt=").append(rejectedAmnt).append(", occurrenceTotal=").append(occurrenceTotal)
				.append(", occurrenceAmnt=").append(occurrenceAmnt).append(", expiredTotal=").append(expiredTotal)
				.append(", content=").append(content).append('}');
		return sb.toString();
	}
}