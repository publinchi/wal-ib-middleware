package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api;

import com.cobis.trfspeiservice.bsl.dto.*;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.services.orchestrator.ISPOrchestrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpReverseSpeiSent extends SpUtil{
    private static ILogger logger = LogFactory.getLogger(SpReverseSpeiSent.class);
    protected static final String SP_EXECUTOR_TRN_ORIGEN = "MASSIVE_PAYROLL";

    private SpReverseSpeiSent() {

    }

    public static void initializeSpReverseSpei(IProcedureRequest aProcedureRequest, RegisterSpeiSpResponse aSpeiRegisterResponse){

        String targetId = "central";
        String database = "cob_bvirtual";
        String spName 	= "sp_reverso_spei";

        logDebug("initializeSpReverseSpei - trn_origen: " + aSpeiRegisterResponse.getTrnOrigen());
        if(SP_EXECUTOR_TRN_ORIGEN.equals(aSpeiRegisterResponse.getTrnOrigen())){
            initialize(aProcedureRequest, aSpeiRegisterResponse, database, spName, targetId);
        } else {
            initialize(aProcedureRequest, database, spName, targetId);
        }

        aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18009");

        if(null != aSpeiRegisterResponse.getCuentaOrigen()){
            aProcedureRequest.addInputParam("@i_cuenta_ori", ICTSTypes.SYBVARCHAR,aSpeiRegisterResponse.getCuentaOrigen());
        }
        if(null != aSpeiRegisterResponse.getConcepto()){
            aProcedureRequest.addInputParam("@i_concepto", ICTSTypes.SYBVARCHAR,aSpeiRegisterResponse.getConcepto());
        }
        if(null != aSpeiRegisterResponse.getMonto()){
            aProcedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY,aSpeiRegisterResponse.getMonto());
        }
        if(null != aSpeiRegisterResponse.getMoneda()){
            aProcedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4,aSpeiRegisterResponse.getMoneda());
        }
        if(null != aSpeiRegisterResponse.getServicio()){
            aProcedureRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4,aSpeiRegisterResponse.getServicio());
        }
        if(null != aSpeiRegisterResponse.getTipoError()){
            aProcedureRequest.addInputParam("@i_tipo_error", ICTSTypes.SYBINT4,aSpeiRegisterResponse.getTipoError());
        }
        if(null != aSpeiRegisterResponse.getComision()){
            aProcedureRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEY,aSpeiRegisterResponse.getComision());
        }
        if(null != aSpeiRegisterResponse.getProcesoOrigen()){
            aProcedureRequest.addInputParam("@i_proceso_origen", ICTSTypes.SYBINT4,aSpeiRegisterResponse.getProcesoOrigen());
        }
        if(null != aSpeiRegisterResponse.getTransactionCore()){
            aProcedureRequest.addInputParam("@i_transaccion_core", ICTSTypes.SYBINT4,aSpeiRegisterResponse.getTransactionCore());
        }


        aProcedureRequest.addOutputParam("@o_id_resultado", ICTSTypes.SQLINTN, "");
        aProcedureRequest.addOutputParam("@o_resultado", ICTSTypes.SQLVARCHAR, "");

    }

    public static SpResponse executeSpReverseSpei(ISPOrchestrator aSpOrchestrator,RegisterSpeiSpResponse aSpeiRegisterResponse) {

        String wInfo = "[SpReverseSpeiUtil][executeSpReverseSpei]";

        if (logger.isDebugEnabled()) {
            logger.logDebug("Starting Corebanking execution...");
            logger.logDebug(wInfo + "aSpeiRegisterResponse "+ aSpeiRegisterResponse.toString());
        }

        SpResponse wSpeiResponse;
        IProcedureRequest wProcedureRequest = new ProcedureRequestAS();
        initializeSpReverseSpei(wProcedureRequest, aSpeiRegisterResponse);

        IProcedureResponse wProcedureResponse = executeProcedure(wProcedureRequest, aSpOrchestrator);

        if (!wProcedureResponse.hasError()) {
            List<Map<String, Object>> wLParams = SpUtil.getParams(wProcedureResponse);
            wSpeiResponse = setResponseTo(wLParams);
        }else {
            wSpeiResponse = returnErrorResponseTO(wProcedureResponse);
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("End Corebanking execution...");
        }

        return wSpeiResponse;
    }

    protected static SpResponse setResponseTo(List<Map<String, Object>> wParams){
        RegisterSpeiSpResponse response = new RegisterSpeiSpResponse();
        HashMap<String, Object> resultData = (HashMap<String, Object>)wParams.get(0);

        response.setIdResultado(Integer.parseInt((String)resultData.get("@o_id_resultado")));
        response.setResultado((String)resultData.get("@o_resultado"));

        return response;
    }

    protected static RegisterSpeiSpResponse returnErrorResponseTO(IProcedureResponse wProcedureResponse) {

        IMessageBlock wMessageDBlock = evaluateResponseStatusSP(wProcedureResponse);
        RegisterSpeiSpResponse wSpeiResponse = new RegisterSpeiSpResponse();
        wSpeiResponse.setIdResultado(wProcedureResponse.getReturnCode());
        wSpeiResponse.setResultado(null != wMessageDBlock ? wMessageDBlock.getMessageText() : "Error");

        return wSpeiResponse;
    }

    private static void logDebug(String aMenssage) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(aMenssage);
        }
    }
}
