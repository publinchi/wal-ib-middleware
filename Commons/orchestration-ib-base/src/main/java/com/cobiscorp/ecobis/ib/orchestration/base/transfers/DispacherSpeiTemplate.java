package com.cobiscorp.ecobis.ib.orchestration.base.transfers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ValidaSpei;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ordenpago;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

public abstract class DispacherSpeiTemplate  extends SPJavaOrchestrationBase {

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
    protected static final String XML_REQUEST = "XML_REQUEST";
    protected static final String XML_RESPONSE = "XML_RESPONSE";
    protected static final String SPEI_TRANSACTION = "speiTransaction";
    protected static final String STATUS_TRANSACTION = "STATUS_TRANSACTION";
    protected static final String RESULT_VALIDACION_SPEI = "RESULT_VALIDACION_SPEI";
    protected static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";
    protected static final String INIT_TASK = "-----------------> init task ";
    protected static final String END_TASK = "-----------------> end task ";
    protected static final String ESTADO_SPEI_PENDIENTE = "PND";
    protected static final String ESTADO_SPEI_PENDIENTE_DEVOLUCION = "PDEV";
    protected static final String ESTADO_SPEI_PROCESADO = "PROC";
    protected static final String ESTADO_SPEI_ERROR = "ERR";

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
     * @param Map<String,       Object> aBagSPJavaOrchestration
     * @return IProcedureResponse
     */
    protected abstract IProcedureResponse validateCentralExecution(IProcedureRequest request,
                                                                   Map<String, Object> aBagSPJavaOrchestration);

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
        logger.logInfo("SERVER RESPONSE: " + responseServer.toString());
        aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
        mensaje message = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
        ValidaSpei resultado = (ValidaSpei) aBagSPJavaOrchestration.get(RESULT_VALIDACION_SPEI);


     /*   if(anOriginalRequest.readValueFieldInHeader("comision") != null) {
            if (logger.isInfoEnabled())
                logger.logInfo("Llegada de comisiom ---> " + anOriginalRequest.readValueFieldInHeader("comision"));
     }*/

        SUPPORT_OFFLINE = true;

        // Valida firmas fisicas
   /*     IProcedureResponse responseSigner = new ProcedureResponseAS();
        responseSigner.setReturnCode(0);
        aBagSPJavaOrchestration.put(RESPONSE_QUERY_SIGNER, responseSigner);*/

