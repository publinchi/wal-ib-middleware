package com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions;

public class BusinessException extends ApplicationException {
    public BusinessException(int clientErrorCode, String message) {
        super(clientErrorCode, message);
    }
}
