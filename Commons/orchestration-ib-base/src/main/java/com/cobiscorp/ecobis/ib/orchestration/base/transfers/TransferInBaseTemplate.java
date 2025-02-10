package com.cobiscorp.ecobis.ib.orchestration.base.transfers;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.*;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

import java.util.Map;

public abstract class TransferInBaseTemplate extends SPJavaOrchestrationBase {


    protected static final String CLASS_NAME = " [SpeiInTemplate] ";
    protected static final String COBIS_CONTEXT = "COBIS";
    protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
    protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
    protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
    protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
    protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
    protected static final String RESPONSE_OFFLINE = "RESPONSE_OFFLINE";
    protected static final String RESPONSE_TRANSFER = "RESPONSE_TRANSFER";
    protected static final String RESPONSE_FIND_OFFICERS = "RESPONSE_FIND_OFFICERS";
    protected static final String RESPONSE_QUERY_SIGNER = "RESPONSE_QUERY_SIGNER";
    protected static final String RESPONSE_LOCAL_VALIDATION = "RESPONSE_LOCAL_VALIDATION";
    protected static final String RESPONSE_CENTRAL_VALIDATION = "RESPONSE_CENTRAL_VALIDATION";
    protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION";
    protected static final String REENTRY_EXE = "reentryExecution";
    protected static final String TRANSFER_NAME = "TRANSFER_NAME";
    protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
    protected static final int CODE_OFFLINE = 40004;
    protected static final String TYPE_REENTRY_OFF_SPI="S";
    protected static final String TYPE_REENTRY_OFF="OFF_LINE";
    protected static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";
    protected static final String INIT_TASK = "-----------------> init task ";
    protected static final String END_TASK = "-----------------> end task ";


    private static ILogger logger = LogFactory.getLogger(TransferInBaseTemplate.class);

    /**
     * Constant controller offline functionality activation.<br>
     * When this value is true the functionality is enabled.
     */
    public boolean SUPPORT_OFFLINE = false;

    /**
     * Methods for Dependency Injection.
     *
     * @return ICoreServiceNotification
     */
    protected abstract ICoreServiceSendNotification getCoreServiceNotification();

    public abstract ICoreService getCoreService();

    public abstract ICoreServer getCoreServer();

    /**
     * Method for core preconditions validation.
     *
     * @param IProcedureRequest request
     * @param Map<String, Object> aBagSPJavaOrchestration
     * @return IProcedureResponse
     */
    protected abstract IProcedureResponse validateCentralExecution(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

    public abstract NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest, OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration);

