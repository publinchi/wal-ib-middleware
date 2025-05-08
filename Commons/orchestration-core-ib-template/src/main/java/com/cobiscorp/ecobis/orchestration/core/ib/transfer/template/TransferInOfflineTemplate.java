package com.cobiscorp.ecobis.orchestration.core.ib.transfer.template;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferInBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;

import java.util.Map;

public abstract class TransferInOfflineTemplate extends TransferInBaseTemplate {
    protected static String CORE_SERVER = "CORE_SERVER";
    protected static String TRANSFER_RESPONSE = "TRANSFER_RESPONSE";
    protected static final String TRANSFER_NAME = "TRANSFER_NAME";
    protected static final int CODE_OFFLINE = 40004;

    protected abstract IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration);

    private static ILogger logger = LogFactory.getLogger(TransferInOfflineTemplate.class);

    public abstract ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent();

    @Override
    protected IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest,
                                                    Map<String, Object> aBagSPJavaOrchestration) {
        IProcedureResponse responseTransfer = null;

        if (logger.isDebugEnabled())
            logger.logDebug(CLASS_NAME + "Ejecutando método executeTransaction Request: " + anOriginalRequest);

        StringBuilder messageErrorTransfer = new StringBuilder();
        messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

        ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

        if (logger.isDebugEnabled()){
            logger.logDebug("Status Servidor 1");
            logger.logDebug(serverResponse);
        }

        // JC if is Offline and if is reentryExecution , have to leave
        if (Boolean.TRUE.equals(getFromReentryExcecution(aBagSPJavaOrchestration))) {
            if (Boolean.FALSE.equals(serverResponse.getOnLine())) {
                IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
                aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
                aBagSPJavaOrchestration.put("@s_error", "40004");
                return resp;
            }
        }
        logger.logInfo("WInfo is offline: "+serverResponse.toString());

        responseTransfer = executeTransfer(aBagSPJavaOrchestration);

        if (Boolean.TRUE.equals(serverResponse.getOnLine())) {

            logDebug(CLASS_NAME + " Respuesta de ejecución método executeTransfer: " + responseTransfer.getProcedureResponseAsString());

            if (Utils.flowError(messageErrorTransfer.append(" --> executeTransfer").toString(), responseTransfer)) {
                logDebug(CLASS_NAME + messageErrorTransfer);
                aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);
                return responseTransfer;
            }
        } else if (responseTransfer.getReturnCode()!=2){
            // Si no es ejecucion de reentry, grabar en reentry
            if (Boolean.FALSE.equals(getFromReentryExcecution(aBagSPJavaOrchestration))) {
                if (logger.isInfoEnabled()){
                    logger.logInfo(CLASS_NAME + " Transferencia en OffLine serverResponse :" + serverResponse.toString());
                    logger.logInfo(CLASS_NAME + " Respuesta de ejecución método executeTransfer validando offline mode: " + responseTransfer.getProcedureResponseAsString());
                }

                if (logger.isInfoEnabled())
                    logger.logInfo("::::SAVED REENTRY:::: "+anOriginalRequest);
                IProcedureResponse aReentryResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
                aBagSPJavaOrchestration.put("@s_error","40004");
                aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseTransfer);
                responseTransfer.setReturnCode(aReentryResponse.getReturnCode());
            }
        }

        aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseTransfer);

        logDebug(CLASS_NAME + "Respuesta de ejecución metodo executeTransaction Response:  " + responseTransfer.getProcedureResponseAsString());

        return responseTransfer;
    }
    
    public void registerAllTransactionSuccess(String tipoTran, IProcedureRequest aRequest,String causal , Map<String, Object> aBagSPJavaOrchestration) {	
        try {
            IProcedureRequest request = new ProcedureRequestAS();

            if (logger.isDebugEnabled()) {
                logger.logDebug(" Entrando en registerAllTransactionSuccess");
            }
            String movementType = "SPEI_CREDIT";
            request.setSpName("cob_bvirtual..sp_bv_transacciones_exitosas");
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
            
            request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

            request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
            if(tipoTran.equals("SPEI_CREDIT")) {
                request.addInputParam("@i_eventType", ICTSTypes.SQLVARCHAR, "TRANSACCION SUCCESS");
               
                request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, (String)aBagSPJavaOrchestration.get("externalCustId"));
                request.addInputParam("@i_transactionAmount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_monto"));
                request.addInputParam("@i_transactionDate", ICTSTypes.SQLVARCHAR , aRequest.readValueParam("@i_fechaOperacion"));
                request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
                request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
                request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, causal);
                request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
                request.addInputParam("@i_commission", ICTSTypes.SQLMONEY , "0");
                request.addInputParam("@i_iva", ICTSTypes.SQLMONEY , "0");
                request.addInputParam("@i_movementId", ICTSTypes.SQLINTN , (String)aRequest.readValueParam("@s_ssn"));
                request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@x_request_id"));
                request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
                
                request.addInputParam("@i_sourceBankName", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@i_institucionOrdenante"));
                request.addInputParam("@i_sourceAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("originAccountType"));
                request.addInputParam("@i_sourceAccountNumber", ICTSTypes.SQLVARCHAR, (String)aRequest.readValueParam("@i_cuentaOrdenante"));
                request.addInputParam("@i_sourceAccountName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_nombreOrdenante")); //consultar
                
                request.addInputParam("@i_destinationAccountName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_nombreBeneficiario")); //consultar
                request.addInputParam("@i_destinationAccountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cuentaBeneficiario"));
                request.addInputParam("@i_destinationAccountType", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("destinationAccountType"));
                request.addInputParam("@i_destinationExternalCustomerId", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("externalCustId"));
                
                request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_idSpei"));
                request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR,  aRequest.readValueParam("@i_claveRastreo"));
                
                request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, "{}");
                request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
                IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

                if (logger.isDebugEnabled()) {
                    logger.logDebug("Response Corebanking registerAllTransactionSuccess: " + wProductsQueryResp.getProcedureResponseAsString());
                    logger.logDebug(" Saliendo de registerAllTransactionSuccess");
                }
            }
        }catch(Exception e){
            logger.logError(" Error Catastrofico en registerAllTransactionSuccess SPEI_CREDIT");
        }	
    }

    protected IProcedureResponse saveReentry(IProcedureRequest anOriginalRequest,
                                             Map<String, Object> aBagSPJavaOrchestration) {

        String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
        IProcedureRequest request = anOriginalRequest.clone();
        IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
                .get(RESPONSE_LOCAL_VALIDATION);

        ComponentLocator componentLocator = null;
        IReentryPersister reentryPersister = null;
        componentLocator = ComponentLocator.getInstance(this);

        //Utils.addInputParam(request, "@i_clave_bv", 56, responseLocalValidation.readValueParam("@o_clave_bv"));
        Utils.addInputParam(request, "@i_en_linea", 39, "N");
        Utils.addOutputParam(request, "@o_clave", 56, "0");

        reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader("trn", 'N', request.readValueFieldInHeader("trn"));

        request.removeParam("@t_rty");

        if (logger.isDebugEnabled()) {
            logger.logDebug("REQUEST TO SAVE REENTRY -->" + request.getProcedureRequestAsString());
        }
        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;

    }

    private void logDebug(Object aMessage){
        if(logger.isDebugEnabled()){
            logger.logDebug(aMessage);
        }
    }
}
