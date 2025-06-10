package com.cobiscorp.ecobis.orchestration.core.ib.consignment.operations.services;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;
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
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ApplicationException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.BusinessException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ErrorHandler;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.FlowTerminatedException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.ErrorCode;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.ParameterValidationUtil;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.ValidationType;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.Constants;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import static com.cobiscorp.ecobis.orchestration.core.ib.consignment.operations.utils.ConstantsUtil.CLASS_NAME_CONSIGNMENT_CREDIT;
import static com.cobiscorp.ecobis.orchestration.core.ib.consignment.operations.utils.ConstantsUtil.TRN_CONSIGNMENT_CREDIT;
import static com.cobiscorp.ecobis.orchestration.core.ib.consignment.operations.utils.ConstantsUtil.TRN_CREDIT;

import java.util.Map;


/**
 * Esta clase representa una persona.
 *
 * @author egalicia
 * @version 1.0
 * @since 21/05/2025
 */
@Component(name = "ConsignmentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ConsignmentOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
        @Property(name = "service.identifier", value = "ConsignmentOrchestrationCore"),
        @Property(name = "service.spName", value = "cob_procesador..sp_consignment_operations")
})
public class ConsignmentOrchestrationCore extends OfflineApiTemplate {

    private ILogger logger = (ILogger) this.getLogger();
    @Override
    protected void loadDataCustomer(IProcedureRequest iProcedureRequest, Map<String, Object> map) {

    }

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeJavaOrchestration]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
        }

        aBagSPJavaOrchestration.put(Constants.ORIGINAL_REQUEST, anOriginalRequest);
        aBagSPJavaOrchestration.put(Constants.CAUSA, "10"); // TODO validar causa
        aBagSPJavaOrchestration.put(Constants.IS_ONLINE, false);

        try {
            String servicio = anOriginalRequest.readValueParam("@i_servicio");
            initializeValidationParameters(aBagSPJavaOrchestration, servicio);
            validateParameters(aBagSPJavaOrchestration);

            aBagSPJavaOrchestration.put(Constants.IS_REENTRY, evaluateExecuteReentry(anOriginalRequest));

            if (logger.isDebugEnabled()) {
                logger.logDebug("Response flowRty: " + aBagSPJavaOrchestration.get(Constants.IS_REENTRY));
            }

            if (!(boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
                aBagSPJavaOrchestration.put(Constants.PROCESS_OPERATION, "CONSIGNMENT_CREDIT");
                IProcedureResponse potency = logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);
                IResultSetRow resultSetRow = potency.getResultSet(1).getData().getRowsAsArray()[0];
                IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
                if (columns[0].getValue().equals("false") ) {
                    throw new ApplicationException(Integer.parseInt(columns[1].getValue()), columns[2].getValue());
                }
            }

            ServerResponse serverResponse = serverStatus();
            aBagSPJavaOrchestration.put(Constants.IS_ONLINE, serverResponse.getOnLine());
            aBagSPJavaOrchestration.put(Constants.PROCESS_DATE, serverResponse.getProcessDate());

            if (logger.isDebugEnabled()) {
                logger.logDebug("Response Online: " + aBagSPJavaOrchestration.get(Constants.IS_ONLINE));
            }

            validateContextTransacction(aBagSPJavaOrchestration);

            return processTransaction(aBagSPJavaOrchestration, anOriginalRequest);

        } catch (BusinessException e) {
            if (logger.isErrorEnabled()) {
                logger.logError("BusinessException in consignment credit operation: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (FlowTerminatedException e) {
            if (logger.isErrorEnabled()) {
                logger.logError("Flow terminated: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (ApplicationException e) {
            if (logger.isErrorEnabled()) {
                logger.logError("ApplicationException in consignment credit operation: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.logError("Exception Error in consignment credit operation: " + e.getMessage(), e);
            }   
            ApplicationException appEx = new ApplicationException(50061, "Error in consignment credit operation.");
            ErrorHandler.handleException(appEx, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Finish [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeJavaOrchestration]");
            }
        }
    }

    private void initializeValidationParameters(Map<String, Object> aBagSPJavaOrchestration, String servicio) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][initializeValidationParameters]");
        }

        ParameterValidationUtil[] validations;

        if(Constants.CONSIGNMENT_CREDIT.equals(servicio)) {
            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil("@i_externalCustomerId", ValidationType.NOT_EMPTY, ErrorCode.E40030),
                new ParameterValidationUtil("@i_accountNumber", ValidationType.NOT_EMPTY, ErrorCode.E40082),
                new ParameterValidationUtil("@i_referenceNumber", ValidationType.NOT_EMPTY, ErrorCode.E40092),
                new ParameterValidationUtil("@i_creditConcept", ValidationType.NOT_EMPTY, ErrorCode.E40093),
                new ParameterValidationUtil("@i_amount", ValidationType.NOT_EMPTY, ErrorCode.E40300),
                new ParameterValidationUtil("@i_amount", ValidationType.IS_DOUBLE, ErrorCode.E40300),
                new ParameterValidationUtil("@i_amount", ValidationType.GREATER_THAN_ZERO_DOUBLE, ErrorCode.E40107), 
                new ParameterValidationUtil("@i_commission", ValidationType.NOT_EMPTY, ErrorCode.E40301),
                new ParameterValidationUtil("@i_commission", ValidationType.IS_DOUBLE, ErrorCode.E40301),
                new ParameterValidationUtil("@i_commission", ValidationType.GREATER_THAN_ZERO_DOUBLE, ErrorCode.E40108),
                new ParameterValidationUtil("@i_originCode", ValidationType.NOT_EMPTY, ErrorCode.E40302),
                new ParameterValidationUtil("@i_senderName", ValidationType.NOT_EMPTY, ErrorCode.E40303),
                new ParameterValidationUtil("@i_moneyTransmitter", ValidationType.NOT_EMPTY, ErrorCode.E40304),
                new ParameterValidationUtil("@i_originCountry", ValidationType.NOT_EMPTY, ErrorCode.E40305),
                new ParameterValidationUtil("@i_currency", ValidationType.NOT_EMPTY, ErrorCode.E40306),
                new ParameterValidationUtil("@i_originCurrency", ValidationType.NOT_EMPTY, ErrorCode.E40307),
                new ParameterValidationUtil("@i_exchangeRate", ValidationType.NOT_EMPTY, ErrorCode.E40308)
            };
        } 
        else if(Constants.CONSIGNMENT_UNLOCK.equals(servicio)) {
            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil("@i_externalCustomerId", ValidationType.NOT_EMPTY,  ErrorCode.E40310),
                new ParameterValidationUtil("@i_accountNumber", ValidationType.NOT_EMPTY,  ErrorCode.E40130),
                new ParameterValidationUtil("@i_referenceNumber", ValidationType.NOT_EMPTY, ErrorCode.E40131),
                new ParameterValidationUtil("@i_movementId", ValidationType.NOT_EMPTY,  ErrorCode.E40133)
            };
        } 
        else if(Constants.CONSIGNMENT_REFUND.equals(servicio)) {
            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil("@i_externalCustomerId", ValidationType.NOT_EMPTY,  ErrorCode.E40310),
                new ParameterValidationUtil("@i_accountNumber", ValidationType.NOT_EMPTY, ErrorCode.E40130),
                new ParameterValidationUtil("@i_referenceNumber", ValidationType.NOT_EMPTY, ErrorCode.E40092),
                new ParameterValidationUtil("@i_reversal_concept", ValidationType.NOT_EMPTY, ErrorCode.E40311),
                new ParameterValidationUtil("@i_referenceNumber_trn", ValidationType.NOT_EMPTY, ErrorCode.E40131),
                new ParameterValidationUtil("@i_reversal_reason", ValidationType.NOT_EMPTY, ErrorCode.E40134),
                new ParameterValidationUtil("@i_movementId", ValidationType.NOT_EMPTY,  ErrorCode.E40133)
            };
        } 
        else {
            throw new BusinessException(-2, "La operación solicitada no es válida.");
        }
        aBagSPJavaOrchestration.put(Constants.PARAMETERS_VALIDATE, validations);
    }

    private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][processTransaction]");
        }

        //Ejecución Central operación 1
        centralExecutionConsignment (aBagSPJavaOrchestration, anOriginalRequest);

        //Ejecución Local
        localExecutionConsignment (aBagSPJavaOrchestration, anOriginalRequest);

        //Ejecución Central operación 2
        centralExecutionConsignment(aBagSPJavaOrchestration, anOriginalRequest);

        //Validar errores en ejecucion central y local
        if (aBagSPJavaOrchestration.containsKey(Constants.CENTRAL_ERROR_CODE_OP + "1")) {
            int errorCode = Integer.parseInt((String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "1"));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_MSG_OP + "1");
            throw new ApplicationException(errorCode, errorMessage);
        }
        else if (aBagSPJavaOrchestration.containsKey(Constants.LOCAL_ERROR_CODE)) {
            int errorCode = Integer.parseInt((String) aBagSPJavaOrchestration.get(Constants.LOCAL_ERROR_CODE));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.LOCAL_ERROR_MSG);
            throw new ApplicationException(errorCode, errorMessage);
        }
        else if (aBagSPJavaOrchestration.containsKey(Constants.CENTRAL_ERROR_CODE_OP + "2")) {
            int errorCode = Integer.parseInt((String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "2"));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_MSG_OP + "2");
            throw new ApplicationException(errorCode, errorMessage);
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    private void centralExecutionConsignment(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][centralExecutionConsignmentCredit]");
        }

        IProcedureRequest centralTransactionRequest = (initProcedureRequest(anOriginalRequest));
        centralTransactionRequest.setSpName("cob_ahorros..sp_ahconsignment");
        centralTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        centralTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
        centralTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.NO);
        centralTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(TRN_CONSIGNMENT_CREDIT));
        
        String rty = "N"; // N = Normal, S = Reentry
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
            rty = "S";
        }

        String tipoEject = "F"; // F = Offline, L = Online
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_ONLINE)) {
            tipoEject = "L";
        }

        String operacion = "1";
        if (aBagSPJavaOrchestration.containsKey(Constants.LOCAL_RESPONSE)) {
            operacion = "2";
        }

        centralTransactionRequest.addInputParam(Constants.T_TRN, ICTSTypes.SQLINT4, String.valueOf(TRN_CREDIT));
        centralTransactionRequest.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, rty);
        centralTransactionRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, tipoEject);
        centralTransactionRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
        centralTransactionRequest.addInputParam("@i_filial", ICTSTypes.SQLINT4, "1");

        //Copiar los parámetros del request original al request central
        copyParams(anOriginalRequest, centralTransactionRequest);

        if("2".equals(operacion)) {
            //Copiar outpus de ejecución central op1 a inputs de ejecución central op2
            IProcedureResponse centralTransactionRequestOp1 = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.CENTRAL_RESPONSE_OP + "1");
            copyOutputParamsAsInputParams(centralTransactionRequestOp1, centralTransactionRequest);
        }

        centralTransactionRequest.addInputParam("@i_mon", ICTSTypes.SQLINT4, Constants.DEFAULT_CURRENCY);
        centralTransactionRequest.addInputParam("@i_canal", ICTSTypes.SQLINT4, Constants.DEFAULT_CANAL);

        String errorOp1 = (String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "1");
        centralTransactionRequest.addInputParam("@i_error_local", ICTSTypes.SQLINT4, errorOp1);
        
        //parametros de salida
        centralTransactionRequest.addOutputParam("@o_saldo_disponible", ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam("@o_saldo_girar", ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam("@o_saldo_contable", ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam("@o_monto_bloq", ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam("@o_ssn_host", ICTSTypes.SQLINT4, "0");
        centralTransactionRequest.addOutputParam("@o_error", ICTSTypes.SQLINT4, "0");
        centralTransactionRequest.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "X");

        IProcedureResponse centralProcedureResponse = executeCoreBanking(centralTransactionRequest);
        String error = centralProcedureResponse.readValueParam("@o_error");
        if(error != null && !error.isEmpty() && !"0".equals(error)) {
            String mensaje = centralProcedureResponse.readValueParam("@o_mensaje");
            if (logger.isErrorEnabled()) {
                logger.logError("Error in central execution Op" + operacion + ": " + error + "-" + mensaje);
            }
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_CODE_OP + operacion, error);
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_MSG_OP + operacion, mensaje);
        }
        else if("1".equals(operacion)) {
            String ssnHost = centralProcedureResponse.readValueParam("@o_ssn_host");
            aBagSPJavaOrchestration.put(Constants.SSN_HOST, ssnHost);
        }

        aBagSPJavaOrchestration.put(Constants.CENTRAL_RESPONSE_OP + operacion, centralProcedureResponse);

    }   

    private void localExecutionConsignment(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][centralExecutionConsignmentCredit]");
        }

        IProcedureRequest localTransactionRequest = (initProcedureRequest(anOriginalRequest));
        localTransactionRequest.setSpName("cob_ahorros..sp_ahconsignment_local");
        localTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        localTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        localTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.NO);
        localTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(TRN_CONSIGNMENT_CREDIT));
        
        String rty = "N"; // N = Normal, S = Reentry
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
            rty = "S";
        }

        String tipoEject = "F"; // F = Offline, L = Online
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_ONLINE)) {
            tipoEject = "L";
        }

        String operacion = "1"; 

        localTransactionRequest.addInputParam(Constants.T_TRN, ICTSTypes.SQLINT4, String.valueOf(TRN_CREDIT));
        localTransactionRequest.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, rty);
        localTransactionRequest.addInputParam("@i_tipo_ejec", ICTSTypes.SQLVARCHAR, tipoEject);
        localTransactionRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
        localTransactionRequest.addInputParam("@i_filial", ICTSTypes.SQLINT4, "1");

         IProcedureResponse centralTransactionResponseOp1 = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.CENTRAL_RESPONSE_OP + "1");

        //Copiar los parámetros del request original al request local
        copyParams(anOriginalRequest, localTransactionRequest);

        //Copiar outpus de ejecución central a inputs de ejecución local
        copyOutputParamsAsInputParams(centralTransactionResponseOp1, localTransactionRequest);

        localTransactionRequest.addInputParam("@i_mon", ICTSTypes.SQLINT4, Constants.DEFAULT_CURRENCY); 
        localTransactionRequest.addInputParam("@i_canal", ICTSTypes.SQLINT4, Constants.DEFAULT_CANAL);
        localTransactionRequest.addInputParam("@i_latitud", ICTSTypes.SQLDECIMAL, "0.0"); 
        localTransactionRequest.addInputParam("@i_longitud", ICTSTypes.SQLDECIMAL, "0.0"); 
        
        //parametros de salida
        localTransactionRequest.addOutputParam("@o_saldo_disponible", ICTSTypes.SQLMONEY, "0");
        localTransactionRequest.addOutputParam("@o_saldo_girar", ICTSTypes.SQLMONEY, "0");
        localTransactionRequest.addOutputParam("@o_saldo_contable", ICTSTypes.SQLMONEY, "0");
        localTransactionRequest.addOutputParam("@@o_monto_bloq", ICTSTypes.SQLMONEY, "0");
        localTransactionRequest.addOutputParam("@o_ssn", ICTSTypes.SQLINT4, "0");
        localTransactionRequest.addOutputParam("@o_error", ICTSTypes.SQLINT4, "0");
        localTransactionRequest.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "0");

        IProcedureResponse localProcedureResponse = executeCoreBanking(localTransactionRequest);

        String error = localProcedureResponse.readValueParam("@o_error");
        if(error != null && !error.isEmpty() && !"0".equals(error)) {
            String mensaje = localProcedureResponse.readValueParam("@o_mensaje");
            if (logger.isErrorEnabled()) {
                logger.logError("Error in local execution: " + error + "-" + mensaje);
            }
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_CODE, error);
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_MSG, mensaje);
        }
        
        aBagSPJavaOrchestration.put(Constants.LOCAL_RESPONSE, localProcedureResponse);

    }   


    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][processResponse]");
        }

        IResultSetHeader metaData = new ResultSetHeader();
        IResultSetData data = new ResultSetData();
        IResultSetRow row = new ResultSetRow();

        metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SYBVARCHAR, 255));

        IProcedureResponse errorResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.RESPONSE_ERROR_HANDLER);

        // Verifica si hay un error en el response
        if (errorResponse != null) {
            // Si el resultado indica un error
            if (ICSP.ERROR_EXECUTION_SERVICE.equals(errorResponse.readValueFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT))) {
                // Flujo fallido
                if (logger.isDebugEnabled()) {
                    logger.logDebug("Ending flow, processResponse failed.");
                    logger.logDebug("success: false");
                    logger.logDebug("code: " + errorResponse.readValueFieldInHeader(ICSP.SERVICE_ERROR_CODE));
                    logger.logDebug("message: " + errorResponse.readValueFieldInHeader(ICSP.MESSAGE_ERROR));
                }

                row.addRowData(1, new ResultSetRowColumnData(false, "false"));
                row.addRowData(2, new ResultSetRowColumnData(false, errorResponse.readValueFieldInHeader(ICSP.SERVICE_ERROR_CODE)));
                row.addRowData(3, new ResultSetRowColumnData(false, errorResponse.readValueFieldInHeader(ICSP.MESSAGE_ERROR)));
                row.addRowData(4, new ResultSetRowColumnData(false, null));
                data.addRow(row);

                registerTransactionFailed(CLASS_NAME_CONSIGNMENT_CREDIT, "", anOriginalRequest, aBagSPJavaOrchestration);
            }
        } else {
            // No hay errores, proceder con el flujo exitoso
            String movementId = (String)aBagSPJavaOrchestration.get(Constants.SSN_HOST);
            if (logger.isDebugEnabled()) {
                logger.logDebug("Ending flow, processResponse success.");
                logger.logDebug("movementId: " + movementId);
            }

            row.addRowData(1, new ResultSetRowColumnData(false, "true"));
            row.addRowData(2, new ResultSetRowColumnData(false, "0"));
            row.addRowData(3, new ResultSetRowColumnData(false, "success"));
            row.addRowData(4, new ResultSetRowColumnData(false, movementId));
            data.addRow(row);

            registerAllTransactionSuccess(CLASS_NAME_CONSIGNMENT_CREDIT, anOriginalRequest, aBagSPJavaOrchestration.get(Constants.CAUSA).toString(), aBagSPJavaOrchestration);
        }

        aBagSPJavaOrchestration.replace(Constants.PROCESS_OPERATION, Constants.FINISH_OPERATION);
        logIdempotence(anOriginalRequest, aBagSPJavaOrchestration);

        IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
        IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
        wProcedureResponse.addResponseBlock(resultBlock);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response: " +  wProcedureResponse.getProcedureResponseAsString());
        }

        return wProcedureResponse;
    }

    private void copyParams(IProcedureRequest requestFrom, IProcedureRequest requestTo) {
        Object[] params = requestFrom.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureRequestParam) {
				IProcedureRequestParam param = (IProcedureRequestParam) params[i];
                requestTo.addParam(param.getName(), param.getDataType(), param.getIOType(),
					param.getLen(), param.getValue());
			}
		}
    }

    private void copyOutputParamsAsInputParams(IProcedureResponse requestFrom, IProcedureRequest requestTo) {
        Object[] params = requestFrom.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureRequestParam) {
				IProcedureRequestParam param = (IProcedureRequestParam) params[i];
                String paramName = param.getName();
                if (paramName.startsWith("@o_")) {
                    paramName = paramName.replaceFirst("@o_", "@i_");
                    requestTo.addParam(paramName, param.getDataType(), param.getIOType(),
					param.getLen(), param.getValue());
                }
			}
		}
    }

    @Override
    public ICoreServer getCoreServer() {
        return null;
    }
    
}
