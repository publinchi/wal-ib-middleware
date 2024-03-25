package com.cobiscorp.ecobis.orchestration.core.ib.api.template;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.*;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.*;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class ApplicationApiBase extends SPJavaOrchestrationBase {

    protected static final String CLASS_NAME = " >-----> ";
    private static final String COBIS_CONTEXT = "COBIS";
    protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
    protected static final String RESPONSE_VALIDATE_LOCAL= "RESPONSE_VALIDATE_LOCAL";
    protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
    protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
    protected static final String APPLICATION_NAME = "APPLICATION_NAME";
    protected static final String LOG_MESSAGE = "LOG_MESSAGE";
    protected static final int TRN_MANAGERCHECK_REQUEST = 1800120;
    protected static final int TRN_CHECKBOOK_REQUEST = 1800005;
    protected static final int TRN_INDIVIDUALCHECKPREAUT_REQUEST = 1801001;
    protected static final int TRN_MASIVCHECKPREAUT_REQUEST = 1875038;
    protected static final int TRN_SEARCHOFFICAL_REQUEST = 1800196;
    protected static final int TRN_APPLICATION_DISBURSEMENT = 1800264;
    protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
    protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
    protected static final String RESPONSE_OFFLINE ="RESPONSE_OFFLINE";
    protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION";
    protected static final int CODE_OFFLINE = 40004;
    protected static final int CODE_OFFLINE_WITHOUT_BAL = 40002;
    protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
    protected static final String ACCOUNTING_PARAMETER_COMMISSION = "ACCOUNTING_PARAMETER_COMMISSION";

    public String graba_tranMonet = "S";

    /**
     * Constant controller offline functionality activation.<br>
     * When this value is true the functionality is enabled.
     */
    public boolean SUPPORT_OFFLINE = false;


    private static ILogger logger = LogFactory.getLogger(ApplicationsBaseTemplate.class);
    protected abstract ICoreService getCoreService();
    protected abstract ICoreServer getCoreServer();

    public abstract ICoreServiceNotification getCoreServiceNotification();
    public abstract ICoreServiceSendNotification getCoreServiceSendNotification();
    protected abstract ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction();


    public abstract NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest, OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration);

    /**
     * This method has to be override to implement call of service
     *
     * @param anOriginalRequest
     * @param aBagSPJavaOrchestration
     * @return
     * @throws CTSInfrastructureException
     * @throws CTSServiceException
     */
    protected abstract IProcedureResponse executeApplication(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)  ;
    /**
     * Contains primary steps for execution of Request
     *
     * @param anOriginalRequest
     * @param aBagSPJavaOrchestration
     * @return
     * @throws CTSInfrastructureException
     * @throws CTSServiceException
     */
    protected IProcedureResponse executeStepsApplicationBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {


        IProcedureResponse responseValidateCoreSigners  = null;
        IProcedureResponse responseNotification = null;
        IProcedureResponse responseValidateLocalExecution = null;
        IProcedureResponse responseExecuteQuery = null ;
        IProcedureResponse responseLocalExecution = null;
        AccountingParameterRequest requestAccountingParameters = null;

        if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "START");
        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
        String messageErrorApplication = null;
        messageErrorApplication =(String)aBagSPJavaOrchestration.get(APPLICATION_NAME);
        ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get("coreServiceMonetaryTransaction");
        ServerResponse responseServer = null;
  /*     ServerResponse responseServer =  validateServerStatus(anOriginalRequest, aBagSPJavaOrchestration);
        //responseServer.setOnLine(false);
        //responseServer.setOfflineWithBalances(true);
        aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);


        if(responseServer.getOnLine() ){

        }else {


        }*/


        responseValidateLocalExecution = validateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
        if (Utils.flowError(messageErrorApplication +" --> validateLocalExecution", responseValidateLocalExecution)) {
            if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateLocalExecution);
            return responseValidateLocalExecution;
        };

        aBagSPJavaOrchestration.put(RESPONSE_VALIDATE_LOCAL, responseValidateLocalExecution);

        requestAccountingParameters = new AccountingParameterRequest();
        if (logger.isDebugEnabled()) logger.logDebug("gya anOriginalRequest:" + anOriginalRequest);
        requestAccountingParameters.setOriginalRequest(anOriginalRequest);
        requestAccountingParameters.setTransaction(Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))));
        if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Servicio inyectado Monetary:" + getCoreServiceMonetaryTransaction());
        if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "reqqqq: " + requestAccountingParameters);
        coreServiceMonetaryTransaction = getCoreServiceMonetaryTransaction();
        //AccountingParameterResponse responseAccountingParameters =  coreServiceMonetaryTransaction.getAccountingParameter(requestAccountingParameters);
        AccountingParameterResponse responseAccountingParameters =  getCoreServiceMonetaryTransaction().getAccountingParameter(requestAccountingParameters);
        if (logger.isInfoEnabled())
            logger.logInfo("RESPONSE ACCOUNTING PARAMETERS -->"+responseAccountingParameters.getAccountingParameters().toString());

        if (!responseAccountingParameters.getSuccess()){
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseAccountingParameters);
            return Utils.returnException(responseAccountingParameters.getReturnCode(), new StringBuilder(messageErrorApplication).append(responseAccountingParameters.getMessage()).toString());
        }
        int producto = 0;
        if (anOriginalRequest.readValueParam("@i_prod")!= null)
            producto = 	Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString());

        Map<String, AccountingParameter> map   = existsAccountingParameter(responseAccountingParameters , producto, "T");
        if (map!=null)
            aBagSPJavaOrchestration.put(ACCOUNTING_PARAMETER, map.get("ACCOUNTING_PARAM"));


        Map<String,AccountingParameter> mapCommission   = existsAccountingParameter(responseAccountingParameters , producto, "C");
        if (mapCommission !=null)
            aBagSPJavaOrchestration.put(ACCOUNTING_PARAMETER_COMMISSION, mapCommission.get("ACCOUNTING_PARAM"));


        //Valida el fuera de línea

        if (logger.isInfoEnabled())
            logger.logInfo("Llama a la funcion validateBvTransaction");

        String responseSupportOffline = validateBvTransaction(aBagSPJavaOrchestration);

        if (logger.isInfoEnabled())
            logger.logInfo("responseSupportOffline ---> " + responseSupportOffline);

        if(responseSupportOffline == null || responseSupportOffline == "") {
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea"));
            return Utils.returnException("Ha ocurrido un error intentando validar si la transferencia permite fuera de línea");
        }

        if(responseSupportOffline.equals("S")) {
            SUPPORT_OFFLINE = true;
        }else {
            SUPPORT_OFFLINE = false;
        }

        if (!SUPPORT_OFFLINE && !responseServer.getOnLine()) {
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea"));
            return Utils.returnException("Transferencia no permite ejecución mientras el servidor este fuera de linea");
        }

        responseExecuteQuery = executeApplication(anOriginalRequest, aBagSPJavaOrchestration);
        if(responseServer.getOnLine() || (!responseServer.getOnLine() && responseServer.getOfflineWithBalances())){
            if (Utils.flowError(messageErrorApplication +" --> executeQuery", responseExecuteQuery)) {
                if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
                aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);

                if (responseServer.getOnLine() ){
                    if (!evaluateExecuteReentry(anOriginalRequest)){
                        if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "::Fin prematuro del flujo. No se ejecuto la transferencia.");
                        return responseExecuteQuery;
                    }
                }
                else{
                    if (evaluateExecuteReentry(anOriginalRequest)){
                        if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
                        return responseExecuteQuery;
                    }
                }
            }//;
        }


        aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseExecuteQuery);


        responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
        if (Utils.flowError(messageErrorApplication +" --> updateLocalExecution", responseLocalExecution)){
            if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + messageErrorApplication);
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
            return responseLocalExecution;
        }

        aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);




        if (logger.isInfoEnabled()) logger.logInfo("RESPONSE_TRANSACTION --> "+responseExecuteQuery.getProcedureResponseAsString());
        if (logger.isInfoEnabled()) logger.logInfo("RESPONSE_UPDATE_LOCAL --> "+responseLocalExecution.getProcedureResponseAsString());

        logger.logInfo("RESPONSE_TRANSACTION_ABT --> "+responseExecuteQuery.getProcedureResponseAsString());
        logger.logInfo("RESPONSE_UPDATE_LOCAL_ABT --> "+responseLocalExecution.getProcedureResponseAsString());

        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }




    //WMF

    //FIN WMF



    /**
     * Validation signers of account for authorization.
     */
    protected IProcedureResponse validateCoreSigners(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

        if (logger.isInfoEnabled()) logger.logInfo(CLASS_NAME + "Iniciando consulta de Firmas Fisicas");

        try {
            if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Servicio inyectado:" + getCoreService());

            SignerRequest signerRequest = new SignerRequest();
            Product product = new Product();
            Client client = new Client();

            if (!Utils.isNull(anOriginalRequest.readParam("@i_ente")))
                client.setId(anOriginalRequest.readValueParam("@i_ente"));


            if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
                product.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());


            signerRequest.setUser(client);
            signerRequest.setOriginProduct(product);
            if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
                signerRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));


            if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Consultando firmantes:" + signerRequest);
            SignerResponse signerResponse = getCoreService().getSignatureCondition(signerRequest);
            if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "Respuesta firmantes:" + signerResponse);

            IProcedureResponse res = new ProcedureResponseAS();
            res.addParam("@o_condiciones_firmantes", ICTSTypes.SQLVARCHAR, 0, (signerResponse.getSigner() != null ? signerResponse.getSigner().getCondition() : ""));

            if (logger.isDebugEnabled()) logger.logDebug(CLASS_NAME + "RESPUESTA CORE:" + res);
            res.setReturnCode(0);
            return res;
        }
        catch (CTSServiceException e) {
            return Utils.returnExceptionService(anOriginalRequest, e);
        }
        catch (CTSInfrastructureException e) {
            return Utils.returnExceptionService(anOriginalRequest, e);
        }
    }

    protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
        IProcedureRequest request = initProcedureRequest(anOriginalRequest);

        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));

        request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_bv_validacion");

        request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
        request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_monto")))
            request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));
        //else
        //request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto_solicitado"));
        if (logger.isInfoEnabled())
            logger.logInfo( "validateLocalExecution: Cuenta " + anOriginalRequest.readValueParam("@i_cta"));

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta")))
            Utils.copyParam("@i_cta", anOriginalRequest, request);

        if (logger.isInfoEnabled())
            logger.logInfo( "@i_valida_des:  " + anOriginalRequest.readValueParam("@i_valida_des"));

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_valida_des")))
            Utils.copyParam("@i_valida_des", anOriginalRequest, request);

        //en caso de que no se envíe la moneda va 0
        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_mon")))
            Utils.copyParam("@i_mon", anOriginalRequest, request);
        else
            request.addInputParam("@i_mon", ICTSTypes.SQLINT1, "0");

        Utils.copyParam("@i_prod", anOriginalRequest, request);

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_proposito")))
            request.addInputParam("@i_concepto",ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_proposito"));
        else
            request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, "Solicitud");

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
            request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(),anOriginalRequest.readValueParam("@i_login"));
        else{
            if (!Utils.isNull(anOriginalRequest.readValueParam("@i_usuario")))
                request.addInputParam("@i_login", anOriginalRequest.readParam("@i_usuario").getDataType(),anOriginalRequest.readValueParam("@i_usuario"));
        }

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_fecha")))
            request.addInputParam("@i_fecha", anOriginalRequest.readParam("@i_fecha").getDataType(),anOriginalRequest.readValueParam("@i_fecha"));

        Utils.copyParam("@i_doble_autorizacion", anOriginalRequest, request);
        Utils.copyParam("@i_ente", anOriginalRequest, request);
        request.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR,"N");

        request.addOutputParam("@o_cliente_mis", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_prod", ICTSTypes.SQLINT1, "0");
        request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        request.addOutputParam("@o_mon", ICTSTypes.SQLINT2, "0");
        request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT1, "0");
        request.addOutputParam("@o_cta_des", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT2, "0");
        request.addOutputParam("@o_retorno", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_condicion", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_srv_host", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
        request.addOutputParam("@o_autorizacion", ICTSTypes.SQLCHAR, "X");
        request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_cta_cobro", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
        request.addOutputParam("@o_prod_cobro", ICTSTypes.SQLINT1	, "0");
        request.addOutputParam("@o_cod_mis", ICTSTypes.SQLINT4	, "0");
        request.addOutputParam("@o_clave_bv", ICTSTypes.SYBINT4, "0");

        if (logger.isDebugEnabled()) {
            logger.logDebug("Validate local, request: "
                    + request.getProcedureRequestAsString());
        }

        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse response = executeCoreBanking(request);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Validate local, response: "
                    + response.getProcedureResponseAsString());
        }
        if (logger.isInfoEnabled()) {
            logger.logInfo("Finalize Validate local");
        }

        return response;
    }
    /**
     * updateLocalExecution .
     *
     * @param anOriginalRequest
     * @param aBagSPJavaOrchestration
     * @return
     * @throws CTSInfrastructureException
     * @throws CTSServiceException
     */
    protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest,Map<String, Object> bag) {
        IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());
        ServerResponse  responseServer = (ServerResponse)bag.get(RESPONSE_SERVER);
        IProcedureResponse responseBalance = (IProcedureResponse)bag.get(RESPONSE_BALANCE);
        IProcedureResponse applicationResponse=(IProcedureResponse)bag.get(RESPONSE_TRANSACTION);


        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,ICOBISTS.HEADER_STRING_TYPE,IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addInputParam("@t_trn", ICTSTypes.SYBINTN, request.readValueParam("@t_trn"));

        request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_bv_transaccion");
        request.addInputParam("@t_trn", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@t_trn"));
        request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
        request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
        request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, graba_tranMonet);
        request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
        request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
        request.addInputParam("@i_mon", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
        request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_prod"));
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_causa"));
        //request.addInputParam("@i_nombre_cr", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));
        request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tipo_notif"));
        if (logger.isInfoEnabled())
            logger.logInfo("Enviando la moneda del monto a consultar");
        if (anOriginalRequest.readValueParam("@i_moneda_monto") != null)
            request.addInputParam("@i_mon_2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_moneda_monto"));

        if (logger.isInfoEnabled())
            logger.logInfo("Application Base Templatet --> t_trn "+ anOriginalRequest.readValueParam("@t_trn"));

        if (Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))== TRN_APPLICATION_DISBURSEMENT)
            request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));

        if (Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))==TRN_MANAGERCHECK_REQUEST)
        {
            request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_monto"));
            request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_proposito"));
            request.addInputParam("@i_nombre_benef", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_beneficiario"));
        }
        else if (Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))==TRN_CHECKBOOK_REQUEST)
        {
            request.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tchq"));
            request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nchqs"));
        }
        else if (Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))==TRN_MASIVCHECKPREAUT_REQUEST || Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))==TRN_INDIVIDUALCHECKPREAUT_REQUEST)
        {
            request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "N");
        }

        if (anOriginalRequest.readParam("@i_login") == null) {
            request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));
        }
        else
            request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));


        /****Modif*/
        request.removeParam("@t_rty");
        if(evaluateExecuteReentry(anOriginalRequest)){
            request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
        }else{
            request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "N");
        }


        // obtener returnCode de ejecucion de Core
        if (!responseServer.getOnLine()){
            if(responseServer.getOfflineWithBalances())
                Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, String.valueOf(CODE_OFFLINE));
            else
                Utils.addInputParam(request,"@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(applicationResponse.getReturnCode())));

            if (applicationResponse.getMessageListSize() > 0){
                Utils.addInputParam(request,"@s_msg", ICTSTypes.SQLVARCHAR, (applicationResponse.getMessage(1).getMessageText()));
            }
        }

        if(!responseServer.getOnLine())//si esta fuera de linea
        {
            if (!Utils.isNull(request.readValueParam("@o_clave"))){
                Utils.addInputParam(request,"@i_clave_rty", request.readParam("@o_clave").getDataType(), request.readValueParam("@o_clave"));
            }else {
                if (logger.isInfoEnabled())logger.logInfo("Parámetro @o_clave no encontrado");
            }

            if(responseServer.getOfflineWithBalances()){
                request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "S");
            }else{
                request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
            }
            request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "F");
            request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "PN");
            request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
            request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_factor", ICTSTypes.SQLINTN, "1");
        }

        if(responseServer.getOnLine() || evaluateExecuteReentry(anOriginalRequest)){
            request.addInputParam("@i_fl_conslds", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, "L");
            request.addInputParam("@i_estado_ejec", ICTSTypes.SQLVARCHAR, "EJ");
            request.addInputParam("@i_estado_cta", ICTSTypes.SQLVARCHAR, "A");
            request.addInputParam("@i_time_out", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_error_ejec", ICTSTypes.SQLVARCHAR, "N");
            request.addInputParam("@i_factor", ICTSTypes.SQLINTN, "1");
        }

        //copia variables r_ como parametros de entrada para sincronizar saldos
        if (logger.isInfoEnabled())
            logger.logInfo("RESPONSE BALANCE " +responseBalance);
        if (!Utils.isNull(responseBalance)){
            if (responseBalance.getResultSetListSize() >0){

                IResultSetHeaderColumn[] columns = responseBalance.getResultSet(responseBalance.getResultSetListSize()).getMetaData().getColumnsMetaDataAsArray();
                IResultSetRow[] rows =  responseBalance.getResultSet(responseBalance.getResultSetListSize()).getData().getRowsAsArray();
                IResultSetRowColumnData[] cols =  rows[0].getColumnsAsArray();

                int i = 0;
                for (IResultSetHeaderColumn iResultSetHeaderColumn : columns) {
                    if (!iResultSetHeaderColumn.getName().equals(""))
                        if (cols[i].getValue()!=null){
                            if (logger.isDebugEnabled())
                                logger.logDebug("PARAMETROS AGREGADOS :"+iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_")+" VALOR: "+cols[i].getValue());
                            Utils.addInputParam(request, iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_"), iResultSetHeaderColumn.getType(), cols[i].getValue());
                        }
                    i++;
                }
            }
        }

        request.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR,"N");
        request.addOutputParam("@o_tipo_mensaje", ICTSTypes.SQLVARCHAR	, "F");
        request.addOutputParam("@o_numero_producto", ICTSTypes.SQLVARCHAR	, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        if (logger.isDebugEnabled()) {
            logger.logDebug("Update local, request: "
                    + request.getProcedureRequestAsString());
        }
        /* Ejecuta y obtiene la respuesta */
        IProcedureResponse pResponse = executeCoreBanking(request);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Update local, response: "
                    + pResponse.getProcedureResponseAsString());
        }
        if (logger.isInfoEnabled()) {
            logger.logInfo("Finalize Update local");
        }
        return pResponse;
    }
    /**
     * Find Officers of Account and Client
     */
    private OfficerByAccountResponse findOfficers(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException{
        OfficerByAccountRequest request = new OfficerByAccountRequest();
        Product product = new Product();

        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta"))) product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_prod"))) product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
        request.setProduct(product);

        return getCoreService().getOfficerByAccount(request);

    }

    /**
     * Send Notification
     */
    protected IProcedureResponse sendNotification(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration)throws CTSServiceException, CTSInfrastructureException {

        OfficerByAccountResponse findOfficersExecutionResponse = findOfficers(anOriginalRequest.clone(), aBagSPJavaOrchestration);
        NotificationRequest notificationRequest =  transformNotificationRequest(anOriginalRequest, findOfficersExecutionResponse, aBagSPJavaOrchestration);
        IProcedureResponse responseTransaction = (IProcedureResponse)aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
        ServerResponse responseServer = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

        Client client = new Client();
        client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));
        if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login"))){
            client.setLogin(anOriginalRequest.readValueParam("@i_login"));
        }

        notificationRequest.setClient(client);
        notificationRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));

        if (logger.isDebugEnabled())
            logger.logDebug(" validacion responseServer.getOnLine");

        if(!responseServer.getOnLine())
            notificationRequest.getNotification().setNotificationType("O");  //offline
        else
        {
            if (responseTransaction.getReturnCode() != 0){
                if (evaluateExecuteReentry(anOriginalRequest)){
                    notificationRequest.getNotification().setNotificationType("E");  //en linea, con error y por reentry
                    if (responseTransaction.getMessageListSize() > 0)
                        notificationRequest.getNotificationDetail().setAuxiliary10(generaMensaje(responseTransaction.getMessage(1).getMessageText()));
                }
            }
            else
                notificationRequest.getNotification().setNotificationType("F");  //en linea y ok
        }

        if (logger.isDebugEnabled())
            logger.logDebug(" llamada al getCoreServiceSendNotification.sendNotification");
        NotificationResponse notificationResponse =  getCoreServiceSendNotification().sendNotification(notificationRequest);


        if (!notificationResponse.getSuccess()){
            if (logger.isDebugEnabled()){
                logger.logDebug(" Error enviando notificación: "+notificationResponse.getMessage().getCode()+" - "+notificationResponse.getMessage().getDescription());
            }
        }

        IProcedureResponse response = new ProcedureResponseAS();
        response.setReturnCode(0);


        return response;

        //logger.logInfo("Finalize  findOfficersExecutionResponse");
        //IProcedureRequest requestNotification = transformNotificationRequest(aBagSPJavaOrchestration,findOfficersExecutionResponse);
        //logger.logInfo("Finalize  transformNotificationRequest :"+ requestNotification.toString());
        //IProcedureResponse notificationResponse =  getCoreServiceNotification().registerSendNotification(requestNotification,aBagSPJavaOrchestration);
        //logger.logInfo("Finalize  notificationResponse");
        //return notificationResponse;
        //return null;
    }
    /**
     * Transform Notification Request input param
     */
