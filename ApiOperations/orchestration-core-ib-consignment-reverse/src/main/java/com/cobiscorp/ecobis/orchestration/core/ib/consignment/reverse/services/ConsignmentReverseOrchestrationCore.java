package com.cobiscorp.ecobis.orchestration.core.ib.consignment.reverse.services;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.*;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.*;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ApplicationException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.BusinessException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.ErrorHandler;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.exceptions.FlowTerminatedException;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.utils.ParameterValidationUtil;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.Constants;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import java.util.Map;

import static com.cobiscorp.ecobis.orchestration.core.ib.consignment.reverse.utils.ConstantsUtil.*;


/**
 * Esta clase representa una persona.
 *
 * @author egalicia
 * @version 1.0
 * @since 21/05/2025
 */
@Component(name = "ConsignmentReverseOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ConsignmentReverseOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
        @Property(name = "service.identifier", value = "ConsignmentReverseOrchestrationCore"),
        @Property(name = "service.spName", value = "cob_procesador..sp_consignment_reverse")
})
public class ConsignmentReverseOrchestrationCore extends OfflineApiTemplate {

    private ILogger logger = (ILogger) this.getLogger();
    @Override
    protected void loadDataCustomer(IProcedureRequest iProcedureRequest, Map<String, Object> map) {

    }

    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][executeJavaOrchestration]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
        }

        aBagSPJavaOrchestration.put(Constants.IS_ONLINE, false);

        try {
            initializeValidationParameters(aBagSPJavaOrchestration);
            validateParameters(aBagSPJavaOrchestration);

            aBagSPJavaOrchestration.put(Constants.IS_REENTRY, evaluateExecuteReentry(anOriginalRequest));

            if (logger.isDebugEnabled()) {
                logger.logDebug("Response flowRty: " + aBagSPJavaOrchestration.get(Constants.IS_REENTRY));
            }

            if (!(Boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
                aBagSPJavaOrchestration.put(Constants.PROCESS_OPERATION, "CONSIGNMENT_REVERSE");
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
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (FlowTerminatedException e) {
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (ApplicationException e) {
            ErrorHandler.handleException(e, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationException appEx = new ApplicationException(50061, "Error in consignment reverse operation.");
            ErrorHandler.handleException(appEx, aBagSPJavaOrchestration);
            return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Finish [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][executeJavaOrchestration]");
            }
        }
    }

    private void initializeValidationParameters(Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][initializeValidationParameters]");
        }

        ParameterValidationUtil[] validations = {
                new ParameterValidationUtil("@i_reversalConcept", "notEmpty", 40128, "reversalConcept must not be empty."),
                new ParameterValidationUtil("@i_referenceNumber", "notEmpty", 40092, "referenceNumber must not be empty."),
                //new ParameterValidationUtil("@i_referenceNumber", "length", 40104, "referenceNumber must have 6 digits.", new HashMap<String, Object>() {{ put("expectedLength", 6); }}),
                new ParameterValidationUtil("@i_externalCustomerId_ori", "greaterThanZero", 40129, "externalCustomerId must be greater than 0."),
                new ParameterValidationUtil("@i_accountNumber_ori", "notEmpty", 40130, "originalTransactionData.accountNumber must not be empty."),
                new ParameterValidationUtil("@i_referenceNumber_ori", "notEmpty", 40131, "originalTransactionData.referenceNumber must not be empty."),
                new ParameterValidationUtil("@i_movementId_ori", "notEmpty", 40133, "originalTransactionData.movementId must not be empty."),
                new ParameterValidationUtil("@i_reversalReason_ori", "notEmpty", 40134, "originalTransactionData.reversalReason must not be empty.")
        };

        aBagSPJavaOrchestration.put(Constants.PARAMETERS_VALIDATE, validations);
    }

    private IProcedureResponse processTransaction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][processTransaction]");
        }
        if (!(Boolean)aBagSPJavaOrchestration.get(Constants.IS_ONLINE)) {
            if (!(Boolean)aBagSPJavaOrchestration.get(Constants.IS_REENTRY)) {
                processOffline(aBagSPJavaOrchestration, anOriginalRequest);
            } else {
                throw new FlowTerminatedException(50041, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
            }
        } else {
            processOnline(aBagSPJavaOrchestration, anOriginalRequest);
        }

        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }

    private void processOffline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][processOffline]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
        }

        IProcedureResponse procedureResponse;

        procedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response [saveReentry]: " + procedureResponse.getProcedureResponseAsString());
        }
        /*
        procedureResponse = getValAccount(anOriginalRequest, aBagSPJavaOrchestration);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response [getValAccount]: " + procedureResponse.getProcedureResponseAsString());
        }
        if (!procedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
            throw new BusinessException(Integer.parseInt(procedureResponse.getResultSetRowColumnData(2, 1, 1).getValue()), procedureResponse.getResultSetRowColumnData(2, 1, 2).getValue());
        }
        */
        IProcedureRequest localTransactionRequest = (initProcedureRequest(anOriginalRequest));

        localTransactionRequest.setSpName("cob_bvirtual..sp_consignment_reverse_local_api");
        localTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        localTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
        localTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
        localTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(TRN_CONSIGNMENT_REVERSE));

        localTransactionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, String.valueOf(TRN_CONSIGNMENT_REVERSE));
        localTransactionRequest.addInputParam("@i_reversalConcept",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalConcept"));
        localTransactionRequest.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
        localTransactionRequest.addInputParam("@i_externalCustomerId_ori", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
        localTransactionRequest.addInputParam("@i_accountNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber_ori"));
        localTransactionRequest.addInputParam("@i_referenceNumber_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_ori"));
        localTransactionRequest.addInputParam("@i_movementId_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_ori"));
        localTransactionRequest.addInputParam("@i_reversalReason_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalReason_ori"));

        localTransactionRequest.addOutputParam("@o_causa", ICTSTypes.SQLVARCHAR, "X");

        procedureResponse = executeCoreBanking(localTransactionRequest);

        aBagSPJavaOrchestration.put(Constants.CAUSA, procedureResponse.readValueParam("@o_causa"));

        if (logger.isDebugEnabled()) {
            logger.logDebug("[cob_bvirtual..sp_consignment_reverse_local_api][procedureResponse]: " + procedureResponse.getProcedureResponseAsString());
        }

        if (!procedureResponse.hasError()) {
            IResultSetRow resultSetRow = procedureResponse.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {
                aBagSPJavaOrchestration.put(Constants.COLUMNS_RETURN, columns);
                aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
            } else {
                throw new BusinessException(Integer.parseInt(columns[1].getValue()), columns[2].getValue());
            }
        } else {
            throw new  ApplicationException(50061, "Error in consignment reverse operation.");
        }
    }

    private void processOnline(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][processOnline]");
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
        }

        IProcedureRequest centralTransactionRequest = (initProcedureRequest(anOriginalRequest));

        centralTransactionRequest.setSpName("cob_bvirtual..sp_consignment_reverse_central_api");
        centralTransactionRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, Constants.COBIS_CONTEXT);
        centralTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
        centralTransactionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
        centralTransactionRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, String.valueOf(TRN_CONSIGNMENT_REVERSE));

        centralTransactionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, String.valueOf(TRN_CONSIGNMENT_REVERSE));
        centralTransactionRequest.addInputParam("@i_reversalConcept",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalConcept"));
        centralTransactionRequest.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber"));
        centralTransactionRequest.addInputParam("@i_externalCustomerId_ori", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId_ori"));
        centralTransactionRequest.addInputParam("@i_accountNumber_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber_ori"));
        centralTransactionRequest.addInputParam("@i_referenceNumber_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenceNumber_ori"));
        centralTransactionRequest.addInputParam("@i_movementId_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_movementId_ori"));
        centralTransactionRequest.addInputParam("@i_reversalReason_ori",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reversalReason_ori"));

        centralTransactionRequest.addOutputParam("@o_causa", ICTSTypes.SQLVARCHAR, "X");

        IProcedureResponse procedureResponse = executeCoreBanking(centralTransactionRequest);

        aBagSPJavaOrchestration.put(Constants.CAUSA, procedureResponse.readValueParam("@o_causa"));

        if (logger.isDebugEnabled()) {
            logger.logDebug("[cob_bvirtual..sp_consignment_reverse_central_api][procedureResponse]: " + procedureResponse.getProcedureResponseAsString());
        }

        if (!procedureResponse.hasError()) {
            IResultSetRow resultSetRow = procedureResponse.getResultSet(1).getData().getRowsAsArray()[0];
            IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

            if (columns[0].getValue().equals("true")) {
                aBagSPJavaOrchestration.put(Constants.COLUMNS_RETURN, columns);
                aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
            } else {
                throw new BusinessException(Integer.parseInt(columns[1].getValue()), columns[2].getValue());
            }
        } else {
            throw new  ApplicationException(50061, "Error in consignment reverse operation.");
        }
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Begin [" + CLASS_NAME_CONSIGNMENT_REVERSE + "][processResponse]");
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

                registerTransactionFailed(CLASS_NAME_CONSIGNMENT_REVERSE, "", anOriginalRequest, aBagSPJavaOrchestration);
            }
        } else {
            // No hay errores, proceder con el flujo exitoso
            IResultSetRowColumnData[] columnsToReturn = (IResultSetRowColumnData[]) aBagSPJavaOrchestration.get(Constants.COLUMNS_RETURN);
            if (logger.isDebugEnabled()) {
                logger.logDebug("Ending flow, processResponse success.");
                logger.logDebug("success: " + columnsToReturn[0].getValue());
                logger.logDebug("code: " + columnsToReturn[1].getValue());
                logger.logDebug("message: " + columnsToReturn[2].getValue());
                logger.logDebug("movementId: " + columnsToReturn[3].getValue());
            }

            row.addRowData(1, new ResultSetRowColumnData(false, columnsToReturn[0].getValue()));
            row.addRowData(2, new ResultSetRowColumnData(false, columnsToReturn[1].getValue()));
            row.addRowData(3, new ResultSetRowColumnData(false, columnsToReturn[2].getValue()));
            row.addRowData(4, new ResultSetRowColumnData(false, columnsToReturn[3].getValue()));
            data.addRow(row);

            registerAllTransactionSuccess(CLASS_NAME_CONSIGNMENT_REVERSE, anOriginalRequest, aBagSPJavaOrchestration.get(Constants.CAUSA).toString(), aBagSPJavaOrchestration);
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
}
