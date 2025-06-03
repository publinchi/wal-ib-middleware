package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
	@JsonProperty("ARN")
	private String arn;

	@JsonProperty("ID_CARDBRAND")
	private String idCardbrand;

	@JsonProperty("EXTERNAL_ID")
	private String externalId;

	@JsonProperty("VERSION")
	private int version;

	@JsonProperty("PAN")
	private String pan;

	@JsonProperty("BIN_CARD")
	private String binCard;

	@JsonProperty("CARD_ID")
	private String cardId;

	@JsonProperty("PRODUCT_REFERENCE_ID")
	private String productReferenceId;

	@JsonProperty("ACQUIRER_ID")
	private String acquirerId;

	@JsonProperty("TRANSACTION_TYPE_INDICATOR")
	private String transactionTypeIndicator;

	@JsonProperty("AUTHORIZATION")
	private String authorization;

	@JsonProperty("LOCAL_DATE")
	private String localDate;

	@JsonProperty("GMT_DATE")
	private String gmtDate;

	@JsonProperty("INSTALLMENT_NBR")
	private int installmentNbr;

	@JsonProperty("MCC")
	private int mcc;

	@JsonProperty("SOURCE_CURRENCY")
	private int sourceCurrency;

	@JsonProperty("SOURCE_VALUE")
	private double sourceValue;

	@JsonProperty("DEST_CURRENCY")
	private int destCurrency;

	@JsonProperty("DEST_VALUE")
	private double destValue;

	@JsonProperty("PURCHASE_VALUE")
	private double purchaseValue;

	@JsonProperty("INSTALLMENT_DATA")
	private InstallmentData installmentData;

	@JsonProperty("INSTALLMENT_VALUE_1")
	private double installmentValue1;

	@JsonProperty("INSTALLMENT_VALUE_N")
	private double installmentValueN;

	@JsonProperty("BOARDING_FEE")
	private double boardingFee;

	@JsonProperty("MERCHANT")
	private String merchant;

	@JsonProperty("MERCHANT_DATA")
	private MerchantData merchantData;

	@JsonProperty("BUSINESS_ARRANGEMENT")
	private BusinessArrangement businessArrangement;

	@JsonProperty("ENTRY_MODE")
	private int entryMode;

	@JsonProperty("AUTHORIZATION_DATE")
	private String authorizationDate;

	@JsonProperty("STATUS")
	private int status;

	@JsonProperty("TRANSACTION_QUALIFIER")
	private String transactionQualifier;

	@JsonProperty("CLASSIFICATION")
	private List<String> classification;

	@JsonProperty("OPERATION_TYPE")
	private int operationType;

	@JsonProperty("POS_ENTRY_MODE")
	private String posEntryMode;

	@JsonProperty("ISSUER_EXCHANGE_RATE")
	private double issuerExchangeRate;

	@JsonProperty("CDT_AMOUNT")
	private double cdtAmount;

	@JsonProperty("PRODUCT_CODE")
	private String productCode;

	@JsonProperty("REASON_CODE")
	private String reasonCode;

	@JsonProperty("UUID")
	private String uuid;

	@JsonProperty("OPERATION_CODE")
	private String operationCode;

	@JsonProperty("AGENCY")
	private String agency;

	@JsonProperty("ACCOUNT_NUMBER")
	private String accountNumber;

	@JsonProperty("LATE_PRESENTATION")
	private boolean latePresentation;

	@JsonProperty("PRESENTATION_DATA")
	private PresentationData presentationData;

	@JsonProperty("ERROR_CODE")
	private String errorCode;

	@JsonProperty("CARDHOLDER_BILLING_DATA")
	private CardholderBillingData cardholderBillingData;

	@JsonProperty("RECEIVED_CHANGE")
	private double receivedChange;

	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public String getIdCardbrand() {
		return idCardbrand;
	}

	public void setIdCardbrand(String idCardbrand) {
		this.idCardbrand = idCardbrand;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getBinCard() {
		return binCard;
	}

	public void setBinCard(String binCard) {
		this.binCard = binCard;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getProductReferenceId() {
		return productReferenceId;
	}

	public void setProductReferenceId(String productReferenceId) {
		this.productReferenceId = productReferenceId;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getTransactionTypeIndicator() {
		return transactionTypeIndicator;
	}

	public void setTransactionTypeIndicator(String transactionTypeIndicator) {
		this.transactionTypeIndicator = transactionTypeIndicator;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	public String getLocalDate() {
		return localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getGmtDate() {
		return gmtDate;
	}

	public void setGmtDate(String gmtDate) {
		this.gmtDate = gmtDate;
	}

	public int getInstallmentNbr() {
		return installmentNbr;
	}

	public void setInstallmentNbr(int installmentNbr) {
		this.installmentNbr = installmentNbr;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getSourceCurrency() {
		return sourceCurrency;
	}

	public void setSourceCurrency(int sourceCurrency) {
		this.sourceCurrency = sourceCurrency;
	}

	public double getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(double sourceValue) {
		this.sourceValue = sourceValue;
	}

	public int getDestCurrency() {
		return destCurrency;
	}

	public void setDestCurrency(int destCurrency) {
		this.destCurrency = destCurrency;
	}

	public double getDestValue() {
		return destValue;
	}

	public void setDestValue(double destValue) {
		this.destValue = destValue;
	}

	public double getPurchaseValue() {
		return purchaseValue;
	}

	public void setPurchaseValue(double purchaseValue) {
		this.purchaseValue = purchaseValue;
	}

	public InstallmentData getInstallmentData() {
		return installmentData;
	}

	public void setInstallmentData(InstallmentData installmentData) {
		this.installmentData = installmentData;
	}

	public double getInstallmentValue1() {
		return installmentValue1;
	}

	public void setInstallmentValue1(double installmentValue1) {
		this.installmentValue1 = installmentValue1;
	}

	public double getInstallmentValueN() {
		return installmentValueN;
	}

	public void setInstallmentValueN(double installmentValueN) {
		this.installmentValueN = installmentValueN;
	}

	public double getBoardingFee() {
		return boardingFee;
	}

	public void setBoardingFee(double boardingFee) {
		this.boardingFee = boardingFee;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public MerchantData getMerchantData() {
		return merchantData;
	}

	public void setMerchantData(MerchantData merchantData) {
		this.merchantData = merchantData;
	}

	public BusinessArrangement getBusinessArrangement() {
		return businessArrangement;
	}

	public void setBusinessArrangement(BusinessArrangement businessArrangement) {
		this.businessArrangement = businessArrangement;
	}

	public int getEntryMode() {
		return entryMode;
	}

	public void setEntryMode(int entryMode) {
		this.entryMode = entryMode;
	}

	public String getAuthorizationDate() {
		return authorizationDate;
	}

	public void setAuthorizationDate(String authorizationDate) {
		this.authorizationDate = authorizationDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTransactionQualifier() {
		return transactionQualifier;
	}

	public void setTransactionQualifier(String transactionQualifier) {
		this.transactionQualifier = transactionQualifier;
	}

	public List<String> getClassification() {
		return classification;
	}

	public void setClassification(List<String> classification) {
		this.classification = classification;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getPosEntryMode() {
		return posEntryMode;
	}

	public void setPosEntryMode(String posEntryMode) {
		this.posEntryMode = posEntryMode;
	}

	public double getIssuerExchangeRate() {
		return issuerExchangeRate;
	}

	public void setIssuerExchangeRate(double issuerExchangeRate) {
		this.issuerExchangeRate = issuerExchangeRate;
	}

	public double getCdtAmount() {
		return cdtAmount;
	}

	public void setCdtAmount(double cdtAmount) {
		this.cdtAmount = cdtAmount;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public boolean isLatePresentation() {
		return latePresentation;
	}

	public void setLatePresentation(boolean latePresentation) {
		this.latePresentation = latePresentation;
	}

	public PresentationData getPresentationData() {
		return presentationData;
	}

	public void setPresentationData(PresentationData presentationData) {
		this.presentationData = presentationData;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public CardholderBillingData getCardholderBillingData() {
		return cardholderBillingData;
	}

	public void setCardholderBillingData(CardholderBillingData cardholderBillingData) {
		this.cardholderBillingData = cardholderBillingData;
	}

	public double getReceivedChange() {
		return receivedChange;
	}

	public void setReceivedChange(double receivedChange) {
		this.receivedChange = receivedChange;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Transaction {").append("arn='").append(arn).append('\'').append(", idCardbrand='")
				.append(idCardbrand).append('\'').append(", externalId='").append(externalId).append('\'')
				.append(", version=").append(version).append(", pan='").append(pan).append('\'').append(", binCard='")
				.append(binCard).append('\'').append(", cardId='").append(cardId).append('\'')
				.append(", productReferenceId='").append(productReferenceId).append('\'').append(", acquirerId='")
				.append(acquirerId).append('\'').append(", transactionTypeIndicator='").append(transactionTypeIndicator)
				.append('\'').append(", authorization='").append(authorization).append('\'').append(", localDate='")
				.append(localDate).append('\'').append(", gmtDate='").append(gmtDate).append('\'')
				.append(", installmentNbr=").append(installmentNbr).append(", mcc=").append(mcc)
				.append(", sourceCurrency=").append(sourceCurrency).append(", sourceValue=").append(sourceValue)
				.append(", destCurrency=").append(destCurrency).append(", destValue=").append(destValue)
				.append(", purchaseValue=").append(purchaseValue).append(", installmentData=").append(installmentData)
				.append(", installmentValue1=").append(installmentValue1).append(", installmentValueN=")
				.append(installmentValueN).append(", boardingFee=").append(boardingFee).append(", merchant='")
				.append(merchant).append('\'').append(", merchantData=").append(merchantData)
				.append(", businessArrangement=").append(businessArrangement).append(", entryMode=").append(entryMode)
				.append(", authorizationDate='").append(authorizationDate).append('\'').append(", status=")
				.append(status).append(", transactionQualifier='").append(transactionQualifier).append('\'')
				.append(", classification=").append(classification).append(", operationType=").append(operationType)
				.append(", posEntryMode='").append(posEntryMode).append('\'').append(", issuerExchangeRate=")
				.append(issuerExchangeRate).append(", cdtAmount=").append(cdtAmount).append(", productCode='")
				.append(productCode).append('\'').append(", reasonCode='").append(reasonCode).append('\'')
				.append(", uuid='").append(uuid).append('\'').append(", operationCode='").append(operationCode)
				.append('\'').append(", agency='").append(agency).append('\'').append(", accountNumber='")
				.append(accountNumber).append('\'').append(", latePresentation=").append(latePresentation)
				.append(", presentationData=").append(presentationData).append(", errorCode='").append(errorCode)
				.append('\'').append(", cardholderBillingData=").append(cardholderBillingData)
				.append(", receivedChange=").append(receivedChange).append('}');
		return sb.toString();
	}
}