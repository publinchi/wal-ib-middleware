package com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils;

public enum ErrorCode {
    E40030(40030, "externalCustomerId no debe estar vacío."),
    E40082(40082, "accountNumber no debe estar vacío."),
    E40092(40092, "referenceNumber no debe estar vacío."),
    E40093(40093, "creditConcept no debe estar vacío."),
    E40300(40300, "amount debe ser un número decimal."),
    E40107(40107, "amount debe ser mayor que 0."),
    E40301(40301, "commission debe ser un valor decimal."),
    E40108(40108, "commission debe ser mayor que 0."),
    E40302(40302, "originCode no debe estar vacío."),
    E40303(40303, "supplementaryData.senderName no debe estar vacío."),
    E40304(40304, "supplementaryData.moneyTransmitter no debe estar vacío."),
    E40305(40305, "supplementaryData.originCountry no debe estar vacío."),
    E40306(40306, "supplementaryData.currency no debe estar vacío."),
    E40307(40307, "supplementaryData.originCurrency no debe estar vacío."),
    E40308(40308, "supplementaryData.exchangeRate no debe estar vacío."),
    E40310(40310, "originalTransactionData.externalCustomerId no debe estar vacío."),
    E40130(40130, "originalTransactionData.accountNumber no debe estar vacío."),
    E40131(40131, "originalTransactionData.referenceNumber no debe estar vacío."),
    E40133(40133, "originalTransactionData.movementId no debe estar vacío."),
    E40311(40311, "reversalConcept no debe estar vacío."),
    E40134(40134, "originalTransactionData.reversalReason no debe estar vacío."),
    E40312(40312, "amount no debe estar vacío."),
    E40313(40313, "commission no debe estar vacío."),
    E40314(40314, "referenceNumber no debe tener más de 34 caracteres."),
    E40315(40315, "originalTransactionData.referenceNumber no debe tener más de 34 caracteres."),
    E50062(50062, "No se pudo obtener la causa de la transacción.");

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
