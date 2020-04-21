package com.cobiscorp.channels.cc.connector;

public class CreditCardConnectorServiceException extends Exception {

    private static final long serialVersionUID = 6545454209876305909L;

    public CreditCardConnectorServiceException() {
    }

    public CreditCardConnectorServiceException(String message) {
        super(message);
    }

    public CreditCardConnectorServiceException(Throwable cause) {
        super(cause);
    }

    public CreditCardConnectorServiceException(String message, Throwable cause) {
        super(message, cause);
    }


}
