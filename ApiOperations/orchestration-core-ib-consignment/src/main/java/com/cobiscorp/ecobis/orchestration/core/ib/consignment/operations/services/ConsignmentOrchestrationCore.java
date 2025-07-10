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
import com.cobiscorp.cobis.cts.domains.IProcedureResponseParam;
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
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.Inputs;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.Outputs;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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

    private ILogger loggerL = (ILogger) this.getLogger();
    
    @Override
    protected void loadDataCustomer(IProcedureRequest iProcedureRequest, Map<String, Object> map) {
        // No es necesario para esta orquestación
    }

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeJavaOrchestration]");
        }

        aBagSPJavaOrchestration.put(Constants.ORIGINAL_REQUEST, anOriginalRequest);
        aBagSPJavaOrchestration.put(Constants.IS_ONLINE, false);
        
        try {
            String servicio = anOriginalRequest.readValueParam(Inputs.I_SERVICIO);
            initializeValidationParameters(aBagSPJavaOrchestration, servicio);
            validateParameters(aBagSPJavaOrchestration);

            boolean isReentry = evaluateExecuteReentry(anOriginalRequest);

            aBagSPJavaOrchestration.put(Constants.IS_REENTRY, isReentry);

            if(!isReentry && Constants.CONSIGNMENT_CREDIT.equals(servicio)){
                validateLimits(anOriginalRequest, aBagSPJavaOrchestration);
            }

            executeIdempotence(isReentry, anOriginalRequest, aBagSPJavaOrchestration);

            ServerResponse serverResponse = serverStatus();
            aBagSPJavaOrchestration.put(Constants.IS_ONLINE, serverResponse.getOnLine());
            aBagSPJavaOrchestration.put(Constants.PROCESS_DATE, serverResponse.getProcessDate());

            if (loggerL.isDebugEnabled()) {
                loggerL.logDebug("Response Online: " + aBagSPJavaOrchestration.get(Constants.IS_ONLINE));
            }

            validateContextTransacction(aBagSPJavaOrchestration);

            return processTransaction(aBagSPJavaOrchestration, anOriginalRequest);

        } catch (BusinessException e) {
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("BusinessException in consignment credit operation: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (FlowTerminatedException e) {
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("Flow terminated: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (ApplicationException e) {
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("ApplicationException in consignment credit operation: " + e.getMessage(), e);
            }
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (Exception e) {
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("Exception Error in consignment credit operation: " + e.getMessage(), e);
            }   
            ApplicationException appEx = new ApplicationException(Constants.DEFAULT_ERROR, Constants.DEFAULT_ERROR_MSG);
            ErrorHandler.handleException(appEx, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } finally {
            if (loggerL.isInfoEnabled()) {
                loggerL.logInfo("Finish [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeJavaOrchestration]");
            }
        }
    }

    private void initializeValidationParameters(Map<String, Object> aBagSPJavaOrchestration, String servicio) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][initializeValidationParameters]");
        }

        ParameterValidationUtil[] validations;

        if(Constants.CONSIGNMENT_CREDIT.equals(servicio)) {
            aBagSPJavaOrchestration.put(Constants.PROCESS, "CREDIT_OPERATION");

            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil(Inputs.I_EXTERNALCUSTOMERID, ValidationType.NOT_EMPTY, ErrorCode.E40030),
                new ParameterValidationUtil(Inputs.I_ACCOUNTNUMBER, ValidationType.NOT_EMPTY, ErrorCode.E40082),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.NOT_EMPTY, ErrorCode.E40092),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.MAX_LENGTH, ErrorCode.E40314, new HashMap<String, Object>() {{
                    put("length", 34);
                }}),
                new ParameterValidationUtil(Inputs.I_CREDITCONCEPT, ValidationType.NOT_EMPTY, ErrorCode.E40093),
                new ParameterValidationUtil(Inputs.I_AMOUNT, ValidationType.NOT_EMPTY, ErrorCode.E40312),
                new ParameterValidationUtil(Inputs.I_AMOUNT, ValidationType.IS_DOUBLE, ErrorCode.E40300),
                new ParameterValidationUtil(Inputs.I_AMOUNT, ValidationType.GREATER_THAN_ZERO_DOUBLE, ErrorCode.E40107),
                new ParameterValidationUtil(Inputs.I_COMMISSION, ValidationType.NOT_EMPTY, ErrorCode.E40313),
                new ParameterValidationUtil(Inputs.I_COMMISSION, ValidationType.IS_DOUBLE, ErrorCode.E40301),
                new ParameterValidationUtil(Inputs.I_ORIGINCODE, ValidationType.NOT_EMPTY, ErrorCode.E40302),
                new ParameterValidationUtil(Inputs.I_SENDERNAME, ValidationType.NOT_EMPTY, ErrorCode.E40303),
                new ParameterValidationUtil(Inputs.I_MONEYTRANSMITTER, ValidationType.NOT_EMPTY, ErrorCode.E40304),
                new ParameterValidationUtil(Inputs.I_ORIGINCOUNTRY, ValidationType.NOT_EMPTY, ErrorCode.E40305),
                new ParameterValidationUtil(Inputs.I_CURRENCY, ValidationType.NOT_EMPTY, ErrorCode.E40306),
                new ParameterValidationUtil(Inputs.I_ORIGINCURRENCY, ValidationType.NOT_EMPTY, ErrorCode.E40307),
                new ParameterValidationUtil(Inputs.I_EXCHANGERATE, ValidationType.NOT_EMPTY, ErrorCode.E40308)
            };
        } 
        else if(Constants.CONSIGNMENT_UNLOCK.equals(servicio)) {
            aBagSPJavaOrchestration.put(Constants.PROCESS, "UNLOCK_CREDIT_OPERATION");

            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil(Inputs.I_EXTERNALCUSTOMERID, ValidationType.NOT_EMPTY,  ErrorCode.E40310),
                new ParameterValidationUtil(Inputs.I_ACCOUNTNUMBER, ValidationType.NOT_EMPTY,  ErrorCode.E40130),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.NOT_EMPTY, ErrorCode.E40131),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.MAX_LENGTH, ErrorCode.E40315, new HashMap<String, Object>() {{
                    put("length", 34);
                }}),
                new ParameterValidationUtil(Inputs.I_MOVEMENTID, ValidationType.NOT_EMPTY,  ErrorCode.E40133)
            };
        } 
        else if(Constants.CONSIGNMENT_REFUND.equals(servicio)) {
            aBagSPJavaOrchestration.put(Constants.PROCESS, "REVERSAL_CREDIT_OPERATION");

            validations = new ParameterValidationUtil[]{
                new ParameterValidationUtil(Inputs.I_EXTERNALCUSTOMERID, ValidationType.NOT_EMPTY,  ErrorCode.E40310),
                new ParameterValidationUtil(Inputs.I_ACCOUNTNUMBER, ValidationType.NOT_EMPTY, ErrorCode.E40130),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.NOT_EMPTY, ErrorCode.E40092),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER, ValidationType.MAX_LENGTH, ErrorCode.E40314, new HashMap<String, Object>() {{
                    put("length", 34);
                }}),
                new ParameterValidationUtil(Inputs.I_REVERSAL_CONCEPT, ValidationType.NOT_EMPTY, ErrorCode.E40311),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER_TRN, ValidationType.NOT_EMPTY, ErrorCode.E40131),
                new ParameterValidationUtil(Inputs.I_REFERENCENUMBER_TRN, ValidationType.MAX_LENGTH, ErrorCode.E40315, new HashMap<String, Object>() {{
                    put("length", 34);
                }}),
                new ParameterValidationUtil(Inputs.I_REVERSAL_REASON, ValidationType.NOT_EMPTY, ErrorCode.E40134),
                new ParameterValidationUtil(Inputs.I_MOVEMENTID, ValidationType.NOT_EMPTY,  ErrorCode.E40133)
            };
        } 
        else {
            throw new BusinessException(-2, "La operación solicitada no es válida.");
        }
        aBagSPJavaOrchestration.put(Constants.PARAMETERS_VALIDATE, validations);
    }

    private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][processTransaction]");
        }

        if(!aBagSPJavaOrchestration.containsKey(Constants.LIMIT_ERROR_CODE)){

            //Ejecución Central operación 1
            centralExecutionConsignment (aBagSPJavaOrchestration, anOriginalRequest);

            if (aBagSPJavaOrchestration.containsKey(Constants.CENTRAL_ERROR_CODE_OP + "1")) {
                int errorCode = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "1"));
                if (errorCode == Constants.REENTRY_ERROR) {
                    return (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.CENTRAL_RESPONSE_OP + "1");
                }
            }

        }

        //Ejecución Local
        localExecutionConsignment (aBagSPJavaOrchestration, anOriginalRequest);

        //Validar errores en ejecucion central y local
        if (aBagSPJavaOrchestration.containsKey(Constants.LIMIT_ERROR_CODE)) {
            int errorCode = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.LIMIT_ERROR_CODE));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.LIMIT_ERROR_MSG);
            throw new ApplicationException(errorCode, errorMessage);
        }
        else if (aBagSPJavaOrchestration.containsKey(Constants.CENTRAL_ERROR_CODE_OP + "1")) {
            int errorCode = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "1"));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_MSG_OP + "1");
            throw new ApplicationException(errorCode, errorMessage);
        }
        else if (aBagSPJavaOrchestration.containsKey(Constants.LOCAL_ERROR_CODE)) {
            int errorCode = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.LOCAL_ERROR_CODE));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.LOCAL_ERROR_MSG);
            throw new ApplicationException(errorCode, errorMessage);
        }
        else if (aBagSPJavaOrchestration.containsKey(Constants.CENTRAL_ERROR_CODE_OP + "2")) {
            int errorCode = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "2"));
            String errorMessage = (String) aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_MSG_OP + "2");
            throw new ApplicationException(errorCode, errorMessage);
        }

        String rty = "N"; // N = Normal, S = Reentry
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
            rty = "S";
        }

        String tipoEject = "F"; // F = Offline, L = Online
        if ((boolean)aBagSPJavaOrchestration.get(Constants.IS_ONLINE)) {
            tipoEject = "L";
        }

        if("F".equals(tipoEject) && "N".equals(rty)) {
            IProcedureResponse procedureResponse;

            procedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
            if (loggerL.isDebugEnabled()) {
                loggerL.logDebug("Response [saveReentry]: " + procedureResponse.getProcedureResponseAsString());
            }
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    private void centralExecutionConsignment(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][centralExecutionConsignmentCredit]");
        }

        IProcedureRequest centralTransactionRequest = (initProcedureRequest(anOriginalRequest));
        centralTransactionRequest.setSpName("cob_ahorros..sp_ahconsignment");
        centralTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        centralTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
        centralTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
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

        String reentrySSN = anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX");

        if (Objects.nonNull(reentrySSN) && !reentrySSN.isEmpty()) {
            centralTransactionRequest.addInputParam(Inputs.I_SSN, ICTSTypes.SQLINT4, reentrySSN);
        }

        centralTransactionRequest.addInputParam(Constants.T_TRN, ICTSTypes.SQLINT4, String.valueOf(TRN_CREDIT));
        centralTransactionRequest.addInputParam(Inputs.T_RTY, ICTSTypes.SQLVARCHAR, rty);
        centralTransactionRequest.addInputParam(Inputs.I_TIPO_EJEC, ICTSTypes.SQLVARCHAR, tipoEject);
        centralTransactionRequest.addInputParam(Inputs.I_OPERACION, ICTSTypes.SQLVARCHAR, operacion);
        centralTransactionRequest.addInputParam(Inputs.I_FILIAL, ICTSTypes.SQLINT4, "1");

        //Copiar los parámetros del request original al request central
        copyParams(anOriginalRequest, centralTransactionRequest);

        if("2".equals(operacion)) {
            //Copiar outpus de ejecución central op1 a inputs de ejecución central op2
            IProcedureResponse centralTransactionRequestOp1 = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.CENTRAL_RESPONSE_OP + "1");
            copyOutputParamsAsInputParams(centralTransactionRequestOp1, centralTransactionRequest);
        }

        centralTransactionRequest.addInputParam(Inputs.I_MON, ICTSTypes.SQLINT4, Constants.DEFAULT_CURRENCY);
        centralTransactionRequest.addInputParam(Inputs.I_CANAL, ICTSTypes.SQLINT4, Constants.DEFAULT_CANAL);

        //parametros de salida
        centralTransactionRequest.addOutputParam(Outputs.O_SALDO_DISPONIBLE, ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_SALDO_GIRAR, ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_SALDO_CONTABLE, ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_MONTO_BLOQ, ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_SSN_HOST, ICTSTypes.SQLINT4, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_ERROR, ICTSTypes.SQLINT4, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_MENSAJE, ICTSTypes.SQLVARCHAR, "X");
        centralTransactionRequest.addOutputParam(Outputs.O_CAUSA, ICTSTypes.SQLVARCHAR, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_AMOUNT, ICTSTypes.SQLMONEY, "0");
        centralTransactionRequest.addOutputParam(Outputs.O_CREDITCONCEPT, ICTSTypes.SQLVARCHAR, "");
        centralTransactionRequest.addOutputParam(Outputs.O_MONEYTRANSMITTER, ICTSTypes.SQLVARCHAR, "");
        centralTransactionRequest.addOutputParam(Outputs.O_SENDERNAME, ICTSTypes.SQLVARCHAR, "");

        IProcedureResponse centralProcedureResponse = executeCoreBanking(centralTransactionRequest);

        String ssnHost = centralProcedureResponse.readValueParam(Outputs.O_SSN_HOST);
        aBagSPJavaOrchestration.put("ssn", ssnHost);

        String error = centralProcedureResponse.readValueParam(Outputs.O_ERROR);
        int returnCode = centralProcedureResponse.getReturnCode();
        if(returnCode != 0 && error != null && !error.isEmpty() && !"0".equals(error)) {
            String mensaje = centralProcedureResponse.readValueParam(Outputs.O_MENSAJE);
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("Error in central execution Op" + operacion + ": " + error + "-" + mensaje);
            }
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_CODE_OP + operacion, error);
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_MSG_OP + operacion, mensaje);
        }else if(returnCode != 0 && (error == null || error.isEmpty() || "0".equals(error))) {//Posible error SQL
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_CODE_OP + operacion, Constants.DEFAULT_ERROR);
            aBagSPJavaOrchestration.put(Constants.CENTRAL_ERROR_MSG_OP + operacion, Constants.DEFAULT_ERROR_MSG);
        }
        else if("1".equals(operacion)) {
            String causa = centralProcedureResponse.readValueParam(Outputs.O_CAUSA);
            aBagSPJavaOrchestration.put(Constants.SSN_HOST, ssnHost);
            aBagSPJavaOrchestration.put(Constants.CAUSA, causa);
        }

        aBagSPJavaOrchestration.put(Constants.CENTRAL_RESPONSE_OP + operacion, centralProcedureResponse);

    }   

    private void localExecutionConsignment(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][centralExecutionConsignmentCredit]");
        }

        IProcedureRequest localTransactionRequest = (initProcedureRequest(anOriginalRequest));
        localTransactionRequest.setSpName("cob_ahorros..sp_ahconsignment_local");
        localTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        localTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        localTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
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
        localTransactionRequest.addInputParam(Inputs.T_RTY, ICTSTypes.SQLVARCHAR, rty);
        localTransactionRequest.addInputParam(Inputs.I_TIPO_EJEC, ICTSTypes.SQLVARCHAR, tipoEject);
        localTransactionRequest.addInputParam(Inputs.I_OPERACION, ICTSTypes.SQLVARCHAR, operacion);
        localTransactionRequest.addInputParam(Inputs.I_FILIAL, ICTSTypes.SQLINT4, "1");

         IProcedureResponse centralTransactionResponseOp1 = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.CENTRAL_RESPONSE_OP + "1");

        //Copiar los parámetros del request original al request local
        copyParams(anOriginalRequest, localTransactionRequest);

        //Copiar outpus de ejecución central a inputs de ejecución local
        copyOutputParamsAsInputParams(centralTransactionResponseOp1, localTransactionRequest);

        int errorCentralop1 = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.CENTRAL_ERROR_CODE_OP + "1"));
        int errorValidateLimits = convertObjectToInt(aBagSPJavaOrchestration.get(Constants.LIMIT_ERROR_CODE)); 

        int errorCode = errorCentralop1 > 0 ? errorCentralop1 : errorValidateLimits;

        localTransactionRequest.addInputParam(Inputs.I_ERROR_CENTRAL, ICTSTypes.SQLINT4, errorCode + "");
        localTransactionRequest.addInputParam(Inputs.I_MON, ICTSTypes.SQLINT4, Constants.DEFAULT_CURRENCY); 
        localTransactionRequest.addInputParam(Inputs.I_CANAL, ICTSTypes.SQLINT4, Constants.DEFAULT_CANAL);

        //parametros de salida
        localTransactionRequest.addOutputParam(Outputs.O_SSN, ICTSTypes.SQLINT4, "0");
        localTransactionRequest.addOutputParam(Outputs.O_ERROR, ICTSTypes.SQLINT4, "0");
        localTransactionRequest.addOutputParam(Outputs.O_MENSAJE, ICTSTypes.SQLVARCHAR, "0");

        IProcedureResponse localProcedureResponse = executeCoreBanking(localTransactionRequest);

        String error = localProcedureResponse.readValueParam(Outputs.O_ERROR);
        int returnCode = localProcedureResponse.getReturnCode();
        if(returnCode != 0 && error != null && !error.isEmpty() && !"0".equals(error)) {
            String mensaje = localProcedureResponse.readValueParam(Outputs.O_MENSAJE);
            if (loggerL.isErrorEnabled()) {
                loggerL.logError("Error in local execution: " + error + "-" + mensaje);
            }
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_CODE, error);
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_MSG, mensaje);
        }
        else if(returnCode != 0 && (error == null || error.isEmpty() || "0".equals(error))) {//Posible error SQL
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_CODE, Constants.DEFAULT_ERROR);
            aBagSPJavaOrchestration.put(Constants.LOCAL_ERROR_MSG, Constants.DEFAULT_ERROR_MSG);
        }
        
        aBagSPJavaOrchestration.put(Constants.LOCAL_RESPONSE, localProcedureResponse);

    }   


    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][processResponse]");
        }

        IResultSetHeader metaData = new ResultSetHeader();
        IResultSetData data = new ResultSetData();
        IResultSetRow row = new ResultSetRow();

        String servicio = anOriginalRequest.readValueParam(Inputs.I_SERVICIO);

        metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SYBVARCHAR, 255));
        if(Constants.CONSIGNMENT_REFUND.equals(servicio)) {
            metaData.addColumnMetaData(new ResultSetHeaderColumn("key", ICTSTypes.SYBVARCHAR, 255));
		    metaData.addColumnMetaData(new ResultSetHeaderColumn("value", ICTSTypes.SYBVARCHAR, 255));
        }
        
        IProcedureResponse errorResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(Constants.RESPONSE_ERROR_HANDLER);

        // Verifica si hay un error en el response
        if (errorResponse != null) {
            // Si el resultado indica un error
            if (ICSP.ERROR_EXECUTION_SERVICE.equals(errorResponse.readValueFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT))) {
                String errorCode = errorResponse.readValueFieldInHeader(ICSP.SERVICE_ERROR_CODE);
                String errorMessage = errorResponse.readValueFieldInHeader(ICSP.MESSAGE_ERROR);
                // Flujo fallido
                if (loggerL.isDebugEnabled()) {
                    loggerL.logDebug("Ending flow, processResponse failed.");
                    loggerL.logDebug("success: false");
                    loggerL.logDebug("code: " + errorCode);
                    loggerL.logDebug("message: " + errorMessage);
                }

                row.addRowData(1, new ResultSetRowColumnData(false, "false"));
                row.addRowData(2, new ResultSetRowColumnData(false, errorCode));
                row.addRowData(3, new ResultSetRowColumnData(false, errorMessage));
                row.addRowData(4, new ResultSetRowColumnData(false, null));
                data.addRow(row);

                aBagSPJavaOrchestration.put("code_error", errorResponse.readValueFieldInHeader(ICSP.SERVICE_ERROR_CODE));
                aBagSPJavaOrchestration.put("message_error", errorResponse.readValueFieldInHeader(ICSP.MESSAGE_ERROR));
                
                registerTransactionFailed(CLASS_NAME_CONSIGNMENT_CREDIT, "", anOriginalRequest, aBagSPJavaOrchestration);
            }
        } else {
            // No hay errores, proceder con el flujo exitoso
            String movementId = (String)aBagSPJavaOrchestration.get(Constants.SSN_HOST);
            if (loggerL.isDebugEnabled()) {
                loggerL.logDebug("Ending flow, processResponse success.");
                loggerL.logDebug("movementId: " + movementId);
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

        if (loggerL.isDebugEnabled()) {
            loggerL.logDebug("Response: " +  wProcedureResponse.getProcedureResponseAsString());
        }

        return wProcedureResponse;
    }

    private void copyParams(IProcedureRequest requestFrom, IProcedureRequest requestTo) {
        if(Objects.isNull(requestFrom) || Objects.isNull(requestTo)) {
            return;
        }

        Object[] params = requestFrom.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureRequestParam ) {
				IProcedureRequestParam param = (IProcedureRequestParam) params[i];
                String paramValue = param.getValue();
                if(Objects.nonNull(paramValue)){
                    requestTo.addParam(param.getName(), param.getDataType(), param.getIOType(),
					param.getLen(), paramValue);
                }
                
			}
		}
    }

    private void copyOutputParamsAsInputParams(IProcedureResponse responseFrom, IProcedureRequest requestTo) {
        if(Objects.isNull(responseFrom) || Objects.isNull(requestTo)) {
            return;
        }

        Object[] params = responseFrom.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureResponseParam) {
				IProcedureResponseParam param = (IProcedureResponseParam) params[i];
                String paramName = param.getName();
                String paramValue = param.getValue();
                if(Objects.nonNull(paramValue) && paramName.startsWith("@o_")){
                    paramName = paramName.replaceFirst("@o_", "@i_");
                    requestTo.addInputParam(paramName, param.getDataType(), paramValue);
                }
			}
		}
    }

    @Override
    public ICoreServer getCoreServer() {
        return null;
    }

    public static int convertObjectToInt(Object obj) {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("String does not represent a valid integer: " + obj);
            }
        } else {
            throw new IllegalArgumentException("Unsupported Object type: " + obj.getClass().getName());
        }
    }

    private void executeIdempotence(boolean isReentry, IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) 
            throws ApplicationException {
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeIdempotence]");
        }   
        if (loggerL.isDebugEnabled()) {
                loggerL.logDebug("Value isReentry: " + isReentry);
            }
        if (!isReentry) {
            aBagSPJavaOrchestration.put(Constants.PROCESS_OPERATION, "CONSIGNMENT_CREDIT");
            IProcedureResponse potency = logIdempotence(anOriginalRequest,aBagSPJavaOrchestration);
            IResultSetRow resultSetRow = potency.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
            if (columns[0].getValue().equals("false") ) {
                throw new ApplicationException(Integer.parseInt(columns[1].getValue()), columns[2].getValue());
            }
        }
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo("Finish [" + CLASS_NAME_CONSIGNMENT_CREDIT + "][executeIdempotence]");
        }
    }

    private void validateLimits(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) 
                                throws ApplicationException, BusinessException {
		
		if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.BEGIN + CLASS_NAME_CONSIGNMENT_CREDIT + "][validateLimits]");
        }
		
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		String account = anOriginalRequest.readValueParam(Inputs.I_ACCOUNTNUMBER);
        String originCode = anOriginalRequest.readValueParam(Inputs.I_ORIGINCODE);
        String cause = "";
        String amount = anOriginalRequest.readValueParam(Inputs.I_AMOUNT);

        if("6010".equals(originCode)){
            cause = getParam(anOriginalRequest, "NCRREL", "AHO");
        }
        else if("6020".equals(originCode)){
            cause = getParam(anOriginalRequest, "NCRRES", "AHO");
        }

        if (Objects.isNull(cause) || cause.isEmpty()) {
            aBagSPJavaOrchestration.put(Constants.LIMIT_ERROR_CODE, ErrorCode.E50062.getCode());
            aBagSPJavaOrchestration.put(Constants.LIMIT_ERROR_MSG, ErrorCode.E50062.getMessage());
        }

		procedureRequest.setSpName("cob_bvirtual..sp_bv_valida_limites");
        procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "V");
		procedureRequest.addInputParam("@i_trn", ICTSTypes.SQLINT4, "18701001");
		procedureRequest.addInputParam("@i_tipo_trn", ICTSTypes.SQLINT4, "253");
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY, amount);		
        procedureRequest.addInputParam("@i_causal", ICTSTypes.SQLINT4, cause);
        procedureRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, account);
		
		IProcedureResponse anProcedureResponse =  executeCoreBanking(procedureRequest);
		Integer code = anProcedureResponse.getReturnCode();
		if(code != 0){
            String message = anProcedureResponse.getMessage(1).getMessageText();
            aBagSPJavaOrchestration.put(Constants.LIMIT_ERROR_CODE, code);
            aBagSPJavaOrchestration.put(Constants.LIMIT_ERROR_MSG, message);
		}
        if (loggerL.isInfoEnabled()) {
            loggerL.logInfo(Constants.END + CLASS_NAME_CONSIGNMENT_CREDIT + "][validateLimits]");
        }
	}

    private String getParam(IProcedureRequest anOriginalRequest, String nemonico, String producto) {
    	if (loggerL.isInfoEnabled()) {
			loggerL.logInfo(Constants.BEGIN + CLASS_NAME + "][getParam]");
		}

		String result = "";
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cobis..sp_parametro");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		reqTMPCentral.addInputParam("@i_nemonico",ICTSTypes.SQLVARCHAR, nemonico);
		reqTMPCentral.addInputParam("@i_producto",ICTSTypes.SQLVARCHAR, producto);	 
	    reqTMPCentral.addInputParam("@i_modo",ICTSTypes.SQLINT4, "4");

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (!wProcedureResponseCentral.hasError() && wProcedureResponseCentral.getResultSetListSize() > 0) {
			
			IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
			
			if (resultSetRows.length > 0) {
				IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
				result = columns[2].getValue();
				return result;
			} 
		} 

		if (loggerL.isInfoEnabled()) {
			loggerL.logInfo(Constants.END + CLASS_NAME + "][getParam]");
		}
		
		return result;
	}
}