        if (message != null) {

            // METODO GUARDAR CAMPOS SEPARADOS QUE EXISTAN
            IProcedureResponse pResponse = insertSpeiCentarlExecution(anOriginalRequest, aBagSPJavaOrchestration);
            logInfo(CLASS_NAME + "[executeStepsTransactionsBase] Resultado: " + resultado.isResultado());
            if (resultado.isResultado()) {
                logInfo(CLASS_NAME + "[executeStepsTransactionsBase] Categoria: " + message.getCategoria());
                if (message.getCategoria() != null) {
                    if (message.getCategoria().equals("ODPS_LIQUIDADAS_ABONOS")) {
                        responseTransfer = invokeNotifyDeposit(anOriginalRequest, aBagSPJavaOrchestration);
                    } else if (message.getCategoria().equals("ODPS_LIQUIDADAS_CARGOS")) {
                        aBagSPJavaOrchestration.put(STATUS_TRANSACTION, "LIQUIDADO");
                        responseTransfer = invokeNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration);
                    } else if (message.getCategoria().equals("ODPS_CANCELADAS_LOCAL")) {
                        aBagSPJavaOrchestration.put(STATUS_TRANSACTION, "CANCELADO");
                        responseTransfer = invokeNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration);
                    }
                }
                logInfo(CLASS_NAME + "[executeStepsTransactionsBase]: " + responseTransfer);
                logInfo(CLASS_NAME + "[executeStepsTransactionsBase]Tiene Error: " + responseTransfer.hasError());
                updateSpeiInDevolutionExecution(anOriginalRequest, aBagSPJavaOrchestration, (responseTransfer == null ? true : responseTransfer.hasError()));
            }
        }
        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }

    protected abstract void executeCreditTransferOrchest(IProcedureRequest request,
                                                         Map<String, Object> aBagSPJavaOrchestration);

    protected abstract Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

    protected abstract IProcedureResponse invokeNotifyDeposit(IProcedureRequest request,
                                                  Map<String, Object> aBagSPJavaOrchestration);

    protected abstract IProcedureResponse invokeNotifyStatus(IProcedureRequest request,
                                                  Map<String, Object> aBagSPJavaOrchestration);

    protected abstract IProcedureResponse updateSpeiInDevolutionExecution(IProcedureRequest request,
                                                             Map<String, Object> aBagSPJavaOrchestration, boolean isError);

    /**
     * Insert Spei: method that inserts a SPEI transaction
     *
     * @param originalRequest
     * @param bag
     * @return
     */
    protected IProcedureResponse insertSpeiCentarlExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        final String METHOD_NAME = "[insertSpeiCentarlExecution]";
        logInfo(METHOD_NAME + " [INI]");

        logInfo("Ejecutando metodo updateLocalExecution: " + anOriginalRequest.toString());

        IProcedureRequest request = initProcedureRequest(anOriginalRequest);

        ServerResponse responseServer = (ServerResponse) bag.get(RESPONSE_SERVER);
        IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);

        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
        request.setSpName("cob_bvirtual..sp_spei_operation_in");

        request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
        request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
        request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
        request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
        if (anOriginalRequest.readValueParam("@s_cliente") == null || "".equals(anOriginalRequest.readValueParam("@s_cliente"))) {
            request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_codigo_cliente"));
        }

        mensaje message = (mensaje) bag.get(SPEI_TRANSACTION);
        ValidaSpei resultado = (ValidaSpei) bag.get(RESULT_VALIDACION_SPEI);

        request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");
        request.addInputParam("@i_mensaje", ICTSTypes.SQLVARCHAR, message.getCategoria());
        request.addInputParam("@i_fecha_oper", ICTSTypes.SQLDATETIME, message.getOrdenpago().getOpFechaOper());
        request.addInputParam("@i_folio", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpFolio());
        request.addInputParam("@i_ins_clave", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpInsClave());
        request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, message.getOrdenpago().getOpMonto().toString());
        request.addInputParam("@i_tp_clave", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpTpClave());
        request.addInputParam("@i_cve_rastreo", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpCveRastreo());
        request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpEstado());
        request.addInputParam("@i_tipo_orden", ICTSTypes.SQLCHAR, message.getOrdenpago().getOpTipoOrden());
        request.addInputParam("@i_prioridad", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpPrioridad());
        request.addInputParam("@i_me_clave", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpMeClave());
        request.addInputParam("@i_topologia", ICTSTypes.SQLCHAR, message.getOrdenpago().getOpTopologia());
        request.addInputParam("@i_usu_clave", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpUsuClave());
        request.addInputParam("@i_fecha_cap", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpFechaCap());
        request.addInputParam("@i_clave", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpClave());
        request.addInputParam("@i_nom_ord", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpNomOrd());
        request.addInputParam("@i_tc_clave_ord", ICTSTypes.SQLVARCHAR, "" + message.getOrdenpago().getOpTcClaveOrd());
        request.addInputParam("@i_cuenta_ord", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpCuentaOrd());
        request.addInputParam("@i_rfc_curp_ord", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpRfcCurpOrd());
        request.addInputParam("@i_nom_ben", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpNomBen());
        request.addInputParam("@i_tc_clave_ben", ICTSTypes.SQLVARCHAR, "" + message.getOrdenpago().getOpTcClaveBen());
        request.addInputParam("@i_cuenta_ben", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpCuentaBen());
        request.addInputParam("@i_rfc_curp_ben", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpRfcCurpBen());
        request.addInputParam("@i_concepto_pag2", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpConceptoPag2());
        request.addInputParam("@i_iva", ICTSTypes.SQLMONEY, "" + message.getOrdenpago().getOpIva());
        request.addInputParam("@i_ref_numerica", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpRefNumerica());
        request.addInputParam("@i_ref_cobranza", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpRefCobranza());
        request.addInputParam("@i_folio_servidor", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpFolioServidor());
        request.addInputParam("@i_usu_autoriza", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpUsuAutoriza());
        request.addInputParam("@i_err_clave", ICTSTypes.SQLINT4, "" + message.getOrdenpago().getOpErrClave());
        request.addInputParam("@i_razon_rechazo", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpRazonRechazo());
        request.addInputParam("@i_hora_cap", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpHoraCap());
        request.addInputParam("@i_hora_liq_bm", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpHoraLiqBm());
        request.addInputParam("@i_hora_liq_sist", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpHoraLiqSist());
        request.addInputParam("@i_cde", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpCde());
        request.addInputParam("@i_firma_dig", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpFirmaDig());
        request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, (String) bag.get(XML_REQUEST));
        request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, (String) bag.get(XML_RESPONSE));
        // CAMPOS DE VALIDACION
        request.addInputParam("@i_error_codigo", ICTSTypes.SQLINT4, "" + resultado.getCodigoError());
        request.addInputParam("@i_error_descripcion", ICTSTypes.SQLVARCHAR, resultado.getDescripcionError());
        request.addInputParam("@i_estado_job", ICTSTypes.SQLVARCHAR, (resultado.isResultado() ? ESTADO_SPEI_PENDIENTE : ESTADO_SPEI_ERROR));

        logInfo(METHOD_NAME + "DATA------>>" + responseServer.getOnLine());

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

        if (bag.get("@s_error") != null && !"0".equals(bag.get("@s_error"))) {
            Utils.addInputParam(request, "@s_error", ICTSTypes.SQLVARCHAR, bag.get("@s_error").toString());
        }

        // envio de t_rty
        if (logger.isInfoEnabled())
            logger.logInfo("Update local param reentryExecution" + request.readValueFieldInHeader("reentryExecution"));
        request.removeParam("@t_rty");


        logger.logInfo(METHOD_NAME + ", request: " + request.getProcedureRequestAsString());

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        logger.logInfo(METHOD_NAME + ", response: " + pResponse.getProcedureResponseAsString());
        logger.logInfo(METHOD_NAME + " [FIN]");
        return pResponse;
    }

    /**
     * Select Spei In: method to select an incoming SPEI transaction
     *
     * @param originalRequest
     * @param bag
     * @return
     */
    protected IProcedureResponse selectSpeiInCentarlExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        final String METHOD_NAME = "[selectSpeiInCentarlExecution]";
        logInfo(METHOD_NAME + " [INI]");

        logDebug("Ejecutando metodo updateLocalExecution: " + anOriginalRequest.toString());

        IProcedureRequest request = initProcedureRequest(anOriginalRequest);

        ServerResponse responseServer = (ServerResponse) bag.get(RESPONSE_SERVER);
        IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);

        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));
        request.setSpName("cob_bvirtual..sp_spei_operation_in");

        request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn_branch"));
        request.addInputParam("@s_ssn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@s_ssn"));
        request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
        request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));
        if( anOriginalRequest.readValueParam("@s_cliente") == null || "".equals(anOriginalRequest.readValueParam("@s_cliente"))){
            request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_codigo_cliente"));
        }

        mensaje message=(mensaje)bag.get(SPEI_TRANSACTION);

        request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
        request.addInputParam("@i_cve_rastreo", ICTSTypes.SQLVARCHAR, message.getOrdenpago().getOpCveRastreo());

        logInfo(METHOD_NAME + "DATA------>>" + responseServer.getOnLine());

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
        /*if (Boolean.TRUE.equals(getFromReentryExcecution(bag))) {
            Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
        } else { // no es ejecucion de reentry
            Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
        }*/

        logDebug(METHOD_NAME + ", request: " + request.getProcedureRequestAsString());

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        logDebug(METHOD_NAME + ", response: " + pResponse.getProcedureResponseAsString());
        logInfo(METHOD_NAME + " [FIN]");
        return pResponse;
    }

    private List<mensaje> transformResponseToMensajeSpei(IProcedureResponse aResponse) {
        final String METHOD_NAME = "[insertSpeiCentarlExecution]";
        logInfo(METHOD_NAME + " [INI]");

        List<mensaje> listMensaje = new ArrayList<mensaje>();
        mensaje mensajeResponse = null;

        IResultSetRow[] rows = aResponse.getResultSet(1).getData().getRowsAsArray();
        logDebug(METHOD_NAME + ", Registros: " + rows.length);
        if(rows.length != 0){
            for (IResultSetRow iResultSetRow : rows) {
                IResultSetRowColumnData[] cols = iResultSetRow.getColumnsAsArray();
                mensajeResponse = new mensaje();
                mensajeResponse.setOrdenpago(new ordenpago());
                mensajeResponse.setCategoria(cols[0].getValue());
                mensajeResponse.getOrdenpago().setOpFechaOper(cols[1].getValue());
                mensajeResponse.getOrdenpago().setOpFolio(Integer.parseInt(cols[2].getValue()));
                mensajeResponse.getOrdenpago().setOpInsClave(Integer.parseInt(cols[3].getValue()));
                mensajeResponse.getOrdenpago().setOpMonto(new BigDecimal(cols[4].getValue()));
                mensajeResponse.getOrdenpago().setOpTpClave(Integer.parseInt(cols[5].getValue()));
                mensajeResponse.getOrdenpago().setOpCveRastreo(cols[6].getValue());
                mensajeResponse.getOrdenpago().setOpEstado(cols[7].getValue());
                mensajeResponse.getOrdenpago().setOpTipoOrden(cols[8].getValue());
                mensajeResponse.getOrdenpago().setOpPrioridad(Integer.parseInt(cols[9].getValue()));
                mensajeResponse.getOrdenpago().setOpMeClave(Integer.parseInt(cols[10].getValue()));
                mensajeResponse.getOrdenpago().setOpTopologia(cols[11].getValue());
                mensajeResponse.getOrdenpago().setOpUsuClave(cols[12].getValue());
                if(cols[13].isNull())
                    mensajeResponse.getOrdenpago().setOpFechaCap(cols[13].getValue());
                if(cols[14].isNull())
                    mensajeResponse.getOrdenpago().setOpClave(Integer.parseInt(cols[14].getValue()));
                mensajeResponse.getOrdenpago().setOpNomOrd(cols[15].getValue());
                mensajeResponse.getOrdenpago().setOpTcClaveOrd(Integer.parseInt(cols[16].getValue()));
                mensajeResponse.getOrdenpago().setOpCuentaOrd(cols[17].getValue());
                mensajeResponse.getOrdenpago().setOpRfcCurpOrd(cols[18].getValue());
                mensajeResponse.getOrdenpago().setOpNomBen(cols[19].getValue());
                mensajeResponse.getOrdenpago().setOpTcClaveBen(Integer.parseInt(cols[20].getValue()));
                mensajeResponse.getOrdenpago().setOpCuentaBen(cols[21].getValue());
                if(cols[22].isNull())
                    mensajeResponse.getOrdenpago().setOpRfcCurpBen(cols[22].getValue());
                mensajeResponse.getOrdenpago().setOpConceptoPag2(cols[23].getValue());
                if(cols[24].isNull())
                    mensajeResponse.getOrdenpago().setOpIva(new BigDecimal(cols[24].getValue()));
                mensajeResponse.getOrdenpago().setOpRefNumerica(Integer.parseInt(cols[25].getValue()));
                if(cols[26].isNull())
                    mensajeResponse.getOrdenpago().setOpRefCobranza(cols[26].getValue());
                if(cols[27].isNull())
                    mensajeResponse.getOrdenpago().setOpFolioServidor(Integer.parseInt(cols[27].getValue()));
                if(cols[28].isNull())
                    mensajeResponse.getOrdenpago().setOpUsuAutoriza(cols[28].getValue());
                if(cols[29].isNull())
                    mensajeResponse.getOrdenpago().setOpErrClave(Integer.parseInt(cols[29].getValue()));
                if(cols[30].isNull())
                    mensajeResponse.getOrdenpago().setOpRazonRechazo(cols[30].getValue());
                if(cols[31].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraCap(cols[31].getValue());
                if(cols[32].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraLiqBm(cols[32].getValue());
                if(cols[33].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraLiqSist(cols[33].getValue());
                if(cols[34].isNull())
                    mensajeResponse.getOrdenpago().setOpCde(cols[34].getValue());

                mensajeResponse.getOrdenpago().setOpFirmaDig(cols[35].getValue());

                listMensaje.add(mensajeResponse);
            }
        }
        logInfo(METHOD_NAME + " [FIN]");
        return listMensaje;
    }

    private void logDebug(Object aMessage){
        if(logger.isDebugEnabled()){
            logger.logDebug(aMessage);
        }
    }

    private void logInfo(Object aMessage){
        if(logger.isInfoEnabled()){
            logger.logInfo(aMessage);
        }
    }
}
