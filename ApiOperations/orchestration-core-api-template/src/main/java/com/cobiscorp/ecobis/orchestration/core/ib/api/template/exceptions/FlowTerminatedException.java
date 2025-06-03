package com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions;

public class FlowTerminatedException extends ApplicationException {
    public FlowTerminatedException(int clientErrorCode, String message) {
        super(clientErrorCode, message);
    }
}
