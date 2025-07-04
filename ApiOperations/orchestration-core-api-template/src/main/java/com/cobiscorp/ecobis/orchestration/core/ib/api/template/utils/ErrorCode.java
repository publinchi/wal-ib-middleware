package com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils;

public enum ErrorCode {
    E40030(40030, "externalCustomerId must not be empty."),
    E40082(40082, "accountNumber must not be empty."),
    E40092(40092, "referenceNumber must not be empty."),
    E40093(40093, "creditConcept must not be empty."),
    E40300(40300, "amount must be a decimal number."),
    E40107(40107, "amount must be greater than 0."),
    E40301(40301, "commission must be a decimal value."),
    E40108(40108, "commission must be greater than 0."),
    E40302(40302, "originCode must not be empty."),
    E40303(40303, "supplementaryData.senderName must not be empty."),
    E40304(40304, "supplementaryData.moneyTransmitter must not be empty."),
    E40305(40305, "supplementaryData.originCountry must not be empty."),
    E40306(40306, "supplementaryData.currency must not be empty."),
    E40307(40307, "supplementaryData.originCurrency must not be empty."),
    E40308(40308, "supplementaryData.exchangeRate must not be empty."),
    E40310(40310, "originalTransactionData.externalCustomerId must not be empty."),
    E40130(40130, "originalTransactionData.accountNumber must not be empty."),
    E40131(40131, "originalTransactionData.referenceNumber must not be empty."),
    E40133(40133, "originalTransactionData.movementId must not be empty."),
    E40311(40311, "reversalConcept must not be empty."),
    E40134(40134, "originalTransactionData.reversalReason must not be empty."),
    E40312(40312, "amount must not be empty."),
    E40313(40313, "commission must not be empty.");

    private final int code;
    private final String message;

    // Constructor
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
               "code=" + code +
               ", message='" + message + '\'' +
               '}';
    }
}
