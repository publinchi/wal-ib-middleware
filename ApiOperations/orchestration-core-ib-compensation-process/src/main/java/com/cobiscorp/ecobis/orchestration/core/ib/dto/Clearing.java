package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Clearing {
	@JsonProperty("VERSION")
	private int version;

	@JsonProperty("INSTALLMENT")
	private int installment;

	@JsonProperty("CURRENCY")
	private int currency;

	@JsonProperty("VALUE")
	private double value;

	@JsonProperty("BOARDING_FEE")
	private boolean boardingFee;

	@JsonProperty("COMMISSION")
	private double commission;

	@JsonProperty("INTERCHANGE_FEE_SIGN")
	private String interchangeFeeSign;

	@JsonProperty("SETTLEMENT_DATE")
	private String settlementDate;

	@JsonProperty("IS_INTERNATIONAL")
	private boolean isInternational;

	@JsonProperty("PRESENTATION")
	private int presentation;

	@JsonProperty("ACTION_CODE")
	private int actionCode;

	@JsonProperty("REASON_LIST")
	private List<String> reasonList;

	@JsonProperty("TOTAL_PARTIAL_TRANSACTION")
	private int totalPartialTransaction;

	@JsonProperty("FLAG_PARTIAL_SETTLEMENT")
	private boolean flagPartialSettlement;

	@JsonProperty("CANCEL")
	private boolean cancel;

	@JsonProperty("CONFIRM")
	private boolean confirm;

	@JsonProperty("ADD")
	private boolean add;

	@JsonProperty("CREDIT")
	private boolean credit;

	@JsonProperty("DEBIT")
	private boolean debit;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getInstallment() {
		return installment;
	}

	public void setInstallment(int installment) {
		this.installment = installment;
	}

	public int getCurrency() {
		return currency;
	}

	public void setCurrency(int currency) {
		this.currency = currency;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isBoardingFee() {
		return boardingFee;
	}

	public void setBoardingFee(boolean boardingFee) {
		this.boardingFee = boardingFee;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public String getInterchangeFeeSign() {
		return interchangeFeeSign;
	}

	public void setInterchangeFeeSign(String interchangeFeeSign) {
		this.interchangeFeeSign = interchangeFeeSign;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public boolean isInternational() {
		return isInternational;
	}

	public void setInternational(boolean isInternational) {
		this.isInternational = isInternational;
	}

	public int getPresentation() {
		return presentation;
	}

	public void setPresentation(int presentation) {
		this.presentation = presentation;
	}

	public int getActionCode() {
		return actionCode;
	}

	public void setActionCode(int actionCode) {
		this.actionCode = actionCode;
	}

	public List<String> getReasonList() {
		return reasonList;
	}

	public void setReasonList(List<String> reasonList) {
		this.reasonList = reasonList;
	}

	public int getTotalPartialTransaction() {
		return totalPartialTransaction;
	}

	public void setTotalPartialTransaction(int totalPartialTransaction) {
		this.totalPartialTransaction = totalPartialTransaction;
	}

	public boolean isFlagPartialSettlement() {
		return flagPartialSettlement;
	}

	public void setFlagPartialSettlement(boolean flagPartialSettlement) {
		this.flagPartialSettlement = flagPartialSettlement;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}

	public boolean isCredit() {
		return credit;
	}

	public void setCredit(boolean credit) {
		this.credit = credit;
	}

	public boolean isDebit() {
		return debit;
	}

	public void setDebit(boolean debit) {
		this.debit = debit;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Clearing {").append("version=").append(version).append(", installment=").append(installment)
				.append(", currency=").append(currency).append(", value=").append(value).append(", boardingFee=")
				.append(boardingFee).append(", commission=").append(commission).append(", interchangeFeeSign='")
				.append(interchangeFeeSign).append('\'').append(", settlementDate='").append(settlementDate)
				.append('\'').append(", isInternational=").append(isInternational).append(", presentation=")
				.append(presentation).append(", actionCode=").append(actionCode).append(", reasonList=")
				.append(reasonList)
				.append(", totalPartialTransaction=").append(totalPartialTransaction).append(", flagPartialSettlement=")
				.append(flagPartialSettlement).append(", cancel=").append(cancel).append(", confirm=").append(confirm)
				.append(", add=").append(add).append(", credit=").append(credit).append(", debit=").append(debit)
				.append('}');
		return sb.toString();
	}
}