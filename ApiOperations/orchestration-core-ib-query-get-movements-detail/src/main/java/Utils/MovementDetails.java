package Utils;

import java.math.BigDecimal;

public class MovementDetails {

    private BigDecimal accountingBalance;
    private BigDecimal availableBalance;
    private String movementType;
    private BigDecimal amount;
    private String transactionDate;
    private String operationType;
    private BigDecimal commission;
    private BigDecimal iva;
    private Integer transactionReferenceNumber;
    private String description;
    private String transactionStatus;

    // Card details
    private String maskedCardNumber;
    private String code;
    private String pin;
    private String mode;

    // Source account
    private String ownerNameSA;
    private String accountNumberSA;
    private String bankNameSA;

    // Destination account
    private String ownerNameDA;
    private String accountNumberDA;
    private String bankNameDA;

    // SPEI details
    private String referenceCode;
    private String trackingId;

    // ATM details
    private String bankNameATM;
    private String locationId;
    private String transactionIdATM;

    // Merchant details
    private String establishmentNameMD;
    private String transactionIdMD;

    // Store details
    private String establishmentNameSD;
    private String transactionIdSD;

    private String transactionId;
    private String authorizationCode;
    private String bankBranchCode;
    private BigDecimal purchaseAmount;
    private BigDecimal withdrawalAmount;

    private String errorCode;
    private String errorMessage;

    private String uuid;
    private String cardId;

    // Commission details
    private String reason;

    public BigDecimal getAccountingBalance() {
        return accountingBalance;
    }

    public void setAccountingBalance( BigDecimal accountingBalance ) {
        this.accountingBalance = accountingBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance( BigDecimal availableBalance ) {
        this.availableBalance = availableBalance;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType( String movementType ) {
        this.movementType = movementType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount( BigDecimal amount ) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate( String transactionDate ) {
        this.transactionDate = transactionDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType( String operationType ) {
        this.operationType = operationType;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission( BigDecimal commission ) {
        this.commission = commission;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva( BigDecimal iva ) {
        this.iva = iva;
    }

    public Integer getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber( Integer transactionReferenceNumber ) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber( String maskedCardNumber ) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getOwnerNameSA() {
        return ownerNameSA;
    }

    public void setOwnerNameSA( String ownerNameSA ) {
        this.ownerNameSA = ownerNameSA;
    }

    public String getAccountNumberSA() {
        return accountNumberSA;
    }

    public void setAccountNumberSA( String accountNumberSA ) {
        this.accountNumberSA = accountNumberSA;
    }

    public String getBankNameSA() {
        return bankNameSA;
    }

    public void setBankNameSA( String bankNameSA ) {
        this.bankNameSA = bankNameSA;
    }

    public String getOwnerNameDA() {
        return ownerNameDA;
    }

    public void setOwnerNameDA( String ownerNameDA ) {
        this.ownerNameDA = ownerNameDA;
    }

    public String getAccountNumberDA() {
        return accountNumberDA;
    }

    public void setAccountNumberDA( String accountNumberDA ) {
        this.accountNumberDA = accountNumberDA;
    }

    public String getBankNameDA() {
        return bankNameDA;
    }

    public void setBankNameDA( String bankNameDA ) {
        this.bankNameDA = bankNameDA;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode( String referenceCode ) {
        this.referenceCode = referenceCode;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId( String trackingId ) {
        this.trackingId = trackingId;
    }

    public String getBankNameATM() {
        return bankNameATM;
    }

    public void setBankNameATM( String bankNameATM ) {
        this.bankNameATM = bankNameATM;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId( String locationId ) {
        this.locationId = locationId;
    }

    public String getTransactionIdATM() {
        return transactionIdATM;
    }

    public void setTransactionIdATM( String transactionIdATM ) {
        this.transactionIdATM = transactionIdATM;
    }

    public String getEstablishmentNameMD() {
        return establishmentNameMD;
    }

    public void setEstablishmentNameMD( String establishmentNameMD ) {
        this.establishmentNameMD = establishmentNameMD;
    }

    public String getTransactionIdMD() {
        return transactionIdMD;
    }

    public void setTransactionIdMD( String transactionIdMD ) {
        this.transactionIdMD = transactionIdMD;
    }

    public String getEstablishmentNameSD() {
        return establishmentNameSD;
    }

    public void setEstablishmentNameSD( String establishmentNameSD ) {
        this.establishmentNameSD = establishmentNameSD;
    }

    public String getTransactionIdSD() {
        return transactionIdSD;
    }

    public void setTransactionIdSD( String transactionIdSD ) {
        this.transactionIdSD = transactionIdSD;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId( String transactionId ) {
        this.transactionId = transactionId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode( String authorizationCode ) {
        this.authorizationCode = authorizationCode;
    }

    public String getBankBranchCode() {
        return bankBranchCode;
    }

    public void setBankBranchCode( String bankBranchCode ) {
        this.bankBranchCode = bankBranchCode;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount( BigDecimal purchaseAmount ) {
        this.purchaseAmount = purchaseAmount;
    }

    public BigDecimal getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount( BigDecimal withdrawalAmount ) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid( String uuid ) {
        this.uuid = uuid;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId( String cardId ) {
        this.cardId = cardId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason( String reason ) {
        this.reason = reason;
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getPin() { return pin; }

    public void setPin(String pin) { this.pin = pin; }

    public String getMode() { return mode; }

    public void setMode(String mode) { this.mode = mode; }

    public String getErrorCode() { return errorCode; }

    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }

    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getTransactionStatus() { return transactionStatus; }

    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    @Override
    public String toString() {
        return "MovementDetails{" +
                "accountingBalance=" + accountingBalance +
                ", availableBalance=" + availableBalance +
                ", movementType='" + movementType + '\'' +
                ", amount=" + amount +
                ", transactionDate='" + transactionDate + '\'' +
                ", operationType='" + operationType + '\'' +
                ", commission=" + commission +
                ", iva=" + iva +
                ", transactionReferenceNumber=" + transactionReferenceNumber +
                ", description='" + description + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", maskedCardNumber='" + maskedCardNumber + '\'' +
                ", code='" + code + '\'' +
                ", pin='" + pin + '\'' +
                ", mode='" + mode + '\'' +
                ", ownerNameSA='" + ownerNameSA + '\'' +
                ", accountNumberSA='" + accountNumberSA + '\'' +
                ", bankNameSA='" + bankNameSA + '\'' +
                ", ownerNameDA='" + ownerNameDA + '\'' +
                ", accountNumberDA='" + accountNumberDA + '\'' +
                ", bankNameDA='" + bankNameDA + '\'' +
                ", referenceCode='" + referenceCode + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", bankNameATM='" + bankNameATM + '\'' +
                ", locationId='" + locationId + '\'' +
                ", transactionIdATM='" + transactionIdATM + '\'' +
                ", establishmentNameMD='" + establishmentNameMD + '\'' +
                ", transactionIdMD='" + transactionIdMD + '\'' +
                ", establishmentNameSD='" + establishmentNameSD + '\'' +
                ", transactionIdSD='" + transactionIdSD + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", bankBranchCode='" + bankBranchCode + '\'' +
                ", purchaseAmount=" + purchaseAmount +
                ", withdrawalAmount=" + withdrawalAmount +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", uuid='" + uuid + '\'' +
                ", cardId='" + cardId + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
