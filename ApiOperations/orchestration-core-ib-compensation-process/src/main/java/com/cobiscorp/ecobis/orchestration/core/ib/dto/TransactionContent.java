package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionContent {
	@JsonProperty("ID")
	private String id;

	@JsonProperty("RECORD_CODE")
	private int recordCode;

	@JsonProperty("TRANSACTION")
	private Transaction transaction;

	@JsonProperty("CLEARING")
	private Clearing clearing;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRecordCode() {
		return recordCode;
	}

	public void setRecordCode(int recordCode) {
		this.recordCode = recordCode;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Clearing getClearing() {
		return clearing;
	}

	public void setClearing(Clearing clearing) {
		this.clearing = clearing;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TransactionContent {").append("id='").append(id).append('\'').append(", recordCode=")
				.append(recordCode).append(", transaction=").append(transaction).append(", clearing=").append(clearing)
				.append('}');
		return sb.toString();
	}

}