    protected abstract void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest, IProcedureRequest anOriginalRequest);

    protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);


    public String generaMensaje(String vars) {
        vars = vars.substring(vars.indexOf("]") + 1, vars.length());
        return vars;
    }


    /**
     * Contains primary steps of transaction execution.
     *
     * @param anOriginalRequest
     * @param aBagSPJavaOrchestration
     * @return
     * @throws CTSInfrastructureException
     * @throws CTSServiceException
     */
    protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
        if (logger.isInfoEnabled())
            logger.logInfo(CLASS_NAME + " Ejecutando metodo executeStepsTransactionsBase: " + anOriginalRequest);

        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

        StringBuilder messageErrorTransfer = new StringBuilder();
        messageErrorTransfer.append((String) aBagSPJavaOrchestration.get(TRANSFER_NAME));

        IProcedureResponse responseTransfer = null;
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
        ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
        logger.logInfo("SERVER RESPONSE: "+responseServer.toString());
        aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);

        if(anOriginalRequest.readValueFieldInHeader("comision") != null) {
            if (logger.isInfoEnabled())
                logger.logInfo("Llegada de comisiom ---> " + anOriginalRequest.readValueFieldInHeader("comision"));
        }

        SUPPORT_OFFLINE = true;

        // Valida firmas fisicas
        IProcedureResponse responseSigner = new ProcedureResponseAS();
        responseSigner.setReturnCode(0);
        aBagSPJavaOrchestration.put(RESPONSE_QUERY_SIGNER, responseSigner);

        // Ejecuta transaccion core
        responseTransfer = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);
        if (Utils.flowError("executeTransaction", responseTransfer)) {
            if (Boolean.TRUE.equals(responseServer.getOnLine())) {
                if (Boolean.FALSE.equals(getFromReentryExcecution(aBagSPJavaOrchestration))) {
                    logDebug(CLASS_NAME + "::Fin del flujo se ejecuta con errores.");
                    return responseTransfer;
                }
            } else {
                logDebug(CLASS_NAME + "Response transfer obtained from executing transaction: "+responseTransfer.getProcedureResponseAsString());
                logDebug(CLASS_NAME + "aBagSPJavaOrchestration: base tm "+aBagSPJavaOrchestration);

                if (Boolean.TRUE.equals(getFromReentryExcecution(aBagSPJavaOrchestration)) ) {
                    logDebug(CLASS_NAME + "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
                    return responseTransfer;
                }
            }
        }

        // Actualizacion local
        IProcedureResponse responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
        aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);

        if (logger.isInfoEnabled())
            logger.logInfo(new StringBuilder(CLASS_NAME).append("Respuesta metodo executeStepsTransactionsBase: " + aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION)).toString());

        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }

    /**
     * Local updates: saves monetary trn, saves log, syncs local balances
     *
     * @param originalRequest
     * @param bag
     * @return
     */
    protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        if (logger.isDebugEnabled())
            logger.logDebug("Ejecutando metodo updateLocalExecution: " + anOriginalRequest.toString());

        IProcedureRequest request = initProcedureRequest(anOriginalRequest);

        ServerResponse responseServer = (ServerResponse) bag.get(RESPONSE_SERVER);
        IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);

        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
        request.setSpName("cob_bvirtual..sp_bv_tran_entrante");

        request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
        request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
        request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
        request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
        if( anOriginalRequest.readValueParam("@s_cliente") == null || "".equals(anOriginalRequest.readValueParam("@s_cliente"))){
            request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_codigo_cliente"));
        }

        int tTrn = Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"));
        if(tTrn == 18500069){
            request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_producto"));
            request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuenta_cobis"));
            request.addInputParam("@i_cuenta_dst", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
            request.addInputParam("@i_prod_des", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
            request.addInputParam("@i_mon_des", ICTSTypes.SQLINT4, "0");
            request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));
            request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
            request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, "0");
            request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, "9");
            request.addInputParam("@i_banco_origen", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_institucionOrdenante"));
            request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, (String)bag.get("@i_estado_ejec"));
            request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
            request.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
            request.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
            request.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
            request.addInputParam("@i_institucionOrdenante", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_institucionOrdenante"));
            request.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
            request.addInputParam("@i_tipoCuentaBeneficiario", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
            request.addInputParam("@i_tipoCuentaOrdenante", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaOrdenante"));
            request.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_idSpei"));
        }

        // Datos de tran monet
        Utils.copyParam("@i_cta", anOriginalRequest, request);
        Utils.copyParam("@i_mon", anOriginalRequest, request);
        Utils.copyParam("@i_cta_des", anOriginalRequest, request);
        Utils.copyParam("@i_prod_des", anOriginalRequest, request);
        Utils.copyParam("@i_mon_des", anOriginalRequest, request);
        Utils.copyParam("@i_val", anOriginalRequest, request);
        Utils.copyParam("@i_concepto", anOriginalRequest, request);

        if (logger.isInfoEnabled())
            logger.logInfo("DATA------>>" + responseServer.getOnLine());


        addParametersRequestUpdateLocal(request, anOriginalRequest);

        if (Boolean.FALSE.equals(Utils.isNull(anOriginalRequest.readValueParam("@i_login"))))
            request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
        else
            request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));

        // Enviar clave de reentry si fue ejecucion en fuera de linea
        if (Boolean.FALSE.equals(responseServer.getOnLine())) {
            if (Boolean.FALSE.equals(Utils.isNull(request.readValueParam("@o_clave"))))
                Utils.addInputParam(request, "@i_clave_rty", request.readParam("@o_clave").getDataType(), request.readValueParam("@o_clave"));
            else {
                if (logger.isInfoEnabled())
                    logger.logInfo("ParÃ¡metro @o_clave no encontrado");
            }
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo("Transaccion ejecutando en linea: " + responseServer.getOnLine());
            //logger.logInfo("Respuesta del core al ejecutar transferencia: " + responseTransaction != null ? responseTransaction.toString() : "ERROR ejecucion en el core es NULL");
        }

        // obtener returnCode de ejecucion de Core, si es fuera de linea el error es 40004
        if (responseTransaction != null && responseTransaction.getReturnCode() != 0) { // error en ejec. core
            Utils.addInputParam(request, "@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(responseTransaction.getReturnCode())));
            if (responseTransaction.getMessageListSize() > 0)
                Utils.addInputParam(request, "@s_msg", ICTSTypes.SQLVARCHAR, (responseTransaction.getMessage(1).getMessageText()));
        }

        if(bag.get("@s_error") != null && !"0".equals(bag.get("@s_error"))){
            Utils.addInputParam(request, "@s_error", ICTSTypes.SQLVARCHAR, bag.get("@s_error").toString());
        }

        // envio de t_rty
        if (logger.isInfoEnabled())
            logger.logInfo("Update local param reentryExecution" + request.readValueFieldInHeader("reentryExecution"));
        request.removeParam("@t_rty");
        if (Boolean.TRUE.equals(getFromReentryExcecution(bag))) {
            Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
        } else { // no es ejecucion de reentry
            Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
        }


        if (logger.isDebugEnabled()) {
            logger.logDebug("Update local, request: " + request.getProcedureRequestAsString());
        }

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Update local, response: " + pResponse.getProcedureResponseAsString());
        }
        if (logger.isInfoEnabled()) {
            logger.logInfo("Finalize Update local");
        }
        return pResponse;
    }

    protected Boolean getFromReentryExcecution(Map<String, Object> aBagSPJavaOrchestration) {
        IProcedureRequest request = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
        return ("Y".equals(request.readValueFieldInHeader(REENTRY_EXE)));
    }

    private void logDebug(Object aMessage){
        if(logger.isDebugEnabled()){
            logger.logDebug(aMessage);
        }
    }

}
