/**
 *
 */
package com.cobiscorp.ecobis.orchestration.core.ib.accountdebitoperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;

/**
 * @author cecheverria
 * @version 1.0.0
 * @since Sep 2, 2014
 */
@Component(name = "AccountDebitOperationOrchestrationCore", immediate = false)
@Service(value = {ICISSPBaseOrchestration.class, IOrchestrator.class})
@Properties(value = {@Property(name = "service.description", value = "AccountDebitOperationOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
        @Property(name = "service.identifier", value = "AccountDebitOperationOrchestrationCore"),
        @Property(name = "service.spName", value = "cob_procesador..sp_debit_operation_api")
})
public class AccountDebitOperationOrchestrationCore extends OfflineApiTemplate {// SPJavaOrchestrationBase

    private ILogger logger = (ILogger) this.getLogger();
    private IResultSetRowColumnData[] columnsToReturn;
    private static final String CLASS_NAME = "AccountDebitOperationOrchestrationCore --->";
    private static final String REENTRY_SSN_TRX = "REENTRY_SSN_TRX";

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Begin [" + CLASS_NAME + "][executeJavaOrchestration]");
        }

        Boolean isErrors = validateParameters(aBagSPJavaOrchestration, anOriginalRequest);
        aBagSPJavaOrchestration.put("isErrors", isErrors);
        if (isErrors)
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
        aBagSPJavaOrchestration.put("REENTRY_SSN", anOriginalRequest.readValueFieldInHeader(REENTRY_SSN_TRX));

        boolean isOnline = false;
        try {
            isOnline = getServerStatus();
        } catch (CTSServiceException | CTSInfrastructureException e) {
            logger.logError("Error getting server status: " + e.toString());
        }
        aBagSPJavaOrchestration.put("isOnline", isOnline);

        Boolean flowRty = evaluateExecuteReentry(anOriginalRequest);
        aBagSPJavaOrchestration.put("flowRty", flowRty);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Online: " + isOnline);
            logger.logDebug("Response flowRty: " + flowRty);
        }

        if (!validateContextTransacction(aBagSPJavaOrchestration, isOnline)) {
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(this.MESSAGE_RESPONSE));
            return Utils.returnException(this.MESSAGE_RESPONSE);
        }

        return processTransaction(aBagSPJavaOrchestration, anOriginalRequest);
    }

    @Override
    protected void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

    }

    private boolean validateParameters(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][validateTransaction]");
        }
        String idCustomer = anOriginalRequest.readValueParam("@i_externalCustomerId");
        String accountNumber = anOriginalRequest.readValueParam("@i_accountNumber");
        String referenceNumber = anOriginalRequest.readValueParam("@i_referenceNumber");
        String debitReason = anOriginalRequest.readValueParam("@i_debitReason");
        BigDecimal amount = new BigDecimal(anOriginalRequest.readValueParam("@i_amount"));
        int originCode = 0;
        String originCodeStr = anOriginalRequest.readValueParam("@i_originCode");
        if (originCodeStr != null && !originCodeStr.isEmpty() && !originCodeStr.equals("null")) {
            originCode = Integer.parseInt(originCodeStr);
        }
        String originMovementId = (anOriginalRequest.readValueParam("@i_originMovementId"));
        String originReferenceNumber = (anOriginalRequest.readValueParam("@i_originReferenceNumber"));
        if (idCustomer.isEmpty()) {
            aBagSPJavaOrchestration.put("error_code", "40030");
            aBagSPJavaOrchestration.put("error_message", "externalCustomerId must not be empty.");
            return true;
        }
        if (accountNumber.isEmpty()) {
            aBagSPJavaOrchestration.put("error_code", "40082");
            aBagSPJavaOrchestration.put("error_message", "accountNumber must not be empty");
            return true;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            aBagSPJavaOrchestration.put("error_code", "40107");
            aBagSPJavaOrchestration.put("error_message", "amount must be greater than 0");
            return true;
        }
        if (referenceNumber.isEmpty()) {
            aBagSPJavaOrchestration.put("error_code", "40092");
            aBagSPJavaOrchestration.put("error_message", "referenceNumber must not be empty");
            return true;
        }
        if (referenceNumber.length() != 6) {
            aBagSPJavaOrchestration.put("error_code", "40104");
            aBagSPJavaOrchestration.put("error_message", "referenceNumber must have 6 digits");
            return true;
        }
        if (debitReason.trim().isEmpty()) {
            aBagSPJavaOrchestration.put("error_code", "40123");
            aBagSPJavaOrchestration.put("error_message", "debitReason must not be empty");
            return true;
        }
        switch (anOriginalRequest.readValueParam("@i_debitReason").trim()) {
            case "Card delivery fee":
                anOriginalRequest.setValueParam("@i_debitReason", "8110");
                break;
            case "False chargeback claim":
            case "FALSE_CHARGEBACK":
                if (originMovementId.isEmpty()) {
                    aBagSPJavaOrchestration.put("error_code", "40126");
                    aBagSPJavaOrchestration.put("error_message", "The originMovementId must not be empty");
                    return true;
                }

                if (originReferenceNumber.isEmpty()) {
                    aBagSPJavaOrchestration.put("error_code", "40127");
                    aBagSPJavaOrchestration.put("error_message", "The originReferenceNumber must not be empty");
                    return true;
                }
                anOriginalRequest.setValueParam("@i_debitReason", "3101");
                break;
            default:
                aBagSPJavaOrchestration.put("error_code", "40124");
                aBagSPJavaOrchestration.put("error_message", "debit reason not found");
                return true;
        }
        if (originCode <= 0 || originCode > 3) {
            aBagSPJavaOrchestration.put("error_code", "40125");
            aBagSPJavaOrchestration.put("error_message", "origin code not found");
            return true;
        }
        return false;
    }

    private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME + "][processTransaction]");
        }

        boolean isOnline = (Boolean) aBagSPJavaOrchestration.get("isOnline");
        boolean flowRty = (Boolean) aBagSPJavaOrchestration.get("flowRty");

        if (!isOnline) {
            IProcedureResponse anProcedureResponse = getValAccountReq(anOriginalRequest, aBagSPJavaOrchestration);

            if (logger.isDebugEnabled()) {
                logger.logInfo("anProcedureResponse FHU " + anProcedureResponse);
                logger.logDebug(" validating Central: " + anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue());
            }
            if (!anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
                aBagSPJavaOrchestration.clear();
                aBagSPJavaOrchestration.put(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue(), anProcedureResponse.getResultSetRowColumnData(2, 1, 2).getValue());
                return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
            }

            if (!flowRty) {
                if (logger.isDebugEnabled()) {
                    logger.logDebug("evaluateExecuteReentry");
                }

                anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);

                if (logger.isDebugEnabled()) {
                    logger.logDebug("executeOfflinePurchaseCobis " + anProcedureResponse.toString());
                }
                executeOfflineTransacction(aBagSPJavaOrchestration, anOriginalRequest);
            } else {
                logger.logDebug("evaluateExecuteReentry FALSE");
                anProcedureResponse = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
                logger.logDebug("Response Exception: " + anProcedureResponse.toString());
                aBagSPJavaOrchestration.clear();
                aBagSPJavaOrchestration.put("50041", "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
                return anProcedureResponse;
            }
        } else {
            queryAccountDebitOperation(aBagSPJavaOrchestration, anOriginalRequest);
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    private void executeOfflineTransacction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        logger.logDebug("execute executeOfflineTransacction: ");
        aBagSPJavaOrchestration.clear();

        logger.logDebug("Begin flow, queryAccountDebitOperation Offline with id: " + anOriginalRequest.readValueParam("@i_externalCustomerId"));

        IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));
        reqTMPCentral.setSpName("cob_bvirtual..sp_account_operation_val_api");
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', IMultiBackEndResolverService.TARGET_LOCAL);
        reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
        reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
        reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
        reqTMPCentral.addInputParam("@i_amount", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
        //reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_commission"));
        //eqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_debditConcept"));
        // reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_originCode"));

        reqTMPCentral.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
        reqTMPCentral.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
        reqTMPCentral.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
        reqTMPCentral.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");

        IProcedureResponse wProcedureResponseVal = executeCoreBanking(reqTMPCentral);

        aBagSPJavaOrchestration.put("o_prod", wProcedureResponseVal.readValueParam("@o_prod"));
        aBagSPJavaOrchestration.put("o_mon", wProcedureResponseVal.readValueParam("@o_mon"));
        aBagSPJavaOrchestration.put("o_login", wProcedureResponseVal.readValueParam("@o_login"));
        aBagSPJavaOrchestration.put("o_ente_bv", wProcedureResponseVal.readValueParam("@o_ente_bv"));

        if (logger.isInfoEnabled()) {
            logger.logDebug("Ending flow, queryAccountDebitOperation Offline with wProcedureResponseCentral: " + wProcedureResponseVal.getProcedureResponseAsString());
        }

        if (!wProcedureResponseVal.hasError()) {
            IResultSetRow resultSetRow = wProcedureResponseVal.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {

                if (logger.isInfoEnabled()) {
                    logger.logInfo("Ejecutando transferencia Offline a terceros CORE COBIS" + anOriginalRequest);
                    logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
                    logger.logInfo("********** CAUSA ORIGEN --->>> " + "4060");
                    logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));

                }
                //IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
                anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
                anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                        IMultiBackEndResolverService.TARGET_LOCAL);
                anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
                anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500111");

                anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api");

                anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500118");
                anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
                anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
                anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
                anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_debitReason"));
                anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
                anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
                anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");
                anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get("o_ente_bv"));
                anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
                anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
                anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_amount"));
                anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_creditConcept"));
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

                if (logger.isDebugEnabled())
                    logger.logDebug("Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
                anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));

                anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

                if (logger.isDebugEnabled())
                    logger.logDebug("Data enviada a ejecutar api:" + anOriginalRequest);
                IProcedureResponse response = executeCoreBanking(anOriginalRequest);

                if (logger.isInfoEnabled())
                    logger.logInfo("Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

                logger.logInfo("Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
                response.readValueParam("@o_fecha_tran");

                logger.logInfo("Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
                if (response.readValueFieldInHeader("ssn") != null)
                    aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));

                if (!response.hasError()) {

                    resultSetRow = response.getResultSet(1).getData().getRowsAsArray()[0];
                    columns = resultSetRow.getColumnsAsArray();

                    if (columns[0].getValue().equals("true")) {
                        this.columnsToReturn = columns;
                        logger.logInfo("DCO LOG COLUMNS[1]: " + this.columnsToReturn[1].getValue());

                        for (int i = 0; i < this.columnsToReturn.length; i++)
                            logger.logInfo("DCO LOG COLUMNS[" + i + "]: " + this.columnsToReturn[i].getValue());


                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                        return;

                    } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {

                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                        return;
                    }

                } else {
                    aBagSPJavaOrchestration.put("50045", "Error account debit operation");
                    return;
                }

            } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {

                aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId") + " does not exist");
                return;

            } else {

                aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                return;
            }


        } else {
            aBagSPJavaOrchestration.put("50045", "Error account debit operation");
            return;
        }
    }


    private void queryAccountDebitOperation(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Begin [" + CLASS_NAME + "][queryAccountDebitOperation]");
        }

        String reentryCode = (String) aBagSPJavaOrchestration.get("REENTRY_SSN");

        aBagSPJavaOrchestration.clear();

        logger.logDebug("Begin flow, queryAccountDebitOperation with id: " + anOriginalRequest.readValueParam("@i_externalCustomerId"));

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
        reqTMPCentral.addInputParam("@i_debitReason", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_debitReason"));
        reqTMPCentral.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
        if(anOriginalRequest.readValueParam("@i_debitReason").equals("3101")) {
            reqTMPCentral.addInputParam("@i_originMovementId",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originMovementId"));
            reqTMPCentral.addInputParam("@i_originReferenceNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_originReferenceNumber"));
        }

        aBagSPJavaOrchestration.put("ssn", anOriginalRequest.readValueFieldInHeader("ssn"));

        IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);

        if (logger.isInfoEnabled()) {
            logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
        }

        IProcedureResponse wProcedureResponseLocal;
        if (!wProcedureResponseCentral.hasError()) {
            IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {
                this.columnsToReturn = columns;
                IProcedureRequest reqTMPLocal = (initProcedureRequest(anOriginalRequest));

                reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
                reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
                reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
                reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
                reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
                reqTMPLocal.addInputParam("@i_amount", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
                //reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));
                //reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
                //reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
                reqTMPLocal.addInputParam("@i_referenceNumber", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
                //reqTMPLocal.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_debitConcept"));
                // reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));

                wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
                if (logger.isInfoEnabled()) {
                    logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
                }
                if (!wProcedureResponseLocal.hasError()) {
                    resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
                    columns = resultSetRow.getColumnsAsArray();
                    if (columns[0].getValue().equals("true")) {
                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
                        aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    }
                } else {
                    aBagSPJavaOrchestration.put("50045", "Error account debit operation");
                }
            } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
                aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + anOriginalRequest.readValueParam("@i_externalCustomerId") + " does not exist");
            } else {
                aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
            }
        } else {
            aBagSPJavaOrchestration.put("50045", "Error account debit operation");
        }
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
        IResultSetHeader metaData = new ResultSetHeader();
        IResultSetData data = new ResultSetData();
        IResultSetRow row = new ResultSetRow();
        IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

        metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SYBVARCHAR, 255));

        boolean isErrors = (Boolean) aBagSPJavaOrchestration.get("isErrors");

        if (!isErrors) {
            logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
            row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
            row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
            row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
            row.addRowData(4, new ResultSetRowColumnData(false, this.columnsToReturn[3].getValue()));
            data.addRow(row);
            aBagSPJavaOrchestration.put("transaccionDate", transaccionDate);
            registerAllTransactionSuccess("AccountDebitOperationOrchestrationCore", anOriginalRequest, "4060", aBagSPJavaOrchestration);
        } else {
            logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
            row.addRowData(1, new ResultSetRowColumnData(false, "false"));
            row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_code")));
            row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("error_message")));
            row.addRowData(4, new ResultSetRowColumnData(false, null));
            data.addRow(row);
        }

        IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
        wProcedureResponse.addResponseBlock(resultBlock);
        return wProcedureResponse;
    }

    private IProcedureResponse getValAccountReq(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
        IProcedureRequest request = new ProcedureRequestAS();
        IProcedureResponse response = null;

        try {
            if (logger.isInfoEnabled()) {
                logger.logInfo(CLASS_NAME + " Entrando en getValAccountReq");
            }
            aBagSPJavaOrchestration.clear();
            // Configuración del procedimiento
            request.setSpName("cobis..sp_val_data_account_api");

            // Agregar encabezados
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                    IMultiBackEndResolverService.TARGET_CENTRAL);
            request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

            // Validar y agregar parámetros de entrada
            String externalCustomerId = aRequest.readValueParam("@i_externalCustomerId");
            String accountNumber = aRequest.readValueParam("@i_accountNumber");

            request.addInputParam("@i_ente", ICTSTypes.SQLINTN, externalCustomerId);
            request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, accountNumber);
            request.addInputParam("@i_error_ndnc", ICTSTypes.SQLVARCHAR, "S");

            // Ejecutar el procedimiento
            response = executeCoreBanking(request);

            if (logger.isDebugEnabled()) {
                logger.logDebug("Response Corebanking getValAccountReq FHU : " + response.getProcedureResponseAsString());
            }
        } catch (Exception e) {
            logger.logError(CLASS_NAME + " Error al obtener la validación de la cuenta: " + e.getMessage(), e);
            throw new RuntimeException("Error en la validación de la cuenta", e);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo(CLASS_NAME + " Saliendo de getValAccountReq");
            }
        }

        return response;
    }
}
