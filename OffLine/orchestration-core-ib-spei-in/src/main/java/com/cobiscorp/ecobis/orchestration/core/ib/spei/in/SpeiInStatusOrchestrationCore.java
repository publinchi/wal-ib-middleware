package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.*;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.services.orchestrator.ISPOrchestrator;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.*;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferInOfflineTemplate;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;

import java.util.*;

@Component(name = "SpeiInStatusOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SpeiInStatusOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
        @Property(name = "service.identifier", value = "SpeiInStatusOrchestrationCore") })
public class SpeiInStatusOrchestrationCore extends TransferInOfflineTemplate {

    private static ILogger logger = LogFactory.getLogger(SpeiInStatusOrchestrationCore.class);

    private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";
    private static final String MAP_REVERSE_SPEI = "MAP_REVERSE_SPEI";

    @Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
    protected ICoreServer coreServer;

    /**
     * Instance Service Interface
     *
     * @param service
     */
    protected void bindCoreServer(ICoreServer service) {
        coreServer = service;
    }

    /**
     * Deleting Service Interface
     *
     * @param service
     */
    protected void unbindCoreServer(ICoreServer service) {
        coreServer = null;
    }

    @Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
    protected ICoreService coreService;

    /**
     * Instance Service Interface
     *
     * @param service
     */
    public void bindCoreService(ICoreService service) {
        coreService = service;
    }

    /**
     * Deleting Service Interface
     *
     * @param service
     */
    public void unbindCoreService(ICoreService service) {
        coreService = null;
    }

    @Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
    protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