//	protected IProcedureRequest transformNotificationRequest( Map<String, Object> aBagSPJavaOrchestration,OfficerByAccountResponse anOfficer){
//		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
//		OfficerByAccountResponse officerResponse = anOfficer;
//		IProcedureResponse responseUpdateLocal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_UPDATE_LOCAL);
//		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
//		IProcedureRequest requestNotification = initProcedureRequest(anOriginalRequest);
//
//		String t_trn = anOriginalRequest.readValueParam("@t_trn");
//		requestNotification.addInputParam("@i_ente_ib", anOriginalRequest.readParam("@s_cliente").getDataType(), anOriginalRequest.readValueParam("@s_cliente"));
//		requestNotification.addInputParam("@i_producto", anOriginalRequest.readParam("@i_prod").getDataType(), anOriginalRequest.readValueParam("@i_prod"));
//		requestNotification.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
//		requestNotification.addInputParam("@i_transaccion_id", ICTSTypes.SYBINTN, t_trn);
//		requestNotification.addInputParam("@i_oficial_cli", ICTSTypes.SQLVARCHAR, officerResponse.getOfficer().getAcountEmailAdress());
//		requestNotification.addInputParam("@i_oficial_cta", ICTSTypes.SQLVARCHAR, officerResponse.getOfficer().getOfficerEmailAdress());
//		requestNotification.addInputParam("@i_m", anOriginalRequest.readParam("@i_mon").getDataType(), anOriginalRequest.readValueParam("@i_mon"));
//
//
//		if ("1800005".equals(t_trn)) {
//			requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));
//			requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR,responseTransaction.readValueParam("@o_tipo_chequera"));
//			requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nchqs"));
//			requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_dia_entrega"));
//			requestNotification.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombre_entrega"));
//			requestNotification.addInputParam("@i_aux5", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_id_entrega"));
//		}
//		else if ("1800120".equals(t_trn)) {
//			requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));
//			requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_oficina"));
//			requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_beneficiario"));
//			requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_retira_id"));
//			requestNotification.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_retira_nombre"));
//			requestNotification.addInputParam("@i_aux5", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_proposito"));
//			requestNotification.addInputParam("@i_md", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
//			requestNotification.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_monto"));
//			requestNotification.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_login"));
//		}
//		else if ("1800023".equals(t_trn)) {
//			requestNotification.addInputParam("@i_num_producto", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));
//			requestNotification.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cheque_ini"));
//			requestNotification.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_chq_hasta"));
//			requestNotification.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@o_causal"));
//			requestNotification.addInputParam("@i_m2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
//			requestNotification.addInputParam("@i_v3", ICTSTypes.SQLVARCHAR, responseUpdateLocal.readValueParam("@o_comision"));
//		}
//		requestNotification.addInputParam("@i_c1", anOriginalRequest.readParam("@i_cta").getDataType(), anOriginalRequest.readValueParam("@i_cta"));
//		requestNotification.addInputParam("@i_f", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
//		requestNotification.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_val"));
//		requestNotification.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));
//		requestNotification.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_ssn_branch"));
//
//		return requestNotification;
//	}

    protected ValidationAccountsRequest transformToValidationAccountRequest(IProcedureRequest anOriginalRequest){

        ValidationAccountsRequest request = new ValidationAccountsRequest();

        Product originProduct = new Product();
        Currency originCurrency = new Currency();
        if (anOriginalRequest.readValueParam("@i_cta") != null)
            originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
        else
            originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_deb"));

        if (anOriginalRequest.readValueParam("@i_prod") != null)
            originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
        else
            originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_deb").toString()));

        if (anOriginalRequest.readValueParam("@i_mon") != null)
            originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));
        else
            originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_deb").toString()));

        originProduct.setCurrency(originCurrency);
        Secuential originSSn = new Secuential();
        if (anOriginalRequest.readValueParam("@s_ssn") != null) originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());
        if (anOriginalRequest.readValueParam("@s_servicio") != null) request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
        if (anOriginalRequest.readValueParam("@t_trn") != null) request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

        request.setSecuential(originSSn);
        request.setOriginProduct(originProduct);
        request.setOriginalRequest(anOriginalRequest);
        return request;
    }

    protected IProcedureResponse getBalancesToSynchronize(IProcedureRequest anOriginalRequest){
        ValidationAccountsRequest validations = new ValidationAccountsRequest();
        validations = transformToValidationAccountRequest(anOriginalRequest);
        IProcedureResponse response = getCoreServiceMonetaryTransaction().getBalancesToSynchronize(validations);

        if (logger.isInfoEnabled())
            logger.logInfo("RESPONSE getBalancesToSynchronize -->"+response.getProcedureResponseAsString());

        return response ;
    }


    private void trnRegistration(IProcedureRequest aRequest, IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration) {

        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en transactionRegister");
        }

        request.setSpName("cob_bvirtual..sp_bv_transaction_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

        request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_date"));
        request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_culture"));

        request.addInputParam("@i_trn", ICTSTypes.SQLINTN, "18500114");
        request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_ente_bv"));
        request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
        request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
        request.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));
        request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
        request.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
        request.addInputParam("@i_prod", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_prod"));
        request.addInputParam("@i_prod_des", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_prod_des"));
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
        request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
        request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
        request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_ssn_branch"));
        request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
        request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));

        logger.logDebug("Request Corebanking registerLog: " + request.toString());

        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking transactionRegister: " + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de transactionRegister");
        }
    }


    protected IProcedureResponse saveReentry(IProcedureRequest wQueryRequest, Map<String, Object> aBagSPJavaOrchestration) {
        String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
        IProcedureRequest request = wQueryRequest.clone();
        ComponentLocator componentLocator = null;
        IReentryPersister reentryPersister = null;
        componentLocator = ComponentLocator.getInstance(this);

        aBagSPJavaOrchestration.put("rty_ssn",request.readValueFieldInHeader("ssn"));

        reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");

        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
        request.removeParam("@t_rty");

        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
            logger.logDebug("Ending flow, saveReentry failed");
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
            logger.logDebug("Ending flow, saveReentry success");
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;
    }

    public boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest){
        if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))){
            if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")){
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    public String generaMensaje(String vars){
        vars=vars.substring(vars.indexOf("]")+1,vars.length());
        return vars;
    }

    protected IProcedureResponse executeOfflineOperationTrasactionCobis(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()){
            logger.logInfo(CLASS_NAME + "Ejecutando Purchase API Operation" + anOriginalRequest);
            logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
            logger.logInfo("********** CAUSA ORIGEN --->>> " + "1010");
            logger.logInfo("********** CAUSA COMISI --->>> " + "185");
            logger.logInfo("********** CAUSA DESTIN --->>> " + "1020");

            logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));

        }
        //IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
        anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);


        anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

        anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api");


        anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18306");
        anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
        anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
        anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
        anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, "1010");
        anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, "1020");

        anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
        anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");


        anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");


        anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("o_ente_bv"));

        anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
        anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());

        anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon_des").toString());
        anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod_des").toString());

        anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");

        anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
        anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
        anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
        anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");


        anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
        anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
        anOriginalRequest.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));


        if (logger.isDebugEnabled())
            logger.logDebug(CLASS_NAME + "Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
        anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));


        anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

        if (logger.isDebugEnabled())
            logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest);
        IProcedureResponse response = executeCoreBanking(anOriginalRequest);

        if (logger.isInfoEnabled())
            logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

        logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
        response.readValueParam("@o_fecha_tran");

        logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
        if(response.readValueFieldInHeader("ssn")!=null) {
            aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));
            aBagSPJavaOrchestration.put("transactionOff", response.readValueFieldInHeader("true"));
        }

        return response;
    }

    private Map<String,AccountingParameter> existsAccountingParameter(AccountingParameterResponse anAccountingParameterResponse, int product, String type ){

        Map<String,AccountingParameter> map = null;

        if (anAccountingParameterResponse.getAccountingParameters().size()==0)
            return map;

        for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {


            if (parameter.getTypeCost().equals(type) && parameter.getProductId()== product){
                if (logger.isDebugEnabled())
                    logger.logDebug("SI HAY TRN: "+String.valueOf(parameter.getTransaction())+" CAUSA: "+ parameter.getCause()+" TIPO :"+parameter.getTypeCost() );
                map = new HashMap<String,AccountingParameter>();
                map.put("ACCOUNTING_PARAM", parameter);
                break;
            }
        }
        return map;
    }

    /**
     * validateBvTransaction: local account, virtual signers checking
     *
     * @param aBagSPJavaOrchestration
     * @return String
     */
    protected String validateBvTransaction(Map<String, Object> aBagSPJavaOrchestration) {

        if (logger.isInfoEnabled()) {
            logger.logInfo("Initialize method validateBvTransaction");
        }

        String responseSupportOffline = "N";

        //valida la parametria de la tabla bv_transaccion
        IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
        IProcedureRequest request = initProcedureRequest(originalRequest);

        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800090");
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_bv_transaction_context");

        request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "IB");
        request.addInputParam("@i_transaccion", ICTSTypes.SQLINTN, originalRequest.readValueParam("@t_trn"));
        request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, originalRequest.readValueParam("@s_servicio"));

        request.addOutputParam("@o_autenticacion", ICTSTypes.SYBCHAR, "N");
        request.addOutputParam("@o_fuera_de_linea", ICTSTypes.SYBCHAR, "N");
        request.addOutputParam("@o_doble_autorizacion", ICTSTypes.SYBCHAR, "N");
        request.addOutputParam("@o_sincroniza_saldos", ICTSTypes.SYBCHAR, "N");
        request.addOutputParam("@o_mostrar_costo", ICTSTypes.SYBCHAR, "N");
        request.addOutputParam("@o_tipo_costo", ICTSTypes.SYBCHAR, "N");

        if (logger.isInfoEnabled()) {
            logger.logInfo("Finalize method validateBvTransaction");
        }

        // Ejecuta validacion a la tabla bv_transaccion
        IProcedureResponse tResponse = executeCoreBanking(request);

        if (logger.isDebugEnabled())
            logger.logDebug(CLASS_NAME + "Validacion local, response: " + tResponse.getProcedureResponseAsString());
        if (logger.isInfoEnabled())
            logger.logInfo(CLASS_NAME + "Finaliza validacion local");

        responseSupportOffline = tResponse.readValueParam("@o_fuera_de_linea");

        aBagSPJavaOrchestration.put(RESPONSE_BV_TRANSACTION, tResponse);

        // Valida si ocurrio un error en la ejecucion
        if (Utils.flowError("validateBvTransaction", tResponse)) {
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, tResponse);
        }

        return responseSupportOffline;


    }

}
