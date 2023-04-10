package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;

public class Util {
    private Util(){

    }

    private static final ILogger logger = LogFactory.getLogger(Util.class);

    public static IProcedureResponse returnCorrectResponse(IProcedureResponse responseData) {
        String wInfo = "[Util [returnException ";
        if (logger.isDebugEnabled())
            logger.logDebug( wInfo+"Success service: returnCorrectResponse: "+ responseData.getProcedureResponseAsString());

        IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();

        wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "0");

        wProcedureRespFinal.addParam("@o_resultado", ICTSTypes.SQLINT4, 50, "0");
        wProcedureRespFinal.addParam("@o_folio", ICTSTypes.SQLVARCHAR, 50, responseData.readValueParam("@o_id_interno"));
        String wDescription = responseData.readValueParam("@o_descripcion");
        String wRefundDescription = responseData.readValueParam("@o_descripcion_error");
        wProcedureRespFinal.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, wDescription != null ? wDescription : wRefundDescription);
        wProcedureRespFinal.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, responseData.readValueParam("@o_id_causa_devolucion"));
        wProcedureRespFinal.addParam("@o_cuenta_cobis", ICTSTypes.SQLVARCHAR, 50, responseData.readValueParam("@o_cuenta_cobis"));
        wProcedureRespFinal.addParam("@o_prod_cta", ICTSTypes.SQLINT4, 50, responseData.readValueParam("@o_prod_cta"));

        wProcedureRespFinal.setReturnCode(0);

        return wProcedureRespFinal;

    }

    public static IProcedureResponse returnException(int returnCode, String messageError) {
        String wInfo = "[Util][returnException] ";
        if (logger.isDebugEnabled())
            logger.logDebug( wInfo+"ERROR EXECUTING SERVICE MessageError: "+ messageError);

        IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();

        ErrorBlock eb = new ErrorBlock(returnCode, messageError);
        wProcedureRespFinal.addResponseBlock(eb);
        wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
        wProcedureRespFinal.setReturnCode(returnCode);
        wProcedureRespFinal.addMessage(returnCode, messageError);

        logger.logError(new IllegalArgumentException(messageError).getMessage());
        return wProcedureRespFinal;

    }
}
