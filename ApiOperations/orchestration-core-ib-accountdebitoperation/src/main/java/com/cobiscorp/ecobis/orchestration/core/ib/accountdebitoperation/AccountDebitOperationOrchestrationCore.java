package com.cobiscorp.ecobis.orchestration.core.ib.accountdebitoperation;

import java.math.BigDecimal;
import java.util.Map;

import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.Constants;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.log.ILogger;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;

@Component(name = "AccountDebitOperationOrchestrationCore", immediate = false)
@Service(value = {ICISSPBaseOrchestration.class, IOrchestrator.class})
@Properties(value = {@Property(name = "service.description", value = "AccountDebitOperationOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
        @Property(name = "service.identifier", value = "AccountDebitOperationOrchestrationCore"),
        @Property(name = "service.spName", value = "cob_procesador..sp_debit_operation_api")
})
public class AccountDebitOperationOrchestrationCore extends OfflineApiTemplate {// SPJavaOrchestrationBase

    @Override
    public ICoreServer getCoreServer() {
        return coreServer;
    }

    private ILogger logger = (ILogger) this.getLogger();
    private static final String CLASS_NAME = "AccountDebitOperationOrchestrationCore --->";
    protected static final String COLUMNS_RETURN = "columnsToReturn";

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][executeJavaOrchestration]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
        }

        aBagSPJavaOrchestration.put(IS_ONLINE, false);
        aBagSPJavaOrchestration.put(IS_ERRORS, false);
        aBagSPJavaOrchestration.put(IS_REENTRY, evaluateExecuteReentry(anOriginalRequest));

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response flowRty: " + aBagSPJavaOrchestration.get(IS_REENTRY));
        }

        if (!(Boolean)aBagSPJavaOrchestration.get(IS_REENTRY)) {
            aBagSPJavaOrchestration.put("process", "DEBIT_OPERATION");
            IProcedureResponse potency = logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);
            IResultSetRow resultSetRow = potency.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
            if (columns[0].getValue().equals("false") ) {
                setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
                return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
            }
        }

        if (validateParameters(aBagSPJavaOrchestration, anOriginalRequest))
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

        try {
            ServerResponse serverResponse = serverStatus();
            aBagSPJavaOrchestration.put(IS_ONLINE, serverResponse.getOnLine());
            aBagSPJavaOrchestration.put(PROCESS_DATE, serverResponse.getProcessDate());
        } catch (CTSServiceException | CTSInfrastructureException e) {
            if (logger.isErrorEnabled()){
                logger.logError("Error getting server status: " + e.toString());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Online: " + aBagSPJavaOrchestration.get(IS_ONLINE));
        }

        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST,anOriginalRequest);
        dataTrn(anOriginalRequest,aBagSPJavaOrchestration);
        validateLocalExecution(aBagSPJavaOrchestration);

        IProcedureResponse wProcedureResponse = processTransaction(aBagSPJavaOrchestration, anOriginalRequest);

        aBagSPJavaOrchestration.put("s_error", aBagSPJavaOrchestration.get(ERROR_CODE));
        aBagSPJavaOrchestration.put("s_msg", aBagSPJavaOrchestration.get(ERROR_MESSAGE));

        updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);

        return wProcedureResponse;
    }

    @Override
    protected void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

    }

    private boolean validateParameters(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][validateParameters]");
        }

        String accountNumber = anOriginalRequest.readValueParam("@i_accountNumber");
        String referenceNumber = anOriginalRequest.readValueParam("@i_referenceNumber");
        String debitReason = anOriginalRequest.readValueParam("@i_debitReason").trim();
        BigDecimal amount = new BigDecimal(anOriginalRequest.readValueParam("@i_amount"));
        int originCode = 0;
        String originCodeStr = anOriginalRequest.readValueParam("@i_originCode");
        String originMovementId = anOriginalRequest.readValueParam("@i_originMovementId");
        String originReferenceNumber = anOriginalRequest.readValueParam("@i_originReferenceNumber");

        if (originCodeStr != null && !originCodeStr.isEmpty() && !originCodeStr.equals("null")) {
            originCode = Integer.parseInt(originCodeStr);
        }
        
        if (accountNumber.isEmpty()) {
            setError(aBagSPJavaOrchestration, "40082", "accountNumber must not be empty.");
            return true;
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            setError(aBagSPJavaOrchestration, "40107", "amount must be greater than 0.");
            return true;
        }
        
        if (referenceNumber.isEmpty()) {
            setError(aBagSPJavaOrchestration, "40092", "referenceNumber must not be empty.");
            return true;
        }
        if (debitReason.isEmpty()) {
            setError(aBagSPJavaOrchestration, "40123", "debitReason must not be empty.");
            return true;
        }
        
        switch (debitReason) {
            case "Card delivery fee":
                aBagSPJavaOrchestration.put("debitConcept", "CARD_DELIVERY_FEE");
                break;
            case "False chargeback claim":
            case "FALSE_CHARGEBACK":
                if (originMovementId.isEmpty()) {
                    setError(aBagSPJavaOrchestration, "40126", "The originMovementId must not be empty.");
                    return true;
                }

                if (originReferenceNumber.isEmpty()) {
                    setError(aBagSPJavaOrchestration, "40127", "The originReferenceNumber must not be empty.");
                    return true;
                }
                aBagSPJavaOrchestration.put("debitConcept", "FALSE_CHARGEBACK");
                break;
            default:
                setError(aBagSPJavaOrchestration, "40124", "debit reason not found.");
                return true;
        }
        
        if (originCode <= 0 || originCode > 3) {
            setError(aBagSPJavaOrchestration, "40125", "origin code not found.");
            return true;
        }
        
        return false;
    }

    private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][processTransaction]");
        }
        if (!(Boolean)aBagSPJavaOrchestration.get(IS_ONLINE)) {
            if (!(Boolean)aBagSPJavaOrchestration.get(IS_REENTRY)) {
                processOffline(aBagSPJavaOrchestration, anOriginalRequest);
            } else {
                setError(aBagSPJavaOrchestration, "50041", "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
            }
        } else {
            processOnline(aBagSPJavaOrchestration, anOriginalRequest);
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    private void processOffline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][processOffline]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
        }

        IProcedureResponse wProcedureResponseVal;

        wProcedureResponseVal = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response [saveReentry]: " + wProcedureResponseVal.getProcedureResponseAsString());
        }

        wProcedureResponseVal = getValAccount(anOriginalRequest, aBagSPJavaOrchestration);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response [getValAccount]: " + wProcedureResponseVal.getProcedureResponseAsString());
        }
        if (!wProcedureResponseVal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
            setError(aBagSPJavaOrchestration, wProcedureResponseVal.getResultSetRowColumnData(2, 1, 1).getValue(), wProcedureResponseVal.getResultSetRowColumnData(2, 1, 2).getValue());
            return;
        }

        IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));
        reqTMPCentral.setSpName("cob_bvirtual..sp_account_operation_val_api");
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', IMultiBackEndResolverService.TARGET_LOCAL);
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
        reqTMPCentral.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500118");
        reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
        reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
        reqTMPCentral.addInputParam("@i_amount", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
        reqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("debitConcept").toString());
        if(aBagSPJavaOrchestration.get("debitConcept").toString().equals("FALSE_CHARGEBACK")) {
            reqTMPCentral.addInputParam("@i_originMovementId",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originMovementId"));
            reqTMPCentral.addInputParam("@i_originReferenceNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originReferenceNumber"));
        }
        reqTMPCentral.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
        reqTMPCentral.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
        reqTMPCentral.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
        reqTMPCentral.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
        reqTMPCentral.addOutputParam("@o_causa", ICTSTypes.SQLVARCHAR, "X");

        wProcedureResponseVal = executeCoreBanking(reqTMPCentral);

        aBagSPJavaOrchestration.put("o_prod", wProcedureResponseVal.readValueParam("@o_prod"));
        aBagSPJavaOrchestration.put("o_mon", wProcedureResponseVal.readValueParam("@o_mon"));
        aBagSPJavaOrchestration.put("o_login", wProcedureResponseVal.readValueParam("@o_login"));
        aBagSPJavaOrchestration.put("o_ente_bv", wProcedureResponseVal.readValueParam("@o_ente_bv"));
        aBagSPJavaOrchestration.put("causa", wProcedureResponseVal.readValueParam("@o_causa"));

        if (logger.isDebugEnabled()) {
            logger.logDebug("OFFLINE [executeOfflineTransacction][cob_bvirtual..sp_account_operation_val_api][Local] wProcedureResponseVal: " + wProcedureResponseVal.getProcedureResponseAsString());
        }

        if (!wProcedureResponseVal.hasError()) {
            IResultSetRow resultSetRow = wProcedureResponseVal.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {

                if (logger.isDebugEnabled()) {
                    logger.logDebug("Ejecutando transferencia Offline a terceros CORE COBIS" + anOriginalRequest);
                    logger.logDebug("CAUSA [aBagSPJavaOrchestration.get(\"causa\").toString()] --->>> " + aBagSPJavaOrchestration.get("causa").toString());
                    logger.logDebug("CLIENTE MIS [aBagSPJavaOrchestration.get(\"@i_externalCustomerId\").toString()] --->>> " +  anOriginalRequest.readValueParam("@i_externalCustomerId"));
                    logger.logDebug("COMISION [aBagSPJavaOrchestration.get(\"@i_comision\").toString()] --->>> " +  anOriginalRequest.readValueParam("@i_comision"));
                }

                anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
                anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
                anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
                anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500111");

                anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api");

                anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500118");
                anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
                anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
                anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
                anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("causa").toString());
                anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
                anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
                anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");
                anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get("o_ente_bv"));
                anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
                anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
                anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_amount"));
                anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_debitReason"));
                anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
                anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());
                anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");
                anOriginalRequest.addInputParam("@i_type_response", ICTSTypes.SYBCHAR, "S");
                anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
                anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
                anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
                anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");
                anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
                anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
                anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));
                anOriginalRequest.addInputParam("@i_refer_transaction", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));

                anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

                if (logger.isDebugEnabled())
                    logger.logDebug("Data enviada a ejecutar api:" + anOriginalRequest.getProcedureRequestAsString());

                IProcedureResponse response = executeCoreBanking(anOriginalRequest);

                if (logger.isInfoEnabled())
                    logger.logInfo("Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

                response.readValueParam("@o_fecha_tran");

                if (response.readValueFieldInHeader("ssn") != null){
                    aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));
                    aBagSPJavaOrchestration.put("ssn_branch", anOriginalRequest.readValueFieldInHeader("ssn_branch"));
                }

                if (!response.hasError()) {
                    resultSetRow = response.getResultSet(1).getData().getRowsAsArray()[0];
                    columns = resultSetRow.getColumnsAsArray();

                    if (columns[0].getValue().equals("true")) {
                        aBagSPJavaOrchestration.put(COLUMNS_RETURN, columns);
                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
                        setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
                    }
                } else {
                    setError(aBagSPJavaOrchestration, "50045", "Error account debit operation.");
                }
            } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
                setError(aBagSPJavaOrchestration, columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId") + " does not exist");
            } else {
                setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
            }
        } else {
            setError(aBagSPJavaOrchestration, "50045", "Error account debit operation.");
        }
    }

    private void processOnline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][processOnline]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("Request param @i_externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId"));
        }

        String reentryCode = anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX");
        IProcedureRequest reqTMPCentral = anOriginalRequest;

        if (reentryCode != null) {
            logger.logDebug("Flow: " + reentryCode);
            reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_SSN, reentryCode);
        }

        reqTMPCentral.setSpName("cobis..sp_account_debit_operation_central_api");
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
        reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
        reqTMPCentral.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
        reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
        reqTMPCentral.addInputParam("@i_amount", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
        reqTMPCentral.addInputParam("@i_originCode", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_originCode"));
        reqTMPCentral.addInputParam("@i_debitConcept", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("debitConcept").toString());
        reqTMPCentral.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
        if(aBagSPJavaOrchestration.get("debitConcept").toString().equals("FALSE_CHARGEBACK")) {
            reqTMPCentral.addInputParam("@i_originMovementId",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originMovementId"));
            reqTMPCentral.addInputParam("@i_originReferenceNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originReferenceNumber"));
        }
        reqTMPCentral.addOutputParam("@o_causa", ICTSTypes.SQLVARCHAR, "X");

        aBagSPJavaOrchestration.put("ssn", anOriginalRequest.readValueFieldInHeader("ssn"));
        aBagSPJavaOrchestration.put("ssn_branch", anOriginalRequest.readValueFieldInHeader("ssn_branch"));

        IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);

        aBagSPJavaOrchestration.put("causa", wProcedureResponseCentral.readValueParam("@o_causa"));

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response executeCoreBanking cobis..sp_account_debit_operation_central_api: " + wProcedureResponseCentral.getProcedureResponseAsString());
        }

        IProcedureResponse wProcedureResponseLocal;
        if (!wProcedureResponseCentral.hasError()) {
            IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {
                aBagSPJavaOrchestration.put(COLUMNS_RETURN, columns);
                IProcedureRequest reqTMPLocal = (initProcedureRequest(anOriginalRequest));

                reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
                reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
                reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
                reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
                reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
                reqTMPLocal.addInputParam("@i_amount", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
                reqTMPLocal.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));

                wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
                if (logger.isInfoEnabled()) {
                    logger.logDebug("Ending flow, processOnline with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
                }

                if (!wProcedureResponseLocal.hasError()) {
                    resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
                    columns = resultSetRow.getColumnsAsArray();
                    if (columns[0].getValue().equals("true")) {
                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
                        setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
                    }
                } else {
                    setError(aBagSPJavaOrchestration, "50045", "Error account debit operation.");
                }
            } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
                setError(aBagSPJavaOrchestration, columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId") + " does not exist");
            } else {
                setError(aBagSPJavaOrchestration, columns[1].getValue(), columns[2].getValue());
            }
        } else {
            setError(aBagSPJavaOrchestration, "50045", "Error account debit operation.");
        }
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][processResponse]");
        }

        IResultSetHeader metaData = new ResultSetHeader();
        IResultSetData data = new ResultSetData();
        IResultSetRow row = new ResultSetRow();
        IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

        metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SYBVARCHAR, 255));

        if (logger.isDebugEnabled()) {
            logger.logDebug("Valida errores [isErrors]: " + aBagSPJavaOrchestration.get(IS_ERRORS).toString());
        }

        aBagSPJavaOrchestration.put("@i_debitReason", anOriginalRequest.readValueParam("@i_debitReason").trim());
        aBagSPJavaOrchestration.put("causal", aBagSPJavaOrchestration.get("causa"));

        if(aBagSPJavaOrchestration.get("debitConcept").toString().equals("FALSE_CHARGEBACK")) {
            aBagSPJavaOrchestration.put("@i_originMovementId", anOriginalRequest.readValueParam("@i_originMovementId"));
            aBagSPJavaOrchestration.put("@i_originReferenceNumber", anOriginalRequest.readValueParam("@i_originReferenceNumber"));
        }

        if (!(Boolean)aBagSPJavaOrchestration.get(IS_ERRORS)) {
            IResultSetRowColumnData[] columnsToReturn = (IResultSetRowColumnData[]) aBagSPJavaOrchestration.get(COLUMNS_RETURN);
            if (logger.isDebugEnabled()) {
                logger.logDebug("Ending flow, processResponse success.");
                logger.logDebug("success: " +  columnsToReturn[0].getValue());
                logger.logDebug("code: " +  columnsToReturn[1].getValue());
                logger.logDebug("message: " +  columnsToReturn[2].getValue());
                logger.logDebug("movementId: " +  columnsToReturn[3].getValue());
            }

            row.addRowData(1, new ResultSetRowColumnData(false, columnsToReturn[0].getValue()));
            row.addRowData(2, new ResultSetRowColumnData(false, columnsToReturn[1].getValue()));
            row.addRowData(3, new ResultSetRowColumnData(false, columnsToReturn[2].getValue()));
            row.addRowData(4, new ResultSetRowColumnData(false, columnsToReturn[3].getValue()));
            data.addRow(row);

            registerAllTransactionSuccess("AccountDebitOperationOrchestrationCore", anOriginalRequest, aBagSPJavaOrchestration.get("causa").toString(), aBagSPJavaOrchestration);
        } else {
            if (logger.isDebugEnabled()) {
                logger.logDebug("Ending flow, processResponse failed.");
                logger.logDebug("success: false");
                logger.logDebug("code: " +  aBagSPJavaOrchestration.get("error_code"));
                logger.logDebug("message: " +  aBagSPJavaOrchestration.get("error_message"));
                logger.logDebug("movementId: null");
            }
            row.addRowData(1, new ResultSetRowColumnData(false, "false"));
            row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_code")));
            row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_message")));
            row.addRowData(4, new ResultSetRowColumnData(false, null));
            data.addRow(row);

            aBagSPJavaOrchestration.put("code_error", aBagSPJavaOrchestration.get("error_code").toString());
            aBagSPJavaOrchestration.put("message_error", aBagSPJavaOrchestration.get("error_message").toString());
            
            registerTransactionFailed("AccountDebitOperationOrchestrationCore", "", anOriginalRequest, aBagSPJavaOrchestration);
        }

        IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
        wProcedureResponse.addResponseBlock(resultBlock);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response: " +  wProcedureResponse.getProcedureResponseAsString());
        }

        aBagSPJavaOrchestration.replace("process", "FINISH_OPERATION");
        logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);

        return wProcedureResponse;
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
    public ICoreServer getCoreServer() {
        return coreServer;
    }
    
    public void dataTrn(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][dataTrn]");
        }
        
        String debitConcept = Objects.nonNull(aBagSPJavaOrchestration.get("debitConcept"))
                              ? aBagSPJavaOrchestration.get("debitConcept").toString()
                              : "";

        aBagSPJavaOrchestration.put("i_prod", null);
        aBagSPJavaOrchestration.put("i_prod_des", null );
        aBagSPJavaOrchestration.put("i_login", null );
        aBagSPJavaOrchestration.put("i_cta_des", null);  
        aBagSPJavaOrchestration.put("i_cta", aRequest.readValueParam("@i_accountNumber") ); 
        aBagSPJavaOrchestration.put("i_concepto", debitConcept);
        aBagSPJavaOrchestration.put("i_val", aRequest.readValueParam("@i_amount"));
        aBagSPJavaOrchestration.put("i_mon", null );
   }

}
