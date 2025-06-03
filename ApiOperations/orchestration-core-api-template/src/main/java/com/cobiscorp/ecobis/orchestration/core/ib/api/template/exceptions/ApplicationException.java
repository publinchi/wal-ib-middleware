package com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions;

import com.cobiscorp.cobis.commons.exceptions.COBISRuntimeException;

public class ApplicationException extends COBISRuntimeException{
    private final int errorCode;

    public ApplicationException(int clientErrorCode, String message) {
        super(message);
        this.errorCode = clientErrorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
