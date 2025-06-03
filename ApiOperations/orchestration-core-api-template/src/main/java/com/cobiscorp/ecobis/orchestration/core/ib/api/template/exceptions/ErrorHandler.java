package com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseWSAS;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.Constants;

import java.util.Map;

public class ErrorHandler {
    private static final ILogger logger = LogFactory.getLogger(ErrorHandler.class);

    public static void handleException(ApplicationException e, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [ErrorHandler][handleException]");
        }

        if (logger.isErrorEnabled()) {
            logger.logError("Error occurred: " + e.getMessage(), e);
        }

        IProcedureResponse response = new ProcedureResponseWSAS();

        response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
        response.addFieldInHeader(ICSP.SERVICE_ERROR_CODE, ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(e.getErrorCode()));
        response.addFieldInHeader(ICSP.MESSAGE_ERROR, ICOBISTS.HEADER_STRING_TYPE, e.getMessage());

        aBagSPJavaOrchestration.put(Constants.RESPONSE_ERROR_HANDLER, response);
    }
}