    public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
        coreServiceMonetaryTransaction = service;
    }

    public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
        coreServiceMonetaryTransaction = null;
    }

    @Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
    public ICoreServiceSendNotification coreServiceNotification;

    /**
     * Instance Service Interface
     *
     * @param service
     */
    public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = service;
    }

    /**
     * Deleting Service Interface
     *
     * @param service
     */
    public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = null;
    }


    @Override
    protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {

        String wInfo = "[SpeiServiceOrchestration][notifyStatus] ";
        logInfo(wInfo + "[INI]");

        IProcedureResponse response = null;
        try {
            IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
            logDebug(wInfo + "aSpeiStatusRequest: " + anOriginalRequest.getProcedureRequestAsString());
            response = executeSpNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration, "G");

            if (response.hasError()) {
                logDebug(wInfo + END_OPERATION + response);
                executeSpNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration, "H");
                return response;
            }

            if (!"LIQUIDADO".equals(anOriginalRequest.readValueParam("@i_estado_act"))) {
                logDebug(wInfo + "reverseOperation" + response);
                // GENERAR OPERACION DE REVERSO
                aBagSPJavaOrchestration.put(MAP_REVERSE_SPEI, getParams(response));
                IProcedureResponse responseReverseSpei = executeSpReverseSpei(anOriginalRequest, aBagSPJavaOrchestration);
                logDebug(wInfo + "Response Reverse SPEI: " +responseReverseSpei.getProcedureResponseAsString());
                executeSpNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration, "H");
                return response;
            }

            executeSpNotifyStatus(anOriginalRequest, aBagSPJavaOrchestration, "H");

        } catch (Exception e) {
            logger.logError("AN ERROR OCURRED: ", e);
        }
        logDebug("[executeTransfer] Response: " + response);
        return response;
    }

    @Override
    public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
        return null;
    }

    @Override
    protected ICoreServiceSendNotification getCoreServiceNotification() {
        return coreServiceNotification;
    }

    @Override
    public ICoreService getCoreService() {
        return coreService;
    }

    @Override
    public ICoreServer getCoreServer() {
        return coreServer;
    }

    @Override
    protected IProcedureResponse validateCentralExecution(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
        return null;
    }

    @Override
    public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest, OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
        return null;
    }

    @Override
    protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest, IProcedureRequest anOriginalRequest) {

    }

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        IProcedureResponse response = null;
        IProcedureRequest originalByNotify = anOriginalRequest;

        if (logger.isInfoEnabled())
            logger.logInfo("SpeiInStatusOrchestrationCore: executeJavaOrchestration");

        Map<String, Object> mapInterfaces = new HashMap<String, Object>();

        mapInterfaces.put("coreServer", coreServer);
        mapInterfaces.put("coreService", coreService);
        mapInterfaces.put("coreServiceNotification", coreServiceNotification);

        Utils.validateComponentInstance(mapInterfaces);
        aBagSPJavaOrchestration.put(TRANSFER_NAME, "STATUS SPEI IN");
        aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
        try {
            response = executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (CTSServiceException e) {
            e.printStackTrace();
        } catch (CTSInfrastructureException e) {
            e.printStackTrace();
        }

        if (response != null && !response.hasError() && response.getReturnCode() == 0) {
            String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
            if(null == idDevolucion || "0".equals(idDevolucion)){
                notifySpei(anOriginalRequest, aBagSPJavaOrchestration);
            }
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }

    @Override
    public void loadConfiguration(IConfigurationReader iConfigurationReader) {

    }

    private void notifySpei (IProcedureRequest anOriginalRequest, java.util.Map map) {

        try {
            ServerResponse serverResponse = (ServerResponse) map.get(RESPONSE_SERVER);

            //Por definicion funcional no se notifica en modo offline
            if(Boolean.FALSE.equals(serverResponse.getOnLine())){
                return;
            }

            logInfo(CLASS_NAME + "Enviando notificacion spei");

            IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

            String cuentaClave=anOriginalRequest.readValueParam("@i_cuenta_beneficiario");

            logInfo(CLASS_NAME + "using clabe account account "+cuentaClave);

            procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
            procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S',"local");
            procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1800195");

            procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
            procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
            // procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
            procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
            procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N145");
            procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
            procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
            procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
            procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
            procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "spei");
            procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaClave);
            procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
            procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
            procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
            procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, String.valueOf(  anOriginalRequest.readValueParam("@i_monto")));
            procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
            procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
            procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));


            IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

            logInfo("jcos proceso de notificaciom terminado");

        }catch(Exception xe) {
            logger.logError("Error en la notficación de spei recibida", xe);
        }
    }

    public IProcedureResponse executeSpNotifyStatus(IProcedureRequest anOriginalRequest,
                                                        Map<String, Object> aBagSPJavaOrchestration, String aOperation) {

        String wInfo = "[SpeiInStatusOrchestrationCore][executeSpNotifyStatus]";
        logInfo(wInfo + "[INI]");
        IProcedureResponse response = new ProcedureResponseAS();

        IProcedureRequest requestTransfer = initializeSpNotifyStatus(anOriginalRequest, aOperation);

        logDebug(wInfo + "Request TransferStatus: " + requestTransfer.getProcedureRequestAsString());

        response = executeCoreBanking(requestTransfer);

        logDebug(wInfo + "response de local: " + response);
        logInfo(wInfo + "Proceso con Error? " + response.hasError());
        logInfo(wInfo + END_TASK);

        return response;
    }

    private IProcedureRequest initializeSpNotifyStatus(IProcedureRequest anOriginalRequest, String aOperation) {

        String wInfo = "[SpeiInStatusOrchestrationCore][initializeSpNotifyStatus]";

        logInfo(wInfo + INIT_TASK);

        IProcedureRequest aProcedureRequest = initProcedureRequest(anOriginalRequest);
        aProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500068");
        aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        aProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        aProcedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

        aProcedureRequest.setSpName("cob_bvirtual..sp_registra_spei");
        aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18010");
        aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18010");

        aProcedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, aOperation);


        aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18010");
        aProcedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, aOperation);

        // if(null != anOriginalRequest.readValueParam("")) //aSpeiStatusRequest.getId())
        // aProcedureRequest.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("")); // String.valueOf(aSpeiStatusRequest.getId()));
        if (null != anOriginalRequest.readValueParam("@i_claveRastreo")) {
            aProcedureRequest.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo")); // String.valueOf(aSpeiStatusRequest.getId()));
            aProcedureRequest.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
        }
        if (null != anOriginalRequest.readValueParam("@i_estado_act")) //aSpeiStatusRequest.getEstado())
            aProcedureRequest.addInputParam("@i_estado_act", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_estado_act")); // aSpeiStatusRequest.getEstado());
        if (null != anOriginalRequest.readValueParam("@i_causaDevolucion")) { //aSpeiStatusRequest.getCausaDevolucion()){
            aProcedureRequest.addInputParam("@i_causa_devolucion", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_causaDevolucion")); // aSpeiStatusRequest.getCausaDevolucion());
            aProcedureRequest.addInputParam("@i_descripcion_error", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_descripcion_error")); // aSpeiStatusRequest.getCausaDevolucion());
        }
        if (null != anOriginalRequest.readValueParam("@i_xml_response")) //aSpeiStatusRequest.getResponse())
            aProcedureRequest.addInputParam("@i_xml_res", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_xml_response")); // aSpeiStatusRequest.getResponse());
        if (null != anOriginalRequest.readValueParam("@i_ts_liquidacion")) //aSpeiStatusRequest.getTsLiquidacion())
            aProcedureRequest.addInputParam("@i_ts_liquidacion", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ts_liquidacion")); // String.valueOf(aSpeiStatusRequest.getTsLiquidacion()));
        if (null != anOriginalRequest.readValueParam("@i_empresa")) //aSpeiStatusRequest.getEmpresa())
            aProcedureRequest.addInputParam("@i_empresa", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_empresa")); // String.valueOf(aSpeiStatusRequest.getEmpresa()));

        aProcedureRequest.addInputParam("@i_xml_req", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_xml_request")); // aSpeiStatusRequest.toString());

        aProcedureRequest.addOutputParam("@o_id_resultado", ICTSTypes.SQLINT4, "");
        aProcedureRequest.addOutputParam("@o_resultado", ICTSTypes.SQLVARCHAR, "");
        aProcedureRequest.addOutputParam("@o_i_cuenta_ori", ICTSTypes.SYBVARCHAR, "");
        aProcedureRequest.addOutputParam("@o_i_concepto", ICTSTypes.SYBVARCHAR, "");
        aProcedureRequest.addOutputParam("@o_i_monto", ICTSTypes.SYBMONEY, "");
        aProcedureRequest.addOutputParam("@o_i_mon", ICTSTypes.SYBINT4, "");
        aProcedureRequest.addOutputParam("@o_i_servicio", ICTSTypes.SYBINT4, "");
        aProcedureRequest.addOutputParam("@o_i_tipo_error", ICTSTypes.SYBINT4, "");
        aProcedureRequest.addOutputParam("@o_i_comision", ICTSTypes.SYBMONEY, "");
        aProcedureRequest.addOutputParam("@o_i_proceso_origen", ICTSTypes.SYBINT4, "");
        aProcedureRequest.addOutputParam("@o_i_transaccion_core", ICTSTypes.SYBINT4, "");

        logInfo(wInfo + END_TASK);

        return aProcedureRequest;
    }

    public IProcedureResponse executeSpReverseSpei(IProcedureRequest anOriginalRequest,
                                                    Map<String, Object> aBagSPJavaOrchestration) {

        String wInfo = "[SpeiInStatusOrchestrationCore][executeSpReverseSpei]";
        logInfo(wInfo + "[INI]");
        IProcedureResponse response = new ProcedureResponseAS();

        IProcedureRequest requestTransfer = initializeSpReverseSpei(anOriginalRequest, aBagSPJavaOrchestration);

        logDebug(wInfo + "Request ReverseSpei: " + requestTransfer.getProcedureRequestAsString());

        response = executeCoreBanking(requestTransfer);

        logDebug(wInfo + "response de local: " + response);
        logInfo(wInfo + "Proceso con Error? " + response.hasError());
        logInfo(wInfo + END_TASK);

        return response;
    }

    private IProcedureRequest initializeSpReverseSpei(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

        String wInfo = "[SpeiInStatusOrchestrationCore][initializeSpReverseSpei]";

        logInfo(wInfo + INIT_TASK);

        IProcedureRequest aProcedureRequest = initProcedureRequest(anOriginalRequest);
        aProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500068");
        aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_CENTRAL);
        aProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        aProcedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

        aProcedureRequest.setSpName("cob_bvirtual..sp_reverso_spei");
        aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18009");
        aProcedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18009");

        Map wOutputParams = (Map<String, Object>) aBagSPJavaOrchestration.get(MAP_REVERSE_SPEI);
        logDebug(wInfo + " Map Revserse Spei: " + wOutputParams.toString());
        if (wOutputParams.containsKey("@o_i_cuenta_ori")) {
            aProcedureRequest.addInputParam("@i_cuenta_ori", ICTSTypes.SYBVARCHAR, (String) wOutputParams.get("@o_i_cuenta_ori"));
        }
        if (wOutputParams.containsKey("@o_i_concepto")) {
            aProcedureRequest.addInputParam("@i_concepto", ICTSTypes.SYBVARCHAR, (String) wOutputParams.get("@o_i_concepto"));
        }
        if (wOutputParams.containsKey("@o_i_monto")) {
            aProcedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY, (String) wOutputParams.get("@o_i_monto"));
        }
        if (wOutputParams.containsKey("@o_i_mon")) {
            aProcedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, (String) wOutputParams.get("@o_i_mon"));
        }
        if (wOutputParams.containsKey("@o_i_servicio")) {
            aProcedureRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4, (String) wOutputParams.get("@o_i_servicio"));
        }
        if (wOutputParams.containsKey("@o_i_tipo_error")) {
            aProcedureRequest.addInputParam("@i_tipo_error", ICTSTypes.SYBINT4, (String) wOutputParams.get("@o_i_tipo_error"));
        }
        if (wOutputParams.containsKey("@o_i_comision")) {
            aProcedureRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEY, (String) wOutputParams.get("@o_i_comision"));
        }
        if (wOutputParams.containsKey("@o_i_proceso_origen")) {
            aProcedureRequest.addInputParam("@i_proceso_origen", ICTSTypes.SYBINT4, (String) wOutputParams.get("@o_i_proceso_origen"));
        }
        if (wOutputParams.containsKey("@o_i_transaccion_core")) {
            aProcedureRequest.addInputParam("@i_transaccion_core", ICTSTypes.SYBINT4, (String) wOutputParams.get("@o_i_transaccion_core"));
        }

        aProcedureRequest.addOutputParam("@o_id_resultado", ICTSTypes.SQLINTN, "");
        aProcedureRequest.addOutputParam("@o_resultado", ICTSTypes.SQLVARCHAR, "");

        logInfo(wInfo + END_TASK);
        return aProcedureRequest;
    }

    public static Map<String, Object> getParams(IProcedureResponse wProcedureResponseSpei){
        String wInfo = "[SpeiInStatusOrchestrationCore][getParams]";
        Iterator wIt = wProcedureResponseSpei.getParams().iterator();
        Map wOutputParams = new HashMap();

        while(wIt.hasNext()) {
            IProcedureResponseParam w = (IProcedureResponseParam) wIt.next();
            if (logger.isDebugEnabled()) {
                logger.logDebug(wInfo + "<<<<<<<<<< wProcedureResponseSpei.getResultSets() >>>>>>>>>> "+ wProcedureResponseSpei.getResultSets());
                logger.logDebug(wInfo + "<<<<<<<<<< w.getValue() >>>>>>>>>> "+ w.getValue());
                logger.logDebug(wInfo + "<<<<<<<<<< w.getValue() >>>>>>>>>> "+ w.getName());
            }
            wOutputParams.put(w.getName(), w.getValue());
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug(wInfo + "<<<<<<<<<< wOutputParams >>>>>>>>>> "+ wOutputParams);
        }

        return wOutputParams;
    }

    private static void logDebug(Object aMessage){
        if(logger.isDebugEnabled()){
            logger.logDebug(aMessage);
        }
    }

    private static void logInfo(Object aMessage){
        if(logger.isInfoEnabled()){
            logger.logInfo(aMessage);
        }
    }
}
