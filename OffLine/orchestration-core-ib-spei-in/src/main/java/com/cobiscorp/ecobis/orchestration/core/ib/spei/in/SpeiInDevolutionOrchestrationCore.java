package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ordenpago;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi.Constants;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi.dto.AccendoConnectionData;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferInOfflineTemplate;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Component(name = "SpeiInDevolutionOrchestrationCore", immediate = false)
@Service(value = {ICISSPBaseOrchestration.class, IOrchestrator.class})
@Properties(value = {@Property(name = "service.description", value = "SpeiInDevolutionOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
        @Property(name = "service.identifier", value = "SpeiInDevolutionOrchestrationCore")})
public class SpeiInDevolutionOrchestrationCore extends TransferInOfflineTemplate {

    private static ILogger logger = LogFactory.getLogger(SpeiInDevolutionOrchestrationCore.class);

    protected static final String SPEI_CONNECTION_DATA = "SpeiConnectionData";
    protected static final String SPEI_RESPONSE_EXECUTE = "SpeiResponseExecute";
    protected static final String SPEI_CLAVE_RASTREO = "SpeiClaveRastreo";
    protected static final String ERROR_DEVOLUCION_SPEI = "ERROR EN DEVOLUCION SPEI";
    protected static final String ESTADO_SPEI_PENDIENTE_DEVOLUCION = "PDEV";
    protected static final String ESTADO_SPEI_DEVUELTA = "DEV";
    protected static final String ESTADO_SPEI_ERROR_DEVUELTA = "EDEV";

    @Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
    public ICoreServiceSendNotification coreServiceNotification;

    public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = service;
    }

    public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = null;
    }

    @Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
    protected ICoreServer coreServer;

    protected void bindCoreServer(ICoreServer service) {
        coreServer = service;
    }

    protected void unbindCoreServer(ICoreServer service) {
        coreServer = null;
    }

    @Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
    protected ICoreService coreService;

    public void bindCoreService(ICoreService service) {
        coreService = service;
    }

    public void unbindCoreService(ICoreService service) {
        coreService = null;
    }

    @Override
    protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
        String wInfo = "[SpeiInDevolutionOrchestrationCore][executeTransfer] ";
        logInfo(wInfo + "[INI]");

        IProcedureResponse response = null;
        try {
            IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
            logDebug(wInfo + "aSpeiDevolutionRequest: " + anOriginalRequest.getProcedureRequestAsString());
            // CONSULTA DE ORDENES POR DEVOLVER
            response = selectSpeiInDevolutionExecution(anOriginalRequest, aBagSPJavaOrchestration);
            if (!response.hasError()) {
                // CONVERTIR A OBJETO
                List<mensaje> lstMensaje = transformResponseToMensajeSpei(response);
                logDebug(wInfo + " Lista Mensaje Spei: " + lstMensaje.size());
                // LEER DATOS DE CONEXION
                AccendoConnectionData speiConnectionData = retrieveAccendoConnectionData();
                aBagSPJavaOrchestration.put(SPEI_CONNECTION_DATA, speiConnectionData);
                // RECORRO LA LISTA
                for (mensaje iterMensaje : lstMensaje) {
                    try {
                        // LIMPIO DATOS
                        aBagSPJavaOrchestration.remove(SPEI_CLAVE_RASTREO);
                        aBagSPJavaOrchestration.remove(SPEI_RESPONSE_EXECUTE);
                        aBagSPJavaOrchestration.put(SPEI_CLAVE_RASTREO, iterMensaje.getOrdenpago().getOpCveRastreo());
                        logInfo(wInfo + "INICIA PROCESO: " + aBagSPJavaOrchestration.get(SPEI_CLAVE_RASTREO) + " Intento: " + iterMensaje.getOrdenpago().getIntento());
                        // ENVIO LA SPEI
                        IProcedureResponse responseTransfer = executeSpei(anOriginalRequest, aBagSPJavaOrchestration, iterMensaje);
                        // CAMBIO DE ESTADO - SI ES CORRECTO SE ACTUALIZA CON EXITOSO, CASO CONTRARIO SE VUELVE A PDEV PENDIENTE DE DEVOLUCION
                        logDebug(wInfo + "EXISTE ERROR EN PROCESO: " + iterMensaje.getOrdenpago().getOpCveRastreo() + " -> " + responseTransfer.hasError());
                        updateSpeiInDevolutionExecution(anOriginalRequest, aBagSPJavaOrchestration, iterMensaje, responseTransfer.hasError());
                        logInfo(wInfo + "FINALIZA PROCESO: " + aBagSPJavaOrchestration.get(SPEI_CLAVE_RASTREO));
                    } catch (Exception e) {
                        logger.logError(wInfo + "AN ERROR OCURRED: ", e);
                        logInfo(wInfo + "ERROR OCURRIDO PROCESO: " + aBagSPJavaOrchestration.get(SPEI_CLAVE_RASTREO));
                        // SE REGISTRA EN EL LOG
                        aBagSPJavaOrchestration.remove(SPEI_RESPONSE_EXECUTE);
                        speiEntrante(anOriginalRequest, aBagSPJavaOrchestration);
                        // EN CASO DE ERROR VOLVER A ESTADO PENDIENTE DE DEVOLUCION SOLO LA ORDEN ACTUAL
                        updateSpeiInDevolutionExecution(anOriginalRequest, aBagSPJavaOrchestration, iterMensaje, true);
                    }
                }
            }
        } catch (Exception e) {
            logger.logError("AN ERROR OCURRED: ", e);
        }
        logInfo(wInfo + "[FIN]");
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
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest iProcedureRequest, Map<String, Object> map) {
        String wInfo = "[SpeiInDevolutionOrchestrationCore][executeJavaOrchestration] ";
        logInfo(wInfo + "[INI]");

        IProcedureResponse response = null;
        IProcedureRequest originalByNotify = iProcedureRequest;

        logInfo("SpeiInStatusOrchestrationCore: executeJavaOrchestration");

        Map<String, Object> mapInterfaces = new HashMap<String, Object>();

        mapInterfaces.put("coreServer", coreServer);
        mapInterfaces.put("coreService", coreService);
        mapInterfaces.put("coreServiceNotification", coreServiceNotification);

        Utils.validateComponentInstance(mapInterfaces);
        map.put(TRANSFER_NAME, "DEVOLUTION SPEI IN");
        try {
            response = executeStepsTransactionsBase(iProcedureRequest, map);
        } catch (CTSServiceException e) {
            e.printStackTrace();
        } catch (CTSInfrastructureException e) {
            e.printStackTrace();
        }

        if (response != null && !response.hasError() && response.getReturnCode() == 0) {
            String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
            if (null == idDevolucion || "0".equals(idDevolucion)) {
                logDebug(wInfo + "notifySpei");
                // notifySpei(iProcedureRequest, map);
            }
        }
        logInfo(wInfo + "[FIN]");
        return processResponse(iProcedureRequest, map);
    }

    protected IProcedureResponse selectSpeiInDevolutionExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        final String METHOD_NAME = "[selectSpeiInDevolutionExecution]";
        logInfo(METHOD_NAME + " [INI]");

        logDebug("Ejecutando metodo selectSpeiInDevolutionExecution: " + anOriginalRequest.toString());

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

        // OPERACION DE CONSULTAS PARA LAS DEVOLUCIONES
        request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "D");

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
                logInfo("Parametro @o_clave no encontrado");
            }
        }

        logInfo("Transaccion ejecutando en linea: " + responseServer.getOnLine());

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
        logInfo("Update local param reentryExecution" + request.readValueFieldInHeader("reentryExecution"));
        request.removeParam("@t_rty");

        logDebug(METHOD_NAME + ", request: " + request.getProcedureRequestAsString());

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        logDebug(METHOD_NAME + ", response: " + pResponse.getProcedureResponseAsString());
        logInfo(METHOD_NAME + " [FIN]");
        return pResponse;
    }

    protected IProcedureResponse updateSpeiInDevolutionExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag, mensaje aMensaje, boolean isError) {
        final String METHOD_NAME = "[updateSpeiInDevolutionExecution]";
        logInfo(METHOD_NAME + " [INI]");

        logDebug("Ejecutando metodo updateSpeiInDevolutionExecution: " + anOriginalRequest.toString());

        String nuevoEstado = ESTADO_SPEI_PENDIENTE_DEVOLUCION; // PENDIENTE
        if(isError){
            if(aMensaje.getOrdenpago().getIntento() >= 3)
                nuevoEstado = ESTADO_SPEI_ERROR_DEVUELTA; // ERROR EN DEVOLUCION
        } else {
            nuevoEstado = ESTADO_SPEI_DEVUELTA; // DEVUELTA
        }

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

        // OPERACION  PARA ACTUALIZAR LAS ORDENES ENTRANTES
        request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "U");
        logInfo(METHOD_NAME + " CLAVE RASTREO: " + aMensaje.getOrdenpago().getOpCveRastreo());
        logInfo(METHOD_NAME + " INTENTO: " + aMensaje.getOrdenpago().getIntento());
        logInfo(METHOD_NAME + " CON ERROR: " + isError);
        logInfo(METHOD_NAME + " NUEVO ESTADO: " + nuevoEstado);
        request.addInputParam("@i_cve_rastreo", ICTSTypes.SQLCHAR, aMensaje.getOrdenpago().getOpCveRastreo());
        request.addInputParam("@i_estado_job", ICTSTypes.SQLCHAR, nuevoEstado);


        logDebug(METHOD_NAME + ", request: " + request.getProcedureRequestAsString());

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        logDebug(METHOD_NAME + ", response: " + pResponse.getProcedureResponseAsString());
        logInfo(METHOD_NAME + " [FIN]");
        return pResponse;
    }

    private List<mensaje> transformResponseToMensajeSpei(IProcedureResponse aResponse) {
        final String METHOD_NAME = "[transformResponseToMensajeSpei]";
        logInfo(METHOD_NAME + " [INI]");

        List<mensaje> listMensaje = new ArrayList<mensaje>();
        mensaje mensajeResponse = null;

        IResultSetRow[] rows = aResponse.getResultSet(1).getData().getRowsAsArray();
        logDebug(METHOD_NAME + ", Registros: " + rows.length);
        if (rows.length != 0) {
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
                if (!cols[13].isNull())
                    mensajeResponse.getOrdenpago().setOpFechaCap(cols[13].getValue());
                if (!cols[14].isNull())
                    mensajeResponse.getOrdenpago().setOpClave(Integer.parseInt(cols[14].getValue()));
                mensajeResponse.getOrdenpago().setOpNomOrd(cols[15].getValue());
                mensajeResponse.getOrdenpago().setOpTcClaveOrd(Integer.parseInt(cols[16].getValue()));
                mensajeResponse.getOrdenpago().setOpCuentaOrd(cols[17].getValue());
                mensajeResponse.getOrdenpago().setOpRfcCurpOrd(cols[18].getValue());
                mensajeResponse.getOrdenpago().setOpNomBen(cols[19].getValue());
                mensajeResponse.getOrdenpago().setOpTcClaveBen(Integer.parseInt(cols[20].getValue()));
                mensajeResponse.getOrdenpago().setOpCuentaBen(cols[21].getValue());
                if (!cols[22].isNull())
                    mensajeResponse.getOrdenpago().setOpRfcCurpBen(cols[22].getValue());
                mensajeResponse.getOrdenpago().setOpConceptoPag2(cols[23].getValue());
                if (!cols[24].isNull())
                    mensajeResponse.getOrdenpago().setOpIva(new BigDecimal(cols[24].getValue()));
                mensajeResponse.getOrdenpago().setOpRefNumerica(Integer.parseInt(cols[25].getValue()));
                if (!cols[26].isNull())
                    mensajeResponse.getOrdenpago().setOpRefCobranza(cols[26].getValue());
                if (!cols[27].isNull())
                    mensajeResponse.getOrdenpago().setOpFolioServidor(Integer.parseInt(cols[27].getValue()));
                if (!cols[28].isNull())
                    mensajeResponse.getOrdenpago().setOpUsuAutoriza(cols[28].getValue());
                if (!cols[29].isNull())
                    mensajeResponse.getOrdenpago().setOpErrClave(Integer.parseInt(cols[29].getValue()));
                if (!cols[30].isNull())
                    mensajeResponse.getOrdenpago().setOpRazonRechazo(cols[30].getValue());
                if (!cols[31].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraCap(cols[31].getValue());
                if (!cols[32].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraLiqBm(cols[32].getValue());
                if (!cols[33].isNull())
                    mensajeResponse.getOrdenpago().setOpHoraLiqSist(cols[33].getValue());
                if (!cols[34].isNull())
                    mensajeResponse.getOrdenpago().setOpCde(cols[34].getValue());

                mensajeResponse.getOrdenpago().setOpFirmaDig(cols[35].getValue());
                mensajeResponse.getOrdenpago().setEstadoJob(cols[36].getValue());
                if (!cols[38].isNull())
                    mensajeResponse.getOrdenpago().setIntento(Integer.parseInt(cols[38].getValue()));

                listMensaje.add(mensajeResponse);
            }
        }
        logInfo(METHOD_NAME + " [FIN]");
        return listMensaje;
    }

    private IProcedureResponse getAccendoProfile() {
        final String wInfo = "[getAccendoProfile]";
        logInfo(wInfo + "[INI]");

        IProcedureRequest request = new ProcedureRequestAS();
        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500065");
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_CENTRAL);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_get_catalog_by_table");
        request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500065");
        request.addInputParam("@tabla", ICTSTypes.SYBVARCHAR, "cl_spei_io");
        request.addInputParam("@operacion", ICTSTypes.SYBVARCHAR, "Q");

        IProcedureResponse response = executeCoreBanking(request);
        logInfo(wInfo + "[FIN]");
        return response;
    }

    private AccendoConnectionData retrieveAccendoConnectionData() {
        final String wInfo = "[retrieveAccendoConnectionData]";
        logInfo(wInfo + "[INI]");


        IProcedureResponse response = null;
        int sequence = 20;
        int accumulated = 0;

        response = getAccendoProfile();

        AccendoConnectionData accendoConnectionData = new AccendoConnectionData();

        if (response != null && response.getResultSetListSize() > 0) {
            logInfo(wInfo + "jcos V2 resultados validacion");

            IResultSetBlock block = response.getResultSet(1);
            if (block != null && block.getData().getRowsNumber() >= 1) {
                IResultSetData data = block.getData();
                for (IResultSetRow row : data.getRowsAsArray()) {
                    logInfo(wInfo + "jcos Access to Data parameter");

                    logInfo(wInfo + "names " + this.getString(row, 2));
                    logInfo(wInfo + "valor " + this.getString(row, 3));

                    if (this.getString(row, 2).equals(Constants.COMPANY_ID)) {
                        accendoConnectionData.setCompanyId(this.getString(row, 3));
                    }

                    if (this.getString(row, 2).equals(Constants.BASE_URL)) {
                        accendoConnectionData.setBaseUrl(this.getString(row, 3));
                    }

                    if (this.getString(row, 2).equals(Constants.TRACKING_KEY_PREFIX)) {
                        accendoConnectionData.setTrackingKeyPrefix(this.getString(row, 3));
                    }

                    if (this.getString(row, 2).equals(Constants.ALGN_URI)) {
                        accendoConnectionData.setAlgnUri(this.getString(row, 3));
                    }

                    if (this.getString(row, 2).equals(Constants.CERT_URI)) {
                        accendoConnectionData.setCertUri(this.getString(row, 3));
                    }

                    if( this.getString(row, 2).equals(Constants.SPEI_DUMMY)){
                        accendoConnectionData.setSpeiDummy(this.getString(row, 3));
                    }

                    if( this.getString(row, 2).equals(Constants.TIME_INIT_DAY)){
                        accendoConnectionData.setTimeInitDay(this.getString(row, 3));
                    }
                }
            }
        }

        logger.logInfo(wInfo + "[FIN]");

        return accendoConnectionData;

    }

    private IProcedureResponse executeSpei(IProcedureRequest originalRequest, Map<String, Object> aBagSPJavaOrchestration, mensaje aMensaje) {
        final String wInfo = "[executeSpei]";
        logInfo(wInfo + "[INI]");

        // SE LLAMA LA SERVICIO DE BANPAY REVERSA DE REVERSA
        IProcedureResponse responseTransfer = SpeiExecution(originalRequest, aBagSPJavaOrchestration, aMensaje);
        if (responseTransfer != null) {
            if (!"00".equals(responseTransfer.readValueParam("@o_cod_respuesta"))) {
                logDebug("Error SPEI");
                // DATOS PARA INSERTAR LINEA DE LOG CON ERROR
                speiEntrante(originalRequest, aBagSPJavaOrchestration);
                return Utils.returnException(1, ERROR_DEVOLUCION_SPEI);
            } else {
                logDebug("Paso exitoso");
                // DATOS PARA INSERTAR LINEA DE LOG DE DEVOLUCION EXITOSO
                speiEntrante(originalRequest, aBagSPJavaOrchestration);
                //SE REGISTRA EN LOCAL EL ENVIO DE DEVOLUCION EXITOSO
                responseTransfer = persistDataLocalSpei(originalRequest, aBagSPJavaOrchestration);
            }
        } else {
            logDebug("responseTransfer null");
            // DATOS PARA INSERTAR LINEA DE LOG CON ERROR
            aBagSPJavaOrchestration.remove(SPEI_RESPONSE_EXECUTE);
            speiEntrante(originalRequest, aBagSPJavaOrchestration);
            return Utils.returnException(1, ERROR_DEVOLUCION_SPEI);
        }
        return responseTransfer;
    }

    protected IProcedureResponse SpeiExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag, mensaje aMensaje) {
        String wInfo = "[SpeiInDevolutionOrchestrationCore][SpeiExecution] ";
        logInfo(wInfo + "[INI]");

        IProcedureResponse connectorSpeiResponse = null;
        try {
            ordenpago wOrden = aMensaje.getOrdenpago();
            // SE SETEAN LOS PARAMETROS DE ENTRADA
            anOriginalRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR, wOrden.getOpConceptoPag2());
            anOriginalRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, wOrden.getOpCuentaBen());
            anOriginalRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR, wOrden.getOpCuentaOrd());

            //FECHA
            logDebug(wInfo + "Fecha Operación Antes: " + wOrden.getOpFechaOper());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date wFecha = format.parse(wOrden.getOpFechaOper());
            logDebug(wInfo + "Fecha Operación Despues: " + wFecha);
            SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
            anOriginalRequest.addInputParam("@i_fecha_operacion", ICTSTypes.SQLVARCHAR, forma.format(wFecha));
            anOriginalRequest.addInputParam("@i_institucion_contraparte", ICTSTypes.SQLVARCHAR, "" + wOrden.getOpInsClave());
            // anOriginalRequest.addInputParam("@i_institucion_operante", ICTSTypes.SQLVARCHAR, data.get(0));
            anOriginalRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, "" + wOrden.getOpMonto());
            anOriginalRequest.addInputParam("@i_nombre_beneficiario", ICTSTypes.SQLVARCHAR, wOrden.getOpNomBen());
            anOriginalRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, wOrden.getOpNomOrd());
            anOriginalRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, "" + wOrden.getOpRefNumerica()); // OPCIONAL
            anOriginalRequest.addInputParam("@i_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, wOrden.getOpRfcCurpBen()); // OPCIONAL
            anOriginalRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, wOrden.getOpRfcCurpOrd());
            anOriginalRequest.addInputParam("@i_tipo_cuenta_beneficiario", ICTSTypes.SQLINT1, "" + wOrden.getOpTcClaveBen());
            anOriginalRequest.addInputParam("@i_tipo_cuenta_ordenante", ICTSTypes.SQLINT1, "" + wOrden.getOpTcClaveOrd());

            anOriginalRequest.addInputParam("@i_tipo_pago", ICTSTypes.SQLINT1, "0");  // TIPO DE PAGO 0 CORRESPONDIENTE A DEVOLUCION NO ACREDITADA DEL DIA
            anOriginalRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_ssn"));

            //DatosAccendo
            // anOriginalRequest.addInputParam("@i_beneficiario_cc", ICTSTypes.SQLINT1, data.get(4));
            // anOriginalRequest.addInputParam("@i_tercer_ordenante", ICTSTypes.SQLINT1, data.get(5));

            anOriginalRequest.addInputParam("@i_cuenta_clabe", ICTSTypes.SQLVARCHAR, wOrden.getOpCuentaOrd());
            // VARIABLES DE SALIDA
            anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_msj_respuesta", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_id", ICTSTypes.SQLINT1, "0");
            anOriginalRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "X");

            AccendoConnectionData loadded = (AccendoConnectionData) bag.get(SPEI_CONNECTION_DATA);

            anOriginalRequest.addInputParam("@i_empresa", ICTSTypes.SQLVARCHAR, loadded.getCompanyId());
            anOriginalRequest.addInputParam("@i_algotih", ICTSTypes.SQLVARCHAR, "SHA256withRSA");
            anOriginalRequest.addInputParam("@i_prefijo_rastreo", ICTSTypes.SQLVARCHAR, loadded.getTrackingKeyPrefix());
            anOriginalRequest.addInputParam("@i_base_url", ICTSTypes.SQLVARCHAR, loadded.getBaseUrl());
            anOriginalRequest.addInputParam("@i_algn_path", ICTSTypes.SQLVARCHAR, loadded.getAlgnUri());
            anOriginalRequest.addInputParam("@i_cert_path", ICTSTypes.SQLVARCHAR, loadded.getCertUri());
            // DEVOLUCION NO MANEJA CALCULO DE FECHA PORQUE TODAS LAS DEVOLUCIONES DEBEN SER DEL MISMO DIA,
            // CASO CONTRARIO ENTRARIAN COMO ERROR EN DEVOLUCION Y SE DEBE REALIZAR UN PROCESO OPERATIVO EN EL ADMIN DE KARPAY
            anOriginalRequest.addInputParam("@i_time_init_day", ICTSTypes.SQLVARCHAR, loadded.getTimeInitDay());
            anOriginalRequest.addInputParam("@i_spei_dummy", ICTSTypes.SQLVARCHAR, loadded.getSpeiDummy());

            anOriginalRequest.addInputParam("@i_clave_rastreo_connection", ICTSTypes.SQLVARCHAR, wOrden.getOpCveRastreo());
            bag.put("@i_clave_rastreo", wOrden.getOpCveRastreo());

            //anOriginalRequest.addInputParam("@i_transaccion_spei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_transaccion_spei"));
            anOriginalRequest.addInputParam("@i_transaccion_spei", ICTSTypes.SQLVARCHAR, "" + wOrden.getOpRefNumerica());
            anOriginalRequest.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_ssn_branch"));

            // SE HACE LA LLAMADA AL CONECTOR
            bag.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorSpei)");
            anOriginalRequest.setSpName("cob_procesador..sp_orq_banpay_spei");
            anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
            anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1870013");

            // SE EJECUTA
            connectorSpeiResponse = executeProvider(anOriginalRequest, bag);
            // SE VALIDA LA RESPUESTA
            if (!connectorSpeiResponse.hasError()) {
                logDebug(wInfo + "success CISConnectorSpei: true");
                logDebug(wInfo + "connectorSpeiResponse: " + connectorSpeiResponse.getParams());

                logDebug(wInfo + "readValueParam @o_cod_respuesta: " + connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
                logDebug(wInfo + "readValueParam @o_msj_respuesta: " + connectorSpeiResponse.readValueParam("@o_msj_respuesta"));

                logDebug(wInfo + "readValueParam @o_clave_rastreo:" + connectorSpeiResponse.readValueParam("@o_clave_rastreo"));

                // SE ALMACENA EL DATO DE CLAVE DE RASTREO
                String rastreo = connectorSpeiResponse.readValueParam("@o_clave_rastreo");
                if (null == rastreo) {
                    rastreo = anOriginalRequest.readValueParam("i_clave_rastreo_connection");
                }
                Map<String, Object> bagResponse = new HashMap<String, Object>();
                bagResponse.put("@i_clave_rastreo", rastreo);
                bagResponse.put("@i_msj_respuesta", connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
                bagResponse.put("@i_cod_respuesta", connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
                bagResponse.put("@i_id", connectorSpeiResponse.readValueParam("@o_id"));
                bagResponse.put("@i_descripcion_error", connectorSpeiResponse.readValueParam("@o_descripcion_error"));

                bagResponse.put("@i_mensaje_acc", connectorSpeiResponse.readValueParam("@i_mensaje_acc"));
                bagResponse.put("@i_id_spei_acc", connectorSpeiResponse.readValueParam("@i_id_spei_acc"));
                bagResponse.put("@i_codigo_acc", connectorSpeiResponse.readValueParam("@i_codigo_acc"));
                logDebug(wInfo + "transaccion Spei " + anOriginalRequest.readValueParam("@i_transaccion_spei"));
                bagResponse.put("@i_transaccion_spei", anOriginalRequest.readValueParam("@i_transaccion_spei"));
                logDebug(wInfo + "i_ssn_branch origin" + anOriginalRequest.readValueParam("@i_ssn_branch"));
                bagResponse.put("@i_ssn_branch", anOriginalRequest.readValueParam("@i_ssn_branch"));

                bagResponse.put("@o_spei_request", connectorSpeiResponse.readValueParam("@o_spei_request"));
                bagResponse.put("@o_spei_response", connectorSpeiResponse.readValueParam("@o_spei_response"));

                bagResponse.put("@o_transaccion_spei", anOriginalRequest.readValueParam("@i_transaccion_spei"));

                bag.put(SPEI_RESPONSE_EXECUTE, bagResponse);
            } else {
                logDebug(wInfo + "Error Catastrifico respuesta de SPEI");
                logDebug(wInfo + "Error connectorSpeiResponse Catastrifico: " + connectorSpeiResponse);
            }
        } catch (Exception e) {
            logger.logError(e);
            logger.logInfo(wInfo + "Error Catastrofico de SpeiExecution");
            e.printStackTrace();
            logger.logInfo(wInfo + "Error Catastrofico de SpeiExecution");

        } finally {
            logger.logInfo(wInfo + "[FIN]");
        }
        // SE REGRESA RESPUESTA
        return connectorSpeiResponse;
    }

    private IProcedureResponse persistDataLocalSpei(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        String wInfo = "[persistDataLocalSpei] ";
        logger.logInfo(wInfo + "init task ----> ");
        IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

        // SE SETEAN DATOS
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_registra_spei");
        request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
        request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "L");
        request.addInputParam("@i_ente_bv", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
        request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_login"));
        request.addInputParam("@i_canal", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_servicio"));
        request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
        request.addInputParam("@i_cuenta_des", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));
        request.addInputParam("@i_monto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_val"));
        request.addInputParam("@i_moneda", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));
        request.addInputParam("@i_banco_dest", ICTSTypes.SQLVARCHAR, bag.get("@i_banco_dest") != null ? bag.get("@i_banco_dest").toString() : "");
        request.addInputParam("@i_cuenta_clabe_dest", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));
        request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "A");  // ESTADO A DE APLICADO EN CASO DE QUE SE HAYA ENVIADO CORRECTAMENTE
        request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo") != null ? bag.get("@i_clave_rastreo").toString() : "");
        request.addInputParam("@i_proceso_origen", ICTSTypes.SQLINT1, "1");
        request.addInputParam("@i_mensaje_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_mensaje_acc") != null ? bag.get("@i_mensaje_acc").toString() : "");
        request.addInputParam("@i_codigo_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_codigo_acc") != null ? bag.get("@i_codigo_acc").toString() : "");
        request.addInputParam("@i_transaccion_spei", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_transaccion_spei"));
        request.addInputParam("@i_spei_request", ICTSTypes.SQLVARCHAR, bag.get("@o_spei_request") != null ? bag.get("@o_spei_request").toString() : "");
        request.addInputParam("@i_spei_response", ICTSTypes.SQLVARCHAR, bag.get("@o_spei_response") != null ? bag.get("@o_spei_response").toString() : "");
        request.addInputParam("@i_reference_number", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_reference_number"));

        // SE SETEA VARIABLE DE SALIDA
        request.addOutputParam("@o_salida", ICTSTypes.SYBVARCHAR, "0");

        // SE EJECUTA Y SE OBTIENE LA RESPUESTA
        IProcedureResponse pResponse = executeCoreBanking(request);

        logger.logInfo(wInfo + "end task ----> ");

        return pResponse;

    }

    private IProcedureResponse speiEntrante(IProcedureRequest anOriginalRequest, Map<String, Object> map) {
        String wInfo = "[speiEntrante] ";
        logInfo(wInfo + "[INI]");

        BigInteger messageNumber = new BigInteger("999");
        String messageText = "ERROR EN CONECTOR SPEI";
        String xmlRequest = null;

        Map<String, Object> mapResponse = null;
        if (map.containsKey(SPEI_RESPONSE_EXECUTE)) {
            mapResponse = (Map) map.get(SPEI_RESPONSE_EXECUTE);
            logDebug(wInfo + SPEI_RESPONSE_EXECUTE + ": " + ((Map) map.get(SPEI_RESPONSE_EXECUTE)));
            messageNumber = new BigInteger((String) mapResponse.get("@i_cod_respuesta"));
            messageText = (String) mapResponse.get("@i_msj_respuesta");
            xmlRequest = (String) mapResponse.get("@o_spei_request");
        }

        boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

        IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

        procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
        procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
        procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");

        procedureRequest.addInputParam("@i_val", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));
        procedureRequest.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "249");
        procedureRequest.addInputParam("@i_causa_comi", ICTSTypes.SYBVARCHAR, "250");
        procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
        procedureRequest.addInputParam("@i_fecha", ICTSTypes.SYBDATETIME, anOriginalRequest.readValueFieldInHeader("date"));
        procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "9");

        procedureRequest.addInputParam("@i_codigo_error", ICTSTypes.SYBINT4, String.valueOf(messageNumber));
        procedureRequest.addInputParam("@i_descripcion_error", ICTSTypes.SYBVARCHAR, messageText);
        procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "D");
        procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, (String) map.get(SPEI_CLAVE_RASTREO));
        procedureRequest.addInputParam("@i_xml_request", ICTSTypes.SYBVARCHAR, xmlRequest);
        procedureRequest.addInputParam("@i_tipo_ejecucion", ICTSTypes.SYBVARCHAR, (isReentryExecution ? "F" : "L"));

        IProcedureResponse procedureResponseSpeiEntrante = executeCoreBanking(procedureRequest);

        messageText = procedureResponseSpeiEntrante.readValueFieldInHeader("messageError");
        logInfo("Mensaje SPEI Entrante: " + messageText);

        procedureResponseSpeiEntrante.addParam("@o_descripcion_error", ICTSTypes.SYBCHAR, 255, messageText);

        map.put("procedureResponseSpeiEntrante", procedureResponseSpeiEntrante);

        return procedureResponseSpeiEntrante;
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest iProcedureRequest, Map<String, Object> map) {
        return (IProcedureResponse) map.get(RESPONSE_TRANSACTION);
    }

    @Override
    public void loadConfiguration(IConfigurationReader iConfigurationReader) {

    }

    public static Object getObject(IResultSetRow row, int col) {
        IResultSetRowColumnData iResultColumnData = null;
        iResultColumnData = row.getRowData(col);
        return (null == iResultColumnData) ? null : iResultColumnData.getValue();
    }


    public String getString(IResultSetRow row, int col) {
        String resultado = null;
        Object obj = null;
        try {
            obj = getObject(row, col);
            resultado = (null == obj) ? null : obj.toString();
            if (null != resultado) {
                resultado = (resultado.trim().equalsIgnoreCase("null") ? null : resultado);
            }
        } catch (Exception e) {
            logger.logError("[getString] Error obteniendo cadena de respuesta CTS.", e);
        }
        return resultado;
    }

    private static void logDebug(Object aMessage) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(aMessage);
        }
    }

    private static void logInfo(Object aMessage) {
        if (logger.isInfoEnabled()) {
            logger.logInfo(aMessage);
        }
    }
}
